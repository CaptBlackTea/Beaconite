package com.example.deas.beaconite;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

/**
 * A special/customized map for connecting a Beacon with its rssi values i.e. its visibility to the
 * device.
 * <p>
 * Created by deas on 16/09/16.
 */
public class BeaconMap extends HashMap<Beacon, SortedMap<Long, Integer>> {

	/**
	 * Special map method for this special map. Takes a Beacon and a List of TimeInterval objects.
	 * (Remember: in a BeaconMap a Beacon is connected with timestamp-rssi value pairs!) The method
	 * then traverses the Beacons stored timestamp-rssi pairs and returns a list containing only
	 * rssi values of this Beacon that occurred in the bounds of the given timeintervals List.
	 *
	 * @param beacon        the beacon to get filtered rssi values from
	 * @param timeintervals a List of TimeIntervals which represent the boundaries for the relevant
	 *                      rssi values of the given Beacon.
	 * @return a list containing only such rssi values, as Integers, of the given beacon that
	 * occurred in between the given timeintervals.
	 */
	public List<Integer> rssisForTimeintervals(Beacon beacon, List<TimeInterval> timeintervals) {
		List<Integer> rssis = new ArrayList<>();
		SortedMap<Long, Integer> tsRssiMap = this.get(beacon);

		for (TimeInterval t : timeintervals) {
			rssis.addAll(tsRssiMap.subMap(t.getStartTimestamp(), t.getStopTimestamp() + 1).values());
		}

		return rssis;
	}

}