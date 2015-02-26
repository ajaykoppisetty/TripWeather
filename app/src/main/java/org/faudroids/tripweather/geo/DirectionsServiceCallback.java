package org.faudroids.tripweather.geo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class DirectionsServiceCallback implements Callback<ObjectNode> {

	private final RouteParser routeParser;

	public DirectionsServiceCallback(RouteParser routeParser) {
		this.routeParser = routeParser;
	}


    @Override
    public void success(ObjectNode jsonNodes, Response response) {
        ArrayList<Route> routes = routeParser.parse(jsonNodes);
        for(int i = 0; i < routes.size(); ++i) {
            ArrayList<WayPoint> w = routes.get(i).interpolate();

            double total = 0;
            for(int j = 0; j < w.size()-1; ++j) {
                total += w.get(j).getDistance(w.get(j+1));
                Timber.d("Lat: " + w.get(j).getLat());
                Timber.d("Lng: " + w.get(j).getLng());
                Timber.d("Length: " + w.get(j).getDistance(w.get(j+1)));
            }
            Timber.d("Lat: " + w.get(w.size()-1).getLat());
            Timber.d("Lng: " + w.get(w.size()-1).getLng());
            Timber.d("Total length: " + total);
        }
        Timber.d("Success: " + jsonNodes.get("status"));
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.e(error.getMessage());
    }

}
