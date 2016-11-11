package com.example.deas.beaconite;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.altbeacon.beacon.Beacon;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implements that a fingerprint as calculated as a median of the given data.
 * <p>
 * Created by deas on 16/09/16.
 */
public class FingerprintMedian extends Fingerprint {
	protected static final String TAG = "FingerprintMedian";

//	private Map<Beacon, BeaconFingerPrint> beacons = new HashMap<>();

	@JsonCreator
	public FingerprintMedian(@JsonProperty("allBeacons") BeaconMap allBeacons, @JsonProperty("timeIntervals")
			List<TimeInterval> timeIntervals, @JsonProperty("beacons") Map<String, FingerprintMedian.BeaconFingerPrint> beaconInput) {
		super(allBeacons, timeIntervals);

		setBeacons(beaconInput);
		Log.d(TAG, "Beacons Map after Constructor call: " + beacons);
	}



	/**
	 * Traverses all Beacons, given via the constructor, and generates a fingerprint for each
	 * beacon. Then stores a beacon-fingerprint pair in an internal data structure. A
	 * general/big/cache Fingerprint here therefor consists of a list of beacons, where each beacon
	 * has a BeaconFingerPrint.
	 */
	@Override
	protected void calculateFingerprint() {
		if (allBeacons != null) {
			for (Beacon b : allBeacons.keySet()) {
				BeaconFingerPrint bfp = new BeaconFingerPrint(allBeacons.rssisForTimeintervals(b,
						timeIntervals));

				Log.d(TAG, "Beacons: " + beacons);
				Log.d(TAG, "B: " + b);
				Log.d(TAG, "BFP: " + bfp);

				if (beacons != null) {
					beacons.put(b, bfp);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "FingerprintMedian{" +
				"beacons=" + beacons +
				'}';
	}

	/**
	 * Generates a fingerprint for an individual Beacon.
	 *
	 * !!!: is static due to (de-)serializing problems in Jackson
	 * -> TODO: maybe refactor to a normal class in a separate file, not static then ;)
	 */
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
			include = JsonTypeInfo.As.PROPERTY,
			property = "@class",
			defaultImpl = FingerprintMedian.BeaconFingerPrint.class)
	static class BeaconFingerPrint {
		// FIXME: some calculations result in NaN!!
		private Integer median = INVISIBLE;
		private double upperLimit = INVISIBLE;
		private double lowerLimit = INVISIBLE;

		@JsonCreator
		private BeaconFingerPrint() {
		}

		/**
		 * Takes a list of Integer rssi values. Sorts the list, if not empty, and calculates a
		 * Median for the values in the list as well as a corridor.
		 *
		 * @param rssis
		 */
		public BeaconFingerPrint(List<Integer> rssis) {
			if (!rssis.isEmpty()) {
				Collections.sort(rssis);
				this.median = calcMedian(rssis);
				calcCorridor(rssis);
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			BeaconFingerPrint that = (BeaconFingerPrint) o;

			if (Double.compare(that.upperLimit, upperLimit) != 0) return false;
			if (Double.compare(that.lowerLimit, lowerLimit) != 0) return false;
			return median != null ? median.equals(that.median) : that.median == null;

		}

		@Override
		public int hashCode() {
			int result;
			long temp;
			result = median != null ? median.hashCode() : 0;
			temp = Double.doubleToLongBits(upperLimit);
			result = 31 * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(lowerLimit);
			result = 31 * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		public int getMedian() {
			return median;
		}

		public void setMedian(int median) {
			this.median = median;
		}

		public double getUpperLimit() {
			return upperLimit;
		}

		public void setUpperLimit(double upperLimit) {
			this.upperLimit = upperLimit;
		}

		public double getLowerLimit() {
			return lowerLimit;
		}

		public void setLowerLimit(double lowerLimit) {
			this.lowerLimit = lowerLimit;
		}

		@Override
		public String toString() {
			return "BeaconFingerPrint{" +
					"INVISIBLE=" + INVISIBLE +
					", median=" + median +
					", upperLimit=" + upperLimit +
					", lowerLimit=" + lowerLimit +
					'}';
		}

		// TODO: change return value to an array with upper and lower limit
		/**
		 * A corridor for given rssi values. The corridor represents the margin of deviation from
		 * the calculated Median for these rssi values.
		 *
		 * @param rssis
		 */
		private void calcCorridor(List<Integer> rssis) {

			if (this.median == INVISIBLE) {
				return; // TODO: return an array with INVISIBLE/INVISIBLE for upper and lower
				// limit values
			}

			Long upperSum = 0L;
			int upperSumElementCounter = 0;

			Long lowerSum = 0L;
			int lowerSumElementCounter = 0;

			for (Integer rssi : rssis) {
				Long distance = rssi.longValue() - median;
				if (distance > 0) {
					upperSum += distance * distance;
					upperSumElementCounter++;
				} else if (distance < 0) {
					lowerSum += distance * distance;
					lowerSumElementCounter++;
				}
			}

			upperLimit = median + calcOffset(upperSum, upperSumElementCounter);
			lowerLimit = median - calcOffset(lowerSum, lowerSumElementCounter);

		}

		//TODO: COMMENTS!!!11elf!
		private double calcOffset(Long sum, int count) {
			if (count == 0) {
				return 0;
			}

			return Math.sqrt(sum) / count;

		}

		private Integer calcMedian(List<Integer> rssis) {
			Integer median = rssis.get(rssis.size() / 2);

			if (!validRssi(median)) {
				median = INVISIBLE;
			}

			return median;
		}

		/**
		 * A valid rssi value is between -100 and -25 (see Beacon specs!)
		 *
		 * @param median
		 * @return
		 */
		private boolean validRssi(Integer median) {
			return ((median >= -100) && (median <= -25));
		}

		/**
		 * Returns if given rssi value is in the boundaries, i.e. the calculated corridor, of this
		 * BeaconFingerPrint.
		 *
		 * @param rssi the rssi value to check if it is in this beacons fingerprint.
		 * @return true if the rssi value is in the boundaries/corridor of this beacons fingerprint;
		 * false if not.
		 */
		boolean isCovered(Integer rssi) {
			return (rssi >= lowerLimit) && (rssi <= upperLimit);
		}

	}
}
