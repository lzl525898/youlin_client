package com.easemob.chatuidemo.receiver;


import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.activity.VideoCallActivity;
import com.easemob.chatuidemo.activity.VoiceCallActivity;
import com.easemob.chatuidemo.utils.Loger;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EasyUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStatReceiver extends BroadcastReceiver{   
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
        CallListener customphonelistener = new CallListener(context);
        tm.listen(customphonelistener, PhoneStateListener.LISTEN_CALL_STATE);
    }   
    public class CallListener extends PhoneStateListener{
        public CallListener(Context context){
            super();
        }
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // TODO Auto-generated method stub
            super.onCallStateChanged(state, incomingNumber);
            if(state == TelephonyManager.CALL_STATE_RINGING){
                Loger.d("TEST","来电话了！！！！！");
                boolean bStatus = false;
                try {
                    //VideoCallActivity.VideoCallContext.getPackageName();
                        try {
                            EMChatManager.getInstance().endCall();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(VoiceCallActivity.VoiceCallContext!=null){
                            ((Activity) VoiceCallActivity.VoiceCallContext).finish();
                        }
                        if(VideoCallActivity.videoContex!=null){
                            ((Activity) VideoCallActivity.videoContex).finish();
                        }
                        //bStatus = EasyUtils.isAppRunningForeground(VoiceCallActivity.VoiceCallContext);
                    
                } catch (Exception e) {
                    Loger.i("TEST","VideoCall-onCallStateChanged-Err==>"+e.getMessage());
                }
//                if(bStatus){
//                    Loger.d("TEST","VideoCall 前台！");
//                    if(VoiceCallActivity.VoiceCallContext!=null){
//                       
//                    }
//                }else{
//                    Loger.d("TEST","VideoCall 后台！");
//                }
            }
        }
    }
}
