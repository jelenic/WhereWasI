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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class Fragment_list extends Fragment implements  AdapterView.OnItemClickListener{
    ListView mListView;
    static LogListAdapter adapter;

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
        mListView.setOnItemClickListener(this);
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
