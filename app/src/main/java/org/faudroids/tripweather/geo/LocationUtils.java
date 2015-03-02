package org.faudroids.tripweather.geo;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public final class LocationUtils {

	private static final Pattern coordinatesLocationPattern = Pattern.compile("([0-9]{1,13}(\\.[0-9]*)?),([0-9]{1,13}(\\.[0-9]*)?)");


	@Inject
	LocationUtils() { }


	public String encodeCoordiantes(double lat, double lng) {
		return lat + "," + lng;
	}


	public double decodeLat(String location) {
		Matcher matcher = coordinatesLocationPattern.matcher(location);
		matcher.find();
		return Double.valueOf(matcher.group(1));
	}


	public double decodeLng(String location) {
		Matcher matcher = coordinatesLocationPattern.matcher(location);
		matcher.find();
		return Double.valueOf(matcher.group(3));
	}


	public boolean isEncodedLocation(String location) {
		return coordinatesLocationPattern.matcher(location).matches();
	}

}
