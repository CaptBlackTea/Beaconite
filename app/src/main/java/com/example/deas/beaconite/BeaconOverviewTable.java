package com.example.deas.beaconite;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

	public BeaconOverviewTable(final Collection<Beacon> beacons, Activity rangingActivity) {
		this.currentBeacons = beacons;

		// when the table is first created there is no previous list of detected beacons
		// therefore it is set to the same value as the variable currentBeacons
		this.previousBeacons = beacons;

		this.rangingActivity = rangingActivity;
		this.table = (TableLayout) rangingActivity.findViewById(R.id.table);
	}

	@Override
	public void run() {


		if (!currentBeacons.isEmpty()) {

			//if there is a Beacon that is in previousBeacons and not in currentBeacons: mark the
			// table row; else do nothing
			//TODO: refactor to make it more efficient if possible

			Collection<Beacon> tempBeacons = previousBeacons;
//			tempBeacons.removeAll(currentBeacons);
			Log.d(TAG, "Current Beacons: " + currentBeacons + " ; Temp Beacons: " + tempBeacons);

			for (Beacon b : previousBeacons) {
				for (Beacon cb : currentBeacons) {
//					tempBeacons.removeIf(b.equals(cb));

				}
			}


//			for(Beacon b : tempBeacons) {
//				TableRow markRow = getTableRow(b.getId1().toString());
//				colorDisappearedBeaconRow(markRow);
//			}

//			for (Beacon pb : previousBeacons) {

//				for(Beacon cb : currentBeacons){
//					Log.d(TAG, "Comparing previous Beacon: " + pb.getId2() + " with current " +
//								"Beacon: " + cb.getId2() + " ; equals: " + pb.equals(cb));
//
//					if (!pb.equals(cb)){
//
//					TableRow markRow = getTableRow(pb.getId1().toString());
//					colorDisappearedBeaconRow(markRow);
//
//					}
//				}


//				TableRow markRow = getTableRow(pb.getId1().toString());
//				colorDisappearedBeaconRow(markRow);


//			}

			// after checking which beacons are currently seen compared to the last list that was
			// received the current list becomes the new previous list.
			previousBeacons = currentBeacons;


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

				writeBeaconInFile(b);

			}
			// code from the tutorial, but can only display the first beacon, therefore commented out
//					Beacon firstBeacon = currentBeacons.iterator().next();
//					logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
		}

	}

	/**
	 * Takes a TableRow and removes all its child views and clears its from any set background color.
	 *
	 * @param refreshRow the TableRow View to clean up. If this parameter is null nothing happens.
	 */
	private void clearRowView(TableRow refreshRow) {

		if (refreshRow != null) {
			// remove all children of this view
			refreshRow.removeAllViews();

			// TODO: clear background color
//			refreshRow.setBackgroundColor(0);
		}
	}
	private void writeBeaconInFile(Beacon b) {
		// TODO: implement this some day...maybe...ideas change
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
