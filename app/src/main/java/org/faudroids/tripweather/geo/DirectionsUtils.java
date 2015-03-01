package org.faudroids.tripweather.geo;

import android.util.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

import javax.inject.Inject;

public final class DirectionsUtils {

	@Inject
	DirectionsUtils() { }

    public ArrayList<Route> parse(
			String fromLocationDescription,
			String toLocationDescription,
			ObjectNode routeJson) throws DirectionsException {

		assertIsValidDirectionsData(fromLocationDescription, toLocationDescription, routeJson);

        ArrayList<Route> routeList = new ArrayList<>();
        String copyright, warnings;
        WayPoint origin, destination;
        double meanSpeed;
        ArrayList<Pair<WayPoint, Double>> wp = new ArrayList<>();

        JsonNode routes = routeJson.get("routes");

        for(int routeIdx = 0; routeIdx < routes.size(); ++routeIdx) {
            copyright = routes.get(routeIdx).get("copyrights").asText();
            warnings = routes.get(routeIdx).get("warnings").asText();

            JsonNode legs = routes.get(routeIdx).get("legs");

            for (int legIdx = 0; legIdx < legs.size(); ++legIdx) {

                origin = new WayPoint(legs.get(legIdx)
                                          .get("start_location")
                                          .get("lat").asDouble(),
                                      legs.get(legIdx)
                                          .get("start_location")
                                          .get("lng").asDouble());
                wp.add(new Pair<>(origin, 0.0));


                destination = new WayPoint(legs.get(legIdx)
                                               .get("end_location")
                                               .get("lat").asDouble(),
                                           legs.get(legIdx)
                                               .get("end_location")
                                               .get("lng").asDouble());


                JsonNode steps = legs.get(legIdx).get("steps");

                for (int idx = 0; idx < steps.size(); ++idx) {
                    meanSpeed = (steps.get(idx).get("distance").get("value").asDouble()/
                                 steps.get(idx).get("duration").get("value").asDouble())*3.6;

                    wp.add(new Pair<>(new WayPoint(steps.get(idx)
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
                                       .wayPoints(wp)
                                       .copyright(copyright)
                                       .warnings(warnings)
                                       .create();
                routeList.add(route);
                wp.clear();
            }
        }

        return routeList;
    }


	private void assertIsValidDirectionsData(
			String fromLocationDescription,
			String toLocationDescription,
			ObjectNode data) throws DirectionsException {

		if ("OK".equals(data.path("status").asText())) return;
		if ("ZERO_RESULTS".equals(data.path("status").asText())) throw DirectionsException.createZeroResultsException(fromLocationDescription, toLocationDescription);
		throw DirectionsException.createInteralException();
	}

}
