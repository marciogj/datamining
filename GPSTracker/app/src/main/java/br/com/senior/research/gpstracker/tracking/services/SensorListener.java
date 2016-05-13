package br.com.senior.research.gpstracker.tracking.services;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Created by marcio.jasinski on 11/11/2015.
 * http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125
 */
public class SensorListener implements SensorEventListener {
    private static String TAG = SensorListener.class.getName();
    private Sensor sensor = null;

    public SensorListener(Sensor sensor) {
        this.sensor = sensor;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: " + event.sensor.getName());

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged: " + sensor.getName());

    }

}
