package org.faudroids.tripweather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.faudroids.tripweather.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_input_map)
public class MapInputActivity extends RoboActivity implements OnMapReadyCallback {

	public static final String EXTRA_LOCATION = "EXTRA_LOCATION";

	private static final String EXTRA_IS_DESTINATION = "EXTRA_IS_DESTINATION";

	public static Intent createIntent(Context context, boolean isDestination) {
		Intent intent = new Intent(context, MapInputActivity.class);
		intent.putExtra(EXTRA_IS_DESTINATION, isDestination);
		return intent;
	}


	@InjectView(R.id.map) MapView mapView;
	@InjectView(R.id.icon) ImageView markerIconView;
	@InjectView(R.id.select) Button selectButton;
	@InjectView(R.id.cancel) Button cancelButton;


    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setSubtitle(getString(R.string.input_map_hint));

		boolean isDestination = getIntent().getBooleanExtra(EXTRA_IS_DESTINATION, false);
		if (isDestination) {
			getActionBar().setTitle(getString(R.string.input_choose_destination));
			markerIconView.setImageResource(R.drawable.ic_destination_colored);
		} else {
			getActionBar().setTitle(getString(R.string.input_choose_origin));
			markerIconView.setImageResource(R.drawable.ic_location_colored);
		}

		mapView.onCreate(savedInstanceState);
		mapView.getMapAsync(this);

		if (mapView.getMap() == null) selectButton.setEnabled(false);
		selectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LatLng location = mapView.getMap().getCameraPosition().target;
				Intent resultIntent = new Intent();
				resultIntent.putExtra(EXTRA_LOCATION, location);
				setResult(RESULT_OK, resultIntent);
				finish();
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED, new Intent());
				finish();
			}
		});
	}


	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}


	@Override
	public void onPause() {
		mapView.onPause();
		super.onPause();
	}


	@Override
	public void onDestroy() {
		mapView.onDestroy();
		super.onDestroy();
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		mapView.onSaveInstanceState(savedInstanceState);
		super.onSaveInstanceState(savedInstanceState);
	}


	@Override
	public void onMapReady(final GoogleMap googleMap) {
		MapsInitializer.initialize(this);
		googleMap.setMyLocationEnabled(true);
		selectButton.setEnabled(true);
	}

}
