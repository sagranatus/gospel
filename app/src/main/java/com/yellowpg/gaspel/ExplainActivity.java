package com.yellowpg.gaspel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
// 현재 사용 안함
public class ExplainActivity extends Activity {
    Button title, con1,con2, con3, step1, step2, step3;
    TextView explain1, explain2, explain3;
    LinearLayout ll_step1, ll_step2, ll_step3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);
        step1 = (Button) findViewById(R.id.bt_step1);
        step2 = (Button) findViewById(R.id.bt_step2);
        step3 = (Button) findViewById(R.id.bt_step3);

        ll_step1 = (LinearLayout) findViewById(R.id.ll_step1);
        ll_step2 = (LinearLayout) findViewById(R.id.ll_step2);
        ll_step3 = (LinearLayout) findViewById(R.id.ll_step3);

        title = (Button) findViewById(R.id.bt_title);
        con1 = (Button) findViewById(R.id.bt_con11);
        con2 = (Button) findViewById(R.id.bt_con22);
        con3 = (Button) findViewById(R.id.bt_con33);
        explain1 = (TextView) findViewById(R.id.tv_explain11);
        explain2 = (TextView) findViewById(R.id.tv_explain22);
        explain3 = (TextView) findViewById(R.id.tv_explain33);

        ll_step1.setVisibility(View.VISIBLE);
        step1.setBackgroundColor(Color.parseColor("#210B61"));
        step1.setOnClickListener(listener);
        step2.setOnClickListener(listener);
        step3.setOnClickListener(listener);
        // exp : 텍스트 사이즈 설정
        SharedPreferences sp2 = getSharedPreferences("setting",0);
        String textsize = sp2.getString("textsize", "");
        if(textsize.equals("small")){
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            con1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            explain1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            con2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            explain2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            con3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            explain3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        }else if(textsize.equals("big")){
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            con1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            explain1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            con2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            explain2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
            con3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            explain3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 19);
        }else if(textsize.equals("toobig")){
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
            con1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            explain1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            con2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            explain2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
            con3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            explain3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 21);
        }else{

        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ll_step1.setVisibility(View.GONE);
            ll_step3.setVisibility(View.GONE);
            ll_step2.setVisibility(View.GONE);
            step1.setBackgroundColor(Color.parseColor("#000000"));
            step2.setBackgroundColor(Color.parseColor("#000000"));
            step3.setBackgroundColor(Color.parseColor("#000000"));
            switch (v.getId()) {
                case R.id.bt_step1:
                    step1.setBackgroundColor(Color.parseColor("#210B61"));
                    ll_step1.setVisibility(View.VISIBLE);
                    break;
                case R.id.bt_step2:
                    step2.setBackgroundColor(Color.parseColor("#210B61"));
                    ll_step2.setVisibility(View.VISIBLE);
                    break;
                case R.id.bt_step3:
                    step3.setBackgroundColor(Color.parseColor("#210B61"));
                    ll_step3.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


}
