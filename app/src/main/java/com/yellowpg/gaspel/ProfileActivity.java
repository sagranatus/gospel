package com.yellowpg.gaspel;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaCas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.yellowpg.gaspel.etc.SessionManager;
import com.yellowpg.gaspel.server.Server_CommentData;
import com.yellowpg.gaspel.server.Server_LectioData;
import com.yellowpg.gaspel.server.Server_UserData;
import com.yellowpg.gaspel.server.Server_WeekendData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private Button btnLogout;
    private SessionManager session;


    Context mContext;
    EditText name_et, userId_et, email_et, christ_name_et, cathedral_et, age_et, region_et;
    static String uid;
    String name, user_id, email, christ_name, cathedral, password;
    String age, region;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //actionbar setting
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar_mypage);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
        actionbar.setElevation(0);

        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
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

        int color = Color.parseColor("#FFFFFF");
        name_et.setEnabled(false);
        userId_et.setEnabled(false);
        email_et.setEnabled(false);

        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser(session, ProfileActivity.this);
        }
        uid = session.getUid();
        Log.d("saea", uid);


        ArrayList<UserData> userdata = new ArrayList<UserData>();
        DBManager dbMgr = new DBManager(ProfileActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectUserData(UsersDBSqlData.SQL_DB_SELECT_DATA, uid, userdata);
        dbMgr.dbClose();
        if(userdata.isEmpty()) {
            Log.d("saea", "getuser and insertdatato Server DB");
            // 서버에서 user 정보를 가져와서 user database에 삽입
           // getUser(ProfileActivity.this, uid);


        }else{
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
        }

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser(session,ProfileActivity.this);
            }
        });


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

                name = name_et.getText().toString();
                email = email_et.getText().toString();
                user_id = userId_et.getText().toString();
                christ_name = christ_name_et.getText().toString();
                cathedral = cathedral_et.getText().toString();
                age = age_et.getText().toString();
                region = region_et.getText().toString();
                Log.d("saea", name+email+user_id+" "+uid);
                Server_UserData.updateUser(ProfileActivity.this, uid, email, name, christ_name, age, region, cathedral);
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

        //sharedpreference 삭제
        Calendar c1 = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String date_val2 = sdf2.format(c1.getTime());
        SharedPreferences pref = context.getSharedPreferences("todaysentence", 0);

        SharedPreferences.Editor editor = pref.edit();
        editor.remove(date_val2);
        c1.add(Calendar.DATE, -1);

        String date_val2_yesterday = sdf2.format(c1.getTime());
        editor.remove(date_val2_yesterday);

        // commit changes
        editor.commit();


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

        // Launching the login activity
        Intent intent = new Intent(context, PreviousActivity.class);
        context.startActivity(intent);
    }



    public void getUser(final Context context, final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        final ArrayList<UserData>  userData =  new ArrayList<UserData>();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_USERUPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("saea", "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        name = user.getString("name");
                        user_id = user.getString("user_id");
                        email = user.getString("email");
                        christ_name = user.getString("christ_name");
                        age = user.getString("age");
                        region = user.getString("region");
                        cathedral = user.getString("cathedral");
                        String created_at = user.getString("created_at");

                        // userdatabase에 user 정보 삽입
                        UserData cData = new UserData(uid, user_id, email, name, christ_name, age, region, cathedral, created_at);
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.insertUserData(UsersDBSqlData.SQL_DB_INSERT_DATA, cData);
                        dbMgr.dbClose();
                        Log.d("saea", uid+"add user into DB");

                        // Displaying the user details on the screen
                        name_et.setText(name, TextView.BufferType.EDITABLE);
                        userId_et.setText(user_id);
                        email_et.setText(email, TextView.BufferType.EDITABLE);
                        christ_name_et.setText(christ_name, TextView.BufferType.EDITABLE);
                        cathedral_et.setText(cathedral, TextView.BufferType.EDITABLE);
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                   //     Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "GetUser Error: " + error.getMessage());
             //   Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "get");
                params.put("uid", uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

}