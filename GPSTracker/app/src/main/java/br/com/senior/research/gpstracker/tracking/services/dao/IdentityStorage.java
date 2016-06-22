package br.com.senior.research.gpstracker.tracking.services.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import br.com.senior.research.gpstracker.tracking.services.model.TrackedIdentity;

public class IdentityStorage extends SQLiteOpenHelper {
    private static String TAG = IdentityStorage.class.getName();
	private static final String[] EMPTY_ARGUMENT = null;
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "identities";
    private static IdentityStorage instance = null;
    private Context context;

    public static IdentityStorage getInstance(Context ctx) {
        if (instance == null) {
            instance = new IdentityStorage(ctx.getApplicationContext());
        }
        return instance;
    }

	private IdentityStorage(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
            cursor = database.rawQuery("SELECT COUNT(userId) FROM " + TABLE_NAME, EMPTY_ARGUMENT);
            cursor.moveToFirst();
            count = cursor.getInt(0);
        } finally {
            close(cursor);
            close(database);
        }
        return count;
    }

	public void save(TrackedIdentity identity) {
        Log.d(TAG, "save");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("userId", identity.getUserId());
            values.put("deviceId", identity.getDeviceId());
            values.put("tenantId", identity.getTenantId());
            values.put("username", identity.getUsername());
            values.put("email", identity.getEmail());
            database.insert(TABLE_NAME, null, values);
        } finally {
            close(database);
        }
    }

    public void update(TrackedIdentity identity) {
        Log.d(TAG, "save");
        SQLiteDatabase database = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("userId", identity.getUserId());
            values.put("tenantId", identity.getTenantId());
            values.put("username", identity.getUsername());
            values.put("email", identity.getEmail());
            database.update(TABLE_NAME, values, "deviceId = ?", new String[] {identity.getDeviceId()});
        } finally {
            close(database);
        }
    }

    public TrackedIdentity load() {
        Log.d(TAG, "load");
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = null;
        TrackedIdentity identity = null;
		try {
            String whereClause = null, groupBy = null, having = null, orderBy = null, strLimit = null;
            String[] columnNames = null; //null is used for SELECT * FROM
            String[] whereArgs = null;

            cursor = database.query(TABLE_NAME, columnNames, whereClause, whereArgs, groupBy, having, orderBy, strLimit);
            if (cursor.moveToFirst()) {
                identity = cursorToIdentity(cursor);
            }

        } finally {
            close(cursor);
            close(database);
        }
		return identity;
	}

    private TrackedIdentity cursorToIdentity(final Cursor cursor) {
        TrackedIdentity identity = new TrackedIdentity();
        identity.setUserId(cursor.getString(0));
        identity.setDeviceId(cursor.getString(1));
        identity.setTenantId(cursor.getString(2));
        identity.setUsername(cursor.getString(3));
        identity.setEmail(cursor.getString(4));
        return identity;
    }

    private String getCreateTableScript() {
        String query  ="CREATE TABLE " + TABLE_NAME + " (" +
            "userId TEXT, " +
            "deviceId TEXT, " +
            "tenantId TEXT, " +
            "username TEXT, " +
            "email TEXT " +
         ");";

        return query;
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