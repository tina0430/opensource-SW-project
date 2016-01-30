/**
 * 파일명: DiaryMemoEdit.java
 * 최종수정: 2012년 3월 25일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: Edit,View기능 처리
 */
package com.ybproject.diarymemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;

import com.ybproject.diarymemo.desk.DiaryMemoHelp;
import com.ybproject.diarymemo.provider.*;

public class DiaryMemoEdit extends Activity {
	private static final int REVERT_ID 		= Menu.FIRST + 0;
	private static final int DELETE_ID 		= Menu.FIRST + 1;
	private static final int SEND_ID 		= Menu.FIRST + 2;
	private static final int PREFS_ID 		= Menu.FIRST + 3;
	private static final int EDIT_ID		= Menu.FIRST + 4;
	private static final int STATE_EDIT 	= 0;
	private static final int STATE_INSERT 	= 1;
	private static final int STATE_VIEW		= 2;
	private static final String[] PROJECTION = new String[] { 
		DiaryMemo._ID, DiaryMemo.TITLE, DiaryMemo.BODY, DiaryMemo.CREATED, DiaryMemo.WIDGET_ID
	};

	private static final String ORIGINAL_NOTE = "originalNote";
	private int mState;
	private Uri mUri;
	private EditText mBodyText;
	private TextView mViewText;
	private TextView mCreateText;
	private DiaryMemo mOriginalNote;
	
	//광고
    private AdView adView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (savedInstanceState != null) {
			final Object note = savedInstanceState.get(ORIGINAL_NOTE);
			if (note != null) mOriginalNote = (DiaryMemo) note;
		}

		final Intent intent = getIntent();
		final String action = intent.getAction();
		
		if(Intent.ACTION_VIEW.equals(action)){
			// 보기 액션 처리 부
			mState = STATE_VIEW;
			mUri = intent.getData();
			setContentView(R.layout.view);
			mViewText = (TextView)findViewById(R.id.body);
			mCreateText = (TextView)findViewById(R.id.create_time);
			
		}else if(Intent.ACTION_EDIT.equals(action)){
			// 에디터 액션 처리 부
//			mState = STATE_EDIT;
			mState = STATE_VIEW;
			mUri = intent.getData();
//			setContentView(R.layout.edit);
			setContentView(R.layout.view);
//			mBodyText = (EditText)findViewById(R.id.body);
			mViewText = (TextView)findViewById(R.id.body);
			mCreateText = (TextView)findViewById(R.id.create_time);
			
			// 메모 신규작성 처리 액션
		}else if (Intent.ACTION_INSERT.equals(action)) {
			mState = STATE_INSERT;
			if (mOriginalNote == null) {
				mUri = getContentResolver().insert(intent.getData(), null);
			} else {
				mUri = mOriginalNote.getUri();
			}

			setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));
			setContentView(R.layout.insert);
			//광고 삽입
			String systemLanguage = getResources().getConfiguration().locale.getLanguage();
			String Ko_lang = "ko";
			//사용언어가 한국어 일경우에만 아담 광고 노출
			if(Ko_lang.equals(systemLanguage)){
				initAdam(); //아담 광고 초기화
			}else{
				//차후 로케이션이 영문일경우 애드몹 광고 추가예정
				Log.i("lang:",systemLanguage);
			}
			mBodyText = (EditText)findViewById(R.id.insert_body);
			Refresh();	// 현재 메모하는 시간을 출력하기 위해 시간을 얻어옴
		}

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
		
	}
	
	public void Edit_mOnClick(View v) {
		switch (v.getId()) {
		case R.id.edit_btn:
//			String sdk_version = Build.VERSION.SDK;
//			int min_version = 11;
//			int sdk_temp = Integer.valueOf(sdk_version);
			
			//URI값을 수정을 위해 ViewEdit로 전달
			Intent intent_m = new Intent(DiaryMemoEdit.this, DiaryMemoViewEdit.class);
			String temp = mUri.toString();			
			intent_m.putExtra("mUri",temp);
			startActivityForResult(intent_m, 0);
			//Android3.0 이상일경우 view모드로 돌아가지 않고, list로 돌아간다
//			if(sdk_temp >= min_version ){finish();}
			break;
		case R.id.del_btn:
			deleteNote(this, mUri);
			break;
		case R.id.insert_ok:
			finish();
			break;
		case R.id.can_btn:
			cancelNote();
			break;
		}
	}

	public void mOnClick(View v) {
		switch (v.getId()) {
		case R.id.create_btn:
			startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
			break;
		case R.id.search_btn:
			onSearchRequested();
			break;
		case R.id.click1:
    		AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
    		alert2.setTitle(R.string.create_type);
    		alert2.setMessage(R.string.create_type_selete);

    		// Set an EditText view to get user input
    		alert2.setPositiveButton(R.string.create_drawnote, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Intent intent = new Intent(DiaryMemoEdit.this, DrawNoteBoard.class);
    				startActivity(intent);
    			}
    		});

    		alert2.setNegativeButton(R.string.create_note,
    				new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton) {
    					    Intent intent2 = new Intent(DiaryMemoEdit.this,DiaryMemoList.class );
    					    startActivity(intent2);
    					}
    				});
    		alert2.show();
    		break;
		case R.id.click2:
			Intent intent = new Intent(DiaryMemoEdit.this, DrawNoteList.class);
			startActivity(intent);
			break;
		case R.id.click3:
    		AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
    		alert3.setTitle(R.string.help_connting);
    		alert3.setMessage(R.string.help_warring);

    		// Set an EditText view to get user input
    		alert3.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Intent HelpActivity = new Intent(DiaryMemoEdit.this, DiaryMemoHelp.class);
    				startActivity(HelpActivity);
    			}
    		});

    		alert3.setNegativeButton(R.string.dialog_cancel,
    				new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton) {
//    					   finish();
    					}
    				});
    		alert3.show();
			break;
		case R.id.click4:
			Intent prefsActivity = new Intent(this, Preferences.class);
			startActivity(prefsActivity);
			break;	
		}
	}
	
	// 현재 시간을 구함
	void Refresh() {
		StringBuilder time = new StringBuilder();

		Calendar cal = new GregorianCalendar();
		time.append(String.format("%d-%d-%d / %d:%d\n", 
//		time.append(String.format("%d"+R.string.yyyy+" %d"+R.string.mm+" %d"+R.string.dd+" %d"+R.string.hh+" %d"+R.string.min+"\n", 
				cal.get(Calendar.YEAR), 
				cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
				cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
		
		Calendar tom = new GregorianCalendar();
		tom.add(Calendar.DAY_OF_MONTH, 1);

		TextView result = (TextView)findViewById(R.id.result);
		result.setText(time.toString());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(ORIGINAL_NOTE, mOriginalNote);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		float textSize = Float.valueOf(preferences.getString("textSize", "16"));
		
		//TODO: VIEW / EDIT MODE에 따라 정의
		if(mState == STATE_VIEW){
			mViewText.setTextSize(textSize);
		}else{
			mBodyText.setTextSize(textSize);
		}
		
//		CursorLoader cursor = new CursorLoader(null, mUri, PROJECTION, null, null, null);

		@SuppressWarnings("deprecation")
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
					if(mState == STATE_VIEW){
						mViewText.setTextKeepState(note.getBody());
					}else{
						mBodyText.setTextKeepState(note.getBody());
					}
			}
		}else{
			if (note != null) {
				if (mOriginalNote == null) mOriginalNote = note;
					//TODO:VIEW,EDIT 에 따라 정의
					if(mState == STATE_VIEW){
						mViewText.setTextKeepState(note.getBody());
					}else{
						mBodyText.setTextKeepState(note.getBody());
					}
				
				//작성된 시간갑을 가져와 년/월/일/시/분/초 로 변환
				Long callDate = Long.parseLong(note.getCreated());
				Date date1 = new Date(callDate);
				SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy년 MM월 dd일  HH시 mm분 ss초에 작성한 메모입니다.", Locale.KOREA );
//				SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy"+R.string.yyyy+" MM"+R.string.mm+" dd"+R.string.dd+" HH"+R.string.hh+" mm"+R.string.mm+" ss"+R.string.ss+"", Locale.KOREA );
				String strCallDate = formatter.format(date1);
				//작성시간을 출력
				mCreateText.setTextKeepState(strCallDate);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mUri != null) {
			if(mState == STATE_VIEW){
				String bodyText = mViewText.getText().toString();
				
				int length = bodyText.length();
	
				if ((mState == STATE_INSERT) && isFinishing() && (length == 0)) {
					// 메모의 내용이 없다면 작성중인 메모를 삭제함
					setResult(RESULT_CANCELED);
					deleteNote();
				} else {
					ContentValues values = mOriginalNote.getContentValues();
						if (values.containsKey(DiaryMemo._ID)) values.remove(DiaryMemo._ID);
	
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
			}else{
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
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		
		if (mState == STATE_EDIT) {
			menu.add(0, REVERT_ID, 0, R.string.menu_revert)
				.setIcon(android.R.drawable.ic_menu_revert);
			menu.add(0, DELETE_ID, 0, R.string.menu_delete)
				.setIcon(android.R.drawable.ic_menu_delete);
		}else{
			menu.add(0, DELETE_ID, 0, R.string.menu_delete)
			.setIcon(android.R.drawable.ic_menu_delete);
		}
		menu.add(0, SEND_ID, 0, R.string.menu_send)
			.setIcon(android.R.drawable.ic_menu_send);

		menu.add(0, PREFS_ID, 0, R.string.menu_prefs)
			.setIcon(android.R.drawable.ic_menu_preferences);
		
		if(mState == STATE_VIEW){
		menu.add(0, EDIT_ID, 0, R.string.menu_edit)
		.setIcon(android.R.drawable.ic_menu_edit);
		}
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		String sdk_version = Build.VERSION.SDK;
//		int min_version = 11;
//		int sdk_temp = Integer.valueOf(sdk_version);
//		
//		Intent intent2 = new Intent();
		
		switch (item.getItemId()) {
			case DELETE_ID:
				deleteNote(this, mUri);
				return true;
			case REVERT_ID:
				mBodyText.setTextKeepState(mOriginalNote.getBody());
				return true;
			case SEND_ID:
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				if(mState == STATE_VIEW){
					intent.putExtra(Intent.EXTRA_TEXT, mViewText.getText().toString());
				}else{
					intent.putExtra(Intent.EXTRA_TEXT, mBodyText.getText().toString());
				}
				startActivity(Intent.createChooser(intent, getString(R.string.menu_send)));
				//TODO: Android3.x 이상 버전에서 오류발생해서 임시로 수정
//				if(sdk_temp >= min_version ){finish();} 
				return true;
			case PREFS_ID:
				Intent prefsActivity = new Intent(this, Preferences.class);
				startActivity(prefsActivity);
				return true;
			case EDIT_ID:
				//URI값을 수정을 위해 ViewEdit로 전달
				Intent intent_m = new Intent(DiaryMemoEdit.this, DiaryMemoViewEdit.class);
				String temp = mUri.toString();			
				intent_m.putExtra("mUri",temp);
				startActivityForResult(intent_m, 0);
				//TODO: Android3.0 이상일경우 view모드로 돌아가지 않고, list로 돌아간다
//				if(sdk_temp >= min_version ){finish();}
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
							finish();
						}
						
					})
				.setNegativeButton(R.string.dialog_cancel, null)
				.create();
			alertDialog.show();
		} else {
			getContentResolver().delete(noteUri, null, null);
			finish();
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
