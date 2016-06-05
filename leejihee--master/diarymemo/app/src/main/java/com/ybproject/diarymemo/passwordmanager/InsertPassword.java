package com.ybproject.diarymemo.passwordmanager;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;
import net.daum.adam.publisher.AdView.OnAdClickedListener;
import net.daum.adam.publisher.AdView.OnAdClosedListener;
import net.daum.adam.publisher.AdView.OnAdFailedListener;
import net.daum.adam.publisher.AdView.OnAdLoadedListener;
import net.daum.adam.publisher.AdView.OnAdWillLoadListener;
import net.daum.adam.publisher.impl.AdError;

import com.ybproject.diarymemo.DiaryMemoAppManager;
import com.ybproject.diarymemo.DiaryMemoList;
import com.ybproject.diarymemo.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class InsertPassword extends Activity {
	//광고
    private AdView adView = null;
	EditText editText;
	String password_number;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_manager);
        //아담 광고
		String systemLanguage = getResources().getConfiguration().locale.getLanguage();
		String Ko_lang = "ko";

		//사용언어가 한국어 일경우에만 아담 광고 노출
		if(Ko_lang.equals(systemLanguage)){
			initAdam(); //아담 광고 초기화
		}else{
			//차후 로케이션이 영문일경우 애드몹 광고 추가예정
			Log.i("lang:",systemLanguage);
		}
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean Password_check = preferences.getBoolean("enable_password", true);
		if(Password_check == true){
			Log.d("PASSOWRD:","Password init On");
		}else{
			//암호설정 체크 해제시 비밀번호 초기화
			SharedPreferences prefs = getSharedPreferences("user_password" , MODE_PRIVATE);
			SharedPreferences.Editor ed = prefs.edit();
			ed.putString("user_password" , "0000" );
			ed.commit();
			finish();
			Log.d("PASSOWRD:","Password init Off");
		}

        findViewById(R.id.EnablePassword).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(InsertPassword.this, Password.class);
            	intent.putExtra(Password.NEXT_ACTIVITY, "com.ybproject.diarymemo.passwordmanager.InsertResultPassword");
                intent.putExtra(Password.PASSWORD, "0000"); //기본 비밀번호 0000
                intent.putExtra(Password.MODE, Password.MODE_INIT_PASSWORD);
                startActivity(intent);
                finish();
            }
		});
        findViewById(R.id.DisablePassword).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
	            finish();
            }
		});      
    }   
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//Back Key 처리
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	            //백키 누를시 설정 취소
	            SharedPreferences pass_check = getSharedPreferences("enable_password" , MODE_PRIVATE);
	            SharedPreferences.Editor ed = pass_check.edit();
	      	  	ed.putBoolean("enable_password", false);
	      	  	ed.commit();
	            finish();
	        }
	    return super.onKeyDown(keyCode, event);
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