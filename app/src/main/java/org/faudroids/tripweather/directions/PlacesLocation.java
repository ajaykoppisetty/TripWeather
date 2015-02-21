package org.faudroids.tripweather.directions;


import org.roboguice.shaded.goole.common.base.Objects;

/**
 * Represents one location in the google places api.
 */
public class PlacesLocation {

	private final String id;
	private final String description;

	public PlacesLocation(String id, String description) {
		this.id = id;
		this.description = description;
	}


	public String getId() {
		return id;
	}


	public String getDescription() {
		return description;
	}


	@Override
	public String toString() {
		return description;
	}


	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof PlacesLocation)) return false;
		PlacesLocation location = (PlacesLocation) other;
		return Objects.equal(id, location.id) && Objects.equal(description, location.description);
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(id, description);
	}

}
