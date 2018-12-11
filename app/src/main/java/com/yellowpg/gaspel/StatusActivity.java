package com.yellowpg.gaspel;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yellowpg.gaspel.DB.CommentDBSqlData;
import com.yellowpg.gaspel.DB.DBManager;
import com.yellowpg.gaspel.adapter.StatusSaveAdapter;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.data.MonthRecord;
import com.yellowpg.gaspel.etc.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class StatusActivity extends AppCompatActivity {

    Calendar c1 = Calendar.getInstance();

    //현재 해 + 달 구하기
    SimpleDateFormat sdf_today = new SimpleDateFormat("yyyy년 MM월 dd일");
    String date_today = sdf_today.format(c1.getTime());

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월");
    String date_val = sdf.format(c1.getTime());
    SimpleDateFormat sdf_year  = new SimpleDateFormat("yyyy년");
    String year = sdf_year.format(c1.getTime());
    Button thismonth;
    ImageView walk1, walk2, walk3, walk4, walk5, walk6, walking_person, arrive_person;
    int i=0;
    int j=0;
    TextView status;
    LinearLayout ll2, ll3;
    private SessionManager session;
    String uid = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        session = new SessionManager(getApplicationContext());
        uid = session.getUid();

        //actionbar setting
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar_status);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01579b")));
        actionbar.setElevation(0);
        // actionbar의 왼쪽에 버튼을 추가하고 버튼의 아이콘을 바꾼다.
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.back);

        status = (TextView) findViewById(R.id.tv_status);
        walk1 = (ImageView) findViewById(R.id.walking1);
        walk2 = (ImageView) findViewById(R.id.walking2);
        walk3 = (ImageView) findViewById(R.id.walking3);
        walk4 = (ImageView) findViewById(R.id.walking4);
        walk5 = (ImageView) findViewById(R.id.walking5);
        walk6 = (ImageView) findViewById(R.id.walking6);
        walking_person = (ImageView) findViewById(R.id.walking_person);
        arrive_person = (ImageView) findViewById(R.id.arrive_person);
        thismonth = (Button) findViewById(R.id.bt_thismonth) ;




        // 텍스트 사이즈 설정
        SharedPreferences sp2 = getSharedPreferences("setting",0);
        String textsize = sp2.getString("textsize", "");
        if(textsize.equals("big")){
        }else{

        }
      //  init();
        SQLiteDatabase db;

        ArrayList<Comment> comments_arr = new ArrayList<Comment>();
        String comment_str = null;
        DBManager dbMgr = new DBManager(StatusActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectCommentAllData("SELECT * FROM comment WHERE uid = ? AND date LIKE '%"+date_val+"%'", uid, comments_arr);
        dbMgr.dbClose();

        //Iterator 통한 전체 조회
        Iterator iterator = comments_arr.iterator();
        String date;
        while (iterator.hasNext()) {
            date = null;
            Comment comment = (Comment) iterator.next();
            String comment_con = comment.getComment();
            date = comment.getDate();
            String sentence = comment.getOneSentence();
            if(date != null){
                i++;
            }
        }

        // 코멘트 기록을 가져온다
   /*     commentInfoHelper = new CommentInfoHelper(this);
        SQLiteDatabase db;
        ContentValues values;
            try{
               // String comment_str = null;
                String date_str = null;
              //  String sentence_str = null;
                db = commentInfoHelper.getReadableDatabase();
                String[] columns = {"comment_con", "date", "sentence"};
                String query = "SELECT comment_con, date, sentence FROM comment WHERE date LIKE '%"+date_val+"%'"; // WHERE date BETWEEN '2018-11-01' and '2018-11-31'
                Cursor cursor = db.rawQuery(query, null);
                while(cursor.moveToNext()){
                    date_str = cursor.getString(1);

                    if(date_str != null){
                        i++;
                    }
                }
                cursor.close();
                commentInfoHelper.close();

            }catch(Exception e){

            }

        */
       // 렉시오디비나 기록을 가져온다
        ArrayList<Lectio> lectios_arr = new ArrayList<Lectio>();
        String bg1_str = null;
        dbMgr.dbOpen();
        dbMgr.selectLectioAllData("SELECT * FROM lectio WHERE uid = ? AND date LIKE '%"+date_val+"%'", uid, lectios_arr);
        dbMgr.dbClose();

        //Iterator 통한 전체 조회
        Iterator iterator2 = lectios_arr.iterator();
        String date2;
        while (iterator2.hasNext()) {
            date2 = null;
            Lectio lectio = (Lectio) iterator2.next();
            date2 = lectio.getDate();
            if(date2 != null){
                j++;
            }
        }

    /*
        lectioInfoHelper = new LectioInfoHelper(this);

        try{
        db = lectioInfoHelper.getReadableDatabase();
        String query = "SELECT bg1, bg2, bg3, sum1, sum2, js1, js2, date, onesentence FROM lectio WHERE date LIKE '%"+date_val+"%'";
        Cursor cursor = db.rawQuery(query, null);

        String date_str = null;

        while(cursor.moveToNext()){
            date_str = cursor.getString(7);
            if(date_str!=null){
                j++;
            }
            Log.d("saea",j+date_str);
        }

            cursor.close();
            lectioInfoHelper.close();
        }
      catch(Exception e){
        Toast.makeText(StatusActivity.this, "실패", Toast.LENGTH_SHORT).show();
    }

    */
    int point = i+j*5;
    status.setText("복음 묵상 "+i+"일\n렉시오디비나 "+j+"일");
    if(1<= point && point < 5){
        walk1.setVisibility(View.VISIBLE);
    }else if(5<= point && point < 10){
        walk1.setVisibility(View.VISIBLE);
        walk2.setVisibility(View.VISIBLE);
    }else if(10<= point && point < 15){
        walk1.setVisibility(View.VISIBLE);
        walk2.setVisibility(View.VISIBLE);
        walk3.setVisibility(View.VISIBLE);
    }else if(15<= point && point < 20){
        walk1.setVisibility(View.VISIBLE);
        walk2.setVisibility(View.VISIBLE);
        walk3.setVisibility(View.VISIBLE);
        walk4.setVisibility(View.VISIBLE);
    }else if(20<= point && point < 25){
        walk1.setVisibility(View.VISIBLE);
        walk2.setVisibility(View.VISIBLE);
        walk3.setVisibility(View.VISIBLE);
        walk4.setVisibility(View.VISIBLE);
        walk5.setVisibility(View.VISIBLE);
    }else if(25<= point && point < 30){
        walk1.setVisibility(View.VISIBLE);
        walk2.setVisibility(View.VISIBLE);
        walk3.setVisibility(View.VISIBLE);
        walk4.setVisibility(View.VISIBLE);
        walk5.setVisibility(View.VISIBLE);
        walk6.setVisibility(View.VISIBLE);
    }else if(30<= point){
        walk1.setVisibility(View.VISIBLE);
        walk2.setVisibility(View.VISIBLE);
        walk3.setVisibility(View.VISIBLE);
        walk4.setVisibility(View.VISIBLE);
        walk5.setVisibility(View.VISIBLE);
        walk6.setVisibility(View.VISIBLE);
        arrive_person.setVisibility(View.VISIBLE);
        walking_person.setVisibility(View.GONE);
    }

        ArrayList<MonthRecord> data = new ArrayList<MonthRecord>();


        StatusSaveAdapter adapter = new StatusSaveAdapter(StatusActivity.this, R.layout.custom_status, data, textsize);
        ListView lv = (ListView) findViewById(R.id.lv_month);
        lv.setAdapter(adapter);

        for(int k=1; k<10; k++){
            if(date_val.contains("0"+k+"월")){
                continue;
            }
            int[] records = getData("0"+k+"월");
            int points = records[0] + records[1]*5;
            String comments =  Integer.toString(records[0]);
            String lectios =  Integer.toString(records[1]);
            if(!comments.equals("0") || !lectios.equals("0") ){
                data.add(new MonthRecord("0"+k+"월", comments, lectios, points ));
                Log.d("saea", comments+lectios+points);
            }

        }
        for(int k=10; k<13; k++){
            if(date_val.contains(" "+k+"월")){
                continue;
            }
            int[] records = getData(" "+k+"월");
            int points = records[0] + records[1]*5;
            String comments =  Integer.toString(records[0]);
            String lectios =  Integer.toString(records[1]);
            if(!comments.equals("0") || !lectios.equals("0") ){
                data.add(new MonthRecord(k+"월", comments, lectios, points ));
                Log.d("saea", comments+lectios+points);
            }

        }
        Log.d("saea", data.size()+"size");
}
    // 커스텀 다이얼로그 선택시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                finish();
                return true;
        }
    }

    public int[] getData(String month){
        int i = 0;
        int j = 0;
        SQLiteDatabase db;
        // 코멘트 기록을 가져온다
    /*    commentInfoHelper = new CommentInfoHelper(this);
        SQLiteDatabase db;
        ContentValues values;
        try{
            // String comment_str = null;
            String date_str = null;
            //  String sentence_str = null;
            db = commentInfoHelper.getReadableDatabase();
            String[] columns = {"comment_con", "date", "sentence"};
            String query = "SELECT comment_con, date, sentence FROM comment WHERE date LIKE '%"+month+"%'"; // WHERE date BETWEEN '2018-11-01' and '2018-11-31'
            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
                date_str = cursor.getString(1);

                if(date_str != null){
                    i++;
                }
            }
            cursor.close();
            commentInfoHelper.close();

        }catch(Exception e){

        }

        */
        ArrayList<Comment> comments = new ArrayList<Comment>();
        String comment_str = null;
        DBManager dbMgr = new DBManager(StatusActivity.this);
        dbMgr.dbOpen();
        dbMgr.selectCommentAllData("SELECT * FROM comment WHERE uid = ? AND date LIKE '%"+month+"%'", uid, comments);
        dbMgr.dbClose();

        if(!comments.isEmpty()){
            comment_str = comments.get(0).getComment();
        }else{
        }
        //Iterator 통한 전체 조회
        Iterator iterator = comments.iterator();
        String date;
        while (iterator.hasNext()) {
            date = null;
            Comment comment = (Comment) iterator.next();
            String comment_con = comment.getComment();
            date = comment.getDate();
            String sentence = comment.getOneSentence();
            if(date != null){
                i++;
            }
        }


        ArrayList<Lectio> lectios_arr = new ArrayList<Lectio>();
        dbMgr.dbOpen();
        dbMgr.selectLectioAllData("SELECT * FROM lectio WHERE uid = ? AND date LIKE '%"+month+"%'", uid, lectios_arr);
        dbMgr.dbClose();

        //Iterator 통한 전체 조회
        Iterator iterator2 = lectios_arr.iterator();
        String date2;
        while (iterator2.hasNext()) {
            date2 = null;
            Lectio lectio = (Lectio) iterator2.next();
            date2 = lectio.getDate();
            if(date2 != null){
                j++;
            }
        }

        return new int[]{i,j};
    }



}