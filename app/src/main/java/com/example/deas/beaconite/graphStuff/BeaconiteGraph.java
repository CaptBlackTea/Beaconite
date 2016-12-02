package com.example.deas.beaconite.graphStuff;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleGraph;

/**
 * Data structure representing an undirected graph. Such a graph has vertices (v) and edges (e).
 * <p>
 * Created by deas on 01/12/16.
 */

public class BeaconiteGraph {

	public BeaconiteGraph() {

		UndirectedGraph<BeaconiteVertex, BeaconiteEdge> graph = new SimpleGraph<>(new
				ClassBasedEdgeFactory<BeaconiteVertex, BeaconiteEdge>(BeaconiteEdge.class));
	}
}
