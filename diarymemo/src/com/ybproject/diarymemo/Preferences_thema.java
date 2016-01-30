package com.ybproject.diarymemo;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences_thema extends PreferenceActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences_thema);
	}

}
