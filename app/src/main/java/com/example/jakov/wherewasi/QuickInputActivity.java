package com.example.jakov.wherewasi;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class QuickInputActivity extends AppCompatActivity {
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

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_input);

        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());

        verifyPermissions();

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

        final String [] items           = new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder     = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {
                    Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file        = new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        final AlertDialog dialog = builder.create();

        mImageView = (ImageView) findViewById(R.id.mImageView);

        ((Button) findViewById(R.id.imgBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        bitmap  = null;
        path     = "";

        if (requestCode == PICK_FROM_FILE) {
            mImageCaptureUri = data.getData();
            path = getRealPathFromURI(mImageCaptureUri); //from Gallery

            if (path == null)
                path = mImageCaptureUri.getPath(); //from File Manager

            if (path != null)
                bitmap  = BitmapFactory.decodeFile(path);
        } else {
            path    = mImageCaptureUri.getPath();
            bitmap  = BitmapFactory.decodeFile(path);
        }

        mImageView.setImageBitmap(bitmap);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = managedQuery( contentUri, proj, null, null,null);

        if (cursor == null) return null;

        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }








    public void addfulldata(){
        Insertbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //get langitude and longitude
                String latitude=Double.toString(latituded);
                String longitude=Double.toString(longituded);
                String name = nameet.getText().toString();
                String desc = descriptionet.getText().toString();
                boolean insertlog = logdb.addData(name,desc,latitude,longitude, path);
                if (insertlog==true){
                    Toast.makeText(QuickInputActivity.this,"INSERTED",Toast.LENGTH_LONG).show();

                }
                else Toast.makeText(QuickInputActivity.this,"NOPE",Toast.LENGTH_LONG).show();

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
}
