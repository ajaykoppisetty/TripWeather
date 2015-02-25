package org.faudroids.tripweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import org.faudroids.tripweather.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity implements View.OnClickListener {

	private static final int LOCATION_REQUEST = 42;

	@InjectView(R.id.input_from) TextView textFrom;
	@InjectView(R.id.input_to) TextView textTo;
	@InjectView(R.id.map) MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mapView.onCreate(savedInstanceState);
		textFrom.setOnClickListener(this);
		textTo.setOnClickListener(this);
    }


	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();

		GoogleMap map = mapView.getMap();
		if (map != null) {
			map.setMyLocationEnabled(true);
			map.getUiSettings().setMyLocationButtonEnabled(true);
			MapsInitializer.initialize(this);
		}
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
	public void onClick(View view) {
		String currentLocation = ((TextView) view).getText().toString();
		boolean fromInput;
		fromInput = (R.id.input_from == view.getId());
		Intent intent = LocationInputActivity.createIntent(this, currentLocation, fromInput);
		startActivityForResult(intent, LOCATION_REQUEST);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != LOCATION_REQUEST) return;
		if (resultCode != RESULT_OK) return;
		String location = data.getExtras().getString(LocationInputActivity.EXTRA_LOCATION);
		boolean fromInput = data.getBooleanExtra(LocationInputActivity.EXTRA_FROM, false);
		if (fromInput) textFrom.setText(location);
		else textTo.setText(location);
	}

}
