package com.example.jakov.wherewasi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LoggedInActivity extends AppCompatActivity implements AddLogDialog.AddLogDialogListener, SearchDialog.SearchDialogListener {
    private static final String TAG = "LoggedInActivity";
    private Button deleteLogs, openViewBtn,fileBackupBtn,QuickCheckInBtn, QuickInputBtn, startServiceBtn, stopServiceBtn, StartNewLogBtn,GetFileBtn;
    private Spinner pickLog;


    private EditText serviceTimeET;
    private AdView simpleBannerAd;

    private File source;

    private DatabaseHelper logdb;
    private DatabaseHelper mDatabaseHelper2;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public double latituded,longituded;

    private String activeLog;
    private Long minTime;
    private Float minDistance;
    private int time;
    private int check = 0;
    private ArrayList<String> listDataSpinner;
    private HashMap<String, String> logNameID;

    private Handler mHandler;

    private static DecimalFormat numberFormat = new DecimalFormat("#.0000");

    private final int PICKFILE_RESULT_CODE = 2;
    public static final String CHANNEL_ID = "GPS_Service";
    static final int REQUEST_LOCATION = 1;

    SharedPreferences prefs;

    private static Geocoder geocoder;

    protected void onDestroy() {
        super.onDestroy();


    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        activeLog=prefs.getString("ActiveLog", "Default log");
        check = 0;
        loadSpinnerData();
        Log.d(TAG, "onResume: " + prefs.getString("serviceRunning", "false"));
        if(prefs.getString("serviceRunning", "false").equals("true")){
            stopServiceBtn.setEnabled(true);
            startServiceBtn.setEnabled(false);
        }
        super.onResume();
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs =  this.getSharedPreferences("MyValues", 0);
        setContentView(R.layout.activity_logged_in);

        getSupportActionBar().hide();



        logNameID = new HashMap<>();
        // My App ID: ca-app-pub-3775405938489529~5074959444
        MobileAds.initialize(this, "ca-app-pub-3775405938489529~5074959444");

        findViewByID();

        createNotificationChannel();

        getPrefs();

        setHandler();





        serviceTimeET.setTransformationMethod(null);
        logdb = new DatabaseHelper(this);
        mDatabaseHelper2 = new DatabaseHelper(this, "logs_table");

        loadSpinnerData();
        pickLog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected1 " + check);
                if (++check > 1) {
                    String newActive = listDataSpinner.get(i);
                    Log.d(TAG, "onItemSelected: " + i + " " + newActive);
                    SharedPreferences.Editor saveValue = prefs.edit();
                    saveValue.putString("ActiveLog", newActive);
                    saveValue.apply();
                    activeLog = newActive;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setLocationManager();


        setListeners();


        verifyPermissions();

        simpleBannerAd = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        simpleBannerAd.loadAd(adRequest);

        geocoder = new Geocoder(this, Locale.getDefault());
        Log.d(TAG, "onCreate:servic " + prefs.getString("serviceRunning", "false"));
        if(prefs.getString("serviceRunning", "false").equals("true")){
            stopServiceBtn.setEnabled(true);
            startServiceBtn.setEnabled(false);
        }
        else {
            stopServiceBtn.setEnabled(false);
            startServiceBtn.setEnabled(true);
        }

        QuickCheckInBtn.setEnabled(false);
        QuickInputBtn.setEnabled(false);
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



    @SuppressLint("MissingPermission")
    private void verifyPermissions(){
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};
Log.d("permissionLog","1");
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    permissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    permissions[1]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    permissions[2]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    permissions[3]) == PackageManager.PERMISSION_GRANTED ){
                Log.d("permissionLog","2");
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && haveNetworkConnection()){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
                }
                else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
                }
                }else{
                ActivityCompat.requestPermissions(LoggedInActivity.this,
                        permissions,
                        1);
                Log.d("permissionLog","3");
            }
        } else {
            Log.d("permissionLog","4");
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && haveNetworkConnection()){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
            }
            else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
            }}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("grantRessult",grantResults.toString());

        if(grantResults.length>0 /*&& grantResults[0]==PackageManager.PERMISSION_GRANTED*/){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
                    ||ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                Log.d("permissionLog","5");
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)  && haveNetworkConnection()){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
                }
                else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
                }}
        }

    }



    private void setListeners() {
        startServiceListener();
        stopServiceListener();
        quickInputListener();
        newLogListener();
        quickCheckInListener();
        FilePickerListener();
        fileBackupListener();
        openViewListener();
        deleteLogsListener();

    }

    private void deleteLogsListener() {
        deleteLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoggedInActivity.this);

                final boolean[] checkedItems = new boolean[listDataSpinner.size()];
                Arrays.fill(checkedItems, Boolean.FALSE);
                builder.setTitle("Delete logs");
                String[] logsArray = new String[listDataSpinner.size()];
                logsArray = listDataSpinner.toArray(logsArray);
                //set multichoice
                builder.setMultiChoiceItems(logsArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedItems[which] = isChecked;
                    }
                });
                // Set the positive/yes button click listener
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click positive button
                        for (int i = 0; i<checkedItems.length; i++){
                            boolean checked = checkedItems[i];
                            if (checked) {
                                String logName = listDataSpinner.get(i);
                                Log.d(TAG, "onClick:delete " + logName);
                                if (logName.equals("Default log")){
                                    logdb.deleteEntryName(logName);
                                }
                                else {
                                    mDatabaseHelper2.deleteLog(logNameID.get(logName),logName);
                                    logdb.deleteEntryName(logName);
                                }

                            }
                        }
                        loadSpinnerData();
                    }
                });
                // Set the neutral/cancel button click listener
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when click the neutral button
                    }
                });
                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
            }
        });
    }


    private void fileBackupListener() {
        fileBackupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchDialog searchDialog=new SearchDialog();
                Bundle args = new Bundle();
                args.putSerializable("logs", listDataSpinner);
                args.putString("layout", "filter");
                searchDialog.setArguments(args);
                searchDialog.show(getSupportFragmentManager(),"searchDialog");
            }
        });
    }

    private void openViewListener() {
        openViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoggedInActivity.this, ViewActivity.class);
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
                        String latitude=Double.toString(latituded) + "0000";
                        String longitude=Double.toString(longituded) + "0000";
                        String adress = getCompleteAddressString(latituded,longituded);
                        String path = "";
                        boolean insertlog = logdb.addData("QCK",null,latitude,longitude, path, activeLog,adress);

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



    private void newLogListener() {
        StartNewLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddLogDialog addLogDialog = new AddLogDialog();
                addLogDialog.show(getSupportFragmentManager(),"addLogDialog");

            }
        });
    }

    private void quickInputListener() {
        QuickInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                Log.d(TAG, "stops " + prefs.getString("ActiveLog", "err") + " " + activeLog );
                Toast.makeText(LoggedInActivity.this,"Stopped service", Toast.LENGTH_SHORT).show();
                stopServiceBtn.setEnabled(false);
                startServiceBtn.setEnabled(true);
                SharedPreferences.Editor saveValue = prefs.edit();
                saveValue.putString("serviceRunning", "false");
                saveValue.apply();
            }
        });
    }

    private void startServiceListener() {
        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(LoggedInActivity.this, GPS_Service.class);
                stopService(serviceIntent);
                Log.d(TAG, "starts " + prefs.getString("ActiveLog", "err") + " " + activeLog );
                serviceIntent.putExtra("activeLog", activeLog);
                String serviceTime = serviceTimeET.getText().toString();
                if (!serviceTime.isEmpty()) time = Integer.parseInt(serviceTime);
                if (time == 0) time = 1;
                serviceIntent.putExtra("time", time);



                ContextCompat.startForegroundService(LoggedInActivity.this, serviceIntent);

                Toast.makeText(LoggedInActivity.this,"Started service", Toast.LENGTH_SHORT).show();
                stopServiceBtn.setEnabled(true);
                startServiceBtn.setEnabled(false);
                SharedPreferences.Editor saveValue = prefs.edit();
                saveValue.putString("serviceRunning", "true");
                saveValue.apply();
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
                QuickCheckInBtn.setEnabled(true);
                QuickInputBtn.setEnabled(true);
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
        serviceTimeET.setTransformationMethod(null);
        pickLog = findViewById(R.id.LogsSpinner);
        startServiceBtn = findViewById(R.id.startServiceBtn);
        stopServiceBtn = findViewById(R.id.stopServiceBtn);
        StartNewLogBtn = findViewById(R.id.StartNewLogBtn);
        QuickInputBtn = findViewById(R.id.QuickInputBtn);
        QuickCheckInBtn = findViewById(R.id.QuickCheckInBtn);
        GetFileBtn = findViewById(R.id.GetFileBtn);
        fileBackupBtn = findViewById(R.id.fileBackupBtn);
        openViewBtn = findViewById(R.id.openViewBtn);
        deleteLogs = findViewById(R.id.deleteLogs);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            Log.d(TAG, "onActivityResult: "  + uri);
            readFromFile(uri);
        }
    }



    public void readFromFile(final Uri uri){
        //TO-DO check if logs exist, add if they don't

        Thread myThread = new Thread(new Runnable(){
            @Override
            public void run()
            {
                try{
                    InputStream fis = getContentResolver().openInputStream(uri);
                    BufferedReader br=new BufferedReader(new InputStreamReader(fis));
                    int i = 0;


                    for (String line = br.readLine(); line != null; line = br.readLine()) {
                        System.out.println("line:" + line);
                        String[] separatedline = line.split("\\|");
                        String lat, lng;
                        lat = separatedline[2];
                        lng = separatedline[3];
                        String path = "";
                        Log.d(TAG, "path from fileRead:" + path);
                        //String path = "";
                        String adding = separatedline[0]+separatedline[1]+separatedline[2]+separatedline[3]+ separatedline[4]+ separatedline[5]+ path;
                        Log.d(TAG, "adding:" + adding);
                        boolean insertlog = logdb.addMailData(separatedline[0],separatedline[1],separatedline[2],separatedline[3], separatedline[4], separatedline[5], path);
                        if (insertlog) i++;
                        if (!listDataSpinner.contains(separatedline[4])) {
                            mDatabaseHelper2.addLog(separatedline[4], false);

                        }
                    }

                    br.close();

                    final int finalI = i;
                    LoggedInActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadSpinnerData();
                            toast("Importing from file finished. Added " + finalI + " entries");
                        }
                    });
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        myThread.start();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void loadSpinnerData() {
        // database handler
        listDataSpinner = new ArrayList<>();
        Cursor data = mDatabaseHelper2.getLogData();
        while(data.moveToNext()){
            Log.d(TAG, "adding DATA:" + data.getString(1));
            listDataSpinner.add(data.getString(1));
            logNameID.put(data.getString(1), data.getString(0));
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, listDataSpinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickLog.setAdapter(spinnerAdapter);

        int pos = listDataSpinner.indexOf(activeLog);
        pickLog.setSelection(pos);

    }

    @Override
    public void addLog(String logName, boolean setActive) {
        Log.d(TAG, "addLog: " + logName + " " + setActive);
        mDatabaseHelper2.addLog(logName, setActive);
        activeLog = prefs.getString("ActiveLog", "Default log");
        loadSpinnerData();


    }

    private String subString(String string) {
        if (string.length() > 10) return string.substring(0,10);
        else return string;
    }

    @Override
    public void applyText(final String entryName, final String dateTo, final String dateFrom, final ArrayList<String> logs) {
        Thread myThread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Cursor data = null;
                    data = logdb.getBackupData();

                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    String date = df.format(Calendar.getInstance().getTime());
                    String path = Environment.getExternalStorageDirectory() + File.separator + "WhereWasI" + File.separator + "fileBackup";
                    File directory = new File(path);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    final File backupFile = new File(directory, "backup " + date + ".txt");
                    BufferedWriter bw = new BufferedWriter(new FileWriter(backupFile));

                    while(data.moveToNext()){
                        LogEntry entry = new LogEntry(data.getString(0),data.getString(1) , subString(data.getString(2)),subString(data.getString(3)),
                                null, "", "", data.getString(5), data.getString(4));
                        if (entry.getName().toLowerCase().contains(entryName.toLowerCase())
                                && (logs.size() == 0 || logs.contains("ALL LOGS") || logs.contains(entry.getLogName()))
                                &&(dateFrom==""  || dateFrom==null || Integer.parseInt(entry.getTimestamp().substring(0,10).replace(".",""))>Integer.parseInt(dateFrom))&&(dateTo==""|| dateTo==null || Integer.parseInt(entry.getTimestamp().substring(0,10).replace(".",""))<Integer.parseInt(dateTo))) {
                            String content = data.getString(0) + "|" + data.getString(1) + "|" + data.getString(2) + "|"
                                    + data.getString(3) + "|" + data.getString(4) + "|" + data.getString(5);
                            Log.d("data from DB", "content: " + content);
                            bw.write(content);
                        }


                    }

                    LoggedInActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoggedInActivity.this, "created file " + backupFile.getName() + " in " + backupFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + backupFile.getAbsolutePath()));
                    startActivity(Intent.createChooser(sharingIntent, "share file with"));

                    bw.close();




                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        myThread.start();
    }
}
