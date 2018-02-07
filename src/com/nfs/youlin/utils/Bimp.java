package com.nfs.youlin.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.nfs.youlin.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class Bimp {
	public static int max = 0;
	public static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<ImageItem>();   //选择的图片的临时列表
	//public static ArrayList<Bitmap> bitmapList=new ArrayList<Bitmap>();
	
	public Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		//FileInputStream in = new FileInputStream(new File(path));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap=null;
//		while (true) {
//			if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
//			    in = new BufferedInputStream(new FileInputStream(new File(path)));
//			   	options.inSampleSize = (int) Math.pow(2.0D, i);
//				options.inJustDecodeBounds = false;
//				options.inPurgeable = true;
//				options.inInputShareable = true;
//				bitmap = BitmapFactory.decodeStream(in, null, options);				
//				break;
//			}
//			i += 1;
//		}
		
//		 in = new BufferedInputStream(new FileInputStream(new File(path)));
//		 options.inSampleSize=computeSampleSize(options, -1, 1000*1000);
//		 options.inJustDecodeBounds = false;
//		 options.inPurgeable = true;
//		 options.inInputShareable = true;
//		 bitmap = BitmapFactory.decodeStream(in, null, options);
//		 bitmapList.add(bitmap);
		
		
		int scale = 1;
        if (options.outHeight > 1000 || options.outWidth > 1000) {
            scale = (int)Math.pow(2, (int) Math.round(Math.log(1000 / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        o2.inJustDecodeBounds = false;
        options.inPurgeable = true;
		options.inInputShareable = true;
        in = new BufferedInputStream(new FileInputStream(new File(path)));
        bitmap = BitmapFactory.decodeStream(in, null, o2);
        in.close();
		return bitmap;
	}
	public Bitmap revitionImageSize2(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		//FileInputStream in = new FileInputStream(new File(path));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap=null;
		while (true) {
			if ((options.outWidth >> i <= 256) && (options.outHeight >> i <= 256)) {
			    in = new BufferedInputStream(new FileInputStream(new File(path)));
			   	options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				options.inPurgeable = true;
				options.inInputShareable = true;
				bitmap = BitmapFactory.decodeStream(in, null, options);				
				//bitmapList.add(bitmap);
				break;
			}
			i += 1;
		}
		
		return bitmap;
	}

	public  int computeSampleSize(BitmapFactory.Options options,

	int minSideLength, int maxNumOfPixels) {

		int initialSize = computeInitialSampleSize(options, minSideLength,

		maxNumOfPixels);

		int roundedSize;

		if (initialSize <= 8) {

			roundedSize = 1;

			while (roundedSize < initialSize) {

				roundedSize <<= 1;

			}

		} else {

			roundedSize = (initialSize + 7) / 8 * 8;

		}

		return roundedSize;
	}

	private int computeInitialSampleSize(BitmapFactory.Options options,

	int minSideLength, int maxNumOfPixels) {

		double w = options.outWidth;

		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 :

		(int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

		int upperBound = (minSideLength == -1) ? 128 :

		(int) Math.min(Math.floor(w / minSideLength),

		Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {

			// return the larger one when there is no overlapping zone.

			return lowerBound;

		}

		if ((maxNumOfPixels == -1) &&

		(minSideLength == -1)) {

			return 1;

		} else if (minSideLength == -1) {

			return lowerBound;

		} else {

			return upperBound;

		}
	}
}
