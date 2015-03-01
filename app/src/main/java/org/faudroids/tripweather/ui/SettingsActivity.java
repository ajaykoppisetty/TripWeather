package org.faudroids.tripweather.ui;


import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import org.faudroids.tripweather.R;

import roboguice.activity.RoboPreferenceActivity;
import timber.log.Timber;

public final class SettingsActivity extends RoboPreferenceActivity {

	@Override
	@SuppressWarnings("deprecated")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		getPref(R.string.settings_version).setSummary(getVersion());
		setOnClickDialogForPref(R.string.settings_about, R.string.settings_about_title, R.string.settings_about_msg);
		setOnClickDialogForPref(R.string.settings_credits, R.string.settings_credits_title, R.string.settings_credits_msg);
	}


	private void setOnClickDialogForPref(int prefResourceId, final int titleResourceId, final int msgResourceId) {
		getPref(prefResourceId).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this)
						.setTitle(titleResourceId)
						.setMessage(Html.fromHtml(getString(msgResourceId)))
						.setPositiveButton(android.R.string.ok, null)
						.show();

				((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
				return true;
			}
		});

	}


	private Preference getPref(int stringResourceId) {
		return findPreference(getString(stringResourceId));
	}


	private String getVersion() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException nnfe) {
			Timber.e(nnfe, "failed to get version");
			return null;
		}
	}

}
