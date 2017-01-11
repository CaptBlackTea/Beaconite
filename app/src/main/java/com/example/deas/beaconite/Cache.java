package com.example.deas.beaconite;

import com.example.deas.beaconite.graphStuff.BeaconiteVertex;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * A cache has two timestamps: start- and stop-record timestamp. A cache can have many of such
 * timestamp pairs. These timestamps can be use to later associate which Beacons belong in which
 * intensity (RSSI value at given time) to a cache. In this manner a cache should be recognisable
 * later on -> Fingerprinting
 * <p>
 * Created by dea on 04.09.2016.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class Cache {
	protected static final String TAG = "Cache";
	private final String name;
	private Fingerprint fingerprint;
	// Collection of all time intervals associated with this cache.
	private List<TimeInterval> timeIntervals;

	// @JsonBackReference is the back part of reference – it will be omitted from serialization.
	@JsonBackReference
	private BeaconiteVertex vertex;

	/**
	 * Sets up the cache with an empty data structure for time intervals and a name for this cache.
	 * Name must not be null or empty: throws IllegalArgumentException otherwise.
	 *
	 * @param name must not be null or an empty String. The name of the cache and it cannot be
	 *             changed (final field).
	 * @throws IllegalArgumentException if name was null or empty
	 */
	@JsonCreator
	public Cache(@JsonProperty("cacheName") String name) throws IllegalArgumentException {

		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException();
		}

		timeIntervals = new ArrayList<>();
		this.name = name;

		// FIXME: initial Fingerprint?
		calculateFingerprint(null);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Cache cache = (Cache) o;

//		if (!name.equals(cache.name)) return false;
//		if (fingerprint != null ? !fingerprint.equals(cache.fingerprint) : cache.fingerprint != null)
//			return false;
//		return timeIntervals != null ? timeIntervals.equals(cache.timeIntervals) : cache.timeIntervals == null;

		return this.name.equals(cache.name);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + (fingerprint != null ? fingerprint.hashCode() : 0);
		result = 31 * result + (timeIntervals != null ? timeIntervals.hashCode() : 0);
		return result;
	}

	public Fingerprint getFingerprint() {
		return fingerprint;
	}

	private void setFingerprint(Fingerprint fingerprint) {
		this.fingerprint = fingerprint;
	}

	/**
	 * Takes two values to create and add a time interval to a cache. Adds the given values as start
	 * and stop time to this cache. These two values create a time interval of the type
	 * TimeInterval.
	 *
	 * @param startTimestamp time value which represents the lower boundary of the time interval
	 *                       that will be created and added to this cache.
	 * @param stopTimestamp  time value which represents the upper boundary of the time interval
	 *                       that will be created and added to this cache.
	 */
	public void addNewTimestampPair(Long startTimestamp, Long stopTimestamp) {
		timeIntervals.add(new TimeInterval(startTimestamp, stopTimestamp));
	}

	/**
	 * Takes a interval of the type TimeInterval and adds it to the caches time intervals.
	 *
	 * @param interval the time interval to add to i.e. associate with this cache.
	 */
	@JsonSetter("timeInterval")
	public void addNewTimeInterval(TimeInterval interval) {
		timeIntervals.add(interval);
	}

	/**
	 * Returns the name of this cache.
	 *
	 * @return the name of this cache as a string.
	 */
	public String getCacheName() {
		return name;
	}

	/**
	 * Returns all time intervals associated with this cache.
	 *
	 * @return a list of TimeInterval objects stored for this cache.
	 */
	public List<TimeInterval> getTimeIntervals() {
		return timeIntervals;
	}

	@Override
	public String toString() {
		return this.name + " timestamp-intervals: " + timeIntervals.toString() + "; Beacons for " +
				"FP: " + fingerprint;
	}

	/**
	 * Calculates a fingerprint for this cache.
	 *
	 * @param allMyBeacons the data of which the fingerprint is created. Here all known Beacons.
	 */
	public void calculateFingerprint(BeaconMap allMyBeacons) {
		fingerprint = new FingerprintMedian(allMyBeacons, timeIntervals, null);
	}


	public void connectToVertex(BeaconiteVertex vertex) {
		if (this.vertex == null) {
			this.vertex = vertex;
			this.vertex.connectToCache(this);
		} else if (!this.vertex.equals(vertex)) {
			throw new RuntimeException("Cache is already connected to a vertex!");
		}
	}

	/**
	 * TODO: update!
	 *
	 * @param vertex
	 * @return true if the given vertex was disconnected from this cache false if given vertex is
	 * not the one this cache is connected to or null
	 */
	public boolean disconnectVertex(BeaconiteVertex vertex) {
		BeaconiteVertex v = this.vertex;

		if (v != null && v.equals(vertex)) {
			this.vertex = null;
			v.disconnectFromCache(this);
			return true;
		}

		// given vertex is not the one this cache is connected to or null
		return false;
	}


	public BeaconiteVertex getVertex() {
		return this.vertex;
	}

}
