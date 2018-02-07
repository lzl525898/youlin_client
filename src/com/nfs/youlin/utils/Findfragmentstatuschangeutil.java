package com.nfs.youlin.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Findfragmentstatuschangeutil {
	private static StatusChangeListener listener;
	private static List<StatusChangeListener> listenerlist = new ArrayList<StatusChangeListener>();
	public void statuschangelistener(StatusChangeListener change){
		listenerlist.add(change);
//		listener = change;
	}
	public void setstatuschange(String describe,int status){
//		listener.setstatuschanged(status);
		for(int i=0;i<listenerlist.size();i++){
			listenerlist.get(i).setstatuschanged(status);
		}
	}
}
