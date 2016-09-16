package com.example.deas.beaconite;

import org.altbeacon.beacon.Beacon;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by deas on 16/09/16.
 */
public class FingerprintMedian extends Fingerprint {
	private Map<Beacon, BeaconFingerPrint> beacons = new HashMap<>();

	public FingerprintMedian(BeaconMap allBeacons, List<TimeInterval> timeIntervals) {
		super(allBeacons, timeIntervals);
	}

	@Override
	protected void calculateFingerprint() {
		for (Beacon b : allBeacons.keySet()) {
			BeaconFingerPrint bfp = new BeaconFingerPrint(allBeacons.rssisForTimeintervals(b,
					timeIntervals));
			beacons.put(b, bfp);
		}
	}

	class BeaconFingerPrint {
		// default values for invisible Beacons
		// -200 because -100 is the lowest possible Rssi value (very far away)
		final Integer INVISIBLE = -200;
		private Integer median = INVISIBLE;
		private double upperLimit = INVISIBLE;
		private double lowerLimit = INVISIBLE;

		public BeaconFingerPrint(List<Integer> rssis) {
			if (!rssis.isEmpty()) {
				Collections.sort(rssis);
				calcMedian(rssis);
				calcCorridor(rssis);
			}
		}

		private void calcCorridor(List<Integer> rssis) {
			Long upperSum = 0L;
			int upperN = 0;

			Long lowerSum = 0L;
			int lowerN = 0;

			for (Integer rssi : rssis) {
				Long distance = rssi.longValue() - median;
				if (distance > 0) {
					upperSum += distance * distance;
					upperN++;
				} else if (distance < 0) {
					lowerSum += distance * distance;
					lowerN++;
				}
			}

			upperLimit = median + Math.sqrt(upperSum) / upperN;
			lowerLimit = median - Math.sqrt(lowerSum) / lowerN;
		}

		private void calcMedian(List<Integer> rssis) {
			this.median = rssis.get(rssis.size() / 2);
		}

		boolean isCovered(Integer rssi) {
			return (rssi >= lowerLimit) && (rssi <= upperLimit);
		}

	}
}
