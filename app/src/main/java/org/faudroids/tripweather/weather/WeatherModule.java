package org.faudroids.tripweather.weather;


import com.google.inject.Binder;
import com.google.inject.Module;

public final class WeatherModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(WeatherService.class).toProvider(WeatherServiceProvider.class);
	}

}
