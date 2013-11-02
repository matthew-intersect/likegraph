package com.death.likegraph;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class GraphPreferenceActivity extends PreferenceActivity
{
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

		addPreferencesFromResource(R.xml.graph_preferences);
	}
}
