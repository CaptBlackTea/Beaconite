package com.example.deas.beaconite.graphStuff.BeaconiteAppGraph;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.EdgeAttribute;

import org.jgrapht.graph.SimpleDirectedGraph;

/**
 * Created by deas on 16/02/17.
 */

public class LimitAccessEventHandler<V> extends BeaconiteVertexEventHandler {
	private SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph;

	public LimitAccessEventHandler(Activity activity, SimpleDirectedGraph<BeaconiteVertex,
			BeaconiteEdge> graph) {
		super(activity);
		this.graph = graph;
	}

	/**
	 * Adds incoming edges to the selected/given vertex from all vertices of the graph to which it
	 * has no edges so far. All added edges will be annotated with "MUSTNOT" Precondition: Directed
	 * graph, no loops (vertex has no edge to itself) will be made.
	 *
	 * @param vertex the selected vertex to add incoming edges to.
	 * @return true: the event that this vertex was selected was consumed.
	 */
	@Override
	public boolean vertexSelected(@Nullable BeaconiteVertex vertex) {

		if (graph != null) {
			// -1 because the given vertex is also part of the set
			int maxAmountOfInEdges = graph.vertexSet().size() - 1;
			System.out.println("maxAmountOfInEdges/vertexSet: " +
					maxAmountOfInEdges + " / " + graph.vertexSet().size());

			//only add edges if the given vertex is not connected to all vertices of the graph
			if (graph.inDegreeOf(vertex) < maxAmountOfInEdges) {
				for (BeaconiteVertex v : graph.vertexSet()) {
					if (!graph.containsEdge(v, vertex) && !v.equals(vertex)) {
						graph.addEdge(v, vertex).setAttribute(EdgeAttribute.MUSTNOT);
					}
				}
			}
		}

		return true;
	}
}
