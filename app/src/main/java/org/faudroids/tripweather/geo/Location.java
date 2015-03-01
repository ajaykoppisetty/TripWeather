package org.faudroids.tripweather.geo;


import android.os.Parcel;
import android.os.Parcelable;

import org.roboguice.shaded.goole.common.base.Objects;

/**
 * Represents one location in the google maps api.
 */
public class Location implements Parcelable {

	private final String description;
	private final double lat, lng;

	public Location(String description, double lat, double lng) {
		this.description = description;
		this.lat = lat;
		this.lng = lng;
	}


	public String getDescription() {
		return description;
	}


	public double getLat() {
		return lat;
	}


	public double getLng() {
		return lng;
	}


	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Location)) return false;
		Location location = (Location) other;
		return Objects.equal(description, location.description)
				&& Objects.equal(lat, location.lat)
				&& Objects.equal(lng, location.lng);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(description, lat, lng);
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(description);
		dest.writeDouble(lat);
		dest.writeDouble(lng);
	}


	public static final Creator<Location> CREATOR = new Creator<Location>() {
		@Override
		public  Location createFromParcel(Parcel in) {
			return new Location(in.readString(), in.readDouble(), in.readDouble());
		}

		@Override
		public Location[] newArray(int size) {
			return new Location[size];
		}
	};

}
