package com.example.deas.beaconite;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.altbeacon.beacon.Beacon;

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
 *
 * Cache series are plotted on the same line due to good visibility when plotting two graphs at once
 * -> cache series are displayed together with the beacon series.
 * This line is set to -62,5 by default (this is the middle of possible rssi values: -25 to -100)
 * The value is reachable and changeable via a getter and setter.
 */
public class GraphActivity extends AppCompatActivity {

	private static final String TAG = "GraphActivity";
	private static int[] COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN,
			Color.MAGENTA, Color.YELLOW, Color.DKGRAY, Color.GRAY, Color.LTGRAY};
	private Intent beaconDataServiceIntent = null;
	private boolean mIsBound = false;
	private BeaconDataService mService = null;
	private Map<Beacon, Map<Long, Integer>> dataToPlot;
	private List<Cache> cachesToPlot;

	// value is the line all caches shall be displayed on
	private double yValueCacheSeries = -62.5;

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

	public double getyValueCacheSeries() {
		return yValueCacheSeries;
	}

	public void setyValueCacheSeries(double yValueCacheSeries) {
		this.yValueCacheSeries = yValueCacheSeries;
	}

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
			cachesToPlot = mService.getAllMyCaches();
		}

		GraphView graph = (GraphView) findViewById(R.id.graph);
		int colorCounter = 0;
		if (dataToPlot != null && !dataToPlot.isEmpty()) {
			for (Map.Entry<Beacon, Map<Long, Integer>> entry : dataToPlot.entrySet()) {
				Beacon b = entry.getKey();
				Map<Long, Integer> timeRssiMap = entry.getValue();

				LineGraphSeries<DataPoint> serieBeacons = makeLineSerie(b, timeRssiMap);
				serieBeacons.setColor(getSeriesColor(colorCounter));
				graph.addSeries(serieBeacons);

				colorCounter++;
				Log.d(TAG, "Beacon " + b.getId2() + ": " + timeRssiMap.toString());
			}
		}

		// make a serie out of caches
		colorCounter = 0;
		if (cachesToPlot != null && !cachesToPlot.isEmpty()) {
			for (Cache c : cachesToPlot) {
				// for each cache make 2 series: first one holds all begin timestamps of this cache and all get the same symbol
				// second one holds all end timestamps of this cache and all get the same symbol (distinct to the start symbol)
				PointsGraphSeries<DataPoint> serieCachesBegins = makePointSerie(c, true);
				PointsGraphSeries<DataPoint> serieCachesEnds = makePointSerie(c, false);

				// but both series get the same color because they belong to the same cache
				serieCachesBegins.setColor(getSeriesColor(colorCounter));
				serieCachesEnds.setColor(getSeriesColor(colorCounter));

				graph.addSeries(serieCachesBegins);
				graph.addSeries(serieCachesEnds);

				colorCounter++;
			}
		}
	}

	private LineGraphSeries<DataPoint> makeLineSerie(Beacon b, Map<Long, Integer> timeRssiMap) {
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

	/**
	 * DEPRECATED! Plotting caches (begin/end timestamp) as a bar graph is impractical...remove at some point
	 *
	 * @param cache
	 * @return
	 */
	private BarGraphSeries<DataPoint> makeBarSerie(Cache cache) {
		BarGraphSeries<DataPoint> cacheSerie = new BarGraphSeries<>();
		SortedMap<Long, Long> sortedMap;

		// the graph library needs a sorted data structure!
		if (!(cache.getTimestampPairs() instanceof SortedMap)) {
			sortedMap = new TreeMap<>(cache.getTimestampPairs());
		} else {
			sortedMap = cache.getTimestampPairs();
		}

		for (Map.Entry<Long, Long> entry : sortedMap.entrySet()) {
			Long startTimestamp = entry.getKey();
			Long stopTimestamp = entry.getValue();
			DataPoint dataPoint = new DataPoint(startTimestamp.doubleValue(), stopTimestamp.doubleValue());
			cacheSerie.appendData(dataPoint, true, 100);
		}

		cacheSerie.setTitle(cache.getCacheName());

		return cacheSerie;
	}


	/**
	 * Plot a cache as a graph: x-value=timestamp, it uses the current y-value (default is -62,5: the middle of possible rssi values, range from -25 to -100)
	 * so all points are on the same line.
	 * A point shape is set for a start timestamp x-value and a cross shape is set for an end timestamp x-value.
	 *
	 * @param cache             the cache from which data (start and endtimestamps) is to be transferred to a point serie.
	 * @param isStarttimestamp: true if this series has to express all starttimestamps of the given cache
	 *                          false if it has to express all endtimestamps
	 * @return a point graph serie representation of the given cache data
	 */
	private PointsGraphSeries<DataPoint> makePointSerie(Cache cache, boolean isStarttimestamp) {
		PointsGraphSeries<DataPoint> cacheSerie = new PointsGraphSeries<>();

		if (isStarttimestamp) {
			cacheSerie.setShape(PointsGraphSeries.Shape.POINT);
		} else {
			cacheSerie.setCustomShape(makeCrossShape());
		}

		SortedMap<Long, Long> sortedMap;

		// the graph library needs a sorted data structure!
		if (!(cache.getTimestampPairs() instanceof SortedMap)) {
			sortedMap = new TreeMap<>(cache.getTimestampPairs());
		} else {
			sortedMap = cache.getTimestampPairs();
		}

		for (Map.Entry<Long, Long> entry : sortedMap.entrySet()) {

			if (isStarttimestamp) {
				Long startTimestamp = entry.getKey();
				DataPoint dataPoint = new DataPoint(startTimestamp.doubleValue(), yValueCacheSeries);
				cacheSerie.appendData(dataPoint, true, 100);
			} else {
				Long stopTimestamp = entry.getValue();
				DataPoint dataCross = new DataPoint(stopTimestamp.doubleValue(), yValueCacheSeries);
				cacheSerie.appendData(dataCross, true, 100);
			}
		}

		return cacheSerie;
	}

	private PointsGraphSeries.CustomShape makeCrossShape() {
		return new PointsGraphSeries.CustomShape() {
			@Override
			public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
				paint.setStrokeWidth(10);
				canvas.drawLine(x - 20, y - 20, x + 20, y + 20, paint);
				canvas.drawLine(x+20, y-20, x-20, y+20, paint);
			}
		};
	}


}
