package org.faudroids.tripweather.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.geo.PlacesService;

public final class LocationInputAdapter extends RecyclerView.Adapter<LocationInputAdapter.AbstractViewHolder> {

	private final Activity context;
	private final PlacesService placesService;
	private final LocationListener locationListener;
	private final String currentLocation;
	private final boolean chooseFrom;

	public LocationInputAdapter(
			Activity context,
			PlacesService placesService,
			LocationListener locationListener,
			String currentLocation,
			boolean chooseFrom) {

		this.context = context;
		this.placesService = placesService;
		this.locationListener = locationListener;
		this.currentLocation = currentLocation;
		this.chooseFrom = chooseFrom;
	}


	@Override
	public AbstractViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		LayoutInflater inflater = LayoutInflater
				.from(viewGroup.getContext());
		switch(viewType) {
			case InputViewHolder.VIEW_TYPE:
				return new InputViewHolder(context, inflater.inflate(R.layout.card_input, viewGroup, false));
			case CommonLocationsViewHolder.VIEW_TYPE:
				return new CommonLocationsViewHolder(context, inflater.inflate(R.layout.card_locations_common, viewGroup, false));
		}
		return null;
	}


	@Override
	public int getItemViewType(int position) {
		switch(position) {
			case 0: return InputViewHolder.VIEW_TYPE;
			case 1: return CommonLocationsViewHolder.VIEW_TYPE;
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


	private final class InputViewHolder extends AbstractViewHolder {

		private static final int VIEW_TYPE = 0;
		private final AutoCompleteTextView autocompleteTextView;

		public InputViewHolder(Activity context, View view) {
			super(context, view);
			this.autocompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.input);
		}


		@Override
		public void onBindViewHolder() {
			PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(context, android.R.layout.simple_dropdown_item_1line, placesService);
			if ("".equals(autocompleteTextView.getText().toString())) autocompleteTextView.setText(currentLocation);
			autocompleteTextView.setSelection(autocompleteTextView.getText().length());
			if (chooseFrom) autocompleteTextView.setHint(context.getString(R.string.input_choose_origin));
			else autocompleteTextView.setHint(context.getString(R.string.input_choose_destination));
			autocompleteTextView.setAdapter(adapter);
			autocompleteTextView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (event.getRawX() >= (autocompleteTextView.getRight() - autocompleteTextView.getCompoundDrawables()[2].getBounds().width())) {
							autocompleteTextView.setText("");
							autocompleteTextView.dismissDropDown();
							return true;
						}
					}
					return false; }
			});
			autocompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (EditorInfo.IME_ACTION_SEARCH == actionId) {
						locationListener.onLocationSelected(autocompleteTextView.getText().toString());
						return true;
					}
					return false;
				}
			});
		}

	}


	private final class CommonLocationsViewHolder extends AbstractViewHolder {

		private static final int VIEW_TYPE = 1;

		private final View yourLocation;
		private final View mapLocation;

		public CommonLocationsViewHolder(Activity context, View view) {
			super(context, view);
			this.yourLocation = view.findViewById(R.id.your_location);
			this.mapLocation = view.findViewById(R.id.map_location);
		}


		@Override
		public void onBindViewHolder() {
			yourLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					locationListener.onLocationSelected(context.getString(R.string.input_your_location));
				}
			});
			mapLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = MapInputActivity.createIntent(context, !chooseFrom);
					context.startActivityForResult(intent, LocationInputActivity.REQUEST_LOCATION_FROM_MAP);
				}
			});

		}

	}

}
