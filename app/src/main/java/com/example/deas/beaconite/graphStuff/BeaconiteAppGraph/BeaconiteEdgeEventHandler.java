package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import com.example.deas.beaconite.graphStuff.BeaconiteEdge;

import org.agp8x.android.lib.andrograph.model.defaults.EdgeEvent;

/**
 * Created by deas on 31/01/17.
 */

public class BeaconiteEdgeEventHandler<V, E> implements EdgeEvent<V, BeaconiteEdge> {
	@Override
	public boolean edgeSelected(V sourceV, V targetV, BeaconiteEdge edge) {
		// do stuff when edge selected:

		// return true so the edge won't be deleted! -> default behaviour from interface;
		return true;
	}
}
