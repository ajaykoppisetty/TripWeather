package org.faudroids.tripweather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.directions.PlacesService;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_input)
public class LocationInputActivity extends RoboActivity implements LocationListener {

	static final String EXTRA_LOCATION = "EXTRA_LOCATION";
	static final String EXTRA_FROM = "EXTRA_FROM";

	public static Intent createIntent(Context context, String location, boolean from) {
		Intent intent = new Intent(context, LocationInputActivity.class);
		intent.putExtra(EXTRA_LOCATION, location);
		intent.putExtra(EXTRA_FROM, from);
		return intent;
	}


	@InjectView(R.id.list) RecyclerView list;
	@Inject PlacesService placesService;
	private String currentLocation;
	private boolean chooseFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		currentLocation = getIntent().getStringExtra(EXTRA_LOCATION);
		chooseFrom = getIntent().getBooleanExtra(EXTRA_FROM, false);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		list.setHasFixedSize(true);
		list.setLayoutManager(layoutManager);
		list.setAdapter(new LocationInputAdapter(placesService, this, currentLocation, chooseFrom));
    }


	@Override
	public void onBackPressed() {
		Intent resultIntent = new Intent();
		setResult(RESULT_CANCELED, resultIntent);
		finish();
		super.onBackPressed();
	}


	@Override
	public void onLocationSelected(String location) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_LOCATION, location);
		resultIntent.putExtra(EXTRA_FROM, chooseFrom);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

}
