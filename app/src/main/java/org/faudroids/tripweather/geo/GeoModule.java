package org.faudroids.tripweather.geo;


import android.content.Context;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import org.faudroids.tripweather.R;

import javax.inject.Inject;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;
import roboguice.inject.ContextSingleton;

public final class GeoModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(PlacesService.class).toProvider(PlacesServiceProvider.class);
        binder.bind(DirectionsService.class).toProvider(DirectionsServiceProvider.class);
	}


	@Provides
	@ContextSingleton
	@Inject
	public GeoCodingService provideGeoCodingService(final Context context) {
		RestAdapter adapter = new RestAdapter.Builder()
				.setEndpoint(context.getString(R.string.google_geocoding_api_base_url))
				.setConverter(new JacksonConverter())
				.setRequestInterceptor(new RequestInterceptor() {
					@Override
					public void intercept(RequestFacade request) {
						request.addQueryParam("key", context.getString(R.string.google_places_key));
					}
				})
				.build();
		return adapter.create(GeoCodingService.class);
	}

}
