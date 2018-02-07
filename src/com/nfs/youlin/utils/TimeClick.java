package com.nfs.youlin.utils;

public class TimeClick {
	private static long lastClickTime;
	private static long lastClickTime1000;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();   
        if ( time - lastClickTime < 500) {   
            return true;   
        }   
        lastClickTime = time;   
        return false;   //可以点击
    }
    public synchronized static boolean isFastClick1000() {
        long time1000 = System.currentTimeMillis();   
        if ( time1000 - lastClickTime1000 < 1000) {   
            return true;   
        }   
        lastClickTime1000 = time1000;   
        return false;   //可以点击
    }
}
