/**
 * 파일명: DiaryMemoHelo.java
 * 최종수정: 2012년 2월 12일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명:버전 체크
 */
package com.ybproject.diarymemo.desk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;

import com.ybproject.diarymemo.DiaryMemoAppManager;
import com.ybproject.diarymemo.R;
import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

public class DiaryMemoInfo extends Activity {
	String result;
	DownloadText downloadtext = new DownloadText();
	//광고
    private AdView adView = null;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		String systemLanguage = getResources().getConfiguration().locale.getLanguage();
		String Ko_lang = "ko";

		//사용언어가 한국어 일경우에만 아담 광고 노출
		if(Ko_lang.equals(systemLanguage)){
			initAdam(); //아담 광고 초기화
		}else{
			//차후 로케이션이 영문일경우 애드몹 광고 추가예정
			Log.i("lang:",systemLanguage);
		}

		// 업데이트 스레드 시작
		downloadtext.start();

	}
	
	public void newOnClick(View v) {		
		DownloadText test123 = new DownloadText();
		test123.setDaemon(true);
		test123.start();
		switch (v.getId()) {
			case R.id.btnok2:
			try{				
				if(Double.parseDouble(result) > DiaryMemoAppManager.sMyVersion )   
				{
					//구버전일 경우
					ShowDialog();
				}else{
					Toast.makeText(DiaryMemoInfo.this, R.string.last_version, Toast.LENGTH_LONG).show();
				}
				break;
			}catch(Exception ex){ Toast.makeText(DiaryMemoInfo.this, "ERROR CODE: 100\nCan not connect to the network.", Toast.LENGTH_LONG).show(); }
		}
	}
	// 업데이트 체크 스레드
	private class DownloadText extends Thread {
			public void run() {
		    	StringBuilder text = new StringBuilder();
		    	text.append("");
		    	try{
//		    		URL url = new URL("http://top6616.dothome.co.kr/help/update/version.txt");
		    		URL url = new URL(DiaryMemoAppManager.UpdateServer);
		    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		    		if(conn != null)
		    		{
		    			conn.setConnectTimeout(1000); // 1초 동안 인터넷 연결을 실패할경우 Fall 처리
		    			conn.setUseCaches(false);
		    			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
		    				BufferedReader br = new BufferedReader(
		    						new InputStreamReader(conn.getInputStream()));
		    				for(;;){
		    					String line = br.readLine();
		    					if(line == null) break;
		    					text.append(line + "\n");
		    				}
		    				br.close();
		    			}
		    			conn.disconnect();
		    		}
		    		
		    	}
		    	catch(Exception ex){}
		    	
		    	result = text.toString();
			}
	}
	
	    protected Dialog ShowDialog() {
		    		AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
		    		alert2.setTitle(R.string.update_title);
		    		alert2.setMessage(R.string.update_new);

		    		// Set an EditText view to get user input
		    		alert2.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int whichButton) {
		    				if(DiaryMemoAppManager.CHECK == DiaryMemoAppManager.ANDROID_MARKET){
		    				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ybproject.diarymemo"));
		    				startActivity(intent);
		    				}else if(DiaryMemoAppManager.CHECK == DiaryMemoAppManager.T_STORE){
		    					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tstore://PRODUCT_VIEW/0000276446/0"));
		    					startActivity(intent);
		    				}
		    			}
		    		});

		    		alert2.setNegativeButton(R.string.dialog_cancel,
		    				new DialogInterface.OnClickListener() {
		    					public void onClick(DialogInterface dialog, int whichButton) {
		    						return;
		    					}
		    				});
		    		alert2.show();  	
	    	return null;
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