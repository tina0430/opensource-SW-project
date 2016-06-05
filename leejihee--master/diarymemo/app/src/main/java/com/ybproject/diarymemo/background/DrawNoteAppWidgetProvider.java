package com.ybproject.diarymemo.background;

import java.util.ArrayList;

import com.ybproject.diarymemo.DrawNoteBoard;
import com.ybproject.diarymemo.DrawNoteList;
import com.ybproject.diarymemo.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import com.ybproject.diarymemo.provider.DrawNoteDB;

public class DrawNoteAppWidgetProvider extends AppWidgetProvider {

	private static final String TAG = "duck";
	private static DrawNoteDB dbCon;
	private static final int FLIPPER_LENGTH = 10;
	private RemoteViews views;
	private AppWidgetManager appWidgetManager;
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled");
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled");
       		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		if(intent.getAction().equals("DRAWNOTE_END"))
		{
			ComponentName thisWidget = new ComponentName(context, getClass());
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(thisWidget, views);
			this.onUpdate(context, manager, manager.getAppWidgetIds(thisWidget));
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(TAG, "onUpdate");
		
		this.appWidgetManager = appWidgetManager;
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            updateDrawNote(context, appWidgetId);
        }
      
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	public void updateDrawNote(Context context, int appWidgetId) {
		Log.d(TAG, "updateDrawNote()");
		Intent listIntent = new Intent(context, DrawNoteList.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, listIntent, 0);
		views.setOnClickPendingIntent(R.id.flipper, pi);
		listIntent = new Intent(context, DrawNoteBoard.class);
		pi = PendingIntent.getActivity(context, 0, listIntent, 0);
		views.setOnClickPendingIntent(R.id.btn_widget, pi);

		int[] viewId = { R.id.view01, R.id.view02, R.id.view03, R.id.view04,
				R.id.view05, R.id.view06, R.id.view07, R.id.view08,
				R.id.view09, R.id.view10 };

		dbCon = new DrawNoteDB(context);
		try {
			ArrayList<Bitmap> bitmap = dbCon.select_widget();
			// FIXME : 메모가 없을 때 처리
			if (bitmap.isEmpty()) {
//				Toast.makeText(context, "위젯에 등록된 메모가 없습니다", Toast.LENGTH_LONG).show();
				for (int i = 0; i < FLIPPER_LENGTH; i++) {
					views.setImageViewResource(viewId[i], R.id.check_back);
				}
			} else {
				int temp = 0;
				int size = bitmap.size();
				if (size != 0) {
					for (int i = 0; i < FLIPPER_LENGTH; i++) {
						views.setImageViewBitmap(viewId[i], bitmap.get(temp++));
						if (temp == size) {
							temp = 0;
						}
					}
				}
			}
		} catch (Exception e) {
		}
		dbCon.closeTodo();

		// Tell the widget manager
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
}
