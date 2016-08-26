package com.example.deas.beaconite;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

import java.util.Collection;

/**
 * Creates a TableView and puts all data from the Beacons the BeaconManager provides in it. Does
 * also do a ranging: how far is a Beacon away? Does refresh its View every time a new list of
 * Beacons is provides Does start a Service to store Beacon data (all Beacons that were seen, Rssi
 * over time) so it can be accessed by other Activities (e.g. an Activity that plots the Beacon data
 * in a graph)
 */
public class RangingActivity extends AppCompatActivity implements BeaconConsumer {
	protected static final String TAG = "RangingActivity";
	private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

	private Collection<Beacon> previousBeacons = null;

//	// Collects all Beacons that were seen since Activity start
//	// idea is that it only holds MyBeacons, as they can store Rssi connected with timestamp
//	private List<Beacon> allMyBeacons;

	// the Service
	private BeaconDataService mService;
	private boolean mIsBound = false;

	/**
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection mConnection
			= new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			// We've bound to BeaconDataService, cast the IBinder and get BeaconDataService instance

			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.

			BeaconDataService.BeaconDataBinder serviceBinder = (BeaconDataService.BeaconDataBinder)
					binder;
			mService = serviceBinder.getService();
			mIsBound = true;

			Log.d(TAG, "------ on Service connected was called. mService, mIsBound: " + mService
					+ ", " + mIsBound);

			Log.d(TAG, "AllMyBeacons " + mService.getAllMyBeacons());
//				printAllBeaconsWithRssiOverTime();
			mService.printAllBeaconsWithRssiOverTime();

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mService = null;
			mIsBound = false;
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_ranging);
		setContentView(R.layout.activity_ranging_table);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

		beaconManager.bind(this);

//		mConnection = new ServiceConnection() {
//
//			@Override
//			public void onServiceConnected(ComponentName className, IBinder service) {
//				// We've bound to BeaconDataService, cast the IBinder and get BeaconDataService instance
//				BeaconDataService.BeaconDataBinder binder = (BeaconDataService.BeaconDataBinder) service;
//				mService = binder.getService();
//				mIsBound = true;
//			}
//
//			@Override
//			public void onServiceDisconnected(ComponentName arg0) {
//				mIsBound = false;
//			}
//		};

//		allMyBeacons = new ArrayList<Beacon>();

		// Bind to BeaconDataService
		Intent intent = new Intent(this, BeaconDataService.class);
		Log.d(TAG, "mConnection is " + mConnection);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		Log.d(TAG, "SERVICE is " + mService);
		Log.d(TAG, "mBOUND is " + mIsBound);

		Log.i(TAG, "********* Ranging!");

	}

	@Override
	protected void onStart() {
		super.onStart();
//		// Bind to BeaconDataService
//		Intent intent = new Intent(this, BeaconDataService.class);
//		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

//		mIsBound = true;
		Log.d(TAG, "SERVICE is " + mService);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind from the service
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
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
		unbindService(mConnection);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);

		Intent intent = new Intent(this, BeaconDataService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onBeaconServiceConnect() {
		final Activity rangingActivityContext = this;
		final BeaconDataService service = this.mService;

		beaconManager.addRangeNotifier(new RangeNotifier() {
			@Override
			public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region
					region) {
				runOnUiThread(new BeaconOverviewTable(beacons, previousBeacons,
						rangingActivityContext, service));
				previousBeacons = beacons;
			}
		});

		try {
			beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
		} catch (RemoteException e) {
			Log.i(TAG, "------ Exception!" + e);
		}
	}

//	/**
//	 * Traverses all currently known Beacons and prints all their timestamp-rssi data to the
//	 * console.
//	 */
//	private void printAllBeaconsWithRssiOverTime() {
//		if (!allMyBeacons.isEmpty()) {
//			for (Beacon b : allMyBeacons) {
//				MyBeacon mb = (MyBeacon) b;
//				Log.d(TAG, "Beacon " + mb.getId2() + ": " + mb.getAllTimestamps().toString());
//			}
//		}
//	}

	private void logToDisplay(final String line) {
		runOnUiThread(new Runnable() {
			public void run() {
				EditText editText = (EditText) RangingActivity.this.findViewById(R.id.rangingText);

				editText.append(line + "\n");

			}
		});
	}

//	/**
//	 * Adds a given Beacon to the list of all Beacons that were seen since Activity start. Wraps the
//	 * given Beacon in a MyBeacon and then adds it to the list.
//	 *
//	 * @param beacon the Beacon to wrap in a MyBeacon and add to the list of all Beacons.
//	 * @return true: the given Beacon was successfully added to the list. false: the given Beacon is
//	 * already in the list and therefore was not added and the list did not change.
//	 */
//	public boolean addBeaconToList(Beacon beacon) {
//		if (!allMyBeacons.contains(beacon)) {
//			return allMyBeacons.add(new MyBeacon(beacon));
//		}
//
//		return false;
//	}

//	public boolean existsBeacon(Beacon beacon) {
//		return allMyBeacons.contains(beacon);
//	}
//
//	/**
//	 * Takes a Beacon and adds given Rssi value and Timestamp to this Beacon.
//	 *
//	 * @param b         the Beacon to add the values to
//	 * @param timestamp the timestamp when the given rssi occurred
//	 * @param rssi      the Rssi value the given Beacon hat at the given timestamp
//	 * @return true if the timestamp and rssi value were successfully added to the Beacon false if
//	 * it was not possible. Can also mean that this Beacon is unknown and not listed. Test if the
//	 * Beacon exists and add it if it is unknown (does not exist).
//	 */
//	public boolean addNewRssiToBeacon(Beacon b, Long timestamp, int rssi) {
//		if (allMyBeacons.contains(b)) {
//			int index = allMyBeacons.indexOf(b);
//			MyBeacon updateBeacon = (MyBeacon) allMyBeacons.get(index);
//			updateBeacon.addRssiAndTime(timestamp, rssi);
//			return true;
//		}
//
//		return false;
//	}

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
