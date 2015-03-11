package org.faudroids.tripweather.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.faudroids.tripweather.R;
import org.faudroids.tripweather.geo.DirectionsException;
import org.faudroids.tripweather.geo.GeoCodingException;
import org.faudroids.tripweather.geo.LocationUtils;
import org.faudroids.tripweather.geo.TravelMode;
import org.faudroids.tripweather.network.DataManager;
import org.faudroids.tripweather.network.TripData;
import org.faudroids.tripweather.weather.WeatherException;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity implements
		GoogleApiClient.OnConnectionFailedListener,
		GoogleApiClient.ConnectionCallbacks {

	private static final PrettyTime prettyTime = new PrettyTime();

	private static final String
			STATE_FROM = "from",
			STATE_FROM_ICON = "fromIcon",
			STATE_TO = "to",
			STATE_TO_ICON = "toIcon",
			STATE_TIME = "time",
			STATE_TIME_ICON = "timeIcon",
			STATE_TRAVEL_MODE = "travelMode",
			STATE_TRAVEL_MODE_ICON = "travelModeIcon";

	private static final int
			LOCATION_REQUEST = 42,
			TIME_REQUEST = 43,
			TRAVEL_MODE_REQUEST = 44,
			GOOGLE_API_CLIENT_REQUEST = 45;

	@InjectView(R.id.origin) View originView;
	@InjectView(R.id.origin_description) TextView originDescriptionView;
	@InjectView(R.id.origin_value) AutoResizeTextView originValueView;
	@InjectView(R.id.origin_icon) ImageView originIcon;

	@InjectView(R.id.destination) View destinationView;
	@InjectView(R.id.destination_description) TextView destinationDescriptionView;
	@InjectView(R.id.destination_value) AutoResizeTextView destinationValueView;
	@InjectView(R.id.destination_icon) ImageView destinationIcon;

	@InjectView(R.id.time) View timeView;
	@InjectView(R.id.time_description) TextView timeDescriptionView;
	@InjectView(R.id.time_value) AutoResizeTextView timeValueView;
	@InjectView(R.id.time_icon) ImageView timeIcon;

	@InjectView(R.id.travel_mode) View travelModeView;
	@InjectView(R.id.travel_mode_description) TextView travelModeDescriptionView;
	@InjectView(R.id.travel_mode_value) AutoResizeTextView travelModeValueView;
	@InjectView(R.id.travel_mode_icon) ImageView travelModeIcon;

	@InjectView(R.id.settings) View settingsView;

	private String locationFrom, locationTo;
	private long startTime = 0;
	private TravelMode travelMode;

	private GoogleApiClient googleApiClient;

	@Inject DataManager dataManager;
	@Inject LocationUtils locationUtils;
	private CompositeSubscription tripDataDownload = new CompositeSubscription();
	private ProgressDialog progressDialog;

	private MenuItem goMenuItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.settings, false);

		googleApiClient = createGoogleApiClient();
		googleApiClient.connect();

		updateInputViews();
		originView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = LocationInputActivity.createIntent(MainActivity.this, locationFrom, true);
				startActivityForResult(intent, LOCATION_REQUEST);
			}
		});

		destinationView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = LocationInputActivity.createIntent(MainActivity.this, locationTo, false);
				startActivityForResult(intent, LOCATION_REQUEST);
			}
		});

		timeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = TimeInputActivity.createIntent(MainActivity.this, startTime);
				startActivityForResult(intent, TIME_REQUEST);
			}
		});

		travelModeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = TravelModeInputActivity.createIntent(MainActivity.this);
				startActivityForResult(intent, TRAVEL_MODE_REQUEST);
			}
		});

		settingsView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
				startActivity(intent);
			}
		});
	}


	@Override
	public void onDestroy() {
		googleApiClient.disconnect();
		tripDataDownload.unsubscribe();
		super.onDestroy();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		switch(requestCode) {
			// user has selected location
			case LOCATION_REQUEST:
				String location = data.getExtras().getString(LocationInputActivity.EXTRA_LOCATION);
				boolean isFromLocation = data.getBooleanExtra(LocationInputActivity.EXTRA_FROM, false);
				if (isFromLocation) locationFrom = location;
				else locationTo = location;
				updateInputViews();
				checkInputAndShowDetailsActivity();
				if (isFromLocation) showDoneAnimation(originIcon);
				else showDoneAnimation(destinationIcon);
				break;

			case TIME_REQUEST:
				startTime = data.getLongExtra(TimeInputActivity.EXTRA_TIME, 0);
				updateInputViews();
				checkInputAndShowDetailsActivity();
				showDoneAnimation(timeIcon);
				break;

			case TRAVEL_MODE_REQUEST:
				travelMode = (TravelMode) data.getSerializableExtra(TravelModeInputActivity.EXTRA_TRAVEL_MODE);
				updateInputViews();
				checkInputAndShowDetailsActivity();
				showDoneAnimation(travelModeIcon);
				break;

			// resolved google api client connection?
			case GOOGLE_API_CLIENT_REQUEST:
				if (!googleApiClient.isConnected() && !googleApiClient.isConnecting()) {
					googleApiClient.connect();
				}
				break;
		}
	}


	private void showDoneAnimation(final ImageView imageView) {
		if (((Integer) R.drawable.ic_done).equals(imageView.getTag())) return;
		imageView.setTag(R.drawable.ic_done);
		Animation shrinkAnim = AnimationUtils.loadAnimation(this, R.anim.scale_hide);
		final Animation growAnim = AnimationUtils.loadAnimation(this, R.anim.scale_show);
		imageView.setAnimation(shrinkAnim);
		shrinkAnim.setStartOffset(300);
		shrinkAnim.start();
		shrinkAnim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) { }

			@Override
			public void onAnimationRepeat(Animation animation) { }

			@Override
			public void onAnimationEnd(Animation animation) {
				imageView.setImageResource(R.drawable.ic_done);
				imageView.setAnimation(growAnim);
				growAnim.start();
			}
		});
	}


	private void updateInputViews() {
		updateInputView(locationFrom, originDescriptionView, originValueView, R.string.input_from, R.string.input_from_example);
		updateInputView(locationTo, destinationDescriptionView, destinationValueView, R.string.input_to, R.string.input_to_example);

		String formattedTime = (startTime == 0) ? null : prettyTime.format(new Date(startTime * 1000));
		if ("moments ago".equals(formattedTime)) formattedTime = "now";
		updateInputView(formattedTime, timeDescriptionView, timeValueView, R.string.input_time, R.string.input_time_example);

		String formattedTravelMode = null;
		if (travelMode != null) formattedTravelMode = getString(travelMode.getResourceId());
		updateInputView(formattedTravelMode, travelModeDescriptionView, travelModeValueView, R.string.input_how, R.string.input_how_example);
	}


	private void updateInputView(String location, TextView descriptionView, AutoResizeTextView valueView, int descriptionResource, int valueResource) {
		if (location == null || location.equals("")) {
			descriptionView.setText(getString(descriptionResource, getString(R.string.question_mark)));
			descriptionView.setTextAppearance(this, R.style.MainMenuFontLarge);
			valueView.setText(getString(valueResource));
			valueView.setTextAppearance(this, R.style.MainMenuFontSmall);
			valueView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_menu_font_small));

		} else {
			descriptionView.setText(getString(descriptionResource, ""));
			descriptionView.setTextAppearance(this, R.style.MainMenuFontSmall);
			if (locationUtils.isEncodedLocation(location)) {
				valueView.setText(getString(R.string.input_selected_from_map));
			} else {
				valueView.setText(location);
			}
			valueView.setTextAppearance(this, R.style.MainMenuFontLarge);
			valueView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.main_menu_font_large));
		}
		valueView.resetTextSize();
	}


	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		locationFrom = restoreInput(savedInstanceState, STATE_FROM, STATE_FROM_ICON, originIcon);
		locationTo = restoreInput(savedInstanceState, STATE_TO, STATE_TO_ICON, destinationIcon);

		Long restoredTime = restoreInput(savedInstanceState, STATE_TIME, STATE_TIME_ICON, timeIcon);
		if (restoredTime != null) startTime = restoredTime;

		travelMode = restoreInput(savedInstanceState, STATE_TRAVEL_MODE, STATE_TRAVEL_MODE_ICON, travelModeIcon);

		updateInputViews();
		toggleGoMenuItem();
	}


	@SuppressWarnings("unchecked")
	private <T> T restoreInput(Bundle savedInstanceState, String keyValue, String keyIconTag, ImageView icon) {
		T value = null;
		if (savedInstanceState.containsKey(keyValue)) {
			value = (T) savedInstanceState.get(keyValue);
		}
		int iconId = savedInstanceState.getInt(keyIconTag);
		if (iconId != 0) {
			icon.setTag(iconId);
			icon.setImageResource(R.drawable.ic_done);
		}
		return value;
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString(STATE_FROM, locationFrom);
		if (originIcon.getTag() != null) savedInstanceState.putInt(STATE_FROM_ICON, (int) originIcon.getTag());

		savedInstanceState.putString(STATE_TO, locationTo);
		if (destinationIcon.getTag() != null) savedInstanceState.putInt(STATE_TO_ICON, (int) destinationIcon.getTag());

		if (startTime != 0) savedInstanceState.putLong(STATE_TIME, startTime);
		if (timeIcon.getTag() != null) savedInstanceState.putInt(STATE_TIME_ICON, (int) timeIcon.getTag());

		if (travelMode != null) savedInstanceState.putSerializable(STATE_TRAVEL_MODE, travelMode);
		if (travelModeIcon.getTag() != null) savedInstanceState.putInt(STATE_TRAVEL_MODE_ICON, (int) travelModeIcon.getTag());

		super.onSaveInstanceState(savedInstanceState);
	}


	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Timber.w("Google Api Client connection failed, " + connectionResult.getErrorCode());
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, GOOGLE_API_CLIENT_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				Timber.e(e, "failed to start google client api resolution");
				googleApiClient.connect();
			}
		} else {
			GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, GOOGLE_API_CLIENT_REQUEST).show();
		}
	}


	@Override
	public void onConnected(Bundle bundle) {
		toggleGoMenuItem();
	}


	@Override
	public void onConnectionSuspended(int i) {
		toggleGoMenuItem();
	}


	private boolean isInputComplete() {
		if (locationFrom == null || locationTo == null || startTime == 0 || travelMode == null) return false;
		return googleApiClient.isConnected();
	}


	private void checkInputAndShowDetailsActivity() {
		if (!isInputComplete()) return;

		tripDataDownload.add(Observable
				.just(null)
				.delay(500, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<Object>() {
					@Override
					public void call(Object o) {
						progressDialog = ProgressDialog.show(MainActivity.this, getString(R.string.loading_title), getString(R.string.loading_message), true, true);
						progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								tripDataDownload.unsubscribe();
								tripDataDownload = new CompositeSubscription();
							}
						});

						tripDataDownload.add(dataManager
								.getData(googleApiClient, locationFrom, locationTo, travelMode, startTime)
								.subscribeOn(Schedulers.io())
								.observeOn(AndroidSchedulers.mainThread())
								.subscribe(new Action1<TripData>() {
									@Override
									public void call(TripData tripData) {
										Intent intent = DetailsActivity.createIntent(MainActivity.this, tripData);
										startActivity(intent);
										dismissProgressAndResetSubscription();
										toggleGoMenuItem();
									}
								}, new Action1<Throwable>() {
									@Override
									public void call(Throwable throwable) {
										Timber.i(throwable, "failed to run data manager");
										dismissProgressAndResetSubscription();
										handleDataDownloadError(throwable);
									}
								}));
					}
				}));
	}


	private void dismissProgressAndResetSubscription() {
		progressDialog.dismiss();
		progressDialog = null;

		tripDataDownload.unsubscribe();
		tripDataDownload = new CompositeSubscription();
	}


	private GoogleApiClient createGoogleApiClient() {
		return new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		goMenuItem = menu.findItem(R.id.go);
		toggleGoMenuItem();
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.go:
				checkInputAndShowDetailsActivity();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void toggleGoMenuItem() {
		if (goMenuItem == null) return;
		if (isInputComplete()) goMenuItem.setVisible(true);
		else goMenuItem.setVisible(false);
	}


	private void handleDataDownloadError(Throwable throwable) {
		int titleResource;
		String msg = null;

		if (throwable instanceof GeoCodingException) {
			GeoCodingException e = (GeoCodingException) throwable;
			titleResource = R.string.error_location_title;
			switch(e.getType()) {
				case ZERO_RESULTS:
					msg = getString(R.string.error_location_nonexistent, e.getLocation());
					break;

				case INTERNAL_ERROR:
					msg = getString(R.string.error_location_internal, e.getLocation());
					break;

				case USER_LOCATION_UNAVAILABLE:
					msg = getString(R.string.error_location_user);
					break;
			}

		} else if (throwable instanceof DirectionsException) {
			DirectionsException e = (DirectionsException) throwable;
			titleResource = R.string.error_route_title;
			switch(e.getType()) {
				case ZERO_RESULTS:
					msg = getString(R.string.error_route_nonexistent);
					break;

				case INTERNAL_ERROR:
					msg = getString(R.string.error_route_internal);
					break;
			}

		} else if (throwable instanceof WeatherException) {
			WeatherException e = (WeatherException) throwable;
			titleResource = R.string.error_route_title;
			switch (e.getType()) {
				case TOO_DISTANT_DATE:
					msg = getString(R.string.error_weather_data_too_distant);
					break;
			}

		} else if (throwable instanceof RetrofitError) {
			titleResource = R.string.error_network_title;
			msg = getString(R.string.error_network_msg);

		} else {
			Timber.e(throwable, "unknown error in DataManager");
			titleResource = R.string.error_unknown_title;
			msg = getString(R.string.error_unknown_msg);
		}

		new AlertDialog.Builder(this)
				.setTitle(titleResource)
				.setMessage(msg)
				.setIcon(R.drawable.ic_action_warning)
				.setPositiveButton(android.R.string.ok, null)
				.show();
	}

}
