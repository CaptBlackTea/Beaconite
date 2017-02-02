package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.graphics.Color;
import android.graphics.Paint;

import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.VertexAttribute;

import org.agp8x.android.lib.andrograph.model.Coordinate;
import org.agp8x.android.lib.andrograph.model.VertexPaintProvider;

import java.util.Map;

/**
 * Created by deas on 02/02/17.
 */
public class BeaconiteVertexPaintProvider<V> implements
		VertexPaintProvider<BeaconiteVertex> {

	private final Paint fallback;
	private final Paint selected;
	private final Paint labelPaint;
	private final Coordinate offset;
	private final Map<VertexAttribute, Paint> vertexPaintMap;

	public BeaconiteVertexPaintProvider(Map<VertexAttribute, Paint> vertexPaintMap) {
		this.vertexPaintMap = vertexPaintMap;

		fallback = new Paint();
		fallback.setColor(Color.RED);
		fallback.setStrokeWidth(5);
		fallback.setAntiAlias(true);

		selected = new Paint(fallback);
		selected.setColor(Color.GREEN);

		labelPaint = new Paint();
		labelPaint.setColor(Color.BLACK);
		labelPaint.setAntiAlias(true);
		labelPaint.setTextSize(25);
		labelPaint.setStrokeWidth(2);

		offset = new Coordinate(0.02, 0.03);
	}

	@Override
	public Paint getVertexPaint(BeaconiteVertex beaconiteVertex) {
		VertexAttribute vertexAttribute = beaconiteVertex.getAttribute();

		if (vertexPaintMap != null && vertexPaintMap.containsKey(vertexAttribute)) {
			return vertexPaintMap.get(vertexAttribute);
		}
		return fallback;
	}

	@Override
	public Paint getSelectedPaint(BeaconiteVertex beaconiteVertex) {
		return selected;
	}

	@Override
	public int getRadius(BeaconiteVertex beaconiteVertex) {
		return 25;
	}

	@Override
	public String getLabel(BeaconiteVertex beaconiteVertex) {
		return "" + beaconiteVertex.getName() + " / " + beaconiteVertex.getAttribute();
	}

	@Override
	public Paint getLabelPaint(BeaconiteVertex beaconiteVertex) {
		return labelPaint;
	}

	@Override
	public Coordinate getLabelOffset(BeaconiteVertex beaconiteVertex) {
		return offset;
	}
}
