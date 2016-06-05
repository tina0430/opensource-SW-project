/**
 * 파일명: AppUpdateReciever.java
 * 최종수정: 2012년 3월 25일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: 어플리케이션 업데이트시 일부 설정값을 초기화 처리함
 */
package com.ybproject.diarymemo;

import android.content.BroadcastReceiver;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class AppUpdateReciever extends BroadcastReceiver {
	@Override 
    public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction(); //발생한 액션을 저장(신규,업데이트,제거등)
	    final String packageName = intent.getData().getSchemeSpecificPart(); //설치되는 어플리케이션의 패키지명을 알아냄

//	    Log.d("환경설정 초기화:","업데이트에 따른 일부 설정 초기화 작업 시작");
	    // 패키지명 체크, 패키지명이 동일한지 확인
		if(packageName.equals("com.ybproject.diarymemo")){
			// 발생하는 액션이 재설치일 경우 업데이트 내역 안내,업데이트 나중에 하기 를 초기화 한다
			if(Intent.ACTION_PACKAGE_REPLACED.equals(action)){
				SharedPreferences prefs = context.getSharedPreferences("update_check" , Context.MODE_PRIVATE);
				SharedPreferences.Editor ed = prefs.edit();
				ed.putInt("welcon_news" , 0 );
				ed.putInt("update_check" , 0 );
				ed.commit();
			}
		}
		
    } 
}