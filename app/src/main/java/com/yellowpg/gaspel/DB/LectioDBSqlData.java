package com.yellowpg.gaspel.DB;

public class LectioDBSqlData {
    public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE if not exists lectio "+
            "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "uid INTEGER NOT NULL, "
            +"date TEXT NOT NULL, "
            +"onesentence TEXT NOT NULL, "
            +"bg1 TEXT NULL, "
            +"bg2 TEXT NULL, "
            +"bg3 TEXT NULL, "
            +"sum1 TEXT NULL, "
            +"sum2 TEXT NULL, "
            +"js1 TEXT NULL, "
            +"js2 TEXT NULL)";
    public static final String SQL_DB_INSERT_DATA = "INSERT INTO lectio "
            +"(uid, date, onesentence, bg1, bg2, bg3, sum1, sum2, js1, js2)"
            +"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_DB_UPDATE_DATA = "UPDATE lectio " + "SET bg1=?, bg2=?, bg3=?, sum1=?, sum2=?, js1=?, js2=?"+" WHERE uid=? AND date=?";
    public static final String SQL_DB_SELECT_DATA = "SELECT * " + "FROM lectio" +" WHERE uid=? AND date=?";
    public static final String SQL_DB_SELECT_DATA_ALL = "SELECT * " + "FROM lectio"+" WHERE uid=?";
    public static final String SQL_DB_DELETE_DATA = "DELETE FROM lectio "+"WHERE uid=?";
}
