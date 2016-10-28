package com.example.deas.beaconite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.altbeacon.beacon.Beacon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides some methods needed to calculate a fingerprint.
 * <p>
 * A Fingerprint is represented by the signal strength (i.e. visibility/distance to the device) of
 * Beacons in given time intervals, i.e. here for a caches time intervals.
 * <p/>
 * Created by deas on 16/09/16.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
		include = JsonTypeInfo.As.PROPERTY,
		property = "@class")

// all known subtypes, separate by comma if there is more than one.
@JsonSubTypes({
		@JsonSubTypes.Type(value = FingerprintMedian.class)
})
public abstract class Fingerprint {

	@JsonIgnore
	protected BeaconMap allBeacons;
	@JsonIgnore
	protected List<TimeInterval> timeIntervals;

	// moved here from FingerprintMedian!
	protected Map<Beacon, FingerprintMedian.BeaconFingerPrint> beacons = new HashMap<>();

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

	public Map<Beacon, FingerprintMedian.BeaconFingerPrint> getBeacons() {
		return beacons;
	}

	@Override
	public String toString() {
		return "Fingerprint{" +
				"allBeacons=" + allBeacons +
				", timeIntervals=" + timeIntervals +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Fingerprint that = (Fingerprint) o;

		return beacons.equals(that.beacons);

	}

	@Override
	public int hashCode() {
		return beacons.hashCode();
	}
}
