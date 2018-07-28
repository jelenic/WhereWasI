package com.example.jakov.wherewasi;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class LogViewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "LogViewActivity";
    DatabaseHelper mDatabaseHelper;
    DatabaseHelper mDatabaseHelper2;
    ListView mListView;
    Spinner pickLog;
    Button setActive;
    String name;
    ArrayList<LogEntry> listData;
    ArrayList<LogEntry> listData_selected;
    LogListAdapter adapter;
    int count;
    String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_view);
        mListView = (ListView) findViewById(R.id.LogsListView);
        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper2 = new DatabaseHelper(this, "logs_table");

        pickLog = (Spinner) findViewById(R.id.LogsSpinner);

        setActive = (Button) findViewById(R.id.SetActiveLog);
        loadSpinnerData();
        pickLog.setOnItemSelectedListener(this);
        setActive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ActiveLog.getInstance().setValue(name);
            }
        });
    }



    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = mDatabaseHelper.getData();
        Bitmap image = null;
        listData = new ArrayList<>();
        while(data.moveToNext()){
            image = null;
            if (data.getString(5) != null) {
                /*image = BitmapFactory.decodeByteArray(data.getBlob(5), 0, data.getBlob(5).length);*/
                image  = BitmapFactory.decodeFile(data.getString(5));
            }
            //get the value from the database in column 1
            //then add it to the ArrayList
            Log.d(TAG, "adding path:" + data.getString(5));

            if (data.getString(6).equals(name)) {
                listData.add(new LogEntry(data.getString(0) + "-" +  data.getString(6),data.getString(1) , data.getString(3),data.getString(4), image));
            }
        }
        //create the list adapter and set the adapter
        adapter = new LogListAdapter(this, R.layout.logs_list_view_adapter, listData);
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listData_selected=new ArrayList<>();
        count=0;

        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.context_menu,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.delete_id:
                        for (LogEntry item : listData_selected){
                            timestamp = item.getTimestamp();
                            adapter.remove(item);
                            mDatabaseHelper.deleteEntry(timestamp);
                        }
                        Toast.makeText(getBaseContext(),count+"removed",Toast.LENGTH_SHORT).show();
                        count=0;
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                if (listData_selected.contains(listData.get(i))){
                    count = count-1;
                    listData_selected.remove(listData.get(i));
                    actionMode.setTitle(count + "Items selected");
                }
                else{

                    listData_selected.add(listData.get(i));
                    count += 1;
                    actionMode.setTitle(count + "Items selected");
                }


            }
        });



    }


    private void loadSpinnerData() {
        // database handler
        Cursor data = mDatabaseHelper2.getLogData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            Log.d(TAG, "adding DATA:" + data.getString(1));
            listData.add(data.getString(1));
        }
        Log.d(TAG,"listData:"+listData);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listData);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickLog.setAdapter(spinnerAdapter);



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        name = parent.getItemAtPosition(position).toString();
        populateListView();
        Log.d(TAG, "you clicked");
        Log.d(TAG, name);
        Toast.makeText(parent.getContext(),name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}