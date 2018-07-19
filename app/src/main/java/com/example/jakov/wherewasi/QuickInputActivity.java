package com.example.jakov.wherewasi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class QuickInputActivity extends AppCompatActivity {
    Button Insertbtn;
    EditText nameet;
    EditText descriptionet;
    DatabaseHelper logdb;
    double longituded;
    double latituded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_input);
        Insertbtn = (Button) findViewById(R.id.Insertbtn);
        nameet= (EditText) findViewById(R.id.nameet);
        descriptionet= (EditText) findViewById(R.id.descriptionet);
        logdb = new DatabaseHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            longituded = extras.getDouble("longitude");
            latituded = extras.getDouble("latitude");

        }
        addfulldata();
    }








    public void addfulldata(){
        Insertbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //get langitude and longitude
                String latitude=Double.toString(latituded);
                String longitude=Double.toString(longituded);
                String name = nameet.getText().toString();
                String desc = descriptionet.getText().toString();
                boolean insertlog = logdb.addData(name,desc,latitude,longitude);
                if (insertlog==true){
                    Toast.makeText(QuickInputActivity.this,"INSERTED",Toast.LENGTH_LONG).show();

                }
                else Toast.makeText(QuickInputActivity.this,"NOPE",Toast.LENGTH_LONG).show();

            }
        });
    }
}
