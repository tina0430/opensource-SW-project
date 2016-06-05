package com.ybproject.diarymemo;

import com.ybproject.diarymemo.R;
import com.ybproject.diarymemo.R.id;
import com.ybproject.diarymemo.R.layout;
import com.ybproject.diarymemo.R.string;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class DrawNotePenPaletteDialog extends Activity {

	GridView grid;
	PenDataAdapter adapter;
	
	public static OnPenSelectedListener listener;
	
	public interface OnPenSelectedListener {
		public void onPenSelected(int pen);
	 
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.penpalette);
		
        this.setTitle(R.string.pen_title);
        
        grid = (GridView) findViewById(R.id.colorGrid);
        
//        grid.setColumnWidth(14);
        grid.setBackgroundColor(Color.GRAY);
        grid.setVerticalSpacing(20);
//        grid.setHorizontalSpacing(4);
        
        adapter = new PenDataAdapter(this);
        grid.setAdapter(adapter);
        grid.setNumColumns(adapter.getNumColumns());
        
	}

}
class PenDataAdapter extends BaseAdapter {

	Context mContext;
    public static final int [] pens = new int[] {
        3,6,9,12,16,20,25
    };
	
	int rowCount;
	int columnCount;
	
	
	
	public PenDataAdapter(Context context) {
		super();

		mContext = context;

		rowCount = 7;
		columnCount = 1;

	}

	public int getNumColumns() {
		return columnCount;
	}

	public int getCount() {
		return rowCount * columnCount;
	}

	public Object getItem(int position) {
		return pens[position];
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View view, ViewGroup group) {
		Log.d("PenDataAdapter", "getView(" + position + ") called.");

		// calculate position
		int rowIndex = position / rowCount;
		int columnIndex = position % rowCount;
		Log.d("PenDataAdapter", "Index : " + rowIndex + ", " + columnIndex);

		GridView.LayoutParams params = new GridView.LayoutParams(
				GridView.LayoutParams.FILL_PARENT,
				GridView.LayoutParams.FILL_PARENT);
		
		// create a Pen Image
		final int areaWidth = 10;
		final int areaHeight = 32;
		
		final Bitmap penBitmap = Bitmap.createBitmap(areaWidth, areaHeight, Bitmap.Config.ARGB_8888);
		final Canvas penCanvas = new Canvas();
		penCanvas.setBitmap(penBitmap);
		
		Paint mPaint = new Paint();
		mPaint.setColor(Color.WHITE);	
		mPaint.setAntiAlias(true);
//		mPaint.setStyle(Paint.Style.STROKE);
//		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		penCanvas.drawRect(0, 0, areaWidth, areaHeight, mPaint);
		
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth((float)pens[position]);
		penCanvas.drawLine(0, areaHeight/2, areaWidth-1, areaHeight/2, mPaint);
		BitmapDrawable penDrawable = new BitmapDrawable(mContext.getResources(), penBitmap);
		
		// create a Button with the color
		final Button aItem = new Button(mContext);
		aItem.setText(" ");
		aItem.setLayoutParams(params);
		aItem.setPadding(4, 4, 4, 4);
		aItem.setBackgroundDrawable(penDrawable);
		aItem.setHeight(25);
		aItem.setTag(pens[position]);
		


		
		aItem.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					Paint mPaint = new Paint();
					mPaint.setColor(Color.MAGENTA);		
					penCanvas.drawRect(0, 0, areaWidth, areaHeight, mPaint);
					
					mPaint.setColor(Color.BLACK);
					mPaint.setStrokeWidth((float)pens[position]);
					penCanvas.drawLine(0, areaHeight/2, areaWidth-1, areaHeight/2, mPaint);
					BitmapDrawable penDrawable = new BitmapDrawable(mContext.getResources(), penBitmap);
					aItem.setBackgroundDrawable(penDrawable);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					if (DrawNotePenPaletteDialog.listener != null) {
						DrawNotePenPaletteDialog.listener.onPenSelected(((Integer) v.getTag()).intValue());
					}
					((DrawNotePenPaletteDialog)mContext).finish();
				}
				return false;
			}
		});
		
		return aItem;
	}
}


