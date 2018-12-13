package com.yellowpg.gaspel;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.DB.UsersDBSqlData;
import com.yellowpg.gaspel.data.UserData;
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.server.Server_UserData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    private Button btnLogout;
    private SessionManager session;

    EditText name_et, userId_et, email_et, christ_name_et, cathedral_et, age_et, region_et;
    static String uid;
    String name, user_id, email, christ_name, cathedral;
    String age, region;
    private ProgressDialog pDialog;
    NetworkInfo mobile;
    NetworkInfo wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // session manager
        session = new SessionManager(getApplicationContext());
        uid = session.getUid();
        Log.d("saea", uid);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // exp : 인터넷연결 확인
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //actionbar setting
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar_mypage);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
        actionbar.setElevation(0);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

        name_et = (EditText) findViewById(R.id.name);
        userId_et = (EditText) findViewById(R.id.userid);
        email_et = (EditText) findViewById(R.id.email);
        christ_name_et = (EditText) findViewById(R.id.christ_name);
        cathedral_et =  (EditText) findViewById(R.id.cathedral);
        age_et = (EditText) findViewById(R.id.age);
        region_et = (EditText) findViewById(R.id.region);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        email_et.setFocusable(false);
        email_et.setClickable(false);
        userId_et.setFocusable(false);
        userId_et.setClickable(false);

        name_et.setBackgroundResource(R.drawable.edit_bg);
        userId_et.setBackgroundResource(R.drawable.edit_bg);
        email_et.setBackgroundResource(R.drawable.edit_bg);
        christ_name_et.setBackgroundResource(R.drawable.edit_bg);
        cathedral_et.setBackgroundResource(R.drawable.edit_bg);
        age_et.setBackgroundResource(R.drawable.edit_bg);
        region_et.setBackgroundResource(R.drawable.edit_bg);

        name_et.setEnabled(false);
        userId_et.setEnabled(false);
        email_et.setEnabled(false);


        // 화면 클릭시 soft keyboard hide
        findViewById(R.id.ll).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });

        ArrayList<UserData> userdata = new ArrayList<UserData>();
        DBManager dbMgr = new DBManager(ProfileActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectUserData(UsersDBSqlData.SQL_DB_SELECT_DATA, uid, userdata);
        dbMgr.dbClose();

        UserData udata = userdata.get(0);
        name = udata.getName();
        user_id = udata.getUserId();
        email = udata.getEmail();
        christ_name = udata.getChristName();
        age = udata.getAge();
        region = udata.getRegion();
        cathedral = udata.getCathedral();
        name_et.setText(udata.getName(), TextView.BufferType.EDITABLE);
        userId_et.setText(udata.getUserId());
        email_et.setText(udata.getEmail(), TextView.BufferType.EDITABLE);
        christ_name_et.setText(udata.getChristName(), TextView.BufferType.EDITABLE);
        cathedral_et.setText(udata.getCathedral(), TextView.BufferType.EDITABLE);
        age_et.setText(udata.getAge(), TextView.BufferType.EDITABLE);
        region_et.setText(udata.getRegion(), TextView.BufferType.EDITABLE);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser(session,ProfileActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu_profile, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_one:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                if ((wifi.isConnected() || mobile.isConnected())) {
                    name = name_et.getText().toString();
                    email = email_et.getText().toString();
                    user_id = userId_et.getText().toString();
                    christ_name = christ_name_et.getText().toString();
                    cathedral = cathedral_et.getText().toString();
                    age = age_et.getText().toString();
                    region = region_et.getText().toString();
                    Log.d("saea", name + email + user_id + " " + uid);
                    Server_UserData.updateUser(ProfileActivity.this, pDialog, uid, email, name, christ_name, age, region, cathedral);
                }else{
                    Toast.makeText(this, "인터넷을 연결해주세요", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                Intent i = new Intent(ProfileActivity.this, FirstActivity.class);
                startActivity(i);
                return true;
        }
    }


    // 로그아웃할때
    public static void logoutUser(SessionManager session, Context context) {
        session.setLogin(false, "");

        //sharedpreference 삭제 - 오늘, 어제 값 삭제
        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String date_val2 = sdf2.format(c1.getTime());
        SharedPreferences pref = context.getSharedPreferences("todaysentence", 0);

        SharedPreferences.Editor editor = pref.edit();
        editor.remove(date_val2);
        c1.add(Calendar.DATE, -1);

        String date_val2_yesterday = sdf2.format(c1.getTime());
        editor.remove(date_val2_yesterday);

        editor.commit();

        /*
        DBManager dbMgr = new DBManager(context);
        dbMgr.dbOpen();
        dbMgr.deleteCommentData(CommentDBSqlData.SQL_DB_DELETE_DATA, uid);
        dbMgr.dbClose();

        dbMgr.dbOpen();
        dbMgr.deleteLectioData(LectioDBSqlData.SQL_DB_DELETE_DATA, uid);
        dbMgr.dbClose();


        dbMgr.dbOpen();
        dbMgr.deleteWeekendData(WeekendDBSqlData.SQL_DB_DELETE_DATA, uid);
        dbMgr.dbClose();
    */

        // 메인으로 이동
        Intent intent = new Intent(context, PreviousActivity.class);
        context.startActivity(intent);
    }


}