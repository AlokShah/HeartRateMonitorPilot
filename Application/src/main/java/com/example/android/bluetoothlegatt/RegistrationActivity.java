package com.example.android.bluetoothlegatt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class RegistrationActivity extends Activity implements View.OnClickListener, Interface{


    private TextView mFName;
    private TextView mLName;
    private TextView mHeightFeet;
    private TextView mHeightInch;
    private TextView mWeight;
    private TextView mAge;
    private Spinner mSpinner;
    private Spinner mSpinner2;

    private Button mSubmit;

    private String ID;
    private String urlPath = "http://54.190.56.49:8080/Login";

    private PostLoginTask task;

    String fname;
    String lname;
    String heightfeet;
    String heightinches;
    String weight;
    String age;
    String gender;
    String sport;

    Double height;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        mFName = findViewById(R.id.new_player_fname);
        mLName = findViewById(R.id.new_player_lname);
        mHeightFeet = findViewById(R.id.new_player_heightfeet);
        mHeightInch = findViewById(R.id.new_player_heightinches);
        mWeight = findViewById(R.id.new_player_weight);
        mAge = findViewById(R.id.new_player_age);
        mSubmit = findViewById(R.id.new_submit);
        mSpinner = findViewById(R.id.spinner);
        mSpinner2 = findViewById(R.id.spinner2);


      //  mSpinner.setOnItemSelectedListener(this);

        mSubmit.setOnClickListener(this);



        ID = getSaltString();




    }

    @Override
    public void onClick(View view) {

        switch(view.getId())
        {
            case R.id.new_submit:
                fname = mFName.getText().toString();
                lname = mLName.getText().toString();
                heightfeet = mHeightFeet.getText().toString();
                heightinches = mHeightInch.getText().toString();
                weight = mWeight.getText().toString();
                age = mAge.getText().toString();
                sport = mSpinner.getSelectedItem().toString();
                gender = mSpinner2.getSelectedItem().toString();

                if(fname.equals("") || lname.equals("") || heightfeet.equals("")  || weight.equals("") || age.equals(""))
                {
                    Toast.makeText(this, "Please fill in all details", Toast.LENGTH_LONG).show();
                }
                else {
                    task = new PostLoginTask();
                    task.delegate = this;
                    height = Double.valueOf((Integer.parseInt(mHeightFeet.getText().toString())*30.48) + (Integer.parseInt(mHeightInch.getText().toString())*2.54));

                    task.execute(urlPath, fname, lname, String.valueOf(height), weight, age, sport, gender, ID);
                }
                break;
        }
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 7) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    @Override
    public void processFinish(String output) {

        if(output.equals("Success"))
        {
            showIDDialog(true);
        }
        else
        {
            showIDDialog(false);
        }
    }


    private void showIDDialog(Boolean success)
    {
        final Dialog dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(success) {
            builder.setTitle("Success!");
            builder.setMessage("Your unique ID is : " + ID);

            SharedPreferences.Editor editor = getSharedPreferences("prefs2", MODE_PRIVATE).edit();
            editor.putString("ID", ID);
            editor.putString("Lname",lname);
            editor.putString("Height",String.valueOf(height));
            editor.apply();

            builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();

//                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//                    startActivity(i);
                   onBackPressed();
                }
            });

        }
        else
        {
            builder.setTitle("Error!");
            builder.setMessage("Could not register!");
        }
        dialog = builder.create();
        dialog.show();
    }

}
