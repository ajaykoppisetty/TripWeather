package org.faudroids.tripweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.directions.DirectionsService;
import org.faudroids.tripweather.directions.DirectionsServiceCallback;
import org.faudroids.tripweather.directions.PlacesLocation;
import org.faudroids.tripweather.directions.PlacesService;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_input)
public class MainActivity extends RoboActivity implements View.OnClickListener {

	@InjectView(R.id.input_from) AutoCompleteTextView autoCompleteFrom;
	@InjectView(R.id.input_to) AutoCompleteTextView autoCompleteTo;
	@InjectView(R.id.map) MapView mapView;
	@Inject PlacesService placesService;
    @Inject DirectionsService directionsService;

	private ArrayAdapter<PlacesLocation> fromAdapter, toAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		fromAdapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item, placesService);
		toAdapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item, placesService);
        autoCompleteFrom.setAdapter(fromAdapter);
        autoCompleteTo.setAdapter(toAdapter);
		mapView.onCreate(savedInstanceState);
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
	public void onClick(View v) {
		// String fromPlaceId = fromAdapter.getItem(0).getId();
		// String toPlaceId = toAdapter.getItem(0).getId();
		// Intent intent = GraphActivity.createIntent(this, fromPlaceId, toPlaceId);

        directionsService.getRoute("Erlangen", "München", new DirectionsServiceCallback());
		Intent intent = GraphActivity.createIntent(this, "", "");
		startActivity(intent);
	}

}
