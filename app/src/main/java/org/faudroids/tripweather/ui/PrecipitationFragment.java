package org.faudroids.tripweather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.weather.Forecast;
import org.faudroids.tripweather.weather.ForecastMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.inject.Inject;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import timber.log.Timber;


public class PrecipitationFragment extends RoboFragment {

	private static final String EXTRA_FORECASTS = "EXTRA_FORECASTS";
	private static final String STATE_SHOW_RAIN = "STATE_SHOW_RAIN";

	private static final DecimalFormat yLabelsFormat = new DecimalFormat("####.##");


	public static PrecipitationFragment createInstance(Forecast[] forecasts) {
		Bundle bundle = new Bundle();
		bundle.putParcelableArray(EXTRA_FORECASTS, forecasts);

		PrecipitationFragment fragment = new PrecipitationFragment();
		fragment.setArguments(bundle);
		return fragment;
	}


	@InjectView(R.id.graph) CombinedChart combinedChart;
	@Inject GraphUtils graphUtils;
	private boolean showRain = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_precipitation, container, false);
	}


	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) showRain = savedInstanceState.getBoolean(STATE_SHOW_RAIN);
		updateGraph();
	}


	private void updateGraph() {
		Forecast[] forecasts = (Forecast[]) getArguments().getParcelableArray(EXTRA_FORECASTS);

		long startTimestamp = forecasts[0].getTimestamp();
		long endTimestamp = forecasts[forecasts.length - 1].getTimestamp();

		// all possible x values
		ArrayList<String> xValues = graphUtils.createXLabelsFromTimestamps(startTimestamp, endTimestamp);
		for (String xValue : xValues) Timber.d("xValue " + xValue);

		// data entries
		TreeMap<Integer, BarEntry> barEntries = new TreeMap<>();
		TreeMap<Integer, Entry> lineEntries = new TreeMap<>();

		for (Forecast forecast : forecasts) {
			int xValue = graphUtils.createIndexFromTimestamp(startTimestamp, forecast.getTimestamp());
			Timber.d("xValue = " + xValue);
			float yValue = (showRain) ? (float) forecast.getRain() : (float) forecast.getSnow();
			barEntries.put(xValue, new BarEntry(yValue, xValue));
			lineEntries.put(xValue, new Entry(yValue, xValue));
		}

		// put data onto the graph!
		Timber.d("there are " + barEntries.size() + " y values and " + xValues.size() + " x values");
		BarData barData = new BarData();
		barData.addDataSet(new BarDataSet(new ArrayList<>(barEntries.values()), ""));
		styleDataSet(barData.getDataSetByIndex(0));

		LineData lineData = new LineData();
		lineData.addDataSet(new LineDataSet(new ArrayList<>(lineEntries.values()), ""));
		styleDataSet(lineData.getDataSetByIndex(0));

		CombinedData combinedData = new CombinedData(xValues);
		combinedData.setData(barData);
		combinedData.setData(lineData);

		combinedChart.setData(combinedData);
		combinedChart.invalidate();
		styleGraph(forecasts[0].getForecastMode());
	}


	private void styleGraph(ForecastMode forecastMode) {
		int hoursOfForecast = forecastMode.getHourInterval();
		String description = (showRain)
				? getString(R.string.graph_rain_legend, hoursOfForecast)
				: getString(R.string.graph_snow_legend, hoursOfForecast);
		combinedChart.getLegend().setEnabled(false);
		combinedChart.setDescription(description);
		combinedChart.setDescriptionTextSize(16);

		XAxis xAxis = combinedChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

		combinedChart.getAxisRight().setEnabled(false);
		YAxis yAxis = combinedChart.getAxisLeft();
		yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
		yAxis.setValueFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float v) {
				return yLabelsFormat.format(v) + " mm";
			}
		});
		yAxis.setStartAtZero(true);
	}


	private void styleDataSet(BarDataSet dataSet) {
		int color = (showRain) ? R.color.blue : android.R.color.white;
		dataSet.setColor(getResources().getColor(color));
		dataSet.setBarShadowColor(getResources().getColor(android.R.color.transparent));
		dataSet.setValueFormatter(new ValueFormatter() {
			@Override
			public String getFormattedValue(float value) {
				if (value < 0.001) return "";
				else return yLabelsFormat.format(value);
			}
		});
	}


	private void styleDataSet(LineDataSet dataSet) {
		int color = (showRain) ? R.color.blue : android.R.color.white;
		dataSet.setColor(getResources().getColor(color));
		dataSet.setDrawCubic(true);
		dataSet.setLineWidth(4f);
		dataSet.setCircleSize(0f);
		dataSet.setDrawValues(false);
		dataSet.setDrawFilled(true);
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putBoolean(STATE_SHOW_RAIN, showRain);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_precipitation, menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.toggle:
				showRain = !showRain;
				updateGraph();
				getActivity().invalidateOptionsMenu();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem item = menu.findItem(R.id.toggle);
		if (showRain) item.setTitle(getString(R.string.graph_show_snow));
		else item.setTitle(getString(R.string.graph_show_rain));
	}

}
