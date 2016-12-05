package com.example.deas.beaconite.graphStuff;

import org.agp8x.android.lib.andrograph.model.defaults.DefaultPermissionPolicy;

/**
 * Created by deas on 05/12/16.
 */
public class BeaconitePermissionPolicy<BeaconiteVertex, BeaconiteEdge> extends
		DefaultPermissionPolicy {

	@Override
	public boolean allowVertexInsertion() {
		return false;
	}
}
