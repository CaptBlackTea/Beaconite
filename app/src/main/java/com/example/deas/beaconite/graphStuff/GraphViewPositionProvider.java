package com.example.deas.beaconite.graphStuff;

import org.agp8x.android.lib.andrograph.model.Coordinate;
import org.agp8x.android.lib.andrograph.model.defaults.MapPositionProvider;

import java.util.HashMap;

/**
 * Created by deas on 05/12/16.
 */

public class GraphViewPositionProvider<BeaconiteVertex> extends MapPositionProvider<BeaconiteVertex> {

	private int count;
	private Double defaultX;
	private Double defaultY;
	private Double defaultOffset;

	public GraphViewPositionProvider() {
		super(new HashMap<BeaconiteVertex, Coordinate>(), null);

		this.count = 0;
		this.defaultX = 0.1;
		this.defaultY = 0.1;
		this.defaultOffset = 0.05;

	}

	@Override
	public Coordinate getPosition(BeaconiteVertex vertex) {
		Coordinate coordinate = super.getPosition(vertex);

		if (coordinate == null) {
			coordinate = new Coordinate(this.defaultX, this.defaultY + this.defaultOffset * this.count);

			this.count++;

			setPosition(vertex, coordinate);
		}

		return coordinate;
	}
}
