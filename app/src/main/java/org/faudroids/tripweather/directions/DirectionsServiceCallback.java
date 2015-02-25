package org.faudroids.tripweather.directions;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class DirectionsServiceCallback implements Callback<ObjectNode> {
    @Override
    public void success(ObjectNode jsonNodes, Response response) {
        RouteParser test = new RouteParser(jsonNodes);
        ArrayList<Route> routes = test.parse();
        Timber.d("Empty? - " + test.isEmpty());
        Timber.d("Elements: " + routes.size());
        for(int i = 0; i < routes.size(); ++i) {
            Timber.d("no. of waypoints: " + routes.get(i).getWaypoints().size());
            for(int j = 0; j < routes.get(i).getWaypoints().size(); ++j) {
                Timber.d("lat: " + routes.get(i).getWaypoints().get(j).getLat());
                Timber.d("lng: " + routes.get(i).getWaypoints().get(j).getLng());
            }
        }
        Timber.d("Success: " + jsonNodes.get("status"));
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.e(error.getMessage());
    }
}
