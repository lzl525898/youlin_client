package com.nfs.youlin.utils;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;

public class StatusChangeutils {
	private static StatusChangeListener listener;
	private static Map<String, StatusChangeListener> listenerlist = new HashMap<String, StatusChangeListener>();
	public void statuschangelistener(String describe,StatusChangeListener change){
		listenerlist.put(describe, change);
//		listener = change;
	}
	public void setstatuschange(String describe,int status){
//		listener.setstatuschanged(status);
		try {
			listenerlist.get(describe).setstatuschanged(status);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public void setstatuschange(String describe,int status,Bundle data){
//		listener.setstatuschanged(status);
		try {
			listenerlist.get(describe).setstatuschanged(status,data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
