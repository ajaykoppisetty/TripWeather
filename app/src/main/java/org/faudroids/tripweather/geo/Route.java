package org.faudroids.tripweather.geo;

import android.util.Pair;

import java.util.ArrayList;

import timber.log.Timber;

public class Route {


    private final WayPoint origin;
    private final WayPoint destination;
    private final String copyright;
    private final String warnings;
    private final ArrayList<Pair<WayPoint, Double>> waypoints = new ArrayList<>();


    private Route(Builder builder) {
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.copyright = builder.copyright;
        this.warnings = builder.warnings;
        this.waypoints.addAll(builder.waypoints);
    }


    /**
     *
     * @return ArrayList containing all waypoints.
     */
    public ArrayList<WayPoint> getWaypoints() {
        ArrayList<WayPoint> returnList = new ArrayList<>();
        for(int i = 0; i < waypoints.size(); ++i) {
            returnList.add(waypoints.get(i).first);
        }
        return returnList;
    }


    /**
     *
     * @return Destination waypoint.
     */
    public WayPoint getDestination() {
        return destination;
    }


    /**
     *
     * @return Starting waypoint.
     */
    public WayPoint getOrigin() {
        return origin;
    }


    /**
     *
     * @return Total length of the route in km.
     */
    public double getTotalLength() {
        double totalLength = 0;
        for(int i = 0; i < waypoints.size()-1; ++i) {
            totalLength += waypoints.get(i).first.getDistance(waypoints.get(i+1).first);
        }
        return totalLength;
    }


    /**
     *
     * @return Total time to travel the route in hours.
     */
    public double getTotalTime() {
        double totalTime = 0;
        for(int i = 0; i < waypoints.size()-1; ++i) {
            totalTime += waypoints.get(i).first.getDuration(waypoints.get(i+1).first,
                    waypoints.get(i+1).second);
        }
        return totalTime;
    }


    /**
     *
     * @return Returns an ArrayList containing the mean traveling speed for each waypoint.
     */
    public ArrayList<Double> getMeanTravelSpeeds() {
        ArrayList<Double> returnList = new ArrayList<>();
        for(int i = 0; i < waypoints.size(); ++i) {
            returnList.add(waypoints.get(i).second);
        }
        return returnList;
    }


    /**
     * Interpolates a given list of waypoints to segment a route in 1 hour chunks.
     * @return ArrayList\<Waypoint\>
     */
    public ArrayList<WayPoint> interpolate() {
        ArrayList<WayPoint> interpolatedRoute = new ArrayList<>();

        interpolatedRoute.add(waypoints.get(0).first);

        double travelTime = 0;
        int i = 1;

        WayPoint startPoint = interpolatedRoute.get(interpolatedRoute.size()-1);

        while(i < waypoints.size()) {
            WayPoint currentWayPoint = waypoints.get(i).first;
            double currentSpeed = waypoints.get(i).second;
            double duration = startPoint.getDuration(currentWayPoint, currentSpeed);

            travelTime += duration;
            if(travelTime > 1) {
                double deltaTime = duration - Math.abs(1-travelTime);
                assert(deltaTime > 0);

                double deltaLength = currentSpeed * deltaTime;
                assert(deltaLength > 0);

                Timber.d("Adding new waypoint!");
                interpolatedRoute.add(new Interpolator().getIntermediatePoint(startPoint,
						currentWayPoint, deltaLength));

                startPoint = interpolatedRoute.get(interpolatedRoute.size()-1);
                Timber.d("New Lat: " + startPoint.getLat());
                Timber.d("New Lng: " + startPoint.getLng());
                travelTime = 0;
            } else {
                startPoint = waypoints.get(i).first;
                ++i;
            }
        }

        interpolatedRoute.add(waypoints.get(waypoints.size()-1).first);

        return interpolatedRoute;
    }


    /**
     * Used to interpolate intermediate points to split routes in equal chunks.
     * Formulas for coordinate calculation were taken from:
     *
     * http://www.movable-type.co.uk/scripts/latlong.html
     *
     */
    private static class Interpolator {

        private final double earthRadius = 6371.009;

        private double calculateBearing(WayPoint start, WayPoint end) {

            double phi1 = Math.toRadians(start.getLat());
            double lambda1 = Math.toRadians(start.getLng());
            double phi2 = Math.toRadians(end.getLat());
            double lambda2 = Math.toRadians(end.getLng());

            double y = Math.sin(lambda2-lambda1)*
                       Math.cos(phi2);

            double x = Math.cos(phi1) *
                       Math.sin(phi2) -
                       Math.sin(phi1) *
                       Math.cos(phi2) *
                       Math.cos(lambda2 - lambda1);

            return Math.atan2(y, x);
        }


        public WayPoint getIntermediatePoint(WayPoint start, WayPoint end, double distance) {
            double bearing = calculateBearing(start, end);
;
            double phi1 = Math.toRadians(start.getLat());
            double lambda1 = Math.toRadians(start.getLng());
            double angularDistance = distance/earthRadius;

            double newLat = Math.toDegrees(Math.asin(Math.sin(phi1) *
                                           Math.cos(angularDistance) +
                                           Math.cos(phi1) *
                                           Math.sin(angularDistance) *
                                           Math.cos(bearing)));

            double newLng = Math.toDegrees(lambda1 +
                            Math.atan2(Math.sin(bearing) *
                                       Math.sin(angularDistance) *
                                       Math.cos(phi1),
                                       Math.cos(angularDistance) -
                                       Math.sin(phi1)*Math.sin(newLat)));

            return new WayPoint(newLat, newLng);
        }
    }


    public static class Builder {
        private WayPoint origin;
        private WayPoint destination;
        private String copyright;
        private String warnings;
        private ArrayList<Pair<WayPoint, Double>> waypoints = new ArrayList<>();

        public Builder from(WayPoint origin) {
            this.origin = origin;
            return this;
        }


        public Builder to(WayPoint destination) {
            this.destination = destination;
            return this;
        }


        public Builder copyright(String message) {
            this.copyright = message;
            return this;
        }


        public Builder warnings(String message) {
            this.warnings = message;
            return this;
        }


        public Builder waypoints(ArrayList<Pair<WayPoint, Double>> wp) {
            this.waypoints.addAll(wp);
            return this;
        }


        public Route construct() {
            return new Route(this);
        }
    }
}
