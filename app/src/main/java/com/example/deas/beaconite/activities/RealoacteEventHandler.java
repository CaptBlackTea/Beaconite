package com.example.deas.beaconite.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.deas.beaconite.graphStuff.BeaconiteVertex;

import org.agp8x.android.lib.andrograph.model.VertexEvent;

/**
 * Created by deas on 06/02/17.
 */
public class RealoacteEventHandler<V> implements VertexEvent<BeaconiteVertex> {
	private final Activity activity;

	public RealoacteEventHandler(Activity activity) {
		this.activity = activity;
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
		Intent myIntent = new Intent(this.activity, GenerateCachesActivity.class);
		myIntent.putExtra("cache", vertex.getCache().getCacheName());
		this.activity.startActivity(myIntent);
		return true;
	}
}
