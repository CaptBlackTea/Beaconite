package com.example.deas.beaconite;

import com.example.deas.beaconite.GameStuff.GameToken;
import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.EdgeAttribute;

import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * Takes a graph and generates a basic game based on the constraints annotated in this graph.
 * <p>
 * Created by deas on 07/02/17.
 */
public class BaseGame {

	private final SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph;

	public BaseGame(SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) {
		this.graph = graph;

		// each vertex gets tokes attributes needed to make a base game
		createGameVertices();
	}

	private void createGameVertices() {
		Map<String, GameToken> allPossibleVertices = new HashMap<>();
		for (BeaconiteVertex v : graph.vertexSet()) {
			allPossibleVertices.put(v.getId(), GameToken.UNKNOWN);
		}

		for (BeaconiteEdge edge : graph.edgeSet()) {

			BeaconiteVertex vertexSource = (BeaconiteVertex) edge.getVertexSource();
			BeaconiteVertex vertexTarget = (BeaconiteVertex) edge.getVertexTarget();

			if (vertexSource.getTokens().isEmpty()) {
				vertexSource.setTokens(allPossibleVertices);
			}
			if (edge.getAttribute().equals(EdgeAttribute.MUSTNOT)) {
				vertexSource.setToken(vertexTarget.getId(), GameToken.NO_ACCESS);
			} else {
				vertexSource.setToken(vertexTarget.getId(), GameToken.ACCESS);
			}
		}
	}


}
