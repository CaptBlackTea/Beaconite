package com.example.deas.beaconite.activities;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.deas.beaconite.R;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

/**
 * Creates a table that displays detected Beacons and information about them.
 * <p/>
 * Created by dea on 28/06/16.
 */
public class BeaconOverviewTable implements Runnable {
	protected static final String TAG = "BeaconOverviewTable";
	private final Collection<Beacon> currentBeacons;

	private Collection<Beacon> previousBeacons;

	private Activity rangingActivity;
	private TableLayout table;
//	private BeaconDataService service;

	public BeaconOverviewTable(final Collection<Beacon> beacons, Collection<Beacon>
			previousBeacons, Activity rangingActivity) {

		this.currentBeacons = beacons;

		this.previousBeacons = previousBeacons;

		this.rangingActivity = rangingActivity;

//		if (service != null) {
//			this.service = (BeaconDataService) service;
//		} else {
//			Log.d(TAG, "SERVICE IS NULL!");
//		}

		this.table = (TableLayout) rangingActivity.findViewById(R.id.table);
	}

	@Override
	public void run() {


		if (!currentBeacons.isEmpty()) {

			//if there is a Beacon that is in previousBeacons and not in currentBeacons: mark the
			// table row; else do nothing
			//TODO: refactor to make it more efficient if possible

			Log.d(TAG, "Current Beacons: " + currentBeacons);
			Log.d(TAG, "Prev Beacons: " + previousBeacons);

			if (previousBeacons != null && !previousBeacons.isEmpty()) {
				for (Beacon b : previousBeacons) {
					Log.d(TAG, "Previous Beacon: " + b.getId2());
					Log.d(TAG, "Is " + b.getId2() + " in current beacons? contains = " + currentBeacons
							.contains(b));

					if (!currentBeacons.contains(b)) {
						Log.d(TAG, "Beacon " + b.getId2() + " was not in current beacons.");
						TableRow markRow = getTableRow(b.getId1().toString());
						colorDisappearedBeaconRow(markRow);
					}
				}
			}

			for (Beacon b : currentBeacons) {

				// check if an element with the UUID as tag of the beacon exists
				String bUuid = b.getId1().toString();
				Log.d(TAG, "Beacon UUID: " + bUuid);

				// if there exists a row with this UUID as tag -> this beacon was seen before!
				// If this beacon was seen before: the data of this row needs to be refreshed and no additional table row should be created.
				TableRow refreshRow = getTableRow(bUuid);

				// clear all child views of this row -> clear old data
				// clear it from color
				clearRowView(refreshRow);

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

				// add the rssi of this beacon to its corresponding Beacon
//				addRssiTimestamp(b);


			}

		}

	}

	/**
	 * Takes a Beacon and adds the current rssi value connected with a timestamp to it.
	 *
	 * @param b the Beacon to which the rssi and timestamp is added to.
	 */
//	private void addRssiTimestamp(Beacon b) {
//		if (service != null) {
//			// if there is no such Beacon create it and add it to the list of known Beacons.
//			if (!service.existsBeacon(b)) {
//				service.addBeaconToList(b);
//			}
//
//			// add the current time and rssi to the given or just created Beacon
//			service.addNewRssiToBeacon(b, System.currentTimeMillis(), b
//					.getRssi());
//		}
//	}

	/**
	 * Takes a TableRow and removes all its child views and clears its from any set background color.
	 *
	 * @param refreshRow the TableRow View to clean up. If this parameter is null nothing happens.
	 */
	private void clearRowView(TableRow refreshRow) {

		if (refreshRow != null) {
			// remove all children of this view
			refreshRow.removeAllViews();

			// clear background color
			refreshRow.setBackgroundColor(0);
		}
	}


	/**
	 * Searches if there exists a table row with the given bUuid as tag attribute.
	 * If there is no such row a row will be created. This row has then the given bUuid as tag.
	 * Otherwise the found row is returned.
	 *
	 * @param bUuid the tag of the table row to search for
	 * @return a TableRow with the given bUuid as tag attribute
	 */
	private TableRow getTableRow(String bUuid) {

		//TODO: check if bUuid is null? Should not happen and it is a private method, but...
		TableRow row = (TableRow) this.table.findViewWithTag(bUuid);

		if (row == null) {
			// No View/TableRow exists with this UUID as id
			// create row
			row = new TableRow(rangingActivity);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(lp);
			row.setTag(bUuid);

			// add to TableLayout
			this.table.addView(row);
		}

		return row;
	}

	private void colorDisappearedBeaconRow(TableRow row) {
		row.setBackgroundColor(Color.RED);
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
