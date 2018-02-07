package com.nfs.youlin.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.neighbor.AllNewshistoryList;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.PushRecord;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.TimeToStr;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

@SuppressLint("ViewHolder")
public class PushNewsDetailActivity extends ListActivity{
	private PushRecordDaoDBImpl recordDaoDBImpl;
	private List<Object> itemNewsList;
	private List<String> itemList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_news_detail);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("小区新闻");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		initData();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initData(){
		if(App.sUserLoginId<=0){
			AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(this);
			App.sUserLoginId = dbImpl.getUserId();
		}
		itemList = new ArrayList<String>();
		itemNewsList = new ArrayList<Object>();
		recordDaoDBImpl = new PushRecordDaoDBImpl(this);
		int objSize = recordDaoDBImpl.findAppointObjs(String.valueOf(2001),String.valueOf(App.sUserLoginId)).size();
		for(int i=0;i<objSize;i++){
			itemList.add("news");
			Bundle itemDetail = new Bundle();
			PushRecord record = ((PushRecord)(recordDaoDBImpl.findAppointObjs(String.valueOf(2001),String.valueOf(App.sUserLoginId)).get(i)));
			itemDetail.putString("time", TimeToStr.getTimeToStr(record.getPush_time()));
			JSONObject object = null;
			try {
				object = new JSONObject(record.getContent());
			} catch (JSONException e) {
				e.printStackTrace();
				object = null;
			}
			if(null!=object){
				try {
					itemDetail.putString("content", object.getString("message"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(PushNewsDetailActivity.this, "数据加载失败！", Toast.LENGTH_SHORT).show();
					finish();
				}
			}else{
				Toast.makeText(PushNewsDetailActivity.this, "数据加载失败！", Toast.LENGTH_SHORT).show();
				finish();
			}
			itemNewsList.add(itemDetail);
		}
		setListAdapter(new PushNewsAdapter(this, R.layout.item_news_detail,itemList,itemNewsList));
	}
	
	class PushNewsAdapter extends ArrayAdapter<String>{

		private int resource;
		private Context context;
		private List<Object> list=new ArrayList<Object>();
		
		public PushNewsAdapter(Context context, int resource, List<String> objects, List<Object> objs) {
			super(context, resource, objects);
			this.list = objs;
			this.context = context;
			this.resource = resource;
		}
		
		class ViewHolder{
			public TextView 	   newsTime;
			public TextView 	   newsPushInfo;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Bundle bundle = (Bundle) list.get(position);
			ViewHolder holder = null;
			holder = new ViewHolder();
			//创建布局
			convertView = new LinearLayout(this.context);   
			LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
	        inflater.inflate(this.resource, (LinearLayout)convertView, true);
	        holder.newsTime = (TextView) convertView.findViewById(R.id.tv_push_news_detail_time);
	        holder.newsPushInfo = (TextView) convertView.findViewById(R.id.tv_push_news_detail_info);
	        //赋值
	        holder.newsTime.setText(bundle.getString("time"));
	        holder.newsPushInfo.setText(bundle.getString("content"));
	        convertView.setTag(holder);
			return convertView;
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		startActivity(new Intent(PushNewsDetailActivity.this,AllNewshistoryList.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		finish();
		super.onListItemClick(l, v, position, id);
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
