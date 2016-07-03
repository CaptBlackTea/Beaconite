package com.example.deas.beaconite;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

/**
 * Created by dea on 28/06/16.
 */
public class BeaconOverviewTable implements Runnable {
	protected static final String TAG = "BeaconOverviewTable";
	private final Collection<Beacon> beacons;
	private Activity rangingActivity;
	private TableLayout table;

	public BeaconOverviewTable(final Collection<Beacon> beacons, Activity rangingActivity) {
		this.beacons = beacons;
		this.rangingActivity = rangingActivity;
		this.table = (TableLayout) rangingActivity.findViewById(R.id.table);
	}

	@Override
	public void run() {
		if (!beacons.isEmpty()) {
			int index = 0;

			for (Beacon b : beacons) {
//				index++;
//				logToDisplay("The " + index + ". beacon " + b.toString() + " is about " + b.getDistance() + " meters away.");

				// check if an element with the UUID as tag of the beacon exists
				String bUuid = b.getId1().toString();
				Log.d(TAG, "Beacon UUID: " + bUuid);

				// there exists a row with this UUID as tag -> this beacon was seen before!
				// clear all child views of this row -> clear old data
				TableRow refreshRow = getTableRow(bUuid);

				// create a TextView for each Beacon information: UUID, RSSI, Major, Minor, Accuracy, Distance
				TextView uuid = new TextView(rangingActivity);
				TextView rssi = new TextView(rangingActivity);
				TextView major = new TextView(rangingActivity);
				TextView minor = new TextView(rangingActivity);
				TextView accuracy = new TextView(rangingActivity);
				TextView distance = new TextView(rangingActivity);

				uuid.setEms(5);
				uuid.setText(b.getId1().toString());

				rssi.setText("" + b.getRssi());
				major.setText(b.getId2().toString());
				minor.setText(b.getId3().toString());

				accuracy.setText(calculateAccuracy(b.getDistance()));
				accuracy.setEms(5);

				distance.setText("" + b.getDistance());

				refreshRow.addView(uuid);
				refreshRow.addView(rssi);
				refreshRow.addView(major);
				refreshRow.addView(minor);
				refreshRow.addView(accuracy);
				refreshRow.addView(distance);

			}
			// code from the tutorial, but can only display the first beacon, therefore commented out
//					Beacon firstBeacon = beacons.iterator().next();
//					logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
		}

	}

	private TableRow getTableRow(String bUuid){
		TableRow row = (TableRow) table.findViewWithTag(bUuid);

		if (row == null) {
			// No View/TableRow exists with this UUID as id
			// create row
			row = new TableRow(rangingActivity);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(lp);
			row.setTag(bUuid);

			// add to TableLayout
			table.addView(row);
		} else {
			row.removeAllViews();
		}

		return row;
	}

	private String calculateAccuracy(double distance) {
		if (distance == -1.0) {
			return "Unknown";
		} else if (distance < 1) {
			return "Immediate, under 1 meter";
		} else if (distance < 3) {
			return "Near, under 3 meter";
		} else {
			return "Far";
		}
	}
}
