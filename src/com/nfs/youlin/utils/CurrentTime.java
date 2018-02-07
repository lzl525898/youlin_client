package com.nfs.youlin.utils;

import java.text.SimpleDateFormat;

import android.util.Log;

public class CurrentTime {
	public String getCurTime(){
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String time=dateFormat.format(new java.util.Date());
		Loger.i("youlin","time--->"+time);
		return time;
	}
}
