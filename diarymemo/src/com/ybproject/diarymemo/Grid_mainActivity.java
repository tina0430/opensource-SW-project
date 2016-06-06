package com.ybproject.diarymemo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ybproject.diarymemo.desk.DiaryMemoBugreport;
import com.ybproject.diarymemo.desk.DiaryMemoHelp;
import com.ybproject.diarymemo.desk.DiaryMemoHomepage;
import com.ybproject.diarymemo.provider.DiaryMemo;
import com.ybproject.diarymemo.xml.RssReaderActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Grid_mainActivity extends Activity {
	
	private Handler mHandler;
	private boolean mFlag = false;
	public static String UpdateServer= DiaryMemoAppManager.UpdateServer;
	String version_result;
	DownloadText downloadtext = new DownloadText();
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	// 메뉴 이미지 정의
	private Integer[] mThumbIds = {
			R.drawable.home,
			R.drawable.notice,
			R.drawable.qna,
			R.drawable.help,
			R.drawable.search,
			R.drawable.note,
			R.drawable.draw,
			R.drawable.gallery,
			R.drawable.homepage,
			R.drawable.yongdev_apps,
			R.drawable.versio_chk,
			R.drawable.setting,
			};
	
//	private String[] mMenuName = {
//			"환영합니다!","공지사항","문의하기","사용가이드","메모검색","일반메모 ","바로그리기","그리기메모","홈페이지","다른어플","버전체크","환경설정"
//			
//	};
	private int[] mMenuName = {
			R.string.grid_welcom,
			R.string.grid_notice,
			R.string.grid_contact,
			R.string.grid_useguide,
			R.string.grid_Search,
			R.string.grid_note,
			R.string.grid_drawing,
			R.string.grid_drawing_list,
			R.string.grid_homepage,
			R.string.grid_etcapp,
			R.string.grid_versionchk,
			R.string.grid_setting
			
	};
    public class MyAdapter extends BaseAdapter {
    	
    	private Context mContext;

		public MyAdapter(Context c) {
			// TODO Auto-generated constructor stub
			mContext = c;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return mThumbIds.length;
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mThumbIds[arg0];
		}

		public long getItemId(int arg0) {
			int temp = arg0;
			// TODO: 입력 액션
			switch( temp ){
				case 0:
					Toast.makeText(Grid_mainActivity.this, "원하시는 메뉴를 선택해보세요^^", Toast.LENGTH_SHORT).show();
				break;
				
				case 1: //공지사항
					Intent NoticeActivity = new Intent(Grid_mainActivity.this, RssReaderActivity.class);
					startActivity(NoticeActivity);
					break;
				
				case 2: //문의하기
					Intent report = new Intent(Grid_mainActivity.this , DiaryMemoBugreport.class);
					startActivity(report);
					break;
				case 3: //사용가이드
					Intent math_info = new Intent(Grid_mainActivity.this , DiaryMemoHelp.class);
					startActivity(math_info);
					break;
					
				case 4: //메모검색
					onSearchRequested();
					break;
					
				case 5: //일반메모 목록
					Intent memo_list = new Intent(Grid_mainActivity.this, DiaryMemoList.class);
					startActivity(memo_list);
					break;
					
				case 6: //그리기메모 작성

					Intent newDrawNote = new Intent(Grid_mainActivity.this, DrawNoteBoard.class);
					startActivity(newDrawNote);
					break;
					
				case 7: //그리기메모 목록
					Intent matching = new Intent(Grid_mainActivity.this, DrawNoteList.class);
					startActivity(matching);
					break;
					
				case 8: //홈페이지
					Intent hompageActivity = new Intent(Grid_mainActivity.this, DiaryMemoHomepage.class);
					startActivity(hompageActivity);
					break;
					
				case 9: //더많은 어플 보기
					if(DiaryMemoAppManager.CHECK == 0){
					String url = "market://search?q=YONGYONGDEV";
		            Intent i = new Intent(Intent.ACTION_VIEW);
		            i.setData(Uri.parse(url));
		            startActivity(i);
					}else{
						String url = "http://www.tstore.co.kr/userpoc/game/viewProduct.omp?t_top=DP000504&dpCatNo=DP04003&insDpCatNo=DP04003&insProdId=0000280203";
			            Intent i = new Intent(Intent.ACTION_VIEW);
			            i.setData(Uri.parse(url));
			            startActivity(i);
					}
					break;
					
				case 10: //버전체크
					try{ //네트워크 오류 체크
						if(Double.parseDouble(version_result) > DiaryMemoAppManager.sMyVersion )   
						{
							//구버전일 경우
							ShowDialog(0);
						}else{
							Toast.makeText(Grid_mainActivity.this, "최신버전을 사용중입니다", Toast.LENGTH_SHORT).show();
						}
					}catch(Exception ex){ Toast.makeText(Grid_mainActivity.this, "업데이트 서버에 접속실패 하였습니다\n어플리케이션을 재실행 한 뒤에 다시 체크해주세요", Toast.LENGTH_SHORT).show(); }
					break;
					
				case 11: //환경설정
					Intent prefsActivity = new Intent(Grid_mainActivity.this, Preferences.class);
					startActivity(prefsActivity);
					break;
			}
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			View grid;
			 
			if(convertView==null){
				grid = new View(mContext);
				LayoutInflater inflater=getLayoutInflater();
				grid=inflater.inflate(R.layout.mygrid, parent, false);
			}else{
				grid = (View)convertView;
			}
			
			ImageView imageView = (ImageView)grid.findViewById(R.id.imagepart);
			TextView textView = (TextView)grid.findViewById(R.id.textpart);
			imageView.setImageResource(mThumbIds[position]);
			textView.setText(mMenuName[position]);
			textView.setGravity(Gravity.CENTER);
			textView.setTextColor(Color.parseColor("#ffffff"));    // 메인 화면 아이콘의 이름 색상을 흰색으로 바꿈.

			return grid;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀바 숨김
		setContentView(R.layout.grid_main);
		
        //그리드 메뉴
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new MyAdapter(this));
        Toast.makeText(Grid_mainActivity.this, R.string.grid_welcom, Toast.LENGTH_SHORT).show();
		
		// 업데이트 스레드 시작
		downloadtext.start();
		// BACK키 핸들러 ( 휴대폰 종료 관련 로직 )
		mHandler = new Handler() {
		    @Override
		    public void handleMessage(Message msg) {
		        if(msg.what == 0) {
		            mFlag = false;
		        }
		    }
		};
	}
	
	//TODO: 방사패턴 측정기 실행
	public void applan(){
		 Intent intent;
		 String packageName = "com.ybproject.sig_testapp";   
		 PackageManager pm = getPackageManager();
		 //앱이 설치됬는지 확인
	     try {
		   pm.getApplicationInfo(packageName,PackageManager.GET_META_DATA);
		   intent = this.getPackageManager().getLaunchIntentForPackage(packageName);
		   startActivity(intent);
		   Toast.makeText(Grid_mainActivity.this, "방사패턴 측정기를 실행합니다.", Toast.LENGTH_LONG).show();
		  }catch (NameNotFoundException e)
		    {
			  Toast.makeText(Grid_mainActivity.this, "어플리케이션을 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
			   ShowDialog(3);
			}

	}

	/**
	  백키 이벤트를 가로채서 플래그값 확인 후 처리.
	  플래그 값이 true인 상태에서 2초 이내에 백키를 누르면 액티비티 종료.
	**/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//Back Key 처리
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        if(!mFlag) {
	            Toast.makeText(Grid_mainActivity.this, "뒤로가기 버튼을 한번더 터치하시면 종료됩니다", Toast.LENGTH_SHORT).show();
	            mFlag = true;
	            mHandler.sendEmptyMessageDelayed(0, 2000);
	            return false;
	        } else {
	            //어플종료시 실행값 초기화
	            SharedPreferences pass_check = getSharedPreferences("user_password_check" , MODE_PRIVATE);
	            SharedPreferences.Editor ed = pass_check.edit();
	      	  	ed.putInt("user_password_check" , 0 );
	      	  	ed.commit();
	            finish();
	        }
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}
	
	public void mOnClick(View v) {     
		switch (v.getId()) {
		case 1:
//			Intent Analyze = new Intent(Grid_mainActivity.this, Analyze_Activity.class);
//			startActivity(Analyze);
	    	break;
		case 2:
//			Intent Synthesis = new Intent(Grid_mainActivity.this, Synthesis_Activity.class);
//			startActivity(Synthesis);
		break;
		}
	}
	
	// 업데이트 체크 스레드
	private class DownloadText extends Thread {
			public void run() {
		    	StringBuilder text = new StringBuilder();
		    	text.append("");
		    	try{
		    		URL url = new URL(UpdateServer);
		    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		    		if(conn != null)
		    		{
		    			conn.setConnectTimeout(1000); // 1초 동안 인터넷 연결을 실패할경우 Fall 처리
		    			conn.setUseCaches(false);
		    			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
		    				BufferedReader br = new BufferedReader(
		    						new InputStreamReader(conn.getInputStream()));
		    				for(;;){
		    					String line = br.readLine();
		    					if(line == null) break;
		    					text.append(line + "\n");
		    				}
		    				br.close();
		    			}
		    			conn.disconnect();
		    		}
		    		
		    	}
		    	catch(Exception ex){}
		    	
		    	version_result = text.toString();
		    	Log.d("현재 웹 버전코드:",version_result);
			}
	}
	
	    protected Dialog ShowDialog(int id) {
	    	switch (id) {
	    	case 0:
		    		AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
		    		alert2.setTitle("최신버전 발견!");
		    		alert2.setMessage("다운로드 버튼을 눌러 버전을 업데이트해주세요\n안드로이드 마켓으로 이동합니다.");

		    		alert2.setPositiveButton("다운로드", new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int whichButton) {
		    				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ybproject.diarymemo"));
		    				startActivity(intent);
		    			}
		    		});

		    		alert2.setNegativeButton("나중에",
		    				new DialogInterface.OnClickListener() {
		    					public void onClick(DialogInterface dialog, int whichButton) {
		    						Toast.makeText(Grid_mainActivity.this,"업데이트를 취소하였습니다.", Toast.LENGTH_SHORT).show();
		    						return;
		    					}
		    				});
		    		alert2.show();
			    break;
			//와이파이 상태 팝업
	    	case 1:
	    		AlertDialog.Builder alert_wifi = new AlertDialog.Builder(this);
	    		alert_wifi.setTitle("안내");
	    		alert_wifi.setMessage(" ");
	    		// 와이파이 미연결시 신호탐색 버튼
	    		alert_wifi.setPositiveButton("신호탐색", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {

	    				////////////////////////////////////////////////////

	    				////////////////////////////////////////////////////
	    			}
	    		});
	    		
	    		alert_wifi.setNegativeButton("취소",new DialogInterface.OnClickListener() {
	    					public void onClick(DialogInterface dialog, int whichButton) {
	    						finish();
	    						Toast.makeText(Grid_mainActivity.this,"취소하였습니다.\nAPP을 종료합니다", Toast.LENGTH_SHORT).show();
	    						return;
	    					}
	    				});
	    		alert_wifi.show();
	    		break;
	    	//신호가 잡혔을 경우	
	    	case 2:          
	    		AlertDialog.Builder alert_wifi_on = new AlertDialog.Builder(this);
	    		alert_wifi_on.setTitle(" ");
	    		alert_wifi_on.setPositiveButton(" ", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    	            WifiManager wManager; 
	    	            wManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
	    	            WifiInfo wInfo = wManager.getConnectionInfo();    
	    	            
	    			}
	    		});
	    		
	    		alert_wifi_on.setNegativeButton("확인",new DialogInterface.OnClickListener() {
	    					public void onClick(DialogInterface dialog, int whichButton) {
	    						return;
	    					}
	    				});
	    		alert_wifi_on.show();
	    		break;
	    		
	    	case 3:
	    		AlertDialog.Builder alert_not_app = new AlertDialog.Builder(this);
	    		alert_not_app.setTitle("앱 설치가 필요합니다.");
	    		alert_not_app.setMessage("마이크로 스트립 계산기를 사용하기 위해선 설치가 필요합니다.\n설치 하시겠습니까?");
	    		// 와이파이 미연결시 신호탐색 버튼
	    		alert_not_app.setPositiveButton("설치하기", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				//TODO:마켓에 등록되기 전까지 임시로 홈페이지 자료실로 링크
	    				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ybproject.microstripcalculator"));
	    				startActivity(intent);
	    			}
	    		});
	    		
	    		alert_not_app.setNegativeButton("취소",new DialogInterface.OnClickListener() {
	    					public void onClick(DialogInterface dialog, int whichButton) {
	    						return;
	    					}
	    				});
	    		alert_not_app.show();
	    		break;

		    	}
			    
	    	return null;
	    }	
}
