package com.example.deas.beaconite;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

/**
 * Created by deas on 16/09/16.
 */
public class BeaconMap extends HashMap<Beacon, SortedMap<Long, Integer>> {

	public List<Integer> rssisForTimeintervals(Beacon beacon, List<TimeInterval> timeintervals) {
		List<Integer> rssis = new ArrayList<>();
		SortedMap<Long, Integer> tsRssiMap = this.get(beacon);

		for (TimeInterval t : timeintervals) {
			rssis.addAll(tsRssiMap.subMap(t.getStartTimestamp(), t.getStopTimestamp() + 1).values());
		}

		return rssis;
	}

}