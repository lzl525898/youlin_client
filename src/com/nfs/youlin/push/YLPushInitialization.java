package com.nfs.youlin.push;

import java.util.HashSet;
import java.util.Set;

import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

@SuppressLint("HandlerLeak")
public class YLPushInitialization {
	
	private final String TAG = "TEST";
	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;
	
	public static void InitPush(Context context){
		JPushInterface.init(context);
		JPushInterface.setLatestNotificationNumber(context, 1);
	}
	
	public static void stopPush(Context context){
		JPushInterface.stopPush(context);
	}
	
	public static boolean isPushStop(Context context){
		return JPushInterface.isPushStopped(context);
	}
	
	public static void resumePush(Context context){
		JPushInterface.resumePush(context);
	}
	
	public static void SharePrefrence(Context context,String alias){
		if(null!=alias){
			Editor sharedata = context.getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
			sharedata.putInt("aliasStatus", 1);
			sharedata.commit();
		}
	}
	
	public static int GetSharePrefrence(Context context){
		SharedPreferences sharedata = context.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getInt("aliasStatus", 0);
	}
	
	private Context context;
	
	public YLPushInitialization(Context context){
		this.context = context;
	}
	
	public void InitAlias(String sUserId) {
		if(YLPushInitialization.GetSharePrefrence(context)!=0){
			//已经绑定了strAlias
			String alias = App.PUSH_ALIAS_SUFFIX + sUserId;
			Loger.i(TAG, "已经绑定了strAlias==>"+alias);
			return;
		}
		String alias = App.PUSH_ALIAS_SUFFIX + sUserId;
	    if (YLPushUtils.isEmpty(alias)) {
	    	Loger.i(TAG, "当前为空strAlias==>"+alias);
	        return;
	    }
	    if (!YLPushUtils.isValidTagAndAlias(alias)) {
	    	Loger.i(TAG, "当前非正确格式strAlias==>"+alias);
	        return;
	    }
	    // 调用 Handler 来异步设置别名
	    mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
	}
	
	public void setAlias(String strAlias) {
		String alias = App.PUSH_ALIAS_SUFFIX + strAlias;
	    if (YLPushUtils.isEmpty(alias)) {
	        return;
	    }
	    if (!YLPushUtils.isValidTagAndAlias(alias)) {
	        return;
	    }
	    // 调用 Handler 来异步设置别名
	    mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
	}

	public void deleteAlias(){
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, ""));
	}
	
	public void setTags(Context context, Set<String> tags) {
		try {
			JPushInterface.setTags(context, tags, mTagsCallback);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteTags(Context context){
		Set<String> tags = new HashSet<String>();
		tags.clear();
		JPushInterface.setTags(context, tags, mTagsCallback);
		tags = null;
	}
	
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
	    @Override
	    public void gotResult(int code, String alias, Set<String> tags) {
	        String logs ;
	        switch (code) {
	        case 0:
	            logs = "Set alias ["+ alias +"] success";
	            Loger.i(TAG, logs);
	            if(("".equals(alias))){
	            	YLPushInitialization.SharePrefrence(context, null);
	            }else{
	            	YLPushInitialization.SharePrefrence(context, alias);
	            }
	            break;
	        case 6002:
	            logs = "Failed to set alias and tags due to timeout. Try again after 10s.";
	            Loger.i(TAG, logs);
	            // 延迟 60 秒来调用 Handler 设置别名
	            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 10);
	            break;
	        default:
	            logs = "Failed with errorCode = " + code;
	            Loger.e(TAG, logs);
	        }
//	        YLPushUtils.showToast(logs, context);
	    }
	};
	
	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            Loger.i("TEST", "mTagsCallback=Code=>"+code);
            switch (code) {
            case 0:
                logs = "Set tag success";
                Loger.i(TAG, logs);
                break;
            case 6002:
                logs = "Failed to set alias and tags due to timeout. Try again after 5s.";
                Loger.i(TAG, logs);
                if (YLPushUtils.isConnected(context)) {
                	mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 5);
                } else {
                	Loger.i(TAG, "No network");
                }
                break;
            default:
                logs = "Failed with errorCode = " + code;
                Loger.e(TAG, logs);
            }
        }
    };
	
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SET_ALIAS:
				Loger.d(TAG, "Set alias in handler.");
				// 调用 JPush 接口来设置别名。
				JPushInterface.setAliasAndTags(context,
						(String) msg.obj, null, mAliasCallback);
				break;
			default:
				Loger.i(TAG, "Unhandled msg - " + msg.what);
			}
		}
	};
}
