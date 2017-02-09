package com.example.deas.beaconite.GameStuff;

import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.EdgeAttribute;

import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Takes a graph and generates a basic game based on the constraints annotated in this graph.
 * <p>
 * Created by deas on 07/02/17.
 */
public class BaseGame {

	private final Player player;
	private SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph;
	private List<String> possibleTokens;

	public BaseGame(SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) {
		this.graph = graph;
		player = new Player();
		possibleTokens = new ArrayList<>();

		// each vertex gets tokes attributes needed to make a base game
		createGameVertices(graph);

		player.fillTokenlist(possibleTokens);
	}

	// FIXME: Delete this method and according to it the datastructur in the Vertices etc!
	private void createGameVertices(SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) {
		Map<String, VertexAccess> allPossibleVertices = new HashMap<>();
		for (BeaconiteVertex v : graph.vertexSet()) {
			allPossibleVertices.put(v.getId(), VertexAccess.UNKNOWN);
			this.possibleTokens.add(v.getId());
		}

		for (BeaconiteEdge edge : graph.edgeSet()) {

			BeaconiteVertex vertexSource = (BeaconiteVertex) edge.getVertexSource();
			BeaconiteVertex vertexTarget = (BeaconiteVertex) edge.getVertexTarget();

			if (vertexSource.getTokens().isEmpty()) {
				vertexSource.setTokens(allPossibleVertices);
			}
			if (edge.getAttribute().equals(EdgeAttribute.MUSTNOT)) {
				vertexSource.setToken(vertexTarget.getId(), VertexAccess.NO_ACCESS);
			} else {
				vertexSource.setToken(vertexTarget.getId(), VertexAccess.ACCESS);
			}
		}
	}

	public boolean goToVertex(String vertexId) {
		return player.hasToken(vertexId);
	}

	// FIXME: refactor name and docu!

	/**
	 * Get all nodes from a vertex which should not be reachable.
	 *
	 * @param vertex
	 * @return
	 */
	public Set<BeaconiteVertex> forbiddenNodesOf(BeaconiteVertex vertex) {

		Set<BeaconiteVertex> nodes = new HashSet<>();

		for (BeaconiteEdge edge : graph.outgoingEdgesOf(vertex)) {
			if (edge.getAttribute().equals(EdgeAttribute.MUSTNOT)) {
				nodes.add((BeaconiteVertex) edge.getVertexTarget());
			}
		}

		return nodes;
	}

}
