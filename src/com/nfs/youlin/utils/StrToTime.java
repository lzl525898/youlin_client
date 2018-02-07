package com.nfs.youlin.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.provider.ContactsContract.Data;
import android.util.Log;

public class StrToTime {
	public long getTime(String time){
//		String reTime=null;
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy年MM月dd日");
		Date data=new Date();
		try {
			data=dateFormat.parse(time);
//			long lTime=d.getTime();
//			String str=String.valueOf(lTime);
//			reTime=str.substring(0,10);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return data.getTime();
	}
	
	public static long getTimeActivity(String time){
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy年MM月dd日hh时mm分");
		Date data=new Date();
		try {
			data=dateFormat.parse(time);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return data.getTime();
	}
}
