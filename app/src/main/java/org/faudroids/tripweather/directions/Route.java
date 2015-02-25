package org.faudroids.tripweather.directions;

import android.util.Pair;

import java.util.ArrayList;

public class Route {


    private final Waypoint origin;
    private final Waypoint destination;
    private final String copyright;
    private final String warnings;
    private final ArrayList<Pair<Waypoint, Double>> waypoints = new ArrayList<>();


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
    public ArrayList<Waypoint> getWaypoints() {
        ArrayList<Waypoint> returnList = new ArrayList<>();
        for(int i = 0; i < waypoints.size(); ++i) {
            returnList.add(waypoints.get(i).first);
        }
        return returnList;
    }


    /**
     *
     * @return Destination waypoint.
     */
    public Waypoint getDestination() {
        return destination;
    }


    /**
     *
     * @return Starting waypoint.
     */
    public Waypoint getOrigin() {
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


    public ArrayList<Waypoint> interpolate() {
        ArrayList<Waypoint> interpolatedRoute = new ArrayList<>();

        double epsilon = 0.16;

        interpolatedRoute.add(waypoints.get(0).first);

        double travelTime = 0;

        for(int i = 0; i < waypoints.size(); ++i) {
            Waypoint startPoint = interpolatedRoute.get(interpolatedRoute.size()-1);
            Waypoint currentWaypoint = waypoints.get(i).first;
            double currentSpeed = waypoints.get(i).second;
            double duration = startPoint.getDuration(currentWaypoint, currentSpeed);

            //TODO: Finish me :)
            if(Math.abs(1 - duration) <= epsilon) {
                interpolatedRoute.add(currentWaypoint);
            } else if(1 - duration < 0) {

            } else {
                travelTime += startPoint.getDuration(currentWaypoint, currentSpeed);
            }
        }

        return null;
    }

    public static class Builder {
        private Waypoint origin;
        private Waypoint destination;
        private String copyright;
        private String warnings;
        private ArrayList<Pair<Waypoint, Double>> waypoints = new ArrayList<>();

        public Builder from(Waypoint origin) {
            this.origin = origin;
            return this;
        }


        public Builder to(Waypoint destination) {
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


        public Builder waypoints(ArrayList<Pair<Waypoint, Double>> wp) {
            this.waypoints.addAll(wp);
            return this;
        }


        public Route construct() {
            return new Route(this);
        }
    }
}
