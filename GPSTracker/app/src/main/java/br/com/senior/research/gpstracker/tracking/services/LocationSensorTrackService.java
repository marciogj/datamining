package br.com.senior.research.gpstracker.tracking.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import br.com.senior.research.gpstracker.tracking.services.dao.LocationStorage;

/**
 * Created by marcio.jasinski on 06/11/2015.
 */
public class LocationSensorTrackService extends Service {
    private static final String TAG = LocationSensorTrackService.class.getName();
    private static final int LOCATION_INTERVAL = 1000;//minimum time interval between location updates, in milliseconds
    private static final float LOCATION_DISTANCE = 5f;//minimum distance between location updates, in meters
    private static final long ALARM_COORDINATE_UPLOAD_INTERVAL = 5000;

    private LocationManager locationManager = null;
    private SensorManager sensorManager = null;

    private LocationListener gpsListener = null;
    private SensorListener accelerometerListener = null;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId)  {
        Log.d(TAG, "onStartCommand");

        Toast.makeText(this, "GPSTracker Started", Toast.LENGTH_LONG).show();

        super.onStartCommand(intent, flags, startId);
        // http://stackoverflow.com/questions/9093271/start-sticky-and-start-not-sticky
        // START_STICKY is only relevant when the phone runs out of memory and kills the service before it finishes executing.
        // START_STICKY tells the OS to recreate the service after it has enough memory and call onStartCommand() again with a null intent
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        if (locationManager == null) {

            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Location gpsLocation =  new Location(LocationManager.GPS_PROVIDER);
            gpsListener = new LocationListener(gpsLocation, LocationStorage.getInstance(getApplicationContext()));

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


            //TYPE_AMBIENT_TEMPERATURE
            //TYPE_GRAVITY
            //TYPE_GYROSCOPE
            //TYPE_LINEAR_ACCELERATION
            //TYPE_MAGNETIC_FIELD
            //TYPE_ORIENTATION
            //TYPE_PRESSURE
            //TYPE_RELATIVE_HUMIDITY
            //TYPE_ROTATION_VECTOR

            /*
            accelerometerListener = new SensorListener(accelerometerSensor);
            registerSensorListener(accelerometerSensor, accelerometerListener, SensorManager.SENSOR_DELAY_NORMAL);
            */
            registerLocationListener(LocationManager.GPS_PROVIDER, gpsListener, LOCATION_INTERVAL, LOCATION_DISTANCE);

            //Sender alarm is scheduled here and cancelled on destroy.
            SenderAlarm.schedule(getApplicationContext(), ALARM_COORDINATE_UPLOAD_INTERVAL);

        }
    }

    private void registerSensorListener(Sensor sensor, SensorListener listener, int delayMicroseconds) {
        Log.d(TAG, "registerSensorListener " + sensor);
        try {
            sensorManager.registerListener(listener, sensor, delayMicroseconds);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "Could not initialize listener since it was not authorized to use " + sensor, ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "sensor " + sensor + " does not exist on this phone." + ex.getMessage());
        }
    }

    private void registerLocationListener(String provider, android.location.LocationListener listener, int minInterval, float minDistance) {
        Log.d(TAG, "registerLocationListener " + provider);
        try {
            locationManager.requestLocationUpdates(provider, minInterval, minDistance, listener);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "Could not initialize listener since it was not authorized to use " + provider, ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Provider " + provider + " does not exist on this phone." + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        removeLocationUpdates(gpsListener);
        removeSensorUpdates(accelerometerListener);
        //Sender alarm is cancelled here and schedulled on create.
        SenderAlarm.cancel(getApplicationContext());
    }

    private void removeLocationUpdates(android.location.LocationListener listener) {
        Log.d(TAG, "removeLocationUpdates");
        if (locationManager == null || listener == null) return;

        boolean hasPermissionFineLocation = checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (hasPermissionFineLocation) {
            locationManager.removeUpdates(listener);
        }

    }

    private void removeSensorUpdates(SensorListener listener) {
        Log.d(TAG, "removeSensorUpdates");
        sensorManager.unregisterListener(listener);
    }


    //-------------------------------------------


    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            String dataString = data.getString("MyString");
            Toast.makeText(getApplicationContext(),
                    dataString, Toast.LENGTH_SHORT).show();
        }
    }

    final Messenger myMessenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return myMessenger.getBinder();
    }
}
