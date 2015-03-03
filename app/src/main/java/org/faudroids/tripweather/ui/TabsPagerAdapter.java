package org.faudroids.tripweather.ui;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
		Forecast[] forecasts = tripData.getForecasts().toArray(new Forecast[tripData.getForecasts().size()]);
		switch(position) {
			case 0: return MapFragment.createInstance(tripData);
			case 1: return TemperatureFragment.createInstance(forecasts);
			case 2: return PrecipitationFragment.createInstance(forecasts);
		}
		return null;
	}


	@Override
	public int getCount() {
		return 3;
	}


	@Override
	public CharSequence getPageTitle(int position) {
		switch(position) {
			case 0: return context.getString(R.string.tab_map);
			case 1: return context.getString(R.string.tab_temperature);
			case 2: return context.getString(R.string.tab_precipitation);
		}
		return null;
	}
}
