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
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.DB.WeekendDBSqlData;
import com.yellowpg.gaspel.LectioActivity;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Weekend;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hirondelle.date4j.DateTime;

public class Server_WeekendData {

    public static void insertWeekend(final Context context, final String uid, final String date, final String mysentence, final String mythought) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("saea", "Starting Upload...");
                insertWeekend_Connect(context, uid, date, mysentence, mythought);

            }
        });
        t.start();

    }

    public static void selectAll(final Context context, final String uid, final ArrayList<Weekend> mAppItem) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("saea", "Starting Upload...");

                selectAll_Connect(context, uid, mAppItem);


            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public static void insertWeekend_Connect(Context context, final String uid, final String date, final String mySentence, final String myThought) {
        // Tag used to cancel the request
        String tag_string_req = "req_weekend";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_WEEKENDDATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("saea", " Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject weekend = jObj.getJSONObject("weekend");
                        String mysentence = weekend.getString("mysentence");
                        String mythought = weekend.getString("mythought");

                        Log.d("saea", "result:"+ uid + mysentence + mythought);

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
                Log.e("saea", "InsertWeekend Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "insert");
                params.put("uid", uid);
                params.put("date", date);
                params.put("mysentence", mySentence);
                params.put("mythought", myThought);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    public static void selectAll_Connect(final Context context, final String uid, final ArrayList<Weekend> mAppItem) {
        // Tag used to cancel the request
        String tag_string_req = "req_weekend";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_WEEKENDDATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                ArrayList<Weekend> weekendItems = new ArrayList<Weekend>();
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
                                Weekend weekend = new Weekend(arr[0], arr[1], arr[2], arr[3]);
                                Log.d("saea", arr[1]+arr[2]+arr[3]);

                                ArrayList<Weekend> weekends = new ArrayList<Weekend>();
                                String comment_str = null;
                                DBManager dbMgr = new DBManager(context);
                                dbMgr.dbOpen();
                                dbMgr.selectWeekendData(WeekendDBSqlData.SQL_DB_SELECT_DATA, uid, arr[1], weekends);
                                dbMgr.dbClose();
                                String weekend_str = null;
                                String mythought = "";
                                if(!weekends.isEmpty()){
                                    weekend_str = weekends.get(0).getMySentence();
                                    mythought = weekends.get(0).getMyThought();
                                }

                                if(weekend_str!=null){
                                    Log.d("saea", "기존 값이 있음");
                                }else{
                                    Log.d("saea", "기존 값이 없음");
                                    // db=commentInfoHelper.getWritableDatabase();
                                    Weekend weekendData = new Weekend(arr[0], arr[1], arr[2], arr[3]);
                                    dbMgr.dbOpen();
                                    dbMgr.insertWeekendData(WeekendDBSqlData.SQL_DB_INSERT_DATA, weekendData);
                                    dbMgr.dbClose();
                                    //   commentInfoHelper.close();
                                }

                         /*       WeekendInfoHelper weekendInfoHelper = new WeekendInfoHelper(context);
                                ContentValues values;
                                SQLiteDatabase db3;
                                try{
                                    String weekend_str = null;
                                    db3 = weekendInfoHelper.getReadableDatabase();
                                    String[] columns = {"mysentence", "mythought"};
                                    String whereClause = "date = ?";
                                    String[] whereArgs = new String[] {
                                            arr[1]
                                    };
                                    Cursor cursor = db3.query("weekend", columns,  whereClause, whereArgs, null, null, null);

                                    String mythought = "";
                                    while(cursor.moveToNext()){
                                        weekend_str = cursor.getString(0);
                                        mythought = cursor.getString(1);
                                    }
                                    if(weekend_str!=null){
                                        Log.d("saea", "기존 값이 있음");
                                    }else{
                                        Log.d("saea", "기존 값이 없음");
                                        // db=commentInfoHelper.getWritableDatabase();
                                        values = new ContentValues();
                                        values = new ContentValues();
                                        values.put("date", arr[1]);
                                        values.put("mysentence",  arr[2]);
                                        values.put("mythought",  arr[3]);
                                        db3.insert("weekend", null, values);
                                        //   commentInfoHelper.close();
                                    }

                                    cursor.close();
                                    weekendInfoHelper.close();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }

                                */

                                weekendItems.add(weekend);
                                Log.d("saea", "weekends size:"+String.valueOf(weekendItems.size()));
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
                Log.e("saea", "GetWeekends Error: " + error.getMessage());

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
        Log.d("saea", "third"+String.valueOf(mAppItem.size()));

    }


    public static void updateWeekend(final Context context, final String uid, final String date, final String mysentence, final String mythought) {


        Log.i("saea", "Starting Upload...");
        updateWeekend_Connect(context, uid, date, mysentence, mythought);


    }


    public static void updateWeekend_Connect(final Context context, final String uid, final String date, final String mySentence, final String myThought) {
        // Tag used to cancel the request
        String tag_string_req = "req_weekend";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_WEEKENDDATA, new Response.Listener<String>() {

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
                        JSONObject weekend = jObj.getJSONObject("weekend");
                        String mysentence = weekend.getString("mysentence");
                        String mythought = weekend.getString("mythought");

                        Log.d("saea", "result:"+ uid + mysentence + mythought);


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
                Log.e("saea", "UpdateWeekend Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Log.d("saea", "update: "+mySentence);
                params.put("status", "update");
                params.put("uid", uid);
                params.put("date", date);
                params.put("mysentence", mySentence);
                params.put("mythought", myThought);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
