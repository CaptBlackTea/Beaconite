package com.example.deas.beaconite;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RangingActivity extends AppCompatActivity implements BeaconConsumer {
	protected static final String TAG = "RangingActivity";
	private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

	Logger log = LoggerFactory.getLogger(RangingActivity.class);
	private Collection<Beacon> previousBeacons = null;

	// Collects all Beacons that were seen since Activity start
	// idea is that it only holds MyBeacons, as they can store Rssi connected with timestamp
	private List<Beacon> allMyBeacons;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_ranging);
		setContentView(R.layout.activity_ranging_table);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

		beaconManager.bind(this);

		allMyBeacons = new ArrayList<Beacon>();

		Log.i(TAG, "********* Ranging!");
		log.info("*** Started Ranging App");
		log.info("Does it reload now?!");


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		beaconManager.unbind(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
	}

	@Override
	public void onBeaconServiceConnect() {
		final Activity rangingActivityContext = this;

		beaconManager.addRangeNotifier(new RangeNotifier() {
			@Override
			public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region
					region) {
				runOnUiThread(new BeaconOverviewTable(beacons, previousBeacons,
						rangingActivityContext));
				previousBeacons = beacons;
				Log.d(TAG, "AllMyBeacons " + allMyBeacons);
				printAllBeaconsWithRssiOverTime();
			}
		});

		try {
			beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
		} catch (RemoteException e) {
			Log.i(TAG, "------ Exception!" + e);
		}
	}

	/**
	 * Traverses all currently known Beacons and prints all their timestamp-rssi data to the
	 * console.
	 */
	private void printAllBeaconsWithRssiOverTime() {
		if (!allMyBeacons.isEmpty()) {
			for (Beacon b : allMyBeacons) {
				MyBeacon mb = (MyBeacon) b;
				Log.d(TAG, "Beacon " + mb.getId2() + ": " + mb.getAllTimestamps().toString());
			}
		}
	}

	private void logToDisplay(final String line) {
		runOnUiThread(new Runnable() {
			public void run() {
				EditText editText = (EditText) RangingActivity.this.findViewById(R.id.rangingText);

				editText.append(line + "\n");

			}
		});
	}

	/**
	 * Adds a given Beacon to the list of all Beacons that were seen since Activity start. Wraps the
	 * given Beacon in a MyBeacon and then adds it to the list.
	 *
	 * @param beacon the Beacon to wrap in a MyBeacon and add to the list of all Beacons.
	 * @return true: the given Beacon was successfully added to the list. false: the given Beacon is
	 * already in the list and therefore was not added and the list did not change.
	 */
	public boolean addBeaconToList(Beacon beacon) {
		if (!allMyBeacons.contains(beacon)) {
			return allMyBeacons.add(new MyBeacon(beacon));
		}

		return false;
	}

	public boolean existsBeacon(Beacon beacon) {
		return allMyBeacons.contains(beacon);
	}

	/**
	 * Takes a Beacon and adds given Rssi value and Timestamp to this Beacon.
	 *
	 * @param b         the Beacon to add the values to
	 * @param timestamp the timestamp when the given rssi occurred
	 * @param rssi      the Rssi value the given Beacon hat at the given timestamp
	 * @return true if the timestamp and rssi value were successfully added to the Beacon false if
	 * it was not possible. Can also mean that this Beacon is unknown and not listed. Test if the
	 * Beacon exists and add it if it is unknown (does not exist).
	 */
	public boolean addNewRssiToBeacon(Beacon b, Long timestamp, int rssi) {
		if (allMyBeacons.contains(b)) {
			int index = allMyBeacons.indexOf(b);
			MyBeacon updateBeacon = (MyBeacon) allMyBeacons.get(index);
			updateBeacon.addRssiAndTime(timestamp, rssi);
			return true;
		}

		return false;
	}

//	private void logToDisplay(final String line) {
//		runOnUiThread(new Runnable() {
//			public void run() {
//				TextView textView = (TextView) RangingActivity.this.findViewById(R.id.rangingText);
//
//				textView.append(line + "\n");
//
//			}
//		});
//	}


}
