package uolnmmu.wildlife.model.database;

import java.util.ArrayList;
import java.util.List;

import uolnmmu.wildlife.model.dataTransferObject.Sighting;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SightingDatabase implements ISightingDatabase,
		IUpdateSightingDatabase {

	// logger
	private static final String LOGCAT = SightingDatabase.class.getSimpleName();

	private SightingsDBHelper sightingsDBHelper;
	private NewSightingDBHelper newSightingDBHelper;
	private SQLiteDatabase sightingsDatabase;
	private SQLiteDatabase newSightingDatabase;

	/**
	 * Constructor.
	 * 
	 * @param applicationContext
	 *            Application Context
	 */
	public SightingDatabase(Context applicationContext) {
		this.sightingsDBHelper = SightingsDBHelper
				.getInstance(applicationContext);
		this.newSightingDBHelper = NewSightingDBHelper
				.getInstance(applicationContext);
	}

	@Override
	public void addSighting(Sighting sighting) {

		// put sighting information into a ContentValues object
		ContentValues values = sightingToContentValue(sighting);

		// Insert Sighting into SightingsDatabase
		insertSightingToSightingsDB(values);

		openWritableNewSightingDB();

		// insert the information and get the id in return
		long insertId = newSightingDatabase.insert(
				NewSightingDBHelper.TABLE_SIGHTINGS, null, values);

		if (insertId == -1) {
			Log.d(LOGCAT, "Sighting could not be added.");
		} else {
			Log.d(LOGCAT, "new Sighting added. ID: " + insertId);
			Log.d(LOGCAT, "ImagePath: " + sighting.getImageName());
		}

		// close NewSightingsDatabase
		closeNewSightingDB();
	}

	@Override
	public Sighting getSighting(int sightingId) {
		openReadableSightingsDB();
		Sighting sighting = new Sighting();

		// Select ALL where id=sightingId
		String selectQuery = "SELECT * FROM "
				+ SightingsDBHelper.TABLE_SIGHTINGS + " WHERE "
				+ SightingsDBHelper.ID + "=" + sightingId;

		Cursor cursor = sightingsDatabase.rawQuery(selectQuery, null);

		cursor.moveToFirst();

		// prepare sighting
		sighting.setSightingId(cursor.getInt(0));
		sighting.setAnimalName(cursor.getString(1));
		sighting.setImageName(cursor.getString(2));
		sighting.setLatitude(cursor.getDouble(3));
		sighting.setLongitude(cursor.getDouble(4));
		sighting.setTimestamp(cursor.getString(5));
		sighting.setDescription(cursor.getString(6));
		sighting.setObserverName(cursor.getString(7));

		cursor.close();
		closeSightingsDB();

		return sighting;
	}

	@Override
	public List<Sighting> getSightingList() {
		openReadableSightingsDB();
		List<Sighting> sightings = new ArrayList<Sighting>();

		// Select ALL query
		String selectQuery = "SELECT * FROM "
				+ SightingsDBHelper.TABLE_SIGHTINGS;
		Cursor cursor = sightingsDatabase.rawQuery(selectQuery, null);

		// read sightings from courser and store them in a list
		sightings = createSightingList(cursor);

		// close Cursor and Database
		cursor.close();
		closeSightingsDB();

		Log.d(LOGCAT, "AllSightings: ArrayList contains: " + sightings.size()
				+ " elements.");
		return sightings;
	}

	@Override
	public List<Sighting> getFilteredSightingList(List<String> filters) {
		openReadableSightingsDB();
		List<Sighting> sightings = new ArrayList<Sighting>();

		// create the select part
		String select = "";
		for (String entry : filters) {
			select += SightingsDBHelper.ANIMAL_NAME + "=\"" + entry + "\" or ";
		}
		
		// cut the last ' or '
		select = select.substring(0, select.length() - 4);
		Log.d(LOGCAT, "Expression for SELECT: " + select);

		// create the hole expression
		String selectQuery = "SELECT * FROM "
				+ SightingsDBHelper.TABLE_SIGHTINGS + " WHERE " + select;
		Cursor cursor = sightingsDatabase.rawQuery(selectQuery, null);

		// read sightings vom courser and store them in a list
		sightings = createSightingList(cursor);

		// close Cursor and Database
		cursor.close();
		closeSightingsDB();

		Log.d(LOGCAT, "AllSightings: ArrayList contains: " + sightings.size()
				+ " elements.");
		return sightings;
	}

	@Override
	public List<Sighting> getSightingsFromNewSightingDB() {
		openReadableNewSightingDB();

		List<Sighting> sightings = new ArrayList<Sighting>();

		// Select ALL query
		String selectQuery = "SELECT * FROM "
				+ NewSightingDBHelper.TABLE_SIGHTINGS;
		Cursor cursor = newSightingDatabase.rawQuery(selectQuery, null);

		// read sightings from courser and store them in a list
		sightings = createSightingList(cursor);

		// close Cursor and Database
		cursor.close();

		closeNewSightingDB();
		
		return sightings;
	}

	@Override
	public void updateSightingDatabase(List<Sighting> sightingList) {

		ContentValues contentValues;
		
		// delete all sightings
		deleteAllSightings();

		for (Sighting sighting : sightingList) {

			contentValues = sightingToContentValue(sighting);
			insertSightingToSightingsDB(contentValues);
		}

	}

	@Override
	public void clearNewSightingsDatabase() {
		openWritableNewSightingDB();

		String selectQuery = "DELETE FROM "
				+ NewSightingDBHelper.TABLE_SIGHTINGS;
		newSightingDatabase.execSQL(selectQuery);

		closeNewSightingDB();
	}

	/**
	 * Delets all entries from Sightings DB.
	 */
	private void deleteAllSightings() {
		openWritableSightingsDB();

		String selectQuery = "DELETE FROM " + SightingsDBHelper.TABLE_SIGHTINGS;
		sightingsDatabase.execSQL(selectQuery);

		closeSightingsDB();
	}

	/**
	 * Adds a new sighting into the Sightings database.
	 * 
	 * @param values
	 *            ContentValues
	 */
	private void insertSightingToSightingsDB(ContentValues values) {
		openWritableSightingsDB();

		// insert the information and get the id in return
		long insertId = sightingsDatabase.insert(
				SightingsDBHelper.TABLE_SIGHTINGS, null, values);

		if (insertId == -1) {
			Log.d(LOGCAT, "Sighting could not be added.");
		} else {
			Log.d(LOGCAT, "new Sighting added. ID: " + insertId);
		}

		closeSightingsDB();
	}

	/**
	 * Iterates over a cursor and stores all entries in a list.
	 * 
	 * @param cursor
	 *            Cursor with sighting entries.
	 * @return a list with sightings
	 */
	private List<Sighting> createSightingList(Cursor cursor) {

		List<Sighting> sightings = new ArrayList<Sighting>();

		// loop over each row and add the data to a new sighting
		if (cursor.moveToFirst()) {
			do {
				// prepare sighting
				Sighting sighting = new Sighting();
				sighting.setSightingId(cursor.getInt(0));
				sighting.setAnimalName(cursor.getString(1));
				sighting.setImageName(cursor.getString(2));
				sighting.setLatitude(cursor.getDouble(3));
				sighting.setLongitude(cursor.getDouble(4));
				sighting.setDistance(-1);
				sighting.setTimestamp(cursor.getString(5));
				sighting.setDescription(cursor.getString(6));
				sighting.setObserverName(cursor.getString(7));

				// add sighting to list
				sightings.add(sighting);
			} while (cursor.moveToNext());
		}

		return sightings;
	}

	/**
	 * Transfers all Information from Sighting into a content Value.
	 * 
	 * @param sighting
	 */
	private ContentValues sightingToContentValue(Sighting sighting) {
		ContentValues cv = new ContentValues();

		cv.put(SightingsDBHelper.ANIMAL_NAME, sighting.getAnimalName());
		cv.put(SightingsDBHelper.IMAGE_PATH, sighting.getImageName());
		cv.put(SightingsDBHelper.GPS_LATITUDE, sighting.getLatitude());
		cv.put(SightingsDBHelper.GPS_LONGITUDE, sighting.getLongitude());
		cv.put(SightingsDBHelper.TIMESTAMP, sighting.getTimestamp());
		cv.put(SightingsDBHelper.DESCRIPTION, sighting.getDescription());
		cv.put(SightingsDBHelper.OBSERVER_NAME, sighting.getObserverName());

		return cv;
	}

	/**
	 * Opens the Sightings database with read access.
	 * 
	 * @throws SQLException
	 */
	private void openReadableSightingsDB() throws SQLException {
		sightingsDatabase = sightingsDBHelper.getReadableDatabase();
		Log.d(LOGCAT, "Sightings DB open to read");
	}

	/**
	 * Opens the Sightings database with write access.
	 * 
	 * @throws SQLException
	 */
	private void openWritableSightingsDB() throws SQLException {
		sightingsDatabase = sightingsDBHelper.getWritableDatabase();
		Log.d(LOGCAT, "Sightings DB open to write");
	}

	/**
	 * Closes the Sightings database.
	 */
	private void closeSightingsDB() {
		sightingsDBHelper.close();
		Log.d(LOGCAT, "Sightings DB closed");
	}

	/**
	 * Opens the NewSighting database with read access.
	 * 
	 * @throws SQLException
	 */
	private void openReadableNewSightingDB() throws SQLException {
		newSightingDatabase = newSightingDBHelper.getReadableDatabase();
		Log.d(LOGCAT, "NewSighting DB open to read");
	}

	/**
	 * Opens the NewSighting database with write access.
	 * 
	 * @throws SQLException
	 */
	private void openWritableNewSightingDB() throws SQLException {
		newSightingDatabase = newSightingDBHelper.getWritableDatabase();
		Log.d(LOGCAT, "NewSighting DB open to write");
	}

	/**
	 * Closes the NewSighting database.
	 */
	private void closeNewSightingDB() {
		newSightingDatabase.close();
		Log.d(LOGCAT, "NewSighting DB closed");
	}
}
