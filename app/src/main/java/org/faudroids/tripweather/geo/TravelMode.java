package org.faudroids.tripweather.geo;

public enum TravelMode {

	DRIVING("driving"),
	WALKING("walking"),
	BICYCLING("bicycling");

	private final String value;

	TravelMode(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
