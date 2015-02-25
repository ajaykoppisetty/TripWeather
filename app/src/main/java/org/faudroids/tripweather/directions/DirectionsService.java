package org.faudroids.tripweather.directions;

import com.fasterxml.jackson.databind.node.ObjectNode;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface DirectionsService {

    @GET("/json?sensor=true&units=metric")
    public ObjectNode getRoute(@Query("origin") String start, @Query("destination") String end);


    @GET("/json?sensor=true&units=metric")
    public void getRoute(@Query("origin") String start, @Query("destination") String end, Callback<ObjectNode> callback);
}
