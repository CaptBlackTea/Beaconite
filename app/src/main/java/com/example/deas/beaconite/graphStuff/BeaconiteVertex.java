package com.example.deas.beaconite.graphStuff;

import android.support.annotation.NonNull;

import com.example.deas.beaconite.Cache;
import com.example.deas.beaconite.GameStuff.GameToken;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.HashMap;
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
	private Map<String, GameToken> tokens = new HashMap<>();


	// the cache connected to this vertex
	// @JsonBackReference is the back part of reference – it will be omitted from serialization.
	@JsonBackReference
	private Cache cache = null;

	public BeaconiteVertex(String name) {
		this.name = name;

		// TODO: maybe generate id in another way somewhere else -> just for dot import testing!
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

	public BeaconiteVertex(String name, String id) {
		this.name = name;
		this.id = id;
		this.attribute = VertexAttribute.NONE;
	}

	public Map<String, GameToken> getTokens() {
		return tokens;
	}

	public void setTokens(Map<String, GameToken> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Add a token stating how this vertex is connected to an other vertex. No duplicates: if the
	 * given vertex id is already stored in this vertex it is not added again. If the vertex id is
	 * already stored its value (ACCESS) is updated to the given ACCESS value.
	 *
	 * @param vertexId  the vertex id of a connected vertex. Must not be null or empty, adding will
	 *                  be ignored in those cases.
	 * @param isAllowed true if the connection between this vertex and the given vertex id is
	 *                  allowed. false if there is a connection, but it is not allowed to go from
	 *                  this vertex to the given vertex id.
	 */
	public void setToken(@NonNull String vertexId, GameToken isAllowed) {
		if (vertexId != null && !vertexId.isEmpty()) {
			tokens.put(vertexId, isAllowed);
		}
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
	 * TODO: update! ... ???
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
	 * TODO: update! ... ???
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

	public String getName() {
		return name;
	}


	/**
	 * Set a name for this vertex. Must not be null or an empty String! Also renames the cache
	 * connected to this vertex.
	 *
	 * @param name the new name of the vertex
	 * @throws IllegalArgumentException
	 */
	public void setName(String name) throws IllegalArgumentException {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name must not be empty or null!");
		}
		this.name = name;

		if (this.cache != null && !cache.getCacheName().equals(this.name)) {
			this.cache.setName(this.name);
		}
	}

	@Override
	public String toString() {
		return "BeaconiteVertex{" +
				"name='" + name + '\'' +
				"id='" + id + '\'' +
				"attribute='" + attribute + '\'' +
				"cache='" + cache + '\'' +
				'}';
	}


	/**
	 * works with the vertex id instead of the name
	 * @param o the vertex that is being compared to this one.
	 * @return true if the vertices have the same id
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BeaconiteVertex vertex = (BeaconiteVertex) o;

//		return name != null ? name.equals(vertex.name) : vertex.name == null;
		return id != null ? id.equals(vertex.id) : vertex.id == null;

	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	public void updateVertex(Map<String, String> attributes) {
		// TODO: do something useful here later, when the vertex attributes get updated
		// e.g. when "treasure", "danger" or "protection" is assigned to a vertex

		// FIXME: add the new game attributes!
		if (attributes.containsKey("attribute")) {
			VertexAttribute attr = VertexAttribute.valueOf(attributes.get("attribute"));
			setAttribute(attr);
		}

	}



}
