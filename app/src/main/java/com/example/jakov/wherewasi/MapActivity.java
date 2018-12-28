package com.example.jakov.wherewasi;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    DatabaseHelper mDatabaseHelper;
    DatabaseHelper mDatabaseHelper2;
    public static ArrayList<LogEntry> listData;
    ArrayList<String> listDataSpinner;
    boolean time = true;
    String name = ActiveLog.getInstance().getValue();
    LatLng startLocation;
    FloatingActionButton startAnimation;
    GoogleMap map;
    int count = 0;
    ArrayList<Marker> markerList;
    TextView timeTV;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper2 = new DatabaseHelper(this, "logs_table");

        getData();

        LogEntry start = listData.get(0);
        startLocation = new LatLng(Double.parseDouble(start.getLatitude()), Double.parseDouble(start.getLongitude()));

        timeTV = findViewById(R.id.timeTV);

        startAnimation = findViewById(R.id.startAnimation);


        startAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mapAnimation( getIntent().getExtras().getInt("refresh"));
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getData() {
        Cursor data = null;
        if (name.equals("ALL LOGS")) {
            data = mDatabaseHelper.getDataASC();
        }
        else
            data = mDatabaseHelper.getDataASC(name);
        listData = new ArrayList<>();


        while(data.moveToNext()){
            listData.add(new LogEntry(data.getString(0),data.getString(1) ,
                    data.getString(4),data.getString(3),null , data.getString(5),
                    data.getString(2), data.getString(7)));

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;

        googleMap.setMinZoomPreference(8);
        googleMap.setMyLocationEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 12.0f));


        markerList = new ArrayList<>();
        for (LogEntry le : listData) {
            LatLng pos = new LatLng(Double.parseDouble(le.getLatitude()), Double.parseDouble(le.getLongitude()));
            markerList.add(googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .anchor(0.5f, 0.5f)
                    .title(le.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(42))
                    .snippet(le.getTimestamp() + "   " + le.getLatitude() + " " + le.getLongitude())));


        }



    }

    private void mapAnimation(final long time) {
        map.clear();
        count = 0;
        final int updateNumber = (int) Math.ceil(1500/time);
        final int size = markerList.size() - 1;

        final Runnable updatePositions = new Runnable() {

            public void run() {

                Marker m = markerList.get(count);

                map.addMarker(new MarkerOptions()
                        .position(m.getPosition())
                        .anchor(0.5f, 0.5f)
                        .title(m.getTitle())
                        .icon(BitmapDescriptorFactory.defaultMarker(42))
                        .snippet(m.getSnippet()));

                if (count % updateNumber == 0) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLng(m.getPosition());
                    map.animateCamera(cu);
                }
                timeTV.setText(listData.get(count).getTimestamp());
                if (count < size) {
                    count++;
                    handler.postDelayed(this, time);
                }
                else {
                    handler.removeCallbacks(this);
                }



            }
        };
        handler.removeCallbacksAndMessages (null);
        handler.postDelayed(updatePositions, 500);
    }


}
