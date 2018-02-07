package com.nfs.youlin.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SMSVCode {
	//private final String APPKEY = "955eeb564356".trim();
	private final String APPKEY = "d3f836c7d14c";
	//private final String APPSECRET = "f66549e965558edce18d30398864f66c".trim();
	private final String APPSECRET = "203b2509d7f89a3a97bb44ee489f5f38";
	public SMSVCode(Context context, final Handler handler, EventHandler eventHandler){
		SMSSDK.initSDK(context, APPKEY, APPSECRET);
		eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Loger.i("TEST","data==>"+(String)data.toString() +" "+event+" "+result);
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		SMSSDK.registerEventHandler(eventHandler);
	}
}
