package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.graphics.Color;
import android.graphics.Paint;

import com.example.deas.beaconite.graphStuff.EdgeAttribute;

import org.agp8x.android.lib.andrograph.model.EdgePaintProvider;

import java.util.Map;

/**
 * Created by deas on 19/01/17.
 */

public class BeaconiteEdgePainterProvider<BeaconiteEdge> implements EdgePaintProvider<BeaconiteEdge> {
	protected static final String TAG = "BeaconiteEdgePaintProv";
	private final Map<EdgeAttribute, Paint> edgePaintMap;
	Paint fallback;

	public BeaconiteEdgePainterProvider(Map<EdgeAttribute, Paint> edgePaintMap) {
		this.edgePaintMap = edgePaintMap;
		fallback = new Paint();
		fallback.setColor(Color.BLACK);
		fallback.setAntiAlias(true);
	}

	@Override
	public Paint getEdgePaint(BeaconiteEdge edge) {
		EdgeAttribute edgeAttribute = ((com.example.deas
				.beaconite.graphStuff.BeaconiteEdge) edge).getAttribute();

		if (edgePaintMap != null && edgePaintMap.containsKey(edgeAttribute)) {
			return edgePaintMap.get(edgeAttribute);
		}

		return fallback;
	}

}
