/**
 * 파일명: DiaryMemo.java
 * 최종수정: 2012년 2월 11일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: DB 테이블 정의
 */
package com.ybproject.diarymemo.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

/** Data class representing a single note. */
public class DiaryMemo implements BaseColumns, Parcelable {
	/** The authority for this data class. */
	public static final String AUTHORITY = "com.ybproject.diarymemo";

	/** The content:// style URL for this data class. */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notes");

	/** The MIME type of {@link #CONTENT_URI} providing a directory of notes. */
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.diarymemo.note";

	/** The MIME type of a {@link #CONTENT_URI} sub-directory of a single note. */
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.diarymemo.note";

	/** The default sort order. */
	public static final String DEFAULT_SORT_ORDER = "_id";

	/** Possible relevant sort orders. */
	public static final String[] SORT_ORDERS = new String[] { 
		DiaryMemo.DEFAULT_SORT_ORDER, DiaryMemo.TITLE, DiaryMemo.CREATED
	};

	/** The title of the note. <P>Type: TEXT</P> */
	public static final String TITLE = "title";

	/** The body of the note. <P>Type: TEXT</P> */
	public static final String BODY = "body";
	
	/** Widget ID **/
	public static final String WIDGET_ID = "widget_id";

	/** The creation date of the note. <P>Type: INTEGER</P> */
	public static final String CREATED = "created";

	private long mId;
	private String mTitle;
	private String mBody;
	private String mWidgetId;
	private String mCreated;

	public String getTitle() 	{ return mTitle; }
	public String getBody() 	{ return mBody; }
	public String getWidgetId()	{ return mWidgetId; }
	public String getCreated()	{ return mCreated; }

	public Uri getUri()			{ return ContentUris.withAppendedId(CONTENT_URI, mId); }
	
	/** Creates a new instance of the <code>Note</code> class. */
	public DiaryMemo() {
		mId = -1;
	}

	/** Copy constructor */
	public DiaryMemo(DiaryMemo note) {
		mId = note.mId;
		mTitle = note.mTitle;
		mBody = note.mBody;

	}

	private DiaryMemo(Parcel in) {
		mId = in.readLong();
		mTitle = in.readString();
		mBody = in.readString();
		
	}

	/** Returns a <code>ContentValues</code> object representing this note.
	 * @return <code>ContentValues</code> instance holding the values of the note.
	 */
	public ContentValues getContentValues() {
		final ContentValues values = new ContentValues();

		values.put(_ID, mId);
		values.put(TITLE, mTitle);
		values.put(BODY, mBody);
		values.put(WIDGET_ID, 0);

		return values;
	}

	/** Creates a new note instance from a cursor returned by <code>com.ybproject.DiaryMemoProvider</code>.
	 * @param cursor Cursor to a row representing a note.
	 * @return new instance of a note object.
	 */
	public static DiaryMemo fromCursor(Cursor cursor) {
		final DiaryMemo note = new DiaryMemo();

		if (cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				note.mId = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
				note.mTitle = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
				note.mBody = cursor.getString(cursor.getColumnIndexOrThrow(BODY));
				note.mWidgetId = cursor.getString(cursor.getColumnIndexOrThrow(WIDGET_ID));
				note.mCreated = cursor.getString(cursor.getColumnIndexOrThrow(CREATED));

			}
		}

		return note;
	}

	// Parcelable
	public int describeContents() {
		return 0;
	}

	// Parcelable
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mId);
		dest.writeString(mTitle);
		dest.writeString(mBody);
	}

	public static final Creator<DiaryMemo> CREATOR = new Creator<DiaryMemo>() {
		public DiaryMemo createFromParcel(Parcel in) {
			return new DiaryMemo(in);
		}

		public DiaryMemo[] newArray(int size) {
			return new DiaryMemo[size];
		}
	};

}
