package com.nfs.youlin.utils;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;

import android.content.Context;
import android.util.Log;

public class EasemobHandler {
	public static boolean bCheckUserLogin = false;
	
	private Context context;
	
	public EasemobHandler(Context context){
		this.context = context;
	}
	
	public void userLogin(String loginId){
		EMChat.getInstance().init(this.context);
		EMChatManager.getInstance().login(loginId, loginId, new EMCallBack() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				EMChatManager.getInstance().loadAllConversations();
				
				//成功登录
			}
			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void userLongon(){
		DemoApplication.hxSDKHelper.logout(new EMCallBack() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				DemoApplication.hxSDKHelper.setContactList(null);
				DemoApplication.hxSDKHelper.setRobotList(null);
				DemoApplication.hxSDKHelper.getModel().closeDB();
				Loger.i("TEST", "环信登出成功");
			}
			@Override
			public void onProgress(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Loger.i("TEST", "环信登出失败");
			}
		});
	}
}
