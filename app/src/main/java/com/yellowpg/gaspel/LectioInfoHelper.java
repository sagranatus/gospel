package com.yellowpg.gaspel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Saea on 2017-08-07.
 */


public class LectioInfoHelper extends SQLiteOpenHelper {

    public LectioInfoHelper(Context c) {
        super(c, "LectioInfo.db", null, 1); //데이터베이스 이름.

        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //맨처음 테이블을 생성한다.
        // TODO Auto-generated method stub
        String query ="CREATE TABLE lectio (_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT, bg1 TEXT, bg2 TEXT, bg3 TEXT, sum1 TEXT, sum2 TEXT, js1 TEXT, js2 TEXT, onesentence TEXT)"; //코멘트, 날짜, 주제성구이다.
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        String query = "DROP TABLE IF EXISTS lectio";
        db.execSQL(query);
        onCreate(db);
    }

}
