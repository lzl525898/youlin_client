package com.nfs.youlin.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.nfs.youlin.activity.MainActivity;

public class NetworkJudge {
	public Boolean checkNetwork(Context context){
		ConnectivityManager manager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info=manager.getActiveNetworkInfo();
		if(info==null){
			return false;
			//Loger.i("youlin","没网111111111111");
		}else{
			//Loger.i("youlin","有网111111111111");
			boolean isAlive=info.isAvailable();
			if(isAlive){
				return true;
				//Loger.i("youlin","有网可用111111111111");
			}else{
				return false;
				//Loger.i("youlin","有网不可用111111111111");
			}
	
		}
		
	}
}
