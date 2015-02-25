package org.faudroids.tripweather.directions;

import android.util.Pair;

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
        Route.Builder builder = new Route.Builder();
        ArrayList<Route> routeList = new ArrayList<>();

        String copyright = null;
        String warnings = null;
        Waypoint origin = null;
        Waypoint destination = null;
        double meanSpeed;
        ArrayList<Pair<Waypoint, Double>> wp = new ArrayList<>();

        JsonNode routes = routeJSON.get("routes");

        for(int routeIdx = 0; routeIdx < routes.size(); ++routeIdx) {
            copyright = routes.get(routeIdx).get("copyrights").asText();
            warnings = routes.get(routeIdx).get("warnings").asText();

            JsonNode legs = routes.get(routeIdx).get("legs");

            for (int legIdx = 0; legIdx < legs.size(); ++legIdx) {

                origin = new Waypoint(legs.get(legIdx)
                                          .get("start_location")
                                          .get("lat").asDouble(),
                                      legs.get(legIdx)
                                          .get("start_location")
                                          .get("lng").asDouble());
                wp.add(new Pair<>(origin, 0.0));


                destination = new Waypoint(legs.get(legIdx)
                                               .get("end_location")
                                               .get("lat").asDouble(),
                                           legs.get(legIdx)
                                               .get("end_location")
                                               .get("lng").asDouble());


                JsonNode steps = legs.get(legIdx).get("steps");

                for (int idx = 0; idx < steps.size(); ++idx) {
                    meanSpeed = (steps.get(idx).get("distance").get("value").asDouble()/
                                 steps.get(idx).get("duration").get("value").asDouble())*3.6;

                    wp.add(new Pair<>(new Waypoint(steps.get(idx)
                                                        .get("end_location")
                                                        .get("lat").asDouble(),
                                                   steps.get(idx)
                                                        .get("end_location")
                                                        .get("lng").asDouble()),
                                      meanSpeed));
                }

                Route route = new Route.Builder()
                                       .from(origin)
                                       .to(destination)
                                       .waypoints(wp)
                                       .copyright(copyright)
                                       .warnings(warnings)
                                       .construct();
                routeList.add(route);
                wp.clear();
            }
        }

        return routeList;
    }
}
