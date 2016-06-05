/**
 * 파일명: DiaryMemoHelo.java
 * 최종수정: 2012년 2월 12일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: 도움말 페이지
 */
package com.ybproject.diarymemo.desk;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;
import com.ybproject.diarymemo.DiaryMemoAppManager;
import com.ybproject.diarymemo.R;
import android.app.*;
import android.content.res.Configuration;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.webkit.*;

public class DiaryMemoHelp extends Activity {
	WebView mWeb;
	//광고
    private AdView adView = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		String systemLanguage = getResources().getConfiguration().locale.getLanguage();
		String Ko_lang = "ko";

		//사용언어가 한국어 일경우에만 아담 광고 노출
		if(Ko_lang.equals(systemLanguage)){
			initAdam(); //아담 광고 초기화
		}else{
			//차후 로케이션이 영문일경우 애드몹 광고 추가예정
			Log.i("lang:",systemLanguage);
		}

		mWeb = (WebView)findViewById(R.id.web);
		mWeb.setWebViewClient(new MyWebClient());
		WebSettings set = mWeb.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(false);
		
		mWeb.loadUrl("");
		
	}

	
	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	
	  //광고 모듈
		@Override
		public void onDestroy() {
			super.onDestroy();

			if (adView != null) {
				adView.destroy();
				adView = null;
			}
		}
		
		private void initAdam() {
			// Ad@m sdk 초기화 시작
			DiaryMemoAppManager AdClientID = new DiaryMemoAppManager();
			String ClientID = AdClientID.ClientID;
			adView = (AdView) findViewById(R.id.adview);
			adView.setRequestInterval(5);

			// 할당 받은 clientId 설정
			adView.setClientId(ClientID);

			adView.setRequestInterval(12);

			// Animation 효과 : 기본 값은 AnimationType.NONE
			adView.setAnimationType(AnimationType.FLIP_HORIZONTAL);

			adView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onConfigurationChanged(Configuration newConfig) {
			super.onConfigurationChanged(newConfig);
		}
}