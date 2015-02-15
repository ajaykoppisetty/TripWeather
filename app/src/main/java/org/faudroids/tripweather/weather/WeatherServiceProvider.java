package org.faudroids.tripweather.weather;


import android.content.Context;

import com.google.inject.Provider;

import org.faudroids.tripweather.R;

import javax.inject.Inject;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;
import roboguice.inject.ContextSingleton;


public final class WeatherServiceProvider implements Provider<WeatherService> {

	private final Context context;

	@Inject
	public WeatherServiceProvider(Context context) {
		this.context = context;
	}


	@Override
	@ContextSingleton
	public WeatherService get() {
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
