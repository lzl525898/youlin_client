package com.nfs.youlin.push;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.TimeToStr;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class PushRecordDetailActivity extends Activity {
	private ActionBar actionBar;
	private TextView pushTimeTextView, pushInfoTextView;
	private String pushTitle, pushContent, pushTime, pushJSONString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		pushJSONString = intent.getStringExtra("pushInfo");
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(pushJSONString);
			pushTitle   = jsonObject.getString("title");
			pushContent = jsonObject.getString("content");
			pushTime    = jsonObject.getString("pushTime");
			setTitle(pushTitle);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.actionBar = getActionBar();
		this.actionBar.setDisplayHomeAsUpEnabled(true);
		this.actionBar.setDisplayShowHomeEnabled(false);
		this.setContentView(R.layout.activity_push_record_detail);
		this.pushTimeTextView = (TextView) findViewById(R.id.tv_push_detail_time);
		this.pushInfoTextView = (TextView) findViewById(R.id.tv_push_detail_info);
		this.pushTimeTextView.setText(TimeToStr.getTimeToStr(Long.parseLong(pushTime)));
		this.pushInfoTextView.setText(pushContent);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(android.R.id.home == item.getItemId()){
			finish();
			return true;
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
