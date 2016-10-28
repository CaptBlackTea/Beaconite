package com.example.deas.beaconite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
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

	// default values for invisible Beacons
	// -200 because -100 is the lowest possible Rssi value (very far away)
	final static Integer INVISIBLE = -200;
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

	private Beacon constructBeacon(String key) {
		//Beacon for toString looks like this:
		// "id1: df7e1c79-43e9-44ff-886f-100000000003 id2: 3 id3: 1"

		String[] ids = key.split(" ");

		// HINT: constructs a Beacon without txPower -> if needed do not forget to store it in
		// the JSON, because currently JSON only stores the ids!
		return new Beacon.Builder().setId1(ids[1])
				.setId2(ids[3]).setId3(ids[5]).build();
	}

	/**
	 * Implement to set how the fingerprint is calculated.
	 * Enables to change the used fingerprint calculation.
	 */
	protected abstract void calculateFingerprint();

	public Map<Beacon, FingerprintMedian.BeaconFingerPrint> getBeacons() {
		return beacons;
	}

	@JsonSetter
	protected void setBeacons(Map<String, FingerprintMedian.BeaconFingerPrint> beaconInput) {
		if (beaconInput == null) {
			return;
		}
		for (Map.Entry<String, FingerprintMedian.BeaconFingerPrint> entry : beaconInput.entrySet()) {
			Beacon b = constructBeacon(entry.getKey());
			beacons.put(b, entry.getValue());
		}
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
