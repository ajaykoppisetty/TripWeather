package org.faudroids.tripweather.geo;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Route {

	private static final WayPointInterpolator wayPointInterpolator = new WayPointInterpolator();


    private final WayPoint origin;
    private final WayPoint destination;
    private final String copyright;
    private final String warnings;
    private final ArrayList<Pair<WayPoint, Double>> waypoints = new ArrayList<>();


    private Route(Builder builder) {
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.copyright = builder.copyright;
        this.warnings = builder.warnings;
        this.waypoints.addAll(builder.wayPoints);
    }


    /**
     *
     * @return ArrayList containing all wayPoints.
     */
    public ArrayList<WayPoint> getWaypoints() {
        ArrayList<WayPoint> returnList = new ArrayList<>();
        for(int i = 0; i < waypoints.size(); ++i) {
            returnList.add(waypoints.get(i).first);
        }
        return returnList;
    }


    /**
     *
     * @return Destination waypoint.
     */
    public WayPoint getDestination() {
        return destination;
    }


    /**
     *
     * @return Starting waypoint.
     */
    public WayPoint getOrigin() {
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
            totalTime += waypoints.get(i).first.getDuration(
					waypoints.get(i+1).first,
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


    /**
     * Interpolates a given list of way points to segment a route in 1 hour chunks.
	 * @return list of way points including an timestamp in minutes when user is expected to arrive a location,
	 * assuming that a trip starts at 0 minutes.
     */
    public List<Pair<WayPoint, Long>> interpolate() {
        List<Pair<WayPoint, Long>> interpolatedRoute = new ArrayList<>();
        interpolatedRoute.add(new Pair<>(waypoints.get(0).first, 0l));

		long totalTravelTime = 0;
        int wayPointIdx = 1;
        WayPoint startPoint = interpolatedRoute.get(0).first;
		double travelTime = 0;

        while (wayPointIdx < waypoints.size()) {
            WayPoint currentWayPoint = waypoints.get(wayPointIdx).first;
            double currentSpeed = waypoints.get(wayPointIdx).second;
            double duration = startPoint.getDuration(currentWayPoint, currentSpeed);

			travelTime += duration;
            if(travelTime > 1) {
				double deltaTime = duration - (travelTime - 1);
                double deltaLength = currentSpeed * deltaTime;
				totalTravelTime += 60;

				WayPoint newWayPoint = wayPointInterpolator.getIntermediatePoint(startPoint, currentWayPoint, deltaLength);
                interpolatedRoute.add(new Pair<>(
						newWayPoint,
						totalTravelTime));

				startPoint = newWayPoint;
				travelTime = 0;

            } else {
				startPoint = waypoints.get(wayPointIdx).first;
                ++wayPointIdx;
            }
        }

		totalTravelTime += travelTime * 60;
        interpolatedRoute.add(new Pair<>(
				waypoints.get(waypoints.size()-1).first,
				totalTravelTime));

        return interpolatedRoute;
    }


	public static final class Builder {
        private WayPoint origin;
        private WayPoint destination;
        private String copyright;
        private String warnings;
        private ArrayList<Pair<WayPoint, Double>> wayPoints = new ArrayList<>();

        public Builder from(WayPoint origin) {
            this.origin = origin;
            return this;
        }


        public Builder to(WayPoint destination) {
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


        public Builder wayPoints(ArrayList<Pair<WayPoint, Double>> wp) {
            this.wayPoints.addAll(wp);
            return this;
        }


        public Route create() {
            return new Route(this);
        }
    }
}
