package com.nfs.youlin.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class BackgroundAlpha {
	public BackgroundAlpha(float bgAlpha,Context context){
		WindowManager.LayoutParams lp= ((Activity) context).getWindow().getAttributes();
		lp.alpha=bgAlpha;
		((Activity) context).getWindow().setAttributes(lp);
	}
}
