package br.com.senior.research.gpstracker;

import android.Manifest;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.senior.research.gpstracker.tracking.IdentityProvider;
import br.com.senior.research.gpstracker.tracking.services.LocationSensorTrackService;
import br.com.senior.research.gpstracker.tracking.services.dao.IdentityStorage;
import br.com.senior.research.gpstracker.tracking.services.dao.LocationStorage;
import br.com.senior.research.gpstracker.tracking.services.model.TrackedIdentity;

public class MainActivity extends AppCompatActivity {
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 121;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        requestPermissionsIfNotAllowed();
        registerIdentity();
        Log.e("MAIN", "Initializing my intent....");
        Intent i = new Intent(this.getApplicationContext(), LocationSensorTrackService.class);
        bindService(i, myConnection, Context.BIND_AUTO_CREATE);



        /*
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        boolean hasPermissionFineLocation = checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (hasPermissionFineLocation) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
        */

        runThread();
    }

    private void registerIdentity() {
        IdentityProvider identityProvider = new IdentityProvider(getApplicationContext());
        IdentityStorage identityStorage = IdentityStorage.getInstance(getApplicationContext());
        if (identityStorage.count() == 0 ) {
            TrackedIdentity identity = new TrackedIdentity();
            identity.setTenantId("senior");
            identity.setDeviceId(identityProvider.getDeviceId());
            identity.setUserId(identityProvider.getUsername());
            identity.setEmail(identityProvider.getEmail());
            identity.setUsername(identityProvider.getUsername());
            identityStorage.save(identity);
        } else {
            TrackedIdentity identity = new TrackedIdentity();
            identity.setTenantId("senior");
            //identity.setUserId(identityProvider.getUsername());
            identity.setEmail(identityProvider.getEmail());
            identity.setUsername(identityProvider.getUsername());
            identityStorage.update(identity);
        }
    }



    private void runThread() {

        new Thread() {
            public void run() {

                while (true) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                LocationStorage storage = LocationStorage.getInstance(getApplicationContext());
                                TextView title = (TextView) findViewById(R.id.pendingDataLabel);
                                title.setText("Dados: " + storage.count());

                                Location latestLocation = storage.loadLatest();
                                updateUI(latestLocation);


                            }
                        });
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private Location lastLocation;
    private double distance;
    private long FIVE_MIN_MILIS = 5 * 60 * 1000;
    private void updateUI(Location loc) {
        String longitude = "Longitude: ";
        String latitude = "Latitude: ";
        String speed = "Velocidade: ";
        String status = "Status: Procurando Satélites";
        String accuracy = "Precisão: ";
        String strDistance = "Distância: ";
        long currentTime = System.currentTimeMillis();
        long updateInterval = currentTime - ( loc == null ? 0 : loc.getTime());
        if (loc != null && updateInterval < FIVE_MIN_MILIS) {
            longitude += loc.getLongitude();
            latitude +=  loc.getLatitude();

            float speedKmh = loc.getSpeed() * 3.6f;
            status += loc.getExtras().get("satellites") + " Satélites";
            accuracy +=  loc.getAccuracy() + " m";

            long timeDifference = loc.getTime() - ( lastLocation == null ? 0 : lastLocation.getTime());
            if (timeDifference > FIVE_MIN_MILIS) {
                distance = 0;
            }

            float distanceFromLast = lastLocation == null ? 0 : loc.distanceTo(lastLocation);
            distance += distanceFromLast;

            strDistance += String.format(Locale.ENGLISH,"%.2f", distance) + " m";
            if ( distance >= 1000 ) {
                strDistance += String.format(Locale.ENGLISH, "%.2f", distance/1000) + " km";
            }

            lastLocation = loc;
        }

        TextView latitudeTxt = (TextView) findViewById(R.id.latitude);
        latitudeTxt.setText(latitude);

        TextView longitudeTxt = (TextView) findViewById(R.id.longitude);
        longitudeTxt.setText(longitude);

        TextView speedTxt = (TextView) findViewById(R.id.speed);
        speedTxt.setText(speed);

        TextView distanceTxt = (TextView) findViewById(R.id.distance);
        distanceTxt.setText(strDistance);

        //TextView localeTxt = (TextView) findViewById(R.id.locale);
        //localeTxt.setText("Local: " + getLocale(loc));

        TextView statusTxt = (TextView) findViewById(R.id.status);
        statusTxt.setText(status);

        TextView accuracyTxt = (TextView) findViewById(R.id.accuracy);
        accuracyTxt.setText(accuracy);
    }

    //https://developer.android.com/training/permissions/requesting.html
    private boolean checkPermission(String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
        return PackageManager.PERMISSION_GRANTED == permissionCheck;
    }

    private boolean isPermissionsAllowed() {
        boolean isCoarseLocationAllowed = checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean isFineLocationAllowed = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        boolean isAccountAllowed = checkPermission(Manifest.permission.GET_ACCOUNTS);
        return isCoarseLocationAllowed && isFineLocationAllowed && isAccountAllowed;
    }

    private void requestPermissionsIfNotAllowed() {
        if (!isPermissionsAllowed()) {
            String[] locationPermissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.GET_ACCOUNTS};
            ActivityCompat.requestPermissions(this, locationPermissions, PERMISSION_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        //if (id == R.id.action_settings) {
        //    return true;
        //}
        //return super.onOptionsItemSelected(item);
        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    Messenger myService = null;
    boolean isBound;
    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            myService = new Messenger(service);
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            myService = null;
            isBound = false;
        }
    };

    public void sendMessage() {
        if (!isBound) return;

        Message msg = Message.obtain();

        Bundle bundle = new Bundle();
        bundle.putString("MyString", "Message Received");

        msg.setData(bundle);

        try {
            myService.send(msg);


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class MyLocationListener implements LocationListener {
        private Location lastLocation;
        private double distance;
        private long FIVE_MIN_MILIS = 5 * 60 * 1000;

        @Override
        public void onLocationChanged(Location loc) {
            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();

            TextView latitudeTxt = (TextView) findViewById(R.id.latitude);
            latitudeTxt.setText(latitude);

            TextView longitudeTxt = (TextView) findViewById(R.id.longitude);
            longitudeTxt.setText(longitude);

            TextView speedTxt = (TextView) findViewById(R.id.speed);
            float speedKmh = loc.getSpeed() * 3.6f;
            speedTxt.setText("Velocidade: " + speedKmh + " km/h");

            //Stop detection based on time
            long timeDifference = loc.getTime() - ( lastLocation == null ? 0 : lastLocation.getTime());
            if (timeDifference > FIVE_MIN_MILIS) {
                distance = 0;
            }

            TextView distanceTxt = (TextView) findViewById(R.id.distance);
            float distanceFromLast = lastLocation == null ? 0 : loc.distanceTo(lastLocation);
            distance += distanceFromLast;

            if ( distance < 1000 ) {
                distanceTxt.setText("Distância: " + String.format(Locale.ENGLISH,"%.2f", distance) + " m");
            } else {
                distanceTxt.setText("Distância: " + String.format(Locale.ENGLISH, "%.2f", distance/1000) + " km");
            }

            //TextView localeTxt = (TextView) findViewById(R.id.locale);
            //localeTxt.setText("Local: " + getLocale(loc));

            TextView statusTxt = (TextView) findViewById(R.id.status);
            statusTxt.setText("Status: " + loc.getExtras().get("satellites") + " Satélites");

            TextView accuracyTxt = (TextView) findViewById(R.id.accuracy);
            accuracyTxt.setText("Precisão: " + loc.getAccuracy() + " m");

            lastLocation = loc;
        }

        private String getLocale(Location loc) {
            String cityName = "";
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return cityName;
        }

        @Override
        public void onProviderDisabled(String provider) {
            TextView latitudeTxt = (TextView) findViewById(R.id.status);
            latitudeTxt.setText("Status: Disabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            TextView latitudeTxt = (TextView) findViewById(R.id.status);
            latitudeTxt.setText("Status: Enabled");

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            TextView statusTxt = (TextView) findViewById(R.id.status);
            //OUT_OF_SERVICE = 0;
            //TEMPORARILY_UNAVAILABLE = 1;
            //AVAILABLE = 2;
            statusTxt.setText("Status: " + (status <= 1 ? "Sem serviço/Indisponível" : "Disponível"));


        }


    }

}

