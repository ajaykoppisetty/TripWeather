package org.faudroids.tripweather.directions;

public class Waypoint {

    //Latitude and longitude in degrees
    private double lat;
    private double lng;


    //Earth radius in km
    private double earthRadius = 6371.009;


    public Waypoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }


    public double getLng() {
        return lng;
    }


    public double getLat() {
        return lat;
    }


    //Returns geographic distance between two points given by latitude/longitude
    //"Spherical Earth projected to a plane"
    public double getDistance(Waypoint other) {
        double deltaPhi = Math.toRadians(other.getLat()-this.getLat());
        double deltaLambda = Math.toRadians(other.getLng() - this.getLng());

        double meanPhi = Math.toRadians(this.getLat()+other.getLat())/2;

        return earthRadius*(Math.sqrt(
                (deltaPhi*deltaPhi)+
                (Math.cos(meanPhi)*
                (deltaLambda*deltaLambda))));
    }

    public double getDistance(double lat, double lng) {
        double deltaPhi = Math.toRadians(lat-this.getLat());
        double deltaLambda = Math.toRadians(lng - this.getLng());

        double meanPhi = Math.toRadians(this.getLat()+lat)/2;

        return earthRadius*(Math.sqrt(
                (deltaPhi*deltaPhi)+
                (Math.cos(meanPhi)*
                (deltaLambda*deltaLambda))));
    }


    //Returns traveling time from current waypoint to another according to given travel mode
    public double getDuration(Waypoint other, Route.TravelMode travelMode) {
        return (double)(getDistance(other)/travelMode.value());
    }


    public double getDuration(double lat, double lng, Route.TravelMode travelMode) {
        return (double)(getDistance(lat, lng)/travelMode.value());
    }
}
