package com.yellowpg.gaspel;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.yellowpg.gaspel.DB.UserDBSqlData;
import com.yellowpg.gaspel.DB.UsersDBSqlData;
import com.yellowpg.gaspel.DB.WeekendDBSqlData;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.data.UserData;
import com.yellowpg.gaspel.data.Weekend;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.BottomNavigationViewHelper;
import com.yellowpg.gaspel.etc.DownloadImageTask;
import com.yellowpg.gaspel.etc.DownloadImageTask_bitmap;
import com.yellowpg.gaspel.etc.ListSelectorDialog;
import com.yellowpg.gaspel.etc.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hirondelle.date4j.DateTime;

import static com.yellowpg.gaspel.MainActivity.playSound;
import static com.yellowpg.gaspel.MainActivity.releaseCpuLock;


public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
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
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월");
    String date_val = sdf.format(c1.getTime());
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

    static String day;

    String name, user_id, email, christ_name, cathedral, password;
    String age, region;

    private static PowerManager.WakeLock myWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        c1.add(Calendar.MONTH, 1);

        date_today = sdf_today.format(c1.getTime());
        year_val = year.format(c1.getTime());
        date_val1 = sdf1.format(c1.getTime());
        month_val = month.format(c1.getTime());
        date_val2 = sdf2.format(c1.getTime());

        //preference에 저장한다.
        pref = FirstActivity.this.getSharedPreferences("todaysentence", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(date_val2 );
        editor.commit();
        */

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

        todaysentence = (TextView) findViewById(R.id.bt_today);
        date = (TextView) findViewById(R.id.date);


       // c1.add(Calendar.DATE, 1);
        date_today = sdf_today.format(c1.getTime());
        date_today = date_today +""+getDay()+"요일";
        Log.d("saea", "today"+date_today);
        String typedDate = date_val1;
        date.setText(typedDate);
        circle1 = (Button) findViewById(R.id.circle1);
        circle2 = (Button) findViewById(R.id.circle2);
        circle3 = (Button) findViewById(R.id.circle3);

        pref = FirstActivity.this.getSharedPreferences("todaysentence", 0);
        String saved_todaysentence = pref.getString(date_val2, "");        // 오늘 구절은 sharedpreference에 저장해둔다.

        if (saved_todaysentence != "") {
            todaysentence.setText("\" " + saved_todaysentence + " \"");
            Log.d("saea", "saved already");
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
            // 인터넷연결된 상태에서만 데이터 가져오기
            if ((wifi.isConnected() || mobile.isConnected())) {
                Log.d("saea", "get data");
                getGaspel(date_val2);


                //    new DownloadImageTask(img3).execute("https://sssagranatus.cafe24.com/resource/firstimg.png");
                String urldisplay ="https://sssagranatus.cafe24.com/resource/firstimg.png";
                Log.d("saea", "urld"+urldisplay);
                Bitmap mbitmap = null;
                new DownloadImageTask_bitmap(FirstActivity.this, uid, "firstimage", mbitmap).execute(urldisplay);
                //   img3.setImageBitmap(mbitmap);
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

        int todaynum = getData("today", "", "");
        Log.d("saea", todaynum+"today");
        circle1.setText(Integer.toString(todaynum));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if(date_today.contains("일요일")){
            cal.add(Calendar.DATE, -7);
        }
        SimpleDateFormat sdf_monday = new SimpleDateFormat("dd");
        String date_monday = sdf_monday.format(cal.getTime());

        int weeknum = getData("weekend", month_val, date_monday);
        Log.d("saea", weeknum+"weeknum");
        circle2.setText(Integer.toString(weeknum));

        int monthnum = getData("month", month_val, "");
        Log.d("saea", monthnum+"monthnum");
        circle3.setText(Integer.toString(monthnum));


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
                            Intent i = new Intent(FirstActivity.this, LoginActivity.class);
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
                                todaysentence.setText("\" " + gaspel_sentence + " \"");

                                //preference에 저장한다.
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(date, gaspel_sentence);
                                // 전날 preference값을 지운다.
                                Calendar c1 = Calendar.getInstance();
                                c1.add(Calendar.DATE, -1);
                                date_val2_yesterday = sdf2.format(c1.getTime());
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


    // 요일 얻어오기
    public String getDay() {
        int dayNum = c1.get(Calendar.DAY_OF_WEEK);
        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

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
        }

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
        //catch에 안 붙잡히면 테이블이 있다는 의미이므로 true, 잡히면 테이블이 없으므로 false를 반환
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
            String today = date_today;

            //comment
            ArrayList<Comment> comments_arr = new ArrayList<Comment>();
            String comment_str = null;
            DBManager dbMgr = new DBManager(FirstActivity.this);
            dbMgr.dbOpen();
            dbMgr.selectCommentAllData("SELECT * FROM comment WHERE uid = ? AND date LIKE '%" + date_today + "%'", uid, comments_arr);
            dbMgr.dbClose();

            if (!comments_arr.isEmpty()) {
                i++;
            }

            ArrayList<Lectio> lectios_arr = new ArrayList<Lectio>();
            String bg1_str = null;
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
                int length = (int)(Math.log10(k)+1);
                String day = String.format("%02d", k);

                String thisdate = year_val+"년 "+month+"월 "+day+"일";
                Log.d("saea",thisdate);
                ArrayList<Comment> comments_arr = new ArrayList<Comment>();
                String comment_str = null;
                DBManager dbMgr = new DBManager(FirstActivity.this);
                dbMgr.dbOpen();
                dbMgr.selectCommentAllData("SELECT * FROM comment WHERE uid = ? AND date LIKE '%" + thisdate + "%'", uid, comments_arr);
                dbMgr.dbClose();

                if (!comments_arr.isEmpty()) {
                    i++;
                }

                ArrayList<Lectio> lectios_arr = new ArrayList<Lectio>();
                String bg1_str = null;
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
                String comment_str = null;
                DBManager dbMgr = new DBManager(FirstActivity.this);
                dbMgr.dbOpen();
                dbMgr.selectCommentAllData("SELECT * FROM comment WHERE uid = ? AND date LIKE '%" + thisdate + "%'", uid, comments_arr);
                dbMgr.dbClose();

                if (!comments_arr.isEmpty()) {
                    i++;
                }

                ArrayList<Lectio> lectios_arr = new ArrayList<Lectio>();
                String bg1_str = null;
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
                int length = (int)(Math.log10(k)+1);
                String day = String.format("%02d", k);

                String thisdate = year_val+"년 "+month+"월 "+day+"일";
                Log.d("saea",thisdate);
                ArrayList<Comment> comments_arr = new ArrayList<Comment>();
                String comment_str = null;
                DBManager dbMgr = new DBManager(FirstActivity.this);
                dbMgr.dbOpen();
                dbMgr.selectCommentAllData("SELECT * FROM comment WHERE uid = ? AND date LIKE '%" + thisdate + "%'", uid, comments_arr);
                dbMgr.dbClose();

                if (!comments_arr.isEmpty()) {
                    i++;
                }

                ArrayList<Lectio> lectios_arr = new ArrayList<Lectio>();
                String bg1_str = null;
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
}
