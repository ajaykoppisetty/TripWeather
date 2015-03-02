package org.faudroids.tripweather.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import org.faudroids.tripweather.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.view.View.*;

public final class TimeInputAdapter extends RecyclerView.Adapter<TimeInputAdapter.AbstractViewHolder> {

	private final Activity context;
	private final TimeListener timeListener;
	private Calendar startTime;

	public TimeInputAdapter(
			Activity context,
			TimeListener locationListener,
			long startTime) {

		this.context = context;
		this.timeListener = locationListener;
		this.startTime = Calendar.getInstance();
		this.startTime.setTime(new Date(startTime * 1000));
	}


	@Override
	public AbstractViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
		switch(viewType) {
			case InputViewHolder.VIEW_TYPE:
				return new InputViewHolder(context, inflater.inflate(R.layout.card_time_input, viewGroup, false));
			case CommonTimesViewHolder.VIEW_TYPE:
				return new CommonTimesViewHolder(context, inflater.inflate(R.layout.card_time_commons, viewGroup, false));
		}
		return null;
	}


	@Override
	public int getItemViewType(int position) {
		switch(position) {
			case 0: return InputViewHolder.VIEW_TYPE;
			case 1: return CommonTimesViewHolder.VIEW_TYPE;
		}
		return -1;
	}


	@Override
	public void onBindViewHolder(AbstractViewHolder viewHolder, int viewType) {
		viewHolder.onBindViewHolder();
	}


	@Override
	public int getItemCount() {
		return 2;
	}



	abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

		protected final Activity context;

		public AbstractViewHolder(Activity context, View view) {
			super(view);
			this.context = context;
		}

		public abstract void onBindViewHolder();

	}


	private final class InputViewHolder extends AbstractViewHolder implements
			TimePickerDialog.OnTimeSetListener,
			DatePickerDialog.OnDateSetListener {

		private final SimpleDateFormat
				timeFormat = new SimpleDateFormat("HH:mm"),
				dateFormat = new SimpleDateFormat("dd. MMM yyyy");

		private static final int VIEW_TYPE = 0;
		private final TextView timeView, dateView;

		public InputViewHolder(Activity context, View view) {
			super(context, view);
			this.timeView = (TextView) view.findViewById(R.id.time);
			this.dateView = (TextView) view.findViewById(R.id.date);
		}


		@Override
		public void onBindViewHolder() {
			dateView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DatePickerDialog dateDialog = DatePickerDialog.newInstance(
							InputViewHolder.this,
							startTime.get(Calendar.YEAR),
							startTime.get(Calendar.MONTH),
							startTime.get(Calendar.DAY_OF_MONTH));
					dateDialog.setMinDate(Calendar.getInstance());
					dateDialog.setMinDate(Calendar.getInstance());
					Calendar maxDate = Calendar.getInstance();
					maxDate.add(Calendar.DATE, 14);
					dateDialog.setMaxDate(maxDate);
					dateDialog.show(context.getFragmentManager(), "date picker");
				}
			});

			timeView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TimePickerDialog dialog = TimePickerDialog.newInstance(
							InputViewHolder.this,
							startTime.get(Calendar.HOUR_OF_DAY),
							startTime.get(Calendar.MINUTE), true);
					dialog.show(context.getFragmentManager(), "time picker");
				}
			});

			updateTimeViews();
		}


		@Override
		public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
			startTime.set(year, monthOfYear, dayOfMonth);
			timeListener.onTimeChanged(startTime.getTimeInMillis() / 1000);
			updateTimeViews();
		}


		@Override
		public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
			startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			startTime.set(Calendar.MINUTE, minute);
			timeListener.onTimeChanged(startTime.getTimeInMillis() / 1000);
			updateTimeViews();
		}


		private void updateTimeViews() {
			timeView.setText(timeFormat.format(startTime.getTime()));
			dateView.setText(dateFormat.format(startTime.getTime()));
		}

	}


	private final class CommonTimesViewHolder extends AbstractViewHolder {

		private static final int VIEW_TYPE = 1;

		private final View nowView;
		private final View tomorrowView;

		public CommonTimesViewHolder(Activity context, View view) {
			super(context, view);
			this.nowView = view.findViewById(R.id.now);
			this.tomorrowView = view.findViewById(R.id.tomorrow);
		}


		@Override
		public void onBindViewHolder() {
			nowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					timeListener.onTimeChanged(System.currentTimeMillis() / 1000);
					timeListener.onTimeSelected();
				}
			});
			tomorrowView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date());
					calendar.add(Calendar.DATE, 1);
					timeListener.onTimeChanged(calendar.getTimeInMillis() / 1000);
					timeListener.onTimeSelected();
				}
			});

		}

	}

}
