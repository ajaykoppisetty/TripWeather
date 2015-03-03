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
	private final ForecastMode forecastMode;
	private final double temperature;
	private final double rain; // mm
	private final double snow; // mm
	private final long timestamp; // minutes in the future

	public Forecast(
			WayPoint wayPoint,
			ForecastMode forecastMode,
			double temperature,
			double rain,
			double snow,
			long timestamp) {

		this.wayPoint = wayPoint;
		this.forecastMode = forecastMode;
		this.temperature = temperature;
		this.rain = rain;
		this.snow = snow;
		this.timestamp = timestamp;
	}


	public WayPoint getWayPoint() {
		return wayPoint;
	}


	public ForecastMode getForecastMode() {
		return forecastMode;
	}


	public double getTemperature() {
		return temperature;
	}


	public double getRain() {
		return rain;
	}


	public double getSnow() {
		return snow;
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
				&& Objects.equal(forecastMode, forecast.forecastMode)
				&& Objects.equal(temperature, forecast.temperature)
				&& Objects.equal(rain, forecast.rain)
				&& Objects.equal(snow, forecast.snow)
				&& Objects.equal(timestamp, forecast.timestamp);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(wayPoint, forecastMode, temperature, rain, snow, timestamp);
	}


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDoubleArray(new double[] { wayPoint.getLat(), wayPoint.getLng(), temperature, rain, snow });
		dest.writeString(forecastMode.name());
		dest.writeLong(timestamp);
	}


	public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>() {

		@Override
		public Forecast createFromParcel(Parcel in) {
			double[] data = new double[5];
			in.readDoubleArray(data);
			ForecastMode mode = ForecastMode.valueOf(in.readString());
			return new Forecast(
					new WayPoint(data[0], data[1]),
					mode,
					data[2],
					data[3],
					data[4],
					in.readLong());
		}


		@Override
		public Forecast[] newArray(int size) {
			return new Forecast[size];
		}

	};


	public static final class Builder {

		private WayPoint wayPoint;
		private double temperature, rain, snow;
		private ForecastMode forecastMode;
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

		public ForecastMode forecastMode() {
			return forecastMode;
		}

		public Builder forecastMode(ForecastMode forecastMode) {
			this.forecastMode = forecastMode;
			return this;
		}

		public double temperature() {
			return temperature;
		}

		public Builder timestamp(long timestamp) {
			this.timestamp = timestamp;
			return this;
		}

		public double rain() {
			return rain;
		}

		public Builder rain(double rain) {
			this.rain = rain;
			return this;
		}

		public double snow() {
			return snow;
		}

		public Builder snow(double snow) {
			this.snow = snow;
			return this;
		}

		public long timestamp() {
			return timestamp;
		}

		public Forecast build() {
			return new Forecast(wayPoint, forecastMode, temperature, rain, snow, timestamp);
		}

	}


}
