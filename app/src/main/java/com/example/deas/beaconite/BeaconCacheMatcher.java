package com.example.deas.beaconite;

import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by deas on 14/11/16.
 */
public final class BeaconCacheMatcher {

	protected static final String TAG = "BeaconCacheMatcher";

	private BeaconCacheMatcher() {
	}


	/**
	 * Takes a collection of Beacons and Caches. Traverses both and collects all Caches that match
	 * the Beacons in the collection. Returns and empty List if no matching Cache was found.
	 *
	 * @param beacons
	 * @return
	 */
	public static List<Cache> matchesAnyCache(Collection<Beacon> beacons, Collection<Cache>
			caches) {

		Collection<Beacon> scannedBeacons = beacons;
		List<Cache> matchingCaches = new ArrayList<>();

		// traverse Caches
		for (Cache cachePotentialMatch : caches) {
			// for each cache: are the Beacons associated with this Cache a subset of the given
			// beacons Collection?

			Map<Beacon, FingerprintMedian.BeaconFingerPrint> cacheBeacons = cachePotentialMatch.getFingerprint().getBeacons();

			Log.d(TAG, "Scanned Beacons: " + scannedBeacons + "; " + cachePotentialMatch.getCacheName() + ": Cache " +
					"Beacons: " + cacheBeacons.keySet());

			Log.d(TAG, "Result of contains: " + beacons.containsAll(cacheBeacons.keySet()));

			// are the Beacons associated with this Cache a subset of the given
			// beacons Collection
			if (scannedBeacons.containsAll(cacheBeacons.keySet())) {
				// if yes: traverse beacons and check if a beacon is in the fingerprint-corridor of this
				// cache matching beacon
				for (Beacon scannedBeacon : scannedBeacons) {
					// if all beacons match a corridor of this caches corresponding Beacons
					// Fingerprint: add cache to returned list

					int rssiToCheck = scannedBeacon.getRssi();

					if (cacheBeacons.containsKey(scannedBeacon)) {
						FingerprintMedian.BeaconFingerPrint beaconFingerPrint = cacheBeacons.get(scannedBeacon);

						if (beaconFingerPrint.isCovered(rssiToCheck)) {
							if (!matchingCaches.contains(cachePotentialMatch)) {
								matchingCaches.add(cachePotentialMatch);
								// if not all match: next cache
							}
						}
						// if no: next cache
					}
				}
			}

		}

		Log.d(TAG, "Matching Caches: " + matchingCaches);

		return matchingCaches;
	}
}
