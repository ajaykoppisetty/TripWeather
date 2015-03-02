package org.faudroids.tripweather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.weather.Forecast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.inject.Inject;

import roboguice.fragment.RoboFragment;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import timber.log.Timber;


@ContentView(R.layout.fragment_graph)
public class GraphFragment extends RoboFragment {

	private static final String EXTRA_FORECASTS = "EXTRA_FORECASTS";

	private static final DecimalFormat yLabelsFormat = new DecimalFormat("##.#");


	public static GraphFragment createInstance(Forecast[] forecasts) {
		Bundle bundle = new Bundle();
		bundle.putParcelableArray(EXTRA_FORECASTS, forecasts);

		GraphFragment fragment = new GraphFragment();
		fragment.setArguments(bundle);
		return fragment;
	}


	@InjectView(R.id.graph) LineChart lineChart;
	@Inject GraphUtils graphUtils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_graph, container, false);
	}


	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Forecast[] forecasts = (Forecast[]) getArguments().getParcelableArray(EXTRA_FORECASTS);

		long startTimestamp = forecasts[0].getTimestamp();
		long endTimestamp = forecasts[forecasts.length - 1].getTimestamp();

		// all possible x values
		ArrayList<String> xValues = graphUtils.createXLabelsFromTimestamps(startTimestamp, endTimestamp);
		for (String xValue : xValues) Timber.d("xValue " + xValue);

		// actual data entries
		TreeMap<Integer, Entry> entries = new TreeMap<>();
		// ArrayList<Entry> entries = new ArrayList<>();
		for (Forecast forecast : forecasts) {
			Timber.d("adding " + (float) forecast.getTemperature() + ", " + graphUtils.createIndexFromTimestamp(startTimestamp, forecast.getTimestamp()) + " (" + forecast.getTimestamp() + ")");
			int xValue = graphUtils.createIndexFromTimestamp(startTimestamp, forecast.getTimestamp());
			entries.put(xValue, new Entry((float) forecast.getTemperature(), xValue));
		}

		// put lines onto the graph!
		LineDataSet dataSet = new LineDataSet(new ArrayList<>(entries.values()), getString(R.string.graph_temp_legend));
		styleTemperatureLine(dataSet);
		LineData lineData = new LineData(xValues, dataSet);
		lineChart.setData(lineData);
		lineChart.setStartAtZero(false);
		styleGraph(lineChart);
	}


	private void styleGraph(LineChart lineChart) {
		lineChart.setDescription("");
		lineChart.setDrawYValues(false);
		lineChart.setDrawLegend(false);

		XLabels xLabels = lineChart.getXLabels();
		xLabels.setPosition(XLabels.XLabelPosition.BOTTOM);

		YLabels yLabels = lineChart.getYLabels();
		yLabels.setPosition(YLabels.YLabelPosition.LEFT);
		yLabels.setFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float v) {
				return yLabelsFormat.format(v) + " " + (char) 0x00B0 + "C";
			}
		});
	}


	private void styleTemperatureLine(LineDataSet dataSet) {
		dataSet.setDrawCubic(true);
		dataSet.setLineWidth(4f);
		dataSet.setCircleSize(8f);
	}

}
