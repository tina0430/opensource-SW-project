package com.ybproject.DiaryMemoUtile.Utile;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CommonUtil {
	
	private static final String TAG = "CommonUtil";
	
	/**
	 * 이동 통신사 이름 얻어 오기
	 * @param activity
	 * @return 
	 */
	public static String getOperatorName(Activity activity){
    	TelephonyManager tm = (TelephonyManager) activity.getSystemService
    			(activity.TELEPHONY_SERVICE);
    	return tm.getNetworkOperatorName();
    }
	
	/**
	 * 국가 코드 얻어 오기
	 * < 참고자료 > ISO 국가 CODE URL
	 * https://digitalid.crosscert.com/secureserver/server/help/ccodes.htm
	 * @param activity
	 * @return 
	 */
	public static String getCountrylso(Activity activity){
    	TelephonyManager tm = (TelephonyManager) activity.getSystemService
    			(activity.TELEPHONY_SERVICE);
    	return tm.getSimCountryIso();
    }
	
	/**
	 * 로밍 여부 확인
	 * @param activity
	 * @return boolean
	 */
	public static boolean getRoamingState(Activity activity){
    	TelephonyManager tm = (TelephonyManager) activity.getSystemService
    			(activity.TELEPHONY_SERVICE);
    	return tm.isNetworkRoaming();
    }
	
	/**
	 * 시스템 언어를 가져와서 준비된 언어가 아닐경우 영어로 지정
	 * @param con
	 * @return
	 */
	public static String getLanguage(Context con) {
		Log.d(TAG, "getLanguage() Start");
		String systemLanguage = con.getResources().getConfiguration().locale.getLanguage();
		
		if(systemLanguage.equals(Locale.KOREAN.toString())) {
			 Log.d(TAG, "System language is KOREAN.");
		 } else if(systemLanguage.equals(Locale.ENGLISH.toString())) {
			 Log.d(TAG, "System language is ENGLISH.");
		 } else if(systemLanguage.equals(Locale.JAPANESE.toString())) {
			 Log.d(TAG, "System language is JAPANESE.");
		 } else if(systemLanguage.equals(Locale.CHINESE.toString())) {
			 Log.d(TAG, "System language is CHINESS.");
		 } else if(systemLanguage.equals("in")) {
			 Log.d(TAG, "System language is INDONESIAN.");
		 } else {
			 Log.d(TAG, "System language is other. set default(en)");
			 systemLanguage = "en";
		 }
		
		return systemLanguage;
	}
}
