package org.faudroids.tripweather.directions;


import com.fasterxml.jackson.databind.node.ObjectNode;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface PlacesService {

	@GET("/autocomplete/json?types=(cities)")
	public ObjectNode getAutocomplete(@Query("input") String input);

	@GET("/autocomplete/json?types=(cities)")
	public void getAutocomplete(@Query("input") String input, Callback<ObjectNode> callback);


	@GET("/details/json")
	public ObjectNode getDetails(@Query("placeid") String placeId);

	@GET("/details/json")
	public void getDetails(@Query("placeid") String placeId, Callback<ObjectNode> callback);

}
