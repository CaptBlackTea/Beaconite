package com.example.deas.beaconite.activities;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.deas.beaconite.R;

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
public class TableActivity extends AppCompatActivity implements BeaconConsumer {
	protected static final String TAG = "TableActivity";
	private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

	private Collection<Beacon> previousBeacons = null;

	private RangeNotifier beaconNotifier = new RangeNotifier() {
		@Override
		public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region
				region) {
			runOnUiThread(new BeaconOverviewTable(beacons, previousBeacons,
					TableActivity.this));

			previousBeacons = beacons;
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ranging_table);

		beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
		Log.i(TAG, "********* Ranging!");

	}

	@Override
	protected void onStart() {
		super.onStart();

		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);


		Log.d(TAG, "onStart; toolbar: " + toolbar);

		// Get a support ActionBar corresponding to this toolbar
		ActionBar ab = getSupportActionBar();

		Log.d(TAG, "getSupportActionBar: " + ab);
		// Enable the Up button
		if (ab != null) {
			Log.d(TAG, "YAY!");
			ab.setDisplayHomeAsUpEnabled(true);
		}
		// bind the Beacon notifier
		beaconManager.bind(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Remove beaconNotifier
		beaconManager.removeRangeNotifier(beaconNotifier);
		beaconManager.unbind(this);

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();


	}

	@Override
	protected void onPause() {
		super.onPause();
		// TODO: is BackgroundMode on/off needed for this Activity??
//		if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// TODO: is BackgroundMode on/off needed for this Activity??
//		if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
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
}
