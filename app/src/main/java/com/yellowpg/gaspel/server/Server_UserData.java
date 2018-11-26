package com.yellowpg.gaspel.server;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.DB.UserDBSqlData;
import com.yellowpg.gaspel.MainActivity;
import com.yellowpg.gaspel.ProfileActivity;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.UserData;
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
                AppConfig.URL_LOGIN, new Response.Listener<String>() { // URL_LOGIN : "http://192.168.116.1/android_login_api/login.php";
            boolean error;
            @Override
            public void onResponse(String response) {
                //sae  Log.d(TAG, "Login Response: " + response.toString());
                if (pDialog.isShowing())
                    pDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) { // error가 false인 경우에 로그인 성공
                        // user successfully logged in
                        // Create login session

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        session.setLogin(true, uid); // exp : sharedpreference에서 로그인 트루로

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String user_id = user.getString("user_id");
                        String email = user.getString("email");

                        String created_at = user
                                .getString("created_at"); // 보내는 값이 json형식의 response 이다
                        // Launch main activity
                        ArrayList<Comment> comments = new  ArrayList<Comment>();
                        Server_CommentData.selectAll(context, uid, comments);
                        Intent intent = new Intent(context, ProfileActivity.class);
                        context.startActivity(intent);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                                errorMsg, Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Login Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage()+"3333", Toast.LENGTH_LONG).show();

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
                                   final String christ_name, final String cathedral) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        if (!pDialog.isShowing())
            pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_REGISTER, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

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
                        String cathedral = user.getString("cathedral");
                        String created_at = user.getString("created_at"); // 보내는 값이 json형식의 response 이다

                        Log.d("saea", uid);
                        UserData cData = new UserData(uid, user_id, email, name,  christ_name, cathedral, created_at);
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.insertUserData(UserDBSqlData.SQL_DB_INSERT_DATA, cData);
                        dbMgr.dbClose();

                        session.setLogin(true, uid); // exp : sharedpreference에서 로그인 트루로

                        SharedPreferences setPreference = context.getSharedPreferences("Setting", MODE_PRIVATE);
                        SharedPreferences.Editor setEditPreference = setPreference.edit();
                        Log.d("saea", name+christ_name);
                        Toast.makeText(context, "성공적으로 가입되었습니다.", Toast.LENGTH_LONG).show();

                        ArrayList<Comment> comments = new  ArrayList<Comment>();
                        Server_CommentData.selectAll(context, uid, comments);

                        Intent i = new Intent(context,
                                MainActivity.class);
                        String[] info = {name, email, christ_name, user_id};
                        i.putExtra("uid", uid);
                        context.startActivity(i);
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                                errorMsg+"1111", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage()+"2222", Toast.LENGTH_LONG).show();
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
                params.put("email", email);
                params.put("password", password);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public static void updateUser(final Context context, final String uid, final String email, final String name,
                                  final String christ_name, final String cathedral) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        //   pDialog.setMessage("Registering ...");
        //  showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_USERUPDATE, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", "Update Response: " + response.toString());
                //  hideDialog();

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
                        String created_at = user.getString("created_at"); // 보내는 값이 json형식의 response 이다

                        Log.d("saea", uid);

                        Log.d("saea", christ_name + cathedral);
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.updateUserData(UserDBSqlData.SQL_DB_UPDATE_DATA, new String[]{email,name, christ_name, cathedral, uid});
                        dbMgr.dbClose();
                        Toast.makeText(context, "성공적으로 수정되었습니다.", Toast.LENGTH_LONG).show();

                     //   Intent i = new Intent(context, MainActivity.class);
                     //   context.startActivity(i);
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                                errorMsg+"1111", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Saea", "Update Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage()+"2222", Toast.LENGTH_LONG).show();
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
                params.put("cathedral", cathedral);
                params.put("email", email);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public static void updateUser_profile(final Context context, final String uid, final String name, final String email,
                                          final String password, final String gender, final String user_id, final String profile_pic) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        //   pDialog.setMessage("Registering ...");
        //  showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_USERUPDATE, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", "Update Response: " + response.toString());
                //  hideDialog();

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
                        String gender = user.getString("gender");
                        String profile_pic = user.getString("profile_pic");
                        DBManager dbMgr = new DBManager(context);
                        dbMgr.dbOpen();
                        dbMgr.updateUserData(UserDBSqlData.SQL_DB_UPDATE_DATA, new String[]{user_id, name, email, gender, profile_pic, uid});
                        dbMgr.dbClose();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                                errorMsg+"1111", Toast.LENGTH_LONG).show();
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
                        error.getMessage()+"2222", Toast.LENGTH_LONG).show();
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
                params.put("user_id", user_id);
                params.put("email", email);
                params.put("gender", gender);
                params.put("profile_pic", profile_pic);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    // 회원가입 등록하는 코드
    public static void getUser(final Context context, final String from, final ImageView imageview, final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        final ArrayList<UserData> userData =  new ArrayList<UserData>();

        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_REGISTER, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                Log.d("saea", "Register Response: " + response.toString());

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
                        String gender = user.getString("gender");
                        String created_at = user.getString("created_at"); // 보내는 값이 json형식의 response 이다
                        String profile_pic = user.getString("profile_pic");
                     //   userData.add(new UserData(uid, user_id, name, email, gender, created_at, profile_pic)); saea
                        Log.d("saea", uid);

                        if(from.equals("MainActivity")){
                         //   new DownloadImageTask(imageview).execute("https://ssagranatus.cafe24.com/files/user"+uid+"/"+profile_pic);
                          //  Glide.with(context).load(profile_pic).into(imageview);
                            SharedPreferences setPreference = context.getSharedPreferences("Setting", MODE_PRIVATE);
                            SharedPreferences.Editor setEditPreference = setPreference.edit();
                            setEditPreference.putString("gender", gender);
                            setEditPreference.commit();
                        }
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(context,
                                errorMsg+"1111", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "Registration Error: " + error.getMessage());
                Toast.makeText(context,
                        error.getMessage()+"2222", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
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
