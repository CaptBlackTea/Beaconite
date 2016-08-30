package com.example.deas.beaconite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Draws Beacons in a graph to show how their visibility (RSSI value) changes overtime.
 * <p/>
 * Connects to the BeaconDataService to grab the data to be plotted.
 */
public class GraphActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
	}


	// bind to Service if this service was started.

	// grab data from service and pull out data needed to draw the graph
	// x-Axis: time(stamps)
	// y-Axis: Rssi
	// for each known Beacon a curve of its Rssi values overtime


	// draw the graph

}
