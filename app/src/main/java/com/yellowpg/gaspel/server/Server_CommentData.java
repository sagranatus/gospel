package com.yellowpg.gaspel.server;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.DB.CommentDBSqlData;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server_CommentData {

    public static void insertComment(final Context context, final String uid, final String aDate, final String aOnesentence, final String aComment) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("saea", "Starting Upload...");
                String comment = aComment;
                String oneSentence = aOnesentence;
                String date = aDate;
                insertComment_Connect(context, uid, date, oneSentence, comment);


            }
        });
        t.start();

    }


    public static void selectAll(final Context context, final String uid, final ArrayList<Comment> mAppItem) {
        selectAll_Connect(context, uid, mAppItem);

    }


    public static void insertComment_Connect(Context context, final String uid, final String date, final String oneSentence, final String comment) {
        // Tag used to cancel the request
        String tag_string_req = "req_comment";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_COMMENTDATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"saea");
                    if (!error) {
                        String uid = jObj.getString("uid");

                        JSONObject comments = jObj.getJSONObject("comment");
                        String onesentence = comments.getString("onesentence");
                        String date = comments.getString("date");
                        Log.d("saea", "result:"+ uid + date + onesentence);


                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "InsertComment Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "insert");
                params.put("uid", uid);
                params.put("date", date);
                params.put("onesentence", oneSentence);
                params.put("comment", comment);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    public static void selectAll_Connect(final Context context, final String uid, final ArrayList<Comment> mAppItem) {
        // Tag used to cancel the request
        String tag_string_req = "req_comments";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_COMMENTDATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                ArrayList<Comment> commentItems = new ArrayList<Comment>();
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"saea");


                    if (!error) {
                        if(jObj.has("stack")){
                            JSONArray stack = jObj.getJSONArray("stack");
                            for(int i=0; i<stack.length(); i++) {
                                JSONArray eachstack = stack.getJSONArray(i);
                                String[] arr=new String[eachstack.length()];
                                for(int j=0; j<arr.length; j++) {
                                    arr[j]=eachstack.optString(j);
                                }
                                Comment comment = new Comment(uid, arr[1], arr[2], arr[3]);
                                Log.d("saea", arr[1]+arr[2]+arr[3]);


                                ArrayList<Comment> comments = new ArrayList<Comment>();
                                String comment_str = null;
                                DBManager dbMgr = new DBManager(context);
                                dbMgr.dbOpen();
                                dbMgr.selectCommentData(CommentDBSqlData.SQL_DB_SELECT_DATA, uid, arr[1] , comments);
                                dbMgr.dbClose();

                                if(!comments.isEmpty()){
                                    comment_str = comments.get(0).getComment();
                                }else{
                                }

                                if(comment_str!=null){
                                   Log.d("saea", "기존 값이 있음");
                                }else{

                                    Log.d("saea", "기존 값이 없음");

                                    Comment commentData = new Comment(uid, arr[1],arr[2],arr[3]);
                                    dbMgr.dbOpen();
                                    dbMgr.insertCommentData(CommentDBSqlData.SQL_DB_INSERT_DATA, commentData);
                                    dbMgr.dbClose();
                                }


                                commentItems.add(comment);
                                Log.d("saea", "comments size:"+String.valueOf(commentItems.size()));
                            }

                            Log.d("saea", "resultfirst");

                        }else{
                            Log.d("saea", "noval");

                        }

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "GetComments Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Log.e("saea", "insert: " + uid);
                params.put("status", "selectall");
                params.put("uid", uid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public static void updateComment(final Context context, final String uid, final String aDate, final String aOnesentence, final String aComment) {
        updateComment_Connect(context, uid, aDate, aOnesentence, aComment);
    }


    public static void updateComment_Connect(final Context context, final String uid, final String date, final String oneSentence, final String comment) {
        // Tag used to cancel the request
        String tag_string_req = "req_comment";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_COMMENTDATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    Log.d("saea", error+"saea");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject comments = jObj.getJSONObject("comment");
                        String comment = comments.getString("comment");

                        Log.d("saea", "result:"+ uid + comment );


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("saea", "UpdateComment Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Log.d("saea", "update: "+oneSentence);
                params.put("status", "update");
                params.put("uid", uid);
                params.put("date", date);
                params.put("onesentence", oneSentence);
                params.put("comment", comment);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
