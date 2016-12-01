package com.example.deas.beaconite.activities;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.deas.beaconite.AdapterCache;
import com.example.deas.beaconite.BeaconDataService;
import com.example.deas.beaconite.R;

public class GenerateCachesActivity extends AppCompatActivity {

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

			setupRecordCacheButton();

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

		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);

		Log.d(TAG, "#### CREATE");
	}

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

	private void resetCountDownTimer() {
		bar = (ProgressBar) findViewById(R.id.progressbar);
		bar.setProgress(0);
		bar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

	}

	private CountDownTimer makeCountDownProgressBar() {

		// FIXME: set to 60!000
		final int oneMin = 60000; // 1 minute in milli seconds

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
		CountDownTimer cdt = new CountDownTimer(oneMin + 200, 1000) {
			public void onTick(long millisUntilFinished) {
				bar.setMax(60);
				bar.incrementProgressBy(1);

				Log.d(TAG, "Progress: " + bar.getProgress());
			}

			public void onFinish() {
				// DO something when 1 minute is up
				bar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
			}
		};

		return cdt;
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
	}


	public void createNewCacheBtn(View view) {
		String newCacheName = "Cache" + cacheCounter;
		if (!mService.getAllMyCaches().contains(newCacheName)) {
			mService.addCache(newCacheName);
		}

		TextView recordThisCache = (TextView) this.findViewById(R.id.cacheInQuestion);
		recordThisCache.setText(newCacheName);

		cacheCounter++;
		adbCache.notifyDataSetChanged();
	}

	public void deleteCacheBtn(View view) {
		TextView cacheToDelete = (TextView) this.findViewById(R.id.cacheInQuestion);
		String cacheName = cacheToDelete.getText().toString();

		// TODO: AlertDialog

		if (mService.deleteCache(cacheName)) {
			cacheToDelete.setText("");
			adbCache.notifyDataSetChanged();
		}

	}

	public void onGraphBtnClicked(View view) {
		Intent myIntent = new Intent(this, ChartActivity.class);
		this.startActivity(myIntent);
	}
}
