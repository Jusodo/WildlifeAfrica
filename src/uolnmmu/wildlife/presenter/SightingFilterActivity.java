package uolnmmu.wildlife.presenter;

import java.util.ArrayList;

import uolnmmu.wildlife.R;
import uolnmmu.wildlife.presenter.adapter.FilterItem;
import uolnmmu.wildlife.presenter.adapter.FilterListAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SightingFilterActivity extends Activity {

	private static final String LOGCAT = SightingFilterActivity.class
			.getSimpleName();

	private ArrayList<FilterItem> items;
	private ListView checkBoxListView;
	private FilterListAdapter listAdapter;
	private Button selectAllBtn, deselectAllBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sighting_filter);

		// set up the Action Bar. Disable title and enable icon to navigate up.
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		checkBoxListView = (ListView) findViewById(R.id.filter_checkBoxList);

		String[] animalNames = getResources().getStringArray(
				R.array.animalOverview_array);

		items = new ArrayList<FilterItem>();
		for (String name : animalNames) {
			FilterItem item = new FilterItem(name, false);
			items.add(item);
		}

		listAdapter = new FilterListAdapter(this,
				R.layout.sighting_filter_item, items);
		checkBoxListView.setAdapter(listAdapter);

		// Listener for the Checkbox view
		checkBoxListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FilterItem item = (FilterItem) parent
						.getItemAtPosition(position);
				if (item.isSelected()) {
					item.setSelected(false);
				} else {
					item.setSelected(true);
				}
				listAdapter.notifyDataSetChanged();
				Toast.makeText(
						getApplicationContext(),
						"You choose: " + item.getAnimalName()
								+ "and this is selected: " + item.isSelected(),
						Toast.LENGTH_SHORT).show();
			}
		});

		// get the select all Button and handle click
		selectAllBtn = (Button) findViewById(R.id.filter_selectAllBtn);
		selectAllBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (FilterItem item : items) {
					item.setSelected(true);
				}
				listAdapter.notifyDataSetChanged();
			}
		});

		deselectAllBtn = (Button) findViewById(R.id.filter_deselectAlllBtn);
		deselectAllBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (FilterItem item : items) {
					item.setSelected(false);
				}
				listAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_sighting_filter, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case android.R.id.home:
			cancelFilterOptions();
			break;
		case R.id.action_cancel:
			cancelFilterOptions();
				break;
		case R.id.action_accept:
			acceptFilterOptions();
			break;
		case R.id.action_addSighting:
			// start new activity
			intent = new Intent(this, AddSightingActivity.class);
			this.startActivity(intent);
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * Collects the checked names and finishes this Activity with a positive.
	 */
	private void acceptFilterOptions() {
		Log.d(LOGCAT, "Clicked on Accept");
		Intent intent = new Intent();
		Bundle extras = new Bundle();

		// put ArrayList with checked names into the bundle
		ArrayList<String> list = new ArrayList<String>();
		for (FilterItem item : items) {
			if (item.isSelected()) {
				Log.d(LOGCAT, item.getAnimalName());
				list.add(item.getAnimalName());
			}
		}

		extras.putSerializable("list", list);

		intent.putExtras(extras);
		setResult(Activity.RESULT_OK, intent);
		this.finish();
	}

	/**
	 * Finishes this Activity with a negative result.
	 */
	private void cancelFilterOptions() {
		Log.d(LOGCAT, "Clicked on Canceled");
		setResult(Activity.RESULT_CANCELED);
		this.finish();
	}
}
