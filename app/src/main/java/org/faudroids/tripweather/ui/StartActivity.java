package org.faudroids.tripweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import org.faudroids.tripweather.R;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Calendar;
import java.util.Date;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_start)
public class StartActivity extends RoboActivity implements
		TimePickerDialog.OnTimeSetListener,
		DatePickerDialog.OnDateSetListener {

	private static final PrettyTime prettyTime = new PrettyTime();

	private static final String
			STATE_FROM = "from",
			STATE_FROM_ICON = "fromIcon",
			STATE_TO = "to",
			STATE_TO_ICON = "toIcon",
			STATE_TIME = "time",
			STATE_TIME_ICON = "timeIcon";

	private static final int
			LOCATION_REQUEST = 42;

	@InjectView(R.id.origin) View originView;
	@InjectView(R.id.origin_description) TextView originDescriptionView;
	@InjectView(R.id.origin_value) AutoResizeTextView originValueView;
	@InjectView(R.id.origin_icon) ImageView originIcon;

	@InjectView(R.id.destination) View destinationView;
	@InjectView(R.id.destination_description) TextView destinationDescriptionView;
	@InjectView(R.id.destination_value) AutoResizeTextView destinationValueView;
	@InjectView(R.id.destination_icon) ImageView destinationIcon;

	@InjectView(R.id.time) View timeView;
	@InjectView(R.id.time_description) TextView timeDescriptionView;
	@InjectView(R.id.time_value) AutoResizeTextView timeValueView;
	@InjectView(R.id.time_icon) ImageView timeIcon;

	private String locationFrom, locationTo;
	private Calendar startTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		updateInputViews();
		originView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this, LocationInputActivity.class);
				intent.putExtra(LocationInputActivity.EXTRA_FROM, true);
				intent.putExtra(LocationInputActivity.EXTRA_LOCATION, locationFrom);
				startActivityForResult(intent, LOCATION_REQUEST);
			}
		});

		destinationView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(StartActivity.this, LocationInputActivity.class);
				intent.putExtra(LocationInputActivity.EXTRA_FROM, false);
				intent.putExtra(LocationInputActivity.EXTRA_LOCATION, locationTo);
				startActivityForResult(intent, LOCATION_REQUEST);
			}
		});

		timeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar time = startTime;
				if (time == null) time = Calendar.getInstance();
				DatePickerDialog dateDialog = DatePickerDialog.newInstance(StartActivity.this, time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH));
				dateDialog.setMinDate(Calendar.getInstance());
				dateDialog.setMinDate(Calendar.getInstance());
				Calendar maxDate = Calendar.getInstance();
				maxDate.add(Calendar.DATE, 16);
				dateDialog.setMaxDate(maxDate);
				dateDialog.show(getFragmentManager(), "date picker");
			}
		});


	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		switch(requestCode) {
			// user has selected location
			case LOCATION_REQUEST:
				String location = data.getExtras().getString(LocationInputActivity.EXTRA_LOCATION);
				boolean isFromLocation = data.getBooleanExtra(LocationInputActivity.EXTRA_FROM, false);
				if (isFromLocation) locationFrom = location;
				else locationTo = location;
				updateInputViews();
				if (isFromLocation) showDoneAnimation(originIcon);
				else showDoneAnimation(destinationIcon);
				break;
		}
	}


	private void showDoneAnimation(final ImageView imageView) {
		if (((Integer) R.drawable.ic_done).equals(imageView.getTag())) return;
		imageView.setTag(R.drawable.ic_done);
		Animation shrinkAnim = AnimationUtils.loadAnimation(this, R.anim.scale_hide);
		final Animation growAnim = AnimationUtils.loadAnimation(this, R.anim.scale_show);
		imageView.setAnimation(shrinkAnim);
		shrinkAnim.setStartOffset(300);
		shrinkAnim.start();
		shrinkAnim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) { }

			@Override
			public void onAnimationRepeat(Animation animation) { }

			@Override
			public void onAnimationEnd(Animation animation) {
				imageView.setImageResource(R.drawable.ic_done);
				imageView.setAnimation(growAnim);
				growAnim.start();
			}
		});
	}


	private void updateInputViews() {
		updateInputView(locationFrom, originDescriptionView, originValueView, R.string.input_from, R.string.input_from_example);
		updateInputView(locationTo, destinationDescriptionView, destinationValueView, R.string.input_to, R.string.input_to_example);
		String formattedTime = (startTime == null) ? null : prettyTime.format(startTime.getTime());
		if ("moments ago".equals(formattedTime)) formattedTime = "now";
		updateInputView(formattedTime, timeDescriptionView, timeValueView, R.string.input_time, R.string.input_time_example);
	}


	private void updateInputView(String location, TextView descriptionView, AutoResizeTextView valueView, int descriptionResource, int valueResource) {
		if (location == null || location.equals("")) {
			descriptionView.setText(getString(descriptionResource, getString(R.string.where)));
			descriptionView.setTextAppearance(this, R.style.MainMenuFontLarge);
			valueView.setText(getString(valueResource));
			valueView.setTextAppearance(this, R.style.MainMenuFontSmall);
			valueView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_menu_font_small));

		} else {
			descriptionView.setText(getString(descriptionResource, ""));
			descriptionView.setTextAppearance(this, R.style.MainMenuFontSmall);
			valueView.setText(location);
			valueView.setTextAppearance(this, R.style.MainMenuFontLarge);
			valueView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_menu_font_large));
		}
		valueView.resetTextSize();
	}


	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
		if (startTime == null) startTime = Calendar.getInstance();
		startTime.set(year, monthOfYear, dayOfMonth);
		TimePickerDialog dialog = TimePickerDialog.newInstance(StartActivity.this, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), true);
		dialog.show(getFragmentManager(), "time picker");
	}


	@Override
	public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
		startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
		startTime.set(Calendar.MINUTE, minute);
		updateInputViews();
		showDoneAnimation(timeIcon);
	}


	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		locationFrom = savedInstanceState.getString(STATE_FROM);
		int fromIconId = savedInstanceState.getInt(STATE_FROM_ICON);
		if (fromIconId != 0) {
			originIcon.setTag(fromIconId);
			originIcon.setImageResource(R.drawable.ic_done);
		}

		locationTo = savedInstanceState.getString(STATE_TO);
		int toIconId = savedInstanceState.getInt(STATE_TO_ICON);
		if (toIconId != 0) {
			destinationIcon.setTag(fromIconId);
			destinationIcon.setImageResource(R.drawable.ic_done);
		}

		if (savedInstanceState.containsKey(STATE_TIME)) {
			startTime = Calendar.getInstance();
			startTime.setTime(new Date(savedInstanceState.getLong(STATE_TIME)));
		}
		int timeIconId = savedInstanceState.getInt(STATE_TIME_ICON);
		if (timeIconId != 0) {
			timeIcon.setTag(timeIconId);
			timeIcon.setImageResource(R.drawable.ic_done);
		}

		updateInputViews();
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString(STATE_FROM, locationFrom);
		if (originIcon.getTag() != null) savedInstanceState.putInt(STATE_FROM_ICON, (int) originIcon.getTag());

		savedInstanceState.putString(STATE_TO, locationTo);
		if (destinationIcon.getTag() != null) savedInstanceState.putInt(STATE_TO_ICON, (int) destinationIcon.getTag());

		if (startTime != null) savedInstanceState.putLong(STATE_TIME, startTime.getTime().getTime());
		if (timeIcon.getTag() != null) savedInstanceState.putInt(STATE_TIME_ICON, (int) timeIcon.getTag());

		super.onSaveInstanceState(savedInstanceState);
	}
}
