package org.faudroids.tripweather.directions;

import java.util.ArrayList;

public class Route {


    public enum TravelMode {
        walking(5), bicycling(15), driving(80);

        private final int speed;

        private TravelMode(int speed) {
            this.speed = speed;
        }

        public int value() {
            return speed;
        }
    }


    private final Waypoint origin;
    private final Waypoint destination;
    private final String copyright;
    private final String warnings;
    private final TravelMode travelMode;
    private final ArrayList<Waypoint> waypoints = new ArrayList<>();


    private Route(Builder builder) {
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.copyright = builder.copyright;
        this.warnings = builder.warnings;
        this.travelMode = builder.travelMode;
        this.waypoints.addAll(builder.waypoints);
    }


    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }


    public Waypoint getDestination() {
        return destination;
    }


    public Waypoint getOrigin() {
        return origin;
    }


    public double getTotalLength() {
        return 0;
    }


    public double getTotalTime() {
        return 0;
    }


    public TravelMode getTravelMode() {
        return travelMode;
    }


    public int getTravelSpeed() {
        return travelMode.value();
    }

    public static class Builder {
        private Waypoint origin;
        private Waypoint destination;
        private String copyright;
        private String warnings;
        private TravelMode travelMode;
        private ArrayList<Waypoint> waypoints = new ArrayList<>();

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


        public Builder travelMode(TravelMode mode) {
            this.travelMode = mode;
            return this;
        }


        public Builder waypoints(ArrayList<Waypoint> wp) {
            this.waypoints.addAll(wp);
            return this;
        }


        public Route construct() {
            return new Route(this);
        }
    }
}
