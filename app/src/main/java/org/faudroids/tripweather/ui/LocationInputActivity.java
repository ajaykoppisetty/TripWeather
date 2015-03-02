package org.faudroids.tripweather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.geo.LocationUtils;
import org.faudroids.tripweather.geo.PlacesService;

import javax.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_input)
public class LocationInputActivity extends AbstractBackActivity implements LocationListener {

	static final int REQUEST_LOCATION_FROM_MAP = 42;

	static final String EXTRA_LOCATION = "EXTRA_LOCATION";
	static final String EXTRA_FROM = "EXTRA_FROM";

	private static final String STATE_CURRENT_LOCATION = "currentLocation";

	public static Intent createIntent(Context context, String location, boolean from) {
		Intent intent = new Intent(context, LocationInputActivity.class);
		intent.putExtra(EXTRA_LOCATION, location);
		intent.putExtra(EXTRA_FROM, from);
		return intent;
	}


	@InjectView(R.id.list) RecyclerView list;
	@Inject PlacesService placesService;
	@Inject LocationUtils locationUtils;
	private String currentLocation;
	private boolean chooseFrom;
	private MenuItem doneMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		if (savedInstanceState != null) currentLocation = savedInstanceState.getString(STATE_CURRENT_LOCATION);
		else currentLocation = getIntent().getStringExtra(EXTRA_LOCATION);
		chooseFrom = getIntent().getBooleanExtra(EXTRA_FROM, false);

		if (chooseFrom) getActionBar().setTitle(getString(R.string.input_choose_origin));
		else getActionBar().setTitle(getString(R.string.input_choose_destination));

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
	public void onLocationSelected() {
		returnLocationResult(currentLocation);
	}


	@Override
	public void onLocationChanged(String location) {
		currentLocation = location;
		toggleDoneMenuItem();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED) return;
		switch(requestCode) {
			case REQUEST_LOCATION_FROM_MAP:
				LatLng location = data.getParcelableExtra(MapInputActivity.EXTRA_LOCATION);
				returnLocationResult(locationUtils.encodeCoordiantes(location.latitude, location.longitude));
		}
	}


	private void returnLocationResult(String location) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_LOCATION, location);
		resultIntent.putExtra(EXTRA_FROM, chooseFrom);
		setResult(RESULT_OK, resultIntent);
		finish();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_input, menu);
		doneMenuItem = menu.findItem(R.id.done);
		toggleDoneMenuItem();
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.done:
				returnLocationResult(currentLocation);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void toggleDoneMenuItem() {
		if (currentLocation != null && !currentLocation.equals("")) {
			doneMenuItem.setEnabled(true);
			doneMenuItem.getIcon().setAlpha(255);
		}
		else {
			doneMenuItem.setEnabled(false);
			doneMenuItem.getIcon().setAlpha(130);
		}
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString(STATE_CURRENT_LOCATION, currentLocation);
	}

}
