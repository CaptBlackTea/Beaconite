package com.example.deas.beaconite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by deas on 05/09/16.
 */
public class AdapterCache extends ArrayAdapter<Cache> {


	private static LayoutInflater inflater = null;
	private Context activity;
	private List<Cache> cacheList;

	public AdapterCache(Context context, int resource, List<Cache> objects) {
		super(context, resource, objects);

		this.activity = context;
		this.cacheList = objects;

		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return cacheList.size();
	}

	public Cache getItem(int position) {
		return cacheList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = inflater.inflate(R.layout.cachelist_item, null);
		TextView cacheName = (TextView) itemView.findViewById(R.id.listItemCacheName);
		TextView numberOfRecordings = (TextView) itemView.findViewById(R.id
				.listItemNumberOfRecords);

		cacheName.setText(cacheList.get(position).getCacheName());
		numberOfRecordings
				.setText(String.valueOf(cacheList.get(position).getTimeIntervals().size()));

		return itemView;
	}
}


