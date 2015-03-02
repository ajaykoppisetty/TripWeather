package org.faudroids.tripweather.ui;

import android.os.Bundle;
import android.view.MenuItem;

import roboguice.activity.RoboActivity;

abstract class AbstractBackActivity extends RoboActivity {


	@Override
	public void onCreate(Bundle savedInstaceState) {
		super.onCreate(savedInstaceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
