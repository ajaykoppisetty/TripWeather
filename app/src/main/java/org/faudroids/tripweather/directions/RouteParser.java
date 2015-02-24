package org.faudroids.tripweather.directions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class RouteParser {

    private ObjectNode routeJSON = null;

    public RouteParser(ObjectNode routeJSON) {
        this.routeJSON = routeJSON;
    }

    public boolean isEmpty() {
        if(routeJSON.get("status")
                    .asText()
                    .equals("OK")) {
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<Route> parse() {
        ArrayList<Waypoint> wp = new ArrayList<>();
        ArrayList<Route> routeList = new ArrayList<>();

        JsonNode routes = routeJSON.get("routes");

        for(int routeIdx = 0; routeIdx < routes.size(); ++routeIdx) {
            JsonNode legs = routes.get(routeIdx).get("legs");

            if(legs != null) {
                for (int legIdx = 0; legIdx < legs.size(); ++legIdx) {
                    Route route = new Route();
                    route.setOrigin(new Waypoint(legs.get(legIdx).get("start_location")
                                                     .get("lat").asDouble(),
                                                 legs.get(legIdx).get("start_location")
                                                     .get("lng").asDouble(), 0, 0));

                    route.setDestination(new Waypoint(legs.get(legIdx)
                                                          .get("end_location")
                                                          .get("lat").asDouble(),
                                                      legs.get(legIdx)
                                                          .get("end_location")
                                                          .get("lng").asDouble(),
                                                      legs.get(legIdx)
                                                          .get("distance")
                                                          .get("value").asDouble(),
                                                      legs.get(legIdx).get("duration")
                                                          .get("value").asDouble()));

                    JsonNode steps = legs.get(legIdx).get("steps");

                    for (int idx = 0; idx < steps.size(); ++idx) {
                        wp.add(new Waypoint(steps.get(idx)
                                                 .get("end_location")
                                                 .get("lat").asDouble(),
                                            steps.get(idx)
                                                 .get("end_location")
                                                 .get("lng").asDouble(),
                                            steps.get(idx)
                                                 .get("distance")
                                                 .get("value").asDouble(),
                                            steps.get(idx)
                                                 .get("duration")
                                                 .get("value").asDouble()));
                    }

                    route.setWaypoints(wp);
                    routeList.add(route);
                }
            }
        }

        return routeList;
    }
}
