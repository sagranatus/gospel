package com.yellowpg.gaspel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CellView;
import com.yellowpg.gaspel.LectioActivity;
import com.yellowpg.gaspel.MainActivity;
import com.yellowpg.gaspel.R;
import com.yellowpg.gaspel.WeekendActivity;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.data.Weekend;
import com.yellowpg.gaspel.etc.getDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import hirondelle.date4j.DateTime;

public class CaldroidSampleCustomAdapter extends CaldroidGridAdapter {
    int month;
    int year;
    TextView date, oneSentence, comment, upper;
    TextView bg1;
    Context mContext;
    ImageView slide1, slide2, slide3;
    Calendar c1;
    String day;
    Button edit, goComment, goLectio, goWeekend;
    LinearLayout ll_content;
    Intent intent;
    protected HashMap<DateTime, Comment> events = new HashMap<DateTime, Comment>();
    protected HashMap<DateTime, Lectio> events2 = new HashMap<DateTime, Lectio>();
    protected HashMap<DateTime, Weekend> events3 = new HashMap<DateTime, Weekend>();
    //hirondelle.date4j.DateTime blueDate2 = new DateTime("2017-07-19 00:00:00.000000000");

    public CaldroidSampleCustomAdapter(Context context,  Intent intent, int month, int year,
                                       HashMap<String, Object> caldroidData,
                                       HashMap<String, Object> extraData, TextView date, TextView oneSentence, TextView comment,
                                       TextView bg1, ImageView slide1, ImageView slide2, ImageView slide3, TextView upper, Button edit, Button goComment, Button goLectio, Button goWeekend, LinearLayout ll_content) {
        super(context, month, year, caldroidData, extraData);
        this.mContext = context;
        this.month = month;
        this.year = year;
        this.date = date;
        this.comment = comment;
        this.oneSentence = oneSentence;
        this.bg1 = bg1;
        this.slide1 = slide1;
        this.slide2 = slide2;
        this.slide3 = slide3;
        this.upper = upper;
        this.edit = edit;
        this.goComment = goComment;
        this.goLectio = goLectio;
        this.goWeekend = goWeekend;

        this.ll_content = ll_content;
        this.intent = intent;
    }

    public void setEvents(HashMap<DateTime, Comment> events) {
        // TODO Auto-generated method stub
        this.events = events;
    }

    public void setEvents2(HashMap<DateTime, Lectio> events2) {
        // TODO Auto-generated method stub
        this.events2 = events2;
    }

    public void setEvents3(HashMap<DateTime, Weekend> events3) {
        // TODO Auto-generated method stub
        this.events3 = events3;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;

        if (convertView == null) {
            cellView = inflater.inflate(R.layout.custom_cell, null);
        }
        cellView = inflater.inflate(R.layout.custom_cell, null);

        TextView tv1 = (TextView) cellView.findViewById(R.id.tv1);
        final TextView tv2 = (TextView) cellView.findViewById(R.id.tv2);
        final ImageView img = (ImageView) cellView.findViewById(R.id.img);
        final ImageView img2 = (ImageView) cellView.findViewById(R.id.img2);
        Resources resources = context.getResources();
        final DateTime dateTime = this.datetimeList.get(position);

        // 배경 하얀색, 글씨 검정색으로 세팅 및 날짜 삽입
        cellView.setBackgroundColor(resources
                .getColor(com.caldroid.R.color.caldroid_white));
        tv1.setTextColor(Color.parseColor("#999999"));
        tv1.setText("" + dateTime.getDay());

        edit.setBackgroundResource(R.drawable.button_bg2);
        goComment.setBackgroundResource(R.drawable.button_bg2);
        goLectio.setBackgroundResource(R.drawable.button_bg2);
        goWeekend.setBackgroundResource(R.drawable.button_bg2);
        date.setVisibility(View.VISIBLE);

        // 오늘 날짜로 세팅한다
        c1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 ");
        String date_typed = sdf.format(c1.getTime()); // yyyy-MM-dd => yyyy년 MM월 dd일 x요일
        String typedDate = date_typed+getDay.getDay(c1)+"요일";
        date.setText(typedDate);

        // 나의 기록으로오는 경우 오늘날짜를 보여주고 되돌아 오는 경우 dateback 값을 보여준다
        DateTime firstdate = getToday();
        String daydate = intent.getStringExtra("dateBack");
        if(daydate != null) {
            firstdate = new DateTime(daydate + " 00:00:00.000000000");
        }

        // 여기부터 오늘의 데이터를 가져온다.
        // 당일 날짜값이 없는 경우
        if(events.get(firstdate)==null && events2.get(firstdate)==null){
            // 이는
            if(daydate != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date_ = null;
                try {
                    date_ = formatter.parse(daydate); // string yyyy-MM-dd => Date 형식
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c1 = Calendar.getInstance();
                c1.setTime(date_);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy년 MM월 dd일 ");
                String date_val1 = sdf1.format(c1.getTime()); //  yyyy-MM-dd => yyyy년 MM월 dd일 x요일
                typedDate = date_val1 + getDay.getDay(c1) + "요일";
                date.setText(typedDate);
            }

            slide1.setVisibility(View.GONE);
            slide2.setVisibility(View.GONE);
            slide3.setVisibility(View.GONE);
            upper.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
            if(typedDate.contains("일요일")){
                goWeekend.setVisibility(View.VISIBLE);
                goComment.setVisibility(View.GONE);
                goLectio.setVisibility(View.GONE);
            }else{
                goComment.setVisibility(View.VISIBLE);
                goLectio.setVisibility(View.VISIBLE);
                goWeekend.setVisibility(View.GONE);
            }

            date.setVisibility(View.VISIBLE);
            oneSentence.setVisibility(View.GONE);
            bg1.setVisibility(View.GONE);

        }else{
            slide1.setVisibility(View.VISIBLE);
            slide3.setVisibility(View.VISIBLE);

            goComment.setVisibility(View.GONE);
            goLectio.setVisibility(View.GONE);
            goWeekend.setVisibility(View.GONE);

            slide1.setVisibility(View.VISIBLE);
            upper.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            slide1.setImageResource(R.drawable.slide1);
            upper.setText("그날의 복음 말씀");
            slide2.setImageResource(R.drawable.slide2_off);
            slide3.setImageResource(R.drawable.slide3_off);
            date.setVisibility(date.VISIBLE);
            comment.setVisibility(View.GONE);
            bg1.setVisibility(View.GONE);


            slide1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goComment.setVisibility(View.GONE);
                    goLectio.setVisibility(View.GONE);
                    goWeekend.setVisibility(View.GONE);
                    slide1.setImageResource(R.drawable.slide1);
                    slide2.setImageResource(R.drawable.slide2_off);
                    slide3.setImageResource(R.drawable.slide3_off);
                    upper.setText("그날의 복음 말씀");
                    edit.setVisibility(View.GONE);
                    comment.setVisibility(View.GONE);
                    oneSentence.setVisibility(View.VISIBLE);
                    bg1.setVisibility(View.GONE);
                }
            });
            // TODO Auto-generated method stub

            if(events.get(firstdate)!=null) {
                if(events.get(firstdate).getDate().contains("일요일")){
                    slide2.setVisibility(View.GONE);
                }else{
                    slide2.setVisibility(View.VISIBLE);
                }
                oneSentence.setVisibility(oneSentence.VISIBLE);
                //  comment.setVisibility(comment.VISIBLE);
                date.setText(events.get(firstdate).getDate());
                oneSentence.setText(events.get(firstdate).getOneSentence());
                comment.setText(events.get(firstdate).getComment());

                slide2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slide1.setImageResource(R.drawable.slide1_off);
                        slide2.setImageResource(R.drawable.slide2);
                        slide3.setImageResource(R.drawable.slide3_off);
                        upper.setText("말씀 새기기");
                        edit.setVisibility(View.VISIBLE);
                        comment.setVisibility(View.VISIBLE);
                        oneSentence.setVisibility(View.GONE);
                        bg1.setVisibility(View.GONE);

                        edit.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                Intent intent = new Intent(mContext, MainActivity.class);
                                String _date = date.getText().toString();
                                int y1 = _date.indexOf("년");
                                int m1 = _date.indexOf("월 ");
                                int d1 = _date.indexOf("일 ");
                                String year = _date.substring(0,y1);
                                String month = _date.substring(6,m1);
                                String day = _date.substring(m1+2, d1);
                                String thisdate = year+"-"+month+"-"+day;
                                intent.putExtra("date",thisdate);
                                mContext.startActivity(intent);
                            }
                        });
                    }

                });


            }else{
                slide2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        oneSentence.setVisibility(oneSentence.GONE);
                        upper.setText("말씀새기기");
                        edit.setVisibility(View.GONE);
                        bg1.setVisibility(View.GONE);
                        slide1.setImageResource(R.drawable.slide1_off);
                        slide2.setImageResource(R.drawable.slide2);
                        slide3.setImageResource(R.drawable.slide3_off);
                        goComment.setVisibility(View.VISIBLE);
                    }
                });
            }
            if(events2.get(firstdate)!=null) {
                if(events2.get(firstdate).getDate().contains("일요일")){
                    slide2.setVisibility(View.GONE);
                }else{
                    slide2.setVisibility(View.VISIBLE);
                }

                date.setText(events2.get(firstdate).getDate());

                oneSentence.setVisibility(oneSentence.VISIBLE);
                oneSentence.setText(events2.get(firstdate).getOneSentence());

                // 주일 데이터 있을때
                if(events3.get(firstdate) != null){
                    Log.d("saea", events3.get(firstdate).getMyThought());
                    if(events3.get(firstdate).getMyThought().equals("")){
                        bg1.setText(Html.fromHtml("<font color=\"#999999\">· 이 복음의 등장인물은 </font> " + events2.get(firstdate).getBg1()
                                +"<br><font color=\"#999999\">· 장소는</font> " + events2.get(firstdate).getBg2() +
                                "<br><font color=\"#999999\">· 시간은</font> " + events2.get(firstdate).getBg3()
                                +"<br><font color=\"#999999\">· 이 복음의 내용을 간추리면</font> " + events2.get(firstdate).getSum1()
                                +"<br><font color=\"#999999\">· 특별히 눈에 띄는 부분은</font> " + events2.get(firstdate).getSum2()
                                +"<br><font color=\"#999999\">· 이 복음에서 보여지는 예수님은</font> " + events2.get(firstdate).getJs1()
                                +"<br><font color=\"#999999\">· 결과적으로 이 복음을 통해 \n예수님께서 내게 해주시는 말씀은</font> \"" + events2.get(firstdate).getJs2()+"\""
                                +"<br><font color=\"#999999\">· 주일 복음에서 묵상한 구절은 </font> " + events3.get(firstdate).getMySentence()
                        ));
                    }else{
                        bg1.setText(Html.fromHtml("<font color=\"#999999\">· 이 복음의 등장인물은 </font> " + events2.get(firstdate).getBg1()
                                +"<br><font color=\"#999999\">· 장소는</font> " + events2.get(firstdate).getBg2() +
                                "<br><font color=\"#999999\">· 시간은</font> " + events2.get(firstdate).getBg3()
                                +"<br><font color=\"#999999\">· 이 복음의 내용을 간추리면</font> " + events2.get(firstdate).getSum1()
                                +"<br><font color=\"#999999\">· 특별히 눈에 띄는 부분은</font> " + events2.get(firstdate).getSum2()
                                +"<br><font color=\"#999999\">· 이 복음에서 보여지는 예수님은</font> " + events2.get(firstdate).getJs1()
                                +"<br><font color=\"#999999\">· 결과적으로 이 복음을 통해 \n예수님께서 내게 해주시는 말씀은</font> \"" + events2.get(firstdate).getJs2()+"\""
                                +"<br><font color=\"#999999\">· 주일 복음에서 묵상한 구절은 </font> " + events3.get(firstdate).getMySentence()
                                +"<br><font color=\"#999999\">· 구절을 묵상하면 내가 느낀 점은 </font> " + events3.get(firstdate).getMyThought()
                        ));
                    }

                }else{
                    bg1.setText(Html.fromHtml("<font color=\"#999999\">· 이 복음의 등장인물은 </font> " + events2.get(firstdate).getBg1()
                            +"<br><font color=\"#999999\">· 장소는</font> " + events2.get(firstdate).getBg2() +
                            "<br><font color=\"#999999\">· 시간은</font> " + events2.get(firstdate).getBg3()
                            +"<br><font color=\"#999999\">· 이 복음의 내용을 간추리면</font> " + events2.get(firstdate).getSum1()
                            +"<br><font color=\"#999999\">· 특별히 눈에 띄는 부분은</font> " + events2.get(firstdate).getSum2()
                            +"<br><font color=\"#999999\">· 이 복음에서 보여지는 예수님은</font> " + events2.get(firstdate).getJs1()
                            +"<br><font color=\"#999999\">· 결과적으로 이 복음을 통해 \n예수님께서 내게 해주시는 말씀은</font> \"" + events2.get(firstdate).getJs2()+"\""));
                }


                final DateTime finalFirstdate = firstdate;
                slide3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slide1.setImageResource(R.drawable.slide1_off);
                        slide2.setImageResource(R.drawable.slide2_off);
                        slide3.setImageResource(R.drawable.slide3);
                        if(events2.get(finalFirstdate).getDate().contains("일요일")){
                            upper.setText("주일의 독서");
                        }else{
                            upper.setText("거룩한 독서");
                        }
                        edit.setVisibility(View.VISIBLE);
                        comment.setVisibility(View.GONE);
                        bg1.setVisibility(View.VISIBLE);
                        oneSentence.setVisibility(View.GONE);

                        edit.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub

                                String _date = date.getText().toString();
                                Log.d("saea", "!!!!!!!!!!!!!"+_date);

                                int y1 = _date.indexOf("년");
                                int m1 = _date.indexOf("월 ");
                                int d1 = _date.indexOf("일 ");
                                String year = _date.substring(0,y1);
                                String month = _date.substring(6,m1);
                                String day = _date.substring(m1+2, d1);
                                String thisdate = year+"-"+month+"-"+day;

                                if(_date.contains("일요일")){
                                    Intent intent = new Intent(mContext, LectioActivity.class);
                                    intent.putExtra("weekend",true);
                                    intent.putExtra("date",thisdate);
                                    mContext.startActivity(intent);
                                }else{
                                    Intent intent = new Intent(mContext, LectioActivity.class);
                                    intent.putExtra("date",thisdate);
                                    mContext.startActivity(intent);
                                }


                            }
                        });

                    }
                });


            }else{
                slide3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oneSentence.setVisibility(oneSentence.GONE);
                        upper.setText("거룩한 독서");

                        comment.setVisibility(View.GONE);
                        edit.setVisibility(View.GONE);

                        slide1.setImageResource(R.drawable.slide1_off);
                        slide2.setImageResource(R.drawable.slide2_off);
                        slide3.setImageResource(R.drawable.slide3);
                        goLectio.setVisibility(View.VISIBLE);
                    }
                });
            }

        }

        //여기까지


        // 데이터값이 있는 경우 별이 보이게 하는 부분 - 렉시오 디비나 부분
        if(events2.get(dateTime)!=null) {
           // img.getLayoutParams().height = 50;
            img2.setVisibility(View.VISIBLE);
         }

        // 데이터값이 있는 경우 별이 보이게 하는 부분 - 코멘트 부분 (코멘트 있고 렉시오 없을때)
        if(events.get(dateTime)!=null && events2.get(dateTime)==null){
            img.setVisibility(View.VISIBLE);
        }

        // 별이 보이는 경우에 클릭 이벤트
        if (img.getVisibility() == View.VISIBLE || img2.getVisibility() == View.VISIBLE)  {

            if(events.get(dateTime)!=null && events2.get(dateTime)!=null){
                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(img.getLayoutParams());
                int sizeInDP = 1;
                int marginInDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, sizeInDP, context.getResources()
                                .getDisplayMetrics());
                marginParams.setMargins(0,0, marginInDp , 0);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                img.setLayoutParams(layoutParams);
            }
            // Its visible
            tv1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    goComment.setVisibility(View.GONE);
                    goLectio.setVisibility(View.GONE);
                    goWeekend.setVisibility(View.GONE);

                    slide1.setVisibility(View.VISIBLE);
                    slide3.setVisibility(View.VISIBLE);
                    upper.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.GONE);
                    slide1.setImageResource(R.drawable.slide1);
                    upper.setText("그날의 복음 말씀");
                    slide2.setImageResource(R.drawable.slide2_off);
                    slide3.setImageResource(R.drawable.slide3_off);
                    date.setVisibility(date.VISIBLE);
                    comment.setVisibility(View.GONE);
                    bg1.setVisibility(View.GONE);

                    // TODO Auto-generated method stub

                    slide1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goComment.setVisibility(View.GONE);
                            goLectio.setVisibility(View.GONE);
                            goWeekend.setVisibility(View.GONE);
                            slide1.setImageResource(R.drawable.slide1);
                            slide2.setImageResource(R.drawable.slide2_off);
                            slide3.setImageResource(R.drawable.slide3_off);
                            upper.setText("그날의 복음 말씀");
                            edit.setVisibility(View.GONE);
                            comment.setVisibility(View.GONE);
                            oneSentence.setVisibility(View.VISIBLE);
                            bg1.setVisibility(View.GONE);
                        }
                    });

                    if(events.get(dateTime)!=null) {
                        if(events.get(dateTime).getDate().contains("일요일")){
                            slide2.setVisibility(View.GONE);
                        }else{
                            slide2.setVisibility(View.VISIBLE);
                        }
                        oneSentence.setVisibility(oneSentence.VISIBLE);
                        date.setText(events.get(dateTime).getDate());
                        oneSentence.setText(events.get(dateTime).getOneSentence());
                        comment.setText(events.get(dateTime).getComment());


                        slide2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goComment.setVisibility(View.GONE);
                                goLectio.setVisibility(View.GONE);
                                goWeekend.setVisibility(View.GONE);
                                slide1.setImageResource(R.drawable.slide1_off);
                                slide2.setImageResource(R.drawable.slide2);
                                slide3.setImageResource(R.drawable.slide3_off);
                                upper.setText("말씀 새기기");
                                edit.setVisibility(View.VISIBLE);
                                comment.setVisibility(View.VISIBLE);
                                oneSentence.setVisibility(View.GONE);
                                bg1.setVisibility(View.GONE);

                                edit.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        Intent intent = new Intent(mContext, MainActivity.class);
                                        String _date = date.getText().toString();
                                        int y1 = _date.indexOf("년");
                                        int m1 = _date.indexOf("월 ");
                                        int d1 = _date.indexOf("일 ");
                                        String year = _date.substring(0,y1);
                                        String month = _date.substring(6,m1);
                                        String day = _date.substring(m1+2, d1);
                                        String thisdate = year+"-"+month+"-"+day;
                                        intent.putExtra("date",thisdate);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }

                        });


                    }else{
                        slide2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                oneSentence.setVisibility(oneSentence.GONE);
                                upper.setText("말씀새기기");
                                edit.setVisibility(View.GONE);
                                bg1.setVisibility(View.GONE);
                                slide1.setImageResource(R.drawable.slide1_off);
                                slide2.setImageResource(R.drawable.slide2);
                                slide3.setImageResource(R.drawable.slide3_off);
                                goComment.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    if(events2.get(dateTime)!=null) {
                        if(events2.get(dateTime).getDate().contains("일요일")){
                            slide2.setVisibility(View.GONE);
                        }else{
                            slide2.setVisibility(View.VISIBLE);
                        }
                        date.setText(events2.get(dateTime).getDate());
                    //    sentence2.setVisibility(sentence2.VISIBLE);
                    //    bg1.setVisibility(bg1.VISIBLE);
                        oneSentence.setVisibility(oneSentence.VISIBLE);
                        oneSentence.setText(events2.get(dateTime).getOneSentence());
                        // 주일 데이터 있는 경우
                        if(events3.get(dateTime) != null){
                            if(events3.get(dateTime).getMyThought().equals("")){
                                bg1.setText(Html.fromHtml("<font color=\"#999999\">· 이 복음의 등장인물은 </font> " + events2.get(dateTime).getBg1()
                                        +"<br><font color=\"#999999\">· 장소는</font> " + events2.get(dateTime).getBg2() +
                                        "<br><font color=\"#999999\">· 시간은</font> " + events2.get(dateTime).getBg3()
                                        +"<br><font color=\"#999999\">· 이 복음의 내용을 간추리면</font> " + events2.get(dateTime).getSum1()
                                        +"<br><font color=\"#999999\">· 특별히 눈에 띄는 부분은</font> " + events2.get(dateTime).getSum2()
                                        +"<br><font color=\"#999999\">· 이 복음에서 보여지는 예수님은</font> " + events2.get(dateTime).getJs1()
                                        +"<br><font color=\"#999999\">· 결과적으로 이 복음을 통해 \n예수님께서 내게 해주시는 말씀은</font> \"" + events2.get(dateTime).getJs2()+"\""
                                        +"<br><font color=\"#999999\">· 주일 복음에서 묵상한 구절은 </font> " + events3.get(dateTime).getMySentence()
                                ));
                            }else{
                                bg1.setText(Html.fromHtml("<font color=\"#999999\">· 이 복음의 등장인물은 </font> " + events2.get(dateTime).getBg1()
                                        +"<br><font color=\"#999999\">· 장소는</font> " + events2.get(dateTime).getBg2() +
                                        "<br><font color=\"#999999\">· 시간은</font> " + events2.get(dateTime).getBg3()
                                        +"<br><font color=\"#999999\">· 이 복음의 내용을 간추리면</font> " + events2.get(dateTime).getSum1()
                                        +"<br><font color=\"#999999\">· 특별히 눈에 띄는 부분은</font> " + events2.get(dateTime).getSum2()
                                        +"<br><font color=\"#999999\">· 이 복음에서 보여지는 예수님은</font> " + events2.get(dateTime).getJs1()
                                        +"<br><font color=\"#999999\">· 결과적으로 이 복음을 통해 \n예수님께서 내게 해주시는 말씀은</font> \"" + events2.get(dateTime).getJs2()+"\""
                                        +"<br><font color=\"#999999\">· 주일 복음에서 묵상한 구절은 </font> " + events3.get(dateTime).getMySentence()
                                        +"<br><font color=\"#999999\">· 구절을 묵상하면 내가 느낀 점은 </font> " + events3.get(dateTime).getMyThought()
                                ));
                            }

                        }else{
                            bg1.setText(Html.fromHtml("<font color=\"#999999\">· 이 복음의 등장인물은 </font> " + events2.get(dateTime).getBg1()
                                    +"<br><font color=\"#999999\">· 장소는</font> " + events2.get(dateTime).getBg2() +
                                    "<br><font color=\"#999999\">· 시간은</font> " + events2.get(dateTime).getBg3()
                                    +"<br><font color=\"#999999\">· 이 복음의 내용을 간추리면</font> " + events2.get(dateTime).getSum1()
                                    +"<br><font color=\"#999999\">· 특별히 눈에 띄는 부분은</font> " + events2.get(dateTime).getSum2()
                                    +"<br><font color=\"#999999\">· 이 복음에서 보여지는 예수님은</font> " + events2.get(dateTime).getJs1()
                                    +"<br><font color=\"#999999\">· 결과적으로 이 복음을 통해 \n예수님께서 내게 해주시는 말씀은</font> \"" + events2.get(dateTime).getJs2()+"\""));
                        }

                        slide3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                slide1.setImageResource(R.drawable.slide1_off);
                                slide2.setImageResource(R.drawable.slide2_off);
                                slide3.setImageResource(R.drawable.slide3);
                                if(events2.get(dateTime).getDate().contains("일요일")){
                                    upper.setText("주일의 독서");
                                }else{
                                    upper.setText("거룩한 독서");
                                }

                                edit.setVisibility(View.VISIBLE);
                                comment.setVisibility(View.GONE);
                                bg1.setVisibility(View.VISIBLE);
                                oneSentence.setVisibility(View.GONE);

                                edit.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        String _date = date.getText().toString();
                                        Log.d("saea", "!!!!!!!!!!!!!"+_date);

                                        int y1 = _date.indexOf("년");
                                        int m1 = _date.indexOf("월 ");
                                        int d1 = _date.indexOf("일 ");
                                        String year = _date.substring(0,y1);
                                        String month = _date.substring(6,m1);
                                        String day = _date.substring(m1+2, d1);
                                        String thisdate = year+"-"+month+"-"+day;

                                        if(_date.contains("일요일")){
                                            Intent intent = new Intent(mContext, LectioActivity.class);
                                            intent.putExtra("weekend",true);
                                            intent.putExtra("date",thisdate);
                                            mContext.startActivity(intent);
                                        }else{
                                            Intent intent = new Intent(mContext, LectioActivity.class);
                                            intent.putExtra("date",thisdate);
                                            mContext.startActivity(intent);
                                        }
                                    }
                                });
                            }
                        });



                    }else{
                        slide3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                oneSentence.setVisibility(oneSentence.GONE);
                                upper.setText("거룩한 독서");

                                comment.setVisibility(View.GONE);
                                edit.setVisibility(View.GONE);

                                slide1.setImageResource(R.drawable.slide1_off);
                                slide2.setImageResource(R.drawable.slide2_off);
                                slide3.setImageResource(R.drawable.slide3);
                                goLectio.setVisibility(View.VISIBLE);
                            }
                        });
                    }




                }


            });
        // 별이 없는 경우에 클릭이벤트
        } else {

            tv1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    slide1.setVisibility(View.GONE);
                    slide2.setVisibility(View.GONE);
                    slide3.setVisibility(View.GONE);
                    upper.setVisibility(View.GONE);
                    edit.setVisibility(View.GONE);
                    comment.setVisibility(View.GONE);


                    //  Intent intent = new Intent(mContext, MainActivity.class);
                    String month = dateTime.getMonth().toString();
                    String day = dateTime.getDay().toString();
                    int length = month.length();
                    int length_day = day.length();
                    if(length == 1){
                        month = "0"+month;
                    }
                    if(length_day == 1){
                        day = "0"+day;
                    }

                    date.setVisibility(View.VISIBLE);
                    oneSentence.setVisibility(View.GONE);
                    bg1.setVisibility(View.GONE);

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date_ = null;
                    try {
                        date_ = formatter.parse(dateTime.getYear()+"-"+month+"-"+day);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    c1 = Calendar.getInstance();
                    c1.setTime(date_);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 ");
                    String date_typed = sdf.format(c1.getTime()); // cf : yyyy-MM-dd => yyyy년 MM월 dd일 x요일
                    String typedDate = date_typed+ getDay.getDay(c1)+"요일"; // c1으로 getday()함

                    date.setText(typedDate);
                  //  intent.putExtra("date",dateTime.getYear()+"-"+month+"-"+day);
                  //  mContext.startActivity(intent);

                    if(typedDate.contains("일요일")){
                        goWeekend.setVisibility(View.VISIBLE);
                        goComment.setVisibility(View.GONE);
                        goLectio.setVisibility(View.GONE);
                    }else{
                        goComment.setVisibility(View.VISIBLE);
                        goLectio.setVisibility(View.VISIBLE);
                        goWeekend.setVisibility(View.GONE);
                    }
                }

            });
        }

        //method

        goLectio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mContext, LectioActivity.class);
                String _date = date.getText().toString();
                int y1 = _date.indexOf("년");
                int m1 = _date.indexOf("월 ");
                int d1 = _date.indexOf("일 ");
                String year = _date.substring(0,y1);
                String month = _date.substring(6,m1);
                String day = _date.substring(m1+2, d1);
                String thisdate = year+"-"+month+"-"+day;
                intent.putExtra("date",thisdate);
                mContext.startActivity(intent);
            }
        });

        goComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mContext,MainActivity.class);
                String _date = date.getText().toString();
                int y1 = _date.indexOf("년");
                int m1 = _date.indexOf("월 ");
                int d1 = _date.indexOf("일 ");
                String year = _date.substring(0,y1);
                String month = _date.substring(6,m1);
                String day = _date.substring(m1+2, d1);
                String thisdate = year+"-"+month+"-"+day;
                intent.putExtra("date",thisdate);
                mContext.startActivity(intent);
            }
        });

        goWeekend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mContext, LectioActivity.class);
                String _date = date.getText().toString();
                int y1 = _date.indexOf("년");
                int m1 = _date.indexOf("월 ");
                int d1 = _date.indexOf("일 ");
                String year = _date.substring(0,y1);
                String month = _date.substring(6,m1);
                String day = _date.substring(m1+2, d1);
                String thisdate = year+"-"+month+"-"+day;
                intent.putExtra("date",thisdate);
                intent.putExtra("weekend",true);
                mContext.startActivity(intent);
            }
        });


        setCustomResources(dateTime, cellView, tv1);
        return cellView;
    }

}
