package com.yellowpg.gaspel;

/**
 * Created by Saea on 2017-08-25.
 */

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */

public class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
    private com.google.api.services.calendar.Calendar mService = null;
    private Exception mLastError = null;
    GoogleAccountCredential mCredential;
    Context context;
    MemberInfoHelper dailyInfoHelper;
    LectioInfoHelper lectioInfoHelper;
    Calendar c1;
    ProgressDialog mProgress;
    String day;
    TextView mOutputText;
    MakeRequestTask(Context c, GoogleAccountCredential credential, ProgressDialog p, TextView t) {
        this.mOutputText = t;
        this.context = c;
        this.mCredential = credential;
        this.mProgress = p;
        dailyInfoHelper = new MemberInfoHelper(context);
        lectioInfoHelper = new LectioInfoHelper(context);
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void... params) {
        try {
            // exp : 앱 -> 구글 데이터 삽입(오늘의복음)
            try {
                SQLiteDatabase db;
                ContentValues values;
                db = dailyInfoHelper.getReadableDatabase();
                String query = "SELECT comment_con, date, sentence FROM comment";
                Cursor cursor = db.rawQuery(query, null);
                if(cursor != null) {
                    while (cursor.moveToNext()) {
                        String comment_con = cursor.getString(0);
                        String date = cursor.getString(1);
                        String sentence = cursor.getString(2);

                        int yearsite = date.indexOf("년");
                        int monthsite = date.indexOf("월");
                        int daysite = date.indexOf("일 ");
                        String year = date.substring(0, yearsite);
                        String month;
                        String day;
                        if (date.substring(yearsite + 2, monthsite).length() > 1) {
                            month = date.substring(yearsite + 2, monthsite);
                        } else {
                            month = "0" + date.substring(yearsite + 2, monthsite);
                        }
                        if (date.substring(monthsite + 2, daysite).length() > 1) {
                            day = date.substring(monthsite + 2, daysite);
                        } else {
                            day = "0" + date.substring(monthsite + 2, daysite);
                        }
                        new MakeInsertTask(mCredential, year + "-" + month + "-" + day, year + month + day + "aeasaeapj", sentence + "&" + comment_con, "오늘의복음").execute();

                    }
                }
                cursor.close();
                dailyInfoHelper.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            // exp : 앱 -> 구글 데이터 삽입(렉시오디비나)
            try {
                String bg1_str = null;
                String bg2_str= null;
                String bg3_str= null;
                String sum1_str= null;
                String sum2_str= null;
                String js1_str= null;
                String js2_str= null;

                String date_str = null;
                String onesentence_str = null;
                SQLiteDatabase db2;
                ContentValues values;
                db2 = lectioInfoHelper.getReadableDatabase();
                String query = "SELECT bg1, bg2, bg3, sum1, sum2, js1, js2, date, onesentence FROM lectio";
                Cursor cursor = db2.rawQuery(query, null);

                if(cursor != null) {

                    while (cursor.moveToNext()) {
                        bg1_str = cursor.getString(0);
                        bg2_str = cursor.getString(1);
                        bg3_str = cursor.getString(2);
                        sum1_str = cursor.getString(3);
                        sum2_str = cursor.getString(4);
                        js1_str = cursor.getString(5);
                        js2_str = cursor.getString(6);
                        date_str = cursor.getString(7);
                        onesentence_str = cursor.getString(8);
                        int yearsite = date_str.indexOf("년");
                        int monthsite = date_str.indexOf("월 ");
                        int daysite = date_str.indexOf("일 ");
                        String year = date_str.substring(0, yearsite);
                        String month;
                        String day;
                        month = date_str.substring(yearsite + 2, monthsite);
                        day = date_str.substring(monthsite + 2, daysite);

                        new MakeInsertTask(mCredential, year + "-" + month + "-" + day, year + month + day + "aeasaeapj2", onesentence_str + "&" + "이 복음의 등장인물은 " + bg1_str +
                                "장소는 " + bg3_str + "시간은 " + bg3_str +
                                "이 복음의 내용을 간추리면 " + sum1_str +
                                "특별히 눈에 띄는 부분은 " + sum2_str +
                                "이 복음에서 보여지는 예수님은 " + js1_str +
                                "결과적으로 이 복음을 통해 예수님께서 내게 해주시는 말씀은 " + js2_str, "렉시오디비나").execute();
                    }
                }
                cursor.close();
                dailyInfoHelper.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime __start = new DateTime("2017-07-18T09:00:00-07:00");
        List<String> eventStrings = new ArrayList<String>();
        Events events = mService.events().list("primary")
                .setMaxResults(1000000000)
                .setTimeMin(__start)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();

        if(items != null) {
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                String date;
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }

                // exp : 구글 => 어플 캘린더 데이터 가져오기 (오늘의 복음)
                if (event.getSummary().equals("오늘의복음")) {
                    eventStrings.add(
                            String.format("%s, %s, (%s)", event.getSummary(), event.getDescription(), start)); // 이런형식으로 데이터를 가져온다

                    SQLiteDatabase db;
                    ContentValues values;

                    String timeStr = start.toString();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date_ = null;
                    try {
                        date_ = formatter.parse(timeStr); // cf : 이는 String 날짜를 -> 다시 date format으로 변경하여 날짜를 년,월,일 형식으로 바꿔주기 위함이다.
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c1 = Calendar.getInstance();
                    c1.setTime(date_);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy년 MM월 dd일 ");
                    String date_val1 = sdf1.format(c1.getTime());
                    date = date_val1 + getDay() + "요일";

                    String description = event.getDescription();
                    int sentencebefore = description.indexOf("&");
                    String sentence = description.substring(0, sentencebefore);
                    String comment_con = description.substring(sentencebefore + 1);
                    int already = 0;
                    try {
                        String comment_str = null;
                        db = dailyInfoHelper.getReadableDatabase();
                        String[] columns = {"comment_con", "date", "sentence"};
                        String whereClause = "sentence = ?";
                        String[] whereArgs = new String[]{
                                sentence
                        };
                        Cursor cursor = db.query("comment", columns, whereClause, whereArgs, null, null, null);

                        while (cursor.moveToNext()) {
                            comment_str = cursor.getString(0);
                        }
                        if (comment_str != null) {
                            already = 1;
                        }

                    } catch (Exception e) {

                    }
                    if (already == 0) {
                        try {
                            db = dailyInfoHelper.getWritableDatabase();
                            values = new ContentValues();
                            values.put("comment_con", comment_con);
                            values.put("date", date);
                            values.put("sentence", sentence);
                            db.insert("comment", null, values);
                            dailyInfoHelper.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                }
                // exp : 구글 => 어플 캘린더 데이터 가져오기 (렉시오 디비나)
                if (event.getSummary().equals("렉시오디비나")) {
                    eventStrings.add(
                            String.format("%s, %s, (%s)", event.getSummary(), event.getDescription(), start)); // 이런형식으로 데이터를 가져온다


                    String timeStr = start.toString();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date_ = null;
                    try {
                        date_ = formatter.parse(timeStr); // cf : 이는 String 날짜를 -> 다시 date format으로 변경하여 날짜를 년,월,일 형식으로 바꿔주기 위함이다.
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c1 = Calendar.getInstance();
                    c1.setTime(date_);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy년 MM월 dd일 ");
                    String date_val1 = sdf1.format(c1.getTime());
                    date = date_val1 + getDay() + "요일";

                    String description = event.getDescription();
                    int sentence = description.indexOf("&");
                    int bg1 = description.indexOf("이 복음의 등장인물은 ");
                    int bg2 = description.indexOf("장소는 ");
                    int bg3 = description.indexOf("시간은 ");
                    int sum1 = description.indexOf("이 복음의 내용을 간추리면 ");
                    int sum2 = description.indexOf("특별히 눈에 띄는 부분은 ");
                    int js1 = description.indexOf("이 복음에서 보여지는 예수님은 ");
                    int js2 = description.indexOf("결과적으로 이 복음을 통해 예수님께서 내게 해주시는 말씀은 ");

                    String onesentence1 = description.substring(0, sentence);
                    String background1 = description.substring(bg1 + 12, bg2);
                    String background2 = description.substring(bg2 + 4, bg3);
                    String background3 = description.substring(bg3 + 4, sum1);
                    String summary1 = description.substring(sum1 + 15, sum2);
                    String summary2 = description.substring(sum2 + 14, js1);
                    String jesus1 = description.substring(js1 + 17, js2);
                    String jesus2 = description.substring(js2 + 33);

                    SQLiteDatabase db2;
                    ContentValues values2;
                    int already2 = 0;
                    // cf : 기존에 값이 있는지 확인 후 있는 경우에는 already값을 1로 준다.
                    try {

                        String bg1_str = null;
                        db2 = lectioInfoHelper.getReadableDatabase();
                        String[] columns = {"bg1", "bg2", "bg3", "sum1", "sum2", "js1", "js2", "date", "onesentence"};
                        String whereClause = "onesentence = ?";
                        String[] whereArgs = new String[]{
                                onesentence1
                        };
                        Cursor cursor = db2.query("lectio", columns, whereClause, whereArgs, null, null, null);

                        while (cursor.moveToNext()) {
                            bg1_str = cursor.getString(0);
                        }
                        if (bg1_str != null) {
                            already2 = 1;
                        }
                        cursor.close();
                        lectioInfoHelper.close();
                    } catch (Exception e) {

                    }
                    if (already2 == 0) {
                        try {
                            db2 = lectioInfoHelper.getWritableDatabase();
                            values2 = new ContentValues();
                            values2.put("bg1", background1);
                            values2.put("bg2", background2);
                            values2.put("bg3", background3);
                            values2.put("sum1", summary1);
                            values2.put("sum2", summary2);
                            values2.put("js1", jesus1);
                            values2.put("js2", jesus2);
                            values2.put("date", date);
                            values2.put("onesentence", onesentence1);
                            db2.insert("lectio", null, values2);
                            lectioInfoHelper.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                }

            }
        }
        return eventStrings;
    }


    @Override
    protected void onPreExecute() {
       // mOutputText.setText("");
        mProgress.show();
    }

    @Override
    protected void onPostExecute(List<String> output) {
        mProgress.hide();
        if (output == null || output.size() == 0) {
            Toast.makeText(context, "연동에 성공하였습니다", Toast.LENGTH_SHORT).show();
        } else {
            output.add(0, "Data retrieved using the Google Calendar API:");
            mOutputText.setText(TextUtils.join("\n", output));
            Toast.makeText(context, "연동에 성공하였습니다", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCancelled() {
        mProgress.hide();
        if (mLastError != null)
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                Toast.makeText(context, "error1", Toast.LENGTH_SHORT).show();
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                Toast.makeText(context, "error2", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "연동에 실패하였습니다", Toast.LENGTH_SHORT).show();
                mOutputText.setText("The following error occurred:\n"
                        + mLastError.getMessage());
            }
        else {
            mOutputText.setText("Request cancelled.");
            Toast.makeText(context, "error4", Toast.LENGTH_SHORT).show();
        }
    }
    public String getDay(){
        int dayNum = c1.get(Calendar.DAY_OF_WEEK) ;

        switch(dayNum){
            case 1:
                day = "일";
                break ;
            case 2:
                day = "월";
                break ;
            case 3:
                day = "화";
                break ;
            case 4:
                day = "수";
                break ;
            case 5:
                day = "목";
                break ;
            case 6:
                day = "금";
                break ;
            case 7:
                day = "토";
                break ;

        }
        return day;
    }
}


