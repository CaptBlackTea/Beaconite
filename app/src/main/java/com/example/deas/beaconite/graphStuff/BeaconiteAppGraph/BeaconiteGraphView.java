package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Pair;

import org.agp8x.android.lib.andrograph.view.GraphView;
import org.jgrapht.graph.DefaultEdge;

/**
 * Created by deas on 25/01/17.
 */

public class BeaconiteGraphView<V, E extends DefaultEdge> extends GraphView<V, E> {
	public BeaconiteGraphView(Context context) {
		super(context);
	}

	public BeaconiteGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BeaconiteGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void drawEdge(Canvas canvas, E edge, V edgeSource, V edgeTarget) {
		drawDirectedEdge(canvas, edge, edgeSource, edgeTarget);
	}

	public void drawDirectedEdge(Canvas canvas, E edge, V edgeSource, V edgeTarget) {
		// draw the edge
		super.drawEdge(canvas, edge, edgeSource, edgeTarget);

		// to draw the arrow head to this edge:
		// l: length of the edge
		// r: vector of size 1 of this edge
		// arrowHeadBaseCoord: the point in the length r from the edgeTarget
		// rOrth: orthogonal vector of r
		// arrowLeftTip: endpoint of one side of an arrow
		// arrowRightTip: endpoint of one other side of an arrow
		// arrow head: line between target and V, line between target and W

		// l = |targetCoordinates - sourceCoordinates| -> l = number
		double edgeLength = calcEdgeLength();

		// r = 1/l * (targetCoordinates - sourceCoordinates) -> r = (rx, ry)
		// r has the length 1, multiply with a factor to make it big enough to be seen!
		int factor = 100;
		Pair<Float, Float> r = calcR(edgeLength, factor);


		// rOrthogonal = (ry, -rx)
		Pair<Float, Float> rOrth = new Pair<>(r.second, -r.first);

		// arrowHeadBaseCoord = targetCoordinates + r -> Coordinate Pair (ux, uy)
		Pair<Float, Float> arrowHeadBaseCoord = new Pair<>(edgeStyle.xy2.first + r.first,
				edgeStyle.xy2.second + r.second);

		// arrowLeftTip = arrowHeadBaseCoord + rOrthogonal -> Coordinate Pair (vx, vy)
		Pair<Float, Float> arrowLeftTip = new Pair<>(arrowHeadBaseCoord.first + rOrth.first, arrowHeadBaseCoord
				.second + rOrth.second);

		// arrowRightTip = arrowHeadBaseCoord - rOrthogonal -> Coordinate Pair (wx, wy)
		Pair<Float, Float> arrowRightTip = new Pair<>(arrowHeadBaseCoord.first - rOrth.first,
				arrowHeadBaseCoord
						.second - rOrth.second);

		//draw
		canvas.drawLine(
				edgeStyle.xy2.first,
				edgeStyle.xy2.second,
				arrowLeftTip.first,
				arrowLeftTip.second,
				controller.getEdgePaint(edge));

		canvas.drawLine(
				edgeStyle.xy2.first,
				edgeStyle.xy2.second,
				arrowRightTip.first,
				arrowRightTip.second,
				controller.getEdgePaint(edge));
	}

	private Pair<Float, Float> calcR(double edgeLength, int factor) {
		// r = 1/l * (targetCoordinates - sourceCoordinates) -> r = (rx, ry)
		double dx = edgeStyle.xy1.first - edgeStyle.xy2.first;

		double dy = edgeStyle.xy1.second - edgeStyle.xy2.second;

		double rx = 1 / edgeLength * dx;
		double ry = 1 / edgeLength * dy;

		return new Pair<>(new Float(factor * rx), new Float(factor * ry));
	}

	private double calcEdgeLength() {
		// l = |targetCoordinates - sourceCoordinates| -> l = number

		double dx = edgeStyle.xy1.first - edgeStyle.xy2.first;

		double dy = edgeStyle.xy1.second - edgeStyle.xy2.second;

		// remember: sqrt returns only positive result, therefore no .abs needed!
		return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
	}

	private void calculateArrow(Pair<Float, Float> source, Pair<Float, Float> target) {
		// l = |targetCoordinates - sourceCoordinates| -> l = number
		// r = 1/l * (targetCoordinates - sourceCoordinates) -> r = (rx, ry)

		// rOrthogonal = (ry, -rx)
		// U = targetCoordinates - r -> U = (ux, uy)

		// V = U + rOrthogonal -> V = (vx, vy)
		// W = U - rOrthogonal -> W = (wx, wy)


	}

}
