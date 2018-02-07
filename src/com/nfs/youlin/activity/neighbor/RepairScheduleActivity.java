package com.nfs.youlin.activity.neighbor;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.utils.ClearSelectImg;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.TimeToStr;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RepairScheduleActivity extends Activity{
	private ActionBar actionBar;
	private RelativeLayout schedulenew;
	private RelativeLayout scheduleold;
	private RelativeLayout scheduleancestor;
	private TextView newtext;
	private TextView oldtext;
	private TextView ancestortext;
	private TextView newtime;
	private TextView oldtime;
	private TextView ancestortime;
	private ImageView gaizhang;
	private Intent intent;
	private List<Long> scheduletime = new ArrayList<Long>();
	private PopupWindow popupShareWindow;
	private View popupShareView;
	private String shareInformation ;
	private LinearLayout topLinearLayout;
	private RelativeLayout shareRrecordLayout;
	private ImageView contactsImageView;
	private ImageView tengxunImageView;
	private ImageView weixinImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repairschedulelay);
		actionBar=getActionBar();
		actionBar.setTitle("维修进度");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		schedulenew = (RelativeLayout)findViewById(R.id.schedulenew);
		scheduleold = (RelativeLayout)findViewById(R.id.scheduleold);
		scheduleancestor = (RelativeLayout)findViewById(R.id.scheduleancestor);
		newtext = (TextView)findViewById(R.id.schedulenewtext);
		oldtext = (TextView)findViewById(R.id.scheduleoldtext);
		ancestortext = (TextView)findViewById(R.id.scheduleancestortext);
		newtime = (TextView)findViewById(R.id.schedulenewtime);
		oldtime = (TextView)findViewById(R.id.scheduleoldtime);
		ancestortime = (TextView)findViewById(R.id.scheduleancestortime);
		gaizhang = (ImageView)findViewById(R.id.wancheng);
		intent = getIntent();
		int schedule = intent.getIntExtra("schedule", 1);
		Loger.d("test4", "schedule = "+schedule);
		Loger.d("test4", "schedule data= "+intent.getStringExtra("scheduledata"));
		for(int i = 0;i<schedule;i++){
			try {
				JSONObject object = new JSONObject(intent.getStringExtra("scheduledata"));
				scheduletime.add(Long.parseLong(object.getString(String.valueOf(i+1))));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Loger.d("test4", "scheduletime length = "+scheduletime.size());
		if(schedule == 1){
			schedulenew.setVisibility(View.VISIBLE);
			newtext.setText("报修内容已提交，请耐心等待...");
			newtime.setText("");
			shareInformation = new TimeToStr().getTimeToStr(scheduletime.get(0))+":报修内容已提交...";
		}else if(schedule == 2){
			scheduleold.setVisibility(View.VISIBLE);
			newtext.setText("审核处理中...");
			newtime.setText(new TimeToStr().getTimeToStr(scheduletime.get(1)));
			oldtext.setText("报修内容已提交，请耐心等待...");
			oldtime.setText(new TimeToStr().getTimeToStr(scheduletime.get(0)));
			oldtext.setTextColor(0xff909090);
			shareInformation =new TimeToStr().getTimeToStr(scheduletime.get(0))+":报修内容已提交..."+
			"\n"+new TimeToStr().getTimeToStr(scheduletime.get(1))+":审核处理中...";
		}else if(schedule == 3){
			scheduleold.setVisibility(View.VISIBLE);
			scheduleancestor.setVisibility(View.VISIBLE);
			newtext.setText("维修完成.");
			newtime.setText(new TimeToStr().getTimeToStr(scheduletime.get(2)));
			oldtext.setText("审核处理中...");
			oldtime.setText(new TimeToStr().getTimeToStr(scheduletime.get(1)));
			oldtext.setTextColor(0xff909090);
			ancestortext.setText("报修内容已提交，请耐心等待...");
			ancestortime.setText(new TimeToStr().getTimeToStr(scheduletime.get(0)));
			ancestortext.setTextColor(0xff909090);
			gaizhang.setVisibility(View.VISIBLE);
			shareInformation =new TimeToStr().getTimeToStr(scheduletime.get(0))+":报修内容已提交..."+
					"\n"+new TimeToStr().getTimeToStr(scheduletime.get(1))+":审核处理中..."
					+"\n"+new TimeToStr().getTimeToStr(scheduletime.get(2))+":维修完成，望给出您的真挚评价";
		}
		
		 	popupShareView =this.getLayoutInflater().inflate(R.layout.popup_personal_share, null);
			topLinearLayout = (LinearLayout) popupShareView.findViewById(R.id.ll_top_share);
			shareRrecordLayout = (RelativeLayout) popupShareView.findViewById(R.id.ll_popup_share_record);
			contactsImageView = (ImageView) popupShareView.findViewById(R.id.iv_popup_personal_share_contact);
			tengxunImageView = (ImageView) popupShareView.findViewById(R.id.iv_popup_personal_share_tengxun);
			weixinImageView = (ImageView) popupShareView.findViewById(R.id.iv_popup_personal_share_weixin);
			contactsImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					Toast.makeText(RepairScheduleActivity.this, "分享中...",Toast.LENGTH_SHORT).show();
					Uri smsToUri = Uri.parse("smsto:");
					Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
					sendIntent.putExtra( "sms_body", shareInformation);  
				    sendIntent.setType( "vnd.android-dir/mms-sms" );
				    RepairScheduleActivity.this.startActivity(sendIntent);
					popupShareWindow.dismiss();
				}
			});

			weixinImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!isApkInstalled(RepairScheduleActivity.this,"com.tencent.mm")) {
						Toast.makeText(RepairScheduleActivity.this, "手机未发现有微信，请先安装!",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(RepairScheduleActivity.this, "分享中...",
								Toast.LENGTH_SHORT).show();
						initShareIntent("com.tencent.mm");
					}
					popupShareWindow.dismiss();
				}
			});
			tengxunImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!isApkInstalled(RepairScheduleActivity.this, "com.tencent.mobileqq")) {
						Toast.makeText(RepairScheduleActivity.this, "手机未发现有QQ，请先安装!",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(RepairScheduleActivity.this, "分享中...",
								Toast.LENGTH_SHORT).show();
						initShareIntent("tencent.mobileqq");
					}
					popupShareWindow.dismiss();
				}
			});
			popupShareView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					popupShareWindow.dismiss();
				}
			});

			shareRrecordLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(RepairScheduleActivity.this, "查看分享", Toast.LENGTH_LONG)
							.show();
				}
			});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.repairschedule, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
//		case R.id.scheduleshare:
//			setPopupWindow();
//			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
	}
	private boolean isApkInstalled(Context context, String packageName) {
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	@SuppressLint("DefaultLocale")
	private void initShareIntent(String type) {
		boolean found = false;
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = this.getPackageManager().queryIntentActivities(
				share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(type)
						|| info.activityInfo.name.toLowerCase().contains(type)) {
					share.putExtra(Intent.EXTRA_TEXT, shareInformation);
					share.setPackage(info.activityInfo.packageName);
					found = true;
					break;
				}
			}
			if (!found)
				return;
			startActivity(share);
		}
	}
}
