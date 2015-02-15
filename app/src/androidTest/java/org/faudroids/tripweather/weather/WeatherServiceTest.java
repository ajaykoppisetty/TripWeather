package org.faudroids.tripweather.weather;


import android.test.AndroidTestCase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import junit.framework.Assert;

import org.faudroids.tripweather.R;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

public final class WeatherServiceTest extends AndroidTestCase {

	private WeatherService service;
	private Object lock;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		RestAdapter adapter = new RestAdapter.Builder()
				.setEndpoint(getContext().getString(R.string.open_weather_base_url))
				.setConverter(new JacksonConverter())
				.setRequestInterceptor(new RequestInterceptor() {
					@Override
					public void intercept(RequestFacade request) {
						request.addHeader("x-api-key", getContext().getString(R.string.open_weather_key));
					}
				})
				.build();
		this.service = adapter.create(WeatherService.class);
		this.lock = new Object();
	}


	public void testGetCurrentWeather() throws Exception {
		service.getCurrentWeather(45.0, 45.0, new CallbackAssertion(lock) {
			@Override
			protected void doSuccess(JsonNode jsonNode) {
				Assert.assertTrue(jsonNode instanceof ObjectNode);
				Assert.assertTrue(jsonNode.has("weather"));
			}
		});
		waitForCallback();
	}


	public void testGetForecast() throws Exception {
		service.getForecast(45.0, 45.0, new CallbackAssertion(lock) {
			@Override
			protected void doSuccess(JsonNode jsonNode) {
				Assert.assertEquals(41, jsonNode.get("list").size());
			}
		});
		waitForCallback();
	}


	public void testGetDailyForecast() throws Exception {
		service.getDailyForecast(45.0, 45.0, 7, new CallbackAssertion(lock) {
			@Override
			protected void doSuccess(JsonNode jsonNode) {
				Assert.assertEquals(7, jsonNode.get("list").size());
			}
		});
		waitForCallback();
	}


	private void waitForCallback() throws Exception {
		synchronized (lock) {
			lock.wait();
		}
	}


	private static abstract class CallbackAssertion implements Callback<JsonNode> {

		private final Object lock;

		public CallbackAssertion(Object lock) {
			this.lock = lock;
		}

		@Override
		public final void success(JsonNode jsonNode, Response response) {
			doSuccess(jsonNode);
			synchronized (lock) {
				lock.notify();
			}
		}

		@Override
		public final void failure(RetrofitError error) {
			Assert.fail(error.getMessage());
		}

		protected abstract void doSuccess(JsonNode jsonNode);

	}

}
