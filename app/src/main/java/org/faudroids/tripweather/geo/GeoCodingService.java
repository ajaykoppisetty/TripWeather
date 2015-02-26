package org.faudroids.tripweather.geo;


import com.fasterxml.jackson.databind.node.ObjectNode;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GeoCodingService {

	@GET("/json?sensor=true")
	public ObjectNode getGeoCodeForAddress(@Query("address") String address);

	@GET("/json?sensor=true")
	public void getGeoCodeForAddress(@Query("address") String address, Callback<ObjectNode> callback);

}
