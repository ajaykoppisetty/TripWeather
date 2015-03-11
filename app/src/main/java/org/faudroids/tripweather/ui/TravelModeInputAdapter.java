package org.faudroids.tripweather.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.geo.TravelMode;

public final class TravelModeInputAdapter extends RecyclerView.Adapter<TravelModeInputAdapter.TravelModeViewHolder> {

	private final TravelModeListener modeListener;

	public TravelModeInputAdapter(TravelModeListener modeListener) {
		this.modeListener = modeListener;
	}


	@Override
	public TravelModeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
		return new TravelModeViewHolder(inflater.inflate(R.layout.card_travel_mode, viewGroup, false));
	}


	@Override
	public int getItemViewType(int position) {
		return 0;
	}


	@Override
	public void onBindViewHolder(TravelModeViewHolder viewHolder, int viewType) {
		viewHolder.onBindViewHolder();
	}


	@Override
	public int getItemCount() {
		return 1;
	}



	final class TravelModeViewHolder extends RecyclerView.ViewHolder {

		private final View carView;
		private final View bikeView;
		private final View walkView;

		public TravelModeViewHolder(View view) {
			super(view);
			this.carView = view.findViewById(R.id.driving);
			this.bikeView = view.findViewById(R.id.bicycling);
			this.walkView = view.findViewById(R.id.walking);
		}


		public void onBindViewHolder() {
			carView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					modeListener.onTravelModeSelected(TravelMode.DRIVING);
				}
			});
			bikeView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					modeListener.onTravelModeSelected(TravelMode.BICYCLING);
				}
			});
			walkView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					modeListener.onTravelModeSelected(TravelMode.WALKING);
				}
			});
		}

	}

}
