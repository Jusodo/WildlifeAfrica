package uolnmmu.wildlife.presenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uolnmmu.wildlife.R;
import uolnmmu.wildlife.model.ImageHelper;
import uolnmmu.wildlife.model.dataTransferObject.Sighting;
import uolnmmu.wildlife.model.database.ISightingDatabase;
import uolnmmu.wildlife.model.database.SightingDatabase;
import uolnmmu.wildlife.model.gpsService.LocationService;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddSightingActivity extends Activity {

	// logger
	private final static String LOGCAT = AddSightingActivity.class
			.getSimpleName();

	// constants to identify the returned Intent
	private static final int CAMERA_REQUEST = 1;
	private static final int GALLERY_REQUEST = 2;

	// UI elements
	private Spinner animalSpinner;
	private ArrayAdapter<String> spinnerAdapter;
	private ImageView sightingImage;
	private ImageButton cameraBtn, galleryBtn;
	private TextView descriptionView, observerView;

	// Fields
	private String timestamp, filename;
	private Uri imageUri;
	private SimpleDateFormat sdf;
	private Location location;

	private BroadcastReceiver locationReciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			Location newLocation = (Location) intent.getExtras()
					.get("location");

			setLocation(newLocation);

			Toast.makeText(
					getApplicationContext(),
					newLocation.getLatitude() + ", "
							+ newLocation.getLongitude(), Toast.LENGTH_SHORT)
					.show();

			AddSightingActivity.this.location = newLocation;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_sighting);

		// Set up Action Bar. Disbale title and enable icon to navigate up.
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// get the spinner and initialze it
		animalSpinner = (Spinner) findViewById(R.id.sighting_spinner);

		// Create String array to fill the spinner
		String[] animalEntries = getResources().getStringArray(
				R.array.animalOverview_array);

		// Create ArrayAdapter to fill the spinner
		spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, animalEntries);

		animalSpinner.setAdapter(spinnerAdapter);

		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY);
		timestamp = sdf.format(new Date());

		sightingImage = (ImageView) findViewById(R.id.sighting_image);

		cameraBtn = (ImageButton) findViewById(R.id.sighting_cameraBtn);
		cameraBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				filename = "IMG_" + timestamp + ".jpg";
				File file = new File(
						getExternalFilesDir(Environment.DIRECTORY_PICTURES),
						"image.jpg");
				Log.d(LOGCAT, "absoulut path: " + file.getAbsolutePath());

				imageUri = Uri.fromFile(file);

				Intent takePictureIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				// put Extras into the intent and launch the camera
				takePictureIntent.putExtra("crop", "true");
				takePictureIntent.putExtra("outputX", 450);
				takePictureIntent.putExtra("outputY", 450);
				takePictureIntent.putExtra("aspectX", 1);
				takePictureIntent.putExtra("aspectY", 1);
				takePictureIntent.putExtra("scale", true);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

				startActivityForResult(takePictureIntent, CAMERA_REQUEST);

			}
		});

		galleryBtn = (ImageButton) findViewById(R.id.sighting_galleryBtn);
		galleryBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				filename = "IMG_" + timestamp + ".jpg";
				File file = new File(
						getExternalFilesDir(Environment.DIRECTORY_PICTURES),
						"image.jpg");
				Log.d(LOGCAT, "absoulut path: " + file.getAbsolutePath());

				imageUri = Uri.fromFile(file);

				Intent photoPickerIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				// put Extras into the intent and launch the camera
				photoPickerIntent.setType("image/*");
				photoPickerIntent.putExtra("crop", "true");
				photoPickerIntent.putExtra("outputX", 500);
				photoPickerIntent.putExtra("outputY", 500);
				photoPickerIntent.putExtra("aspectX", 1);
				photoPickerIntent.putExtra("aspectY", 1);
				photoPickerIntent.putExtra("scale", true);
				photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

				startActivityForResult(Intent.createChooser(photoPickerIntent,
						"Select Picture"), GALLERY_REQUEST);
			}
		});

		descriptionView = (TextView) findViewById(R.id.sighting_description);
		observerView = (TextView) findViewById(R.id.sighting_observer);

	}

	protected void setLocation(Location newLocation) {
		this.location = newLocation;
		Log.v(LOGCAT,
				this.location.getLatitude() + ", "
						+ this.location.getLongitude());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_add_sighting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			break;
		case R.id.action_save:
			saveSighting();
			break;
		case R.id.action_cancel:
			super.onBackPressed();
			break;
		default:
			break;
		}
		return true;
	}

	// called when returning from Camera App or Gallery App
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap bitmap;
		ImageHelper imageHelper = new ImageHelper(getApplicationContext());

		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == CAMERA_REQUEST) {

				Log.d(LOGCAT, "Uri in Activity Result: " + imageUri);

				bitmap = BitmapFactory.decodeFile(imageUri.getPath());
				sightingImage.setImageBitmap(bitmap);

				if (bitmap != null) {
					// save the cropped image
					imageHelper.saveToInternalStorage(bitmap, filename);
				} else {
					Log.d(LOGCAT, "Cannot access the image.");
				}
			}

			if (requestCode == GALLERY_REQUEST) {

				bitmap = BitmapFactory.decodeFile(imageUri.getPath());
				sightingImage.setImageBitmap(bitmap);

				if (bitmap != null) {
					// save the cropped image
					imageHelper.saveToInternalStorage(bitmap, filename);
				} else {
					Log.d(LOGCAT, "Cannot access the image.");
				}
			}
		}

		if (resultCode == Activity.RESULT_CANCELED) {
			imageUri = null;
		}
	}

	/**
	 * Collects all needed information, saves a sighting to the database and
	 * navigates back to the previous screen
	 */
	private void saveSighting() {

		Log.d(LOGCAT, "click on saveSighting");

		Sighting sighting = new Sighting();

		sighting.setAnimalName(animalSpinner.getSelectedItem().toString());
		if (filename != null) {
			sighting.setImageName(filename);
			Log.v(LOGCAT, filename);
		}
		if (location != null) {
			sighting.setLatitude(location.getLatitude());
			sighting.setLongitude(location.getLongitude());
			Log.v(LOGCAT,
					location.getLatitude() + ", " + location.getLongitude());
		} else {
			Toast.makeText(getApplicationContext(),
					"No location available. Saving canceled",
					Toast.LENGTH_SHORT).show();
			 return;
		}
		sighting.setTimestamp(timestamp);
		sighting.setDescription(descriptionView.getText().toString());
		sighting.setObserverName(observerView.getText().toString());

		Log.d(LOGCAT, "new sighting: " + sighting.getAnimalName());

		// Save to database
		ISightingDatabase database = new SightingDatabase(this);
		database.addSighting(sighting);

		Toast.makeText(getApplicationContext(), "Saved new Sighting",
				Toast.LENGTH_SHORT).show();

		super.onBackPressed();
	}

	@Override
	public void onResume() {
		super.onResume();

		// start LocationService
		Intent service = new Intent(this, LocationService.class);
		startService(service);

		// register reciver
		IntentFilter filter = new IntentFilter();
		filter.addAction(LocationService.BROADCAST_ACTION);
		registerReceiver(locationReciver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();

		unregisterReceiver(locationReciver);
	}
}
