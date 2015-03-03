package org.faudroids.tripweather.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.geo.Location;
import org.faudroids.tripweather.network.TripData;

import java.util.LinkedList;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import timber.log.Timber;


public class MapFragment extends RoboFragment implements OnMapReadyCallback {

	private static final String
			EXTRA_TRIP_DATA = "EXTRA_TRIP_DATA";

	public static MapFragment createInstance(TripData tripData) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(EXTRA_TRIP_DATA, tripData);

		MapFragment fragment = new MapFragment();
		fragment.setArguments(bundle);
		return fragment;
	}


	@InjectView(R.id.map) MapView mapView;
	@InjectView(R.id.copyrights) TextView copyrightsView;
	@InjectView(R.id.warnings) TextView warningsView;

	private LatLng fromLocation, toLocation;
	private String fromTitle, toTitle;
	private String encodedRoute;


    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_map, container, false);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mapView.onCreate(savedInstanceState);
		mapView.getMapAsync(this);

		TripData tripData = getArguments().getParcelable(EXTRA_TRIP_DATA);
		JsonNode routeData = tripData.getRoute().path("routes").path(0);

		this.fromLocation = convertLocationToLatLng(tripData.getFromLocation());
		this.fromTitle = tripData.getFromLocation().getDescription();
		this.toLocation = convertLocationToLatLng(tripData.getToLocation());
		this.toTitle = tripData.getToLocation().getDescription();
		this.encodedRoute = routeData.path("overview_polyline").path("points").asText();

		copyrightsView.setText(routeData.path("copyrights").asText());

		List<String> warnings = new LinkedList<>();
		for (JsonNode warning : routeData.path("warnings")) warnings.add(warning.asText());

		if (warnings.size() > 0) {
			final String warningTitle = getResources().getQuantityText(R.plurals.warnings, warnings.size()).toString();
			final StringBuilder warningMsg = new StringBuilder();
			for (String warning : warnings) warningMsg.append(warning).append("\n");

			warningsView.setText(warningTitle);
			warningsView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(MapFragment.this.getActivity())
							.setTitle(warningTitle)
							.setMessage(warningMsg.toString())
							.setPositiveButton(android.R.string.ok, null)
							.show();
				}
			});
		}

		Timber.d("set copyright to " + copyrightsView.getText());
    }


	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}


	@Override
	public void onPause() {
		if (mapView != null) mapView.onPause();
		super.onPause();
	}


	@Override
	public void onDestroy() {
		if (mapView != null) mapView.onDestroy();
		super.onDestroy();
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (mapView != null) mapView.onSaveInstanceState(savedInstanceState);
		super.onSaveInstanceState(savedInstanceState);
	}


	@Override
	public void onMapReady(GoogleMap googleMap) {
		MapsInitializer.initialize(getActivity()); // required for the CameraUpdateFactory to be initialized
		googleMap.clear();
		googleMap.setMyLocationEnabled(true);

		googleMap.addMarker(new MarkerOptions().position(fromLocation).title(fromTitle));
		googleMap.addMarker(new MarkerOptions().position(toLocation).title(toTitle));

		List<LatLng> route = PolyUtil.decode(encodedRoute);
		googleMap.addPolyline(new PolylineOptions().addAll(route).color(R.color.green));

		LatLngBounds bounds = new LatLngBounds.Builder()
				.include(fromLocation)
				.include(toLocation)
				.build();
		googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
	}


	private LatLng convertLocationToLatLng(Location location) {
		return new LatLng(location.getLat(), location.getLng());
	}
}
