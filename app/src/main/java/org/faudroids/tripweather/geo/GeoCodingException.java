package org.faudroids.tripweather.geo;


public final class GeoCodingException extends RuntimeException {

	public static enum Type {
		ZERO_RESULTS, USER_LOCATION_UNAVAILABLE, INTERNAL_ERROR
	}


	private final Type type;

	public GeoCodingException(Type type) {
		this.type = type;
	}


	public Type getType() {
		return type;
	}

}
