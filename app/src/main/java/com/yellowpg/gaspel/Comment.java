package com.yellowpg.gaspel;

import hirondelle.date4j.DateTime;

public class Comment {
	private String date;
	private String oneSentence;
	private String comment;
	DateTime date_time;
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

	
}

