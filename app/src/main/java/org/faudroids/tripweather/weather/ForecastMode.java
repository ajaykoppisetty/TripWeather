package org.faudroids.tripweather.weather;

/**
 * Collection of forecast related constants.
 */
public enum ForecastMode {

	FIVE_DAYS(5, 3),
	SIXTEEN_DAYS(16, 24);

	private final int daysOfForecast;
	private final int hourInterval;

	ForecastMode(int daysOfForecast, int hourInterval) {
		this.daysOfForecast = daysOfForecast;
		this.hourInterval = hourInterval;
	}

	public int getDaysOfForecast() {
		return daysOfForecast;
	}

	public int getHourInterval() {
		return hourInterval;
	}

	public int getMaxForecastItems() {
		return daysOfForecast * 24 / hourInterval;
	}

}
