package org.faudroids.tripweather.directions;

public class Waypoint {

    //Latitude and longitude in degrees
    private double lat;
    private double lng;


    //Earth radius in km
    private final double earthRadius = 6371.009;


    /**
     *
     * @param lat: Latidude of waypoint.
     * @param lng: Longitude of waypoint.
     */
    public Waypoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }


    /**
     *
     * @return Longitude of waypoint.
     */
    public double getLng() {
        return lng;
    }


    /**
     *
     * @return Latitude of waypoint.
     */
    public double getLat() {
        return lat;
    }


    //Returns geographic distance between two points given by latitude/longitude
    //"Spherical Earth projected to a plane"
    /**
     *
     * @param other: Destination waypoint.
     * @return Distance in km.
     */
    public double getDistance(Waypoint other) {
        double deltaPhi = Math.toRadians(other.getLat()-this.getLat());
        double deltaLambda = Math.toRadians(other.getLng() - this.getLng());

        double meanPhi = Math.toRadians(this.getLat()+other.getLat())/2;

        return earthRadius*(Math.sqrt(
                (deltaPhi*deltaPhi)+
                (Math.cos(meanPhi)*
                (deltaLambda*deltaLambda))));
    }


    /**
     *
     * @param lat: Latitude of destination point.
     * @param lng: Longitude of destination point.
     * @return Distance in km.
     */
    public double getDistance(double lat, double lng) {
        double deltaPhi = Math.toRadians(lat-this.getLat());
        double deltaLambda = Math.toRadians(lng - this.getLng());

        double meanPhi = Math.toRadians(this.getLat()+lat)/2;

        return earthRadius*(Math.sqrt(
                (deltaPhi*deltaPhi)+
                (Math.cos(meanPhi)*
                (deltaLambda*deltaLambda))));
    }


    /**
     *
     * @param other: Destination waypoint
     * @param meanTravelSpeed: Average traveling speed in km/h
     * @return Duration in hours.
     */
    public double getDuration(Waypoint other, double meanTravelSpeed) {
        return getDistance(other)/meanTravelSpeed;
    }


    /**
     *
     * @param lat: Latitude of destination point
     * @param lng: Longitude of destination point
     * @param meanTravelSpeed: Average traveling speed in km/h
     * @return Duration in hours.
     */
    public double getDuration(double lat, double lng, double meanTravelSpeed) {
        return getDistance(lat, lng)/meanTravelSpeed;
    }
}
