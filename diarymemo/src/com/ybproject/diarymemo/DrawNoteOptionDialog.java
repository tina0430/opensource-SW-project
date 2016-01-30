package com.ybproject.diarymemo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;

import com.ybproject.diarymemo.R;
import com.ybproject.diarymemo.R.id;
import com.ybproject.diarymemo.R.layout;
import com.ybproject.diarymemo.R.string;
import com.ybproject.diarymemo.provider.DrawNoteDB;

public class DrawNoteOptionDialog extends Activity implements OnClickListener{

	private CheckBox check_noti;
	private CheckBox check_widget;
	private TextView text_date;
	
	private static DrawNoteDB dbCon;
	
	private int mYear;
    private int mMonth;
    private int mDay;
    private int todoIndex;
    private String notiDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.option_dialog);

		this.setTitle(R.string.option_title);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.btn_cancel).setOnClickListener(this);
		findViewById(R.id.btn_date).setOnClickListener(this);
		check_noti = (CheckBox) findViewById(R.id.check_noti);
//		check_widget = (CheckBox) findViewById(R.id.check_widget);
		text_date = (TextView) findViewById(R.id.text_date);
		dbCon = new DrawNoteDB(this.getApplicationContext());
		
		Intent intent = getIntent();
		todoIndex = intent.getIntExtra("todoIndex", -1);
		if(intent.getIntExtra("widget", 0) == 1){
			check_widget.setChecked(true);
		}
		if(intent.getIntExtra("noti", 0) == 1){
			check_noti.setChecked(true);
			notiDate = intent.getStringExtra("notiDate");
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date d = df.parse(notiDate);
				df.getCalendar();
				mYear = d.getYear()+1900;
				mMonth = d.getMonth();
				mDay = d.getDate();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else{
			final Calendar c = Calendar.getInstance();
	        mYear = c.get(Calendar.YEAR);
	        mMonth = c.get(Calendar.MONTH);
	        mDay = c.get(Calendar.DAY_OF_MONTH);
		}
        updateDisplay();
	}
	
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_date:
			showDialog(0);
			break;
		case R.id.btn_save:
			int widget = 0;
			int noti = 0;
//			if (check_widget.isChecked()) {
//				widget = 1;
//			}
			if (check_noti.isChecked()) {
				noti = 1;
			}
			if (todoIndex == -1) { // 새로 저장하는 경우
				todoIndex = dbCon.insert_todo(widget, noti, text_date.getText()
						.toString()
						+ " 00:00:00");
				DrawNoteBoard.mdrawnote.save(todoIndex);
			} else { // 편집하는 경우
				dbCon.update_todo(todoIndex, widget, noti, text_date.getText()
						.toString()
						+ " 00:00:00");
			}

			intent = new Intent(DrawNoteOptionDialog.this, DrawNoteList.class);
			startActivity(intent);
			finish();
			break;
		case R.id.btn_cancel:
			finish();
			break;
		}
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
		switch (id) 
		{
        case 0:    
		return new DatePickerDialog(this,mDateSetListener,mYear, mMonth, mDay);
		}
		return null;
    }
	@Override
    protected void onPrepareDialog(int id, Dialog dialog) {
				if(id == 0)
				{
					((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
				}


    }
	private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDisplay();
            }
        };
        private void updateDisplay() {
        	
        	String month = String.format("%02d", mMonth + 1);
            
            String day = String.format("%02d", mDay);                
            
            text_date.setText(
                	new StringBuilder()
                        // Month is 0 based so add 1
                	.append(mYear).append("-").append(month).append("-").append(day));          	
        }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbCon.closeTodo();
	}
}
