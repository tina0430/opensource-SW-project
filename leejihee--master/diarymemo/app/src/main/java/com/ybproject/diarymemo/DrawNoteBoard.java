package com.ybproject.diarymemo;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ybproject.diarymemo.R;
import com.ybproject.diarymemo.provider.DrawNoteDB;
import com.ybproject.diarymemo.view.DrawNoteDraw;

public class DrawNoteBoard extends Activity implements OnClickListener{
	
	public static DrawNoteDraw mdrawnote;
	private static ImageView[] page;
	
	private ImageButton btn_stop;
	public static int play_flag = 1;
	public static int now_page = 0;
	
	public static int todoIndex;
	private int noti;
	private int widget;
	private String notiDate;
	private int mColor;
	
	private static final String ACTION_PICK_COLOR = "com.ybproject.diarymemo.action.PICK_COLOR";
	private static final int REQUEST_CODE_PICK_COLOR = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawboard);
        mdrawnote = (DrawNoteDraw)findViewById(R.id.board);
        findViewById(R.id.btn_undo).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_color).setOnClickListener(this);
        findViewById(R.id.btn_pen).setOnClickListener(this);
        
        //현재설정값을 받아옴.
        DrawNoteDB dbCon;
        dbCon = new DrawNoteDB(this);
		Integer result[] = dbCon.getDraw();
		dbCon.closeTodo();
		
		
		play_flag = result[0];
		
		if (play_flag == 0) {
			btn_stop.setBackgroundResource(R.drawable.btn_check);
		}

        now_page = 0;
        todoIndex = -1;
        
        page = new ImageView[1];
        page[0] = (ImageView)findViewById(R.id.page0);
              
        Intent intent = getIntent();
		todoIndex = intent.getIntExtra("todoIndex", -1);
		widget = intent.getIntExtra("widget", 0);
		noti = intent.getIntExtra("noti", 0);
		notiDate = intent.getStringExtra("notiDate");
        
     // FIXME: DB초기화
//	    deleteDatabase(".db");
    }

	@Override
	protected void onStop() {
		finish();              // 옵션창이 꺼질 때 같이 finish 되도록 
		super.onStop();
	}
	
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_undo:
			mdrawnote.undo();
			break;
		case R.id.btn_clear:
			mdrawnote.clear();
			break;
		case R.id.btn_save:
			intent = new Intent(DrawNoteBoard.this, DrawNoteOptionDialog.class);
			if (todoIndex != -1) { // 수정일 때
				intent.putExtra("todoIndex", todoIndex);
				intent.putExtra("widget", widget);
				intent.putExtra("noti", noti);
				intent.putExtra("notiDate", notiDate);
				mdrawnote.save(todoIndex);
			}
			startActivity(intent);
			break;
		case R.id.btn_back:
			intent = new Intent(DrawNoteBoard.this, DrawNoteList.class);
			startActivity(intent);
			finish();
			break;
		case R.id.btn_color:
			intent = new Intent();
			intent.setAction(ACTION_PICK_COLOR);
			mColor = mdrawnote.getColor();
			intent.putExtra("color", mColor);
			startActivityForResult(intent, REQUEST_CODE_PICK_COLOR);
			break;
		case R.id.btn_pen:
			mdrawnote.setPen();
			intent = new Intent(DrawNoteBoard.this, DrawNotePenPaletteDialog.class);
			startActivity(intent);
			break;
		}
	}
	
	/////////////////////////////////////////////////////
	// Color changed listener:
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case REQUEST_CODE_PICK_COLOR:
			if (resultCode == RESULT_OK) {
				mColor = data.getIntExtra("color", mColor);
				mdrawnote.setColor(mColor);
			}
			break;
		}
	}
}