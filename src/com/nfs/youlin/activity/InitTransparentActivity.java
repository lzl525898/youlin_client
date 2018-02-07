package com.nfs.youlin.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.activity.VideoCallActivity;
import com.nfs.youlin.R;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class InitTransparentActivity extends Activity {
	private int nIndex;
	private int NotificationStatus;
	private String Extras = null;
	private final String NOTIFY= "Notification";
	private final String EXTRAS = "Extras";
	public static AssetManager am;
	private Intent networkMonitorIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init_transparent);
		am = getAssets();
		Intent exitIntent = getIntent();
		String status = exitIntent.getStringExtra("exit");
		nIndex = 0;
		if("exit".equals(status)){
			Loger.i("TEST", "执行退出操作......");
			beforeupdateApp(getApplication());
			finish();
		}else if("toast".equals(status)){//broadreceiver
			Intent intent = new Intent(InitTransparentActivity.this,SplashActivity.class);
			intent.putExtra(EXTRAS, exitIntent.getStringExtra("message"));
			intent.putExtra(NOTIFY, 9527);
			startActivity(intent);
		}else{
			Loger.i("TEST", "进入初始化Splash");
			App.sPhoneIMEI = YLPushUtils.getImei(getApplicationContext(), App.sPhoneIMEI);
			Intent intent = new Intent(InitTransparentActivity.this,SplashActivity.class);
			NotificationStatus = this.getIntent().getIntExtra(NOTIFY, 0);
			try {
				Extras = this.getIntent().getStringExtra(EXTRAS);
			} catch (Exception e) {
				Extras = null;
				e.printStackTrace();
			}
			intent.putExtra(NOTIFY, NotificationStatus);
			intent.putExtra(EXTRAS, Extras);
			startActivity(intent);
		}
		networkMonitorIntent = new Intent();
		networkMonitorIntent.setAction("youlin.network.monitor");
		networkMonitorIntent.setPackage(getPackageName());
		startService(networkMonitorIntent);
		//MobclickAgent.openActivityDurationTrack(false);
		AnalyticsConfig.enableEncrypt(true);
	}
	private void beforeupdateApp(Context context){
		EMChatManager.getInstance().endCall();
		//注销环信账户
		EMChatManager.getInstance().logout();
		//推送功能注销
		JPushInterface.stopPush(context);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		nIndex = nIndex + 1;
		JPushInterface.onResume(InitTransparentActivity.this);
		MobclickAgent.onResume(this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SystemClock.sleep(400);
				try {
					if(nIndex!=1){
						Loger.i("TEST", "特殊情况需要特殊处理	......");
						InitTransparentActivity.this.finish();
					}else{
						Loger.i("TEST", "特殊情况需要特殊处理	.....但不在前台");
					}
				} catch (Exception e) {
					Loger.i("TEST", "特殊情况需要特殊处理	....Err");
				}
			}
		}).start();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(InitTransparentActivity.this);
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Loger.i("TEST", "进入初始化Activity-->onDestroy");
		stopService(networkMonitorIntent);
	}
}
