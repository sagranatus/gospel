package com.yellowpg.gaspel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.adapter.DailyAdapter;
import com.yellowpg.gaspel.data.Daily;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SecondActivity extends Activity {

	private ArrayList<Daily> data = null;
	private DailyAdapter adapter = null;
	private ListView lv = null;
	Button daily;
	Calendar c1 = Calendar.getInstance();
	BottomNavigationView bottomNavigationView;

	//현재 해 + 달 구하기
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
	SimpleDateFormat sdf_y = new SimpleDateFormat("yyyy");
	SimpleDateFormat sdf_m = new SimpleDateFormat("MM");
	String date_val = sdf.format(c1.getTime());
	String year = sdf_y.format(c1.getTime());
	String month = sdf_m.format(c1.getTime());
	String textsize;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		//getActionBar().setDisplayShowTitleEnabled(false);// cf : 맨위에 제목 안보이게 하는 것
		lv = (ListView) findViewById(R.id.lv_daily);
		daily = (Button) findViewById(R.id.bt_daily);
		getGaspel();
		// exp : bottombar 설정
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

		MenuItem menuItem = menu.getItem(2);
		menuItem.setChecked(true);
		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.action_one:
						Intent i = new Intent(SecondActivity.this, MainActivity.class);
						startActivity(i);
						break;
					case R.id.action_two:
						Intent i2 = new Intent(SecondActivity.this, LectioActivity.class);
						startActivity(i2);
						break;
					case R.id.action_three:
						Intent i3 = new Intent(SecondActivity.this, SecondActivity.class);
						startActivity(i3);
						break;
					case R.id.action_four:
						Intent i4 = new Intent(SecondActivity.this, FourthActivity.class);
						startActivity(i4);
						break;
				}
				return false;
			}

		});

		//daily
		daily.setText(year+"년 "+month+"월의 매일복음");

		// exp : textsize 설정
		SharedPreferences sp2 = getSharedPreferences("setting",0);
		textsize = sp2.getString("textsize", "");
		if(textsize.equals("small")){
			daily.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		}else if(textsize.equals("big")){
			daily.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		}else if(textsize.equals("toobig")){
			daily.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
		}else{

		}


	}


	public void getGaspel() {
		// Tag used to cancel the request
		String tag_string_req = "req_getgaspeldaily";
		StringRequest strReq = new StringRequest(Request.Method.POST,
				AppConfig.URL_DAILY, new Response.Listener<String>() { // URL_LOGIN : "http://192.168.116.1/android_login_api/login.php";
			boolean error;
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jObj = new JSONObject(response);
					error = jObj.getBoolean("error");

					// Check for error node in json
					if (!error) { // error가 false인 경우에 데이터얻기 성공

						// Now store the user in SQLite
					//	Toast.makeText(SecondActivity.this, gaspel_sentence, Toast.LENGTH_SHORT).show();

						Calendar cal = Calendar.getInstance();
						int maxday = cal.getMaximum(Calendar.DAY_OF_MONTH); // 마지막날
						data = new ArrayList<Daily>();
						for(int i=0; i<maxday; i++){
							data.add(new Daily(jObj.getString("created_at"+i), jObj.getString("sentence"+i), jObj.getString("created_at"+i)));
						}

						adapter = new DailyAdapter(SecondActivity.this, R.layout.custom_layout, data, textsize);
						lv = (ListView) findViewById(R.id.lv_daily);
						lv.setAdapter(adapter);

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
			//	Log.e(TAG, "Login Error: " + error.getMessage());
			}

		}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting parameters to login url
				Map<String, String> params = new HashMap<String, String>();

				return params;
			}

		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
				Intent i = new Intent(SecondActivity.this, ExplainActivity.class);
				startActivity(i);
				break;
			case R.id.action_menu_02:
				Intent i2 = new Intent(SecondActivity.this, ThirdActivity.class);
				startActivity(i2);
				break;
			case R.id.action_menu_03:
				Intent i3 = new Intent(SecondActivity.this, StatusActivity.class);
				startActivity(i3);
				break;
		}

		return super.onOptionsItemSelected(item);
	}
}




