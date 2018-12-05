package com.yellowpg.gaspel.data;

import android.widget.Button;

import hirondelle.date4j.DateTime;

public class Comment {
	private String uid;
	private String date;
	private String oneSentence;
	private String comment;
	DateTime date_time;

    public Comment(String uid, String date, String onesentence, String comment) {
    	this.uid = uid;
		this.oneSentence = onesentence;
		this.date = date;
        this.comment = comment;
    }

	public Comment(String uid, DateTime date_time, String date, String onesentence, String comment){
    	this.uid = uid;
		this.date_time = date_time;
		this.date = date;
		this.oneSentence = onesentence;
		this.comment = comment;
	}
	public String getUid(){return  uid;}
	public String getDate(){
		return date;
	}
	public String getOneSentence(){
		return oneSentence;
	}
	public String getComment(){
		return comment;
	}

	public String[] getcDataArray(){
		String[] cData = {
				this.uid,
				this.date,
				this.oneSentence,
				this.comment
		};
		return cData;
	}
	
}

