package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.graphics.Color;
import android.graphics.Paint;

import org.agp8x.android.lib.andrograph.model.EdgePaintProvider;

/**
 * Created by deas on 19/01/17.
 */

public class BeaconiteEdgePainterProvider<E> implements EdgePaintProvider<E> {
	Paint fallback;
	Paint currentColor;

	public BeaconiteEdgePainterProvider() {
		fallback = new Paint();
		fallback.setColor(Color.BLACK);
		fallback.setAntiAlias(true);
	}

	@Override
	public Paint getEdgePaint(E edge) {
		return fallback;
	}

	public void setEdgePaint(Paint paint) {
		currentColor = paint;
	}
}
