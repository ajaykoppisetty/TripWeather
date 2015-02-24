package org.faudroids.tripweather.app;


import android.app.Application;

import org.faudroids.tripweather.directions.DirectionsModule;
import org.faudroids.tripweather.weather.WeatherModule;

import roboguice.RoboGuice;
import timber.log.Timber;

public final class TripWeatherApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Timber.plant(new Timber.DebugTree());
		RoboGuice.getOrCreateBaseApplicationInjector(
				this,
				RoboGuice.DEFAULT_STAGE,
				RoboGuice.newDefaultRoboModule(this),
				new WeatherModule(),
				new DirectionsModule());
	}

}
