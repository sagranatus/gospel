package com.yellowpg.gaspel;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.DB.LectioInfoHelper;
import com.yellowpg.gaspel.DB.WeekendInfoHelper;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.server.Server_WeekendData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class WeekendActivity extends AppCompatActivity implements View.OnClickListener {

	Calendar c1 = Calendar.getInstance();
	BottomNavigationView bottomNavigationView;
	String day;
	String date_detail;

	ListSelectorDialog dlg_left;
	String[] listk_left, listv_left;

	//현재 해 + 달 구하기
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 ");
	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	String date_val = sdf.format(c1.getTime());
	String textsize;
	Button weekendGaspel,mySentence, saveThought;
	EditText Thought;
	private SessionManager session;
	String uid = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weekend);

		// session정보 가져오기
		session = new SessionManager(getApplicationContext());
		uid = session.getUid();

		//actionbar setting
		android.support.v7.app.ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setCustomView(R.layout.actionbar_weekend);
		TextView mytext = (TextView) findViewById(R.id.mytext);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2980b9")));
		actionbar.setElevation(0);
		// actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeAsUpIndicator(R.drawable.list);

		weekendGaspel = (Button) findViewById(R.id.bt_weekend_gaspel);
		mySentence = (Button) findViewById(R.id.mySentence);
		saveThought = (Button) findViewById(R.id.bt_saveThought);
		Thought = (EditText) findViewById(R.id.et_Thought);

		weekendGaspel.setVisibility(View.GONE);
		mySentence.setVisibility(View.GONE);
		saveThought.setVisibility(View.GONE);
		Thought.setVisibility(View.GONE);

		weekendGaspel.setOnClickListener(listener);
		saveThought.setOnClickListener(listener);

		saveThought.setBackgroundResource(R.drawable.button_bg);
		// 한주복음묵상 데이터 있는지 확인
		checkWeekendRecord();

		// bottomnavigation 뷰 등록
		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

		Menu menu = bottomNavigationView.getMenu();
		MenuItem menuItem_1 = menu.getItem(0);
		MenuItem menuItem_2 = menu.getItem(1);
		MenuItem menuItem_3 = menu.getItem(2);
		MenuItem menuItem_4 = menu.getItem(3);
		menuItem_1.setChecked(false);
		menuItem_2.setChecked(false);
		menuItem_3.setChecked(false);
		menuItem_4.setChecked(false);

		MenuItem menuItem = menu.getItem(2);
		menuItem.setChecked(true);
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_one:
						Intent i = new Intent(WeekendActivity.this, MainActivity.class);
						startActivity(i);
						break;
					case R.id.action_two:
						Intent i2 = new Intent(WeekendActivity.this, LectioActivity.class);
						startActivity(i2);
						break;
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
		listv_left = new String[] {"설정", "나의 상태", "수정"};

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

		findViewById(R.id.mySentence).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				if(getCurrentFocus() != null) {
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				}
				return true;
			}
		});

		// 텍스트사이즈 설정
		SharedPreferences sp2 = getSharedPreferences("setting",0);
		textsize = sp2.getString("textsize", "");
		if(textsize.equals("big")){
			weekendGaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			mySentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			Thought.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		}else{

		}

	}

	public void checkWeekendRecord(){
		SQLiteDatabase db;
		ContentValues values;
		WeekendInfoHelper weekendInfoHelper = new WeekendInfoHelper(WeekendActivity.this);
		c1.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);

		c1.add(c1.DATE,7);

		date_val = sdf.format(c1.getTime());
		date_detail = date_val+getDay()+"요일";
		Log.d("saea", date_detail);

		try{
			String mysentence = null;
			String mythought = null;
			db = weekendInfoHelper.getReadableDatabase();
			String[] columns = {"mysentence", "mythought"};
			String whereClause = "date = ?";
			String[] whereArgs = new String[] {
					date_detail
			};
			Cursor cursor = db.query("weekend", columns,  whereClause, whereArgs, null, null, null);

			while(cursor.moveToNext()){
				mysentence = cursor.getString(0);
				mythought = cursor.getString(1);
				Log.d("saea", mysentence+mythought);

			}
			// 기존 값이 있는 경우 보여지기
            if(mysentence != null){
                Log.d("saea", "한주복음묵상 있음");
                mySentence.setVisibility(View.VISIBLE);
                saveThought.setVisibility(View.VISIBLE);
                Thought.setVisibility(View.VISIBLE);
                mySentence.setText(mysentence);
                if(mythought != null){
					Thought.setText(mythought);
				}

            }else{
                Log.d("saea", "한주복음묵상 없음");
                weekendGaspel.setVisibility(View.VISIBLE);
            }
			cursor.close();
			weekendInfoHelper.close();
		}catch(Exception e){

		}
	}

	// exp : 요일 얻어오기
	public String getDay(){
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

	// 날짜 이전 이후 선택시 값 변경 이벤트
	View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.bt_weekend_gaspel:
					Intent intent = new Intent(WeekendActivity.this, LectioActivity.class);
					String thisweekend = sdf2.format(c1.getTime());
					intent.putExtra("date",thisweekend);
					intent.putExtra("date_detail",date_detail);
					WeekendActivity.this.startActivity(intent);
					break;
				case R.id.bt_saveThought:
					SQLiteDatabase db;
					ContentValues values;
					WeekendInfoHelper weekendInfoHelper = new WeekendInfoHelper(WeekendActivity.this);
					String weekend_date = date_detail;
					String mythought = Thought.getText().toString();
					String mysentence = mySentence.getText().toString();
					try{
						db=weekendInfoHelper.getWritableDatabase();
						values = new ContentValues();
						values.put("mythought",  mythought);
						String where = "date=?";
						String[] whereArgs = new String[] {weekend_date};
						db.update("weekend", values, where, whereArgs);
						weekendInfoHelper.close();
					}catch(Exception e){
						e.printStackTrace();
					}

					if(uid != null && uid != ""){
						Server_WeekendData.updateWeekend(WeekendActivity.this, uid, weekend_date, mysentence, mythought);
					}

					Toast.makeText(WeekendActivity.this, "묵상내용이 저장되었습니다.", Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};

	@Override
	public void onClick(View view) {

	}

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
							}else if(item.equals("나의 상태")){
								Intent i = new Intent(WeekendActivity.this, StatusActivity.class);
								startActivity(i);
							}else if(item.equals("수정")){
								Intent intent = new Intent(WeekendActivity.this, LectioActivity.class);
								String thisweekend = sdf2.format(c1.getTime());
								intent.putExtra("date",thisweekend);
								intent.putExtra("date_detail",date_detail);
								WeekendActivity.this.startActivity(intent);


							}
						}
					});


				return true;
		}
	}
}




