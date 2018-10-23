package com.yellowpg.gaspel;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.yellowpg.gaspel.DB.MemberInfoHelper;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.BaseActivity;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.Fonttype;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.googlesync.MakeInsertTask;
import com.yellowpg.gaspel.googlesync.MakeUpdateTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;
public class MainActivity extends BaseActivity
		implements EasyPermissions.PermissionCallbacks {
	final static String TAG = "MainActivity";
	Button btnNetCon;
	Button btnNetCon2;
	Button comment_save;
	TextView tv;
	TextView tv2;
	Button date, start, end;
	ImageButton before, after;
	static String day;
	String textsize;
	EditText comment;

	BottomNavigationView bottomNavigationView;
	ListSelectorDialog dlg_left;
	String[] listk_left, listv_left;

	int already = 0;
	private MediaPlayer mMediaPlayer;
	InputMethodManager imm;
	LinearLayout ll, ll_date;
	MemberInfoHelper memberInfoHelper;

	static Calendar c1 = Calendar.getInstance();
	//현재 해 + 달 구하기
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	String date_val = sdf.format(c1.getTime());
	//현재 해 + 달 구하기
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy년 MM월 dd일 ");
	String date_val1 = sdf1.format(c1.getTime());

	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	String date_val2 = sdf2.format(c1.getTime());


	// exp : 묵상하기 부분
	private CountDownTimer countDownTimer;
	CountDownTimer countDownTimer2;
	final int MILLISINFUTURE = 11*1000;
	final int COUNT_DOWN_INTERVAL = 1000;
	final int MILLISINFUTURE2 = 21*1000;
	Toast unlockMessage;
	Toast unlockMessage2;
    Toast last;
	MediaPlayer mediaPlayer;

    ViewGroup group;
    TextView messageTextView;

	// exp : 알람 연관된 부분
	private static PowerManager.WakeLock myWakeLock;

		// exp : 구글 캘린더 연동 부분이다.
		GoogleAccountCredential mCredential;
	//	private TextView mOutputText;
	//	ProgressDialog mProgress;

	//	static final int REQUEST_ACCOUNT_PICKER = 1000;
	//	static final int REQUEST_AUTHORIZATION = 1001;
	//	static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
	//	static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

	//	private static final String BUTTON_TEXT = "Call Google Calendar API";
	//	private static final String PREF_ACCOUNT_NAME = "accountName";
		private static final String[] SCOPES = { CalendarScopes.CALENDAR }; // exp : 읽기만 허용하는 부분 CalendarScopes.CALENDAR_READONLY

	@SuppressLint("InvalidWakeLockTag")
	protected void onCreate(Bundle savedInstanceState){

		// exp : 인터넷연결 확인
		ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		// exp : 구글캘린더 연동 부분
		mCredential = GoogleAccountCredential.usingOAuth2( // exp : 사용자 인증을 얻는 부분
				getApplicationContext(), Arrays.asList(SCOPES))
				.setBackOff(new ExponentialBackOff());

		SharedPreferences sp_account = getSharedPreferences("setting",0);
		String ac = sp_account.getString("account", "");
		mCredential.setSelectedAccountName(ac);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		android.support.v7.app.ActionBar actionbar = getSupportActionBar();

//actionbar setting
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setCustomView(R.layout.actionbar);
		TextView mytext = (TextView) findViewById(R.id.mytext);
		Fonttype.setFont( "Billabong",MainActivity.this, mytext);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

		// actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeAsUpIndicator(R.drawable.list);
		ll = (LinearLayout) findViewById(R.id.ll);
		ll_date = (LinearLayout) findViewById(R.id.ll_date);

		// date
		date = (Button) findViewById(R.id.bt_date);
		before = (ImageButton) findViewById(R.id.bt_beforedate);
		after = (ImageButton) findViewById(R.id.bt_afterdate);

		// 복음 묵상하기
		start = (Button) findViewById(R.id.bt_start);
		end = (Button) findViewById(R.id.bt_end);
		start.setVisibility(start.GONE);
		end.setVisibility(end.GONE);

		// 복음 제목, 내용
		tv = (TextView) findViewById(R.id.tv_01);
		btnNetCon = (Button) findViewById(R.id.bt_network_con);

		// 말씀 새기기 제목, 내용, 코멘트
		tv2 = (TextView) findViewById(R.id.tv_02);
		btnNetCon2 = (Button) findViewById(R.id.bt_network_con2);
		comment = (EditText) findViewById(R.id.et_comment);
		comment_save = (Button) findViewById(R.id.bt_comment);

		comment.setVisibility(comment.GONE);
		comment_save.setVisibility(comment_save.GONE);

		//exp : 요일 앞뒤로 변경하는 소스
		date.setText(date_val1+getDay()+"요일"); // "yyyy년 MM월 dd일 "
		before.setOnClickListener(listener_date);
		after.setOnClickListener(listener_date);

		// bottomnavigation 뷰 등록
		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
		BottomNavigationViewHelper.disableShiftMode2(bottomNavigationView);

		Menu menu = bottomNavigationView.getMenu();
		MenuItem menuItem_1 = menu.getItem(0);
		MenuItem menuItem_2 = menu.getItem(1);
		MenuItem menuItem_3 = menu.getItem(2);
		MenuItem menuItem_4 = menu.getItem(3);
		menuItem_1.setChecked(false);
		menuItem_2.setChecked(false);
		menuItem_3.setChecked(false);
		menuItem_4.setChecked(false);

		MenuItem menuItem = menu.getItem(0);
		menuItem.setChecked(true);
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_one:
						Intent i = new Intent(MainActivity.this, MainActivity.class);
						startActivity(i);
						break;
					case R.id.action_two:
						Intent i2 = new Intent(MainActivity.this, SecondActivity.class);
						startActivity(i2);
						break;
					case R.id.action_three:
						Intent i3 = new Intent(MainActivity.this, LectioActivity.class);
						startActivity(i3);
						break;
					case R.id.action_four:
						Intent i4 = new Intent(MainActivity.this, FourthActivity.class);
						startActivity(i4);
						break;
				}
				return false;
			}

		});

		// exp : 키보드를 보여주고 가리는데 사용하는 객체
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// exp : 아래는 edittext 시에 다른 부분을 누르면 키보드가 사라지게 하는 부분이다.
		ll.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				hideKeyboard();
				switch(v.getId()){
					case R.id.ll :
						break;
				}
			}
		});

		// exp : textsize 설정
		SharedPreferences sp = getSharedPreferences("setting",0);
		textsize = sp.getString("textsize", "");
		if(textsize.equals("small")){
			date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			btnNetCon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			btnNetCon2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			comment_save.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            start.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			end.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		}else if(textsize.equals("big")){
			date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			btnNetCon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			btnNetCon2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
			tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			comment_save.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            start.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			end.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
		}else if(textsize.equals("toobig")){
			date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
			btnNetCon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			btnNetCon2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
			tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			comment_save.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			start.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			end.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
		}else{

		}
		// exp : 다른 activity에서 다른 날짜에 복음 내용을 얻기 위해 전달된 date intent값을 받아 출력하기 위한 부분
		Intent intent = getIntent();
		String daydate = intent.getStringExtra("date");


		// exp : 인터넷연결된 상태에서만 데이터 가져오기
		if ((wifi.isConnected() || mobile.isConnected())) {
			// exp : 이는 비동기적 방식으로 데이터를 가져오는 부분이다.
			start.setVisibility(start.VISIBLE);
			start.setOnClickListener(startlistener);
			end.setOnClickListener(startlistener);
			if(daydate != null){ // daydate가 null이 아니면 그때의 값을 가져온다
				comment.setText("");
				String timeStr = daydate;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date_ = null;
				try {
					date_ = formatter.parse(timeStr); // cf : 이는 String 날짜를 -> 다시 date format으로 변경하여 날짜를 년,월,일 형식으로 바꿔주기 위함이다.
				} catch (ParseException e) {
					e.printStackTrace();
				}
				c1 = Calendar.getInstance();
				c1.setTime(date_);
				String date_val1 = sdf1.format(c1.getTime());
				date.setText(date_val1+getDay()+"요일");
				getGaspel(timeStr);
				getComments(timeStr);
			}else{
				getGaspel(date_val2);
				getComments(date_val2);
			}

		} else {
			tv.setText("인터넷을 연결해주세요");
			tv.setGravity(Gravity.CENTER);
		}

		// exp : 코멘트 저장버튼을 누르면 발생하는 이벤트(코멘트 저장 및 수정)를 설정해준다.
		comment_save.setOnClickListener(listener);

		/// exp : 코멘트 데이터베이스를 생성 및 초기화
		memberInfoHelper = new MemberInfoHelper(this);

		// exp : 세팅에서 알람 설정
		String alarm = intent.getStringExtra("str");
		if(alarm!=null){
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			myWakeLock= pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
					PowerManager.ACQUIRE_CAUSES_WAKEUP |
					PowerManager.ON_AFTER_RELEASE, TAG);
			myWakeLock.acquire(); //실행후 리소스 반환 필수
			releaseCpuLock();
			playSound(); //
	}

		// exp : 이는 baseActivity에 있는 함수로서 키보드가 보이고 보이지 않을때 이벤트를 주기 위해 불러오는 함수이다.
		attachKeyboardListeners();

		// exp : 이는 현재날짜의 경우와 오늘의 복음을 클릭할때만 복음묵상 시작하기 버튼이 보이고 이벤트를 주기 위한 부분
		Calendar c2 = Calendar.getInstance();
		String date_val1 = sdf1.format(c2.getTime());
	//	if(date.getText().equals(date_val1+getDay()+"요일") && daydate == null){

	//	}


		// custom dialog setting
		dlg_left  = new ListSelectorDialog(this, "Select an Operator");

		// custom dialog key, value 설정
		listk_left = new String[] {"a", "b", "c"};
		listv_left = new String[] {"사용 설명서", "설정", "나의 상태"};



	} //onCreate 메소드 마침

	// 커스텀 다이얼로그 선택시
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			default:
				// show the list dialog.
				dlg_left.show(listv_left, listk_left, new ListSelectorDialog.listSelectorInterface() {

					// procedure if user cancels the dialog.
					public void selectorCanceled() {
					}
					// procedure for when a user selects an item in the dialog.
					public void selectedItem(String key, String item) {
						if(item.equals("사용 설명서")){
							Intent i = new Intent(MainActivity.this, ExplainActivity.class);
							startActivity(i);
						}else if(item.equals("설정")){
							Intent i = new Intent(MainActivity.this, ThirdActivity.class);
							startActivity(i);
						}else if(item.equals("나의 상태")){
							Intent i = new Intent(MainActivity.this, StatusActivity.class);
							startActivity(i);
						}
					}
				});
				return true;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
	}

	String gaspel_date;
	String gaspel_sentence;
	String gaspel_contents;
	public void getGaspel(final String date) {
		// Tag used to cancel the request
		String tag_string_req = "req_getgaspel";
		StringRequest strReq = new StringRequest(Request.Method.POST,
				AppConfig.URL_TODAY, new Response.Listener<String>() { // URL_LOGIN : "http://192.168.116.1/android_login_api/login.php";
			boolean error;
			@Override
			public void onResponse(String response) {
				Log.d(TAG, "Login Response: " + response.toString());
				try {
					JSONObject jObj = new JSONObject(response);
					error = jObj.getBoolean("error");

					// Check for error node in json
					if (!error) { // error가 false인 경우에 데이터가져오기 성공
						// Now store the user in SQLite
						gaspel_date = jObj.getString("created_at");
						Log.d(TAG,gaspel_date);
						gaspel_sentence = jObj.getString("sentence");
						gaspel_contents = jObj.getString("contents");
						String contents = gaspel_contents;
						contents = contents.replaceAll("&gt;", ">");
						contents = contents.replaceAll("&lt;", "<");
						contents = contents.replaceAll("&ldquo;", "\"");
						contents = contents.replaceAll("&rdquo;", "\"");
                        contents = contents.replaceAll("&lsquo;", "\'");
                        contents = contents.replaceAll("&rsquo;", "\'");
						int idx = contents.indexOf("✠");
						int idx2 = contents.indexOf("주님의 말씀입니다.");
						contents = contents.substring(idx+1, idx2);

						tv.setText(contents);
						tv2.setText("주님께서 오늘 나에게 하시는 말씀이 무엇인지 생각해보며, 말씀을 살아가기 위하여 어떻게 해야할지 적어 봅시다.");
						btnNetCon.setText(gaspel_sentence);
						btnNetCon2.setText("말씀새기기");
						comment.setVisibility(comment.VISIBLE);
						comment_save.setVisibility(comment_save.VISIBLE);

					} else {
						// Error in login. Get the error message
						String errorMsg = jObj.getString("error_msg");

					}
				} catch (JSONException e) {
					// JSON error
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e(TAG, "Login Error: " + error.getMessage());
			}

		}) {

			@Override
			protected Map<String, String> getParams() { // 파라미터를 전달한다. date 값
				// Posting parameters to login url
				Map<String, String> params = new HashMap<String, String>();
				params.put("date", date);

				return params;
			}

		};


		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

	//	gaspel_date = "date";
	//	Log.d(TAG,gaspel_date);

	}

	public void getComments(String date){

// exp : 아래에는 date값에 해당하는 저장한 comment값을 가져오도록 하였다.
		MemberInfoHelper memberInfoHelper;
		memberInfoHelper = new MemberInfoHelper(this);
		SQLiteDatabase db;
		//ContentValues values1;
		try{
			String comment_str = null;
			Date origin_date = sdf2.parse(date);
			String date_aft = sdf1.format(origin_date) + getDay() + "요일";
			db = memberInfoHelper.getReadableDatabase();
			String[] columns = {"comment_con", "date", "sentence"};
			String whereClause = "date = ?";
			String[] whereArgs = new String[] {
					date_aft // cf : 날짜에 맞는 코멘트를 가져온다.
			};

			Cursor cursor = db.query("comment", columns,  whereClause, whereArgs, null, null, null); // cf : 그때의 코멘트를 가져온다.

			if(cursor != null){
				while(cursor.moveToNext()){
					comment_str = cursor.getString(0);
					comment.setText(comment_str, TextView.BufferType.EDITABLE);
				}
			}else{
				comment.setText("", TextView.BufferType.EDITABLE);
			}



		}catch(Exception e){

		}
	}


	// exp : 날짜 전후를 클릭할때 진행되는 이벤트
	OnClickListener listener_date = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.bt_beforedate:
					c1.add(Calendar.DATE, -1);
					date_val = sdf1.format(c1.getTime()); // cf : ~년~월~일
					date.setText(date_val + getDay() + "요일");
					date_val2 = sdf2.format(c1.getTime());// cf : YYYY-mm-dd
					comment.setText("");
					getGaspel(date_val2);
					getComments(date_val2);
					break;
				case R.id.bt_afterdate:
					c1.add(Calendar.DATE, 1);
					date_val = sdf1.format(c1.getTime());
					date.setText(date_val + getDay() + "요일");
					date_val2 = sdf2.format(c1.getTime());
					comment.setText("");
					getGaspel(date_val2);
					getComments(date_val2);
					break;

			}
		}
	};

	// exp : 알람에서 사용되는 메소드 - 화면이 꺼져있을때 켜지게 하는
	static void releaseCpuLock() {

		Log.d(TAG,"Releasing cpu wake lock");

		if (myWakeLock!= null) {
			myWakeLock.release();
			myWakeLock= null;
		}

	}
	// exp : 알람시 ringtonePanager를 이용하며 uri를 설정한다. 알람 -> 없을경우에 noti -> 없을경우에 ringtone순서로
	private Uri getAlarmUri() {
		Intent i = getIntent();
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
	}

	// exp : MediaPlayer객체를 생성 및 설정 하여 알람이 울리게 함
	private void playSound() {

		mMediaPlayer = new MediaPlayer();
		try {
			AssetFileDescriptor afd = getAssets().openFd("bell.mp3"); // cf : 파일을 여는 부분
			mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			mMediaPlayer.prepare();
			mMediaPlayer.start();

			// cf : 이때 setOnseekCompleteListener를 이용하여 알람이 한번만 울리고 멈추게 해 주었다.
			mMediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
				public void onSeekComplete(MediaPlayer mMediaPlayer) {
					mMediaPlayer.stop();
					mMediaPlayer.release();
				}
			});

		} catch (IOException e) {
			System.out.println("OOPS");
		}

	}


	private void getlevelchange() {
		try {
			SQLiteDatabase db;
			String date_str = null;
			db = memberInfoHelper.getReadableDatabase();
			String[] columns = {"comment_con", "date", "sentence"};
			String query = "SELECT comment_con, date, sentence FROM comment";
			Cursor cursor = db.rawQuery(query, null);
			int i = 0;
			while (cursor.moveToNext()) {
				date_str = cursor.getString(1);
				if (date_str!=null) {
					i++;
				}
			}
			// 레벨 자동 올리는 부분
			if (i == 9) {
				Intent i2 = new Intent(MainActivity.this, StatusActivity.class);
				startActivity(i2);
			} else if (i >= 10) { //레벨이 올라간 후에 최근 안쓰면 레벨 떨어지도록 하기 위해서 done으로 바꿔준다.
				SharedPreferences sp_level;
				sp_level = getSharedPreferences("setting",0);
				SharedPreferences.Editor editor;
				editor =  sp_level.edit();
				editor.putString("first", "done");
				editor.commit();
			//	Toast.makeText(MainActivity.this, "10개", Toast.LENGTH_SHORT).show();
			}

		}catch (Exception e){

		}
	}


	OnClickListener startlistener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.bt_start: // start를 누르면 countDownTimer1이 불러와짐
					start.setVisibility(start.GONE);
					end.setVisibility(end.VISIBLE);
					countDownTimer1(); // cf : 카운트다운타이머를 주어서 토스트가 나오는 시간을 조정
					countDownTimer.start();
							 // cf : 이는 새로운 custom_toast.xml의 레이아웃가 토스트를 대체하는 방법이다.

					unlockMessage = Toast.makeText(MainActivity.this, "음악이 나오는 동안 \n 잠시 눈을 감고, \n 침묵해 봅시다", Toast.LENGTH_LONG);

					group = (ViewGroup) unlockMessage.getView(); // cf : 이는 토스트에 있는 텍스트를 얻어와서 설정해주기 위한 부분
					messageTextView = (TextView) group.getChildAt(0);

					SharedPreferences sp = getSharedPreferences("setting",0); // cf : 글씨 크기 조정
					textsize = sp.getString("textsize", "");
					if(textsize.equals("small")){
						messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					}else if(textsize.equals("big")){
						messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
					}else if(textsize.equals("toobig")){
						messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
					}else{
						messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
					}
					messageTextView.setGravity(Gravity.CENTER);
					messageTextView.setLineSpacing(5, 1);
					unlockMessage.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					//unlockMessage.show();
					break;
				case R.id.bt_end:
					end.setVisibility(end.GONE);
					start.setVisibility(start.VISIBLE);
					onDestroy();
					break;
			}
		}
	};

	// exp : 카운트다운타이머(토스트 시간조정) 함수
	public void countDownTimer1(){ // 음악이 나오고, 동시에 countdowntimer를 통해서 메시지가 시간동안 나온다.
		mediaPlayer= MediaPlayer.create(MainActivity.this, R.raw.calm);
		mediaPlayer.start();
		countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
			public void onTick(long millisUntilFinished) {
				unlockMessage.show();

				//date.setText(String.valueOf(count)); // cf : count 숫자 출력방법
				//count --;
			}
			public void onFinish() {
				//unlockMessage.show();

				countDownTimer2(); // cf : 이어서 성령청원기도 토스트 출력 20초간
				countDownTimer2.start();


				unlockMessage2 = Toast.makeText(MainActivity.this, "청원 기도를 바쳐봅시다. \n\n 오소서, 성령님,주님의 빛, 그 빛살을 하늘에서 내리소서.\n " +
								"가난한 이 아버지, 오소서 은총 주님, 오소서 마음의 빛.\n 가장 좋은 위로자, 영혼의 기쁜 손님, 저희 생기 돋우소서." +
								"\n일할 때에 휴식을, 무더위에 시원함을, 슬플 때에 위로를.\n영원하신 행복의 빛, 저희 마음 깊은 곳을 가득하게 채우소서." +
								"\n주님 도움 없으시면, 저희 삶의 그 모든 것, 해로운 것 뿐이리라.\n허물들은 씻어 주고, 마른 땅 물 주시고, 병든 것을 고치소서." +
								"\n굳은 마음 풀어 주고,차디찬 맘 데우시고, 빗나간 길 바루소서.\n 성령님을 굳게 믿고, 의지하는 이들에게, 성령칠은 베푸소서." +
								"\n덕행 공로 쌓게 하고,구원의 문 활짝 열어, 영원복락 주옵소서. 아멘."
										, Toast.LENGTH_LONG);
				group = (ViewGroup) unlockMessage2.getView();
				messageTextView = (TextView) group.getChildAt(0);
                SharedPreferences sp = getSharedPreferences("setting",0);
                textsize = sp.getString("textsize", "");
                if(textsize.equals("small")){
                    messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                }else if(textsize.equals("big")){
                    messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
                }else if(textsize.equals("toobig")){
					messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
				}else{
                    messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                }
                messageTextView.setGravity(Gravity.CENTER);
                messageTextView.setLineSpacing(5, 1);
				unlockMessage2.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				//unlockMessage2.show();
			}
		};
	}

	public void countDownTimer2(){

		countDownTimer2 = new CountDownTimer(MILLISINFUTURE2, COUNT_DOWN_INTERVAL) {
			public void onTick(long millisUntilFinished) {
				unlockMessage2.show();
			}
			public void onFinish() {
				//unlockMessage2.show();
				last = Toast.makeText(MainActivity.this, "이제 복음을 소리내어 읽어봅시다", Toast.LENGTH_LONG);
				group = (ViewGroup) last.getView();
				messageTextView = (TextView) group.getChildAt(0);
                SharedPreferences sp = getSharedPreferences("setting",0);
                textsize = sp.getString("textsize", "");
                if(textsize.equals("small")){
                    messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                }else if(textsize.equals("big")){
                    messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
                }else if(textsize.equals("toobig")){
					messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
				}else{
                    messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                }
                messageTextView.setGravity(Gravity.CENTER);
				last.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				last.show();

				start.setVisibility(start.VISIBLE);
				end.setVisibility(end.GONE);
			}
		};
	}

	//exp : CountTimer 멈출때 메소드
	@Override
	public void onDestroy() {
		super.onDestroy();
		try{
			countDownTimer.cancel();
			unlockMessage.cancel();
			unlockMessage2.cancel();
			countDownTimer2.cancel();
		} catch (Exception e) {}
		countDownTimer=null;
		countDownTimer2=null;
		mediaPlayer.stop();
	}

	// exp : 요일 얻어오기
	public static String getDay(){
		int dayNum = c1.get(Calendar.DAY_OF_WEEK) ;

		switch(dayNum){
			case 1:
				day = "일";
				break ;
			case 2:
				day = "월";
				break ;
			case 3:
				day = "화";
				break ;
			case 4:
				day = "수";
				break ;
			case 5:
				day = "목";
				break ;
			case 6:
				day = "금";
				break ;
			case 7:
				day = "토";
				break ;

		}
		return day;
	}




	// exp : 저장버튼 누를때 진행되는 이벤트로 코멘트 내용을 저장하거나 수정한다.
	OnClickListener listener = new OnClickListener() {

		public void onClick(View v) {

			hideKeyboard();
			SQLiteDatabase db;
			ContentValues values;
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.bt_comment:
					getlevelchange();
				String comment_con = comment.getText().toString();
				String comment_date = (String) date.getText();
				String sentence = (String) btnNetCon.getText();

				// cf : 기존에 comment 값이 있는지 값이 있는지 확인하여 있는 경우, already값을 1로 준다.
				try{
					String comment_str = null;
					db = memberInfoHelper.getReadableDatabase();
					String[] columns = {"comment_con", "date", "sentence"};
					String whereClause = "date = ?";
					String[] whereArgs = new String[] {
							date.getText().toString()
					};
					Cursor cursor = db.query("comment", columns,  whereClause, whereArgs, null, null, null);

					while(cursor.moveToNext()){
						comment_str = cursor.getString(0);
					}
					if(comment_str!=null){
						already = 1;
					}

				}catch(Exception e){

				}


				// cf : comment가 없는 경우에는 삽입한다 -> already = 0
				if(already==0){
					try{
						db=memberInfoHelper.getWritableDatabase();
						values = new ContentValues();
						values.put("comment_con", comment_con);
						values.put("date", comment_date);
						values.put("sentence", sentence);
						db.insert("comment", null, values);
						memberInfoHelper.close();
					}catch(Exception e){
						e.printStackTrace();
					}

					//exp : 이는 구글캘린더 연동 하는 부분
					int yearsite = comment_date.indexOf("년");
					int monthsite = comment_date.indexOf("월");
					int daysite = comment_date.indexOf("일 ");
					String year= comment_date.substring(0, yearsite);
					String month= comment_date.substring(yearsite+2, monthsite);
					String day = comment_date.substring(monthsite+2, daysite);
					SharedPreferences sp = getSharedPreferences("setting",0);
					String calendarstatus = sp.getString("gcal", "");
					if(calendarstatus.equals("on")) {
						new MakeInsertTask(mCredential, year+"-"+month+"-"+day, year+month+day+"aeasaeapj", sentence+"&"+comment_con, "오늘의복음").execute();
					}

					Toast.makeText(MainActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();

				}else if(already ==1){ // cf : comment가 있는 경우에는 update 한다 -> already =1
					try{
						db=memberInfoHelper.getWritableDatabase();
						values = new ContentValues();
						values.put("comment_con", comment_con);
						String where = "date=?";
						String[] whereArgs = new String[] {date.getText().toString()};

						db.update("comment", values, where, whereArgs);
						memberInfoHelper.close();
					}catch(Exception e){
						e.printStackTrace();
					}
					//exp : 이는 구글캘린더 연동 하는 부분
					int yearsite = comment_date.indexOf("년");
					int monthsite = comment_date.indexOf("월");
					int daysite = comment_date.indexOf("일 ");
					String year= comment_date.substring(0, yearsite);
					String month= comment_date.substring(yearsite+2, monthsite);
					String day = comment_date.substring(monthsite+2, daysite);

					SharedPreferences sp = getSharedPreferences("setting",0);
					String calendarstatus = sp.getString("gcal", "");
						if(calendarstatus.equals("on")) {
							new MakeUpdateTask(mCredential, year+"-"+month+"-"+day, year+month+day+"aeasaeapj", sentence+"&"+comment_con, "오늘의복음").execute();
						}
					Toast.makeText(MainActivity.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
									}

					break;

			}
		}
	};



	// exp : 키보드가 보일때, 보이지 않을때 이벤트 주는 부분 (BaseActivity의 메소드)
	@Override
	protected void onShowKeyboard(int keyboardHeight) {
		// do things when keyboard is shown
		bottomNavigationView.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onHideKeyboard() {
		// do things when keyboard is hidden
		bottomNavigationView.setVisibility(View.GONE);
	}

	// exp : 다른 부분을 클릭하면 키보드가 사라지도록 하기 위해 만든 메소드 // cf : 결국 키보드 보이고 안보이는 데는 imm과 baseActivity 두개가 사용됨
	private void hideKeyboard(){
		imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
	}



	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {

	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {

	}

	@Override
	public void onBackPressed() {
		//Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();

	}


}
