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

import java.util.Collection;

public class RangingActivity extends AppCompatActivity implements BeaconConsumer {
	protected static final String TAG = "RangingActivity";
	private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

	Logger log = LoggerFactory.getLogger(RangingActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_ranging);
		setContentView(R.layout.activity_ranging_table);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

		beaconManager.bind(this);

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
			public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
				runOnUiThread(new BeaconOverviewTable(beacons, rangingActivityContext));

			}
		});

		try {
			beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
		} catch (RemoteException e) {
			Log.i(TAG, "------ Exception!" + e);
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
