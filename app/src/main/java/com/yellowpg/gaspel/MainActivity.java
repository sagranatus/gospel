package com.yellowpg.gaspel;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yellowpg.gaspel.DB.CommentDBSqlData;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.etc.getDay;
import com.yellowpg.gaspel.server.Server_CommentData;
import com.yellowpg.gaspel.server.Server_getGaspel;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
	final static String TAG = "MainActivity";
	Button btnNetCon;
	Button comment_save, comment_edit;
	TextView tv;
	TextView tv2;
	TextView tv_comment;
	String typedDate;
	String textsize;
	EditText comment;
	String daydate;
	BottomNavigationView bottomNavigationView;
	ImageButton up,down;
	ListSelectorDialog dlg_left;
	String[] listk_left, listv_left;
	private SessionManager session;
	String uid = null;
	NetworkInfo mobile;
	NetworkInfo wifi;

	Calendar c1 = Calendar.getInstance();

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy년 MM월 dd일 ");
	String date_val1 = sdf1.format(c1.getTime());

	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	String date_val2 = sdf2.format(c1.getTime());


	protected void onCreate(Bundle savedInstanceState){

		//session 정보 가져오기
		session = new SessionManager(getApplicationContext());
		uid = session.getUid();

		// exp : 인터넷연결 확인
		ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

		mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		//actionbar setting
		android.support.v7.app.ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setCustomView(R.layout.actionbar_main);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
		actionbar.setElevation(0);


		tv = (TextView) findViewById(R.id.tv_01); // 복음 내용
		btnNetCon = (Button) findViewById(R.id.bt_network_con); // 복음 첫 구절

		tv2 = (TextView) findViewById(R.id.tv_02); // 말씀새기기 질문
		tv_comment = (TextView) findViewById(R.id.tv_comment); // 저장된 코멘트 내용
		comment = (EditText) findViewById(R.id.et_comment); // 코멘트 edittext
		comment_save = (Button) findViewById(R.id.bt_save);
		comment_edit = (Button) findViewById(R.id.bt_edit);

		up = (ImageButton) findViewById(R.id.up);
		down = (ImageButton) findViewById(R.id.down);

		// 코멘트 저장버튼을 누르면 발생하는 이벤트(코멘트 저장 및 수정)를 설정해준다.
		comment_save.setOnClickListener(listener);
		comment_edit.setOnClickListener(listener);
		up.setOnClickListener(listener);
		down.setOnClickListener(listener);
		comment_save.setBackgroundResource(R.drawable.button_bg);
		comment_edit.setBackgroundResource(R.drawable.button_bg);
		comment.setBackgroundResource(R.drawable.edit_bg_white);
		tv_comment.setBackgroundResource(R.drawable.edit_bg_grey);

		// textsize 설정
		SharedPreferences sp = getSharedPreferences("setting",0);
		textsize = sp.getString("textsize", "");
		if(textsize.equals("big")){
			btnNetCon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			tv_comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		}else{

		}

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

		// soft keyboard 보일때 이벤트
		KeyboardVisibilityEvent.setEventListener(
				MainActivity.this,
				new KeyboardVisibilityEventListener() {
					@Override
					public void onVisibilityChanged(boolean isOpen) {

						if(isOpen && daydate == null){
						// some code depending on keyboard visiblity status
						bottomNavigationView.setVisibility(View.GONE);
						//가장 아래에 붙인다
						LinearLayout ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ll_bottom.getLayoutParams();
						params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
						ll_bottom.setLayoutParams(params);

						}else if(!isOpen && daydate == null){
							// some code depending on keyboard visiblity status
							bottomNavigationView.setVisibility(View.VISIBLE);
							final float scale = getResources().getDisplayMetrics().density;
							LinearLayout ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
							RelativeLayout.LayoutParams params =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) (120 * scale + 0.5f));
							params.addRule(RelativeLayout.ABOVE, bottomNavigationView.getId());
							ll_bottom.setLayoutParams(params);
						}
					}
				});

		// list클릭시 이벤트 custom dialog setting
		dlg_left  = new ListSelectorDialog(this, "Select an Operator");

		// custom dialog key, value 설정
		listk_left = new String[] {"a", "b", "c"};
		listv_left = new String[] { "설정",  "계정정보","로그아웃"};


		// 화면 클릭시 soft keyboard hide
		findViewById(R.id.ll).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				if (getCurrentFocus() != null) {
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				}
				return true;
			}
		});

		// 다른 activity에서 전달된 date intent값을 받아 출력
		Intent intent = getIntent();
		daydate = intent.getStringExtra("date");


		// intent로 들어온 daydate가 null이 아닌 경우
		if(daydate != null){ // yyyy-MM-dd  형식 날짜
			actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

			actionbar.setDisplayHomeAsUpEnabled(true);
			actionbar.setHomeAsUpIndicator(R.drawable.back);
			bottomNavigationView.setVisibility(View.GONE);

			//가장 아래에 붙인다
			LinearLayout ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ll_bottom.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			ll_bottom.setLayoutParams(params);

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
			typedDate = date_val1+getDay.getDay(c1)+"요일";

			if ((wifi.isConnected() || mobile.isConnected())) {
				//getGaspel(timeStr); // yyyy-MM-dd
				Server_getGaspel.get_Gaspel(up, down, timeStr, btnNetCon, tv );
			}else{
				LinearLayout.LayoutParams params_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
				params_.weight = 1.0f;
				params_.gravity = Gravity.CENTER;

				tv.setLayoutParams(params_);
				tv.setText("인터넷을 연결해주세요");
			}
			getComments();

		}else{
			typedDate = date_val1+getDay.getDay(c1)+"요일";
			Log.d("saea", "날짜"+typedDate );
			if ((wifi.isConnected() || mobile.isConnected())) {
			//	getGaspel(date_val2); // yyyy-MM-dd
				Server_getGaspel.get_Gaspel(up, down, date_val2, btnNetCon, tv );
			}else{
				LinearLayout.LayoutParams params_ = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
				params_.weight = 1.0f;
				params_.gravity = Gravity.CENTER;

				tv.setLayoutParams(params_);
				tv.setText("인터넷을 연결해주세요");
			}
			getComments();
		}

	} //onCreate 메소드 마침

	@Override
	protected void onResume() {
		super.onResume();
	}

	// 커스텀 다이얼로그 선택시
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.list:
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
						}else if(item.equals("계정정보")){
							Intent i = new Intent(MainActivity.this, ProfileActivity.class);
							startActivity(i);
						}else if (item.equals("로그아웃")) {
							ProfileActivity.logoutUser(session,MainActivity.this);
						}
					}
				});
				return true;
			default:
				// intent daydate가 null이 아닌 경우
				if(daydate != null){
					Intent intent = new Intent(MainActivity.this, RecordActivity.class);
					intent.putExtra("dateBack",daydate);
					startActivity(intent);
				}

				return true;
		}
	}


	public void getComments() {

		comment.setText("");
		ArrayList<Comment> comments = new ArrayList<Comment>();
		String comment_str = null;
		DBManager dbMgr = new DBManager(MainActivity.this);
		dbMgr.dbOpen();
		dbMgr.selectCommentData(CommentDBSqlData.SQL_DB_SELECT_DATA, uid, typedDate , comments);
		dbMgr.dbClose();

		if(!comments.isEmpty()){
			comment_str = comments.get(0).getComment();
		}

		// 기존 값이 있는 경우
		if (comment_str != null) {
			comment_edit.setVisibility(View.VISIBLE);
			comment_save.setVisibility(View.GONE);
			tv2.setText("복음에서 가장 마음에 드는 구절");
			Log.d("saea", comment_str);
			tv_comment.setVisibility(View.VISIBLE);
			tv_comment.setText(comment_str);
			comment.setText(comment_str, TextView.BufferType.EDITABLE);
			comment.setVisibility(View.GONE);
		}else{
			comment_edit.setVisibility(View.GONE);
			comment_save.setVisibility(View.VISIBLE);
			tv2.setVisibility(View.VISIBLE);
			tv2.setText("오늘의 복음에서 가장 마음에 드는 구절을 적어 봅시다.");
			tv_comment.setVisibility(View.GONE);
			comment.setVisibility(View.VISIBLE);
		}

	}


	// 저장버튼 누를때 진행되는 이벤트 - 코멘트 내용을 저장하거나 수정
	OnClickListener listener = new OnClickListener() {

		public void onClick(View v) {
			SQLiteDatabase db = null;
			ContentValues values;
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.bt_save:
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					if (getCurrentFocus() != null) {
						imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
					}
					if ((wifi.isConnected() || mobile.isConnected())) {
						String comment_con = comment.getText().toString();
						String comment_date = typedDate;
						String sentence = (String) btnNetCon.getText();

						ArrayList<Comment> comments = new ArrayList<Comment>();
						String comment_str = null;
						DBManager dbMgr = new DBManager(MainActivity.this);
						dbMgr.dbOpen();
						dbMgr.selectCommentData(CommentDBSqlData.SQL_DB_SELECT_DATA, uid, comment_date, comments);
						dbMgr.dbClose();

						if (!comments.isEmpty()) {
							comment_str = comments.get(0).getComment();
						}
						// 기존 값이 있는 경우 수정하기
						if (comment_str != null) {
							dbMgr.dbOpen();
							dbMgr.updateCommentData(CommentDBSqlData.SQL_DB_UPDATE_DATA, uid, typedDate, comment_con);
							dbMgr.dbClose();

							Toast.makeText(MainActivity.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();

							if (uid != null && uid != "") {
								Server_CommentData.updateComment(MainActivity.this, uid, comment_date, sentence, comment_con);
							}
							// 기존 값이 없는 경우 추가 하기
						} else {
							Comment commentData = new Comment(uid, comment_date, sentence, comment_con);
							dbMgr.dbOpen();
							dbMgr.insertCommentData(CommentDBSqlData.SQL_DB_INSERT_DATA, commentData);
							dbMgr.dbClose();
							Toast.makeText(MainActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
							if (uid != null && uid != "") {
								Server_CommentData.insertComment(MainActivity.this, uid, comment_date, sentence, comment_con);
							}
						}

						comment_edit.setVisibility(View.VISIBLE);
						comment_save.setVisibility(View.GONE);
						tv2.setText("복음에서 가장 마음에 드는 구절");
						Log.d("saea", comment_con);
						tv_comment.setVisibility(View.VISIBLE);
						tv_comment.setText(comment_con);
						comment.setVisibility(View.GONE);
					}else{
						Toast.makeText(MainActivity.this, "인터넷을 연결해주세요", Toast.LENGTH_SHORT).show();
					}

				break;
				case R.id.bt_edit:
					if ((wifi.isConnected() || mobile.isConnected())) {
						comment_edit.setVisibility(View.GONE);
						comment_save.setVisibility(View.VISIBLE);
						tv2.setText("복음에서 가장 마음에 드는 구절을 적어 봅시다.");
						tv_comment.setVisibility(View.GONE);
						comment.setVisibility(View.VISIBLE);
						comment.setText(tv_comment.getText().toString());
					}else{
						Toast.makeText(MainActivity.this, "인터넷을 연결해주세요", Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
	};

	// actionbar 오른쪽 아이콘 추가
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.topmenu_main, menu);
		return true;
	}



}