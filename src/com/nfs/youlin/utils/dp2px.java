package com.nfs.youlin.utils;

import android.content.Context;

public class dp2px {
		
	    /** 
	     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	     */  
	    public  static int dip2px(float dpValue,Context context) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (dpValue * scale + 0.5f);  
	    }  
	  
	    /** 
	     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
	     */  
	    public int px2dip(float pxValue,Context context) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (pxValue / scale + 0.5f);  
	    }
	    /** 
	     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp 
	     */ 
	    public int px2sp(float pxValue,Context context) {  
	        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
	        return (int) (pxValue / fontScale + 0.5f);  
	    }
	    /** 
	     * 根据手机的分辨率从 sp 的单位 转成为 px(像素) 
	     */ 
	    public int sp2px(float spValue,Context context) {  
	        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
	        return (int) (spValue * fontScale + 0.5f);  
	    }
	
}
