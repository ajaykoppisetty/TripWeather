package org.faudroids.tripweather.geo;


import android.content.Context;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import org.faudroids.tripweather.R;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;
import roboguice.inject.ContextSingleton;

public final class GeoModule implements Module {

	@Override
	public void configure(Binder binder) {
		// nothing to do for now ...
	}


	@Provides
	@ContextSingleton
	public GeoCodingService provideGeoCodingService(Context context) {
		RestAdapter adapter = createBaseRestAdapter(context)
				.setEndpoint(context.getString(R.string.google_geocoding_api_base_url))
				.build();
		return adapter.create(GeoCodingService.class);
	}


	@Provides
	@ContextSingleton
	public PlacesService providePlacesService(Context context) {
		RestAdapter adapter = createBaseRestAdapter(context)
				.setEndpoint(context.getString(R.string.google_places_api_base_url))
				.build();
		return adapter.create(PlacesService.class);
	}


	@Provides
	@ContextSingleton
	public DirectionsService provideDirectionsService(Context context) {
		RestAdapter restAdapter = createBaseRestAdapter(context)
				.setEndpoint(context.getString(R.string.google_directions_api_base_url))
				.build();
		return restAdapter.create(DirectionsService.class);
	}


	private RestAdapter.Builder createBaseRestAdapter(final Context context) {
		return new RestAdapter.Builder()
				.setConverter(new JacksonConverter())
				.setRequestInterceptor(new RequestInterceptor() {
					@Override
					public void intercept(RequestFacade request) {
						request.addQueryParam("key", context.getString(R.string.google_places_key));
					}
				});
	}

}
