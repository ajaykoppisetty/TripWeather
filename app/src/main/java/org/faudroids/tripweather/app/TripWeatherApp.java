package org.faudroids.tripweather.app;


import android.app.Application;

import com.crashlytics.android.Crashlytics;

import org.faudroids.tripweather.BuildConfig;
import org.faudroids.tripweather.geo.GeoModule;
import org.faudroids.tripweather.weather.WeatherModule;

import io.fabric.sdk.android.Fabric;
import roboguice.RoboGuice;
import timber.log.Timber;

public final class TripWeatherApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		RoboGuice.getOrCreateBaseApplicationInjector(
				this,
				RoboGuice.DEFAULT_STAGE,
				RoboGuice.newDefaultRoboModule(this),
				new WeatherModule(),
				new GeoModule());

		if (BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		} else {
			Fabric.with(this, new Crashlytics());
			Timber.plant(new CrashReportingTree());
		}
	}


	private static final class CrashReportingTree extends Timber.HollowTree {

		@Override
		public void e(String msg, Object... args) {
			Crashlytics.log(msg);
		}

		@Override
		public void e(Throwable e, String msg, Object... args) {
			Crashlytics.log(msg);
			Crashlytics.logException(e);
		}
	}

}
