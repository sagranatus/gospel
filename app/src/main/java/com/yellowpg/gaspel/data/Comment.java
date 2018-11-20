package com.yellowpg.gaspel.data;

import android.widget.Button;

import hirondelle.date4j.DateTime;

public class Comment {
	private String date;
	private String oneSentence;
	private String comment;
	DateTime date_time;

    public Comment(String com, String date, String sentence) {
        this.comment = com;
        this.date = date;
        this.oneSentence = sentence;
    }

	public Comment(DateTime date_time, String date, String sentence, String com){
		this.date_time = date_time;
		this.date = date;
		this.oneSentence = sentence;
		this.comment = com;
	}
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
				this.comment,
				this.date,
				this.oneSentence
		};
		return cData;
	}
	
}

