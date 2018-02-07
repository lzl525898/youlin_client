package com.nfs.youlin.utils;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class error_logtext extends Activity {
	Intent intent;
	TextView text1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.error_logtext);
		intent = getIntent();
		text1 = (TextView) findViewById(R.id.test22);
		String error = intent.getStringExtra("error");
		text1.setText(error);
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
