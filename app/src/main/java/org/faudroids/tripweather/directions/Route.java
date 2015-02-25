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


    public ArrayList<Waypoint> getWaypoints() {
        ArrayList<Waypoint> returnList = new ArrayList<>();
        for(int i = 0; i < waypoints.size(); ++i) {
            returnList.add(waypoints.get(i).first);
        }
        return returnList;
    }


    public Waypoint getDestination() {
        return destination;
    }


    public Waypoint getOrigin() {
        return origin;
    }


    public double getTotalLength() {
        double totalLength = 0;
        for(int i = 0; i < waypoints.size()-1; ++i) {
            totalLength += waypoints.get(i).first.getDistance(waypoints.get(i+1).first);
        }
        return totalLength;
    }


    public double getTotalTime() {
        double totalTime = 0;
        for(int i = 0; i < waypoints.size()-1; ++i) {
            totalTime += waypoints.get(i).first.getDuration(waypoints.get(i+1).first,
                    waypoints.get(i+1).second);
        }
        return totalTime;
    }


    public ArrayList<Double> getMeanTravelSpeeds() {
        ArrayList<Double> returnList = new ArrayList<>();
        for(int i = 0; i < waypoints.size(); ++i) {
            returnList.add(waypoints.get(i).second);
        }
        return returnList;
    }


    public ArrayList<Waypoint> interpolate() {
        ArrayList<Waypoint> interpolatedRoute = new ArrayList<>();

        interpolatedRoute.add(waypoints.get(0).first);

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
