package br.com.senior.research.gpstracker.tracking.services.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.Collection;
import java.util.LinkedList;

import br.com.senior.research.gpstracker.tracking.AndroidUtil;
import br.com.senior.research.gpstracker.tracking.services.TrackedIdentity;

public class LocationStorage extends SQLiteOpenHelper {
    private static String TAG = LocationStorage.class.getName();
	private static final String[] EMPTY_ARGUMENT = null;
	private static final int DATABASE_VERSION = 2;
	private static final String TABLE_NAME = "location_history";
    private static LocationStorage instance = null;

    public static LocationStorage getInstance(Context ctx) {
        if (instance == null) {
            instance = new LocationStorage(ctx.getApplicationContext());
        }
        return instance;
    }


	private LocationStorage(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
		db.execSQL(getCreateTableScript());
        //TODO: Evaluate close db. For some reason a close db here will break any count atteptive from app initializing.
        //close(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        Log.d(TAG, "onUpgrade");
		// TODO Auto-generated method stub
	}

	public int count() {
        Log.d(TAG, "count");
        int count = 0;
        Cursor cursor = null;
        SQLiteDatabase database = null;
        try {
            database = this.getReadableDatabase();
            cursor = database.rawQuery("SELECT COUNT(longitude) FROM " + TABLE_NAME, EMPTY_ARGUMENT);
            cursor.moveToFirst();
            count = cursor.getInt(0);
        } finally {
            close(cursor);
            close(database);
        }
		return count;
	}

	public void save(Location location) {
        Log.d(TAG, "save");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("datetime", location.getTime());
            values.put("longitude", location.getLongitude());
            values.put("latitude", location.getLatitude());
            values.put("altitude", location.getAltitude());
            values.put("accuracy", location.getAccuracy());
            values.put("speed", location.getSpeed());
            values.put("bearing", location.getBearing());
            values.put("extras", AndroidUtil.toJson(location.getExtras()).toString());

            database.insert(TABLE_NAME, null, values);
        } finally {
            close(database);
        }
    }

    public Collection<Location> load(int skip, int count) {
        Log.d(TAG, "load");
        Collection<Location> records = new LinkedList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = null;
		try {
            String whereClause = null, groupBy = null, having = null, orderBy = null, strLimit = skip + ", " + count;
            String[] columnNames = null; //null is used for SELECT * FROM
            String[] whereArgs = null;

            cursor = database.query(TABLE_NAME, columnNames, whereClause, whereArgs, groupBy, having, orderBy, strLimit);
            if (cursor.moveToFirst()) {
                do {
                    records.add(cursorToLocation(cursor));
                } while (cursor.moveToNext());
            }

        } finally {
            close(cursor);
            close(database);
        }
		return records;
	}

    public void remove(Collection<Location> locations) {
        Log.d(TAG, "remove");
        SQLiteDatabase database = this.getWritableDatabase();
        int i = 0;
        try {
            String[] whereArgs = new String[locations.size()];
            String placeHolders = "";
            for (Location location: locations) {
                if (i != 0) {
                    placeHolders += ",";
                }
                whereArgs[i++] = ""+location.getTime();
                placeHolders += "?";
            }

            String whereClause = "datetime IN ("+ placeHolders +")";
            String[] columnNames = null; //null is used for SELECT * FROM
            int affectedRows = database.delete(TABLE_NAME, whereClause, whereArgs);
            Log.d(TAG, "remove affected " + affectedRows + " rows");
        } finally {
            close(database);
        }
    }

    private Location cursorToLocation(final Cursor cursor) {
        //Log.d(TAG, "cursorToLocation");
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setTime(cursor.getLong(0));
        location.setLongitude(cursor.getDouble(1));
        location.setLatitude(cursor.getDouble(2));
        location.setAltitude(cursor.getDouble(3));
        location.setAccuracy(cursor.getFloat(4));
        location.setSpeed(cursor.getFloat(5));
        location.setBearing(cursor.getFloat(6));
        location.setExtras(AndroidUtil.toBundle(cursor.getString(7)));
        return location;
    }

    private String getCreateTableScript() {
        String query  ="CREATE TABLE " + TABLE_NAME + " (" +
            "datetime LONG, " +
            "longitude REAL, " +
            "latitude REAL, " +
            "altitude REAL, " +
            "accuracy REAL, " +
            "speed REAL, " +
            "bearing REAL, " +
            "extras TEXT " +
         ");";

        return query;
    }

    public TrackedIdentity loadIdentity() {
        //TODO: Add implementation to save and load phone identity
        return new TrackedIdentity();
    }

    private void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    private void close(SQLiteDatabase database) {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

}