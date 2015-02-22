package org.faudroids.tripweather.weather;


import com.fasterxml.jackson.databind.node.ObjectNode;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface WeatherService {

	static final String
			WEATHER_URL = "/weather",
			FORECAST_URL = "/forecast",
			FORECAST_DAILY_URL = FORECAST_URL + "/daily";


	/**
	 * Returns the weather for one location at this very moment.
	 */
	@GET(WEATHER_URL)
	public void getCurrentWeather(
			@Query("lat") double latitude,
			@Query("lon") double longitude,
			Callback<ObjectNode> callback);

	@GET(WEATHER_URL)
	public ObjectNode getCurrentWeather(
			@Query("lat") double latitude,
			@Query("lon") double longitude);


	/**
	 *
	 * Returns an 5 days forecast for one location at an interval of 3 hours.
	 */
	@GET(FORECAST_URL)
	public void getForecast(
			@Query("lat") double latitude,
			@Query("lon") double longitude,
			Callback<ObjectNode> callback);

	public ObjectNode getForecast(
			@Query("lat") double latitude,
			@Query("lon") double longitude);


	/**
	 * Returns an up to 16 days daily forecast for one location.
	 * @param count How many days to include. Must be equal or less than 16.
	 */
	@GET(FORECAST_DAILY_URL)
	public void getDailyForecast(
			@Query("lat") double latitude,
			@Query("lon") double longitude,
			@Query("cnt") int count,
			Callback<ObjectNode> callback);

	@GET(FORECAST_DAILY_URL)
	public ObjectNode getDailyForecast(
			@Query("lat") double latitude,
			@Query("lon") double longitude,
			@Query("cnt") int count);

}
