package org.faudroids.tripweather.ui;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.List;

import timber.log.Timber;


public class GraphUtilsTest extends TestCase {

	private GraphUtils graphUtils;


	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.graphUtils = new GraphUtils();
	}


	public void testCreateXLabels() {
		long currentTime = System.currentTimeMillis() / 1000l;
		List<String> xValues = graphUtils.createXLabelsFromTimestamps(currentTime, currentTime + 13023);
		Assert.assertEquals(16, xValues.size());
		for (String xValue : xValues) Timber.d(xValue);
	}


	public void testCreateIndexFromTimestamp() {
		long currentTime = System.currentTimeMillis() / 1000l;
		Assert.assertEquals(0, graphUtils.createIndexFromTimestamp(currentTime, currentTime + 1));
		Assert.assertEquals(1, graphUtils.createIndexFromTimestamp(currentTime, currentTime + 60 * 15 + 2));
	}

}