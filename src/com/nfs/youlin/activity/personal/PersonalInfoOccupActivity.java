package com.nfs.youlin.activity.personal;

import java.io.File;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.RegisterActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.SwitchView;
import com.nfs.youlin.view.SwitchView.OnStateChangedListener;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
@SuppressLint("HandlerLeak")
public class PersonalInfoOccupActivity extends Activity {
	private TextView goBackTv;
	private TextView finishTv;
	private SwitchView switchView;
	private EditText changeOccupEt;
	private AccountDaoDBImpl accountDaoDBImpl;
	private Account account;
	private String changeOccupStr;
	private NeighborsHttpRequest httpRequestOpen;
	private final int SUCCESS_CODE = 100;
	private final int FAILED_CODE = 101;
	private final int REFUSE_CODE = 103;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCESS_CODE:
				PersonalInformationActivity.occupationTv.setText(changeOccupStr);
				account.setUser_vocation(changeOccupStr);
				accountDaoDBImpl.modifyObject(account);
				accountDaoDBImpl.releaseDatabaseRes();
				Loger.i("TEST", "ok...................");
				break;
			case FAILED_CODE:
				Loger.i("TEST", "no...................");
				break;
			case REFUSE_CODE:
				Loger.i("TEST", "error................");
				break;
			default:
				break;
			}
		};
	};
	private Handler occupHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCESS_CODE:
				Loger.i("TEST", "ok.....................");
				break;
			case FAILED_CODE:
				Loger.i("TEST", "no...................");
				break;
			case REFUSE_CODE:
				Loger.i("TEST", "error................");
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info_occup);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("职业");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		
		accountDaoDBImpl=new AccountDaoDBImpl(this);
		account=accountDaoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
		String defaultOccup=PersonalInformationActivity.occupationTv.getText().toString();
		changeOccupEt=(EditText)findViewById(R.id.occupName);
		changeOccupEt.setText(defaultOccup.equals("null")?"":defaultOccup);
		
		switchView=(SwitchView)findViewById(R.id.occup_switch);
		getOpenStatus("null");
		
		switchView.setOnStateChangedListener(new OnStateChangedListener() {
			@Override
			public void toggleToOn() {
				// TODO Auto-generated method stub
				if(NetworkService.networkBool){
					switchView.toggleSwitch(true);
					getOpenStatus("on");
				}else{
					switchView.toggleSwitch(false);
					Toast.makeText(PersonalInfoOccupActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				}
//				switchView.postDelayed(new Runnable() {
//		            @Override 
//		            public void run() {
//		            	switchView.toggleSwitch(true); //以动画效果切换到打开的状态
//		            }},200);
			}
			@Override
			public void toggleToOff() {
				// TODO Auto-generated method stub
				if(NetworkService.networkBool){
					switchView.toggleSwitch(false);
					getOpenStatus("off");
				}else{
					switchView.toggleSwitch(true);
					Toast.makeText(PersonalInfoOccupActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_info_signature, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.finish:
			OnClickFinish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	public void OnClickFinish(){
		changeOccupStr=changeOccupEt.getText().toString().trim();
		//int nStatus = account.getUser_public_status();
		//Loger.i("TEST", "current public status->"+nStatus);
			if(NetworkService.networkBool){
				finish();
				httpRequestOpen = new NeighborsHttpRequest(PersonalInfoOccupActivity.this);
				RequestParams params = new RequestParams();
				params.put("user_id", App.sUserLoginId);
				params.put("user_phone_number", App.sUserPhone);
				params.put("user_vocation", changeOccupStr);
				params.put("tag", "upload");
				params.put("apitype", IHttpRequestUtils.APITYPE[0]);
				//params.put("user_public_status", nStatus);
				httpRequestOpen.updateUserInfo(params,handler);
			}else{
				Toast.makeText(PersonalInfoOccupActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
			}
	}
	
	private int getPublicStatus(){
		SharedPreferences sharedata = getSharedPreferences(App.ADDRESSOCCUP, Context.MODE_PRIVATE);
		boolean occupStatus = sharedata.getBoolean("occup_status", true);
		boolean addressStatus = sharedata.getBoolean("address_status", true);
		/*
		 * 1.地址不公开、职业不公开
		 * 2.地址公开、职业不公开
		 * 3.地址不公开、职业公开
		 * 4.地址公开、职业公开
		 * */
		if(true == occupStatus && true == addressStatus){
			return 4;
		}else if(true == occupStatus && false == addressStatus){
			return 3;
		}else if(false == occupStatus && true == addressStatus){
			return 2;
		}else if(false == occupStatus && false == addressStatus){
			return 1;
		}
		return 4;
	}
	
	public void getOpenStatus(final String kaiguan){
		RequestParams params = new RequestParams();
		params.put("user_id", App.sUserLoginId);
		params.put("user_phone", App.sUserPhone);
		params.put("tag", "getstatus");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient httpRequest = new AsyncHttpClient();
		httpRequest.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				try {
					int status= Integer.parseInt(response.getString("status"));
					String flag = response.getString("flag");
					Loger.i("TEST", "------------------------->"+status+" "+flag+" "+App.sUserPhone+" "+App.sUserLoginId);
					if(kaiguan.equals("null")){
						if(status==3||status==4){
							switchView.setState(true);
						}else{
							switchView.setState(false);
						}
					}
					if(kaiguan.equals("on")){
						if(status==1){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInfoOccupActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 3);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,occupHandler);
						}
						if(status==2){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInfoOccupActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 4);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,occupHandler);
						}
						if(status==3){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInfoOccupActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 3);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,occupHandler);
						}
						if(status==4){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInfoOccupActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 4);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,occupHandler);
						}
					}
					if(kaiguan.equals("off")){
						if(status==1){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInfoOccupActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 1);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,occupHandler);
						}
						if(status==2){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInfoOccupActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 2);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,occupHandler);
						}
						if(status==3){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInfoOccupActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 1);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,occupHandler);
						}
						if(status==4){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInfoOccupActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 2);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,occupHandler);
						}
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				new ErrorServer(PersonalInfoOccupActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
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
