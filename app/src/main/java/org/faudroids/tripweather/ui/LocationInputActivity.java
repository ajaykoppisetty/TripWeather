package org.faudroids.tripweather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.faudroids.tripweather.R;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_input)
public class LocationInputActivity extends RoboActivity {

	private static final String EXTRA_LOCATION = "EXTRA_LOCATION";

	public static Intent createIntent(Context context, String location) {
		Intent intent = new Intent(context, LocationInputActivity.class);
		intent.putExtra(EXTRA_LOCATION, location);
		return intent;
	}


	@InjectView(R.id.list) RecyclerView list;
	@Inject LocationInputAdapter locationInputAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		list.setHasFixedSize(true);
		list.setLayoutManager(layoutManager);
		list.setAdapter(locationInputAdapter);
    }

}
