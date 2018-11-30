package com.yellowpg.gaspel;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
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
import com.yellowpg.gaspel.DB.CommentInfoHelper;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.DB.LectioInfoHelper;
import com.yellowpg.gaspel.DB.UserDBSqlData;
import com.yellowpg.gaspel.DB.WeekendInfoHelper;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private Button btnLogout;
    private SessionManager session;


    Context mContext;
    EditText name_et, userId_et, email_et, christ_name_et, cathedral_et;
    String uid;
    String name, user_id, email, christ_name, cathedral, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //actionbar setting
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar_mypage);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2980b9")));
        actionbar.setElevation(0);

        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

        name_et = (EditText) findViewById(R.id.name);
        userId_et = (EditText) findViewById(R.id.userid);
        email_et = (EditText) findViewById(R.id.email);
        christ_name_et = (EditText) findViewById(R.id.christ_name);
        cathedral_et =  (EditText) findViewById(R.id.cathedral);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        email_et.setFocusable(false);
        email_et.setClickable(false);
        userId_et.setFocusable(false);
        userId_et.setClickable(false);

        int color = Color.parseColor("#6E6E6E");
        name_et.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        userId_et.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        email_et.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }
        uid = session.getUid();
        Log.d("saea", uid);


        ArrayList<UserData> userdata = new ArrayList<UserData>();
        DBManager dbMgr = new DBManager(ProfileActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectUserData(UserDBSqlData.SQL_DB_SELECT_DATA, uid, userdata);
        dbMgr.dbClose();
        if(userdata.isEmpty()){
            Log.d("saea", "getuser and insertdatato Server DB");
            // 서버에서 user 정보를 가져와서 user database에 삽입
            getUser(ProfileActivity.this, uid);

            // 모바일 데이터가 있는 경우, 그리고 uid값이 없는 경우
            CommentInfoHelper commentInfoHelper = new CommentInfoHelper(ProfileActivity.this);
            SQLiteDatabase db;
            ContentValues values;

            try{
                db = commentInfoHelper.getReadableDatabase();
                String query = "SELECT comment_con, date, sentence FROM comment";
                Cursor cursor = db.rawQuery(query, null);

                while(cursor.moveToNext()){
                    String comment_con = cursor.getString(0);
                    String date = cursor.getString(1);
                    String sentence = cursor.getString(2);
                    if(uid != null){
                        Log.d("saea", uid+date+sentence+comment_con);
                        Server_CommentData.insertComment(ProfileActivity.this, uid, date, sentence, comment_con);
                    }
                }
                cursor.close();
                commentInfoHelper.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            // 렉시오디비나
            LectioInfoHelper lectioInfoHelper = new LectioInfoHelper(ProfileActivity.this);
            SQLiteDatabase db2;

            try{
                String bg1 = null;
                String bg2 = null;
                String bg3 = null;
                String sum1 = null;
                String sum2 = null;
                String js1 = null;
                String js2 = null;

                String date = null;
                String onesentence = null;
                db2 = lectioInfoHelper.getReadableDatabase();
                String query = "SELECT bg1, bg2, bg3, sum1, sum2, js1, js2, date, onesentence FROM lectio";
                Cursor cursor = db2.rawQuery(query, null);

                while(cursor.moveToNext()){
                    bg1 = cursor.getString(0);
                    bg2 = cursor.getString(1);
                    bg3 = cursor.getString(2);
                    sum1 = cursor.getString(3);
                    sum2 = cursor.getString(4);
                    js1 = cursor.getString(5);
                    js2 = cursor.getString(6);
                    date = cursor.getString(7);
                    onesentence = cursor.getString(8);
                    if(uid != null){
                        Log.d("saea", uid+date+onesentence+bg1);
                        Lectio lectio = new Lectio(date, onesentence, bg1, bg2, bg3, sum1, sum2, js1, js2);
                        Server_LectioData.insertLectio(ProfileActivity.this, uid, lectio);
                    }
                }

                cursor.close();
                lectioInfoHelper.close();
            }
            catch(Exception e){

            }
            // weekend 데이터
            WeekendInfoHelper weekendInfoHelper = new WeekendInfoHelper(ProfileActivity.this);
            ContentValues values3;
            SQLiteDatabase db3;

            try{
                db3 = weekendInfoHelper.getReadableDatabase();
                String query = "SELECT date, mysentence, mythought FROM weekend";
                Cursor cursor = db3.rawQuery(query, null);
                String weekend_str = null;
                while(cursor.moveToNext()) {
                    weekend_str = cursor.getString(0);
                    String date = cursor.getString(0);
                    String mysentence = cursor.getString(1);
                    String mythought = cursor.getString(2);
                    if(uid != null){
                        if(mythought == null){
                            mythought = "";
                        }
                        Log.d("saea", uid+date+mysentence+mythought);
                        Server_WeekendData.insertWeekend(ProfileActivity.this, uid, date, mysentence, mythought);
                    }
                }

                cursor.close();
                weekendInfoHelper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            UserData udata = userdata.get(0);
            name = udata.getName();
            user_id = udata.getUserId();
            email = udata.getEmail();
            christ_name = udata.getChristName();
            cathedral = udata.getCathedral();
            name_et.setText(udata.getName(), TextView.BufferType.EDITABLE);
            userId_et.setText(udata.getUserId());
            email_et.setText(udata.getEmail(), TextView.BufferType.EDITABLE);
            christ_name_et.setText(udata.getChristName(), TextView.BufferType.EDITABLE);
            cathedral_et.setText(udata.getCathedral(), TextView.BufferType.EDITABLE);

        }

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });


        // 화면 클릭시 soft keyboard hide
        findViewById(R.id.ll).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
                Log.d("saea", name+email+user_id+" "+uid);
                Server_UserData.updateUser(ProfileActivity.this, uid, email, name, christ_name, cathedral);
                return true;
            default:
                Intent i = new Intent(ProfileActivity.this, FirstActivity.class);
                startActivity(i);
                return true;
        }
    }


    // 로그아웃할때
    private void logoutUser() {
        session.setLogin(false, "");

        SQLiteDatabase db = null;
        CommentInfoHelper commentInfoHelper = new CommentInfoHelper(this);
        try {
            db = commentInfoHelper.getReadableDatabase();
            db.delete("comment", null, null);
            db.close();
        }catch(Exception e){

        }

        LectioInfoHelper lectioInfoHelper = new LectioInfoHelper(this);
        try {
            db = lectioInfoHelper.getReadableDatabase();
            db.delete("lectio", null, null);
            db.close();
        }catch(Exception e){

        }

        WeekendInfoHelper weekendInfoHelper = new WeekendInfoHelper(this);
        try {
            db = weekendInfoHelper.getReadableDatabase();
            db.delete("weekend", null, null);
            db.close();
        }catch(Exception e){

        }
        // Launching the login activity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                        cathedral = user.getString("cathedral");
                        String created_at = user.getString("created_at");

                        // userdatabase에 user 정보 삽입
                        UserData cData = new UserData(uid, user_id, email, name,  christ_name, cathedral, created_at);
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.insertUserData(UserDBSqlData.SQL_DB_INSERT_DATA, cData);
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