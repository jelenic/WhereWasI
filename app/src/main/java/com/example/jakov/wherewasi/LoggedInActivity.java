package com.example.jakov.wherewasi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoggedInActivity extends AppCompatActivity {

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
        Button QuickCheckInBtn=(Button) findViewById(R.id.QuickCheckInBtn);
        QuickCheckInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = 12;
                int lan = 45;
                int lon = 87;


            }
        });

    }
}
