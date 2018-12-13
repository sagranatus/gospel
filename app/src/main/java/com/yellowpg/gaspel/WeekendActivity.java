package com.yellowpg.gaspel;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.DB.LectioDBSqlData;
import com.yellowpg.gaspel.DB.WeekendDBSqlData;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.data.Weekend;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.etc.getDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class WeekendActivity extends AppCompatActivity {
	Calendar c1 = Calendar.getInstance();
	BottomNavigationView bottomNavigationView;
	String day;
	String date_detail;

	ListSelectorDialog dlg_left;
	String[] listk_left, listv_left;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 ");
	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	String date_val = sdf.format(c1.getTime());
	String textsize;
	Button weekendGaspel;
	LinearLayout after_save;
	Button oneSentence, edit;
	TextView after_save_tv1, after_save_tv2, after_save_tv3, after_save_tv4, after_save_tv5, after_save_tv6, after_save_tv7, after_save_tv8;
	LinearLayout ll_main;
	String date_navi;
	private SessionManager session;
	NetworkInfo mobile;
	NetworkInfo wifi;
	String uid = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weekend);

		// session정보 가져오기
		session = new SessionManager(getApplicationContext());
		uid = session.getUid();

		c1 = Calendar.getInstance();
	//	c1.add(Calendar.DATE, 1);
		date_navi = sdf.format(c1.getTime());
		date_navi = date_navi+getDay.getDay(c1)+"요일";
		Log.d("saea","today"+date_navi);

		// 인터넷연결 확인
		ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		//actionbar setting
		android.support.v7.app.ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setCustomView(R.layout.actionbar_weekend);
		TextView mytext = (TextView) findViewById(R.id.mytext);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
		actionbar.setElevation(0);

		ll_main = (LinearLayout) findViewById(R.id.ll_main);
		after_save = (LinearLayout) findViewById(R.id.after_save);

		weekendGaspel = (Button) findViewById(R.id.bt_weekend_gaspel);
		oneSentence = (Button) findViewById(R.id.bt_onesentence);
		edit  = (Button) findViewById(R.id.bt_edit);
		after_save.setVisibility(View.GONE);

		after_save_tv1 = (TextView) findViewById(R.id.after_save_tv1);
		after_save_tv2 = (TextView) findViewById(R.id.after_save_tv2);
		after_save_tv3 = (TextView) findViewById(R.id.after_save_tv3);
		after_save_tv4 = (TextView) findViewById(R.id.after_save_tv4);
		after_save_tv5 = (TextView) findViewById(R.id.after_save_tv5);
		after_save_tv6 = (TextView) findViewById(R.id.after_save_tv6);
		after_save_tv7 = (TextView) findViewById(R.id.after_save_tv7);
		after_save_tv8 = (TextView) findViewById(R.id.after_save_tv8);

		weekendGaspel.setOnClickListener(listener);
		edit.setOnClickListener(listener);
		weekendGaspel.setBackgroundResource(R.drawable.button_bg2);
		edit.setBackgroundResource(R.drawable.button_bg2);

		after_save_tv1.setBackgroundResource(R.drawable.edit_bg_white);
		after_save_tv2.setBackgroundResource(R.drawable.edit_bg_white);
		after_save_tv3.setBackgroundResource(R.drawable.edit_bg_white);
		after_save_tv4.setBackgroundResource(R.drawable.edit_bg_white);
		after_save_tv5.setBackgroundResource(R.drawable.edit_bg_white);
		after_save_tv6.setBackgroundResource(R.drawable.edit_bg_white);
		after_save_tv7.setBackgroundResource(R.drawable.edit_bg_white);
		after_save_tv8.setBackgroundResource(R.drawable.edit_bg_white);


		// 텍스트사이즈 설정
		SharedPreferences sp2 = getSharedPreferences("setting",0);
		textsize = sp2.getString("textsize", "");
		if(textsize.equals("big")){
			after_save_tv1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			after_save_tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			after_save_tv3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			after_save_tv4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			after_save_tv5.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			after_save_tv6.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			after_save_tv7.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			after_save_tv8.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			oneSentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);

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
		MenuItem menuItem = menu.getItem(3);
		menuItem.setChecked(true);
		menuItem.setChecked(true);
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_zero:
						Intent i0 = new Intent(WeekendActivity.this, FirstActivity.class);
						startActivity(i0);
						break;
					case R.id.action_one:
						if (date_navi.contains("일요일")) {
							Toast.makeText(WeekendActivity.this, "일요일에는 주일의 독서를 해주세요", Toast.LENGTH_SHORT).show();
							Intent i3 = new Intent(WeekendActivity.this, WeekendActivity.class);
							startActivity(i3);
							break;
						} else {
							Intent i = new Intent(WeekendActivity.this, MainActivity.class);
							startActivity(i);
							break;
						}
					case R.id.action_two:
						if (date_navi.contains("일요일")) {
							Toast.makeText(WeekendActivity.this, "일요일에는 주일의 독서를 해주세요", Toast.LENGTH_SHORT).show();
							Intent i3 = new Intent(WeekendActivity.this, WeekendActivity.class);
							startActivity(i3);
							break;
						} else {
							Intent i2 = new Intent(WeekendActivity.this, LectioActivity.class);
							startActivity(i2);
							break;
						}
					case R.id.action_three:
						Intent i3 = new Intent(WeekendActivity.this, WeekendActivity.class);
						startActivity(i3);
						break;
					case R.id.action_four:
						Intent i4 = new Intent(WeekendActivity.this, RecordActivity.class);
						startActivity(i4);
						break;
				}
				return true;
			}

		});


		// 왼쪽 list클릭시 이벤트
		dlg_left  = new ListSelectorDialog(this, "Select an Operator");

		// custom dialog key, value 설정
		listk_left = new String[] {"a", "b", "c"};
		listv_left = new String[] { "설정", "계정정보", "로그아웃"};

		// 화면 클릭시 soft keyboard hide
		findViewById(R.id.ll).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				if(getCurrentFocus() != null) {
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				}
				return true;
			}
		});

		// 데이터 있는지 확인
		checkWeekendRecord();


	}

	public void checkWeekendRecord(){

		if(c1.get(Calendar.DAY_OF_WEEK) == 1){ // 일요일인 경우에는 오늘 날짜로 가져옴

		}else{
			c1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			c1.add(c1.DATE,7);
		}

		date_val = sdf.format(c1.getTime());
		date_detail = date_val+ getDay.getDay(c1)+"요일";
		Log.d("saea", date_detail);
		String mysentence = null;

		ArrayList<Weekend> weekends = new ArrayList<Weekend>();
		DBManager dbMgr = new DBManager(WeekendActivity.this);
		dbMgr.dbOpen();
		dbMgr.selectWeekendData(WeekendDBSqlData.SQL_DB_SELECT_DATA, uid, date_detail, weekends);
		dbMgr.dbClose();

		if(!weekends.isEmpty()){
			mysentence = weekends.get(0).getMySentence();
		}

		ArrayList<Lectio> lectios = new ArrayList<Lectio>();
		dbMgr.dbOpen();
		dbMgr.selectLectioData(LectioDBSqlData.SQL_DB_SELECT_DATA, uid, date_detail , lectios);
		dbMgr.dbClose();
		String bg1_str = null;
		String bg2_str = null;
		String bg3_str = null;
		String sum1_str = null;
		String sum2_str = null;
		String js1_str = null;
		String js2_str = null;
		String onesentence = null;

		if(!lectios.isEmpty()){
			bg1_str = lectios.get(0).getBg1();
			bg2_str = lectios.get(0).getBg2();
			bg3_str = lectios.get(0).getBg3();
			sum1_str = lectios.get(0).getSum1();
			sum2_str = lectios.get(0).getSum2();
			js1_str = lectios.get(0).getJs1();
			js2_str = lectios.get(0).getJs2();
			onesentence = lectios.get(0).getOneSentence();

			// edittext에 가져오기
			oneSentence.setText(onesentence);
			after_save_tv1.setText(bg1_str);
			after_save_tv2.setText(bg2_str);
			after_save_tv3.setText(bg3_str);
			after_save_tv4.setText(sum1_str);
			after_save_tv5.setText(sum2_str);
			after_save_tv6.setText(js1_str);
			after_save_tv7.setText(js2_str);
		}

		// 기존 값이 있는 경우 보여지기
		if(mysentence != null){
			oneSentence.setVisibility(View.VISIBLE);
			ll_main.setVisibility(View.GONE);
			after_save.setVisibility(View.VISIBLE);
			after_save_tv8.setText(mysentence);
			Log.d("saea", "한주복음묵상 있음");

		}else{
			oneSentence.setVisibility(View.GONE);
			Log.d("saea", "한주복음묵상 없음");
			weekendGaspel.setVisibility(View.VISIBLE);
		}

	}


	View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.bt_weekend_gaspel:
					if ((wifi.isConnected() || mobile.isConnected())) {
						Intent intent = new Intent(WeekendActivity.this, LectioActivity.class);
						String thisweekend = sdf2.format(c1.getTime());
						intent.putExtra("date", thisweekend);
						intent.putExtra("date_detail", date_detail);
						WeekendActivity.this.startActivity(intent);
					}else{
						Toast.makeText(WeekendActivity.this, "인터넷을 연결해주세요", Toast.LENGTH_SHORT).show();
					}
					break;
				case R.id.bt_edit:
					if ((wifi.isConnected() || mobile.isConnected())) {
					Intent intent2 = new Intent(WeekendActivity.this, LectioActivity.class);
					String thisweekend2 = sdf2.format(c1.getTime());
					intent2.putExtra("date",thisweekend2);
					intent2.putExtra("date_detail",date_detail);
					intent2.putExtra("edit",true);
					WeekendActivity.this.startActivity(intent2);
					}else{
						Toast.makeText(WeekendActivity.this, "인터넷을 연결해주세요", Toast.LENGTH_SHORT).show();
					}
					break;
			}
		}
	};


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
							if(item.equals("설정")){
								Intent i = new Intent(WeekendActivity.this, SettingActivity.class);
								startActivity(i);
							}else if(item.equals("계정정보")){
								Intent i = new Intent(WeekendActivity.this, ProfileActivity.class);
								startActivity(i);
							}else if (item.equals("로그아웃")) {
								ProfileActivity.logoutUser(session,WeekendActivity.this);
							}
						}
					});


				return true;
		}
	}

	// actionbar 오른쪽 추가
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.topmenu_main, menu);
		return true;
	}

}




