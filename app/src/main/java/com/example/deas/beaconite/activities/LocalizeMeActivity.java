package com.example.deas.beaconite.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deas.beaconite.BeaconCacheMatcher;
import com.example.deas.beaconite.BeaconDataService;
import com.example.deas.beaconite.Cache;
import com.example.deas.beaconite.R;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;
import java.util.List;

/**
 * Connects to the data service and reports if a cache was entered. Does this by using
 * fingerprinting. Idea is that it creates temporary fingerprints every second and compares these to
 * the known fingerprints of the caches.
 * <p>
 * Created by deas on 26/09/16.
 */
public class LocalizeMeActivity extends MenuActivity {
	protected static final String TAG = "LocalizeMeActivity";

	// the Service
	private BeaconDataService mService;
	private boolean mIsBound = false;
	private Intent beaconDataServiceIntent;
	private BeaconDataService.BeaconPositionCallback beaconPositionCallback;
	private Toast toast;
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

			// TODO: do stuff with Service data here
			fillCacheList();

			// TODO: grab cache fingerprints from the service
			// TODO: make tmpFingerPrints every second or so and compare those to the cache FPs
			// TODO: report/print if a cache was entered/FP matches together with useful debug data.

			Log.d(TAG, "------ on Service connected was called. mService, mIsBound: " + mService
					+ ", " + mIsBound);

//			Log.d(TAG, "AllMyCaches " + mService.getAllMyCaches());

			beaconPositionCallback = new BeaconDataService.BeaconPositionCallback() {
				@Override
				public void update(Collection<Beacon> beacons) {
					// TODO: method or class instance to handle what to do with the beacon
					// scan (=beacons)
					//e.g. Toast; refresh screen; calculate position; find matching caches etc

					List<Cache> matchingCaches = BeaconCacheMatcher.matchesAnyCache(beacons, mService
							.getAllMyCaches());

					Log.d(TAG, "Matching Caches: " + matchingCaches);
					showMatchResult(matchingCaches);

				}
			};

			mService.setBeaconPositionCallback(beaconPositionCallback);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mIsBound = false;
			Log.d(TAG, "********** SERVICE WAS DISCONNECTED!");
		}
	};

	private void showMatchResult(List<Cache> matchingCaches) {
		Log.d(TAG, "TOAST!");
		String cacheNames = "Matching Caches \n";

		if (!matchingCaches.isEmpty()) {

			for (Cache c : matchingCaches) {
				cacheNames = cacheNames + c.getCacheName() + "\n";
			}
		} else {
			cacheNames = cacheNames + "- none found; searching -";
		}

		final String finalCacheNames = cacheNames;


		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (toast == null) { // Initialize toast if needed
					toast = Toast.makeText(LocalizeMeActivity.this, "", Toast.LENGTH_SHORT);
				}
				toast.setText(finalCacheNames);
				toast.show();
			}
		});
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_localize_me);

		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);

		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);

		if (toast == null) { // Initialize toast if needed
			toast = Toast.makeText(LocalizeMeActivity.this, "", Toast.LENGTH_SHORT);
		}

		Log.d(TAG, "#### CREATE");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();


		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
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

		mService.setBeaconPositionCallback(null);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mService.setBeaconPositionCallback(null);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Bind to BeaconDataService
		bindService(beaconDataServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
		Log.d(TAG, "#### START: mIsBound: " + mIsBound + " mService: " + mService);

	}

	private void fillCacheList() {

		TextView cacheListTextView = (TextView) findViewById(R.id.recodedCachesForMatch);

		if (mIsBound) {
//			cacheListTextView.append(mService.getAllMyCaches().toString());

			for (Cache c : mService.getAllMyCaches()) {
				cacheListTextView.append(c.getCacheName() + " \n");
			}
		}
	}

}
