package org.faudroids.tripweather.geo;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.inject.Inject;

import timber.log.Timber;


public final class GeoCodingUtils {

	@Inject
	GeoCodingUtils() { }

	public Location parseLocation(String originalLocationDescription, ObjectNode data) throws GeoCodingException {
		assertValidGeoCodingData(originalLocationDescription, data);

		Timber.d(data.toString());
		JsonNode result = data.path("results").path(0);
		String description = result.path("formatted_address").asText();
		JsonNode locationResult = result.path("geometry").path("location");
		double lat = locationResult.path("lat").asDouble();
		double lon = locationResult.path("lng").asDouble();
		return new Location(description, lat, lon);
	}


	private void assertValidGeoCodingData(String originalLocationDescription, ObjectNode data) throws GeoCodingException {
		if ("OK".equals(data.path("status").asText())) return;
		if ("ZERO_RESULTS".equals(data.path("status").asText())) throw GeoCodingException.createZeroResultsException(originalLocationDescription);
		throw GeoCodingException.createInteralException();
	}

}
