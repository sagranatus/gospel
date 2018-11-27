package com.yellowpg.gaspel;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.DB.LectioInfoHelper;
import com.yellowpg.gaspel.DB.WeekendInfoHelper;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.OnKeyboardVisibilityListener;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.server.Server_LectioData;
import com.yellowpg.gaspel.server.Server_WeekendData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LectioActivity extends AppCompatActivity{
    final static String TAG = "lectioActivity";
    LinearLayout ll_first, ll1, ll_upper, ll_date;
    String date_intent;
    EditText bg1, bg2, bg3;
    EditText sum1, sum2;
    EditText js1, js2, weekend;
    ScrollView scrollView, after_save;
    Button save,start, edit;
    Button prev, next;
    Button date;
    ImageButton before, after;
    InputMethodManager imm;
    TextView contentsGaspel;
    TextView q1, firstSentence, after_save_tv;
    Button onesentence;
    BottomNavigationView bottomNavigationView;
    String day;
    Calendar c1 = Calendar.getInstance();

    // 오늘날짜 생성
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 ");
    String date_val = sdf.format(c1.getTime());
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    String date_val2 = sdf2.format(c1.getTime());

    LectioInfoHelper lectioInfoHelper;
    int already;
    Boolean edit_now;

    ListSelectorDialog dlg_left;
    String[] listk_left, listv_left;

    int lectio_order;

    private SessionManager session;
    String uid = null;
    String weekend_date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // exp : 인터넷연결 확인
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectio);

        // session 정보 가져오기
        session = new SessionManager(getApplicationContext());
        uid = session.getUid();

        // edit 일단 false 설정
        edit_now = false;

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();

        // bottomnavigation 뷰 등록
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        BottomNavigationViewHelper.disableShiftMode2(bottomNavigationView);

        // intent값 가져오기 - 한주복음묵상에서 오는 경우
        Intent intent = getIntent();
        date_intent = intent.getStringExtra("date");

        // intent에 따른 actionbar, bottomnavigationview 상태 설정
        // 한주복음묵상에서 온 경우
        if(date_intent != null) {
            actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionbar.setCustomView(R.layout.actionbar_back_weekend);
            actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2980b9")));
            actionbar.setElevation(0);

            // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.back);
            bottomNavigationView.setVisibility(View.GONE);
        // 일반 렉시오 디비나의 경우
        }else{
            //actionbar setting
            actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionbar.setCustomView(R.layout.actionbar_lectio);
            actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2980b9")));
            actionbar.setElevation(0);

            // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.list);
        }

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        ll_first = (LinearLayout) findViewById(R.id.ll_first);
        ll1 = (LinearLayout) findViewById(R.id.ll1);

        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        ll_upper = (LinearLayout) findViewById(R.id.ll_upper);

        save = (Button) findViewById(R.id.bt_save);
        edit = (Button) findViewById(R.id.bt_edit);
        bg1 = (EditText) findViewById(R.id.et_background1);
        bg2 = (EditText) findViewById(R.id.et_background2);
        bg3 = (EditText) findViewById(R.id.et_background3);
        sum1 = (EditText) findViewById(R.id.et_summary1);
        sum2 = (EditText) findViewById(R.id.et_summary2);
        js1 = (EditText) findViewById(R.id.et_jesus1);
        js2 = (EditText) findViewById(R.id.et_jesus2);
        weekend = (EditText) findViewById(R.id.et_weekend);

        date = (Button) findViewById(R.id.bt_date);
        before = (ImageButton) findViewById(R.id.bt_before);
        after = (ImageButton) findViewById(R.id.bt_after);

        q1 = (TextView) findViewById(R.id.question1);

        // 데이터값이 있는 경우 보여질 view
        after_save = (ScrollView) findViewById(R.id.after_save);
        after_save_tv = (TextView) findViewById(R.id.after_save_tv);

        onesentence = (Button) findViewById(R.id.bt_onesentence);
        start = (Button) findViewById(R.id.bt_start);
        firstSentence = (TextView) findViewById(R.id.tv_first);

        contentsGaspel = (TextView) findViewById(R.id.tv_contents);

        q1.setVisibility(View.GONE);
        bg1.setVisibility(View.GONE);
        bg2.setVisibility(View.GONE);
        bg3.setVisibility(View.GONE);
        bg3.setVisibility(View.GONE);
        sum1.setVisibility(View.GONE);
        sum2.setVisibility(View.GONE);
        js1.setVisibility(View.GONE);
        js2.setVisibility(View.GONE);
        weekend.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        after_save.setVisibility(View.GONE);

        prev = (Button) findViewById(R.id.bt_prev);
        next = (Button) findViewById(R.id.bt_next);
        prev.setBackgroundResource(R.drawable.button_bg_grey);
        next.setBackgroundResource(R.drawable.button_bg_grey);

        prev.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        prev.setOnClickListener(listener);
        next.setOnClickListener(listener);
        start.setOnClickListener(listener);
        edit.setOnClickListener(listener);

        save.setBackgroundResource(R.drawable.button_bg);
        edit.setBackgroundResource(R.drawable.button_bg);
        start.setBackgroundResource(R.drawable.button_bg2);

        // 맨처음에는 start 버튼만 보이도록 순서 세팅
        lectio_order = 0;

        // 한주복음묵상에서 온 경우 날짜 삽입 / 날짜에 맞는 복음 및 정보 가져온다
        if(date_intent != null){
            before.setVisibility(View.GONE);
            after.setVisibility(View.GONE);
            String date_detail = intent.getStringExtra("date_detail"); // 날짜 형식 : yyyy년 MM월 dd일 x요일
            date.setText(date_detail);
            date_val2 = date_intent; // 형식 : yyyy-MM-dd
            checkRecord();
            getGaspel(date_val2);
        }else{
            date.setText(date_val+getDay()+"요일");
            lectioInfoHelper = new LectioInfoHelper(this);
            checkRecord();
        }



        // exp : 맨처음에는 복음 내용이 보이지 않는다
      //  ll_upper.setVisibility(ll_upper.GONE);



        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem_1 = menu.getItem(0);
        MenuItem menuItem_2 = menu.getItem(1);
        MenuItem menuItem_3 = menu.getItem(2);
        MenuItem menuItem_4 = menu.getItem(3);
        menuItem_1.setChecked(false);
        menuItem_2.setChecked(false);
        menuItem_3.setChecked(false);
        menuItem_4.setChecked(false);

        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_one:
                        Intent i = new Intent(LectioActivity.this, MainActivity.class);
                        startActivity(i);
                        break;
                    case R.id.action_two:
                        Intent i2 = new Intent(LectioActivity.this, LectioActivity.class);
                        startActivity(i2);
                        break;
                    case R.id.action_three:
                        Intent i3 = new Intent(LectioActivity.this, SecondActivity.class);
                        startActivity(i3);
                        break;
                    case R.id.action_four:
                        Intent i4 = new Intent(LectioActivity.this, FourthActivity.class);
                        startActivity(i4);
                        break;
                }
                return false;
            }

        });

        // custom dialog setting
        dlg_left  = new ListSelectorDialog(this, "Select an Operator");

        // custom dialog key, value 설정
        listk_left = new String[] {"a", "b"};
        listv_left = new String[] {"설정", "나의 상태"};

        // exp : 텍스트사이즈 설정
        SharedPreferences sp = getSharedPreferences("setting",0);
        String textsize = sp.getString("textsize", "");
        if(textsize.equals("big")){
            date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            onesentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            firstSentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            contentsGaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            q1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            weekend.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            after_save_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        //    bt_notyet.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
        }else{

        }

        // 키보드 없애기 - 새로 추가한 부분
        findViewById(R.id.ll_first).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });
        findViewById(R.id.ll1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });
        findViewById(R.id.ll_upper).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });
        findViewById(R.id.tv_contents).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });

       // setKeyboardVisibilityListener(this);

        // exp : 다른 부분 터치시 키보드 사라지게 하기 이벤트

        // exp : 복음 보이기, 복음 가리기 이벤트
//        stopgaspel.setVisibility(stopgaspel.GONE);
   //     showgaspel.setOnClickListener(showlistener);
    //    stopgaspel.setOnClickListener(showlistener);

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
            contentsGaspel.setText("인터넷을 연결해주세요");
            contentsGaspel.setGravity(Gravity.CENTER);
        }



    }
    // 저장된 값이 있는지 확인하고 있는경우 가져옴
    public void checkRecord(){

        if(date.getText().toString().contains("일요일")){
            weekend_date = "일요일";
        }else{
            weekend_date = null;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        edit_now = false;
        // cf : 렉시오 디비나 부분
        lectioInfoHelper = new LectioInfoHelper(LectioActivity.this);
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

            String date_aft = (String) date.getText();
            db2 = lectioInfoHelper.getReadableDatabase();
            String[] columns = {"bg1", "bg2", "bg3", "sum1", "sum2", "js1", "js2"};
            String whereClause = "date = ?";
            String[] whereArgs = new String[] {
                    date_aft
            };
            Cursor cursor = db2.query("lectio", columns,  whereClause, whereArgs, null, null, null);

            if(cursor != null){
                after_save.setVisibility(View.VISIBLE);
                while(cursor.moveToNext()){
                    bg1_str = cursor.getString(0);
                    bg2_str = cursor.getString(1);
                    bg3_str = cursor.getString(2);
                    sum1_str = cursor.getString(3);
                    sum2_str = cursor.getString(4);
                    js1_str = cursor.getString(5);
                    js2_str = cursor.getString(6);
                }
                // edittext에 가져오기
                bg1.setText(bg1_str);
                bg2.setText(bg2_str);
                bg3.setText(bg3_str);
                sum1.setText(sum1_str);
                sum2.setText(sum2_str);
                js1.setText(js1_str);
                js2.setText(js2_str);
            }else{


            }

            cursor.close();
            lectioInfoHelper.close();
        }
        catch(Exception e){

        }

        WeekendInfoHelper weekendInfoHelper = new WeekendInfoHelper(LectioActivity.this);
        String weekend_date_ = date.getText().toString();

        String mysentence = null;
        String mythought = "";
        try{
            db2 = weekendInfoHelper.getReadableDatabase();
            String[] columns = {"mysentence", "mythought"};
            String whereClause = "date = ?";
            String[] whereArgs = new String[] {
                    weekend_date_
            };
            Cursor cursor = db2.query("weekend", columns,  whereClause, whereArgs, null, null, null);


            while(cursor.moveToNext()){
                mysentence = cursor.getString(0);
                mythought = cursor.getString(1);
            }
            // edittext에 가져오기
            weekend.setText(mysentence);

            cursor.close();
            weekendInfoHelper.close();
        }catch(Exception e){

        }

        if(bg1_str != null){
            start.setVisibility(View.GONE);
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
            q1.setVisibility(View.GONE);
            bg1.setVisibility(View.GONE);
            bg2.setVisibility(View.GONE);
            bg3.setVisibility(View.GONE);
            sum1.setVisibility(View.GONE);
            sum2.setVisibility(View.GONE);
            js1.setVisibility(View.GONE);
            js2.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            onesentence.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            firstSentence.setVisibility(View.GONE);
            start.setVisibility(View.GONE);
            weekend.setVisibility(View.GONE);
            if(weekend_date != null){
                if(mythought == null){
                    mythought = "";
                }
                if(mysentence == null){
                    mysentence = "";
                }
                after_save_tv.setText(Html.fromHtml("<font color=\"#999999\">· 이 복음의 등장인물은 </font> " + bg1_str
                        +"<br><font color=\"#999999\">· 장소는</font> " + bg2_str
                        +"<br><font color=\"#999999\">· 시간은</font> " + bg3_str
                        +"<br><font color=\"#999999\">· 이 복음의 내용을 간추리면</font> " + sum1_str
                        +"<br><font color=\"#999999\">· 특별히 눈에 띄는 부분은</font> " + sum2_str
                        +"<br><font color=\"#999999\">· 이 복음에서 보여지는 예수님은</font> " + js1_str
                        +"<br><font color=\"#999999\">· 결과적으로 이 복음을 통해 \n예수님께서 내게 해주시는 말씀은</font> \"" + js2_str+"\""
                        +"<br><font color=\"#999999\">· 주일 복음에서 내가 묵상하기로 선택한 구절은 </font> " + mysentence
                        +"<br><font color=\"#999999\">· 내가 묵상한 내용은 </font> " + mythought));
            }else{
                after_save_tv.setText(Html.fromHtml("<font color=\"#999999\">· 이 복음의 등장인물은 </font> " + bg1_str
                        +"<br><font color=\"#999999\">· 장소는</font> " + bg2_str
                        +"<br><font color=\"#999999\">· 시간은</font> " + bg3_str
                        +"<br><font color=\"#999999\">· 이 복음의 내용을 간추리면</font> " + sum1_str
                        +"<br><font color=\"#999999\">· 특별히 눈에 띄는 부분은</font> " + sum2_str
                        +"<br><font color=\"#999999\">· 이 복음에서 보여지는 예수님은</font> " + js1_str
                        +"<br><font color=\"#999999\">· 결과적으로 이 복음을 통해 \n예수님께서 내게 해주시는 말씀은</font> \"" + js2_str+"\""));
            }

        }else{
            after_save.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            onesentence.setVisibility(View.VISIBLE);
            firstSentence.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            start.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            q1.setVisibility(View.GONE);
            bg1.setVisibility(View.GONE);
            bg2.setVisibility(View.GONE);
            bg3.setVisibility(View.GONE);
            sum1.setVisibility(View.GONE);
            sum2.setVisibility(View.GONE);
            js1.setVisibility(View.GONE);
            js2.setVisibility(View.GONE);
            weekend.setVisibility(View.GONE);
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        }

    }

    // 커스텀 다이얼로그 선택시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pray:
                showPraying(item);
                return true;
            default:
                if(date_intent != null){
                    finish();
                }else{
                    // show the list dialog.
                    dlg_left.show(listv_left, listk_left, new ListSelectorDialog.listSelectorInterface() {

                        // procedure if user cancels the dialog.
                        public void selectorCanceled() {
                        }
                        // procedure for when a user selects an item in the dialog.
                        public void selectedItem(String key, String item) {
                            if(item.equals("설정")){
                                Intent i = new Intent(LectioActivity.this, ThirdActivity.class);
                                startActivity(i);
                            }else if(item.equals("나의 상태")){
                                Intent i = new Intent(LectioActivity.this, StatusActivity.class);
                                startActivity(i);
                            }
                        }
                    });
                }

                return true;
        }
    }

    // 저장시 내용 입력 및 수정
    View.OnClickListener listener_save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SQLiteDatabase db;
            ContentValues values;
            // TODO Auto-generated method stub
            if(v.getId()==R.id.bt_save){
                if(date.getText().toString().contains("일요일")){
                    weekend_date = "일요일";
                }else{
                    weekend_date = null;
                }
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                String background1 = bg1.getText().toString();
                String background2 = bg2.getText().toString();
                String background3 = bg3.getText().toString();
                String summary1 = sum1.getText().toString();
                String summary2 = sum2.getText().toString();
                String jesus1 = js1.getText().toString();
                String jesus2 = js2.getText().toString();
                String date1 = date.getText().toString();
                String onesentence1 = onesentence.getText().toString();

                String bg1_str = null;
                try{
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

                    cursor.close();

                    if(bg1_str!= null){
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
                        db.update("lectio", values, where, whereArgs);

                        if(uid != null && uid != ""){
                            Lectio lectio = new Lectio(date1, onesentence1, background1, background2, background3, summary1, summary2, jesus1, jesus2);
                            Server_LectioData.updateLectio(LectioActivity.this, uid, lectio);
                        }
                    }else{
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
                        if(uid != null && uid != ""){
                            Lectio lectio = new Lectio(date1, onesentence1, background1, background2, background3, summary1, summary2, jesus1, jesus2);
                            Server_LectioData.insertLectio(LectioActivity.this, uid, lectio);
                        }
                    }
                    lectioInfoHelper.close();
                }catch(Exception e){

                }

                // 일요일의 경우에
                if(date_intent != null || weekend_date != null){
                    WeekendInfoHelper weekendInfoHelper = new WeekendInfoHelper(LectioActivity.this);
                    String weekend_date = date.getText().toString();
                    String mysentence = weekend.getText().toString();
                    try{
                        String mysentence_str = null;
                        db = weekendInfoHelper.getReadableDatabase();
                        String[] columns = {"mysentence", "mythought"};
                        String whereClause = "date = ?";
                        String[] whereArgs = new String[] {
                                weekend_date
                        };
                        Cursor cursor = db.query("weekend", columns,  whereClause, whereArgs, null, null, null);

                        String mythought = "";
                        while(cursor.moveToNext()){
                            mysentence_str = cursor.getString(0);
                            mythought = cursor.getString(1);
                        }
                        if(mysentence_str!= null){
                            values = new ContentValues();
                            values.put("mysentence",  mysentence);
                            String where = "date=?";
                            String[] whereArgs_ = new String[] {weekend_date};
                            db.update("weekend", values, where, whereArgs_);
                            if(mythought == null){
                                mythought = "";
                            }
                            if(uid != null && uid != ""){
                                Server_WeekendData.updateWeekend(LectioActivity.this, uid, weekend_date, mysentence, mythought);
                            }
                        }else{
                            values = new ContentValues();
                            values.put("date",  weekend_date);
                            values.put("mysentence",  mysentence);
                            db.insert("weekend", null, values);

                            if(uid != null && uid != ""){
                                Lectio lectio = new Lectio(date1, onesentence1, background1, background2, background3, summary1, summary2, jesus1, jesus2);
                                Server_WeekendData.insertWeekend(LectioActivity.this, uid, weekend_date, mysentence, mythought);
                            }
                        }
                        cursor.close();
                        weekendInfoHelper.close();
                    }catch(Exception e){

                    }
                    // 한주복음묵상에서 온경우 다시 이동
                    if(date_intent != null) {
                        Intent intent = new Intent(LectioActivity.this, SecondActivity.class);
                        LectioActivity.this.startActivity(intent);
                    }
                    checkRecord();
                }else{
                    checkRecord();
                }
            }
        }
    };

    String gaspel_date;
    String gaspel_sentence;
    String gaspel_contents;
    public void getGaspel(final String date) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
        String tag_string_req = "req_getgaspel";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_TODAY, new Response.Listener<String>() {
            boolean error;
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) { // error가 false인 경우에 데이터가져오기 성공
                        // Now store the user in SQLite
                        gaspel_date = jObj.getString("created_at");

                        gaspel_sentence = jObj.getString("sentence");
                        gaspel_contents = jObj.getString("contents");

                        //복음 내용 편집
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

                        int idx3 = contents.indexOf("거룩한 복음입니다.");
                        int length = "거룩한 복음입니다.".length();
                        final String after = contents.substring(idx3+length+11);

                        Pattern p = Pattern.compile(".\\d+");
                        Matcher m = p.matcher(after);
                        while (m.find()) {
                            Log.d("saea", after);
                            contents = contents.replaceAll(m.group(), "\n"+m.group());
                        }
                        contentsGaspel.setText(contents);
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

            }
        });
        t.start();

    }

    // 버튼 선택시 값 변경 이벤트
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_prev:
                    if(lectio_order == 1){
                        // edit 버튼 클릭후에 다시 돌아가는 경우
                        if(edit_now){
                            checkRecord();
                        }else{
                            onesentence.setVisibility(View.VISIBLE);
                            firstSentence.setVisibility(View.VISIBLE);
                            start.setVisibility(View.VISIBLE);
                            prev.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                            q1.setVisibility(View.GONE);
                            lectio_order = 0;
                            bg1.setVisibility(View.GONE);
                        }

                    }else if(lectio_order == 2){
                        lectio_order = 1;
                        bg1.setVisibility(View.VISIBLE);
                        bg2.setVisibility(View.GONE);
                    }else if(lectio_order == 3) {
                        lectio_order = 2;
                        bg2.setVisibility(View.VISIBLE);
                        bg3.setVisibility(View.GONE);
                    }else if(lectio_order == 4) {
                        q1.setText("복음의 배경은 무엇인가요?");
                        lectio_order = 3;
                        bg3.setVisibility(View.VISIBLE);
                        sum1.setVisibility(View.GONE);
                    }else if(lectio_order == 5) {
                        lectio_order = 4;
                        sum1.setVisibility(View.VISIBLE);
                        sum2.setVisibility(View.GONE);
                    }else if(lectio_order == 6) {
                        q1.setText("복음 전체 내용을 사건 중심으로 요약해 봅시다");
                        lectio_order = 5;
                        sum2.setVisibility(View.VISIBLE);
                        js1.setVisibility(View.GONE);
                    }else if(lectio_order == 7) {
                        if (date_intent != null || weekend_date != null) {
                            lectio_order = 6;
                            js1.setVisibility(View.VISIBLE);
                            js2.setVisibility(View.GONE);
                            weekend.setVisibility(View.GONE);
                            next.setVisibility(View.VISIBLE);
                            save.setVisibility(View.GONE);
                        }else{
                            lectio_order = 6;
                            js1.setVisibility(View.VISIBLE);
                            js2.setVisibility(View.GONE);
                            next.setVisibility(View.VISIBLE);
                            save.setVisibility(View.GONE);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            int sizeInDP2 = 280;
                            int marginInDp2 = (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, sizeInDP2, getResources()
                                            .getDisplayMetrics());
                            params.setMargins(10,10,10, marginInDp2);
                            contentsGaspel.setLayoutParams(params);
                        }
                    }else if(lectio_order == 8) {
                        if (date_intent != null || weekend_date != null) {
                            q1.setText("복음에서 보여지는 예수님의 모습을 보고 생각해봅시다");
                            lectio_order = 7;
                            js2.setVisibility(View.VISIBLE);
                            weekend.setVisibility(View.GONE);
                            next.setVisibility(View.VISIBLE);
                            save.setVisibility(View.GONE);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            int sizeInDP2 = 280;
                            int marginInDp2 = (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, sizeInDP2, getResources()
                                            .getDisplayMetrics());
                            params.setMargins(10, 10, 10, marginInDp2);
                            contentsGaspel.setLayoutParams(params);
                        }
                    }
                    break;
                case R.id.bt_next:
                    if(lectio_order == 1){
                        lectio_order = 2;
                        prev.setVisibility(View.VISIBLE);
                        bg1.setVisibility(View.GONE);
                        bg2.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 2) {
                        lectio_order = 3;
                        bg2.setVisibility(View.GONE);
                        bg3.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 3) {
                        q1.setText("복음 전체 내용을 사건 중심으로 요약해 봅시다");
                        lectio_order = 4;
                        bg3.setVisibility(View.GONE);
                        sum1.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 4) {
                        lectio_order = 5;
                        sum1.setVisibility(View.GONE);
                        sum2.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 5) {
                        q1.setText("복음에서 보여지는 예수님의 모습을 보고 생각해봅시다");
                        lectio_order = 6;
                        sum2.setVisibility(View.GONE);
                        js1.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 6) {
                        if(date_intent != null || weekend_date != null){
                            lectio_order = 7;
                            js1.setVisibility(View.GONE);
                            js2.setVisibility(View.VISIBLE);
                        }else{
                            lectio_order = 7;
                            js1.setVisibility(View.GONE);
                            js2.setVisibility(View.VISIBLE);
                            next.setVisibility(View.INVISIBLE);
                            save.setVisibility(View.VISIBLE);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            int sizeInDP2 = 320;
                            int marginInDp2 = (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, sizeInDP2, getResources()
                                            .getDisplayMetrics());
                            params.setMargins(10,10,10, marginInDp2);
                            contentsGaspel.setLayoutParams(params);
                        }

                    }else if(lectio_order == 7) {
                        if (date_intent != null || weekend_date != null) {
                            q1.setText("이번주 복음에서 특별히 와닿는 구절을 선택해 봅시다");
                            lectio_order = 8;
                            js2.setVisibility(View.GONE);
                            weekend.setVisibility(View.VISIBLE);
                            next.setVisibility(View.INVISIBLE);
                            save.setVisibility(View.VISIBLE);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            int sizeInDP2 = 320;
                            int marginInDp2 = (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, sizeInDP2, getResources()
                                            .getDisplayMetrics());
                            params.setMargins(10,10,10, marginInDp2);
                            contentsGaspel.setLayoutParams(params);
                        }
                    }


                    break;

                case R.id.bt_start:
                    lectio_order = 1;
                    prev.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    q1.setVisibility(View.VISIBLE);
                    bg1.setVisibility(View.VISIBLE);

                    onesentence.setVisibility(View.GONE);
                    firstSentence.setVisibility(View.GONE);
                    start.setVisibility(View.GONE);
                    break;

                case R.id.bt_edit:
                    edit.setVisibility(View.GONE);
                    edit_now = true;
                    lectio_order = 1;
                    onesentence.setVisibility(View.GONE);
                    firstSentence.setVisibility(View.GONE);
                    after_save.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    prev.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    q1.setVisibility(View.VISIBLE);
                    bg1.setVisibility(View.VISIBLE);
                    break;

                case R.id.bt_before:
                    c1.add( Calendar.DATE, -1 );
                    date_val = sdf.format(c1.getTime());
                    date.setText(date_val+getDay()+"요일");
                    date_val2 = sdf2.format(c1.getTime());
                    checkRecord();
                    getGaspel(date_val2);
                    q1.setText("복음의 배경은 무엇인가요?");

                    break;
                case R.id.bt_after:
                    c1.add( Calendar.DATE, 1 );
                    date_val = sdf.format(c1.getTime());
                    date.setText(date_val+getDay()+"요일");
                    date_val2 = sdf2.format(c1.getTime());
                    checkRecord();
                    getGaspel(date_val2);
                    q1.setText("복음의 배경은 무엇인가요?");
                    break;
            }
        }
    };

    // 요일 얻어오기
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
    // actionbar 오른쪽 벨 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_main, menu);

        return true;
    }

    // 벨 클릭시 성령청원기도 보이기
    public void showPraying(final MenuItem item)
    {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock myWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, TAG);
        myWakeLock.acquire(); //실행후 리소스 반환 필수
        MainActivity.releaseCpuLock();
        MainActivity.playSound(LectioActivity.this, "pray");

        item.setIcon(getResources().getDrawable(R.drawable.notification));
        // Create custom dialog object
        final Dialog dialog = new Dialog(LectioActivity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog);
        // Set dialog title
        dialog.setTitle("Custom Dialog");

        TextView text_ttl = (TextView) dialog.findViewById(R.id.titleDialog);
        text_ttl.setText("성령 청원 기도");

        // set values for custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.textDialog);
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
                "영복을 얻게 하소서.아멘"); //saea
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                item.setIcon(getResources().getDrawable(R.drawable.notification_base));
            }
        });
        // 기도마침 클릭시 이벤트
        Button declineButton = (Button) dialog.findViewById(R.id.declineButton);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                item.setIcon(getResources().getDrawable(R.drawable.notification_base));
                dialog.dismiss();

            }
        });
    }



}
