package com.yellowpg.gaspel.DB;

public class UserDBSqlData {
    public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE if not exists user "+
            "(uid INTEGER NOT NULL," +
            "user_id TEXT NOT NULL, "
            +"email TEXT NOT NULL UNIQUE, "
            +"name TEXT NOT NULL, "
            +"christ_name TEXT NOT NULL, "
            +"age TEXT NOT NULL, "
            +"region TEXT NOT NULL, "
            +"cathedral TEXT NULL, "
            +"created_at TEXT NULL)";
    public static final String SQL_DB_INSERT_DATA = "INSERT INTO user "
            +"(uid, user_id, email, name, christ_name, age, region, cathedral, created_at) "
            +"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_DB_UPDATE_DATA = "UPDATE user " + "SET email=?, name=?, christ_name=?, age=?, region=?, cathedral=?"+" WHERE uid=?";
    public static final String SQL_DB_SELECT_DATA = "SELECT * " + "FROM user" +" WHERE uid=?";
    public static final String SQL_DB_DELETE_DATA = "DELETE FROM user "+"WHERE uid=?";
}
