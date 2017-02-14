package com.example.deas.beaconite.GameStuff;

import com.example.deas.beaconite.Cache;
import com.example.deas.beaconite.graphStuff.BeaconiteEdge;
import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.example.deas.beaconite.graphStuff.EdgeAttribute;

import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Takes a graph and generates a basic game based on the constraints annotated in this graph.
 * <p>
 * Created by deas on 07/02/17.
 */
public class BaseGame {

	private final Player player;
	private SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph;
	private List<BeaconiteVertex> possibleTokens;

	/**
	 * Initial value: null. Is set to the current (last confirmed) cache/location the player is in.
	 */
	private Cache currentCache;


	/**
	 * Status of the game. Initial value is true. True: We have a state in which the game can
	 * proceed to a next round.
	 */
	private boolean proceedGame;
	private boolean gameFrozen;

	public BaseGame(SimpleDirectedGraph<BeaconiteVertex, BeaconiteEdge> graph) {
		this.graph = graph;
		player = new Player();
		possibleTokens = new ArrayList<>();
		possibleTokens.addAll(graph.vertexSet());

		// in the first startup of the base game the player has no location, therefore the
		// current cache is null.
		currentCache = null;
		proceedGame = true;

		player.fillTokenlist(possibleTokens);
	}

	public boolean goToVertex(BeaconiteVertex vertex) {
		return player.hasToken(vertex);
	}

	/**
	 * Take the given vertex and return a Set of vertices which are not allowed to be reached
	 * directly from the given vertex. I.e. all neighbors (vertices) of the given vertex to which an
	 * edge exists that is annotated with MUSTNOT.
	 *
	 * @param vertex the source vertex of an edge. If an egde is annotated with MUSTNOT its target
	 *               vertex is added to the returned set.
	 * @return a set of vertices that are forbidden to reach from the current vertex.
	 */
	public Set<BeaconiteVertex> noAccessVerticesOf(BeaconiteVertex vertex) {

		Set<BeaconiteVertex> vertices = new HashSet<>();

		for (BeaconiteEdge edge : graph.outgoingEdgesOf(vertex)) {
			if (edge.getAttribute().equals(EdgeAttribute.MUSTNOT)) {
				vertices.add((BeaconiteVertex) edge.getVertexTarget());
			}
		}

		return vertices;
	}

	/**
	 * Take the given vertex and return a Set of vertices which are allowed to be reached directly
	 * from the given vertex. I.e. all neighbors (vertices) of the given vertex to which an edge
	 * exists that is annotated with something else than MUSTNOT.
	 * <p>
	 * Corresponding method to @link{noAccessVerticesOf}.
	 *
	 * @param vertex the source vertex of an edge. If an egde is annotated with REQUIRED or NONE its
	 *               target vertex is added to the returned set.
	 * @return a set of vertices that are allowed to reach from the current vertex.
	 */
	public Set<BeaconiteVertex> accessVerticesOf(BeaconiteVertex vertex) {

		Set<BeaconiteVertex> nodes = new HashSet<>();

		for (BeaconiteEdge edge : graph.outgoingEdgesOf(vertex)) {
			if (!edge.getAttribute().equals(EdgeAttribute.MUSTNOT)) {
				nodes.add((BeaconiteVertex) edge.getVertexTarget());
			}
		}

		return nodes;
	}


	public List<String> getPlayerTokensNames() {
		List<String> tokenNames = new ArrayList<>();
		for (BeaconiteVertex vertex : player.getTokens()) {
			tokenNames.add(vertex.getName());
		}
		return tokenNames;
	}

	/**
	 * Confirmed that the player is in this cache. If the cache is null then the value of the
	 * currentCache field is not updated, so it stores the last confirmed location.
	 *
	 * @param currentCache confirmed location of the player; if null the currentCache value stays
	 *                     unchanged.
	 */
	public void setCurrentCache(Cache currentCache) {
		if (currentCache != null)
			this.currentCache = currentCache;
	}

	public void setProceedGame(boolean proceedGame) {
		this.proceedGame = proceedGame;
	}

	public boolean proceedGame() {
		return this.proceedGame;
	}

	/**
	 * Start a next game round. Fill up players tokens according to the cache the player is
	 * currently in.
	 */
	public void nextRound() {
		if (currentCache == null || gameFrozen) {
			return;
		}
		List<BeaconiteVertex> freshTokenList = new ArrayList<>(possibleTokens);
		freshTokenList.removeAll(noAccessVerticesOf(currentCache.getVertex()));
		player.fillTokenlist(freshTokenList);
	}

	public void unfreeze() {
		this.gameFrozen = false;
	}

	public void freeze() {
		this.gameFrozen = true;
	}
}
