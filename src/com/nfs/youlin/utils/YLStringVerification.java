package com.nfs.youlin.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

public class YLStringVerification {
	@SuppressWarnings("unused")
	private Context context;
	
	public YLStringVerification(Context context){
		this.context = context;
	}
	
	public boolean checkPhoneNumFormat(String pStr){ 
		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(16[0-9])|(14[0-9]))\\d{8}$");  
		Matcher m = p.matcher(pStr);  
		return m.matches();
	}
	public boolean checkPhoneNumFormat86(String pStr){ 
		Pattern p = Pattern.compile("^((\\+86)|(86))?((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(16[0-9])|(14[0-9]))\\d{8}$");  
		Matcher m = p.matcher(pStr);  
		return m.matches();
	}
	public boolean checkPwdFormat(String pStr){
		Pattern p = Pattern.compile("[\\w\\W]{6,16}");  
		Matcher m = p.matcher(pStr);
		return m.matches();
	}
	
	public boolean checkNickNameFormat(String pStr){
		String regex = "[^%$&]{1,}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(pStr);
		return m.matches();
	}
	private static String reg ="^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){2,}|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";
	public static boolean checkNickNameBiaoQing(String pStr){
		Pattern pattern=Pattern.compile(reg);
		Matcher matcher=pattern.matcher(pStr);
		return matcher.matches();
	}
	
	public static String cancelSpaceWithString(String text){
		Pattern p = Pattern.compile("//s*|/t|/r|/n");  
		Matcher m = p.matcher(text);   
		return m.replaceAll(""); 
	}
}
