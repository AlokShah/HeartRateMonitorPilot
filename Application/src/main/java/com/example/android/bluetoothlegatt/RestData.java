package com.example.android.bluetoothlegatt;

/**
 * Created by alokshah on 4/13/18.
 */

public class RestData {
    public static final String TABLE_NAME = "restData";

    public static final String COLUMN_id = "id";
    public static final String COLUMN_RunID = "RunID";
    public static final String COLUMN_Time = "Time";





    private int id;
    private String RunID;
    private String Time;





    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_RunID + " TEXT,"
                    + COLUMN_Time + " TEXT"
                    + ")";

    public RestData() {
    }

    public RestData(int id, String RunID, String Time) {
        this.id = id;
        this.RunID = RunID;
        this.Time = Time;

    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }


    public String getRunID() {
        return RunID;
    }

    public void setRunID(String RunID) {
        this.RunID = RunID;
    }




}
