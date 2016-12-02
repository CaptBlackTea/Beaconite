package com.example.deas.beaconite.graphStuff;

import com.example.deas.beaconite.Cache;

import org.jgrapht.ext.VertexUpdater;

import java.util.Map;

/**
 * A vertex is a node in a graph. It stores which edges belong to it.
 * <p>
 * Created by deas on 23/11/16.
 */

public class BeaconiteVertex implements VertexUpdater {

	private String name;

	// of what kind is this vertex? e.g. "treasure", "danger" or "protection"
	// TODO: change type to more specific VertexAttribute?
	private Enum attribute;

	// TODO! implement!
	// the cache connected to this vertex
	private Cache cache;

	public BeaconiteVertex(String name) {
		this.name = name;
		this.attribute = VertexAttribute.NONE;
	}

	// TODO: what for should the default constructor be??
	public BeaconiteVertex() {
	}

	public Enum getAttribute() {
		return attribute;
	}

	public void setAttribute(VertexAttribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * A vertex can contain a cache object which is returned by this method or null if no cache has
	 * been added.
	 *
	 * @return the cache associated with this vertex or null if no cache is associated with this
	 * vertex
	 */
	public Cache getCache() {
		return cache;
	}

	/**
	 * A vertex can contain a cache object. If the given cache is null the vertex is no longer
	 * associated with a cache.
	 *
	 * @param cache
	 */
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public String getName() {
		return name;
	}

	// TODO: should the name (=main identificator) be changable??
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "BeaconiteVertex{" +
				"name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BeaconiteVertex vertex = (BeaconiteVertex) o;

		return name != null ? name.equals(vertex.name) : vertex.name == null;

	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	@Override
	public void updateVertex(Object vertex, Map attributes) {
		// TODO: do something useful here later, when the vertex attributes get updated
		// e.g. when "treasure", "danger" or "protection" is assigned to a vertex
	}
}
