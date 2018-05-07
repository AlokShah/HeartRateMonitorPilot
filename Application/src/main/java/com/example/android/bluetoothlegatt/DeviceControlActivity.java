

package com.example.android.bluetoothlegatt;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceActivity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.RequiresApi;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class DeviceControlActivity extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener, SensorEventListener, StepListener{

    private String heartRate = "";
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";



    private TextView mConnectionState;
    private TextView mDataField;
    private TextView mPeakField;
    private TextView mTimeField;
    private TextView mDistanceFied;
    private TextView mAvgRate;



    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    private String mDeviceName;
    private String mDeviceAddress;
    private Button mStartButton;
    private Button mStopButton;
    private int mTotalTime;


    private boolean mMeasureRate = false;
    private boolean mCurrentRate = true;
    private Handler handler1 = null;
    private Handler handler2 = null;
    private Handler handler3 = null;
    private String urlPath = "http://54.190.56.49:8080/HeartRate";
    private String intervalsPath = "http://54.190.56.49:8080/Intervals";
    private String restPath = "http://54.190.56.49:8080/RestTime";
    private ArrayList<String> params = new ArrayList<String>();



    private  TextView tvTimer;
    private long startTime, timeInMilliseconds = 0;
    private Handler customHandler = new Handler();

    private SensorManager sManager;
    private Sensor stepSensor;
    private DatabaseHelper db;

    private long endPoint = 0;
    private long runTime = 0;
    private long elapsedTime;
    private String RunID = "";
    private Runnable mRunnable1;
    private Runnable mRunnable2;
    private Runnable mRunnable3;

    private ArrayList<Double> mLaps;
    private ArrayList<Double> mLapsTemp;
    private ArrayList<Double> mTemp;

    private String[] mPreWorkout;
    private String[] mPostWorkout;
    private String[] mMessages;

    private String mPreTitle;
    private String mPostTitle;

    private double height;

    private boolean mTimer = false;

    ArrayList<Integer> sum = new ArrayList<Integer>();
    ArrayList<Integer> intervals = new ArrayList<Integer>();
    ArrayList<Integer> restTime = new ArrayList<Integer>();

    final long[] pattern1 = { 0, 100, 900, 100, 900, 100, 900, 100, 900, 100, 900, 1000,0};
    final long[] pattern2 = { 0,1000, 0, 0, 900, 0, 900, 0, 900, 0, 900, 0, 900,0, 900, 0, 900, 100, 900, 100, 900, 1000, 0};




    Vibrator v ;
    TextToSpeech t1;
    //private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private BluetoothGattCharacteristic characteristic = null;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
//                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                evalGattServices(mBluetoothLeService.getSupportedGattServices());
                displayHearRate();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                if(mCurrentRate) {
                    mStartButton.setEnabled(true);
                    displayData("Current Heart Rate: " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                }

                heartRate = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if(mMeasureRate)
                {
                    sum.add(Integer.parseInt(heartRate));
                }
            }


        }
    };


    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        //mDataField.setText(R.string.no_data);
        mAvgRate.setText("");
        mAvgRate.setVisibility(View.INVISIBLE);
        mPeakField.setText("");
        mTimeField.setText("");
        mDistanceFied.setText("");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        db = new DatabaseHelper(this);
        tvTimer = (TextView) findViewById(R.id.tvTimer);

        //String[] items = {getString(R.string.pre1), getString(R.string.pre2), getString(R.string.pre3)};
        mPreWorkout = new String[]{getString(R.string.pre1), getString(R.string.pre2), getString(R.string.pre3)};

        mPostWorkout = new String[]{getString(R.string.post1), getString(R.string.post2), getString(R.string.post3)};

        mMessages = new String[]{getString(R.string.m1), getString(R.string.m2), getString(R.string.m3), getString(R.string.m4), getString(R.string.m5), getString(R.string.m6), getString(R.string.m7), getString(R.string.m8), getString(R.string.m9)};

        mPreTitle = getString(R.string.preTitle);
        mPostTitle = getString(R.string.postTitle);

        showWorkoutDialog(mPreWorkout, mPreTitle, false);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mLapsTemp = (ArrayList<Double>) getIntent().getSerializableExtra("LapTime");

        mTemp = new ArrayList<>(mLapsTemp);

        mStartButton = findViewById(R.id.button2);
        mStartButton.setEnabled(false);
        mStartButton.setOnClickListener(this);

        mStopButton = findViewById(R.id.button3);
        mStopButton.setOnClickListener(this);
        // Sets up UI references.
        //  mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        //  mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mDataField = (TextView) findViewById(R.id.data_value);
        mPeakField = (TextView) findViewById(R.id.data_value2);
        mTimeField = (TextView) findViewById(R.id.data_value3);
        mDistanceFied = (TextView) findViewById(R.id.data_value4);
        mAvgRate = (TextView)findViewById(R.id.data_valueX);

        t1 = new TextToSpeech(getApplicationContext(), this, "com.google.android.tts");

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        SharedPreferences prefs = getSharedPreferences("prefs2", MODE_PRIVATE);

        height = Double.valueOf(prefs.getString("Height", "180"));
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());


        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        if(handler2!= null && handler1!=null && mRunnable1!=null && mRunnable2!=null)
        {
            handler1.removeCallbacks(mRunnable1);
            handler2.removeCallbacks(mRunnable2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //   mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }


    private void evalGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();

            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);

            gattServiceData.add(currentServiceData);


            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();

                if (gattCharacteristic.getUuid().toString().equals(SampleGattAttributes.HEART_RATE_MEASUREMENT)) {
                    characteristic = gattCharacteristic;
                }
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);

        }


    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void displayHearRate() {
        if (mGattCharacteristics != null) {
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true);
            }

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button2:

                mLaps = new ArrayList<>(mTemp);
                mStopButton.setVisibility(View.VISIBLE);
                mStartButton.setVisibility(View.INVISIBLE);

                clearUI();
                mCurrentRate = true;

                double runVal = mLaps.get(0)*1000;
                mLaps.remove(0);
                startCountDown(pattern1, 0, 5000, runVal);


                endPoint = 0;
                resetTimer();
                tvTimer.setVisibility(View.VISIBLE);


                break;


            case R.id.button3 :

                mStopButton.setVisibility(View.INVISIBLE);
                mStartButton.setVisibility(View.VISIBLE);


                handler1.removeCallbacks(mRunnable1);
                handler2.removeCallbacks(mRunnable2);
               // sManager.unregisterListener(this, stepSensor);

                mCurrentRate = false;
                mMeasureRate = false;
                runTime = endPoint;
                RunID = UUID.randomUUID().toString();
                mDistanceFied.setText("Training Distance: " + String.valueOf(Math.round((getDistanceRun()*0.4*height/1000)*100.0)/100.0 + "m" ));
                mTimeField.setText("Training Time: " + String.valueOf(runTime) + "s");
                displayAverage(sum);
                displayPeak(sum);
                sum = new ArrayList<Integer>();
                intervals = new ArrayList<Integer>();
                restTime = new ArrayList<Integer>();
                tvTimer.setVisibility(View.INVISIBLE);
                stopTimer();
                showWorkoutDialog(mPostWorkout, mPostTitle, true);
                mTimer = false;

                sensorManager.unregisterListener(this);
                //mTimeField.setText(String.valueOf(endPoint - startPoint - 5));

                break;

        }



    }



    private void startCountDown(final long[] pattern, double buzzDelay, int vibTime, double runTime)

    {

         if(vibTime!=5000) {

             intervals.add((int) ((buzzDelay + vibTime + runTime) / 1000 ) + 1);
             int val1 = (int)((runTime + vibTime + mLaps.get(0)*1000)/1000);
             restTime.add(val1 + 1);
         }
         else {
             intervals.add((int) ((buzzDelay + runTime) / 1000) + 1);
             int val2 = (int)((runTime + mLaps.get(0)*1000)/1000);
             restTime.add(val2 + 1);
         }



         if(mStartButton.getVisibility() == View.INVISIBLE) {


             v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


              handler1 = new Handler();
             handler1.postDelayed( mRunnable1 = new Runnable() {
                 @Override
                 public void run() {

                     int len = 0;

                         len = pattern.length/2 - 1;


                     v.vibrate(pattern, -1);

                     if(pattern.length/2 < 10)
                     speech(len);


                     mStopButton.setEnabled(false);


                     if(pattern.length/2 > 10)
                     countdownDisplay(len, false);
                     else
                     countdownDisplay(len, true);

                 }
             }, (long)buzzDelay);


             handler2 = new Handler();


             handler2.postDelayed(mRunnable2 = new Runnable() {
                 @Override
                 public void run() {


                     v.vibrate(1000);

                     double buzzVal = mLaps.get(0)*1000;
                     double runVal = mLaps.get(1)*1000;
                     mLaps.remove(0);
                     mLaps.remove(0);
                     startCountDown(pattern2, buzzVal, 10000, runVal);

                 }
             }, (long)(buzzDelay + vibTime + runTime));


         }


    }

    private void speech(final int count)
    {


        if(count < 0)
            return;

        t1.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // Speaking started.

            }

            @Override
            public void onDone(String utteranceId) {



               speech(count-1);


            }

            @Override
            public void onError(String s) {

            }

        });

        HashMap<String,String> params = new HashMap<String,String>();

        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "theUtId");

        if( count != 0) {

                t1.setSpeechRate(0.4f);
                t1.speak(String.valueOf(count), TextToSpeech.QUEUE_FLUSH, params);

        }

        else if(count == 0) {
            t1.setSpeechRate(0.1f);
            t1.speak("GO!!!", TextToSpeech.QUEUE_FLUSH, params);

            if(!mTimer) {
                mTimer = true;
                startTimer();
                numSteps = 0;
                sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }





    }

    private void displayAverage(ArrayList<Integer> sum)
    {
        int total = 0;


        for(int j = 0; j < intervals.size(); j++)
        {
            if(isNetworkAvailable()) {


                new PostIntervalTask().execute(intervalsPath, RunID, String.valueOf(intervals.get(j)));

            }
            else
            {
                db.insertIntervalData(RunID, String.valueOf(intervals.get(j)) );
            }
        }

        for(int k = 0; k < restTime.size(); k++)
        {
            if(isNetworkAvailable()) {
                new PostRestTask().execute(restPath, RunID, String.valueOf(restTime.get(k)));
            }
            else
            {
                db.insertRestData(RunID,String.valueOf(restTime.get(k)));
            }
        }

        for(int i = 0; i < sum.size(); i++)
        {

                total = total + sum.get(i);

                SharedPreferences prefs = getSharedPreferences("prefs2", MODE_PRIVATE);

                String lname = prefs.getString("Lname", "Player");//"No name defined" is the default value.

                String ID = prefs.getString("ID", "0");

                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");

                String date = sdf.format(new Date());

                if(isNetworkAvailable())
                new PostDataTask().execute(urlPath,RunID,lname,String.valueOf(runTime),String.valueOf(sum.get(i)), ID, date);
                else
                    db.insertTrainingData(RunID,lname,String.valueOf(runTime),String.valueOf(sum.get(i)), ID, date);

        }


        try {
            displayData(String.valueOf(""));
            mAvgRate.setVisibility(View.VISIBLE);
            mAvgRate.setText("Average Heart Rate: "+ String.valueOf(total / sum.size()));

        }
        catch(Exception e)
        {

        }

        sum = new ArrayList<Integer>();
        total = 0;

    }

    private void displayPeak(ArrayList<Integer> sum)
    {
        Collections.sort(sum);

        mPeakField.setText("Peak Heart Rate: " + String.valueOf(sum.get(sum.size() - 1)));

    }



    private void countdownDisplay(final int length, final boolean fullSpeech)
    {
        final int[] secondsLeft = {0};


        new CountDownTimer(1000*length, 100) {
            public void onTick(long ms) {
                if (Math.round((float)ms / 1000.0f) != secondsLeft[0])
                {
                    secondsLeft[0] = Math.round((float)ms / 1000.0f);
                    if(secondsLeft[0] != 0) {

                        mStopButton.setText("" + secondsLeft[0]);

                        if(secondsLeft[0] == 3 && !fullSpeech)
                        {
                            speech(3);
                        }
                    }
                    else {
                        mStopButton.setText("GO");

                    }
                }
            }

            public void onFinish() {
                mMeasureRate = true;

                handler3 = new Handler();
                handler3.postDelayed( mRunnable3 = new Runnable() {
                    @Override
                    public void run() {

                        mStopButton.setText("STOP");
                        mStopButton.setEnabled(true);

                    }
                }, 3000);




            }
        }.start();
    }


    private void showWorkoutDialog(final String[] items, String title, final Boolean flag)
    {
        final Dialog dialog;
        final int[] selection = new int[1];
        //final String[] items = {getString(R.string.pre1), getString(R.string.pre2), getString(R.string.pre3)};
        final ArrayList itemsSelected = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.d(TAG,"The wrong button was tapped: " + items[whichButton]);
                selection[0] = whichButton;
            }
        });


        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

                if(flag && (selection[0] == 0 || selection[0] == 1))
                {
                    showMotivationalDialog(mMessages);
                }
            }
        });

        dialog = builder.create();
        dialog.show();
    }


    private void showMotivationalDialog(final String[] items)
    {
        final Dialog dialog;
        final ArrayList itemsSelected = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Random rand = new Random();


        int index = rand.nextInt(items.length);


       builder.setMessage(items[index]);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();

            }
        });

        dialog = builder.create();
        dialog.show();
    }


    public static String getDateFromMillis(long d) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(d);
    }

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimer() {
        customHandler.removeCallbacks(updateTimerThread);
    }

    public void resetTimer()
    {
        tvTimer.setText("00:00:00");
    }
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            String time = getDateFromMillis(timeInMilliseconds);
            tvTimer.setText(time);
            endPoint = Long.valueOf(timeInMilliseconds/1000);
            customHandler.postDelayed(this, 1000);
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.ENGLISH);
                    t1.setVoice(getVoice());
                    t1.setPitch(2.0f);

                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            private Voice getVoice()
            {
                Voice v = null;
                for (Voice tmpVoice : t1.getVoices()) {
                    if (tmpVoice.getName().equals("en-us-x-sfg#male_1-local")) {
                        v = tmpVoice;
                       break;
                    }
                }
                return v;
            }







    private int getDistanceRun(){
       return numSteps;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void step(long timeNs) {
        numSteps++;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }



}



