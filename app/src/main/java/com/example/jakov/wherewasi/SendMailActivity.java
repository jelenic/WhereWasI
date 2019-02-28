package com.example.jakov.wherewasi;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SendMailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseHelper logdb;
    private String msg;
    private String subject;
    private Button sendMailBtn;
    private EditText subjectText;
    private EditText msgText;
    private EditText mailText;
    private String date;
    private Spinner pickLog;
    private ArrayList<String> listDataSpinner;
    private DatabaseHelper mDatabaseHelper2;
    private final String TAG = "SendMailActivity";
    private String logName = "ALL LOGS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
        logdb = new DatabaseHelper(this);
        mDatabaseHelper2 = new DatabaseHelper(this, "logs_table");
        msg="";
        subject="";
        sendMailBtn = findViewById(R.id.sendMailBtn);
        subjectText =  findViewById(R.id.subjectEditText);
        msgText =  findViewById(R.id.msgEditText);
        mailText = findViewById(R.id.mailEditText);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        date = df.format(Calendar.getInstance().getTime());
        pickLog = findViewById(R.id.LogsSpinner);
        loadSpinnerData();
        pickLog.setSelection(listDataSpinner.size() - 1);
        pickLog.setOnItemSelectedListener(this);
        SendMailListener();


    }


    private void SendMailListener(){
        sendMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject=subjectText.getText().toString();
                msg=msgText.getText().toString();
                sendMail(date,mailText.getText().toString());
            }
        });
    }

    private void loadSpinnerData() {
        // database handler
        listDataSpinner = new ArrayList<>();
        Cursor data = mDatabaseHelper2.getLogData();
        while(data.moveToNext()){
            Log.d(TAG, "adding DATA:" + data.getString(1));
            listDataSpinner.add(data.getString(1));
        }
        listDataSpinner.add("ALL LOGS");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listDataSpinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickLog.setAdapter(spinnerAdapter);



    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        logName = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void sendMail(final String date, final String recipient) {
        Thread myThread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Cursor data = null;
                    if (logName.equals("ALL LOGS")) {
                        data = logdb.getMailData();
                    }
                    else
                        data = logdb.getMailData(logName);

                    String path = Environment.getExternalStorageDirectory() + File.separator + ".WhereWasI" + File.separator + "mailFile";
                    File directory = new File(path);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    File mailFile = new File(directory, "MailFile " + date + ".txt");
                    BufferedWriter bw = new BufferedWriter(new FileWriter(mailFile));

                    while(data.moveToNext()){
                        String content = data.getString(0) + "|" + data.getString(1) + "|" + data.getString(2) + "|"
                                + data.getString(3) + "|" + data.getString(4) + "|" + data.getString(5);
                        Log.d("data from DB", "content: " + content);
                        bw.write(content);


                    }
                    String pathMailFile = mailFile.getPath();

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + mailFile.getAbsolutePath()));
                    startActivity(Intent.createChooser(sharingIntent, "share file with"));

                    bw.close();


                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        myThread.start();
    }
}