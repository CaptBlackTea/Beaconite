package com.example.deas.beaconite.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.deas.beaconite.BeaconDataService;
import com.example.deas.beaconite.R;
import com.example.deas.beaconite.graphStuff.BeaconiteAppGraph.BeaconiteEdgeEventHandler;
import com.example.deas.beaconite.graphStuff.BeaconiteAppGraph.BeaconiteEdgePainterProvider;
import com.example.deas.beaconite.graphStuff.BeaconiteAppGraph.BeaconiteVertexEventHandler;
import com.example.deas.beaconite.graphStuff.BeaconiteAppGraph.BeaconiteVertexPaintProvider;
import com.example.deas.beaconite.graphStuff.BeaconiteAppGraph.LimitAccessEventHandler;
import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconitePermissionPolicy;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.EdgeAttribute;
import com.example.deas.beaconite.graphStuff.VertexAttribute;

import org.agp8x.android.lib.andrograph.model.EdgePaintProvider;
import org.agp8x.android.lib.andrograph.model.PermissionPolicy;
import org.agp8x.android.lib.andrograph.model.VertexPaintProvider;
import org.agp8x.android.lib.andrograph.model.defaults.DefaultGraphViewController;
import org.agp8x.android.lib.andrograph.model.defaults.RestrictedPermissionPolicy;
import org.agp8x.android.lib.andrograph.model.defaults.StringVertexFactory;
import org.agp8x.android.lib.andrograph.view.GraphView;
import org.jgrapht.VertexFactory;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.HashMap;
import java.util.Map;

public class GraphEditActivity extends MenuActivity {

	protected static final String TAG = "GraphEditActivity";

	// if true the graph cannot be edited or moved
	private boolean readOnly = false;

	// the Service
	private BeaconDataService mService;
	private boolean mIsBound = false;
	private Intent beaconDataServiceIntent;

	// the graph
	private GraphView<BeaconiteVertex, BeaconiteEdge> graphView;
	private DefaultGraphViewController<BeaconiteVertex, BeaconiteEdge> graphViewController;

	private SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph;
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

			graph = mService.getGraph();
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

		Map<EdgeAttribute, Paint> edgePaintMap = makeEdgePaintMap();
		Map<VertexAttribute, Paint> vertexPaintMap = makeVertexPaintMap();

		// TODO: implement later -> nice to have
//		makeEdgeColorLegend(edgePaintMap);

//		PositionProvider<BeaconiteVertex> positionProvider =  new GraphViewPositionProvider<>();
		EdgePaintProvider<BeaconiteEdge> epp = new BeaconiteEdgePainterProvider<>(edgePaintMap);
		VertexPaintProvider<BeaconiteVertex> vpp = new BeaconiteVertexPaintProvider<>
				(vertexPaintMap);

		PermissionPolicy<BeaconiteVertex, BeaconiteEdge> pp = new BeaconitePermissionPolicy();

		// if in read only mode disable movement:
		if (readOnly) {
			pp = new RestrictedPermissionPolicy<>();
		}

		// TODO: custom permission policy or other VertexFactory
		VertexFactory<BeaconiteVertex> vertexFactory = new StringVertexFactory<>();

		graphViewController = new DefaultGraphViewController<>
				(mService.getGraph(), mService.getPositionProvider(), epp, vpp, vertexFactory, pp);

		graphView.setController(graphViewController);
		graphView.invalidate();

		Log.d(TAG, "#### Graph: " + mService.getGraph());
	}

	// TODO: implement later -> nice to have
//	private void makeEdgeColorLegend(Map<EdgeAttribute, Paint> edgePaintMap) {
//		// Find the ListView resource.
//		ListView legend = (ListView) findViewById(R.id.edgeAttributesLegend);
//
//		// Create and populate the list with the edge attribute enums
//		Enum[] attributes = EdgeAttribute.values();
//
//		// Create ArrayAdapter using the array.
//		int colors;
//		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.edgecolors_legend, colors,
//				attributes);
//
//		// If you passed a String[] instead of a List<String>
//		// into the ArrayAdapter constructor, you must not add more items.
//		// Otherwise an exception will occur.
//
//		// Set the ArrayAdapter as the ListView's adapter.
//		legend.setAdapter( listAdapter );
//	}


	@NonNull
	private Map<EdgeAttribute, Paint> makeEdgePaintMap() {
		int strokeWidth = 5;
		Map<EdgeAttribute, Paint> paintHashMap = new HashMap<>();
		paintHashMap.put(EdgeAttribute.MUSTNOT, makePaint(R.color.red_500, strokeWidth));
		paintHashMap.put(EdgeAttribute.REQUIRED, makePaint(R.color.green_500, strokeWidth));
		paintHashMap.put(EdgeAttribute.NONE, makePaint(R.color.black, strokeWidth));
		return paintHashMap;
	}

	@NonNull
	private Map<VertexAttribute, Paint> makeVertexPaintMap() {
		int strokeWidth = 5;

		Map<VertexAttribute, Paint> paintHashMap = new HashMap<>();
		paintHashMap.put(VertexAttribute.DANGER, makePaint(R.color.purple_500, strokeWidth));
		paintHashMap.put(VertexAttribute.PROTECTION, makePaint(R.color.indigo_500, strokeWidth));
		paintHashMap.put(VertexAttribute.TREASURE, makePaint(R.color.amber_A400, strokeWidth));
		paintHashMap.put(VertexAttribute.NONE, makePaint(R.color.black, strokeWidth));
		return paintHashMap;
	}

	@NonNull
	private Paint makePaint(int color, int strokeWidth) {
		Paint paint = new Paint();
		paint.setColor(this.getResources().getColor(color));
		paint.setStrokeWidth(strokeWidth);
		return paint;
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


		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);

		Log.d(TAG, "#### CREATE");

		graphView = (GraphView<BeaconiteVertex, BeaconiteEdge>) findViewById(R.id.graphview);

//		final Switch creationSwitch = (Switch) findViewById(R.id.graphEditSwitch);
//		creationSwitch.setChecked(true);
//		creationSwitch.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				graphView.setInsertionMode(creationSwitch.isChecked());
//				creationSwitch.setChecked(graphView.isInsertionMode());
//			}
//		});

	}

	@Override
	protected void onStart() {
		super.onStart();

		// Bind to BeaconDataService
		bindService(beaconDataServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
		Log.d(TAG, "#### START: mIsBound: " + mIsBound + " mService: " + mService);


		Intent intent = getIntent();
		readOnly = intent.hasExtra("readOnly") && intent.getBooleanExtra("readOnly", false);

		if (readOnly) {
			View changeEditModeButtons = findViewById(R.id.changeEditMode);
			((ViewGroup) changeEditModeButtons.getParent()).removeView(changeEditModeButtons);
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


	public void onRadioButtonClicked(View view) {

		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch (view.getId()) {
			case R.id.modeMove:
				if (checked) {
					// vertices can be moved
					graphView.setInsertionMode(true);
					// just in case: reset the Handler to the default behaviour.
					graphViewController.setEdgeEventHandler(null);
					graphViewController.setVertexEventHandler(null);
				}
				break;
			case R.id.modeEditEdges:
				if (checked) {
					// make / delete edges
					graphView.setInsertionMode(false);
					// reset the edgeEventHandler to the default. adds/deletes edges if selected
					graphViewController.setEdgeEventHandler(null);
					graphViewController.setVertexEventHandler(null);
				}
				break;
			case R.id.modeAnnotateEdges:
				if (checked) {
					// annotate edges
					graphView.setInsertionMode(false);

					// use own EventHandler to manage the behaviour when an edge is selected in this
					// mode
					graphViewController.setEdgeEventHandler(new BeaconiteEdgeEventHandler(this));
					graphViewController.setVertexEventHandler(null);
				}
				break;
			case R.id.modeAnnotateVertex:
				if (checked) {
					Log.d(TAG, "Mode: Annotate Vertex");
					// annotate edges
					graphView.setInsertionMode(false);

					// use own EventHandler to manage the behaviour when an vertex is selected in
					// this mode
					graphViewController.setEdgeEventHandler(null);
					graphViewController.setVertexEventHandler(new BeaconiteVertexEventHandler
							(this));
				}
				break;
			case R.id.modeLimitAccess:
				if (checked) {
					Log.d(TAG, "Mode: Limit Access");
					// insert ingoing MUSTNOT edges to the selected vertex -> makes the vertex
					// "unreachable" from any vertex.
					graphView.setInsertionMode(false);

					// use own EventHandler to manage the behaviour when an vertex is selected in
					// this mode
					graphViewController.setEdgeEventHandler(null);
					graphViewController.setVertexEventHandler(new LimitAccessEventHandler(this,
							this.graph));
				}
				break;
		}

	}
}