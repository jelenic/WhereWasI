package com.example.jakov.wherewasi;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.jakov.wherewasi.LoggedInActivity.CHANNEL_ID;

public class GPS_Service extends Service {
    private LocationManager locationManager;
    private LocationListener locationListener;
    double longitude, latitude;
    private DatabaseHelper logdb;
    private String activeLog;
    private boolean shouldContinue = true;
    private final int NOTIF_ID = 1;
    private NotificationManager mNotificationManager;
    private int time;





    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        logdb = new DatabaseHelper(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("LoggedInActivity:", location.toString());
                latitude = location.getLatitude();
                longitude = location.getLongitude();

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
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && haveNetworkConnection()){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*time, 0, locationListener);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*time, 0, locationListener);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*time, 0, locationListener);
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

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        activeLog = intent.getStringExtra("activeLog");
        time = intent.getIntExtra("time",2);


        Notification notification = getMyActivityNotification("service started");

        startForeground(NOTIF_ID, notification);

        Thread myThread = new Thread(new Runnable(){
            @Override
            public void run()
            {
                while (shouldContinue) {
                    addData();
                    try {

                        Thread.sleep(1000*time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        myThread.start();


        return START_NOT_STICKY;
    }





    private Notification getMyActivityNotification (String text) {
        Intent notificationIntent = new Intent(this, LoggedInActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GPS_Service")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .setContentText(text)
                .setOnlyAlertOnce(true)
                .build();
    }

    @Override
    public void onDestroy() {
        Log.d("GPS_Service", "onDestroy: service stopped");
        shouldContinue = false;
    }

    public void addData() {
        String lat=Double.toString(latitude) + "0000";
        String longi=Double.toString(longitude) + "0000";
        String adress = LoggedInActivity.getCompleteAddressString(latitude,longitude);
        final String path = "";
        boolean insertlog = logdb.addData("Service",null,lat,longi, path, activeLog,adress);

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        final String date = df.format(Calendar.getInstance().getTime());
        String text = date + "  " + adress;
        Notification notification = getMyActivityNotification(text);
        mNotificationManager.notify(NOTIF_ID, notification);

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
