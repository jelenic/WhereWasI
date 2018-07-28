package com.example.jakov.wherewasi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;

public class DialogActivity extends AppCompatActivity {
    String Name;
    String Description;
    Bitmap Image;
    TextView DialogName;
    ImageView DialogImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Intent intent = getIntent();
        Name = intent.getStringExtra("name");
        Image=null;
        if (intent.hasExtra("image")){
            Image = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
        }


        DialogName=(TextView)findViewById(R.id.DialogName);
        DialogName.setText(Name);
        DialogImage = (ImageView) findViewById(R.id.DialogImage);
        DialogImage.setImageBitmap(Image);


    }
}
