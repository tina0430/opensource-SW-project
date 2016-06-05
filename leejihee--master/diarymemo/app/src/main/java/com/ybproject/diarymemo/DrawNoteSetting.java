package com.ybproject.diarymemo;

import net.daum.adam.publisher.AdView;
import net.daum.adam.publisher.AdView.AnimationType;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ybproject.diarymemo.R;
import com.ybproject.diarymemo.DrawNotePenPaletteDialog.OnPenSelectedListener;
import com.ybproject.diarymemo.R.id;
import com.ybproject.diarymemo.R.layout;
import com.ybproject.diarymemo.provider.DrawNoteDB;

public class DrawNoteSetting extends Activity implements OnClickListener{

	private View setWidth;
	private View setColor1;
	private View setColor2;
	private View setColor3;
	private RadioGroup radio;
	private int width;
	private int color;
	private int scroll;
	private int color1;
	private int color2;
	private int color3;
	
	private DrawNoteDB dbCon;
	private static final String ACTION_PICK_COLOR = "com.ybproject.diarymemo.action.PICK_COLOR";
	private static final int REQUEST_CODE_PICK_COLOR = 1;
	private static final int REQUEST_CODE_PICK_COLOR1 = 2;
	private static final int REQUEST_CODE_PICK_COLOR2 = 3;
	private static final int REQUEST_CODE_PICK_COLOR3 = 4;
	//광고
    private AdView adView = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
        //아담 광고
		String systemLanguage = getResources().getConfiguration().locale.getLanguage();
		String Ko_lang = "ko";

		//사용언어가 한국어 일경우에만 아담 광고 노출
		if(Ko_lang.equals(systemLanguage)){
			initAdam(); //아담 광고 초기화
		}else{
			//차후 로케이션이 영문일경우 애드몹 광고 추가예정
			Log.i("lang:",systemLanguage);
		}
		
		setWidth = findViewById(R.id.setWidth);
		setColor1 = findViewById(R.id.color1);
		setColor1.setOnClickListener(this);
		setColor2 = findViewById(R.id.color2);
		setColor2.setOnClickListener(this);
		setColor3 = findViewById(R.id.color3);
		setColor3.setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_width).setOnClickListener(this);
		findViewById(R.id.btn_color).setOnClickListener(this);
//		radio = (RadioGroup)findViewById(R.id.radiogroup);
		
		dbCon = new DrawNoteDB(this);
		
		//현재설정값을 받아옴.
		Integer result[] = dbCon.getDraw();
		width = result[0];
		color = result[1];
		scroll = result[2];
		
		result = dbCon.getFavoriteColor();
		color1 = result[0];
		color2 = result[1];
		color3 = result[2];
		
		setPen();
		setColor1.setBackgroundColor(color1);
		setColor2.setBackgroundColor(color2);
		setColor3.setBackgroundColor(color3);
		
		//스크롤 off 설정
//		if(scroll == 0){
//			RadioButton off = (RadioButton)findViewById(R.id.off);
//			off.setChecked(true);
//		}
	}

	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_save:
//			if(radio.getCheckedRadioButtonId() == R.id.on){
//				scroll = 1;
//			}else{
//				scroll = 0;
//			}
			dbCon.setDraw(width, color, scroll);
			dbCon.setFavoriteColor(color1, color2, color3);
			finish();
			break;
		case R.id.btn_width:
			DrawNotePenPaletteDialog.listener = new OnPenSelectedListener() {
				public void onPenSelected(int size) {
					width = size;
					setPen();
				}
			};
			intent = new Intent(DrawNoteSetting.this, DrawNotePenPaletteDialog.class);
			startActivity(intent);
			break;
		case R.id.btn_color:
			intent = new Intent();
			intent.setAction(ACTION_PICK_COLOR);
			intent.putExtra("color", color);
			intent.putExtra("setting", true);
			startActivityForResult(intent, REQUEST_CODE_PICK_COLOR);
			break;
		case R.id.color1:
			intent = new Intent();
			intent.setAction(ACTION_PICK_COLOR);
			intent.putExtra("color", color1);
			intent.putExtra("setting", true);
			startActivityForResult(intent, REQUEST_CODE_PICK_COLOR1);
			break;
		case R.id.color2:
			intent = new Intent();
			intent.setAction(ACTION_PICK_COLOR);
			intent.putExtra("color", color2);
			intent.putExtra("setting", true);
			startActivityForResult(intent, REQUEST_CODE_PICK_COLOR2);
			break;
		case R.id.color3:
			intent = new Intent();
			intent.setAction(ACTION_PICK_COLOR);
			intent.putExtra("color", color3);
			intent.putExtra("setting", true);
			startActivityForResult(intent, REQUEST_CODE_PICK_COLOR3);
			break;
		}
	}
	
	 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			switch(requestCode) {
			case REQUEST_CODE_PICK_COLOR:
				if (resultCode == RESULT_OK) {
					color = data.getIntExtra("color", color);
					setPen();
				}
				break;
			case REQUEST_CODE_PICK_COLOR1:
				if (resultCode == RESULT_OK) {
					color1 = data.getIntExtra("color", color);
					setColor1.setBackgroundColor(color1);
				}
				break;
			case REQUEST_CODE_PICK_COLOR2:
				if (resultCode == RESULT_OK) {
					color2 = data.getIntExtra("color", color);
					setColor2.setBackgroundColor(color2);
				}
				break;
			case REQUEST_CODE_PICK_COLOR3:
				if (resultCode == RESULT_OK) {
					color3 = data.getIntExtra("color", color);
					setColor3.setBackgroundColor(color3);;
				}
				break;
			}
		}
	 
	 public void setPen(){
		//펜 두께 설정
			ViewGroup.LayoutParams params = setWidth.getLayoutParams();
			final Bitmap penBitmap = Bitmap.createBitmap(params.width, params.height, Bitmap.Config.ARGB_8888);
			final Canvas penCanvas = new Canvas();
			penCanvas.setBitmap(penBitmap);
			Paint mPaint = new Paint();
			mPaint.setColor(Color.BLACK);	
			mPaint.setAntiAlias(true);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			penCanvas.drawRect(0, 0, params.width, params.height, mPaint);
			
			mPaint.setColor(color);
			mPaint.setStrokeWidth((float)width);
			
//			penCanvas.drawCircle(params.width/2, params.height/2, width, mPaint);
			penCanvas.drawLine(10, params.height/2, params.width-10, params.height/2, mPaint);
			BitmapDrawable penDrawable = new BitmapDrawable(this.getResources(), penBitmap);
			setWidth.setBackgroundDrawable(penDrawable);
	 }
	    //광고 모듈
		@Override
		public void onDestroy() {
			super.onDestroy();

			if (adView != null) {
				adView.destroy();
				adView = null;
			}
		}
		
		private void initAdam() {
			// Ad@m sdk 초기화 시작
			DiaryMemoAppManager AdClientID = new DiaryMemoAppManager();
			String ClientID = AdClientID.ClientID;
			adView = (AdView) findViewById(R.id.adview);
			adView.setRequestInterval(5);

			// 할당 받은 clientId 설정
			adView.setClientId(ClientID);

			adView.setRequestInterval(12);

			// Animation 효과 : 기본 값은 AnimationType.NONE
			adView.setAnimationType(AnimationType.FLIP_HORIZONTAL);

			adView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onConfigurationChanged(Configuration newConfig) {
			super.onConfigurationChanged(newConfig);
		}
}
