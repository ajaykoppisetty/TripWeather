package org.faudroids.tripweather.weather;


import android.os.Parcel;
import android.os.Parcelable;

import org.faudroids.tripweather.geo.WayPoint;
import org.roboguice.shaded.goole.common.base.Objects;

/**
 * Collection of data items that characterize a forecast for a single location.
 */
public final class Forecast implements Parcelable {

	private final WayPoint wayPoint;
	private final double temperature;
	private final long timestamp; // minutes in the future

	public Forecast(
			WayPoint wayPoint,
			double temperature,
			long timestamp) {

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


	public long getTimestamp() {
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


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDoubleArray(new double[] { wayPoint.getLat(), wayPoint.getLng(), temperature });
		dest.writeLong(timestamp);
	}


	public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>() {

		@Override
		public Forecast createFromParcel(Parcel in) {
			double[] data = new double[3];
			in.readDoubleArray(data);
			return new Forecast(
					new WayPoint(data[0], data[1]),
					data[2],
					in.readLong());
		}


		@Override
		public Forecast[] newArray(int size) {
			return new Forecast[size];
		}

	};


	public static final class Builder {

		private WayPoint wayPoint;
		private double temperature;
		private long timestamp;

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

		public Builder timestamp(long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public long timestamp() {
			return timestamp;
		}

		public Forecast build() {
			return new Forecast(wayPoint, temperature, timestamp);
		}

	}


}
