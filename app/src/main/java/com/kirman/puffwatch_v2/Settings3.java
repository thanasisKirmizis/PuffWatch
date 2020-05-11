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

public class Settings3 extends WearableActivity {

    Button mApplyBtnMagnet;
    RadioButton mHighMagnet;
    RadioButton mMedMagnet;
    RadioButton mOffMagnet;
    RadioGroup mMagnetOptions;
    ImageButton mLeftButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings3);

        // Assign UI Elements
        mApplyBtnMagnet = findViewById(R.id.applyBtnMagnet);
        mHighMagnet = findViewById(R.id.highFreqMagnet);
        mMedMagnet = findViewById(R.id.medFreqMagnet);
        mOffMagnet = findViewById(R.id.offMagnet);
        mMagnetOptions = findViewById(R.id.optionsMagnet);
        mLeftButton2 = findViewById(R.id.leftArrow2);

        // Set the pre-checked radio button
        preCheckRadioButtons();

        // Set 'Apply' button functionality
        mApplyBtnMagnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedGyroOptionId = mMagnetOptions.getCheckedRadioButtonId();

                if (selectedGyroOptionId == R.id.highFreqMagnet) {

                    SplashActivity.MAGNET_OPTION = 2;
                } else if (selectedGyroOptionId == R.id.medFreqMagnet) {

                    SplashActivity.MAGNET_OPTION = 1;
                } else {

                    SplashActivity.MAGNET_OPTION = 0;
                }

                Toast.makeText(Settings3.this, "Options changed!", Toast.LENGTH_SHORT).show();

                Intent goToHomeIntent = new Intent(Settings3.this, MainActivity.class);
                startActivity(goToHomeIntent);
            }
        });

        // Set 'Left button' functionality
        mLeftButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedMagnetOptionId = mMagnetOptions.getCheckedRadioButtonId();

                if (selectedMagnetOptionId == R.id.highFreqMagnet) {

                    SplashActivity.MAGNET_OPTION = 2;
                } else if (selectedMagnetOptionId == R.id.medFreqMagnet) {

                    SplashActivity.MAGNET_OPTION = 1;
                } else {

                    SplashActivity.MAGNET_OPTION = 0;
                }

                Intent goToSettingsIntent = new Intent(Settings3.this, Settings2.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                startActivity(goToSettingsIntent);
            }
        });

    }

    // Function to pre-check the radio buttons based on the already selected option
    void preCheckRadioButtons() {

        if(SplashActivity.MAGNET_OPTION ==0)

        {

            mHighMagnet.setChecked(false);
            mMedMagnet.setChecked(false);
            mOffMagnet.setChecked(true);
        }
        else if(SplashActivity.MAGNET_OPTION ==1)

        {

            mHighMagnet.setChecked(false);
            mMedMagnet.setChecked(true);
            mOffMagnet.setChecked(false);
        }
        else if(SplashActivity.MAGNET_OPTION ==2)

        {

            mHighMagnet.setChecked(true);
            mMedMagnet.setChecked(false);
            mOffMagnet.setChecked(false);
        }
    }
}
