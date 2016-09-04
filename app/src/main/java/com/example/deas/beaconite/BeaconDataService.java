package com.example.deas.beaconite;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * A BoundService to store all known Beacons and data connected to them. Currently: Beacons and
 * their Rssi values over time
 * Holds all recoded Caches.
 * Holds a connection to a BeaconConsumer so it can fill its data structures by itself.
 * <p/>
 * Created by deas on 26/08/16.
 */
public class BeaconDataService extends Service implements BeaconConsumer {
	protected static final String TAG = "BeaconDataService";

	// Collects all Beacons that were seen since Activity start
	// idea is that it holds Beacons connected with a Map. In the map a timestamp(Long) and
	// corresponding Rssi value (int) is stored. So a it is stored how a Beacons visibility is
	// changes over time.

	//	private List<Beacon> allMyBeacons;
	private Map<Beacon, Map<Long, Integer>> allMyBeacons;

	private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

	private RangeNotifier beaconNotifier = new RangeNotifier() {
		@Override
		public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

			addAllBeacons(beacons, System.currentTimeMillis());

			printAllBeaconsWithRssiOverTime();

		}
	};

	/**
	 * Add all Beacons in the given Collection to the internal Datastructure.
	 *
	 * @param beacons
	 * @param timestamp
	 */
	private void addAllBeacons(Collection<Beacon> beacons, long timestamp) {
		if (beacons != null && !beacons.isEmpty()) {
			if (allMyBeacons != null) {
				for (Beacon b : beacons) {
					addBeaconToList(b);
					addNewRssiToBeacon(b, timestamp, b.getRssi());
				}
			}
		}
	}


	// Binder given to clients
	private final IBinder mBinder = new BeaconDataBinder();

	@Override
	public void onBeaconServiceConnect() {
		if (!beaconManager.getRangingNotifiers().contains(beaconNotifier)) {
			beaconManager.addRangeNotifier(beaconNotifier);
		}
		try {
			beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));


		} catch (RemoteException e) {
			Log.i(TAG, "------ Exception!" + e);
		}
	}

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

		allMyBeacons = new HashMap<>();

		beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

		// bind the Beacon notifier
		beaconManager.bind(this);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		super.onUnbind(intent);
		return true;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		// Remove beaconNotifier
		beaconManager.removeRangeNotifier(beaconNotifier);
		beaconManager.unbind(this);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.beacon_data_service_stopped, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Traverses all currently known Beacons and prints all their timestamp-rssi data to the
	 * console.
	 */
	public void printAllBeaconsWithRssiOverTime() {
		if (!allMyBeacons.isEmpty()) {
			for (Entry<Beacon, Map<Long, Integer>> entry : allMyBeacons.entrySet()) {
				Beacon b = entry.getKey();
				Map<Long, Integer> timeRssiMap = entry.getValue();

				Log.d(TAG, "Beacon " + b.getId2() + ": " + timeRssiMap.toString());
			}
		}
	}

	public Map<Beacon, Map<Long, Integer>> getAllMyBeacons() {
		return allMyBeacons;
	}

	/**
	 * Adds a given Beacon to the list of all Beacons that were seen since Activity start.
	 *
	 * @param beacon the Beacon to add to the list of all Beacons.
	 *
	 */
	private void addBeaconToList(Beacon beacon) {
		if (!allMyBeacons.containsKey(beacon)) {
			allMyBeacons.put(beacon, new HashMap<Long, Integer>());
		}
	}

	public boolean existsBeacon(Beacon beacon) {
		return allMyBeacons.containsKey(beacon);
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
	private boolean addNewRssiToBeacon(Beacon b, Long timestamp, int rssi) {
		if (allMyBeacons.containsKey(b)) {
			allMyBeacons.get(b).put(timestamp, rssi);
			return true;
		}

		return false;
	}

	private void writeBeaconInFile(Beacon b) {
		// TODO: implement this some day...maybe...ideas change
	}
}
