package org.faudroids.tripweather.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.network.TripData;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_details)
public final class DetailsActivity extends RoboFragmentActivity {

	private static final String EXTRA_TRIP_DATA = "EXTRA_TRIP_DATA";

	public static Intent createIntent(Context context, TripData tripData) {
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(EXTRA_TRIP_DATA, tripData);
		return intent;
	}


	@InjectView(R.id.pager) ViewPager viewPager;
	@InjectView(R.id.tabs) PagerSlidingTabStrip tabStrip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TripData tripData = getIntent().getParcelableExtra(EXTRA_TRIP_DATA);
		TabsPagerAdapter pagerAdapter = new TabsPagerAdapter(getApplicationContext(), getSupportFragmentManager(), tripData);
		viewPager.setAdapter(pagerAdapter);
		tabStrip.setViewPager(viewPager);
	}

}
