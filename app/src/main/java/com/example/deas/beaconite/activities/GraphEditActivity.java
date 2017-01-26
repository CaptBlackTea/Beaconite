package com.example.deas.beaconite.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;

import com.example.deas.beaconite.BeaconDataService;
import com.example.deas.beaconite.R;
import com.example.deas.beaconite.dataIO.DOTSettings;
import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconitePermissionPolicy;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;

import org.agp8x.android.lib.andrograph.model.EdgePaintProvider;
import org.agp8x.android.lib.andrograph.model.GraphViewController;
import org.agp8x.android.lib.andrograph.model.PermissionPolicy;
import org.agp8x.android.lib.andrograph.model.VertexPaintProvider;
import org.agp8x.android.lib.andrograph.model.defaults.DefaultEdgePaintProvider;
import org.agp8x.android.lib.andrograph.model.defaults.DefaultGraphViewController;
import org.agp8x.android.lib.andrograph.model.defaults.DefaultVertexPaintProvider;
import org.agp8x.android.lib.andrograph.model.defaults.StringVertexFactory;
import org.agp8x.android.lib.andrograph.view.GraphView;
import org.jgrapht.VertexFactory;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.io.StringWriter;

public class GraphEditActivity extends AppCompatActivity {

	protected static final String TAG = "GraphEditActivity";

	// the Service
	private BeaconDataService mService;
	private boolean mIsBound = false;
	private Intent beaconDataServiceIntent;

	// the graph
//	private SimpleGraph<String, DefaultEdge> graph;
//	private TextView textView;
	private GraphView<BeaconiteVertex, BeaconiteEdge> graphView;

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

			// TODO
			// setup or update graph view
			setupGraphView();

			Log.d(TAG, "------ on Service connected was called. mService, mIsBound: " + mService
					+ ", " + mIsBound);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mIsBound = false;
			Log.d(TAG, "********** SERVICE WAS DISCONNECTED!");
		}
	};

	private void setupGraphView() {

//		PositionProvider<BeaconiteVertex> positionProvider =  new GraphViewPositionProvider<>();
		EdgePaintProvider<BeaconiteEdge> epp = new DefaultEdgePaintProvider<>();
		VertexPaintProvider<BeaconiteVertex> vpp = new DefaultVertexPaintProvider<>();

		PermissionPolicy<BeaconiteVertex, BeaconiteEdge> pp = new BeaconitePermissionPolicy();

		// TODO: custom permission policy or other VertexFactory
		VertexFactory<BeaconiteVertex> vertexFactory = new StringVertexFactory<>();

		GraphViewController<BeaconiteVertex, BeaconiteEdge> graphViewController = new
				DefaultGraphViewController<>
				(mService.getGraph(), mService.getPositionProvider(), epp, vpp, vertexFactory, pp);

		graphView.setController(graphViewController);
		graphView.invalidate();

		Log.d(TAG, "#### Graph: " + mService.getGraph());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_edit_activty);
		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);


		Log.d(TAG, "onStart; toolbar: " + toolbar);

		// Get a support ActionBar corresponding to this toolbar
		ActionBar ab = getSupportActionBar();

		Log.d(TAG, "getSupportActionBar: " + ab);
		// Enable the Up button
		if (ab != null) {
			Log.d(TAG, "YAY!");
			ab.setDisplayHomeAsUpEnabled(true);
		}

		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);

		Log.d(TAG, "#### CREATE");

//		textView = (TextView) findViewById(R.id.textview);

//		graph = TestData.getStringDefaultEdgeSimpleGraph();

//		textView.setText(graphToDot(graph));

//		graphView = (BeaconiteGraphView<BeaconiteVertex, BeaconiteEdge>) findViewById(R.id.graphview);
		graphView = (GraphView<BeaconiteVertex, BeaconiteEdge>) findViewById(R.id.graphview);

//		PositionProvider<String> positionProvider = new MapPositionProvider<>(TestData.getStringDefaultEdgeSimpleGraphPositions(), new Coordinate(0.5, 0.8));
//		EdgePaintProvider<DefaultEdge> epp = new DefaultEdgePaintProvider<>();
//		VertexPaintProvider<String> vpp = new DefaultVertexPaintProvider<>();
//
//		VertexFactory<String> vf = new StringVertexFactory<>();
//
//		PermissionPolicy<String, DefaultEdge> pp = new DefaultPermissionPolicy<>();
//		//pp=new RestrictedPermissionPolicy<>();
//
//		GraphViewController<String, DefaultEdge> gvc = new DefaultGraphViewController<>(graph, positionProvider, epp, vpp, vf, pp);
//
//		graphView.setController(gvc);

		final Switch creationSwitch = (Switch) findViewById(R.id.graphEditSwitch);
		creationSwitch.setChecked(true);
		creationSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				graphView.setInsertionMode(creationSwitch.isChecked());
				creationSwitch.setChecked(graphView.isInsertionMode());
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mIsBound) { // TODO: What should be tested here??
//			if (mIsBound && textView != null) {
//				textView.setText(graphToDot(mService.getGraph()));
//			}
		}
		return super.onTouchEvent(event);


	}

	@Override
	protected void onStart() {
		super.onStart();

		// Bind to BeaconDataService
		bindService(beaconDataServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
		Log.d(TAG, "#### START: mIsBound: " + mIsBound + " mService: " + mService);
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

	private String graphToDot(SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) {

		DOTExporter<BeaconiteVertex, BeaconiteEdge> exporter = DOTSettings.makeDOTExporter();
		StringWriter stringWriter = new StringWriter();
		exporter.export(stringWriter, graph);

		return stringWriter.toString();

	}
}