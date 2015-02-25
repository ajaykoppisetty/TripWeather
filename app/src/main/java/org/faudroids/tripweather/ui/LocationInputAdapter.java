package org.faudroids.tripweather.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.directions.PlacesService;

import javax.inject.Inject;

public final class LocationInputAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final Context context;
	private final PlacesService placesService;

	@Inject
	public LocationInputAdapter(Context context, PlacesService placesService) {
		this.context = context;
		this.placesService = placesService;
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		LayoutInflater inflater = LayoutInflater
				.from(viewGroup.getContext());
		switch(viewType) {
			case InputViewHolder.VIEW_TYPE: return new InputViewHolder(inflater.inflate(R.layout.card_input, viewGroup, false));
			case CommonLocationsViewHolder.VIEW_TYPE: return new CommonLocationsViewHolder(inflater.inflate(R.layout.card_locations_common, viewGroup, false));
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
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int viewType) {
		switch(viewType) {
			case InputViewHolder.VIEW_TYPE:
				InputViewHolder inputViewHolder = (InputViewHolder) viewHolder;
				PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(context, android.R.layout.simple_dropdown_item_1line, placesService);
				inputViewHolder.autocompleteTextView.setAdapter(adapter);
				break;

			case CommonLocationsViewHolder.VIEW_TYPE:
				CommonLocationsViewHolder locationsViewHolder = (CommonLocationsViewHolder) viewHolder;
				/*
				locationsViewHolder.textYourLocation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Toast.makeText(context, "foo bar", Toast.LENGTH_SHORT).show();
					}
				});
				*/
		}
	}


	@Override
	public int getItemCount() {
		return 2;
	}


	private static final class InputViewHolder extends RecyclerView.ViewHolder {

		private static final int VIEW_TYPE = 0;
		private final AutoCompleteTextView autocompleteTextView;

		public InputViewHolder(View view) {
			super(view);
			this.autocompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.input);
		}

	}


	private static final class CommonLocationsViewHolder extends RecyclerView.ViewHolder {

		private static final int VIEW_TYPE = 1;
		private final TextView textYourLocation;
		private final TextView textMapLocation;

		public CommonLocationsViewHolder(View view) {
			super(view);
			this.textYourLocation = (TextView) view.findViewById(R.id.your_location);
			this.textMapLocation = (TextView) view.findViewById(R.id.map_location);
		}

	}

}
