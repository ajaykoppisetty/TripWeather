package org.faudroids.tripweather.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.faudroids.tripweather.R;

public final class InputAdapter extends RecyclerView.Adapter<InputAdapter.InputViewHolder> {

	@Override
	public InputViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater
				.from(viewGroup.getContext())
				.inflate(R.layout.card_input, viewGroup, false);
		return new InputViewHolder(view);
	}


	@Override
	public void onBindViewHolder(InputViewHolder inputViewHolder, int i) {
		inputViewHolder.textView.setText("Route " + (int) (Math.random() * 100));
	}


	@Override
	public int getItemCount() {
		return 5;
	}


	static final class InputViewHolder extends RecyclerView.ViewHolder {

		private final TextView textView;

		public InputViewHolder(View view) {
			super(view);
			textView = (TextView) view.findViewById(R.id.route);
		}

	}

}
