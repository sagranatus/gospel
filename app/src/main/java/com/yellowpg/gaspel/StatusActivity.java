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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yellowpg.gaspel.DB.CommentInfoHelper;
import com.yellowpg.gaspel.DB.LectioInfoHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StatusActivity extends AppCompatActivity {
    CommentInfoHelper commentInfoHelper;
    LectioInfoHelper lectioInfoHelper;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        android.support.v7.app.ActionBar actionbar = getSupportActionBar();

//actionbar setting
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar_status);
        TextView mytext = (TextView) findViewById(R.id.mytext);
        actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2980b9")));
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




        // exp : 텍스트 사이즈 설정
        SharedPreferences sp2 = getSharedPreferences("setting",0);
        String textsize = sp2.getString("textsize", "");
        if(textsize.equals("big")){
          //  thismonth.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
          //  status.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        }else{

        }
      //  init();
        
              // exp : 이 부분은 코멘트 데이터를 가져와서 레벨 업 시켜주는 부분
        commentInfoHelper = new CommentInfoHelper(this);
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
                //    comment_str = cursor.getString(0);
                    date_str = cursor.getString(1);
                 //   sentence_str = cursor.getString(2);

                 //   if(date_str.contains(date_val)){
                    if(date_str != null){
                      //  status.setText(date_str);
                        i++;
                    }
                }




            }catch(Exception e){

            }



        // exp : 이 아래부분은 렉시오디비나에 대한 데이터를 가져와서 레벨업 시켜주는 부분이다.
        lectioInfoHelper = new LectioInfoHelper(this);

        try{
        db = lectioInfoHelper.getReadableDatabase();
        String query = "SELECT bg1, bg2, bg3, sum1, sum2, js1, js2, date, onesentence FROM lectio WHERE date LIKE '%"+date_val+"%'";
        Cursor cursor = db.rawQuery(query, null);
    //   String bg1_str = null;
    //    String bg2_str= null;
    //    String bg3_str= null;
    //    String sum1_str= null;
     //   String sum2_str= null;
     //   String js1_str= null;
    //    String js2_str= null;
        String date_str = null;
     //   String onesentence_str = null;

        while(cursor.moveToNext()){
     //       bg1_str = cursor.getString(0);
     //       bg2_str = cursor.getString(1);
      //      bg3_str = cursor.getString(2);
      //      sum1_str = cursor.getString(3);
      //      sum2_str = cursor.getString(4);
      //      js1_str = cursor.getString(5);
      //      js2_str = cursor.getString(6);
            date_str = cursor.getString(7);
     //       onesentence_str = cursor.getString(8);
         //   if(date_str.contains(date_val)){
            if(date_str!=null){
                j++;
            }
        }

            cursor.close();
            lectioInfoHelper.close();
        }
      catch(Exception e){
        Toast.makeText(StatusActivity.this, "실패", Toast.LENGTH_SHORT).show();
    }
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



}