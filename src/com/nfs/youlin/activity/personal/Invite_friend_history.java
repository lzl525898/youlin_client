package com.nfs.youlin.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.adapter.Invitehistoryadapter;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ClearSelectImg;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.CustomDialog;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.jpush.android.api.JPushInterface;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Invite_friend_history extends Activity{
	private YLProgressDialog youpd;
	private ActionBar actionbar;
	private TextView invitetitle;
	private ListView invitehistory;
	
	private int invitenum;
	private List<Map<String, Object>> invitelist = new ArrayList<Map<String,Object>>();
	private boolean getinviterequest = false;
	private Invitehistoryadapter madapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invitehistorylay);
		actionbar = getActionBar();
		actionbar.setTitle("邀请好友");
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayHomeAsUpEnabled(true);
		youpd = YLProgressDialog.createDialogwithcircle(this,"加载中...",1);
		invitetitle = (TextView)findViewById(R.id.invitetitle);
		invitehistory = (ListView)findViewById(R.id.invitelist);
		GetInvitehistoryhttprequest();
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
	private void GetInvitehistoryhttprequest(){
		RequestParams params = new RequestParams();
		params.put("user_id", App.sUserLoginId);
		params.put("tag", "getinviteinfo");
		params.put("apitype", "users");
		
		youpd.show();
		AsyncHttpClient httpClient = new  AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					String flag = response.getString("flag");
					JSONArray jsonArray=new JSONArray(response.getString("info"));
					if(flag.equals("no")){
						getinviterequest = true;
					}else if(flag.equals("ok")){
						Loger.i("test4","5555555--->"+response.toString());
						invitenum = Integer.valueOf(response.getString("count"));
						getdata(invitelist, jsonArray);
						getinviterequest = true;
//						invitetitle.setText(Html.fromHtml("<font color='#323232'>您成功邀请了 </font>"+"<font color='#ffba02'><b>"+invitenum+"</b></font>"+
//						"<font color='#323232'> 位好友注册优邻</font>"));
							invitetitle.setText(Html.fromHtml("<font color='#ffba02'><b>"+invitenum+"</b></font>"+"<font color='#323232'> 位好友成功注册优邻</font>"));
							madapter = new Invitehistoryadapter(Invite_friend_history.this, invitelist, R.layout.invitehistoryitem,
									new String[]{"inv_time","inv_phone","inv_status"},new int[]{R.id.inv_time,R.id.inv_phone,R.id.inv_status});
							invitehistory.setAdapter(madapter);
							invitehistory.setOnItemClickListener(new invhistoryitemclick(Invite_friend_history.this));
							AnimationSet set = new AnimationSet(false);  
							
//							Animation animation = new RotateAnimation(9,0);    //TranslateAnimation  控制画面平移的动画效果  
//							animation.setDuration(200);  
//							set.addAnimation(animation);
//							animation = new ScaleAnimation(0.5f,1f,0.5f,1f);    //RotateAnimation  控制画面角度变化的动画效果  
//							animation.setDuration(200);  
//							set.addAnimation(animation);
							Animation animation = new AlphaAnimation(0.5f,1f);    //RotateAnimation  控制画面角度变化的动画效果  
							animation.setDuration(200);  
							set.addAnimation(animation);
							 animation = new TranslateAnimation(-31, 0, -50, 0);    //RotateAnimation  控制画面角度变化的动画效果  
							animation.setDuration(200);
//							animation.setFillAfter(true);
							set.addAnimation(animation);
							
							LayoutAnimationController controller = new LayoutAnimationController(set);  
							controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
							invitehistory.setLayoutAnimation(controller);					
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				youpd.cancel();
			}
			@Override
			public void onSuccess(int statusCode,Header[] headers,JSONArray response) {
				super.onSuccess(statusCode,headers,response);
			}
		});
	}
	private List<Map<String, Object>> getdata(List<Map<String, Object>> adapterlist ,JSONArray srcdata){
		for(int i=0;i<srcdata.length();i++){
			Map<String, Object> hashmap = new HashMap<String, Object>();
			try {
				hashmap.put("inv_time", ((JSONObject)srcdata.get(i)).getString("inv_time"));
				hashmap.put("inv_phone", ((JSONObject)srcdata.get(i)).getString("inv_phone"));
				hashmap.put("inv_status", ((JSONObject)srcdata.get(i)).getString("inv_status"));
				hashmap.put("inv_type", ((JSONObject)srcdata.get(i)).getString("inv_type"));
				adapterlist.add(hashmap);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return adapterlist;
	}
	private class invhistoryitemclick implements OnItemClickListener{
		private final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		private final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
		private Intent sentIntent;
		private Intent deliverIntent;
		private PendingIntent sentPI;
		private PendingIntent deliverPI; 
		private boolean bSMSstatus = false;
		private final String down_url = IHttpRequestUtils.URL+"adminpush/index";
		private int inv_type = 0;
		private int inv_status =0;
		private String contactorphone;
		private Context mContext;
		public invhistoryitemclick(Context mcontext){
			sentIntent = new Intent(SENT_SMS_ACTION);
			sentPI = PendingIntent.getBroadcast(mcontext, 0, sentIntent, 0);
			deliverIntent = new Intent(DELIVERED_SMS_ACTION);
			deliverPI = PendingIntent.getBroadcast(mcontext, 0, deliverIntent, 0);
			mContext = mcontext;
		}
		@Override
		public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
			// TODO Auto-generated method stub
			inv_type = Integer.parseInt(invitelist.get(position).get("inv_type").toString());
			inv_status = Integer.parseInt(invitelist.get(position).get("inv_status").toString());
			contactorphone = invitelist.get(position).get("inv_phone").toString();
			if(inv_status == 3){
				CustomDialog.Builder builder=new CustomDialog.Builder(mContext);
				builder.setTitle("重新邀请此好友");
				//builder.setCancelable(false);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						requestInviteCode(position);
						dialog.cancel();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
				builder.create().show();
				
			}else if(inv_status == 2){
				Toast.makeText(mContext, "邀请已经成功", Toast.LENGTH_SHORT).show();
			}
//			else{
//				Toast.makeText(mContext, "邀请中，请耐心等候", Toast.LENGTH_SHORT).show();
//			}
			
		}
		private void requestInviteCode(int position){
			Loger.i("TEST", "开始调用==》requestInviteCode");
			RequestParams params = new RequestParams();
			params.put("user_id", App.sUserLoginId);
			params.put("inv_phone", invitelist.get(position).get("inv_phone"));
			params.put("inv_status", 0);//常量值0
			params.put("inv_type", invitelist.get(position).get("inv_type"));
			params.put("family_id", App.sFamilyId);
			params.put("tag", "getinvcode");
			params.put("apitype", "users");
			Loger.i("TEST", "开始调用==》user_id="+App.sUserLoginId+"inv_phone="+invitelist.get(position).get("inv_phone")+
					"inv_type="+invitelist.get(position).get("inv_type"));
			AsyncHttpClient httpClient = new  AsyncHttpClient();
			httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
					new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					try {
						String flag = response.getString("flag");
						Loger.i("TEST", "response=>"+response.toString());
//						String yl_msg = response.getString("yl_msg");
//						Toast.makeText(Invite_friend_dealactivity.this, yl_msg, Toast.LENGTH_SHORT).show();
						if(flag.equals("ok")){
							//调用手机短信
							final String invCode = response.getString("inv_code");
							Loger.i("TEST", "返回的邀请码==>"+invCode);
							bSMSstatus = false;
							String contentWithSMS = "NULL";
							if(inv_type==1){
								contentWithSMS = "【优邻】邀请码:"+invCode+"。快来加入优邻。去看看"+down_url;
							}else if(inv_type==2){
								contentWithSMS = "【优邻】邀请码:"+invCode+"。快来加入优邻。去看看"+down_url;
							}
							sendSMS(contactorphone,contentWithSMS);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									youpd.show();
								}
							});
							registerReceiver(new BroadcastReceiver(){
								@Override
								public void onReceive(Context context, Intent intent) {
									Loger.i("TEST", "接受者已经接收成功");
								}
							}, new IntentFilter(DELIVERED_SMS_ACTION));
							registerReceiver(new BroadcastReceiver() {
								@Override
								public void onReceive(Context context, Intent intent) {
									// TODO Auto-generated method stub
									switch (getResultCode()) {
									case Activity.RESULT_OK:
										Loger.i("TEST", "短信发送成功");
										bSMSstatus = true;
										break;
									case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
										Loger.i("TEST", "RESULT_ERROR_GENERIC_FAILURE");
										Toast.makeText(mContext, "请开启发送短信权限", Toast.LENGTH_LONG).show();
										break;
									case SmsManager.RESULT_ERROR_RADIO_OFF:
										Loger.i("TEST", "RESULT_ERROR_RADIO_OFF");
										break;
									case SmsManager.RESULT_ERROR_NULL_PDU:
										Loger.i("TEST", "RESULT_ERROR_NULL_PDU");
										break;
									default:
										break;
									}
								}
							}, new IntentFilter(SENT_SMS_ACTION));
							new AsyncTask<Void, Void, Void>(){
								@Override
								protected Void doInBackground(Void... params) {
									Long currenttime = System.currentTimeMillis();
									while(true){
										if(((System.currentTimeMillis()-currenttime)>8000) || bSMSstatus){
											Loger.i("TEST", "bSMSstatus==>"+bSMSstatus);
											break;
										}
									}
									return null;
								}
								protected void onPostExecute(Void result) {
									if(!bSMSstatus){
										Loger.i("TEST", "手机没有赋予发送短信权限");
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												// TODO Auto-generated method stub
												youpd.dismiss();
											}
										});
										Toast.makeText(mContext, "请开启发送短信权限", Toast.LENGTH_LONG).show();
										return;
									}else{
										Loger.i("TEST", "手机给app赋予发送短信权限");
										PostInvitehttprequest(contactorphone,invCode, App.sFamilyId);
									}
								};
							}.execute();
						}else if(flag.equals("overflow")){
							String msgInfo = response.getString("yl_msg");
							Toast.makeText(mContext, msgInfo, Toast.LENGTH_LONG).show();
							youpd.cancel();
						}else if(flag.equals("exist")){//
							String msgInfo = response.getString("yl_msg");
							Toast.makeText(mContext, msgInfo, Toast.LENGTH_LONG).show();
							youpd.cancel();
						}else{
							youpd.cancel();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		
		/*user_id==>发起邀请者用户ID
	inv_phone==>被邀请用户手机
	inv_type==>邀请类型 1表示家人 2表示朋友
	family_id==>发起邀请者familyId
	tag==>invitenewusers
	apitype==>users*/
		private void PostInvitehttprequest(String inv_phone,String inv_code,long family_id){
			RequestParams params = new RequestParams();
			params.put("user_id", App.sUserLoginId);
			params.put("inv_phone", inv_phone);
			params.put("inv_type", inv_type);
			params.put("family_id", family_id);
			params.put("inv_code", inv_code);
			params.put("tag", "invitenewusers");
			params.put("apitype", "users");
			AsyncHttpClient httpClient = new  AsyncHttpClient();
			Loger.i("TEST", "开始POST==>PostInvitehttprequest");
			httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
					new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					Loger.i("TEST", "开始执行PostInvitehttprequest=>"+response.toString());
					try {
						String flag = response.getString("flag");
						String yl_msg = response.getString("yl_msg");
						Toast.makeText(mContext, yl_msg, Toast.LENGTH_SHORT).show();
						if(flag.equals("ok")){
							inv_type = 0;
							youpd.cancel();
						}else{
							youpd.cancel();
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString,
						Throwable throwable) {
					new ErrorServer(Invite_friend_history.this, responseString.toString());
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		}
		private void sendSMS(String phoneNumber, String message){
			android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
			List<String> divideContents = smsManager.divideMessage(message);
			for(String text : divideContents){
				smsManager.sendTextMessage(phoneNumber, null, text, sentPI, deliverPI);
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
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
