package com.example.deas.beaconscannerapp;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class MonitoringActivity extends ActionBarActivity implements BeaconConsumer {
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;
    private BluetoothManager mBluetoothManager;
    private BluetoothLeScanner mBluetoothLeScanner;

    private TableRow foundRow;
    private TableRow uuidRow;
    private TableRow rssiRow;
    private TableRow majorRow;
    private TableRow minorRow;
    private TableRow accuracyRow;
    private TableRow distanceRow;

    private TextView foundText;
    private TextView uuidText;
    private TextView rssiText;
    private TextView majorText;
    private TextView minorText;
    private TextView accuracyText;
    private TextView distanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        initializeTable();

        // setup beacon manager
        beaconManager = BeaconManager.getInstanceForApplication(this);

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // the following is the iBeacon layout
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.bind(this);

        Log.i(TAG, "********* Here I am!");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    // when the start scanning button is pressed
    public void startScanningForBeacons(View view) {
        try {
            foundText.setText(getString(R.string.scanning));

            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueID", null, null, null));
            Log.i(TAG, "mmmmm startMonitoringBeacons was called");

            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            Log.i(TAG, "#### Ranging beacons was called");

        } catch (RemoteException e) {
            Log.i(TAG, "------ Exception!" + e);
        }

    }

    // when the stop scanning button is pressed
    public void stopScanningForBeacons(View view){

        try {
            foundText.setText(getString(R.string.not_scanning));
            beaconManager.stopMonitoringBeaconsInRegion(new Region("myMonitoringUniqueID", null, null, null));
            beaconManager.stopRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "++++ Service Connect was called");

        setupBeaconMonitoringActions();

        setupBeaconRangingActions();

    }

    private void setupBeaconMonitoringActions() {
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I saw a Beacon for the first time!");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        foundText.setText("Yes");
                    }
                });

            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see a Beacon.");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        foundText.setText("No");
                    }
                });
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I just switched from seeing / not seeing Beacons: " + state);
            }
        });
    }

    private void setupBeaconRangingActions() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                try {
                    if (beacons.size() > 0) {
                        for (final Beacon beacon : beacons) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    uuidText.setText("" + beacon.getId1());
                                    rssiText.setText("" + beacon.getRssi());
                                    majorText.setText("" + beacon.getId2());
                                    minorText.setText("" + beacon.getId3());
                                    distanceText.setText("" + beacon.getDistance());
                                    accuracyText.setText(calculateAccuracy(beacon.getDistance()));
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error was thrown: " + e.getMessage());
                }
            }
        });
    }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initializeTable() {

        foundRow = (TableRow) findViewById(R.id.foundRow);
        uuidRow = (TableRow) findViewById(R.id.uuidRow);
        rssiRow = (TableRow) findViewById(R.id.rssiRow);
        majorRow = (TableRow) findViewById(R.id.majorRow);
        minorRow = (TableRow) findViewById(R.id.minorRow);
        accuracyRow = (TableRow) findViewById(R.id.accuracyRow);
        distanceRow = (TableRow) findViewById(R.id.distanceRow);

        foundText = new TextView(this);
        foundText.setText(getString(R.string.not_scanning));
        uuidText = new TextView(this);
        rssiText = new TextView(this);
        majorText = new TextView(this);
        minorText = new TextView(this);
        accuracyText = new TextView(this);
        distanceText = new TextView(this);

        foundRow.addView(foundText);
        uuidRow.addView(uuidText);
        rssiRow.addView(rssiText);
        majorRow.addView(majorText);
        minorRow.addView(minorText);
        accuracyRow.addView(accuracyText);
        distanceRow.addView(distanceText);
    }



}
