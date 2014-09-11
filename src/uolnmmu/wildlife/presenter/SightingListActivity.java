package uolnmmu.wildlife.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uolnmmu.wildlife.R;
import uolnmmu.wildlife.model.dataTransferObject.Sighting;
import uolnmmu.wildlife.model.database.ISightingDatabase;
import uolnmmu.wildlife.model.database.SightingDatabase;
import uolnmmu.wildlife.model.gpsService.LocationService;
import uolnmmu.wildlife.model.syncService.DownloadService;
import uolnmmu.wildlife.model.syncService.UploadService;
import uolnmmu.wildlife.presenter.adapter.SightingListAdapter;
import uolnmmu.wildlife.presenter.comparator.SightingDistanceComparator;
import uolnmmu.wildlife.presenter.comparator.SightingNameComparator;
import uolnmmu.wildlife.presenter.comparator.SightingNameReverseComparator;
import uolnmmu.wildlife.presenter.comparator.SightingTimeComparator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class SightingListActivity extends Activity {

	// constants to identify the returned Intent
	private static final int FILTER_REQUEST = 1;

	private static final String LOGCAT = SightingListActivity.class
			.getSimpleName();

	// UI elements
	private ListView listView;
	protected List<Sighting> sightings;
	private SightingListAdapter listAdapter;
	private Spinner sortSpinner;

	private ISightingDatabase database;
	private Boolean activityResult;
	private Location location;

	private BroadcastReceiver locationReciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			Location newLocation = (Location) intent.getExtras()
					.get("location");
			Toast.makeText(
					getApplicationContext(),
					newLocation.getLatitude() + ", "
							+ newLocation.getLongitude(), Toast.LENGTH_SHORT)
					.show();

			setLocation(newLocation);

			calcDistanceForSightings();
		}
	};

	private BroadcastReceiver downloadReciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			SightingListActivity.this.sightings = (ArrayList<Sighting>) SightingListActivity.this.database
							.getSightingList();
			
			SightingListActivity.this.listAdapter.clear();
			SightingListActivity.this.listAdapter.addAll(sightings);
			SightingListActivity.this.listAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sighting_list);

		this.activityResult = false;

		// Set up Action bar. Disable title.
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);

		// get the Spinner
		sortSpinner = (Spinner) findViewById(R.id.sightingList_spinner);
		// create an ArrayAdapter, with default layout and a String-Array
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(this, R.array.filterOptions,
						R.layout.simple_spinner_list);
		// set the layout to use when the list appears
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// connect Adapter to the Spinner
		sortSpinner.setAdapter(spinnerAdapter);
		sortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				switch (position) {
				case 0:
					sortSightingsByTime();
					listAdapter.notifyDataSetChanged();
					break;
				case 1:
					sortSightingsAToZ();
					listAdapter.notifyDataSetChanged();
					break;
				case 2:
					sortSightingsZToA();
					listAdapter.notifyDataSetChanged();
					break;
				case 3:
					sortSightingsByDistance();
					listAdapter.notifyDataSetChanged();
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// get Sightings from database
		database = new SightingDatabase(this);
		sightings = database.getSightingList();
		
//		ArrayList<Sighting> sightingList = new ArrayList<Sighting>();
//		for (String animal : getResources().getStringArray(R.array.animalOverview_array)) {
//			Sighting s = new Sighting();
//			s.setAnimalName(animal);
//			sightingList.add(s);
//		}

		listView = (ListView) findViewById(R.id.sightingList_view);

		// create an ArrayAdapter to fill the ListView
		listAdapter = new SightingListAdapter(this,
				R.layout.sighting_list_item, sightings);

		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				Intent intent = new Intent(getApplicationContext(),
						SightingDetailsActivity.class);

				// get the sighting id and store it in the intent
				Sighting sighting = (Sighting) listView
						.getItemAtPosition(position);
				Log.d(LOGCAT, "SightingId" + sighting.getSightingId());
				int sightingId = sighting.getSightingId();
				intent.putExtra("sightingId", sightingId);

				startActivity(intent);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_sighting_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_refresh:
			// get the Connection Manager
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

			// check if internet connection is available
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				// start Upload Service
				Intent ulService = new Intent(this, UploadService.class);
				startService(ulService);
			} else {
				Toast.makeText(this, "No network connection available.",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.action_mapView:
			intent = new Intent(this, SightingMapActivity.class);
			this.startActivity(intent);
			break;
		case R.id.action_filter:
			intent = new Intent(this, SightingFilterActivity.class);
			this.startActivityForResult(intent, FILTER_REQUEST);
			break;
		case R.id.action_addSighting:
			intent = new Intent(this, AddSightingActivity.class);
			this.startActivity(intent);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == FILTER_REQUEST) {
				// get the ArrayList from the Intent
				Bundle extra = data.getExtras();
				@SuppressWarnings("unchecked")
				ArrayList<String> filterList = (ArrayList<String>) extra
						.get("list");

				// check the size of the ArrayList
				if (!filterList.isEmpty()) {
					// get Sightings from database
					database = new SightingDatabase(this);
					this.sightings.clear();
					this.sightings.addAll(database
							.getFilteredSightingList(filterList));
				}

				this.activityResult = true;
			}
		}

		if (requestCode == Activity.RESULT_CANCELED) {
			if (requestCode == FILTER_REQUEST) {
				this.activityResult = false;
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// start LocationService
		Intent service = new Intent(this, LocationService.class);
		startService(service);

		// register locationReciver
		IntentFilter locationFilter = new IntentFilter();
		locationFilter.addAction(LocationService.BROADCAST_ACTION);
		registerReceiver(locationReciver, locationFilter);

		// register updateReciver
		IntentFilter updateFilter = new IntentFilter();
		updateFilter.addAction(DownloadService.BROADCAST_ACTION);
		registerReceiver(downloadReciver, updateFilter);

		if (this.activityResult == false) {
			database = new SightingDatabase(this);
			this.sightings.clear();
			this.sightings.addAll(database.getSightingList());
		}

		sortSightingsByTime();
		listAdapter.notifyDataSetChanged();

		this.activityResult = false;
	}

	@Override
	public void onPause() {
		super.onPause();

		unregisterReceiver(locationReciver);
		unregisterReceiver(downloadReciver);
	}

	/**
	 * Setter for location information.
	 * 
	 * @param newLocation
	 *            location
	 */
	protected void setLocation(Location newLocation) {

		this.location = newLocation;
	}

	/**
	 * Sorts the sighting list by time
	 */
	private void sortSightingsByTime() {
		SightingTimeComparator comparator = new SightingTimeComparator();
		Collections.sort(this.sightings, comparator);
	}

	/**
	 * Sorts the sighting list by name from A to Z
	 */
	private void sortSightingsAToZ() {
		SightingNameComparator comparator = new SightingNameComparator();
		Collections.sort(this.sightings, comparator);
	}

	/**
	 * Sorts the sighting list by name from Z to A
	 */
	private void sortSightingsZToA() {
		SightingNameReverseComparator comparator = new SightingNameReverseComparator();
		Collections.sort(this.sightings, comparator);
	}

	/**
	 * Sorts the sighting list by distance
	 */
	private void sortSightingsByDistance() {
		SightingDistanceComparator comparator = new SightingDistanceComparator();
		Collections.sort(this.sightings, comparator);
	}

	/**
	 * Calculates the differenz between two points. One point is the user
	 * location, the other point is stored in the sightings.
	 */
	private void calcDistanceForSightings() {
		ArrayList<Sighting> list = new ArrayList<Sighting>();
		if (location != null) {
			for (Sighting sighting : this.sightings) {

				double sLat = sighting.getLatitude();
				double sLong = sighting.getLongitude();

				Location sightingLocation = new Location((String) null);
				sightingLocation.setLatitude(sLat);
				sightingLocation.setLongitude(sLong);

				float distance = this.location.distanceTo(sightingLocation);

				sighting.setDistance(distance);
				list.add(sighting);

				Log.d(LOGCAT, sighting.getAnimalName() + " with distance "
						+ distance);
			}

			this.listAdapter.clear();
			this.listAdapter.addAll(list);
			this.listAdapter.notifyDataSetChanged();
		}

	}
}