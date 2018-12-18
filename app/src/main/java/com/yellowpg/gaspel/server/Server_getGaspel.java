package com.yellowpg.gaspel.server;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server_getGaspel {
    public static void getGaspel(final ImageButton up, final ImageButton down, final String date, final TextView tv_onesentence, final TextView tv_contents) {
        get_Gaspel(up, down, date, tv_onesentence, tv_contents);
    }
    static String TAG = "getGaspel";

    // 그날 복음 내용 가져오기
    public static void get_Gaspel(final ImageButton up, final ImageButton down, final String date, final TextView tv_onesentence, final TextView tv_contents) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Tag used to cancel the request
                String tag_string_req = "req_getgaspel";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_TODAY, new Response.Listener<String>() { // URL_LOGIN : "http://192.168.116.1/android_login_api/login.php";
                    boolean error;
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "getGaspel Response: " + response.toString());
                        try {
                            JSONObject jObj = new JSONObject(response);
                            error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                String gaspel_date;
                                String gaspel_sentence;
                                String gaspel_contents;
                                final String today_gaspel;
                                String today_where;
                                String firstverse = null;
                                String lastverse = null;
                                final int[] prev = {0};
                                final int[] next = {0};

                                // Now store the user in SQLite
                                gaspel_date = jObj.getString("created_at");
                                Log.d(TAG, gaspel_date);
                                gaspel_sentence = jObj.getString("sentence");
                                gaspel_contents = jObj.getString("contents");

                                // DB에서 가져온 복음내용 편집하기
                                String contents = gaspel_contents;
                                contents = contents.replaceAll("&gt;", ">");
                                contents = contents.replaceAll("&lt;", "<");
                                contents = contents.replaceAll("&ldquo;", "");
                                contents = contents.replaceAll("&rdquo;", "");
                                contents = contents.replaceAll("&lsquo;", "");
                                contents = contents.replaceAll("&rsquo;", "");
                                contents = contents.replaceAll("&prime;", "'");
                                contents = contents.replaceAll("\n", " ");
                                contents = contents.replaceAll("&hellip;", "…");
                                contents = contents.replaceAll("주님의 말씀입니다.", "\n"+"주님의 말씀입니다.");

                                int idx = contents.indexOf("✠");
                                int idx2 = contents.indexOf("◎ 그리스도님 찬미합니다");
                                contents = contents.substring(idx, idx2);
                                Log.d("saea",contents);
                                // 줄넘김 편집
                                int idx3 = contents.indexOf("거룩한 복음입니다.");
                                int length = "거룩한 복음입니다.".length();
                                final String after = contents.substring(idx3+length+4);

                                // 추가
                                int idx_today = 0;
                                int today_length = 0;

                                idx_today = contents.indexOf("전한 거룩한 복음입니다.");
                                if(idx_today == -1){
                                    idx_today = contents.indexOf("전한 거룩한 복음의 시작입니다.");
                                    today_length = "전한 거룩한 복음의 시작입니다.".length();
                                    today_gaspel = contents.substring(2,idx_today-2); // 복음사 사람 이름
                                }else{
                                    today_length = "전한 거룩한 복음입니다.".length();
                                    today_gaspel = contents.substring(2,idx_today-2);
                                }

                                today_where = contents.substring(idx_today+today_length,idx_today+today_length+5);
                                //	Log.d("saea","today who?"+today_gaspel+today_where);
                                if(today_where.contains(",")){
                                    today_where = today_where.substring(3,4); // 장
                                }else{
                                    today_where = today_where.substring(3);
                                }


                                Pattern p = Pattern.compile(".\\d+");
                                Matcher m = p.matcher(after);

                                while (m.find()) {
                                    if(m.group().contains(",")){
                                        firstverse = m.group().substring(1);
                                    }
                                    //Log.d("saea", m.group());
                                    if(!m.group().contains(",") && !m.group().contains("-") ){
                                        contents = contents.replaceAll(m.group(), "\n"+m.group());
                                    }

                                    lastverse = m.group().trim();
                                }
                                Log.d("saea", today_gaspel+"/"+today_where+"/"+firstverse+"/"+lastverse);

                                prev[0] = Integer.parseInt(firstverse); // 시작절
                                next[0] = Integer.parseInt(lastverse); // 마침절
                                // 내용이랑 첫문장 세팅
                                tv_contents.setText("\n"+contents+"\n");
                                tv_onesentence.setText(gaspel_sentence);

                                final String finalToday_where = today_where;
                                up.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v) {
                                        prev[0] = prev[0] -3;
                                        Server_ThreeGaspel.getMoreGaspel("prev", tv_contents, today_gaspel, finalToday_where, Integer.toString(prev[0]));
                                        Log.d("saea", today_gaspel+"/"+finalToday_where+"/"+prev[0]+"/"+next[0]);
                                    }

                                });

                                down.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v) {
                                        next[0] = next[0] +3;
                                        Server_ThreeGaspel.getMoreGaspel("next", tv_contents, today_gaspel, finalToday_where, Integer.toString(next[0]));
                                        //   Log.d("saea", today_gaspel+"/"+today_where+"/"+firstverse+"/"+lastverse);
                                    }

                                });

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
                        params.put("date", date);
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
