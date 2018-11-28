package com.yellowpg.gaspel.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yellowpg.gaspel.MainActivity;
import com.yellowpg.gaspel.R;
import com.yellowpg.gaspel.data.MonthRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StatusSaveAdapter extends BaseAdapter{
    private Context mContext = null;
    private int layout = 0;
    private ArrayList<MonthRecord> data = null;
    private LayoutInflater inflater = null;
    private String textsize;
    static Calendar c1 = Calendar.getInstance();
    static String day;
    public StatusSaveAdapter(Context c, int l, ArrayList<MonthRecord> d, String ts){
        this.mContext = c;
        this.layout = l;
        this.data = d;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.textsize = ts;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position).getMonth();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if(convertView == null){
            convertView = inflater.inflate(this.layout, parent, false);
        }
        TextView status = (TextView) convertView.findViewById(R.id.tv_status);
        ImageView walk1 = (ImageView) convertView.findViewById(R.id.walking1);
        ImageView walk2 = (ImageView) convertView.findViewById(R.id.walking2);
        ImageView walk3 = (ImageView) convertView.findViewById(R.id.walking3);
        ImageView walk4 = (ImageView) convertView.findViewById(R.id.walking4);
        ImageView walk5 = (ImageView) convertView.findViewById(R.id.walking5);
        ImageView walk6 = (ImageView) convertView.findViewById(R.id.walking6);
        ImageView walking_person = (ImageView) convertView.findViewById(R.id.walking_person);
        ImageView arrive_person = (ImageView) convertView.findViewById(R.id.arrive_person);
        Button thismonth = (Button) convertView.findViewById(R.id.bt_thismonth) ;

        if(textsize.equals("big")){

        }else{

        }

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd일 ");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String Month = data.get(position).getMonth();
        Date origin_date = null;
        String date_aft = null;
        thismonth.setText(data.get(position).getMonth());
        int point = Integer.parseInt(data.get(position).getComments())+Integer.parseInt(data.get(position).getLectios())*5;
        status.setText("복음 묵상 "+data.get(position).getComments()+"일\n렉시오디비나 "+data.get(position).getLectios()+"일");
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


        if((position%2)==1){
            convertView.setBackgroundColor(0xc0c0c0);
        }else{
            convertView.setBackgroundColor(0xa9a9a9);
        }

        return convertView;

    }

}