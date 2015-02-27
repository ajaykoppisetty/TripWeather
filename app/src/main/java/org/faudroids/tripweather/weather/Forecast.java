package org.faudroids.tripweather.weather;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.faudroids.tripweather.geo.WayPoint;
import org.roboguice.shaded.goole.common.base.Objects;

/**
 * Collection of data items that characterize a forecast for a single location.
 */
public final class Forecast {

	private final WayPoint wayPoint;
	private final double temperature;
	private final int timestamp; // hours in the future

	@JsonCreator
	public Forecast(
			@JsonProperty("wayPoint") WayPoint wayPoint,
			@JsonProperty("temperature") double temperature,
			@JsonProperty("timestamp") int timestamp) {

		this.wayPoint = wayPoint;
		this.temperature = temperature;
		this.timestamp = timestamp;
	}


	public WayPoint getWayPoint() {
		return wayPoint;
	}


	public double getTemperature() {
		return temperature;
	}


	public int getTimestamp() {
		return timestamp;
	}


	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Forecast)) return false;
		if (other == this) return true;
		Forecast forecast = (Forecast) other;
		return Objects.equal(wayPoint, forecast.wayPoint)
				&& Objects.equal(temperature, forecast.temperature)
				&& Objects.equal(timestamp, forecast.timestamp);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(wayPoint, temperature, timestamp);
	}


	public static final class Builder {

		private WayPoint wayPoint;
		private double temperature;
		private int timestamp;

		public Builder wayPoint(WayPoint wayPoint) {
			this.wayPoint = wayPoint;
			return this;
		}

		public WayPoint wayPoint() {
			return wayPoint;
		}

		public Builder temperature(double temperature) {
			this.temperature = temperature;
			return this;
		}

		public double temperature() {
			return temperature;
		}

		public Builder  timestamp(int timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public int timestamp() {
			return timestamp;
		}

		public Forecast build() {
			return new Forecast(wayPoint, temperature, timestamp);
		}

	}


}
