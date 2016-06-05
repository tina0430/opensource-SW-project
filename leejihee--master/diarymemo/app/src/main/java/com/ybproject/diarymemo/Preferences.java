/**
 * 파일명: Preferences.java
 * 최종수정: 2012년 2월 11일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: 환경설정에 관련 설정
 */
package com.ybproject.diarymemo;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Window;

/** 다이어리 메모 어플리케이션에 대한 환경설정을 담당하는 액티비티  */
public class Preferences extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
	private static final String KEY_SORTORDER = "sortOrder";
	private static final String KEY_SORTASCENDING = "sortAscending";
	private static final String KEY_TEXTSIZE = "textSize";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		ListPreference sortOrderPreference = (ListPreference) findPreference(KEY_SORTORDER);
		sortOrderPreference.setOnPreferenceChangeListener(this);
		setSortOrderSummary(sortOrderPreference);
		setSortAscendingEnabled(!sortOrderPreference.getValue().equals("0"));

		ListPreference textSizePreference = (ListPreference) findPreference(KEY_TEXTSIZE);
		textSizePreference.setOnPreferenceChangeListener(this);
		setTextSizeSummary(textSizePreference);
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		final String key = preference.getKey();

		if (KEY_SORTORDER.equals(key)) {
			ListPreference sortOrderPreference = (ListPreference) preference;
			sortOrderPreference.setValue((String) newValue);
			setSortOrderSummary(sortOrderPreference);
			setSortAscendingEnabled(!((String) newValue).equals("0"));
			return false;
		}
		if (KEY_TEXTSIZE.equals(key)) {
			ListPreference textSizePreference = (ListPreference) preference;
			textSizePreference.setValue((String) newValue);
			setTextSizeSummary(textSizePreference);
			return false;
		}
		return true;
	}

	private void setSortOrderSummary(ListPreference preference) {
		preference.setSummary(getString(R.string.pref_sortOrderSummary, preference.getEntry()));	
	}

	private void setTextSizeSummary(ListPreference preference) {
		preference.setSummary(getString(R.string.pref_textSizeSummary, preference.getEntry()));
	}

	private void setSortAscendingEnabled(boolean enabled) {
		CheckBoxPreference sortAscendingPreference = (CheckBoxPreference) findPreference(KEY_SORTASCENDING);
		sortAscendingPreference.setEnabled(enabled);
		if (!enabled) sortAscendingPreference.setChecked(true);
	}
}
