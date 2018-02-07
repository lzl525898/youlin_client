package com.nfs.youlin.activity.thirdlogin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chatuidemo.DemoApplication;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.LoginActivity;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.RegisterActivity;
import com.nfs.youlin.activity.VerificationActivity;
import com.nfs.youlin.activity.personal.SystemSetActivity;
import com.nfs.youlin.controler.SMSReceiver;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.HttpClientHelper;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.push.YLPushInitialization;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.service.NetworkMonitorService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.MD5Util;
import com.nfs.youlin.utils.SMSVCode;
import com.nfs.youlin.utils.YLStringVerification;
import com.nfs.youlin.view.VerificationTimeButtonBind;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ThirdLoginBindActivity extends Activity{
	private Intent networkMonitorIntent;
	private YLStringVerification stringVerification;
	private VerificationTimeButtonBind getVCodeTextView; 
	private final int TIME = 500;
	private EditText phoneEditText;
	private EditText vCodeEditText;
	public static String sCode;
	public  static boolean bEditPhoneStatus = false;
	@SuppressWarnings("unused")
	private SMSVCode smsvCode = null;
	private EventHandler eventHandler;
	private BroadcastReceiver smsReceiver = null;
	public  static String sPhoneString;
	public  static boolean bVCodeStatus = false;
	private boolean registerBool = true;
	public static boolean sClearStatus;
	private String userId;
	private String name;
	private String userIcon;
	private String gender;
	private String thirdType;
	private YLProgressDialog ylProgressDialog;
	private Thread runQueryThread;
	private final int REG_SUCCESS      = 100;
	private final int REG_FAILED       = 101;
	private final int REG_NOTHING      = 102;
	private final int FAILED_GET_INFO  = 105;
	private final int REFUSE_GET_INFO  = 106;
	private final int UPLOAD_INFO      = 107;
	private final int SUCCESS_LOGIN    = 111;
	private Thread loginInsertDataThread;
	private Bundle updateShareXmlBundle;
	private static int sInsertDataForLogin = 0;
	@SuppressLint("HandlerLeak")
	private Handler thirdHandler = new Handler(){
		public void handleMessage(Message msg) {
			final String userJson = (String) msg.obj;
			switch(msg.what){
			case REG_SUCCESS:
				if(msg.what == REG_SUCCESS){
					Bundle msgBundle = msg.getData();
					final String msgUserJson = msgBundle.getString("user_json");
					setPushTag(msgUserJson);			
					if(runQueryThread !=null){
						runQueryThread.interrupt();
						runQueryThread = null;
					}
					Message msg0 = new Message();
					msg0.what = SUCCESS_LOGIN;
					thirdHandler.sendMessage(msg0);
					return;
				}
				break;
			case SUCCESS_LOGIN:
				Loger.i("NEW", "SUCCESS_LOGIN");
				Intent logintent = new Intent(ThirdLoginBindActivity.this,MainActivity.class);
				//logintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				logintent.putExtra("startmethod", "login");
				startActivity(logintent);
				finish();
				break;
			case REG_FAILED:
				if(runQueryThread !=null){
					runQueryThread.interrupt();
					runQueryThread = null;
				}
				ylProgressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "用户不存在!", 0).show();
				break;
			case REG_NOTHING:
				if(runQueryThread !=null){
					runQueryThread.interrupt();
					runQueryThread = null;
				}
				ylProgressDialog.dismiss();
				Toast.makeText(ThirdLoginBindActivity.this, "帐户名或密码错误!", 0).show();
				break;
			case FAILED_GET_INFO:
				Toast.makeText(ThirdLoginBindActivity.this, "网络有问题", 0).show();
				ylProgressDialog.dismiss();
				break;
			case REFUSE_GET_INFO:
				break;
			case UPLOAD_INFO:
				String imeiString = YLPushUtils.getImei(getApplicationContext(), null);
				RequestParams imeiParams = new RequestParams();
				imeiParams.put("user_id", App.sUserLoginId);
				imeiParams.put("user_phone_number", App.sUserPhone);
				imeiParams.put("imei", imeiString);
				imeiParams.put("tag", "upload");
				imeiParams.put("apitype", IHttpRequestUtils.APITYPE[0]);
				AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
				asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
						imeiParams, new JsonHttpResponseHandler(){
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							Loger.i("TEST","UPLOAD_INFO flag->"+response);
							String flag = response.getString("flag");
							if("ok".equals(flag)){
								Loger.i("TEST","success->"+flag);
								Loger.i("TEST", "开始设置PushTag");
								JSONArray jsonArray = null;
								Set<String> tagList = null;
								String adminTag = null;
								String newsTag = App.PUSH_NEWS_NOTICE + String.valueOf(App.sFamilyCommunityId);
								if("null".equals(userJson) && App.sFamilyCommunityId>0){
									tagList = new HashSet<String>();
									adminTag = App.PUSH_TAG_COMMUNITY_TOPIC + String.valueOf(App.sFamilyCommunityId);
									tagList.add(adminTag);
									tagList.add(newsTag);
									YLPushInitialization ylPushInitialization = new YLPushInitialization(ThirdLoginBindActivity.this);
									ylPushInitialization.setTags(ThirdLoginBindActivity.this, tagList);
									tagList.clear();
									jsonArray = null;
									tagList = null;
									return;
								}
								try {
									try {
										jsonArray = new JSONArray(userJson);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										Loger.i("TEST", "setPushTag=>ERROR=>"+e.getMessage());
										e.printStackTrace();
										return;
									}
									String nUserCommunityId = jsonArray.getJSONObject(0).getString("communityId");
									if(String.valueOf(App.sFamilyCommunityId).equals(nUserCommunityId)){//证明是当前小区的管理者
										tagList = new HashSet<String>();
										String nUserType = jsonArray.getJSONObject(0).getString("userType");
										if(nUserType==null){
											adminTag = App.PUSH_TAG_COMMUNITY_TOPIC + nUserCommunityId;
											tagList.add(adminTag);
											tagList.add(newsTag);
											Loger.i("TEST","2当前所在组==>"+adminTag);
											YLPushInitialization ylPushInitialization = new YLPushInitialization(getApplicationContext());
											ylPushInitialization.setTags(getApplicationContext(), tagList);
											tagList.clear();
											jsonArray = null;
											tagList = null;
											return;
										}
										YLPushInitialization ylPushInitialization = new YLPushInitialization(getApplicationContext());
										switch(Integer.parseInt(nUserType)){
										case 0:
										case 1://申请管理员状态
											adminTag = App.PUSH_TAG_COMMUNITY_TOPIC + nUserCommunityId;
											tagList.add(newsTag);
											tagList.add(adminTag);
											Loger.i("TEST","3当前所在组==>"+adminTag);
											ylPushInitialization.setTags(getApplicationContext(), tagList);
											tagList.clear();
											break;
										case 2://管理员状态
										case 3://管理员状态
											adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + nUserCommunityId;
											tagList.add(adminTag);
											tagList.add(newsTag);
											Loger.i("TEST","4当前所在组==>"+adminTag);
											ylPushInitialization.setTags(getApplicationContext(), tagList);
											tagList.clear();
											break;
										case 4://物业状态
										case 5://物业状态
											Loger.i("TEST","5当前所在组==>"+adminTag);
											adminTag = App.PUSH_TAG_PROPERTY_ADMIN + nUserCommunityId;
											tagList.add(adminTag);
											tagList.add(newsTag);
											Loger.i("TEST","5当前所在组==>"+adminTag);
											ylPushInitialization.setTags(getApplicationContext(), tagList);
											tagList.clear();
											break;
										case 6://物业+管理员
											adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + nUserCommunityId;
											tagList.add(adminTag);
											tagList.add(newsTag);
											Loger.i("TEST","6当前所在组==>"+adminTag);
											adminTag = App.PUSH_TAG_PROPERTY_ADMIN + nUserCommunityId;
											tagList.add(adminTag);
											Loger.i("TEST","7当前所在组==>"+adminTag);
											ylPushInitialization.setTags(getApplicationContext(), tagList);
											tagList.clear();
											break;
										default:
											break;
										}
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally{
									jsonArray = null;
									tagList = null;
								}
								
								
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Loger.i("TEST","UPLOAD_INFO->"+e.getMessage());
							e.printStackTrace();
						}
						super.onSuccess(statusCode, headers, response);
					};
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						new ErrorServer(ThirdLoginBindActivity.this, responseString);						
						super.onFailure(statusCode, headers, responseString, throwable);
					};
				});
				break;
			default:
				break;
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thrid_login);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("绑定");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		Intent intent=getIntent();
		userId=intent.getStringExtra("userId");
		name=intent.getStringExtra("name");
		userIcon=intent.getStringExtra("userIcon");
		gender=intent.getStringExtra("gender");
		thirdType=intent.getStringExtra("third_type");
		ylProgressDialog = YLProgressDialog.createDialogwithcircle(this,"加载中...",0);
		ylProgressDialog.setCanceledOnTouchOutside(false);
		networkMonitorIntent = new Intent();
		networkMonitorIntent.setAction("youlin.network.monitor");
		networkMonitorIntent.setPackage(getPackageName());
		startService(networkMonitorIntent);
		updateShareXmlBundle = new Bundle();
		handler.postDelayed(runnable, TIME);
		stringVerification  = new YLStringVerification(this);
		getVCodeTextView    = (VerificationTimeButtonBind) (findViewById(R.id.tv_verificat_getvcode));
		getVCodeTextView.onCreate(savedInstanceState);

		phoneEditText       = (EditText) findViewById(R.id.et_verificat_phone);
		vCodeEditText       = (EditText) findViewById(R.id.et_verificat_vcode);
		vCodeEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String strCode = s.toString();
				sCode = vCodeEditText.getText().toString();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		phoneEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String tagString = s.toString();
				if(tagString.length()==11){
					if(stringVerification.checkPhoneNumFormat(tagString)){
						bEditPhoneStatus = true;
					}else{
						bEditPhoneStatus = false;
					}
				}else{
					bEditPhoneStatus = false;
					return;
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		
		getVCodeTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone=phoneEditText.getText().toString();
				if(phone.isEmpty()||phone.equals("null")||phone==null||phone.length()<=0){
					Toast.makeText(ThirdLoginBindActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
					return;
				}		
				v.setBackgroundResource(R.drawable.bg_account_press);
				//sProgressDialog = new ProgressDialog(VerificationActivity.this);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//sProgressDialog.setMessage("正在请求验证码");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								phoneEditText.setEnabled(false);
								smsvCode = new SMSVCode( ThirdLoginBindActivity.this,handler,eventHandler);
								
								Loger.i("TEST", "verifyCode==111111111>");
								smsReceiver = new SMSReceiver(
										new SMSSDK.VerifyCodeReadListener() {
											@Override
											public void onReadVerifyCode(
													final String verifyCode) {
												Message msg = new Message();
												msg.what = 2;
												msg.obj = verifyCode;
												Loger.i("TEST", "verifyCode==>"+verifyCode);
												handler.sendMessage(msg);
											}
										});
								// 注册短信通知广播,获取验证码
								registerReceiver(smsReceiver,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
								SMSSDK.getVerificationCode("86",phoneEditText.getText().toString().trim());
								sPhoneString = phoneEditText.getText().toString().trim();
							}
						});
					}
				}).start();
			}
		});
		findViewById(R.id.bind_bt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isNext();
			}
		});
	}

	Runnable runnable = new Runnable() {  
        @Override  
        public void run() { 
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(getVCodeTextView.getClearInfoStatus()&&(true==ThirdLoginBindActivity.sClearStatus)){//为true表示已经读完60s
						ThirdLoginBindActivity.sPhoneString = "";
						ThirdLoginBindActivity.sCode = "";
						phoneEditText.setText(ThirdLoginBindActivity.sPhoneString);
						vCodeEditText.setText(ThirdLoginBindActivity.sCode);
						vCodeEditText.setEnabled(true);
						phoneEditText.setEnabled(true);
						bVCodeStatus = false;
						bEditPhoneStatus = false;
						ThirdLoginBindActivity.sClearStatus = false;
		        	}
				}
			});
        	handler.postDelayed(this, TIME);  
        }  
    };
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {		
		if(!getVCodeTextView.isEnabled()){
			phoneEditText.setText(ThirdLoginBindActivity.sPhoneString);
			vCodeEditText.setText(ThirdLoginBindActivity.sCode);
			phoneEditText.setEnabled(false);
			bEditPhoneStatus = true;
			ThirdLoginBindActivity.sClearStatus = false;
		}else{
			ThirdLoginBindActivity.sPhoneString = "";
			ThirdLoginBindActivity.sCode = "";
			phoneEditText.setText(ThirdLoginBindActivity.sPhoneString);
			vCodeEditText.setText(ThirdLoginBindActivity.sCode);
			phoneEditText.setEnabled(true);
			bEditPhoneStatus = false;
			bVCodeStatus = false;
			ThirdLoginBindActivity.sClearStatus = true;
		}
		if(bVCodeStatus){//表明验证码正确
			vCodeEditText.setEnabled(false);
		}else{
			vCodeEditText.setEnabled(true);
		}
		super.onWindowFocusChanged(hasFocus);
	}
	
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			int index = msg.what;
			Loger.i("NEW", "result->"+result);
			if (index == 1) {
				
				return;
			}
			if(index==2){
				ThirdLoginBindActivity.sCode = data.toString();
				vCodeEditText.setText(ThirdLoginBindActivity.sCode);
				return;
			}
			if(index==3){//证明可以点击下一步
				bindPhone();
				return;
			}
			if (result == SMSSDK.RESULT_COMPLETE) {
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					phoneEditText.setEnabled(false);
					Loger.i("TEST", "提交验证码成功");
				}else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					
				}
				return;
			} else {
				//验证码错误
				if(bVCodeStatus){
					Toast.makeText(ThirdLoginBindActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(ThirdLoginBindActivity.this, "服务器维护中...", Toast.LENGTH_SHORT).show();
				}
				bVCodeStatus = false;
				return;
			}
		}
	};
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(ThirdLoginBindActivity.this);
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		if(smsReceiver != null){
			SMSSDK.unregisterEventHandler(eventHandler);
			this.unregisterReceiver(smsReceiver);
			smsReceiver = null;
		}
		super.onPause();
		JPushInterface.onPause(ThirdLoginBindActivity.this);
		MobclickAgent.onPause(this);
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(registerBool){
			getVCodeTextView.onDestroy();
		}
		super.onDestroy();
		stopService(networkMonitorIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			   finish();
			   Platform qq=ShareSDK.getPlatform(ThirdLoginBindActivity.this, QQ.NAME);
			   if(qq.isValid()){
				   qq.removeAccount();
			   }
			   bEditPhoneStatus = false;
			   bVCodeStatus = false; 
			break;		
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void isNext(){
		String phone=phoneEditText.getText().toString();
		if(!NetworkMonitorService.bNetworkMonitor){
			Toast.makeText(ThirdLoginBindActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
			return;
		}else if(phone.isEmpty()||phone.equals("null")||phone==null||phone.length()<=0){
			Toast.makeText(ThirdLoginBindActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}else if(!stringVerification.checkPhoneNumFormat(phone) || phone.length()!=11){
			Toast.makeText(ThirdLoginBindActivity.this,"请填写正确的手机号", Toast.LENGTH_SHORT).show();
			return;
		}else{
			if(bVCodeStatus){
				Message message = new Message();
				message.what = 3;
				handler.sendMessage(message);
			}else{
				try {
					ylProgressDialog.show();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
//					SMSSDK.submitVerificationCode("86", sPhoneString, sCode);
					RequestParams params = new RequestParams();
					params.put("phone", ThirdLoginBindActivity.sPhoneString);
					params.put("code", ThirdLoginBindActivity.sCode);
					params.put("tag", "mobverify");
					params.put("apitype", IHttpRequestUtils.APITYPE[0]);
					AsyncHttpClient httpClient = new AsyncHttpClient();
					Loger.i("TEST", "向服务器进行比对");
					httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler(){
						public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
							int flag = 0;
							try {
								Loger.i("TEST", "flag   response==>"+response.toString());
								flag = response.getInt("flag");
								if(200==flag){//成功
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											bVCodeStatus = true;
											Message message = new Message();
											message.what = 3;
											handler.sendMessage(message);
										}
									});
									return;
								}else if(500==flag){//失败
									bVCodeStatus = false;
									Toast.makeText(ThirdLoginBindActivity.this, "验证码错误，请正确填写", Toast.LENGTH_SHORT).show();
									Loger.i("TEST", "失败");
									try {
										ylProgressDialog.dismiss();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return;
								}else{//异常 
									bVCodeStatus = false;
									Toast.makeText(ThirdLoginBindActivity.this, "验证码错误，请正确填写", Toast.LENGTH_SHORT).show();
									Loger.i("TEST", "异常");
									try {
										ylProgressDialog.dismiss();
									} catch (Exception e) {
										e.printStackTrace();
									}
									return;
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						};
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void bindPhone(){
		RequestParams params = new RequestParams();
		final String phone = phoneEditText.getText().toString();
		params.put("phonenum", phone);
		params.put("tag", "check");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+ IHttpRequestUtils.YOULIN,
				params, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode,Header[] headers,JSONObject response) {
						JSONObject jsonContext = response;
						try {
							Loger.i("NEW", "bindPhone->"+response);
							final String flag = jsonContext.getString("flag");
							if ("ok".equals(flag)) {
								saveSharePrefrence();
								bVCodeStatus = false;
								ThirdLoginBindActivity.sPhoneString = "";
								ThirdLoginBindActivity.sCode = "";
								ThirdLoginBindActivity.sClearStatus = false;
								Intent intent=new Intent(ThirdLoginBindActivity.this, ThirdLoginRegisterActivity.class);
								intent.putExtra("inviteType", "0");
								intent.putExtra("loginType", 3);
								intent.putExtra("userId", userId);
								intent.putExtra("name", name);
								intent.putExtra("userIcon", userIcon);
								intent.putExtra("gender", gender);
								intent.putExtra("third_type", thirdType);
								VerificationActivity.sVerificationStatus = true;
								startActivity(intent);
								finish();
								LoginActivity.sLoginActivity.finish();
							} else if ("exist".equals(flag)) {
								bindAccount(userId,phone,name,userIcon,gender,thirdType);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
							Loger.i("TEST",e.getMessage());
						}
					}

					@Override
					public void onFailure(int statusCode,Header[] headers,String responseString,Throwable throwable) {
						super.onFailure(statusCode,headers,responseString,throwable);
						
					}
				});
	}
	private void saveSharePrefrence(){
		Editor sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("phone", sPhoneString);
		sPhoneString = null;
		sharedata.commit();
	}
	public void bindAccount(String userId,String phone,String name,String userIcon,String gender,String thirdType){
		Loger.i("NEW", "bindAccount-Original-->"+thirdType);
		RequestParams params=new RequestParams();
		params.put("third_account", userId);
		params.put("phone_number", phone);
		params.put("third_nick", name);
		params.put("third_icon", userIcon);
		params.put("third_gender", gender);
		params.put("third_note", "111");
		params.put("third_type", thirdType);
		params.put("tag", "bindthird");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		 if(App.sPhoneIMEI==null){
	    		try {
					App.sPhoneIMEI = YLPushUtils.getImei(MainActivity.sMainActivity, App.sPhoneIMEI);
					params.put("phone_imei", App.sPhoneIMEI);
				} catch (Exception e) {
					e.printStackTrace();
				}
	    	}else{
	    		params.put("phone_imei", App.sPhoneIMEI);
	    	}
		AsyncHttpClient client=new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					Loger.i("NEW", "bindAccount--->"+response.toString());
					String flag = response.getString("flag");
					String retPhone = response.getString("phone");
					String passwd = response.getString("passwd");
					if("ok".equals(flag)){
						login(retPhone,passwd);
						return;
					}else{
						Loger.i("NEW", "bindAccount--->error");
						Toast.makeText(ThirdLoginBindActivity.this, "绑定失败", 0).show();
						return;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("NEW","JSONException:" + e.getMessage());
					e.printStackTrace();
					return;
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, responseString, throwable);
				new ErrorServer(ThirdLoginBindActivity.this, responseString);
			}
		});
	}
	public void login(final String phone,final String passwd) {
		if(!NetworkMonitorService.bNetworkMonitor){
			return;
		}
		if (phone == null || phone.isEmpty()) {
			Toast toast = Toast.makeText(getApplicationContext(), "账户不能为空",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		} else {
			runQueryThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					releaseData();
					final NeighborsHttpRequest httpRequest = new NeighborsHttpRequest(ThirdLoginBindActivity.this);
					httpRequest.setHttpUrl(IHttpRequestUtils.URL);
					try {
						YLStringVerification verification = new YLStringVerification(ThirdLoginBindActivity.this);
						if (!verification.checkPhoneNumFormat(phone)) {
							Toast.makeText(ThirdLoginBindActivity.this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
							return;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					httpRequest.getAccountInfo(phone, passwd.trim());
					Long currenttime = System.currentTimeMillis();
					while (!httpRequest.cRequestSet) {
						if(httpRequest.flag != null){
							if (httpRequest.flag.equals("no1")) {
								Loger.d("TEST", "httpRequest.flag.equals(no1)");
								httpRequest.flag = "none";
								Message msg = new Message();
								msg.what = REG_NOTHING;
								thirdHandler.sendMessage(msg);
								return;
							} else if (httpRequest.flag.equals("no")) {
								Loger.d("TEST", "httpRequest.flag.equals(no)");
								httpRequest.flag = "none";
								Message msg = new Message();
								msg.what = REG_FAILED;
								thirdHandler.sendMessage(msg);
								return;
							}
						}
						if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
							httpRequest.cRequestSet = true;
						}
					}
					Loger.i("TEST","1111111111111222222222222--->"+httpRequest.cRequestSet+" "+httpRequest.flag);
					if (httpRequest.cRequestSet && httpRequest.flag != null) {// 正确获取数据
						Loger.i("TEST", "正确获取数据");
						if (httpRequest.flag.equals("no1")) {
							Loger.d("TEST", "httpRequest.flag.equals(no111)");
							httpRequest.flag = "none";
							Message msg = new Message();
							msg.what = REG_NOTHING;
							thirdHandler.sendMessage(msg);
						} else if (httpRequest.flag.equals("no")) {
							Loger.d("TEST", "httpRequest.flag.equals(no11111)");
							httpRequest.flag = "none";
							Message msg = new Message();
							msg.what = REG_FAILED;
							thirdHandler.sendMessage(msg);
							return;
						} else if (httpRequest.flag.equals("ok")) {
							loginInsertDataThread = new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(ThirdLoginBindActivity.this);
									Bundle bundle = null;
									boolean bRet = false;
									int accountLen = httpRequest.acountBundleList.size();
									if (accountLen == 1) {
										bundle = httpRequest.acountBundleList.get(0);

									} else {
										AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(ThirdLoginBindActivity.this);
										AllFamily allFamily = null;
										Bundle familyBundle = null;
										bundle = httpRequest.acountBundleList.get(0);

										for (int i = 1; i < accountLen; i++) {
											allFamily = new AllFamily(ThirdLoginBindActivity.this);
											familyBundle = httpRequest.acountBundleList.get(i);
											String familyId = familyBundle.getString("family_id");
											if (familyId == null || familyId.isEmpty() || familyId.length() <= 0
													|| familyId.equals("null")) {
												familyId = "0";
											}
											allFamily.setFamily_id(Long.parseLong(familyId));
											String familyName = familyBundle.getString("family_name");
											allFamily.setFamily_name(familyName);
											allFamily.setFamily_address(familyBundle.getString("family_address"));
											String cityName = familyBundle.getString("city_name");
											allFamily.setFamily_city(cityName);
											long city_id;
											try {
												city_id = Long.parseLong(familyBundle.getString("city_id"));
											} catch (NumberFormatException e) {
												// TODO Auto-generated catch block
												city_id = 0;
												e.printStackTrace();
											}
											allFamily.setFamily_city_id(city_id);
											allFamily.setFamily_block(familyBundle.getString("block_name"));
											String blockId = familyBundle.getString("block_id");
											if (blockId == null || blockId.isEmpty() || blockId.length() <= 0
													|| blockId.equals("null")) {
												blockId = "0";
											}
											allFamily.setFamily_block_id(Long.parseLong(blockId));
											String communityName = familyBundle.getString("community_name");
											allFamily.setFamily_community(communityName);
											long community_id = 0;
											try {
												community_id = Long.parseLong(familyBundle.getString("community_id"));
											} catch (NumberFormatException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											allFamily.setFamily_community_id(community_id);
											allFamily.setFamily_building_num(familyBundle.getString("family_building_num"));
											allFamily.setFamily_apt_num(familyBundle.getString("family_apt_num"));
											String isfamilyMember = familyBundle.getString("is_family_member");
											if (isfamilyMember == null || isfamilyMember.isEmpty()
													|| isfamilyMember.length() <= 0 || isfamilyMember.equals("null")) {
												isfamilyMember = "0";
											}
											allFamily.setIs_family_member(Integer.parseInt(isfamilyMember));
											String familyMemberCount = familyBundle.getString("family_member_count");
											if (familyMemberCount == null || familyMemberCount.isEmpty()
													|| familyMemberCount.length() <= 0 || familyMemberCount.equals("null")) {
												familyMemberCount = "0";
											}
											allFamily.setFamily_member_count(Integer.parseInt(familyMemberCount));
											String primaryFlag = familyBundle.getString("primary_flag");
											if (primaryFlag == null || primaryFlag.isEmpty() || primaryFlag.length() <= 0
													|| primaryFlag.equals("null")) {
												primaryFlag = "0";
											}
											allFamily.setPrimary_flag(Integer.parseInt(primaryFlag));
											allFamily.setUser_alias(bundle.getString("user_nick"));
											allFamily.setUser_avatar(bundle.getString("user_portrait"));
											allFamily.setLogin_account(Long.parseLong(bundle.getString("user_id")));
											String entityType = familyBundle.getString("entity_type");
											if (entityType == null || entityType.isEmpty() || entityType.length() <= 0
													|| entityType.equals("null")) {
												entityType = "0";
											}
											allFamily.setEntity_type(Integer.parseInt(entityType));
											String neStatus = familyBundle.getString("ne_status");
											if (neStatus == null || neStatus.isEmpty() || neStatus.length() <= 0
													|| neStatus.equals("null")) {
												neStatus = "0";
											}
											allFamily.setNe_status(Integer.parseInt(neStatus));
											String city_code = familyBundle.getString("city_code");
											Loger.d("test4", "login citycode="+city_code);
											if (city_code == null || city_code.isEmpty() || city_code.length() <= 0
													|| city_code.equals("null")) {
												city_code = "0";
											}
											allFamily.setfamily_city_code(city_code);
											Loger.d("test4", "login citycode="+allFamily.getfamily_city_code());
											String num_id = familyBundle.getString("apt_num_id");
											Loger.d("test4", "login num_id="+num_id);
											if (num_id == null || num_id.isEmpty() || num_id.length() <= 0
													|| num_id.equals("null")) {
												num_id = "0";
											}
											allFamily.setapt_num_id(Long.parseLong(num_id));
											Loger.d("test4", "login num_id="+allFamily.getapt_num_id());
											String building_id = familyBundle.getString("building_num_id");
											Loger.d("test4", "login building_id="+building_id);
											if (building_id == null || building_id.isEmpty() || building_id.length() <= 0
													|| building_id.equals("null")) {
												building_id = "0";
											}
											allFamily.setbuilding_num_id(Long.parseLong(building_id));
											Loger.d("test4", "login building_id="+allFamily.getbuilding_num_id());
											if ("1".equals(primaryFlag)) {
												updateShareXmlBundle.putString("blockid", blockId);
												updateShareXmlBundle.putString("detail", familyName);
												updateShareXmlBundle.putString("village", communityName);
												updateShareXmlBundle.putString("familycommunityid",
														String.valueOf(community_id));
												updateShareXmlBundle.putString("city", cityName);
											}
											allFamily.setFamily_address_id(Long.parseLong(familyBundle.getString("fr_id")));
											allFamilyDaoDBImpl.saveObject(allFamily);
										}
										allFamilyDaoDBImpl.releaseDatabaseRes();
										uploadAllfamilySharePrefrence(updateShareXmlBundle);
									}
									String tmpPhone = accountDaoDBImpl.findAccountByLoginID(bundle.getString("user_id"))
											.getUser_phone_number();
									Account account = null;
									if (tmpPhone == null || tmpPhone.isEmpty() || tmpPhone.length() <= 0) {
										account = new Account(ThirdLoginBindActivity.this);
										account.setUser_id(Long.parseLong(bundle.getString("user_id")));
										account.setLogin_account(Long.parseLong(bundle.getString("user_id")));
										account.setUser_portrait(bundle.getString("user_portrait"));
										account.setUser_phone_number(bundle.getString("user_phone_number"));
										String user_family_id = bundle.getString("user_family_id");
										if (user_family_id == null || user_family_id.isEmpty() || user_family_id.length() <= 0
												|| user_family_id.equals("null")) {
											user_family_id = "0";
										}
										account.setUser_family_id(Long.parseLong(user_family_id));
										account.setUser_family_address(bundle.getString("user_family_address"));
										account.setUser_name(bundle.getString("user_nick"));
										String user_gender = bundle.getString("user_gender");
										if (user_gender == null || user_gender.isEmpty() || user_gender.length() <= 0
												|| user_gender.equals("null")) {
											user_gender = "0";
										}
										account.setUser_gender(Integer.parseInt(user_gender));
										account.setUser_email(bundle.getString("user_email"));
										String user_birthday = bundle.getString("user_birthday");
										if (user_birthday == null || user_birthday.isEmpty() || user_birthday.length() <= 0
												|| user_birthday.equals("null")) {
											user_birthday = "0";
										}
										account.setUser_birthday(Long.parseLong(user_birthday));
										String user_public_status = bundle.getString("user_public_status");
										if (user_public_status == null || user_public_status.isEmpty()
												|| user_public_status.length() <= 0 || user_public_status.equals("null")) {
											user_public_status = "0";
										}
										account.setUser_public_status(Integer.parseInt(user_public_status));
										String userType = bundle.getString("user_type");
										if (userType == null || userType.isEmpty() || userType.length() <= 0
												|| userType == "null") {
											userType = "0";
										}
										account.setUser_type(Integer.parseInt(userType));
										App.sUserType = Integer.parseInt(userType);
										account.setUser_json(bundle.getString("user_json"));
										account.setUser_vocation(bundle.getString("user_profession"));
										account.setUser_time(Long.parseLong(bundle.getString("user_time")));
										String newsReceiveStatus = bundle.getString("user_news_receive");
										if(newsReceiveStatus == null || newsReceiveStatus.isEmpty() || newsReceiveStatus.length() <= 0
												|| newsReceiveStatus == "null"){
											newsReceiveStatus = "0";
										}
										if("2".equals(newsReceiveStatus)){
											App.setNewsRecviceStatus(ThirdLoginBindActivity.this, false);
										}else{
											App.setNewsRecviceStatus(ThirdLoginBindActivity.this, true);
										}
									}
									if (account != null) {
										Loger.i("TEST", "向数据库中插入新数据");
										saveSignature(bundle.getString("user_signature"));
										saveSharePrefrence(bundle.getString("user_phone_number"));
										accountDaoDBImpl.deleteAllObjects();
										accountDaoDBImpl.saveObject(account);
									}
									EasemobHandler easemobHandler = new EasemobHandler(ThirdLoginBindActivity.this);
									easemobHandler.userLogin(bundle.getString("user_id"));
									DemoApplication.getInstance().setUserName(bundle.getString("user_id"));
									uploadUserSharePrefrence(bundle);
									uploadSMSSharePrefrence(phone);
									getUserLoginID(phone);
									ThirdLoginBindActivity.sInsertDataForLogin = 1;
								}
							});
							loginInsertDataThread.start();
							/////////////////////////////////////////////////////////////////////////
							new AsyncTask<Void, Void, Void>(){
								@Override
								protected Void doInBackground(Void... params) {
									// TODO Auto-generated method stub
									while(true){
										if(ThirdLoginBindActivity.sInsertDataForLogin>0){
											ThirdLoginBindActivity.sInsertDataForLogin = 0;
											if(loginInsertDataThread!=null){
												loginInsertDataThread.interrupt();
												loginInsertDataThread = null;
											}
											break;
										}
									}
									return null;
								}
								protected void onPostExecute(Void result) {
									Message msg = new Message();
									msg.what = REG_SUCCESS;
									Bundle data = new Bundle();
									AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(getApplicationContext());
									Account account = accountDaoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
									data.putString("user_json", account.getUser_json());
									msg.setData(data);
									httpRequest.flag = "none";
									thirdHandler.sendMessage(msg);
								};
							}.execute();
						}
					} else {
						Message msg = new Message();
						msg.what = REG_NOTHING;
						Loger.d("TEST", "httpRequest.flag.equals(no1111111111)");
						thirdHandler.sendMessage(msg);
					}
				}
			});
			runQueryThread.start();
		}
	}
	private void releaseData(){
		MainActivity.currentcity = "未设置";
		MainActivity.currentvillage = "未设置";
		App.sUserType = -1;
		App.sUserLoginId = -1;
		App.sNewPushRecordCount = null;
		App.sUserPhone = null;
		App.imei = null;
		App.pushTags = null;
		App.sDeleteTagFinish = false;
		App.sDeviceLoginStatus = false;
		App.sFamilyId = 00000000000L;
		App.sFamilyCommunityId = 0000000000L;
		App.sFamilyBlockId = 0000000000L;
		App.sUserAppTime = 0000000000L;
		App.sPushUserID = null;
		App.sPushChannelID = null;
		App.sLoadNewTopicStatus = false;
		App.NORMAL_TYPE = 2;
		App.GONGGAO_TYPE = 3;
		App.BAOXIU_TYPE = 4;
		App.JIANYI_TYPE = 5;
		AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(getApplicationContext());
		allFamilyDaoDBImpl.deleteAllObject();
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(getApplicationContext());
		accountDaoDBImpl.deleteAllObjects();
//		PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(getApplicationContext());
//		daoDBImpl.deleteAllObjects();
//		String dataBasePath=getFilesDir().getParent()+File.separator+"databases";
//		File dataBaseFile=new File(dataBasePath);
//		DataCleanManager.deleteFilesByDirectory(dataBaseFile);
		initSharePrefrence(null, null, null, null, null, null, null);
		String filePath = "/data/data/" + getPackageName().toString()
				+ "/shared_prefs";
		String fileName1 = App.SMS_VERIFICATION_USER + ".xml";
		String fileName2 = App.VIBRATION + ".xml";
		String fileName3 = App.VOICE + ".xml";
		String fileName4 = App.ADDRESSOCCUP + ".xml";
		CommonTools commonTools = new CommonTools(getApplicationContext());
		commonTools.delTargetFile(filePath, fileName1);
		commonTools.delTargetFile(filePath, fileName2);
		commonTools.delTargetFile(filePath, fileName3);
		commonTools.delTargetFile(filePath, fileName4);
		MainActivity.sMainInitData = 0;
	}
	private void initSharePrefrence(String city, String village,String detail,String familyid,
			String fimalycommunityid,String blockid,String username){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("city", city);
		sharedata.putString("village", village);
		sharedata.putString("detail", detail);
		sharedata.putString("familyid", familyid);
		sharedata.putString("familycommunityid", fimalycommunityid);
		sharedata.putString("blockid", blockid);
		sharedata.putString("username", username);
		sharedata.putString("recordCount","0"); 
		sharedata.putString("atype","0");
		sharedata.putInt("aliasStatus",0);
		sharedata.putString("encryption","0");
		sharedata.putString("account","0");
		sharedata.putString("signature","null");
		sharedata.commit();
	}
	private void setPushTag(String userJson){
		Message msg = new Message();
		msg.what = UPLOAD_INFO;
		msg.obj = userJson;
		thirdHandler.sendMessage(msg);
	}
	private void uploadAllfamilySharePrefrence(Bundle bundle){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("blockid", bundle.getString("blockid"));
		sharedata.putString("detail", bundle.getString("detail"));
		sharedata.putString("village", bundle.getString("village"));
		sharedata.putString("familycommunityid", bundle.getString("familycommunityid"));
		try {
			App.sFamilyCommunityId = Long.parseLong(bundle.getString("familycommunityid"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			App.sFamilyCommunityId = 0L;
			e.printStackTrace();
		}
		try {
			App.sFamilyBlockId = Long.parseLong(bundle.getString("blockid"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			App.sFamilyBlockId = 0L;
			e.printStackTrace();
		}
		sharedata.putString("city", bundle.getString("city"));
		sharedata.commit();
	}
	private void saveSignature(String strInfo){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("signature", strInfo);
		sharedata.commit();
	}
	private void saveSharePrefrence(String phone){
		Editor sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("phone", phone);
		sharedata.commit();
	}
	private void uploadUserSharePrefrence(Bundle bundle){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("familyid", bundle.getString("user_family_id"));
		sharedata.putString("phone", bundle.getString("user_phone_number"));
		sharedata.putString("username", bundle.getString("user_nick"));
		sharedata.putString("encryption", bundle.getString("user_password"));
		sharedata.putString("account", bundle.getString("user_id"));
		String strType = bundle.getString("user_type");
		if(strType==null || strType.isEmpty() || strType.length()<=0 || strType=="null"){
			sharedata.putString("atype", "0");
		}else{
			sharedata.putString("atype", strType);
		}
		Loger.i("TEST", "ATYPE==>"+strType);
		sharedata.commit();
	}
	private void uploadSMSSharePrefrence(String phone){
		Editor sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("phone", phone);
		sharedata.commit();
	}
	private void getUserLoginID(String phone){
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(ThirdLoginBindActivity.this);
		Account account = accountDaoDBImpl.findAccountByPhone(phone);
		accountDaoDBImpl.releaseDatabaseRes();
		long userLoginId = account.getUser_id();
		Loger.i("TEST", "userid=====>"+userLoginId);
		if(userLoginId>0){
			Loger.i("TEST", "Login LoginId->"+ userLoginId);
			Loger.i("TEST", "Login phone->"+ account.getUser_phone_number());
			App.sUserLoginId = userLoginId;
			App.sUserPhone   = account.getUser_phone_number();
		}
	}
}
