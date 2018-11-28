package com.yellowpg.gaspel.data;

import hirondelle.date4j.DateTime;

public class Weekend {
    private String date;
    private String mySentence;
    private String myThought;
    DateTime date_time;

    public Weekend(String adate, String amySentence, String amyThought) {
        this.date = adate;
        this.mySentence = amySentence;
        this.myThought = amyThought;
    }

    public Weekend(DateTime date_time, String adate, String amySentence, String amyThought) {
        this.date_time = date_time;
        this.date = adate;
        this.mySentence = amySentence;
        this.myThought = amyThought;
    }

    public String getDate(){
        return date;
    }
    public String getMySentence(){
        return mySentence;
    }
    public String getMyThought(){
        return myThought;
    }

    public String[] getcDataArray(){
        String[] cData = {
                this.date,
                this.mySentence,
                this.myThought
        };
        return cData;
    }

}
