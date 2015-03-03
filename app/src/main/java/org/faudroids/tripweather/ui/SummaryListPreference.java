package org.faudroids.tripweather.ui;


import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public final class SummaryListPreference extends ListPreference {

	public SummaryListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public SummaryListPreference(Context context) {
		super(context);
	}


	@Override
	public void setValue(String value) {
		super.setValue(value);
		setSummary(value);
	}


	@Override
	public void setSummary(CharSequence summary) {
		super.setSummary(getEntry());
	}

}