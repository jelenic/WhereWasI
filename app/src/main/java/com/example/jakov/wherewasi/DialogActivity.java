package com.example.jakov.wherewasi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class DialogActivity extends AppCompatActivity {
    String name;
    String description;
    String path;
    String longitude;
    String latitude;
    String timestamp;
    String adress;
    Bitmap image;
    TextView nameTV;
    TextView descriptionTV;
    TextView longitudeTV;
    TextView latitudeTV;
    TextView timestampTV;
    TextView adressTV;
    ImageView imageV;
    Button openMapsBtn;

    Float x1,x2,y1,y2;

    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();

                Log.d("dialog activiy", "x1:" + x1 + " x2:" + x2);
                Log.d("dialog activiy", "y1:" + y1 + " y2:" + y2);

        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            name = extras.getString("name");
            description = extras.getString("description");
            path = extras.getString("path");
            longitude = extras.getString("longitude");
            latitude = extras.getString("latitude");
            timestamp = extras.getString("timestamp");
            adress = extras.getString("adress");

        }
        if (adress.isEmpty()) adress = "Unknown address";
        image = BitmapFactory.decodeFile(path);

        openMapsBtn = findViewById(R.id.openMapsBtn);
        openMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double longi = Double.parseDouble(longitude);
                Double lati = Double.parseDouble(latitude);
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", longi, lati);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        nameTV = findViewById(R.id.nameTextView);
        descriptionTV = findViewById(R.id.descriptionTextView);
        imageV = findViewById(R.id.ImageView);
        longitudeTV = findViewById(R.id.longitudeTextView);
        latitudeTV = findViewById(R.id.latitudeTextView);
        timestampTV = findViewById(R.id.timestampTextView);
        adressTV = findViewById(R.id.adressTextView);

        nameTV.setText(name);
        descriptionTV.setText(description);
        descriptionTV.setMovementMethod(new ScrollingMovementMethod());

        longitudeTV.setText(longitude);
        latitudeTV.setText(latitude);
        timestampTV.setText(timestamp);
        adressTV.setText(adress);
        imageV.setImageBitmap(image);

    }
}
