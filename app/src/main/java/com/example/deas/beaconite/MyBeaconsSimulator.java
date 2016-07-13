package com.example.deas.beaconite;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.simulator.BeaconSimulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to simulate beacons. Inspired by the the TimedBeaconSimulator from the code examples from
 * the altBeacon library.
 * <p/>
 * Sets up several beacons that can by detected by the application. Simulates several "realistic"
 * scenarios, e.g. changing of the signal strength (rssi) of a beacon, vanishing of a beacon signal
 * entirely, ...
 * <p/>
 * Developed to implement core features of the application without the need to have real beacons all
 * the time.
 * <p/>
 * <p/>
 * Created by dea on 12/07/16.
 */
public class MyBeaconsSimulator implements BeaconSimulator {

	// time when an instance is first invoke
	private long startTime;

	// the complete list of all simulated beacons
	private List<Beacon> beacons;


	/**
	 * Constructor to set up the simulation.
	 *
	 * @param amountOfBeacons the number of simulated beacons that will be constructed and managed
	 *                        by this class.
	 */
	public MyBeaconsSimulator(int amountOfBeacons) {
		startTime = System.currentTimeMillis();
		beacons = beaconBuilder(amountOfBeacons);
	}

	/**
	 * Required getter method that is called regularly by the Android Beacon Library. Any beacons
	 * returned by this method will appear within the test environment immediately.
	 * <p/>
	 * In this implementation every time this method is called all beacons receive an update of
	 * their current signal strength (rssi value). Simulates the changing values of beacons over
	 * time. Whereas time in this case is the regularity in which the Android Beacon Library calls
	 * this method and the rssi values are dependent on the time that has passed since the instance
	 * was constructed.
	 *
	 * @return a list containing configured beacons.
	 */
	@Override
	public List<Beacon> getBeacons() {
		long deltaTime = System.currentTimeMillis() - startTime;

		updateSignalStrength((int) deltaTime / 1000);

		return filtered(beacons);
	}

	/**
	 * Method to alter the list of all available configured beacons. Useful to e.g. simulate that a
	 * beacon is not visible any more and therefore is excluded from the list returned by this
	 * method. But in the complete list of all generated simulated beacons {@link this.beacons} this
	 * beacon still exists and can be used again.
	 *
	 * @param beacons a list of Beacon objects to be filtered, i.e. beacons matching some criteria
	 *                will not be added to the list returned by this method.
	 * @return a list with beacons
	 */
	private List<Beacon> filtered(List<Beacon> beacons) {
		ArrayList<Beacon> result = new ArrayList<Beacon>();
		for (Beacon b : beacons
				) {

			// if the signal strength is 0 the beacon is not visible
			if (b.getRssi() != 0) {
				result.add(b);
			}

		}
		return result;
	}

	/**
	 * Change the signal strength of all beacons in the beacons list. Simulates the change of the
	 * signal strength of all beacons over time.
	 *
	 * @param seconds an integer value representing seconds.
	 */
	private void updateSignalStrength(int seconds) {
		for (Beacon b : beacons) {
			updateBeacon(b, seconds);

		}
	}

	/**
	 * Updates values of a given beacon in dependency of the time value given.
	 *
	 * @param b       a beacon which values will be updated
	 * @param seconds time value that the change depends on
	 */
	private void updateBeacon(Beacon b, int seconds) {
		int rssi;

		// get the given beacons Major as an integer
		switch (b.getId2().toInt()) {
			// for the beacon with the Major 1 the rssi value gets bigger over time -> beacon
			// gets closer
			case 1:
				rssi = -100 + (7500 / (100 + seconds));
				break;

			// for the beacon with the Major 2 the rssi value gets smaller over time -> beacon
			// gets further away
			case 2:
				rssi = -25 - (7500 / (100 + seconds));
				break;

			// for the beacon with the Major 3 the rssi value is set to 0 for the first 10
			// seconds then it is set to a fixed value -> beacon appears
			case 3:
				if (seconds > 10) {
					rssi = -55;
				} else {
					rssi = 0;
				}
				break;

			// for the beacon with the Major 4 the rssi value is set to a fixed value for the first
			// 10 seconds then it is set to 0 -> beacon disappears after 10 seconds
			case 4:
				if (seconds < 10) {
					rssi = -55;
				} else {
					rssi = 0;
				}
				break;

			// if there are more then beacons than the number of cases set a fixed value for the
			// rest
			default:
				rssi = -66;

		}
		b.setRssi(rssi);
	}

	/**
	 * Builds a number of AltBeacons and adds them to a list. Each beacon has a UUID (Id1), Major
	 * (Id2) and Minor(Id3). Also the TxPower is set to -55 for all beacons.
	 * Id1: will be unique. Last part of the Id is used as a counter. Starting with
	 * DF7E1C79-43E9-44FF-886F-100000000001 for the first beacon constructed and counting upwards
	 * to the number given.
	 *
	 * Id2: will behave in the same way. First beacon constructed will have Major 1, second Major
	 * 2 and so on.
	 *
	 * @param number the number of beacons that will be constructed and put in a list.
	 * @return a list with configured beacons.
	 */
	private List<Beacon> beaconBuilder(int number) {
		List<Beacon> beaconsList = new ArrayList<Beacon>();

		for (int i = 1; i <= number; i++) {
			beaconsList.add(new AltBeacon.Builder().setId1("DF7E1C79-43E9-44FF-886F-" + Long.toHexString(i + 0x100000000000L))
					.setId2("" + i).setId3("1").setTxPower(-55).build());
		}

		return beaconsList;
	}
}
