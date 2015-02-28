package org.faudroids.tripweather.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.faudroids.tripweather.R;

import roboguice.fragment.RoboFragment;

public final class TestFragment extends RoboFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_test, container, false);
	}

}
