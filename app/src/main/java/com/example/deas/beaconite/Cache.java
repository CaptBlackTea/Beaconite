package com.example.deas.beaconite;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A cache has two timestamps: start- and stop-record timestamp. A cache can have a lot of such timestamp pairs.
 * These timestamps can be use to later associate which Beacons belong in which intensity (RSSI value at given time) to a cache.
 * In this manner a cache should be recognisable later on -> Fingerprinting
 * Created by dea on 04.09.2016.
 */
public class Cache {
	protected static final String TAG = "Cache";
	private final String name;

	// Collection of all time intervals associated with this cache.
	private SortedMap<Long, Long> timestampPairs;

	// Sets up the cache with an empty data structure and a name for this cache.
	public Cache(String name) {
		timestampPairs = new TreeMap<>();
		this.name = name;
	}

	public void addNewTimestampPair(Long startTimestamp, Long stopTimestamp) {
		timestampPairs.put(startTimestamp, stopTimestamp);
	}

	public String getCacheName() {
		return name;
	}

	public SortedMap<Long, Long> getTimestampPairs() {
		return timestampPairs;
	}

	@Override
	public String toString() {
		return this.name + " timestamp-intervals: " + timestampPairs.toString();
	}
}
