package org.faudroids.tripweather.directions;

import android.content.Context;

import org.faudroids.tripweather.R;

import java.util.HashMap;

public class DirectionsApiRequestBuilder {

    private final Context context;

    enum TravelMode {
        TRAVEL_MODE_DRIVING,
        TRAVEL_MODE_WALKING,
        TRAVEL_MODE_BICYCLING,
        TRAVEL_MODE_TRANSIT
    }

    enum Units {
        UNIT_METRIC,
        UNIT_IMPERIAL
    }

    HashMap<TravelMode, String> travelModeMap = new HashMap<>();
    HashMap<Units, String> unitMap = new HashMap<>();

    /*
    Required fields for every request.
     */
    String origin = null;
    String destination = null;
    boolean useSensor = true;

    /*
    Optional parameters.
     */
    TravelMode travelMode = TravelMode.TRAVEL_MODE_DRIVING;
    boolean alternatives = true;
    boolean avoidTolls = false;
    boolean avoidHighways = false;
    Units units = Units.UNIT_METRIC;


    public DirectionsApiRequestBuilder(Context ctx) {
        context = ctx;

        travelModeMap.put(TravelMode.TRAVEL_MODE_DRIVING,
                          this.context.getString(R.string.directions_api_driving));
        travelModeMap.put(TravelMode.TRAVEL_MODE_WALKING,
                          this.context.getString(R.string.directions_api_walking));
        travelModeMap.put(TravelMode.TRAVEL_MODE_BICYCLING,
                          this.context.getString(R.string.directions_api_bicycling));
        travelModeMap.put(TravelMode.TRAVEL_MODE_TRANSIT,
                          this.context.getString(R.string.directions_api_transit));

        unitMap.put(Units.UNIT_METRIC,
                    this.context.getString(R.string.directions_api_unit_metric));
        unitMap.put(Units.UNIT_IMPERIAL,
                    this.context.getString(R.string.directions_api_unit_imerial));
    }


    DirectionsApiRequestBuilder destination(String val) { this.destination = val; return this; }
    DirectionsApiRequestBuilder sensor(boolean val) { this.useSensor = val; return this; }
    DirectionsApiRequestBuilder travelMode(TravelMode val) { this.travelMode = val; return this; }
    DirectionsApiRequestBuilder alternatives(boolean val) { this.alternatives= val; return this; }
    DirectionsApiRequestBuilder avoidTolls(boolean val) { this.avoidTolls = val; return this; }
    DirectionsApiRequestBuilder avoidHighways(boolean val) { this.avoidHighways= val; return this; }
    DirectionsApiRequestBuilder units(Units val) { this.units = val; return this; }


    @Override
    public String toString() {
        if(origin == null || destination == null) {
            throw new RuntimeException("Arguments missing!");
        }

        return this.context.getString(R.string.directions_api_base_url) +
               this.context.getString(R.string.directions_api_origin_prefix) +
               origin +
               this.context.getString(R.string.directions_api_destination_prefix) +
               destination +
               this.context.getString(R.string.directions_api_destination_prefix) +
               String.valueOf(useSensor) +
               this.context.getString(R.string.directions_api_destination_prefix) +
               travelModeMap.get(travelMode) +
               this.context.getString(R.string.directions_api_destination_prefix) +
               unitMap.get(units) +
               this.context.getString(R.string.directions_api_destination_prefix) +
               String.valueOf(avoidHighways) +
               this.context.getString(R.string.directions_api_destination_prefix) +
               String.valueOf(avoidTolls) +
               this.context.getString(R.string.directions_api_destination_prefix) +
               String.valueOf(alternatives);
    }
}
