package com.kirman.puffwatch_v2;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Settings2 extends WearableActivity {

    Button mApplyBtnGyro;
    RadioButton mHighGyro;
    RadioButton mMedGyro;
    RadioButton mOffGyro;
    RadioGroup mGyroOptions;
    ImageButton mLeftButton;
    ImageButton mRightButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        // Assign UI Elements
        mApplyBtnGyro = findViewById(R.id.applyBtnGyro);
        mHighGyro = findViewById(R.id.highFreqGyro);
        mMedGyro = findViewById(R.id.medFreqGyro);
        mOffGyro = findViewById(R.id.offGyro);
        mGyroOptions = findViewById(R.id.optionsGyro);
        mLeftButton = findViewById(R.id.leftArrow);
        mRightButton2 = findViewById(R.id.rightArrow2);

        // Set the pre-checked radio button
        preCheckRadioButtons();

        // Set 'Apply' button functionality
        mApplyBtnGyro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGyroOptionId = mGyroOptions.getCheckedRadioButtonId();

                if (selectedGyroOptionId == R.id.highFreqGyro) {

                    SplashActivity.GYRO_OPTION = 2;
                } else if (selectedGyroOptionId == R.id.medFreqGyro) {

                    SplashActivity.GYRO_OPTION = 1;
                } else {

                    SplashActivity.GYRO_OPTION = 0;
                }

                Toast.makeText(Settings2.this, "Options changed!", Toast.LENGTH_SHORT).show();

                Intent goToHomeIntent = new Intent(Settings2.this, MainActivity.class);
                startActivity(goToHomeIntent);
            }
        });

        // Set 'Left button' functionality
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGyroOptionId = mGyroOptions.getCheckedRadioButtonId();

                if (selectedGyroOptionId == R.id.highFreqGyro) {

                    SplashActivity.GYRO_OPTION = 2;
                } else if (selectedGyroOptionId == R.id.medFreqGyro) {

                    SplashActivity.GYRO_OPTION = 1;
                } else {

                    SplashActivity.GYRO_OPTION = 0;
                }

                Intent goToSettingsIntent = new Intent(Settings2.this, Settings.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                startActivity(goToSettingsIntent);
            }
        });

        // Set 'Right button' functionality
        mRightButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGyroOptionId = mGyroOptions.getCheckedRadioButtonId();

                if (selectedGyroOptionId == R.id.highFreqGyro) {

                    SplashActivity.GYRO_OPTION = 2;
                } else if (selectedGyroOptionId == R.id.medFreqGyro) {

                    SplashActivity.GYRO_OPTION = 1;
                } else {

                    SplashActivity.GYRO_OPTION = 0;
                }

                Intent goToSettingsIntent = new Intent(Settings2.this, Settings3.class);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                startActivity(goToSettingsIntent);
            }
        });

    }

    // Function to pre-check the radio buttons based on the already selected option
    void preCheckRadioButtons() {

        if(SplashActivity.GYRO_OPTION ==0)

        {

            mHighGyro.setChecked(false);
            mMedGyro.setChecked(false);
            mOffGyro.setChecked(true);
        }
        else if(SplashActivity.GYRO_OPTION ==1)

        {

            mHighGyro.setChecked(false);
            mMedGyro.setChecked(true);
            mOffGyro.setChecked(false);
        }
        else if(SplashActivity.GYRO_OPTION ==2)

        {

            mHighGyro.setChecked(true);
            mMedGyro.setChecked(false);
            mOffGyro.setChecked(false);
        }
    }
}
