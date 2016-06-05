package com.ybproject.diarymemo.background;

import com.ybproject.diarymemo.DrawNoteList;
import com.ybproject.diarymemo.R;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.ybproject.diarymemo.provider.DrawNoteDB;

public class DrawNoteNotification extends Service {
	
	private static final int DRAW_NOTE_NOTIFICATION = 1;
	private static DrawNoteDB dbCon;
	
	public static void schedule(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, DrawNoteNotification.class);
		PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
		
		//FIXME: 현재 시작시간 수정
		// 대충 반복하는거 배터리 소모 덜함
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
						System.currentTimeMillis()+2000, 216000000, pi); //1시간에 한번씩 
		// am.setRepeating(type, triggerAtTime, interval, operation);

	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		dbCon = new DrawNoteDB(this.getApplicationContext());
		
		int notiCount = dbCon.select_noti();
		dbCon.delete_noti();
		dbCon.closeTodo();
		
		if(notiCount != 0){
			notifyNewDrawNote(notiCount);
		}
		stopSelf();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	void notifyNewDrawNote(int notiCount) {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification noti = new Notification(R.drawable.ic_launcher,
				""+R.string.new_noti_draw+"", System.currentTimeMillis());
		
		
		Intent intent = new Intent(this, DrawNoteList.class);
		intent.putExtra("noti", true);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		noti.setLatestEventInfo(this, ""+R.string.noti_draw_1+"" , notiCount + ""+R.string.noti_draw_2+"", pi);
		nm.notify(DRAW_NOTE_NOTIFICATION, noti);
		
	}

}
