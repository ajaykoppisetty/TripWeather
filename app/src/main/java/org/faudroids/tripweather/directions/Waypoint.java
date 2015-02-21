package org.faudroids.tripweather.directions;

public class Waypoint {

    private double lat;
    private double lng;
    private String description = null;

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    //Returns geographic distance between two points given by latitude/longitude
    public double distance(Waypoint other) {
        return 0;
    }

    public String getDescription() {
        return description;
    }
}
