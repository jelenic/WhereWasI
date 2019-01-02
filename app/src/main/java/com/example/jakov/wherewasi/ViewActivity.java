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

public class ViewActivity extends AppCompatActivity {
    ArrayList<LogEntry> listData;
    ArrayList<LogEntry> databaseData;
    Location mLocation = new Location("");
    Button placeHolder;
    private Handler mHandler;
    private final String TAG ="ViewActivity";
    DatabaseHelper mDatabaseHelper;
    DatabaseHelper mDatabaseHelper2;
    String name = ActiveLog.getInstance().getValue();

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
        placeHolder = findViewById(R.id.placeHolder);
        placeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "started changing" + listData.size());
                listData.clear();
                Log.d(TAG, "finished changing" + listData.size());
                Log.d(TAG, "db size" + databaseData.size());
                int i = 0;
                for (LogEntry le : databaseData) {
                    LogEntry le2 = new LogEntry(le.getTimestamp(),"new name " + i, le.getLatitude(), le.getLongitude(), le.getImage(), le.getPath(), le.getDescription(), le.getAdress());
                    listData.add(le2);
                    Log.d(TAG, le2.getName());
                    i++;
                }
                Fragment_list.update();
            }
        });

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper2 = new DatabaseHelper(this, "logs_table");

        loadData();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_list()).commit();




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

