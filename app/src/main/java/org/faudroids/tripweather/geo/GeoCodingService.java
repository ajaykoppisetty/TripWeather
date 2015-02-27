package org.faudroids.tripweather.geo;


import com.fasterxml.jackson.databind.node.ObjectNode;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface GeoCodingService {

	@GET("/json?sensor=true")
	public Observable<ObjectNode> getGeoCodeForAddress(@Query("address") String address);

}
