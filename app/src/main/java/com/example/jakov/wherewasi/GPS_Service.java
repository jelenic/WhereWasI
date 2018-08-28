package com.example.jakov.wherewasi;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        activeLog = intent.getStringExtra("activeLog");
        time = intent.getIntExtra("time",2);
        Intent notificationIntent = new Intent(this, LoggedInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

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
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        String lat=Double.toString(latitude);
        String longi=Double.toString(longitude);
        String adress = getCompleteAddressString(latitude,longitude);
        String url = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=17&size=640x640&markers=color:blue%7C%7C" + latitude + "," + longitude + "&sensor=false";
        String path = LoggedInActivity.getPath(url, lat, longi);
        boolean insertlog = logdb.addData("Service",null,lat,longi, path, activeLog,adress);

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        String text = date + "  " + adress;
        Notification notification = getMyActivityNotification(text);
        mNotificationManager.notify(NOTIF_ID, notification);
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
