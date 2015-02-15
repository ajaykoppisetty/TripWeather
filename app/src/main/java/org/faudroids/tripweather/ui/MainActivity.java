package org.faudroids.tripweather.ui;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;

import org.faudroids.tripweather.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity {

	@InjectView(R.id.autocomplete_from) AutoCompleteTextView autoCompleteFrom;
	@InjectView(R.id.autocomplete_to) AutoCompleteTextView autoCompleteTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autoCompleteFrom.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item));
        autoCompleteTo.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_item));
    }

}
