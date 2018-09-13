package com.example.jakov.wherewasi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "log_table";
    private static final String LOG_TABLE_NAME = "logs_table";
    private static final String COL0 = "timestamp";
    private static final String COL1 = "name";
    private static final String COL2 = "description";
    private static final String COL3 = "latitude";  //North-South
    private static final String COL4 = "longitude";   //East-West
    private static final String COL5 = "image";
    private static final String COL6 = "log_name";
    private static final String COL7 = "adress";



    public DatabaseHelper(Context context, String tablename) {
        super(context,tablename,null,1);
    }

    public DatabaseHelper(Context context) {
        super(context,TABLE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL0 + " TEXT PRIMARY KEY , " +
                COL1 +" TEXT , " + COL2 + " TEXT, " + COL3 + " TEXT, "+ COL4 + " TEXT, " + COL5 + " TEXT, " + COL6 + " TEXT,"+COL7 + " TEXT  )";
        db.execSQL(createTable);
        String createTable2 = "CREATE TABLE " + LOG_TABLE_NAME + " (" + COL0 + " TEXT , " +
                COL1 +" TEXT PRIMARY KEY , " + COL2 + " TEXT )";
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        String insertDefault = "INSERT INTO " + LOG_TABLE_NAME +  " VALUES('On install', 'Default log' , 'default desctiption' )";
        db.execSQL(createTable2);
        db.execSQL(insertDefault);

    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
        onCreate(db);

    }


    public boolean addData(String name,String description, String latitude, String longitude, String path, String log_name, String adress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        contentValues.put(COL0, date);
        contentValues.put(COL1, name);
        contentValues.put(COL2, description);
        contentValues.put(COL3, latitude);
        contentValues.put(COL4, longitude);
        contentValues.put(COL5, path);
        contentValues.put(COL6, log_name);
        contentValues.put(COL7, adress);

        Log.d(TAG, "addData: Adding " + name + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addMailData(String date,String name, String latitude, String longitude, String log_name, String adress, String path){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, date);
        contentValues.put(COL1, name);
        contentValues.put(COL3, latitude);
        contentValues.put(COL4, longitude);
        contentValues.put(COL5, path);
        contentValues.put(COL6, log_name);
        contentValues.put(COL7, adress);
        Log.d(TAG, "addMailData: Adding " + name + " to " + TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addLog(String name, String description){
        if (name.isEmpty()) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        ActiveLog.getInstance().setValue(name);
        contentValues.put(COL0, date);
        contentValues.put(COL1, name);
        contentValues.put(COL2, description);

        Log.d(TAG, "addData: Adding " + name + " to " + LOG_TABLE_NAME);
        long result = db.insert(LOG_TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL0 + " DESC";
        Cursor data = db.rawQuery(query, null);

        return data;
    }

    public Cursor getData(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL6 + " = '" + name + "'" + " ORDER BY " + COL0 + " DESC";
        Cursor data = db.rawQuery(query, null);

        return data;
    }


    public Cursor getMailData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL0 + "," + COL1 + "," + COL3 + "," + COL4 + "," + COL6 + "," + COL7  + " FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);

        return data;
    }

    public Cursor getGifData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL0 + "," + COL5  + " FROM " + TABLE_NAME + " ORDER BY " + COL0 + " ASC";
        Cursor data = db.rawQuery(query, null);

        return data;
    }



    public Cursor getLogData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + LOG_TABLE_NAME;
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

    public void deleteEntry(String id){
        String todel= "'"+id+"'";
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL0 + " = " +  todel;
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + id + " from database.");

        db.execSQL(query);
        db.close();
    }


}