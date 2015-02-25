package org.faudroids.tripweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.directions.PlacesLocation;
import org.faudroids.tripweather.directions.PlacesService;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import timber.log.Timber;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity implements View.OnClickListener {

	private static final int LOCATION_REQUEST = 42;

	@InjectView(R.id.input_from) TextView textFrom;
	@InjectView(R.id.input_to) TextView textTo;
	@InjectView(R.id.map) MapView mapView;
	@Inject PlacesService placesService;

	private PlacesLocation locationFrom, locationTo; // contain the actual lat / lon


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
			updateMarkers();
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
		final String locationDescription = data.getExtras().getString(LocationInputActivity.EXTRA_LOCATION);
		final boolean fromInput = data.getBooleanExtra(LocationInputActivity.EXTRA_FROM, false);

		if (fromInput) {
			textFrom.setText(locationDescription);
			placesService.getTextSearch(locationDescription, new PlacesQueryCallback() {
				@Override
				public void success(ObjectNode objectNode, Response response) {
					locationFrom = parseLocation(objectNode);
					updateMarkers();
				}
			});

		} else {
			textTo.setText(locationDescription);
			placesService.getTextSearch(locationDescription, new PlacesQueryCallback() {
				@Override
				public void success(ObjectNode objectNode, Response response) {
					locationTo = parseLocation(objectNode);
					updateMarkers();
				}
			});

		}
	}


	private void updateMarkers() {
		GoogleMap map = mapView.getMap();
		map.clear();
		map.set
		if (locationFrom != null) updateMarker(map, locationFrom);
		if (locationTo != null) updateMarker(map, locationTo);
		if (locationFrom != null && locationTo != null)  moveCameraToRoute(map);
		else if (locationFrom != null) moveCameraToMarker(locationFrom, map);
		else if (locationTo != null) moveCameraToMarker(locationFrom, map);
	}


	private void updateMarker(GoogleMap map, PlacesLocation location) {
		map.addMarker(new MarkerOptions()
				.position(new LatLng(location.getLat(), location.getLon()))
				.title(location.getDescription()));
	}


	private void moveCameraToRoute(GoogleMap map) {
		LatLngBounds bounds =new LatLngBounds.Builder()
				.include(new LatLng(locationFrom.getLat(), locationFrom.getLon()))
				.include(new LatLng(locationTo.getLat(), locationTo.getLon()))
				.build();
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
	}


	private void moveCameraToMarker(PlacesLocation location, GoogleMap map) {
		Timber.d("Moving camera to " + location.getLat() + " " + location.getLon());
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLat(), location.getLon()), 14));
	}


	private PlacesLocation parseLocation(ObjectNode data) {
		Timber.d(data.toString());
		JsonNode result = data.path("results").path(0);
		String placeId = result.path("place_id").asText();
		String description = result.path("formatted_address").asText();
		JsonNode locationResult = result.path("geometry").path("location");
		double lat = locationResult.path("lat").asDouble();
		double lon = locationResult.path("lng").asDouble();
		return new PlacesLocation(placeId, description, lat, lon);
	}



	private abstract class PlacesQueryCallback implements Callback<ObjectNode> {

		@Override
		public void failure(RetrofitError error) {
			Timber.e(error.getMessage());
			Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

}
