package com.example.deas.beaconite;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * A BoundService to store all known Beacons and data connected to them. Currently: Beacons and
 * their Rssi values over time Holds all recoded Caches. Holds a connection to a BeaconConsumer so
 * it can fill its data structures by itself.
 * <p/>
 * <p>
 * Collects all data needed for fingerprinting and localization and such. That means for now Beacons
 * that were seen since Activity start and recorded Caches; Idea is that it stores all Beacons in a
 * data structure connected to timestamp(Long) and corresponding Rssi value (int). That means it is
 * stored how a Beacons visibility changes over time.
 * <p>
 * This service represents roughly the model. All activities etc can bind to it and ask for the
 * currently stored data.
 * <p>
 * TODO: implement permanent data storage to a file!
 * <p>
 * Created by deas on 26/08/16.
 */
public class BeaconDataService extends Service implements BeaconConsumer {
	protected static final String TAG = "BeaconDataService";

	// Binder given to clients
	private final IBinder mBinder = new BeaconDataBinder();
	private FileSupervisor fileSupervisor;
	// the file to where the Caches are written to and read from on the Android Device
	private File fileForCaches;
	private BeaconMap allMyBeacons;
	private List<Cache> allMyCaches;

	private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
	private RangeNotifier beaconNotifier = new RangeNotifier() {
		@Override
		public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

			addAllBeacons(beacons, System.currentTimeMillis());

			printAllBeaconsWithRssiOverTime();

			printAllMyCaches();

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

	public void resetBeaconData() {
		allMyBeacons.clear();
	}

	public void resetCacheData() {
		allMyCaches.clear();
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

		allMyBeacons = new BeaconMap();

		allMyCaches = new ArrayList<>();

		beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

		// bind the Beacon notifier
		beaconManager.bind(this);

		// TODO: make read/load file changeable
		// setup file for writing and reading the Cache data to/from
		fileSupervisor = new FileSupervisor(setUpFileForCaches("allCaches.json"));

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
		// Remove beaconNotifier
		beaconManager.removeRangeNotifier(beaconNotifier);
		beaconManager.unbind(this);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.beacon_data_service_stopped, Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}

	/**
	 * Traverses all currently known Beacons and prints all their timestamp-rssi data to the
	 * console.
	 */
	public void printAllBeaconsWithRssiOverTime() {
		if (!allMyBeacons.isEmpty()) {
			for (Entry<Beacon, SortedMap<Long, Integer>> entry : allMyBeacons.entrySet()) {
				Beacon b = entry.getKey();
				Map<Long, Integer> timeRssiMap = entry.getValue();

				Log.d(TAG, "Beacon " + b.getId2() + ": " + timeRssiMap.toString());
			}
		}
	}

	public BeaconMap getAllMyBeacons() {
		return allMyBeacons;
	}

	/**
	 * Adds a given Beacon to the list of all Beacons that were seen since Activity start.
	 *
	 * @param beacon the Beacon to add to the list of all Beacons.
	 */
	private void addBeaconToList(Beacon beacon) {
		if (!allMyBeacons.containsKey(beacon)) {
			allMyBeacons.put(beacon, new TreeMap<Long, Integer>());
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

	public Cache addCache(String cachename) {
		Cache newCache = new Cache(cachename);
		allMyCaches.add(newCache);
		return newCache;
	}

	public boolean deleteCache(String cacheName) {
		Cache delCache = getCacheByName(cacheName);
		return allMyCaches.remove(delCache);
	}

	/**
	 * If the given cache is unknown it creates a new cache with this name and adds the given
	 * timestamp pair. If this cache exists it just adds the timestamp pair.
	 *
	 * @param cachename
	 * @param startTimestamp
	 * @param stopTimestamp
	 */
	public void addTimestampPairToCache(String cachename, Long startTimestamp, Long stopTimestamp) {
		Cache cache = getCacheByName(cachename);
		if (cache == null) {
			cache = addCache(cachename);
		}

		cache.addNewTimestampPair(startTimestamp, stopTimestamp);

		cache.calculateFingerprint(allMyBeacons);

		// FIXME: Exception handling!
		try {
			writeAllCachesInFile();
			Log.d(TAG, "############ WROTE FILE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Cache> getAllMyCaches() {
		return allMyCaches;
	}

	/**
	 * Traverses all currently known Caches and prints all their timestamp-pairs to the console.
	 */
	public void printAllMyCaches() {
		if (!allMyCaches.isEmpty()) {
			for (Cache c : allMyCaches) {
				Log.d(TAG, c.toString());
			}
		} else {
			Log.d(TAG, "++++ No Caches to show ++++");
		}
	}

	private Cache getCacheByName(String cachename) {
		for (Cache c : allMyCaches) {
			if (c.getCacheName().equals(cachename)) {
				return c;
			}
		}

		return null;
	}

	private void writeAllCachesInFile() throws IOException {

		// Create the file.

		fileSupervisor.writeAllCachesInFile(allMyCaches);
	}


	// FIXME: do correct stuff here!
	// FIXME: Handle Exception!
	public void loadCachesFromFile() throws IOException {

		//JSON from file to Object

		allMyCaches = fileSupervisor.loadCachesFromFile();

		// TODO: check if the loading was successful; e.g. introduce a variable
		// Tell the user that caches were loaded
		int numberOfCurrentCaches = allMyCaches.size();
		Toast.makeText(this, "Cache data loaded; Currently there are" +
				" " +
				numberOfCurrentCaches + " caches.", Toast
				.LENGTH_SHORT).show();
	}

	/**
	 * Generates a file, to which the caches are written and read, at the Applications public
	 * folder.
	 */
	private File setUpFileForCaches(String filename) {

		// check if writable on external, if not write to internal storage!
		if (isExternalStorageWritable()) {

			// this path is used for reading and writing
			String path =
					this.getExternalFilesDir(null) + File.separator +
							"Beaconite-Data";

			// Create the folder.
			File folder = new File(path);
			folder.mkdirs();

			fileForCaches = new File(folder, filename);

		} else {
			fileForCaches = new File(this.getFilesDir(), filename);
		}

		Log.d(TAG, "FileForCaches absolute Path: " + fileForCaches.getAbsolutePath());

		return fileForCaches;
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
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
}
