package com.nfs.youlin.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class XuanzhuanBitmap {
	
	/**  
	 * 读取图片属性：旋转的角度  
	 * @param path 图片绝对路径  
	 * @return degree旋转的角度  
	 */    
	   public  int readPictureDegree(String path) {    
	       int degree  = 0;    
	       try {    
	               ExifInterface exifInterface = new ExifInterface(path);    
	               int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);    
	               switch (orientation) {    
	               case ExifInterface.ORIENTATION_ROTATE_90:    
	                       degree = 90;    
	                       break;    
	               case ExifInterface.ORIENTATION_ROTATE_180:    
	                       degree = 180;    
	                       break;    
	               case ExifInterface.ORIENTATION_ROTATE_270:    
	                       degree = 270;    
	                       break;    
	               }    
	       } catch (IOException e) {    
	               e.printStackTrace();    
	       }    
	       return degree;    
	   }   
	/**  
     * 旋转图片  
     * @param angle  
     * @param bitmap  
     * @return Bitmap  
     */    
    public  Bitmap rotaingImageView(int angle , Bitmap bitmap) {    
        //旋转图片 动作     
        Matrix matrix = new Matrix();;    
        matrix.postRotate(angle);    
        System.out.println("angle2=" + angle);    
        // 创建新的图片 
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,    
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        //BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(newPath)));
        //Bitmap bitmap2 = BitmapFactory.decodeStream(is, outPadding, opts)
        return resizedBitmap;    
    }
    
}
