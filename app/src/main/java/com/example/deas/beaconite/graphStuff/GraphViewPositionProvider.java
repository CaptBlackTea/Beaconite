package com.example.deas.beaconite.graphStuff;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import org.agp8x.android.lib.andrograph.model.Coordinate;
import org.agp8x.android.lib.andrograph.model.defaults.MapPositionProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by deas on 05/12/16.
 */

public class GraphViewPositionProvider<BeaconiteVertex> extends
		MapPositionProvider<BeaconiteVertex> {

	private int count;
	private Double defaultX;
	private Double defaultY;
	private Double defaultOffset;
	private Map<String, Coordinate> jacksonPositionMap;

	public GraphViewPositionProvider() {
		super(new HashMap<BeaconiteVertex, Coordinate>(), null);

		this.count = 0;
		this.defaultX = 0.1;
		this.defaultY = 0.1;
		this.defaultOffset = 0.05;

	}

	public Map<String, Coordinate> getJacksonPositionMap() {
		return jacksonPositionMap;
	}

	// makes that the vertices in the graphView are displayed beneath each other
	@Override
	public Coordinate getPosition(BeaconiteVertex vertex) {
		convertJacksonEntry(vertex);

		Coordinate coordinate = super.getPosition(vertex);

		if (coordinate == null) {
			coordinate = new Coordinate(this.defaultX, this.defaultY + this.defaultOffset * this.count);

			this.count++;

			setPosition(vertex, coordinate);
		}

		return coordinate;
	}

	private void convertJacksonEntry(BeaconiteVertex vertex) {
		if (jacksonPositionMap != null) {
			String vertexName = ((com.example.deas.beaconite.graphStuff
					.BeaconiteVertex) vertex).getName();
			if (jacksonPositionMap.containsKey(vertexName)) {
				setPosition(vertex, jacksonPositionMap.remove(vertexName));
			}

			if (jacksonPositionMap.isEmpty()) {
				jacksonPositionMap = null;
			}

		}
	}

	@JsonSetter("positionMap")
	public void setAllPositions(Map<String, Coordinate> positionMap) {
		this.jacksonPositionMap = positionMap;
	}

	/**
	 * getter with Jackson annotation; returns a complete map TODO: fix docu!!
	 */
	@JsonGetter("positionMap")
	public Map<String, Coordinate> getPositionMap() {

		HashMap<String, Coordinate> jacksonPosMap = new HashMap<>();
		for (Map.Entry<BeaconiteVertex, Coordinate> entry : positionMap.entrySet()) {

			jacksonPosMap.put(((com.example.deas.beaconite.graphStuff.BeaconiteVertex) entry.getKey
					()).getName(), entry.getValue());
		}
		return jacksonPosMap;
	}
}
