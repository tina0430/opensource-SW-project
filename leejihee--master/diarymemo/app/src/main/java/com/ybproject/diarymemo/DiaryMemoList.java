/**
 * 파일명: DiaryMemoList.java
 * 최종수정: 2012년 2월 13일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: 메인 액티비티, 전체적인 구성( list 처리 )
 */
package com.ybproject.diarymemo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import com.ybproject.diarymemo.desk.DiaryMemoHelp;
import com.ybproject.diarymemo.provider.*;

/** Main activity for DiaryMemo, shows a list of notes. */
public class DiaryMemoList extends ListActivity {
//	public static Activity BActivity;
	public static final int INSERT_ID 	= Menu.FIRST + 0;
	public static final int SEARCH_ID 	= Menu.FIRST + 1;
	public static final int PREFS_ID 	= Menu.FIRST + 2;
	public static final int DRAW_ID		= Menu.FIRST + 3;
	public static final int HELP_ID		= Menu.FIRST + 4;
	public static final int DELETE_ID 	= Menu.FIRST + 3;
	public static final int SEND_ID 	= Menu.FIRST + 4;
	
	// 어플종료 관련 핸들러 플래그 선언
	private Handler mHandler;
	private boolean mFlag = false;
	private static final String[] PROJECTION = new String[] { 
		DiaryMemo._ID, DiaryMemo.TITLE
	};

	private static final int COLUMN_INDEX_ID 		= 0;
	private static final int COLUMN_INDEX_TITLE 	= 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean Thema = preferences.getBoolean("Old_Thema", true);
		if(Thema == true){
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.list_note);
		}else{
			setContentView(R.layout.list_old);
		}

		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(DiaryMemo.CONTENT_URI);
		}
		SharedPreferences grid_pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean Thema2 = grid_pref.getBoolean("grid_Thema_main", true);
		if(Thema2 == false){
		// BACK키 핸들러 ( 휴대폰 종료 관련 로직 )
		mHandler = new Handler() {
		    @Override
		    public void handleMessage(Message msg) {
		        if(msg.what == 0) {
		            mFlag = false;
		        }
		    }
		 };
		}else{}
      
      registerForContextMenu(getListView());
	}

	/*
	  백키 이벤트를 가로채서 플래그값 확인 후 처리.
	  플래그 값이 true인 상태에서 2초 이내에 백키를 누르면 액티비티 종료.
	*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {	
		SharedPreferences grid_pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean Thema2 = grid_pref.getBoolean("grid_Thema_main", true);
		if(Thema2 == false){
		
		//Back Key 처리
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        if(!mFlag) {
	            Toast.makeText(DiaryMemoList.this, R.string.exit_back, Toast.LENGTH_SHORT).show();
	            mFlag = true;
	            mHandler.sendEmptyMessageDelayed(0, 2000);
	            return false;
	        } else {
	            //어플종료시 실행값 초기화
	            SharedPreferences pass_check = getSharedPreferences("user_password_check" , MODE_PRIVATE);
	            SharedPreferences.Editor ed = pass_check.edit();
	      	  	ed.putInt("user_password_check" , 0 );
	      	  	ed.commit();
	            finish();
	        }
	    }
	}
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * - 스플래시 화면이 표시되는 동안에 어떤 일(예를 들면 초기화  작업)을 시키려면 작업을 메인쓰레드가 아닌 다른 쓰레드에서 처리하도록 해야됨
	 * - 메인 쓰레드는 스플래시 화면을 표시하는 일을 해야하니까.. 동시에 두개 작업은 할 수 없음
	 */
	@Override
	public void onResume() {
		super.onResume();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean largeListItems = preferences.getBoolean("listItemSize", true);

		int sortOrder = Integer.valueOf(preferences.getString("sortOrder", "1"));
		boolean sortAscending = preferences.getBoolean("sortAscending", true);
		String sorting = DiaryMemo.SORT_ORDERS[sortOrder] + ((sortAscending ? " ASC" : " DESC"));

		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, sorting);
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
			(largeListItems) ? R.layout.row_large : R.layout.row_small,
			cursor, 
			new String[] { DiaryMemo.TITLE }, new int[] { android.R.id.text1 }
		);
		setListAdapter(adapter);
		
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

    		// 신규 메모
    		alert2.setPositiveButton(R.string.create_drawnote, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Intent intent = new Intent(DiaryMemoList.this, DrawNoteBoard.class);
    				startActivity(intent);
    			}
    		});

    		alert2.setNegativeButton(R.string.create_note,
    				new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton) {
    					    startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
    					}
    				});
    		alert2.show();
    		break;
		case R.id.click2:
			Intent intent = new Intent(DiaryMemoList.this, DrawNoteList.class);
			startActivity(intent);
			break;
		case R.id.click3:
			AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
    		alert3.setTitle(R.string.help_connting);
    		alert3.setMessage(R.string.help_warring);

    		// Set an EditText view to get user input
    		alert3.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Intent HelpActivity = new Intent(DiaryMemoList.this, DiaryMemoHelp.class);
    				startActivity(HelpActivity);
    			}
    		});

    		alert3.setNegativeButton(R.string.dialog_cancel,
    				new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// 메뉴 옵션 구성
		menu.add(0, INSERT_ID, 0, R.string.menu_insert)
			.setIcon(android.R.drawable.ic_menu_add);
		
		menu.add(0, DRAW_ID, 0, R.string.create_drawnote)
		.setIcon(android.R.drawable.ic_input_add);

		menu.add(0, SEARCH_ID, 0, R.string.menu_search)
			.setIcon(android.R.drawable.ic_menu_search);

		menu.add(0, PREFS_ID, 0, R.string.menu_prefs)
			.setIcon(android.R.drawable.ic_menu_preferences);
		
		menu.add(0, HELP_ID, 0, R.string.menu_help)
		.setIcon(android.R.drawable.ic_menu_help);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case INSERT_ID:
				Toast.makeText(DiaryMemoList.this, R.string.new_memo_info, Toast.LENGTH_LONG).show();
				startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
				return true;
			case SEARCH_ID:
				onSearchRequested();
				return true;
			case PREFS_ID:
				Intent prefsActivity = new Intent(this, Preferences.class);
				startActivity(prefsActivity);
				return true;
			case DRAW_ID:
				Intent intent = new Intent(DiaryMemoList.this, DrawNoteList.class);
				startActivity(intent);
				return true;
			case HELP_ID:
				Toast.makeText(DiaryMemoList.this, R.string.net_warning, Toast.LENGTH_LONG).show();
				Intent HelpActivity = new Intent(this, DiaryMemoHelp.class);
				startActivity(HelpActivity);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			return;
		}

		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		if (cursor == null) {
			return;
		}

		menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

		Uri uri = ContentUris.withAppendedId(getIntent().getData(), cursor.getInt(COLUMN_INDEX_ID));

		Intent[] specifics = new Intent[1];
		
		// 롱터치 -> 수정 터치시 액션 처리 루틴
		specifics[0] = new Intent(Intent.ACTION_EDIT, uri);
		MenuItem[] items = new MenuItem[1];

		Intent intent = new Intent(null, uri);
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, null, specifics, intent, 0, items);

		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, SEND_ID, 0, R.string.menu_send);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
//		String sdk_version = Build.VERSION.SDK;
//		int min_version = 11;
//		int sdk_temp = Integer.valueOf(sdk_version);
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			return false;
		}
		switch (item.getItemId()) {
			case DELETE_ID:
				deleteNote(this, info.id);
				return true;
			case SEND_ID:
				Uri uri = ContentUris.withAppendedId(DiaryMemo.CONTENT_URI, info.id);
				Cursor cursor = managedQuery(
					uri, new String[] { DiaryMemo._ID, DiaryMemo.TITLE, DiaryMemo.BODY, DiaryMemo.WIDGET_ID, DiaryMemo.CREATED }, null, null, null
				);
				DiaryMemo note = DiaryMemo.fromCursor(cursor);
				// startManagingCursor -> CursorLoader 로 바꿔야함, 일단 임시로 사용
				startManagingCursor(cursor);
//				cursor.close();

				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, note.getBody());
				startActivity(Intent.createChooser(intent, getString(R.string.menu_send)));
				return true;
		}
		return false;
	}

	// ListItem터치 이벤트 처리 루틴
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		
		String action = getIntent().getAction();
		if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
			setResult(RESULT_OK, new Intent().setData(uri));
		} else {
			
			//모드 선택 옵션
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			boolean ListItemsView = preferences.getBoolean("listItemViewSelect", false);
			
			if(ListItemsView == false){
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}else{
				startActivity(new Intent(Intent.ACTION_EDIT, uri));	
			}
		}
	}
	
	private void deleteNote(Context context, long id) {
		final long noteId = id;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean deleteConfirmation = preferences.getBoolean("deleteConfirmation", true);
		if (deleteConfirmation) {
			AlertDialog alertDialog = new AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.dialog_delete)
				.setMessage(R.string.delete_confirmation)
				.setPositiveButton(R.string.dialog_confirm,
					new DialogInterface.OnClickListener() {
						// OnClickListener
						public void onClick(DialogInterface dialog, int which) {
							Uri noteUri = ContentUris.withAppendedId(DiaryMemo.CONTENT_URI, noteId);
							getContentResolver().delete(noteUri, null, null);
						}
					}
				)
				.setNegativeButton(R.string.dialog_cancel, null)
				.create();
			alertDialog.show();
		} else {
			Uri noteUri = ContentUris.withAppendedId(DiaryMemo.CONTENT_URI, noteId);
			getContentResolver().delete(noteUri, null, null);
		}
	}
}