package com.yellowpg.gaspel.DB;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.UserData;

import java.util.ArrayList;

public class DBManager_Comment {
    private final String DB_NAME = "CommentInfo.db";
    private final int DB_VERSION = 1;

    private Context mContext = null;
    private OpenHelper mOpener = null;
    private SQLiteDatabase mDbController = null;

    class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase aDb) {
            aDb.execSQL(UserDBSqlData.SQL_DB_CREATE_TABLE);
            aDb.execSQL(CommentDBSqlData.SQL_DB_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public DBManager_Comment(Context aContext){
        this.mContext = aContext;
        this.mOpener = new OpenHelper(mContext, DB_NAME, null, DB_VERSION);
    }

    public void dbOpen(){
        this.mDbController = mOpener.getWritableDatabase();
    }
    public void dbClose(){
        this.mDbController.close();
    }


    //user data
    public void insertUserData(String aSql, UserData aCData) {
        //    mDbController.execSQL("DROP TABLE IF EXISTS user");
        //   mDbController.execSQL("delete from user");
        String[] sqlData = aCData.getcDataArray();
        this.mDbController.execSQL(aSql, sqlData);


    }

    public void selectUserData(String aSql, String uid, ArrayList<UserData> aCDataList){
        String[] sqlData = {uid};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            UserData cData = new UserData(
                    results.getString(0),
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }
    public void updateUserData(String aSql, String[] info) {
        //    mDbController.execSQL("DROP TABLE IF EXISTS user");
        //   mDbController.execSQL("delete from user");
        String[] sqlData = info;
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void deleteUserData(String aSql, String uid) {
        String[] sqlData = {uid};
        this.mDbController.execSQL(aSql, sqlData);
    }



    // comment data
    //user data
    public void insertCommentData(String aSql, Comment aCData) {
        //    mDbController.execSQL("DROP TABLE IF EXISTS user");
        //   mDbController.execSQL("delete from user");
        String[] sqlData = aCData.getcDataArray();
        this.mDbController.execSQL(aSql, sqlData);


    }

    public void selectCommentData(String aSql, String uid, ArrayList<Comment> aCDataList){
        String[] sqlData = {uid};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            Comment cData = new Comment(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }

    public void selectCommentAllData(String aSql, ArrayList<Comment> aCDataList){
        Cursor results = this.mDbController.rawQuery(aSql, null);
        results.moveToNext();
        while(!results.isAfterLast()){
            Comment cData = new Comment(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }
    public void updateCommentData(String aSql, String[] info) {
        //    mDbController.execSQL("DROP TABLE IF EXISTS user");awd
        //   mDbController.execSQL("delete from user");
        String[] sqlData = info;
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void deleteCommentData(String aSql, String uid) {
        String[] sqlData = {uid};
        this.mDbController.execSQL(aSql, sqlData);
    }


}
