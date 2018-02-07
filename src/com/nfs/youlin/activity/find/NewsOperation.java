package com.nfs.youlin.activity.find;

import com.nfs.youlin.R;
import com.nfs.youlin.push.YLPushTagManager;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.app.ActionBar.LayoutParams;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

public class NewsOperation extends Activity implements OnClickListener{
	private LinearLayout returnbutton;
	private ImageButton newoperation;
	private RelativeLayout historymessage;
	private RelativeLayout messageset;
	private PopupWindow popupShareWindow;
	private View popupShareView;	
	private LinearLayout topLinearLayout;
	private PopupWindow xuanxiangpopupShareWindow;
	private View xuanxiangpopupShareView;	
	private LinearLayout xuanxiangtopLinearLayout;
	private Button newsexpect;
	private Button newsrefuse;
	private Button newscancel;
	private ImageView xinwengroup_view;
	private TextView xinwengroup_text;
	private boolean bPullUpStatus = false;
	private ImageLoader imageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newsoperationlay);
        imageLoader = ImageLoader.getInstance();
		getActionBar().hide();
		Intent intent = getIntent();
		
		returnbutton = (LinearLayout)findViewById(R.id.newsopreturn);
		newoperation = (ImageButton)findViewById(R.id.newsxuanxiang);
		historymessage = (RelativeLayout)findViewById(R.id.historymessage);
		messageset = (RelativeLayout)findViewById(R.id.messageset);
		xinwengroup_view = (ImageView) findViewById(R.id.xinwengroupimg);
		xinwengroup_text = (TextView) findViewById(R.id.xinwengrouptext);
		xinwengroup_text.setText(intent.getStringExtra("descripe").toString());
//		Picasso.with(this)
//		  .load(intent.getStringExtra("url").toString())
//		  .into(xinwengroup_view);
		imageLoader.displayImage(intent.getStringExtra("url").toString(), xinwengroup_view,App.options_error);
		
		returnbutton.setOnClickListener(this);
		newoperation.setOnClickListener(this);
		historymessage.setOnClickListener(this);
		messageset.setOnClickListener(this);
		
		popupShareView =NewsOperation.this.getLayoutInflater().inflate(R.layout.newsoperationdetaillay, null);
		topLinearLayout = (LinearLayout) popupShareView.findViewById(R.id.news_top_share);
		popupShareView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupShareWindow.dismiss();
			}
		});
//		xuanxiangpopupShareView =NewsOperation.this.getLayoutInflater().inflate(R.layout.newsxianxiangdetaillay, null);
//		xuanxiangtopLinearLayout = (LinearLayout) xuanxiangpopupShareView.findViewById(R.id.news_top_xuan);
		newsexpect = (Button)popupShareView.findViewById(R.id.newstoreceive);
		newsrefuse =  (Button)popupShareView.findViewById(R.id.newsrefudereceive);
		newscancel = (Button)popupShareView.findViewById(R.id.receivecancel);
		newscancel.setOnClickListener(this);
//		xuanxiangpopupShareView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				xuanxiangpopupShareWindow.dismiss();
//			}
//		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId() == R.id.newsopreturn){
			finish();
		}else if(v.getId() == R.id.newsxuanxiang){
			//setxuanxiangPopupWindow();
		}else if(v.getId() == R.id.historymessage){
			Intent intent = new Intent(NewsOperation.this,NewsHistorylist.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			NewsOperation.this.startActivity(intent);
		}else if(v.getId() == R.id.messageset){
			setPopupWindow();
		}else if(v.getId() == R.id.receivecancel){
			popupShareWindow.dismiss();
		}
	}
	private void setPopupWindow() {
		popupShareWindow = new PopupWindow(popupShareView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ColorDrawable cd = new ColorDrawable(0x90000000);
		popupShareWindow.setBackgroundDrawable(cd);
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 1f,
				Animation.RELATIVE_TO_PARENT, 0f);
		popupShareWindow.setFocusable(true);
		anim.setDuration(500);
		topLinearLayout.setAnimation(anim);
		popupShareWindow.showAtLocation(this.getWindow()
				.getDecorView(), Gravity.BOTTOM, 0, 0);
		popupShareWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		if(App.getNewsRecviceStatus(this)){	
			Loger.d("test5","getNewsRecviceStatus="+ App.getNewsRecviceStatus(this));
			newsexpect.setTextColor(0xffcccccc);
			
			newsexpect.setEnabled(false);
			newsexpect.setClickable(false);
			newsrefuse.setTextColor(0xff323232);
			newsrefuse.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final ProgressDialog pd=new ProgressDialog(NewsOperation.this);
					pd.setMessage("请求中...");
					pd.show();
					new YLPushTagManager(NewsOperation.this).setNewsRecviceStatusWithService(NewsOperation.this);
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							// TODO Auto-generated method stub
							Long currenttime = System.currentTimeMillis();
							while (!bPullUpStatus) {
								
								if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
									bPullUpStatus = true;
								}else if(App.getNewsRecviceStatus(NewsOperation.this)==false){
									bPullUpStatus = true;
								}
							}
							Loger.i("TEST", "pullUp置位成功！！");
							return null;
						}

						protected void onPostExecute(Void result) {
							super.onPostExecute(result);
							if(bPullUpStatus = true){
								bPullUpStatus = false;
								pd.dismiss();
								popupShareWindow.dismiss();
								newsrefuse.setTextColor(0xffcccccc);
								newsexpect.setEnabled(true);
								newsexpect.setClickable(true);
								newsrefuse.setEnabled(false);
								newsrefuse.setClickable(false);
								newsexpect.setTextColor(0xff323232);
							}
						};
					}.execute();
					
				}
			});
		}else{
			Loger.d("test5","getNewsRecviceStatus="+ App.getNewsRecviceStatus(this));
			newsrefuse.setTextColor(0xffcccccc);
			newsrefuse.setClickable(false);
			newsrefuse.setEnabled(false);
			newsexpect.setTextColor(0xff323232);
			newsexpect.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final ProgressDialog pd=new ProgressDialog(NewsOperation.this);
					pd.setMessage("请求中...");
					pd.show();
					new YLPushTagManager(NewsOperation.this).setNewsRecviceStatusWithService(NewsOperation.this);
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							// TODO Auto-generated method stub
							Long currenttime = System.currentTimeMillis();
							while (!bPullUpStatus) {
								if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
									bPullUpStatus = true;
								}else if(App.getNewsRecviceStatus(NewsOperation.this)==false){
									bPullUpStatus = true;
								}
							}
							Loger.i("TEST", "pullUp置位成功！！");
							return null;
						}

						protected void onPostExecute(Void result) {
							super.onPostExecute(result);
							if(bPullUpStatus = true){
								bPullUpStatus = false;
								pd.dismiss();
								popupShareWindow.dismiss();
								newsexpect.setTextColor(0xffcccccc);
								newsexpect.setEnabled(false);
								newsexpect.setClickable(false);
								newsrefuse.setEnabled(true);
								newsrefuse.setClickable(true);
								newsrefuse.setTextColor(0xff323232);
							}
						};
					}.execute();
					
				}
			});
		}
	}
	private void setxuanxiangPopupWindow() {
		xuanxiangpopupShareWindow = new PopupWindow(xuanxiangpopupShareView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ColorDrawable cd = new ColorDrawable(0x90000000);
		xuanxiangpopupShareWindow.setBackgroundDrawable(cd);
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 1f,
				Animation.RELATIVE_TO_PARENT, 0f);
		xuanxiangpopupShareWindow.setFocusable(true);
		anim.setDuration(500);
		xuanxiangtopLinearLayout.setAnimation(anim);
		xuanxiangpopupShareWindow.showAtLocation(this.getWindow()
				.getDecorView(), Gravity.BOTTOM, 0, 0);
		xuanxiangpopupShareWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
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
