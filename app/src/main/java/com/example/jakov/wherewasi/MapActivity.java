package com.example.jakov.wherewasi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    DatabaseHelper mDatabaseHelper;
    DatabaseHelper mDatabaseHelper2;
    public static ArrayList<LogEntry> listData;
    ArrayList<String> listDataSpinner;
    boolean moveCamera = false;
    String name = ActiveLog.getInstance().getValue();
    LatLng startLocation;
    FloatingActionButton startAnimation;
    GoogleMap map;
    int count = 0;
    ArrayList<Marker> markerList;
    TextView timeTV;
    Handler handler = new Handler();
    Handler handler2 = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper2 = new DatabaseHelper(this, "logs_table");

        getData();

        if (listData.size() > 0) {
            moveCamera = true;
            LogEntry start = listData.get(0);
            startLocation = new LatLng(Double.parseDouble(start.getLatitude()), Double.parseDouble(start.getLongitude()));
        }


        timeTV = findViewById(R.id.timeTV);

        startAnimation = findViewById(R.id.startAnimation);


        startAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mapAnimation(getIntent().getExtras().getInt("refresh"));

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
        map.setOnInfoWindowClickListener(this);

        googleMap.setMinZoomPreference(8);
        googleMap.setMyLocationEnabled(true);
        if (moveCamera) googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 12.0f));


        markerList = new ArrayList<>();
        int i = 0;
        for (LogEntry le : listData) {
            LatLng pos = new LatLng(Double.parseDouble(le.getLatitude()), Double.parseDouble(le.getLongitude()));
            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .anchor(0.5f, 0.5f)
                    .title(le.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(42))
                    .snippet(le.getTimestamp() + "   " + le.getLatitude() + " " + le.getLongitude()));
            m.setTag(i++);
            markerList.add(m);
        }
    }

    private void mapAnimation(final long time) {

        map.clear();
        count = 0;
        int updateNumberHolder = 1;
        if (time < 1500) updateNumberHolder = (int) Math.ceil(1500/time);
        final int updateNumber = updateNumberHolder;
        final int size = markerList.size() - 1;

        final Runnable updatePositions = new Runnable() {

            public void run() {

                Marker m = markerList.get(count);
                final Marker m2 = map.addMarker(new MarkerOptions()
                        .position(m.getPosition())
                        .anchor(0.5f, 0.5f)
                        .title(m.getTitle())
                        .icon(BitmapDescriptorFactory.defaultMarker(42))
                        .snippet(m.getSnippet()));
                m2.setTag(count);


                final Runnable removeMarker = new Runnable() {
                    @Override
                    public void run() {
                       m2.remove();
                    }
                };
                handler2.postDelayed(removeMarker, time*10);


                if (count % updateNumber == 0) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLng(m.getPosition());
                    map.animateCamera(cu);
                    //CaptureMapScreen();
                }
                String timeStamp = listData.get(count).getTimestamp();
                timeTV.setText(timeStamp);
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


    public void CaptureMapScreen()
    {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                try {
                    String path = Environment.getExternalStorageDirectory() + File.separator + ".WhereWasI" + File.separator + "MapScreenshots";
                    FileOutputStream out = new FileOutputStream(path + System.currentTimeMillis()
                            + ".png");

                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        map.snapshot(callback);


    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this,DialogActivity.class);
        int position = (Integer) marker.getTag();
        LogEntry entry = listData.get(position);

        intent.putExtra("name",entry.getName());
        intent.putExtra("description",entry.getDescription());
        intent.putExtra("path",entry.getPath());
        intent.putExtra("timestamp",entry.getTimestamp());
        intent.putExtra("latitude",entry.getLatitude());
        intent.putExtra("longitude",entry.getLongitude());
        intent.putExtra("adress",entry.getAdress());
        intent.putExtra("position",position);
        intent.putExtra("activity","Map");
        startActivity(intent);



    }
}