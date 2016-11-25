package com.example.deas.beaconite.graphStuff;

import org.jgrapht.graph.DefaultEdge;

/**
 * Created by deas on 25/11/16.
 */
public class LabeledEdge<V> extends DefaultEdge {

	private V vertexSource;
	private V vertexTarget;
	private Enum label;

	public LabeledEdge(V vertexSource, V vertexTarget, Enum label) {
		this.vertexSource = vertexSource;
		this.vertexTarget = vertexTarget;
		this.label = label;
	}

	public LabeledEdge(V vertexSource, V vertexTarget) {
		this.vertexSource = vertexSource;
		this.vertexTarget = vertexTarget;
		this.label = EdgeLabel.NONE;
	}

	public V getVertexSource() {
		return vertexSource;
	}

	public V getVertexTarget() {
		return vertexTarget;
	}

	public Enum getLabel() {
		return label;
	}

	public String toString() {
		return label.toString();
	}

}
