package com.nfs.youlin.activity;

import java.util.List;

import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class ActivityManager {
	
	private String pkName;
	private String packageName;
	private Context context;
	private PackageManager packageManager;
	private List<PackageInfo> packageInfos;
	
	public ActivityManager(Context context){
		this.context = context;
		this.pkName = context.getPackageName();
		this.packageManager = context.getPackageManager();
	}
	
	public void ShowAllActivity(){
		Loger.i("TEST", "包名==>"+pkName);
		packageInfos = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
		for(PackageInfo info : packageInfos){
			ApplicationInfo appInfo = info.applicationInfo;
			if(appInfo.packageName.equals(pkName)){
				ActivityInfo[] activityInfos = info.activities;
				Loger.i("TEST", "activity数量==>"+activityInfos.length);
				if(activityInfos!=null && activityInfos.length>0){
					for(ActivityInfo activiyinfo : activityInfos){
						Loger.i("TEST", "====>"+activiyinfo.name);
					}
//					packageName = activityInfos[0].name;
				}
			}
		}
	}
}
