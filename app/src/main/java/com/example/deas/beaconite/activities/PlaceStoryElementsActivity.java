package com.example.deas.beaconite.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.deas.beaconite.BeaconDataService;
import com.example.deas.beaconite.R;
import com.example.deas.beaconite.graphStuff.BeaconiteAppGraph.BeaconiteEdgePainterProvider;
import com.example.deas.beaconite.graphStuff.BeaconiteAppGraph.BeaconiteVertexPaintProvider;
import com.example.deas.beaconite.graphStuff.BeaconiteAppGraph.PlaceElementsEventHandler;
import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconitePermissionPolicy;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.EdgeAttribute;
import com.example.deas.beaconite.graphStuff.GraphColor;
import com.example.deas.beaconite.graphStuff.VertexAttribute;

import org.agp8x.android.lib.andrograph.model.EdgePaintProvider;
import org.agp8x.android.lib.andrograph.model.PermissionPolicy;
import org.agp8x.android.lib.andrograph.model.VertexPaintProvider;
import org.agp8x.android.lib.andrograph.model.defaults.DefaultGraphViewController;
import org.agp8x.android.lib.andrograph.model.defaults.StringVertexFactory;
import org.agp8x.android.lib.andrograph.view.GraphView;
import org.jgrapht.VertexFactory;

import java.util.List;
import java.util.Map;

public class PlaceStoryElementsActivity extends MenuActivity {

	protected static final String TAG = "PlaceStoryElementsAct.";

	// the Service
	private BeaconDataService mService;
	private boolean mIsBound = false;
	private Intent beaconDataServiceIntent;

	// the graph
	private GraphView<BeaconiteVertex, BeaconiteEdge> graphView;
	private DefaultGraphViewController<BeaconiteVertex, BeaconiteEdge> graphViewController;

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

		Map<EdgeAttribute, Paint> edgePaintMap = GraphColor.makeEdgePaintMap(this);
		Map<VertexAttribute, Paint> vertexPaintMap = GraphColor.makeVertexPaintMap(this);

		EdgePaintProvider<BeaconiteEdge> epp = new BeaconiteEdgePainterProvider<>(edgePaintMap);
		VertexPaintProvider<BeaconiteVertex> vpp = new BeaconiteVertexPaintProvider<>
				(vertexPaintMap);

		PermissionPolicy<BeaconiteVertex, BeaconiteEdge> pp = new BeaconitePermissionPolicy();

		// TODO: custom permission policy or other VertexFactory
		VertexFactory<BeaconiteVertex> vertexFactory = new StringVertexFactory<>();

		graphViewController = new DefaultGraphViewController<>
				(mService.getGraph(), mService.getPositionProvider(), epp, vpp, vertexFactory, pp);

		graphViewController.setVertexEventHandler(new PlaceElementsEventHandler(this));

		graphView.setController(graphViewController);
		graphView.invalidate();

		Log.d(TAG, "#### Graph: " + mService.getGraph());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph_relocation);
		// Find the toolbar view inside the activity layout
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		// Sets the Toolbar to act as the ActionBar for this Activity window.
		// Make sure the toolbar exists in the activity and is not null
		setSupportActionBar(toolbar);


		beaconDataServiceIntent = new Intent(this, BeaconDataService.class);

		Log.d(TAG, "#### CREATE");

		graphView = (GraphView<BeaconiteVertex, BeaconiteEdge>) findViewById(R.id.graphAddElements);

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

	public List<String> getAvailableElements() {
		// TODO: return the story elements from the game.
		// TODO: write method in service to get this info from the game -> EventHandler needs this
		return null;
	}
}
