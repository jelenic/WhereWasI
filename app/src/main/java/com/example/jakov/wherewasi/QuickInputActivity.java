package com.example.jakov.wherewasi;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class QuickInputActivity extends AppCompatActivity implements IPickResult {
    Button Insertbtn;
    Button imgBtn;
    EditText nameet;
    EditText descriptionet;
    DatabaseHelper logdb;
    double longituded;
    double latituded;
    Bitmap bitmap = null;
    String path = null;
    private Uri mImageCaptureUri;
    private ImageView mImageView;

    private SharedPreferences prefs;

    private Handler mHandler;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_input);

        getSupportActionBar().hide();

        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());

        prefs = this.getSharedPreferences("MyValues",0);

        verifyPermissions();

        setHandler();

        Insertbtn = (Button) findViewById(R.id.Insertbtn);
        nameet= (EditText) findViewById(R.id.nameet);
        descriptionet= (EditText) findViewById(R.id.descriptionet);
        logdb = new DatabaseHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            longituded = extras.getDouble("longitude");
            latituded = extras.getDouble("latitude");

        }
        addfulldata();




        mImageView = (ImageView) findViewById(R.id.mImageView);

        ((Button) findViewById(R.id.imgBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup().setTitle("Pick an image")).show(QuickInputActivity.this);
            }
        });


    }

    private void setHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                String text = message.arg1 == 1 ? "INSERTED" : "FAILED";
                Toast.makeText(QuickInputActivity.this, text, Toast.LENGTH_SHORT).show();

            }
        };
    }



    public void addfulldata(){
        Insertbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Thread myThread = new Thread(new Runnable(){
                    @Override
                    public void run()
                    {
                        if (path == null) path = "";
                        String latitude=Double.toString(latituded);
                        String longitude=Double.toString(longituded);
                        String name = nameet.getText().toString();
                        if (name.isEmpty()) name = "Quick Input";
                        String desc = descriptionet.getText().toString();
                        String adress = LoggedInActivity.getCompleteAddressString(latituded,longituded);

                        boolean insertlog = logdb.addData(name,desc,latitude,longitude, path, prefs.getString("ActiveLog", "Default Log"),adress);


                        Message message = mHandler.obtainMessage();
                        message.arg1 = 0;
                        if (insertlog) {
                            message.arg1 = 1;
                        }

                        message.sendToTarget();
                    }
                });
                myThread.start();
                finish();

            }
        });
    }

    private void verifyPermissions(){
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED){
        }else{
            ActivityCompat.requestPermissions(QuickInputActivity.this,
                    permissions,
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        verifyPermissions();
    }


    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            //Setting the real returned image.
            //getImageView().setImageURI(r.getUri());

            //If you want the Bitmap.
            mImageView.setImageBitmap(r.getBitmap());

            //Image path
            //r.getPath();
            Log.d("quick input", "onPickResult: " + r.getPath());
            /*Bitmap image = rotateImageIfRequired(this, r.getBitmap(), r.getUri());
            r.setBitmap(image);*/
            path = r.getPath();

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Rotate an image if required.
     * @param img
     * @param selectedImage
     * @return
     */
    private static Bitmap rotateImageIfRequired(Context context,Bitmap img, Uri selectedImage) {

        // Detect rotation
        int rotation = getRotation(context, selectedImage);
        Log.d("quick input", "rotateImageIfRequired:rot " + rotation);
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            return rotatedImg;
        }
        else{
            return img;
        }
    }

    /**
     * Get the rotation of the last image added.
     * @param context
     * @param selectedImage
     * @return
     */
    private static int getRotation(Context context, Uri selectedImage) {

        int rotation = 0;
        ContentResolver content = context.getContentResolver();

        Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { "orientation", "date_added" },
                null, null, "date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() != 0) {
            while(mediaCursor.moveToNext()){
                rotation = mediaCursor.getInt(0);
                break;
            }
        }
        mediaCursor.close();
        return rotation;
    }
}
