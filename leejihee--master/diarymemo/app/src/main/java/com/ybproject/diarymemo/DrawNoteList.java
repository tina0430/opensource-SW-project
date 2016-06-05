package com.ybproject.diarymemo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ybproject.diarymemo.R;
import com.ybproject.diarymemo.R.id;
import com.ybproject.diarymemo.R.layout;
import com.ybproject.diarymemo.R.string;
import com.ybproject.diarymemo.background.DrawNoteNotification;
import com.ybproject.diarymemo.provider.DrawNoteDB;
import com.ybproject.diarymemo.provider.DrawNoteList_Row;

public class DrawNoteList extends ListActivity implements OnClickListener{
	private static DrawNoteDB dbCon;
	private ArrayList<DrawNoteList_Row> mTodoList;
	private ListAdapter mAdapter;
	private LinearLayout list_menu;
	
	private int selectedIndex;
	private int selectedNoti;
	private int selectedWidget;
	private String selectedNotiDate;
	private int selectedPosition;
	private boolean deleteFlag;
	private int tempPosition;
	private int tempIndex;
	
	private float x;
	
	//FIXME: 실제 단말에 넣을 때 저장 경로 sdcard로 변경
//	private static final String PATH = "/data/data/com.ybproject.diarymemo/";
	private static final String PATH = "/sdcard/DiaryNotepad/DrawNote/";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_draw);
		findViewById(R.id.btn_add).setOnClickListener(this);
		list_menu = (LinearLayout) findViewById(R.id.list_menu);
		list_menu.setVisibility(View.GONE);
		findViewById(R.id.btn_edit).setOnClickListener(this);
		findViewById(R.id.btn_share).setOnClickListener(this);
		findViewById(R.id.btn_setting).setOnClickListener(this);
		findViewById(R.id.btn_check).setOnClickListener(this);
		findViewById(R.id.btn_delete).setOnClickListener(this);
		findViewById(R.id.btn_notemain).setOnClickListener(this);
		
		mTodoList = new ArrayList<DrawNoteList_Row>();
		selectedPosition = -1;
		
		PaintDrawable d = new PaintDrawable(Color.TRANSPARENT);
		getListView().setSelector(d);
		
		//서비스 해제
		AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(DrawNoteList.this, DrawNoteNotification.class);
		PendingIntent pi = PendingIntent.getService(DrawNoteList.this, 0, intent, 0);
		am.cancel(pi);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();
	}
	
	@Override
	protected void onRestart() {
		mTodoList = new ArrayList<DrawNoteList_Row>();
		selectedPosition = -1;
		list_menu.setVisibility(View.GONE);
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		dbCon = new DrawNoteDB(this);
		
		mTodoList = dbCon.select_todo();
//		list_menu.setVisibility(View.GONE);
		mAdapter = new ListAdapter(this, R.layout.list_row, mTodoList);
		setListAdapter(mAdapter);
		
		super.onResume();
	}
	
	
	@Override
	protected void onStop() {
		dbCon.closeTodo();
		super.onStop();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		DrawNoteNotification.schedule(DrawNoteList.this);  // 서비스 시작
		
		Intent intent = new Intent("DRAWNOTE_END");
		sendBroadcast(intent);
	}
	
	public void onClick(View v) {
		Intent intent;
		switch(v.getId())
		{
		case R.id.btn_add:
			intent = new Intent(DrawNoteList.this, DrawNoteBoard.class);
			startActivity(intent);
			//finish();
			break;
		case R.id.btn_edit:
			intent = new Intent(DrawNoteList.this, DrawNoteBoard.class);
			intent.putExtra("todoIndex", selectedIndex);
			intent.putExtra("widget", selectedWidget);
			intent.putExtra("noti", selectedNoti);
			intent.putExtra("notiDate", selectedNotiDate);
			startActivity(intent);
			//finish();
			break;
		case R.id.btn_share:
	    	Intent i= new Intent(android.content.Intent.ACTION_SEND);
			 i.setType("image/jpeg");
			 i.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse("file://" + PATH + selectedIndex + ".jpg"));
			 startActivity(Intent.createChooser(i, "Send to"));
			break;
		case R.id.btn_setting:
			intent = new Intent(DrawNoteList.this, DrawNoteOptionDialog.class);
			intent.putExtra("todoIndex", selectedIndex);
			intent.putExtra("widget", selectedWidget);
			intent.putExtra("noti", selectedNoti);
			intent.putExtra("notiDate", selectedNotiDate);
			startActivity(intent);
			break;
		case R.id.btn_check:
			if (mTodoList.get(selectedPosition).getchecked() == 0) { //체크
				dbCon.setChecked(selectedIndex, 1);
				mTodoList.get(selectedPosition).setchecked(1);
				setListAdapter(mAdapter);
			} 
			else{
				dbCon.setChecked(selectedIndex, 0);
				mTodoList.get(selectedPosition).setchecked(0);
				setListAdapter(mAdapter);
			}
			break;
		case R.id.btn_delete:
			deleteFlag = true;
			alertDialog();
			break;
		//다시 메모장 으로 이동한다.
		case R.id.btn_notemain:
			Intent intent_home = new Intent(DrawNoteList.this, DiaryMemoList.class);
			startActivity(intent_home);
			finish();
			break;
		}
	}
	
	public void alertDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(this.getResources().getText(R.string.delete_confirmation))
	            .setCancelable(false)
	            .setPositiveButton(this.getResources().getText(R.string.dialog_confirm),
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int id) {
	                        	if(deleteFlag){
		                        	DrawNoteList_Row temp = new DrawNoteList_Row(mTodoList.get(selectedPosition));
		            				selectedIndex = temp.gettodoIndex();
		            				mTodoList.remove(selectedPosition);
		            				
		            				//가장 끝 리스트가 지워졌을 경우
		            				if(selectedPosition == mTodoList.size()){
		            					selectedPosition--;
		            				}
		            				//리스트가 더이상 없는경우
		            				if(mTodoList.isEmpty()){
		            					selectedPosition = -1;
		            					list_menu.setVisibility(View.GONE);
		            				}
		            				setListAdapter(mAdapter);
		            				dbCon.delete_todo(selectedIndex);
	                        	}
	                        	else{
	                        		mTodoList.remove(tempPosition);
									setListAdapter(mAdapter);
									dbCon.delete_todo(tempIndex);
									list_menu.setVisibility(View.GONE);
	                        	}
	                        }
	                        
	                })
	            .setNegativeButton(this.getResources().getText(R.string.dialog_cancel),
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int id) {
	                            dialog.cancel();
	                        }
	                });
	    AlertDialog alert = builder.create();
	    alert.show();
	}

	// //////////////////////////List Adapter/////////////////////////
	public class ListAdapter extends ArrayAdapter<DrawNoteList_Row> {
		private ArrayList<DrawNoteList_Row> items;

		public ListAdapter(Context context, int textViewResourceId,
				ArrayList<DrawNoteList_Row> objects) {
			super(context, textViewResourceId, objects);
			items = objects;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = convertView;
			final ViewHolder vh;
			final DrawNoteList_Row temp;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_row, null);
				ImageView image = (ImageView) v.findViewById(R.id.image);
				ImageView check = (ImageView) v.findViewById(R.id.check);
				TextView date = (TextView) v.findViewById(R.id.date);
				ImageView widget = (ImageView) v.findViewById(R.id.widget);
				ImageView noti = (ImageView) v.findViewById(R.id.noti);
				ImageView check_back = (ImageView) v.findViewById(R.id.check_back);
				ImageView select_back = (ImageView) v.findViewById(R.id.select_back);
				
				vh = new ViewHolder();
				vh.image = image;
				vh.checked = check;
				vh.notiDate = date;
				vh.widget = widget;
				vh.noti = noti;
				vh.check_back = check_back;
				vh.select_back = select_back;
				v.setTag(vh);
			} else {
				vh = (ViewHolder) v.getTag();
			}
			
			
			temp = new DrawNoteList_Row(items.get(position));

			vh.image.setImageBitmap(temp.getImage());
			if (temp.getchecked() == 1) {
				vh.checked.setVisibility(View.VISIBLE);
				vh.check_back.setVisibility(View.VISIBLE);
			} else {
				vh.checked.setVisibility(View.INVISIBLE);
				vh.check_back.setVisibility(View.GONE);
			}
			if (temp.getWidget() == 1) {
				vh.widget.setVisibility(View.VISIBLE);
			} else {
				vh.widget.setVisibility(View.INVISIBLE);
			}
			if (temp.getNoti() == 1) {
				vh.noti.setVisibility(View.VISIBLE);
				String date = temp.getNotiDate();
	    		date = date.substring(0, date.indexOf(" "));
				vh.notiDate.setText(date);
			} else {
				vh.noti.setVisibility(View.INVISIBLE);
				vh.notiDate.setText("");
			}
			if(selectedPosition == position){
				vh.select_back.setVisibility(View.VISIBLE);
				list_menu.setVisibility(View.VISIBLE);
			} else{
				vh.select_back.setVisibility(View.GONE);
			}
			
			v.setOnTouchListener(new OnTouchListener() {
				   
				   public boolean onTouch(View v, MotionEvent event) {
				    if(event.getAction() == event.ACTION_DOWN){
				    	v.setBackgroundColor(Color.WHITE);
				    	x = event.getX();
				    }
				    else if(event.getAction() == event.ACTION_UP){
				    	
				    	if(event.getX()-x > 50){
				    		if (mTodoList.get(position).getchecked() == 0) { //체크
								mTodoList.get(position).setchecked(1);
								setListAdapter(mAdapter);
								dbCon.setChecked(temp.gettodoIndex(), 1);
							} else { //삭제
								deleteFlag = false;
								tempPosition = position;
								tempIndex = temp.gettodoIndex();
								alertDialog();
							}
				    	}
				    	else if(event.getX()-x < -50){
				    		if(mTodoList.get(position).getchecked() == 1){ //체크해제
								mTodoList.get(position).setchecked(0);
								setListAdapter(mAdapter);
								dbCon.setChecked(temp.gettodoIndex(), 0);
				    		}
				    	}
				    	else{    // 리스트 클릭 리스너
				    		if(list_menu.getVisibility() == View.GONE){  //리스트 처음선택
				    			list_menu.setVisibility(View.VISIBLE);
				    			vh.select_back.setVisibility(View.VISIBLE);
				    			selectedIndex = temp.gettodoIndex();
				    			selectedNoti = temp.getNoti();
				    			selectedNotiDate = temp.getNotiDate();
				    			selectedWidget = temp.getWidget();
				    			selectedPosition = position;
				    		}
				    		else{
				    			if(selectedPosition != position){   //리스트선택된 상태에서 다른리스트 선택
					    			selectedIndex = temp.gettodoIndex();
					    			selectedNoti = temp.getNoti();
					    			selectedNotiDate = temp.getNotiDate();
					    			selectedWidget = temp.getWidget();
					    			selectedPosition = position;
									setListAdapter(mAdapter);
				    			}else{								//리스트 선택해제
				    				list_menu.setVisibility(View.GONE);
				    				selectedPosition = -1;
									setListAdapter(mAdapter);
				    			}
				    		}
				    	}
				    }
				    return true;
				   }
				  });

			return v;
		}
	}

	static class ViewHolder {
		ImageView image;
		ImageView checked;
		TextView notiDate;
		ImageView widget;
		ImageView noti;
		ImageView check_back;
		ImageView select_back;
	}
	
}
