package org.faudroids.tripweather.geo;


import android.content.Context;

import com.google.inject.Provider;

import org.faudroids.tripweather.R;

import javax.inject.Inject;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;
import roboguice.inject.ContextSingleton;


public final class PlacesServiceProvider implements Provider<PlacesService> {

	private final Context context;

	@Inject
	public PlacesServiceProvider(Context context) {
		this.context = context;
	}


	@Override
	@ContextSingleton
	public PlacesService get() {
		RestAdapter adapter = new RestAdapter.Builder()
				.setEndpoint(context.getString(R.string.google_places_base_url))
				.setConverter(new JacksonConverter())
				.setRequestInterceptor(new RequestInterceptor() {
					@Override
					public void intercept(RequestFacade request) {
						request.addQueryParam("key", context.getString(R.string.google_places_key));
					}
				})
				.build();
		return adapter.create(PlacesService.class);
	}

}
