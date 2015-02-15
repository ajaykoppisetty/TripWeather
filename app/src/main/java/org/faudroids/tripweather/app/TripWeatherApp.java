package org.faudroids.tripweather.app;


import android.app.Application;

import org.faudroids.tripweather.weather.WeatherModule;

import roboguice.RoboGuice;

public final class TripWeatherApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		RoboGuice.getOrCreateBaseApplicationInjector(
				this,
				RoboGuice.DEFAULT_STAGE,
				RoboGuice.newDefaultRoboModule(this),
				new WeatherModule());
	}

}
