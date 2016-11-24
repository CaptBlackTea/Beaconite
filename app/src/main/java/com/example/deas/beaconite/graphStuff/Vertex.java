package com.example.deas.beaconite.graphStuff;

/**
 * A vertex is a node in a graph. It stores which edges belong to it.
 * <p>
 * Created by deas on 23/11/16.
 */

public class Vertex {

	private String name;

	public Vertex(String name) {
		this.name = name;
	}

	public Vertex() {
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Vertex{" +
				"name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Vertex vertex = (Vertex) o;

		return name != null ? name.equals(vertex.name) : vertex.name == null;

	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
