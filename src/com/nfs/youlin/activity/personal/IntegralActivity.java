package com.nfs.youlin.activity.personal;

import cn.jpush.android.api.JPushInterface;

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

import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.easemob.util.NetUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
import com.nfs.youlin.adapter.Jiangpinadapter;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkMonitorService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.Getgiftsuccess;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class IntegralActivity extends Activity implements OnRefreshListener<GridView>,OnClickListener{
	private ActionBar actionBar;
	private TextView creditdata;
	private PullToRefreshGridView jiangpingridview;
	private TextView mygift;
	private ImageView mygiftview;
	private YLProgressDialog dialog;
	private TextView netnotify;
	private TextView jifenRule;
	
	private List<Map<String, Object>> jiangpinlist = new ArrayList<Map<String, Object>>();
	private Jiangpinadapter madapter;
	private String httpflag = "none";
	private boolean getgiftsuccess = false;
	private String creditcount;
	private final int REQUEST_GET_GIFT = 4222;
	private Timer timer = new Timer();
	private TimerTask timertask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("积分");
		setContentView(R.layout.credit_detail_lay);
		actionBar = getActionBar();  
		actionBar.setDisplayHomeAsUpEnabled(true); 		 // 设置回退功能
		actionBar.setDisplayShowHomeEnabled(false);      // 取消图片显示
		creditdata = (TextView)findViewById(R.id.creditdate);
		jiangpingridview = (PullToRefreshGridView)findViewById(R.id.jiangpingridview);
		mygift = (TextView) findViewById(R.id.mygift);
		mygiftview = (ImageView) findViewById(R.id.mygiftview);
		netnotify = (TextView)findViewById(R.id.netpip);
		netnotify.setOnClickListener(this);
		jifenRule = (TextView) findViewById(R.id.creditinstruction);
		jifenRule.setOnClickListener(this);
		
		creditcount = getIntent().getStringExtra("creditdata");
		if(creditcount==null){
			creditcount="0";
		}
		creditdata.setText(creditcount);
		float height = creditdata.getHeight();
		float width = creditdata.getWidth();
		Loger.d("test4", height+"--------"+width);
		 Keyframe k0 = Keyframe.ofFloat(0f, 0.5f);
	     Keyframe k1 = Keyframe.ofFloat(0.275f, 1f);
	     Keyframe k2 = Keyframe.ofFloat(0.69f, 0.75f);
	     Keyframe k3 = Keyframe.ofFloat(1f, 1f);
	     Keyframe kw0 = Keyframe.ofFloat(0f, 0.5f);
	     Keyframe kw1 = Keyframe.ofFloat(0.275f, 1f);
	     Keyframe kw2 = Keyframe.ofFloat(0.69f, 0.75f);
	     Keyframe kw3 = Keyframe.ofFloat(1f, 1f);
	     
	     PropertyValuesHolder scaleX = PropertyValuesHolder.ofKeyframe("scaleX", kw0,kw1,kw2,kw3);
	     PropertyValuesHolder scaleY = PropertyValuesHolder.ofKeyframe("scaleY", k0,k1,k2,k3);
	     ObjectAnimator yAlphaBouncer = ObjectAnimator.ofPropertyValuesHolder(creditdata,scaleX,scaleY).setDuration(1300);
	     yAlphaBouncer.start();
	     timertask = new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Loger.d("test4", "等待网络");
					if(NetworkMonitorService.bNetworkMonitor){
						Loger.d("test4", "有网了..");
						Message message = new Message();  
						message.what = 1;  
						timerhandler.sendMessage(message);
						cancel();
					}	
				}
		};
		Getcredithttprequest(App.sUserLoginId);
		
		dialog = YLProgressDialog.createDialogwithcircle(this,"",1);
		dialog.show();
		jiangpinlist.clear();
		Getjiangpinlisthttprequest(App.sFamilyCommunityId);
		madapter = new Jiangpinadapter(IntegralActivity.this,jiangpinlist,Integer.parseInt(creditcount));
		jiangpingridview.setAdapter(madapter);
		jiangpingridview.setMode(Mode.PULL_FROM_END);
		jiangpingridview.setOnRefreshListener(this);
		jiangpingridview.setVisibility(View.VISIBLE);
		netnotify.setVisibility(View.GONE);
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
					madapter.notifyDataSetChanged();
					netnotify.setVisibility(View.GONE);
					jiangpingridview.setVisibility(View.VISIBLE);
				}else{
					netnotify.setVisibility(View.VISIBLE);
					jiangpingridview.setVisibility(View.GONE);
					if(!NetworkMonitorService.bNetworkMonitor){
						//没网
						netnotify.setText("网络已断开，等待重连");
						netnotify.setTag(1);
						netnotify.setTextColor(0xffcccccc);
						timer.schedule(timertask, 1000, 1000);
					}else{
						//有网 加载失败
						netnotify.setText("网络不稳定 请重新加载");
						netnotify.setTag(0);
						netnotify.setTextColor(0xffffba02);
						
					}
				}
				dialog.dismiss();
				httpflag = "none";
				getgiftsuccess = false;
			}
		}.execute();
		mygift.setOnClickListener(this);
		mygiftview.setOnClickListener(this);
	}
	
	private void Getjiangpinlisthttprequest(long community_id){
		RequestParams params = new RequestParams();
		params.put("community_id", community_id);
		params.put("tag", "getgiftlist");
		params.put("apitype", "exchange");
		
		AsyncHttpClient httpClient = new  AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					String giftflag = response.getString("flag");
					if(giftflag.equals("empty")){
						Toast.makeText(IntegralActivity.this, "礼品准备中，敬请期待...", Toast.LENGTH_SHORT).show();
						httpflag = "ok";
						getgiftsuccess = true;
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
//				Loger.d("test5",response.toString() );
				if(response.length()>0){
					httpflag = "ok";
				}
				getgiftsuccess = true;
				getdata(jiangpinlist,response);	
			}
		});
	}
	private void Getcredithttprequest(long user_id){
		RequestParams params = new RequestParams();
		params.put("user_id", user_id);
		params.put("tag", "usercredit");
		params.put("apitype", "users");
		
		AsyncHttpClient httpClient = new  AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					String credit = response.getString("credit");
					creditdata.setText(credit);
					creditcount = credit;
					madapter = new Jiangpinadapter(IntegralActivity.this,jiangpinlist,Integer.parseInt(creditcount));
					jiangpingridview.setAdapter(madapter);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	
	@Override
	protected void onResume() {
	// TODO Auto-generated method stub
		super.onResume();
		Getcredithttprequest(App.sUserLoginId);
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
		timer.cancel();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	private List<Map<String, Object>> getdata(List<Map<String, Object>> srclist,JSONArray array){
		for(int i=0;i<array.length();i++){
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("name", ((JSONObject)array.get(i)).get("gl_name").toString());
				map.put("credit", ((JSONObject)array.get(i)).get("gl_credit").toString());
				map.put("giftid", ((JSONObject)array.get(i)).get("gl_id").toString());
				map.put("imgurl", ((JSONObject)array.get(i)).get("gl_pic").toString());
				Loger.d("test5","name="+ ((JSONObject)array.get(i)).get("gl_name").toString()+
						" credit="+((JSONObject)array.get(i)).get("gl_credit").toString()+
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
	public void onRefresh(PullToRefreshBase<GridView> refreshView) {
		// TODO Auto-generated method stub
		if (refreshView.isShownFooter()) {
			
			jiangpinlist.clear();
			Getjiangpinlisthttprequest(App.sFamilyCommunityId);
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
						madapter.notifyDataSetChanged();
					}
					httpflag = "none";
					getgiftsuccess = false;
					jiangpingridview.onRefreshComplete();
				}
			}.execute();
			// 将下拉视图收起
			
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.mygift:
		case R.id.mygiftview:
			Intent intent = new Intent(IntegralActivity.this,Exchanged_giftactivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			break;
		case R.id.creditinstruction:
			startActivity(new Intent(IntegralActivity.this,CreditRuleActivity.class));
			break;
		case R.id.netpip:
			if(((TextView)v).getTag().toString().equals("0")){
				Getcredithttprequest(App.sUserLoginId);
				
				dialog = YLProgressDialog.createDialogwithcircle(this,"",1);
				dialog.show();
				jiangpinlist.clear();
				Getjiangpinlisthttprequest(App.sFamilyCommunityId);
				madapter = new Jiangpinadapter(IntegralActivity.this,jiangpinlist,Integer.parseInt(creditcount));
				jiangpingridview.setAdapter(madapter);
				jiangpingridview.setMode(Mode.PULL_FROM_END);
				jiangpingridview.setOnRefreshListener(this);
				netnotify.setVisibility(View.GONE);
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
							madapter.notifyDataSetChanged();
							netnotify.setVisibility(View.GONE);
							jiangpingridview.setVisibility(View.VISIBLE);
						}else{
							netnotify.setVisibility(View.VISIBLE);
							jiangpingridview.setVisibility(View.GONE);
							if(!NetworkMonitorService.bNetworkMonitor){
								//没网
								netnotify.setText("网络已断开，请联网后重试");
								netnotify.setTag(1);
								netnotify.setTextColor(0xffcccccc);
								timer.cancel();
								timer.schedule(timertask, 1000, 1000);
							}else{
								//有网 加载失败
								netnotify.setText("网络不稳定 请重新加载");
								netnotify.setTag(0);
								netnotify.setTextColor(0xffffba02);
								
							}
						}
						dialog.dismiss();
						httpflag = "none";
						getgiftsuccess = false;
					}
				}.execute();
				mygift.setOnClickListener(this);
				mygiftview.setOnClickListener(this);
			}else{
				return;
			}
			break;
		}
		
	}
	Handler timerhandler = new Handler() {  
		  public void handleMessage(Message msg) {  
			  if(msg.what == 1){
				  Getcredithttprequest(App.sUserLoginId);
					dialog = YLProgressDialog.createDialogwithcircle(IntegralActivity.this,"",1);
					dialog.show();
					jiangpinlist.clear();
					Getjiangpinlisthttprequest(App.sFamilyCommunityId);
					madapter = new Jiangpinadapter(IntegralActivity.this,jiangpinlist,Integer.parseInt(creditcount));
					jiangpingridview.setAdapter(madapter);
					jiangpingridview.setMode(Mode.PULL_FROM_END);
					jiangpingridview.setOnRefreshListener(IntegralActivity.this);
					netnotify.setVisibility(View.GONE);
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
								madapter.notifyDataSetChanged();
								netnotify.setVisibility(View.GONE);
								jiangpingridview.setVisibility(View.VISIBLE);
							}else{
								netnotify.setVisibility(View.VISIBLE);
								jiangpingridview.setVisibility(View.GONE);
								if(!NetworkMonitorService.bNetworkMonitor){
									//没网
									netnotify.setText("网络已断开，请联网后重试");
									netnotify.setTag(1);
									netnotify.setTextColor(0xffcccccc);
								}else{
									//有网 加载失败
									netnotify.setText("网络不稳定 请重新加载");
									netnotify.setTag(0);
									netnotify.setTextColor(0xffffba02);
								}
							}
							dialog.dismiss();
							httpflag = "none";
							getgiftsuccess = false;
						}
					}.execute();
					mygift.setOnClickListener(IntegralActivity.this);
					mygiftview.setOnClickListener(IntegralActivity.this);
			  }
		      super.handleMessage(msg);  
		  };  
	};  

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(REQUEST_GET_GIFT == requestCode){
			if(resultCode == RESULT_OK){
				Getgiftsuccess.createDialog(this).setMessage("").show();
				Toast.makeText(IntegralActivity.this, "兑换礼品成功!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
