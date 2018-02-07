package com.nfs.youlin.activity.titlebar.newtopic;

import cn.jpush.android.api.JPushInterface;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.titlebar.startactivity.SendActivity;
import com.nfs.youlin.activity.titlebar.startactivity.SendActivityAddress;
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

public class NewTopicRange extends Activity implements OnClickListener {
	private TextView newTopicTv1;
	private LinearLayout newTopicTv2;
	private LinearLayout newTopicTv3;
	private LinearLayout newTopicTv4;
	private ImageView newTopicImg1;
	private ImageView newTopicImg2;
	private ImageView newTopicImg3;
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
		newTopicTv1 = (TextView) findViewById(R.id.new_topic_range_tv1);
		newTopicTv2 = (LinearLayout) findViewById(R.id.new_topic_range_tv2);
		newTopicTv3 = (LinearLayout) findViewById(R.id.new_topic_range_tv3);
		newTopicTv4 = (LinearLayout) findViewById(R.id.new_topic_range_tv4);
		newTopicImg1 = (ImageView) findViewById(R.id.new_topic_range_img1);
		newTopicImg2 = (ImageView) findViewById(R.id.new_topic_range_img2);
		newTopicImg3 = (ImageView) findViewById(R.id.new_topic_range_img3);
		newTopicTv1.setText(village);
		rangeStr=newTopicTv1.getText().toString();
		newTopicImg1.setBackgroundResource(R.drawable.btn_xuanzhong);
		newTopicTv2.setOnClickListener(this);
		newTopicTv3.setOnClickListener(this);
		newTopicTv4.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.new_topic_range_tv2:
			//NewTopic.newTopicTv.setText(rangeStr+"(仅本小区可见)");
			NewTopic.newTopicTv.setText("本小区");
			NewTopic.nforumId = 0;
			newTopicImg1.setBackgroundResource(R.drawable.btn_xuanzhong);
			break;
		case R.id.new_topic_range_tv3:
			//NewTopic.newTopicTv.setText(rangeStr+"(周边可见)");
			NewTopic.newTopicTv.setText("周边");
			NewTopic.nforumId = 1;
			newTopicImg1.setBackgroundResource(R.drawable.btn_weixuanzhong);
			newTopicImg2.setBackgroundResource(R.drawable.btn_xuanzhong);
			break;
		case R.id.new_topic_range_tv4:
			//NewTopic.newTopicTv.setText(rangeStr+"(同城可见)");
			NewTopic.newTopicTv.setText("同城");
			NewTopic.nforumId = 2;
			newTopicImg1.setBackgroundResource(R.drawable.btn_weixuanzhong);
			newTopicImg3.setBackgroundResource(R.drawable.btn_xuanzhong);
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
