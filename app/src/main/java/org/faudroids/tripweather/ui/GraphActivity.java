package org.faudroids.tripweather.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.weather.Forecast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import timber.log.Timber;


@ContentView(R.layout.activity_graph)
public class GraphActivity extends RoboActivity {

	private static final ObjectMapper mapper = new ObjectMapper();
	private static final String EXTRA_FORECASTS = "EXTRA_FORECASTS";


	public static Intent createIntent(Context context, List<Forecast> forecasts) {
		Intent intent = new Intent(context, GraphActivity.class);
		intent.putExtra(EXTRA_FORECASTS, mapper.valueToTree(forecasts).toString());
		return intent;
	}


	@InjectView(R.id.graph) LineChart lineChart;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<Forecast> forecasts;
		try {
			forecasts = mapper.readValue(getIntent().getStringExtra(EXTRA_FORECASTS), new TypeReference<List<Forecast>>() {
			});
		} catch (IOException ioe) {
			Timber.e(ioe, "failed to parse forecast data");
			// TODO should never happen
			return;
		}

		// all possible x values
		ArrayList<String> xValues = new ArrayList<>();
		int maxHour = forecasts.get(forecasts.size() - 1).getTimestamp();
		for (int hour = 0; hour <= maxHour; ++hour) {
			xValues.add(String.valueOf(hour));
		}

		// actual data entries
		ArrayList<Entry> entries = new ArrayList<>();
		for (Forecast forecast : forecasts) {
			Timber.d("adding " + (float) forecast.getTemperature() + ", " + forecast.getTimestamp());
			entries.add(new Entry((float) forecast.getTemperature(), forecast.getTimestamp()));
		}

		// put lines onto the graph!
		LineDataSet dataSet = new LineDataSet(entries, getString(R.string.graph_temp_legend));
		styleTemperatureLine(dataSet);
		LineData lineData = new LineData(xValues, dataSet);
		lineChart.setData(lineData);
		lineChart.setStartAtZero(false);
		styleGraph(lineChart);
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
