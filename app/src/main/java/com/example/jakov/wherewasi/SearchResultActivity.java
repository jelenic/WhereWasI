package com.example.jakov.wherewasi;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class SearchResultActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener {
    private static final String TAG = "SearchResultActivity";
    DatabaseHelper mDatabaseHelper;
    ListView mListView;
    ArrayList<LogEntry> listData;
    ArrayList<LogEntry> listData_selected;
    LogListAdapter adapter;
    int count;
    String timestamp;

    String nameFilter = null;
    String dateFromm = null;
    String dateToo = null;

    Float radius;
    Location location, location2;
    Double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        mListView = (ListView) findViewById(R.id.LogsListView);
        mDatabaseHelper = new DatabaseHelper(this);
        location = new Location("");

        Bundle extras = getIntent().getExtras();

        if (getIntent().hasExtra("namefilter")) nameFilter = extras.getString("namefilter");
        if (getIntent().hasExtra("datefromfilter")) dateFromm = extras.getString("datefromfilter");
        if (getIntent().hasExtra("longitude")) longitude = extras.getDouble("longitude");
        if (getIntent().hasExtra("latitude")) latitude = extras.getDouble("latitude");
        if (getIntent().hasExtra("latitude")) radius = extras.getFloat("radius");

        if(dateFromm != null){
            if (dateFromm.isEmpty()) dateFromm = null;
        }

        if(dateToo != null){
            if (dateToo.isEmpty()) dateToo = null;
        }

        if (longitude != null && latitude != null) {
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }
        else location = null;

        Log.d(TAG, "name:" + nameFilter + "|   from:" + dateFromm + "|  to:" + dateToo);
        populateListView();
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
                image  = BitmapFactory.decodeFile(data.getString(5));
            }
            String date = data.getString(0);
            String name = data.getString(1);
            Double latitude2 = Double.parseDouble(data.getString(3));
            Double longitude2 = Double.parseDouble(data.getString(4));

            int from;
            int to;
            Boolean fromm = false;
            Boolean too = false;
            int dateInt = Integer.parseInt(date.substring(0,10).replace(".",""));
            if (dateFromm != null) {
                from = Integer.parseInt(dateFromm.substring(0,10).replace(".",""));
                fromm = dateInt >= from;
                Log.d(TAG, "dateInt:"+dateInt+"|from:"+from);
            }
            else fromm = true;
            if (dateToo != null) {
                to = Integer.parseInt(dateToo.substring(0,10).replace(".",""));
                too = dateInt <= to;
                Log.d(TAG, "dateInt:"+dateInt+"|to:"+to);
            }
            else too = true;

            Boolean namee = false;
            if (nameFilter != null) namee = name.startsWith(nameFilter);
            else namee = true;

            Boolean distancee = false;

            if (location != null) {
                location2 = new Location("");
                location2.setLongitude(longitude2);
                location2.setLatitude(latitude2);
                Float distance2 = location.distanceTo(location2);
                Log.d(TAG, "populateListView, distance2:" + distance2);
                if (distance2 <= radius) distancee = true;
            }
            else distancee = true;

            if (fromm && too && namee && distancee) {
                listData.add(new LogEntry(date,name , data.getString(3),data.getString(4), image, data.getString(5), data.getString(2),data.getString(7)));
            }
        }
        //create the list adapter and set the adapter
        Collections.reverse(listData);
        adapter = new LogListAdapter(this, R.layout.logs_list_view_adapter, listData);
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setOnItemClickListener(this);
        listData_selected=new ArrayList<>();
        count=0;
        //mListView.setSelection(adapter.getCount() - 1);

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



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,DialogActivity.class);
        LogEntry entry = listData.get(position);

        intent.putExtra("name",entry.getName());
        intent.putExtra("description",entry.getDescription());
        intent.putExtra("path",entry.getPath());
        intent.putExtra("timestamp",entry.getTimestamp());
        intent.putExtra("latitude",entry.getLatitude());
        intent.putExtra("longitude",entry.getLongitude());
        intent.putExtra("adress",entry.getAdress());
        intent.putExtra("position",position);
        startActivity(intent);
    }


}
