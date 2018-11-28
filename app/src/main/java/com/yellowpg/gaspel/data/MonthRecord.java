package com.yellowpg.gaspel.data;


public class MonthRecord {
    private String month;
    private String comments;
    private String lectios;

    public MonthRecord(String month, String comments, String lectios){
        this.month = month;
        this.comments = comments;
        this.lectios = lectios;
    }

    public String getMonth(){
        return month;
    }
    public String getComments(){
        return comments;
    }

    public String getLectios(){
        return lectios;
    }

}