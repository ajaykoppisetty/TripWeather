package org.faudroids.tripweather.weather;

import android.util.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.faudroids.tripweather.geo.WayPoint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Builds the weather forecast for a given route.
 */
public final class WeatherForecastGenerator {

	private static final float KELVIN_BASE = 273.15f;

	private final WeatherService weatherService;

	@Inject
	WeatherForecastGenerator(WeatherService weatherService) {
		this.weatherService = weatherService;
	}


	/**
	 * Assumes that each way point is one hour from the next.
	 */
	public Observable<Forecast> createForecast(List<Pair<WayPoint, Long>> wayPoints) {
		System.out.println(wayPoints.size());
		/*
		// keep only every nth way point based on accuracy for forecast
		int hoursCount = wayPoints.size();
		final ForecastMode forecastMode;
		if (hoursCount < ForecastMode.FIVE_DAYS.getDaysOfForecast() * 24) forecastMode = ForecastMode.FIVE_DAYS;
		else if (hoursCount < ForecastMode.SIXTEEN_DAYS.getDaysOfForecast() * 24) forecastMode = ForecastMode.SIXTEEN_DAYS;
		else throw new RuntimeException("too many way points");
		final LinkedList<Forecast.Builder> forecastBuilders = retainEveryNthElement(wayPoints, forecastMode.getHourInterval());

		// always add last way point to avoid forecasts with one element only
		WayPoint lastWayPoint = wayPoints.get(wayPoints.size() - 1);
		if (!lastWayPoint.equals(forecastBuilders.getLast().wayPoint())) {
			forecastBuilders.add(new Forecast.Builder().wayPoint(lastWayPoint).timestamp(wayPoints.size() - 1));
		}

		// fetch weather data for each way point
		return Observable.from(forecastBuilders)
				.flatMap(new Func1<Forecast.Builder, Observable<Forecast>>() {
					@Override
					public Observable<Forecast> call(final Forecast.Builder builder) {
						Observable<ObjectNode> observable = null;
						switch(forecastMode) {
							case FIVE_DAYS:
								observable =  weatherService.getForecast(builder.wayPoint().getLat(), builder.wayPoint().getLng());
								break;
							case SIXTEEN_DAYS:
								int daysCount = Math.min(forecastBuilders.size(), forecastMode.getMaxForecastItems());
								observable = weatherService.getDailyForecast(builder.wayPoint().getLat(), builder.wayPoint().getLat(), daysCount);
						}
						return observable
								.map(new Func1<ObjectNode, Forecast>() {
									@Override
									public Forecast call(ObjectNode objectNode) {
										return parseWeatherData(builder, objectNode, forecastMode);
									}
								});
					}
				});
				*/
		return null;
	}


	private LinkedList<Forecast.Builder> retainEveryNthElement(List<WayPoint> wayPoints, int nth) {
		LinkedList<Forecast.Builder> result = new LinkedList<>();
		Iterator<WayPoint> iter = wayPoints.iterator();
		int hours = 0;
		while (iter.hasNext()) {
			WayPoint wayPoint = iter.next();
			if (hours % nth == 0) {
				result.add(new Forecast
						.Builder()
						.wayPoint(wayPoint)
						.timestamp(hours));
			}
			++hours;
		}
		return result;
	}


	private Forecast parseWeatherData(Forecast.Builder builder, ObjectNode forecast, ForecastMode forecastMode) {
		int idx = builder.timestamp() / forecastMode.getHourInterval();
		JsonNode singleForecast = forecast.path("list").path(idx);
		double temperature = 0;
		if (forecastMode.equals(ForecastMode.FIVE_DAYS)) temperature = parseHourlyTemperature(singleForecast);
		else if (forecastMode.equals(ForecastMode.SIXTEEN_DAYS)) temperature = parseDailyTemperature(singleForecast);
		return builder.temperature(temperature).build();
	}


	private double parseDailyTemperature(JsonNode dayData) {
		return dayData.path("temp").path("day").asDouble() - KELVIN_BASE;
	}


	private double parseHourlyTemperature(JsonNode hourData) {
		return hourData.path("main").path("temp").asDouble() - KELVIN_BASE;
	}

}
