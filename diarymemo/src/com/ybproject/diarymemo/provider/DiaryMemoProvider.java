/**
 * 파일명: DiaryMemoProvider.java
 * 최종수정: 2012년 2월 26일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: SQLite DB생성 처리 및 컬럼 선언
 */
package com.ybproject.diarymemo.provider;

import java.util.HashMap;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/** <code>ContentProvider</code> implementation for <code>bander.provider.Note</code> objects. */
public class DiaryMemoProvider extends ContentProvider {
	private static final String DATABASE_NAME = "diarymemo.db";
	private static final String DATABASE_TABLE = "notes";
	private static final int DATABASE_VERSION = 6;

	private static final int SEARCH 	= 1;
	private static final int NOTES 		= 2;
	private static final int NOTE_ID 	= 3;

	private static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(DiaryMemo.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH);
		sUriMatcher.addURI(DiaryMemo.AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH);
		sUriMatcher.addURI(DiaryMemo.AUTHORITY, "notes", NOTES);
		sUriMatcher.addURI(DiaryMemo.AUTHORITY, "notes/#", NOTE_ID);
	}
	
	private static HashMap<String, String> sNotesProjectionMap;
	static {
		sNotesProjectionMap = new HashMap<String, String>();
		sNotesProjectionMap.put(DiaryMemo._ID, DiaryMemo._ID);
		sNotesProjectionMap.put(DiaryMemo.TITLE, DiaryMemo.TITLE);
		sNotesProjectionMap.put(DiaryMemo.BODY, DiaryMemo.BODY);
		sNotesProjectionMap.put(DiaryMemo.WIDGET_ID, DiaryMemo.WIDGET_ID);
		sNotesProjectionMap.put(DiaryMemo.CREATED, DiaryMemo.CREATED);
	}
	private static HashMap<String, String> sSuggestionProjectionMap;
	static {
		sSuggestionProjectionMap = new HashMap<String, String>();
		sSuggestionProjectionMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1,
			DiaryMemo.TITLE + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
		sSuggestionProjectionMap.put(SearchManager.SUGGEST_COLUMN_TEXT_2,
			DiaryMemo.BODY + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_2);
		sSuggestionProjectionMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, 
			DiaryMemo._ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		sSuggestionProjectionMap.put(DiaryMemo._ID, DiaryMemo._ID);
	}

	/** Helper class to open, create, and upgrade the database file. */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" 
				+ DiaryMemo._ID + " INTEGER PRIMARY KEY," 
				+ DiaryMemo.TITLE + " TEXT,"
				+ DiaryMemo.BODY + " TEXT," 
				+ DiaryMemo.WIDGET_ID + " INTEGER,"
				+ DiaryMemo.CREATED + " INTEGER" 
				+ ");"
			);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			int version = oldVersion;
			
//			Log.d("TAG", "Upgrading form version " + oldVersion + " to " + newVersion + ", 데이터베이스 버전 확인");
			
			// DataBase Version 5 -> 6 으로 업데이트 되면서 추가된 부분(Ver0.6.0 에서 추가됨 )
			 if (version != DATABASE_VERSION) {
				 db.execSQL("ALTER TABLE "+DATABASE_TABLE+" ADD COLUMN "+DiaryMemo.WIDGET_ID+" integer");
			 }
		}
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(DATABASE_TABLE);

		switch (sUriMatcher.match(uri)) {
			case SEARCH:
				qb.setProjectionMap(sSuggestionProjectionMap);
				String query = uri.getLastPathSegment();
				if (!TextUtils.isEmpty(query)) {
					qb.appendWhere(DiaryMemo.TITLE + " LIKE ");
					qb.appendWhereEscapeString('%' + query + '%');
					qb.appendWhere(" OR ");
					qb.appendWhere(DiaryMemo.BODY + " LIKE ");
					qb.appendWhereEscapeString('%' + query + '%');
				}
				break;

			case NOTES:
				qb.setProjectionMap(sNotesProjectionMap);
				break;

			case NOTE_ID:
				qb.setProjectionMap(sNotesProjectionMap);
				qb.appendWhere(DiaryMemo._ID + "=" + uri.getPathSegments().get(1));
				break;

			default:
				throw new IllegalArgumentException("Unsupported URI " + uri);
		}

		// 정렬 순서 설정( 미설정시 기본 값 )
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = DiaryMemo.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		// 데이터베이스를 개방
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		// DB의 URI를 전달함, 이 URI값을 변경한다는건 원본 값이 바뀐 다는걸 의미
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case NOTES:
				return DiaryMemo.CONTENT_TYPE;
			case NOTE_ID:
				return DiaryMemo.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException("Unsupported URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// 요청된 URI 유효성 검사
		if (sUriMatcher.match(uri) != NOTES) {
			throw new IllegalArgumentException("Unsupported URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		if (values.containsKey(DiaryMemo.TITLE) == false) {
			Resources r = Resources.getSystem();
			values.put(DiaryMemo.TITLE, r.getString(android.R.string.untitled)); //TODO: 내용이 없을경우 안드로이드 기본 스트링이 들어감  <제목없음>
		}

		if (values.containsKey(DiaryMemo.BODY) == false) {
			values.put(DiaryMemo.BODY, "");
		}
		
		if (values.containsKey(DiaryMemo.WIDGET_ID) == false) {
			values.put(DiaryMemo.WIDGET_ID, 0);
		}

		if (values.containsKey(DiaryMemo.CREATED) == false) {
			long now = System.currentTimeMillis();
			values.put(DiaryMemo.CREATED, now);
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(DATABASE_TABLE, DiaryMemo.BODY, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(DiaryMemo.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
			case NOTE_ID:
				String noteId = uri.getPathSegments().get(1);
				count = db.delete(DATABASE_TABLE, DiaryMemo._ID + "=" + noteId
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs
				);
				break;

			default:
				throw new IllegalArgumentException("Unsupported URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int count;
		switch (sUriMatcher.match(uri)) {
			case NOTE_ID:
				String noteId = uri.getPathSegments().get(1);
				count = db.update(DATABASE_TABLE, values, DiaryMemo._ID + "=" + noteId
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
				break;

			default:
				throw new IllegalArgumentException("Unsupported URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
