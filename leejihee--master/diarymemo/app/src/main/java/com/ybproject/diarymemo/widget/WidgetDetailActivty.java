package com.ybproject.diarymemo.widget;

import com.ybproject.diarymemo.R;
import com.ybproject.diarymemo.provider.DiaryMemo;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.*;
import android.preference.PreferenceManager;
import android.util.*;
import android.widget.*;

public class WidgetDetailActivty extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_detail);

		
		Intent intent = getIntent();
		int noteid = intent.getIntExtra("noteid", 100); 
//		Log.d(DiaryMemoWidgetProvider2x.TAG, "컨텐츠 ID = " + noteid);
		TextView detail = (TextView)findViewById(R.id.detailnews);
		detail.setText("컨텐츠 ID : " + noteid);
	}
	
}
