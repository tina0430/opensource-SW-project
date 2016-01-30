/**
 * 파일명: DiaryMemoHelo.java
 * 최종수정: 2012년 2월 25일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: 홈페이지 이동
 */
package com.ybproject.diarymemo.desk;

import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.webkit.*;

public class DiaryMemoHomepage extends Activity {
	WebView mWeb;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Uri uri = Uri.parse("");
		Intent it = new Intent(Intent.ACTION_VIEW,uri);
		startActivity(it);
		finish();
	}
}