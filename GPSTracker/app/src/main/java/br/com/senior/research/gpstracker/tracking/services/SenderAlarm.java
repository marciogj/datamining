package br.com.senior.research.gpstracker.tracking.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import br.com.senior.research.gpstracker.tracking.AndroidUtil;

public class SenderAlarm extends BroadcastReceiver {
	private static final String TAG = SenderAlarm.class.getName();
	private static final int REQUEST_CODE = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();
		try {
            boolean isInternetAvailable = AndroidUtil.isNetworkAvailable(context);
			Log.d(TAG, "isInternetAvailable " + isInternetAvailable);
            if ( isInternetAvailable ) {
                LocationRESTClient client = new LocationRESTClient(context);
                client.sendPendingCoordinates();
            }
		} finally {
			wl.release();
		}
	}

	public static void schedule(Context context, long scheduleInMilis) {
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SenderAlarm.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), scheduleInMilis, pendingIntent);
	}

	public static void cancel(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SenderAlarm.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}
}
