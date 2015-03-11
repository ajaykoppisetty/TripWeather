package org.faudroids.tripweather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.geo.TravelMode;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_input)
public class TravelModeInputActivity extends AbstractBackActivity implements TravelModeListener {

	static final String EXTRA_TRAVEL_MODE = "EXTRA_TRAVEL_MODE";

	public static Intent createIntent(Context context) {
		return new Intent(context, TravelModeInputActivity.class);
	}


	@InjectView(R.id.list) RecyclerView list;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		getActionBar().setTitle(getString(R.string.input_choose_how));

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		list.setHasFixedSize(true);
		list.setLayoutManager(layoutManager);
		list.setAdapter(new TravelModeInputAdapter(this));
    }


	@Override
	public void onBackPressed() {
		Intent resultIntent = new Intent();
		setResult(RESULT_CANCELED, resultIntent);
		finish();
		super.onBackPressed();
	}


	@Override
	public void onTravelModeSelected(TravelMode travelMode) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_TRAVEL_MODE, travelMode);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

}
