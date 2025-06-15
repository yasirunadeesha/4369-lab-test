package com.s23010269.ramanayaka;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.s23010269.ramanayaka.R; // Ensure correct package for R

public class tempAudio extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private TextView temperatureValue, statusText;
    private Button stopAudioButton;
    private MediaPlayer mediaPlayer;
    private boolean isAudioPlaying = false;
    private final float TEMPERATURE_THRESHOLD = 69.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);


        temperatureValue = findViewById(R.id.temperature_value);
        statusText = findViewById(R.id.status_text);
        stopAudioButton = findViewById(R.id.stop_audio_button);
        Button backButton = findViewById(R.id.back_button);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (temperatureSensor == null) {
            statusText.setText("Temperature sensor not available on this device.");
            stopAudioButton.setEnabled(false);
        }


        mediaPlayer = MediaPlayer.create(this, R.raw.alert);
        if (mediaPlayer == null) {
            statusText.setText("Error: Audio file not found or invalid.");
            stopAudioButton.setEnabled(false);
        }


        stopAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(tempAudio.this, activity_map.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (temperatureSensor != null) {
            sensorManager.unregisterListener(this);
        }
        stopAudio();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float currentTemperature = event.values[0];
            temperatureValue.setText(String.format("%.1f °C", currentTemperature));

            if (currentTemperature > TEMPERATURE_THRESHOLD && !isAudioPlaying) {
                playAudio();
            } else if (currentTemperature <= TEMPERATURE_THRESHOLD && isAudioPlaying) {
                stopAudio();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void playAudio() {
        if (mediaPlayer != null && !isAudioPlaying) {
            mediaPlayer.start();
            isAudioPlaying = true;
            statusText.setText("Audio playing: Temperature above 69°C");
            stopAudioButton.setEnabled(true);
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null && isAudioPlaying) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            isAudioPlaying = false;
            statusText.setText("Audio stopped: Temperature below 69°C or manually stopped");
            stopAudioButton.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}