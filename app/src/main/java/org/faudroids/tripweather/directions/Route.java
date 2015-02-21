package org.faudroids.tripweather.directions;

import android.util.Pair;

import java.util.ArrayList;

public class Route {

    double length;


    Waypoint origin = null;
    Waypoint destination = null;

    ArrayList<Waypoint> waypoints = new ArrayList<>();
}
