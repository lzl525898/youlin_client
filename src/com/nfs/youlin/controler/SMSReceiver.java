/*
 * 官网地站:http://www.mob.com
 * �?术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第�?时间通过微信将版本更新内容推送给您�?�如果使用过程中有任何问题，也可以�?�过微信与我们取得联系，我们将会�?24小时内给予回复）
 *
 * Copyright (c) 20com.example.smsforme All rights reserved.
 */
package com.nfs.youlin.controler;

import com.nfs.youlin.activity.VerificationActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import cn.smssdk.SMSSDK;
/** 短信监听接收器，用于自动获取短信验证码，然后自动填写到验证码区域*/
public class SMSReceiver extends BroadcastReceiver {

	private static final String ACTION_SMS_RECEIVER = "android.provider.Telephony.SMS_RECEIVED";

	private SMSSDK.VerifyCodeReadListener listener;
	public SMSReceiver(SMSSDK.VerifyCodeReadListener verifyCodeReadListener) {
		this.listener = verifyCodeReadListener;
	}

	public SMSReceiver() {
//		String msg = "Please dynamically register an instance of this class with Context.registerReceiver."
//				+"\r\nIf not, the SMSSDK.VerifyCodeReadListener will be null!";
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(ACTION_SMS_RECEIVER.equals(intent.getAction())) {
			Bundle bundle = intent.getExtras();
			if(bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] smsArr = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                	smsArr[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
//                for(SmsMessage message : smsArr){
//                	String messageBody = message.getMessageBody();
//                	String sender = message.getOriginatingAddress();
//                	int length = messageBody.length();
//                	try {
//                		 = messageBody.substring(length-4, length);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//                }
                for (SmsMessage sms: smsArr) {
                	if(sms != null) {
                		SMSSDK.readVerificationCode(sms, listener);
                	}
				}
			}
		}
	}
}
