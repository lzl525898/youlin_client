package com.nfs.youlin.activity.neighbor;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PropertyGonggaoApply extends Activity {
	Button button;
	String applyCancelStr;
	String applyStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property_gonggao_apply);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("申请小区公告管理员");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		applyStr="尊敬的用户,您提交了成为xxxx小区管理员的申请,优邻客服将会马上审核。";
		applyCancelStr="尊敬的用户,您取消了成为xxxx小区管理员的申请。";
		button = (Button)findViewById(R.id.admin_apply_bn);
		button.setText("申请管理员");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(button.getText().equals("申请管理员")){
					applyDialog();
					//myNotify(applyStr);
					button.setText("取消申请");
				}else if(button.getText().equals("取消申请")){
					applyCancleDialog();
				}
			}
		});
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void myNotify(String str){
		NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification=new Notification(R.drawable.youlin, "提示", 1000);
		notification.defaults=Notification.DEFAULT_ALL;
		PendingIntent intent=PendingIntent.getActivity(this, 0,new Intent(), 0);
		notification.setLatestEventInfo(this, "提示", str ,intent);
		manager.notify(0, notification);
	}
	
	public void applyDialog(){
		CustomDialog.Builder builder=new CustomDialog.Builder(this);
		builder.setMessage("您已提交管理员申请,优邻将马上审核您的申请");
		builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		builder.create().show();
	}
	
	public void applyCancleDialog(){
		CustomDialog.Builder builder=new CustomDialog.Builder(this);
		//builder.setTitle("提示");
		builder.setMessage("确定取消社区管理员申请？");
		builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				button.setText("申请管理员");
				//myNotify(applyCancelStr);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		builder.create().show();
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
