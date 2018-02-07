package com.nfs.youlin.activity.neighbor;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.UpdataInfo;
import com.umeng.analytics.MobclickAgent;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PropertyAboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property_about);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("关于物业");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		RelativeLayout aboutLayout=(RelativeLayout)findViewById(R.id.about_layout);
		final TextView telPhoneTv=(TextView)findViewById(R.id.about_call_tv2);
		TextView addressTv=(TextView)findViewById(R.id.about_address_tv2);
		TextView timeTv=(TextView)findViewById(R.id.about_time_tv2);
		telPhoneTv.setText("1231231231");
		addressTv.setText("xxxxxxxxxxxxxxxx");
		timeTv.setText("xxxxxxxxxxxxxx");
		aboutLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+telPhoneTv.getText().toString()));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==android.R.id.home){
			finish();
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
	
}
