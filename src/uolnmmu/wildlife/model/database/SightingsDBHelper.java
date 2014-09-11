package uolnmmu.wildlife.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SightingsDBHelper extends SQLiteOpenHelper {
	// logger
	private static final String LOGCAT = SightingsDBHelper.class
			.getSimpleName();

	private static SightingsDBHelper dbHelper = null;
	private static final String DATABASE_NAME = "SightingDatabase";
	private static final int DATABASE_VERSION = 1;

	// Table name
	protected static final String TABLE_SIGHTINGS = "Sightings";

	// Columns names
	protected static final String ID = "id";
	protected static final String ANIMAL_NAME = "animal_name";
	protected static final String IMAGE_PATH = "image_path";
	protected static final String GPS_LATITUDE = "gps_latitude";
	protected static final String GPS_LONGITUDE = "gps_longitude";
	protected static final String TIMESTAMP = "timestamp";
	protected static final String DESCRIPTION = "description";
	protected static final String OBSERVER_NAME = "observer_name";

	// Create table statement
	private String CREATE_SIGHTINGS_TABLE = "CREATE TABLE " + TABLE_SIGHTINGS
			+ "(" + ID + " INTEGER PRIMARY KEY," + ANIMAL_NAME + " TEXT,"
			+ IMAGE_PATH + " TEXT," + GPS_LATITUDE + " REAL,"
			+ GPS_LONGITUDE + " REAL," + TIMESTAMP
			+ " TEXT," + DESCRIPTION + " TEXT," + OBSERVER_NAME + " TEXT" + ")";

	// Constructor.
	private SightingsDBHelper(Context applicationContext) {
		super(applicationContext, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(LOGCAT, "SightingDataHelper created");
	}

	/**
	 * Singleton for database.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SightingsDBHelper getInstance(Context applicationContext) {
		if (dbHelper == null) {
			dbHelper = new SightingsDBHelper(applicationContext);
		}
		return dbHelper;
	}

	// Called when no database exists, to create a new one.
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SIGHTINGS_TABLE);
		Log.d(LOGCAT, "table created");
	}

	// Called when there is a database version mismatch and the database needs
	// to be upgraded to the current version.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIGHTINGS);

		// Create tables again
		onCreate(db);

	}

}
