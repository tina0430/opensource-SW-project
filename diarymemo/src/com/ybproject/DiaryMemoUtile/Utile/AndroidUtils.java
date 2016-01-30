/**
 * 파일명: AndroidUtils.java
 * 최종수정: 2012년 2월 7일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: 버전 관리를 위한 유틸( 현재 재대로 사용은 아니하고 있음 )
 */
package com.ybproject.DiaryMemoUtile.Utile;

import java.util.List;

import com.ybproject.diarymemo.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

public class AndroidUtils {
	/**
	 * 용도 설명
	 * - 버전 관리를 매니퍼스트에서 체계적으로 관리하기 위하여,
	 *   매니퍼스트에서 정의된 버전 값을 가져와 다른 곳에서도 사용하게 합니다.
	 *   현재는 정의만 해놓고 사용은 하지 않습니다.
	 * 
	 */
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities(intent,
	                    PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	public static void setThemeFromPreferences(Context context) {
		SharedPreferences gameSettings = PreferenceManager.getDefaultSharedPreferences(context);
		String theme = gameSettings.getString("theme", "default");
		if (theme.equals("default")) {
			context.setTheme(R.style.MemoTheme);
		} else if (theme.equals("paperi")) {
			context.setTheme(R.style.MemoTheme);
		} else if (theme.equals("paperii")) {
			context.setTheme(R.style.MemoTheme);
		} else {
			context.setTheme(R.style.MemoTheme);
		}
	}
	
	/** 
	 * 현재 어플리케이션의 버전 정보 코드를 리턴합니다.
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** 
	 * 현재의 버전을 retrun 합니다.
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
