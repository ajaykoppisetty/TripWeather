package org.faudroids.tripweather.ui;


import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;

import org.faudroids.tripweather.R;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_details)
public final class DetailsActivity extends RoboFragmentActivity {

	@InjectView(R.id.pager) ViewPager viewPager;
	@InjectView(R.id.tabs) PagerSlidingTabStrip tabStrip;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TabsPagerAdapter pagerAdapter = new TabsPagerAdapter(getApplicationContext(), getSupportFragmentManager());
		viewPager.setAdapter(pagerAdapter);
		tabStrip.setViewPager(viewPager);
	}

}
