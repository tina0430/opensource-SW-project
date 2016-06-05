/**
 * 파일명: DiaryMemoBugreport.java
 * 최종수정: 2012년 2월 25일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: 버그리포트 페이지 이동
 * TODO: desk 를 한개로 묶어 버릴 수 없나...
 */
package com.ybproject.diarymemo.desk;

import com.ybproject.diarymemo.R;

import android.app.*;
import android.content.Intent;
import android.os.*;

public class DiaryMemoBugreport extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"yongyongdev@gmail.com"});
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "다이어리 메모장 문의합니다");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, "내용을 입력해주세요\n요청분류(버그&개선):\n문의내용:");
		startActivity(Intent.createChooser(intent, "전송"));
		finish();

	}
}