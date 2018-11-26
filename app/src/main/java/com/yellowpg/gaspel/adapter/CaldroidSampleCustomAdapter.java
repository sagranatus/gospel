package com.yellowpg.gaspel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidGridAdapter;
import com.yellowpg.gaspel.MainActivity;
import com.yellowpg.gaspel.R;
import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.data.Weekend;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import hirondelle.date4j.DateTime;

/**
 * Created by Saea on 2017-08-02.
 */
public class CaldroidSampleCustomAdapter extends CaldroidGridAdapter {
    int month;
    int year;
    TextView text;
    TextView today, date, oneSentence, comment;
    TextView date2, sentence2, bg1;
    Context mContext;
    protected HashMap<DateTime, Comment> events = new HashMap<DateTime, Comment>();
    protected HashMap<DateTime, Lectio> events2 = new HashMap<DateTime, Lectio>();
    protected HashMap<DateTime, Weekend> events3 = new HashMap<DateTime, Weekend>();
    //hirondelle.date4j.DateTime blueDate2 = new DateTime("2017-07-19 00:00:00.000000000");

    public CaldroidSampleCustomAdapter(Context context, int month, int year,
                                       HashMap<String, Object> caldroidData,
                                       HashMap<String, Object> extraData, HashMap<DateTime, Comment> events, HashMap<DateTime, Lectio> events2, TextView today, TextView date, TextView oneSentence, TextView comment,
                                       TextView date2, TextView sentence2, TextView bg1) {
        super(context, month, year, caldroidData, extraData);
        this.mContext = context;
        this.month = month;
        this.year = year;
        this.events = events;
        this.today = today;
        this.date = date;
        this.comment = comment;
        this.oneSentence = oneSentence;
        this.date2 = date2;
        this.sentence2  = sentence2;
        this.bg1 = bg1;

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
        ImageView img2 = (ImageView) cellView.findViewById(R.id.img2);
        Resources resources = context.getResources();
        final DateTime dateTime = this.datetimeList.get(position);
        // exp : 배경 하얀색 / 글씨 검정색으로 세팅 및 날짜 삽입
        cellView.setBackgroundColor(resources
                .getColor(com.caldroid.R.color.caldroid_white));
        tv1.setTextColor(Color.parseColor("#999999"));
        tv1.setText("" + dateTime.getDay());

        date.setVisibility(View.GONE);
        oneSentence.setVisibility(View.GONE);
        comment.setVisibility(View.GONE);
        date2.setVisibility(View.GONE);
        sentence2.setVisibility(View.GONE);
        bg1.setVisibility(View.GONE);
    // exp : 이는 오늘의 경우에 가져오는 것
       /* if(dateTime.equals(getToday())) {
                if(events2.get(dateTime)!=null) {
                today.setVisibility(today.GONE);
                date2.setVisibility(date2.VISIBLE);
                sentence2.setVisibility(sentence2.VISIBLE);
                bg1.setVisibility(bg1.VISIBLE);
                bg2.setVisibility(bg2.VISIBLE);
                bg3.setVisibility(bg3.VISIBLE);
                sum1.setVisibility(sum1.VISIBLE);
                sum2.setVisibility(sum2.VISIBLE);
                js1.setVisibility(js1.VISIBLE);
                js2.setVisibility(js2.VISIBLE);
                bg1.setText("이 복음의 등장인물은 " + events2.get(dateTime).getBg1());
                bg2.setText("장소는 " + events2.get(dateTime).getBg2());
                bg3.setText("시간은 " + events2.get(dateTime).getBg3());
                sum1.setText("이 복음의 내용을 간추리면 " + events2.get(dateTime).getSum1());
                sum2.setText("특별히 눈에 띄는 부분은 " + events2.get(dateTime).getSum2());
                js1.setText("이 복음에서 보여지는 예수님은 " + events2.get(dateTime).getJs1());
                js2.setText("결과적으로 이 복음을 통해 예수님께서 내게 해주시는 말씀은 \"" + events2.get(dateTime).getJs2()+"\"");

                if (events.get(dateTime) != null) {
                    date2.setText("");
                    sentence2.setText("렉시오 디비나");
                } else {
                    date2.setText(events2.get(dateTime).getDate());
                    sentence2.setText(events2.get(dateTime).getOneSentence());
                }
            }else{
                date2.setVisibility(date2.GONE);
                sentence2.setVisibility(sentence2.GONE);
                bg1.setVisibility(bg1.GONE);
                bg2.setVisibility(bg2.GONE);
                bg3.setVisibility(bg3.GONE);
                sum1.setVisibility(sum1.GONE);
                sum2.setVisibility(sum2.GONE);
                js1.setVisibility(js1.GONE);
                js2.setVisibility(js2.GONE);
            }

            if(events.get(dateTime)!=null) {
                today.setVisibility(today.GONE);
              //  date.setVisibility(date.VISIBLE);
              //  oneSentence.setVisibility(oneSentence.VISIBLE);
              //  comment.setVisibility(comment.VISIBLE);
                date.setText(events.get(dateTime).getDate());
                oneSentence.setText(events.get(dateTime).getOneSentence()); //here
                comment.setText(events.get(dateTime).getComment());
            }else{
                date.setVisibility(date.GONE);
                oneSentence.setVisibility(oneSentence.GONE);
                comment.setVisibility(comment.GONE);
                }

            if(events.get(dateTime) == null && events2.get(dateTime) == null){
                today.setVisibility(today.VISIBLE);
                today.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Calendar c1 = Calendar.getInstance();
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        String date_val2 = sdf2.format(c1.getTime());
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.putExtra("date",date_val2);
                        mContext.startActivity(intent);
                    }
                });
            }
            }
            */
        today.setVisibility(today.VISIBLE);
        // exp :  데이터값이 있는 경우 별이 보이게 하는 부분 - 렉시오 디비나 부분
        if(events2.get(dateTime)!=null && events2.get(dateTime).getOneSentence()!=null) {

           // img.getLayoutParams().height = 50;

            img2.setVisibility(View.VISIBLE);

            date2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    // TODO Auto-generated method stub
                    Intent intent = new Intent(mContext, MainActivity.class);
                    String _date = date2.getText().toString();
                    int y1 = _date.indexOf("년");
                    int m1 = _date.indexOf("월 ");
                    int d1 = _date.indexOf("일 ");
                    String year = _date.substring(0,y1);
                    String month = _date.substring(6,m1);
                    String day = _date.substring(m1+2, d1);
                    String thisdate = year+"-"+month+"-"+day;
                    intent.putExtra("date", thisdate);
                    mContext.startActivity(intent);
                }
            });
            if (events.get(dateTime) != null) {
                sentence2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(mContext, MainActivity.class);
                        String _date = null;
                        if(date2.getText().toString() == ""){
                            _date = date.getText().toString();
                        }else{
                            _date = date2.getText().toString();
                        }

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

        }

        // exp :  데이터값이 있는 경우 별이 보이게 하는 부분 - 코멘트 부분
        if(events.get(dateTime)!=null && events.get(dateTime).getOneSentence()!=null){
            img.setVisibility(View.VISIBLE);
            date.setOnClickListener(new View.OnClickListener()
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

         /*   oneSentence.setOnClickListener(new View.OnClickListener()
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
            }); */
            comment.setOnClickListener(new View.OnClickListener()
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

        // exp : 별이 보이는 경우에 클릭 이벤트
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
                    today.setVisibility(today.GONE);
                    // TODO Auto-generated method stub
                    //Toast.makeText(context, "oh..", Toast.LENGTH_SHORT).show();
                    //text.setText(events.get(dateTime).getPlus());


                    if(events.get(dateTime)!=null) {
                        date.setVisibility(date.VISIBLE);
                        oneSentence.setVisibility(oneSentence.VISIBLE);
                        comment.setVisibility(comment.VISIBLE);
                        date.setText(events.get(dateTime).getDate());
                        oneSentence.setText(events.get(dateTime).getOneSentence());
                        comment.setText(events.get(dateTime).getComment());
                    }else{
                        date.setVisibility(date.GONE);
                        oneSentence.setVisibility(oneSentence.GONE);
                        comment.setVisibility(comment.GONE);
                    }
                    if(events2.get(dateTime)!=null) {
                        date2.setVisibility(date2.VISIBLE);
                        sentence2.setVisibility(sentence2.VISIBLE);
                        bg1.setVisibility(bg1.VISIBLE);
                        if(events.get(dateTime)!=null) {
                            date2.setText("");
                            sentence2.setText("렉시오 디비나");
                        }else{
                            date2.setText(events2.get(dateTime).getDate());
                            sentence2.setText(events2.get(dateTime).getOneSentence());
                        }
                        if(events3.get(dateTime) != null){
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
                        }else{
                            bg1.setText(Html.fromHtml("<font color=\"#999999\">· 이 복음의 등장인물은 </font> " + events2.get(dateTime).getBg1()
                                    +"<br><font color=\"#999999\">· 장소는</font> " + events2.get(dateTime).getBg2() +
                                    "<br><font color=\"#999999\">· 시간은</font> " + events2.get(dateTime).getBg3()
                                    +"<br><font color=\"#999999\">· 이 복음의 내용을 간추리면</font> " + events2.get(dateTime).getSum1()
                                    +"<br><font color=\"#999999\">· 특별히 눈에 띄는 부분은</font> " + events2.get(dateTime).getSum2()
                                    +"<br><font color=\"#999999\">· 이 복음에서 보여지는 예수님은</font> " + events2.get(dateTime).getJs1()
                                    +"<br><font color=\"#999999\">· 결과적으로 이 복음을 통해 \n예수님께서 내게 해주시는 말씀은</font> \"" + events2.get(dateTime).getJs2()+"\""));
                        }


               //         bg1.setText(Html.fromHtml("sss"+ "<font color=\"#000000\">" +"ss"+ "</font><br><br>"));
                      /*  bg2.setText("장소는\n" + events2.get(dateTime).getBg2());
                        bg3.setText("시간은\n" + events2.get(dateTime).getBg3());
                        sum1.setText("이 복음의 내용을 간추리면\n" + events2.get(dateTime).getSum1());
                        sum2.setText("특별히 눈에 띄는 부분은\n" + events2.get(dateTime).getSum2());
                        js1.setText("이 복음에서 보여지는 예수님은\n" + events2.get(dateTime).getJs1());
                        js2.setText("결과적으로 이 복음을 통해 \n예수님께서 내게 해주시는 말씀은 \n\"" + events2.get(dateTime).getJs2()+"\""); */
                    }else{
                        date2.setVisibility(date2.GONE);
                        sentence2.setVisibility(sentence2.GONE);
                        bg1.setVisibility(bg1.GONE);
                    }

                }


            });
        //exp : 별이 없는 경우에 클릭이벤트
        } else {

            tv1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, MainActivity.class);
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
                    intent.putExtra("date",dateTime.getYear()+"-"+month+"-"+day);
                    mContext.startActivity(intent);

                }

            });
        }
        setCustomResources(dateTime, cellView, tv1);
        return cellView;
    }

}
