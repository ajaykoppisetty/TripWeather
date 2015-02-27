package org.faudroids.tripweather.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.faudroids.tripweather.geo.WayPoint;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Builds the weather forecast for a given route.
 */
public final class WeatherForecastGenerator {

	private final WeatherService weatherService;
	private final WeatherUtils weatherUtils;

	@Inject
	WeatherForecastGenerator(WeatherService weatherService, WeatherUtils weatherUtils) {
		this.weatherService = weatherService;
		this.weatherUtils = weatherUtils;
	}


	/**
	 * Assumes that each way point is one hour from the next.
	 */
	public Observable<Forecast> createForecast(List<WayPoint> wayPoints) {
		// keep only every nth way point based on accuracy for forecast
		int hoursCount = wayPoints.size();
		int hourInterval;
		if (hoursCount < 5 * 24) hourInterval = 3;
		else if (hoursCount < 16 * 24) hourInterval = 24;
		else throw new RuntimeException("too many way points");
		LinkedList<Forecast.Builder> forecastBuilders = retainEveryNthElement(wayPoints, hourInterval);

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
						return weatherService.getForecast(builder.wayPoint().getLat(), builder.wayPoint().getLng())
								.map(new Func1<ObjectNode, Forecast>() {
									@Override
									public Forecast call(ObjectNode objectNode) {
										return parseWeatherData(builder, objectNode);
									}
								});
					}
				});
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


	private Forecast parseWeatherData(Forecast.Builder builder, ObjectNode weatherData) {
		int idx = builder.timestamp() / 3;
		JsonNode weather = weatherData.path("list").path(idx);
		double temperature = weatherUtils.getTemperature((ObjectNode) weather);
		return builder.temperature(temperature).build();
	}

}
