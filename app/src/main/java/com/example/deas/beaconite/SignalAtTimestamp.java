package com.example.deas.beaconite;

/**
 * Created by deas on 25/08/16.
 */
public class SignalAtTimestamp {

	private Long timestamp;
	private int rssi;
	private int txPower;


	public SignalAtTimestamp(Long timestamp, int rssi, int txPower) {
		this.timestamp = timestamp;
		this.rssi = rssi;
		this.txPower = txPower;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public int getRssi() {
		return rssi;
	}

	public int getTxPower() {
		return txPower;
	}

	public String toString() {

		return "Timestamp - Rssi, Tx-Power: [" + this.timestamp + " - " + rssi + ", " + txPower +
				"]";
	}
}
