package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.deas.beaconite.activities.GenerateCachesActivity;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;

/**
 * Created by deas on 06/02/17.
 */
public class RealoacteEventHandler<V> extends BeaconiteVertexEventHandler {

	public RealoacteEventHandler(Activity activity) {
		super(activity);
	}

	/**
	 * Handles what happens if a vertex is selected. In the relocation mode the handler triggers,
	 * that the RecordCachesActivity is opened and the given Vertex is set to be recorded.
	 *
	 * @param vertex the selected vertex
	 * @return true: the event that this vertex was selected was consumed.
	 */
	@Override
	public boolean vertexSelected(@Nullable final BeaconiteVertex vertex) {
		Intent myIntent = new Intent(activity, GenerateCachesActivity.class);
		myIntent.putExtra("cache", vertex.getCache().getCacheName());
		activity.startActivity(myIntent);
		return true;
	}
}
