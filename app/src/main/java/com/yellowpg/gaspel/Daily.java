package com.yellowpg.gaspel;

public class Daily {
	private String date;
	private String oneSentence;
	private String dateInfo;
	
	public Daily(String date, String oneSentence, String dateinfo){
		this.date = date;
		this.oneSentence = oneSentence;
		this.dateInfo = dateinfo;
	}

	public String getDate(){
		return date;
	}
	public String getOneSentence(){
		return oneSentence;
	}

	public String getdateInfo(){
		return dateInfo;
	}
	
}

