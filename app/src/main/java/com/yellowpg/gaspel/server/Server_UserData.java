package com.yellowpg.gaspel.server;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.DB.UsersDBSqlData;
import com.yellowpg.gaspel.FirstActivity;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.data.UserData;
import com.yellowpg.gaspel.data.Weekend;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;
import com.yellowpg.gaspel.etc.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Server_UserData {

    public static void checkLogin(final Context context, final SessionManager session, final ProgressDialog pDialog, final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog.setMessage("Logging in ...");
        if (!pDialog.isShowing())
            pDialog.show();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {
            boolean error;
            @Override
            public void onResponse(String response) {
                //sae  Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    error = jObj.getBoolean("error");

                    if (!error) { // error가 false인 경우에 로그인 성공

                        // Now store the user in SQLite
                        final String uid = jObj.getString("uid");

                        session.setLogin(true, uid); // sharedpreference에서 로그인 트루로

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String user_id = user.getString("user_id");
                        String email = user.getString("email");
                        String christ_name = user.getString("christ_name");
                        String age = user.getString("age");
                        String region = user.getString("region");
                        String cathedral = user.getString("cathedral");
                        String created_at = user.getString("created_at");


                        ArrayList<UserData> userdata = new ArrayList<UserData>();
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.selectUserData(UsersDBSqlData.SQL_DB_SELECT_DATA, uid, userdata);
                        dbMgr.dbClose();
                        if(userdata.isEmpty()) {
                            // userdatabase에 user 정보 삽입
                            UserData cData = new UserData(uid, user_id, email, name, christ_name, age, region, cathedral, created_at);
                            dbMgr.dbOpen();
                            dbMgr.insertUserData(UsersDBSqlData.SQL_DB_INSERT_DATA, cData);
                            dbMgr.dbClose();
                            Log.d("saea", uid+"add user into DB");
                        }

                        // 서버 DB에 있는 정보를 모두 가져온다
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Log.i("saea", "Starting Upload...");

                                ArrayList<Comment> comments = new  ArrayList<Comment>();
                                Server_CommentData.selectAll(context, uid, comments);

                                ArrayList<Lectio> lectios = new  ArrayList<Lectio>();
                                Server_LectioData.selectAll(context, uid, lectios);

                                ArrayList<Weekend> weekends = new  ArrayList<Weekend>();
                                Server_WeekendData.selectAll(context, uid, weekends);

                            }
                        });
                        t.start();
                        try {
                            t.join();
                            if (pDialog.isShowing())
                                pDialog.dismiss();

                            Intent intent = new Intent(context, FirstActivity.class);
                            context.startActivity(intent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }



                    } else {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                                errorMsg, Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(context, "인터넷을 연결해주세요", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Login Error: " + error.getMessage());
                Toast.makeText(context,
                        "인터넷을 연결해주세요", Toast.LENGTH_LONG).show();

                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "login");
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }



    // 회원가입 등록하는 코드
    public static void registerUser(final Context context, final SessionManager session, final ProgressDialog pDialog, final String id, final String email,  final String password, final String name,
                                   final String christ_name,  final String age, final String region, final String cathedral) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("saea", "Register Response: " + response.toString());
                if (pDialog.isShowing())
                    pDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String user_id = user.getString("user_id");
                        String email = user.getString("email");
                        String christ_name = user.getString("christ_name");
                        String age = user.getString("age");
                        String region = user.getString("region");
                        String cathedral = user.getString("cathedral");
                        String created_at = user.getString("created_at"); // 보내는 값이 json형식의 response 이다


                        Log.d("saea", uid);
                        UserData cData = new UserData(uid, user_id, email, name,  christ_name, age, region, cathedral, created_at);
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.insertUserData(UsersDBSqlData.SQL_DB_INSERT_DATA, cData);
                        dbMgr.dbClose();

                        session.setLogin(true, uid); // sharedpreference에서 로그인 트루로

                        SharedPreferences setPreference = context.getSharedPreferences("Setting", MODE_PRIVATE);
                        SharedPreferences.Editor setEditPreference = setPreference.edit();
                        Log.d("saea", name+christ_name);
                        Toast.makeText(context, "성공적으로 가입되었습니다.", Toast.LENGTH_LONG).show();

                        Intent i = new Intent(context,
                                FirstActivity.class);
                        i.putExtra("uid", uid);
                        context.startActivity(i);
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,
                            "인터넷을 연결해주세요", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());
                Toast.makeText(context,
                        "인터넷을 연결해주세요", Toast.LENGTH_LONG).show();
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "register");
                params.put("name", name);
                params.put("id", id);
                params.put("christ_name", christ_name);
                params.put("cathedral", cathedral);
                params.put("age", age);
                params.put("region", region);
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public static void updateUser(final Context context, final ProgressDialog pDialog, final String uid, final String email, final String name,
                                  final String christ_name, final String age, final String region, final String cathedral) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

           pDialog.setMessage("Updating ...");


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_USERUPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("saea", "Update Response: " + response.toString());
                if (pDialog.isShowing())
                    pDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String user_id = user.getString("user_id");
                        String email = user.getString("email");
                        String christ_name = user.getString("christ_name");
                        String cathedral = user.getString("cathedral");
                        String created_at = user.getString("created_at");
                        String age = user.getString("age");
                        String region = user.getString("region");

                        Log.d("saea", uid);

                        Log.d("saea", christ_name + cathedral);
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.updateUserData(UsersDBSqlData.SQL_DB_UPDATE_DATA, new String[]{email,name, christ_name, age, region, cathedral, uid});
                        dbMgr.dbClose();
                        Toast.makeText(context, "프로필이 수정되었습니다.", Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                               "실패하였습니다", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Update Error: " + error.getMessage());
                Toast.makeText(context,
                        "실패하였습니다", Toast.LENGTH_LONG).show();
                //    hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "update");
                params.put("uid", uid);
                params.put("name", name);
                params.put("christ_name", christ_name);
                params.put("age", age);
                params.put("region", region);
                params.put("cathedral", cathedral);
                params.put("email", email);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
