package com.ybproject.diarymemo.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ybproject.diarymemo.provider.DrawNoteDatabase.darwlist;
import com.ybproject.diarymemo.provider.DrawNoteDatabase.drawInit;

public class DrawNoteDatabaseHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "diarymemo_draw.db";
    private static final int DATABASE_VERSION = 1;
	
	public DrawNoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the card table
		db.execSQL("CREATE TABLE " +  darwlist.TODO_TABLE_NAME + " ( " 
				+ darwlist.INDEX + " INTEGER PRIMARY KEY ," 
				+ darwlist.IMAGE + " TEXT , "
				+ darwlist.CHECK  + " INTEGER , "
				+ darwlist.WIDGET  + " INTEGER , "
				+ darwlist.NOTI  + " INTEGER , "
				+ darwlist.NOTI_DATE  + " datetime "
				+ ");");	
		
		db.execSQL("CREATE TABLE " +  drawInit.DRAW_TABLE_NAME + " ( " 
				+ drawInit.INDEX + " INTEGER PRIMARY KEY ," 
				+ drawInit.WIDTH + " INTEGER ," 
				+ drawInit.COLOR + " INTEGER , "
				+ drawInit.SCROLL  + " INTEGER, "
				+ drawInit.COLOR1  + " INTEGER, "
				+ drawInit.COLOR2  + " INTEGER, "
				+ drawInit.COLOR3  + " INTEGER "
				+ ");");
		
		db.execSQL("INSERT INTO drawInit (drawIndex, width, color, scroll, color1, color2, color3) VALUES(1, 16, -1, 1, -256, -65536, -16711681)");
		
//	db.execSQL("INSERT INTO todoList (imagePath, checked, widget, noti, notiDate) VALUES('/data/data/com.ybproject.diarymemo/1.jpg', 1, 0, 0,'2010-10-10')");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("CREATE TABLE " +  drawInit.DRAW_TABLE_NAME + " ( " 
				+ drawInit.INDEX + " INTEGER PRIMARY KEY ," 
				+ drawInit.WIDTH + " INTEGER ," 
				+ drawInit.COLOR + " INTEGER , "
				+ drawInit.SCROLL  + " INTEGER, "
				+ drawInit.COLOR1  + " INTEGER, "
				+ drawInit.COLOR2  + " INTEGER, "
				+ drawInit.COLOR3  + " INTEGER "
				+ ");");
		
		db.execSQL("INSERT INTO drawInit (drawIndex, width, color, scroll, color1, color2, color3) VALUES(1, 16, -1, 1, -256, -65536, -16711681)");
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
	
	public void logCursorInfo(Cursor c,String tag)
	{
		Log.i(tag,"*** Cursor Begin ***" + " Result:" + c.getCount() + " Colums: " + 
				c.getColumnCount());
		
		String rowHeaders = "|| ";
		
		for (int i = 0 ; i < c.getColumnCount() ; i++)
		{
			rowHeaders = rowHeaders.concat(c.getColumnName(i) + " || ");
		}
		
		Log.i(tag,"COLUMNS " + rowHeaders);
		
		c.moveToFirst();
		while(c.isAfterLast() == false)
		{
			
			String rowResults = "|| ";
			for (int i = 0; i < c.getColumnCount() ; i++)
			{
				rowResults = rowResults.concat(c.getString(i) + " || ");
			}
			
			Log.i(tag,"Row " + c.getPosition() +": " + rowResults);
			
			c.moveToNext();
		}
	}
	
	
}
