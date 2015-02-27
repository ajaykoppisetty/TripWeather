package org.faudroids.tripweather.geo;


import junit.framework.Assert;
import junit.framework.TestCase;

public final class WayPointInterpolatorTest extends TestCase {

	private WayPointInterpolator interpolator;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.interpolator = new WayPointInterpolator();
	}


	public void testSimpleInterpolation() {
		WayPoint p1 = new WayPoint(49.9462852, 9.342552);
		WayPoint p2 = new WayPoint(48.7293971, 9.3535383);
		WayPoint intermediate = interpolator.getIntermediatePoint(p1, p2, 200);

		Assert.assertEquals(200, p1.getDistance(intermediate), 2);
	}

}
