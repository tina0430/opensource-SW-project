package com.ybproject.diarymemo.view;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Stack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ybproject.diarymemo.DrawNoteBoard;
import com.ybproject.diarymemo.DrawNotePenPaletteDialog;
import com.ybproject.diarymemo.DrawNotePenPaletteDialog.OnPenSelectedListener;
import com.ybproject.diarymemo.provider.DrawNoteDB;

public class DrawNoteDraw extends View{

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
	private Path mPath = new Path();
	private float mCurveEndX;
	private float mCurveEndY;
	float lastX;
	float lastY;
	private int mInvalidateExtraBorder = 10;
	private static final float TOUCH_TOLERANCE = 8;
	private float mStrokeWidth = 12.0f;
	private int mCertainColor = Color.WHITE;
	Stack<Path> undos = new Stack<Path>();
	public static int maxUndos = 5;
	private static final int SCREEN_WIDTH = 800;
	private static final int VIEW_WIDTH = 2700;
	private int moveSize = 0;
	private ArrayList<PaintStack> undo_paint = new ArrayList<PaintStack>();
	private int bClearFlag = 0;
	
	//FIXME: 실제 단말에 넣을 때 저장 경로 sdcard로 변경
//	private static final String PATH = "/data/data/com.ybproject.diarymemo/";
	private static final String PATH = "/sdcard/DiaryNotepad/DrawNote/";
	public DrawNoteDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//현재설정값을 받아옴.
		DrawNoteDB dbCon;
		dbCon = new DrawNoteDB(context);
		Integer result[] = dbCon.getDraw();
		mStrokeWidth = result[0];
//		mCertainColor = result[1];

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
//		mPaint.setColor(mCertainColor);
		mPaint.setColor(Color.BLACK); //기본 펜색상을 지정함
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(mStrokeWidth);
		mPaint.setDither(true);

		mCanvas = new Canvas();
		lastX = -1;
		lastY = -1;
		bClearFlag = 0;
	}

	public void newImage(int width, int height) {
		//저장될 이미지셋팅(투명색 비트맵 생성후 배경 화이트 지정)
		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);
		mCanvas.drawColor(Color.WHITE);

		if(DrawNoteBoard.todoIndex != -1){
			load_image(DrawNoteBoard.todoIndex);
		}

		invalidate();
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w > 0 && h > 0) {
			newImage(w, h);
		}
	}

	public void clearUndo() {
		try{
			while (true) {
				Path prev = undos.pop();
				if (prev == null)
					return;
	
				prev.close();
			}
			
		}catch (Exception e) {
		}
		
		undo_paint.clear();

	}

	public void saveUndo() {
		if (mPath == null)
			return;

//		while (undos.size() >= maxUndos) {
//			Path i = undos.get(0);
//			Path i2 = undos.get(1);
//			i2.addPath(i);
//			i2.moveTo(0, 0);
//			undos.remove(i);
//			i.close();
//			i2.close();
//			
//			i2.addPath(i);
//			i2.moveTo(0, 0);
//			undos.remove(i);
//			i.close();
//			i2.close();
//		}
		
		undos.push(mPath);
		PaintStack paintInfo = new PaintStack();
		paintInfo.setColor(mPaint.getColor());
		paintInfo.setSize(mPaint.getStrokeWidth());
		undo_paint.add(paintInfo);
	}

	public void undo() {
		Path prev = new Path();
		try {
			prev = undos.pop();
			undo_paint.remove(undo_paint.size()-1);
			
			if (prev != null) {
				if (mCanvas != null) {
					mCanvas.drawColor(Color.WHITE); // 한단계 취소시 씌울 색상
				}
				if(DrawNoteBoard.todoIndex != 0){
					load_image(DrawNoteBoard.todoIndex);
				}
				for(int i=0; i < undos.size(); i++){
					if(undos.get(i) != null){
						mPaint.setColor(undo_paint.get(i).getColor());
						mPaint.setStrokeWidth(undo_paint.get(i).getSize());
						mCanvas.drawPath(undos.get(i), mPaint);
					}
				}
				invalidate();
			}
			
		} catch (Exception ex) {
			Log.e("GoodPaintBoard", "Exception : " + ex.getMessage());
		}

		prev.close();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, 0, 0, mPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_UP:

			Rect rect = touchUp(event, false);
			if (rect != null) {
				invalidate(rect);
			}
			mPath.moveTo(0, 0);
			saveUndo();
			mPath.close();
			if(DrawNoteBoard.play_flag == 1){
				int move = ((int)event.getX()-moveSize)/20;
				if(this.getRight() < SCREEN_WIDTH+move){
					moveSize = VIEW_WIDTH-SCREEN_WIDTH;
					this.layout(SCREEN_WIDTH-mBitmap.getWidth(), this.getTop(), SCREEN_WIDTH, this.getBottom());
					
				}else{
					moveSize += move;
					this.layout(this.getLeft()-move, this.getTop(), this.getRight()-move, this.getBottom());
				}
				int nowX = this.getLeft();
				
			}
			return true;

		case MotionEvent.ACTION_DOWN:
			rect = touchDown(event);
			if (rect != null) {
				invalidate(rect);
			}
			return true;

		case MotionEvent.ACTION_MOVE:
			rect = touchMove(event);
			if (rect != null) {
				invalidate(rect);
			}
			if(DrawNoteBoard.play_flag == 1){
				int move = ((int)event.getX()-moveSize)/80;
				if(this.getRight() < SCREEN_WIDTH+move){
					moveSize = VIEW_WIDTH-SCREEN_WIDTH;
					this.layout(SCREEN_WIDTH-mBitmap.getWidth(), this.getTop(), SCREEN_WIDTH, this.getBottom());
				}else{
					moveSize += move;
					this.layout(this.getLeft()-move, this.getTop(), this.getRight()-move, this.getBottom());
				}
			}
			return true;
		}

		return false;
	}

	private Rect touchDown(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();

		lastX = x;
		lastY = y;

		Rect mInvalidRect = new Rect();
		mPath = new Path();
		mPath.moveTo(x, y);

		final int border = mInvalidateExtraBorder;
		mInvalidRect.set((int) x - border, (int) y - border, (int) x + border,
				(int) y + border);

		mCurveEndX = x;
		mCurveEndY = y;
		
		mCanvas.drawPath(mPath, mPaint);

		return mInvalidRect;
	}

	private Rect touchMove(MotionEvent event) {
		Rect rect = processMove(event);
		return rect;
	}

	private Rect touchUp(MotionEvent event, boolean cancel) {
		Rect rect = processMove(event);

		return rect;
	}

	private Rect processMove(MotionEvent event) {

		final float x = event.getX();
		final float y = event.getY();

		final float dx = Math.abs(x - lastX);
		final float dy = Math.abs(y - lastY);

		Rect mInvalidRect = new Rect();
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			final int border = mInvalidateExtraBorder;
			mInvalidRect.set((int) mCurveEndX - border, (int) mCurveEndY
					- border, (int) mCurveEndX + border, (int) mCurveEndY
					+ border);

			float cX = mCurveEndX = (x + lastX) / 2;
			float cY = mCurveEndY = (y + lastY) / 2;

			mPath.quadTo(lastX, lastY, cX, cY);
			
			// union with the control point of the new curve
			mInvalidRect.union((int) lastX - border, (int) lastY - border,
					(int) lastX + border, (int) lastY + border);

			// union with the end point of the new curve
			mInvalidRect.union((int) cX - border, (int) cY - border, (int) cX
					+ border, (int) cY + border);

			lastX = x;
			lastY = y;
			mCanvas.drawPath(mPath, mPaint);
		}

		return mInvalidRect;
	}

	public void clear() {
		if (mCanvas != null) {
			mCanvas.drawColor(Color.WHITE); //배경 클리어시 덮어씌울 색상 지정
			clearUndo();
			invalidate();
			
			bClearFlag = 1;
			
			//첫번째 페이지로 간다.
			this.layout(0, this.getTop(), mBitmap.getWidth(), this.getBottom());
			moveSize = 0;
			DrawNoteBoard.now_page = 0;
		}
	}

	public boolean save(int todoIndex) {
		try {
        	String fullSrc = PATH + todoIndex + ".jpg";    
        	File directory = new File(PATH);                   	
        	
        	if (directory.exists() == false)
        	{
        		Boolean tt = directory.mkdir();               	
        	} 
			
			FileOutputStream out = new FileOutputStream(fullSrc);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		} catch (Exception e) {
			Log.d("ERROR", "DrawNoteDraw: Save error CODE 1");
		}
		return true;
	}

	public void load_image(int todoIndex) {
		if(bClearFlag == 0){
			try {
				Bitmap load_img = BitmapFactory.decodeFile(PATH + todoIndex + ".jpg");
				mCanvas.drawBitmap(load_img, 0, 0, mPaint);
				invalidate();
			} catch (Exception e) {
				Log.d("ERROR", "DrawNoteDraw: error CODE 2");
			}
		}
	}
	public void next() {
		switch(DrawNoteBoard.now_page){
		case 1:
			this.layout(-675, this.getTop(), 2025, this.getBottom());
			moveSize = 675;
			break;
		case 2:
			this.layout(-1350, this.getTop(), 1350, this.getBottom());
			moveSize = 1350;
			break;
		case 3:
			this.layout(-1900, this.getTop(), 800, this.getBottom());
			moveSize = 1900;
			break;
		}
		
	}
	public void prev() {
		switch(DrawNoteBoard.now_page){
		case 0:
			this.layout(0, this.getTop(), 2700, this.getBottom());
			moveSize = 0;
			break;
		case 1:
			this.layout(-675, this.getTop(), 2025, this.getBottom());
			moveSize = 675;
			break;
		case 2:
			this.layout(-1350, this.getTop(), 1350, this.getBottom());
			moveSize = 1350;
			break;
		}
	}
	
	public int getColor(){
		return mPaint.getColor();
	}

	public void setColor(int color){
		mPaint.setColor(color);
	}
	
	public void setPen(){
		DrawNotePenPaletteDialog.listener = new OnPenSelectedListener() {
			public void onPenSelected(int size) {
				mPaint.setStrokeWidth(size);
			}
		};
	}
}
