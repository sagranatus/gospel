package com.yellowpg.gaspel;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.DB.CommentDBSqlData;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.DB.LectioDBSqlData;
import com.yellowpg.gaspel.DB.UsersDBSqlData;
import com.yellowpg.gaspel.DB.WeekendDBSqlData;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.data.UserData;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.DownloadImageTask_bitmap;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.etc.getDay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class FirstActivity extends AppCompatActivity {
    final static String TAG = "FirstActivity";
    private SessionManager session;
    String uid = null;
    Button circle1, circle2, circle3;
    BottomNavigationView bottomNavigationView;
    ListSelectorDialog dlg_left;
    String[] listk_left, listv_left;
    SharedPreferences pref;
    Calendar c1 = Calendar.getInstance();

    //현재 해 + 달 구하기
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy. MM. dd.");
    String date_val1 = sdf1.format(c1.getTime());

    SimpleDateFormat year = new SimpleDateFormat("yyyy");
    String year_val = year.format(c1.getTime());

    SimpleDateFormat month = new SimpleDateFormat("MM");
    String month_val = month.format(c1.getTime());

    SimpleDateFormat sdf_today = new SimpleDateFormat("yyyy년 MM월 dd일 ");
    String date_today = sdf_today.format(c1.getTime());

    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    String date_val2 = sdf2.format(c1.getTime());

    String date_val2_yesterday;
    TextView todaysentence;
    TextView date;

    public static MediaPlayer mMediaPlayer;
    private static PowerManager.WakeLock myWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        //session 정보 가져오기
        session = new SessionManager(getApplicationContext());
        uid = session.getUid();

        // 첫화면에서 로그인 안한 경우에는 이렇게 넣으면 된다.
        if (uid == null || uid == "") {
            Log.d("saea", "this is ");
            Intent i0 = new Intent(FirstActivity.this, PreviousActivity.class);
            startActivity(i0);
        } else {
            // 만약 로그인상태에서 users 테이블이 없는 경우는 강제로 logout한다.
            SQLiteDatabase db = null;
            Boolean result = checkTable(db);
            if (!result) {
                Log.d("saea", "테이블 없음");
                logoutUser();
            } else {
                Log.d("saea", "테이블 있음");
            }
        }

        // 세팅에서 알람 설정
        Intent intent = getIntent();
        String alarm = intent.getStringExtra("str");
        if(alarm!=null){
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            myWakeLock= pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, TAG);
            myWakeLock.acquire(); //실행후 리소스 반환 필수
            releaseCpuLock();
            playSound(FirstActivity.this, "alarm");

        }

        // 인터넷연결 확인
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //actionbar setting
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
        actionbar.setElevation(0);


        todaysentence = (TextView) findViewById(R.id.bt_today); // 오늘 구절
        date = (TextView) findViewById(R.id.date); // 날짜

        circle1 = (Button) findViewById(R.id.circle1);
        circle2 = (Button) findViewById(R.id.circle2);
        circle3 = (Button) findViewById(R.id.circle3);


        // textsize 설정
        SharedPreferences sp = getSharedPreferences("setting", 0);
        String textsize = sp.getString("textsize", "");
        if (textsize.equals("big")) {
            todaysentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
        } else {

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
                        if (date_today.contains("일요일")) {
                            Toast.makeText(FirstActivity.this, "일요일에는 주일의 독서를 해주세요", Toast.LENGTH_SHORT).show();
                            Intent i1 = new Intent(FirstActivity.this, FirstActivity.class);
                            startActivity(i1);
                            break;
                        } else {
                            Intent i = new Intent(FirstActivity.this, MainActivity.class);
                            startActivity(i);
                            break;
                        }

                    case R.id.action_two:
                        if (date_today.contains("일요일")) {
                            Toast.makeText(FirstActivity.this, "일요일에는 주일의 독서를 해주세요", Toast.LENGTH_SHORT).show();
                            Intent i2 = new Intent(FirstActivity.this, FirstActivity.class);
                            startActivity(i2);
                            break;
                        } else {
                            Intent i2 = new Intent(FirstActivity.this, LectioActivity.class);
                            startActivity(i2);
                            break;
                        }
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
        dlg_left = new ListSelectorDialog(this, "Select an Operator");

        // custom dialog key, value 설정
        listk_left = new String[]{"a", "b", "c"};
        listv_left = new String[]{"설정", "계정정보", "로그아웃"};



       // c1.add(Calendar.DATE, 1);
        date_today = sdf_today.format(c1.getTime());
        date_today = date_today +""+getDay.getDay(c1)+"요일";
        Log.d("saea", "today"+date_today);

        String typedDate = date_val1; // yyyy. MM. dd. 형식
        date.setText(typedDate);

        // sharedpreference (todaysentence)에 저장한 값을 불러온다. id는 date_val2(오늘날짜 yyyy-MM-dd)
        pref = FirstActivity.this.getSharedPreferences("todaysentence", 0);
        String saved_todaysentence = pref.getString(date_val2, "");        // 오늘 구절은 sharedpreference에 저장해둔다.

        // 저장값이 있는 경우
        if (saved_todaysentence != "") {
            Log.d("saea", "saved already");
            todaysentence.setText("\" " + saved_todaysentence + " \"");
            ImageView img3 = (ImageView) findViewById(R.id.img2);

            ContextWrapper cw = new ContextWrapper(FirstActivity.this);
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            try {
                File f=new File(directory, "firstimage");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                img3.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        } else {

            if ((wifi.isConnected() || mobile.isConnected())) {
                Log.d("saea", "get data");
                // 첫 구절 가져오기
                getGaspel(date_val2); // yyyy-MM-dd

                // 이미지 다운로드 및 저장 후에 불러오기
                String urldisplay ="https://sssagranatus.cafe24.com/resource/firstimg.png";
                Log.d("saea", "urld"+urldisplay);
                Bitmap mbitmap = null;
                new DownloadImageTask_bitmap(FirstActivity.this, uid, "firstimage", mbitmap).execute(urldisplay);
                ImageView img3 = (ImageView) findViewById(R.id.img2);

                ContextWrapper cw = new ContextWrapper(FirstActivity.this);
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                try {
                    File f=new File(directory, "firstimage");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    img3.setImageBitmap(b);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            } else {
                todaysentence.setText("인터넷을 연결해주세요");
            }
        }

        // circle에 들어갈 값 가져오기
        // 오늘값 가져오기
        int todaynum = getData("today", "", "");
        Log.d("saea", todaynum+"today");
        circle1.setText(Integer.toString(todaynum));

        // 일주일 값 가져오기
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if(date_today.contains("일요일")){ // 일요일인 경우 전주 값 가져와야 함
            cal.add(Calendar.DATE, -7);
        }
        SimpleDateFormat sdf_monday = new SimpleDateFormat("dd");
        String date_monday = sdf_monday.format(cal.getTime()); // 월요일 날짜 dd 형식

        int weeknum = getData("weekend", month_val, date_monday);
        Log.d("saea", weeknum+"weeknum");
        circle2.setText(Integer.toString(weeknum));

        // 한달 값 가져오기
        int monthnum = getData("month", month_val, "");
        Log.d("saea", monthnum+"monthnum");
        circle3.setText(Integer.toString(monthnum));

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
                        if (item.equals("설정")) {
                            Intent i = new Intent(FirstActivity.this, SettingActivity.class);
                            startActivity(i);
                        } else if (item.equals("계정정보")) {
                            Intent i = new Intent(FirstActivity.this, ProfileActivity.class);
                            startActivity(i);
                        }else if (item.equals("로그아웃")) {
                            ProfileActivity.logoutUser(session,FirstActivity.this);
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


    // 오늘 날짜 복음 구절 가져오기
    public void getGaspel(final String date) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Tag used to cancel the request
                String tag_string_req = "req_getgaspel";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_TODAY, new Response.Listener<String>() {
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
                                todaysentence.setText("\" " + gaspel_sentence + " \"");

                                //preference에 저장한다.
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(date, gaspel_sentence);

                                // 전날 preference값을 지운다.
                                Calendar c1 = Calendar.getInstance();
                                c1.add(Calendar.DATE, -1);
                                date_val2_yesterday = sdf2.format(c1.getTime());
                                Log.d("saea", date_val2_yesterday );
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


    // 로그아웃할때
    public void logoutUser() {
        session.setLogin(false, "");

        SQLiteDatabase db = null;

        DBManager dbMgr = new DBManager(FirstActivity.this);

        dbMgr.dbOpen();
        dbMgr.deleteCommentData(CommentDBSqlData.SQL_DB_DELETE_DATA, uid);
        dbMgr.dbClose();

        dbMgr.dbOpen();
        dbMgr.deleteLectioData(LectioDBSqlData.SQL_DB_DELETE_DATA, uid);
        dbMgr.dbClose();

        dbMgr.dbOpen();
        dbMgr.deleteWeekendData(WeekendDBSqlData.SQL_DB_DELETE_DATA, uid);
        dbMgr.dbClose();

        // Launching the login activity
        Intent intent = new Intent(FirstActivity.this, PreviousActivity.class);
        startActivity(intent);
        finish();
    }


    public boolean checkTable(SQLiteDatabase db) {
        //테이블이 있다는 경우 true, 없는 경우 false를 반환
        ArrayList<UserData> user = new ArrayList<UserData>();
        try {
            DBManager dbMgr = new DBManager(FirstActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectUserData(UsersDBSqlData.SQL_DB_SELECT_DATA, uid, user);
            dbMgr.dbClose();
            Log.d("saea", "user" + user.get(0).getName());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // actionbar 오른쪽 아이콘 추가
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_main, menu);
        return true;
    }

    public int getData(String where, String month, String date) {

        int j = 0;
        if (where.equals("today")) {
            int i = 0;

            //comment
            ArrayList<Comment> comments_arr = new ArrayList<Comment>();
            DBManager dbMgr = new DBManager(FirstActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectCommentAllData("SELECT * FROM comment WHERE uid = ? AND date LIKE '%" + date_today + "%'", uid, comments_arr);
            dbMgr.dbClose();

            if (!comments_arr.isEmpty()) {
                i++;
            }

            ArrayList<Lectio> lectios_arr = new ArrayList<Lectio>();
            dbMgr.dbOpen();
            dbMgr.selectLectioAllData("SELECT * FROM lectio WHERE uid = ? AND date LIKE '%" + date_today + "%'", uid, lectios_arr);
            dbMgr.dbClose();

            if (!lectios_arr.isEmpty()) {
                i++;
            }
            if (i > 0) {
                j++;
            }

        }else if(where.equals("month")){

            for(int k=1; k<32; k++){
                int i = 0;
                //comment
                String day = String.format("%02d", k);

                String thisdate = year_val+"년 "+month+"월 "+day+"일";
                Log.d("saea",thisdate);
                ArrayList<Comment> comments_arr = new ArrayList<Comment>();
                DBManager dbMgr = new DBManager(FirstActivity.this);
                dbMgr.dbOpen();
                dbMgr.selectCommentAllData("SELECT * FROM comment WHERE uid = ? AND date LIKE '%" + thisdate + "%'", uid, comments_arr);
                dbMgr.dbClose();

                if (!comments_arr.isEmpty()) {
                    i++;
                }
                //lectio
                ArrayList<Lectio> lectios_arr = new ArrayList<Lectio>();
                dbMgr.dbOpen();
                dbMgr.selectLectioAllData("SELECT * FROM lectio WHERE uid = ? AND date LIKE '%" + thisdate + "%'", uid, lectios_arr);
                dbMgr.dbClose();

                if (!lectios_arr.isEmpty()) {
                    i++;
                }
                if (i > 0) {
                    j++;
                }
            }

        }else if(where.equals("year")){

            for(int k=1; k<32; k++){
                int i = 0;
                //comment
                int length = (int)(Math.log10(k)+1);
                String day = String.format("%02d", k);

                String thisdate = year_val+"년 "+month+"월 "+day+"일";
                Log.d("saea",thisdate);
                ArrayList<Comment> comments_arr = new ArrayList<Comment>();
                DBManager dbMgr = new DBManager(FirstActivity.this);
                dbMgr.dbOpen();
                dbMgr.selectCommentAllData("SELECT * FROM comment WHERE uid = ? AND date LIKE '%" + thisdate + "%'", uid, comments_arr);
                dbMgr.dbClose();

                if (!comments_arr.isEmpty()) {
                    i++;
                }

                ArrayList<Lectio> lectios_arr = new ArrayList<Lectio>();
                dbMgr.dbOpen();
                dbMgr.selectLectioAllData("SELECT * FROM lectio WHERE uid = ? AND date LIKE '%" + thisdate + "%'", uid, lectios_arr);
                dbMgr.dbClose();

                if (!lectios_arr.isEmpty()) {
                    i++;
                }
                if (i > 0) {
                    j++;
                }
            }

        }else if(where.equals("weekend")){

            int monday = Integer.parseInt(date);
            for(int k=monday; k<monday+7; k++){
                int i = 0;
                //comment
                String day = String.format("%02d", k);

                String thisdate = year_val+"년 "+month+"월 "+day+"일";
                Log.d("saea",thisdate);
                ArrayList<Comment> comments_arr = new ArrayList<Comment>();
                DBManager dbMgr = new DBManager(FirstActivity.this);
                dbMgr.dbOpen();
                dbMgr.selectCommentAllData("SELECT * FROM comment WHERE uid = ? AND date LIKE '%" + thisdate + "%'", uid, comments_arr);
                dbMgr.dbClose();

                if (!comments_arr.isEmpty()) {
                    i++;
                }

                //lectio
                ArrayList<Lectio> lectios_arr = new ArrayList<Lectio>();
                dbMgr.dbOpen();
                dbMgr.selectLectioAllData("SELECT * FROM lectio WHERE uid = ? AND date LIKE '%" + thisdate + "%'", uid, lectios_arr);
                dbMgr.dbClose();

                if (!lectios_arr.isEmpty()) {
                    i++;
                }
                if (i > 0) {
                    j++;
                }
            }
        }

        return j;

    }

    // 알람에서 사용되는 메소드 - 화면이 꺼져있을때 켜지게 하는
    static void releaseCpuLock() {
        Log.d(TAG,"Releasing cpu wake lock");
        if (myWakeLock!= null) {
            myWakeLock.release();
            myWakeLock= null;
        }
    }
    // exp : 알람시 ringtonePanager를 이용하며 uri를 설정한다. 알람 -> 없을경우에 noti -> 없을경우에 ringtone순서로
    private Uri getAlarmUri() {
        Intent i = getIntent();
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    // MediaPlayer객체를 생성 및 설정 알람 울리도록 함
    public static void playSound(Context mcontext, String sound) {
        mMediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = null;
            if(sound.equals("alarm")){
                afd = mcontext.getAssets().openFd("bell.mp3"); // cf : 파일을 여는 부분
            }else{
                afd = mcontext.getAssets().openFd("pray.mp3"); // cf : 파일을 여는 부분
            }

            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            // 이때 setOnseekCompleteListener를 이용하여 알람이 한번만 울리고 멈추게 해 주었다.
            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                public void onSeekComplete(MediaPlayer mMediaPlayer) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
            });

        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }
}
