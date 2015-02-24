package org.faudroids.tripweather.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class WeatherUtils {

	private static final float KELVIN_BASE = 273.15f;

	public float getTemperature(ObjectNode data) {
		return (float) assertValidData(data.path("main").path("temp")).asDouble() - KELVIN_BASE;
	}


	private JsonNode assertValidData(JsonNode jsonNode) {
		if (jsonNode.isMissingNode()) throw new IllegalArgumentException("failed to find data in json");
		return jsonNode;
	}

}
