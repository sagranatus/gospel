package com.yellowpg.gaspel;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yellowpg.gaspel.DB.LectioInfoHelper;
import com.yellowpg.gaspel.DB.MemberInfoHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StatusActivity extends Activity {
    MemberInfoHelper memberInfoHelper;
    LectioInfoHelper lectioInfoHelper;

    Calendar c1 = Calendar.getInstance();

    //현재 해 + 달 구하기
    SimpleDateFormat sdf_today = new SimpleDateFormat("yyyy년 MM월 dd일");
    String date_today = sdf_today.format(c1.getTime());


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월");
    String date_val = sdf.format(c1.getTime());
    SimpleDateFormat sdf_year  = new SimpleDateFormat("yyyy년");
    String year = sdf_year.format(c1.getTime());

    int i=0;
    int j=0;
    int k = 0;
    ImageView status_img;
    TextView status_tv, explain;
    ImageView row1, row2, row3, row4, row5, row6, row7, row8, row9, row10, row11, row12;
    LinearLayout ll2, ll3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        String date_0 = sdf_today.format(c1.getTime());
        c1.add( Calendar.DATE, -1 );
        String date_1 = sdf_today.format(c1.getTime());
        c1.add( Calendar.DATE, -1 );
        String date_2 = sdf_today.format(c1.getTime());
        c1.add( Calendar.DATE, -1 );
        String date_3 = sdf_today.format(c1.getTime());
       // Toast.makeText(StatusActivity.this, date_1+date_2+date_3, Toast.LENGTH_SHORT).show();
        ll2 = (LinearLayout) findViewById(R.id.ll_second);
        ll2.setVisibility(ll2.GONE);
        ll3 = (LinearLayout) findViewById(R.id.ll_third);
        ll3.setVisibility(ll3.GONE);
        status_img = (ImageView) findViewById(R.id.img_status);
        status_tv = (TextView) findViewById(R.id.tv_status);
        explain = (TextView) findViewById(R.id.tv_exp);
        row1 = (ImageView) findViewById(R.id.row1);
        row2 = (ImageView) findViewById(R.id.row2);
        row3 = (ImageView) findViewById(R.id.row3);
        row4 = (ImageView) findViewById(R.id.row4);
        row5 = (ImageView) findViewById(R.id.row5);
        row6 = (ImageView) findViewById(R.id.row6);
        row7 = (ImageView) findViewById(R.id.row7);
        row8 = (ImageView) findViewById(R.id.row8);
        row9 = (ImageView) findViewById(R.id.row9);
        row10 = (ImageView) findViewById(R.id.row10);
        row11 = (ImageView) findViewById(R.id.row11);
        row12 = (ImageView) findViewById(R.id.row12);

        // exp : 텍스트 사이즈 설정
        SharedPreferences sp2 = getSharedPreferences("setting",0);
        String textsize = sp2.getString("textsize", "");
        if(textsize.equals("small")){
            status_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            explain.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

        }else if(textsize.equals("big")){
            status_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            explain.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
        }else if(textsize.equals("toobig")){
            status_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
            explain.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
        }else{

        }
      //  init();
        
        // exp : 이 아래는 매달의 상태를 표시하는 부분
     //  MonthStatusShow();

        // exp : 이 부분은 코멘트 데이터를 가져와서 레벨 업 시켜주는 부분
        memberInfoHelper = new MemberInfoHelper(this);
        SQLiteDatabase db;
        ContentValues values;
            try{
               // String comment_str = null;
                String date_str = null;
              //  String sentence_str = null;
                db = memberInfoHelper.getReadableDatabase();
                String[] columns = {"comment_con", "date", "sentence"};
                String query = "SELECT comment_con, date, sentence FROM comment";
                Cursor cursor = db.rawQuery(query, null);
                while(cursor.moveToNext()){
                //    comment_str = cursor.getString(0);
                    date_str = cursor.getString(1);
                 //   sentence_str = cursor.getString(2);

                 //   if(date_str.contains(date_val)){
                    if(date_str != null){
                        i++;
                    }
                }

                SharedPreferences sp_ = getSharedPreferences("setting",0);
                String first = sp_.getString("first", "");
                if(first.equals("") && i>9){ // 첫번째 조건

                    SharedPreferences sp_level;
                    sp_level = getSharedPreferences("setting",0);
                    SharedPreferences.Editor editor;
                    editor =  sp_level.edit();
                    editor.putString("level", "2");
                    editor.putString("first", "done");
                    editor.commit();

                    explain.setText("축하드립니다.\n이제 신앙어린이가 되었습니다. \n렉시오디비나(거룩한 독서) 10회를 통해 어른이 되는 것에 도전해보세요!");
                    SharedPreferences sp = getSharedPreferences("setting",0);
                    String sx = sp.getString("sx", "");
                    status_tv.setText("level2(신앙어린이)");
                    if(sx.equals("woman")){
                        status_img.setImageResource(R.drawable.girl);
                    }else if(sx.equals("man")){
                        status_img.setImageResource(R.drawable.boy);
                    }else{
                        status_img.setImageResource(R.drawable.boy);
                    }

                }


            }catch(Exception e){

            }


        //만약 오늘로 부터 3일 연속 안쓰면 다시 떨어짐
        memberInfoHelper = new MemberInfoHelper(this);

        try{

            // String comment_str = null;
            String date_str = null;
            //  String sentence_str = null;
            db = memberInfoHelper.getReadableDatabase();
            String[] columns = {"comment_con", "date", "sentence"};
            String query = "SELECT comment_con, date, sentence FROM comment";
            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
                //    comment_str = cursor.getString(0);
                date_str = cursor.getString(1);
                //   sentence_str = cursor.getString(2);
                if(date_str.contains(date_0)){ // date_0 : 오늘
                    k++;
                }
                else if(date_str.contains(date_1)){ // date_1 : 어제
                    k++;
                }else if(date_str.contains(date_2)){ // 그저께
                    k++;
                }else if(date_str.contains(date_3)){
                    k++;
                }
            }
            SharedPreferences sp_ = getSharedPreferences("setting",0);
            String first = sp_.getString("first", "");
            if(first.equals("done") && k==0 ){ // 두번째 조건
                SharedPreferences sp_level;
                sp_level = getSharedPreferences("setting",0);
                SharedPreferences.Editor editor;
                editor =  sp_level.edit();
                editor.putString("level", "1");
                editor.commit();

                explain.setText("오늘의복음을 요즘 소홀히 하시는 거 같아요. \n주님의 말씀을 열심히 읽고 어서 다시 신앙어린이로 자라 볼까요?");
                SharedPreferences sp = getSharedPreferences("setting",0);
                    status_img.setImageResource(R.drawable.baby);
                    status_tv.setText("level1(신앙아기)");

            }else if(first.equals("done") && k>0){ // 세번째 조건
                SharedPreferences sp_level;
                sp_level = getSharedPreferences("setting",0);
                SharedPreferences.Editor editor;
                editor =  sp_level.edit();
                editor.putString("level", "2");
                editor.commit();

                explain.setText("축하드립니다.\n이제 신앙어린이가 되었습니다. \n렉시오디비나(거룩한 독서) 10회를 통해 어른이 되는 것에 도전해보세요!");
                SharedPreferences sp = getSharedPreferences("setting",0);
                String sx = sp.getString("sx", "");
                status_tv.setText("level2(신앙어린이)");
                if(sx.equals("woman")){
                    status_img.setImageResource(R.drawable.girl);
                }else if(sx.equals("man")){
                    status_img.setImageResource(R.drawable.boy);
                }else{
                    status_img.setImageResource(R.drawable.boy);
                }

        }



        }catch(Exception e){

        }



        // exp : 이 아래부분은 렉시오디비나에 대한 데이터를 가져와서 레벨업 시켜주는 부분이다.
        lectioInfoHelper = new LectioInfoHelper(this);

        try{
        db = lectioInfoHelper.getReadableDatabase();
        String query = "SELECT bg1, bg2, bg3, sum1, sum2, js1, js2, date, onesentence FROM lectio";
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

            if(i>0 && j>9){ // 네번째 조건
                SharedPreferences sp_level;
                sp_level = getSharedPreferences("setting",0);
                SharedPreferences.Editor editor;
                editor =  sp_level.edit();
                editor.putString("level", "3");
                editor.commit();
                explain.setText("축하드립니다. \n이제 신앙어른이 되었습니다. \n분명 주님께서 기뻐하실 거예요! \n말씀을 통해 은총 가득한 삶을 살아갑시다!");
                // Toast.makeText(StatusActivity.this, "3개이상 데이터가 있습니다", Toast.LENGTH_SHORT).show();
                SharedPreferences sp = getSharedPreferences("setting",0);
                String sx = sp.getString("sx", "");
                status_tv.setText("level3(신앙어른)");
                if(sx.equals("woman")){
                    status_img.setImageResource(R.drawable.woman);
                }else if(sx.equals("man")){
                    status_img.setImageResource(R.drawable.man);
                }else{
                    status_img.setImageResource(R.drawable.man);
                }

            }
            cursor.close();
            lectioInfoHelper.close();
        }
      catch(Exception e){
        Toast.makeText(StatusActivity.this, "실패", Toast.LENGTH_SHORT).show();
    }


}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_menu_home:
                Intent i0 = new Intent(StatusActivity.this, MainActivity.class);
                startActivity(i0);
                break;
            case R.id.action_menu_01:
                Intent i = new Intent(StatusActivity.this, ExplainActivity.class);
                startActivity(i);
                break;
            case R.id.action_menu_02:
                Intent i2 = new Intent(StatusActivity.this, ThirdActivity.class);
                startActivity(i2);
                break;
            case R.id.action_menu_03:
                Intent i3 = new Intent(StatusActivity.this, StatusActivity.class);
                startActivity(i3);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}