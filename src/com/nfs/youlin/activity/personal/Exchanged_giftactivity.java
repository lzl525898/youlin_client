package com.nfs.youlin.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nfs.youlin.R;
import com.nfs.youlin.adapter.Exchanged_gift_adapter;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

public class Exchanged_giftactivity extends Activity implements OnClickListener,OnRefreshListener<ListView> {
	private ActionBar actionBar;
	private LinearLayout unexchangedgift;
	private LinearLayout exchangedgift;
	private ImageView unexchangedview;
	private ImageView exchangedview;
	private TextView unexchangedtextview;
	private TextView exchangedtextview;
	private  PullToRefreshListView  giftlist;
	private TextView nomoregifttext;
	private YLProgressDialog dialog;
	
	private String httpflag = "none";
	private boolean getgiftsuccess = false;
	private int currentlist_type =1;
	private List<Map<String, Object>> gettinggiftlist = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> finishedgiftlist = new ArrayList<Map<String, Object>>();
	private Exchanged_gift_adapter madapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.giftexchangedlist);
		setTitle("我的礼品");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		unexchangedgift = (LinearLayout) findViewById(R.id.unexchangedgift);
		exchangedgift = (LinearLayout)findViewById(R.id.exchangedgift);
		unexchangedview = (ImageView) findViewById(R.id.unexchangedview);
		exchangedview = (ImageView) findViewById(R.id.exchangedview);
		unexchangedtextview = (TextView)findViewById(R.id.gftext1);
		exchangedtextview = (TextView)findViewById(R.id.gftext2);
		nomoregifttext = (TextView)findViewById(R.id.nomoregift);
		giftlist = (PullToRefreshListView) findViewById(R.id.giftlist);
		
		dialog = YLProgressDialog.createDialogwithcircle(this,"",1);
		dialog.show();
		Getcredithttprequest(1, 1);
		madapter = new Exchanged_gift_adapter(this,gettinggiftlist);
		giftlist.setAdapter(madapter);
		giftlist.setMode(Mode.PULL_FROM_END);
		giftlist.setOnRefreshListener(this);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {

				Long currenttime = System.currentTimeMillis();
					while(!getgiftsuccess){
						if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+5000)){
							getgiftsuccess = true;
						}
					}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if(getgiftsuccess&& httpflag.equals("ok")){
					Loger.d("test5", "getgiftsuccess&& httpflag.equals(ok)");
					madapter.notifyDataSetChanged();
				}
				dialog.dismiss();
				getgiftsuccess = false;
				httpflag = "none";
			}
		}.execute();
		unexchangedgift.setOnClickListener(this);
		exchangedgift.setOnClickListener(this);
		
	}
	private void Getcredithttprequest(final int action_type,int list_type){
		
		String ue_id;
		RequestParams params = new RequestParams();
		final List<Map<String, Object>> currentlist;
		if(list_type == 1){
			currentlist = gettinggiftlist;
		}else{
			currentlist = finishedgiftlist;
		}
		if(currentlist.size() == 0){
			ue_id = "0";
			params.put("action_type", 1);
		}else{
			if(action_type == 2)
				ue_id = currentlist.get(currentlist.size()-1).get("giftid").toString();
			else
				ue_id = "0";
			params.put("action_type", action_type);
		}
		
		
		Loger.d("test5","ue_id="+ue_id+"  action_type="+action_type+"   list_type="+list_type);
		
		params.put("user_id", App.sUserLoginId);
		params.put("community_id", App.sFamilyCommunityId);
		
		params.put("ue_id", ue_id);
		params.put("list_type", list_type);
		params.put("tag", "getmygiftlist");
		params.put("apitype", "exchange");
		AsyncHttpClient httpClient = new  AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Loger.d("test5","no gift object="+response);
				try {
					String flag = response.getString("flag");
					if("no".equals(flag)){
						getgiftsuccess = true;
						httpflag = "ok";
						nomoregifttext.setVisibility(View.VISIBLE);
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									Exchanged_giftactivity.this.runOnUiThread(new Runnable() {
										public void run() {
											nomoregifttext.setVisibility(View.GONE);
										}
									});
								}
							}, 1000);

					}else{

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				
				if(action_type == 2){
					adddata(currentlist, response);
				}else{
					currentlist.clear();
					getdata(currentlist, response);
				}
				
				httpflag = "ok";
				getgiftsuccess = true;
				
				Loger.d("test5", response.toString());
				Loger.d("test5", "gettinggiftlist = "+gettinggiftlist.toString());
				Loger.d("test5", "finishedgiftlist= "+finishedgiftlist.toString());
				
			}
		});
	}
	//[{"gl_name":"超级大豆油","ue_credit":300,"ue_id":4,"ue_count":3,"ue_status":1,"user_id":20,"community_id":1,"ue_time":1456820191993,"ue_glid":1},
	private List<Map<String, Object>> getdata(List<Map<String, Object>> srclist,JSONArray array){
		for(int i=0;i<array.length();i++){
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("name", ((JSONObject)array.get(i)).get("gl_name").toString());
				map.put("credit", ((JSONObject)array.get(i)).get("ue_credit").toString());
				map.put("giftid", ((JSONObject)array.get(i)).get("ue_id").toString());
				map.put("giftnum", ((JSONObject)array.get(i)).get("ue_count").toString());
				map.put("giftimg", ((JSONObject)array.get(i)).get("gl_pic").toString());
				map.put("gifttime", ((JSONObject)array.get(i)).get("ue_time").toString());
				map.put("giftstatus", ((JSONObject)array.get(i)).get("ue_status").toString());
				
				Loger.d("test5","name="+ ((JSONObject)array.get(i)).get("gl_name").toString()+
						" credit="+((JSONObject)array.get(i)).get("ue_credit").toString()+
						" imgurl="+((JSONObject)array.get(i)).get("gl_pic").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			srclist.add(map);
		}
		return srclist;
	}
	private List<Map<String, Object>> adddata(List<Map<String, Object>> srclist,JSONArray array){
		for(int i=0;i<array.length();i++){
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("name", ((JSONObject)array.get(i)).get("gl_name").toString());
				map.put("credit", ((JSONObject)array.get(i)).get("ue_credit").toString());
				map.put("giftid", ((JSONObject)array.get(i)).get("ue_id").toString());
				map.put("giftnum", ((JSONObject)array.get(i)).get("ue_count").toString());
				map.put("giftimg", ((JSONObject)array.get(i)).get("gl_pic").toString());
				map.put("gifttime", ((JSONObject)array.get(i)).get("ue_time").toString());
				map.put("giftstatus", ((JSONObject)array.get(i)).get("ue_status").toString());
				
				Loger.d("test5","name="+ ((JSONObject)array.get(i)).get("gl_name").toString()+
						" credit="+((JSONObject)array.get(i)).get("ue_credit").toString()+
						" imgurl="+((JSONObject)array.get(i)).get("gl_pic").toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			srclist.add(map);
		}
		return srclist;
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.unexchangedgift:
			currentlist_type =1;
			unexchangedview.setBackgroundColor(0xfffa5b11);
			unexchangedtextview.setTextColor(0xfffa5b11);
			exchangedview.setBackgroundColor(0xfffefefe);
			exchangedtextview.setTextColor(0xff909090);
			Loger.d("test5", "------"+gettinggiftlist.toString());
			madapter = new Exchanged_gift_adapter(Exchanged_giftactivity.this,gettinggiftlist);
			giftlist.setAdapter(madapter);
			giftlist.setMode(Mode.PULL_FROM_END);
			giftlist.setOnRefreshListener(Exchanged_giftactivity.this);
			Getcredithttprequest(1, 1);
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {

					Long currenttime = System.currentTimeMillis();
						while(!getgiftsuccess){
							if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+5000)){
								getgiftsuccess = true;
							}
						}
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					if(getgiftsuccess&& httpflag.equals("ok")){
						
						Loger.d("test5", "getgiftsuccess&& httpflag.equals(ok)");
						madapter.notifyDataSetChanged();
//						madapter = new Exchanged_gift_adapter(Exchanged_giftactivity.this,gettinggiftlist);
//						giftlist.setAdapter(madapter);
//						giftlist.setMode(Mode.PULL_FROM_END);
//						giftlist.setOnRefreshListener(Exchanged_giftactivity.this);
					}
					getgiftsuccess = false;
					httpflag = "none";
				}
			}.execute();
			break;
		case R.id.exchangedgift:
			currentlist_type =2;
			unexchangedview.setBackgroundColor(0xfffefefe);
			unexchangedtextview.setTextColor(0xff909090);
			exchangedview.setBackgroundColor(0xfffa5b11);
			exchangedtextview.setTextColor(0xfffa5b11);
			madapter = new Exchanged_gift_adapter(Exchanged_giftactivity.this,finishedgiftlist);
			giftlist.setAdapter(madapter);
			giftlist.setMode(Mode.PULL_FROM_END);
			giftlist.setOnRefreshListener(Exchanged_giftactivity.this);
			Getcredithttprequest(1, 2);
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {

					Long currenttime = System.currentTimeMillis();
						while(!getgiftsuccess){
							if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+5000)){
								getgiftsuccess = true;
							}
						}
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					if(getgiftsuccess&& httpflag.equals("ok")){
						Loger.d("test5", "getgiftsuccess&& httpflag.equals(ok)");
						madapter.notifyDataSetChanged();
//						madapter = new Exchanged_gift_adapter(Exchanged_giftactivity.this,finishedgiftlist);
//						giftlist.setAdapter(madapter);
//						giftlist.setMode(Mode.PULL_FROM_END);
//						giftlist.setOnRefreshListener(Exchanged_giftactivity.this);
					}
					getgiftsuccess = false;
					httpflag = "none";
				}
			}.execute();
			break;
		}
	}
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (refreshView.isShownFooter()) {
			Loger.d("test5", "1111111111111111");
//			madapter.notifyDataSetChanged();
//			giftlist.onRefreshComplete();
			Getcredithttprequest(2, currentlist_type);
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {

					Long currenttime = System.currentTimeMillis();
						while(!getgiftsuccess){
							if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+5000)){
								getgiftsuccess = true;
							}
						}
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					if(getgiftsuccess&& httpflag.equals("ok")){
						Loger.d("test5", "getgiftsuccess&& httpflag.equals(ok)");
						madapter.notifyDataSetChanged();
						
					}
					giftlist.onRefreshComplete();
					getgiftsuccess = false;
					httpflag = "none";
				}
			}.execute();
			// 将下拉视图收起
//			giftlist.onRefreshComplete();
		}
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
