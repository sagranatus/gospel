package com.yellowpg.gaspel;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DailyAdapter extends BaseAdapter{
	private Context mContext = null;
	private int layout = 0;
	private ArrayList<Daily> data = null;
	private LayoutInflater inflater = null;
	private String textsize;
	static Calendar c1 = Calendar.getInstance();
	static String day;
	public DailyAdapter(Context c, int l, ArrayList<Daily> d, String ts){
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
		return data.get(position).getDate();
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

		final TextView tv_date = (TextView) convertView.findViewById(R.id.tv_date);
		TextView tv_oneSentence = (TextView) convertView.findViewById(R.id.tv_oneSentence);
		final Button bt_dateinfo = (Button) convertView.findViewById(R.id.bt_dateinfo);

		if(textsize.equals("small")){
			tv_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			tv_oneSentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			bt_dateinfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);


		}else if(textsize.equals("big")){
			tv_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			tv_oneSentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
			bt_dateinfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
		}else if(textsize.equals("toobig")){
			tv_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			tv_oneSentence.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
			bt_dateinfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
		}else{

		}
		bt_dateinfo.setVisibility(bt_dateinfo.GONE); //이 버튼은 아예 안보이게 한다.


		SimpleDateFormat sdf1 = new SimpleDateFormat("dd일 ");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String date = data.get(position).getDate();
		Date origin_date = null;
		String date_aft = null;
		try {

			origin_date = sdf2.parse(date);
			c1.setTime(origin_date);
			date_aft = sdf1.format(origin_date) + getDay() + "요일";
		} catch (ParseException e) {
			e.printStackTrace();
		}


		tv_date.setText(date_aft);
		tv_oneSentence.setText(data.get(position).getOneSentence());
		bt_dateinfo.setText(data.get(position).getdateInfo());

		if((position%2)==1){
			convertView.setBackgroundColor(0xc0c0c0);
		}else{
			convertView.setBackgroundColor(0xa9a9a9);
		}
		//progressbar.setVisibility(View.GONE);

		//날짜 혹은 주제성구를 누르면 그날의 복음이 자세히 나오도록 이동한다. (mainactivity로 이동)
		tv_oneSentence.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, MainActivity.class);
				intent.putExtra("date",bt_dateinfo.getText()); //intent의 date값으로 YYYY-MM-DD를 전달한다.
				mContext.startActivity(intent);
			}
		});

		tv_date.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, MainActivity.class);
				intent.putExtra("date",bt_dateinfo.getText());
				mContext.startActivity(intent);
			}
		});


		return convertView;

	}

	// exp : 요일 얻어오기
	public static String getDay(){
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
