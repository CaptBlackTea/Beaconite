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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deas.beaconite.BeaconDataService;
import com.example.deas.beaconite.GameStuff.BaseGame;
import com.example.deas.beaconite.R;

public class CreateStoryActivity extends MenuActivity {
	protected static final String TAG = "CreateStoryActivity";

	private BaseGame game;

	// the Service
	private BeaconDataService mService;
	private boolean mIsBound = false;
	private Intent beaconDataServiceIntent;

	/**
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
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

			game = mService.getBaseGame();
			updateAvailableElementsView();

			Log.d(TAG, "------ on Service connected was called. mService, mIsBound: " + mService
					+ ", " + mIsBound);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIsBound = false;
			Log.d(TAG, "********** SERVICE WAS DISCONNECTED!");
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_story);

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
		bindService(beaconDataServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
		Log.d(TAG, "#### START: mIsBound: " + mIsBound + " mService: " + mService);

		TextView availableElements = (TextView) findViewById(R.id.availableStoryElements);
		availableElements.setMovementMethod(new ScrollingMovementMethod());
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

	public void onAddDangerProtectionPairsBtnClicked(View view) {
		if (mService == null) {
			showServiceNotConnected();
			return;
		}

		makeEnterCacheNameDialog();

	}

	private void updateAvailableElementsView() {
		TextView availableElements = (TextView) findViewById(R.id.availableStoryElements);
		if (availableElements != null) {
			availableElements.setText(game.listAllStoryElements());
		}
	}

	private void showServiceNotConnected() {
		Toast.makeText(this, "Try again later. Service has not connected yet.", Toast.LENGTH_SHORT)
				.show();
	}

	private void makeEnterCacheNameDialog() {

		LinearLayout inputFields = new LinearLayout(this);
		inputFields.setOrientation(LinearLayout.VERTICAL);

		final EditText dangerInput = new EditText(this);
		dangerInput.setHint("Danger");

		final EditText protectionInput = new EditText(this);
		protectionInput.setHint("Protection");

		inputFields.addView(dangerInput);
		inputFields.addView(protectionInput);

		final AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("Add: Danger and Protection")
				.setMessage("Enter a Danger and an associated Protection element. Both fields " +
						"must not be empty or names that already exists")
				.setNegativeButton("Cancel", null)
				.setPositiveButton("Ok", null)
				.setView(inputFields)
				.create();
		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {

				Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
				okButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						try {
							String newDangerName = dangerInput.getText().toString();
							String newProtectionName = protectionInput.getText().toString();

							if (!newDangerName.isEmpty() && !newProtectionName.isEmpty()) {

								try {
									game.addDangerProtectionPair(newDangerName, newProtectionName);
								} catch (IllegalArgumentException e) {
									throw e;
								}

								updateAvailableElementsView();
								dialog.dismiss();
							} else {
								throw new IllegalArgumentException("No empty field(s) allowed!");
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

	public void onAddTreasureBtnClicked(View view) {
		final EditText treasureInput = new EditText(this);
		treasureInput.setHint("Treasure");

		final AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("Add: Treasure")
				.setMessage("Enter a Treasure. The field must not be empty or a name that " +
						"already exists")
				.setNegativeButton("Cancel", null)
				.setPositiveButton("Ok", null)
				.setView(treasureInput)
				.create();

		dialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {

				Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
				okButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						try {
							String treasureName = treasureInput.getText().toString();
							if (!treasureName.isEmpty()) {
								try {
									game.addTreasure(treasureName);
								} catch (IllegalArgumentException e) {
									throw e;
								}

								updateAvailableElementsView();
								dialog.dismiss();
							} else {
								throw new IllegalArgumentException("The field must not be empty!");
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

	public void onAddStoryCardBtnClicked(View view) {
		Toast.makeText(this, "Under construction", Toast.LENGTH_SHORT).show();
	}

	public void onPlaceStoryElementsBtnClicked(View view) {
		Intent intent = new Intent(this, PlaceStoryElementsActivity.class);
		this.startActivity(intent);
	}

	public void onPlayStoryGameBtnClicked(View view) {
		Toast.makeText(this, "Under construction", Toast.LENGTH_SHORT).show();
	}
}
