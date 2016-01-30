package com.ybproject.diarymemo.provider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DrawNoteDB {
	
	public Context context;
	
	protected DrawNoteDatabaseHelper mDatabase;
	protected SQLiteDatabase mDB;
	
	//FIXME: 실제 단말에 넣을 때 저장 경로 sdcard로 변경
	private static final String PATH = "/sdcard/DiaryNotepad/DrawNote/";
	private static final String OLD_PATH = "/sdcard/";
	
	public DrawNoteDB(Context context)  
	{
		this.context = context;
		
		mDatabase = new DrawNoteDatabaseHelper(this.context);
		mDB = mDatabase.getWritableDatabase();
	}
	
	public int insert_todo(int widget, int noti, String notiDate){
		int todoIndex = 0;
		int nowIndex = 0;
		
		try{
			Cursor mCursor = mDB.rawQuery("SELECT todoIndex FROM darwlist", null);
			mCursor.moveToFirst();
			while(mCursor.isAfterLast() == false)
			{
				int tmpIndex = mCursor.getColumnIndex("todoIndex");
				nowIndex = mCursor.getInt(tmpIndex);
				if(nowIndex > todoIndex){
					todoIndex = nowIndex;
				}
				mCursor.moveToNext();
			}
			mCursor.close();
		}catch (Exception e) {
		}	
		if(todoIndex < 0 ){
			todoIndex = 0;
		}
		todoIndex = todoIndex + 1;
		String imagePath = PATH + todoIndex + ".jpg";
		
		mDB.execSQL("INSERT INTO darwlist (todoIndex, imagePath, checked, widget, noti, notiDate)" 
					+  "VALUES('" + todoIndex + "', '" + imagePath + "' , 0, '" + widget + "', '" + noti + "' , '" + notiDate + "')");
		
		return todoIndex;
	}
	
	public void update_todo(int todoIndex, int widget, int noti, String notiDate){
		mDB.execSQL("UPDATE darwlist"
					+ " SET widget='" + widget + "', noti='" + noti + "', notiDate = '" + notiDate
					+ "' WHERE todoIndex = '" + todoIndex + "'");
	}
	
	public void setChecked(int todoIndex, int checked){
		mDB.execSQL("UPDATE darwlist "
				+ " SET checked = " + checked 
				+ " WHERE todoIndex = '" + todoIndex + "'");
	}

	public ArrayList<DrawNoteList_Row> select_todo(){
		ArrayList<DrawNoteList_Row> items = new ArrayList<DrawNoteList_Row>();
		Cursor mCursor = mDB.rawQuery("SELECT todoIndex, imagePath, checked, widget, noti, notiDate FROM darwlist ORDER BY checked, noti DESC, widget DESC, notiDate, todoIndex", null);
		mCursor.moveToNext();
		
		mDatabase.logCursorInfo(mCursor, "data");
		
		int todoIndex;
		String imagePath = "";
		int checked;
		int widget;
		int noti;
		String notiDate = "";
		int tmpIndex;
		
		mCursor.moveToFirst();
		
		while(mCursor.isAfterLast() == false)
		{
			DrawNoteList_Row list = new DrawNoteList_Row();
			
			tmpIndex = mCursor.getColumnIndex("todoIndex");
			todoIndex = mCursor.getInt(tmpIndex);
			tmpIndex = mCursor.getColumnIndex("imagePath");
			imagePath = mCursor.getString(tmpIndex);
			tmpIndex = mCursor.getColumnIndex("checked");
			checked = mCursor.getInt(tmpIndex);
			tmpIndex = mCursor.getColumnIndex("widget");
			widget = mCursor.getInt(tmpIndex);
			tmpIndex = mCursor.getColumnIndex("noti");
			noti = mCursor.getInt(tmpIndex);
			tmpIndex = mCursor.getColumnIndex("notiDate");
			notiDate = mCursor.getString(tmpIndex);
			
			list.settodoIndex(todoIndex);
			list.setImagePath(imagePath);
			list.setchecked(checked);
			list.setWidget(widget);
			list.setNoti(noti);
			list.setNotiDate(notiDate);
			
			items.add(new DrawNoteList_Row(list));
			mCursor.moveToNext();
			
		}
		mCursor.close();
        return items;
	}

	public void delete_todo(int todoIndex){
		
		mDB.execSQL("Delete From darwlist Where todoIndex = '" + todoIndex + "'");
		try{
		File f = new File(PATH + todoIndex + ".jpg");
		f.delete();
		}catch (Exception e) {
		}
	}
	
	public int select_noti(){
		Date mDate = new Date();
		Calendar c = Calendar.getInstance();
	    c.set(mDate.getYear() + 1900, mDate.getMonth(), mDate.getDate());
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");    
	    String today = sdf.format(c.getTime());
	    today = today + " 00:00:00";
	    
	    Cursor mCursor = mDB.rawQuery("Select COUNT(todoIndex) as countNoti From darwlist Where checked = 0 AND noti = 1 And notiDate = '" + today + "' ", null);
		mCursor.moveToNext();
		
		int notiCount = mCursor.getColumnIndex("countNoti");
		int result =  mCursor.getInt(notiCount);
		
		mCursor.close();
		
		return result;
	}
	
	public void delete_noti(){
		mDB.execSQL("UPDATE darwlist SET noti = 0");
	}
	
	public ArrayList<Bitmap> select_widget(){
		ArrayList<Bitmap> items = new ArrayList<Bitmap>();
		Cursor mCursor = mDB.rawQuery("SELECT imagePath FROM darwlist Where checked = 0 AND widget = 1", null);
		mCursor.moveToNext();
		
		mDatabase.logCursorInfo(mCursor, "data");
		
		String imagePath = "";
		int tmpIndex;
		
		mCursor.moveToFirst();
		
		while(mCursor.isAfterLast() == false)
		{
			tmpIndex = mCursor.getColumnIndex("imagePath");
			imagePath = mCursor.getString(tmpIndex);
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
			items.add(bitmap);
			mCursor.moveToNext();
		}
		mCursor.close();
		
		return items; 
	}
	
	public void closeTodo(){
		
		mDB.close();
	}
	
	public void moveFile() {
		File directory = new File(PATH);
		if (directory.exists() == false) {
			Boolean tt = directory.mkdir();
		}
		
		Cursor mCursor = mDB.rawQuery("SELECT todoIndex, imagePath FROM darwlist", null);
		mCursor.moveToNext();
		
		mDatabase.logCursorInfo(mCursor, "data");
		
		int todoIndex;
		int tmpIndex;
		String imagePath = "";
		String oldSrc = "";
		String newSrc = "";
		
		mCursor.moveToFirst();
		
		while(mCursor.isAfterLast() == false)
		{
			tmpIndex = mCursor.getColumnIndex("todoIndex");
			todoIndex = mCursor.getInt(tmpIndex);
			
			//DataBase 업데이트
			imagePath = PATH + todoIndex + ".jpg";
			mDB.execSQL("UPDATE darwlist"
					+ " SET imagePath = '" + imagePath 
					+ "' WHERE todoIndex = '" + todoIndex + "'");
			
			//이미지 이동
			oldSrc = OLD_PATH + todoIndex + ".jpg";
			newSrc = PATH + todoIndex + ".jpg";
			File old_file = new File(oldSrc);
			File new_file = new File(newSrc);
			old_file.renameTo(new_file);
			
			mCursor.moveToNext();
		}
		mCursor.close();
	}
	
	public Integer[] getDraw(){
	    Cursor mCursor = mDB.rawQuery("Select width, color, scroll From drawInit Where drawIndex = 1", null);
		mCursor.moveToNext();
		
		int index;
		Integer result[] = new Integer[3];
		
		index = mCursor.getColumnIndex("width");
		result[0] =  mCursor.getInt(index);
		index = mCursor.getColumnIndex("color");
		result[1] =  mCursor.getInt(index);
		index = mCursor.getColumnIndex("scroll");
		result[2] =  mCursor.getInt(index);
		
		mCursor.close();
		
		return result;
	}
		
	public void setDraw(int width, int color, int scroll){
		mDB.execSQL("UPDATE drawInit"
					+ " SET width='" + width + "', color='" + color + "', scroll = '" + scroll
					+ "' WHERE drawIndex = 1");
	}
	
	public Integer[] getFavoriteColor(){
	    Cursor mCursor = mDB.rawQuery("Select color1, color2, color3 From drawInit Where drawIndex = 1", null);
		mCursor.moveToNext();
		
		int index;
		Integer result[] = new Integer[3];
		
		index = mCursor.getColumnIndex("color1");
		result[0] =  mCursor.getInt(index);
		index = mCursor.getColumnIndex("color2");
		result[1] =  mCursor.getInt(index);
		index = mCursor.getColumnIndex("color3");
		result[2] =  mCursor.getInt(index);
		
		mCursor.close();
		
		return result;
	}
		
	public void setFavoriteColor(int color1, int color2, int color3){
		mDB.execSQL("UPDATE drawInit"
					+ " SET color1='" + color1 + "', color2='" + color2 + "', color3 = '" + color3
					+ "' WHERE drawIndex = 1");
	}
	
}