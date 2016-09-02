package com.example.deas.beaconite;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.altbeacon.beacon.BeaconManager;

/**
 * Class was implemented according to the sample Code on the altBeacon site. see:
 * https://github.com/AltBeacon/android-beacon-library-reference/blob/master/app/src/main/java/org/altbeacon/beaconreference/MonitoringActivity.java
 *
 * TODO: Refactoring so that this is the main activity that starts/stops all important services
 * and has the controll over them (resetting data and so on)
 * TODO: scanning mode on/off
 * TODO: start/stop/reset BeaconDataService
 * TODO: use/use not/restart MyBeaconSimulator
 * TODO: go to RangingActivity (= show table with live data)
 * TODO: go to GraphActivity (= show collected data as graph)
 * TODO: go to GenerateCachesActivity (= show/record caches)
 * TODO: Exit BeaconiteApp and close all services and background scans
 *
 * @author dea 25.6.2016
 */

public class MainActivity extends AppCompatActivity {
	protected static final String TAG = "MainActivity";
	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

	private Intent beaconDataServiceIntent;

	/**
	 * Check permissions needes for this app. Start BeaconDataService.
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		verifyBluetooth();
		logToDisplay("Application just launched");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// Android M Permission check
			if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("This app needs location access");
				builder.setMessage("Please grant location access so this app can detect beacons in the background.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

					@TargetApi(23)
					@Override
					public void onDismiss(DialogInterface dialog) {
						requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
								PERMISSION_REQUEST_COARSE_LOCATION);
					}

				});
				builder.show();
			}
		}

		// Start the BeaconSimulator
		BeaconManager.setBeaconSimulator(new MyBeaconsSimulator(4));

		// start beaconDataService
		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);
		startService(beaconDataServiceIntent);


	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_COARSE_LOCATION: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d(TAG, "coarse location permission granted");
				} else {
					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Functionality limited");
					builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
						}

					});
					builder.show();
				}
				return;
			}
		}
	}

	/**
	 * Start the RangingActivity.
	 * @param view
	 */
	public void onRangingClicked(View view) {
		Intent myIntent = new Intent(this, RangingActivity.class);
		this.startActivity(myIntent);
	}

	@Override
	public void onResume() {
		super.onResume();
		// for Background detection
//		((BeaconReferenceApplication) this.getApplicationContext()).setMainActivity(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// for Background detection
//		((BeaconReferenceApplication) this.getApplicationContext()).setMainActivity(null);
	}

	public void logToDisplay(final String line) {
		runOnUiThread(new Runnable() {
			public void run() {
				EditText editText = (EditText) MainActivity.this
						.findViewById(R.id.monitoringText);
				editText.append(line + "\n");
				
			}
		});
	}

	private void verifyBluetooth() {
		try {
			if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");
				builder.setMessage("Please enable Bluetooth in settings and restart this Application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
						System.exit(0);
					}
				});

				builder.show();
			}
		} catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
					System.exit(0);
				}
			});
		}
	}
}