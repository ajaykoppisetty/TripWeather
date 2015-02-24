package org.faudroids.tripweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.faudroids.tripweather.R;
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
	@InjectView(R.id.card_list) RecyclerView cardList;
	// @InjectView(R.id.start) Button startButton;
	@Inject PlacesService placesService;

	private ArrayAdapter<PlacesLocation> fromAdapter, toAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		fromAdapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item, placesService);
		toAdapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item, placesService);
        autoCompleteFrom.setAdapter(fromAdapter);
        autoCompleteTo.setAdapter(toAdapter);
		// startButton.setOnClickListener(this);

		cardList.setHasFixedSize(true);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		cardList.setLayoutManager(layoutManager);
		cardList.setAdapter(new InputAdapter());
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
