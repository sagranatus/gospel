package com.yellowpg.gaspel;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.CaldroidSampleCustomFragment;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.etc.getDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class RecordActivity extends AppCompatActivity {

	String textsize;
	TextView date, comment, sentence;
	TextView bg1;
	BottomNavigationView bottomNavigationView;
	 ListSelectorDialog dlg_left;
	 String[] listk_left, listv_left;

	Calendar c1 = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 ");
	String date_navi;
	String day;
    private SessionManager session;
    String uid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		c1 = Calendar.getInstance();
		//	c1.add(Calendar.DATE, 1);
		date_navi = sdf.format(c1.getTime());
		date_navi = date_navi+ getDay.getDay(c1)+"요일";
		Log.d("saea","today"+date_navi);


		// session정보 가져오기
        session = new SessionManager(getApplicationContext());
        uid = session.getUid();


        //actionbar setting
		android.support.v7.app.ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setCustomView(R.layout.actionbar_record);
		TextView mytext = (TextView) findViewById(R.id.mytext);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
		actionbar.setElevation(0);

		date = (TextView) findViewById(R.id.tv_date);
		comment= (TextView) findViewById(R.id.tv_comment);
		sentence = (TextView) findViewById(R.id.tv_oneSentence);
		bg1 = (TextView) findViewById(R.id.tv_bg1);


		// 텍스트사이즈 설정
		SharedPreferences sp2 = getSharedPreferences("setting",0);
		textsize = sp2.getString("textsize", "");
		if(textsize.equals("big")){
			date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			sentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

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
		MenuItem menuItem = menu.getItem(4);
		menuItem.setChecked(true);
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_zero:
						Intent i0 = new Intent(RecordActivity.this, FirstActivity.class);
						startActivity(i0);
						break;
					case R.id.action_one:
						if (date_navi.contains("일요일")) {
							Toast.makeText(RecordActivity.this, "일요일에는 주일의 독서를 해주세요", Toast.LENGTH_SHORT).show();
							Intent i4 = new Intent(RecordActivity.this, RecordActivity.class);
							startActivity(i4);
							break;
						} else {
							Intent i = new Intent(RecordActivity.this, MainActivity.class);
							startActivity(i);
                            break;
						}

					case R.id.action_two:
						if (date_navi.contains("일요일")) {
							Toast.makeText(RecordActivity.this, "일요일에는 주일의 독서를 해주세요", Toast.LENGTH_SHORT).show();
							Intent i4 = new Intent(RecordActivity.this, RecordActivity.class);
							startActivity(i4);
							break;
						} else {
							Intent i2 = new Intent(RecordActivity.this, LectioActivity.class);
							startActivity(i2);
							break;
						}
					case R.id.action_three:
						Intent i3 = new Intent(RecordActivity.this, WeekendActivity.class);
						startActivity(i3);
						break;
					case R.id.action_four:
						Intent i4 = new Intent(RecordActivity.this, RecordActivity.class);
						startActivity(i4);
						break;
				}
				return false;
			}

		});


		// 왼쪽 list클릭시 이벤트 custom dialog setting
		dlg_left  = new ListSelectorDialog(this, "Select an Operator");
		// custom dialog key, value 설정
		listk_left = new String[] {"a", "b", "c"};
		listv_left = new String[] { "설정", "계정정보", "로그아웃"};
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
							 Intent i = new Intent(RecordActivity.this, SettingActivity.class);
							 startActivity(i);
						 }else if(item.equals("계정정보")){
							 Intent i = new Intent(RecordActivity.this, ProfileActivity.class);
							 startActivity(i);
						 }else if (item.equals("로그아웃")) {
							 ProfileActivity.logoutUser(session,RecordActivity.this);
						 }
					 }
				 });
				 return true;
		 }
	 }


	@Override
	protected void onResume() {
		super.onResume();
		// 달력 내용 가져오기
		getRecord();

	}

	// caldroid 달력 fragment를 생성 하고 설정함
	public void getRecord(){
		// fragment를 커스터마이즈한다
		CaldroidFragment caldroidFragment = new CaldroidSampleCustomFragment();

		// Refresh view
		caldroidFragment.refreshView();

		// 이에 대해 설정
		Bundle args = new Bundle();
		Calendar cal = Calendar.getInstance();
		args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
		args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
		args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY); // Tuesday
	//	args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, false);
		caldroidFragment.setArguments(args);
		// fragment를 달력부분에 넣는다
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();
	}

	// actionbar 오른쪽 아이콘 추가
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.topmenu_main, menu);
		return true;
	}


}
