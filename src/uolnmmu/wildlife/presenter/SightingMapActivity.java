package uolnmmu.wildlife.presenter;

import java.util.ArrayList;
import java.util.HashMap;

import uolnmmu.wildlife.R;
import uolnmmu.wildlife.model.dataTransferObject.Sighting;
import uolnmmu.wildlife.model.database.ISightingDatabase;
import uolnmmu.wildlife.model.database.SightingDatabase;
import uolnmmu.wildlife.model.gpsService.LocationService;
import uolnmmu.wildlife.model.syncService.DownloadService;
import uolnmmu.wildlife.model.syncService.UploadService;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SightingMapActivity extends Activity {

	// constants to identify the returned Intent
	private static final int FILTER_REQUEST = 1;

	// UI elements
	private GoogleMap googleMap;
	private Marker marker;

	private ArrayList<Sighting> sightingsList;
	private HashMap<Marker, Sighting> sightingMarkerMap;
	private ISightingDatabase database;
	private Boolean activityResult;

	private BroadcastReceiver downloadReciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			SightingMapActivity.this.sightingsList = (ArrayList<Sighting>) SightingMapActivity.this.database
					.getSightingList();
			SightingMapActivity.this.setMarkerOnMap();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sighting_map);

		this.activityResult = false;

		try {
			// try loading the map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// get the ActionBar and deactivate the title
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);

		// get sightings from database
		database = new SightingDatabase(this);
		sightingsList = (ArrayList<Sighting>) database.getSightingList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_sighting_map, menu);
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
		case R.id.action_listView:
			intent = new Intent(this, SightingListActivity.class);
			this.startActivity(intent);
			finish();
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
					sightingsList.clear();
					sightingsList.addAll(database
							.getFilteredSightingList(filterList));
				}

				setMarkerOnMap();

				activityResult = true;
			}
		}

		if (requestCode == Activity.RESULT_CANCELED) {
			if (requestCode == FILTER_REQUEST) {
				activityResult = false;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();

		// start LocationService
		Intent service = new Intent(this, LocationService.class);
		startService(service);

		// register updateReciver
		IntentFilter updateFilter = new IntentFilter();
		updateFilter.addAction(DownloadService.BROADCAST_ACTION);
		registerReceiver(downloadReciver, updateFilter);

		if (!activityResult) {
			database = new SightingDatabase(this);
			sightingsList.clear();
			sightingsList.addAll(database.getSightingList());
		}

		if (googleMap != null) {
			// set Marker on the map
			setMarkerOnMap();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		// unregisterReceiver(locationReciver);
		unregisterReceiver(downloadReciver);
	}

	/**
	 * Function to load map. If map is not created it will created.
	 * */
	private void initilizeMap() {

		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map_view)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Unable to create maps", Toast.LENGTH_SHORT).show();
			} else {
				// set start position: Kragga Kamma Game Park
				LatLng target = new LatLng(-33.985860, 25.458548);

				// zoom level between 2.0 and 21.0 are recommended
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						target, 15));

				// enable my location
				googleMap.setMyLocationEnabled(true);

				// set OnInfoWindowClickListener
				googleMap
						.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

							@Override
							public void onInfoWindowClick(Marker arg0) {

								Log.v("MapActivity", "click on InfoWindow");
								// get sighting from HashMap
								Sighting sighting = sightingMarkerMap
										.get(marker);
								Log.v("Map",
										"Sighting: " + sighting.getAnimalName());

								// create new Intent
								Intent intent = new Intent(
										getApplicationContext(),
										SightingDetailsActivity.class);

								// get the sighting id and store it in the
								// intent
								int sightingId = sighting.getSightingId();
								intent.putExtra("sightingId", sightingId);

								startActivity(intent);
							}
						});
			}
		}

	}

	/**
	 * Sets for each sighting a Marker on the map.
	 */
	private void setMarkerOnMap() {

		// remove all Marker from Map
		googleMap.clear();

		// create a new HashMap to map Marker an Sighting.
		sightingMarkerMap = new HashMap<Marker, Sighting>();

		for (Sighting sighting : sightingsList) {

			// add a Maker with following properties: position, title, snippet
			marker = googleMap.addMarker(new MarkerOptions()
					.position(
							new LatLng(sighting.getLatitude(), sighting
									.getLongitude()))
					.title(sighting.getAnimalName())
					.snippet(sighting.getTimestamp()));

			sightingMarkerMap.put(marker, sighting);
			Log.v("SightingMap", "Sighting added: " + sighting.getAnimalName());
		}
	}

}
