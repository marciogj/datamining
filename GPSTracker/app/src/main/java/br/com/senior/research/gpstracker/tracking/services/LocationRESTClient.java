package br.com.senior.research.gpstracker.tracking.services;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import br.com.senior.research.gpstracker.tracking.services.dao.LocationStorage;

/**
 * Created by marcio.jasinski on 10/11/2015.
 * <p/>
 * https://github.com/koush/ion
 */
public class LocationRESTClient {
    public final String TAG = LocationRESTClient.class.getName();
    public final int BATCH_SIZE = 50;
    public final String POST_TRACK_SERVICE_URL = "http://ec2-54-207-45-157.sa-east-1.compute.amazonaws.com:9999/track/marciogj";
    public final String PING_TRACK_SERVICE_URL = "http://ec2-54-207-45-157.sa-east-1.compute.amazonaws.com:9999/ping";

    private Context context;
    private LocationStorage storage;

    LocationRESTClient(Context context) {
        this.context = context;
        storage = LocationStorage.getInstance(context);
    }

    public void sendPendingCoordinates() {
        Log.d(TAG, "sendPendingCoordinates");

        int pendingCoordinates = storage.count();
        int offset = -1;
        Log.d(TAG, "Pending coordinates " + pendingCoordinates);
        int batch = 1;
        while (pendingCoordinates > 0) {
            Log.d(TAG, "Sending batch. Pending batches " + (batch++) + " - offset " + offset + " - " + (batch+BATCH_SIZE));
            sendCoordinateBatch(offset + 1, BATCH_SIZE);
            offset +=  BATCH_SIZE;
            pendingCoordinates -= BATCH_SIZE;
        }
    }

    private void sendCoordinateBatch(int offset, int count) {
        final Collection<Location> locations = storage.load(offset, count);
        TrackedIdentity identity = storage.loadIdentity();
        JsonObject jsonPackage = toJson(identity, locations);

        Ion.with(context)
                .load(POST_TRACK_SERVICE_URL)
                .setJsonObjectBody(jsonPackage)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null && "success".equals(result.get("status").getAsString())) {
                            storage.remove(locations);
                        }
                        if (e != null) {
                            Log.d(TAG, "Error: " + e.getMessage());
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            Log.d(TAG, sw.toString());
                        }
                    }
                });

    }

    /*
    private void ping() {
        Ion.with(context)
                .load(PING_TRACK_SERVICE_URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        Log.i(TAG, "GET Result from Callback: " + result);
                        Log.i(TAG, "GET Exception: " + e.getMessage());
                    }
                });
    }
    */

    private JsonObject toJson(TrackedIdentity identity, Collection<Location> allLocations) {
        JsonObject obj = toJson(identity);
        obj.add("coordinates", toJsonArray(allLocations));
        return obj;
    }

    private JsonArray toJsonArray(Collection<Location> allLocations) {
        final JsonArray jsonLocations = new JsonArray();
        for (Location location : allLocations) {
            jsonLocations.add(toJson(location));
        }
        return jsonLocations;
    }

    private JsonObject toJson(Location location) {
        JsonObject json = new JsonObject();
        json.addProperty("timestamp", location.getTime());
        json.addProperty("longitude", location.getLongitude());
        json.addProperty("latitude", location.getLatitude());
        json.addProperty("altitude", location.getAltitude());
        json.addProperty("accuracy", location.getAccuracy());
        json.addProperty("bearing", location.getBearing());
        json.addProperty("speed", location.getSpeed());

        //TODO: Fix the bundle parser
        json.addProperty("extras", location.getExtras().toString());
        return json;
    }

    private JsonObject toJson(TrackedIdentity device) {
        JsonObject json = new JsonObject();
        json.addProperty("deviceId", device.getDeviceId());
        json.addProperty("tenantId", device.getTenantId());
        json.addProperty("userId", device.getUserId());
        return json;
    }

}
