package com.nfs.youlin.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;


public class CommonTools {
	private Context context;
	
	public CommonTools(Context context){
		this.context = context;
	}
	
	public static void goBack(){
		new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
				} catch (Exception e) {
					Loger.e("Exception when onBack", e.toString());
				}
			}
		}.start(); 
	}
	
	public void delTargetFile(String filePath, String fileName){
		File file= new File(filePath, fileName);
		if(file.exists()){
			file.delete();
		}
	}
	
	public static String getFormatDate(long time){
		Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
	}
	
	public static int genErageRadom(){
		String[] strContent = new String[]{
				"1","2", "3", "4", "5", "6", "7", "8", "9"};
		StringBuilder str = new StringBuilder();
		// 随机串的个数
		int count = strContent.length;
		// 生成4个随机数
		Random random = new Random();
		int randomResFirst  = random.nextInt(count);
		int randomResSecond = random.nextInt(count);
		int randomResThird  = random.nextInt(count);
		int randomResFourth = random.nextInt(count);
		int randomResFifth  = random.nextInt(count);
		int randomResSixth  = random.nextInt(count);
		str.append(strContent[randomResFirst].toString().trim());
		str.append(strContent[randomResSecond].toString().trim());
		str.append(strContent[randomResThird].toString().trim());
		str.append(strContent[randomResFourth].toString().trim());
		str.append(strContent[randomResFifth].toString().trim());
		str.append(strContent[randomResSixth].toString().trim());

		return Integer.parseInt(str.toString());
	}
	
	public static long genFamilyIdRadom(){
		String[] strContent = new String[]{
				"1","2", "3", "4", "5", "6", "7", "8", "9"};
		StringBuilder str = new StringBuilder();
		// 随机串的个数
		int count = strContent.length;
		// 生成4个随机数
		Random random = new Random();
		int randomResFirst  = random.nextInt(count);
		int randomResSecond = random.nextInt(count);
		int randomResThird  = random.nextInt(count);
		int randomResFourth = random.nextInt(count);
		int randomResFifth  = random.nextInt(count);
		int randomResSixth  = random.nextInt(count);
		int randomResFirst1  = random.nextInt(count);
		int randomResSecond1 = random.nextInt(count);
		int randomResThird1  = random.nextInt(count);
		int randomResFourth1 = random.nextInt(count);
		int randomResFifth1  = random.nextInt(count);
		int randomResSixth1  = random.nextInt(count);
		str.append(strContent[randomResFirst].toString().trim());
		str.append(strContent[randomResSecond].toString().trim());
		str.append(strContent[randomResThird].toString().trim());
		str.append(strContent[randomResFourth].toString().trim());
		str.append(strContent[randomResFifth].toString().trim());
		str.append(strContent[randomResSixth].toString().trim());
		str.append(strContent[randomResFirst1].toString().trim());
		str.append(strContent[randomResSecond1].toString().trim());
		return Integer.parseInt(str.toString());
	}
}
