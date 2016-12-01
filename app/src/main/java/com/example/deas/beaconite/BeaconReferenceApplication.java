package com.example.deas.beaconite;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.deas.beaconite.activities.MainActivity;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Currently OUT OF USE! Just here to have it in case the refactoring goes wrong and such an application comes in use.
 * Created by dea on 25/06/16.
 */
public class BeaconReferenceApplication extends Application implements BootstrapNotifier {
	private static final String TAG = "BeaconReferenceApp";
	private RegionBootstrap regionBootstrap;
	private BackgroundPowerSaver backgroundPowerSaver;
	private boolean haveDetectedBeaconsSinceBoot = false;
	private MainActivity mainActivity = null;


	public void onCreate() {
		super.onCreate();
		BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

		// By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
		// find a different type of beacon, you must specify the byte layout for that beacon's
		// advertisement with a line like below.  The example shows how to find a beacon with the
		// same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
		// layout expression for other beacon types, do a web search for "setBeaconLayout"
		// including the quotes.
		//
		//beaconManager.getBeaconParsers().clear();
		//beaconManager.getBeaconParsers().add(new BeaconParser().
		//        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

		Log.d(TAG, "setting up background monitoring for beacons and power saving");
		// wake up the app when a beacon is seen
		Region region = new Region("backgroundRegion",
				null, null, null);
		regionBootstrap = new RegionBootstrap(this, region);

		// simply constructing this class and holding a reference to it in your custom Application
		// class will automatically cause the BeaconLibrary to save battery whenever the application
		// is not visible.  This reduces bluetooth power usage by about 60%
		backgroundPowerSaver = new BackgroundPowerSaver(this);

		// If you wish to test beacon detection in the Android Emulator, you can use code like this:
//		 BeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
//		 ((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();

		BeaconManager.setBeaconSimulator(new MyBeaconsSimulator(4));
	}

	@Override
	public void didEnterRegion(Region arg0) {
		// In this example, this class sends a notification to the user whenever a Beacon
		// matching a Region (defined above) are first seen.
		Log.d(TAG, "did enter region.");
		if (!haveDetectedBeaconsSinceBoot) {
			Log.d(TAG, "auto launching MainActivity");

			// The very first time since boot that we detect an beacon, we launch the
			// MainActivity
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// Important:  make sure to add android:launchMode="singleInstance" in the manifest
			// to keep multiple copies of this activity from getting created if the user has
			// already manually launched the app.
			this.startActivity(intent);
			haveDetectedBeaconsSinceBoot = true;
		} else {
			if (mainActivity != null) {
				// If the Monitoring Activity is visible, we log info about the beacons we have
				// seen on its display
				mainActivity.logToDisplay("I see a beacon again");
			} else {
				// If we have already seen beacons before, but the monitoring activity is not in
				// the foreground, we send a notification to the user on subsequent detections.
				Log.d(TAG, "Sending notification.");
				sendNotification();
			}
		}


	}

	@Override
	public void didExitRegion(Region region) {
		if (mainActivity != null) {
			mainActivity.logToDisplay("I no longer see a beacon.");
		}
	}

	@Override
	public void didDetermineStateForRegion(int state, Region region) {
		if (mainActivity != null) {
			mainActivity.logToDisplay("I have just switched from seeing/not seeing beacons: " + state);
		}
	}

	private void sendNotification() {
		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(this)
						.setContentTitle("Beacon Reference Application")
						.setContentText("An beacon is nearby.")
						.setSmallIcon(R.drawable.ic_launcher);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(resultPendingIntent);
		NotificationManager notificationManager =
				(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, builder.build());
	}

	public void setMainActivity(MainActivity activity) {
		this.mainActivity = activity;
	}
}
