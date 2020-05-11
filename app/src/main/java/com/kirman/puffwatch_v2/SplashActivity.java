package com.kirman.puffwatch_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

public class SplashActivity extends WearableActivity {

    // Public variables to define the selected options
    public static int ACCEL_OPTION;
    public static int GYRO_OPTION;
    public static int MAGNET_OPTION;
    public static String PW_PREFS = "PuffWatchPrefs";
    public static String ACCEL_KEY = "accelKey";
    public static String GYRO_KEY = "gyroKey";
    public static String MAGNET_KEY = "magnetKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retrieve the options from Shared Prefs with 'High Rate' being the default
        SharedPreferences prefs = getSharedPreferences(SplashActivity.PW_PREFS, MODE_PRIVATE);
        ACCEL_OPTION = prefs.getInt(ACCEL_KEY, 2);
        GYRO_OPTION = prefs.getInt(GYRO_KEY, 2);
        MAGNET_OPTION = prefs.getInt(MAGNET_KEY, 2);

        //This Activity is only used to display the icon as splash screen
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
