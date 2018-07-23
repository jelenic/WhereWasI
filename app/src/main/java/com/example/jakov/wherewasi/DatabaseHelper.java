package com.example.jakov.wherewasi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "log_table";
    private static final String COL0 = "timestamp";
    private static final String COL1 = "name";
    private static final String COL2 = "description";
    private static final String COL3 = "latitude";  //North-South
    private static final String COL4 = "longitude";   //East-West
    private static final String COL5 = "image";



    public DatabaseHelper(Context context) {
        super(context,TABLE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL0 + " TEXT PRIMARY KEY , " +
                COL1 +" TEXT , " + COL2 + " TEXT, " + COL3 + " TEXT, "+ COL4 + " TEXT, " + COL5 + " TEXT )";
        db.execSQL(createTable);

    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);

    }
    /*public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }*/

    public boolean addData(String name,String description, String latitude, String longitude, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Date currentTime = Calendar.getInstance().getTime();
        /*byte[] image = null;
        if (img != null)  image = getBitmapAsByteArray(img);*/
        contentValues.put(COL0, currentTime.toString());
        contentValues.put(COL1, name);
        contentValues.put(COL2, description);
        contentValues.put(COL3, latitude);
        contentValues.put(COL4, longitude);
        contentValues.put(COL5, path);

        Log.d(TAG, "addData: Adding " + name + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getNameKeys(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL0 + " FROM " + TABLE_NAME +
                " WHERE " + COL1 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteLog(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL1 + " = '" + id + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + id + " from database.");
        db.execSQL(query);
    }






}
