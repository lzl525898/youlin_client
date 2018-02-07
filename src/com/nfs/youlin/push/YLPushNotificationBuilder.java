package com.nfs.youlin.push;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.Loger;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.util.Log;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class YLPushNotificationBuilder {
	private BasicPushNotificationBuilder builder;
	private Context context;
	public YLPushNotificationBuilder(Context context) {
		this.context=context;
		builder = new BasicPushNotificationBuilder(context);
	}
	public  void setNotificationBuilderStyle(Boolean voiceBool,Boolean vibratebool){
		builder.statusBarDrawable = R.drawable.youlin;
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为自动消失
		if(!voiceBool&&!vibratebool){
			
		}
		if(voiceBool==true&&vibratebool==false){
			Loger.i("TEST", "有声音");
			builder.notificationDefaults = Notification.DEFAULT_SOUND;
		}
		if(voiceBool==false&&vibratebool==true){
			Loger.i("TEST", "有震动");
			builder.notificationDefaults = Notification.DEFAULT_VIBRATE;
		}
		if(voiceBool&&vibratebool){
			Loger.i("TEST", "有声音+震动");
			builder.notificationDefaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
		}
		try {
			Loger.i("TEST", "开始修改自定义通知样式");
			JPushInterface.setDefaultPushNotificationBuilder(builder);
		} catch (Exception e) {
			Loger.i("TEST", "BasicPushNotificationBuilder-ERROR:"+e.getMessage());
			e.printStackTrace();
		}
	}
}
