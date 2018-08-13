package com.example.jakov.wherewasi;

import android.content.Intent;
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

public class LogViewActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    private static final String TAG = "LogViewActivity";
    DatabaseHelper mDatabaseHelper;
    DatabaseHelper mDatabaseHelper2;
    ListView mListView;
    Spinner pickLog;
    Button setActive;
    String name = ActiveLog.getInstance().getValue();
    ArrayList<LogEntry> listData;
    ArrayList<LogEntry> listData_selected;
    LogListAdapter adapter;
    int count;
    String timestamp;
    ArrayList<String> listDataSpinner;
    Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_view);
        mListView = (ListView) findViewById(R.id.LogsListView);
        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper2 = new DatabaseHelper(this, "logs_table");

        pickLog = (Spinner) findViewById(R.id.LogsSpinner);

        setActive = (Button) findViewById(R.id.SetActiveLog);

        listDataSpinner = new ArrayList<>();
        Cursor data = mDatabaseHelper2.getLogData();
        while(data.moveToNext()){
            Log.d(TAG, "adding DATA:" + data.getString(1));
            listDataSpinner.add(data.getString(1));
        }
        int pos = listDataSpinner.indexOf(name);
        loadSpinnerData();
        pickLog.setSelection(pos);

        populateListView();

        pickLog.setOnItemSelectedListener(this);
        setActive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ActiveLog.getInstance().setValue(name);
                Log.d(TAG, name + " is now active");
            }
        });

        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LogViewActivity.this,SearchActivity.class);
                startActivity(intent);
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
                listData.add(new LogEntry(data.getString(0),data.getString(1) , data.getString(3),data.getString(4), image, data.getString(5), data.getString(2), data.getString(7)));
            }
        }
        //create the list adapter and set the adapter
        adapter = new LogListAdapter(this, R.layout.logs_list_view_adapter, listData);
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setOnItemClickListener(this);
        listData_selected=new ArrayList<>();
        count=0;
        mListView.setSelection(adapter.getCount() - 1);
        //mListView.smoothScrollToPosition(adapter.getCount() - 1);


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
                            Log.d(TAG, "timestamp:" + timestamp);
                            adapter.remove(item);
                            mDatabaseHelper.deleteEntry(timestamp);
                        }
                        Toast.makeText(getBaseContext(),count+"removed",Toast.LENGTH_SHORT).show();
                        count=0;
                        actionMode.finish();
                        adapter.notifyDataSetChanged();
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
        listDataSpinner = new ArrayList<>();
        Cursor data = mDatabaseHelper2.getLogData();
        while(data.moveToNext()){
            Log.d(TAG, "adding DATA:" + data.getString(1));
            listDataSpinner.add(data.getString(1));
        }
        Log.d(TAG,"listData:"+listData);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listDataSpinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickLog.setAdapter(spinnerAdapter);



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        name = parent.getItemAtPosition(position).toString();
        populateListView();
        Log.d(TAG, "you clicked");
        Log.d(TAG, name);
        /*Toast.makeText(parent.getContext(),name, Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Toast.makeText(this, "Clicled"+ listData.get(position).getName(), Toast.LENGTH_SHORT).show();*/
        Intent intent = new Intent(this,DialogActivity.class);
        LogEntry entry = listData.get(position);

        intent.putExtra("name",entry.getName());
        intent.putExtra("description",entry.getDescription());
        intent.putExtra("path",entry.getPath());
        intent.putExtra("timestamp",entry.getTimestamp());
        intent.putExtra("latitude",entry.getLatitude());
        intent.putExtra("longitude",entry.getLongitude());
        startActivity(intent);
    }


}