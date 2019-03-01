package com.example.jakov.wherewasi;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    SharedPreferences prefs;
    public static String name;

    public static ArrayList<String> listDataSpinner;
    private TextView textViewName;
    private TextView textViewDateFrom;
    private TextView textViewDateTo;
    private TextView textViewActiveLog;


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

            replaceFragment(selected);

            return true;


        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        getSupportActionBar().hide();


        prefs = this.getSharedPreferences("MyValues", 0);
        name = prefs.getString("ActiveLog", "Default Log");
        textViewName=findViewById(R.id.textViewName);
        textViewDateFrom=findViewById(R.id.textViewDateFrom);
        textViewDateTo=findViewById(R.id.textViewDateTo);
        textViewActiveLog=findViewById(R.id.textViewActiveLog);
        textViewActiveLog.setText(name);
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
        loadSpinnerData();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_list()).commit();




    }

    @Override
    public void applyText(String entryName, String dateTo, String dateFrom, ArrayList<String> logs){
        Log.d(TAG, "applyText begin " + entryName + " " + dateFrom + " " + dateTo );
        if (!entryName.isEmpty() || !dateTo.isEmpty() || !dateFrom.isEmpty() || logs.size() > 0){
            textViewName.setText(entryName);
            textViewDateFrom.setText(dateFrom);
            textViewDateTo.setText(dateTo);
            listData.clear();
            for (LogEntry entry:databaseData){
                Log.d(TAG, "applyText:2 " + entry.getName() + " " + entry.getName().toLowerCase().contains(entryName.toLowerCase()));
                if (entry.getName().toLowerCase().contains(entryName.toLowerCase()) && (logs.size() == 0 || logs.contains("ALL LOGS") || logs.contains(entry.getLogName()))&&(dateFrom==""  || dateFrom==null || Integer.parseInt(entry.getTimestamp().substring(0,10).replace(".",""))>Integer.parseInt(dateFrom))&&(dateTo==""|| dateTo==null || Integer.parseInt(entry.getTimestamp().substring(0,10).replace(".",""))<Integer.parseInt(dateTo))){
                    listData.add(entry);

                }

            }

        }
        else {
            loadData();
        }
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(f instanceof Fragment_map) {
            replaceFragment(new Fragment_map());
        }

        Fragment_list.update();

    }

    public void openDialog(){
        SearchDialog searchDialog=new SearchDialog();
        Bundle args = new Bundle();
        args.putSerializable("logs", listDataSpinner);
        searchDialog.setArguments(args);
        searchDialog.show(getSupportFragmentManager(),"searchDialog");

    }



    private void loadData() {
        Cursor data = null;
        Log.d(TAG, "loadData: starting " + name);

        data = mDatabaseHelper.getData();

        Bitmap image = null;
        databaseData = new ArrayList<>();
        listData = new ArrayList<>();


        while(data.moveToNext()){
            image = null;
            Log.d(TAG, "loadData:pic " + data.getString(5));
            if (data.getString(5) != null && !data.getString(5).isEmpty()) {
                image  = BitmapFactory.decodeFile(data.getString(5));
            }
            Log.d(TAG, "adding path:" + data.getString(5));
            Log.d(TAG, "entry " + data.getString(0) + data.getString(1) + data.getString(3) + data.getString(4) + data.getString(6));

            LogEntry le = new LogEntry(data.getString(0),data.getString(1) , subString(data.getString(3)),subString(data.getString(4)),
                    image, data.getString(5), data.getString(2), data.getString(7), data.getString(6));
            databaseData.add(le);
            if (le.getLogName().equals(name)) listData.add(le);
            Log.d(TAG, "loadData:data " + le.getTimestamp());

        }


    }

    private String subString(String string) {
        if (string.length() > 10) return string.substring(0,10);
        else return string;
    }

    private void replaceFragment (Fragment fragment){

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
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


    }





}

