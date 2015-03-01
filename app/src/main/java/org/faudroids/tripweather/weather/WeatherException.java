package org.faudroids.tripweather.weather;


public final class WeatherException extends RuntimeException {

	public static enum Type {
		TOO_DISTANT_DATE
	}


	public static WeatherException createTooDistanteDateException(long startTimestamp) {
		return new WeatherException(Type.TOO_DISTANT_DATE, startTimestamp);
	}


	private final Type type;
	private final long startTimestamp;

	private WeatherException(Type type, long startTimestamp) {
		this.type = type;
		this.startTimestamp = startTimestamp;
	}


	public Type getType() {
		return type;
	}


	public long getStartTimestamp() {
		return startTimestamp;
	}

}
