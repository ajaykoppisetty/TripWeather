package org.faudroids.tripweather.directions;

import com.fasterxml.jackson.databind.node.ObjectNode;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class DirectionsServiceCallback implements Callback<ObjectNode> {
    @Override
    public void success(ObjectNode jsonNodes, Response response) {
        Timber.d("Success: " + jsonNodes.get("status"));
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.e(error.getMessage());
    }
}
