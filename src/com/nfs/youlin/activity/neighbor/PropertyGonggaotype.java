package com.nfs.youlin.activity.neighbor;

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
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class PropertyGonggaotype extends Activity implements OnClickListener {
	private TextView newTopicTv1;
	private TextView newTopicTv2;
	private TextView newTopicTv3;
	private TextView newTopicTv4;
	private ImageView newTopicImg1;
	private ImageView newTopicImg2;
	private ImageView newTopicImg3;
	private ImageView newTopicImg4;
	private String rangeStr;
	private int CHOOSE_GONGGAO_TYPE = 1;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gonggao_type);
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getWidth() * 0.2);
		 //高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.9); //宽度设置为屏幕的0.8
		p.x = 0;
		p.y = 0;
		getWindow().setAttributes(p);
		newTopicTv1 = (TextView) findViewById(R.id.gonggao_range_tv1);
		newTopicTv2 = (TextView) findViewById(R.id.gonggao_range_tv2);
		newTopicTv3 = (TextView) findViewById(R.id.gonggao_range_tv3);
		newTopicTv4 = (TextView) findViewById(R.id.gonggao_range_tv4);
		newTopicImg1 = (ImageView) findViewById(R.id.gonggao_range_img1);
		newTopicImg2 = (ImageView) findViewById(R.id.gonggao_range_img2);
		newTopicImg3 = (ImageView) findViewById(R.id.gonggao_range_img3);
		newTopicImg4 = (ImageView) findViewById(R.id.gonggao_range_img4);

		newTopicTv1.setOnClickListener(this);
		newTopicTv2.setOnClickListener(this);
		newTopicTv3.setOnClickListener(this);
		newTopicTv4.setOnClickListener(this);
		intent = new Intent();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.gonggao_range_tv1:
			newTopicImg1.setBackgroundResource(R.drawable.btn_xuanzhong);
			
			intent.putExtra("Gonggaotype", newTopicTv1.getText());
			PropertyGonggaotype.this.setResult(RESULT_OK, intent);
			break;
		case R.id.gonggao_range_tv2:
			newTopicImg2.setBackgroundResource(R.drawable.btn_xuanzhong);
			intent.putExtra("Gonggaotype", newTopicTv2.getText());
			PropertyGonggaotype.this.setResult(RESULT_OK, intent);
			break;
		case R.id.gonggao_range_tv3:
			newTopicImg3.setBackgroundResource(R.drawable.btn_xuanzhong);
			intent.putExtra("Gonggaotype", newTopicTv3.getText());
			PropertyGonggaotype.this.setResult(RESULT_OK, intent);
			break;
		case R.id.gonggao_range_tv4:
			newTopicImg4.setBackgroundResource(R.drawable.btn_xuanzhong);
			intent.putExtra("Gonggaotype", newTopicTv4.getText());
			PropertyGonggaotype.this.setResult(RESULT_OK, intent);
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
