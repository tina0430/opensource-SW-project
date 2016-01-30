package com.ybproject.diarymemo.widget;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;

import com.ybproject.diarymemo.DiaryMemoAppManager;
import com.ybproject.diarymemo.R;
import com.ybproject.diarymemo.provider.DiaryMemo;

import android.appwidget.*;
import android.content.*;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.app.ListActivity;

public class WidgetConfigActivty2x extends ListActivity {
	final static String PREF = "com.ybproject.diarymemo.widget.save";
	//DB내용 가져옴
	private static final String[] PROJECTION = new String[] { 
		DiaryMemo._ID, DiaryMemo.TITLE , DiaryMemo.BODY, DiaryMemo.WIDGET_ID
	};
	private static final String String = null;
	//광고
    private AdView adView = null;
	TextView mRed;
	int mId;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_config);
		//광고
		String systemLanguage = getResources().getConfiguration().locale.getLanguage();
		String Ko_lang = "ko";

		//사용언어가 한국어 일경우에만 아담 광고 노출
		if(Ko_lang.equals(systemLanguage)){
			initAdam(); //아담 광고 초기화
		}else{
			//차후 로케이션이 영문일경우 애드몹 광고 추가예정
			Log.i("lang:",systemLanguage);
		}
		
		Intent intent2 = getIntent();
		if (intent2.getData() == null) {
			intent2.setData(DiaryMemo.CONTENT_URI);
		}
		
		// 일단 실패로 가정한다.
        setResult(RESULT_CANCELED);
//        mRed = (TextView)findViewById(R.id.chkred);

        // ID 조사해 둔다.
        Intent intent = getIntent();
    	mId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 
    			AppWidgetManager.INVALID_APPWIDGET_ID);
//		Log.d(DiaryMemoWidgetProvider2x.TAG, "Config onCreate 위젯id(" + mId);

    	//TODO: 일단 위젯 옵션설정 비활성화
        // 설정값 읽어서 체크 박스에 출력.
//        SharedPreferences prefs = getSharedPreferences(PREF, 0);
//        boolean isRed = prefs.getBoolean("red_" + mId, false);
//        mRed.setChecked(isRed);

	}
	
	/**
	 * DB의 내용을 가져와 List에 뿌려준다
	 * 이 부분에서도 환경설정의 일부 옵션은 적용 받는다
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean largeListItems = preferences.getBoolean("listItemSize", true);

		int sortOrder = Integer.valueOf(preferences.getString("sortOrder", "1"));
		boolean sortAscending = preferences.getBoolean("sortAscending", true);
		String sorting = DiaryMemo.SORT_ORDERS[sortOrder] + ((sortAscending ? " ASC" : " DESC"));

		Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, sorting);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
			(largeListItems) ? R.layout.row_large : R.layout.row_small,
			cursor, 
			new String[] { DiaryMemo.TITLE }, new int[] { android.R.id.text1 }
		);
		setListAdapter(adapter);
		
	}
	/**
	 * List의 아이템을 클릭시 아이템의 컨텐츠 ID를 얻는다
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		
		String string_test = "Not Fond!";

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//TEST ( SQLite )
		// TODO: Uri 의 id만 추출
//		long test7 = ContentUris.parseId(uri);
//		
//		//TODO: ID를 기반으로 하여 URI를 찾음
//		Uri test8 = ContentUris.withAppendedId(getIntent().getData(), test7);
//		
//		Log.d("TAG", "[" + new Throwable().getStackTrace()[0].getFileName() + "][" + new Throwable().getStackTrace()[0].getMethodName() + "]["
//        + new Throwable().getStackTrace()[0].getLineNumber() + "] : "+test7+" Uri의 ID만 뽑음");
//
//		Log.d("TAG", "[" + new Throwable().getStackTrace()[0].getFileName() + "][" + new Throwable().getStackTrace()[0].getMethodName() + "]["
//		        + new Throwable().getStackTrace()[0].getLineNumber() + "] : "+test8+" Uri의 ID로 Uri 재조립");
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * @용도: Widget_Id 컬럼에 생성된 widget의 ID를 넣음
		 * @설명: WidgetID 생성과 선택한 Uri의 메모값을 String 형태로 반환하여 DiaryMemoWidgetProvider2x 로 넘김
		 */
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		while(cursor.moveToNext()) {
		         int index=cursor.getColumnIndex(DiaryMemo.BODY);
		         string_test = cursor.getString(index);
		}

		ContentValues values = new ContentValues();
		
		values.put(DiaryMemo.WIDGET_ID, mId);
		getContentResolver().update(uri, values, null, null);
		
//		String action = getIntent().getAction();
//		if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
//			setResult(RESULT_OK, new Intent().setData(uri));
//		} else {		
		
			//TODO: 일단 위젯 옵션설정 비활성화
//	        SharedPreferences prefs = getSharedPreferences(PREF, 0);
//	        SharedPreferences.Editor editor = prefs.edit();
//	        editor.putBoolean("red_" + mId, mRed.isChecked());
//	        editor.commit();

	        // 상태 갱신
	        Context con = WidgetConfigActivty2x.this;
	        DiaryMemoWidgetProvider2x.UpdateNote(con, AppWidgetManager.getInstance(con), mId, string_test);

	        // OK 리턴 보냄
            Intent intent = new Intent();
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mId);
            setResult(RESULT_OK, intent);
            finish();
			
		}
//	}
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
