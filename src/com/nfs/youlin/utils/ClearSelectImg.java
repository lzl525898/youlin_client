package com.nfs.youlin.utils;

import com.nfs.youlin.activity.titlebar.startactivity.AlbumActivity;

import android.graphics.Bitmap;
import android.util.Log;

public class ClearSelectImg {
	public ClearSelectImg (){
//		try {
//			for (int i = 0; i < PublicWay.activityList.size(); i++) {
//				if (null != PublicWay.activityList.get(i)) {
//					PublicWay.activityList.get(i).finish();
//				}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


		try {
			for (int k = 0; k < Bimp.tempSelectBitmap.size(); k++) {
				if(Bimp.tempSelectBitmap.get(k).oldBitmap!=null){
					Bimp.tempSelectBitmap.get(k).oldBitmap.recycle();
					Bimp.tempSelectBitmap.get(k).oldBitmap=null;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
//		for (int m = 0; m < AlbumActivity.contentList.size(); m++) {
//			AlbumActivity.contentList.get(m).imageList.clear();
//		}
		try {
//			PublicWay.activityList.clear();
			Bimp.tempSelectBitmap.clear();
			AlbumActivity.contentList.clear();
			AlbumActivity.dataList.clear();
			AlbumHelper.instance=null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.gc();
	}
}
