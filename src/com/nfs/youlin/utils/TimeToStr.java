package com.nfs.youlin.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class TimeToStr {
	
	private static final long seconds_of_1minute = 60*1000;
	private static final long seconds_of_30minutes = 30*60*1000;
	private static final long seconds_of_1hour = 60*60*1000;
	private static final long seconds_of_1day = 24*60*60*1000;
	private static final long seconds_of_15days = seconds_of_1day*15;
	private static final long seconds_of_30days = seconds_of_1day*30;
	private static final long seconds_of_6months = seconds_of_30days*6;
	private static final long seconds_of_1year = seconds_of_30days*12;
	
	public static String getTimeToStr(Long time){
		Date date=new Date(time);
		SimpleDateFormat dateFormat=new SimpleDateFormat("MM月dd日 HH:mm");
		return dateFormat.format(date);
	}
	public static String getTimeToStrInvate(Long time){
		Date date=new Date(time);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yy-MM-dd\nHH:mm:ss");
		return dateFormat.format(date);
	}
	public static String getDateToString(Long time){
		Date date=new Date(time);
		SimpleDateFormat dateFormat=new SimpleDateFormat("MM月dd日");
		return dateFormat.format(date);
	}
	public static String getActivityTimeToStr(Long time){
		Date date=new Date(time);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		return dateFormat.format(date);
	}
	public static String getActivityTimeToStr2(Long time){
		Date date=new Date(time);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
		return dateFormat.format(date);
	}
	public static String getTimeElapse(long createTime, long systemTime) {
		long elapsedTime = systemTime - createTime;
		if (elapsedTime < seconds_of_1minute) {
			return "刚刚";
		}
		if (elapsedTime < seconds_of_30minutes) {
			return elapsedTime / seconds_of_1minute + "分钟前";
		}
		if (elapsedTime < seconds_of_1hour) {
			return "半小时前";
		}
		if (elapsedTime < seconds_of_1day) {
			return elapsedTime / seconds_of_1hour + "小时前";
		}
		if (elapsedTime < seconds_of_15days) {
			return elapsedTime / seconds_of_1day + "天前";
		}
		if (elapsedTime < seconds_of_30days) {
			return "半个月前";
		}
		if (elapsedTime < seconds_of_6months) {
			return elapsedTime / seconds_of_30days + "月前";
		}
		if (elapsedTime < seconds_of_1year) {
			return "半年前";
		}
		if (elapsedTime >= seconds_of_1year) {
			return elapsedTime / seconds_of_1year + "年前";
		}
		return "";
	}
}
