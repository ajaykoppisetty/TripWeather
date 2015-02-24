package org.faudroids.tripweather.directions;

import java.util.ArrayList;

public class Route {

    double length;
    Waypoint origin = null;
    Waypoint destination = null;
    ArrayList<Waypoint> waypoints = new ArrayList<>();

    public void setLength(double length) {
        this.length = length;
    }

    public void setOrigin(Waypoint origin) {
        this.origin = origin;
    }

    public void setDestination(Waypoint destination) {
        this.destination = destination;
    }

    public void setWaypoints(ArrayList<Waypoint> waypoints) {
        this.waypoints = waypoints;
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

    public double getLength() {
        return length;
    }

    public ArrayList<Waypoint> getSegments(int segmentSize) {
        return null;
    }
}
