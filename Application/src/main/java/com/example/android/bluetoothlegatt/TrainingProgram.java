package com.example.android.bluetoothlegatt;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class TrainingProgram extends Activity implements View.OnClickListener {

    private String mDeviceName;
    private String mDeviceAddress;

    private ArrayList<Integer> arr = new ArrayList<Integer>();


    private Button mSubmit;

    private EditText mAerobic;
    private EditText mAnaerobic;
    private EditText mDistance;

    private ArrayList<Double> mLaps;


    private HashMap<Integer, Double> hmap = new HashMap<Integer, Double>();

    private String urlPath = "http://54.190.56.49:8080/metrics";


    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_program);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mSubmit = findViewById(R.id.submit_program);
        mAerobic = findViewById(R.id.aerobic_val);
        mAnaerobic = findViewById(R.id.anaerobic_val);
        mDistance = findViewById(R.id.distance);

        mSubmit.setOnClickListener(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        setMap();

    }

    @Override
    public void onStart() {

        super.onStart();
        mLaps = new ArrayList<Double>();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.submit_program:

                if (mAerobic.getText().toString().equals("") || mAnaerobic.getText().toString().equals("") || mDistance.getText().toString().equals("")) {
                    Toast.makeText(this, "Please fill in all details!",
                            Toast.LENGTH_LONG).show();
                } else {


                    setSegments();

                    if(getTimeLaps()) {

                        final Intent intent = new Intent(this, DeviceControlActivity.class);
                        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, mDeviceName);
                        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                        intent.putExtra("LapTime", mLaps);


                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(this, "Incorrect combination", Toast.LENGTH_LONG).show();
                    }

                }

                break;
        }
    }

    public void setSegments() {

        int aerobic = Integer.parseInt(mAerobic.getText().toString());
        int anaerobic = Integer.parseInt(mAnaerobic.getText().toString());
        int a = aerobic;
        int an = anaerobic;

        int i = 1;

        int segment = 1;

        int count = 1;

        arr = new ArrayList<>();

        while (segment <= 8) {
            if (count <= 6) {
                arr.add(a);
                count++;
                a++;

                if (count == 7) {
                    segment++;
                    a = aerobic + segment - 1;
                    arr.add(a);
                    count++;
                }

            } else if (count >= 7 && count <= 12) {
               // a = aerobic + segment - 1;
                a = a + anaerobic;
                arr.add(a);
                count++;


                if (count == 13) {
                    segment++;
                    a = aerobic + segment - 1;
                    arr.add(a);
                    count++;
                }

            } else if (count >= 13 && count <= 18) {
                a = a + anaerobic;
                arr.add(a);
                count++;

                if (count == 19) {
                    segment++;
                    a = aerobic + segment - 1;
                    arr.add(a);
                    count++;
                }
            } else if (count >= 19 && count <= 24) {
                a = a + anaerobic;
                arr.add(a);
                count++;

                if (count == 25) {
                    segment++;
                    a = aerobic + segment - 1;
                    arr.add(a);
                    count++;
                }
            } else if (count >= 25 && count <= 30) {
                a = a + anaerobic;
                arr.add(a);
                count++;

                if (count == 31) {
                    segment++;
                    a = aerobic + segment - 1;
                    arr.add(a);
                    count++;
                }
            } else if (count >= 31 && count <= 36) {

                a = a + anaerobic;
                arr.add(a);
                count++;

                if (count == 37) {
                    segment++;
                    a = aerobic + segment - 1;
                    arr.add(a);
                    count++;
                }
            } else if (count >= 37 && count <= 42) {
                a = a + anaerobic;
                arr.add(a);
                count++;

                if (count == 43) {
                    segment++;
                    a = aerobic + segment - 1;
                    arr.add(a);
                    count++;
                }
            } else if (count >= 43 && count <= 48) {

                a = a + anaerobic;
                arr.add(a);
                count++;

                if (count == 49) {
                    segment++;

                }
            }

        }


    }

    private void setMap() {


        int i = 1;


        while (i <= 171) {

            if (i == 1)
                hmap.put(i, 10.0);
            if (i == 2)
                hmap.put(i, 12.0);
            if (i == 3)
                hmap.put(i, 13.0);
            if (i == 4)
                hmap.put(i, 13.0);
            if (i == 5)
                hmap.put(i, 13.5);
            if (i == 6)
                hmap.put(i, 13.5);
            if (i == 7)
                hmap.put(i, 13.5);
            if (i == 8)
                hmap.put(i, 14.0);
            if (i == 9)
                hmap.put(i, 14.0);

            if (i == 10)
                hmap.put(i, 14.0);

            if (i == 11)
                hmap.put(i, 14.0);

            if (i >= 12 && i <= 19) {
                hmap.put(i, 14.5);
            }

            if (i >= 20 && i <= 27) {
                hmap.put(i, 15.0);
            }

            if (i >= 28 && i <= 35) {
                hmap.put(i, 15.5);
            }

            if (i >= 36 && i <= 43) {
                hmap.put(i, 16.0);
            }

            if (i >= 44 && i <= 51) {
                hmap.put(i, 16.5);
            }
            if (i >= 52 && i <= 59) {
                hmap.put(i, 17.0);
            }
            if (i >= 60 && i <= 67) {
                hmap.put(i, 17.5);
            }
            if (i >= 68 && i <= 75) {
                hmap.put(i, 18.0);
            }
            if (i >= 76 && i <= 83) {
                hmap.put(i, 18.5);
            }
            if (i >= 84 && i <= 91) {
                hmap.put(i, 19.0);
            }
            if (i >= 92 && i <= 99) {
                hmap.put(i, 19.5);
            }
            if (i >= 100 && i <= 107) {
                hmap.put(i, 20.0);
            }
            if (i >= 108 && i <= 115) {
                hmap.put(i, 20.5);
            }
            if (i >= 116 && i <= 123) {
                hmap.put(i, 21.0);
            }
            if (i >= 124 && i <= 131) {
                hmap.put(i, 21.5);
            }
            if (i >= 132 && i <= 139) {
                hmap.put(i, 22.0);
            }
            if (i >= 140 && i <= 147) {
                hmap.put(i, 22.5);
            }
            if (i >= 148 && i <= 155) {
                hmap.put(i, 23.0);
            }
            if (i >= 156 && i <= 163) {
                hmap.put(i, 23.5);
            }
            if (i >= 164 && i <= 171) {
                hmap.put(i, 24.0);
            }

            i++;

        }


    }

    private boolean getTimeLaps() {

        Double distance = Double.valueOf(mDistance.getText().toString()) * (0.9144);
        for (int i = 0; i < arr.size(); ++i) {
            if(hmap.containsKey(arr.get(i))) {
                Double speed = ((hmap.get(arr.get(i)) * 1000) / 3600);
                mLaps.add(distance/speed);
            }
            else
            {
                return false;

            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home :
            {
                finish();
            }

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}

