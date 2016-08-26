package com.example.deas.beaconite;

import android.os.Parcel;
import android.os.Parcelable;

import org.altbeacon.beacon.Beacon;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by deas on 25/08/16.
 */
public class MyBeacon extends Beacon implements Parcelable {


	// stores timestamps (Long) connected with Rssi values (int)
	Map<Long, Integer> overTime = new HashMap<>();

	public MyBeacon(Beacon beacon) {
		super(beacon);
	}


	public Map<Long, Integer> getAllTimestamps() {
		return overTime;
	}

	/**
	 * Gets the signal a beacon was seen with at a given time.
	 *
	 * @param timestamp the requested timestamp to look up how strong (rssi value) a beacon was
	 *                  seen
	 * @return if there exists such a timestamp for this Beacon: new SignalAtTime object. Contains
	 * the requested timestamp, connected rssi value and tx-power of this Beacon;
	 * <p/>
	 * if there exists no such timestamp for this Beacon: null;
	 */
	public SignalAtTimestamp getValuesForTimestamp(Long timestamp) {
		if (overTime.containsKey(timestamp)) {
			Integer rssiAtTimestamp = overTime.get(timestamp);
			int txPower = this.getTxPower();

			return new SignalAtTimestamp(timestamp, rssiAtTimestamp, txPower);
		}

		return null;
	}

	public void addRssiAndTime(Long timestamp, int rssi) {
		overTime.put(timestamp, rssi);
	}


	// TODO: following code was auto-generated due to Parcelable interface requirement; Useful
	// that way or should it be implemented more specific?
	protected MyBeacon(Parcel in) {
		super(in);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<MyBeacon> CREATOR = new Creator<MyBeacon>() {
		@Override
		public MyBeacon createFromParcel(Parcel in) {
			return new MyBeacon(in);
		}

		@Override
		public MyBeacon[] newArray(int size) {
			return new MyBeacon[size];
		}
	};
}
