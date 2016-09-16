package com.example.deas.beaconite;

import java.util.List;

/**
 * Provides some methods needed to calculate a fingerprint.
 * <p/>
 * Created by deas on 16/09/16.
 */
public abstract class Fingerprint {

	protected BeaconMap allBeacons;
	protected List<TimeInterval> timeIntervals;

	public Fingerprint(BeaconMap allMyBeacons, List<TimeInterval> timeIntervals) {
		this.allBeacons = allMyBeacons;
		this.timeIntervals = timeIntervals;

		calculateFingerprint();
	}

	protected abstract void calculateFingerprint();

}
