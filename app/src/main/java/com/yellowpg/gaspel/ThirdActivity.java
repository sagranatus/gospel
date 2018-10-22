package com.yellowpg.gaspel;

import android.Manifest;
import android.accounts.AccountManager;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ThirdActivity extends Activity implements EasyPermissions.PermissionCallbacks {
	MemberInfoHelper memberInfoHelper;
	LectioInfoHelper lectioInfoHelper;
	Button step1, step2, step3, step4;
	LinearLayout ll_step0, ll_step1, ll_step2, ll_step3, ll_step4;

	private static final String TAG = "ha";
	Button timebtn, textbtn, sxbtn;
	Button timesetbtn;
	Button stop;
	EditText timeset;
	Editable writeTime;
	String time;
	AlarmManager am;
	TimePicker mTimePicker;

	Button small, normal, big, toobig, man, woman;
	Calendar c1 = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String today = sdf.format(c1.getTime()); //cf : 현재 날짜 구하기
	String hour_str, min_str;

	//cf : 현재 시간과 분을 구한다
	String now_hour = new java.text.SimpleDateFormat("HH").format(new java.util.Date()); //현재 시간
	String now_min = new java.text.SimpleDateFormat("mm").format(new java.util.Date()); //현재 분
	String textsize;

	//cf : 구글 캘린더
	GoogleAccountCredential mCredential;
	Button googlecalon, googlecaloff, cal;
	TextView mOutputText;
	ProgressDialog mProgress;

	static final int REQUEST_ACCOUNT_PICKER = 1000;
	static final int REQUEST_AUTHORIZATION = 1001;
	static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
	static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

	private static final String BUTTON_TEXT = "Call Google Calendar API";
	private static final String PREF_ACCOUNT_NAME = "accountName";
	private static final String[] SCOPES = {CalendarScopes.CALENDAR}; // exp : 읽기만 허용하는 부분 CalendarScopes.CALENDAR_READONLY


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "googlecal");
		setContentView(R.layout.activity_third);

		step1 = (Button) findViewById(R.id.bt_step1);
		step2 = (Button) findViewById(R.id.bt_step2);
		step3 = (Button) findViewById(R.id.bt_step3);
		step4 = (Button) findViewById(R.id.bt_step4);
		ll_step0 = (LinearLayout) findViewById(R.id.ll_step0);
		ll_step1 = (LinearLayout) findViewById(R.id.ll_step1);
		ll_step2 = (LinearLayout) findViewById(R.id.ll_step2);
		ll_step3 = (LinearLayout) findViewById(R.id.ll_step3);
		ll_step4 = (LinearLayout) findViewById(R.id.ll_step4);
		//ll_step0.setVisibility(View.VISIBLE);
		ll_step2.setVisibility(View.VISIBLE);
		step2.setBackgroundColor(Color.parseColor("#210B61"));
		step1.setOnClickListener(listener2);
		step2.setOnClickListener(listener2);
		step3.setOnClickListener(listener2);
		step4.setOnClickListener(listener2);

		cal = (Button) findViewById(R.id.bt_cal);
		googlecalon = (Button) findViewById(R.id.bt_googleon);
		googlecaloff = (Button) findViewById(R.id.bt_googleoff);
		mOutputText = (TextView) findViewById(R.id.tv_googletext);
		mOutputText.setVisibility(mOutputText.GONE);

		// exp : 구글캘린더 연동클릭시 발생하는 이벤트이다
		googlecalon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences sp;
				SharedPreferences.Editor editor;
				sp = getSharedPreferences("setting", 0);
				editor = sp.edit();
				editor.putString("gcal", "on");
				editor.commit();
				if (isGooglePlayServicesAvailable() && isDeviceOnline()) {
					googlecalon.setTextColor(Color.parseColor("#2156a5"));
					googlecaloff.setTextColor(Color.parseColor("#000000"));
				}
				googlecalon.setEnabled(false);
				getResultsFromApi();
				googlecalon.setEnabled(true);

			}
		});
		// exp : 구글캘린더 연동해제 클릭시 발생하는 이벤트이다
		googlecaloff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				googlecaloff.setTextColor(Color.parseColor("#2156a5"));
				googlecalon.setTextColor(Color.parseColor("#000000"));
				SharedPreferences sp;
				SharedPreferences.Editor editor;
				sp = getSharedPreferences("setting", 0);
				editor = sp.edit();
				editor.putString("gcal", "off");
				editor.commit();
			}
		});

		//	mOutputText.setPadding(16, 16, 16, 16);
		//	mOutputText.setVerticalScrollBarEnabled(true);
		//	mOutputText.setMovementMethod(new ScrollingMovementMethod());
		//	mOutputText.setText(
		//			"Click the \'" + BUTTON_TEXT + "\' button to test the API.");
		// activityLayout.addView(mOutputText);

		mProgress = new ProgressDialog(this);
		mProgress.setMessage("Calling Google Calendar API ...");

		//setContentView(activityLayout);

		// 구글 캘린더 연동시 사용
		mCredential = GoogleAccountCredential.usingOAuth2( // exp : 사용자 인증을 얻는 부분
				getApplicationContext(), Arrays.asList(SCOPES))
				.setBackOff(new ExponentialBackOff());
		//	dailyInfoHelper = new MemberInfoHelper(FourthActivity.this);
		//	lectioInfoHelper = new LectioInfoHelper(this);

		timebtn = (Button) findViewById(R.id.bt_time);
		textbtn = (Button) findViewById(R.id.bt_text);
		sxbtn = (Button) findViewById(R.id.bt_sx);

		timesetbtn = (Button) findViewById(R.id.bt_timeset);
		stop = (Button) findViewById(R.id.bt_stop);
		timeset = (EditText) findViewById(R.id.et_timeset);
		mTimePicker = (TimePicker) findViewById(R.id.timePicker);

		// exp : time세팅에 대한 저장 및 해제
		timesetbtn.setOnClickListener(listener);
		stop.setOnClickListener(listener);

		small = (Button) findViewById(R.id.bt_small);
		normal = (Button) findViewById(R.id.bt_normal);
		big = (Button) findViewById(R.id.bt_big);
		toobig = (Button) findViewById(R.id.bt_toobig);
		woman = (Button) findViewById(R.id.bt_woman);
		man = (Button) findViewById(R.id.bt_man);

		// exp : 글씨 크기에 대한 설정
		small.setOnClickListener(textsizelistener);
		normal.setOnClickListener(textsizelistener);
		big.setOnClickListener(textsizelistener);
		toobig.setOnClickListener(textsizelistener);

		woman.setOnClickListener(sxlistener);
		man.setOnClickListener(sxlistener);

		// exp : 알람매니저를 생성
		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		// exp : edittext뷰를 편집하지 못하도록 한다.
		timeset.setFocusable(false);
		timeset.setClickable(false);
		init();

		// exp : 텍스트크기 설정 부분
		SharedPreferences sp = getSharedPreferences("setting", 0);
		textsize = sp.getString("textsize", "");
		if (textsize.equals("small")) {
			timebtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			timeset.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			timesetbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			stop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			textbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			small.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			normal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			big.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			toobig.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			sxbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			woman.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			man.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			cal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			googlecalon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			googlecaloff.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		} else if (textsize.equals("big")) {
			timebtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
			timeset.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			timesetbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			stop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			textbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
			small.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			normal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			big.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			toobig.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			sxbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
			woman.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			man.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			cal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
			googlecalon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			googlecaloff.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
		} else if (textsize.equals("toobig")) {
			timebtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
			timeset.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			timesetbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			stop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			textbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
			small.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			normal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			big.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			toobig.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			sxbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
			woman.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			man.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			cal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
			googlecalon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			googlecaloff.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
		} else {

		}
	}

	// exp : 각각 클릭시에 보이기
	View.OnClickListener listener2 = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ll_step0.setVisibility(View.GONE);
			ll_step1.setVisibility(View.GONE);
			ll_step3.setVisibility(View.GONE);
			ll_step2.setVisibility(View.GONE);
			ll_step4.setVisibility(View.GONE);
			step1.setBackgroundColor(Color.parseColor("#000000"));
			step2.setBackgroundColor(Color.parseColor("#000000"));
			step3.setBackgroundColor(Color.parseColor("#000000"));
			step4.setBackgroundColor(Color.parseColor("#000000"));
			switch (v.getId()) {
				case R.id.bt_step1:
					step1.setBackgroundColor(Color.parseColor("#210B61"));
					ll_step1.setVisibility(View.VISIBLE);
					break;
				case R.id.bt_step2:
					step2.setBackgroundColor(Color.parseColor("#210B61"));
					ll_step2.setVisibility(View.VISIBLE);
					break;
				case R.id.bt_step3:
					step3.setBackgroundColor(Color.parseColor("#210B61"));
					ll_step3.setVisibility(View.VISIBLE);
					break;
				case R.id.bt_step4:
					step4.setBackgroundColor(Color.parseColor("#210B61"));
					ll_step4.setVisibility(View.VISIBLE);
					break;
			}
		}
	};


	// exp : 텍스트사이즈 설정 이벤트
	OnClickListener textsizelistener = new OnClickListener() {
		SharedPreferences sp;
		SharedPreferences.Editor editor;

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
				case R.id.bt_small:
					sp = getSharedPreferences("setting", 0);
					editor = sp.edit();
					editor.putString("textsize", "small");
					editor.commit();
					textsize = sp.getString("textsize", "");
					small.setTextColor(Color.parseColor("#2156a5"));
					normal.setTextColor(Color.parseColor("#000000"));
					big.setTextColor(Color.parseColor("#000000"));
					toobig.setTextColor(Color.parseColor("#000000"));
					timebtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
					timeset.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					timesetbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					stop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					textbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
					small.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					normal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					big.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					toobig.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					sxbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
					woman.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					man.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					cal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
					googlecalon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					googlecaloff.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					break;
				case R.id.bt_big:
					sp = getSharedPreferences("setting", 0);
					editor = sp.edit();
					editor.putString("textsize", "big");
					editor.commit();
					textsize = sp.getString("textsize", "");
					small.setTextColor(Color.parseColor("#000000"));
					normal.setTextColor(Color.parseColor("#000000"));
					big.setTextColor(Color.parseColor("#2156a5"));
					toobig.setTextColor(Color.parseColor("#000000"));
					timebtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
					timeset.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					timesetbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					stop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					textbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
					small.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					normal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					big.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					toobig.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					sxbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
					woman.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					man.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					cal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
					googlecalon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					googlecaloff.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					break;
				case R.id.bt_toobig:
					sp = getSharedPreferences("setting", 0);
					editor = sp.edit();
					editor.putString("textsize", "toobig");
					editor.commit();
					textsize = sp.getString("textsize", "");
					small.setTextColor(Color.parseColor("#000000"));
					normal.setTextColor(Color.parseColor("#000000"));
					big.setTextColor(Color.parseColor("#000000"));
					toobig.setTextColor(Color.parseColor("#2156a5"));
					timebtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
					timeset.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					timesetbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					stop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					textbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
					small.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					normal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					big.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					toobig.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					sxbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
					woman.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					man.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					cal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
					googlecalon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					googlecaloff.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					break;
				case R.id.bt_normal:
					sp = getSharedPreferences("setting", 0);
					editor = sp.edit();
					editor.putString("textsize", "normal");
					editor.commit();
					textsize = sp.getString("textsize", "");
					normal.setTextColor(Color.parseColor("#2156a5"));
					big.setTextColor(Color.parseColor("#000000"));
					small.setTextColor(Color.parseColor("#000000"));
					toobig.setTextColor(Color.parseColor("#000000"));
					timebtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
					timeset.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					timesetbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					stop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					textbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
					small.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					normal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					big.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					toobig.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					sxbtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
					woman.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					man.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					cal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
					googlecalon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					googlecaloff.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					break;
			}
		}

	};


	// exp : 성별 선택 이벤트
	OnClickListener sxlistener = new OnClickListener() {
		SharedPreferences sp;
		SharedPreferences.Editor editor;
		Intent intent;

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
				case R.id.bt_woman:
					sp = getSharedPreferences("setting", 0);
					editor = sp.edit();
					editor.putString("sx", "woman");
					editor.commit();
					textsize = sp.getString("sx", "");
					woman.setTextColor(Color.parseColor("#2156a5"));
					man.setTextColor(Color.parseColor("#000000"));
					break;
				case R.id.bt_man:
					sp = getSharedPreferences("setting", 0);
					editor = sp.edit();
					editor.putString("sx", "man");
					editor.commit();
					textsize = sp.getString("sx", "");
					man.setTextColor(Color.parseColor("#2156a5"));
					woman.setTextColor(Color.parseColor("#000000"));
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
					timesetbtn.setTextColor(Color.parseColor("#2156a5"));
					stop.setTextColor(Color.parseColor("#000000"));
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
					timeset.setText("매일" + hour_str + "시" + min_str + "분에 복음을 전합니다.");
					// cf : 알람을 세팅하는 함수를 불러온다
					oneAlarm();
					break;

				// exp : 알람을 해제하는 경우에 알람매니저에 대한 pendingintent를 제거한다.
				case R.id.bt_stop:
					stop.setTextColor(Color.parseColor("#2156a5"));
					timesetbtn.setTextColor(Color.parseColor("#000000"));
					// TODO Auto-generated method stub
					Intent intent = new Intent(ThirdActivity.this, MainActivity.class);
					PendingIntent pendingIntent = PendingIntent.getActivity(ThirdActivity.this, 0, intent, 0);
					am.cancel(pendingIntent);
					// cf : 세팅값에 time을 비워둔다
					SharedPreferences sp2 = getSharedPreferences("setting", 0);
					SharedPreferences.Editor editor2 = sp2.edit();
					editor2.putString("time", "");
					editor2.commit();
					timeset.setText("");
					timeset.setHint("알람시간을 설정해보세요");
					break;
			}
		}

	};

	// exp : 기존에 있는 값에 대해 알람, 텍스트크기, 성별, 구글연동에 따라 세팅해준다
	private void init() {


		SharedPreferences sp = getSharedPreferences("setting", 0);
		time = sp.getString("time", "");
		if (time != "") {
			timesetbtn.setTextColor(Color.parseColor("#2156a5"));
			int divided = time.indexOf(":");
			String hour_str = time.substring(0, divided);
			String min_str = time.substring(divided + 1);
			timeset.setText("매일" + hour_str + "시" + min_str + "분에 복음을 전합니다.");
		}

		textsize = sp.getString("textsize", "");
		if (textsize != "") {
			if (textsize.equals("small")) {
				small.setTextColor(Color.parseColor("#2156a5"));
			} else if (textsize.equals("normal")) {
				normal.setTextColor(Color.parseColor("#2156a5"));
			} else if (textsize.equals("big")) {
				big.setTextColor(Color.parseColor("#2156a5"));
			} else {
				toobig.setTextColor(Color.parseColor("#2156a5"));
			}
		} else {
			normal.setTextColor(Color.parseColor("#2156a5"));
		}

		String sx = sp.getString("sx", "");
		if (sx != "") {
			if (sx.equals("woman")) {
				woman.setTextColor(Color.parseColor("#2156a5"));
			} else if (sx.equals("man")) {
				man.setTextColor(Color.parseColor("#2156a5"));
			}
		} else {

		}

		String calendarstatus = sp.getString("gcal", "");
		if (calendarstatus != "") {
			if (calendarstatus.equals("on")) {
				googlecalon.setTextColor(Color.parseColor("#2156a5"));

			} else if (calendarstatus.equals("off")) {
				googlecaloff.setTextColor(Color.parseColor("#2156a5"));
			}
		} else {

		}

	}

	// exp : 알람설정에 대한 이벤트이다
	private void oneAlarm() {

		SharedPreferences sp = getSharedPreferences("setting", 0);
		time = sp.getString("time", "");

		// exp : 이 intent str는 사운드를 내기 위해 전달하는 값이다
		Intent intent = new Intent(ThirdActivity.this, MainActivity.class);
		intent.putExtra("str", "value");

		// cf : pending 기능으로 주기적으로 알람을 설정한다
		PendingIntent sender = PendingIntent.getActivity(ThirdActivity.this, 0, intent, 0);

		Date today_date = null;

		try {
			today_date = new SimpleDateFormat("yyyy-MM-dd kk:mm").parse(today + " " + time); // cf : 위에서 설정한 값들을 가져온 후에 Date형식으로 변환
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(ThirdActivity.this, "fail", Toast.LENGTH_LONG).show();
		}

		am.setRepeating(AlarmManager.RTC, today_date.getTime(), 24 * 60 * 60 * 1000, sender); // cf : 주기적 알람 세팅(이는 사운드가 아님)
	}

	;


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_menu_home:
				Intent i0 = new Intent(ThirdActivity.this, MainActivity.class);
				startActivity(i0);
				break;
			case R.id.action_menu_01:
				Intent i = new Intent(ThirdActivity.this, ExplainActivity.class);
				startActivity(i);
				break;
			case R.id.action_menu_02:
				Intent i2 = new Intent(ThirdActivity.this, ThirdActivity.class);
				startActivity(i2);
				break;
			case R.id.action_menu_03:
				Intent i3 = new Intent(ThirdActivity.this, StatusActivity.class);
				startActivity(i3);
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	// exp : 구글캘린더 연동클릭시 불러와지는 메소드이다
	private void getResultsFromApi() { // exp : 인터넷연결, Google Play Service작동, Service사용 계정도 선택하면 MakeRequestTask클래스를 이용하여 서비스를 실행

		if (!isGooglePlayServicesAvailable()) {
			acquireGooglePlayServices();
		} else if (mCredential.getSelectedAccountName() == null) {
			//acquireGooglePlayServices();
			chooseAccount();
			//new MakeRequestTask(ThirdActivity.this, mCredential, mProgress).execute();
		} else if (!isDeviceOnline()) {
			mOutputText.setText("No network connection available.");
			//	new MakeRequestTask(ThirdActivity.this, mCredential, mProgress, mOutputText).execute();
		} else {
			new MakeRequestTask(mCredential, mProgress, mOutputText).execute();
		}
	}

	@AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
	private void chooseAccount() {
		if (EasyPermissions.hasPermissions(
				this, Manifest.permission.GET_ACCOUNTS)) {
			String accountName = getPreferences(Context.MODE_PRIVATE)
					.getString(PREF_ACCOUNT_NAME, null);
			if (accountName != null) {
				mCredential.setSelectedAccountName(accountName);
				SharedPreferences sp_account;
				sp_account = getSharedPreferences("setting", 0);
				SharedPreferences.Editor editor;
				editor = sp_account.edit();
				editor.putString("account", accountName);
				editor.commit();
				getResultsFromApi();
			} else {
				// Start a dialog from which the user can choose an account
				startActivityForResult( // exp : result를 가져오는 intent이다.
						mCredential.newChooseAccountIntent(),
						REQUEST_ACCOUNT_PICKER);
			}
		} else {
			// Request the GET_ACCOUNTS permission via a user dialog
			EasyPermissions.requestPermissions(
					this,
					"This app needs to access your Google account (via Contacts).",
					REQUEST_PERMISSION_GET_ACCOUNTS,
					Manifest.permission.GET_ACCOUNTS);
		}
	}

	/**
	 * Called when an activity launched here (specifically, AccountPicker
	 * and authorization) exits, giving you the requestCode you started it with,
	 * the resultCode it returned, and any additional data from it.
	 *
	 * @param requestCode code indicating which activity result is incoming.
	 * @param resultCode  code indicating the result of the incoming
	 *                    activity result.
	 * @param data        Intent (containing result data) returned by incoming
	 *                    activity result.
	 */
	@Override
	protected void onActivityResult( // exp : intent 결과값 처리하는 부분 -> 결국 성공하면 getResultsFromApi를 불러옴
									 int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_GOOGLE_PLAY_SERVICES:
				if (resultCode != RESULT_OK) {
					mOutputText.setText(
							"This app requires Google Play Services. Please install " +
									"Google Play Services on your device and relaunch this app.");
				} else {
					getResultsFromApi();
				}
				break;
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == RESULT_OK && data != null &&
						data.getExtras() != null) {
					String accountName =
							data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null) {
						SharedPreferences settings =
								getPreferences(Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(PREF_ACCOUNT_NAME, accountName);
						editor.apply();
						mCredential.setSelectedAccountName(accountName);
						getResultsFromApi();
					}
				}
				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == RESULT_OK) {
					getResultsFromApi();
				}
				break;
		}
	}

	/**
	 * Respond to requests for permissions at runtime for API 23 and above.
	 *
	 * @param requestCode  The request code passed in
	 *                     requestPermissions(android.app.Activity, String, int, String[])
	 * @param permissions  The requested permissions. Never null.
	 * @param grantResults The grant results for the corresponding permissions
	 *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(
				requestCode, permissions, grantResults, this);
	}

	/**
	 * Callback for when a permission is granted using the EasyPermissions
	 * library.
	 *
	 * @param requestCode The request code associated with the requested
	 *                    permission
	 * @param list        The requested permission list. Never null.
	 */
	@Override
	public void onPermissionsGranted(int requestCode, List<String> list) {
		// Do nothing.
	}

	/**
	 * Callback for when a permission is denied using the EasyPermissions
	 * library.
	 *
	 * @param requestCode The request code associated with the requested
	 *                    permission
	 * @param list        The requested permission list. Never null.
	 */
	@Override
	public void onPermissionsDenied(int requestCode, List<String> list) {
		// Do nothing.
	}

	/**
	 * Checks whether the device currently has a network connection.
	 *
	 * @return true if the device has a network connection, false otherwise.
	 * exp : 이 아래는 온라인인지 체크하고 구글플레이서비스 가능한지 체크하는 부분이다.
	 */
	private boolean isDeviceOnline() {
		ConnectivityManager connMgr =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	/**
	 * Check that Google Play services APK is installed and up to date.
	 *
	 * @return true if Google Play Services is available and up to
	 * date on this device; false otherwise.
	 */
	private boolean isGooglePlayServicesAvailable() {
		GoogleApiAvailability apiAvailability =
				GoogleApiAvailability.getInstance();
		final int connectionStatusCode =
				apiAvailability.isGooglePlayServicesAvailable(this);
		return connectionStatusCode == ConnectionResult.SUCCESS;
	}

	/**
	 * Attempt to resolve a missing, out-of-date, invalid or disabled Google
	 * Play Services installation via a user dialog, if possible.
	 * exp : 이 부분은 구글플레이서비스 가져오는 부분
	 */
	private void acquireGooglePlayServices() {
		GoogleApiAvailability apiAvailability =
				GoogleApiAvailability.getInstance();
		final int connectionStatusCode =
				apiAvailability.isGooglePlayServicesAvailable(this);
		if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
		}

	}


	/**
	 * Display an error dialog showing that Google Play Services is missing
	 * or out of date.
	 *
	 * @param connectionStatusCode code describing the presence (or lack of)
	 *                             Google Play Services on this device.
	 */
	void showGooglePlayServicesAvailabilityErrorDialog(
			final int connectionStatusCode) {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		Dialog dialog = apiAvailability.getErrorDialog(
				ThirdActivity.this,
				connectionStatusCode,
				REQUEST_GOOGLE_PLAY_SERVICES);
		dialog.show();
	}

	private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
		private com.google.api.services.calendar.Calendar mService = null;
		private Exception mLastError = null;
		GoogleAccountCredential mCredential;
		Context context = ThirdActivity.this;
		MemberInfoHelper dailyInfoHelper;
		LectioInfoHelper lectioInfoHelper;
		Calendar c1;
		ProgressDialog mProgress;
		String day;
		TextView mOutputText;

		MakeRequestTask(GoogleAccountCredential credential, ProgressDialog p, TextView t) {
			this.mOutputText = t;
			//this.context = c;
			this.mCredential = credential;
			this.mProgress = p;
			dailyInfoHelper = new MemberInfoHelper(context);
			lectioInfoHelper = new LectioInfoHelper(context);
			HttpTransport transport = AndroidHttp.newCompatibleTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			mService = new com.google.api.services.calendar.Calendar.Builder(
					transport, jsonFactory, credential)
					.setApplicationName("Google Calendar API Android Quickstart")
					.build();
		}


		@Override
		protected List<String> doInBackground(Void... params) {
			try {
				// exp : 앱 -> 구글 데이터 삽입(오늘의복음)
				try {
					SQLiteDatabase db;
					ContentValues values;
					db = dailyInfoHelper.getReadableDatabase();
					String query = "SELECT comment_con, date, sentence FROM comment";
					Cursor cursor = db.rawQuery(query, null);
					if (cursor != null) {
						while (cursor.moveToNext()) {
							String comment_con = cursor.getString(0);
							String date = cursor.getString(1);
							String sentence = cursor.getString(2);

							int yearsite = date.indexOf("년");
							int monthsite = date.indexOf("월");
							int daysite = date.indexOf("일 ");
							String year = date.substring(0, yearsite);
							String month;
							String day;
							if (date.substring(yearsite + 2, monthsite).length() > 1) {
								month = date.substring(yearsite + 2, monthsite);
							} else {
								month = "0" + date.substring(yearsite + 2, monthsite);
							}
							if (date.substring(monthsite + 2, daysite).length() > 1) {
								day = date.substring(monthsite + 2, daysite);
							} else {
								day = "0" + date.substring(monthsite + 2, daysite);
							}
							new MakeInsertTask(mCredential, year + "-" + month + "-" + day, year + month + day + "aeasaeapj", sentence + "&" + comment_con, "오늘의복음").execute();

						}
					}
					cursor.close();
					dailyInfoHelper.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// exp : 앱 -> 구글 데이터 삽입(렉시오디비나)
				try {
					String bg1_str = null;
					String bg2_str = null;
					String bg3_str = null;
					String sum1_str = null;
					String sum2_str = null;
					String js1_str = null;
					String js2_str = null;

					String date_str = null;
					String onesentence_str = null;
					SQLiteDatabase db2;
					ContentValues values;
					db2 = lectioInfoHelper.getReadableDatabase();
					String query = "SELECT bg1, bg2, bg3, sum1, sum2, js1, js2, date, onesentence FROM lectio";
					Cursor cursor = db2.rawQuery(query, null);

					if (cursor != null) {

						while (cursor.moveToNext()) {
							bg1_str = cursor.getString(0);
							bg2_str = cursor.getString(1);
							bg3_str = cursor.getString(2);
							sum1_str = cursor.getString(3);
							sum2_str = cursor.getString(4);
							js1_str = cursor.getString(5);
							js2_str = cursor.getString(6);
							date_str = cursor.getString(7);
							onesentence_str = cursor.getString(8);
							int yearsite = date_str.indexOf("년");
							int monthsite = date_str.indexOf("월 ");
							int daysite = date_str.indexOf("일 ");
							String year = date_str.substring(0, yearsite);
							String month;
							String day;
							month = date_str.substring(yearsite + 2, monthsite);
							day = date_str.substring(monthsite + 2, daysite);

							new MakeInsertTask(mCredential, year + "-" + month + "-" + day, year + month + day + "aeasaeapj2", onesentence_str + "&" + "이 복음의 등장인물은 " + bg1_str +
									"장소는 " + bg3_str + "시간은 " + bg3_str +
									"이 복음의 내용을 간추리면 " + sum1_str +
									"특별히 눈에 띄는 부분은 " + sum2_str +
									"이 복음에서 보여지는 예수님은 " + js1_str +
									"결과적으로 이 복음을 통해 예수님께서 내게 해주시는 말씀은 " + js2_str, "렉시오디비나").execute();
						}
					}
					cursor.close();
					dailyInfoHelper.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return getDataFromApi();
			} catch (Exception e) {
				mLastError = e;
				cancel(true);
				return null;
			}
		}


		private List<String> getDataFromApi() throws IOException {
			DateTime now = new DateTime(System.currentTimeMillis());
			DateTime __start = new DateTime("2017-07-18T09:00:00-07:00");
			List<String> eventStrings = new ArrayList<String>();
			Events events = mService.events().list("primary")
					.setMaxResults(1000000000)
					.setTimeMin(__start)
					.setOrderBy("startTime")
					.setSingleEvents(true)
					.execute();
			List<Event> items = events.getItems();

			for (Event event : items) {
				DateTime start = event.getStart().getDateTime();
				String date;
				if (start == null) {
					// All-day events don't have start times, so just use
					// the start date.
					start = event.getStart().getDate();

				}

					// exp : 구글 => 어플 캘린더 데이터 가져오기 (오늘의 복음)
						if (event.getSummary().equals("오늘의복음")) {
							Log.d(TAG,"오늘의 복음 들어옴");
						eventStrings.add(
								String.format("%s, %s, (%s)", event.getSummary(), event.getDescription(), start)); // 이런형식으로 데이터를 가져온다
							Log.d(TAG,event.getSummary()+event.getDescription()+start);
						SQLiteDatabase db;
						ContentValues values;

						String timeStr = start.toString();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date date_ = null;
						try {
							date_ = formatter.parse(timeStr); // cf : 이는 String 날짜를 -> 다시 date format으로 변경하여 날짜를 년,월,일 형식으로 바꿔주기 위함이다.
						} catch (ParseException e) {
							e.printStackTrace();
						}
						c1 = Calendar.getInstance();
						c1.setTime(date_);
						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy년 MM월 dd일 ");
						String date_val1 = sdf1.format(c1.getTime());
						date = date_val1 + getDay() + "요일";

						String description = event.getDescription();
						int sentencebefore = description.indexOf("&");
						String sentence = description.substring(0, sentencebefore);
						String comment_con = description.substring(sentencebefore + 1);
						int already = 0;
						try {
							String comment_str = null;
							db = dailyInfoHelper.getReadableDatabase();
							String[] columns = {"comment_con", "date", "sentence"};
							String whereClause = "sentence = ?";
							String[] whereArgs = new String[]{
									sentence
							};
							Cursor cursor = db.query("comment", columns, whereClause, whereArgs, null, null, null);

							while (cursor.moveToNext()) {
								comment_str = cursor.getString(0);
							}
							if (comment_str != null) {
								already = 1;
							}

						} catch (Exception e) {

						}
						if (already == 0) {
							try {
								db = dailyInfoHelper.getWritableDatabase();
								values = new ContentValues();
								values.put("comment_con", comment_con);
								values.put("date", date);
								values.put("sentence", sentence);
								db.insert("comment", null, values);
								dailyInfoHelper.close();
							} catch (Exception e) {
								e.printStackTrace();
							}

						}


					}

					if (event.getSummary().equals("렉시오디비나")) {
						Log.d(TAG,"렉시오디비나 들어옴");
						eventStrings.add(
								String.format("%s, %s, (%s)", event.getSummary(), event.getDescription(), start)); // 이런형식으로 데이터를 가져온다

						String timeStr = start.toString();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date date_ = null;
						try {
							date_ = formatter.parse(timeStr); // cf : 이는 String 날짜를 -> 다시 date format으로 변경하여 날짜를 년,월,일 형식으로 바꿔주기 위함이다.
						} catch (ParseException e) {
							e.printStackTrace();
						}
						c1 = Calendar.getInstance();
						c1.setTime(date_);
						SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy년 MM월 dd일 ");
						String date_val1 = sdf1.format(c1.getTime());
						date = date_val1 + getDay() + "요일";

						String description = event.getDescription();
						int sentence = description.indexOf("&");
						int bg1 = description.indexOf("이 복음의 등장인물은 ");
						int bg2 = description.indexOf("장소는 ");
						int bg3 = description.indexOf("시간은 ");
						int sum1 = description.indexOf("이 복음의 내용을 간추리면 ");
						int sum2 = description.indexOf("특별히 눈에 띄는 부분은 ");
						int js1 = description.indexOf("이 복음에서 보여지는 예수님은 ");
						int js2 = description.indexOf("결과적으로 이 복음을 통해 예수님께서 내게 해주시는 말씀은 ");

						String onesentence1 = description.substring(0, sentence);
						String background1 = description.substring(bg1 + 12, bg2);
						String background2 = description.substring(bg2 + 4, bg3);
						String background3 = description.substring(bg3 + 4, sum1);
						String summary1 = description.substring(sum1 + 15, sum2);
						String summary2 = description.substring(sum2 + 14, js1);
						String jesus1 = description.substring(js1 + 17, js2);
						String jesus2 = description.substring(js2 + 33);

						SQLiteDatabase db2;
						ContentValues values2;
						int already2 = 0;
						// cf : 기존에 값이 있는지 확인 후 있는 경우에는 already값을 1로 준다.
						try {

							String bg1_str = null;
							db2 = lectioInfoHelper.getReadableDatabase();
							String[] columns = {"bg1", "bg2", "bg3", "sum1", "sum2", "js1", "js2", "date", "onesentence"};
							String whereClause = "onesentence = ?";
							String[] whereArgs = new String[]{
									onesentence1
							};
							Cursor cursor = db2.query("lectio", columns, whereClause, whereArgs, null, null, null);

							while (cursor.moveToNext()) {
								bg1_str = cursor.getString(0);
							}
							if (bg1_str != null) {
								already2 = 1;
							}
							cursor.close();
							lectioInfoHelper.close();
						} catch (Exception e) {

						}
						if (already2 == 0) {
							try {
								db2 = lectioInfoHelper.getWritableDatabase();
								values2 = new ContentValues();
								values2.put("bg1", background1);
								values2.put("bg2", background2);
								values2.put("bg3", background3);
								values2.put("sum1", summary1);
								values2.put("sum2", summary2);
								values2.put("js1", jesus1);
								values2.put("js2", jesus2);
								values2.put("date", date);
								values2.put("onesentence", onesentence1);
								db2.insert("lectio", null, values2);
								lectioInfoHelper.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}


			return eventStrings;
		}




		@Override
		protected void onPreExecute() {
			// mOutputText.setText("");
			mProgress.show();
		}

		@Override
		protected void onPostExecute(List<String> output) {
			mProgress.hide();
			if (output == null || output.size() == 0) {
				Toast.makeText(context, "연동에 성공하였습니다", Toast.LENGTH_SHORT).show();
			} else {
				output.add(0, "Data retrieved using the Google Calendar API:");
				mOutputText.setText(TextUtils.join("\n", output));
				Toast.makeText(context, "연동에 성공하였습니다", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void onCancelled() {
			mProgress.hide();
			if (mLastError != null)
				if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
					Toast.makeText(context, "error1", Toast.LENGTH_SHORT).show();
				} else if (mLastError instanceof UserRecoverableAuthIOException) {
					startActivityForResult(
							((UserRecoverableAuthIOException) mLastError).getIntent(),
							ThirdActivity.REQUEST_AUTHORIZATION);

				//	Toast.makeText(context, "error2", Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(context, "연동에 실패하였습니다" + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
					mOutputText.setText("The following error occurred:\n"
							+ mLastError.getMessage());
				}
			else {
				mOutputText.setText("Request cancelled.");
				Toast.makeText(context, "error4", Toast.LENGTH_SHORT).show();
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


}
