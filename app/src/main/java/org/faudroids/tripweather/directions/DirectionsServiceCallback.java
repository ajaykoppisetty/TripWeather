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
        for(int i = 0; i < routes.size(); ++i) {
            for(int j = 0; j < routes.get(i).getWaypoints().size(); ++j) {
                Timber.d("Duration: " + routes.get(i).getWaypoints().get(j).getDuration(routes
                        .get(i)
                        .getWaypoints().get(j+1), routes.get(i).getMeanTravelSpeeds().get(j+1)));
            }
        }
        Timber.d("Success: " + jsonNodes.get("status"));
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.e(error.getMessage());
    }
}
