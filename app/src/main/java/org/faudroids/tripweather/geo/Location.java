package org.faudroids.tripweather.geo;


import org.roboguice.shaded.goole.common.base.Objects;

/**
 * Represents one location in the google maps api.
 */
public class Location {

	private final String description;
	private final double lat, lon;

	public Location(String description, double lat, double lon) {
		this.description = description;
		this.lat = lat;
		this.lon = lon;
	}


	public String getDescription() {
		return description;
	}


	public double getLat() {
		return lat;
	}


	public double getLon() {
		return lon;
	}


	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Location)) return false;
		Location location = (Location) other;
		return Objects.equal(description, location.description)
				&& Objects.equal(lat, location.lat)
				&& Objects.equal(lon, location.lon);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(description, lat, lon);
	}

}
