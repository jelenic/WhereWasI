package com.example.jakov.wherewasi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class LogListAdapter extends ArrayAdapter<LogEntry> {
    private Context mContext;
    int mResource;

    public LogListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<LogEntry> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String Timestamp= getItem(position).getTimestamp();
        String Name= getItem(position).getName();
        String Longitude= getItem(position).getLongitude();
        String Latitude= getItem(position).getLatitude();

        LogEntry logEntry= new LogEntry(Timestamp,Name,Longitude,Latitude);
        LayoutInflater inflater= LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tvTimestamp= (TextView) convertView.findViewById(R.id.textView1);
        TextView tvName= (TextView) convertView.findViewById(R.id.textView2);
        TextView tvLongitude= (TextView) convertView.findViewById(R.id.textView3);
        TextView tvLatitude= (TextView) convertView.findViewById(R.id.textView4);

        tvTimestamp.setText(Timestamp);
        tvName.setText(Name);
        tvLatitude.setText(Latitude);
        tvLongitude.setText(Longitude);

        return convertView;


    }
}
