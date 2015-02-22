package org.faudroids.tripweather.ui;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.directions.PlacesLocation;
import org.faudroids.tripweather.directions.PlacesService;
import org.faudroids.tripweather.weather.WeatherService;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.SafeAsyncTask;
import timber.log.Timber;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity implements View.OnClickListener {

	@InjectView(R.id.autocomplete_from) AutoCompleteTextView autoCompleteFrom;
	@InjectView(R.id.autocomplete_to) AutoCompleteTextView autoCompleteTo;
	@InjectView(R.id.start) Button startButton;
	@Inject WeatherService weatherService;
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
		final String fromPlaceId = fromAdapter.getItem(0).getId();
		final String toPlaceId = toAdapter.getItem(0).getId();
		new SafeAsyncTask<Pair<ObjectNode, ObjectNode>>() {

			@Override
			public Pair<ObjectNode, ObjectNode> call() throws Exception {
				Pair<Double, Double> fromLocation = getCoordinatesFromDetails(placesService.getDetails(fromPlaceId));
				Pair<Double, Double> toLocation = getCoordinatesFromDetails(placesService.getDetails(toPlaceId));
				ObjectNode fromWeather = weatherService.getCurrentWeather(fromLocation.first, fromLocation.second);
				ObjectNode toWeather = weatherService.getCurrentWeather(toLocation.first, toLocation.second);
				return new Pair<>(fromWeather, toWeather);
			}

			@Override
			public void onSuccess(Pair<ObjectNode, ObjectNode> weatherDetails) {
				String fromWeather = weatherDetails.first.get("weather").get(0).get("main").asText();
				String toWeather = weatherDetails.second.get("weather").get(0).get("main").asText();
				Toast.makeText(
						MainActivity.this,
						"The weather is \"" + fromWeather + "\" were you are coming from and \"" +
								toWeather + "\" where you are going to",
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onException(Exception e) {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}

		}.execute();
	}


	private Pair<Double, Double> getCoordinatesFromDetails(ObjectNode placesDetails) {
		JsonNode location = placesDetails.get("result").get("geometry").get("location");
		Timber.i(placesDetails.toString());
		System.out.println(placesDetails);
		return new Pair<>(
				location.get("lat").asDouble(),
				location.get("lng").asDouble());
	}

}
