package com.ybproject.diarymemo.passwordmanager;

import com.ybproject.diarymemo.DiaryMemoAppManager;
import com.ybproject.diarymemo.DiaryMemoList;
import com.ybproject.diarymemo.R;
import com.ybproject.diarymemo.provider.DiaryMemo;

import android.app.Activity; 
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class EnablePassword extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.list_old);
        
        SharedPreferences.Editor edit;
		SharedPreferences prefs = getSharedPreferences("user_password" , 0); //값 가져옴
		SharedPreferences prefs2 = getSharedPreferences("user_password_check" , MODE_PRIVATE);
		edit = prefs.edit(); //값 저장
		String pass = prefs.getString("user_password", "0000");
        
        Intent intent = new Intent(EnablePassword.this, Password.class);
    	intent.putExtra(Password.NEXT_ACTIVITY, "com.ybproject.diarymemo.DiaryMemoAppManager");
        intent.putExtra(Password.PASSWORD, pass);
        intent.putExtra(Password.MODE, Password.MODE_CHECK_PASSWORD);
       	SharedPreferences.Editor ed = prefs2.edit();
   		ed.putInt("user_password_check" , 1 );
   		ed.commit();
        startActivity(intent);
        finish();
    }
}