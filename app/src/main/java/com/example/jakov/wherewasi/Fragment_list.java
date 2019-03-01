package com.example.jakov.wherewasi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class Fragment_list extends Fragment implements  AdapterView.OnItemClickListener{
    ListView mListView;
    static LogListAdapter adapter;
    public static ArrayList<LogEntry> listData;
    ArrayList<LogEntry> listData_selected;
    int count;
    String timestamp;

    public static void update() {
        Log.d("fragment_list", "update: ");
        adapter.notifyDataSetChanged();
    }


    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mListView = view.findViewById(R.id.mListView);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setSelector(android.R.color.transparent);
        mListView.setDivider(null);
        mListView.setDividerHeight(35);
        mListView.setOnItemClickListener(this);
        listData_selected=new ArrayList<>();
        count=0;
        listData=((ViewActivity) getActivity()).getListData();
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
                    case R.id.selectAll_id:
                        int n = listData.size();
                        if (listData_selected.size() == n) {
                            for (int i=0;i < n; i++) {
                                mListView.setItemChecked(i, false);
                            }
                        }
                        else {
                            for (int i=0;i < n; i++) {
                                if (!listData_selected.contains(listData.get(i))) {
                                    mListView.setItemChecked(i, true);
                                }
                            }
                        }
                        return true;
                    case R.id.delete_id:
                        for (LogEntry item : listData_selected){
                            timestamp = item.getTimestamp();
                            adapter.remove(item);
                            ((ViewActivity) getActivity()).mDatabaseHelper.deleteEntry(timestamp);

                        }
                        Toast.makeText(((ViewActivity) getActivity()).getBaseContext(),count+" removed",Toast.LENGTH_SHORT).show();
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
                listData_selected.clear();
                count = 0;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                if (listData_selected.contains(listData.get(i))){
                    count = count-1;
                    listData_selected.remove(listData.get(i));
                    actionMode.setTitle(count + " Items selected");
                }
                else{

                    listData_selected.add(listData.get(i));
                    count += 1;
                    actionMode.setTitle(count + " Items selected");
                }


            }
        });
        adapter = new LogListAdapter(getActivity(), R.layout.logs_list_view_adapter, ((ViewActivity) getActivity()).getListData());
        mListView.setAdapter(adapter);


        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //openDialogActivity(position);
    }

    private void openDialogActivity(int position) {
        Intent intent = new Intent(getActivity(),DialogActivity.class);
        LogEntry entry = ((ViewActivity) getActivity()).getListData().get(position);

        intent.putExtra("name",entry.getName());
        intent.putExtra("description",entry.getDescription());
        intent.putExtra("path",entry.getPath());
        intent.putExtra("timestamp",entry.getTimestamp());
        intent.putExtra("latitude",entry.getLatitude());
        intent.putExtra("longitude",entry.getLongitude());
        intent.putExtra("adress",entry.getAdress());
        intent.putExtra("position",position);
        intent.putExtra("activity","LogView");
        startActivity(intent);
    }


}
