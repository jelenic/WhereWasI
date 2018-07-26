package com.example.jakov.wherewasi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class StartLogActivity extends AppCompatActivity {
    Button StartLogBtn;
    Switch SetActiveSwitch;
    EditText nameET;
    EditText descriptionET;
    DatabaseHelper logdb;
    Boolean switchState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_log);

        StartLogBtn = (Button) findViewById(R.id.StartLogbtn);
        nameET= (EditText) findViewById(R.id.nameet);
        descriptionET= (EditText) findViewById(R.id.descriptionet);
        logdb = new DatabaseHelper(this, "logs_table");
        addLog();
    }
    public void addLog(){
        StartLogBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String name = nameET.getText().toString();
                String description = descriptionET.getText().toString();
                boolean insertlog = logdb.addLog(name, description);
                /*switchState = SetActiveSwitch.isChecked();*/
                /*String text = "INSERTED LOG";
                if (switchState) {
                    ActiveLog.getInstance().setValue(name);
                    text += ", ACTIVE";
                }*/
                if (insertlog==true){
                    Toast.makeText(StartLogActivity.this,"INSERTED LOG",Toast.LENGTH_LONG).show();

                }
                else Toast.makeText(StartLogActivity.this,"NOPE",Toast.LENGTH_LONG).show();

                finish();

            }
        });
    }
}
