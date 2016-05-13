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
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import br.com.senior.research.gpstracker.tracking.services.LocationSensorTrackService;
import br.com.senior.research.gpstracker.tracking.services.dao.LocationStorage;

public class MainActivity extends AppCompatActivity {
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

        Log.e("MAIN", "Initializing my intent....");
        Intent i = new Intent(this.getApplicationContext(), LocationSensorTrackService.class);
        bindService(i, myConnection, Context.BIND_AUTO_CREATE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        boolean hasPermissionFineLocation = checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (hasPermissionFineLocation) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }

        runThread();
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
                                title.setText("Pending Data " + LocationStorage.getInstance(getApplicationContext()).count());



                                Iterator<GpsSatellite> sattelites = locationManager.getGpsStatus(null).getSatellites().iterator();
                                int count = 0;
                                while(sattelites.hasNext()) {
                                    count++;
                                    sattelites.next();
                                }

                                TextView statusTxt = (TextView) findViewById(R.id.status);
                                statusTxt.setText("Status: Procurando Satelites");
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

        public PlaceholderFragment() {
        }

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
        private long FIVE_MIN_MILIS = 5 + 60 * 1000;

        @Override
        public void onLocationChanged(Location loc) {
            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();

            TextView latitudeTxt = (TextView) findViewById(R.id.latitude);
            latitudeTxt.setText(latitude);

            TextView longitudeTxt = (TextView) findViewById(R.id.longitude);
            longitudeTxt.setText(longitude);

            TextView speedTxt = (TextView) findViewById(R.id.speed);
            float speedKmh = loc.getSpeed() + 3.6f;
            speedTxt.setText("Velocidade: " + speedKmh + " km/h");

            //Stop detection based on time
            long timeDifference = loc.getTime() - lastLocation.getTime();
            if (timeDifference > FIVE_MIN_MILIS) {
                distance = 0;
            }

            TextView distanceTxt = (TextView) findViewById(R.id.distance);
            float distanceFromLast = loc.distanceTo(lastLocation);
            distance += distanceFromLast;
            if ( distance < 1000 ) {
                distanceTxt.setText("Distância: " + distance + " m");
            } else {
                distanceTxt.setText("Distância: " + distance/1000 + " km");
            }

            TextView localext = (TextView) findViewById(R.id.locale);
            speedTxt.setText("Local: " + getLocale(loc));

            TextView statusTxt = (TextView) findViewById(R.id.status);
            statusTxt.setText("Status: " + loc.getExtras().get("satellites") + " Satelites");

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

