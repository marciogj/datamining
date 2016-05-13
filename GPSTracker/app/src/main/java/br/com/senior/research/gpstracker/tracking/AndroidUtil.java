package br.com.senior.research.gpstracker.tracking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Marcio.Jasinski on 19/11/2015.
 */
public class AndroidUtil {
    private static final String TAG = AndroidUtil.class.getName();


    //http://stackoverflow.com/questions/4086159/checking-internet-connection-on-android
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        if (!networkInfo.isConnected()) {
            return false;
        }
        if (!networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }


    //http://stackoverflow.com/questions/21858528/convert-a-bundle-to-json
    public static JSONObject toJson(Bundle bundle) {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                json.put(key, JSONObject.wrap(bundle.get(key)));
            } catch(JSONException e) {
                Log.e(TAG, "Error converting bundle data into json", e);
                json = null;
            }
        }
        return json;
    }


    public static Bundle toBundle(String strJson) {
        Bundle bundle = new Bundle();
        try {
            JSONObject json = new JSONObject(strJson);
            for (Iterator<String> iter =  json.keys(); iter.hasNext(); ) {
                String key = iter.next();
                bundle.putString(key, json.getString(key));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse json into bundle: " + strJson);
            e.printStackTrace();
        }

        return bundle;
    }


}
