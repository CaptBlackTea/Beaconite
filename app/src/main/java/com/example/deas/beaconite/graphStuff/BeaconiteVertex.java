package com.example.deas.beaconite.graphStuff;

import com.example.deas.beaconite.Cache;

import java.util.Map;

/**
 * A vertex is a node in a graph. It stores which edges belong to it.
 * <p>
 * Created by deas on 23/11/16.
 */

public class BeaconiteVertex {

	private String name;
	private String id;
	// of what kind is this vertex? e.g. "treasure", "danger" or "protection"
	private VertexAttribute attribute;
	// the cache connected to this vertex
	private Cache cache = null;


	public BeaconiteVertex(String name) {
		this.name = name;

		// FIXME: maybe generate id in another way somewhere else -> just for dot import testing!
		this.id = name + System.nanoTime();
		this.attribute = VertexAttribute.NONE;
	}

	// TODO: what for should the default constructor be??
	public BeaconiteVertex() {
	}

	public BeaconiteVertex(Cache cache) {
		this(cache.getCacheName());
//		this.cache = cache;
		connectToCache(cache);
	}

	// TODO: refactor the constructors so that there are less redundancies!
	public BeaconiteVertex(String name, String id) {
		this.name = name;
		this.id = id;
		this.attribute = VertexAttribute.NONE;
	}

	public String getId() {
		return id;
	}

	public VertexAttribute getAttribute() {
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
	 * TODO: update!
	 *
	 * @param cache
	 */
	public void connectToCache(Cache cache) {
		// make double reference when method is called so that we always have a connection!
		if (this.cache == null) {
			this.cache = cache;
			this.cache.connectToVertex(this);
		} else if (!this.cache.equals(cache)) {
			throw new RuntimeException("Vertex is already connected to a cache!");
		}

	}

	/**
	 * TODO: update!
	 *
	 * @param cache
	 * @return true if the given cache was disconnected from this vertex false if given cache is not
	 * the one this vertex is connected to or null
	 */
	public boolean disconnectFromCache(Cache cache) {
		Cache c = this.cache;

		if (c != null && c.equals(cache)) {
			this.cache = null;
			c.disconnectVertex(this);
			return true;
		}

		// given cache is not the one this vertex is connected to or null
		return false;
	}

	/**
	 * Returns true if the given cache was the the stored cache and was deleted(set null) False if
	 * the cache was not set null or null from the start.
	 *
	 * @param cache
	 * @return
	 */
	public boolean removeCache(Cache cache) {
		if (cache != null && cache.equals(this.cache)) {
//			cache.disconnectVertex(this);
			this.cache = null;
			return true;
		}

		return false;
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
				"id='" + id + '\'' +
				"attribute='" + attribute + '\'' +
				'}';
	}


	// FIXME: maybe equals should work with the vertex id instead of or in combination with the name
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

	public void updateVertex(Map<String, String> attributes) {
		// TODO: do something useful here later, when the vertex attributes get updated
		// e.g. when "treasure", "danger" or "protection" is assigned to a vertex

		if (attributes.containsKey("attribute")) {
			VertexAttribute attr = VertexAttribute.valueOf(attributes.get("attribute"));
			setAttribute(attr);
		}

	}


}
