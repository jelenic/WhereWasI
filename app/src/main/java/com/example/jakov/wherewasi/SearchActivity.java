package com.example.jakov.wherewasi;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {

    Button searchBtn;
    CheckBox nameBox;
    CheckBox dateBox;
    CheckBox locationBox;
    EditText searchNameText;
    TextView searchDateFromText;
    TextView searchDateToText;
    EditText searchLocationText;
    String dateFrom;
    String dateTo;
    private DatePickerDialog.OnDateSetListener mDateListenerFrom;
    private DatePickerDialog.OnDateSetListener mDateListenerTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        nameBox = (CheckBox) findViewById(R.id.nameBox);
        dateBox = (CheckBox) findViewById(R.id.dateBox);
        locationBox = (CheckBox) findViewById(R.id.locationBox);
        searchNameText = (EditText) findViewById(R.id.searchNameText);
        searchDateFromText = (TextView) findViewById(R.id.searchDateFromText);
        searchDateToText = (TextView) findViewById(R.id.searchDateToText);
        searchLocationText = (EditText) findViewById(R.id.searchLocationText);



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
                    intent.putExtra("locationfilter",searchNameText.getText().toString());
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
}