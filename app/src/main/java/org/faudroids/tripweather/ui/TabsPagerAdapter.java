package org.faudroids.tripweather.ui;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.android.gms.maps.model.LatLng;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.network.TripData;
import org.faudroids.tripweather.weather.Forecast;

final class TabsPagerAdapter extends FragmentPagerAdapter {
	
	private final Context context;
	private final TripData tripData;

	public TabsPagerAdapter(Context context, FragmentManager manager, TripData tripData) {
		super(manager);
		this.context = context;
		this.tripData = tripData;
	}


	@Override
	public Fragment getItem(int position) {
		switch(position) {
			case 0: return MapFragment.createInstance(
					new LatLng(tripData.getFromLocation().getLat(), tripData.getFromLocation().getLng()),
					tripData.getFromLocation().getDescription(),
					new LatLng(tripData.getToLocation().getLat(), tripData.getToLocation().getLng()),
					tripData.getToLocation().getDescription(),
					tripData.getRoute().path("routes").path(0).path("overview_polyline").path("points").asText());

			case 1: return GraphFragment.createInstance(
					tripData.getForecasts().toArray(new Forecast[tripData.getForecasts().size()]));
		}
		return null;
	}


	@Override
	public int getCount() {
		return 2;
	}


	@Override
	public CharSequence getPageTitle(int position) {
		switch(position) {
			case 0: return context.getString(R.string.tab_map);
			case 1: return context.getString(R.string.tab_temperature);
		}
		return null;
	}
}
