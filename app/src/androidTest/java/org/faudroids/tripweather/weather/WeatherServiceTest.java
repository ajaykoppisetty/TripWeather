package org.faudroids.tripweather.weather;


import android.test.AndroidTestCase;

import com.fasterxml.jackson.databind.node.ObjectNode;

import junit.framework.Assert;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class WeatherServiceTest extends AndroidTestCase {

	private WeatherService service;
	private Object lock;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.service = new WeatherModule().provideWeatherService(getContext());
		this.lock = new Object();
	}


	/*
	public void testGetCurrentWeather() throws Exception {
		service.getCurrentWeather(45.0, 45.0, new CallbackAssertion(lock) {
			@Override
			protected void doSuccess(ObjectNode objectNode) {
				Assert.assertTrue(objectNode.has("weather"));
			}
		});
		waitForCallback();
	}


	public void testGetForecast() throws Exception {
		service.getForecast(45.0, 45.0, new CallbackAssertion(lock) {
			@Override
			protected void doSuccess(ObjectNode objectNode) {
				Assert.assertEquals(41, objectNode.get("list").size());
			}
		});
		waitForCallback();
	}


	public void testGetDailyForecast() throws Exception {
		service.getDailyForecast(45.0, 45.0, 7, new CallbackAssertion(lock) {
			@Override
			protected void doSuccess(ObjectNode objectNode) {
				Assert.assertEquals(7, objectNode.get("list").size());
			}
		});
		waitForCallback();
	}
	*/


	private void waitForCallback() throws Exception {
		synchronized (lock) {
			lock.wait();
		}
	}


	private static abstract class CallbackAssertion implements Callback<ObjectNode> {

		private final Object lock;

		public CallbackAssertion(Object lock) {
			this.lock = lock;
		}

		@Override
		public final void success(ObjectNode objectNode, Response response) {
			doSuccess(objectNode);
			synchronized (lock) {
				lock.notify();
			}
		}

		@Override
		public final void failure(RetrofitError error) {
			Assert.fail(error.getMessage());
		}

		protected abstract void doSuccess(ObjectNode objectNode);

	}

}
