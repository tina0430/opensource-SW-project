/**
 * 파일명: DiaryMemoAppManager.java
 * 최종수정: 2012년 3월 25일
 * 수정자: 이용범(top6616@gmail.com)
 * 설명: 아플리케이션의 업데이트 체크 관리
 */
package com.ybproject.diarymemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.ybproject.diarymemo.passwordmanager.EnablePassword;
import com.ybproject.diarymemo.provider.DiaryMemo;
import com.ybproject.diarymemo.provider.DrawNoteDB;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;


public class DiaryMemoAppManager extends Activity {
	public static String ClientID="";
	public static String UpdateServer="";
	public static String UpdateServerImportance="";
	public static final int ANDROID_MARKET = 0;
	public static final int T_STORE = 1;
	public static final int CHECK = ANDROID_MARKET;	// 안드로이드 마켓용
//	public static final int CHECK =  T_STORE;		// 티스토어용
	static String logstring[] = new String[5];
	static String result,result2;
	Handler de;
	/**
	 * @deprecated 네트워크 버전 관리
	 * @sMyVersion 네트워크에서 호출하기 위한 버전, 이 버전으로 네트워크에서 버전을 구별하여 호출한다.
	 *             이버전은 어플리케이션 버전과 따로 관리함( 이 버전이 1이고 네트워크 버전이 1이상일 경우 호출하여 업데이트 경고창을 띄운다 )
	 * @sMyVersion2 sMyVersion2: 업데이트 버전이 필수 업데이트 버전이라면, 네트워크에서 1로 값을 줄경우 어플 실행시마다 경고창이 계속 뜬다
	 */
	/*
	 * TODO : 버전별 지정 버전
	 *  Ver0.6.0 - sMyVersion 1
	 *  Ver0.6.1 - sMyVersion 2
	 *  Ver0.6.2 - sMyVersion 3
	 *  Ver0.6.3 - sMyVersion 4
	 *  Ver1.0.0 - sMyVersion 10
	 *  Ver1.0.1 - sMyVersion 11
	 *  Ver1.0.2 - sMyVersion 12
	 *  Ver1.0.3 - sMyVersion 13
	 *  Ver1.0.4 - sMyVersion 14
	 *  Ver1.0.5 - sMyVersion 15 (2012-09-03)
	 *  Ver1.0.6 - sMyVersion 16 (2012-10-05)
	 *  Ver1.0.7 - sMyVersion 17 (2012-10-06)
	 *  Ver1.0.8 - sMyVersion 18 (2012-10-12)
	 *  Ver1.0.80.1 - sMyVersion 19 (2012-11-24)
	 *  Ver1.0.80.2 - sMyVersion 20 (2012-12-05)
	 *  Ver1.5.00.0 - sMyVersion 50 (2013-06-10)
	 *  Ver1.5.01.0 - sMyVersion 50 (2013-06-19)
	 *  Ver1.5.02.0 - sMyVersion 52 (2013-08-18)
	 */
    public static double sMyVersion = 52;	// 어플 버전 체크
    public static double sMyVersion2 = 1;	// 필수 설치 체크( 이게 1일경우 업데이트를 안하면 사용 불가,이는 서버와 코드가 맞아야함 )

    @Override
    public void onCreate(Bundle savedInstanceState) {
//    	AActivity = DiaryMemoAppManager.this;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.loding);
        
 		//폴더 체크( 없으면 생성함 )
        final File mDirectory;
   	 	File root = Environment.getExternalStorageDirectory();
		mDirectory=new File(root.getAbsolutePath() + "/DiaryNotepad");
		mDirectory.mkdirs();
        
		SharedPreferences prefs = getSharedPreferences("update_check",MODE_PRIVATE);
   		SharedPreferences preferences_fastmode = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences pass_check = getSharedPreferences("user_password_check" , 0);
   		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
   		boolean FastMode = preferences_fastmode.getBoolean("fastmode", false);
   		boolean Password = preferences.getBoolean("enable_password", false);
   		
   		
        //빠른실행 모드
   		if(FastMode == true ){
   			if(prefs.getInt("welcon_news", 0) != 1){ 
   				ShowDialog(2);	
   			}else{
   			if(Password == true){
   		   		//암호인증을 했을경우 어플리케이션 실행
   		   			if(pass_check.getInt("user_password_check", 0) == 1){
   		   				initialize();
   		   			}else{
   		   	   			Intent intent = new Intent(DiaryMemoAppManager.this, EnablePassword.class);
   		   	            startActivity(intent);
   		   	            finish();
   		   	   		}
   		   		}else{
   		   		if(prefs.getInt("welcon_news", 0) != 1){ 
   		   			ShowDialog(2);	
   		   		}else{
   		   			initialize();
   		   		 }
   		   	   }
   			}
   		}else{ //빠른실행 모드가 아닐경우
   			setContentView(R.layout.loding);
	   		if(Password == true){
	   		//암호인증을 했을경우 어플리케이션 실행
	   			if(pass_check.getInt("user_password_check", 0) == 1){
	   				initialize();
	   			}else{
	   	   			Intent intent = new Intent(DiaryMemoAppManager.this, EnablePassword.class);
	   	            startActivity(intent);
	   	            finish();
	   	   		}
	   		}else{
		        int sdk_version = Build.VERSION.SDK_INT;  // 4.0 에서 .SDK -> SDK_INT 로 바낌
		        String di_version=Build.VERSION.RELEASE;
		        Log.d("User OS Version: ", di_version);
		        int min_version = 11;
		        
		        if(sdk_version >= min_version){
		        	// 업데이트 체크 스레드 생성
		            DownloadText_4 downloadtext = new DownloadText_4();
		        	downloadtext.start();
		        	
		            try{
		    			if(prefs.getInt("welcon_news", 0) != 1){
		    				Toast.makeText(DiaryMemoAppManager.this, R.string.cash_warning, Toast.LENGTH_SHORT).show();
		    				ShowDialog(2);
		    			}else{
		    				if(Double.parseDouble(result) <= sMyVersion)   
		    				{
		    						Toast.makeText(DiaryMemoAppManager.this, R.string.last_version, Toast.LENGTH_SHORT).show();
		    						 initialize();
		    				}else if(Double.parseDouble(result) > sMyVersion){
		    					//처음 설치인지 체크
		    					if(Double.parseDouble(result2) == sMyVersion2){
		    						ShowDialog(0);
		    					}else{
		    						if(prefs.getInt("update_check", 0) != 1){
		    							ShowDialog(1);
		    						}else{
		    							Toast.makeText(DiaryMemoAppManager.this, R.string.old_version, Toast.LENGTH_SHORT).show();
		    							initialize();
		    						}
		    					}
		    				}
		    			}
		
		    		// 인터넷에 연결되있지 않은 경우
		            }catch(Exception ex){Log.d("Update Check:","Error  -100 network connection failed"); initialize(); }
		        }else{
		            try{
		            	//TODO: 호스팅 만료료 인하여 다음버전부터 체크서버 변경
		            	final String text = DownloadText_old(UpdateServer);
		            	final String text2 = DownloadText_old(UpdateServerImportance);

		    				if(Double.parseDouble(text) <= sMyVersion)   
		    				{
		    					if(prefs.getInt("welcon_news", 0) != 1){
		    						Toast.makeText(DiaryMemoAppManager.this, R.string.cash_warning, Toast.LENGTH_SHORT).show();
		    						ShowDialog(2);
		    					}else{
		    						Toast.makeText(DiaryMemoAppManager.this, R.string.last_version, Toast.LENGTH_SHORT).show();
		    						 initialize();
		    					}
		    	
		    				}else if(Double.parseDouble(text) > sMyVersion){
		    					//처음 설치인지 체크
		    					if(Double.parseDouble(text2) == sMyVersion2){
		    						ShowDialog(0);
		    					}else{
		    						if(prefs.getInt("update_check", 0) != 1){
		    							ShowDialog(1);
		    						}else{
		    							Toast.makeText(DiaryMemoAppManager.this, R.string.old_version, Toast.LENGTH_SHORT).show();
		    							initialize();
		    						}
		    					}
		    				}
		
		    		// 인터넷에 연결되있지 않은 경우
		            }catch(Exception ex){Log.d("Update Check:","Error  -100 network connection failed"); initialize(); }
		        }
	   	 }//Password Check close
   	}//FastMode Close
} //onCreate Close

	static String DownloadText_old(String addr)
    {
    	StringBuilder text = new StringBuilder();
    	text.append("");
    	try{
    		URL url = new URL(addr);
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
    	return text.toString();
    }
    
	 public class DownloadText_4 extends Thread {
			public void run() {
		    	StringBuilder text = new StringBuilder();
		    	StringBuilder text2 = new StringBuilder();
		    	text.append("");
		    	text2.append("");
		    	try{
		    		URL url = new URL(UpdateServer);
		    		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		    		if(conn != null)
		    		{
		    			conn.setConnectTimeout(10000); // 1초 동안 인터넷 연결을 실패할경우 Fall 처리
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
		    		result = text.toString();
		    		
		    		URL url2 = new URL(UpdateServerImportance);
		    		HttpURLConnection conn2 = (HttpURLConnection)url2.openConnection();
		    		if(conn2 != null)
		    		{
		    			conn2.setConnectTimeout(10000); // 1초 동안 인터넷 연결을 실패할경우 Fall 처리
		    			conn2.setUseCaches(false);
		    			if(conn2.getResponseCode() == HttpURLConnection.HTTP_OK){
		    				BufferedReader br = new BufferedReader(
		    						new InputStreamReader(conn2.getInputStream()));
		    				for(;;){
		    					String line = br.readLine();
		    					if(line == null) break;
		    					text2.append(line + "\n");
		    				}
		    				br.close();
		    			}
		    			conn2.disconnect();
		    		}
		    		result2 = text2.toString();
		    		
		    	}
		    	catch(Exception ex){}
//		    	result2 = text2.toString();
//		    	result = text.toString();
//		    	Log.d("버전체크:","최신버전 체크 완료");
			}
	}
	
	protected Dialog ShowDialog(int id) {
		   
	    	switch (id) {
	    		// 필수 업데이트일 경우
		    	case 0:
		    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		    		alert.setTitle(R.string.update_title);
		    		alert.setMessage(R.string.update_new_im);

		    		// Set an EditText view to get user input
		    		alert.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int whichButton) {
		    				if(CHECK == ANDROID_MARKET){
		    				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ybproject.diarymemo"));
		    				startActivity(intent);
		    				}else if(CHECK == T_STORE){
		    					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tstore://PRODUCT_VIEW/0000276446/0"));
		    					startActivity(intent);
		    				}
		    			}
		    		});
		    		alert.setNegativeButton(R.string.dialog_cancel,
		    				new DialogInterface.OnClickListener() {
		    					public void onClick(DialogInterface dialog, int whichButton) {
		    						Toast.makeText(DiaryMemoAppManager.this,R.string.update_cancel, Toast.LENGTH_SHORT).show();
		    						finish();
		    					}
		    				});
		    		alert.show(); 
		    		break;
		    	// 필수 업데이트가 아닐 경우
		    	case 1:
		    		AlertDialog.Builder alert2 = new AlertDialog.Builder(this);
		    		alert2.setTitle(R.string.update_title);
		    		alert2.setMessage(R.string.update_new);

		    		// Set an EditText view to get user input
		    		alert2.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int whichButton) {
		    				if(CHECK == ANDROID_MARKET){
		    				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ybproject.diarymemo"));
		    				startActivity(intent);
		    				}else if(CHECK == T_STORE){
		    					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tstore://PRODUCT_VIEW/0000276446/0"));
		    					startActivity(intent);
		    				}
		    			}
		    		});

		    		alert2.setNegativeButton(R.string.dialog_cancel,
		    				new DialogInterface.OnClickListener() {
		    					public void onClick(DialogInterface dialog, int whichButton) {
		    						// 아니오를 클릭하면 다시는 안띄움
		    						SharedPreferences prefs = getSharedPreferences("update_check" , MODE_PRIVATE);
		    						SharedPreferences.Editor ed = prefs.edit();
		    						ed.putInt("update_check" , 1 );
		    						ed.commit();
		    						Toast.makeText(DiaryMemoAppManager.this,R.string.update_cancel, Toast.LENGTH_SHORT).show();
		    						initialize();
		    					}
		    				});
		    		alert2.show();
		    		break;
		    	// 업데이트 내역 안내
		    	case 2:
		    		AlertDialog.Builder alert3 = new AlertDialog.Builder(this);
		    		alert3.setTitle(R.string.update_title);
		    		alert3.setMessage(R.string.chang_log);

		    		// Set an EditText view to get user input
		    		alert3.setPositiveButton(R.string.dialog_confirm , new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int whichButton) {
    						SharedPreferences prefs = getSharedPreferences("update_check" , MODE_PRIVATE);
    						SharedPreferences.Editor ed = prefs.edit();
    						ed.putInt("welcon_news" , 1 );
    						ed.commit();
    						initialize();
		    			}
		    		});

		    		alert3.show();
		    		break;
		    		
		    	case 3:
		    		AlertDialog.Builder alert4 = new AlertDialog.Builder(this);
		    		alert4.setTitle(R.string.update_title);
		    		alert4.setMessage(R.string.last_version);

		    		// Set an EditText view to get user input
		    		alert4.setPositiveButton(R.string.dialog_confirm , new DialogInterface.OnClickListener() {
		    			public void onClick(DialogInterface dialog, int whichButton) {
		    			}
		    		});

		    		alert4.show();
		    		break;
	    	}
	    	
	    	return null;
	    }
	   
	    private void initialize()
	    {   	
			SharedPreferences grid_pref = PreferenceManager.getDefaultSharedPreferences(this);
	    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	    	boolean Start = preferences.getBoolean("StartType", false);
			boolean Thema = grid_pref.getBoolean("grid_Thema_main", true);
	   		
	    	final Intent intent;
	    	
	    	//그리드형 메인테마 사용할건지
			if(Thema == true){
				intent = new Intent(this, Grid_mainActivity.class);
			}else{
		        //일반메모를 먼저 노출할건지, 그리기메모를 노출할건지..
		    	if(Start == true){
		    		intent = new Intent(this, DrawNoteList.class);
		    	}else{
		    		intent = new Intent(this, DiaryMemoList.class);
		    	}

			}
	        Handler handler =    new Handler()
	                             {
	                                 @Override
	                                 public void handleMessage(Message msg)
	                                 {
	                                	 startActivity(intent);
	                                     finish();	// 액티비티 종료
	                                 }
	                             };
	        handler.sendEmptyMessageDelayed(0, 300);	//ms, 300ms후 종료
	    }
	    
	    // 그리기 메모 초기화 작업
		public void moveFile(){
			SharedPreferences prefs = getSharedPreferences("mypref", 0);
			boolean isMove = prefs.getBoolean("flag", false);
			
			if(!isMove){
				DrawNoteDB dbCon = new DrawNoteDB(this);
				dbCon.moveFile();
				
				Log.d("Init", "Draw Note DB Check..");
				
				SharedPreferences.Editor editor = getSharedPreferences("mypref", 0).edit();
				editor.putBoolean("flag", true);
				editor.commit();
			}
		}
}