package com.example.deas.beaconite.activities;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deas.beaconite.AdapterCache;
import com.example.deas.beaconite.BeaconDataService;
import com.example.deas.beaconite.R;

public class GenerateCachesActivity extends MenuActivity {

	protected static final String TAG = "GenerateCachesActivity";

	private int cacheCounter = 0;

	private Long startTimestampForCache;
	private Long stopTimestampForCache;

	// the Service
	private BeaconDataService mService;
	private boolean mIsBound = false;
	private Intent beaconDataServiceIntent;
	private AdapterCache adbCache;
	private ProgressBar bar;
	private ObjectAnimator animation;
	private Handler progressHandler;
	private ProgressBar progressBarHorizontal;
	private CountDownTimer countDownTimer;
	private String cacheName;
	private int recordingTime = 60000;
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

			setupCacheListView();

			countDownTimer = makeCountDownProgressBar();

			setupRecordCacheButton(recordingTime);

			Log.d(TAG, "------ on Service connected was called. mService, mIsBound: " + mService
					+ ", " + mIsBound);

			Log.d(TAG, "AllMyCaches " + mService.getAllMyCaches());


		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mIsBound = false;
			Log.d(TAG, "********** SERVICE WAS DISCONNECTED!");
		}
	};

	private void setupCacheListView() {
		adbCache = new AdapterCache(this, 0, mService.getAllMyCaches());
		ListView cacheList = (ListView) this.findViewById(R.id.listOfRecordedCaches);
		cacheList.setAdapter(adbCache);

		cacheList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				TextView recordThisCache = (TextView) GenerateCachesActivity.this.findViewById(R
						.id.cacheInQuestion);
				recordThisCache.setText(adbCache.getItem(i).getCacheName());
			}
		});
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generate_caches);

		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);

		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);

		Log.d(TAG, "#### CREATE");
	}

	/**
	 * Sets up a button to record a cache. Recording starts when pushing the button down and lasts
	 * as long as the button is held down. When the button is released (button up event) the
	 * recording stops and the collected data is stored to the currently selected cache.
	 */
	private void setupRecordCacheButton() {
		final Button recordCacheBtn = (Button) this.findViewById(R.id.recordCacheBtn);

		if (recordCacheBtn != null) {
			recordCacheBtn.setOnTouchListener(new View.OnTouchListener() {
				@TargetApi(Build.VERSION_CODES.M)
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (event.getAction() == MotionEvent.ACTION_DOWN) {

						countDownTimer.start();

						startTimestampForCache = System.currentTimeMillis();
						recordCacheBtn.setBackgroundColor(getResources().getColor(R.color.red,
								null));
					} else if (event.getAction() == MotionEvent.ACTION_UP) {

						if (countDownTimer != null) {
							countDownTimer.cancel();
							resetCountDownTimer();
						}

						recordCacheBtn.setBackgroundColor(getResources().getColor(R.color.green,
								null));
						stopTimestampForCache = System.currentTimeMillis();
						TextView cacheNameView = (TextView) GenerateCachesActivity.this.findViewById
								(R.id.cacheInQuestion);

						if (cacheNameView != null && !cacheNameView.getText().equals("")) {
							String cacheName = cacheNameView.getText().toString();

							mService.addTimestampPairToCache(cacheName, startTimestampForCache, stopTimestampForCache);
							adbCache.notifyDataSetChanged();
							startTimestampForCache = -1L;
							stopTimestampForCache = -1L;
						}
					}
					return true;
				}
			});

			recordCacheBtn.setEnabled(true);

		} else {
			showAlertDialog(String.valueOf(R.string.noCache_message));
		}
	}

	/**
	 * Same as the method setupRecordCacheButton. The argument is the recording time for a cache in
	 * milliseconds. When given a int argument the recording is started at button down and stops
	 * after the given recording time. So no button holding down is required.
	 *
	 * @param recordingDuration time in milliseconds for a cache recording.
	 */
	private void setupRecordCacheButton(int recordingDuration) {
		final Button recordCacheBtn = (Button) this.findViewById(R.id.recordCacheBtn);

		if (recordCacheBtn != null) {
			recordCacheBtn.setOnTouchListener(new View.OnTouchListener() {
				@TargetApi(Build.VERSION_CODES.M)
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					if (event.getAction() == MotionEvent.ACTION_DOWN) {

						countDownTimer.start();

						startTimestampForCache = System.currentTimeMillis();
						recordCacheBtn.setBackgroundColor(getResources().getColor(R.color.red,
								null));
					}

					return true;
				}
			});

			recordCacheBtn.setEnabled(true);

		} else {
			showAlertDialog(String.valueOf(R.string.noCache_message));
		}
	}

	private void resetCountDownTimer() {
		bar = (ProgressBar) findViewById(R.id.progressbar);
		bar.setProgress(0);
		bar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

	}

	/**
	 * Makes a countdown timer and progressbar that is connected to it.
	 * @return
	 */
	private CountDownTimer makeCountDownProgressBar() {

		// Was refactored: instead use of "recordingTime", is now a field that can be accessed.
//		final int oneMin = 60000; // 1 minute in milli seconds

		bar = (ProgressBar) findViewById(R.id.progressbar);
		bar.setProgress(0);
		bar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
//		final int[] tick = {0};

		/** CountDownTimer starts with 1 minutes and every onTick is 1 second */
		/**
		 * http://stackoverflow.com/questions/8857590/android-countdowntimer-skips-last-ontick?noredirect=1&lq=1
		 *
		 * At the start of every tick, before onTick() is called, the remaining time until the
		 * end of the countdown is calculated. If this time is smaller than the countdown time
		 * interval, onTick is not called anymore. Instead only the next tick (where the onFinish
		 * () method will be called) is scheduled. Given the fact that hardware clocks are not
		 * always super precise, that there may be other processes in the background that delay
		 * the thread running CountDownTimer plus that Android itself will probably create a
		 * small delay when calling the message handler of CountDownTimer it is more than likely
		 * that the call for the last tick before the end of the count down will be at least one
		 * millisecond late and therefore onTick() will not be called.
		 *
		 * I noticed on my device there is a 6ms delay. So countDownTime+200 should solve it in
		 * most cases and make it almost unrecognizable to the user.
		 *
		 * For applications where the length of the interval time is critical, the other
		 * solutions posted here are probably the best.
		 */
		CountDownTimer cdt = new CountDownTimer(this.recordingTime + 200, 1000) {
			public void onTick(long millisUntilFinished) {
				bar.setMax(60);
				bar.incrementProgressBy(1);

				Log.d(TAG, "Progress: " + bar.getProgress());
			}

			public void onFinish() {
				// DO something when 1 minute is up
				bar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);

				//TODO: added this functionality for the button method with the recording duration.
				// if the button method without the argument is used disable the following or
				// make a method duplicate etc
				finishRecording();
			}
		};

		return cdt;
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void finishRecording() {
		if (countDownTimer != null) {
			countDownTimer.cancel();
			resetCountDownTimer();
		}
		Button recordCacheBtn = (Button) this.findViewById(R.id.recordCacheBtn);

		recordCacheBtn.setBackgroundColor(getResources().getColor(R.color.green,
				null));
		stopTimestampForCache = System.currentTimeMillis();
		TextView cacheNameView = (TextView) GenerateCachesActivity.this.findViewById
				(R.id.cacheInQuestion);

		if (cacheNameView != null && !cacheNameView.getText().equals("")) {
			String cacheName = cacheNameView.getText().toString();

			mService.addTimestampPairToCache(cacheName, startTimestampForCache, stopTimestampForCache);
			adbCache.notifyDataSetChanged();
			startTimestampForCache = -1L;
			stopTimestampForCache = -1L;
		}

	}

	private void showAlertDialog(String message) {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage(message);

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
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
	}

	@Override
	protected void onPause() {
		super.onPause();
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

		Intent intent = getIntent();
		TextView recordThisCache = (TextView) GenerateCachesActivity.this.findViewById(R
				.id.cacheInQuestion);
		recordThisCache.setText(intent.getStringExtra("cache"));
	}


	public void createNewCacheBtn(View view) {

		makeEnterCacheNameDialog();

		TextView recordThisCache = (TextView) this.findViewById(R.id.cacheInQuestion);
		recordThisCache.setText(cacheName);

		adbCache.notifyDataSetChanged();
	}

	private void makeEnterCacheNameDialog() {

		final EditText input = new EditText(this);

		final AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("Enter cache name")
				.setMessage("Enter a name for this cache. Must not be empty or a name that already" +
						" exists")
				.setNegativeButton("Cancel", null)
				.setPositiveButton("Ok", null)
				.setView(input)
				.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {

				Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
				okButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						try {
							String newCacheName = input.getText().toString();
							if (!newCacheName.isEmpty()) {

								if (mService.addCache(newCacheName) == null) {
									throw new IllegalArgumentException("This name already exists!");
								}

								cacheName = newCacheName;
								dialog.dismiss();
							} else {
								throw new IllegalArgumentException("The name field is empty!");
							}

						} catch (IllegalArgumentException e) {
							Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});

		dialog.show();
	}

	public void deleteCacheBtn(View view) {
		final TextView cacheToDelete = (TextView) this.findViewById(R.id.cacheInQuestion);
		final String cacheName = cacheToDelete.getText().toString();

		// AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// Add the buttons
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button
				if (mService.deleteCache(cacheName)) {
					cacheToDelete.setText("");
					adbCache.notifyDataSetChanged();
				} else {
					Toast.makeText(getBaseContext(), "Error: Could not delete cache!", Toast
							.LENGTH_LONG).show();
					dialog.dismiss();
				}
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		});

		// Set other dialog properties
		builder.setTitle("Delete cache")
				.setMessage("Do you really want to delete this cache?");

		// Create the AlertDialog
		AlertDialog dialog = builder.create();
		dialog.show();

//		if (mService.deleteCache(cacheName)) {
//			cacheToDelete.setText("");
//			adbCache.notifyDataSetChanged();
//		}

	}


	public void onGraphBtnClicked(View view) {
		Intent myIntent = new Intent(this, ChartActivity.class);
		this.startActivity(myIntent);
	}
}
