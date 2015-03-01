package org.faudroids.tripweather.weather;


public final class WeatherException extends RuntimeException {

	public static enum Type {
		TOO_DISTANT_DATE
	}


	private final Type type;

	public WeatherException(Type type) {
		this.type = type;
	}


	public Type getType() {
		return type;
	}

}
