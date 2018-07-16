package com.example.jakov.wherewasi;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LogViewActivity extends AppCompatActivity {
    private static final String TAG = "LogViewActivity";
    DatabaseHelper mDatabaseHelper;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_view);
        mListView = (ListView) findViewById(R.id.LogsListView);
        mDatabaseHelper = new DatabaseHelper(this);
        populateListView();
    }



    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = mDatabaseHelper.getData();
        ArrayList<LogEntry> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(new LogEntry(data.getString(0),data.getString(1),data.getString(3),data.getString(4)));
        }
        //create the list adapter and set the adapter
        LogListAdapter adapter = new LogListAdapter(this, R.layout.logs_list_view_adapter, listData);
        mListView.setAdapter(adapter);


    }
}
