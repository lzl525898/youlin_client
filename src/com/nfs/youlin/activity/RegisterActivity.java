package com.nfs.youlin.activity;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jpush.android.api.JPushInterface;

import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.nfs.youlin.R;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.utils.AESandCBC;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.MD5Util;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.utils.YLStringVerification;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 注册页
 */
@SuppressLint("HandlerLeak")
public class RegisterActivity extends Activity {
	private final int REG_SUCCESS = 100;
	private final int REG_FAILED  = 101;
	private static Boolean sSelectSexStatusBoolean=false;
	private EasemobHandler easemobHandler;
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	private RadioButton selBoyRadioButton;
	private RadioButton selGrilRadioButton;
	private YLStringVerification stringVerification;
	private Thread regActionThread;
	private ProgressDialog pd;
	private String inviteType;
	private String friendId;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case REG_SUCCESS:
				Loger.i("TEST", "设置成功 ");
				//设置IMEI
				VerificationActivity.sVerificationStatus = false;
				updateIMEI();
				Intent addressintent = new Intent(RegisterActivity.this, MainActivity.class);
				addressintent.putExtra("startmethod", "registersuccess");
				addressintent.putExtra("invite_type", inviteType);
				addressintent.putExtra("friend_id", friendId);
				addressintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(addressintent);
				finish();
				break;
			case REG_FAILED:
				Loger.i("TEST", "设置失败");
				try {
					pd.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
					pd = null;
				}
				userNameEditText.setText("");
				passwordEditText.setText("");
				confirmPwdEditText.setText("");
				Toast.makeText(RegisterActivity.this, "设置失败!", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_register);	
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("详细信息");
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		Intent intent=getIntent();
		inviteType=intent.getStringExtra("inviteType");
		stringVerification = new YLStringVerification(this);
		userNameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
		selBoyRadioButton = (RadioButton) findViewById(R.id.rb_register_select_boy);
		selBoyRadioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selGrilRadioButton.setChecked(false);
				selBoyRadioButton.setChecked(true);
				RegisterActivity.sSelectSexStatusBoolean = true;
			}
		});
		selGrilRadioButton = (RadioButton) findViewById(R.id.rb_register_select_gril);
		selGrilRadioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selGrilRadioButton.setChecked(true);
				selBoyRadioButton.setChecked(false);
				RegisterActivity.sSelectSexStatusBoolean = true;
			}
		});
	}
	
	private void getUserLoginID(String phone){
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(RegisterActivity.this);
		Account account = accountDaoDBImpl.findAccountByPhone(phone);
		accountDaoDBImpl.releaseDatabaseRes();
		long userLoginId = account.getLogin_account();
		if(userLoginId>0){
			Loger.i("TEST", "Register LoginId->"+ userLoginId);
			Loger.i("TEST", "Register phone->"+ account.getUser_phone_number());
			App.sUserLoginId = userLoginId;
			App.sUserPhone   = account.getUser_phone_number();
		}
	}
	
	private void saveSharePrefrence(String pwd, String phone, String loginId,String username){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("encryption", pwd);
		sharedata.putString("account", loginId);
		sharedata.putString("phone", phone);
		sharedata.putString("username", username);
		sharedata.commit();
	}
	
	private String loadPhonePrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER, Context.MODE_PRIVATE);
		return sharedata.getString("phone", null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.new_topic_change,menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.finish:
			setOnclickFinish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setOnclickFinish() {
		String nameStr=userNameEditText.getText().toString().trim();
		String pwdStr=passwordEditText.getText().toString();
		String pwdConfirmStr=confirmPwdEditText.getText().toString();
		if (!RegisterActivity.sSelectSexStatusBoolean) {
			Toast.makeText(RegisterActivity.this, "请选择性别", Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		}else if (nameStr.length() <= 0){
			Toast.makeText(RegisterActivity.this, "名字不能为空",Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		}else if(!YLStringVerification.checkNickNameBiaoQing(nameStr) || nameStr.equals("null")){
			Toast.makeText(RegisterActivity.this, "名字格式不正确",Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		}else if (!stringVerification.checkPwdFormat(pwdStr)) {
			Toast.makeText(RegisterActivity.this, "输入密码格式不正确",Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		}else if(!stringVerification.checkPwdFormat(pwdConfirmStr)){
			Toast.makeText(RegisterActivity.this, "输入密码格式不正确",Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		}else if (!pwdStr.equals(pwdConfirmStr)) {
			Toast.makeText(RegisterActivity.this, "密码不一致，请重新填写", Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		}else{
			runOnUiThread(new Runnable() {
				public void run() {
					pd = new ProgressDialog(RegisterActivity.this);
					pd.setMessage("设置中...");
					pd.setCancelable(false);
					pd.show();
				}
			});
			
				regActionThread = new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						NeighborsHttpRequest neighborsHttpRequest = new NeighborsHttpRequest(RegisterActivity.this);
						neighborsHttpRequest.setHttpUrl(IHttpRequestUtils.URL);
						Message msg = new Message();
						Account account = new Account(RegisterActivity.this);
						String phone = loadPhonePrefrence();
						App.sUserPhone = phone.trim();
						String pwd = passwordEditText.getText().toString().trim();
						account.setUser_phone_number(phone);
						if (selBoyRadioButton.isChecked()) {
							account.setUser_gender(1); // 男
						} else {
							account.setUser_gender(2); // 女
						}
						String encryptedPwd = null;
						try {
							encryptedPwd = MD5Util.getEncryptedPwd(pwd, App.sUserPhone);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							encryptedPwd = "0";
							e.printStackTrace();
						}
						account.setUser_name(userNameEditText.getText().toString().trim());
						// 注册论坛账户成
						Loger.i("TEST", "22222222222222222--->"+inviteType);
						if (true == neighborsHttpRequest.regForumAccount(account, encryptedPwd, inviteType)) {
							account.setUser_id(App.sUserLoginId);
							account.setLogin_account(App.sUserLoginId);
							if (encryptedPwd != null) {
								saveSharePrefrence(encryptedPwd, phone, String.valueOf(App.sUserLoginId),
										account.getUser_name());
							}
							friendId = neighborsHttpRequest.getFriendId();
						} else {
							Loger.i("TEST", "52323236666666666");
							msg.what = REG_FAILED;
							handler.sendMessage(msg);
							return;
						}
						boolean bRet = false;
						try {
							neighborsHttpRequest.setHttpUrl(IHttpRequestUtils.URL);
							bRet = neighborsHttpRequest.regEasemobAccount(String.valueOf(App.sUserLoginId));
						} catch (Exception e1) {
							e1.printStackTrace();
							bRet = false;
						}
						if (true == bRet) {
							AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(RegisterActivity.this);
							Loger.i("TEST","account.user_id:" + account.getUser_id());
							accountDaoDBImpl.saveObject(account);
							accountDaoDBImpl.releaseDatabaseRes();
							// 登录环信账户
							try {
								easemobHandler = new EasemobHandler(RegisterActivity.this);
								easemobHandler.userLogin(String.valueOf(App.sUserLoginId));
								DemoApplication.getInstance().setUserName(String.valueOf(App.sUserLoginId));
								new Thread(new Runnable() {
									public void run() {
										boolean myBool=EMChatManager.getInstance().updateCurrentUserNick(userNameEditText.getText().toString().trim());
										Loger.i("TEST", "updateCurrentUserNick-->"+myBool);
									}
								}).start();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							msg.what = REG_SUCCESS;
							handler.sendMessage(msg);
						} else {
							Loger.i("TEST", "888888888888888888");
							msg.what = REG_FAILED;
							handler.sendMessage(msg);
						}
					}
				});
				regActionThread.start();
			
		}
	}
	
	private void updateIMEI(){
		String imeiString = YLPushUtils.getImei(getApplicationContext(), null);
		RequestParams imeiParams = new RequestParams();
		imeiParams.put("user_id", App.sUserLoginId);
		imeiParams.put("user_phone_number", App.sUserPhone);
		imeiParams.put("imei", imeiString);
		imeiParams.put("tag", "upload");
		imeiParams.put("apitype", IHttpRequestUtils.APITYPE[0]);
		Loger.i("TEST","UPLOAD_INFO "+"App.sUserLoginId=>"+App.sUserLoginId);
		Loger.i("TEST","UPLOAD_INFO "+"App.sUserPhone=>"+App.sUserPhone);
		Loger.i("TEST","UPLOAD_INFO "+"imei=>"+imeiString);
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
				imeiParams, new JsonHttpResponseHandler(){
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					String flag = response.getString("flag");
					Loger.i("TEST","UPLOAD_INFO flag->"+flag);
					if("ok".equals(flag)){
						Loger.i("TEST","success->"+flag);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST","UPLOAD_INFO->"+e.getMessage());
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			};
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Loger.i("TEST","UPLOAD_INFO-Error->"+responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			};
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(RegisterActivity.this);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(RegisterActivity.this);
		MobclickAgent.onPause(this);
	}
}
