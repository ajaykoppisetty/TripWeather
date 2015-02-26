package org.faudroids.tripweather.weather;


import android.content.Context;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import org.faudroids.tripweather.R;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;
import roboguice.inject.ContextSingleton;

public final class WeatherModule implements Module {

	@Override
	public void configure(Binder binder) {
		// nothing to do here for now
	}


	@Provides
	@ContextSingleton
	public WeatherService provideWeatherService(final  Context context) {
		RestAdapter adapter = new RestAdapter.Builder()
				.setEndpoint(context.getString(R.string.open_weather_base_url))
				.setConverter(new JacksonConverter())
				.setRequestInterceptor(new RequestInterceptor() {
					@Override
					public void intercept(RequestFacade request) {
						request.addHeader("x-api-key", context.getString(R.string.open_weather_key));
					}
				})
				.build();
		return adapter.create(WeatherService.class);
	}

}
