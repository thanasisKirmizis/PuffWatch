package com.kirman.puffwatch_v2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private Button mSmokeButton;
    private TextView xText, yText, zText;
    private ImageButton mRunPauseBtn, mSettingsBtn;
    private ImageButton mRightButtonMain, mLeftButtonMain;
    private ImageView mMeterIcon;
    private TextView mMeasureUnit;

    private SensorManager SM;

    // Define the output file path
    String destPath;
    File dir;

    // Define the accelerometer file's pointer
    File accel_output_file;
    FileOutputStream accel_f;
    DataOutputStream accel_data_f;

    // Define the gyroscope file's pointer
    File gyro_output_file;
    FileOutputStream gyro_f;
    DataOutputStream gyro_data_f;

    // Define the gyroscope file's pointer
    File magnet_output_file;
    FileOutputStream magnet_f;
    DataOutputStream magnet_data_f;

    // Define the Smoking Session Timestamp file's pointer
    File timestamp_output_file;
    FileOutputStream timestamp_f;

    // Keep track of run/pause
    private Boolean isRunning;

    // Keep track of start/stop smoking
    private Boolean isSmoking;

    // Keep track of currently displayed meter (0: Accel 1: Gyro 2: Magnet)
    int currentlySelectedMeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setAmbientEnabled();

        // Assign UI Elements
        mSmokeButton = findViewById(R.id.smokeBtn);
        xText = findViewById(R.id.x);
        yText = findViewById(R.id.y);
        zText = findViewById(R.id.z);
        mRunPauseBtn = findViewById(R.id.runPauseBtn);
        mSettingsBtn = findViewById(R.id.settingsBtn);
        mRightButtonMain = findViewById(R.id.rightArrowMain);
        mLeftButtonMain = findViewById(R.id.leftArrowMain);
        mMeterIcon = findViewById(R.id.meterIcon);
        mMeasureUnit = findViewById(R.id.measureUnit);

        // Setup the output file path
        destPath = Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null)).getAbsolutePath();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        dir = new File(destPath + "/sensors_logs/" + currentDate);
        Boolean dirsMade = dir.mkdirs();

        // Initialize the file for the accelerometer data
        try {

            accel_output_file = new File(dir, "Accel_output.dat");
            accel_f = new FileOutputStream(accel_output_file, true);
            accel_data_f = new DataOutputStream(accel_f);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        // Initialize the file for the gyroscope data
        try {

            gyro_output_file = new File(dir, "Gyro_output.dat");
            gyro_f = new FileOutputStream(gyro_output_file, true);
            gyro_data_f = new DataOutputStream(gyro_f);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        // Initialize the file for the magnetometer data
        try {

            magnet_output_file = new File(dir, "Magnet_output.dat");
            magnet_f = new FileOutputStream(magnet_output_file, true);
            magnet_data_f = new DataOutputStream(magnet_f);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        // Initialize the file for the sessions timestamps
        try {

            timestamp_output_file = new File(dir, "Sessions_timestamps.csv");
            timestamp_f = new FileOutputStream(timestamp_output_file, true);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        // Start from 'Paused' and Non-smoking phase
        isRunning = false;
        isSmoking = false;

        // Start by displaying the accelerometer data
        currentlySelectedMeter = 0;
        refreshDisplayedData();

        // Create our Sensor Manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Get accelerometer Sensor
        final Sensor accel_Sensor = Objects.requireNonNull(SM).getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Get gyroscope Sensor
        final Sensor gyro_Sensor = Objects.requireNonNull(SM).getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Get magnetometer Sensor
        final Sensor magnet_Sensor = Objects.requireNonNull(SM).getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Set the 'Start\End Smoking' button functionality
        mSmokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isRunning) {

                    if (isSmoking) {

                        // Keep timestamp of Ending moment
                        String stamp = ""+System.currentTimeMillis() + "\n";
                        try {

                            timestamp_f.write(stamp.getBytes());
                        } catch (IOException e) {

                            e.printStackTrace();
                        }

                        // Change button text and icon
                        mSmokeButton.setText("START SMOKING");
                        mSmokeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.custom_size_start_smoke, 0, 0, 0);

                        // Display Confirmation Toast
                        Toast.makeText(getApplicationContext(), "Smoking Recorded!", Toast.LENGTH_SHORT).show();

                        isSmoking = false;
                    } else {

                        // Keep timestamp of Starting moment
                        String stamp = ""+System.currentTimeMillis() + "\n";
                        try {

                            timestamp_f.write(stamp.getBytes());
                        } catch (IOException e) {

                            e.printStackTrace();
                        }

                        // Change button text and icon
                        mSmokeButton.setText("STOP SMOKING");
                        mSmokeButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.custom_size_stop_smoke, 0, 0, 0);

                        isSmoking = true;
                    }
                }
                else {

                    Toast.makeText(getApplicationContext(), "Press the Play Button First!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Set the 'Right' button functionality
        mRightButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Display next meter data
                currentlySelectedMeter = (currentlySelectedMeter + 1)%3;
                refreshDisplayedData();
            }
        });

        // Set the 'Left' button functionality
        mLeftButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Display previous meter data
                currentlySelectedMeter = ((currentlySelectedMeter - 1)%3 + 3)%3;
                refreshDisplayedData();
            }
        });

        // Set the 'Run/Pause' button functionality
        mRunPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If sensors are running, pause them
                if(isRunning) {

                    mRunPauseBtn.setImageDrawable(getDrawable(R.drawable.play_icon));
                    mRunPauseBtn.setColorFilter(Color.rgb(0, 255, 40));
                    isRunning = false;
                    SM.unregisterListener(MainActivity.this);
                }
                // If sensors are paused, register them
                else {

                    mRunPauseBtn.setImageDrawable(getDrawable(R.drawable.pause_icon));
                    mRunPauseBtn.setColorFilter(Color.rgb(255, 140, 0));
                    isRunning = true;
                    registerSensors(accel_Sensor, gyro_Sensor, magnet_Sensor);
                }
            }
        });

        // Set the 'Settings' button functionality
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Go to 'Settings'
                Intent goToSettingsIntent = new Intent(MainActivity.this, Settings.class);
                startActivity(goToSettingsIntent);
            }
        });

    }

    @Override
    protected void onPause() {

        super.onPause();

        // Unregister the listener for all sensors
        SM.unregisterListener(MainActivity.this);

        // Close the File and Data Streams
        try {

            accel_f.close();
            gyro_f.close();
            magnet_f.close();
            timestamp_f.close();

            accel_data_f.close();
            gyro_data_f.close();
            magnet_data_f.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

        // Save the applied settings
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(SplashActivity.PW_PREFS, 0);
        prefs.edit().putInt(SplashActivity.ACCEL_KEY, SplashActivity.ACCEL_OPTION).apply();
        prefs.edit().putInt(SplashActivity.GYRO_KEY, SplashActivity.GYRO_OPTION).apply();
        prefs.edit().putInt(SplashActivity.MAGNET_KEY, SplashActivity.MAGNET_OPTION).apply();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch(event.sensor.getType()) {

            case Sensor.TYPE_ACCELEROMETER:

                if(currentlySelectedMeter == 0) {

                    xText.setText("X: " + event.values[0]);
                    yText.setText("Y: " + event.values[1]);
                    zText.setText("Z: " + event.values[2]);
                }

                writeToFile(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2], accel_data_f);
                break;

            case Sensor.TYPE_GYROSCOPE:

                if(currentlySelectedMeter == 1) {

                    xText.setText("X: " + event.values[0]);
                    yText.setText("Y: " + event.values[1]);
                    zText.setText("Z: " + event.values[2]);
                }

                writeToFile(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2], gyro_data_f);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:

                if(currentlySelectedMeter == 2) {

                    xText.setText("X: " + event.values[0]);
                    yText.setText("Y: " + event.values[1]);
                    zText.setText("Z: " + event.values[2]);
                }

                writeToFile(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2], magnet_data_f);
                break;
        }
    }

    private void refreshDisplayedData() {

        switch(currentlySelectedMeter) {

            case 0:
                mMeterIcon.setImageDrawable(getDrawable(R.drawable.accel_icon));
                mMeasureUnit.setText("m/s^2");

                if(SplashActivity.ACCEL_OPTION == 0 || !isRunning) {

                    xText.setText("X: ---");
                    yText.setText("Y: ---");
                    zText.setText("Z: ---");
                }
                break;

            case 1:
                mMeterIcon.setImageDrawable(getDrawable(R.drawable.gyro_icon));
                mMeasureUnit.setText("rad/s");

                if(SplashActivity.GYRO_OPTION == 0 || !isRunning) {

                    xText.setText("X: ---");
                    yText.setText("Y: ---");
                    zText.setText("Z: ---");
                }
                break;

            case 2:
                mMeterIcon.setImageDrawable(getDrawable(R.drawable.magnet_icon));
                mMeasureUnit.setText("uT");

                if(SplashActivity.MAGNET_OPTION == 0 || !isRunning) {

                    xText.setText("X: ---");
                    yText.setText("Y: ---");
                    zText.setText("Z: ---");
                }
                break;
        }
    }

    // Function to register sensor listeners with rate depending on the settings
    private void registerSensors(Sensor snsr1, Sensor snsr2, Sensor snsr3) {

        if (SplashActivity.ACCEL_OPTION == 1) {

            // Slow rate (~15Hz)
            SM.registerListener(this, snsr1, SensorManager.SENSOR_DELAY_UI);
        }
        else if (SplashActivity.ACCEL_OPTION == 2) {

            // Fast rate (~50Hz)
            SM.registerListener(this, snsr1, SensorManager.SENSOR_DELAY_GAME);
        }

        if (SplashActivity.GYRO_OPTION == 1) {

            // Slow rate (~15Hz)
            SM.registerListener(this, snsr2, SensorManager.SENSOR_DELAY_UI);
        }
        else if (SplashActivity.GYRO_OPTION == 2) {

            // Fast rate (~50Hz)
            SM.registerListener(this, snsr2, SensorManager.SENSOR_DELAY_GAME);
        }

        if (SplashActivity.MAGNET_OPTION == 1) {

            // Slow rate (~15Hz)
            SM.registerListener(this, snsr3, SensorManager.SENSOR_DELAY_UI);
        }
        else if (SplashActivity.MAGNET_OPTION == 2) {

            // Fast rate (~50Hz)
            SM.registerListener(this, snsr3, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    // Function to write motion data to specific logs
    private void writeToFile(long stamp, float x, float y, float z, DataOutputStream f) {

        try {

            f.writeLong(stamp);
            f.writeFloat(x);
            f.writeFloat(y);
            f.writeFloat(z);
            f.flush();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}