package org.faudroids.tripweather.geo;

/**
 * Used to interpolate intermediate points to split routes in equal chunks.
 * Formulas for coordinate calculation were taken from:
 *
 * http://www.movable-type.co.uk/scripts/latlong.html
 *
 */
class WayPointInterpolator {

	private final double earthRadius = 6371.009;

	private double calculateBearing(WayPoint start, WayPoint end) {

		double phi1 = Math.toRadians(start.getLat());
		double lambda1 = Math.toRadians(start.getLng());
		double phi2 = Math.toRadians(end.getLat());
		double lambda2 = Math.toRadians(end.getLng());

		double y = Math.sin(lambda2-lambda1)*
				   Math.cos(phi2);

		double x = Math.cos(phi1) *
				   Math.sin(phi2) -
				   Math.sin(phi1) *
				   Math.cos(phi2) *
				   Math.cos(lambda2 - lambda1);

		return Math.atan2(y, x);
	}


	public WayPoint getIntermediatePoint(WayPoint start, WayPoint end, double distance) {
		double bearing = calculateBearing(start, end);
		double phi1 = Math.toRadians(start.getLat());
		double lambda1 = Math.toRadians(start.getLng());
		double angularDistance = distance/earthRadius;

		double newLat = Math.toDegrees(Math.asin(Math.sin(phi1) *
									   Math.cos(angularDistance) +
									   Math.cos(phi1) *
									   Math.sin(angularDistance) *
									   Math.cos(bearing)));

		double newLng = Math.toDegrees(lambda1 +
						Math.atan2(Math.sin(bearing) *
								   Math.sin(angularDistance) *
								   Math.cos(phi1),
								   Math.cos(angularDistance) -
								   Math.sin(phi1)*Math.sin(newLat)));

		return new WayPoint(newLat, newLng);
	}
}
