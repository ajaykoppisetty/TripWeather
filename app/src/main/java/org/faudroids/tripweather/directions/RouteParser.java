package org.faudroids.tripweather.directions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteParser {

    private static HashMap<String, Route.TravelMode> travelModeHashMap = new HashMap<>();
    static {
        travelModeHashMap.put("DRIVING", Route.TravelMode.driving);
        travelModeHashMap.put("WALKING", Route.TravelMode.walking);
        travelModeHashMap.put("BICYCLING", Route.TravelMode.bicycling);
    }

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
        String travelMode = null;
        Waypoint origin = null;
        Waypoint destination = null;
        ArrayList<Waypoint> wp = new ArrayList<>();

        JsonNode routes = routeJSON.get("routes");

        for(int routeIdx = 0; routeIdx < routes.size(); ++routeIdx) {
            copyright = routes.get(routeIdx).get("copyrights").asText();
            warnings = routes.get(routeIdx).get("warnings").asText();

            JsonNode legs = routes.get(routeIdx).get("legs");

            if(legs.size() > 0) {
                if(legs.get(0).get("steps").size() > 0) {
                    travelMode = legs.get(0).get("steps").get(0).get("travel_mode").asText();
                }
            }

            for (int legIdx = 0; legIdx < legs.size(); ++legIdx) {
                origin = new Waypoint(legs.get(legIdx)
                                          .get("start_location")
                                          .get("lat").asDouble(),
                                      legs.get(legIdx)
                                          .get("start_location")
                                          .get("lng").asDouble());
                wp.add(origin);


                destination = new Waypoint(legs.get(legIdx)
                                               .get("end_location")
                                               .get("lat").asDouble(),
                                           legs.get(legIdx)
                                               .get("end_location")
                                               .get("lng").asDouble());


                JsonNode steps = legs.get(legIdx).get("steps");

                for (int idx = 0; idx < steps.size(); ++idx) {
                    wp.add(new Waypoint(steps.get(idx)
                                             .get("end_location")
                                             .get("lat").asDouble(),
                                        steps.get(idx)
                                             .get("end_location")
                                             .get("lng").asDouble()));
                }

                Route route = new Route.Builder()
                                       .from(origin)
                                       .to(destination)
                                       .travelMode (travelModeHashMap.get(travelMode))
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
