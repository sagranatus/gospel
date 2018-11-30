package com.yellowpg.gaspel;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.DB.CommentInfoHelper;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.DB.LectioInfoHelper;
import com.yellowpg.gaspel.DB.UserDBSqlData;
import com.yellowpg.gaspel.DB.WeekendInfoHelper;
import com.yellowpg.gaspel.data.UserData;
import com.yellowpg.gaspel.data.Weekend;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hirondelle.date4j.DateTime;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    final static String TAG = "FirstActivity";
    private SessionManager session;
    String uid = null;

    BottomNavigationView bottomNavigationView;
    ListSelectorDialog dlg_left;
    String[] listk_left, listv_left;
    SharedPreferences pref;
    Calendar c1 = Calendar.getInstance();
    //현재 해 + 달 구하기
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    String date_val = sdf.format(c1.getTime());
    //현재 해 + 달 구하기
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy년 MM월 dd일 ");
    String date_val1 = sdf1.format(c1.getTime());

    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    String date_val2 = sdf2.format(c1.getTime());
    String date_val2_yesterday;
    Button todaysentence, mysentence;
    TextView comment, lectio;

    static String day;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        //session 정보 가져오기
        session = new SessionManager(getApplicationContext());
        uid = session.getUid();

        // 인터넷연결 확인
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //actionbar setting
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2980b9")));
        actionbar.setElevation(0);

        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.list);

        todaysentence = (Button) findViewById(R.id.bt_today);
        mysentence = (Button) findViewById(R.id.bt_weekend);
        comment = (TextView) findViewById(R.id.tv_comment);
        lectio = (TextView) findViewById(R.id.tv_lectio);
        pref = FirstActivity.this.getSharedPreferences("todaysentence", 0);
        String saved_todaysentence = pref.getString(date_val2, "");
        // 오늘 구절은 sharedpreference에 저장해둔다.
        if(saved_todaysentence != ""){
            todaysentence.setText(saved_todaysentence);
            Log.d("saea", "saved already");
        }else{
            // 인터넷연결된 상태에서만 데이터 가져오기
            if ((wifi.isConnected() || mobile.isConnected())) {
                Log.d("saea", "get data");
                getGaspel(date_val2);
            } else {
                todaysentence.setText("인터넷을 연결해주세요");
            }
        }

        // textsize 설정
        SharedPreferences sp = getSharedPreferences("setting",0);
       String textsize = sp.getString("textsize", "");
        if(textsize.equals("big")){
            todaysentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            mysentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            comment.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            lectio.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        }else{

        }

        // 각 코멘트, 렉시오, 한주묵상구절을 가져온다.
        getComments(date_val2);
        getLectio(date_val2);
        getWeekend(date_val2);

        // 오늘의 기록된 값에 따라 나오는 아이콘이 달라진다
        TextView exhausted_name = (TextView) findViewById(R.id.exhausted_name);
        TextView walking_name = (TextView) findViewById(R.id.walking_name);
        TextView happy_name = (TextView) findViewById(R.id.happy_name);
        if(i == 0){
            ImageView exhausted = (ImageView) findViewById(R.id.exhausted_person);
            exhausted.setVisibility(View.VISIBLE);
            exhausted_name.setVisibility(View.VISIBLE);
        }else if(i == 1){
            ImageView walking = (ImageView) findViewById(R.id.walking_person);
            walking.setVisibility(View.VISIBLE);
            walking_name.setVisibility(View.VISIBLE);
        }else if(i >= 2){
            ImageView happy = (ImageView) findViewById(R.id.happy_person);
            happy.setVisibility(View.VISIBLE);
            happy_name.setVisibility(View.VISIBLE);
        }

        // 로그인한 경우에는 값을 가져온다.
        if(uid != "" && uid != null){
            ArrayList<UserData> userdata = new ArrayList<UserData>();
            DBManager dbMgr = new DBManager(FirstActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectUserData(UserDBSqlData.SQL_DB_SELECT_DATA, uid, userdata);
            dbMgr.dbClose();

            UserData udata = userdata.get(0);
            walking_name.setText(udata.getName()+"\n"+udata.getChristName());
            exhausted_name.setText(udata.getName()+"\n"+udata.getChristName());
            happy_name.setText(udata.getName()+"\n"+udata.getChristName());
        }else{
            walking_name.setOnClickListener(this);
            exhausted_name.setOnClickListener(this);
            happy_name.setOnClickListener(this);
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
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_zero:
                        Intent i0 = new Intent(FirstActivity.this, FirstActivity.class);
                        startActivity(i0);
                        break;
                    case R.id.action_one:
                        Intent i = new Intent(FirstActivity.this, MainActivity.class);
                        startActivity(i);
                        break;
                    case R.id.action_two:
                        Intent i2 = new Intent(FirstActivity.this, LectioActivity.class);
                        startActivity(i2);
                        break;
                    case R.id.action_three:
                        Intent i3 = new Intent(FirstActivity.this, WeekendActivity.class);
                        startActivity(i3);
                        break;
                    case R.id.action_four:
                        Intent i4 = new Intent(FirstActivity.this, RecordActivity.class);
                        startActivity(i4);
                        break;
                }
                return true;
            }

        });


        // 왼쪽 list클릭시 이벤트 custom dialog setting
        dlg_left  = new ListSelectorDialog(this, "Select an Operator");

        // custom dialog key, value 설정
        listk_left = new String[] {"a", "b", "c"};
        listv_left = new String[] { "설정", "나의 상태", "계정정보"};

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
                            Intent i = new Intent(FirstActivity.this, SettingActivity.class);
                            startActivity(i);
                        }else if(item.equals("나의 상태")){
                            Intent i = new Intent(FirstActivity.this, StatusActivity.class);
                            startActivity(i);
                        }else if(item.equals("계정정보")){
                            Intent i = new Intent(FirstActivity.this, LoginActivity.class);
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

    // 그날 복음 구절 가져오기
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
                                String gaspel_sentence = jObj.getString("sentence");
                                todaysentence.setText(gaspel_sentence);

                                //preference에 저장한다.
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(date, gaspel_sentence);
                                // 전날 preference값을 지운다.
                                Calendar c1 = Calendar.getInstance();
                                c1.add(Calendar.DATE, -1);
                                date_val2_yesterday = sdf1.format(c1.getTime());
                                editor.remove(date_val2_yesterday);

                                // commit changes
                                editor.commit();
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

        String comment_str = null;
        CommentInfoHelper commentInfoHelper = new CommentInfoHelper(this);
        SQLiteDatabase db = null;
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
            // 기존 값이 있는 경우 수정하기
            if (comment_str != null) {
                Log.d("saea", comment_str);
                comment.setText(comment_str);
                i++;
            }else{
                comment.setText(Html.fromHtml("<font color=\"#999999\">오늘의 복음 말씀새기기 하기</font>"));
                comment.setOnClickListener(this);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public void getLectio(String date){

        Date origin_date = null;
        try {
            origin_date = sdf2.parse(date);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        String date_aft = sdf1.format(origin_date) + getDay() + "요일";
        // cf : 렉시오 디비나 부분
        LectioInfoHelper lectioInfoHelper = new LectioInfoHelper(FirstActivity.this);
        SQLiteDatabase db2;
        String bg1_str = null;
        String bg2_str= null;
        String bg3_str= null;
        String sum1_str= null;
        String sum2_str= null;
        String js1_str= null;
        String js2_str= null;
        String date_str = null;
        String onesentence_str = null;
        try{

            db2 = lectioInfoHelper.getReadableDatabase();
            String[] columns = {"bg1", "bg2", "bg3", "sum1", "sum2", "js1", "js2"};
            String whereClause = "date = ?";
            String[] whereArgs = new String[] {
                    date_aft
            };
            Cursor cursor = db2.query("lectio", columns,  whereClause, whereArgs, null, null, null);

            if(cursor != null){
                while(cursor.moveToNext()){
                    bg1_str = cursor.getString(0);
                    bg2_str = cursor.getString(1);
                    bg3_str = cursor.getString(2);
                    sum1_str = cursor.getString(3);
                    sum2_str = cursor.getString(4);
                    js1_str = cursor.getString(5);
                    js2_str = cursor.getString(6);
                }
                if(sum2_str != null){
                    lectio.setText(sum2_str+"\n\""+js2_str+"\"");
                    i++;
                }else{
                    lectio.setText(Html.fromHtml("<font color=\"#999999\">오늘의 복음 렉시오 디비나 하기</font>"));
                    lectio.setOnClickListener(this);
                }

            }

            cursor.close();
            lectioInfoHelper.close();
        }
        catch(Exception e){

        }
    }

    public void getWeekend(String date){
        c1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        c1.add(c1.DATE,7);

        date_val = sdf1.format(c1.getTime());
        String date_detail = date_val+getDay()+"요일";
        Log.d("saea", date_detail);
        // cf : 렉시오 디비나 부분
        WeekendInfoHelper weekendInfoHelper = new WeekendInfoHelper(FirstActivity.this);
        SQLiteDatabase db;
        String mysentence_str = null;
        String mythought_str= null;
        try{
            db = weekendInfoHelper.getReadableDatabase();
            String[] columns = {"mysentence", "mythought"};
            String whereClause = "date = ?";
            String[] whereArgs = new String[] {
                    date_detail
            };
            Cursor cursor = db.query("weekend", columns,  whereClause, whereArgs, null, null, null);

            while(cursor.moveToNext()){
                mysentence_str = cursor.getString(0);
                mythought_str  = cursor.getString(1);
                Log.d("saea", mysentence_str +mythought_str );

            }
            // 기존 값이 있는 경우 보여지기
            if(mysentence_str != null){
                Log.d("saea", "한주복음묵상 있음");
                i++;
                mysentence.setText(mysentence_str);

            }else{
                Log.d("saea", "한주복음묵상 없음");
                mysentence.setText(Html.fromHtml("<font color=\"#999999\">이번주 복음묵상 하기</font>"));
                mysentence.setOnClickListener(this);
            }
            cursor.close();
            weekendInfoHelper.close();
        }catch(Exception e){

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_comment:
                Intent i0 = new Intent(FirstActivity.this, MainActivity.class);
                startActivity(i0);
                break;
            case R.id.tv_lectio:
                Intent i1 = new Intent(FirstActivity.this, LectioActivity.class);
                startActivity(i1);
                break;
            case R.id.bt_weekend:
                Intent i2 = new Intent(FirstActivity.this, WeekendActivity.class);
                startActivity(i2);
                break;
            case R.id.walking_name:
                Intent i3 = new Intent(FirstActivity.this, LoginActivity.class);
                startActivity(i3);
                break;
            case R.id.exhausted_name:
                Intent i4 = new Intent(FirstActivity.this, LoginActivity.class);
                startActivity(i4);
                break;
            case R.id.happy_name:
                Intent i5 = new Intent(FirstActivity.this, LoginActivity.class);
                startActivity(i5);
                break;
        };
    }
}
