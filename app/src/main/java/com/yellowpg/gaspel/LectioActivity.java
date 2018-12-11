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
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
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
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.DB.CommentDBSqlData;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.DB.LectioDBSqlData;
import com.yellowpg.gaspel.DB.WeekendDBSqlData;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.data.Weekend;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.server.Server_LectioData;
import com.yellowpg.gaspel.server.Server_WeekendData;

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

public class LectioActivity extends AppCompatActivity{
    final static String TAG = "lectioActivity";
    LinearLayout ll_first, ll1, ll_upper, ll_pray, ll_main;
    String date_intent;
    EditText bg1, bg2, bg3;
    EditText sum1, sum2;
    EditText js1, js2, weekend;
    ScrollView scrollView;
    LinearLayout after_save;
    Button save,start, edit;
    Button prev, next,  closePray;
    InputMethodManager imm;
    TextView contentsGaspel;
    TextView q1, firstSentence, pray_content;
    TextView after_save_tv1, after_save_tv2, after_save_tv3, after_save_tv4, after_save_tv5, after_save_tv6, after_save_tv7, after_save_tv8_1, after_save_tv8;
    Button onesentence;
    BottomNavigationView bottomNavigationView;
    String day;
    Calendar c1 = Calendar.getInstance();
    String typedDate;

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
    String weekend_date = null;
    Button declineButton;
    MenuInflater inflater;
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
        start_now = false;


        android.support.v7.app.ActionBar actionbar = getSupportActionBar();

        // bottomnavigation 뷰 등록
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        // intent값 가져오기 - 한주복음묵상에서 오는 경우
        Intent intent = getIntent();
        date_intent = intent.getStringExtra("date");

        // intent에 따른 actionbar, bottomnavigationview 상태 설정
        // 다른 activity에서 온 경우

        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar_lectio);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
        actionbar.setElevation(0);
        if(date_intent != null) {
            // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.back);
            bottomNavigationView.setVisibility(View.GONE);
        // 일반 렉시오 디비나의 경우
        }

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        ll_main= (LinearLayout) findViewById(R.id.ll_main);
        ll_first = (LinearLayout) findViewById(R.id.ll_first);
        ll1 = (LinearLayout) findViewById(R.id.ll1);
        ll_upper = (LinearLayout) findViewById(R.id.ll_upper);
        ll_pray= (LinearLayout) findViewById(R.id.pray);

        save = (Button) findViewById(R.id.bt_save);
        edit = (Button) findViewById(R.id.bt_edit);
        closePray =  (Button) findViewById(R.id.closePray);
        bg1 = (EditText) findViewById(R.id.et_background1);
        bg2 = (EditText) findViewById(R.id.et_background2);
        bg3 = (EditText) findViewById(R.id.et_background3);
        sum1 = (EditText) findViewById(R.id.et_summary1);
        sum2 = (EditText) findViewById(R.id.et_summary2);
        js1 = (EditText) findViewById(R.id.et_jesus1);
        js2 = (EditText) findViewById(R.id.et_jesus2);
        weekend = (EditText) findViewById(R.id.et_weekend);

        q1 = (TextView) findViewById(R.id.question1);
        pray_content = (TextView) findViewById(R.id.pray_content);

        // 데이터값이 있는 경우 보여질 view
        after_save = (LinearLayout) findViewById(R.id.after_save);
        after_save_tv1 = (TextView) findViewById(R.id.after_save_tv1);
        after_save_tv2 = (TextView) findViewById(R.id.after_save_tv2);
        after_save_tv3 = (TextView) findViewById(R.id.after_save_tv3);
        after_save_tv4 = (TextView) findViewById(R.id.after_save_tv4);
        after_save_tv5 = (TextView) findViewById(R.id.after_save_tv5);
        after_save_tv6 = (TextView) findViewById(R.id.after_save_tv6);
        after_save_tv7 = (TextView) findViewById(R.id.after_save_tv7);
        after_save_tv8_1 = (TextView) findViewById(R.id.after_save_tv8_1);
        after_save_tv8 = (TextView) findViewById(R.id.after_save_tv8);

        onesentence = (Button) findViewById(R.id.bt_onesentence);
        start = (Button) findViewById(R.id.bt_start);
        firstSentence = (TextView) findViewById(R.id.tv_first);

        contentsGaspel = (TextView) findViewById(R.id.tv_contents);

        bg1.setBackgroundResource(R.drawable.edit_bg_white);
        bg2.setBackgroundResource(R.drawable.edit_bg_white);
        bg3.setBackgroundResource(R.drawable.edit_bg_white);
        sum1.setBackgroundResource(R.drawable.edit_bg_white);
        sum2.setBackgroundResource(R.drawable.edit_bg_white);
        js1.setBackgroundResource(R.drawable.edit_bg_white);
        js2.setBackgroundResource(R.drawable.edit_bg_white);
        weekend.setBackgroundResource(R.drawable.edit_bg_white);

        after_save_tv1.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv2.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv3.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv4.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv5.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv6.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv7.setBackgroundResource(R.drawable.edit_bg_white);
        after_save_tv8.setBackgroundResource(R.drawable.edit_bg_white);
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
        weekend.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        after_save.setVisibility(View.GONE);


        prev.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        prev.setOnClickListener(listener);
        next.setOnClickListener(listener);
        start.setOnClickListener(listener);
        edit.setOnClickListener(listener);
        closePray.setOnClickListener(listener);

        save.setBackgroundResource(R.drawable.button_bg);
        edit.setBackgroundResource(R.drawable.button_bg);
        start.setBackgroundResource(R.drawable.button_bg2);


        // 맨처음에는 start 버튼만 보이도록 순서 세팅
        lectio_order = 0;

        // 다른 activity에서 온 경우 / 날짜에 맞는 복음 및 정보 가져온다
        if(date_intent != null){
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
            typedDate = date_val1+getDay()+"요일"; // c1으로 getday()함
            Log.d("saea", typedDate);
            if(typedDate.contains("일요일")){
                weekend_date = "일요일";
            }else{
                weekend_date = null;
            }
          //  getGaspel(date_val2);
        }else{
            typedDate = date_val+getDay()+"요일"; // c1으로 getday()함
            Log.d("ha", typedDate);
            if(typedDate.contains("일요일")){
                weekend_date = "일요일";
            }else{
                weekend_date = null;
            }
          //  checkRecord();
        }

        // 인터넷 연결 확인하는 부분
        if (wifi.isConnected() || mobile.isConnected()) {
            // exp : 복음 내용 데이터 가져오기
            checkRecord();
            getGaspel(date_val2);

        } else {
            contentsGaspel.setText("인터넷을 연결해주세요");
            contentsGaspel.setGravity(Gravity.CENTER);
        }


        // 맨처음에는 복음 내용이 보이지 않는다
        ll_upper.setVisibility(ll_upper.GONE);

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
            weekend.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv5.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv6.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv7.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            after_save_tv8.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
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


        // 저장을 누르면 실행되는 이벤트
        save.setOnClickListener(listener_save);

        //weekend에서 온 경우 바로 시작되어야 함
        String date_detail = intent.getStringExtra("date_detail"); // 날짜 형식 : yyyy년 MM월 dd일 x요일
        Boolean edit_true =  intent.getBooleanExtra("edit",false);
        if(date_detail != null && !edit_true){
            actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionbar.setCustomView(R.layout.actionbar_weekend);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.back);
            ll_main.setVisibility(View.GONE);
            // actionbar change
            start_now = true;

            // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
            // actionbar.setDisplayHomeAsUpEnabled(true);
            //  actionbar.setHomeAsUpIndicator(R.drawable.back);

            //  showPraying();
            bottomNavigationView.setVisibility(View.GONE);
            firstSentence.setVisibility(View.VISIBLE);
            lectio_order = -1;
            prev.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            q1.setVisibility(View.VISIBLE);
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
            ll1.setBackgroundColor(Color.parseColor("#e3edf4"));
            firstSentence.setMovementMethod(new ScrollingMovementMethod());
            start.setVisibility(View.GONE);
        }else if(date_detail != null && edit_true){
            checkRecord();
            ll1.setBackgroundColor(Color.parseColor("#e3edf4"));
            start_now = false;

            edit.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
            edit_now = true;
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
        }

    }
    // 저장된 값이 있는지 확인하고 있는경우 가져옴
    public void checkRecord(){

        bg1.setText("");
        bg2.setText("");
        bg3.setText("");
        sum1.setText("");
        sum2.setText("");
        js1.setText("");
        js2.setText("");

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        edit_now = false;
        // cf : 렉시오 디비나 부분


        String date_aft = typedDate;
        ArrayList<Lectio> lectios = new ArrayList<Lectio>();
        String lectio_str = null;
        DBManager dbMgr = new DBManager(LectioActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectLectioData(LectioDBSqlData.SQL_DB_SELECT_DATA, uid, date_aft, lectios);
        dbMgr.dbClose();
        String bg1_str = null;
        String bg2_str = null;
        String bg3_str = null;
        String sum1_str = null;
        String sum2_str = null;
        String js1_str = null;
        String js2_str = null;

        if(!lectios.isEmpty()){
            ll_main.setVisibility(View.GONE);
            after_save.setVisibility(View.VISIBLE);
            ll1.setBackgroundColor(Color.parseColor("#e3edf4"));
            bg1_str = lectios.get(0).getBg1();
            bg2_str = lectios.get(0).getBg2();
            bg3_str = lectios.get(0).getBg3();
            sum1_str = lectios.get(0).getSum1();
            sum2_str = lectios.get(0).getSum2();
            js1_str = lectios.get(0).getJs1();
            js2_str = lectios.get(0).getJs2();

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

        String mysentence = null;
        String mythought = "";
        ArrayList<Weekend> weekends = new ArrayList<Weekend>();
        String comment_str = null;
        dbMgr.dbOpen();
        dbMgr.selectWeekendData(WeekendDBSqlData.SQL_DB_SELECT_DATA, uid, typedDate, weekends);
        dbMgr.dbClose();

        if(!weekends.isEmpty()){
            Log.d("saea", "있음!!!!"+typedDate);
            mysentence = weekends.get(0).getMySentence();
            mythought = weekends.get(0).getMyThought();
            // edittext에 가져오기
            weekend.setText(mysentence);
        }else{
            Log.d("saea", "없음!!!!"+typedDate);
        }

        if(bg1_str != null){
            Intent intent = getIntent();
            Boolean weekend_true = intent.getBooleanExtra("weekend", false);
            if(weekend_true) {
                Log.d("saea", "일요일이지~");
                android.support.v7.app.ActionBar actionbar = getSupportActionBar();
                actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionbar.setCustomView(R.layout.actionbar_weekend);
                actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
                actionbar.setElevation(0);
                actionbar.setDisplayHomeAsUpEnabled(true);
                actionbar.setHomeAsUpIndicator(R.drawable.back);
            }

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
                Log.d("saea2", mythought);
                if(mythought.equals("")){

                }else{

                }
                after_save_tv1.setText(bg1_str);
                after_save_tv2.setText(bg2_str);
                after_save_tv3.setText(bg3_str);
                after_save_tv4.setText(sum1_str);
                after_save_tv5.setText(sum2_str);
                after_save_tv6.setText(js1_str);
                after_save_tv7.setText(js2_str);
                after_save_tv8.setText(mysentence);

            }else{

                after_save_tv1.setText(bg1_str);
                after_save_tv2.setText(bg2_str);
                after_save_tv3.setText(bg3_str);
                after_save_tv4.setText(sum1_str);
                after_save_tv5.setText(sum2_str);
                after_save_tv6.setText(js1_str);
                after_save_tv7.setText(js2_str);
                after_save_tv8_1.setVisibility(View.GONE);
                after_save_tv8.setVisibility(View.GONE);
            }

        }else{
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
            weekend.setVisibility(View.GONE);
            prev.setVisibility(View.GONE);
            next.setVisibility(View.GONE);

            Intent intent = getIntent();
            Boolean weekend_true = intent.getBooleanExtra("weekend", false);
            if(weekend_true){
                Log.d("saea", "일요일이지~");
                android.support.v7.app.ActionBar actionbar = getSupportActionBar();
                actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionbar.setCustomView(R.layout.actionbar_weekend);
                actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
                actionbar.setElevation(0);
                actionbar.setDisplayHomeAsUpEnabled(true);
                actionbar.setHomeAsUpIndicator(R.drawable.back);

                ImageView main1 = (ImageView) findViewById(R.id.main1);
                TextView main2 = (TextView) findViewById(R.id.main2);
                TextView main3 = (TextView) findViewById(R.id.main3);
                ImageView main5 = (ImageView) findViewById(R.id.main5);

                main1.setImageResource(R.drawable.weekend_img1);
                main2.setText("주일의 독서");
                main3.setText("Lectio Divina (dies dominica)");
                main5.setImageResource(R.drawable.weekend_img2);
            }
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
                            Intent i = new Intent(LectioActivity.this, LoginActivity.class);
                            startActivity(i);
                        }else if (item.equals("로그아웃")) {
                            ProfileActivity.logoutUser(session,LectioActivity.this);
                        }
                    }
                });
                return true;
            default:
                if(date_intent != null){
                    Intent intent0 =getIntent();
                    String date_detail = intent0.getStringExtra("date_detail"); // 날짜 형식 : yyyy년 MM월 dd일 x요일
                    if(date_detail != null){
                        finish();
                    }else{
                        Intent intent = new Intent(LectioActivity.this, RecordActivity.class);
                        intent.putExtra("dateBack",date_intent);
                        startActivity(intent);
                    }

                // 시작버튼 누른 경우 뒤로가기 클릭시 이벤트
                }else if(start_now){
                    start_now = false;
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
                    prev.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    onesentence.setVisibility(View.VISIBLE);
                    firstSentence.setVisibility(View.VISIBLE);
                    start.setVisibility(View.VISIBLE);
                    ll_upper.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    firstSentence.setText("당신께 응답하시는 하느님의 말씀을 깨닫기를 바라며 거룩한 독서를 시작해볼까요?");

                    // edit 누른 후 뒤로가기 누를때 이벤트
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
                String lectio_str = null;
                DBManager dbMgr = new DBManager(LectioActivity.this);
                dbMgr.dbOpen();
                dbMgr.selectLectioData(LectioDBSqlData.SQL_DB_SELECT_DATA, uid, typedDate , lectios);
                dbMgr.dbClose();

                if(!lectios.isEmpty()){
                    bg1_str = lectios.get(0).getBg1();
                }
                    if(bg1_str!= null){

                        dbMgr.dbOpen();
                        dbMgr.updateLectioData(LectioDBSqlData.SQL_DB_UPDATE_DATA, uid, date1, background1, background2, background3, summary1, summary2, jesus1, jesus2);
                        dbMgr.dbClose();

                        if(uid != null && uid != ""){
                            Lectio lectio = new Lectio(uid, date1, onesentence1, background1, background2, background3, summary1, summary2, jesus1, jesus2);
                            Server_LectioData.updateLectio(LectioActivity.this, uid, lectio);
                        }
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

                String mysentence = weekend.getText().toString();
                // 일요일의 경우에
                Intent intent_ = getIntent();
                Boolean weekend_true = intent_.getBooleanExtra("weekend", false);

                if(weekend_date != null || weekend_true){
                    weekend.setVisibility(View.GONE);
                    String weekend_date = typedDate;

                    String mysentence_str = null;
                    String mythought = null;
                    ArrayList<Weekend> weekends = new ArrayList<Weekend>();
                    String comment_str = null;
                    dbMgr.dbOpen();
                    dbMgr.selectWeekendData(WeekendDBSqlData.SQL_DB_SELECT_DATA, uid, typedDate, weekends);
                    dbMgr.dbClose();

                    if(!weekends.isEmpty()){
                        mysentence_str = weekends.get(0).getMySentence();
                        mythought = weekends.get(0).getMyThought();
                    }else{
                    }
                    if(mythought == null){
                        mythought = "";
                    }
                    if(mysentence_str!= null){
                        Log.d("saea", "기존 값이 있음");


                        dbMgr.dbOpen();
                        dbMgr.updateWeekendData(WeekendDBSqlData.SQL_DB_UPDATE_DATA, uid, weekend_date, mysentence, mythought);
                        dbMgr.dbClose();
                        if(uid != null && uid != ""){
                            Server_WeekendData.updateWeekend(LectioActivity.this, uid, weekend_date, mysentence, mythought);
                        }
                    }else {
                        Weekend weekendData = new Weekend(uid, weekend_date, mysentence, mythought);
                        dbMgr.dbOpen();
                        dbMgr.insertWeekendData(WeekendDBSqlData.SQL_DB_INSERT_DATA, weekendData);
                        dbMgr.dbClose();

                        if (uid != null && uid != "") {
                            Server_WeekendData.insertWeekend(LectioActivity.this, uid, weekend_date, mysentence, mythought);
                        }
                    }

                }

                // 저장 후에 기도하는 dialog 보이기

             //   showPraying2(summary2, jesus2, mysentence);
                if(edit_now){
                    Intent intent0 =getIntent();
                    Boolean edit_true = intent0.getBooleanExtra("edit", false);
                    if(edit_true){
                        Intent intent = new Intent(LectioActivity.this, WeekendActivity.class);
                        LectioActivity.this.startActivity(intent);
                    }else{
                        ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                        ll_pray.setVisibility(View.GONE);
                        ll1.setVisibility(View.VISIBLE);
                        checkRecord();


                        if(date_intent == null){
                            actionbar.setDisplayHomeAsUpEnabled(false);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }
                    }

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

                    if(mysentence == "" || mysentence.equals("")){
                        firstSentence.setText(Html.fromHtml("<br><br><br><br><br><br><br><br><br><font color=\"#ffffff\">"+
                                "주님께서 나에게 말씀하셨다.<br>" +
                                "\""+jesus2+"\"<br>" +
                                "<br>" +
                                "주님 제가 이 말씀을 깊이 새기고 <br>" +
                                "하루를 살아가도록 이끄소서. 아멘.<br>" +
                                "<br>" +
                                "(세번 반복한다)</font><br><br><br><br>"));
                    }else{
                        firstSentence.setText(Html.fromHtml("<br><br><br><br><br><br><br><br><br><font color=\"#ffffff\">"+
                                "주님께서 나에게 말씀하셨다.<br>" +
                                "\""+jesus2+"\"<br>" +
                                "\""+mysentence+"\"<br>" +
                                "<br>" +
                                "주님 제가 이 말씀을 깊이 새기고 <br>" +
                                "하루를 살아가도록 이끄소서. 아멘.<br>" +
                                "<br>" +
                                "(세번 반복한다)</font><br><br><br><br>"));
                    }

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

            android.support.v7.app.ActionBar actionbar = getSupportActionBar();
            switch (v.getId()) {
                case R.id.bt_prev:
                    if(lectio_order == -1){
                        Intent intent0 =getIntent();
                        String date_detail = intent0.getStringExtra("date_detail"); // 날짜 형식 : yyyy년 MM월 dd일 x요일

                        if(weekend_date != null && date_detail != null) {
                            finish();
                        }else if(date_intent == null){

                            ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                            ll_main.setVisibility(View.VISIBLE);
                            start.setVisibility(View.VISIBLE);
                            prev.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                            firstSentence.setVisibility(View.GONE);
                            q1.setVisibility(View.GONE);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }else{

                            ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                            ll_main.setVisibility(View.VISIBLE);
                            start.setVisibility(View.VISIBLE);
                            prev.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                            firstSentence.setVisibility(View.GONE);
                            q1.setVisibility(View.GONE);
                            actionbar.setDisplayHomeAsUpEnabled(true);
                            actionbar.setHomeAsUpIndicator(R.drawable.back);
                        }

                    }else if(lectio_order == 0){
                        firstSentence.setMovementMethod(new ScrollingMovementMethod());

                        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
                     //   actionbar.setDisplayHomeAsUpEnabled(false);
                       // actionbar.setDisplayHomeAsUpEnabled(true);
                     //   actionbar.setHomeAsUpIndicator(R.drawable.list);
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
                        if(edit_now){
                            Intent intent0 =getIntent();
                            Boolean edit_true = intent0.getBooleanExtra("edit", false);
                            if(edit_true){
                                finish();
                            }else{
                                ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                                if(date_intent == null){
                                    actionbar.setDisplayHomeAsUpEnabled(false);
                                    bottomNavigationView.setVisibility(View.VISIBLE);
                                }
                                lectio_order = 0;
                                bg1.setVisibility(View.GONE);
                                checkRecord();
                            }

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
                        if (date_intent != null && weekend_date != null) {
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
                        if (date_intent != null && weekend_date != null) {
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
                    if(lectio_order == -1){
                        firstSentence.setMovementMethod(null);
                        firstSentence.setBackgroundResource(0);
                        lectio_order = 0;
                        q1.setText("말씀 듣기");
                        firstSentence.setText("복음 말씀을 잘 듣기 위해 소리내어 읽어 봅시다.");
                        ll_upper.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 0){
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
                        if(date_intent != null && weekend_date != null){
                            js1.setVisibility(View.GONE);
                            js2.setVisibility(View.VISIBLE);
                        }else{
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
                        if (date_intent != null && weekend_date != null) {
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
                    }else if(lectio_order == 8){
                        ll1.setBackgroundColor(Color.parseColor("#ffffff"));
                    //    pray_content.setVisibility(View.GONE);
                        firstSentence.setVisibility(View.GONE);
                        ll_pray.setVisibility(View.GONE);
                        ll1.setVisibility(View.VISIBLE);
                        checkRecord();
                        if(date_intent == null){
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }

                        // 한주복음묵상에서 온경우 다시 이동
                        Intent intent0 =getIntent();
                        String date_detail = intent0.getStringExtra("date_detail"); // 날짜 형식 : yyyy년 MM월 dd일 x요일
                        Log.d("saea", "!!!!!!!!"+date_detail);
                        if(weekend_date != null && date_detail != null) {
                            Intent intent = new Intent(LectioActivity.this, WeekendActivity.class);
                            LectioActivity.this.startActivity(intent);
                        }
                    }


                    break;
                // start 클릭시 이벤트
                case R.id.bt_start:
                    ll_main.setVisibility(View.GONE);
                    ll1.setBackgroundColor(Color.parseColor("#e3edf4"));
                    // actionbar change
                    start_now = true;

                    // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
                   // actionbar.setDisplayHomeAsUpEnabled(true);
                  //  actionbar.setHomeAsUpIndicator(R.drawable.back);

                  //  showPraying();
                    bottomNavigationView.setVisibility(View.GONE);
                    firstSentence.setVisibility(View.VISIBLE);
                    lectio_order = -1;
                    prev.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    q1.setVisibility(View.VISIBLE);
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
                    firstSentence.setMovementMethod(new ScrollingMovementMethod());
                    start.setVisibility(View.GONE);
                    break;

                case R.id.bt_edit:
                    // actionbar change
                    ll1.setBackgroundColor(Color.parseColor("#e3edf4"));
                    start_now = false;
                    actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                    actionbar.setDisplayHomeAsUpEnabled(true);
                    actionbar.setHomeAsUpIndicator(R.drawable.back);

                    // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
                  //  actionbar.setDisplayHomeAsUpEnabled(true);
                  //  actionbar.setHomeAsUpIndicator(R.drawable.back);


                    /*
                    *     edit.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    edit_now = true;
                    lectio_order = 1;
                    onesentence.setVisibility(View.GONE);
                    firstSentence.setVisibility(View.VISIBLE);
                    firstSentence.setText("복음을 소리내어 읽어 봅시다. 가슴속에서 말씀을 느끼고 체험할 수 있도록 마음을 다하여 읽어 봅시다.");
                    after_save.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    ll_upper.setVisibility(View.VISIBLE);
                    prev.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    q1.setText("말씀 듣기");
                    q1.setVisibility(View.VISIBLE);
                    * */
                    edit.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    edit_now = true;
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
                    break;
                // start 클릭시 이벤트
                case R.id.closePray:
                    ll_pray.setVisibility(View.GONE);
                    ll1.setVisibility(View.VISIBLE);
                    checkRecord();
                    if(date_intent == null){
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }

                    // 한주복음묵상에서 온경우 다시 이동
                    Intent intent0 =getIntent();
                    String date_detail = intent0.getStringExtra("date_detail"); // 날짜 형식 : yyyy년 MM월 dd일 x요일
                    Log.d("saea", "!!!!!!!!"+date_detail);
                    if(weekend_date != null && date_detail != null) {
                        Intent intent = new Intent(LectioActivity.this, WeekendActivity.class);
                        LectioActivity.this.startActivity(intent);
                    }
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

    Thread t;
    TextView text_ttl;
    TextView text;
    // 벨 클릭시 성령청원기도 보이기
    public void showPraying()
    {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock myWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, TAG);
        myWakeLock.acquire(); //실행후 리소스 반환 필수
        MainActivity.releaseCpuLock();
        MainActivity.playSound(LectioActivity.this, "pray");

        // Create custom dialog object
        final Dialog dialog = new Dialog(LectioActivity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog);
        // Set dialog title
        dialog.setTitle("Custom Dialog");

        // progressbar show
        final ProgressBar progressbar = (ProgressBar) dialog.findViewById(R.id.progress);

        t = new Thread(new Runnable() {
            @Override
            public void run(){
                int progress=0;
                while(progress<100){
                    ++progress;
                    progressbar.setProgress(progress);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if(t.isInterrupted()) { break; }
                }
            }
        });
        t.start();

        text_ttl = (TextView) dialog.findViewById(R.id.titleDialog);
        //text_ttl.setText("성령 청원 기도");
        text_ttl.setText("\n침묵에 들어가는 단계\n");
        // set values for custom dialog components - text, image and button
        text = (TextView) dialog.findViewById(R.id.textDialog);
        text.setText("하느님의 현존을 느껴 봅시다. 하느님께서 주시는 새 마음으로 들어가도록 노력하며 \n" +
                "일상을 떠나 잠시지만 하느님 세계로 차원을 바꿔 봅시다.\n");
     /*   text.setText("오소서, 성령님\n" +
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
                */
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
              //  item.setIcon(getResources().getDrawable(R.drawable.notification_base));
            }
        });

        // 기도마침 클릭시 이벤트
        declineButton = (Button) dialog.findViewById(R.id.declineButton);
        declineButton.setText("[다음 단계]");
        final ImageView dialog_image = (ImageView) dialog.findViewById(R.id.dialog_image);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mMediaPlayer.stop();
                t.interrupt();
                if(declineButton.getText().equals("[다음 단계]")){
                    dialog_image.setVisibility(View.GONE);
                    progressbar.setVisibility(View.GONE);
                    declineButton.setText("[기도 마침]");
                    text_ttl.setText("성령 청원 기도");
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
                            "영복을 얻게 하소서.아멘");
                }else{
                    // Close dialog
                    dialog.dismiss();

                }

            }
        });
    }


    public void showPraying2(String summary2, String jesus2, String mysentence)
    {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock myWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, TAG);
        myWakeLock.acquire(); //실행후 리소스 반환 필수
        MainActivity.releaseCpuLock();
//        MainActivity.playSound(LectioActivity.this, "pray");

        // Create custom dialog object
        final Dialog dialog = new Dialog(LectioActivity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog);
        // Set dialog title
        dialog.setTitle("Custom Dialog");
        final ProgressBar progressbar = (ProgressBar) dialog.findViewById(R.id.progress);

        t = new Thread(new Runnable() {
            @Override
            public void run(){
                int progress=0;
                while(progress<100){
                    ++progress;
                    progressbar.setProgress(progress);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if(t.isInterrupted()) { break; }
                }
            }
        });
        t.start();

        text_ttl = (TextView) dialog.findViewById(R.id.titleDialog);
        //text_ttl.setText("성령 청원 기도");
        text_ttl.setText("\n주님께서 내게 주신 깨달음에 대해 감사하며 이를 실천할 수 있는 힘을 달라고 주님께 기도해 봅시다.\n");
        // set values for custom dialog components - text, image and button
        text = (TextView) dialog.findViewById(R.id.textDialog);
        if(mysentence == null){
            if(summary2 == null){
                summary2 = "";
            }
            if(jesus2 == null){
                jesus2 = "";
            }
            text.setText(Html.fromHtml("<font color=\"#16a085\">"+summary2+"</font><br><font color=\"#16a085\">"+jesus2+"</font>"));
        }else{
            text.setText(Html.fromHtml("<font color=\"#16a085\">"+mysentence+"</font><br><font color=\"#16a085\">"+summary2+"\n</font><br><font color=\"#16a085\">"+jesus2+"</font>"));
        }

        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //  item.setIcon(getResources().getDrawable(R.drawable.notification_base));
            }
        });

        // 기도마침 클릭시 이벤트
        declineButton = (Button) dialog.findViewById(R.id.declineButton);
        declineButton.setText("[기도 마침]");
        final ImageView dialog_image = (ImageView) dialog.findViewById(R.id.dialog_image);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    MainActivity.mMediaPlayer.stop();
                t.interrupt();
                if(date_intent == null){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }

                // Close dialog
                dialog.dismiss();

                // 한주복음묵상에서 온경우 다시 이동
                Intent intent0 =getIntent();
                String date_detail = intent0.getStringExtra("date_detail"); // 날짜 형식 : yyyy년 MM월 dd일 x요일
                Log.d("saea", "!!!!!!!!"+date_detail);
                if(weekend_date != null && date_detail != null) {
                    Intent intent = new Intent(LectioActivity.this, WeekendActivity.class);
                    LectioActivity.this.startActivity(intent);
                }

            }
        });
    }

    // actionbar 오른쪽 기도하기 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_lectio, menu);

        return true;
    }

}
