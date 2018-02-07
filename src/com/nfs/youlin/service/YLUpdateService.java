package com.nfs.youlin.service;

import java.io.File;
import java.util.List;

import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.util.EasyUtils;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.DownLoadManager;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.DataCleanManager;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.Loger;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import cn.jpush.android.api.JPushInterface;

@SuppressLint("HandlerLeak")
public class YLUpdateService extends Service {
	// 通知栏
	private int updateNotifyId = 101;
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	private String appName = null;
	private String url = null;
	private String status = null;
	private Intent intent;
	private boolean bUpdateApkStatus = true;//判断是否下载失败  
	private File updatefile;
	private RemoteViews view=null;
	private int myappid;
	private DownLoadManager downLoadManager;
	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		downLoadManager=new DownLoadManager(); 
		this.intent = intent;
		if(intent!=null){
			appName = intent.getStringExtra("appName");
			status = intent.getStringExtra("status");
			url = intent.getStringExtra("url");
		}
		bUpdateApkStatus = true;
		if(appName!=null){
			App.UPDATE_APK_STATUS = true;
			Loger.i("TEST","开始进行下载更新！！");
			Intent nullIntent = new Intent();
			PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), updateNotifyId, nullIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			updateNotification = new Notification(R.drawable.icon_youlin, "正在更新" + appName, System.currentTimeMillis());
			updateNotification.icon = R.drawable.icon_youlin;
			updateNotification.tickerText = "正在更新" + appName;
//			updateNotification.setLatestEventInfo(getApplication(), "正在下载"+appName,"0%", pendingIntent);
//			updateNotification.flags = Notification.FLAG_ONGOING_EVENT;
			updateNotification.contentIntent = pendingIntent;
			view=new RemoteViews(getPackageName(),R.layout.ylupdatedialog);
			updateNotification.contentView = view;
			updateNotificationManager.notify(updateNotifyId, updateNotification);
			new Thread(new updateRunnable()).start();
		}
		return super.onStartCommand(intent, 0, 0);
	}
//	view.setProgressBar(R.id.pb, 100, progress, false);
//	 view.setTextViewText(R.id.tv, "优邻正在下载"+progress+"%");//
	private class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();
		public void run() {
			message.what = 0;
			try {
				boolean bRet = false;
				if("1".equals(status)){//强制更新
					App.UPDATE_APK_TYPE = 1;
					clearYoulinInfo(getApplicationContext());
					//EMChatManager.getInstance().logout();
					JPushInterface.stopPush(getApplicationContext());
					bRet = getUpdateFile();
					if (bRet) {
						if (updatefile==null){
							Loger.i("TEST","下载出错==112333333333333333");
							Message message = updateHandler.obtainMessage();
							message.what = 1;	
							updateHandler.sendMessage(message);// 下载失败
							return;
						}else{
							bUpdateApkStatus = true;
							Message message = updateHandler.obtainMessage();
							message.what = 0;	
							updateHandler.sendMessage(message);// 下载成功  开始更新
						}
					}
				}else{
					App.UPDATE_APK_TYPE = 2;
					bRet = downloadUpdateFile(url);
					if (bRet) {
						if (updatefile==null){
							Loger.i("TEST", "下载出错====================>");
							Message message = updateHandler.obtainMessage();
							message.what = 1;	
							updateHandler.sendMessage(message);// 下载失败
							return;
						}else{
							bUpdateApkStatus = true;
							Message message = updateHandler.obtainMessage();
							message.what = 0;	
							updateHandler.sendMessage(message);// 下载成功  开始更新
						}
					}
					bUpdateApkStatus = true;

				}

			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = 1;
				// 下载失败
				updateHandler.sendMessage(message);
			}
		}
	}
	
	private Handler updateHandler = new Handler() {
		@SuppressWarnings("deprecation")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// 点击安装PendingIntent
				if("1".equals(status)){
					File indexfile = new File(Environment.getExternalStorageDirectory(), "youlinindex.txt");
					if(indexfile.exists()){
						indexfile.delete();
					}
					DemoApplication myapp = (DemoApplication)getApplication();
					myapp.getupdateDialog().cancel();
					 Intent installIntent = new Intent();  
					 installIntent.setAction(Intent.ACTION_VIEW);  
					 installIntent.setDataAndType(Uri.fromFile(updatefile), "application/vnd.android.package-archive");
					 installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 startActivity(installIntent);
					 
					 Intent closeIntent = new Intent();
					 closeIntent.setClassName("com.nfs.youlin", "com.nfs.youlin.activity.InitTransparentActivity");
//					 closeIntent.addCategory(Intent.CATEGORY_LAUNCHER);
					 closeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 closeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 closeIntent.putExtra("exit", "exit");
					 startActivity(closeIntent);
					 
				}else{
					 Intent installIntent = new Intent();  
					 installIntent.setAction(Intent.ACTION_VIEW);  
					 installIntent.setDataAndType(Uri.fromFile(updatefile), "application/vnd.android.package-archive");  
					PendingIntent updatePendingIntent = PendingIntent.getActivity(YLUpdateService.this,updateNotifyId, installIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
					updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
					updateNotification.setLatestEventInfo(YLUpdateService.this,appName, "下载完成,点击安装...", updatePendingIntent);
					updateNotificationManager.notify(101, updateNotification);
					File indexfile = new File(Environment.getExternalStorageDirectory(), "youlinindex.txt");
					if(indexfile.exists()){
						indexfile.delete();
					}
					Intent closeIntent = new Intent();
					 closeIntent.setClassName("com.nfs.youlin", "com.nfs.youlin.activity.InitTransparentActivity");
//					 closeIntent.addCategory(Intent.CATEGORY_LAUNCHER);
					 closeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 closeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 closeIntent.putExtra("exit", "exit");
					 startActivity(closeIntent);
				}
				
				// 停止服务
				DownLoadManager.sDownloadSchedule = 0;
				
				stopSelf();
				break;
			case 1:
				App.UPDATE_APK_STATUS = false;
				bUpdateApkStatus = false;
				updatefile = null;
				DemoApplication myapp = (DemoApplication)getApplication();
				myapp.getupdateDialog().cancel();
				Intent nullIntent = new Intent();
				PendingIntent pendingIntent = PendingIntent.getActivity(YLUpdateService.this, 0, nullIntent, 0);
				// 下载失败
				updateNotification.setLatestEventInfo(YLUpdateService.this,appName, "网络连接不正常，下载失败！", pendingIntent);
				updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
				updateNotificationManager.notify(updateNotifyId, updateNotification);
				break;
			default:
				stopSelf();
			}
		}
	};
	
	@SuppressWarnings("deprecation")
	public boolean downloadUpdateFile(final String url) throws Exception {
		new Thread() {
			public void run() {
				try {
					Loger.i("TEST","111111---------->"+url);
					updatefile = downLoadManager.getFileFromServer(url, null);
					bUpdateApkStatus = true;
				} catch (Exception e) {
					Loger.i("TEST", "下载出错==>"+e.getMessage());
					Message message = updateHandler.obtainMessage();
					message.what = 1;
					// 下载失败
					updateHandler.sendMessage(message);
				}
			}
		}.start();
        while(true){
        	SystemClock.sleep(2000);
            if(DownLoadManager.sDownloadSchedule!=100){
            	if(!bUpdateApkStatus){
            		break;
            	}else{
//            		Intent updateIntent = new Intent();
//            		  
//
//            		PendingIntent pendingIntent = null;
//            		if(EnsureAPPgoing()){
//            			Loger.d("test4", "downloadUpdateFile APP EnsureAPPgoing");
//            			updateIntent.setClassName("com.nfs.youlin", App.UPDATE_APK_PORSITION);
//                        updateIntent.putExtra("whereflag", "updatenotify");
//                        updateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//
//                        updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        pendingIntent = PendingIntent.getActivity(getApplicationContext(), updateNotifyId, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);//appContext, notifyID, intent2,PendingIntent.FLAG_UPDATE_CURRENT
//                        updateNotification.contentIntent = pendingIntent;
//            		}else{
//            			Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.nfs.youlin");
////            			updateIntent.setClassName("com.nfs.youlin", "com.nfs.youlin.activity.SplashActivity");
//            			LaunchIntent.putExtra("whereflag", "updatenotify");
//            			LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//
//                        pendingIntent = PendingIntent.getActivity(getApplicationContext(), updateNotifyId, LaunchIntent, PendingIntent.FLAG_UPDATE_CURRENT);//appContext, notifyID, intent2,PendingIntent.FLAG_UPDATE_CURRENT
//                        updateNotification.contentIntent = pendingIntent;
//            		} 
            		view.setProgressBar(R.id.pb, 100, DownLoadManager.sDownloadSchedule, false);
            		 view.setTextViewText(R.id.tv, appName+"正在下载"+"  进度为:"+DownLoadManager.sDownloadSchedule+"%");//
//                    updateNotification.setLatestEventInfo(YLUpdateService.this, appName+"正在下载", "当前下载进度为:"+DownLoadManager.sDownloadSchedule+"%", pendingIntent);
                    updateNotificationManager.notify(updateNotifyId, updateNotification);
            	}
            }else{
            	view.setProgressBar(R.id.pb, 100, DownLoadManager.sDownloadSchedule, false);
       		 	view.setTextViewText(R.id.tv, appName+"正在下载"+"  进度为:"+DownLoadManager.sDownloadSchedule+"%");//
//            	updateNotification.setLatestEventInfo(YLUpdateService.this, appName+"正在下载", "当前下载进度为:"+DownLoadManager.sDownloadSchedule+"%", null);
            	return true;
            }                      
        }
		return bUpdateApkStatus;
    }
	
	@SuppressWarnings("deprecation")
	public boolean getUpdateFile() throws Exception {
		new Thread() {
			public void run() {
				try {
					updatefile = downLoadManager.getFileFromServer(url, null);
					bUpdateApkStatus = true;
				}catch (Exception e) {
					Loger.i("TEST", "下载出错==>"+e.getMessage());
					Message message = updateHandler.obtainMessage();
					message.what = 1;
					updateHandler.sendMessage(message);// 下载失败
				}
			}
		}.start();
        while(true){
        	SystemClock.sleep(2000);
            if(DownLoadManager.sDownloadSchedule!=100){ 
            	if(!bUpdateApkStatus){
            		break;
            	}else{
//                Intent updateIntent = new Intent();
//                PendingIntent pendingIntent = null;
//                if(EnsureAPPgoing()){
//                	updateIntent.setClassName("com.nfs.youlin",  App.UPDATE_APK_PORSITION);
//                    updateIntent.putExtra("whereflag", "updatenotify");
//                    updateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//
//                    updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), updateNotifyId, updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//                    updateNotification.contentIntent = pendingIntent;
//                }else{
//                	Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.nfs.youlin");
//        			LaunchIntent.putExtra("whereflag", "updatenotify");
//        			LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//
//                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), updateNotifyId, LaunchIntent, PendingIntent.FLAG_UPDATE_CURRENT);//appContext, notifyID, intent2,PendingIntent.FLAG_UPDATE_CURRENT
//
////                	updateIntent.setClassName("com.nfs.youlin", "com.nfs.youlin.activity.SplashActivity");
////                    updateIntent.putExtra("whereflag", "updatenotify");
////                    updateIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//
////                    pendingIntent = PendingIntent.getActivity(getApplicationContext(), updateNotifyId, updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//                    updateNotification.contentIntent = pendingIntent;
//                }
                view.setProgressBar(R.id.pb, 100, DownLoadManager.sDownloadSchedule, false);
       		 	view.setTextViewText(R.id.tv, appName+"正在下载"+"  进度为:"+DownLoadManager.sDownloadSchedule+"%");//
//                updateNotification.setLatestEventInfo(YLUpdateService.this, appName+"正在下载", "当前下载进度为:"+DownLoadManager.sDownloadSchedule+"%", pendingIntent);
                updateNotificationManager.notify(updateNotifyId, updateNotification);
            	}
            }else{
            	updateNotificationManager.cancel(updateNotifyId);
            	return true;
            }                      
        }
        return bUpdateApkStatus;
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	private void clearYoulinInfo(Context context){
		String dataBasePath=context.getFilesDir().getParent()+File.separator+"databases";
		String sharedPath=context.getFilesDir().getParent()+File.separator+"shared_prefs";
		Loger.i("youlin",dataBasePath);
		File dataBaseFile=new File(dataBasePath);
		File sharedFile=new File(sharedPath);
		DataCleanManager.deleteFilesByDirectory(dataBaseFile);
		DataCleanManager.deleteFilesByDirectory(sharedFile);
		MainActivity.currentcity="未设置";
		MainActivity.currentvillage = "未设置";
		App.sUserType = -1;
		App.sUserLoginId = -1;
		App.sNewPushRecordCount = null;
		App.sUserPhone = null;
		App.imei = null;
		App.pushTags = null;
		App.sDeleteTagFinish = false;
		App.sDeviceLoginStatus = false;
		App.sFamilyId = 00000000000L;
		App.sFamilyCommunityId = 0000000000L;
		App.sFamilyBlockId = 0000000000L;
		App.sUserAppTime = 0000000000L;
		App.sPushUserID = null;
		App.sPushChannelID = null;
		App.sLoadNewTopicStatus = false;
		App.NORMAL_TYPE = 2;
		App.GONGGAO_TYPE = 3;
		App.BAOXIU_TYPE = 4;
		App.JIANYI_TYPE = 5;
		App.UPDATE_APK_STATUS = false;
		App.UPDATE_APK_TYPE = 0;
		//EMChatManager.getInstance().endCall();
	}

	private boolean EnsureAPPgoing(){
		ActivityManager am = (ActivityManager)getApplication().getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningTaskInfo> list = am.getRunningTasks(100);

		boolean isAppRunning = false;

		String MY_PKG_NAME = "com.nfs.youlin";
//		  int pid = android.os.Process.myPid();
		  for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
		   if (appProcess.processName.equals(MY_PKG_NAME)) {
			   Loger.d("TEST","myprocessname"+appProcess.processName);
			   Loger.d("TEST","myPid"+android.os.Process.myPid());
			   Loger.d("TEST","list get myPid"+appProcess.pid);
			   myappid = appProcess.pid;
		   }
		  }

		
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
				isAppRunning = true;
				Loger.i("TEST",info.topActivity.getPackageName() + " info.baseActivity.getPackageName()="+info.baseActivity.getPackageName()+"---"+myappid);
				return isAppRunning;
			}

		}
		return isAppRunning;
	}
}
