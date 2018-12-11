package com.yellowpg.gaspel.DB;

public class CommentDBSqlData {
    public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE if not exists comment "+
            "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "uid INTEGER NOT NULL, "
            +"date TEXT NOT NULL, "
            +"onesentence TEXT NOT NULL, "
            +"comment TEXT NOT NULL)";
    public static final String SQL_DB_INSERT_DATA = "INSERT INTO comment "
            +"(uid, date, onesentence, comment)"
            +"VALUES(?, ?, ?, ?)";
    public static final String SQL_DB_UPDATE_DATA = "UPDATE comment " + "SET comment=?"+" WHERE uid=? AND date=?";
    public static final String SQL_DB_SELECT_DATA = "SELECT * " + "FROM comment" +" WHERE uid=? AND date=?";
    public static final String SQL_DB_SELECT_DATA_ALL = "SELECT * " + "FROM comment"+" WHERE uid=?";
    public static final String SQL_DB_DELETE_DATA = "DELETE FROM comment "+"WHERE uid=?";
}