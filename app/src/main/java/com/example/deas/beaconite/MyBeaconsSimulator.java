package com.example.deas.beaconite;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.simulator.BeaconSimulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dea on 12/07/16.
 */
public class MyBeaconsSimulator implements BeaconSimulator {
	private long startTime;

	private List<Beacon> beacons;

	public MyBeaconsSimulator() {
		startTime = System.currentTimeMillis();
		beacons = beaconBuilder(4);
	}

	@Override
	public List<Beacon> getBeacons() {
		long deltaTime = System.currentTimeMillis() - startTime;

		updateSignalStrength((int) deltaTime / 1000);

		return filtered(beacons);
	}

	private List<Beacon> filtered(List<Beacon> beacons) {
		ArrayList<Beacon> result = new ArrayList<Beacon>();
		for (Beacon b : beacons
				) {
			if (b.getRssi() != 0) {
				result.add(b);
			}

		}
		return result;
	}

	private void updateSignalStrength(int seconds) {
		for (Beacon b : beacons) {
			updateBeacon(b, seconds);

		}
	}

	private void updateBeacon(Beacon b, int seconds) {
		int rssi;

		switch (b.getId2().toInt()) {
			case 1:
				rssi = -100 + (7500 / (100 + seconds));
				break;

			case 2:
				rssi = -25 - (7500 / (100 + seconds));
				break;

			case 3:
				if (seconds < 10) {
					rssi = -55;
				} else {
					rssi = 0;
				}
				break;

			default:
				rssi = -66;

		}
		b.setRssi(rssi);
	}

	private List<Beacon> beaconBuilder(int number) {
		List<Beacon> beaconsList = new ArrayList<Beacon>();

		for (int i = 1; i <= number; i++) {
			beaconsList.add(new AltBeacon.Builder().setId1("DF7E1C79-43E9-44FF-886F-" + Long.toHexString(i + 0x100000000000L))
					.setId2("" + i).setId3("1").setTxPower(-55).build());
		}

		return beaconsList;
	}
}
