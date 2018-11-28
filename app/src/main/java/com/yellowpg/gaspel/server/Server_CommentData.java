package com.yellowpg.gaspel.server;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.DB.CommentInfoHelper;
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

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("saea", "Starting Upload...");

                selectAll_Connect(context, uid, mAppItem);

            }
        });
        t.start();
    }


    public static void insertComment_Connect(Context context, final String uid, final String date, final String oneSentence, final String comment) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


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
                        String comment = comments.getString("comment");
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
                                Comment comment = new Comment(arr[3], arr[1], arr[2]);
                                Log.d("saea", arr[3]+arr[1]+arr[2]);

                                // 기존에 값이 있는지 확인하고 없는 경우에 모바일 DB에 삽입
                                SQLiteDatabase db;
                                    CommentInfoHelper commentInfoHelper = new CommentInfoHelper(context);
                                    ContentValues values;

                                    try{
                                        String comment_str = null;
                                        db = commentInfoHelper.getReadableDatabase();
                                        String[] columns = {"comment_con", "date", "sentence"};
                                        String whereClause = "date = ?";
                                        String[] whereArgs = new String[] {
                                                arr[1]
                                        };
                                        Cursor cursor = db.query("comment", columns,  whereClause, whereArgs, null, null, null);

                                        while(cursor.moveToNext()){
                                            comment_str = cursor.getString(0);
                                        }
                                        if(comment_str!=null){
                                           Log.d("saea", "기존 값이 있음");
                                        }else{
                                            Log.d("saea", "기존 값이 없음");
                                           // db=commentInfoHelper.getWritableDatabase();
                                            values = new ContentValues();
                                            values.put("comment_con", arr[3]);
                                            values.put("date", arr[1]);
                                            values.put("sentence", arr[2]);
                                            db.insert("comment", null, values);

                                        }
                                        commentInfoHelper.close();

                                    }catch(Exception e){

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
        Log.i("saea", "Starting Upload...");
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
