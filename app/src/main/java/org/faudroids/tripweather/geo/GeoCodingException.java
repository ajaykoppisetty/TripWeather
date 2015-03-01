package org.faudroids.tripweather.geo;


public final class GeoCodingException extends RuntimeException {

	public static enum Type {
		ZERO_RESULTS, USER_LOCATION_UNAVAILABLE, INTERNAL_ERROR
	}


	public static GeoCodingException createZeroResultsException(String location) {
		return new GeoCodingException(Type.ZERO_RESULTS, location);
	}


	public static GeoCodingException createInteralException() {
		return new GeoCodingException(Type.INTERNAL_ERROR, null);
	}


	public static GeoCodingException createUserLocationUnavailableException() {
		return new GeoCodingException(Type.USER_LOCATION_UNAVAILABLE, null);
	}


	private final Type type;
	private final String location;

	private GeoCodingException(Type type, String location) {
		this.type = type;
		this.location = location;
	}


	public Type getType() {
		return type;
	}


	public String getLocation() {
		return location;
	}

}
