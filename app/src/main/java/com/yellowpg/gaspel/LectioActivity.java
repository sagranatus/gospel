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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.yellowpg.gaspel.DB.CommentInfoHelper;
import com.yellowpg.gaspel.DB.LectioInfoHelper;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.OnKeyboardVisibilityListener;
import com.yellowpg.gaspel.googlesync.MakeInsertTask;
import com.yellowpg.gaspel.googlesync.MakeUpdateTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.EasyPermissions;

public class LectioActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, OnKeyboardVisibilityListener {
    final static String TAG = "lectioActivity";
LinearLayout ll_notyet, ll_first, ll1, ll2, ll3, ll_upper, ll_date;
    Button bt_notyet;
    EditText bg1, bg2, bg3;
    EditText sum1, sum2;
    EditText js1, js2;
    Button save,start, showgaspel, stopgaspel;
    Button prev, next;
    Button date;
    ImageButton before, after;
    InputMethodManager imm;
    String urlAddr = "http://i.catholic.or.kr/missa/";
    TextView contentsGaspel;
    TextView q1, firstSentence;
    Button onesentence;
    BottomNavigationView bottomNavigationView;
    String day;
    Calendar c1 = Calendar.getInstance();
    //현재 해 + 달 구하기
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 ");
    String date_val = sdf.format(c1.getTime());

    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    String date_val2 = sdf2.format(c1.getTime());

    LectioInfoHelper lectioInfoHelper;
    int already;

    ListSelectorDialog dlg_left;
    String[] listk_left, listv_left;

    int lectio_order;

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
        CommentInfoHelper commentInfoHelper;
        commentInfoHelper = new CommentInfoHelper(this);
        SQLiteDatabase db;
        try {
            String date_str = null;
            db = commentInfoHelper.getReadableDatabase();
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
            commentInfoHelper = new CommentInfoHelper(this);

            // String comment_str = null;
            String date_str = null;
            //  String sentence_str = null;
            db = commentInfoHelper.getReadableDatabase();
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

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();

//actionbar setting
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar_lectio);
        TextView mytext = (TextView) findViewById(R.id.mytext);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2980b9")));
        actionbar.setElevation(0);
        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.list);

       // bt_notyet = (Button)findViewById(R.id.bt_notyet);
       // ll_notyet = (LinearLayout) findViewById(R.id.ll_notyet);
        ll_first = (LinearLayout) findViewById(R.id.ll_first);
        ll1 = (LinearLayout) findViewById(R.id.ll1);
       // ll2 = (LinearLayout) findViewById(R.id.ll2);
       // ll3 = (LinearLayout) findViewById(R.id.ll3);
        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        ll_upper = (LinearLayout) findViewById(R.id.ll_upper);
      //  showgaspel = (Button) findViewById(R.id.bt_showgaspel);
      //  stopgaspel = (Button) findViewById(R.id.bt_stopgaspel);
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
       // q2 = (TextView) findViewById(R.id.question2);
        //q3 = (TextView) findViewById(R.id.question3);

        onesentence = (Button) findViewById(R.id.bt_onesentence);
        start = (Button) findViewById(R.id.bt_start);
        firstSentence = (TextView) findViewById(R.id.tv_first);

        contentsGaspel = (TextView) findViewById(R.id.tv_contents);

      //  ll2.setVisibility(View.GONE);
        //ll3.setVisibility(View.GONE);
        q1.setVisibility(View.GONE);
        bg1.setVisibility(View.GONE);
        bg2.setVisibility(View.GONE);
        bg3.setVisibility(View.GONE);
        bg3.setVisibility(View.GONE);
        sum1.setVisibility(View.GONE);
        sum2.setVisibility(View.GONE);
        js1.setVisibility(View.GONE);
        js2.setVisibility(View.GONE);
        save.setVisibility(View.GONE);


        prev = (Button) findViewById(R.id.bt_prev);
        next = (Button) findViewById(R.id.bt_next);
        prev.setBackgroundResource(R.drawable.button_bg_grey);
        next.setBackgroundResource(R.drawable.button_bg_grey);

        prev.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        prev.setOnClickListener(listener);
        next.setOnClickListener(listener);
        start.setOnClickListener(listener);
        SharedPreferences sp_level = getSharedPreferences("setting",0);
        String level = sp_level.getString("level", "");
        lectio_order = 0;

     /*  if(level.equals("2") || level.equals("3")){
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
        */
        // exp : onesentence는 안보이게 설정했다
      //  onesentence.setVisibility(onesentence.GONE);
        // exp : date에 오늘 날짜를 넣었다
        date.setText(date_val+MainActivity.getDay()+"요일");

        // exp : 렉시오디비나 작성내용 가져오기
        lectioInfoHelper = new LectioInfoHelper(this);
        // exp : 데이터가 있는경우 보이도록 edittext에 삽입한다
        getDataBase();

        // exp : 맨처음에는 복음 내용이 보이지 않는다
      //  ll_upper.setVisibility(ll_upper.GONE);

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
        listk_left = new String[] {"a", "b", "c"};
        listv_left = new String[] {"사용 설명서", "설정", "나의 상태"};

        // exp : 텍스트사이즈 설정
        SharedPreferences sp = getSharedPreferences("setting",0);
        String textsize = sp.getString("textsize", "");
        if(textsize.equals("small")){

            showgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            stopgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            contentsGaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            q1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            save.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
         //   bt_notyet.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        }else if(textsize.equals("big")){
            showgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            stopgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            contentsGaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            q1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            save.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
        //    bt_notyet.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
        }else if(textsize.equals("toobig")){
            showgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            stopgaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            contentsGaspel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            q1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
            bg1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            bg2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            bg3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            sum1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            sum2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            js1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            js2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            save.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
        //    bt_notyet.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
        }else{

        }

        // 키보드 없애기 - 새로 추가한 부분
        findViewById(R.id.ll_first).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
        findViewById(R.id.ll1).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
        findViewById(R.id.ll_upper).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
        findViewById(R.id.tv_contents).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });

        setKeyboardVisibilityListener(this);

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

    // 커스텀 다이얼로그 선택시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pray:
                showPraying(item);
                return true;
            default:
                // show the list dialog.
                dlg_left.show(listv_left, listk_left, new ListSelectorDialog.listSelectorInterface() {

                    // procedure if user cancels the dialog.
                    public void selectorCanceled() {
                    }
                    // procedure for when a user selects an item in the dialog.
                    public void selectedItem(String key, String item) {
                        if(item.equals("사용 설명서")){
                            Intent i = new Intent(LectioActivity.this, ExplainActivity.class);
                            startActivity(i);
                        }else if(item.equals("설정")){
                            Intent i = new Intent(LectioActivity.this, ThirdActivity.class);
                            startActivity(i);
                        }else if(item.equals("나의 상태")){
                            Intent i = new Intent(LectioActivity.this, StatusActivity.class);
                            startActivity(i);
                        }
                    }
                });
                return true;
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
         //   hideKeyboard();
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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
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
                    if (!error) { // error가 false인 경우에 데이터가져오기 성공
                        // Now store the user in SQLite
                        gaspel_date = jObj.getString("created_at");

                        gaspel_sentence = jObj.getString("sentence");
                        gaspel_contents = jObj.getString("contents");
                        String contents = gaspel_contents;
                        contents = contents.replaceAll("&gt;", ">");
                        contents = contents.replaceAll("&lt;", "<");
                        contents = contents.replaceAll("&ldquo;", "");
                        contents = contents.replaceAll("&rdquo;", "");
                        contents = contents.replaceAll("&lsquo;", "");
                        contents = contents.replaceAll("&rsquo;", "");
                        contents = contents.replaceAll("&prime;", "'");
                        contents = contents.replaceAll("\n", " ");
                        contents = contents.replaceAll("주님의 말씀입니다.", "\n"+"주님의 말씀입니다.");

                        //contents = contents.replaceAll("거룩한 복음입니다.", "거룩한 복음입니다."+"\n");

                        int idx = contents.indexOf("✠");
                        int idx2 = contents.indexOf("◎ 그리스도님 찬미합니다");
                        contents = contents.substring(idx, idx2);

                        int idx3 = contents.indexOf("거룩한 복음입니다.");
                        int length = "거룩한 복음입니다.".length();
                        final String after = contents.substring(idx3+length+11);
                        //Log.d("s", after);

                        Pattern p = Pattern.compile(".\\d+");
                        Matcher m = p.matcher(after);
                        while (m.find()) {
                            Log.d("s", after);
                            contents = contents.replaceAll(m.group(), "\n"+m.group());
                            //	Log.d("s", contents);
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

        gaspel_date = "date";
       // Log.d(TAG,gaspel_date);
        //	gaspel_sentence = "sentence";
        //	gaspel_contents = "contents";
            }
        });
        t.start();

    }

  /*  // exp : 키보드 가려지는 이벤트
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

    */




    // exp : 다른 곳 터치시 키보드 안보이게 하기 및 날짜 이전 이후 선택시 값 변경 이벤트
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        //    hideKeyboard();
            switch (v.getId()) {
                case R.id.bt_prev:
                    if(lectio_order == 1){
                        onesentence.setVisibility(View.VISIBLE);
                        firstSentence.setVisibility(View.VISIBLE);
                        start.setVisibility(View.VISIBLE);
                        prev.setVisibility(View.GONE);
                        next.setVisibility(View.GONE);
                        q1.setVisibility(View.GONE);
                        lectio_order = 0;
                        bg1.setVisibility(View.GONE);
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
                        q1.setText("복음 전체 내용을 \n사건 중심으로 요약해 봅시다");
                        lectio_order = 5;
                        sum2.setVisibility(View.VISIBLE);
                        js1.setVisibility(View.GONE);
                    }else if(lectio_order == 7) {
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
                        q1.setText("복음 전체 내용을 \n사건 중심으로 요약해 봅시다");
                        lectio_order = 4;
                        bg3.setVisibility(View.GONE);
                        sum1.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 4) {
                        lectio_order = 5;
                        sum1.setVisibility(View.GONE);
                        sum2.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 5) {
                        q1.setText("복음에서 보여지는 예수님의 \n모습을 보고 생각해봅시다");
                        lectio_order = 6;
                        sum2.setVisibility(View.GONE);
                        js1.setVisibility(View.VISIBLE);
                    }else if(lectio_order == 6) {
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

                case R.id.bt_before:
                    c1.add( Calendar.DATE, -1 );
                    date_val = sdf.format(c1.getTime());
                    date.setText(date_val+MainActivity.getDay()+"요일");
                    date_val2 = sdf2.format(c1.getTime());

                    getGaspel(date_val2);
                    getDataBase();
                    break;
                case R.id.bt_after:
                    c1.add( Calendar.DATE, 1 );
                    date_val = sdf.format(c1.getTime());
                    date.setText(date_val+MainActivity.getDay()+"요일");
                    date_val2 = sdf2.format(c1.getTime());
                    getGaspel(date_val2);
                    getDataBase();
                    break;
            }
        }
    };

    /*
   // exp : 복음 보이기, 가리기 이벤트
    View.OnClickListener showlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //  hideKeyboard();
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

    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_main, menu);
        //   DBManager dbMgr2 = new DBManager(this);
        //   dbMgr2.dbOpen();

        return true;
    }

    public void showPraying(final MenuItem item)
    {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock myWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, TAG);
        myWakeLock.acquire(); //실행후 리소스 반환 필수
        MainActivity.releaseCpuLock();
        MainActivity.playSound(LectioActivity.this, "pray"); //

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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }


    // softkeyboard show/hide catch event
    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }


    @Override
    public void onVisibilityChanged(boolean visible) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int sizeInDP = 400;
        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, getResources()
                        .getDisplayMetrics());

        int sizeInDP2 = 280;
        int marginInDp2 = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP2, getResources()
                        .getDisplayMetrics());
        if(visible == true){
         //   params.setMargins(10,10,10, marginInDp);
         //   contentsGaspel.setLayoutParams(params);
        }else{
          //  params.setMargins(10,10,10,marginInDp2);
          //  contentsGaspel.setLayoutParams(params);
        }
      //  Toast.makeText(LectioActivity.this, visible ? "Keyboard is active" : "Keyboard is Inactive", Toast.LENGTH_SHORT).show();
    }

}
