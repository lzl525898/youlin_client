package com.nfs.youlin.activity.neighbor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import cn.jpush.android.api.JPushInterface;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.titlebar.startactivity.zoom.PhotoView;
import com.nfs.youlin.activity.titlebar.startactivity.zoom.ViewPagerFixed;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CircleDetailGalleryPictureActivity extends Activity {
	private Intent intent;
	private String url;
	ImageLoader imageLoader;
	public  PhotoView  galleryHeadImg;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gallery_picture_head);// 切屏到主界面
		imageLoader = ImageLoader.getInstance();
		intent = getIntent();
		url=intent.getStringExtra("url");
		int type = intent.getIntExtra("type", 0);
		String bigUrl;
		if(type==0){
			bigUrl=url.substring(0,url.lastIndexOf("/"))+"/"+"0"+url.split("[/]")[10]; //comment
		}else{
			bigUrl=url.substring(0,url.lastIndexOf("/"))+"/"+"0"+url.split("[/]")[8]; //head
		}
		Loger.i("LYM", "11111111111--->"+bigUrl);
		galleryHeadImg=(PhotoView)findViewById(R.id.gallery_head_img);
		galleryHeadImg.setBackgroundColor(0xffffffff);
		galleryHeadImg.setTag(1);
		imageLoader.displayImage(bigUrl, galleryHeadImg, App.options_picture_head);
	}

	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onResume(this);
	}
}
