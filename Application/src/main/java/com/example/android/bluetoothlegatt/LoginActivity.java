package com.example.android.bluetoothlegatt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener, Interface {

    //private TextView mLName;
    //private TextView mID;

    private Button mSubmit;
    private TextView mRegister;

    private ValidateLoginTask task;

    private DatabaseHelper db;

  //  private String urlPath = "http://54.190.56.49:8080/validateLogin";

    private String urlPath = "http://54.190.56.49:8080/HeartRate";
    private String intervalsPath = "http://54.190.56.49:8080/Intervals";
    private String restPath = "http://54.190.56.49:8080/RestTime";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

      //  mLName = findViewById(R.id.player_lname);
      //  mID = findViewById(R.id.player_id);

        mSubmit = findViewById(R.id.submit_details);
        mRegister = findViewById(R.id.new_user);

        mSubmit.setOnClickListener(this);
        mRegister.setOnClickListener(this);


        db = new DatabaseHelper(this);

    }


    @Override
    public void onStart() {

        SharedPreferences prefs = getSharedPreferences("prefs2", MODE_PRIVATE);

        //String ID = prefs.getString("ID", "");
        String Lname = prefs.getString("Lname", "");//"No name defined" is the default value.

        if(!Lname.equals("")) {
            mSubmit.setVisibility(View.VISIBLE);
            mSubmit.setText("Continue as" + " " + Lname);
        }
        else
        {
            mSubmit.setVisibility(View.INVISIBLE);
        }


        checkDBStatus();

        super.onStart();
    }


    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.submit_details:

                //verify details with server
               // task = new ValidateLoginTask();
               // task.delegate = this;
               // task.execute(urlPath,mLName.getText().toString(), mID.getText().toString());
//                SharedPreferences.Editor editor = getSharedPreferences("prefs", MODE_PRIVATE).edit();
//                editor.putString("Lname", mLName.getText().toString());
//                editor.apply();
                startDeviceScan();

                break;

            case R.id.new_user:

                //ask for info and save on server
                Intent intent2 = new Intent(this, RegistrationActivity.class);
                startActivity(intent2);
                break;

        }
    }

    @Override
    public void processFinish(String output) {

        if(output.equals("Valid"))
        {

            Intent intent1 = new Intent(this, DeviceScanActivity.class);
            startActivity(intent1);
        }
        else
        {
            Toast.makeText(this, "Wrong Login Details!", Toast.LENGTH_LONG).show();
        }
    }

    public void startDeviceScan()
    {
        Intent intent1 = new Intent(this, DeviceScanActivity.class);
        startActivity(intent1);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    private void checkDBStatus()
    {
        if(isNetworkAvailable())
        {

            if(!db.isTrainingTableEmpty() || !db.isRestTableEmpty() || !db.isIntervalTableEmpty())
            {
                Toast.makeText(this, "Syncing pending information", Toast.LENGTH_LONG).show();
            }

            if(!db.isTrainingTableEmpty())
            {
                List<JSONObject> data1 = db.getTrainingData();

                try {
                    for (int i = 0; i < data1.size(); i++) {
                        new PostDataTask().execute(urlPath, data1.get(i).getString("RunID"), data1.get(i).getString("Name"), data1.get(i).getString("Time"), data1.get(i).getString("HeartRate"), data1.get(i).getString("PlayerID"), data1.get(i).getString("DateValue"));
                    }

                }
                catch(Exception e)
                {

                }

                db.deleteTrainingData();

            }

            if(!db.isRestTableEmpty())
            {
                List<JSONObject> data2 = db.getRestData();
                try
                {
                    for (int i = 0; i < data2.size(); i++) {
                        new PostRestTask().execute(restPath, data2.get(i).getString("RunID"), data2.get(i).getString("Time"));
                    }
                }

                catch(Exception e)
                {

                }

                db.deleteRestData();

            }

            if(!db.isIntervalTableEmpty())
            {
                List<JSONObject> data3 = db.getIntervalData();

                try
                {
                    for (int i = 0; i < data3.size(); i++) {
                        new PostRestTask().execute(intervalsPath, data3.get(i).getString("RunID"), data3.get(i).getString("Time"));
                    }
                }

                catch(Exception e)
                {

                }

                db.deleteIntervalData();

            }
        }
    }
}
