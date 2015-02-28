package org.faudroids.tripweather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;


public class MapFragment extends RoboFragment implements OnMapReadyCallback {

	private static final String
			EXTRA_FROM = "EXTRA_FROM",
			EXTRA_FROM_TITLE = "EXTRA_FROM_TITLE",
			EXTRA_TO = "EXTRA_TO",
			EXTRA_TO_TITLE = "EXTRA_TO_TITLE",
			EXTRA_ROUTE = "EXTRA_ROUTE";

	public static MapFragment createInstance(LatLng fromLocation, String fromTitle, LatLng toLocation, String toTitle, String encodedRoute) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(EXTRA_FROM, fromLocation);
		bundle.putString(EXTRA_FROM_TITLE, fromTitle);
		bundle.putParcelable(EXTRA_TO, toLocation);
		bundle.putString(EXTRA_TO_TITLE, toTitle);
		bundle.putString(EXTRA_ROUTE, encodedRoute);

		MapFragment fragment = new MapFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@InjectView(R.id.map) MapView mapView;
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

		this.fromLocation = getArguments().getParcelable(EXTRA_FROM);
		this.fromTitle = getArguments().getString(EXTRA_FROM_TITLE);
		this.toLocation = getArguments().getParcelable(EXTRA_TO);
		this.toTitle = getArguments().getString(EXTRA_TO_TITLE);
		this.encodedRoute = getArguments().getString(EXTRA_ROUTE);
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
	public void onMapReady(GoogleMap googleMap) {
		MapsInitializer.initialize(getActivity()); // required for the CameraUpdateFactory to be initialized
		googleMap.clear();

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

}
