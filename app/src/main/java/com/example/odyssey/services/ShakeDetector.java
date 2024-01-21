package com.example.odyssey.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeDetector {
    private static final float SHAKE_THRESHOLD = 15F; // Adjust as needed
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private OnShakeListener listener;

    public

    ShakeDetector(Context context)

    {
        sensorManager = (SensorManager)  context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.listener = listener;
    }

    public void startListening() {
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopListening() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override


        public void onSensorChanged(SensorEvent event)

        {
            float x = event.values[0];
            float y = event.values[1];


            float z = event.values[2];

            if (Math.abs(x) > SHAKE_THRESHOLD || Math.abs(y) > SHAKE_THRESHOLD || Math.abs(z) > SHAKE_THRESHOLD) {
                if (listener != null) {
                    listener.onShakeDetected();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not needed for this implementation
        }
    };

    public interface OnShakeListener {
        void onShakeDetected();
    }
}