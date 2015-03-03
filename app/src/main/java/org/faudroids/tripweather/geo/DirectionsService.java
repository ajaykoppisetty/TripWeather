package org.faudroids.tripweather.geo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface DirectionsService {

    @GET("/json?sensor=true&units=metric")
    public Observable<ObjectNode> getRoute(@Query("origin") String start, @Query("destination") String end, @Query("mode") String travelMode);

}
