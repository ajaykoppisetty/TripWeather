package org.faudroids.tripweather.ui;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.directions.PlacesService;
import org.faudroids.tripweather.weather.WeatherService;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity {

	@InjectView(R.id.autocomplete_from) AutoCompleteTextView autoCompleteFrom;
	@InjectView(R.id.autocomplete_to) AutoCompleteTextView autoCompleteTo;
	@Inject WeatherService weatherService;
	@Inject PlacesService placesService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autoCompleteFrom.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item, placesService));
        autoCompleteTo.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item, placesService));

		// small example of how to use the weather service
		weatherService.getCurrentWeather(49.5778294, 10.9942273, new Callback<JsonNode>() {
			@Override
			public void success(JsonNode jsonNode, Response response) {
				String weather = jsonNode.get("weather").get(0).get("main").asText();
				Toast.makeText(
						MainActivity.this,
						"The weather in Erlangen is \"" + weather + "\" today",
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(
						MainActivity.this,
						"Failed to get weather: " + error.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		});
    }

}
