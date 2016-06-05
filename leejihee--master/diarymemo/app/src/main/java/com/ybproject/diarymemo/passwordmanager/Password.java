package com.ybproject.diarymemo.passwordmanager;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;

import com.ybproject.diarymemo.DiaryMemoAppManager;
import com.ybproject.diarymemo.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class Password extends Activity {
	public static final String NEXT_ACTIVITY = "nextActivity";
	public static final String PASSWORD = "password";
	public static final String RESULT_PASSWORD = "resultPassword";
	public static final String MODE = "mode";
	
	public static final int MODE_CHANGE_PASSWORD = 0;
	public static final int MODE_INIT_PASSWORD = 1;
	public static final int MODE_CHECK_PASSWORD = 2;
	
	public static final int PHASE_INIT_PASSWORD = 0;
	public static final int PHASE_INPUT_PASSWORD = 1;
	public static final int PHASE_CONFIRM_PASSWORD = 2;
	
	private int currentMode = PHASE_CONFIRM_PASSWORD;
	private int initMode = PHASE_CONFIRM_PASSWORD;
	private String currentPassword;
	private int passwordLength = 9999;
	private Intent nextActivity;
	private EditText passwordForm, passwordConfirmForm;
	private String passwordString;
	private ViewFlipper passwordFlipper;
	private TranslateAnimation pushLeftIn, pushLeftOut, shakeAni;
	//광고
    private AdView adView = null;
	// 어플종료 관련 핸들러 플래그 선언
	private Handler mHandler;
	private boolean mFlag = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
        
		String systemLanguage = getResources().getConfiguration().locale.getLanguage();
		String Ko_lang = "ko";

		//사용언어가 한국어 일경우에만 아담 광고 노출
		if(Ko_lang.equals(systemLanguage)){
			initAdam(); //아담 광고 초기화
		}else{
			//차후 로케이션이 영문일경우 애드몹 광고 추가예정
			Log.i("lang:",systemLanguage);
		}
        init();
        initAnimation();
        
        // BACK키 핸들러 ( 휴대폰 종료 관련 로직 )
 		mHandler = new Handler() {
 		    @Override
 		    public void handleMessage(Message msg) {
 		        if(msg.what == 0) {
 		            mFlag = false;
 		        }
 		    }
 		};
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	            Toast.makeText(Password.this, R.string.lock_fail, Toast.LENGTH_SHORT).show();
	            //어플종료시 실행값 초기화
	            SharedPreferences pass_check = getSharedPreferences("user_password_check" , MODE_PRIVATE);
	            SharedPreferences.Editor ed = pass_check.edit();
	      	  	ed.putInt("user_password_check" , 0 );
	      	  	ed.commit();
	            finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
    
    private Runnable passwordRunnable = new Runnable(){
        public void run() {
        	checkPassword();
        }
    };
     
    private void checkPassword(){  	
    	switch(currentMode){
    	case PHASE_INIT_PASSWORD:
    		if( passwordString.equals(passwordForm.getText().toString()) ){
    			goToNextPhase();
    		}else{
                passwordForm.startAnimation(shakeAni);
    		}
    		break;
    		
    	case PHASE_INPUT_PASSWORD:
    		goToNextPhase();
    		break;
    		
    	case PHASE_CONFIRM_PASSWORD:
    		EditText currentForm = passwordConfirmForm; 
    		if(initMode == PHASE_CONFIRM_PASSWORD) currentForm = passwordForm;
    		
    		if( currentPassword.equals(currentForm.getText().toString()) )
    			goToNextPhase();
    		else{
                passwordForm.startAnimation(shakeAni);
    		}
    	}
    	
    	passwordForm.setText("");
    	passwordConfirmForm.setText("");
    }
    
    private void goToNextPhase(){
    	switch(currentMode){
    	case PHASE_INIT_PASSWORD:
    		currentMode = PHASE_INPUT_PASSWORD;
    		break;
    		
    	case PHASE_INPUT_PASSWORD:
    		currentPassword = passwordForm.getText().toString();
    		currentMode = PHASE_CONFIRM_PASSWORD;
    	             
    		passwordFlipper.setInAnimation(pushLeftIn);
    		passwordFlipper.setOutAnimation(pushLeftOut);
    		passwordFlipper.showPrevious();
    		break;
    		
    	case PHASE_CONFIRM_PASSWORD:
    		finish();
    		nextActivity.putExtra(RESULT_PASSWORD, currentPassword);
        	startActivity(nextActivity);    		
        	break;
    	}
    }
    
    private void init(){
    	Intent intent = getIntent();
        String nextActivityClassString = intent.getStringExtra(NEXT_ACTIVITY);
        nextActivity = new Intent();
        nextActivity.setClassName(Password.this, nextActivityClassString);
        
        initMode = intent.getIntExtra(MODE, MODE_CHECK_PASSWORD);
        passwordString = intent.getStringExtra(PASSWORD);
        
        currentMode = initMode;
        currentPassword = passwordString;
        passwordLength = passwordString.length();
        
        passwordFlipper = (ViewFlipper)findViewById(R.id.password_flipper);
        passwordForm = (EditText)findViewById(R.id.password);
        passwordForm.addTextChangedListener(new TextWatcher() {
        	public void  afterTextChanged (Editable s){
        	}
            public void  beforeTextChanged  (CharSequence s, int start, int count, int after){
            }
            public void  onTextChanged  (CharSequence s, int start, int before, int count) {
            	if(passwordForm.getText().toString().length() == passwordLength){
            		Handler passwordHandler = new Handler();
            		passwordHandler.postDelayed(passwordRunnable, 200);
        		}
            } 
        });
        
        passwordConfirmForm = (EditText)findViewById(R.id.password_confirm);       
        passwordConfirmForm.addTextChangedListener(new TextWatcher() {
        	public void  afterTextChanged (Editable s){
        	}
            public void  beforeTextChanged  (CharSequence s, int start, int count, int after){
            }
            public void  onTextChanged  (CharSequence s, int start, int before, int count) {
            	if(passwordConfirmForm.getText().toString().length() == passwordLength){
            		Handler passwordHandler = new Handler();
            		passwordHandler.postDelayed(passwordRunnable, 200);
        		}
            } 
        });
    }
    
    private void initAnimation(){
    	pushLeftIn = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 1.0f,   
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
    	pushLeftIn.setDuration(200);
    	pushLeftIn.setFillAfter(true);
    	
    	pushLeftOut = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,   
                TranslateAnimation.RELATIVE_TO_SELF, -1.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
    	pushLeftOut.setDuration(200);
    	pushLeftOut.setFillAfter(true);
    	
    	shakeAni = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,   
                TranslateAnimation.RELATIVE_TO_SELF, 0.05f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
    	shakeAni.setDuration(300);
    	shakeAni.setInterpolator(new CycleInterpolator(2.0f));
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