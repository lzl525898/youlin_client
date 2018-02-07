package com.nfs.youlin.activity.personal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.number_fragment;
import com.nfs.youlin.view.rooft_fragment;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Searchresultlistactivity extends Activity{
	private ListView searchresultlistiew;
	private Intent intent;
	private TextView titletext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchresultlistlayout);
		searchresultlistiew = (ListView) findViewById(R.id.address_search_result);
		titletext = (TextView)findViewById(R.id.addrtitletext);
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		 p.height = (int) (d.getHeight() * 0.35);
		 //高度设置为屏幕的1.0
		 p.width = (int) (d.getWidth() * 0.8); //宽度设置为屏幕的0.8
		p.x = 0;
		p.y = 0;
		getWindow().setAttributes(p);
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		intent = getIntent();
		final String where = intent.getStringExtra("startactivity");
		final SimpleAdapter adapter = new SimpleAdapter(Searchresultlistactivity.this, write_address.searchresult, R.layout.villagelist,
				new String[] { "name"}, 
				new int[] { R.id.villagename });
		if(where.equals("rooft_fragment")){
			titletext.setText("请选择下列楼栋号");
		}else{
			titletext.setText("请选择下列门牌号");
		}
		searchresultlistiew.setAdapter(adapter);
		searchresultlistiew.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(where.equals("rooft_fragment")){
					rooft_fragment.rooftmessage.setText( write_address.searchresult.get(arg2).get("name").toString());
					rooft_fragment.rooftmessage.setSelection(rooft_fragment.rooftmessage.getText().toString().length());
					Loger.d("test4","click text size="+rooft_fragment.rooftmessage.getText().toString().length());
				}else{
					number_fragment.numbermessage.setText( write_address.searchresult.get(arg2).get("name").toString());
					number_fragment.numbermessage.setSelection(number_fragment.numbermessage.getText().toString().length());
					Loger.d("test4","click text size="+number_fragment.numbermessage.getText().toString().length());
				}
				Searchresultlistactivity.this.finish();
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
