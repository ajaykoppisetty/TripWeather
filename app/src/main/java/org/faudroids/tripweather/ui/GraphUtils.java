package org.faudroids.tripweather.ui;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

final class GraphUtils {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
	private static final long quarterHour = 60 * 15;

	public ArrayList<String> createXLabelsFromTimestamps(long startTimestamp, long endTimestamp) {
		ArrayList<String> labels = new ArrayList<>();

		long currentTimestamp = startTimestamp;
		do {
			long roundedTimestamp = roundToQuarterHour(currentTimestamp);
			labels.add(dateFormat.format(new Date(roundedTimestamp * 1000)));
			currentTimestamp += quarterHour;
		} while (roundToQuarterHour(currentTimestamp) <= roundToQuarterHour(endTimestamp));

		return labels;
	}


	public int createIndexFromTimestamp(long startTimestamp, long timestamp) {
		return (int) (roundToQuarterHour(timestamp - startTimestamp) / quarterHour);
	}


	private long roundToQuarterHour(long timestamp) {
		timestamp += quarterHour / 2; // ensures fair rounding (up AND down)
		return timestamp - (timestamp % quarterHour);
	}

}
