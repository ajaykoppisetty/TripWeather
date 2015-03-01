package org.faudroids.tripweather.geo;


public final class DirectionsException extends RuntimeException {

	public static enum Type {
		ZERO_RESULTS, INTERNAL_ERROR
	}


	private final Type type;

	public DirectionsException(Type type) {
		this.type = type;
	}


	public Type getType() {
		return type;
	}

}
