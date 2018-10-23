package com.yellowpg.gaspel.etc;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class Fonttype {
    public static Typeface Billabong;
    public static Typeface Papyrus;
    public static Typeface Ppikke;
    public static Typeface tvN;
    public static void setFont(String typeface, Context context, TextView textView){
        Billabong = Typeface.createFromAsset(context.getAssets(), "Billabong/Billabong.ttf");

        if(typeface.equals("Billabong")){
            textView.setTypeface(Billabong);
        }else if(typeface.equals("Papyrus")){
            textView.setTypeface(Papyrus);
        }else if(typeface.equals("Ppikke")){
            textView.setTypeface(Ppikke);
        }else if(typeface.equals("tvN")){
            textView.setTypeface(tvN);
        }

    }


}
