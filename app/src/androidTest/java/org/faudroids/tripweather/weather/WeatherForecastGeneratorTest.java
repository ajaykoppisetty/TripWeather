package org.faudroids.tripweather.weather;


import android.test.AndroidTestCase;
import android.util.Pair;

import org.faudroids.tripweather.geo.WayPoint;

import java.util.Arrays;
import java.util.List;

import rx.functions.Action1;

public final class WeatherForecastGeneratorTest extends AndroidTestCase {

	private WeatherForecastGenerator forecastGenerator;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		WeatherService weatherService = new WeatherModule().provideWeatherService(getContext());
		this.forecastGenerator = new WeatherForecastGenerator(weatherService);
	}


	public void testSimpleForecast() {
		List<Pair<WayPoint, Long>> wayPoints = Arrays.asList(
				new Pair<>(new WayPoint(49.6539419, 11.1750675), 0l),
				new Pair<>(new WayPoint(49.6539429, 11.1750675), 60l),
				new Pair<>(new WayPoint(49.6539439, 11.1750675), 120l),
				new Pair<>(new WayPoint(49.6539449, 11.1750675), 180l),
				new Pair<>(new WayPoint(49.6539459, 11.1750675), 240l),
				new Pair<>(new WayPoint(49.6539469, 11.1750675), 243l));

		forecastGenerator.createForecast(wayPoints)
				.subscribe(new Action1<Forecast>() {
					@Override
					public void call(Forecast forecast) {
						forecast.getTemperature();
					}
				});
	}

}
