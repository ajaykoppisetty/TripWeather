package org.faudroids.tripweather.directions;


import org.roboguice.shaded.goole.common.base.Objects;

/**
 * Represents one location in the google places api.
 */
public class PlacesLocation {

	private final String id;
	private final String description;
	private final double lat, lon;

	public PlacesLocation(String id, String description, double lat, double lon) {
		this.id = id;
		this.description = description;
		this.lat = lat;
		this.lon = lon;
	}


	public String getId() {
		return id;
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
		if (other == null || !(other instanceof PlacesLocation)) return false;
		PlacesLocation location = (PlacesLocation) other;
		return Objects.equal(id, location.id)
				&& Objects.equal(description, location.description)
				&& Objects.equal(lat, location.lat)
				&& Objects.equal(lon, location.lon);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(id, description, lat, lon);
	}

}
