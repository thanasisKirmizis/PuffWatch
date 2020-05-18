package com.kirman.puffwatch_v2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BackgroundService extends Service implements SensorEventListener {

    private SensorManager SM;
    PowerManager.WakeLock wL;

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String input = intent.getStringExtra("inputExtra");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.app_logo)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wL = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "kirman::MyWakelockTag");
        wL.acquire();

        initializeFiles();

        // Create our Sensor Manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Get accelerometer Sensor
        final Sensor accel_Sensor = Objects.requireNonNull(SM).getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Get gyroscope Sensor
        final Sensor gyro_Sensor = Objects.requireNonNull(SM).getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Get magnetometer Sensor
        final Sensor magnet_Sensor = Objects.requireNonNull(SM).getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        registerSensors(accel_Sensor, gyro_Sensor, magnet_Sensor);

        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        SM.unregisterListener(BackgroundService.this);

        // Save the applied settings
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(SplashActivity.PW_PREFS, 0);
        prefs.edit().putInt(SplashActivity.ACCEL_KEY, SplashActivity.ACCEL_OPTION).apply();
        prefs.edit().putInt(SplashActivity.GYRO_KEY, SplashActivity.GYRO_OPTION).apply();
        prefs.edit().putInt(SplashActivity.MAGNET_KEY, SplashActivity.MAGNET_OPTION).apply();

        // Close the File and Data Streams
        try {

            accel_f.close();
            gyro_f.close();
            magnet_f.close();

            accel_data_f.close();
            gyro_data_f.close();
            magnet_data_f.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

        wL.release();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch(event.sensor.getType()) {

            case Sensor.TYPE_ACCELEROMETER:

                writeToFile(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2], accel_data_f);
                break;

            case Sensor.TYPE_GYROSCOPE:

                writeToFile(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2], gyro_data_f);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:

                writeToFile(System.currentTimeMillis(), event.values[0], event.values[1], event.values[2], magnet_data_f);
                break;
        }
    }

    private void initializeFiles() {

        // Setup the output file path
        destPath = Objects.requireNonNull(getApplicationContext().getExternalFilesDir(null)).getAbsolutePath();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy_hh:mm:ss", Locale.getDefault()).format(new Date());
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
