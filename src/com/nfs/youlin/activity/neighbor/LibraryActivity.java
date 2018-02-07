package com.nfs.youlin.activity.neighbor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class LibraryActivity extends Activity{
	private ListView listView;
	private List<String> nameList=new ArrayList<String>();
	private List<String> phoneList=new ArrayList<String>();
	private List<String> addressList=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_committee);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("图书馆");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		nameList.add("图书馆名字1");
		phoneList.add("1234567891");
		addressList.add("图书馆地址1");
		nameList.add("图书馆名字2");
		phoneList.add("1234567892");
		addressList.add("图书馆地址2");
		listView=(ListView)findViewById(R.id.committee_lv);
		SimpleAdapter adapter=new SimpleAdapter(this, getData(), R.layout.list_committee_item, new String[]{"name","phone","address"},
				new int[]{R.id.name,R.id.phone,R.id.address});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneList.get(arg2)));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.police_station, menu);
//		return true;
//	}
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
	
	List<Map<String,Object>> getData(){
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		for (int i = 0; i < nameList.size(); i++) {
			Map<String, Object> map =new HashMap<String, Object>();
			map.put("name",nameList.get(i));
			map.put("phone",phoneList.get(i));
			map.put("address", addressList.get(i));
			list.add(map);
		}
		return list;
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
