package com.yellowpg.gaspel.data;

import hirondelle.date4j.DateTime;

public class Lectio {
    private String uid;
    private String date;
    private String oneSentence;
    private String bg1, bg2, bg3, sum1, sum2, js1, js2;
    DateTime date_time;

    public Lectio(String uid, String date, String sentence, String bg1, String bg2, String bg3, String sum1, String sum2, String js1, String js2){
        this.uid = uid;
        this.date = date;
        this.oneSentence = sentence;
        this.bg1 = bg1;
        this.bg2 = bg2;
        this.bg3 = bg3;
        this.sum1 = sum1;
        this.sum2 = sum2;
        this.js1 = js1;
        this.js2 = js2;
    }

    public Lectio(String uid, DateTime date_time, String date, String sentence, String bg1, String bg2, String bg3, String sum1, String sum2, String js1, String js2){
        this.uid = uid;
        this.date_time = date_time;
        this.date = date;
        this.oneSentence = sentence;
        this.bg1 = bg1;
        this.bg2 = bg2;
        this.bg3 = bg3;
        this.sum1 = sum1;
        this.sum2 = sum2;
        this.js1 = js1;
        this.js2 = js2;

    }

    public  String getUid(){ return uid; }
    public String getDate(){
        return date;
    }
    public String getOneSentence(){
        return oneSentence;
    }
    public String getBg1(){
        return bg1;
    }
    public String getBg2(){
        return bg2;
    }
    public String getBg3(){
        return bg3;
    }
    public String getSum1(){
        return sum1;
    }
    public String getSum2(){
        return sum2;
    }
    public String getJs1(){
        return js1;
    }
    public String getJs2(){
        return js2;
    }

    public String[] getcDataArray(){
        String[] cData = {
                this.uid,
                this.date,
                this.oneSentence,
                this.bg1,
                this.bg2,
                this.bg3,
                this.sum1,
                this.sum2,
                this.js1,
                this.js2
        };
        return cData;
    }

}

