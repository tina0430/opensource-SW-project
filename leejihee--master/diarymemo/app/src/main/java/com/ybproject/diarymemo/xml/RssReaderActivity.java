package com.ybproject.diarymemo.xml;

import java.util.ArrayList;

import com.ybproject.diarymemo.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class RssReaderActivity extends ListActivity {

	private static final String RSS_FEED_URL = "";
	public static final int MENU_ITEM_RELOAD = Menu.FIRST;
	private ArrayList<Item> mItems;
	private RssListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);

		mItems = new ArrayList<Item>();
		mAdapter = new RssListAdapter(this, mItems);

		RssParserTask task = new RssParserTask(this, mAdapter);
		task.execute(RSS_FEED_URL);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Item item = mItems.get(position);
		Intent intent = new Intent(this, ItemDetailActivity.class);
		intent.putExtra("TITLE", item.getTitle());
		intent.putExtra("DESCRIPTION", item.getDesc());
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_ITEM_RELOAD, 0, "새로고침(Refresh)");
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case MENU_ITEM_RELOAD:

			mItems = new ArrayList<Item>();
			mAdapter = new RssListAdapter(this, mItems);

			RssParserTask task = new RssParserTask(this, mAdapter);
			task.execute(RSS_FEED_URL);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}