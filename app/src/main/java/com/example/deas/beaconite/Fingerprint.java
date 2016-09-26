package com.example.deas.beaconite;

import java.util.List;

/**
 * Provides some methods needed to calculate a fingerprint.
 * <p>
 * A Fingerprint is represented by the signal strength (i.e. visibility/distance to the device) of
 * Beacons in given time intervals, i.e. here for a caches time intervals.
 * <p/>
 * Created by deas on 16/09/16.
 */
public abstract class Fingerprint {

	protected BeaconMap allBeacons;
	protected List<TimeInterval> timeIntervals;

	/**
	 * Creates a Fingerprint of given Beacons and a given list of time intervals.
	 *
	 * @param allMyBeacons  the data to create the fingerprint from.
	 * @param timeIntervals the relevant time intervals for the given (Beacon-) data.
	 */
	public Fingerprint(BeaconMap allMyBeacons, List<TimeInterval> timeIntervals) {
		this.allBeacons = allMyBeacons;
		this.timeIntervals = timeIntervals;

		calculateFingerprint();
	}

	/**
	 * Implement to set how the fingerprint is calculated.
	 * Enables to change the used fingerprint calculation.
	 */
	protected abstract void calculateFingerprint();

}
