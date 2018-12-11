package com.yellowpg.gaspel;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.server.Server_CommentData;
import com.yellowpg.gaspel.server.Server_LectioData;
import com.yellowpg.gaspel.server.Server_WeekendData;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hirondelle.date4j.DateTime;


// setting 설정 페이지
public class SettingActivity extends AppCompatActivity {
	LinearLayout ll_step1, ll_step2, ll_step3;
	private SessionManager session;
	String uid = null;

	private static final String TAG = "ha";
	Button timebtn, textbtn, sxbtn;
	Button timesetbtn;
	Button stop;
	Button timeset;
	Button dataset;
	Editable writeTime;
	String time;
	AlarmManager am;
	TimePicker mTimePicker;
	String day;

	Button normal, big;
	Calendar c1 = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String today = sdf.format(c1.getTime()); //cf : 현재 날짜 구하기
	String hour_str, min_str;

	//cf : 현재 시간과 분을 구한다
	String now_hour = new java.text.SimpleDateFormat("HH").format(new java.util.Date()); //현재 시간
	String now_min = new java.text.SimpleDateFormat("mm").format(new java.util.Date()); //현재 분
	String textsize;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting);
		session = new SessionManager(getApplicationContext());
		uid = session.getUid();

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();

//actionbar setting
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar_setting);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
        actionbar.setElevation(0);
        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

		ll_step1 = (LinearLayout) findViewById(R.id.ll_step1);
		ll_step2 = (LinearLayout) findViewById(R.id.ll_step2);
		ll_step3 = (LinearLayout) findViewById(R.id.ll_step3);
		//ll_step0.setVisibility(View.VISIBLE);

		//	mOutputText.setPadding(16, 16, 16, 16);
		//	mOutputText.setVerticalScrollBarEnabled(true);
		//	mOutputText.setMovementMethod(new ScrollingMovementMethod());
		//	mOutputText.setText(
		//			"Click the \'" + BUTTON_TEXT + "\' button to test the API.");
		// activityLayout.addView(mOutputText);


		//setContentView(activityLayout);


		timebtn = (Button) findViewById(R.id.bt_time);
		textbtn = (Button) findViewById(R.id.bt_text);

		timesetbtn = (Button) findViewById(R.id.bt_timeset);
		stop = (Button) findViewById(R.id.bt_stop);
		timeset = (Button) findViewById(R.id.et_timeset);
		dataset = (Button) findViewById(R.id.bt_setdata);
 		mTimePicker = (TimePicker) findViewById(R.id.timePicker);
		dataset.setBackgroundResource(R.drawable.button_bg2);
		// exp : time세팅에 대한 저장 및 해제
		timesetbtn.setOnClickListener(listener);
		stop.setOnClickListener(listener);
		dataset.setOnClickListener(listener);

		normal = (Button) findViewById(R.id.bt_normal);
		big = (Button) findViewById(R.id.bt_big);

		// exp : 글씨 크기에 대한 설정

		normal.setOnClickListener(textsizelistener);
		big.setOnClickListener(textsizelistener);
		normal.setBackgroundResource(R.drawable.button_bg_grey);
		big.setBackgroundResource(R.drawable.button_bg_grey);

		// exp : 알람매니저를 생성
		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		// exp : edittext뷰를 편집하지 못하도록 한다.
		timeset.setFocusable(false);
		timeset.setClickable(false);
		timeset.setBackgroundResource(R.drawable.button_bg_white);
		init();
		ll_step3.setVisibility(View.GONE);
	/*	if(uid == null || uid == ""){
			ll_step3.setVisibility(View.GONE);

		} */
		// 텍스트크기 설정 부분
		SharedPreferences sp = getSharedPreferences("setting", 0);
		textsize = sp.getString("textsize", "");
		if (textsize.equals("big")) {
		//	timebtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		//	timeset.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		//	timesetbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		//	stop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		//	textbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		//	normal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		//	big.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		} else {

		}
	}



	// exp : 텍스트사이즈 설정 이벤트
	OnClickListener textsizelistener = new OnClickListener() {
		SharedPreferences sp;
		SharedPreferences.Editor editor;

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

				case R.id.bt_big:
					sp = getSharedPreferences("setting", 0);
					editor = sp.edit();
					editor.putString("textsize", "big");
					editor.commit();
					textsize = sp.getString("textsize", "");
					big.setBackgroundResource(R.drawable.button_bg2);
					normal.setBackgroundResource(R.drawable.button_bg_grey);
				//	timebtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
				//	timeset.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
				//	timesetbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				//	stop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				//	textbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
				//	normal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				//	big.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);

					break;

				case R.id.bt_normal:
					sp = getSharedPreferences("setting", 0);
					editor = sp.edit();
					editor.putString("textsize", "normal");
					editor.commit();
					textsize = sp.getString("textsize", "");
					normal.setBackgroundResource(R.drawable.button_bg2);
					big.setBackgroundResource(R.drawable.button_bg_grey);
					timebtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					timeset.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					timesetbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
					stop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
					textbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					normal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
					big.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
					break;
			}
		}

	};


	// exp : 시간 설정에 대한 이벤트
	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.bt_timeset:
					// TODO Auto-generated method stub
					timesetbtn.setBackgroundResource(R.drawable.button_bg2);
					stop.setBackgroundResource(R.drawable.button_bg_grey);
					// cf : Sharedpreference data를 생성하고 저장한다.
					SharedPreferences sp = getSharedPreferences("setting", 0);
					SharedPreferences.Editor editor = sp.edit();

					// cf : timePicker에 설정한 시간과 분 값을 가져온다.
					Integer hour = mTimePicker.getCurrentHour();
					Integer min = mTimePicker.getCurrentMinute();

					// cf : 이를 string화 해준뒤에 time값에 저장한다
					hour_str = String.valueOf(hour);
					min_str = String.valueOf(min);
					time = hour_str + ":" + min_str;
					editor.putString("time", time);
					editor.commit();

					// cf : 다시 원래값을 today에 넣어준다. (아래 코드 실행시 초기화)
					c1 = Calendar.getInstance();
					today = sdf.format(c1.getTime());

					// cf : 시간,분을 비교해서 더 이전을 설정하는 경우에 date를 하루 늘려서 저장한다.
					if (Integer.parseInt(now_hour) > hour) {
						c1 = Calendar.getInstance();
						today = sdf.format(c1.getTime());
						c1.add(Calendar.DATE, 1); // cf : 현재 날짜에서 하루 더하기!
						today = sdf.format(c1.getTime()); // cf : 현재 날짜 구하기
					} else if (Integer.parseInt(now_hour) == hour) {
						if (Integer.parseInt(now_min) > min) {
							c1.add(Calendar.DATE, 1);
							today = sdf.format(c1.getTime());
						} else {
							c1 = Calendar.getInstance();
							today = sdf.format(c1.getTime());
						}

					} else {
						c1 = Calendar.getInstance();
						today = sdf.format(c1.getTime());
					}

					// cf : edittext뷰에 설정한 시간을 알려주는 텍스트를 출력한다.
					timeset.setText(hour_str + "시 " + min_str + "분");
					// cf : 알람을 세팅하는 함수를 불러온다
					oneAlarm();
					break;

				// exp : 알람을 해제하는 경우에 알람매니저에 대한 pendingintent를 제거한다.
				case R.id.bt_stop:
					stop.setBackgroundResource(R.drawable.button_bg2);
					timesetbtn.setBackgroundResource(R.drawable.button_bg_grey);
					// TODO Auto-generated method stub
					Intent intent = new Intent(SettingActivity.this, FirstActivity.class);
					PendingIntent pendingIntent = PendingIntent.getActivity(SettingActivity.this, 0, intent, 0);
					am.cancel(pendingIntent);
					// cf : 세팅값에 time을 비워둔다
					SharedPreferences sp2 = getSharedPreferences("setting", 0);
					SharedPreferences.Editor editor2 = sp2.edit();
					editor2.putString("time", "");
					editor2.commit();
					timeset.setText("");
					timeset.setHint("");
					break;
				case R.id.bt_setdata:
			/*		CommentInfoHelper commentInfoHelper = new CommentInfoHelper(SettingActivity.this);
					SQLiteDatabase db;
					ContentValues values;

					try{
						db = commentInfoHelper.getReadableDatabase();
						String query = "SELECT comment_con, date, sentence FROM comment";
						Cursor cursor = db.rawQuery(query, null);

						while(cursor.moveToNext()){
							String comment_con = cursor.getString(0);
							String date = cursor.getString(1);
							String sentence = cursor.getString(2);
							if(uid != null){
								Log.d("saea", uid+date+sentence+comment_con);
								Server_CommentData.insertComment(SettingActivity.this, uid, date, sentence, comment_con);
							}
						}
						cursor.close();
						commentInfoHelper.close();
					}catch(Exception e){
						e.printStackTrace();
					}

					// 렉시오디비나
					LectioInfoHelper lectioInfoHelper = new LectioInfoHelper(SettingActivity.this);
					SQLiteDatabase db2;

					try{
						String bg1 = null;
						String bg2 = null;
						String bg3 = null;
						String sum1 = null;
						String sum2 = null;
						String js1 = null;
						String js2 = null;

						String date = null;
						String onesentence = null;
						db2 = lectioInfoHelper.getReadableDatabase();
						String query = "SELECT bg1, bg2, bg3, sum1, sum2, js1, js2, date, onesentence FROM lectio";
						Cursor cursor = db2.rawQuery(query, null);

						while(cursor.moveToNext()){
							bg1 = cursor.getString(0);
							bg2 = cursor.getString(1);
							bg3 = cursor.getString(2);
							sum1 = cursor.getString(3);
							sum2 = cursor.getString(4);
							js1 = cursor.getString(5);
							js2 = cursor.getString(6);
							date = cursor.getString(7);
							onesentence = cursor.getString(8);
							if(uid != null){
								Log.d("saea", uid+date+onesentence+bg1);
								Lectio lectio = new Lectio(date, onesentence, bg1, bg2, bg3, sum1, sum2, js1, js2);
								Server_LectioData.insertLectio(SettingActivity.this, uid, lectio);
							}
						}

						cursor.close();
						lectioInfoHelper.close();
					}
					catch(Exception e){

					}
					// weekend 데이터
					WeekendInfoHelper weekendInfoHelper = new WeekendInfoHelper(SettingActivity.this);
					ContentValues values3;
					SQLiteDatabase db3;

					try{
						db3 = weekendInfoHelper.getReadableDatabase();
						String query = "SELECT date, mysentence, mythought FROM weekend";
						Cursor cursor = db3.rawQuery(query, null);
						String weekend_str = null;
						while(cursor.moveToNext()) {
							weekend_str = cursor.getString(0);
							String date = cursor.getString(0);
							String mysentence = cursor.getString(1);
							String mythought = cursor.getString(2);
							if(uid != null){
								if(mythought == null){
									mythought = "";
								}
								Log.d("saea", uid+date+mysentence+mythought);
								Server_WeekendData.insertWeekend(SettingActivity.this, uid, date, mysentence, mythought);
							}
						}

						cursor.close();
						weekendInfoHelper.close();
					}catch(Exception e){
						e.printStackTrace();
					}
					Toast.makeText(SettingActivity.this, "데이터가 성공적으로 전송되었습니다.", Toast.LENGTH_SHORT).show();
					*/


					break;
			}
		}

	};

	// exp : 기존에 있는 값에 대해 알람, 텍스트크기, 성별, 구글연동에 따라 세팅해준다
	private void init() {


		SharedPreferences sp = getSharedPreferences("setting", 0);
		time = sp.getString("time", "");
		if (time != "") {
			timesetbtn.setBackgroundResource(R.drawable.button_bg2);
			stop.setBackgroundResource(R.drawable.button_bg_grey);
			int divided = time.indexOf(":");
			String hour_str = time.substring(0, divided);
			String min_str = time.substring(divided + 1);
			timeset.setText(hour_str + "시 " + min_str + "분");
		}else{
			stop.setBackgroundResource(R.drawable.button_bg2);
			timesetbtn.setBackgroundResource(R.drawable.button_bg_grey);
		}

		textsize = sp.getString("textsize", "");
		if (textsize != "") {
			if (textsize.equals("normal")) {
				normal.setBackgroundResource(R.drawable.button_bg2);
				big.setBackgroundResource(R.drawable.button_bg_grey);
			} else if (textsize.equals("big")) {
				big.setBackgroundResource(R.drawable.button_bg2);
				normal.setBackgroundResource(R.drawable.button_bg_grey);
			}
		} else {
			normal.setBackgroundResource(R.drawable.button_bg2);
			big.setBackgroundResource(R.drawable.button_bg_grey);
		}

	}

	// exp : 알람설정에 대한 이벤트이다
	private void oneAlarm() {

		SharedPreferences sp = getSharedPreferences("setting", 0);
		time = sp.getString("time", "");

		// exp : 이 intent str는 사운드를 내기 위해 전달하는 값이다
		Intent intent = new Intent(SettingActivity.this, FirstActivity.class);
		intent.putExtra("str", "value");

		// cf : pending 기능으로 주기적으로 알람을 설정한다
		PendingIntent sender = PendingIntent.getActivity(SettingActivity.this, 0, intent, 0);

		Date today_date = null;

		try {
			today_date = new SimpleDateFormat("yyyy-MM-dd kk:mm").parse(today + " " + time); // cf : 위에서 설정한 값들을 가져온 후에 Date형식으로 변환
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(SettingActivity.this, "fail", Toast.LENGTH_LONG).show();
		}

		am.setRepeating(AlarmManager.RTC, today_date.getTime(), 24 * 60 * 60 * 1000, sender); // cf : 주기적 알람 세팅(이는 사운드가 아님)
	}




	// 커스텀 다이얼로그 선택시
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			default:
				finish();
				return true;
		}
	}




	public String getDay() {
			int dayNum = c1.get(Calendar.DAY_OF_WEEK);

			switch (dayNum) {
				case 1:
					day = "일";
					break;
				case 2:
					day = "월";
					break;
				case 3:
					day = "화";
					break;
				case 4:
					day = "수";
					break;
				case 5:
					day = "목";
					break;
				case 6:
					day = "금";
					break;
				case 7:
					day = "토";
					break;

			}
			return day;
		}


}
