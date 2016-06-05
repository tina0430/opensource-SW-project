package com.ybproject.diarymemo.widget;

import com.ybproject.diarymemo.*;
import com.ybproject.diarymemo.provider.*;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.*;
import android.content.*;
import android.database.Cursor;
import android.graphics.*;
import android.net.Uri;
import android.util.*;

public class DiaryMemoWidgetProvider2x extends AppWidgetProvider{
	final static String ACTION_WIDGET_CHANGE = "WidgetChange";
	final static String PREF = "com.ybproject.diarymemo.Widget";
	final static String TAG = "WidgetNote";
	private static final String[] PROJECTION = new String[] { 
		DiaryMemo._ID, DiaryMemo.BODY
	};
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		int temp;
		int i = 0;
		String temp2;
		String temp_uri = "content://com.ybproject.diarymemo/notes/";
		Uri uri = null;
		
			Cursor cursor = context.getContentResolver().query(uri.parse(temp_uri), null, DiaryMemo.WIDGET_ID , null, null);
			while(cursor.moveToNext()) {
			         int index=cursor.getColumnIndex(DiaryMemo.WIDGET_ID);
			         int index2=cursor.getColumnIndex(DiaryMemo.BODY);
			         temp = cursor.getInt(index);
			         temp2 = cursor.getString(index2);
					
						appWidgetIds[i] = temp;
							
						RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_2x);
						views.setTextViewText(R.id.view_action2x, temp2);
						appWidgetManager.updateAppWidget(appWidgetIds[i] , views );
						i++;
			}
	}
	
	static void UpdateNote(Context context, AppWidgetManager appWidgetManager, 
			int widgetId , String string_test) {

		String widget_note = "메모내용이 존재하지 않습니다.";

		widget_note = string_test;
		RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_2x);
		
		views.setTextViewText(R.id.view_action2x, widget_note );
		SharedPreferences prefs = context.getSharedPreferences(PREF, 0);
		boolean isRed = prefs.getBoolean("red_" + widgetId, false);
		views.setTextColor(R.id.view_action2x, isRed ? Color.RED:Color.BLACK);
//		Log.d(TAG, "위젯ID = " + widgetId);

//		Intent intent = new Intent(context, WidgetDetailActivty.class);
//		intent.putExtra("noteid", widget_note);
//		PendingIntent pending = PendingIntent.getActivity(context, widgetId, 
//				intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		views.setOnClickPendingIntent(R.id.mainlayout2x, pending);

		appWidgetManager.updateAppWidget(widgetId, views);
	}
	
	public void onReceive(Context context, Intent intent , String test) {
		String action = intent.getAction();
		if (action != null && action.equals(ACTION_WIDGET_CHANGE)) {
			int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 
					AppWidgetManager.INVALID_APPWIDGET_ID);
			UpdateNote(context, AppWidgetManager.getInstance(context), id, test);
			return;
		}
		super.onReceive(context, intent);
	}
	
	public void onDeleted(Context context, int[] appWidgetIds) {
		for (int i = 0; i < appWidgetIds.length; i++) {
//			Log.d(TAG, "onDeleted호출, id = " + appWidgetIds[i]);
			SharedPreferences prefs = context.getSharedPreferences(PREF, 0);
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove("red_" + appWidgetIds[i]);
			editor.commit();
			
			String temp_uri = "content://com.ybproject.diarymemo/notes/#/widget_id";
			Uri uri = null;
			int temp;
			int temp2;
			Cursor cursor = context.getContentResolver().query(uri.parse(temp_uri), null, DiaryMemo.WIDGET_ID , null, null);
			while(cursor.moveToNext()) {
		         int index=cursor.getColumnIndex(DiaryMemo.WIDGET_ID);
		         int index2=cursor.getColumnIndex(DiaryMemo._ID);
		         temp = cursor.getInt(index);
		         temp2 = cursor.getInt(index2);
		         
		         String temp_uri2 = "content://com.ybproject.diarymemo/notes/"+temp2+"";
		         
		         // 삭제되는 위젯ID 를 테이블의 widget_id = 0으로 초기화 한다
		         if(appWidgetIds[i] == temp){
				 		ContentValues values = new ContentValues();
						
						values.put(DiaryMemo.WIDGET_ID, 0);
						context.getContentResolver().update(uri.parse(temp_uri2), values, null, null);
		         }

			}
		}
	}
	
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled");
	}

	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled");
	}
}
