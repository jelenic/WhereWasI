package com.example.jakov.wherewasi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

public class LoggedInActivity extends AppCompatActivity {
    Button QuickCheckInBtn;
    DatabaseHelper logdb;


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
        logdb = new DatabaseHelper(this);
        QuickCheckInBtn= (Button) findViewById(R.id.QuickCheckInBtn);
        adddata();


    }

    public void adddata(){
        QuickCheckInBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //get langitude and longitude
                String latitude="00,000";
                String longitude="00,000";
                boolean insertlog = logdb.addData(null,null,latitude,longitude);
                if (insertlog==true){
                    Toast.makeText(LoggedInActivity.this,"INSERTED",Toast.LENGTH_LONG).show();

                }
                else Toast.makeText(LoggedInActivity.this,"NOPE",Toast.LENGTH_LONG).show();

            }
        });
    }
}
