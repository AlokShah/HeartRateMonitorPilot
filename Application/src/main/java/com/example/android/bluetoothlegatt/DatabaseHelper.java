package com.example.android.bluetoothlegatt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alokshah on 4/13/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "heartrate_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create table
        db.execSQL(TrainingData.CREATE_TABLE);
        db.execSQL(RestData.CREATE_TABLE);
        db.execSQL(IntervalData.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + RestData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IntervalData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrainingData.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertTrainingData(String RunID, String Name, String Time,  String HeartRate, String PlayerID, String DateValue) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(TrainingData.COLUMN_RunID, RunID);
        values.put(TrainingData.COLUMN_Name, Name);
        values.put(TrainingData.COLUMN_Time, Time);
        values.put(TrainingData.COLUMN_HeartRate, HeartRate);
        values.put(TrainingData.COLUMN_PlayerID, PlayerID);
        values.put(TrainingData.COLUMN_DateValue, DateValue);


        // insert row
        long id = db.insert(TrainingData.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    public List<JSONObject> getTrainingData() {
        List<JSONObject> trainingData = new ArrayList<>();

        String selectQuery = "SELECT  *  FROM " + TrainingData.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject data = new JSONObject();
                try {
                    data.put("RunID", cursor.getString(cursor.getColumnIndex(TrainingData.COLUMN_RunID)));
                    data.put("Name", cursor.getString(cursor.getColumnIndex(TrainingData.COLUMN_Name)));
                    data.put("Time", cursor.getString(cursor.getColumnIndex(TrainingData.COLUMN_Time)));
                    data.put("HeartRate", cursor.getString(cursor.getColumnIndex(TrainingData.COLUMN_HeartRate)));
                    data.put("PlayerID", cursor.getString(cursor.getColumnIndex(TrainingData.COLUMN_PlayerID)));
                    data.put("DateValue", cursor.getString(cursor.getColumnIndex(TrainingData.COLUMN_DateValue)));
                }
                catch(Exception e)
                {

                }
                trainingData.add(data);
            } while (cursor.moveToNext());
        }

        db.close();

        return trainingData;
    }

    public void deleteTrainingData() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from "+ TrainingData.TABLE_NAME);

        db.close();
    }

    public boolean isTrainingTableEmpty()
    {
        String selectQuery = "SELECT  count(*)  FROM " + TrainingData.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count > 0)
            return false;
        else
            return true;
    }


    public long insertRestData(String RunID, String Time) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(RestData.COLUMN_RunID, RunID);
        values.put(RestData.COLUMN_Time, Time);



        // insert row
        long id = db.insert(RestData.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    public List<JSONObject> getRestData() {
        List<JSONObject> restData = new ArrayList<>();

        String selectQuery = "SELECT  *  FROM " + RestData.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject data = new JSONObject();
                try {
                    data.put("RunID", cursor.getString(cursor.getColumnIndex(RestData.COLUMN_RunID)));
                    data.put("Time", cursor.getString(cursor.getColumnIndex(RestData.COLUMN_Time)));
                                    }
                catch(Exception e)
                {

                }
                restData.add(data);
            } while (cursor.moveToNext());
        }

        db.close();

        return restData;
    }

    public void deleteRestData() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from "+ RestData.TABLE_NAME);

        db.close();
    }

    public boolean isRestTableEmpty()
    {
        String selectQuery = "SELECT  count(*)  FROM " + RestData.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count > 0)
            return false;
        else
            return true;
    }



    public long insertIntervalData(String RunID, String Time) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(IntervalData.COLUMN_RunID, RunID);
        values.put(IntervalData.COLUMN_Time, Time);



        // insert row
        long id = db.insert(IntervalData.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    public List<JSONObject> getIntervalData() {
        List<JSONObject> intervalData = new ArrayList<>();

        String selectQuery = "SELECT  *  FROM " + IntervalData.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject data = new JSONObject();
                try {
                    data.put("RunID", cursor.getString(cursor.getColumnIndex(IntervalData.COLUMN_RunID)));
                    data.put("Time", cursor.getString(cursor.getColumnIndex(IntervalData.COLUMN_Time)));

                }
                catch(Exception e)
                {

                }
                intervalData.add(data);
            } while (cursor.moveToNext());
        }

        db.close();

        return intervalData;
    }

    public void deleteIntervalData() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from "+ IntervalData.TABLE_NAME);

        db.close();
    }

    public boolean isIntervalTableEmpty()
    {
        String selectQuery = "SELECT  count(*)  FROM " + IntervalData.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        if(count > 0)
            return false;
        else
            return true;
    }





}