package com.example.deas.beaconite;

import java.util.ArrayList;
import java.util.List;

/**
 * A cache has two timestamps: start- and stop-record timestamp. A cache can have a lot of such timestamp pairs.
 * These timestamps can be use to later associate which Beacons belong in which intensity (RSSI value at given time) to a cache.
 * In this manner a cache should be recognisable later on -> Fingerprinting
 * Created by dea on 04.09.2016.
 */
public class Cache {
	protected static final String TAG = "Cache";
	private final String name;
	private Fingerprint fingerprint;

	// Collection of all time intervals associated with this cache.
	private List<TimeInterval> timeIntervals;

	// Sets up the cache with an empty data structure and a name for this cache.
	public Cache(String name) {
		timeIntervals = new ArrayList<>();
		this.name = name;
	}

	public void addNewTimestampPair(Long startTimestamp, Long stopTimestamp) {
		timeIntervals.add(new TimeInterval(startTimestamp, stopTimestamp));
	}

	public void addNewTimeInterval(TimeInterval interval) {
		timeIntervals.add(interval);
	}


	public String getCacheName() {
		return name;
	}

	public List<TimeInterval> getTimeIntervals() {
		return timeIntervals;
	}

	@Override
	public String toString() {
		return this.name + " timestamp-intervals: " + timeIntervals.toString();
	}

	public void calculateFingerprint(BeaconMap allMyBeacons) {
		fingerprint = new FingerprintMedian(allMyBeacons, timeIntervals);
	}
}
