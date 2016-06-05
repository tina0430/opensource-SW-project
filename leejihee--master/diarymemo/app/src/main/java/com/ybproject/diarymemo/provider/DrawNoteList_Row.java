package com.ybproject.diarymemo.provider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DrawNoteList_Row {

	Bitmap mImage;	
	int mtodoIndex;
	String mImagePath;
	int mChecked;
	int mNoti;
	int mWidget;
	String mNotiDate;
	
	public DrawNoteList_Row()
	{	
	}
	public DrawNoteList_Row(DrawNoteList_Row list)
	{
		this.mtodoIndex = list.mtodoIndex;
		this.mImage=list.mImage;
		this.mChecked = list.mChecked;
		this.mWidget = list.mWidget;
		this.mNoti = list.mNoti;
		this.mNotiDate = list.mNotiDate;
	}
	
	public DrawNoteList_Row(String Memo,String Cate,String Money,String list_Date)
	{
	}
	
	public int gettodoIndex() {
		return mtodoIndex;
	}
	public void settodoIndex(int mtodoIndex) {
		this.mtodoIndex = mtodoIndex;
	}
	
	public Bitmap getImage() {
		return mImage;
	}
	public void setImagePath(String mImagePath) {
		this.mImagePath = mImagePath;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		mImage = BitmapFactory.decodeFile(mImagePath, options);
//		mImage =Bitmap.createBitmap(src);
	}
	
	public int getchecked() {
		return mChecked;
	}
	public void setchecked(int mChecked) {
		this.mChecked = mChecked;
	}
	
	public int getNoti() {
		return mNoti;
	}
	public void setNoti(Integer mNoti) {
		this.mNoti = mNoti;
	}
	
	public int getWidget() {
		return mWidget;
	}
	public void setWidget(Integer mWidget) {
		this.mWidget = mWidget;
	}
	
	public String getNotiDate() {
		return mNotiDate;
	}
	public void setNotiDate(String mNotiDate) {
		this.mNotiDate = mNotiDate;
	}
}

