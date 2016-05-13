package br.com.senior.research.gpstracker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import br.com.senior.research.gpstracker.tracking.services.SenderAlarm;

public class AutoStart extends BroadcastReceiver {   
    SenderAlarm alarm = new SenderAlarm();
    
    @Override
    public void onReceive(Context context, Intent intent) {   
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //alarm.schedule(context, 5000);

            //Intent serviceIntent = new Intent(context, LocationSensorTrackService.class);
            //context.startService(serviceIntent);
            Toast.makeText(context, "On Boot from GPS Tracker", Toast.LENGTH_LONG).show();
        }
    }
}