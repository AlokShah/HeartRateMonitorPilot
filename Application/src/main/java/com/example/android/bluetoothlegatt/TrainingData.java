package com.example.android.bluetoothlegatt;

/**
 * Created by alokshah on 4/13/18.
 */


public class TrainingData {
    public static final String TABLE_NAME = "trainingData";

    public static final String COLUMN_id = "id";
    public static final String COLUMN_RunID = "RunID";
    public static final String COLUMN_Name = "Name";
    public static final String COLUMN_Time = "Time";
    public static final String COLUMN_HeartRate = "HeartRate";
    public static final String COLUMN_PlayerID= "PlayerID";
    public static final String COLUMN_DateValue = "DateValue";




    private int id;
    private String RunID;
    private String Name;
    private String Time;
    private String HeartRate;
    private String PlayerID;
    private String DateValue;




    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_RunID + " TEXT,"
                    + COLUMN_Name + " TEXT,"
                    + COLUMN_Time + " TEXT,"
                    + COLUMN_HeartRate + " TEXT,"
                    + COLUMN_PlayerID + " TEXT,"
                    + COLUMN_DateValue + " TEXT"
                    + ")";

    public TrainingData() {
    }

    public TrainingData(int id, String RunID, String Name, String Time,  String HeartRate, String PlayerID, String DateValue) {
        this.id = id;
        this.RunID = RunID;
        this.Name = Name;
        this.Time = Time;
        this.HeartRate = HeartRate;
        this.PlayerID = PlayerID;
        this.DateValue = DateValue;
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

    public String getName() {
        return Name;
    }


    public void setName(String Name) {
        this.Name = Name;
    }

    public String getHeartRate() {
        return HeartRate;
    }


    public void setHeartRate(String HeartRate) {
        this.HeartRate = HeartRate;
    }

    public String getID() {
        return PlayerID;
    }


    public void setID(String PlayerID) {
        this.PlayerID = PlayerID;
    }

    public String getDateValue() {
        return DateValue;
    }


    public void setDateValue(String DateValue) {
        this.DateValue = DateValue;
    }
}