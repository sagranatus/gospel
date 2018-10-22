package com.yellowpg.gaspel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FourthActivity extends FragmentActivity{

	final static String TAG = "FOURTHActivity";
	String textsize;
	TextView date, comment, sentence;
	TextView date2, sentence2, bg1, bg2, bg3, sum1, sum2, js1, js2;
	TextView today;
	/**
	 * Create the main activity.
	 * @param savedInstanceState previously saved instance data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fourth);

		date = (TextView) findViewById(R.id.tv_date);
		comment= (TextView) findViewById(R.id.tv_comment);
		sentence = (TextView) findViewById(R.id.tv_oneSentence);

		date2 = (TextView) findViewById(R.id.tv_date2);
		sentence2 = (TextView) findViewById(R.id.tv_oneSentence2);
		bg1 = (TextView) findViewById(R.id.tv_bg1);
		bg2 = (TextView) findViewById(R.id.tv_bg2);
		bg3 = (TextView) findViewById(R.id.tv_bg3);
		sum1 = (TextView) findViewById(R.id.tv_sum1);
		sum2 = (TextView) findViewById(R.id.tv_sum2);
		js1 = (TextView) findViewById(R.id.tv_js1);
		js2 = (TextView) findViewById(R.id.tv_js2);

		today = (TextView)findViewById(R.id.tv_today);
		// exp : bottombar 설정
		BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
		bottomBar.setDefaultTab(R.id.tab4);
		bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
			@Override

			public void onTabSelected(@IdRes int tabId) {
				if (tabId == R.id.tab1) {
					//아래에는 다른날짜 복음을 보는 상태에서 오늘의 복음을 누르면 그것에 대한 내용이 나오게끔 하는 부분이다.
					Intent i = new Intent(FourthActivity.this, MainActivity.class);
					Calendar c1 = Calendar.getInstance();
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
					String date_val2 = sdf2.format(c1.getTime());
					i.putExtra("date",date_val2);
					startActivity(i);
				}else if(tabId == R.id.tab2){
					Intent i = new Intent(FourthActivity.this, SecondActivity.class);
					startActivity(i);
				}else if(tabId == R.id.tab3){
					Intent i = new Intent(FourthActivity.this, LectioActivity.class);
					startActivity(i);
				}else if(tabId == R.id.tab4){

				}
			}
		});

		// exp : 텍스트사이즈 설정
		SharedPreferences sp2 = getSharedPreferences("setting",0);
		textsize = sp2.getString("textsize", "");
		if(textsize.equals("small")){
			date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			sentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			date2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			sentence2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			today.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		}else if(textsize.equals("big")){
			date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			sentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			date2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			sentence2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
			today.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
		}else if(textsize.equals("toobig")){
			date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
			sentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			date2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			sentence2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
			bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			today.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
		}else{

		}

		// exp : caldroid 달력 fragment를 생성 하고 설정함
		CaldroidFragment caldroidFragment = new CaldroidFragment();
		// cf : 이에 대해 설정하는 부분
		Bundle args = new Bundle();
		Calendar cal = Calendar.getInstance();
		args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
		args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
		caldroidFragment.setArguments(args);

		// cf : 이 fragment를 커스터마이즈한다
		caldroidFragment = new CaldroidSampleCustomFragment(); //changed 없었던 것임.
		// Refresh view
		caldroidFragment.refreshView();

		// cf : 이 fragment를 activity_fourth의 달력부분에 넣는다
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();




	}



	@Override
public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.third, menu);
	return true;
}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
            case R.id.action_menu_01:
                Intent i = new Intent(FourthActivity.this, ExplainActivity.class);
                startActivity(i);
                break;
            case R.id.action_menu_02:
                Intent i2 = new Intent(FourthActivity.this, ThirdActivity.class);
                startActivity(i2);
                break;
            case R.id.action_menu_03:
                Intent i3 = new Intent(FourthActivity.this, StatusActivity.class);
                startActivity(i3);
                break;
		}

		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onResume() {
		super.onResume();
	}

}
