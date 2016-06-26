package com.example.deas.beaconite;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class RangingActivity extends AppCompatActivity implements BeaconConsumer {
	protected static final String TAG = "RangingActivity";
	private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_ranging);
		setContentView(R.layout.activity_ranging_table);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

		beaconManager.bind(this);

		Log.i(TAG, "********* Ranging!");
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
		final Context rangingActivityContext = this;

		beaconManager.setRangeNotifier(new RangeNotifier() {
			@Override
			public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (beacons.size() > 0) {
							int index = 0;
							for (Beacon b : beacons) {
								index++;
//						logToDisplay("The " + index + ". beacon " + b.toString() + " is about " + b.getDistance() + " meters away.");

								TableLayout table = (TableLayout) findViewById(R.id.table);

								// check if an element with the UUID as tag of the beacon exists
								String bUuid = b.getId1().toString();
								Log.d(TAG, "Beacon UUID: " + bUuid);

								View elementWithUUID = table.findViewWithTag(bUuid);


								if (elementWithUUID == null) {
									// No View/TableRow exists with this UUID as id
									// create row
									TableRow row = new TableRow(rangingActivityContext);
									TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
									row.setLayoutParams(lp);
									row.setTag(bUuid);

									// add to TableLayout
									table.addView(row);


								}

								// there exists a row with this UUID as tag -> this beacon was seen before!
								// clear all child views of this row -> clear old data
								TableRow refreshRow = (TableRow) table.findViewWithTag(bUuid);
								refreshRow.removeAllViews();

								// create a TextView for each Beacon information: UUID, RSSI, Major, Minor, Accuracy, Distance
								TextView uuid = new TextView(rangingActivityContext);
								TextView rssi = new TextView(rangingActivityContext);
								TextView major = new TextView(rangingActivityContext);
								TextView minor = new TextView(rangingActivityContext);
								TextView accuracy = new TextView(rangingActivityContext);
								TextView distance = new TextView(rangingActivityContext);

								uuid.setEms(5);
								uuid.setText(b.getId1().toString());

								rssi.setText("" + b.getRssi());
								major.setText(b.getId2().toString());
								minor.setText(b.getId3().toString());

								accuracy.setText(calculateAccuracy(b.getDistance()));
								accuracy.setEms(5);

								distance.setText("" + b.getDistance());

								refreshRow.addView(uuid);
								refreshRow.addView(rssi);
								refreshRow.addView(major);
								refreshRow.addView(minor);
								refreshRow.addView(accuracy);
								refreshRow.addView(distance);

							}
							// code from the tutorial, but can only display the first beacon, therefore commented out
//					Beacon firstBeacon = beacons.iterator().next();
//					logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
						}

					}
				});

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

	private String calculateAccuracy(double distance) {
		if (distance == -1.0) {
			return "Unknown";
		} else if (distance < 1) {
			return "Immediate, under 1 meter";
		} else if (distance < 3) {
			return "Near, under 3 meter";
		} else {
			return "Far";
		}
	}
}
