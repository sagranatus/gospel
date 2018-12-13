package com.yellowpg.gaspel.DB;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yellowpg.gaspel.data.Comment;
import com.yellowpg.gaspel.data.Lectio;
import com.yellowpg.gaspel.data.UserData;
import com.yellowpg.gaspel.data.Weekend;

import java.util.ArrayList;

public class DBManager {
    private final String DB_NAME = "gaspel.db";
    private final int DB_VERSION = 1;

    private Context mContext = null;
    private OpenHelper mOpener = null;
    private SQLiteDatabase mDbController = null;

    class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        // 가장 맨 처음에 만들어 지는 거 같다. 그 이후에는 불러와지지 않는 거 같다.
        @Override
        public void onCreate(SQLiteDatabase aDb) {
            aDb.execSQL(UsersDBSqlData.SQL_DB_CREATE_TABLE);
            aDb.execSQL(CommentDBSqlData.SQL_DB_CREATE_TABLE);
            aDb.execSQL(LectioDBSqlData.SQL_DB_CREATE_TABLE);
            aDb.execSQL(WeekendDBSqlData.SQL_DB_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public DBManager(Context aContext){
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
    // 로그인시에 user있으면 삭제, users 없으면 생성
    public void firstUserData(){
        mDbController.execSQL("DROP TABLE IF EXISTS user");
        mDbController.execSQL(UsersDBSqlData.SQL_DB_CREATE_TABLE); // 만약 users 테이블이 없는 경우에는 억지로라도 생성해야함
    }
    public void insertUserData(String aSql, UserData aCData) {
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
                    results.getString(6),
                    results.getString(7),
                    results.getString(8));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }
    public void updateUserData(String aSql, String[] info) {
        String[] sqlData = info;
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void deleteUserData(String aSql, String uid) {
        String[] sqlData = {uid};
        this.mDbController.execSQL(aSql, sqlData);
    }


    // comment data
    public void insertCommentData(String aSql, Comment aCData) {
        String[] sqlData = aCData.getcDataArray();
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void selectCommentData(String aSql, String uid, String date, ArrayList<Comment> aCDataList){
        String[] sqlData = {uid, date};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            Comment cData = new Comment(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }

    public void selectCommentAllData(String aSql, String uid, ArrayList<Comment> aCDataList){
        String[] sqlData = {uid};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            Comment cData = new Comment(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }
    public void updateCommentData(String aSql, String uid, String date, String comment) { // comment만
        String[] sqlData = {comment, uid, date};
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void deleteCommentData(String aSql, String uid) {
        String[] sqlData = {uid};
        this.mDbController.execSQL(aSql, sqlData);
    }


    // lectio data
    public void insertLectioData(String aSql, Lectio aCData) {
        String[] sqlData = aCData.getcDataArray();
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void selectLectioData(String aSql, String uid, String date, ArrayList<Lectio> aCDataList){
        String[] sqlData = {uid, date};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            Lectio cData = new Lectio(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6),
                    results.getString(7),
                    results.getString(8),
                    results.getString(9),
                    results.getString(10));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }

    public void selectLectioAllData(String aSql, String uid, ArrayList<Lectio> aCDataList){
        String[] sqlData = {uid};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            Lectio cData = new Lectio(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getString(6),
                    results.getString(7),
                    results.getString(8),
                    results.getString(9),
                    results.getString(10));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }
    public void updateLectioData(String aSql, String uid, String date, String bg1, String bg2, String bg3, String sum1, String sum2, String js1, String js2) { // comment만
        String[] sqlData = {bg1, bg2, bg3, sum1, sum2, js1, js2, uid, date};
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void deleteLectioData(String aSql, String uid) {
        String[] sqlData = {uid};
        this.mDbController.execSQL(aSql, sqlData);
    }


    // weekend data
    public void insertWeekendData(String aSql, Weekend aCData) {
        String[] sqlData = aCData.getcDataArray();
        this.mDbController.execSQL(aSql, sqlData);
    }

    public void selectWeekendData(String aSql, String uid, String date, ArrayList<Weekend> aCDataList){
        String[] sqlData = {uid, date};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            Weekend cData = new Weekend(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }

    public void selectWeekendAllData(String aSql, String uid, ArrayList<Weekend> aCDataList){
        String[] sqlData = {uid};
        Cursor results = this.mDbController.rawQuery(aSql, sqlData);
        results.moveToNext();
        while(!results.isAfterLast()){
            Weekend cData = new Weekend(
                    results.getString(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4));
            aCDataList.add(cData);
            results.moveToNext();
        }
        results.close();
    }
    public void updateWeekendData(String aSql, String uid, String date, String mysentence, String mythought) {
        String[] sqlData = {mysentence, mythought, uid, date};
        this.mDbController.execSQL(aSql, sqlData);
    }
    public void deleteWeekendData(String aSql, String uid) {
        String[] sqlData = {uid};
        this.mDbController.execSQL(aSql, sqlData);
    }

}
