package com.yellowpg.gaspel.etc;

import android.content.Context;

import java.util.Calendar;

public class getDay {
    Calendar c1;
    public static String getDay(Calendar c1) {
        int dayNum = c1.get(Calendar.DAY_OF_WEEK);
        String day = "";
        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }
        return day;
    }

}
