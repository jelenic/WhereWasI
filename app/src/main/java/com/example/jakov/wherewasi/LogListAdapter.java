package com.example.jakov.wherewasi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class LogListAdapter extends ArrayAdapter<LogEntry> {
    private Context mContext;
    int mResource;
    private int lastPosition = -1;

    public LogListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<LogEntry> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    private static class ViewHolder {
        TextView tvTimestamp;
        TextView tvName;
        TextView tvAdress;
        TextView tvLatLong;
        ImageView ivImage;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String Timestamp = getItem(position).getTimestamp();
        String Name = getItem(position).getName();
        String Longitude = getItem(position).getLongitude();
        String Latitude = getItem(position).getLatitude();
        Bitmap Image = getItem(position).getImage();
        String Path = getItem(position).getPath();
        String Description = getItem(position).getDescription();
        String Adress = getItem(position).getAdress();

        final View result;

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();

            holder.tvTimestamp=  convertView.findViewById(R.id.textView1);
            holder.tvName=  convertView.findViewById(R.id.textView2);
            holder.tvAdress=  convertView.findViewById(R.id.textView3);
            holder.tvLatLong=  convertView.findViewById(R.id.textView4);

            result = convertView;

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);

        lastPosition = position;

        holder.tvTimestamp.setText(Timestamp);
        holder.tvName.setText(Name);
        holder.tvLatLong.setText(Longitude + "|" + Latitude);
        holder.tvAdress.setText(Adress);
        if (Image!=null) {
            Log.d(TAG, "getView: slika null" + Timestamp);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Image.compress(Bitmap.CompressFormat.JPEG,25,stream);
            byte[] byteArray = stream.toByteArray();
            Image = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            holder.ivImage=  convertView.findViewById(R.id.imageView1);
            holder.ivImage.setImageBitmap(Image);
            holder.ivImage.setVisibility(View.VISIBLE);
        }


        return convertView;


    }
}
