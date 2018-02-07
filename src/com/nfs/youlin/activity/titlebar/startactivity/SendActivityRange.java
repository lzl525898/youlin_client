package com.nfs.youlin.activity.titlebar.startactivity;

import cn.jpush.android.api.JPushInterface;
import com.nfs.youlin.R;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SendActivityRange extends Activity implements OnClickListener {
	private TextView send_range_tv1;
	private LinearLayout send_range_tv2;
	private LinearLayout send_range_tv3;
	private LinearLayout send_range_tv4;
	private ImageView send_range_img1;
	private ImageView send_range_img2;
	private ImageView send_range_img3;
	private String rangeStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_topic_range);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("选择可见范围");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		Intent intent = this.getIntent();
		String village = intent.getStringExtra("village");
		send_range_tv1 = (TextView) findViewById(R.id.new_topic_range_tv1);
		send_range_tv2 = (LinearLayout) findViewById(R.id.new_topic_range_tv2);
		send_range_tv3 = (LinearLayout) findViewById(R.id.new_topic_range_tv3);
		send_range_tv4 = (LinearLayout) findViewById(R.id.new_topic_range_tv4);
		send_range_img1 = (ImageView) findViewById(R.id.new_topic_range_img1);
		send_range_img2 = (ImageView) findViewById(R.id.new_topic_range_img2);
		send_range_img3 = (ImageView) findViewById(R.id.new_topic_range_img3);
		
		send_range_tv1.setText(village);
		rangeStr=send_range_tv1.getText().toString();
		send_range_img1.setBackgroundResource(R.drawable.btn_xuanzhong);
		send_range_tv2.setOnClickListener(this);
		send_range_tv3.setOnClickListener(this);
		send_range_tv4.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.new_topic_range_tv2:
			//SendActivity.sendToTv.setText(rangeStr+"(仅本小区可见)");
			SendActivity.sendToTv.setText("本小区");
			send_range_img1.setBackgroundResource(R.drawable.btn_xuanzhong);
			SendActivity.nactionId = 0;
			break;
		case R.id.new_topic_range_tv3:
			//SendActivity.sendToTv.setText(rangeStr+"(周边可见)");
			SendActivity.sendToTv.setText("周边");
			send_range_img1.setBackgroundResource(R.drawable.btn_weixuanzhong);
			send_range_img2.setBackgroundResource(R.drawable.btn_xuanzhong);
			SendActivity.nactionId = 1;
			break;
		case R.id.new_topic_range_tv4:
			//SendActivity.sendToTv.setText(rangeStr+"(同城可见)");
			SendActivity.sendToTv.setText("同城");
			send_range_img1.setBackgroundResource(R.drawable.btn_weixuanzhong);
			send_range_img3.setBackgroundResource(R.drawable.btn_xuanzhong);
			SendActivity.nactionId = 2;
			break;
		default:
			break;
		}
		finish();
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
