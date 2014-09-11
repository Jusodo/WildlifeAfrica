package uolnmmu.wildlife.presenter;

import java.io.File;

import uolnmmu.wildlife.R;
import uolnmmu.wildlife.model.ImageHelper;
import uolnmmu.wildlife.model.contentProvider.ImageProvider;
import uolnmmu.wildlife.model.dataTransferObject.Sighting;
import uolnmmu.wildlife.model.database.ISightingDatabase;
import uolnmmu.wildlife.model.database.SightingDatabase;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class SightingDetailsActivity extends Activity {

	// UI elements
	private ImageView sightingImage;
	private TextView nameView, timestampView, descriptionView, observerView;

	private String imageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sighting_details);

		// Set up Action Bar. Disbale title and enable icon to navigate up.
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Get UI elements
		sightingImage = (ImageView) findViewById(R.id.details_image);
		nameView = (TextView) findViewById(R.id.details_name);
		timestampView = (TextView) findViewById(R.id.details_timestamp);
		descriptionView = (TextView) findViewById(R.id.details_description);
		observerView = (TextView) findViewById(R.id.details_observer);

		// Get the intent and grab the sighting object
		Bundle extra = getIntent().getExtras();
		int sightingId = extra.getInt("sightingId");

		// Get sighting from Database
		ISightingDatabase database = new SightingDatabase(
				getApplicationContext());
		Sighting sighting = database.getSighting(sightingId);


		// set information to the view
		ImageHelper imageHelper = new ImageHelper(getApplicationContext());
		Bitmap bitmap = imageHelper.getImageFromInternalStorage(sighting
				.getImageName());
		sightingImage.setImageBitmap(bitmap);
		nameView.setText(sighting.getAnimalName());
		timestampView.setText(sighting.getTimestamp());
		descriptionView.setText(sighting.getDescription());
		observerView.setText(sighting.getObserverName());

		imageName = sighting.getImageName();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_sighting_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			break;
		case R.id.action_share:
			startSharing();
			break;
		case R.id.action_addSighting:
			Intent intent = new Intent(this, AddSightingActivity.class);
			this.startActivity(intent);
			break;
		default:
			break;
		}

		return true;
	}

	/**
	 * Shares the sighting information via available services.
	 */
	private void startSharing() {

		// create Message - the animal's name
		String message = nameView.getText().toString();

		Uri uri = Uri.parse("content://" + ImageProvider.AUTHORITY + "/"
				+ imageName);
		Log.d("Provider Uri: ", uri.toString());

		// create new Intent
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("*/*");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);

		// start Activity
		startActivity(Intent
				.createChooser(sharingIntent, "Share this Sighting"));
	}
}
