package com.easemob.chatuidemo.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.utils.App;
import com.easemob.chatuidemo.utils.Loger;
import com.easemob.chatuidemo.video.util.Utils;
import com.easemob.chatuidemo.widget.photoview.PhotoView;
import com.easemob.chatuidemo.widget.photoview.ViewPagerFixed2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CircleDetailGalleryPictureActivity2 extends Activity {
	private Intent intent;
    // 返回按钮
    private Button back_bt;
	//顶部显示预览图片位置的textview
	private TextView positionTextView;
	//获取前一个activity传过来的position
	
	private int location = 0;
	private ArrayList<View> listViews = null;
	public ViewPagerFixed2 pager;
	public MyPageAdapter adapter;

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();
	
	private Context mContext;
	private String url;
	private Button back;
	RelativeLayout photo_relativeLayout;
    ImageLoader imageLoader;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gallery_picture_chat);// 切屏到主界面
        imageLoader = ImageLoader.getInstance();
		File cacheDir = new File(getCacheDir()+File.separator+"imageloader/Cache");
		mContext = this;
		//back=(Button)findViewById(R.id.gallery_back);
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		//isShowOkBt();
		// 为发送按钮设置文字
		pager = (ViewPagerFixed2) findViewById(R.id.gallery_01);
		pager.setOnPageChangeListener(pageChangeListener);
		url=intent.getStringExtra("url");
		if(url==null){
		    url="null";
		}
		int type = intent.getIntExtra("type", 0);
		String bigUrl;
		if(type==0){
			bigUrl=url.substring(0,url.lastIndexOf("/"))+"/"+"0"+url.split("[/]")[10];
		}else{
//			bigUrl=url.substring(0,url.lastIndexOf("/"))+"/"+"0"+url.split("[/]")[8];
		    bigUrl=url;
		}
		Loger.i("youlin", "url-->" + bigUrl);
		initListViews(bigUrl);
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);	
		pager.setPageMargin((int)getResources().getDimensionPixelOffset(R.dimen.ui_10_dip));
//		int id = intent.getIntExtra("ID", 0);
//		Loger.i("youlin","ID-->"+id);
//		Loger.i("youlin","url--->"+url);
//		pager.setCurrentItem(id);
		
		
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		public void onPageScrollStateChanged(int scrollState) {
//			final Picasso picasso = Picasso.with(mContext);
//			switch (scrollState) {
//			case 0:
//				picasso.pauseTag(mContext);
//				break;
//			case 1:
//				picasso.pauseTag(mContext);
//				break;
//			case 2:
//				picasso.pauseTag(mContext);
//				break;
//			default:
//				break;
//			}
			
		}
	};
	
    private void initListViews(String url) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xffffffff);
		//img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		if(url!=null || !url.equals("null")){
//		Picasso.with(mContext) //
//				.load(url) //
//                .error(R.drawable.default_normal_avatar)
//				.into(img);
		imageLoader.displayImage(url, img, App.options_account);
		}else{
//		    Picasso.with(mContext) //
//            .load(R.drawable.account) //
//            .error(R.drawable.account)
//            .into(img);
		    imageLoader.displayImage("null", img, App.options_account);
		}
		
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
			((ViewPagerFixed2) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed2) arg0).addView(listViews.get(arg1 % size), 0);
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
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}
}
