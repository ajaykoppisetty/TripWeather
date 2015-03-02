package org.faudroids.tripweather.network;


import android.content.Context;
import android.util.Pair;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.geo.DirectionsService;
import org.faudroids.tripweather.geo.DirectionsUtils;
import org.faudroids.tripweather.geo.GeoCodingException;
import org.faudroids.tripweather.geo.GeoCodingService;
import org.faudroids.tripweather.geo.GeoCodingUtils;
import org.faudroids.tripweather.geo.Location;
import org.faudroids.tripweather.geo.WayPoint;
import org.faudroids.tripweather.weather.Forecast;
import org.faudroids.tripweather.weather.WeatherException;
import org.faudroids.tripweather.weather.WeatherUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

public final class DataManager {

	private static final Pattern coordinatesLocationPattern = Pattern.compile("([0-9]{1,13}(\\.[0-9]*)?),([0-9]{1,13}(\\.[0-9]*)?)");

	private final Context context;
	private final GeoCodingService geoCodingService;
	private final GeoCodingUtils geoCodingUtils;
	private final DirectionsService directionsService;
	private final DirectionsUtils directionsUtils;
	private final WeatherUtils forecastGenerator;

	@Inject
	DataManager(
			Context context,
			GeoCodingService geoCodingService,
			GeoCodingUtils geoCodingUtils,
			DirectionsService directionsService,
			DirectionsUtils directionsUtils,
			WeatherUtils forecastGenerator) {

		this.context = context;
		this.geoCodingService = geoCodingService;
		this.geoCodingUtils = geoCodingUtils;
		this.directionsService = directionsService;
		this.directionsUtils = directionsUtils;
		this.forecastGenerator = forecastGenerator;
	}


	public Observable<TripData> getData(
			GoogleApiClient googleApiClient,
			String fromLocationDescription,
			String toLocationDescription,
			final long timestamp) {

		return Observable
				.zip(
						geoCodeLocation(googleApiClient, fromLocationDescription),
						geoCodeLocation(googleApiClient, toLocationDescription),
						new Func2<Location, Location, Pair<Location, Location>>() {
							@Override
							public Pair<Location, Location> call(Location fromLocation, Location toLocation) {
								return new Pair<>(fromLocation, toLocation);
							}
						})
				.flatMap(new Func1<Pair<Location, Location>, Observable<TripData.Builder>>() {
					@Override
					public Observable<TripData.Builder> call(final Pair<Location, Location> locations) {
						String start = locations.first.getLat() + "," + locations.first.getLng();
						String end = locations.second.getLat() + "," + locations.second.getLng();
						return directionsService
								.getRoute(start, end)
								.map(new Func1<ObjectNode, TripData.Builder>() {
									@Override
									public TripData.Builder call(ObjectNode objectNode) {
										return new TripData.Builder()
												.fromLocation(locations.first)
												.toLocation(locations.second)
												.route(objectNode);
									}
								});
					}
				})
				.flatMap(new Func1<TripData.Builder, Observable<TripData.Builder>>() {
					@Override
					public Observable<TripData.Builder> call(final TripData.Builder builder) {
						List<Pair<WayPoint, Long>> wayPoints = directionsUtils.parse(
								builder.fromLocation().getDescription(),
								builder.toLocation().getDescription(),
								builder.route())
								.get(0).interpolate();

						Timber.d("about to fetch weather");
						return forecastGenerator
								.createForecast(timestamp, wayPoints)
								.toSortedList(new Func2<Forecast, Forecast, Integer>() {
									@Override
									public Integer call(Forecast forecast, Forecast forecast2) {
										Timber.d("sorting");
										return Long.valueOf(forecast.getTimestamp()).compareTo(forecast2.getTimestamp());
									}
								})
								.map(new Func1<List<Forecast>, TripData.Builder>() {
									@Override
									public TripData.Builder call(List<Forecast> forecasts) {
										if (forecasts.isEmpty()) throw WeatherException.createTooDistanteDateException(timestamp);
										Timber.d("adding forecasts");
										return builder.forecasts(forecasts);
									}
								});
					}
				})
				.map(new Func1<TripData.Builder, TripData>() {
					@Override
					public TripData call(TripData.Builder builder) {
						Timber.d("building final product");
						return builder.build();
					}
				});
	}


	private Observable<Location> geoCodeLocation(GoogleApiClient googleApiClient, final String locationDescription) {
		if (context.getString(R.string.input_your_location).equals(locationDescription)) {
			return Observable.just(LocationServices.FusedLocationApi.getLastLocation(googleApiClient))
					.map(new Func1<android.location.Location, Location>() {
						@Override
						public Location call(android.location.Location location) {
							if (location != null) {
								return new Location(locationDescription, location.getLatitude(), location.getLongitude());
							} else {
								throw GeoCodingException.createUserLocationUnavailableException();
							}
						}
					});


		} else if (coordinatesLocationPattern.matcher(locationDescription).matches()) {
			Matcher matcher = coordinatesLocationPattern.matcher(locationDescription);
			matcher.find();
			return Observable.just(new Location(
					locationDescription,
					Double.valueOf(matcher.group(1)),
					Double.valueOf(matcher.group(3))));

		} else {
			return geoCodingService.getGeoCodeForAddress(locationDescription)
					.map(new Func1<ObjectNode, Location>() {
						@Override
						public Location call(ObjectNode objectNode) {
							return geoCodingUtils.parseLocation(locationDescription, objectNode);
						}
					});
		}

	}

}
