package com.example.deas.beaconite.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deas.beaconite.BeaconCacheMatcher;
import com.example.deas.beaconite.BeaconDataService;
import com.example.deas.beaconite.Cache;
import com.example.deas.beaconite.GameStuff.BaseGame;
import com.example.deas.beaconite.R;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mainly works like LocalizeMe. Look with Fingerprints where we are and do stuff when we hit a
 * cache.
 */
public class RunGameActivity extends MenuActivity {
	protected static final String TAG = "RunGameActivity";

	// the Service
	private BeaconDataService mService;
	private boolean mIsBound = false;
	private Intent beaconDataServiceIntent;
	private BeaconDataService.BeaconPositionCallback beaconPositionCallback;
	private BaseGame game;

	private List<Cache> matchingCaches;
	private int refreshCounter;
	/**
	 * True: access granted for the current position. proceed. or do whatever with this information.
	 * False: access denied.
	 */
	private boolean accessGranted = true;

	private Toast toast;
	private AlertDialog dialog;
	private int correctLocalizations;
	private int falseLocalizations;
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

			// TODO: grab cache fingerprints from the service
			// TODO: make tmpFingerPrints every second or so and compare those to the cache FPs
			// TODO: report/print if a cache was entered/FP matches together with useful debug data.

			Log.d(TAG, "------ on Service connected was called. mService, mIsBound: " + mService
					+ ", " + mIsBound);

			beaconPositionCallback = new BeaconDataService.BeaconPositionCallback() {
				@Override
				public void update(Collection<Beacon> beacons) {
					// TODO: method or class instance to handle what to do with the beacon
					// scan (=beacons)
					//e.g. Toast; refresh screen; calculate position; find matching caches etc

					matchingCaches.addAll(BeaconCacheMatcher.matchesAnyCache(beacons, mService
							.getAllMyCaches()));
					refreshCounter++;

					Log.d(TAG, ">>>> Matching Caches: " + matchingCaches);
					Log.d(TAG, ">>>> Refresh Counter: " + refreshCounter);

					if (refreshCounter >= 6) {
						Cache currentCache = findHighestOccurringCache();
						Log.d(TAG, ">>>> Highest occuring Cache: " + currentCache);
						if (currentCache != null) {
							Log.d(TAG, ">>>> Highest occuring Cachename: " + currentCache.getCacheName
									());
							game.setCurrentCache(currentCache);
							game.freeze();
							showMatchResult(currentCache);
							fillTokenTextView(game.getPlayerTokensNames());
							matchingCaches.clear();
							refreshCounter = 0;
						}
					}

					Log.d(TAG, ">>>> Proceed Game: " + game.proceedGame());
//					if (game.proceedGame() && !accessGranted) {
					if (game.proceedGame()) {
						game.nextRound();
					}


//					// only if we see just one cache:
//					if (matchingCaches.size() == 1) {
//						showMatchResult(matchingCaches);
//					}
				}

			};

			mService.setBeaconPositionCallback(beaconPositionCallback);

			if (mService.getBaseGame() == null) {
				game = mService.generateBaseGame();
			} else {
				game = mService.getBaseGame();
			}


			Intent startIntent = getIntent();
			if (startIntent.hasExtra("startedFrom")) {
//				showAlertDialog(String.valueOf(getString(R.string.runGameInfoDialog)).concat
//						(startIntent
//								.getStringExtra("startedFrom")));
				String text = startIntent.getStringExtra("startedFrom");
				Toast.makeText(RunGameActivity.this, text, Toast.LENGTH_LONG).show();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mIsBound = false;
			Log.d(TAG, "********** SERVICE WAS DISCONNECTED!");
		}
	};

	private void fillTokenTextView(final List<String> playerTokensNames) {
		final TextView tokenList = (TextView) RunGameActivity.this.findViewById(R.id.tokenList);
		final String tokens = playerTokensNames.toString();

		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mService != null && tokenList != null) {
					String newTokenList = String.valueOf(getString(R.string.tokenList)) + "\n";
					newTokenList += tokens;
					tokenList.setText(newTokenList);
				}
			}
		});
	}


	private Cache findHighestOccurringCache() {
		Map<Cache, Integer> cacheOccurrences = new HashMap<>();
		for (Cache cache : matchingCaches) {
			if (cacheOccurrences.containsKey(cache)) {
				cacheOccurrences.put(cache, cacheOccurrences.get(cache) + 1);
			} else {
				cacheOccurrences.put(cache, 1);
			}
		}

		Map.Entry<Cache, Integer> maxEntry = null;

		for (Map.Entry<Cache, Integer> entry : cacheOccurrences.entrySet()) {
			if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
				maxEntry = entry;
			}
		}
		// maxEntry should now contain the maximum,

		if (maxEntry == null) {
			return null;
		}
		return maxEntry.getKey();
	}

	private void showMatchResult(Cache cache) {
		StringBuilder locationInfo = new StringBuilder();
		locationInfo.append(String.format("You are in %s. You are " +
				"", cache
				.getCacheName()));
		if (!this.game.goToVertex(cache.getVertex())) {
			locationInfo.append("not ");
//			accessGranted = false;
		}

		locationInfo.append(String.format("allowed to be here according to your list of tokens: %s %n",
				game.getPlayerTokensNames()));

		locationInfo.append("Is it correct that you are in this location?");

		final String message = locationInfo.toString();

		// IMPORTANT: this gets called from service (onServiceConnect/update) -> change to correct
		// thread
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showAlertDialog(message, "Localization correct?", false);
			}
		});
	}

//	private void showMatchResult(List<Cache> matchingCaches) {
//		Log.d(TAG, "Show Match Result");
//		String cacheNames = "Matching Caches \n";
//
//		if (!matchingCaches.isEmpty()) {
//
//			for (Cache c : matchingCaches) {
//				cacheNames = cacheNames + c.getCacheName() + "\n";
//			}
//		} else {
//			cacheNames = cacheNames + "- none found; searching -";
//		}
//
//		final String finalCacheNames = cacheNames;
//
//
//		this.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				if (toast == null) { // Initialize toast if needed
//					toast = Toast.makeText(RunGameActivity.this, "", Toast.LENGTH_SHORT);
//				}
//				toast.setText(finalCacheNames);
//				toast.show();
//			}
//		});
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_game);

		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);

		matchingCaches = new ArrayList<>();
		refreshCounter = 0;

		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);


//		if (toast == null) { // Initialize toast if needed
//			toast = Toast.makeText(RunGameActivity.this, "", Toast.LENGTH_SHORT);
//		}

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

//		Intent startIntent = getIntent();
//		if (startIntent.hasExtra("startedFrom")) {
//			showAlertDialog(String.valueOf(this.getString(R.string.runGameInfoDialog)).concat
//					(startIntent
//							.getStringExtra("startedFrom")));
//		}
	}

//	private void showAlertDialog(String message) {
//		showAlertDialog(message, "Attention", true);
//	}

	private void showAlertDialog(String message, String title, boolean cancelable) {
		if (dialog != null && dialog.isShowing()) {
			return;
		}
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setCancelable(cancelable);

		if (!cancelable) {
			// setup buttons
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
					// count how often the location was correct
					game.setProceedGame(true);
					game.unfreeze();
					correctLocalizations++;
					dialog.dismiss();
				}
			})
					.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Localization was not correct, count that too
							falseLocalizations++;

							// the player position is not this cache, so the current cache is set
							// null
							game.setCurrentCache(null);
							game.setProceedGame(true);
							game.unfreeze();
							dialog.dismiss();
						}
					});

		} else {
			// setup buttons
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
		}

		// 3. Get the AlertDialog from create()
		dialog = builder.create();
		dialog.show();
	}
}
