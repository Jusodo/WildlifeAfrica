package uolnmmu.wildlife.presenter.adapter;

import java.util.ArrayList;

import uolnmmu.wildlife.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FilterListAdapter extends ArrayAdapter<FilterItem> {

	private int resource;

	public FilterListAdapter(Context context, int resource,
			ArrayList<FilterItem> items) {
		super(context, resource, items);

		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout filterView;

		FilterItem item = getItem(position);

		Boolean selected = item.isSelected();
		String name = item.getAnimalName();

		// check if View must be created
		if (convertView == null) {
			filterView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater layoutInflater;
			layoutInflater = (LayoutInflater) getContext().getSystemService(
					inflater);
			layoutInflater.inflate(resource, filterView, true);
		} else {
			filterView = (LinearLayout) convertView;
		}

		CheckBox checkBox = (CheckBox) filterView
				.findViewById(R.id.filter_checkBox);
		TextView itemName = (TextView) filterView
				.findViewById(R.id.filter_name);

		checkBox.setChecked(selected);
		itemName.setText(name);

		return filterView;
	}
}
