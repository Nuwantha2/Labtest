package com.s23010901;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor;

    private TextView tempText;
    private MediaPlayer mediaPlayer;

    private Button pauseButton, backButton;
    private boolean isPlaying = false;

    private final float TEMPERATURE_THRESHOLD = 1.0f; // Based on SID s23010901

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // UI Components
        tempText = findViewById(R.id.tempText);
        pauseButton = findViewById(R.id.pauseButton);

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.alert); // Audio file

        // Setup Sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            if (temperatureSensor == null) {
                Toast.makeText(this, "Ambient Temperature Sensor not available", Toast.LENGTH_LONG).show();
            }
        }

        // Pause Button Action
        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
                Toast.makeText(this, "Audio Paused", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temperatureSensor != null)
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // Optionally pause audio when activity is not visible
            isPlaying = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float temp = event.values[0];
        tempText.setText("Current Temp: " + temp + "°C");

        if (temp > TEMPERATURE_THRESHOLD && !isPlaying) {
            isPlaying = true;
            mediaPlayer.start();
            Toast.makeText(this, "Threshold exceeded — playing alert", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
