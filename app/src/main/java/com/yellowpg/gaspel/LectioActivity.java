package com.yellowpg.gaspel;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class LectioActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
LinearLayout ll_notyet, ll_first, ll1, ll2, ll3, ll_upper, ll_date;
    Button bt_notyet;
    EditText bg1, bg2, bg3;
    EditText sum1, sum2;
    EditText js1, js2;
    Button save,showgaspel, stopgaspel;
    Button date;
    ImageButton before, after;
    InputMethodManager imm;
    String urlAddr = "http://i.catholic.or.kr/missa/";
    TextView contents;
    TextView q1, q2, q3;
    Button onesentence;
    String day;
    Calendar c1 = Calendar.getInstance();
    //현재 해 + 달 구하기
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 ");
    String date_val = sdf.format(c1.getTime());

    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    String date_val2 = sdf2.format(c1.getTime());


    BottomBar bottomBar;

    LectioInfoHelper lectioInfoHelper;
    int already;

    // exp : 이 아래는 구글 캘린더 연동 부분이다.
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR }; // exp : 읽기만 허용하는 부분 CalendarScopes.CALENDAR_READONLY
    @Override
    protected void onCreate(Bundle savedInstanceState) {

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


        // exp : 이 부분은 코멘트 데이터를 가져와서 레벨 업 시켜주는 부분

        int i = 0;
        MemberInfoHelper memberInfoHelper;
        memberInfoHelper = new MemberInfoHelper(this);
        SQLiteDatabase db;
        try {
            String date_str = null;
            db = memberInfoHelper.getReadableDatabase();
            String[] columns = {"comment_con", "date", "sentence"};
            String query = "SELECT comment_con, date, sentence FROM comment";
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                date_str = cursor.getString(1);
                if (date_str!=null) {
                    i++;
                }
            }

        }catch (Exception e){

        }
        Calendar c0 = Calendar.getInstance();
        SimpleDateFormat sdf_today = new SimpleDateFormat("yyyy년 MM월 dd일");
        String date_0 = sdf_today.format(c0.getTime());
        c0.add( Calendar.DATE, -1 );
        String date_1 = sdf_today.format(c0.getTime());
        c0.add( Calendar.DATE, -1 );
        String date_2 = sdf_today.format(c0.getTime());
        c0.add( Calendar.DATE, -1 );
        String date_3 = sdf_today.format(c0.getTime());
        // String comment_str = null;
        int k = 0;
        try{

            //만약 오늘로 부터 3일 연속 안쓰면 다시 떨어짐
            memberInfoHelper = new MemberInfoHelper(this);

            // String comment_str = null;
            String date_str = null;
            //  String sentence_str = null;
            db = memberInfoHelper.getReadableDatabase();
            String[] columns = {"comment_con", "date", "sentence"};
            String query = "SELECT comment_con, date, sentence FROM comment";
            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
                    //    comment_str = cursor.getString(0);
                    date_str = cursor.getString(1);
                    //   sentence_str = cursor.getString(2);
                    if(date_str.contains(date_0)){
                        k++;
                    }
                    else if(date_str.contains(date_1)){
                        k++;
                    }else if(date_str.contains(date_2)){
                        k++;
                    }else if(date_str.contains(date_3)){
                        k++;
                    }
                }
              }catch (Exception e){

         }
        if(i > 9) { // 일단 코멘트가 10개가 넘으면 레벨을 높인다.
            SharedPreferences sp_level;
            sp_level = getSharedPreferences("setting", 0);
            SharedPreferences.Editor editor;
            editor = sp_level.edit();
            editor.putString("level", "2");;
            editor.putString("first", "done");
            editor.commit();
        }
        SharedPreferences sp_ = getSharedPreferences("setting",0);
        String first = sp_.getString("first", "");
        if(k ==0 && first.equals("done")){ // 코멘트갯수가 10개가 넘고, 최근글이 없으면 레벨을 낮춘다.
            SharedPreferences sp_level;
            sp_level = getSharedPreferences("setting", 0);
            SharedPreferences.Editor editor;
            editor = sp_level.edit();
            editor.putString("level", "1");
            editor.commit();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectio);
        bt_notyet = (Button)findViewById(R.id.bt_notyet);
        ll_notyet = (LinearLayout) findViewById(R.id.ll_notyet);
        ll_first = (LinearLayout) findViewById(R.id.ll_first);
        ll1 = (LinearLayout) findViewById(R.id.ll1);
        ll2 = (LinearLayout) findViewById(R.id.ll2);
        ll3 = (LinearLayout) findViewById(R.id.ll3);
        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        ll_upper = (LinearLayout) findViewById(R.id.ll_upper);
        showgaspel = (Button) findViewById(R.id.bt_showgaspel);
        stopgaspel = (Button) findViewById(R.id.bt_stopgaspel);
        save = (Button) findViewById(R.id.bt_save);
        bg1 = (EditText) findViewById(R.id.et_background1);
        bg2 = (EditText) findViewById(R.id.et_background2);
        bg3 = (EditText) findViewById(R.id.et_background3);
        sum1 = (EditText) findViewById(R.id.et_summary1);
        sum2 = (EditText) findViewById(R.id.et_summary2);
        js1 = (EditText) findViewById(R.id.et_jesus1);
        js2 = (EditText) findViewById(R.id.et_jesus2);

        date = (Button) findViewById(R.id.bt_date);
        before = (ImageButton) findViewById(R.id.bt_before);
        after = (ImageButton) findViewById(R.id.bt_after);

        q1 = (TextView) findViewById(R.id.question1);
        q2 = (TextView) findViewById(R.id.question2);
        q3 = (TextView) findViewById(R.id.question3);

        onesentence = (Button) findViewById(R.id.bt_onesentence);
        contents = (TextView) findViewById(R.id.tv_contents);

        SharedPreferences sp_level = getSharedPreferences("setting",0);
        String level = sp_level.getString("level", "");

       if(level.equals("2") || level.equals("3")){
            ll_upper.setVisibility(ll_upper.VISIBLE);
            ll1.setVisibility(ll1.VISIBLE);
            ll2.setVisibility(ll2.VISIBLE);
            ll3.setVisibility(ll3.VISIBLE);
            ll_date.setVisibility(ll_date.VISIBLE);
            save.setVisibility(save.VISIBLE);
            showgaspel.setVisibility(showgaspel.VISIBLE);
            ll_notyet.setVisibility(ll_notyet.GONE);
        }else{
            ll_upper.setVisibility(ll_upper.GONE);
            ll1.setVisibility(ll1.GONE);
            ll2.setVisibility(ll2.GONE);
            ll3.setVisibility(ll3.GONE);
            ll_date.setVisibility(ll_date.GONE);
            save.setVisibility(save.GONE);
            showgaspel.setVisibility(showgaspel.GONE);
            ll_notyet.setVisibility(ll_notyet.VISIBLE);
            save.setVisibility(save.GONE);
            showgaspel.setVisibility(showgaspel.GONE);
        }

        // exp : onesentence는 안보이게 설정했다
        onesentence.setVisibility(onesentence.GONE);
        // exp : date에 오늘 날짜를 넣었다
        date.setText(date_val+getDay()+"요일");

        // exp : 렉시오디비나 작성내용 가져오기
        lectioInfoHelper = new LectioInfoHelper(this);
        // exp : 데이터가 있는경우 보이도록 edittext에 삽입한다
        getDataBase();

        // exp : 맨처음에는 복음 내용이 보이지 않는다
        ll_upper.setVisibility(ll_upper.GONE);

        // exp : bottombar 삽입 및 설정
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab3);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override

            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab1) {
                    Intent i = new Intent(LectioActivity.this, MainActivity.class);
                    Calendar c1 = Calendar.getInstance();
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    String date_val2 = sdf2.format(c1.getTime());
                    i.putExtra("date",date_val2);
                    startActivity(i);
                }else if(tabId == R.id.tab2){
                    Intent i = new Intent(LectioActivity.this, SecondActivity.class);
                    startActivity(i);
                }else if(tabId == R.id.tab3){
                }else if(tabId == R.id.tab4){
                    Intent i = new Intent(LectioActivity.this, FourthActivity.class);
                    startActivity(i);
                }
            }
        });

        // exp : 텍스트사이즈 설정
        SharedPreferences sp = getSharedPreferences("setting",0);
        String textsize = sp.getString("textsize", "");
        if(textsize.equals("small")){

            showgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            stopgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            contents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            q1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            q2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            q3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            save.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            bt_notyet.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        }else if(textsize.equals("big")){
            showgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            stopgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            contents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            q1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            q2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            q3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            save.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            bt_notyet.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
        }else if(textsize.equals("toobig")){
            showgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            stopgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            contents.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            q1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
            q2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
            q3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
            bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            save.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            bt_notyet.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
        }else{

        }

        // exp : 키보드 관련 부분
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        attachKeyboardListeners();

        // exp : 다른 부분 터치시 키보드 사라지게 하기 이벤트
        ll_first.setOnClickListener(listener);
        ll1.setOnClickListener(listener);
        ll2.setOnClickListener(listener);
        ll3.setOnClickListener(listener);

        // exp : 복음 보이기, 복음 가리기 이벤트
        stopgaspel.setVisibility(stopgaspel.GONE);
        showgaspel.setOnClickListener(showlistener);
        stopgaspel.setOnClickListener(showlistener);

        // exp : 날짜 앞뒤로 이동 이벤트
        before.setOnClickListener(listener);
        after.setOnClickListener(listener);

        // exp : 저장을 누르면 실행되는 이벤트
        save.setOnClickListener(listener_save);

        // exp : 인터넷 연결 확인하는 부분
            if (wifi.isConnected() || mobile.isConnected()) {
            // exp : 복음 내용 데이터 가져오기
                getGaspel(date_val2);

        } else {
            contents.setText("인터넷을 연결해주세요");
            contents.setGravity(Gravity.CENTER);
        }

    }

    // exp : 데이터가 있는 경우에는 edittext에 출력하기
    protected void getDataBase() {
        SQLiteDatabase db;
        ContentValues values;

        try{
            String bg1_str = null;
            String bg2_str= null;
            String bg3_str= null;
            String sum1_str= null;
            String sum2_str= null;
            String js1_str= null;
            String js2_str= null;

            String date_str = null;
            String onesentence_str = null;
            db = lectioInfoHelper.getReadableDatabase();
            String[] columns = {"bg1", "bg2", "bg3", "sum1", "sum2", "js1","js2" , "date", "onesentence"};
            String whereClause = "date = ?";
            String[] whereArgs = new String[] {
                    date.getText().toString()
            };
            Cursor cursor = db.query("lectio", columns,  whereClause, whereArgs, null, null, null);

            while(cursor.moveToNext()){
                bg1_str = cursor.getString(0);
                bg2_str = cursor.getString(1);
                bg3_str = cursor.getString(2);
                sum1_str = cursor.getString(3);
                sum2_str = cursor.getString(4);
                js1_str = cursor.getString(5);
                js2_str = cursor.getString(6);
                date_str = cursor.getString(7);
                onesentence_str = cursor.getString(8);
            }

            bg1.setText(bg1_str);
            bg2.setText(bg2_str);
            bg3.setText(bg3_str);
            sum1.setText(sum1_str);
            sum2.setText(sum2_str);
            js1.setText(js1_str);
            js2.setText(js2_str);
            cursor.close();
            lectioInfoHelper.close();
        }catch(Exception e){
            Toast.makeText(LectioActivity.this, "실패", Toast.LENGTH_SHORT).show();
        }
    }

    // exp : 저장시 내용 입력 및 수정
    View.OnClickListener listener_save = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            hideKeyboard();
            //코멘트저장을 누르면 데이터를 삽입해준다.
            SQLiteDatabase db;
            ContentValues values;
            // TODO Auto-generated method stub
            if(v.getId()==R.id.bt_save){

                String background1 = bg1.getText().toString();
                String background2 = bg2.getText().toString();
                String background3 = bg3.getText().toString();
                String summary1 = sum1.getText().toString();
                String summary2 = sum2.getText().toString();
                String jesus1 = js1.getText().toString();
                String jesus2 = js2.getText().toString();
                String date1 = date.getText().toString();
                String onesentence1 = onesentence.getText().toString();

                // cf : 기존에 값이 있는지 확인 후 있는 경우에는 already값을 1로 준다.
                try{
                    String bg1_str = null;
                    db = lectioInfoHelper.getReadableDatabase();
                    String[] columns = {"bg1", "bg2", "bg3", "sum1", "sum2", "js1", "js2", "date", "onesentence"};
                    String whereClause = "date = ?";
                    String[] whereArgs = new String[] {
                            date.getText().toString()
                    };
                    Cursor cursor = db.query("lectio", columns,  whereClause, whereArgs, null, null, null);

                    while(cursor.moveToNext()){
                        bg1_str = cursor.getString(0);
                    }
                    if(bg1_str!= null){
                        already = 1;
                    }
                    cursor.close();
                    lectioInfoHelper.close();
                }catch(Exception e){

                }


                // cf : contents가 없는 경우에는 삽입한다 -> already = 0
                if(already==0){
                    try{
                        db=lectioInfoHelper.getWritableDatabase();
                        values = new ContentValues();
                        values.put("bg1",  background1);
                        values.put("bg2",  background2);
                        values.put("bg3",  background3);
                        values.put("sum1", summary1);
                        values.put("sum2", summary2);
                        values.put("js1", jesus1);
                        values.put("js2", jesus2);
                        values.put("date", date1);
                        values.put("onesentence", onesentence1);
                        db.insert("lectio", null, values);
                       lectioInfoHelper.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    //Toast.makeText(LectioActivity.this, "날짜"+date1, Toast.LENGTH_SHORT).show();

                    // exp : 이는 구글캘린더 연동 부분이다
                    int yearsite = date1.indexOf("년");
                    int monthsite = date1.indexOf("월");
                    int daysite = date1.indexOf("일 ");
                    String year= date1.substring(0, yearsite);
                    String month;
                    String day;
                    if(date1.substring(yearsite+2, monthsite).length() > 1){
                        month= date1.substring(yearsite+2, monthsite);
                    }else{
                        month= "0"+date1.substring(yearsite+2, monthsite);
                    }
                    if(date1.substring(monthsite+2, daysite).length() > 1){
                        day = date1.substring(monthsite+2, daysite);
                    }else{
                        day = "0"+date1.substring(monthsite+2, daysite);
                    }
                    SharedPreferences sp = getSharedPreferences("setting",0);
                    String calendarstatus = sp.getString("gcal", "");
                    if( calendarstatus != "") {
                        if(calendarstatus.equals("on")) {
                            new MakeInsertTask(mCredential, year+"-"+month+"-"+day, year+month+day+"aeasaeapj2", onesentence1+"&"+"이 복음의 등장인물은 "+background1+
                                    "장소는 "+background2+"시간은 "+background3+
                                    "이 복음의 내용을 간추리면 "+summary1+
                                    "특별히 눈에 띄는 부분은 "+summary2+
                                    "이 복음에서 보여지는 예수님은 "+jesus1+
                                    "결과적으로 이 복음을 통해 예수님께서 내게 해주시는 말씀은 "+jesus2, "렉시오디비나").execute();
                        }

                    }

                 // cf : 내용이 있는 경우에는 update 한다 -> already =1
                    Toast.makeText(LectioActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();

                }else if(already ==1){
                    try{
                        db=lectioInfoHelper.getWritableDatabase();
                        values = new ContentValues();
                        values.put("bg1",  background1);
                        values.put("bg2",  background2);
                        values.put("bg3",  background3);
                        values.put("sum1", summary1);
                        values.put("sum2", summary2);
                        values.put("js1", jesus1);
                        values.put("js2", jesus2);
                        String where = "date=?";
                        String[] whereArgs = new String[] {date.getText().toString()};

                        db.update("lectio", values, where, whereArgs);
                        lectioInfoHelper.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    // exp : 이는 구글캘린더 연동 부분이다
                    int yearsite = date1.indexOf("년");
                    int monthsite = date1.indexOf("월");
                    int daysite = date1.indexOf("일 ");
                    String year= date1.substring(0, yearsite);
                    String month;
                    String day;
                    if(date1.substring(yearsite+2, monthsite).length() > 1){
                        month= date1.substring(yearsite+2, monthsite);
                    }else{
                        month= "0"+date1.substring(yearsite+2, monthsite);
                    }
                    if(date1.substring(monthsite+2, daysite).length() > 1){
                        day = date1.substring(monthsite+2, daysite);
                    }else{
                        day = "0"+date1.substring(monthsite+2, daysite);
                    }
                    SharedPreferences sp = getSharedPreferences("setting",0);
                    String calendarstatus = sp.getString("gcal", "");
                    if( calendarstatus != "") {
                        if(calendarstatus.equals("on")) {
                            new MakeUpdateTask(mCredential, year+"-"+month+"-"+day, year+month+day+"aeasaeapj2", onesentence1+"&"+"이 복음의 등장인물은 "+background1+
                                    "장소는 "+background2+"시간은 "+background3+
                                    "이 복음의 내용을 간추리면 "+summary1+
                                    "특별히 눈에 띄는 부분은 "+summary2+
                                    "이 복음에서 보여지는 예수님은 "+jesus1+
                                    "결과적으로 이 복음을 통해 예수님께서 내게 해주시는 말씀은 "+jesus2, "렉시오디비나").execute();
                        }
                    }
                    Toast.makeText(LectioActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

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
                try {
                    JSONObject jObj = new JSONObject(response);
                    error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) { // error가 false인 경우에 데이터 가져오기
                        // user successfully logged in
                        // Create login session

                        // Now store the user in SQLite
                        gaspel_date = jObj.getString("created_at");
                    //    Log.d(TAG,gaspel_date);
                        gaspel_sentence = jObj.getString("sentence");
                        gaspel_contents = jObj.getString("contents");
                        String contents_ = gaspel_contents;
                        contents_ = contents_.replaceAll("&gt;", ">");
                        contents_ = contents_.replaceAll("&lt;", "<");
                        contents_ = contents_.replaceAll("&ldquo;", "\"");
                        contents_ = contents_.replaceAll("&rdquo;", "\"");
                        contents_ = contents_.replaceAll("&lsquo;", "\'");
                        contents_ = contents_.replaceAll("&rsquo;", "\'");
                        int idx = contents_.indexOf("✠");
                        int idx2 = contents_.indexOf("주님의 말씀입니다.");
                        contents_ = contents_.substring(idx+1, idx2);
                        contents.setText(contents_);
                        onesentence.setText(gaspel_sentence);


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
             //   Log.e(TAG, "Login Error: " + error.getMessage());
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("date", date);

                return params;
            }

        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        gaspel_date = "date";
       // Log.d(TAG,gaspel_date);
        //	gaspel_sentence = "sentence";
        //	gaspel_contents = "contents";


    }

    // exp : 키보드 가려지는 이벤트
    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(bg1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(bg2.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(bg3.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(sum1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(sum2.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(js1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(js2.getWindowToken(), 0);
    }
    // exp : 키보드 보일때는 안보일때 bottombar 조정하기
    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        // do things when keyboard is shown
        bottomBar.setVisibility(View.GONE);
    }

    @Override
    protected void onHideKeyboard() {
        // do things when keyboard is hidden
        bottomBar.setVisibility(View.VISIBLE);
    }

    // exp : 요일 가져오기
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

    // exp : 다른 곳 터치시 키보드 안보이게 하기 및 날짜 이전 이후 선택시 값 변경 이벤트
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
            switch (v.getId()) {
                case R.id.ll_first:
                    break;
                case R.id.ll1:
                    break;
                case R.id.ll2:
                    break;
                case R.id.ll3:
                    break;
                case R.id.bt_before:
                    c1.add( Calendar.DATE, -1 );
                    date_val = sdf.format(c1.getTime());
                    date.setText(date_val+getDay()+"요일");
                    date_val2 = sdf2.format(c1.getTime());
                    getGaspel(date_val2);
                    getDataBase();
                    break;
                case R.id.bt_after:
                    c1.add( Calendar.DATE, 1 );
                    date_val = sdf.format(c1.getTime());
                    date.setText(date_val+getDay()+"요일");
                    date_val2 = sdf2.format(c1.getTime());
                    getGaspel(date_val2);
                    getDataBase();
                    break;
            }
        }
    };

   // exp : 복음 보이기, 가리기 이벤트
    View.OnClickListener showlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
            switch (v.getId()) {
                case R.id.bt_showgaspel:
                    ll_upper.setVisibility(ll_upper.VISIBLE);
                    showgaspel.setVisibility(stopgaspel.GONE);
                    stopgaspel.setVisibility(stopgaspel.VISIBLE);
                    break;
                case R.id.bt_stopgaspel:
                    ll_upper.setVisibility(ll_upper.GONE);
                    showgaspel.setVisibility(stopgaspel.VISIBLE);
                    stopgaspel.setVisibility(stopgaspel.GONE);
                    break;

            }
        }
    };
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
                Intent i = new Intent(LectioActivity.this, ExplainActivity.class);
                startActivity(i);
                break;
            case R.id.action_menu_02:
                Intent i2 = new Intent(LectioActivity.this, ThirdActivity.class);
                startActivity(i2);
                break;
            case R.id.action_menu_03:
                Intent i3 = new Intent(LectioActivity.this, StatusActivity.class);
                startActivity(i3);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
