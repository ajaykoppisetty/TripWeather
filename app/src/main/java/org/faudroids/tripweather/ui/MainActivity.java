package org.faudroids.tripweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.directions.PlacesLocation;
import org.faudroids.tripweather.directions.PlacesService;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity implements View.OnClickListener {

	@InjectView(R.id.autocomplete_from) AutoCompleteTextView autoCompleteFrom;
	@InjectView(R.id.autocomplete_to) AutoCompleteTextView autoCompleteTo;
	@InjectView(R.id.start) Button startButton;
	@Inject PlacesService placesService;

	private ArrayAdapter<PlacesLocation> fromAdapter, toAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		fromAdapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item, placesService);
		toAdapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item, placesService);
        autoCompleteFrom.setAdapter(fromAdapter);
        autoCompleteTo.setAdapter(toAdapter);
		startButton.setOnClickListener(this);
    }


	@Override
	public void onClick(View v) {
		// String fromPlaceId = fromAdapter.getItem(0).getId();
		// String toPlaceId = toAdapter.getItem(0).getId();
		// Intent intent = GraphActivity.createIntent(this, fromPlaceId, toPlaceId);
		Intent intent = GraphActivity.createIntent(this, "", "");
		startActivity(intent);
	}

}
