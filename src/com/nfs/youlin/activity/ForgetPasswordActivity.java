package com.nfs.youlin.activity;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.R.color;
import com.nfs.youlin.activity.titlebar.barter.BarterDedailCommentActivity;
import com.nfs.youlin.controler.SMSReceiver;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.service.NetworkMonitorService;
import com.nfs.youlin.utils.AESandCBC;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.MD5Util;
import com.nfs.youlin.utils.SMSVCode;
import com.nfs.youlin.utils.YLStringVerification;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.VerificationTimeButton;
import com.nfs.youlin.view.VerificationTimeButtonForget;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

@SuppressLint("HandlerLeak")
public class ForgetPasswordActivity extends Activity //implements OnClickListener
{
	private VerificationTimeButtonForget getIdentifyCode;
	private EditText phone;
	private EditText identifyCode;
	private EditText firstPassword;
	private EditText secondPassword;
	public Context sVerificationContext;
	public static boolean bEditPhoneStatus = false;
	public boolean bAgreeStatus = true;
	public static boolean bVCodeStatus = false;
	public static boolean sClearStatus;
	private boolean registerBool = true;
	private static String sPhoneString;
//	private ImageView vCodeStautsImageView;
	private YLStringVerification stringVerification;
	private EventHandler eventHandler;
	private ProgressDialog pd;
	private static String vCode;
	private static String sFirstPwd;
	private static String sSecondPwd;
	private SMSVCode smsvCode = null;
	private BroadcastReceiver smsReceiver = null;
	private boolean sPasswordStatusBoolean = false;
	private boolean sConfirmStatusBoolean  = false;
	private final int RESET_SUCCESS = 100;
	private final int RESET_FAILED  = 101;
	private final int TIME = 500;
	
	private Thread regActionThread;
	private ProgressDialog pd_2;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_forget_password);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("忘记密码");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		sVerificationContext= this;
		stringVerification  = new YLStringVerification(this);
		handler.postDelayed(runnable, TIME);
//		VerificationTitleBar.getVerificatActivityTitleBar(this);
//		vCodeStautsImageView= (ImageView) findViewById(R.id.iv_reset_password_prove_status);
		getIdentifyCode =  (VerificationTimeButtonForget) findViewById(R.id.bt_get_identify_code);
		getIdentifyCode.onCreate(savedInstanceState);
		phone = (EditText) findViewById(R.id.et_forget_passward_phone);
		
		//获取验证码
		getIdentifyCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//输入正确的手机号后......
				RequestParams params = new RequestParams();
				String phoneStr = phone.getText().toString();
				params.put("phonenum", phoneStr);
				params.put("tag", "check");
				params.put("apitype", IHttpRequestUtils.APITYPE[0]);
				AsyncHttpClient client = new AsyncHttpClient();
				client.post(IHttpRequestUtils.URL+ IHttpRequestUtils.YOULIN,
						params, new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,Header[] headers,JSONObject response) {
								JSONObject jsonContext = response;
								try {
									final String flag = jsonContext.getString("flag");
									if ("exist".equals(flag) && bEditPhoneStatus == true){
										phone.setEnabled(false);
											pd = new ProgressDialog(ForgetPasswordActivity.this);
											new Thread(new Runnable() {
												@Override
												public void run() {
													// TODO Auto-generated method stub
													pd.setMessage("正在请求验证码");
													runOnUiThread(new Runnable() {
														@Override
														public void run() {
															pd.show();
															pd.setCancelable(false);
														}
													});
												}
											}).start();
											smsvCode = new SMSVCode(ForgetPasswordActivity.this, handler, eventHandler);
											smsReceiver = new SMSReceiver(new SMSSDK.VerifyCodeReadListener() {
												@Override
												public void onReadVerifyCode(final String verifyCode) {
													Message msg = new Message();
													msg.what = 2;
													msg.obj = verifyCode;
													handler.sendMessage(msg);
												}
											});
											// 注册短信通知广播,获取验证码
											ForgetPasswordActivity.this.registerReceiver(smsReceiver, new IntentFilter(
													"android.provider.Telephony.SMS_RECEIVED"));
											SMSSDK.getVerificationCode("86",phone.getText().toString().trim());
											sPhoneString = phone.getText().toString().trim();
										}else{
											getIdentifyCode.sendCloseMsg();
											registerBool=false;
											Toast toast = Toast.makeText(ForgetPasswordActivity.this,
												     "手机号不正确或用户不存在", Toast.LENGTH_SHORT);
										    toast.setGravity(Gravity.CENTER, 0, 0);
										    toast.show();
											return;
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
		});
	
		phone.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String tagString = s.toString();
				if(tagString.length() == 11){
					if(stringVerification.checkPhoneNumFormat(tagString)){
						Loger.i("TEST", "手机号格式正确");
						bEditPhoneStatus = true;
					}else{
						bEditPhoneStatus = false;
					}
				}else{
					bEditPhoneStatus = false;
					return;
				}
			}
			public void afterTextChanged(Editable s) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});
		identifyCode = (EditText) findViewById(R.id.et_identify_code);
		//验证验证码是否正确
		identifyCode.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				vCode = s.toString();
//				if(vCode.length() == 4){
//					try {
//						SMSSDK.submitVerificationCode("86", sPhoneString, identifyCode.getText().toString());
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
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
		firstPassword = (EditText) findViewById(R.id.et_reset_password);
		firstPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				sFirstPwd = s.toString().trim();
				if(stringVerification.checkPwdFormat(sFirstPwd)){
					sPasswordStatusBoolean = true;
				}else{
					sPasswordStatusBoolean = false;
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		secondPassword = (EditText) findViewById(R.id.et_reset_password_prove);
		secondPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				sSecondPwd = s.toString().trim();
				if(stringVerification.checkPwdFormat(sSecondPwd)){
					sConfirmStatusBoolean = true;
				}else{
					sConfirmStatusBoolean = false;
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
	}
	
	Runnable runnable = new Runnable() {  
        @Override  
        public void run() { 
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(getIdentifyCode.getClearInfoStatus()&&(true==ForgetPasswordActivity.sClearStatus)){//为true表示已经读完60s
						ForgetPasswordActivity.sFirstPwd = "";
						ForgetPasswordActivity.sSecondPwd = "";
						ForgetPasswordActivity.vCode = "";
						ForgetPasswordActivity.sPhoneString = "";
						phone.setText(ForgetPasswordActivity.sPhoneString);
						identifyCode.setText(ForgetPasswordActivity.vCode);
						firstPassword.setText(ForgetPasswordActivity.sFirstPwd);
						secondPassword.setText(ForgetPasswordActivity.sSecondPwd);
						phone.setEnabled(true);
						identifyCode.setEnabled(true);
						bVCodeStatus = false;
						bEditPhoneStatus = false;
						ForgetPasswordActivity.sClearStatus = false;
		        	}
				}
			});
        	handler.postDelayed(this, TIME);  
        }  
    };
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(!getIdentifyCode.isEnabled()){
			phone.setText(ForgetPasswordActivity.sPhoneString);
			phone.setEnabled(false);
			identifyCode.setText(ForgetPasswordActivity.vCode);
			firstPassword.setText(ForgetPasswordActivity.sFirstPwd);
			secondPassword.setText(ForgetPasswordActivity.sSecondPwd);
			bEditPhoneStatus = true;
			ForgetPasswordActivity.sClearStatus = false;
		}else{
			ForgetPasswordActivity.sFirstPwd = "";
			ForgetPasswordActivity.sSecondPwd = "";
			ForgetPasswordActivity.vCode = "";
			ForgetPasswordActivity.sPhoneString = "";
			phone.setText(ForgetPasswordActivity.sPhoneString);
			phone.setEnabled(true);
			identifyCode.setText(ForgetPasswordActivity.vCode);
			firstPassword.setText(ForgetPasswordActivity.sFirstPwd);
			secondPassword.setText(ForgetPasswordActivity.sSecondPwd);
			bEditPhoneStatus = false;
			ForgetPasswordActivity.sClearStatus = true;
		}
		if(bVCodeStatus){//表明验证码正确
			identifyCode.setEnabled(false);
//			vCodeStautsImageView.setVisibility(View.VISIBLE);
//			vCodeStautsImageView.setImageResource(R.drawable.vcode_tick);
		}else{
			identifyCode.setEnabled(true);
//			vCodeStautsImageView.setVisibility(View.GONE);
		}
		super.onWindowFocusChanged(hasFocus);
	}
	
	private void saveSharePrefrence(String pwd, String phone, String userId){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("encryption", pwd);
		sharedata.putString("phone", phone);
		sharedata.putString("account", userId);
		App.sUserLoginId = Long.parseLong(userId);
		App.sUserPhone = phone;
		sharedata.commit();
	}	
	
	private String[] getUserPhone() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String phone = sharedata.getString("phone", null);
		String userId = sharedata.getString("account", "0");
		String[] strArray = new String[2];
		strArray[0] = userId;
		strArray[1] = phone;
		return strArray;
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
			
			switch(msg.what){
			case RESET_SUCCESS:
				Loger.i("TEST", "设置成功 ");
				if(regActionThread !=null){
					regActionThread.interrupt();
					regActionThread = null;
				}
				sConfirmStatusBoolean  = false;
				sPasswordStatusBoolean = false;
				pd_2.cancel();
				bEditPhoneStatus = false;
				ForgetPasswordActivity.sFirstPwd = "";
				ForgetPasswordActivity.sSecondPwd = "";
				ForgetPasswordActivity.vCode = "";
				ForgetPasswordActivity.sPhoneString = "";
				phone.setText(ForgetPasswordActivity.sPhoneString);
				identifyCode.setText(ForgetPasswordActivity.vCode);
				firstPassword.setText(ForgetPasswordActivity.sFirstPwd);
				secondPassword.setText(ForgetPasswordActivity.sSecondPwd);
				phone.setEnabled(true);
				identifyCode.setEnabled(true);
				bVCodeStatus = false;
				bEditPhoneStatus = false;
				ForgetPasswordActivity.sClearStatus = false;
				Toast.makeText(ForgetPasswordActivity.this, "密码重置成功", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case RESET_FAILED:
				Loger.i("TEST", "设置失败");
				if(regActionThread !=null){
					regActionThread.interrupt();
					regActionThread = null;
				}
				firstPassword.setText("");
				secondPassword.setText("");
				pd_2.cancel();
				Toast.makeText(ForgetPasswordActivity.this, "设置失败!", Toast.LENGTH_LONG).show();
				break;
			}
			
			if(index==2){
				vCode = data.toString();
				identifyCode.setText(vCode);
				return;
			}
			if(index==3){//开始设置
				pd_2 = new ProgressDialog(ForgetPasswordActivity.this);
				regActionThread = new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						NeighborsHttpRequest neighborsHttpRequest = new NeighborsHttpRequest(
								ForgetPasswordActivity.this);
						neighborsHttpRequest.setHttpUrl(IHttpRequestUtils.URL);
						// pd_2.setMessage("设置中...");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								pd_2.show();
								pd_2.setCancelable(false);
							}
						});
						final Message msg = new Message();
						// final String[] userInfo = getUserPhone();
						final String pwd = firstPassword.getText().toString().trim();
						String encryptedPwd = null;
						// try {
						final String phoneNumber = phone.getText().toString().trim();
						// encryptedPwd = MD5Util.getEncryptedPwd(pwd);
						try {
							encryptedPwd = MD5Util.getEncryptedPwd(pwd, phoneNumber);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							encryptedPwd = "0";
							e.printStackTrace();
						}
						Loger.d("test4", "encryptedPwd=" + encryptedPwd + "pwd=" + pwd + "tel=" + phoneNumber);
						String imei = "0";
						imei = YLPushUtils.getImei(ForgetPasswordActivity.this, imei);
						RequestParams params = new RequestParams();
						params.put("phone", phoneNumber);
						params.put("password", encryptedPwd);
						params.put("imei", imei);
						params.put("tag", "updatepwd");
						params.put("apitype", IHttpRequestUtils.APITYPE[0]);
						SyncHttpClient asyncHttpClient = new SyncHttpClient();
						asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params,
								new JsonHttpResponseHandler() {
							public void onSuccess(int statusCode, org.apache.http.Header[] headers,
									org.json.JSONObject response) {
								try {
									String flag = response.getString("flag");
									Loger.i("TEST", "update flag->" + flag);
									if ("ok".equals(flag)) {
										Loger.i("TEST", "success->" + flag);
										String userId = response.getString("userId");
										String myPhone = response.getString("phone");
										saveSharePrefrence(pwd, myPhone, userId);
										msg.what = RESET_SUCCESS;
										handler.sendMessage(msg);
									} else if ("none".equals(flag)) {
										Loger.i("TEST", "else if->" + flag);
										msg.what = RESET_FAILED;
										handler.sendMessage(msg);
									} else {
										Loger.i("TEST", "else->" + flag);
										msg.what = RESET_FAILED;
										handler.sendMessage(msg);
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									Loger.i("TEST", "update->" + e.getMessage());
									msg.what = RESET_FAILED;
									handler.sendMessage(msg);
									e.printStackTrace();
								}
							};

							public void onFailure(int statusCode, org.apache.http.Header[] headers,
									String responseString, Throwable throwable) {
								new ErrorServer(ForgetPasswordActivity.this, responseString);
								msg.what = RESET_FAILED;
								handler.sendMessage(msg);
							};
						});
					}
				});
				regActionThread.start();
			}
			if (result == SMSSDK.RESULT_COMPLETE) {
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
//					vCodeStautsImageView.setImageResource(R.drawable.vcode_tick);
//					identifyCode.setEnabled(false);
					bVCodeStatus = true;
				}else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					pd.cancel();
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
				}
				return;
			} else {
				//验证码错误
				try {
					pd.cancel();
					bVCodeStatus = false;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bVCodeStatus = false;
					e.printStackTrace();
				}
				//vCodeStautsImageView.setImageResource(R.drawable.vcode_cross);
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(ForgetPasswordActivity.this);
		MobclickAgent.onResume(this);
	};

	@Override
	protected void onPause() {
		if(smsReceiver != null){
			SMSSDK.unregisterEventHandler(eventHandler);
			this.unregisterReceiver(smsReceiver);
			smsReceiver = null;
		}
		super.onPause();
		JPushInterface.onPause(ForgetPasswordActivity.this);
		MobclickAgent.onPause(this);
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(registerBool){
			getIdentifyCode.onDestroy();
		}
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.new_topic_change, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.finish:
			setOnClickFinish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void	setOnClickFinish(){
		String phoneStr=phone.getText().toString();
		if(!NetworkMonitorService.bNetworkMonitor){
			Toast.makeText(ForgetPasswordActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
			return;
		}else if(phoneStr.isEmpty()||phoneStr.equals("null")||phoneStr==null||phoneStr.length()<=0){
			Toast.makeText(ForgetPasswordActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
//		else if(!bVCodeStatus){
//			Toast.makeText(ForgetPasswordActivity.this, "验证码错误，请正确填写", Toast.LENGTH_SHORT).show();
//			return;
//		}
		else if(!sPasswordStatusBoolean){
			Toast.makeText(ForgetPasswordActivity.this, "输入密码格式不正确", Toast.LENGTH_SHORT).show();
			firstPassword.requestFocus();
			return;
		}else if(!sConfirmStatusBoolean){
			Toast.makeText(ForgetPasswordActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
			secondPassword.requestFocus();
			return;
		}else if(sPasswordStatusBoolean && sConfirmStatusBoolean && bEditPhoneStatus){
			String tmp = secondPassword.getText().toString();
			if(firstPassword.getText().toString().equals(tmp)){
				if(!bVCodeStatus){
					final YLProgressDialog dialog = YLProgressDialog.createDialogwithcircle(ForgetPasswordActivity.this,"",0);
					RequestParams params = new RequestParams();
					params.put("phone", phoneStr);
					params.put("code", vCode);
					params.put("tag", "mobverify");
					params.put("apitype", IHttpRequestUtils.APITYPE[0]);
					AsyncHttpClient httpClient = new AsyncHttpClient();
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
									Loger.i("TEST", "成功");
									try {
										dialog.dismiss();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return;
								}else if(500==flag){//失败
//									vCodeStautsImageView.setVisibility(View.GONE);
									bVCodeStatus = false;
									Toast.makeText(ForgetPasswordActivity.this, "验证码错误，请正确填写", Toast.LENGTH_SHORT).show();
									Loger.i("TEST", "失败");
									try {
										dialog.dismiss();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return;
								}else{//异常
//									vCodeStautsImageView.setVisibility(View.GONE);
									bVCodeStatus = false;
									Toast.makeText(ForgetPasswordActivity.this, "验证码错误，请正确填写", Toast.LENGTH_SHORT).show();
									Loger.i("TEST", "异常");
									try {
										dialog.dismiss();
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
				}else{
					Message message = new Message();
					message.what = 3;
					handler.sendMessage(message);
				}
			}else{
				Toast.makeText(ForgetPasswordActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	
}
