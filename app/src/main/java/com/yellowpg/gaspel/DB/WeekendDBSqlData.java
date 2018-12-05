package com.yellowpg.gaspel.DB;

public class WeekendDBSqlData {
    public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE weekend "+
            "(reg_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "uid INTEGER NOT NULL, "
            +"date TEXT NOT NULL, "
            +"mysentence TEXT NOT NULL, "
            +"mythought TEXT NOT NULL)";
    public static final String SQL_DB_INSERT_DATA = "INSERT INTO weekend "
            +"(uid, date, mysentence, mythought)"
            +"VALUES(?, ?, ?, ?)";
    public static final String SQL_DB_UPDATE_DATA = "UPDATE weekend " + "SET mysentence=?, mythought=?"+" WHERE uid=? AND date=?";
    public static final String SQL_DB_SELECT_DATA = "SELECT * " + "FROM weekend" +" WHERE uid=? AND date=?";
    public static final String SQL_DB_SELECT_DATA_ALL = "SELECT * " + "FROM weekend"+" WHERE uid=?";
    public static final String SQL_DB_DELETE_DATA = "DELETE FROM weekend "+"WHERE uid=?";
}