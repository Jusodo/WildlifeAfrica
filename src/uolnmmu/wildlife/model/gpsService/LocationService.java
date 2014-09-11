package uolnmmu.wildlife.model.gpsService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service implements LocationListener {

	public static final String BROADCAST_ACTION = "LocationService";
	private static final String LOGCAT = LocationService.class.getSimpleName();

	private String serviceName = Context.LOCATION_SERVICE;
	private LocationManager locationManager;
	private Intent intent;

	@Override
	public IBinder onBind(Intent arg0) {
		// nothing to do here
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOGCAT, "Location Service created");

		intent = new Intent(BROADCAST_ACTION);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(LOGCAT, "Location Service started");

		// get the LocationManager
		locationManager = (LocationManager) getSystemService(serviceName);

		// define some criteria
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		// create String with the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		locationManager.requestSingleUpdate(provider, this, null);

		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 1000, 50, this);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(LOGCAT, "Location Service done");
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		locationManager.removeUpdates(this);

		Log.v(LOGCAT, "New location found. Will send Broadcast");
		Log.v(LOGCAT, location.getLatitude() + ", " + location.getLongitude());
		intent.putExtra("location", location);
		intent.setAction(BROADCAST_ACTION);
		sendBroadcast(intent);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps is Disabled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(), "Gps is Enabled",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// nothing to do here
		Log.d(LOGCAT, "Status changed. New provider: " + provider
				+ ", neu Status: " + status);
	}

}
