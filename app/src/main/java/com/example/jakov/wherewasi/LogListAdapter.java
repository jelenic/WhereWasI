package com.example.jakov.wherewasi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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
        Bitmap Image= getItem(position).getImage();
        String Path = getItem(position).getPath();
        String Description = getItem(position).getDescription();
        String Adress = getItem(position).getAdress();

        LogEntry logEntry= new LogEntry(Timestamp,Name,Longitude,Latitude,Image, Path, Description, Adress);
        LayoutInflater inflater= LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tvTimestamp= (TextView) convertView.findViewById(R.id.textView1);
        TextView tvName= (TextView) convertView.findViewById(R.id.textView2);
        TextView tvAdress= (TextView) convertView.findViewById(R.id.textView3);
        TextView tvLatLong= (TextView) convertView.findViewById(R.id.textView4);
        ImageView ivImage= (ImageView) convertView.findViewById(R.id.imageView1);

        tvTimestamp.setText(Timestamp);
        tvName.setText(Name);
        tvLatLong.setText(Longitude + "|" + Latitude);
        tvAdress.setText(Adress);
        if (Image!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Image.compress(Bitmap.CompressFormat.JPEG,50,stream);
            byte[] byteArray = stream.toByteArray();
            Image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            ivImage.setImageBitmap(Image);
        }

        return convertView;


    }
}
