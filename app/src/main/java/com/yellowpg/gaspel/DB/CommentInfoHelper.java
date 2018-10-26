package com.yellowpg.gaspel.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

//이 파일은 안드로이드 안에 탑재되어 있는 데이터베이스를 사용하는 방식이다. 
public class CommentInfoHelper extends SQLiteOpenHelper{

	public CommentInfoHelper(Context c) {
		super(c, "CommentInfo.db", null, 1); //데이터베이스 이름.
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) { //맨처음 테이블을 생성한다.
		// TODO Auto-generated method stub
		String query ="CREATE TABLE comment (_id INTEGER PRIMARY KEY AUTOINCREMENT, comment_con TEXT, date TEXT, sentence TEXT)"; //코멘트, 날짜, 주제성구이다.
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String query = "DROP TABLE IF EXITS comment";
		db.execSQL(query);
		onCreate(db);
	}

}
