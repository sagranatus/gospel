package com.yellowpg.gaspel.etc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.WeekdayArrayAdapter;
import com.yellowpg.gaspel.DB.CommentDBSqlData;
import com.yellowpg.gaspel.DB.DBManager_Comment;
import com.yellowpg.gaspel.DB.LectioInfoHelper;
import com.yellowpg.gaspel.DB.CommentInfoHelper;
import com.yellowpg.gaspel.DB.WeekendInfoHelper;
import com.yellowpg.gaspel.MainActivity;
import com.yellowpg.gaspel.R;
import com.yellowpg.gaspel.adapter.CaldroidSampleCustomAdapter;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.data.Weekend;

import java.util.ArrayList;
import java.util.HashMap;

import hirondelle.date4j.DateTime;

public class CaldroidSampleCustomFragment extends CaldroidFragment {
    CommentInfoHelper dailyInfoHelper;
    LectioInfoHelper lectioInfoHelper;
    protected HashMap<DateTime, Comment> events = new HashMap<DateTime, Comment>();
    protected HashMap<DateTime, Lectio> events2 = new HashMap<DateTime, Lectio>();
    protected HashMap<DateTime, Weekend> events3 = new HashMap<DateTime, Weekend>();
    TextView text;
    TextView today, date, oneSentence, comment;
    TextView date2, sentence2, bg1;


    // gridadapter를 새로 불러와서 생성
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {

        today = (TextView) getActivity().findViewById(R.id.tv_today);
        date = (TextView) getActivity().findViewById(R.id.tv_date);
        oneSentence = (TextView) getActivity().findViewById(R.id.tv_oneSentence);
        comment = (TextView) getActivity().findViewById(R.id.tv_comment);

        date2 = (TextView) getActivity().findViewById(R.id.tv_date2);
        sentence2 = (TextView) getActivity().findViewById(R.id.tv_oneSentence2);
        bg1 = (TextView) getActivity().findViewById(R.id.tv_bg1);
        // TODO Auto-generated method stub

        //  custom adapter에 값을 전달
        return new CaldroidSampleCustomAdapter(getActivity(), month, year,
                getCaldroidData(), extraData, events, events2, today, date, oneSentence, comment, date2, sentence2, bg1);

    }


    public void refreshView() {
        // If month and year is not yet initialized, refreshView doesn't do
        // anything
        if (month == -1 || year == -1) {
            return;
        }
        refreshMonthTitleTextView();

        // 아마도 위에서 커스터마이즈한 것을 refresh한다
        // Refresh the date grid views
        for (CaldroidGridAdapter adapter : datePagerAdapters) {
            // Reset caldroid data
            adapter.setCaldroidData(getCaldroidData());

         // 데이터를 가져온다
            // 코멘트 부분
            dailyInfoHelper = new CommentInfoHelper(getActivity());
            SQLiteDatabase db;

            try{
                db = dailyInfoHelper.getReadableDatabase();
                String query = "SELECT comment_con, date, sentence FROM comment";
                Cursor cursor = db.rawQuery(query, null);

                while(cursor.moveToNext()){
                    String comment_con = cursor.getString(0);
                    String date = cursor.getString(1);
                    String sentence = cursor.getString(2);

                    int yearsite = date.indexOf("년");
                    int monthsite = date.indexOf("월");
                    int daysite = date.indexOf("일 ");
                    String year= date.substring(0, yearsite);
                    String month;
                    String day;
                    if(date.substring(yearsite+2, monthsite).length() > 1){
                        month= date.substring(yearsite+2, monthsite);
                    }else{
                        month= "0"+date.substring(yearsite+2, monthsite);
                    }
                    if(date.substring(monthsite+2, daysite).length() > 1){
                        day = date.substring(monthsite+2, daysite);
                    }else{
                        day = "0"+date.substring(monthsite+2, daysite);
                    }
                    // hashmap에 값을 삽입
                    events.put(new DateTime(year+"-"+month+"-"+day+" 00:00:00.000000000") , new Comment(new DateTime(year+"-"+month+"-"+day+" 00:00:00.000000000"), date, comment_con, sentence));
                }
                cursor.close();
                dailyInfoHelper.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            // 렉시오 디비나 부분
            lectioInfoHelper = new LectioInfoHelper(getActivity());
            SQLiteDatabase db2;
            ContentValues values2;

            try{
                String bg1_str = null;
                String bg2_str= null;
                String bg3_str= null;
                String sum1_str= null;
                String sum2_str= null;
                String js1_str= null;
                String js2_str= null;

                String date_str = null;
                String onesentence_str = null;
                db2 = lectioInfoHelper.getReadableDatabase();
                String query = "SELECT bg1, bg2, bg3, sum1, sum2, js1, js2, date, onesentence FROM lectio";
                Cursor cursor = db2.rawQuery(query, null);

                while(cursor.moveToNext()){
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
                    String year= date_str.substring(0, yearsite);
                    String month;
                    String day;
                    month= date_str.substring(yearsite+2, monthsite);
                    day = date_str.substring(monthsite+2, daysite);
                    events2.put(new DateTime(year+"-"+month+"-"+day+" 00:00:00.000000000") , new Lectio(new DateTime(year+"-"+month+"-"+day+" 00:00:00.000000000"), date_str, onesentence_str, bg1_str, bg2_str, bg3_str, sum1_str, sum2_str, js1_str, js2_str));

                }

                cursor.close();
                lectioInfoHelper.close();
            }
            catch(Exception e){

            }

            //weekend 값 가져오기
            WeekendInfoHelper weekendInfoHelper = new WeekendInfoHelper(getActivity());
            SQLiteDatabase db3;
            ContentValues values3;

            try{
                db3 = weekendInfoHelper.getReadableDatabase();
                String query = "SELECT date, mysentence, mythought FROM weekend";
                Cursor cursor = db3.rawQuery(query, null);

                while(cursor.moveToNext()){
                    String date = cursor.getString(0);
                    String mysentence = cursor.getString(1);
                    String mythought = cursor.getString(2);

                    int yearsite = date.indexOf("년");
                    int monthsite = date.indexOf("월");
                    int daysite = date.indexOf("일 ");
                    String year= date.substring(0, yearsite);
                    String month;
                    String day;
                    if(date.substring(yearsite+2, monthsite).length() > 1){
                        month= date.substring(yearsite+2, monthsite);
                    }else{
                        month= "0"+date.substring(yearsite+2, monthsite);
                    }
                    if(date.substring(monthsite+2, daysite).length() > 1){
                        day = date.substring(monthsite+2, daysite);
                    }else{
                        day = "0"+date.substring(monthsite+2, daysite);
                    }

                    events3.put(new DateTime(year+"-"+month+"-"+day+" 00:00:00.000000000") , new Weekend(new DateTime(year+"-"+month+"-"+day+" 00:00:00.000000000"), date, mysentence, mythought));
                }
                cursor.close();
                weekendInfoHelper.close();
            }catch(Exception e){
                e.printStackTrace();
            }


            // 이는 events와 events2, events3 인 hashmap을 adapter에서 불러올 수 있도록 설정해준 부분
            // reset events
            ((CaldroidSampleCustomAdapter)adapter).setEvents(events);
            ((CaldroidSampleCustomAdapter)adapter).setEvents2(events2);
            ((CaldroidSampleCustomAdapter)adapter).setEvents3(events3);
            // Refresh view
            adapter.notifyDataSetChanged();
        }
    }
}
