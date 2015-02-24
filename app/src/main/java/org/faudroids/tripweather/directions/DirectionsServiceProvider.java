package org.faudroids.tripweather.directions;

import android.content.Context;

import com.google.inject.Provider;

import org.faudroids.tripweather.R;

import javax.inject.Inject;

import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class DirectionsServiceProvider implements Provider<DirectionsService> {

    private final Context context;

    @Inject
    public DirectionsServiceProvider(Context context) {
        this.context = context;
    }

    @Override
    public DirectionsService get() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(context.getString(R.string.directions_api_base_url))
                .setConverter(new JacksonConverter())
                .build();

        return restAdapter.create(DirectionsService.class);
    }
}
