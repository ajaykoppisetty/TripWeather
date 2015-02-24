package org.faudroids.tripweather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.directions.PlacesService;
import org.faudroids.tripweather.weather.WeatherService;
import org.faudroids.tripweather.weather.WeatherUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import timber.log.Timber;


@ContentView(R.layout.activity_graph)
public class GraphActivity extends RoboActivity {

	private static final String
			EXTRA_FROM_PLACE_ID = "EXTRA_FROM_PLACE_ID",
			EXTRA_TO_PLACE_ID = "EXTRA_TO_PLACE_ID";


	public static Intent createIntent(Context context, String fromPlaceId, String toPlaceId) {
		Intent intent = new Intent(context, GraphActivity.class);
		intent.putExtra(EXTRA_FROM_PLACE_ID, fromPlaceId);
		intent.putExtra(EXTRA_TO_PLACE_ID, toPlaceId);
		return intent;
	}


	@InjectView(R.id.graph) LineChart lineChart;
	@Inject WeatherService weatherService;
	@Inject WeatherUtils weatherUtils;
	@Inject PlacesService placesService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		// final String fromPlaceId = getIntent().getExtras().getString(EXTRA_FROM_PLACE_ID);
		// final String toPlaceId = getIntent().getExtras().getString(EXTRA_TO_PLACE_ID);

		int count = 20;
		ArrayList<String> xValues = new ArrayList<>();
		ArrayList<Entry> entries = new ArrayList<>();
		for (int i = 0; i < count; ++i) {
			xValues.add(String.valueOf(i));
			entries.add(new Entry((float) (Math.random() * 100), i));
		}
		LineDataSet dataSet = new LineDataSet(entries, getString(R.string.graph_temp_legend));
		styleTemperatureLine(dataSet);
		LineData lineData = new LineData(xValues, dataSet);
		lineChart.setData(lineData);
		styleGraph(lineChart);

		/*
		new SafeAsyncTask<Pair<ObjectNode, ObjectNode>>() {

			@Override
			public Pair<ObjectNode, ObjectNode> call() throws Exception {
				Pair<Double, Double> fromLocation = getCoordinatesFromDetails(placesService.getDetails(fromPlaceId));
				Pair<Double, Double> toLocation = getCoordinatesFromDetails(placesService.getDetails(toPlaceId));
				ObjectNode fromWeather = weatherService.getCurrentWeather(fromLocation.first, fromLocation.second);
				ObjectNode toWeather = weatherService.getCurrentWeather(toLocation.first, toLocation.second);
				return new Pair<>(fromWeather, toWeather);
			}

			@Override
			public void onSuccess(Pair<ObjectNode, ObjectNode> weatherDetails) {
				ArrayList<Entry> entries = new ArrayList<>();
				entries.add(new Entry(weatherUtils.getTemperature(weatherDetails.first), 0));
				entries.add(new Entry(weatherUtils.getTemperature(weatherDetails.second), 1));
				LineDataSet dataSet = new LineDataSet(entries, "Temperature");
				styleTemperatureLine(dataSet);
				LineData lineData = new LineData(new String[] { "0", "1" }, dataSet);
				lineChart.setData(lineData);
			}

			@Override
			public void onException(Exception e) {
				Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			}

		}.execute();
		*/
    }


	private Pair<Double, Double> getCoordinatesFromDetails(ObjectNode placesDetails) {
		JsonNode location = placesDetails.get("result").get("geometry").get("location");
		Timber.i(placesDetails.toString());
		System.out.println(placesDetails);
		return new Pair<>(
				location.get("lat").asDouble(),
				location.get("lng").asDouble());
	}


	private void styleGraph(LineChart lineChart) {
		lineChart.setDescription("");
		lineChart.setDrawYValues(false);

		Legend legend = lineChart.getLegend();
		legend.setForm(Legend.LegendForm.CIRCLE);

		XLabels xLabels = lineChart.getXLabels();
		xLabels.setPosition(XLabels.XLabelPosition.BOTTOM);

		YLabels yLabels = lineChart.getYLabels();
		yLabels.setPosition(YLabels.YLabelPosition.LEFT);
	}


	private void styleTemperatureLine(LineDataSet dataSet) {
		dataSet.setDrawCubic(true);
		dataSet.setLineWidth(4f);
		dataSet.setCircleSize(8f);
	}

}
