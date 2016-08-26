package com.example.deas.beaconite;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;


/**
 * A BoundService to store all known Beacons and data connected to them. Currently: Beacons and
 * their Rssi values over time
 * <p/>
 * Created by deas on 26/08/16.
 */
public class BeaconDataService extends Service {
	protected static final String TAG = "BeaconDataService";

	// Collects all Beacons that were seen since Activity start
	// idea is that it only holds MyBeacons, as they can store Rssi connected with timestamp
	private List<Beacon> allMyBeacons;


	// Binder given to clients
	private final IBinder mBinder = new BeaconDataBinder();

	/**
	 * Class used for the client Binder.  Because we know this service always runs in the same
	 * process as its clients, we don't need to deal with IPC.
	 */
	public class BeaconDataBinder extends Binder {
		BeaconDataService getService() {
			// Return this instance of BeaconDataService so clients can call public methods
			return BeaconDataService.this;
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}


	@Override
	public void onCreate() {
		// Tell the user we started.
		Toast.makeText(this, R.string.beacon_data_service_started, Toast.LENGTH_SHORT).show();

		allMyBeacons = new ArrayList<Beacon>();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);

//		Toast.makeText(this, R.string.beacon_data_service_started, Toast.LENGTH_SHORT).show();
//
//		allMyBeacons = new ArrayList<Beacon>();

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Tell the user we stopped.
		Toast.makeText(this, R.string.beacon_data_service_stopped, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Traverses all currently known Beacons and prints all their timestamp-rssi data to the
	 * console.
	 */
	public void printAllBeaconsWithRssiOverTime() {
		if (!allMyBeacons.isEmpty()) {
			for (Beacon b : allMyBeacons) {
				MyBeacon mb = (MyBeacon) b;
				Log.d(TAG, "Beacon " + mb.getId2() + ": " + mb.getAllTimestamps().toString());
			}
		}
	}

	public List<Beacon> getAllMyBeacons() {
		return allMyBeacons;
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
}
