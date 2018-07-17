package com.example.jakov.wherewasi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoggedInActivity extends AppCompatActivity {
    Button QuickCheckInBtn;
    DatabaseHelper logdb;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    double latituded;
    double longituded;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        Button QuickInputBtn=(Button) findViewById(R.id.QuickInputBtn);
        QuickInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoggedInActivity.this,QuickInputActivity.class);
                startActivity(intent);

            }
        });
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        getLocation();

        Button ViewLogsBtn=(Button) findViewById(R.id.ViewLogsBtn);
        ViewLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoggedInActivity.this,LogViewActivity.class);
                startActivity(intent);

            }
        });


        logdb = new DatabaseHelper(this);
        QuickCheckInBtn= (Button) findViewById(R.id.QuickCheckInBtn);
        adddata();


    }


    void getLocation() {
        if( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                latituded = location.getLatitude();
                longituded = location.getLongitude();

            }
        }

    }




    public void adddata(){
        QuickCheckInBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //get langitude and longitude
                getLocation();
                String latitude=Double.toString(latituded);
                String longitude=Double.toString(longituded);
                String name = getCompleteAddressString(latituded,longituded);
                boolean insertlog = logdb.addData(null,name,latitude,longitude);
                if (insertlog==true){
                    Toast.makeText(LoggedInActivity.this,"INSERTED",Toast.LENGTH_LONG).show();

                }
                else Toast.makeText(LoggedInActivity.this,"NOPE",Toast.LENGTH_LONG).show();

            }
        });
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

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }
}
