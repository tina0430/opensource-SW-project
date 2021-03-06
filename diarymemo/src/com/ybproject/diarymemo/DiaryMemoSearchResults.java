/**
 * 파일명: DiaryMemoSearchResults.java
 * 최종수정: 2012년 2월 11일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: 검색 결과 처리
 */
package com.ybproject.diarymemo;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.ybproject.diarymemo.provider.*;

/** Secondary activity for Diarmemo, shows search results. */
public class DiaryMemoSearchResults extends ListActivity {
	public static final int SEARCH_ID = Menu.FIRST;

	/** The columns we are interested in from the database */
	private static final String[] PROJECTION = new String[] { 
		DiaryMemo._ID, DiaryMemo.TITLE
	};

	private TextView mEmptyText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_note);
		mEmptyText = (TextView) findViewById(android.R.id.empty);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Intent intent = getIntent();
		handleSearchIntent(intent);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleSearchIntent(intent);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(DiaryMemo.CONTENT_URI, id);
		startActivity(new Intent(Intent.ACTION_EDIT, uri));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, SEARCH_ID, 0, R.string.menu_search)
			.setIcon(android.R.drawable.ic_menu_search);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case SEARCH_ID:
				onSearchRequested();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/** Handles callbacks from the system search service.
	 * @param intent Intent passed to this activity.
	 */
	private void handleSearchIntent(Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_SEARCH.equals(action)) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			showResults(query);
		} else if (Intent.ACTION_VIEW.equals(action)) {
			final Intent viewIntent = new Intent(Intent.ACTION_VIEW, intent.getData());
			startActivity(viewIntent);
			finish();
		} else {
			finish();
		}
	}

	/** Searches the notes for a given search term and displays the results.
	 * @param query Search term to query with.
	 */
	private void showResults(String query) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int sortOrder = Integer.valueOf(preferences.getString("sortOrder", "1"));
		String sorting = DiaryMemo.SORT_ORDERS[sortOrder];

		Cursor cursor = managedQuery(
			DiaryMemo.CONTENT_URI, PROJECTION, DiaryMemo.BODY + " LIKE ?", new String[] { "%" + query + "%" }, sorting
		);

		if (cursor != null) {
			int count = cursor.getCount();
			String countString = getResources().getQuantityString(
				R.plurals.search_results, count, new Object[] { count, query }
			);
			setTitle(countString);

			if (count == 0) {
				mEmptyText.setText(getString(R.string.search_noresults, new Object[] { query }));
			} else {
				Boolean largeListItems = preferences.getBoolean("listItemSize", true);

				SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
					(largeListItems) ? R.layout.row_large : R.layout.row_small, 
					cursor,
					new String[] { DiaryMemo.TITLE }, new int[] { android.R.id.text1 }
				);
				setListAdapter(adapter);
			}
		}
	}

}
