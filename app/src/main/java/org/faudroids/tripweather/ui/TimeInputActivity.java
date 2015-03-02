package org.faudroids.tripweather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.faudroids.tripweather.R;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_input)
public class TimeInputActivity extends AbstractBackActivity implements TimeListener {

	static final String EXTRA_TIME = "EXTRA_TIME";

	private static final String STATE_CURRENT_TIME = "currentTime";

	public static Intent createIntent(Context context, long time) {
		Intent intent = new Intent(context, TimeInputActivity.class);
		intent.putExtra(EXTRA_TIME, time);
		return intent;
	}


	@InjectView(R.id.list) RecyclerView list;
	private long currentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		if (savedInstanceState != null) currentTime = savedInstanceState.getLong(STATE_CURRENT_TIME);
		else currentTime = getIntent().getLongExtra(EXTRA_TIME, 0);
		if (currentTime == 0) currentTime = System.currentTimeMillis() / 1000;

		getActionBar().setTitle(getString(R.string.input_choose_time));

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
		list.setHasFixedSize(true);
		list.setLayoutManager(layoutManager);
		list.setAdapter(new TimeInputAdapter(this, this, currentTime));
    }


	@Override
	public void onBackPressed() {
		Intent resultIntent = new Intent();
		setResult(RESULT_CANCELED, resultIntent);
		finish();
		super.onBackPressed();
	}


	@Override
	public void onTimeSelected() {
		returnTimeResult(currentTime);
	}


	@Override
	public void onTimeChanged(long time) {
		currentTime = time;
	}


	private void returnTimeResult(long time) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_TIME, time);
		setResult(RESULT_OK, resultIntent);
		finish();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_input, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.done:
				returnTimeResult(currentTime);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putLong(STATE_CURRENT_TIME, currentTime);
	}

}
