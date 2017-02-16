package com.example.deas.beaconite.graphStuff;

import android.app.Activity;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.example.deas.beaconite.R;

import java.util.HashMap;
import java.util.Map;

public class GraphColor {

	@NonNull
	public static Map<EdgeAttribute, Paint> makeEdgePaintMap(Activity activity) {
		int strokeWidth = 5;
		Map<EdgeAttribute, Paint> paintHashMap = new HashMap<EdgeAttribute, Paint>();
		paintHashMap.put(EdgeAttribute.MUSTNOT, makePaint(R.color.red_500, strokeWidth, activity));
		paintHashMap.put(EdgeAttribute.REQUIRED, makePaint(R.color.green_500, strokeWidth, activity));
		paintHashMap.put(EdgeAttribute.NONE, makePaint(R.color.black, strokeWidth, activity));
		return paintHashMap;
	}

	@NonNull
	public static Map<VertexAttribute, Paint> makeVertexPaintMap(Activity activity) {
		int strokeWidth = 5;

		Map<VertexAttribute, Paint> paintHashMap = new HashMap<VertexAttribute, Paint>();
		paintHashMap.put(VertexAttribute.DANGER, makePaint(R.color.purple_500, strokeWidth, activity));
		paintHashMap.put(VertexAttribute.PROTECTION, makePaint(R.color.indigo_500, strokeWidth, activity));
		paintHashMap.put(VertexAttribute.TREASURE, makePaint(R.color.amber_A400, strokeWidth, activity));
		paintHashMap.put(VertexAttribute.NONE, makePaint(R.color.black, strokeWidth, activity));
		return paintHashMap;
	}

	@NonNull
	private static Paint makePaint(int color, int strokeWidth, Activity activity) {
		Paint paint = new Paint();
		paint.setColor(activity.getResources().getColor(color));
		paint.setStrokeWidth(strokeWidth);
		return paint;
	}
}