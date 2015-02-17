package org.faudroids.tripweather.directions;

import java.util.HashMap;

public class DirectionsApiRequestBuilder {

    static enum TravelMode {
        TRAVEL_MODE_DRIVING,
        TRAVEL_MODE_WALKING,
        TRAVEL_MODE_BICYCLING,
        TRAVEL_MODE_TRANSIT
    }


    static enum Units {
        UNIT_METRIC,
        UNIT_IMPERIAL
    }


    static HashMap<TravelMode, String> travelModeMap = new HashMap<>();
    static {
        travelModeMap.put(TravelMode.TRAVEL_MODE_DRIVING, "driving");
        travelModeMap.put(TravelMode.TRAVEL_MODE_WALKING, "walking");
        travelModeMap.put(TravelMode.TRAVEL_MODE_BICYCLING, "bicycling");
        travelModeMap.put(TravelMode.TRAVEL_MODE_TRANSIT, "transit");
    }


    static HashMap<Units, String> unitMap = new HashMap<>();
    static {
        unitMap.put(Units.UNIT_METRIC, "metric");
        unitMap.put(Units.UNIT_IMPERIAL, "imperial");
    }


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


    DirectionsApiRequestBuilder origin(String val) { this.origin = val; return this; }
    DirectionsApiRequestBuilder destination(String val) { this.destination= val; return this; }
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

        return DirectionApiFields.requestBaseUrl +
               DirectionApiFields.originPrefix + origin +
               DirectionApiFields.destinationPrefix + destination +
               DirectionApiFields.sensorPrefix + String.valueOf(useSensor) +
               DirectionApiFields.modePrefix + travelModeMap.get(travelMode) +
               DirectionApiFields.unitPrefix + unitMap.get(units) +
               DirectionApiFields.avoidPrefix + String.valueOf(avoidHighways) +
               DirectionApiFields.avoidPrefix + String.valueOf(avoidTolls) +
               DirectionApiFields.alternativesPrefix + String.valueOf(alternatives);
    }
}
