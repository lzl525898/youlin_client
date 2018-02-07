package com.nfs.youlin.activity;

import java.util.ArrayList;
import java.util.List;

import com.nfs.youlin.InitFragment1;
import com.nfs.youlin.InitFragment2;
import com.nfs.youlin.InitFragment3;
import com.nfs.youlin.InitFragment4;
import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.R.style;
import com.nfs.youlin.adapter.MyViewPagerAdapter;
import com.nfs.youlin.utils.AccordionTransformer;
import com.nfs.youlin.utils.CubeTransformer;
import com.nfs.youlin.utils.DepthPageTransformer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.ZoomOutPageTransformer;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.jpush.android.api.JPushInterface;

public class InitActivity extends FragmentActivity implements OnPageChangeListener{
	ViewPager viewPager;
	PagerAdapter pagerAdapter;
	ArrayList<Fragment> fragments;
	ImageView imageView1;
	ImageView imageView2;
	ImageView imageView3;
	ImageView imageView4;
	ImageView[] imageViews;
	//ViewGroup group;
	RelativeLayout initLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_init);
		initLayout=(RelativeLayout) findViewById(R.id.init_layout);
		imageView1=(ImageView)findViewById(R.id.init_img1);
		imageView2=(ImageView)findViewById(R.id.init_img2);
		imageView3=(ImageView)findViewById(R.id.init_img3);
		imageView4=(ImageView)findViewById(R.id.init_img4);
		viewPager=(ViewPager) findViewById(R.id.init_pager);
		fragments=new ArrayList<Fragment>();
		fragments.add(new InitFragment1());
		fragments.add(new InitFragment2());
		fragments.add(new InitFragment3());
		fragments.add(new InitFragment4());
		pagerAdapter= new MyViewPagerAdapter(getSupportFragmentManager(),fragments);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setPageTransformer(true, new AccordionTransformer());
		viewPager.setCurrentItem(0);
		
		imageViews=new ImageView[fragments.size()];
		imageViews[0]=imageView1;
		imageViews[1]=imageView2;
		imageViews[2]=imageView3;
		imageViews[3]=imageView4;
		imageViews[0].setBackgroundResource(R.drawable.init_xuanzhong);
		imageViews[1].setBackgroundResource(R.drawable.init_weixuanzhong);
		imageViews[2].setBackgroundResource(R.drawable.init_weixuanzhong);
		imageViews[3].setBackgroundResource(R.drawable.init_weixuanzhong);
	}


	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[arg0].setBackgroundResource(R.drawable.init_xuanzhong);
			if(arg0 != i){
				imageViews[i].setBackgroundResource(R.drawable.init_weixuanzhong);
			}
		}
//		switch (arg0) {
//			case 0:
//				initLayout.setBackgroundResource(R.drawable.init1);
//				break;
//			case 1:
//				initLayout.setBackgroundResource(R.drawable.init2);
//				break;
//			case 2:
//				initLayout.setBackgroundResource(R.drawable.init3);
//				break;
//			case 3:
//				initLayout.setBackgroundResource(R.drawable.init4);
//				break;	
//			default:
//				break;
//		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Loger.i("TEST", "执行onKeyDown");
			Intent intent = new Intent(InitActivity.this, InitTransparentActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("exit", "exit");
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
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
