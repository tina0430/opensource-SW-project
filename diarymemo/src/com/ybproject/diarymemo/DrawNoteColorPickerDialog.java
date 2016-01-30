package com.ybproject.diarymemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.ybproject.diarymemo.R;
import com.ybproject.diarymemo.R.id;
import com.ybproject.diarymemo.R.layout;
import com.ybproject.diarymemo.R.string;
import com.ybproject.diarymemo.provider.DrawNoteDB;
import com.ybproject.diarymemo.view.ColorCircle;
import com.ybproject.diarymemo.view.ColorSlider;
import com.ybproject.diarymemo.view.OnColorChangedListener;

public class DrawNoteColorPickerDialog extends Activity 
	implements OnColorChangedListener, OnClickListener {
	
	ColorCircle mColorCircle;
	ColorSlider mSaturation;
	ColorSlider mValue;
	View setColor1;
	View setColor2;
	View setColor3;
	
	private int color1;
	private int color2;
	private int color3;
	
	Intent mIntent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.colorpicker);
        setTitle(R.string.color_title);
        
        setColor1 = findViewById(R.id.color1);
		setColor1.setOnClickListener(this);
		setColor2 = findViewById(R.id.color2);
		setColor2.setOnClickListener(this);
		setColor3 = findViewById(R.id.color3);
		setColor3.setOnClickListener(this);
        
		DrawNoteDB dbCon;
        dbCon = new DrawNoteDB(this);
        //현재설정값을 받아옴.
		Integer result[] = dbCon.getFavoriteColor();
		dbCon.closeTodo();
		
		color1 = result[0];
		color2 = result[1];
		color3 = result[2];
		
		setColor1.setBackgroundColor(color1);
		setColor2.setBackgroundColor(color2);
		setColor3.setBackgroundColor(color3);
		
        // Get original color
        mIntent = getIntent();
        if (mIntent == null) {
        	mIntent = new Intent();
        }
        
        //Setting클래스에서 호출한 경우
        boolean flag = mIntent.getBooleanExtra("setting", false);
        if(flag){
        	findViewById(R.id.favorite).setVisibility(View.GONE);
        }

        int color;
        final ColorPickerState state = (ColorPickerState) getLastNonConfigurationInstance();
        if (state != null) {
        	color = state.mColor;
        } else {
        	color = mIntent.getIntExtra("color", Color.BLACK);
        }

        mColorCircle = (ColorCircle) findViewById(R.id.colorcircle);
        mColorCircle.setOnColorChangedListener(this);
        mColorCircle.setColor(color);

        mSaturation = (ColorSlider) findViewById(R.id.saturation);
        mSaturation.setOnColorChangedListener(this);
        mSaturation.setColors(color, Color.BLACK);

        mValue = (ColorSlider) findViewById(R.id.value);
        mValue.setOnColorChangedListener(this);
        mValue.setColors(Color.WHITE, color);
	}

	class ColorPickerState {
    	int mColor;
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
    	ColorPickerState state = new ColorPickerState();
    	state.mColor = this.mColorCircle.getColor();
        return state;
    }
	
	

	public int toGray(int color) {
		int a = Color.alpha(color);
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		int gray = (r + g + b) / 3;
		return Color.argb(a, gray, gray, gray);
	}
	
	
	public void onColorChanged(View view, int newColor) {
		if (view == mColorCircle) {
			mValue.setColors(0xFFFFFFFF, newColor);
	        mSaturation.setColors(newColor, 0xff000000);
		} else if (view == mSaturation) {
			mColorCircle.setColor(newColor);
			mValue.setColors(0xFFFFFFFF, newColor);
		} else if (view == mValue) {
			mColorCircle.setColor(newColor);
		}
		
	}

	
	public void onColorPicked(View view, int newColor) {
		// We can return result
		mIntent.putExtra("color", newColor);
		setResult(RESULT_OK, mIntent);
		finish();
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.color1:
			mIntent.putExtra("color", color1);
			setResult(RESULT_OK, mIntent);
			finish();
			break;
		case R.id.color2:
			mIntent.putExtra("color", color2);
			setResult(RESULT_OK, mIntent);
			finish();
			break;
		case R.id.color3:
			mIntent.putExtra("color", color3);
			setResult(RESULT_OK, mIntent);
			finish();
			break;
		}
	}
}
