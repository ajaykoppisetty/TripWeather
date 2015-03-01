package org.faudroids.tripweather.geo;


public final class DirectionsException extends RuntimeException {

	public static enum Type {
		ZERO_RESULTS, INTERNAL_ERROR
	}


	public static DirectionsException createZeroResultsException(String fromLocation, String toLocation) {
		return new DirectionsException(Type.ZERO_RESULTS, fromLocation, toLocation);
	}


	public static DirectionsException createInteralException() {
		return new DirectionsException(Type.INTERNAL_ERROR, null, null);
	}



	private final Type type;
	private final String fromLocation, toLocation;

	private DirectionsException(Type type, String fromLocation , String toLocation) {
		this.type = type;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
	}


	public Type getType() {
		return type;
	}


	public String getFromLocation() {
		return fromLocation;
	}


	public String getToLocation() {
		return toLocation;
	}
}
