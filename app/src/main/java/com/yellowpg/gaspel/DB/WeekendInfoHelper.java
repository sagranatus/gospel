package com.yellowpg.gaspel.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Saea on 2017-08-07.
 */


public class WeekendInfoHelper extends SQLiteOpenHelper {

    public WeekendInfoHelper(Context c) {
        super(c, "WeekendInfo.db", null, 1); //데이터베이스 이름.

        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //맨처음 테이블을 생성한다.
        // TODO Auto-generated method stub
        String query ="CREATE TABLE weekend (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT, date TEXT, mysentence TEXT, mythought TEXT)"; // 선택구절, 나의묵상.
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        String query = "DROP TABLE IF EXISTS weekend";
        db.execSQL(query);
        onCreate(db);
    }

}
