/**
 * 파일명: DiaryMemoEdit.java
 * 최종수정: 2012년 3월 19일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: Edit기능 처리
 */
package com.ybproject.diarymemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.ybproject.diarymemo.provider.DiaryMemo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.text.ClipboardManager;

public class DiaryMemoViewEdit extends Activity {
	private static final int REVERT_ID 		= Menu.FIRST + 0;
	private static final int DELETE_ID 		= Menu.FIRST + 1;
	private static final int SEND_ID 		= Menu.FIRST + 2;
	private static final int PREFS_ID 		= Menu.FIRST + 3;
	private static final int STATE_EDIT 	= 0;
	private static final int STATE_INSERT 	= 1;

	private static final String[] PROJECTION = new String[] { 
		DiaryMemo._ID, DiaryMemo.TITLE, DiaryMemo.BODY, DiaryMemo.CREATED, DiaryMemo.WIDGET_ID
	};

	private static final String ORIGINAL_NOTE = "originalNote";
	private int mState;
	private Uri mUri;
	private Uri mUri2;
	private EditText mBodyText;
	private TextView mCreateText;
	private DiaryMemo mOriginalNote;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			Toast.makeText(DiaryMemoViewEdit.this, R.string.mode_chang, Toast.LENGTH_SHORT).show();
			//수정을 위해 URI를 받아옴
			Intent intent = getIntent();
			String temp = intent.getStringExtra("mUri");
	
			if (savedInstanceState != null) {
				final Object note = savedInstanceState.get(ORIGINAL_NOTE);
				if (note != null) mOriginalNote = (DiaryMemo) note;
			}
			
			// 에디터 액션 처리 부
			mState = STATE_EDIT;
			mUri = mUri2.parse(temp);
	
			setContentView(R.layout.edit);
			mBodyText = (EditText)findViewById(R.id.body);
			mCreateText = (TextView)findViewById(R.id.create_time);
				
			if (mUri == null) {
				finish();
				return;
			}
	
			Button confirmButton = (Button) findViewById(R.id.confirm);
			confirmButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					finish();
				}
			});
			Button cancelButton = (Button) findViewById(R.id.cancel);
			cancelButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					cancelNote();
				}
			});
		}catch(Exception e){Log.d("Error","알수없는 에러,CODE: 0001");}
		
		//TODO: TestView 에서도 메모내용이 클립보드로 복사가능하도록
//		final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//		mBodyText.setOnLongClickListener(new View.OnLongClickListener () 
//			{
//		            public boolean onLongClick(View v) {
//		            	clipboardManager.setText(mBodyText.getText()); 
//		                return false;
//		            }
//			}
//		);
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		try{
			super.onSaveInstanceState(outState);
			outState.putParcelable(ORIGINAL_NOTE, mOriginalNote);
		}catch(Exception e){Log.d("Error","알수없는 에러,CODE: 0002");}
	}

	@Override
	protected void onResume() {
		try{
			super.onResume();
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			float textSize = Float.valueOf(preferences.getString("textSize", "16"));
			
			mBodyText.setTextSize(textSize);
			Cursor cursor = managedQuery(mUri, PROJECTION, null, null, null);
			DiaryMemo note = DiaryMemo.fromCursor(cursor);
			// startManagingCursor -> CursorLoader 로 바꿔야함, 일단 임시로 사용
			startManagingCursor(cursor);
	//		cursor.close();
	
			/**
			 * @mBodyText.setTextKeepState(note.getBody()); - 메모의 내용을 불러와 뿌려줌
			 * @mCreateText.setTextKeepState(note.getCreated()); - 메모를 작성한 시간을 가져와 뿌려줌
			 * TODO: INSERT시 시간을 불러올 수 업어 오류가 발생함, 그로 인하여 mState가 INSERT일경우 mCreateText를 실행하지 않음
			 */
			if(mState == STATE_INSERT){
				if(note != null){
					if (mOriginalNote == null) mOriginalNote = note;
							mBodyText.setTextKeepState(note.getBody());
				}
			}else{
				if (note != null) {
					if (mOriginalNote == null) mOriginalNote = note;
						mBodyText.setTextKeepState(note.getBody());
					
					//작성된 시간갑을 가져와 년/월/일/시/분/초 로 변환
					Long callDate = Long.parseLong(note.getCreated());
					Date date1 = new Date(callDate);
					SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd / HH:mm:ss", Locale.KOREA );
//					SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy"+R.string.yyyy+" MM"+R.string.mm+" dd"+R.string.dd+" HH"+R.string.hh+" mm"+R.string.min+" ss"+R.string.ss+"", Locale.KOREA );
					String strCallDate = formatter.format(date1);
					//작성시간을 출력
					mCreateText.setTextKeepState(strCallDate);
				}
			}
		}catch(Exception e){Log.d("Error","알수없는 에러,CODE: 0003"); finish();}
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		try{
		
			if (mUri != null) {
					String bodyText = mBodyText.getText().toString();
					
					int length = bodyText.length();
		
					if ((mState == STATE_INSERT) && isFinishing() && (length == 0)) {
						// 메모의 내용이 없다면 작성중인 메모를 삭제함 TODO: 가끔 삭제안됨
						setResult(RESULT_CANCELED);
						deleteNote();
					} else {
						ContentValues values = mOriginalNote.getContentValues();
							if (values.containsKey(DiaryMemo._ID)) values.remove(DiaryMemo._ID);
						// 제목생성 처리( 첫라인을 제목으로 구성한다 )
						if (mState == STATE_INSERT) {
							String[] lines = bodyText.split("[\n\\.]");
							String title = 
								(lines.length > 0) ? lines[0] : getString(android.R.string.untitled);
							if (title.length() > 30) {
								int lastSpace = title.lastIndexOf(' ');
								if (lastSpace > 0) {
									title = title.substring(0, lastSpace);
								}
							}
							values.put(DiaryMemo.TITLE, title);
						}
						values.put(DiaryMemo.BODY, bodyText);
		
						getContentResolver().update(mUri, values, null, null);
					}
				}
		 	}catch(Exception e){Log.d("Error","알수없는 에러,CODE: 0004");}
		}
	
	public void View_mOnClick(View v) {
		try{
			switch (v.getId()) {
			case R.id.ok_btn:
//				ContentValues values = mOriginalNote.getContentValues();
//				String bodyText = mBodyText.getText().toString();

				//TODO: 타이틀도 함께 업데이트 되도록 구현예정
	//			String[] lines = bodyText.split("[\n\\.]");
	//			String title = 
	//				(lines.length > 0) ? lines[0] : getString(android.R.string.untitled);
	//			if (title.length() > 30) {
	//				int lastSpace = title.lastIndexOf(' ');
	//				if (lastSpace > 0) {
	//					title = title.substring(0, lastSpace);
	//				}
	//			}
	//			Log.d("타이틀:", title);		
//				values.put(DiaryMemo.BODY, bodyText);
//				getContentResolver().update(mUri, values, null, null);
				finish();
				break;
			case R.id.v_del:
				cancelNote();
//				deleteNote(this, mUri);
				break;
			}
		}catch(Exception e){Log.d("Error","알수없는 에러,CODE: 0005");}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		if (mState == STATE_EDIT) {
			menu.add(0, REVERT_ID, 0, R.string.menu_revert)
				.setIcon(android.R.drawable.ic_menu_revert);
		}else{
			menu.add(0, DELETE_ID, 0, R.string.menu_delete)
			.setIcon(android.R.drawable.ic_menu_delete);
		}
		menu.add(0, SEND_ID, 0, R.string.menu_send)
			.setIcon(android.R.drawable.ic_menu_send);

		menu.add(0, PREFS_ID, 0, R.string.menu_prefs)
			.setIcon(android.R.drawable.ic_menu_preferences);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
			
		switch (item.getItemId()) {
			case REVERT_ID:
				mBodyText.setTextKeepState(mOriginalNote.getBody());
				return true;
			case SEND_ID:
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, mBodyText.getText().toString());
				startActivity(Intent.createChooser(intent, getString(R.string.menu_send)));
				return true;
			case PREFS_ID:
				Intent prefsActivity = new Intent(this, Preferences.class);
				startActivity(prefsActivity);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** 현재 편집중인 메모를 취소 하는 액티비티  */
	private final void cancelNote() {
		if (mUri != null) {
			if (mState == STATE_EDIT) {
				ContentValues values = mOriginalNote.getContentValues();
				getContentResolver().update(mUri, values, null, null);
				mUri = null;
			} else if (mState == STATE_INSERT) {
				// 빈노트를 생성할때 클리어
				deleteNote();
			}
		}
		setResult(RESULT_CANCELED);
		finish();
	}

	/** 현재 삭제하는 메모 */
	private final void deleteNote() {
		if (mUri != null) {
			getContentResolver().delete(mUri, null, null);
			mUri = null;
		}
	}
	/** 메모삭제시 확인 관련 부분
	 * @param context Context 로 사용
	 * @param id ID 기준으로 사용
	 */
	private void deleteNote(Context context, Uri uri) {
		final Uri noteUri = uri;
		try{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			Boolean deleteConfirmation = preferences.getBoolean("deleteConfirmation", true);
			if (deleteConfirmation) {
				AlertDialog alertDialog = new AlertDialog.Builder(context)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.dialog_delete)
					.setMessage(R.string.delete_confirmation)
					.setPositiveButton(R.string.dialog_confirm,
						new DialogInterface.OnClickListener() {
							// OnClickListener
							public void onClick(DialogInterface dialog, int which) {
								getContentResolver().delete(noteUri, null, null);
								startActivity(new Intent(DiaryMemoViewEdit.this,DiaryMemoList.class));
								finish();
							}
						})
					.setNegativeButton(R.string.dialog_cancel, null)
					.create();
				alertDialog.show();
			} else {
				getContentResolver().delete(noteUri, null, null);
				startActivity(new Intent(DiaryMemoViewEdit.this,DiaryMemoList.class));
				finish();
			}
		}catch(Exception e){Log.d("Error","알수없는 에러,CODE: 0006");}
	}
}
