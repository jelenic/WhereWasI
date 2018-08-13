package com.example.jakov.wherewasi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

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
    ImageView imageV;

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

        image = BitmapFactory.decodeFile(path);

        nameTV = findViewById(R.id.nameTextView);
        descriptionTV = findViewById(R.id.descriptionTextView);
        imageV = findViewById(R.id.ImageView);
        longitudeTV = findViewById(R.id.longitudeTextView);
        latitudeTV = findViewById(R.id.latitudeTextView);
        timestampTV = findViewById(R.id.timestampTextView);

        nameTV.setText(name);
        descriptionTV.setText(description);
        descriptionTV.setMovementMethod(new ScrollingMovementMethod());

        longitudeTV.setText(longitude);
        latitudeTV.setText(latitude);
        timestampTV.setText(timestamp);
        imageV.setImageBitmap(image);

    }
}
