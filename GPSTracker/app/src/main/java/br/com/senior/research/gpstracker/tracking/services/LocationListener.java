package br.com.senior.research.gpstracker.tracking.services;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import br.com.senior.research.gpstracker.tracking.services.dao.LocationStorage;

/**
 * Created by marcio.jasinski on 06/11/2015.
 */
public class LocationListener implements android.location.LocationListener {
    private static String TAG = LocationListener.class.getName();
    private LocationStorage storage;

    public LocationListener(Location location, LocationStorage storage) {
        this.storage = storage;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location);
        storage.save(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: " + provider);
    }
}
