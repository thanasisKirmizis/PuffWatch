package com.kirman.puffwatch_v2;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class Settings extends WearableActivity {

    Button mApplyBtnAccel;
    RadioButton mHighAccel;
    RadioButton mMedAccel;
    RadioButton mOffAccel;
    RadioGroup mAccelOptions;
    ImageButton mRightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Assign UI Elements
        mApplyBtnAccel = findViewById(R.id.applyBtnAccel);
        mHighAccel = findViewById(R.id.highFreqAccel);
        mMedAccel = findViewById(R.id.medFreqAccel);
        mOffAccel = findViewById(R.id.offAccel);
        mAccelOptions = findViewById(R.id.optionsAccel);
        mRightButton = findViewById(R.id.rightArrow);

        // Set the pre-checked radio buttons
        preCheckRadioButtons();

        // Set 'Apply' button functionality
        mApplyBtnAccel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedAccelOptionId = mAccelOptions.getCheckedRadioButtonId();

                if (selectedAccelOptionId == R.id.highFreqAccel) {

                    SplashActivity.ACCEL_OPTION = 2;
                } else if (selectedAccelOptionId == R.id.medFreqAccel) {

                    SplashActivity.ACCEL_OPTION = 1;
                } else {

                    SplashActivity.ACCEL_OPTION = 0;
                }

                Toast.makeText(Settings.this, "Options changed!", Toast.LENGTH_SHORT).show();

                Intent goToHomeIntent = new Intent(Settings.this, MainActivity.class);
                startActivity(goToHomeIntent);
            }
        });

        // Set 'Right button' functionality
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedAccelOptionId = mAccelOptions.getCheckedRadioButtonId();

                if (selectedAccelOptionId == R.id.highFreqAccel) {

                    SplashActivity.ACCEL_OPTION = 2;
                } else if (selectedAccelOptionId == R.id.medFreqAccel) {

                    SplashActivity.ACCEL_OPTION = 1;
                } else {

                    SplashActivity.ACCEL_OPTION = 0;
                }

                Intent goToSettingsIntent = new Intent(Settings.this, Settings2.class);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                startActivity(goToSettingsIntent);
            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent goToHomeIntent = new Intent(Settings.this, MainActivity.class);
        startActivity(goToHomeIntent);
    }

    // Function to pre-check the radio buttons based on the already selected option
    void preCheckRadioButtons() {

        if(SplashActivity.ACCEL_OPTION ==0)

        {

            mHighAccel.setChecked(false);
            mMedAccel.setChecked(false);
            mOffAccel.setChecked(true);
        }
            else if(SplashActivity.ACCEL_OPTION ==1)

        {

            mHighAccel.setChecked(false);
            mMedAccel.setChecked(true);
            mOffAccel.setChecked(false);
        }
            else if(SplashActivity.ACCEL_OPTION ==2)

        {

            mHighAccel.setChecked(true);
            mMedAccel.setChecked(false);
            mOffAccel.setChecked(false);
        }

    }
}
