package com.example.jakov.wherewasi;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

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
    boolean time = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {


        googleMap.setMinZoomPreference(8);
        googleMap.setMyLocationEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.82, 15.99), 12.0f));


        final ArrayList<Marker> markerList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {
                LatLng pos = new LatLng(45.78 , 15.95 );
                markerList.add(googleMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .anchor(0.5f, 0.5f)
                        .title("Marker" + (i*10 + j))
                        .icon(BitmapDescriptorFactory.defaultMarker((i*10 + j) * 3.5f))
                        .snippet(pos.latitude + " " + pos.longitude)));

            }

        }

        final Handler handler = new Handler();
        final Runnable updatePositions = new Runnable() {
            public void run() {
                double moveDistance = 0.0005;
                Random rand = new Random();
                for (Marker m : markerList) {

                    int  broj = rand.nextInt(12) + 1;
                    moveDistance = broj / 25000.;
                    LatLng pos = m.getPosition();
                    switch (broj) {
                        case 1:  pos = new LatLng(pos.latitude - moveDistance, pos.longitude );
                            break;
                        case 2:  pos = new LatLng(pos.latitude + moveDistance, pos.longitude );
                            break;
                        case 3:  pos = new LatLng(pos.latitude, pos.longitude - moveDistance);
                            break;
                        case 4:  pos = new LatLng(pos.latitude, pos.longitude + moveDistance);
                            break;
                        case 5:  pos = new LatLng(pos.latitude - moveDistance, pos.longitude - moveDistance);
                            break;
                        case 6:  pos = new LatLng(pos.latitude - moveDistance, pos.longitude + moveDistance);
                            break;
                        case 7:  pos = new LatLng(pos.latitude + moveDistance, pos.longitude - moveDistance);
                            break;
                        case 8:  pos = new LatLng(pos.latitude + moveDistance, pos.longitude + moveDistance);
                            break;

                    }
                    m.setPosition(pos);
                    m.setSnippet(pos.latitude + " " + pos.longitude);

                }
                handler.postDelayed(this, 33);

            }
        };

        handler.postDelayed(updatePositions, 3000);

    }


}
