package com.yellowpg.gaspel.data;


public class MonthRecord {
    private String month;
    private String comments;
    private String lectios;
    private int point;

    public MonthRecord(String month, String comments, String lectios, int point){
        this.month = month;
        this.comments = comments;
        this.lectios = lectios;
        this.point = point;
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
    public int getPoint(){
        return point;
    }

}