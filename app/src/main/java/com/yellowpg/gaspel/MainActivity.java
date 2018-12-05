package com.yellowpg.gaspel;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.DB.CommentDBSqlData;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.server.Server_CommentData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
	final static String TAG = "MainActivity";
	Button btnNetCon;
	Button btnNetCon2;
	Button comment_save;
	TextView tv;
	TextView tv2;
	Button date;
	ImageButton before, after;
	static String day;
	String textsize;
	EditText comment;
	String daydate;
	BottomNavigationView bottomNavigationView;
	ListSelectorDialog dlg_left;
	String[] listk_left, listv_left;
	private SessionManager session;
	String uid = null;

	public static MediaPlayer mMediaPlayer;
	InputMethodManager imm;
	LinearLayout ll, ll_date;

	static Calendar c1 = Calendar.getInstance();
	//현재 해 + 달 구하기
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	String date_val = sdf.format(c1.getTime());
	//현재 해 + 달 구하기
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy년 MM월 dd일 ");
	String date_val1 = sdf1.format(c1.getTime());

	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	String date_val2 = sdf2.format(c1.getTime());

	// exp : 알람 연관된 부분
	private static PowerManager.WakeLock myWakeLock;


	@SuppressLint("InvalidWakeLockTag")
	protected void onCreate(Bundle savedInstanceState){
		//session 정보 가져오기
		session = new SessionManager(getApplicationContext());
		uid = session.getUid();

		c1 = Calendar.getInstance();

		// exp : 인터넷연결 확인
		ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		super.onCreate(savedInstanceState);
		date_val = sdf.format(c1.getTime());
		date_val1 = sdf1.format(c1.getTime());
		date_val2 = sdf2.format(c1.getTime());

		setContentView(R.layout.activity_main);

		//actionbar setting
		android.support.v7.app.ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setCustomView(R.layout.actionbar);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2980b9")));
		actionbar.setElevation(0);

		// actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeAsUpIndicator(R.drawable.list);


		ll = (LinearLayout) findViewById(R.id.ll);
		ll_date = (LinearLayout) findViewById(R.id.ll_date);
		// date
		date = (Button) findViewById(R.id.bt_date);
		before = (ImageButton) findViewById(R.id.bt_beforedate);
		after = (ImageButton) findViewById(R.id.bt_afterdate);

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

		// 요일 앞뒤로 변경하는 소스
		date.setText(date_val1+getDay()+"요일"); // "yyyy년 MM월 dd일 "
		before.setOnClickListener(listener_date);
		after.setOnClickListener(listener_date);

		// bottomnavigation 뷰 등록
		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
		Menu menu = bottomNavigationView.getMenu();
		MenuItem menuItem_1 = menu.getItem(0);
		MenuItem menuItem_2 = menu.getItem(1);
		MenuItem menuItem_3 = menu.getItem(2);
		MenuItem menuItem_4 = menu.getItem(3);
		MenuItem menuItem_5 = menu.getItem(4);
		menuItem_1.setChecked(false);
		menuItem_2.setChecked(false);
		menuItem_3.setChecked(false);
		menuItem_4.setChecked(false);
		menuItem_5.setChecked(false);
		MenuItem menuItem = menu.getItem(1);
		menuItem.setChecked(true);
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_zero:
						Intent i0 = new Intent(MainActivity.this, FirstActivity.class);
						startActivity(i0);
						break;
					case R.id.action_one:
						Intent i = new Intent(MainActivity.this, MainActivity.class);
						startActivity(i);
						break;
					case R.id.action_two:
						Intent i2 = new Intent(MainActivity.this, LectioActivity.class);
						startActivity(i2);
						break;
					case R.id.action_three:
						Intent i3 = new Intent(MainActivity.this, WeekendActivity.class);
						startActivity(i3);
						break;
					case R.id.action_four:
						Intent i4 = new Intent(MainActivity.this, RecordActivity.class);
						startActivity(i4);
						break;
				}
				return true;
			}

		});

		// 화면 클릭시 soft keyboard hide
		findViewById(R.id.ll).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				return true;
			}
		});
		findViewById(R.id.bt_network_con2).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				return true;
			}
		});

		// textsize 설정
		SharedPreferences sp = getSharedPreferences("setting",0);
		textsize = sp.getString("textsize", "");
		if(textsize.equals("big")){
			date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			btnNetCon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			btnNetCon2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		}else{

		}
		// 다른 activity에서 다른 날짜에 복음 내용을 얻기 위해 전달된 date intent값을 받아 출력
		Intent intent = getIntent();
		daydate = intent.getStringExtra("date");


		// 인터넷연결된 상태에서만 데이터 가져오기
		if ((wifi.isConnected() || mobile.isConnected())) {

			// intent로 들어온 daydate가 null이 아닌 경우
			if(daydate != null){
				comment.setText("");
				String timeStr = daydate;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date_ = null;
				try {
					date_ = formatter.parse(timeStr); // string yyyy-MM-dd => Date 형식
				} catch (ParseException e) {
					e.printStackTrace();
				}
				c1 = Calendar.getInstance();
				c1.setTime(date_);
				String date_val1 = sdf1.format(c1.getTime()); // cf : yyyy-MM-dd => yyyy년 MM월 dd일 x요일
				date.setText(date_val1+getDay()+"요일"); // c1으로 getday()함
				getGaspel(timeStr);
				getComments(timeStr);

				//intent로 온 경우에 날짜 이동 없애고 actionbar 변경하기
				before.setVisibility(View.GONE);
				after.setVisibility(View.GONE);

				actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
				actionbar.setCustomView(R.layout.actionbar_back);
				actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2980b9")));
				actionbar.setElevation(0);

				// actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
				actionbar.setDisplayHomeAsUpEnabled(true);
				actionbar.setHomeAsUpIndicator(R.drawable.back);
				bottomNavigationView.setVisibility(View.GONE);
			}else{
				getGaspel(date_val2);
				getComments(date_val2);
			}

		} else {
			tv.setText("인터넷을 연결해주세요");
			tv.setGravity(Gravity.CENTER);
		}

		// 코멘트 저장버튼을 누르면 발생하는 이벤트(코멘트 저장 및 수정)를 설정해준다.
		comment_save.setOnClickListener(listener);
		comment_save.setBackgroundResource(R.drawable.button_bg);


		// 세팅에서 알람 설정
		String alarm = intent.getStringExtra("str");
		if(alarm!=null){
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			myWakeLock= pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
					PowerManager.ACQUIRE_CAUSES_WAKEUP |
					PowerManager.ON_AFTER_RELEASE, TAG);
			myWakeLock.acquire(); //실행후 리소스 반환 필수
			releaseCpuLock();
			playSound(MainActivity.this, "alarm");
		}

		// 왼쪽 list클릭시 이벤트 custom dialog setting
		dlg_left  = new ListSelectorDialog(this, "Select an Operator");

		// custom dialog key, value 설정
		listk_left = new String[] {"a", "b", "c"};
		listv_left = new String[] { "설정", "나의 상태", "계정정보"};

	} //onCreate 메소드 마침


	// 커스텀 다이얼로그 선택시
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.pray:
				showPraying(item);
				return true;
			default:
				// intent daydate가 null이 아닌 경우
				if(daydate != null){
					finish();
				// mainactivity인 경우
				}else{
					// show the list dialog.
					dlg_left.show(listv_left, listk_left, new ListSelectorDialog.listSelectorInterface() {

						// procedure if user cancels the dialog.
						public void selectorCanceled() {
						}
						// procedure for when a user selects an item in the dialog.
						public void selectedItem(String key, String item) {
							if(item.equals("설정")){
								Intent i = new Intent(MainActivity.this, SettingActivity.class);
								startActivity(i);
							}else if(item.equals("나의 상태")){
								Intent i = new Intent(MainActivity.this, StatusActivity.class);
								startActivity(i);
							}else if(item.equals("계정정보")){
								Intent i = new Intent(MainActivity.this, LoginActivity.class);
								startActivity(i);
							}
						}
					});
				}

				return true;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	Thread t;
	TextView text_ttl;
	TextView text;
	Button declineButton;

	// 오른쪽 종을 누르면 청원기도가 보인다
	@SuppressLint("InvalidWakeLockTag")
	public void showPraying(final MenuItem item)
	{

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		@SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock myWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
				PowerManager.ACQUIRE_CAUSES_WAKEUP |
				PowerManager.ON_AFTER_RELEASE, TAG);
		myWakeLock.acquire(); //실행후 리소스 반환 필수
		MainActivity.releaseCpuLock();
		MainActivity.playSound(MainActivity.this, "pray");

		// Create custom dialog object
		final Dialog dialog = new Dialog(MainActivity.this);
		// Include dialog.xml file
		dialog.setContentView(R.layout.dialog);
		// Set dialog title
		dialog.setTitle("Custom Dialog");

		final ProgressBar progressbar = (ProgressBar) dialog.findViewById(R.id.progress);

		t = new Thread(new Runnable() {
			@Override
			public void run(){
				int progress=0;
				while(progress<100){
					++progress;
					progressbar.setProgress(progress);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(t.isInterrupted()) { break; }
				}
			}
		});
		t.start();

		text_ttl = (TextView) dialog.findViewById(R.id.titleDialog);
		//text_ttl.setText("성령 청원 기도");
		text_ttl.setText("\n침묵에 들어가는 단계\n");
		// set values for custom dialog components - text, image and button
		text = (TextView) dialog.findViewById(R.id.textDialog);
		text.setText("하느님의 현존을 느껴 봅시다. 하느님께서 주시는 새 마음으로 들어가도록 노력하며 \n" +
				"일상을 떠나 잠시지만 하느님 세계로 차원을 바꿔 봅시다.\n");

		dialog.show();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				//  item.setIcon(getResources().getDrawable(R.drawable.notification_base));
			}
		});

		// 기도마침 클릭시 이벤트
		declineButton = (Button) dialog.findViewById(R.id.declineButton);
		declineButton.setText("[다음 단계]");
		final ImageView dialog_image = (ImageView) dialog.findViewById(R.id.dialog_image);
		// if decline button is clicked, close the custom dialog
		declineButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMediaPlayer.stop();
				t.interrupt();
				if(declineButton.getText().equals("[다음 단계]")){
					dialog_image.setVisibility(View.GONE);
					progressbar.setVisibility(View.GONE);
					declineButton.setText("[기도 마침]");
					text_ttl.setText("성령 청원 기도");
					text.setText("오소서, 성령님\n" +
							"당신의 빛, 그 빛살을 하늘에서 내리소서.\n" +
							"가난한 이 아버지, 은총 주님\n" +
							"오소서 마음에 빛을 주소서.\n" +
							"가장 좋은 위로자, 영혼의 기쁜 손님,\n" +
							"생기 돋워 주소서.\n" +
							"일할 때에 휴식을, 무더울 때 바람을,\n" +
							"슬플 때에 위로를, 지복의 빛이시여,\n" +
							"저희 맘 깊은 곳을 가득히 채우소서.\n" +
							"주님 도움 없으면 저희 삶 그 모든 것\n" +
							"이로운 것 없으리.\n" +
							"허물은 씻어 주고 마른 땅 물 주시고\n" +
							"병든 것 고치소서.\n" +
							"굳은 맘 풀어 주고 찬 마음 데우시고\n" +
							"바른길 이끄소서.\n" +
							"성령님을 믿으며 의지하는 이에게\n" +
							"칠은을 베푸소서.\n" +
							"공덕을 쌓게  하고 구원의 문을 넘어\n" +
							"영복을 얻게 하소서.아멘");
				}else{
					// Close dialog
					dialog.dismiss();

				}

			}
		});
	}

	String gaspel_date;
	String gaspel_sentence;
	String gaspel_contents;
	// 그날 복음 내용 가져오기
	public void getGaspel(final String date) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				// Tag used to cancel the request
				String tag_string_req = "req_getgaspel";
				StringRequest strReq = new StringRequest(Request.Method.POST,
						AppConfig.URL_TODAY, new Response.Listener<String>() { // URL_LOGIN : "http://192.168.116.1/android_login_api/login.php";
					boolean error;
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "getGaspel Response: " + response.toString());
						try {
							JSONObject jObj = new JSONObject(response);
							error = jObj.getBoolean("error");

							// Check for error node in json
							if (!error) {
								// Now store the user in SQLite
								gaspel_date = jObj.getString("created_at");
								Log.d(TAG, gaspel_date);
								gaspel_sentence = jObj.getString("sentence");
								gaspel_contents = jObj.getString("contents");

								// DB에서 가져온 복음내용 편집하기
								String contents = gaspel_contents;
								contents = contents.replaceAll("&gt;", ">");
								contents = contents.replaceAll("&lt;", "<");
								contents = contents.replaceAll("&ldquo;", "");
								contents = contents.replaceAll("&rdquo;", "");
								contents = contents.replaceAll("&lsquo;", "");
								contents = contents.replaceAll("&rsquo;", "");
								contents = contents.replaceAll("&prime;", "'");
								contents = contents.replaceAll("\n", " ");
								contents = contents.replaceAll("&hellip;", "…");
								contents = contents.replaceAll("주님의 말씀입니다.", "\n"+"주님의 말씀입니다.");

								int idx = contents.indexOf("✠");
								int idx2 = contents.indexOf("◎ 그리스도님 찬미합니다");
								contents = contents.substring(idx, idx2);

								// 줄넘김 편집
								int idx3 = contents.indexOf("거룩한 복음입니다.");
								int length = "거룩한 복음입니다.".length();
								final String after = contents.substring(idx3+length+17);

								Pattern p = Pattern.compile(".\\d+");
								Matcher m = p.matcher(after);
								while (m.find()) {
									Log.d("saea", after);
									contents = contents.replaceAll(m.group(), "\n"+m.group());
								}
								tv.setText(contents);
								tv2.setText("주님께서 오늘 나에게 하고자 하시는 말씀이 무엇인지 생각해 봅시다.");

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
			}
		});
		t.start();
	}

	public void getComments(String date) {

		Date origin_date = null;
		try {
			origin_date = sdf2.parse(date);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String date_aft = sdf1.format(origin_date) + getDay() + "요일";

	/*	SQLiteDatabase db = null;
		try {
			db = commentInfoHelper.getReadableDatabase();
			String[] columns = {"comment_con", "date", "sentence"};
			String whereClause = "date = ?";
			String[] whereArgs = new String[]{
					date_aft
			};
			Cursor cursor = db.query("comment", columns, whereClause, whereArgs, null, null, null);

			while (cursor.moveToNext()) {
				comment_str = cursor.getString(0);

			}
			*/
		ArrayList<Comment> comments = new ArrayList<Comment>();
		String comment_str = null;
		DBManager dbMgr = new DBManager(MainActivity.this);
		dbMgr.dbOpen();
		dbMgr.selectCommentData(CommentDBSqlData.SQL_DB_SELECT_DATA, uid, date_aft , comments);
		dbMgr.dbClose();

		if(!comments.isEmpty()){
			comment_str = comments.get(0).getComment();
		}else{
		}
			// 기존 값이 있는 경우
			if (comment_str != null) {
				Log.d("saea", comment_str);
				comment.setText(comment_str, TextView.BufferType.EDITABLE);
			}else{

			}
	/*	} catch (Exception e1) {
			e1.printStackTrace();
		} */

	}

	// 날짜 전후를 클릭할때 이벤트
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

	// 알람에서 사용되는 메소드 - 화면이 꺼져있을때 켜지게 하는
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

	// MediaPlayer객체를 생성 및 설정 알람 울리도록 함
	public static void playSound(Context mcontext, String sound) {
		mMediaPlayer = new MediaPlayer();
		try {
			AssetFileDescriptor afd = null;
			if(sound.equals("alarm")){
				afd = mcontext.getAssets().openFd("bell.mp3"); // cf : 파일을 여는 부분
			}else if(sound.equals("pray")){
				afd = mcontext.getAssets().openFd("church_bell.mp3"); // cf : 파일을 여는 부분
			}

			mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			afd.close();
			mMediaPlayer.prepare();
			mMediaPlayer.start();

			// 이때 setOnseekCompleteListener를 이용하여 알람이 한번만 울리고 멈추게 해 주었다.
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

	// 요일 얻어오기
	public String getDay(){
		int dayNum = c1.get(Calendar.DAY_OF_WEEK);
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

	// 저장버튼 누를때 진행되는 이벤트 - 코멘트 내용을 저장하거나 수정
	OnClickListener listener = new OnClickListener() {

		public void onClick(View v) {
			SQLiteDatabase db = null;
			ContentValues values;
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.bt_comment:
					String comment_con = comment.getText().toString();
					String comment_date = (String) date.getText();
					String sentence = (String) btnNetCon.getText();

					// 기존에 comment 값이 있는지 값이 있는지 확인하여 있는 수정, 없는 경우 저장
				/*	String comment_str = null;
					try{
						db = commentInfoHelper.getReadableDatabase();
						String[] columns = {"comment_con", "date", "sentence"};
						String whereClause = "date = ?";
						String[] whereArgs = new String[] {
								date.getText().toString()
						};
						Cursor cursor = db.query("comment", columns,  whereClause, whereArgs, null, null, null);

						while(cursor.moveToNext()){
							comment_str = cursor.getString(0);

						}
					*/
					ArrayList<Comment> comments = new ArrayList<Comment>();
					String comment_str = null;
					DBManager dbMgr = new DBManager(MainActivity.this);
					dbMgr.dbOpen();
					dbMgr.selectCommentData(CommentDBSqlData.SQL_DB_SELECT_DATA, uid, date.getText().toString() , comments);
					dbMgr.dbClose();

					if(!comments.isEmpty()){
						comment_str = comments.get(0).getComment();
					}else{
					}


					// 기존 값이 있는 경우 수정하기
					if(comment_str!=null){
					/*	values = new ContentValues();
						values.put("comment_con", comment_con);
						String where = "date=?";
						db.update("comment", values, where, whereArgs); */
						dbMgr.dbOpen();
						dbMgr.updateCommentData(CommentDBSqlData.SQL_DB_UPDATE_DATA, uid, date.getText().toString(), comment_con);
						dbMgr.dbClose();

						Toast.makeText(MainActivity.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();

						if(uid != null && uid != ""){
							Server_CommentData.updateComment(MainActivity.this, uid, comment_date, sentence, comment_con);
						}
					// 기존 값이 없는 경우 추가 하기
					}else{
						/*
						values = new ContentValues();
						values.put("comment_con", comment_con);
						values.put("date", comment_date);
						values.put("sentence", sentence);
						db.insert("comment", null, values);
						*/
						Comment commentData = new Comment(uid, comment_date, sentence, comment_con);
						dbMgr.dbOpen();
						dbMgr.insertCommentData(CommentDBSqlData.SQL_DB_INSERT_DATA, commentData);
						dbMgr.dbClose();
						Toast.makeText(MainActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
						if(uid != null && uid != ""){
							Server_CommentData.insertComment(MainActivity.this, uid, comment_date, sentence, comment_con);
							}
						}
				/*		}catch(Exception e){

					} */
				break;
			}
		}
	};

	// actionbar 오른쪽 벨 추가
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.topmenu_main, menu);
		return true;
	}


}