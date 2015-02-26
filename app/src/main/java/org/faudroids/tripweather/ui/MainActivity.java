package org.faudroids.tripweather.ui;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.geo.DirectionsService;
import org.faudroids.tripweather.geo.DirectionsServiceCallback;
import org.faudroids.tripweather.geo.PlacesLocation;
import org.faudroids.tripweather.geo.PlacesService;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import timber.log.Timber;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity implements
		View.OnClickListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private static final int
			LOCATION_REQUEST = 42,
			GOOGLE_API_CLIENT_REQUEST = 43;

	@InjectView(R.id.input_from) TextView textFrom;
	@InjectView(R.id.input_to) TextView textTo;
	@InjectView(R.id.map) MapView mapView;
	@Inject PlacesService placesService;
	@Inject DirectionsService directionsService;
	private GoogleApiClient googleApiClient;

	private PlacesLocation locationFrom, locationTo; // contain the actual lat / lon


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		googleApiClient = createGoogleApiClient();
		googleApiClient.connect();

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
		googleApiClient.disconnect();
		super.onDestroy();
	}


	@Override
	public void onClick(View view) {
		String currentLocation = ((TextView) view).getText().toString();
		boolean fromInput;
        directionsService.getRoute("Erlangen", "München", new DirectionsServiceCallback());
		fromInput = (R.id.input_from == view.getId());
		Intent intent = LocationInputActivity.createIntent(this, currentLocation, fromInput);
		startActivityForResult(intent, LOCATION_REQUEST);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		switch(requestCode) {
			// user has selected location
			case LOCATION_REQUEST:
				final String locationDescription = data.getExtras().getString(LocationInputActivity.EXTRA_LOCATION);
				final boolean fromInput = data.getBooleanExtra(LocationInputActivity.EXTRA_FROM, false);
				PlacesLocation selectedLocation = null;

				// handle current user location
				if (getString(R.string.input_your_location).equals(locationDescription)) {
					Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
					if (location != null) {
						selectedLocation = new PlacesLocation("my location", getString(R.string.input_your_location), location.getLatitude(), location.getLongitude());
					} else {
						Toast.makeText(this, getString(R.string.input_your_location_unavailable), Toast.LENGTH_LONG).show();
						Timber.i("Current location unavailable");
						return;
					}
				}

				// handle location query
				if (fromInput) textFrom.setText(locationDescription);
				else textTo.setText(locationDescription);

				if (selectedLocation == null) {
					placesService.getTextSearch(locationDescription, new AbstractCallback() {
						@Override
						public void success(ObjectNode objectNode, Response response) {
							updateSelectedLocation(parseLocation(objectNode), fromInput);
						}
					});
				} else {
					updateSelectedLocation(selectedLocation, fromInput);
				}
				break;

			// resolved google api client connection?
			case GOOGLE_API_CLIENT_REQUEST:
				if (!googleApiClient.isConnected() && !googleApiClient.isConnecting()) {
					googleApiClient.connect();
				}
				break;

		}
	}


	private void updateSelectedLocation(PlacesLocation selectedLocation, boolean fromInput) {
		if (fromInput) locationFrom = selectedLocation;
		else locationTo = selectedLocation;
		updateMarkers();
	}


	private void updateMarkers() {
		GoogleMap map = mapView.getMap();
		map.clear();
		if (locationFrom != null) updateMarker(map, locationFrom);
		if (locationTo != null) updateMarker(map, locationTo);
		if (locationFrom != null && locationTo != null) {
			moveCameraToRoute(map);
			showRoutePolyLine();
		}
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


	private void showRoutePolyLine() {
		String start = locationFrom.getLat() + "," + locationFrom.getLon();
		String end = locationTo.getLat() + "," + locationTo.getLon();
		directionsService.getRoute(start, end, new AbstractCallback() {
			@Override
			public void success(ObjectNode objectNode, Response response) {
				String encodedRoute = objectNode.path("routes").path(0).path("overview_polyline").path("points").asText();
				if (encodedRoute == null || "".equals(encodedRoute)) {
					Timber.w("failed to find polyline for route");
					return;
				}

				List<LatLng> points = PolyUtil.decode(encodedRoute);
				GoogleMap map = mapView.getMap();
				if (map == null) return;
				map.addPolyline(new PolylineOptions()
						.addAll(points)
						.color(R.color.green_dark));
			}
		});
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


	private GoogleApiClient createGoogleApiClient() {
		return new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
	}


	@Override
	public void onConnected(Bundle bundle) {
		Timber.d("Google Api Client connected");
	}


	@Override
	public void onConnectionSuspended(int i) {
		Timber.d("Google Api Client connection suspended (" + i + ")");
	}


	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Timber.w("Google Api Client connection failed, " + connectionResult.getErrorCode());
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, GOOGLE_API_CLIENT_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				Timber.e(e, "failed to start google client api resolution");
				googleApiClient.connect();
			}
		} else {
			GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, GOOGLE_API_CLIENT_REQUEST).show();
		}
	}


	private abstract class AbstractCallback implements Callback<ObjectNode> {

		@Override
		public void failure(RetrofitError error) {
			Timber.e(error.getMessage());
			Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

}
