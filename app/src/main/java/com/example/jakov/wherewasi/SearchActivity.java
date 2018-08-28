package com.example.jakov.wherewasi;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {

    Button searchBtn;
    CheckBox nameBox;
    CheckBox dateBox;
    CheckBox locationBox;
    EditText searchNameText, radiusET;
    TextView searchDateFromText;
    TextView searchDateToText;
    TextView searchLocationText;
    String dateFrom;
    String dateTo;
    private DatePickerDialog.OnDateSetListener mDateListenerFrom;
    private DatePickerDialog.OnDateSetListener mDateListenerTo;
    int PLACE_PICKER_REQUEST = 1;
    PlacePicker.IntentBuilder builder;
    Double longitude, latitude;
    DecimalFormat numberFormat = new DecimalFormat("#.00000");

    private static final int ERROR_DIALOG_REQUEST = 9001;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                LatLng latLng = place.getLatLng();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                searchLocationText.setText(numberFormat.format(latitude) + "  " + numberFormat.format(longitude));

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchBtn = findViewById(R.id.searchBtn);
        nameBox = findViewById(R.id.nameBox);
        dateBox = findViewById(R.id.dateBox);
        locationBox = findViewById(R.id.locationBox);
        searchNameText = findViewById(R.id.searchNameText);
        searchDateFromText = findViewById(R.id.searchDateFromText);
        searchDateToText = findViewById(R.id.searchDateToText);
        searchLocationText = findViewById(R.id.searchLocationText);
        radiusET = findViewById(R.id.radiusET);


        if(isServicesOK()){
            init();
        }

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchActivity.this,SearchResultActivity.class);
                if (nameBox.isChecked() && searchNameText.getText()!=null){
                    intent.putExtra("namefilter",searchNameText.getText().toString());
                }
                if (dateBox.isChecked()){
                    if (searchDateFromText.getText()!=null){
                        dateFrom = searchDateFromText.getText().toString();
                    }
                    if (searchDateToText.getText()!=null){
                        dateTo = searchDateToText.getText().toString();
                    }
                    intent.putExtra("datefromfilter",dateFrom);
                    intent.putExtra("datetofilter",dateTo);
                }
                if (locationBox.isChecked()){
                    intent.putExtra("latitude",latitude);
                    intent.putExtra("longitude",longitude);
                    if (!radiusET.getText().toString().isEmpty()) {
                        intent.putExtra("radius", Float.parseFloat(radiusET.getText().toString()));
                    }
                }

                startActivity(intent);



            }
        });
        searchDateFromText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal= Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog=new DatePickerDialog(SearchActivity.this,android.R.style.Theme_Holo_Dialog_MinWidth,mDateListenerFrom,year,month,day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();

            }
        });
        mDateListenerFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String m,d;
                m=month+"";
                d=day+"";
                if (month<10){
                    m="0" + month;
                }
                if (day<10){
                    d="0"+day;
                }

                dateFrom = year + "." +m + "." + d;
                searchDateFromText.setText(dateFrom);

            }
        };
        searchDateToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal= Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dateDialog=new DatePickerDialog(SearchActivity.this,android.R.style.Theme_Holo_Dialog_MinWidth,mDateListenerTo,year,month,day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();

            }
        });
        mDateListenerTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String m,d;
                m=month+"";
                d=day+"";
                if (month<10){
                    m="0" + month;
                }
                if (day<10){
                    d="0"+day;
                }

                dateTo = year + "." +m + "." + d;
                searchDateToText.setText(dateTo);

            }
        };


    }

    public boolean isServicesOK(){
        Log.d("SearchActivity", "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SearchActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d("SearchActivity", "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d("SearchActivity", "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SearchActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void init(){
        searchLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SearchActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}