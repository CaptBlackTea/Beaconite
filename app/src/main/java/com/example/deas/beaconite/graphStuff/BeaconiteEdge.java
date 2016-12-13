package com.example.deas.beaconite.graphStuff;

import org.jgrapht.graph.DefaultEdge;

import java.util.Map;

/**
 * Created by deas on 25/11/16.
 */
public class BeaconiteEdge<V> extends DefaultEdge {

	private V vertexSource;
	private V vertexTarget;
	//
	private EdgeAttribute attribute;

	public BeaconiteEdge(V vertexSource, V vertexTarget, EdgeAttribute attribute) {
		this.vertexSource = vertexSource;
		this.vertexTarget = vertexTarget;
		this.attribute = attribute;
	}

	public BeaconiteEdge() {
		this.attribute = EdgeAttribute.NONE;
	}

	public BeaconiteEdge(V vertexSource, V vertexTarget) {
		this.vertexSource = vertexSource;
		this.vertexTarget = vertexTarget;
		this.attribute = EdgeAttribute.NONE;
	}

	public V getVertexSource() {
		return vertexSource;
	}

	public V getVertexTarget() {
		return vertexTarget;
	}

	public EdgeAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(EdgeAttribute attribute) {
		this.attribute = attribute;
	}

	public String getName() {
		return vertexSource + "-" + vertexTarget + ": " + attribute.toString();
	}

	public String toString() {
		return attribute.toString();
	}


	public void updateEdgeAttributes(Map<String, String> attributes) {
		if (attributes.containsKey("attribute")) {
			EdgeAttribute attr = EdgeAttribute.valueOf(attributes.get("attribute"));
			setAttribute(attr);
		}
	}
}
