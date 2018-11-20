package com.yellowpg.gaspel.DB;

public class CommentDBSqlData {
    public static final String SQL_DB_CREATE_TABLE = "CREATE TABLE comment "+
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"comment_con TEXT NOT NULL, "
            +"date TEXT NOT NULL, "
            +"sentence TEXT NOT NULL)";
    public static final String SQL_DB_INSERT_DATA = "INSERT INTO comment "
            +"(comment_con, date, sentence)"
            +"VALUES(?, ?, ?)";
    public static final String SQL_DB_UPDATE_DATA = "UPDATE comment " + "SET comment_con=?"+" WHERE date=?";
    public static final String SQL_DB_SELECT_DATA = "SELECT * " + "FROM comment" +" WHERE date=?";
    public static final String SQL_DB_SELECT_DATA_ALL = "SELECT * " + "FROM comment";
    public static final String SQL_DB_DELETE_DATA = "DELETE FROM comment "+"WHERE date=?";
}
