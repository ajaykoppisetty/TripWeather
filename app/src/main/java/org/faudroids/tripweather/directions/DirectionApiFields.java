package org.faudroids.tripweather.directions;

public class DirectionApiFields {
    static String requestBaseUrl = "https://maps.googleapis.com/maps/api/directions/json?";
    static String originPrefix = "origin=";
    static String destinationPrefix = "&destination=";
    static String sensorPrefix = "&sensor=";
    static String modePrefix = "&mode=";
    static String unitPrefix = "&units=";
    static String avoidPrefix = "&avoid=";
    static String alternativesPrefix = "&alternatives=";
}
