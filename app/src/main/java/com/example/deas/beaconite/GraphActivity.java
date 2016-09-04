package com.example.deas.beaconite;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Draws Beacons in a graph to show how their visibility (RSSI value) changes overtime.
 *
 * grab data from service and pull out data needed to draw the graph
 * x-Axis: time(stamps)
 * y-Axis: Rssi
 * for each known Beacon a curve of its Rssi values overtime is drawn
 * draw the graph
 *
 * <p/>
 * Connects to the BeaconDataService to grab the data to be plotted.
 */
public class GraphActivity extends AppCompatActivity {

	private static final String TAG = "GraphActivity";
	private static int[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN,
			Color.MAGENTA, Color.YELLOW, Color.DKGRAY, Color.GRAY, Color.LTGRAY};
	private List<Integer> colors = new ArrayList<>();
	private Intent beaconDataServiceIntent = null;
	private boolean mIsBound = false;
	private BeaconDataService mService = null;
	private Map<Beacon, Map<Long, Integer>> dataToPlot;
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

			Log.d(TAG, "********** Service is connected. mService, mIsBound: " +
					mService
					+ ", " + mIsBound);

			Log.d(TAG, "AllMyBeacons: " + mService.getAllMyBeacons());
			drawGraph();

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mIsBound = false;
			Log.d(TAG, "********** SERVICE IS DISCONNECTED!");
		}
	};

	private static int getSeriesColor(int i) {
		if (i >= COLORS.length) {
			return Color.BLACK; // Default
		}
		return COLORS[i];
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);

		Log.d(TAG, "#### CREATE");
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to BeaconDataService
		bindService(beaconDataServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
		Log.d(TAG, "#### START: mIsBound: " + mIsBound + " mService: " + mService);
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
	protected void onDestroy() {
		super.onDestroy();
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

	private void drawGraph() {
		if (mIsBound) {
			dataToPlot = mService.getAllMyBeacons();
		}

		GraphView graph = (GraphView) findViewById(R.id.graph);
		int colorCounter = 0;
		if (dataToPlot != null && !dataToPlot.isEmpty()) {
			for (Map.Entry<Beacon, Map<Long, Integer>> entry : dataToPlot.entrySet()) {
				Beacon b = entry.getKey();
				Map<Long, Integer> timeRssiMap = entry.getValue();

				LineGraphSeries<DataPoint> serie = makeSerie(b, timeRssiMap);
				serie.setColor(getSeriesColor(colorCounter));
				graph.addSeries(serie);

				colorCounter++;
				Log.d(TAG, "Beacon " + b.getId2() + ": " + timeRssiMap.toString());
			}
		}
	}

	private LineGraphSeries<DataPoint> makeSerie(Beacon b, Map<Long, Integer> timeRssiMap) {
		LineGraphSeries<DataPoint> beaconSerie = new LineGraphSeries<>();

		// the graph library needs a sorted data structure!
		SortedMap<Long, Integer> sortedMap = new TreeMap<>(timeRssiMap);

		for (Map.Entry<Long, Integer> entry : sortedMap.entrySet()) {
			Long timestamp = entry.getKey();
			Integer rssi = entry.getValue();
			DataPoint dataPoint = new DataPoint(timestamp.doubleValue(), rssi.doubleValue());
			beaconSerie.appendData(dataPoint, true, 100);
		}

		beaconSerie.setDrawDataPoints(true);
		beaconSerie.setTitle("Beacon" + b.getId2());

		return beaconSerie;
	}





}
