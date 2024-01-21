package com.example.odyssey.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class RotationDetector {

    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private OnRotationListener listener;

    public RotationDetector(Context context)

    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void setOnRotationListener(OnRotationListener listener) {
        this.listener = listener;
    }

    public void startListening() {
        sensorManager.registerListener(sensorEventListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopListening() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override


        public

        void

        onSensorChanged(SensorEvent event)

        {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                float[] rotationMatrix = new

                        float[9];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

                float[] orientationAngles = new

                        float[3];
                SensorManager.getOrientation(rotationMatrix, orientationAngles);

                float azimuth = orientationAngles[0];
                float pitch = orientationAngles[1];
                float roll = orientationAngles[2];

                if (listener != null) {
                    listener.onRotationChanged(azimuth, pitch, roll);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not needed for this implementation
        }
    };

    public interface OnRotationListener {
        void onRotationChanged(float azimuth, float pitch, float roll);
    }
}