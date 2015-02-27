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
import rx.functions.Func1;
import timber.log.Timber;

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
	 * @param startTimestamp unix start time for this trip
	 * @param wayPoints way points of this tripped paired with their estimated timestamp in minutes (starts from zero!)
	 */
	public Observable<Forecast> createForecast(final long startTimestamp, List<Pair<WayPoint, Long>> wayPoints) {
		// keep only every nth way point based on accuracy for forecast
		int hoursCount = wayPoints.size();
		final ForecastMode forecastMode;
		if (hoursCount < ForecastMode.FIVE_DAYS.getDaysOfForecast() * 24) forecastMode = ForecastMode.FIVE_DAYS;
		else if (hoursCount < ForecastMode.SIXTEEN_DAYS.getDaysOfForecast() * 24) forecastMode = ForecastMode.SIXTEEN_DAYS;
		else throw new RuntimeException("too many way points");
		final LinkedList<Forecast.Builder> forecastBuilders = retainEveryNthElement(startTimestamp, wayPoints, forecastMode.getHourInterval());

		// always add last way point to avoid forecasts with one element only
		Pair<WayPoint, Long> lastWayPoint = wayPoints.get(wayPoints.size() - 1);
		if (!lastWayPoint.first.equals(forecastBuilders.getLast().wayPoint())) {
			forecastBuilders.add(new Forecast.Builder().wayPoint(lastWayPoint.first).timestamp(startTimestamp + lastWayPoint.second * 60));
		}

		// fetch weather data for each way point
		return Observable.from(forecastBuilders)
				.flatMap(new Func1<Forecast.Builder, Observable<Forecast>>() {
					@Override
					public Observable<Forecast> call(final Forecast.Builder builder) {
						Observable<ObjectNode> observable = null;
						switch (forecastMode) {
							case FIVE_DAYS:
								observable = weatherService.getForecast(builder.wayPoint().getLat(), builder.wayPoint().getLng());
								break;
							case SIXTEEN_DAYS:
								int daysCount = Math.min(forecastBuilders.size(), forecastMode.getMaxForecastItems());
								observable = weatherService.getDailyForecast(builder.wayPoint().getLat(), builder.wayPoint().getLat(), daysCount);
						}
						Timber.d("fetching weather data for time " + builder.timestamp());
						return observable
								.map(new Func1<ObjectNode, Forecast>() {
									@Override
									public Forecast call(ObjectNode objectNode) {
										return parseWeatherData(builder, objectNode, forecastMode);
									}
								});
					}
				});
	}


	private LinkedList<Forecast.Builder> retainEveryNthElement(long startTimestamp, List<Pair<WayPoint, Long>> wayPoints, int nth) {
		LinkedList<Forecast.Builder> result = new LinkedList<>();
		Iterator<Pair<WayPoint, Long>> iter = wayPoints.iterator();
		int hours = 0;
		while (iter.hasNext()) {
			Pair<WayPoint, Long> pair = iter.next();
			if (hours % nth == 0) {
				result.add(new Forecast
						.Builder()
						.wayPoint(pair.first)
						.timestamp(startTimestamp + pair.second * 60));
			}
			++hours;
		}
		return result;
	}


	private Forecast parseWeatherData(Forecast.Builder builder, ObjectNode forecast, ForecastMode forecastMode) {
		Timber.d("parsing weather data for builder " + builder.timestamp());

		// search for the two corresponding weather entries
		JsonNode forecastEntries = forecast.path("list");
		JsonNode previousWeather, nextWeather;
		int entryIdx = 0;
		do {
			previousWeather = forecastEntries.path(entryIdx);
			nextWeather = forecastEntries.path(entryIdx + 1);
			if (entryIdx % 10 == 0) Timber.d("in loop with idx " + entryIdx);
			++entryIdx;
		} while (parseTimestamp(nextWeather) < builder.timestamp());

		long previousTimestamp = parseTimestamp(previousWeather);
		long nextTimestamp = parseTimestamp(nextWeather);
		double previousTemp = parseTemperature(previousWeather, forecastMode);
		double nextTemp = parseTemperature(nextWeather, forecastMode);

		double interpolationWeight = (builder.timestamp() - previousTimestamp) / ((double) nextTimestamp - previousTimestamp);
		double temperature = previousTemp + interpolationWeight * (nextTemp - previousTemp);
		Timber.d("builder time = " + builder.timestamp() + ", prev time = " + previousTimestamp + ", next time = " + nextTimestamp);
		Timber.d("previous temp = " + previousTemp + ", next temp " + nextTemp + ", weight = " + interpolationWeight + ", temp = " + temperature);
		return builder.temperature(temperature).build();
	}


	private double parseTemperature(JsonNode data, ForecastMode mode) {
		if (mode.equals(ForecastMode.SIXTEEN_DAYS)) return data.path("temp").path("day").asDouble() - KELVIN_BASE;
		else return data.path("main").path("temp").asDouble() - KELVIN_BASE;
	}


	private long parseTimestamp(JsonNode weatherEntry) {
		return weatherEntry.path("dt").asLong();
	}

}
