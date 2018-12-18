package com.yellowpg.gaspel.server;

import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yellowpg.gaspel.etc.AppConfig;
import com.yellowpg.gaspel.etc.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Server_ThreeGaspel {
    static String TAG = "getThreeGaspel";

    public static void getMoreGaspel(final String status, final TextView tv, final String person, final String chapter, final String verse) {
        get_More_Gaspel(status, tv, person, chapter, verse);
    }

    // 그날 복음 내용 가져오기
    public static void get_More_Gaspel(final String status, final TextView tv, final String person, final String chapter, final String verse) {
        Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                // Tag used to cancel the request
                String tag_string_req = "req_getgaspel";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_THREEGASPEL, new Response.Listener<String>() { // URL_LOGIN : "http://192.168.116.1/android_login_api/login.php";
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
                                String contents = jObj.getString("contents");
                                String prev_contents = tv.getText().toString();
                                if(status.equals("prev")){
                                    tv.setText(contents +"\n"+ prev_contents);
                                }else{
                                 //   tv.setText( prev_contents + "\n" +verse+ contents);
                                    tv.append("\n" +contents);
                                }

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
                        params.put("status", status);
                        params.put("person", person);
                        params.put("chapter", chapter);
                        params.put("verse", verse);

                        return params;
                    }
                };
                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        });
        t.start();
    }
}

