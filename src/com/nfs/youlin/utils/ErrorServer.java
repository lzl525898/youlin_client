package com.nfs.youlin.utils;

import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ErrorServer {
	int temp=6;//发布时写0
	public ErrorServer(Context context,String error) {
		// TODO Auto-generated constructor stub
		if(temp!=0){
			Intent intent = new Intent(context,error_logtext.class);
			intent.putExtra("error", error);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			context.startActivity(intent);
		}else if(temp==0){
			Toast.makeText(context, "网络有问题", 0).show();
		}
	}
}
