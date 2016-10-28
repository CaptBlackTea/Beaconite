package com.example.deas.beaconite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a time interval with a start and stop time.
 * <p>
 * Created by deas on 16/09/16.
 */
public class TimeInterval {

	private final Long stopTimestamp;
	private final Long startTimestamp;

	/**
	 * Creates a time interval. A time interval needs boundaries which are the start and stop
	 * timestamp.
	 *
	 * @param startTimestamp lower boundary of the time interval
	 * @param stopTimestamp  upper boundary of the time interval
	 */
	@JsonCreator
	public TimeInterval(@JsonProperty("startTimestamp") Long startTimestamp, @JsonProperty("stopTimestamp") Long
			stopTimestamp) {
		// TODO: maybe ask if the stopTime is bigger then the startTime?
		this.startTimestamp = startTimestamp;
		this.stopTimestamp = stopTimestamp;
	}

	/**
	 * Get the upper boundary of this time interval;
	 *
	 * @return the upper boundary of the time interval
	 */
	public Long getStopTimestamp() {
		return stopTimestamp;
	}

	/**
	 * Get the upper boundary of this time interval;
	 *
	 * @return the upper boundary of the time interval
	 */
	public Long getStartTimestamp() {
		return startTimestamp;
	}

	/**
	 * Returns if a given time value (timestamp) is in between the boundaries of this time
	 * interval.
	 *
	 * @param timestamp in question
	 * @return true if the given timestamp is in between the lower and upper boundary of this time
	 * interval; false if not;
	 */
	public boolean isCovered(Long timestamp) {
		return (timestamp >= startTimestamp) && (timestamp <= stopTimestamp);
	}

	@Override
	public String toString() {
		return "TimeInterval{" +
				"stopTimestamp=" + stopTimestamp +
				", startTimestamp=" + startTimestamp +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TimeInterval that = (TimeInterval) o;

		if (stopTimestamp != null ? !stopTimestamp.equals(that.stopTimestamp) : that.stopTimestamp != null)
			return false;
		return startTimestamp != null ? startTimestamp.equals(that.startTimestamp) : that.startTimestamp == null;

	}

	@Override
	public int hashCode() {
		int result = stopTimestamp != null ? stopTimestamp.hashCode() : 0;
		result = 31 * result + (startTimestamp != null ? startTimestamp.hashCode() : 0);
		return result;
	}
}
