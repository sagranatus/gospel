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
import com.yellowpg.gaspel.DB.LectioInfoHelper;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server_LectioData {

    public static void insertLectio(final Context context, final String uid, final Lectio lectio) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("saea", "Starting Upload...");
                Lectio lectiodata = lectio;
                insertLectio_Connect(context, uid, lectiodata);


            }
        });
        t.start();

    }


    public static void selectAll(final Context context, final String uid, final ArrayList<Lectio> mAppItem) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("saea", "Starting Upload...");

                selectAll_Connect(context, uid, mAppItem);
                // status = "select_cloth_season_type_detail";

            }
        });
        t.start();
        //     try {
        //        t.join();
        //        Log.d("js", String.valueOf(mAppItem.size()));
        //   } catch (InterruptedException e) {
        //       e.printStackTrace();
        //   }
    }


    public static void insertLectio_Connect(Context context, final String uid, final Lectio lectio) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_LECTIODATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

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

                        JSONObject lectio = jObj.getJSONObject("lectio");
                        String onesentence = lectio.getString("onesentence");
                        String date = lectio.getString("date");
                        String bg1 = lectio.getString("bg1");
                        String bg2 = lectio.getString("bg2");
                        String bg3 = lectio.getString("bg3");
                        String sum1 = lectio.getString("sum1");
                        String sum2 = lectio.getString("sum2");
                        String js1 = lectio.getString("js1");
                        String js2 = lectio.getString("js2");
                        Log.d("saea", "result:"+ uid + date + onesentence + bg1 + bg2 +bg3 + sum1 + sum2 + js1 + js2);


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
                Log.e("saea", "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "insert");
                params.put("uid", uid);
                params.put("date", lectio.getDate());
                params.put("onesentence", lectio.getOneSentence());
                params.put("bg1", lectio.getBg1());
                params.put("bg2", lectio.getBg2());
                params.put("bg3", lectio.getBg3());
                params.put("sum1", lectio.getSum1());
                params.put("sum2", lectio.getSum2());
                params.put("js1", lectio.getJs1());
                params.put("js2", lectio.getJs2());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    public static void selectAll_Connect(final Context context, final String uid, final ArrayList<Lectio> mAppItem) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_LECTIODATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

            @Override
            public void onResponse(String response) {
                ArrayList<Lectio> lectioItems = new ArrayList<Lectio>();
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
                                Lectio lectio = new Lectio(arr[1], arr[2], arr[3], arr[4], arr[5], arr[6], arr[7], arr[8], arr[9]);
                                Log.d("saea", arr[1]+arr[2]+arr[3]);

                                SQLiteDatabase db;
                                LectioInfoHelper lectioInfoHelper = new LectioInfoHelper(context);
                                ContentValues values;
                                // cf : 기존에 값이 있는지 확인 후 있는 경우에는 already값을 1로 준다.
                                try{
                                    String bg1_str = null;
                                    db = lectioInfoHelper.getReadableDatabase();
                                    String[] columns = {"bg1", "bg2", "bg3", "sum1", "sum2", "js1", "js2", "date", "onesentence"};
                                    String whereClause = "date = ?";
                                    String[] whereArgs = new String[] {
                                            arr[1]
                                    };
                                    Cursor cursor = db.query("lectio", columns,  whereClause, whereArgs, null, null, null);

                                    while(cursor.moveToNext()){
                                        bg1_str = cursor.getString(0);
                                    }
                                    if(bg1_str!= null){
                                        Log.d("saea", "기존 값이 있음");
                                    }else{
                                        Log.d("saea", "기존 값이 없음");
                                        values = new ContentValues();
                                        values.put("bg1",  arr[3]);
                                        values.put("bg2",  arr[4]);
                                        values.put("bg3",  arr[5]);
                                        values.put("sum1", arr[6]);
                                        values.put("sum2", arr[7]);
                                        values.put("js1", arr[8]);
                                        values.put("js2", arr[9]);
                                        values.put("date", arr[1]);
                                        values.put("onesentence", arr[2]);
                                        db.insert("lectio", null, values);
                                    }
                                    cursor.close();
                                    lectioInfoHelper.close();
                                }catch(Exception e){

                                }


                                lectioItems.add(lectio);
                                Log.d("saea", "lectios size:"+String.valueOf(lectioItems.size()));
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
                Log.e("saea", "Registration Error: " + error.getMessage());

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


    public static void updateLectio(final Context context, final String uid, final Lectio lectio) {


        Log.i("saea", "Starting Upload...");
        updateLectio_Connect(context, uid, lectio);


    }


    public static void updateLectio_Connect(final Context context, final String uid, final Lectio lectio) {
        // Tag used to cancel the request
        String tag_string_req = "req_cloth";


        StringRequest strReq = new StringRequest(Request.Method.POST, // 여기서 데이터를 POST로 서버로 보내는 것 같다
                AppConfig.URL_LECTIODATA, new Response.Listener<String>() { // URL_REGISTER = "http://192.168.116.1/android_login_api/register.php";

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

                        JSONObject lectio = jObj.getJSONObject("lectio");
                        String onesentence = lectio.getString("onesentence");
                        String date = lectio.getString("date");
                        String bg1 = lectio.getString("bg1");
                        String bg2 = lectio.getString("bg2");
                        String bg3 = lectio.getString("bg3");
                        String sum1 = lectio.getString("sum1");
                        String sum2 = lectio.getString("sum2");
                        String js1 = lectio.getString("js1");
                        String js2 = lectio.getString("js2");
                        Log.d("saea", "update_result:"+ uid + date + onesentence + bg1 + bg2 +bg3 + sum1 + sum2 + js1 + js2);


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
                Log.e("saea", "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() { // StringRequest에 대한 메소드
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Log.d("saea", "update: "+lectio.getOneSentence());
                params.put("status", "update");
                params.put("uid", uid);
                params.put("date", lectio.getDate());
                params.put("onesentence", lectio.getOneSentence());
                params.put("bg1", lectio.getBg1());
                params.put("bg2", lectio.getBg2());
                params.put("bg3", lectio.getBg3());
                params.put("sum1", lectio.getSum1());
                params.put("sum2", lectio.getSum2());
                params.put("js1", lectio.getJs1());
                params.put("js2", lectio.getJs2());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}
