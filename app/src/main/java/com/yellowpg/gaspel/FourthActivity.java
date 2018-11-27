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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.CaldroidSampleCustomFragment;
import com.yellowpg.gaspel.etc.ListSelectorDialog;

import java.util.Calendar;

// calendar
public class FourthActivity extends AppCompatActivity {

	final static String TAG = "FOURTHActivity";
	String textsize;
	TextView date, comment, sentence;
	TextView date2, sentence2, bg1;
	TextView today;
	BottomNavigationView bottomNavigationView;
	 ListSelectorDialog dlg_left;
	 String[] listk_left, listv_left;

	/**
	 * Create the main activity.
	 * @param savedInstanceState previously saved instance data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fourth);

		//actionbar setting
		android.support.v7.app.ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionbar.setCustomView(R.layout.actionbar_record);
		TextView mytext = (TextView) findViewById(R.id.mytext);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2980b9")));
		actionbar.setElevation(0);

		// actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeAsUpIndicator(R.drawable.list);

		date = (TextView) findViewById(R.id.tv_date);
		comment= (TextView) findViewById(R.id.tv_comment);
		sentence = (TextView) findViewById(R.id.tv_oneSentence);

		date2 = (TextView) findViewById(R.id.tv_date2);
		sentence2 = (TextView) findViewById(R.id.tv_oneSentence2);
		bg1 = (TextView) findViewById(R.id.tv_bg1);

		today = (TextView)findViewById(R.id.tv_today);

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

		MenuItem menuItem = menu.getItem(3);
		menuItem.setChecked(true);
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_one:
						Intent i = new Intent(FourthActivity.this, MainActivity.class);
						startActivity(i);
						break;
					case R.id.action_two:
						Intent i2 = new Intent(FourthActivity.this, LectioActivity.class);
						startActivity(i2);
						break;
					case R.id.action_three:
						Intent i3 = new Intent(FourthActivity.this, SecondActivity.class);
						startActivity(i3);
						break;
					case R.id.action_four:
						Intent i4 = new Intent(FourthActivity.this, FourthActivity.class);
						startActivity(i4);
						break;
				}
				return false;
			}

		});

		// 텍스트사이즈 설정
		SharedPreferences sp2 = getSharedPreferences("setting",0);
		textsize = sp2.getString("textsize", "");
		if(textsize.equals("big")){
			date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			sentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			date2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			sentence2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			today.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

		}else{

		}

		// 왼쪽 list 클릭시 이벤트
		dlg_left  = new ListSelectorDialog(this, "Select an Operator");

		listk_left = new String[] {"a", "b"};
		listv_left = new String[] {"설정", "나의 상태" };
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
							 Intent i = new Intent(FourthActivity.this, ThirdActivity.class);
							 startActivity(i);
						 }else if(item.equals("나의 상태")){
							 Intent i = new Intent(FourthActivity.this, StatusActivity.class);
							 startActivity(i);
						 }/*else if(item.equals("기록 삭제")){
							 try{
								 CommentInfoHelper commentInfoHelper;
								 commentInfoHelper = new CommentInfoHelper(FourthActivity.this);
								 SQLiteDatabase db;
								 db=commentInfoHelper.getWritableDatabase();

								 String[] whereArgs = new String[] {date.getText().toString()};
								 db.execSQL("DELETE FROM comment "+"WHERE date=?", whereArgs);
								 commentInfoHelper.close();
								 getRecord();
							 }catch(Exception e){
								 e.printStackTrace();
							 }
						 }*/
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
		CaldroidFragment caldroidFragment = new CaldroidFragment();

		// cf : 이에 대해 설정하는 부분
		Bundle args = new Bundle();
		Calendar cal = Calendar.getInstance();
		args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
		args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
		caldroidFragment.setArguments(args);

		TextView textView = caldroidFragment.getMonthTitleTextView();

		int sizeInDP = 15;
		int marginInDp = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources()
						.getDisplayMetrics());

		// fragment를 커스터마이즈한다
		caldroidFragment = new CaldroidSampleCustomFragment();
		// Refresh view
		caldroidFragment.refreshView();

		// fragment를 이 activity의 달력부분에 넣는다
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();
	}

}
