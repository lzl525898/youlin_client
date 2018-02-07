package com.nfs.youlin.activity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.RunnableFuture;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jpush.android.api.JPushInterface;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.personal.PrivatePolicyActivity;
import com.nfs.youlin.controler.SMSReceiver;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkMonitorService;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.NetworkJudge;
import com.nfs.youlin.utils.SMSVCode;
import com.nfs.youlin.utils.YLStringVerification;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.VerificationTimeButton;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class VerificationActivity extends Activity {
	public  static boolean bEditPhoneStatus = false;
	public  static boolean bAgreeStatus = false;
	public  static boolean bVCodeStatus = false;
	private boolean registerBool = true;
	public  static String sPhoneString;
	private ImageView agreeImageView;
	private EditText phoneEditText;
	private EditText vCodeEditText;
	private TextView agreeInfoTextView;
	private EditText requestCodeEditText;
//	private ImageView vCodeStautsImageView;
	private VerificationTimeButton getVCodeTextView; 
	private YLStringVerification stringVerification;
	private EventHandler eventHandler;
	//public static ProgressDialog sProgressDialog;
	public static String sCode;
	public static String sCodeFromSrv;
	public static String sInvCode;
	@SuppressWarnings("unused")
	private SMSVCode smsvCode = null;
	private BroadcastReceiver smsReceiver = null;
	private final int TIME = 500;
	private Intent networkMonitorIntent;
	public static boolean sClearStatus;
	public static boolean sVerificationStatus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_verification);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("快速注册");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		
		networkMonitorIntent = new Intent();
		networkMonitorIntent.setAction("youlin.network.monitor");
		networkMonitorIntent.setPackage(getPackageName());
		startService(networkMonitorIntent);
		
		VerificationActivity.sVerificationStatus = false;
		
		handler.postDelayed(runnable, TIME);
		
		stringVerification  = new YLStringVerification(this);
		getVCodeTextView    = (VerificationTimeButton) (findViewById(R.id.tv_verificat_getvcode));
		getVCodeTextView.onCreate(savedInstanceState);
		
		agreeInfoTextView   = (TextView) findViewById(R.id.et_verificat_info);
		Spanned spanInfo = Html.fromHtml("<font color='#989898'>我已阅读并同意</font><font color='#fab502'>优邻服务协议</font>");
		agreeInfoTextView.setText(spanInfo);
		agreeInfoTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(VerificationActivity.this,PrivatePolicyActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		phoneEditText       = (EditText) findViewById(R.id.et_verificat_phone);
		vCodeEditText       = (EditText) findViewById(R.id.et_verificat_vcode);
		requestCodeEditText = (EditText) findViewById(R.id.et_verificat_invitation);
		agreeImageView      = (ImageView) findViewById(R.id.iv_verificat_agree);
		setAgreeImageView();
		
//		vCodeStautsImageView= (ImageView) findViewById(R.id.iv_vcode_status);
		
		requestCodeEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String strInvCode = s.toString();
				sInvCode = strInvCode;
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
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
		
		agreeImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if("yes".equals(agreeImageView.getTag().toString())){
					agreeImageView.setTag("no");
					agreeImageView.setImageResource(R.drawable.read_server);
					bAgreeStatus = true;
				}else{
					agreeImageView.setTag("yes");
					agreeImageView.setImageResource(R.drawable.read_server_hui);
					bAgreeStatus = false;
				}
			}
		});
		getVCodeTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone=phoneEditText.getText().toString();
				if(phone.isEmpty()||phone.equals("null")||phone==null||phone.length()<=0){
					Toast.makeText(VerificationActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
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
								RequestParams params = new RequestParams();
								String phone = phoneEditText.getText().toString();
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
													final String flag = jsonContext.getString("flag");
													if ("exist".equals(flag)) {
														Message msg = new Message();
														msg.what = 1;
														handler.sendMessage(msg);
													} else if ("ok".equals(flag)) {
														phoneEditText.setEnabled(false);
														smsvCode = new SMSVCode( 
																VerificationActivity.this,
																handler,
																eventHandler);
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
//														smsReceiver = new SMSReceiver();
														// 注册短信通知广播,获取验证码
														VerificationActivity.this.registerReceiver(smsReceiver,new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
														SMSSDK.getVerificationCode("86",phoneEditText.getText().toString().trim());
														sPhoneString = phoneEditText.getText().toString().trim();
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
								//sProgressDialog.show();
								//sProgressDialog.setCancelable(false);
							}
						});
					}
				}).start();
			}
		});
	}

	Runnable runnable = new Runnable() {  
        @Override  
        public void run() { 
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(getVCodeTextView.getClearInfoStatus()&&(true==VerificationActivity.sClearStatus)){//为true表示已经读完60s
						VerificationActivity.sPhoneString = "";
						VerificationActivity.sInvCode = "";
						VerificationActivity.sCode = "";
						phoneEditText.setText(VerificationActivity.sPhoneString);
						vCodeEditText.setText(VerificationActivity.sCode);
						requestCodeEditText.setText(VerificationActivity.sInvCode);
//						vCodeStautsImageView.setVisibility(View.GONE);
						vCodeEditText.setEnabled(true);
						phoneEditText.setEnabled(true);
						bVCodeStatus = false;
						bEditPhoneStatus = false;
						VerificationActivity.sClearStatus = false;
		        	}
				}
			});
        	handler.postDelayed(this, TIME);  
        }  
    };
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {		
		if(!getVCodeTextView.isEnabled()){
			phoneEditText.setText(VerificationActivity.sPhoneString);
			vCodeEditText.setText(VerificationActivity.sCode);
			requestCodeEditText.setText(VerificationActivity.sInvCode);
			phoneEditText.setEnabled(false);
			bEditPhoneStatus = true;
			VerificationActivity.sClearStatus = false;
		}else{
			VerificationActivity.sPhoneString = "";
			VerificationActivity.sInvCode = "";
			VerificationActivity.sCode = "";
			phoneEditText.setText(VerificationActivity.sPhoneString);
			vCodeEditText.setText(VerificationActivity.sCode);
			requestCodeEditText.setText(VerificationActivity.sInvCode);
			phoneEditText.setEnabled(true);
			bEditPhoneStatus = false;
			bVCodeStatus = false;
			VerificationActivity.sClearStatus = true;
		}
		if(bVCodeStatus){//表明验证码正确
			vCodeEditText.setEnabled(false);
//			vCodeStautsImageView.setVisibility(View.VISIBLE);
//			vCodeStautsImageView.setImageResource(R.drawable.vcode_tick);
		}else{
			vCodeEditText.setEnabled(true);
//			vCodeStautsImageView.setVisibility(View.GONE);
		}
		bAgreeStatus = true;
		agreeImageView.setTag("no");
		agreeImageView.setImageResource(R.drawable.read_server);
		super.onWindowFocusChanged(hasFocus);
	}
	
	private void setAgreeImageView(){
		agreeImageView.setTag("no");
		agreeImageView.setImageResource(R.drawable.read_server);
		bAgreeStatus = true;
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
			if (index == 1) {
				//sProgressDialog.cancel();
				//sProgressDialog = null;
				bEditPhoneStatus = false;
				bAgreeStatus     = false;
				bVCodeStatus     = false;
				phoneEditText.setText("");
				getVCodeTextView.sendCloseMsg();
				//App.map.clear();
				registerBool=false;
				Toast toast = Toast.makeText(getApplicationContext(), "该手机已被注册", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			if(index==2){
				VerificationActivity.sCode = data.toString();
				vCodeEditText.setText(VerificationActivity.sCode);
				return;
			}
			if(index==3){//证明可以点击下一步
				String phone=phoneEditText.getText().toString();
				SetInvite(phone);
				return;
			}
			if (result == SMSSDK.RESULT_COMPLETE) {
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					phoneEditText.setEnabled(false);
					Loger.i("TEST", "提交验证码成功");
				}else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					//sProgressDialog.cancel();
					//sProgressDialog = null;
				}
				return;
			} else {
				//验证码错误
				if(bVCodeStatus){
					Toast.makeText(VerificationActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(VerificationActivity.this, "服务器维护中...", Toast.LENGTH_SHORT).show();
				}
				bVCodeStatus = false;
				//vCodeStautsImageView.setImageResource(R.drawable.vcode_cross);
				return;
			}
		}
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(VerificationActivity.this);
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		if(smsReceiver != null){
			SMSSDK.unregisterEventHandler(eventHandler);
			this.unregisterReceiver(smsReceiver);
			smsReceiver = null;
		}
		super.onPause();
		JPushInterface.onPause(VerificationActivity.this);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.verification_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			   finish();
			   bEditPhoneStatus = false;
			   bAgreeStatus = false;
			   bVCodeStatus = false; 
			break;
		case R.id.finish:
			String phone=phoneEditText.getText().toString();
			if(!NetworkMonitorService.bNetworkMonitor){
				Toast.makeText(VerificationActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				break;
			}else if(phone.isEmpty()||phone.equals("null")||phone==null||phone.length()<=0){
				Toast.makeText(VerificationActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
				break;
			}else if(!stringVerification.checkPhoneNumFormat(phone) || phone.length()!=11){
				Toast.makeText(VerificationActivity.this,"请填写正确的手机号", Toast.LENGTH_SHORT).show();
				break;
			}
//			else if(!bVCodeStatus){
//				Toast.makeText(VerificationActivity.this, "验证码错误，请正确填写", Toast.LENGTH_SHORT).show();
//				break;
//			}
			else if(!bAgreeStatus){
				Toast.makeText(VerificationActivity.this, "请确认已经阅读优邻服务协议", Toast.LENGTH_SHORT).show();
				break;
			}else{
				if(bVCodeStatus){
					Message message = new Message();
					message.what = 3;
					handler.sendMessage(message);
				}else{
					final YLProgressDialog dialog = YLProgressDialog.createDialogwithcircle(VerificationActivity.this,"",0);
					try {
						dialog.show();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
//						SMSSDK.submitVerificationCode("86", sPhoneString, sCode);
						RequestParams params = new RequestParams();
						params.put("phone", VerificationActivity.sPhoneString);
						params.put("code", VerificationActivity.sCode);
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
										Loger.i("TEST", "成功");
										try {
											dialog.dismiss();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										return;
									}else if(500==flag){//失败
//										vCodeStautsImageView.setVisibility(View.GONE);
										bVCodeStatus = false;
										Toast.makeText(VerificationActivity.this, "验证码错误，请正确填写", Toast.LENGTH_SHORT).show();
										Loger.i("TEST", "失败");
										try {
											dialog.dismiss();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										return;
									}else{//异常
//										vCodeStautsImageView.setVisibility(View.GONE);
										bVCodeStatus = false;
										Toast.makeText(VerificationActivity.this, "验证码错误，请正确填写", Toast.LENGTH_SHORT).show();
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
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			}
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void saveSharePrefrence(){
		Editor sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("phone", sPhoneString);
		sPhoneString = null;
		sharedata.commit();
	}

	private void SetInvite(String phone) {
		VerificationActivity.sInvCode =requestCodeEditText.getText().toString().trim();
		RequestParams reference = new RequestParams();
		reference.put("inv_phone", phone);
		reference.put("inv_code", VerificationActivity.sInvCode);
		reference.put("tag", "checkinvstatus");
		reference.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String flag = response.getString("flag");
					if (flag.equals("error")) {
						String errorInfo = response.getString("yl_msg");
						Toast.makeText(VerificationActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
					} else if(flag.equals("overflow")){
						String errorInfo = response.getString("yl_msg");
						Toast.makeText(VerificationActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
					} else if (flag.equals("ok")) {
						String type = response.getString("type");
						saveSharePrefrence();
						bVCodeStatus = false;
						bAgreeStatus = false;
						VerificationActivity.sPhoneString = "";
						VerificationActivity.sInvCode = "";
						VerificationActivity.sCode = "";
						VerificationActivity.sClearStatus = false;
						Intent intent=new Intent(VerificationActivity.this, RegisterActivity.class);
						if(type.equals("0")){
							intent.putExtra("inviteType", "0");
						}else if(type.equals("1")){
							intent.putExtra("inviteType", "1");
						}else if(type.equals("2")){
							intent.putExtra("inviteType", "2");
						}
						sVerificationStatus = true;
						startActivity(intent);
						finish();
						LoginActivity.sLoginActivity.finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(VerificationActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
}
