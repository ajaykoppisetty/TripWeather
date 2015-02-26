package org.faudroids.tripweather.geo;


import android.test.AndroidTestCase;

import com.fasterxml.jackson.databind.node.ObjectNode;

import junit.framework.Assert;

public final class PlacesServiceTest extends AndroidTestCase {

	private PlacesService placesService;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.placesService = new GeoModule().providePlacesService(getContext());
	}


	public void testSimpleAutocomplete() {
		ObjectNode result = placesService.getAutocomplete("Erlangen");
		assertSuccess(result);
		Assert.assertTrue(result.get("predictions").size() > 0);
	}


	public void testSimpleDetails() {
		ObjectNode result = placesService.getDetails("ChIJoTR81cf4oUcR0Me1vjLaHgQ");
		System.out.println(result);
		assertSuccess(result);
		Assert.assertFalse(result.get("result").get("geometry").get("location").isMissingNode());
	}


	private void assertSuccess(ObjectNode result) {
		Assert.assertTrue("ok".equalsIgnoreCase(result.get("status").asText()));
	}

}
