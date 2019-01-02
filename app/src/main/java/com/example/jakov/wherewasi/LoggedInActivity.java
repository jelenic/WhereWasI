package com.example.jakov.wherewasi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class LoggedInActivity extends AppCompatActivity {
    private static final String TAG = "LoggedInActivity";
    private Button mapBtn,mailBackupBtn,QuickCheckInBtn, QuickInputBtn, startServiceBtn, stopServiceBtn, StartNewLogBtn, ViewLogsBtn , GetFileBtn;
    private TextView currentLog;
    private EditText serviceTimeET, refreshET;
    private AdView simpleBannerAd;

    private File source;

    private DatabaseHelper logdb;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public double latituded,longituded;

    private String activeLog;
    private Long minTime;
    private Float minDistance;
    private int time;

    private Handler mHandler;

    private static DecimalFormat numberFormat = new DecimalFormat("#.0000");

    private final int PICKFILE_RESULT_CODE = 2;
    public static final String CHANNEL_ID = "GPS_Service";
    static final int REQUEST_LOCATION = 1;

    private static Geocoder geocoder;

    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = this.getSharedPreferences("MyValues", 0);
        SharedPreferences.Editor saveValue = prefs.edit();
        activeLog = ActiveLog.getInstance().getValue();
        saveValue.putString("ActiveLog", activeLog);
        saveValue.putLong("minTime", minTime);
        saveValue.putFloat("Distance", minDistance);
        saveValue.putInt("time", time);
        saveValue.commit();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentLog.setText(ActiveLog.getInstance().getValue());
        Log.i(TAG, "onResume");
        activeLog = ActiveLog.getInstance().getValue();
        currentLog.setText(activeLog);
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        // My App ID: ca-app-pub-3775405938489529~5074959444
        MobileAds.initialize(this, "ca-app-pub-3775405938489529~5074959444");

        findViewByID();

        createNotificationChannel();

        getPrefs();

        setHandler();

        serviceTimeET.setTransformationMethod(null);
        currentLog.setText(activeLog);
        ActiveLog.getInstance().setValue(activeLog);
        logdb = new DatabaseHelper(this);

        setLocationManager();


        setListeners();


        locationPermissions();

        simpleBannerAd = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        simpleBannerAd.loadAd(adRequest);

        geocoder = new Geocoder(this, Locale.getDefault());
        stopServiceBtn.setEnabled(false);
        startServiceBtn.setEnabled(true);
    }

    private void setHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                String text = message.arg1 == 1 ? "INSERTED" : "FAILED";
                Toast.makeText(LoggedInActivity.this, text, Toast.LENGTH_SHORT).show();

            }
        };
    }


    public static String getPath(String lat, String lng) {
        String url = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng + "&zoom=17&size=640x640&markers=color:blue%7C%7C" + lat + "," + lng + "&sensor=false" + "&key=AIzaSyAV0zQ0zdT6jQviqVhhJkV-LjO3-ZnsspU";
        String latLng = numberFormat.format(Double.parseDouble(lat)) + "|" + numberFormat.format(Double.parseDouble(lng));
        String image_path = null;
        String path = Environment.getExternalStorageDirectory() + File.separator + ".WhereWasI" + File.separator + "StaticMaps";
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, latLng + ".jpg");
        Log.d(TAG, "fileSize1 " + file.getPath() + ":" + file.length());

        if (!file.exists()) {
            Log.d(TAG, "getting image from google static maps");
            Bitmap image = null;
            try {
                URL urll = new URL(url);
                InputStream is = null;
                HttpURLConnection http = (HttpURLConnection)urll.openConnection();
                int statusCode = http.getResponseCode();
                Log.d(TAG, "getPath, status code:" + statusCode);
                if (statusCode >= 200 && statusCode < 400) {
                    // Create an InputStream in order to extract the response object
                    is = http.getInputStream();
                    image = BitmapFactory.decodeStream(is);
                }
                else {
                    is = http.getErrorStream();
                    for (int i = 0; i < is.available(); i++) {
                        System.out.println("" + is.read());
                    }
                }

                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        image_path = file.getPath();
        if (file.length() == 0) {
            file.delete();
        }
        return image_path;
    }



    @SuppressLint("MissingPermission")
    private void locationPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
        }
        else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                return;
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
            }
        }
    }

    private void setListeners() {
        startServiceListener();
        stopServiceListener();
        quickInputListener();
        newLogListener();
        viewLogsListener();
        quickCheckInListener();
        FilePickerListener();
        mailBackupListener();
        mapListener();

    }

    private void mapListener() {
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoggedInActivity.this, MapActivity.class);
                String text = refreshET.getText().toString();
                int refresh = 1000;
                if (!text.isEmpty()) refresh = Integer.parseInt(text);
                if (refresh == 0) refresh = 1;
                intent.putExtra("refresh", refresh);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void mailBackupListener() {
        mailBackupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoggedInActivity.this, SendMailActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void quickCheckInListener() {
        QuickCheckInBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thread myThread = new Thread(new Runnable(){
                    @Override
                    public void run()
                    {
                        String latitude=Double.toString(latituded);
                        String longitude=Double.toString(longituded);
                        String adress = getCompleteAddressString(latituded,longituded);
                        String path = getPath(latitude, longitude);
                        boolean insertlog = logdb.addData("QCK",null,latitude,longitude, path, ActiveLog.getInstance().getValue(),adress);

                        Message message = mHandler.obtainMessage();
                        message.arg1 = 0;
                        if (insertlog) {
                            message.arg1 = 1;
                        }

                        message.sendToTarget();
                    }
                });
                myThread.start();
            }
        });
    }



    private void viewLogsListener() {
        ViewLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLog.setText(ActiveLog.getInstance().getValue());
                Intent intent = new Intent(LoggedInActivity.this, LogViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
    }

    private void newLogListener() {
        StartNewLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLog.setText(ActiveLog.getInstance().getValue());
                Intent intent = new Intent(LoggedInActivity.this, StartLogActivity.class);
                startActivity(intent);

            }
        });
    }

    private void quickInputListener() {
        QuickInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentLog.setText(ActiveLog.getInstance().getValue());
                Intent intent = new Intent(LoggedInActivity.this, QuickInputActivity.class);
                intent.putExtra("latitude",latituded);
                intent.putExtra("longitude",longituded);
                startActivity(intent);

            }
        });
    }

    private void stopServiceListener() {
        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(LoggedInActivity.this, GPS_Service.class);
                stopService(serviceIntent);
                Toast.makeText(LoggedInActivity.this,"Stopped service", Toast.LENGTH_SHORT).show();
                stopServiceBtn.setEnabled(false);
                startServiceBtn.setEnabled(true);
            }
        });
    }

    private void startServiceListener() {
        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(LoggedInActivity.this, GPS_Service.class);
                stopService(serviceIntent);
                serviceIntent.putExtra("activeLog", ActiveLog.getInstance().getValue());
                String serviceTime = serviceTimeET.getText().toString();
                if (!serviceTime.isEmpty()) time = Integer.parseInt(serviceTime);
                if (time == 0) time = 1;
                serviceIntent.putExtra("time", time);

                ContextCompat.startForegroundService(LoggedInActivity.this, serviceIntent);

                Toast.makeText(LoggedInActivity.this,"Started service", Toast.LENGTH_SHORT).show();
                stopServiceBtn.setEnabled(true);
                startServiceBtn.setEnabled(false);
            }
        });
    }

    private void FilePickerListener(){
        GetFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("text/plain");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });
    }

    private void setLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("LoggedInActivity:", location.toString());
                latituded = location.getLatitude();
                longituded = location.getLongitude();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    private void getPrefs() {
        SharedPreferences prefs= this.getSharedPreferences("MyValues", 0);
        activeLog = prefs.getString("ActiveLog", "Default log");
        minTime = prefs.getLong("minTime", 2000);
        minDistance = prefs.getFloat("Distance", 1);
        time = prefs.getInt("time", 30);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "GPS_Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    private void findViewByID() {
        serviceTimeET = findViewById(R.id.serviceTimeET);
        refreshET = findViewById(R.id.refreshET);
        currentLog = findViewById(R.id.CurrentLogTextView);
        startServiceBtn = findViewById(R.id.startServiceBtn);
        stopServiceBtn = findViewById(R.id.stopServiceBtn);
        StartNewLogBtn = findViewById(R.id.StartNewLogBtn);
        QuickInputBtn = findViewById(R.id.QuickInputBtn);
        ViewLogsBtn = findViewById(R.id.ViewLogsBtn);
        QuickCheckInBtn = findViewById(R.id.QuickCheckInBtn);
        GetFileBtn = findViewById(R.id.GetFileBtn);
        mailBackupBtn = findViewById(R.id.mailBackupBtn);
        mapBtn = findViewById(R.id.mapBtn);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK){
            Uri content_describer = data.getData();
            String src = content_describer.getPath();
            source = new File(src);
            Log.d("src is ", source.toString());
            String filename = content_describer.getLastPathSegment();
            Log.d("FileName is ",filename);
            readFromFile(source);
        }
    }

    public void readFromFile(final File file){
        //TO-DO check if logs exist, add if they don't
        Thread myThread = new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    InputStream fis=new FileInputStream(file);
                    BufferedReader br=new BufferedReader(new InputStreamReader(fis));

                    for (String line = br.readLine(); line != null; line = br.readLine()) {
                        System.out.println("line:" + line);
                        String[] separatedline = line.split("\\|");
                        String lat, lng;
                        lat = separatedline[2];
                        lng = separatedline[3];
                        String path = getPath(lat, lng);
                        Log.d(TAG, "path from fileRead:" + path);
                        //String path = "";
                        String adding = separatedline[0]+separatedline[1]+separatedline[2]+separatedline[3]+ separatedline[4]+ separatedline[5]+ path;
                        Log.d(TAG, "adding:" + adding);
                        boolean insertlog = logdb.addMailData(separatedline[0],separatedline[1],separatedline[2],separatedline[3], separatedline[4], separatedline[5], path);
                    }

                    br.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        myThread.start();
    }






    public static String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Cur location address", strReturnedAddress.toString());
            } else {
                Log.w("My Cur location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Cur location address", "Cant get Address!");
        }
        return strAdd;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

    }
}
