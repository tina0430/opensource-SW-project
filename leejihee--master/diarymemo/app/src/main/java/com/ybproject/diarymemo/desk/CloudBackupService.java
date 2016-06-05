package com.ybproject.diarymemo.desk;

import com.ybproject.diarymemo.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


/**
 * 
 * @author 이용범
 * @용도: 클라우드 백업 서비스
 *       메모 단일,개별 백업하는 서비스
 *       1.이메일 백업지원
 *
 */
public class CloudBackupService extends Activity  {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(android.content.Intent.EXTRA_EMAIL, false);
//		Intent.putExtra(Intent.EXTRA_STREAM, uri); //파일경로 지정
		startActivity(Intent.createChooser(intent, "전송"));
		finish();

	}

}
