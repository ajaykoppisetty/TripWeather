package org.faudroids.tripweather.directions;


import com.google.inject.Binder;
import com.google.inject.Module;

public final class DirectionsModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(PlacesService.class).toProvider(PlacesServiceProvider.class);
	}

}
