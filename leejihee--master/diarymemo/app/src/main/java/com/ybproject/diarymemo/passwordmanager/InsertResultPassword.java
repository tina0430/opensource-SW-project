package com.ybproject.diarymemo.passwordmanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.ybproject.diarymemo.*;

public class InsertResultPassword extends Activity {
	EditText editText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.password_result);
//        TextView passwordResult = (TextView)findViewById(R.id.password_result);
//        passwordResult.setText(R.string.result_password_text+""+ getIntent().getStringExtra(Password.RESULT_PASSWORD) +""+R.string.result_password_text2);
//        
		//입력받은 비밀번호 삽입 후 처음으로 돌아감
		SharedPreferences prefs = getSharedPreferences("user_password" , MODE_PRIVATE);
		SharedPreferences.Editor ed = prefs.edit();
		ed.putString("user_password" , getIntent().getStringExtra(Password.RESULT_PASSWORD) );
		ed.commit();
		finish();
		
		/*
        findViewById(R.id.btn_InitHome).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//            	Intent intent = new Intent(InsertResultPassword.this, DiaryMemoList.class);
//                startActivity(intent);
            	//설정 완료시 종료
            	finish();
            }
		});
		*/
    }
}
