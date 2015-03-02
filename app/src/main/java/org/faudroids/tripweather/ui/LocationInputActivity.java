package org.faudroids.tripweather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.geo.PlacesService;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_input)
public class LocationInputActivity extends RoboActivity implements LocationListener {

	static final int REQUEST_LOCATION_FROM_MAP = 42;

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
		if (chooseFrom) getActionBar().setTitle(getString(R.string.title_start_location));
		else getActionBar().setTitle(getString(R.string.title_destination));

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		list.setHasFixedSize(true);
		list.setLayoutManager(layoutManager);
		list.setAdapter(new LocationInputAdapter(this, placesService, this, currentLocation, chooseFrom));
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
		returnLocationResult(location);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) return;
		switch(requestCode) {
			case REQUEST_LOCATION_FROM_MAP:
				LatLng location = data.getParcelableExtra(MapInputActivity.EXTRA_LOCATION);
				returnLocationResult(location.latitude + "," + location.longitude);
		}
	}


	private void returnLocationResult(String location) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_LOCATION, location);
		resultIntent.putExtra(EXTRA_FROM, chooseFrom);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

}
