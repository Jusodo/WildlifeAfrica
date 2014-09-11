package uolnmmu.wildlife.presenter.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import uolnmmu.wildlife.R;
import uolnmmu.wildlife.model.ImageHelper;
import uolnmmu.wildlife.model.dataTransferObject.Sighting;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SightingListAdapter extends ArrayAdapter<Sighting> {

	private final static String LOGCAT = SightingListAdapter.class
			.getSimpleName();
	private Context context;

	public SightingListAdapter(Context context, int resource,
			List<Sighting> sightings) {
		super(context, resource, sightings);

		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout sightingListView;

		// get the clicked item
		Sighting sighting = getItem(position);

		if (convertView == null) {
			sightingListView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater layoutInflater;
			layoutInflater = (LayoutInflater) getContext().getSystemService(
					inflater);
			layoutInflater.inflate(R.layout.sighting_list_item,
					sightingListView, true);
		} else {
			sightingListView = (LinearLayout) convertView;
		}

		// find ui elements
		ImageView image = (ImageView) sightingListView
				.findViewById(R.id.listItem_photo);

		TextView animalName = (TextView) sightingListView
				.findViewById(R.id.listItem_name);

		TextView timestamp = (TextView) sightingListView
				.findViewById(R.id.listItem_date);

		TextView distance = (TextView) sightingListView
				.findViewById(R.id.listItem_distance);

		// set content to view
		ImageHelper imageHelper = new ImageHelper(context);
		Bitmap bitmap = imageHelper.getImageFromInternalStorage(sighting
				.getImageName());

		image.setImageBitmap(bitmap);
		animalName.setText(sighting.getAnimalName());
		timestamp.setText(sighting.getTimestamp());
		if (sighting.getDistance() == -1) {
			distance.setText("not available");
		} else {
			distance.setText(String.valueOf(sighting.getDistance()) + " meter");
		}

		return sightingListView;
	}
}
