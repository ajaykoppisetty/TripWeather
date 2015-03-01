package org.faudroids.tripweather.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.faudroids.tripweather.geo.Location;
import org.faudroids.tripweather.weather.Forecast;
import org.roboguice.shaded.goole.common.base.Objects;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;


public final class TripData implements Parcelable {

	private static final ObjectMapper mapper = new ObjectMapper();

	private final Location fromLocation, toLocation;
	private final ObjectNode route;
	private final List<Forecast> forecasts;

	public TripData(Location fromLocation, Location toLocation, ObjectNode route, List<Forecast> forecasts) {
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.route = route;
		this.forecasts = forecasts;
	}


	public Location getFromLocation() {
		return fromLocation;
	}


	public Location getToLocation() {
		return toLocation;
	}


	public ObjectNode getRoute() {
		return route;
	}


	public List<Forecast> getForecasts() {
		return forecasts;
	}


	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof TripData)) return false;
		if (other == this) return true;
		TripData data = (TripData) other;
		return Objects.equal(fromLocation, data.fromLocation)
				&& Objects.equal(toLocation, data.toLocation)
				&& Objects.equal(route, data.route)
				&& Objects.equal(forecasts, data.forecasts);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(fromLocation, toLocation, route, forecasts);
	}


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(fromLocation, flags);
		dest.writeParcelable(toLocation, flags);
		dest.writeString(route.toString());
		dest.writeParcelableArray(forecasts.toArray(new Forecast[forecasts.size()]), flags);
	}


	public static final Creator<TripData> CREATOR = new Creator<TripData>() {
		@Override
		public TripData createFromParcel(Parcel source) {
			try {
				Location fromLocation = source.readParcelable(Location.class.getClassLoader());
				Location toLocation = source.readParcelable(Location.class.getClassLoader());
				ObjectNode route = (ObjectNode) mapper.readTree(source.readString());
				Parcelable[] forecasts = source.readParcelableArray(Forecast.class.getClassLoader());
				return new TripData(
						fromLocation,
						toLocation,
						route,
						Arrays.asList(Arrays.copyOf(forecasts, forecasts.length, Forecast[].class)));

			} catch (IOException ioe) {
				Timber.e(ioe, "failed to create json from route string");
				return null;
			}
		}

		@Override
		public TripData[] newArray(int size) {
			return new TripData[size];
		}
	};


	public static final class Builder {

		private Location fromLocation, toLocation;
		private ObjectNode route;
		private List<Forecast> forecasts;

		public Builder fromLocation(Location fromLocation) {
			this.fromLocation = fromLocation;
			return this;
		}


		public Location fromLocation() {
			return fromLocation;
		}


		public Builder toLocation(Location toLocation) {
			this.toLocation = toLocation;
			return this;
		}


		public Location toLocation() {
			return toLocation;
		}


		public Builder route(ObjectNode route) {
			this.route = route;
			return this;
		}


		public ObjectNode route() {
			return route;
		}


		public Builder forecasts(List<Forecast> forecasts) {
			this.forecasts = forecasts;
			return this;
		}


		public List<Forecast> forecasts() {
			return forecasts;
		}


		public TripData build() {
			return new TripData(fromLocation, toLocation, route, forecasts);
		}

	}

}
