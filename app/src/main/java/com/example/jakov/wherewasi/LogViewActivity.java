package com.example.jakov.wherewasi;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        Bitmap image = null;
        ArrayList<LogEntry> listData = new ArrayList<>();
        while(data.moveToNext()){
            if (data.getBlob(5) != null) {
                image = BitmapFactory.decodeByteArray(data.getBlob(5), 0, data.getBlob(5).length);
            }
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(new LogEntry(data.getString(0),data.getString(1) , data.getString(3),data.getString(4), image));
        }
        //create the list adapter and set the adapter
        LogListAdapter adapter = new LogListAdapter(this, R.layout.logs_list_view_adapter, listData);
        mListView.setAdapter(adapter);


    }
}
