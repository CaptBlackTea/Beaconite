package com.example.deas.beaconite.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.deas.beaconite.BeaconDataService;
import com.example.deas.beaconite.R;

/**
 * Provides a menu for generating a basic game from the current graph. TODO: make activity to
 * generate this basic game (result of constraints) TODO: save/load game TODO: play (base) game
 * (show tokens, localize player,...) TODO: look at the graph again TODO: make activity to create a
 * story level (which is on top of the base game)
 */
public class BaseGameActivity extends MenuActivity {
	protected static final String TAG = "BaseGameActivity";
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

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mIsBound = false;
			Log.d(TAG, "********** SERVICE WAS DISCONNECTED!");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_game);

		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);


		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Bind to BeaconDataService
		boolean bound = bindService(beaconDataServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

		Log.d(TAG, "----- bind Service: " + bound);
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

	public void onGenerateBaseGameBtnClicked(View view) {
		// TODO: generate automatically a game meeting the constraints annotated in the graph
		if (mIsBound) {
			mService.generateBaseGame();
		}

	}

	public void onSaveBaseGameBtnClicked(View view) {
		// TODO: what to save and in which form? Should it be saved at all or being created fresh
		// every time?
	}

	public void onPlayBaseGameActivityBtnClicked(View view) {
		// TODO: make the generated base game playable: no view of the graph, just tokens and
		// messages of (not) allowed cache entering.
		Intent myIntent = new Intent(this, RunGameActivity.class);

		String startedFrom = this.getString(R.string.title_activity_base_game);

		myIntent.putExtra("startedFrom", startedFrom);
		this.startActivity(myIntent);
	}

	public void onViewGraphBtnClicked(View view) {
		Intent myIntent = new Intent(this, GraphEditActivity.class);
		myIntent.putExtra("readOnly", true);
		this.startActivity(myIntent);
	}
}
