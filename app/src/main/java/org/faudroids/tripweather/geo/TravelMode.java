package org.faudroids.tripweather.geo;

import org.faudroids.tripweather.R;

public enum TravelMode {

	DRIVING("driving", R.string.travel_mode_driving),
	WALKING("walking", R.string.travel_mode_walking),
	BICYCLING("bicycling", R.string.travel_mode_bicycling);

	private final String value;
	private final int resourceId;

	TravelMode(String value, int resourceId) {
		this.value = value;
		this.resourceId = resourceId;
	}


	@Override
	public String toString() {
		return value;
	}


	public int getResourceId() {
		return resourceId;
	}

}
