package org.faudroids.tripweather.geo;


import com.fasterxml.jackson.databind.node.ObjectNode;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface PlacesService {

	@GET("/autocomplete/json")
	public ObjectNode getAutocomplete(@Query("input") String input);

	@GET("/autocomplete/json")
	public void getAutocomplete(@Query("input") String input, Callback<ObjectNode> callback);


	@GET("/textsearch/json")
	public ObjectNode getTextSearch(@Query("query") String query);

	@GET("/textsearch/json")
	public void getTextSearch(@Query("query") String query, Callback<ObjectNode> callback);


	@GET("/details/json")
	public ObjectNode getDetails(@Query("placeid") String placeId);

	@GET("/details/json")
	public void getDetails(@Query("placeid") String placeId, Callback<ObjectNode> callback);

}
