package com.nfs.youlin.activity.neighbor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.easemob.chatuidemo.video.util.Utils;
import com.easemob.chatuidemo.widget.RecyclingImageView;
import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.activity.titlebar.startactivity.AlbumActivity;
import com.nfs.youlin.activity.titlebar.startactivity.GalleryActivity;
import com.nfs.youlin.activity.titlebar.startactivity.ImageFile;
import com.nfs.youlin.activity.titlebar.startactivity.ShowAllPhoto;
import com.nfs.youlin.activity.titlebar.startactivity.zoom.PhotoView;
import com.nfs.youlin.activity.titlebar.startactivity.zoom.ViewPagerFixed;
import com.nfs.youlin.adapter.ImgUrlData;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.PublicWay;
import com.nfs.youlin.utils.Res;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GalleryPictureActivity extends Activity {
	private Intent intent;
    // 返回按钮
    private Button back_bt;
	//顶部显示预览图片位置的textview
	private TextView positionTextView;
	//获取前一个activity传过来的position
	
	private int location = 0;
	public  ArrayList<View> listViews = null;
	public  ViewPagerFixed pager;
	public MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	private String url;
	private String[] urlArray;
	private Button back;
	ImageLoader imageLoader;
	int type=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gallery_picture);// 切屏到主界面
		//File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "imageloader/Cache");
		File cacheDir = new File(getCacheDir()+File.separator+"imageloader/Cache");
		imageLoader = ImageLoader.getInstance();
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		// 为发送按钮设置文字
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		//pager.setOnPageChangeListener(pageChangeListener);
		type=intent.getIntExtra("type", 0);
		if(type==5){
			urlArray=intent.getStringArrayExtra("url");
		}else{
			url=intent.getStringExtra("url");
		}
		if (listViews == null){
			listViews = new ArrayList<View>();
		}else{
			listViews.clear();
		}
		if(type==5){
			if (urlArray != null) {
				for (int i = 0; i < urlArray.length; i++) {
					//https://www.youlinzj.cn/media/youlin/res/96/topic/657/090655172555.jpg
					String bigUrl=urlArray[i].substring(0, urlArray[i].lastIndexOf('/'))+'/'+'0'+ urlArray[i].split("[/]")[9];
					Loger.i("NEW", "bigUrl->"+bigUrl);
					initListViews(bigUrl);
				}
			}
		}else{
			JSONArray imgJsonArray;
			try {
				if (url != null) {
					imgJsonArray = new JSONArray(url);
					for (int i = 0; i < imgJsonArray.length(); i++) {
						JSONObject jsonObject = new JSONObject(imgJsonArray.getString(i));
						String smallUrl = jsonObject.getString("resPath");
						String bigUrl = smallUrl.substring(0, smallUrl.lastIndexOf("/")) + "/" + "0" + smallUrl.split("[/]")[9];
						Loger.i("youlin", "url-->" + bigUrl);
						initListViews(bigUrl);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);	
		pager.setPageMargin((int)getResources().getDimensionPixelOffset(R.dimen.ui_10_dip));
		int id = intent.getIntExtra("ID", 0);
//		Loger.i("youlin","ID-->"+id);
//		Loger.i("youlin","url--->"+url);
		pager.setCurrentItem(id);
		
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		public void onPageScrollStateChanged(int scrollState) {
//			  final Picasso picasso = Picasso.with(mContext);
//			    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//			      picasso.resumeTag(mContext);
//			    } else {
//			      picasso.pauseTag(mContext);
//			    }

		}
	};
	
	private void initListViews(String url) {
			PhotoView img = new PhotoView(GalleryPictureActivity.this); //type 1 代表 头像 
			img.setBackgroundColor(0xff000000);
			//img.setImageBitmap(bm);
			img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//			Picasso.with(mContext)
//			.load(url)
//			.tag(mContext)
//			.into(img);
			imageLoader.displayImage(url, img, App.options_error_no_memory);
			listViews.add(img);
	}


//	public void isShowOkBt() {
//		if (Bimp.tempSelectBitmap.size() > 0) {
//			send_bt.setText(Res.getString("finish")+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
//			send_bt.setPressed(true);
//			send_bt.setClickable(true);
//			send_bt.setTextColor(Color.WHITE);
//		} else {
//			send_bt.setPressed(false);
//			send_bt.setClickable(false);
//			send_bt.setTextColor(Color.parseColor("#E1E0DE"));
//		}
//	}

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
	
	
	public class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;
		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);
			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

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
		MobclickAgent.onPause(this);
	}
	
}
