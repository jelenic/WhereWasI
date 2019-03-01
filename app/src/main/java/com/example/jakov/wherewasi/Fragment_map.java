package com.example.jakov.wherewasi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;


public class Fragment_map extends Fragment implements OnMapReadyCallback {
    boolean moveCamera = false;
    LatLng startLocation;
    GoogleMap map;
    FloatingActionButton startAnimation;
    TextView timeTV;
    int count = 0;
    ArrayList<Marker> markerList;
    Handler handler = new Handler();
    Handler handler2 = new Handler();
    boolean animation = false;




    @Nullable
    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startAnimation = view.findViewById(R.id.startAnimation);

        startAnimation.setEnabled(animation);

        startAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapAnimation(200);

            }
        });

        timeTV = view.findViewById(R.id.timeTV);
        return view;


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (((ViewActivity) getActivity()).getListData().size() > 0) {
            moveCamera = true;
            animation = true;
            LogEntry start = ((ViewActivity) getActivity()).getListData().get(0);
            startLocation = new LatLng(Double.parseDouble(start.getLatitude()), Double.parseDouble(start.getLongitude()));
        }

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;


        googleMap.setMinZoomPreference(8);
        googleMap.setMyLocationEnabled(true);
        if (moveCamera) googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 12.0f));


        loadMarkers();
    }

    private void loadMarkers() {
        markerList = new ArrayList<>();
        int i = 0;
        for (LogEntry le : ((ViewActivity) getActivity()).getListData()) {
            LatLng pos = new LatLng(Double.parseDouble(le.getLatitude()), Double.parseDouble(le.getLongitude()));
            Marker m = map.addMarker(new MarkerOptions()
                    .position(pos)
                    .anchor(0.5f, 0.5f)
                    .title(le.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(42))
                    .snippet(le.getTimestamp() + "   " + le.getLatitude() + " " + le.getLongitude()));
            m.setTag(i++);
            markerList.add(m);
        }
        Collections.reverse(markerList);
    }

    private void mapAnimation(final long time) {
        startAnimation.setBackgroundResource(android.R.drawable.ic_media_pause);
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
                }
                String timeStamp = null;
                try {
                    timeStamp = ((ViewActivity) getActivity()).getListData().get(count).getTimestamp();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                timeTV.setText(timeStamp);
                if (count < size) {
                    count++;
                    handler.postDelayed(this, time);
                }
                else {
                    handler.removeCallbacks(this);
                    loadMarkers();
                }



            }
        };
        handler.removeCallbacksAndMessages (null);
        handler.postDelayed(updatePositions, 500);
    }



    private void openDialogActivity(Marker marker) {
        Intent intent = new Intent(getActivity(), DialogActivity.class);
        int position = (Integer) marker.getTag();
        LogEntry entry = ((ViewActivity) getActivity()).getListData().get(position);

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