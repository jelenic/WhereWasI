package com.example.jakov.wherewasi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {

    Button searchBtn;
    CheckBox nameBox;
    CheckBox dateBox;
    CheckBox locationBox;
    EditText searchNameText;
    EditText searchDateFromText;
    EditText searchDateToText;
    EditText searchLocationText;
    String dateFrom="00,00,00";
    String dateTo="01,01,3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        nameBox = (CheckBox) findViewById(R.id.nameBox);
        dateBox = (CheckBox) findViewById(R.id.dateBox);
        locationBox = (CheckBox) findViewById(R.id.locationBox);
        searchNameText = (EditText) findViewById(R.id.searchNameText);
        searchDateFromText = (EditText) findViewById(R.id.searchDateFromText);
        searchDateToText = (EditText) findViewById(R.id.searchDateToText);
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


    }
}