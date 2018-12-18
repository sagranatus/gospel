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
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.DB.LectioDBSqlData;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.etc.getDay;
import com.yellowpg.gaspel.server.Server_LectioData;
import com.yellowpg.gaspel.server.Server_getGaspel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LectioActivity extends AppCompatActivity{
    LinearLayout ll_first, ll1, ll_upper, ll_main;
    String date_intent;
    EditText bg1, bg2, bg3;
    EditText sum1, sum2;
    EditText js1, js2;
    ScrollView scrollView;
    LinearLayout after_save;
    Button save,start, edit;
    Button prev, next;
    InputMethodManager imm;
    TextView contentsGaspel;
    TextView q1, firstSentence;
    TextView after_save_tv1, after_save_tv2, after_save_tv3, after_save_tv4, after_save_tv5, after_save_tv6, after_save_tv7;
    Button onesentence;
    ImageButton up,down;

    BottomNavigationView bottomNavigationView;
    Calendar c1 = Calendar.getInstance();
    String typedDate;
    NetworkInfo mobile;
    NetworkInfo wifi;

    // 오늘날짜 생성
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 ");
    String date_val = sdf.format(c1.getTime());
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    String date_val2 = sdf2.format(c1.getTime());

    Boolean edit_now, start_now;

    ListSelectorDialog dlg_left;
    String[] listk_left, listv_left;

    int lectio_order;

    private SessionManager session;
    String uid = null;
    MenuInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // session 정보 가져오기
        session = new SessionManager(getApplicationContext());
        uid = session.getUid();


        // 인터넷연결 확인
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectio);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar_lectio);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
        actionbar.setElevation(0);


        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        ll_main= (LinearLayout) findViewById(R.id.ll_main);
        ll_first = (LinearLayout) findViewById(R.id.ll_first);
        ll1 = (LinearLayout) findViewById(R.id.ll1);
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

        q1 = (TextView) findViewById(R.id.question1);

        // 데이터값이 있는 경우 보여질 view
        after_save = (LinearLayout) findViewById(R.id.after_save);
        after_save_tv1 = (TextView) findViewById(R.id.after_save_tv1);
        after_save_tv2 = (TextView) findViewById(R.id.after_save_tv2);
        after_save_tv3 = (TextView) findViewById(R.id.after_save_tv3);
        after_save_tv4 = (TextView) findViewById(R.id.after_save_tv4);
        after_save_tv5 = (TextView) findViewById(R.id.after_save_tv5);
        after_save_tv6 = (TextView) findViewById(R.id.after_save_tv6);
        after_save_tv7 = (TextView) findViewById(R.id.after_save_tv7);

        onesentence = (Button) findViewById(R.id.bt_onesentence);
        start = (Button) findViewById(R.id.bt_start);
        firstSentence = (TextView) findViewById(R.id.tv_first);

        contentsGaspel = (TextView) findViewById(R.id.tv_contents);

        up = (ImageButton) findViewById(R.id.up);
        down = (ImageButton) findViewById(R.id.down);

        bg1.setBackgroundResource(R.drawable.edit_bg_white);
        bg2.setBackgroundResource(R.drawable.edit_bg_white);
        bg3.setBackgroundResource(R.drawable.edit_bg_white);
        sum1.setBackgroundResource(R.drawable.edit_bg_white);
        sum2.setBackgroundResource(R.drawable.edit_bg_white);
        js1.setBackgroundResource(R.drawable.edit_bg_white);
        js2.setBackgroundResource(R.drawable.edit_bg_white);

        after_save_tv1.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv2.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv3.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv4.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv5.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv6.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv7.setBackgroundResource(R.drawable.edit_bg_white);
        prev = (Button) findViewById(R.id.bt_prev);
        next = (Button) findViewById(R.id.bt_next);
        prev.setBackgroundResource(R.drawable.button_bg_blue);
        next.setBackgroundResource(R.drawable.button_bg_blue);

        q1.setVisibility(View.GONE);
        bg1.setVisibility(View.GONE);
        bg2.setVisibility(View.GONE);
        bg3.setVisibility(View.GONE);
        sum1.setVisibility(View.GONE);
        sum2.setVisibility(View.GONE);
        js1.setVisibility(View.GONE);
        js2.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        after_save.setVisibility(View.GONE);

        prev.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        prev.setOnClickListener(listener);
        next.setOnClickListener(listener);
        start.setOnClickListener(listener);
        edit.setOnClickListener(listener);
        // 저장을 누르면 실행되는 이벤트
        save.setOnClickListener(listener_save);

        save.setBackgroundResource(R.drawable.button_bg);
        edit.setBackgroundResource(R.drawable.button_bg);
        start.setBackgroundResource(R.drawable.button_bg2);

        // exp : 텍스트사이즈 설정
        SharedPreferences sp = getSharedPreferences("setting",0);
        String textsize = sp.getString("textsize", "");
        if(textsize.equals("big")){
            onesentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            firstSentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            contentsGaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            q1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv5.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv6.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv7.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            //    bt_notyet.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
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
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_zero:
                        Intent i0 = new Intent(LectioActivity.this, FirstActivity.class);
                        startActivity(i0);
                        break;
                    case R.id.action_one:
                        Intent i = new Intent(LectioActivity.this, MainActivity.class);
                        startActivity(i);
                        break;
                    case R.id.action_two:
                        Intent i2 = new Intent(LectioActivity.this, LectioActivity.class);
                        startActivity(i2);
                        break;
                    case R.id.action_three:
                        Intent i3 = new Intent(LectioActivity.this, WeekendActivity.class);
                        startActivity(i3);
                        break;
                    case R.id.action_four:
                        Intent i4 = new Intent(LectioActivity.this, RecordActivity.class);
                        startActivity(i4);
                        break;
                }
                return false;
            }

        });

        // custom dialog setting
        dlg_left  = new ListSelectorDialog(this, "Select an Operator");

        // custom dialog key, value 설정
        listk_left = new String[] {"a", "b", "c"};
        listv_left = new String[] { "설정", "계정정보", "로그아웃"};



        // intent값 가져오기
        Intent intent = getIntent();
        date_intent = intent.getStringExtra("date");

        // 나의 기록에서 온 경우 / 날짜에 맞는 복음 및 정보 가져온다
        if(date_intent != null){ // 형식 : yyyy-MM-dd
            // back 보이기
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.back);
            bottomNavigationView.setVisibility(View.GONE);

            date_val2 = date_intent; // 형식 : yyyy-MM-dd
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = formatter.parse(date_val2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c1 = Calendar.getInstance();
            c1.setTime(date);
            String date_val1 = sdf.format(c1.getTime()); // cf : yyyy-MM-dd => yyyy년 MM월 dd일 x요일
            typedDate = date_val1+getDay.getDay(c1)+"요일";
            Log.d("saea", typedDate);

        }else{
            // 거룩한독서인 경우
            typedDate = date_val+getDay.getDay(c1)+"요일";
            Log.d("saea", typedDate);
        }

        // 인터넷 연결 확인하는 부분
        if (wifi.isConnected() || mobile.isConnected()) {
            // exp : 복음 내용 데이터 가져오기
            checkRecord();
           // getGaspel(date_val2);
            Server_getGaspel.get_Gaspel(up, down, date_val2, onesentence, contentsGaspel );
        } else {
            checkRecord();
            contentsGaspel.setText("인터넷을 연결해주세요");
            contentsGaspel.setGravity(Gravity.CENTER);
        }

        // 맨처음에는 복음 내용이 보이지 않는다
        ll_upper.setVisibility(ll_upper.GONE);

        // edit 일단 false 설정
        edit_now = false;
        start_now = false;

        // 맨처음에는 start 버튼만 보이도록 순서 세팅
        lectio_order = 0;


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


    }
    // 저장된 값이 있는지 확인하고 있는경우 가져옴
    public void checkRecord(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        bg1.setText("");
        bg2.setText("");
        bg3.setText("");
        sum1.setText("");
        sum2.setText("");
        js1.setText("");
        js2.setText("");

        edit_now = false;

        // 값 가져오고 각  edittext 에 넣기
        String date_aft = typedDate; //yyyy년 MM월 dd일 x요일
        ArrayList<Lectio> lectios = new ArrayList<Lectio>();
        DBManager dbMgr = new DBManager(LectioActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectLectioData(LectioDBSqlData.SQL_DB_SELECT_DATA, uid, date_aft, lectios);
        dbMgr.dbClose();
        String onesentence_str = null;
        String bg1_str = null;
        String bg2_str = null;
        String bg3_str = null;
        String sum1_str = null;
        String sum2_str = null;
        String js1_str = null;
        String js2_str = null;

        // 값이 있는경우에 edittext에 값 삽입하기
        if(!lectios.isEmpty()){
            // 값이 있는 경우에는 edit 형태로 나타나야 한다.
            ll_main.setVisibility(View.GONE);
            after_save.setVisibility(View.VISIBLE);
            ll1.setBackgroundColor(Color.parseColor("#e3edf4"));

            onesentence_str = lectios.get(0).getOneSentence();
            bg1_str = lectios.get(0).getBg1();
            bg2_str = lectios.get(0).getBg2();
            bg3_str = lectios.get(0).getBg3();
            sum1_str = lectios.get(0).getSum1();
            sum2_str = lectios.get(0).getSum2();
            js1_str = lectios.get(0).getJs1();
            js2_str = lectios.get(0).getJs2();

            // edittext에 가져오기
            onesentence.setText(onesentence_str);
            bg1.setText(bg1_str);
            bg2.setText(bg2_str);
            bg3.setText(bg3_str);
            sum1.setText(sum1_str);
            sum2.setText(sum2_str);
            js1.setText(js1_str);
            js2.setText(js2_str);
        }

        // edit화면에 있는 textview 세팅 - 값이 있는 경우에 edit 화면에서 값 넣기
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
            onesentence.setText(onesentence_str);
            after_save_tv1.setText(bg1_str);
            after_save_tv2.setText(bg2_str);
            after_save_tv3.setText(bg3_str);
            after_save_tv4.setText(sum1_str);
            after_save_tv5.setText(sum2_str);
            after_save_tv6.setText(js1_str);
            after_save_tv7.setText(js2_str);

        }else{
            //값이 없는 경우에는 main화면이 보인다
            after_save.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            onesentence.setVisibility(View.GONE);
            firstSentence.setVisibility(View.GONE);
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
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        }

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
                            Intent i = new Intent(LectioActivity.this, SettingActivity.class);
                            startActivity(i);
                        }else if(item.equals("계정정보")){
                            Intent i = new Intent(LectioActivity.this, ProfileActivity.class);
                            startActivity(i);
                        }else if (item.equals("로그아웃")) {
                            ProfileActivity.logoutUser(session,LectioActivity.this);
                        }
                    }
                });
                return true;
            default:
                if(date_intent != null){
                    Intent intent = new Intent(LectioActivity.this, RecordActivity.class);
                    intent.putExtra("dateBack",date_intent);
                    startActivity(intent);

                // 거룩한 독서에서 시작버튼 누른 경우 뒤로가기 클릭시 이벤트
                }else if(start_now){
                    if (getCurrentFocus() != null) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    android.support.v7.app.ActionBar actionbar = getSupportActionBar();
                    actionbar.setDisplayHomeAsUpEnabled(false);
                    ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                    ll_main.setVisibility(View.VISIBLE);
                    start.setVisibility(View.VISIBLE);
                    prev.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    bg1.setVisibility(View.GONE);
                    bg2.setVisibility(View.GONE);
                    bg3.setVisibility(View.GONE);
                    sum1.setVisibility(View.GONE);
                    sum2.setVisibility(View.GONE);
                    js1.setVisibility(View.GONE);
                    js2.setVisibility(View.GONE);
                    save.setVisibility(View.GONE);
                    ll_upper.setVisibility(View.GONE);
                    firstSentence.setVisibility(View.GONE);
                    q1.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.VISIBLE);

                // 거룩한 독서에서 edit 누른 후 뒤로가기 누를때 이벤트
                }else if(edit_now){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    android.support.v7.app.ActionBar actionbar = getSupportActionBar();
                    actionbar.setDisplayHomeAsUpEnabled(false);
                    checkRecord();
                }else{

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
                android.support.v7.app.ActionBar actionbar = getSupportActionBar();
                // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.

                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                String background1 = bg1.getText().toString();
                String background2 = bg2.getText().toString();
                String background3 = bg3.getText().toString();
                String summary1 = sum1.getText().toString();
                String summary2 = sum2.getText().toString();
                String jesus1 = js1.getText().toString();
                String jesus2 = js2.getText().toString();
                String date1 = typedDate;
                String onesentence1 = onesentence.getText().toString();

                String bg1_str = null;

                ArrayList<Lectio> lectios = new ArrayList<Lectio>();
                DBManager dbMgr = new DBManager(LectioActivity.this);
                dbMgr.dbOpen();
                dbMgr.selectLectioData(LectioDBSqlData.SQL_DB_SELECT_DATA, uid, typedDate , lectios);
                dbMgr.dbClose();

                if(!lectios.isEmpty()){
                    bg1_str = lectios.get(0).getBg1();
                }
                    // 기존 값이 있는 경우 수정
                    if(bg1_str!= null){

                        dbMgr.dbOpen();
                        dbMgr.updateLectioData(LectioDBSqlData.SQL_DB_UPDATE_DATA, uid, date1, background1, background2, background3, summary1, summary2, jesus1, jesus2);
                        dbMgr.dbClose();

                        if(uid != null && uid != ""){
                            Lectio lectio = new Lectio(uid, date1, onesentence1, background1, background2, background3, summary1, summary2, jesus1, jesus2);
                            Server_LectioData.updateLectio(LectioActivity.this, uid, lectio);
                        }
                    // 기존 값이 없는 경우 추가
                    }else{

                        Lectio lectioData = new Lectio(uid, date1, onesentence1, background1, background2, background3, summary1, summary2, jesus1, jesus2);
                        dbMgr.dbOpen();
                        dbMgr.insertLectioData(LectioDBSqlData.SQL_DB_INSERT_DATA, lectioData);
                        dbMgr.dbClose();
                        if(uid != null && uid != ""){
                            Lectio lectio = new Lectio(uid, date1, onesentence1, background1, background2, background3, summary1, summary2, jesus1, jesus2);
                            Server_LectioData.insertLectio(LectioActivity.this, uid, lectio);
                        }
                    }

                // 실제 저장 마침

                // edit을 하는 경우
                if(edit_now){
                  //edit화면 보여지기
                    ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                    ll1.setVisibility(View.VISIBLE);
                    checkRecord();

                    if(date_intent == null){
                        actionbar.setDisplayHomeAsUpEnabled(false);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }

                // edit이 아니라 start한 경우는 마침기도로 넘어감
                }else{

                    lectio_order = 8;
                    save.setVisibility(View.GONE);
                    prev.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.VISIBLE);
                    js2.setVisibility(View.GONE);
                    q1.setText("마침기도");
                    ll_upper.setVisibility(View.GONE);
                    firstSentence.setVisibility(View.VISIBLE);
                    firstSentence.setBackgroundResource(R.drawable.pray2_img);
                    firstSentence.setMovementMethod(new ScrollingMovementMethod());
                    firstSentence.setText(Html.fromHtml("<br><br><br><br><br><br><br><br><br><font color=\"#ffffff\">"+
                            "주님께서 나에게 말씀하셨다.<br>" +
                            "\""+jesus2+"\"<br>" +
                            "<br>" +
                            "주님 제가 이 말씀을 깊이 새기고 <br>" +
                            "하루를 살아가도록 이끄소서. 아멘.<br>" +
                            "<br>" +
                            "(세번 반복한다)</font><br><br><br><br>"));

                }
            }
        }
    };


    // 버튼 선택시 값 변경 이벤트
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            android.support.v7.app.ActionBar actionbar = getSupportActionBar();
            switch (v.getId()) {
                case R.id.bt_prev:
                    if (getCurrentFocus() != null) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    if(lectio_order == -1){
                        // start인 경우만 해당
                        if(date_intent == null){
                            actionbar.setDisplayHomeAsUpEnabled(false);
                            ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                            ll_main.setVisibility(View.VISIBLE);
                            start.setVisibility(View.VISIBLE);
                            prev.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                            firstSentence.setVisibility(View.GONE);
                            q1.setVisibility(View.GONE);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }else{ // 이는 나의 기록에서 온 경우이다
                            ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                            ll_main.setVisibility(View.VISIBLE);
                            start.setVisibility(View.VISIBLE);
                            prev.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                            firstSentence.setVisibility(View.GONE);
                            q1.setVisibility(View.GONE);
                        }

                    }else if(lectio_order == 0){
                        // start인 경우만 해당
                        firstSentence.setMovementMethod(new ScrollingMovementMethod());
                        lectio_order = -1;
                        bg1.setVisibility(View.GONE);
                        q1.setText("시작 기도");
                        firstSentence.setText(Html.fromHtml("<br><font color=\"#ffffff\">"+
                                "오소서, 성령님<br>" +
                                "당신의 빛, 그 빛살을 하늘에서 내리소서.<br>" +
                                "가난한 이 아버지, 은총 주님<br>" +
                                "오소서 마음에 빛을 주소서.<br>" +
                                "가장 좋은 위로자, 영혼의 기쁜 손님,<br>" +
                                "생기 돋워 주소서.<br>" +
                                "일할 때에 휴식을, 무더울 때 바람을,<br>" +
                                "슬플 때에 위로를, 지복의 빛이시여,<br>" +
                                "저희 맘 깊은 곳을 가득히 채우소서.<br>" +
                                "주님 도움 없으면 저희 삶 그 모든 것<br>" +
                                "이로운 것 없으리.<br>" +
                                "허물은 씻어 주고 마른 땅 물 주시고<br>" +
                                "병든 것 고치소서.<br>" +
                                "굳은 맘 풀어 주고 찬 마음 데우시고<br>" +
                                "바른길 이끄소서.<br>" +
                                "성령님을 믿으며 의지하는 이에게<br>" +
                                "칠은을 베푸소서.<br>" +
                                "공덕을 쌓게  하고 구원의 문을 넘어<br>" +
                                "영복을 얻게 하소서.아멘</font><br><br>"));
                        firstSentence.setBackgroundResource(R.drawable.pray1_img);
                        ll_upper.setVisibility(View.GONE);

                    }else if(lectio_order == 1){
                        // edit 한 경우는 다시 돌아감
                        if(edit_now){
                            ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                            if(date_intent == null){
                                actionbar.setDisplayHomeAsUpEnabled(false);
                                bottomNavigationView.setVisibility(View.VISIBLE);
                            }
                            lectio_order = 0;
                            bg1.setVisibility(View.GONE);
                            checkRecord();
                        // start에서 온 경우는 말씀듣기로 이동
                        }else{
                            firstSentence.setBackgroundResource(0);
                            q1.setText("말씀 듣기");
                            firstSentence.setVisibility(View.VISIBLE);
                            firstSentence.setText("복음 말씀을 잘 듣기 위해 소리내어 읽어 봅시다.");
                            lectio_order = 0;
                            bg1.setVisibility(View.GONE);
                        }

                    }else if(lectio_order == 2){
                        lectio_order = 1;
                        q1.setText("복음의 등장인물은?");
                        bg1.setVisibility(View.VISIBLE);
                        bg2.setVisibility(View.GONE);
                    }else if(lectio_order == 3) {
                        lectio_order = 2;
                        q1.setText("복음의 배경장소는?");
                        bg2.setVisibility(View.VISIBLE);
                        bg3.setVisibility(View.GONE);
                    }else if(lectio_order == 4) {
                        q1.setText("배경시간 혹은 상황은?");
                        lectio_order = 3;
                        bg3.setVisibility(View.VISIBLE);
                        sum1.setVisibility(View.GONE);
                    }else if(lectio_order == 5) {
                        lectio_order = 4;
                        q1.setText("복음의 내용을 사건 중심으로 요약해 봅시다.");
                        sum1.setVisibility(View.VISIBLE);
                        sum2.setVisibility(View.GONE);
                    }else if(lectio_order == 6) {
                        q1.setText("특별히 눈에 띄는 부분은?");
                        lectio_order = 5;
                        sum2.setVisibility(View.VISIBLE);
                        js1.setVisibility(View.GONE);
                    }else if(lectio_order == 7) {
                        q1.setText("복음에서 보여지는 예수님의 모습은 어떠한가요?");
                        lectio_order = 6;
                        js1.setVisibility(View.VISIBLE);
                        js2.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                        save.setVisibility(View.GONE);

                 /*       LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int sizeInDP2 = 280;
                        int marginInDp2 = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, sizeInDP2, getResources()
                                        .getDisplayMetrics());
                        params.setMargins(10,10,10, marginInDp2);
                        down.setLayoutParams(params); */

                    }
                    break;
                case R.id.bt_next:
                    if (getCurrentFocus() != null) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    if(lectio_order == -1){
                        // start인 경우만
                        firstSentence.setMovementMethod(null);
                        firstSentence.setBackgroundResource(0);
                        lectio_order = 0;
                        q1.setText("말씀 듣기");
                        firstSentence.setText("복음 말씀을 잘 듣기 위해 소리내어 읽어 봅시다.");
                        ll_upper.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 0){
                        // start인 경우만
                        lectio_order = 1;
                        q1.setText("복음의 등장인물은?");
                        firstSentence.setVisibility(View.GONE);
                        bg1.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 1){
                        lectio_order = 2;
                        q1.setText("복음의 배경장소는?");
                        prev.setVisibility(View.VISIBLE);
                        bg1.setVisibility(View.GONE);
                        bg2.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 2) {
                        lectio_order = 3;
                        q1.setText("배경시간 혹은 상황은?");
                        bg2.setVisibility(View.GONE);
                        bg3.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 3) {
                        q1.setText("복음의 내용을 사건 중심으로 요약해 봅시다.");
                        lectio_order = 4;
                        bg3.setVisibility(View.GONE);
                        sum1.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 4) {
                        lectio_order = 5;
                        q1.setText("특별히 눈에 띄는 부분은?");
                        sum1.setVisibility(View.GONE);
                        sum2.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 5) {
                        q1.setText("복음에서 보여지는 예수님의 모습은 어떠한가요?");
                        lectio_order = 6;
                        sum2.setVisibility(View.GONE);
                        js1.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 6) {
                        lectio_order = 7;
                        q1.setText("복음을 통하여 예수님께서 내게 해주시는 말씀은?");
                        js1.setVisibility(View.GONE);
                        js2.setVisibility(View.VISIBLE);
                        next.setVisibility(View.INVISIBLE);
                        save.setVisibility(View.VISIBLE);

                 /*       LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int sizeInDP2 = 320;
                        int marginInDp2 = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, sizeInDP2, getResources()
                                        .getDisplayMetrics());
                        params.setMargins(10,10,10, marginInDp2);
                        down.setLayoutParams(params); */

                    }else if(lectio_order == 8){
                        if(date_intent == null){
                            actionbar.setDisplayHomeAsUpEnabled(false);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }

                        ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                        firstSentence.setVisibility(View.GONE);
                        ll1.setVisibility(View.VISIBLE);
                        checkRecord();
                    }
                    break;

                // start 클릭시 이벤트 - 시작기도가 나옴
                case R.id.bt_start:
                    if (wifi.isConnected() || mobile.isConnected()) {
                        ll_main.setVisibility(View.GONE);
                        ll1.setBackgroundColor(Color.parseColor("#e3edf4"));
                        // actionbar change
                        start_now = true;
                        edit_now = false;

                        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
                        actionbar.setDisplayHomeAsUpEnabled(true);
                        actionbar.setHomeAsUpIndicator(R.drawable.back);

                        bottomNavigationView.setVisibility(View.GONE);
                        firstSentence.setVisibility(View.VISIBLE);
                        lectio_order = -1;
                        prev.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                        q1.setVisibility(View.VISIBLE);
                        q1.setText("시작 기도");
                        firstSentence.setText(Html.fromHtml("<br><font color=\"#ffffff\">" +
                                "오소서, 성령님<br>" +
                                "당신의 빛, 그 빛살을 하늘에서 내리소서.<br>" +
                                "가난한 이 아버지, 은총 주님<br>" +
                                "오소서 마음에 빛을 주소서.<br>" +
                                "가장 좋은 위로자, 영혼의 기쁜 손님,<br>" +
                                "생기 돋워 주소서.<br>" +
                                "일할 때에 휴식을, 무더울 때 바람을,<br>" +
                                "슬플 때에 위로를, 지복의 빛이시여,<br>" +
                                "저희 맘 깊은 곳을 가득히 채우소서.<br>" +
                                "주님 도움 없으면 저희 삶 그 모든 것<br>" +
                                "이로운 것 없으리.<br>" +
                                "허물은 씻어 주고 마른 땅 물 주시고<br>" +
                                "병든 것 고치소서.<br>" +
                                "굳은 맘 풀어 주고 찬 마음 데우시고<br>" +
                                "바른길 이끄소서.<br>" +
                                "성령님을 믿으며 의지하는 이에게<br>" +
                                "칠은을 베푸소서.<br>" +
                                "공덕을 쌓게  하고 구원의 문을 넘어<br>" +
                                "영복을 얻게 하소서.아멘</font><br><br>"));
                        firstSentence.setBackgroundResource(R.drawable.pray1_img);
                        firstSentence.setMovementMethod(new ScrollingMovementMethod());
                        start.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(LectioActivity.this, "인터넷을 연결해주세요", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.bt_edit: // edit 버튼 누르는 경우 - 어떤 경우든지 복음의 등장인물은? 질문이 나옴
                    if (wifi.isConnected() || mobile.isConnected()) {
                        // actionbar change
                        ll1.setBackgroundColor(Color.parseColor("#e3edf4"));
                        start_now = false;
                        edit_now = true;

                        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                        actionbar.setDisplayHomeAsUpEnabled(true);
                        actionbar.setHomeAsUpIndicator(R.drawable.back);

                        edit.setVisibility(View.GONE);
                        bottomNavigationView.setVisibility(View.GONE);
                        lectio_order = 1;
                        onesentence.setVisibility(View.GONE);
                        after_save.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                        ll_upper.setVisibility(View.VISIBLE);
                        prev.setVisibility(View.VISIBLE);
                        next.setVisibility(View.VISIBLE);
                        q1.setText("복음의 등장인물은?");
                        firstSentence.setVisibility(View.GONE);
                        bg1.setVisibility(View.VISIBLE);
                        q1.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(LectioActivity.this, "인터넷을 연결해주세요", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    // actionbar 오른쪽 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_main, menu);

        return true;
    }

}
