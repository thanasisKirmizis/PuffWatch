package com.kirman.puffwatch_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends WearableActivity {

    private Button mSmokeButton;
    private TextView xText, yText, zText;
    private ImageButton mRunPauseBtn, mSettingsBtn;
    private ImageButton mRightButtonMain, mLeftButtonMain;
    private ImageView mMeterIcon;
    private TextView mMeasureUnit;

    // Keep track of run/pause
    public static Boolean isRunning;

    // Keep track of start/stop smoking
    public static Boolean isSmoking;

    // Keep track of currently displayed meter (0: Accel 1: Gyro 2: Magnet)
    int currentlySelectedMeter;

    // Define the output file path
    String destPath;
    File dir;

    // Define the Smoking Session Timestamp file's pointer
    File timestamp_output_file;
    FileOutputStream timestamp_f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Start from 'Paused' and Non-smoking phase
        stopSensors();
        isSmoking = false;

        // Start by displaying the accelerometer data
        currentlySelectedMeter = 0;
        refreshDisplayedData();

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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                // If sensors are running, pause them
                if(isRunning) {

                    stopSensors();

                    refreshDisplayedData();
                }
                // If sensors are paused, register them
                else {

                    mRunPauseBtn.setImageDrawable(getDrawable(R.drawable.pause_icon));
                    mRunPauseBtn.setColorFilter(Color.rgb(255, 140, 0));
                    isRunning = true;

                    refreshDisplayedData();

                    startForegroundService(new Intent(MainActivity.this, BackgroundService.class));
                }
            }
        });

        // Set the 'Settings' button functionality
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isRunning) {

                    stopSensors();
                }

                // Go to 'Settings'
                Intent goToSettingsIntent = new Intent(MainActivity.this, Settings.class);
                startActivity(goToSettingsIntent);
            }
        });

    }

    @Override
    protected void onPause() {

        super.onPause();

        // Close the timestamp's file
        try {
            timestamp_f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        initializeFiles();
    }

    private void initializeFiles() {

        // Setup the output file path
        destPath = Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null)).getAbsolutePath();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        dir = new File(destPath + "/sensors_logs/" + currentDate);
        Boolean dirsMade = dir.mkdirs();

        // Initialize the file for the sessions timestamps
        try {

            timestamp_output_file = new File(dir, "Sessions_timestamps.csv");
            timestamp_f = new FileOutputStream(timestamp_output_file, true);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
    }

    private void stopSensors() {

        mRunPauseBtn.setImageDrawable(getDrawable(R.drawable.play_icon));
        mRunPauseBtn.setColorFilter(Color.rgb(0, 255, 40));
        isRunning = false;

        // Destroy Service
        stopService(new Intent(this, BackgroundService.class));
    }

    private void refreshDisplayedData() {

        switch(currentlySelectedMeter) {

            case 0:
                mMeterIcon.setImageDrawable(getDrawable(R.drawable.accel_icon));
                mMeasureUnit.setText("m/s^2");

                if(SplashActivity.ACCEL_OPTION == 0 || !isRunning) {

                    xText.setText("X: Paused.");
                    yText.setText("Y: Paused.");
                    zText.setText("Z: Paused.");
                }
                else {

                    xText.setText("X: Reading...");
                    yText.setText("Y: Reading...");
                    zText.setText("Z: Reading...");
                }
                break;

            case 1:
                mMeterIcon.setImageDrawable(getDrawable(R.drawable.gyro_icon));
                mMeasureUnit.setText("rad/s");

                if(SplashActivity.GYRO_OPTION == 0 || !isRunning) {

                    xText.setText("X: Paused.");
                    yText.setText("Y: Paused.");
                    zText.setText("Z: Paused.");
                }
                else {

                    xText.setText("X: Reading...");
                    yText.setText("Y: Reading...");
                    zText.setText("Z: Reading...");
                }
                break;

            case 2:
                mMeterIcon.setImageDrawable(getDrawable(R.drawable.magnet_icon));
                mMeasureUnit.setText("uT");

                if(SplashActivity.MAGNET_OPTION == 0 || !isRunning) {

                    xText.setText("X: Paused.");
                    yText.setText("Y: Paused.");
                    zText.setText("Z: Paused.");
                }
                else {

                    xText.setText("X: Reading...");
                    yText.setText("Y: Reading...");
                    zText.setText("Z: Reading...");
                }
                break;
        }
    }

}