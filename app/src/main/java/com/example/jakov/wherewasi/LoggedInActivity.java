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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class LoggedInActivity extends AppCompatActivity {
    private static final String TAG = "LoggedInActivity";
    private Button QuickCheckInBtn, QuickInputBtn, setMin, startServiceBtn, stopServiceBtn, StartNewLogBtn, ViewLogsBtn , GetFileBtn;
    private TextView currentLog;
    private EditText timeET, distanceET, serviceTimeET;
    private final int PICKFILE_RESULT_CODE = 2;
    private File source;

    private DatabaseHelper logdb;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public double latituded,longituded;

    private String activeLog;
    //private Boolean putin;
    private Long minTime;
    private Float minDistance;
    private int time;

    static DecimalFormat numberFormat = new DecimalFormat("#.0000");

    public static final String CHANNEL_ID = "GPS_Service";
    static final int REQUEST_LOCATION = 1;

    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = this.getSharedPreferences("MyValues", 0);
        SharedPreferences.Editor saveValue = prefs.edit();
        activeLog = ActiveLog.getInstance().getValue();
        saveValue.putString("ActiveLog", activeLog);
        //saveValue.putBoolean("Putin", putin);
        saveValue.putLong("minTime", minTime);
        saveValue.putFloat("Distance", minDistance);
        saveValue.putInt("time", time);
        saveValue.commit();
    }





    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        findViewByID();

        createNotificationChannel();

        getPrefs();


        currentLog.setText(activeLog);
        ActiveLog.getInstance().setValue(activeLog);
        logdb = new DatabaseHelper(this);

        setLocationManager();


        setListeners();


        locationPermissions();
    }


    public static String getPath(String url, String lat, String lng) {
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
                InputStream is = (InputStream) new URL(url).getContent();
                image = BitmapFactory.decodeStream(is);
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
        setMinListener();
        quickCheckInListener();
        FilePickerListener();

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
                        String url = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=17&size=640x640&markers=color:blue%7C%7C" + latitude + "," + longitude + "&sensor=false";
                        String path = getPath(url, latitude, longitude);
                        boolean insertlog = logdb.addData("QCK",null,latitude,longitude, path, ActiveLog.getInstance().getValue(),adress);
                    }
                });
                myThread.start();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void setMinListener() {
        setMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String minTimeSet = timeET.getText().toString();
                String minDistSet = distanceET.getText().toString();
                if (!minTimeSet.isEmpty()) {
                    minTime = 1000 * Long.parseLong(minTimeSet);
                }
                if (!minDistSet.isEmpty()) {
                    minDistance = Float.parseFloat(minDistSet);
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
                Toast.makeText(LoggedInActivity.this, "time:" + minTimeSet + "|distance:" + minDistSet, Toast.LENGTH_SHORT).show();
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
                serviceIntent.putExtra("time", time);

                ContextCompat.startForegroundService(LoggedInActivity.this, serviceIntent);

                Toast.makeText(LoggedInActivity.this,"Started service", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void FilePickerListener(){
        GetFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType(".txt");
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
        //putin = prefs.getBoolean("Putin", true);
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
        currentLog = findViewById(R.id.CurrentLogTextView);
        startServiceBtn = findViewById(R.id.startServiceBtn);
        stopServiceBtn = findViewById(R.id.stopServiceBtn);
        StartNewLogBtn = findViewById(R.id.StartNewLogBtn);
        QuickInputBtn = findViewById(R.id.QuickInputBtn);
        ViewLogsBtn = findViewById(R.id.ViewLogsBtn);
        timeET = findViewById(R.id.timeET);
        distanceET = findViewById(R.id.distanceET);
        setMin = findViewById(R.id.setMin);
        QuickCheckInBtn = findViewById(R.id.QuickCheckInBtn);
        GetFileBtn = findViewById(R.id.GetFileBtn);

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
        }
    }

    public void readFromFile(final File file){
        Thread myThread = new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    InputStream fis=new FileInputStream(file);
                    BufferedReader br=new BufferedReader(new InputStreamReader(fis));

                    for (String line = br.readLine(); line != null; line = br.readLine()) {
                        System.out.println(line);
                        String[] separatedline=line.split("|");
                        boolean insertlog = logdb.addMailData(separatedline[0],separatedline[1],separatedline[2],separatedline[3], separatedline[4], separatedline[5]);
                    }

                    br.close();
                }
                catch(Exception e){
                    System.err.println("Error: Target File Cannot Be Read");
                }
            }
        });
        myThread.start();
    }






    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
