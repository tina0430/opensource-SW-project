package com.ybproject.diarymemo.provider;

import android.provider.BaseColumns;

public class DrawNoteDatabase {

	private DrawNoteDatabase() {}
	
	//darwlist
	public static final class darwlist implements BaseColumns {

	      private darwlist() {}
	      
	      public static final String TODO_TABLE_NAME = "darwlist";
	      
	      public static final String INDEX = "todoIndex";
	      public static final String IMAGE = "imagePath";
	      public static final String CHECK = "checked";
	      public static final String WIDGET = "widget";
	      public static final String NOTI = "noti";
	      public static final String NOTI_DATE = "notiDate";

	}
	
	//drawInit
	public static final class drawInit implements BaseColumns {

	      private drawInit() {}
	      
	      public static final String DRAW_TABLE_NAME = "drawInit";
	     
	      public static final String INDEX = "drawIndex";
	      public static final String WIDTH = "width";
	      public static final String COLOR = "color";
	      public static final String SCROLL = "scroll";
	      public static final String COLOR1 = "color1";
	      public static final String COLOR2 = "color2";
	      public static final String COLOR3 = "color3";

	}
}
