package com.example.jakov.wherewasi;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class ViewActivity extends AppCompatActivity implements SearchDialog.SearchDialogListener{
    ArrayList<LogEntry> listData;
    ArrayList<LogEntry> databaseData;
    Location mLocation = new Location("");
    Button placeHolder;
    private Handler mHandler;
    private final String TAG ="ViewActivity";
    DatabaseHelper mDatabaseHelper;
    DatabaseHelper mDatabaseHelper2;
    String name = ActiveLog.getInstance().getValue();
    private TextView textViewName;
    private TextView textViewDateFrom;
    private TextView textViewDateTo;

    private void setHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

            }
        };
    }


    public ArrayList<LogEntry> getListData() {
        return listData;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected = null;
            switch (item.getItemId()) {
                case R.id.navigation_list:
                    selected = new Fragment_list();
                    break;
                case R.id.navigation_mapa:
                    selected = new Fragment_map();
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected).commit();
            return true;


        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setHandler();
        textViewName=findViewById(R.id.textViewName);
        textViewDateFrom=findViewById(R.id.textViewDateFrom);
        textViewDateTo=findViewById(R.id.textViewDateTo);
        placeHolder = findViewById(R.id.placeHolder);
        placeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper2 = new DatabaseHelper(this, "logs_table");

        loadData();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_list()).commit();




    }

    @Override
    public void applyText(String logName, String dateTo, String dateFrom){
        if (logName!="" || dateTo!="" || dateFrom!=""){
            textViewName.setText(logName);
            textViewDateFrom.setText(dateFrom);
            textViewDateTo.setText(dateTo);
            listData.clear();
            String cleanFrom = dateFrom.replaceAll("[^\\d]", "" );
            String cleanTo = dateTo.replaceAll("[^\\d]", "" );
            for (LogEntry entry:databaseData){
                if ((logName=="" || logName==null || entry.getName().toLowerCase().contains(logName.toLowerCase()))&&(dateFrom==""  || dateFrom==null || Integer.parseInt(entry.getTimestamp().substring(0,10).replace(".",""))>Integer.parseInt(dateFrom))&&(dateTo==""|| dateTo==null || Integer.parseInt(entry.getTimestamp().substring(0,10).replace(".",""))<Integer.parseInt(dateTo))){
                    listData.add(entry);

                }

            }

        }
        else{
            databaseData.addAll(listData);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_list()).commit();

    }

    public void openDialog(){
        SearchDialog searchDialog=new SearchDialog();
        searchDialog.show(getSupportFragmentManager(),"searchDialog");

    }



    private void loadData() {
        Cursor data = null;
        if (name.equals("ALL LOGS")) {
            data = mDatabaseHelper.getData();
        }
        else
            data = mDatabaseHelper.getData(name);

        Bitmap image = null;
        databaseData = new ArrayList<>();
        listData = new ArrayList<>();


        while(data.moveToNext()){
            image = null;
            if (data.getString(5) != null) {
                image  = BitmapFactory.decodeFile(data.getString(5));
            }
            //get the value from the database in column 1
            //then add it to the ArrayList
            Log.d(TAG, "adding path:" + data.getString(5));

            LogEntry le = new LogEntry(data.getString(0),data.getString(1) , data.getString(3),data.getString(4),
                    image, data.getString(5), data.getString(2), data.getString(7));
            databaseData.add(le);
            listData.add(le);

        }

    }





}

