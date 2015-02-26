package org.faudroids.tripweather.ui;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.faudroids.tripweather.geo.PlacesService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

	private final PlacesService placesService;

    PlacesAutoCompleteAdapter(
			Context context,
			int textViewResourceId,
			PlacesService placesService) {

        super(context, textViewResourceId);
		this.placesService = placesService;
    }


    @Override
    public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if(constraint != null) {
					ObjectNode data = placesService.getAutocomplete(constraint.toString());
					List<String> places = new LinkedList<>();
					for (JsonNode prediction : data.get("predictions")) {
						places.add(prediction.get("description").asText());
					}
					filterResults.values = places;
					filterResults.count = places.size();
				}
				return filterResults;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void publishResults(CharSequence constraint, FilterResults results) {
				clear();
				if (results.count > 0) {
					addAll((Collection<String>) results.values);
				}
			}
		};

    }
}
