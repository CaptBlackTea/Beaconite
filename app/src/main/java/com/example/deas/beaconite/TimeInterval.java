package com.example.deas.beaconite;

/**
 * Created by deas on 16/09/16.
 */
public class TimeInterval {

	private final Long stopTimestamp;
	private final Long startTimestamp;

	public TimeInterval(Long startTimestamp, Long stopTimestamp) {
		this.startTimestamp = startTimestamp;
		this.stopTimestamp = stopTimestamp;
	}

	public Long getStopTimestamp() {
		return stopTimestamp;
	}

	public Long getStartTimestamp() {
		return startTimestamp;
	}

	public boolean isCovered(Long timestamp) {
		return (timestamp >= startTimestamp) && (timestamp <= stopTimestamp);
	}
}
