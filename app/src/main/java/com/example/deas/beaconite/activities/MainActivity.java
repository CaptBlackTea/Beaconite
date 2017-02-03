package com.example.deas.beaconite.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.deas.beaconite.BeaconDataService;
import com.example.deas.beaconite.MyBeaconsSimulator;
import com.example.deas.beaconite.R;

import org.altbeacon.beacon.BeaconManager;
import org.jgrapht.ext.ImportException;

import java.io.IOException;

/**
 * Class was implemented according to the sample Code on the altBeacon site. see:
 * https://github.com/AltBeacon/android-beacon-library-reference/blob/master/app/src/main/java/org/altbeacon/beaconreference/MonitoringActivity.java
 * <p/>
 * DONE: Refactoring so that this is the main activity that starts/stops all important services
 * DONE: has the control over the services (resetting data and so on)
 * TODO: scanning mode on/off
 * TODO: start/stop/reset BeaconDataService
 * DONE: use/use not/restart MyBeaconSimulator
 * DONE: go to TableActivity (= show table with live data)
 * DONE: go to ChartActivity (= show collected data as graph)
 * DONE: go to GenerateCachesActivity (= show/record caches)
 * DONE: Exit BeaconiteApp and close all services and background scans
 *
 * @author dea 25.6.2016
 */

public class MainActivity extends AppCompatActivity {
	protected static final String TAG = "MainActivity";
	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

	// the Service
	private BeaconDataService mService;
	private boolean mIsBound = false;
	private Intent beaconDataServiceIntent;

	/**
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection mConnection
			= new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to BeaconDataService, cast the IBinder and get BeaconDataService instance

			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.

			BeaconDataService.BeaconDataBinder serviceBinder = (BeaconDataService.BeaconDataBinder)
					service;
			mService = serviceBinder.getService();
			mIsBound = true;

			Log.d(TAG, "------ on Service connected was called. mService, mIsBound: " + mService
					+ ", " + mIsBound);

			Log.d(TAG, "AllMyBeacons " + mService.getAllMyBeacons());

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mIsBound = false;
			Log.d(TAG, "********** SERVICE WAS DISCONNECTED!");
		}
	};


	/**
	 * Check permissions needs for this app. Start BeaconDataService.
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);

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
//		BeaconManager.setBeaconSimulator(new MyBeaconsSimulator(4));

		// start beaconDataService
		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);
		startService(beaconDataServiceIntent);


	}

	@Override
	protected void onStart() {
		super.onStart();

		// Bind to BeaconDataService
		boolean bound = bindService(beaconDataServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

		Log.d(TAG, "----- bind Service: " + bound);
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

	// Menu icons are inflated just as they were with actionbar
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	/**
	 * Start the TableActivity.
	 *
	 * @param view
	 */
	public void onTableBtnClicked(View view) {
		Intent myIntent = new Intent(this, TableActivity.class);
		this.startActivity(myIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// for Background detection
//		((BeaconReferenceApplication) this.getApplicationContext()).setMainActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Unbind from the service
		Log.d(TAG, "#### STOP: mIsBound: " + mIsBound + " mService: " + mService);
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// for Background detection
//		((BeaconReferenceApplication) this.getApplicationContext()).setMainActivity(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}


	/**
	 * NO LONGER IN USE: used by the BeaconReferenceApplication which is currently not used. Kept here, but has no functionality, to satisfy the Applications needs.
	 *
	 * @param line
	 */
	public void logToDisplay(final String line) {
//		runOnUiThread(new Runnable() {
//			public void run() {
//				EditText editText = (EditText) MainActivity.this
//						.findViewById(R.id.monitoringText);
//				editText.append(line + "\n");
//
//			}
//		});
	}

	/**
	 * Shall be called when the Application is to be shut down:
	 * stop all services and so on.
	 *
	 * @param view
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void exitBeaconite(View view) {
		mService.stopSelf();
		// TODO: what else?
		this.finishAndRemoveTask();
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

	/**
	 * Starts the ChartActivity.
	 *
	 * @param view
	 */
	public void onGraphBtnClicked(View view) {
		Intent myIntent = new Intent(this, ChartActivity.class);
		this.startActivity(myIntent);
	}

	/**
	 * Starts the GenerateCachesActivity
	 *
	 * @param view
	 */
	public void onCacheActivityBtnClicked(View view) {
		Intent myIntent = new Intent(this, GenerateCachesActivity.class);
		this.startActivity(myIntent);
	}

	/**
	 * Removes all beacons that were stored till now from the service (stores and manages this data).
	 */
	public void removeAllBeaconData() {
		mService.resetBeaconData();
	}

	/**
	 * Removes all caches that were stored till now from the service (stores and manages this data)
	 */
	public void removeAllCaches() {
		mService.resetCacheData();
	}

	public void removeAllDataBtn(View view) {
		removeAllBeaconData();
		removeAllCaches();
	}

	public void restartBeaconSimulation(View view) {
		BeaconManager.setBeaconSimulator(new MyBeaconsSimulator(4));
	}

	/**
	 * Starts the activity that tries to recognize if a caches is being entered.
	 *
	 * @param view
	 */
	public void onWhereAmIBtnClicked(View view) {
		Intent myIntent = new Intent(this, LocalizeMeActivity.class);
		this.startActivity(myIntent);
	}


	public void onStartEditGraphBtnClicked(View view) {
		Intent myIntent = new Intent(this, GraphEditActivity.class);
		this.startActivity(myIntent);
	}

	public void saveCachesBtn(View view) {
		if (mService != null) {
			try {
				mService.writeAllCachesInFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveGraphBtn(View view) {
		if (mService != null) {
			mService.writeDOTGraphFile();
		}
	}

	public void saveAllDataBtn(View view) {
		if (mService != null) {
			try {
				mService.writeAllDataInFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void onLoadCachesBtnClicked(View view) {
		try {
			mService.loadCachesFromFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadGraphBtn(View view) {
		if (mService != null) {
			mService.loadGraphFromFile();
		}
	}

	public void loadAllDataBtn(View view) {
		if (mService != null) {
			try {
				mService.loadCachesAndGraphFromFile();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ImportException e) {
				e.printStackTrace();
			}
		}
	}

	public void onStartRelocationActivityBtnClicked(View view) {
		Intent myIntent = new Intent(this, RelocationActivity.class);
		this.startActivity(myIntent);
	}
}

