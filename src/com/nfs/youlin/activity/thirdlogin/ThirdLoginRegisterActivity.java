package com.nfs.youlin.activity.thirdlogin;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.VerificationActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.MD5Util;
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
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
@SuppressLint("HandlerLeak")
public class ThirdLoginRegisterActivity extends Activity{
	private final int REG_SUCCESS = 100;
	private final int REG_FAILED  = 101;
	private EasemobHandler easemobHandler;
	private EditText passwordEditText;
	private YLStringVerification stringVerification;
	private Thread regActionThread;
	private ProgressDialog pd;
	private String inviteType;
	private int loginType;
	private String friendId;
	private String name;
	private String gender;
	private String userIcon;
	private String userId;
	private String thirdType;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case REG_SUCCESS:
				Loger.i("TEST", "设置成功 ");
				//设置IMEI
				VerificationActivity.sVerificationStatus = false;
				updateIMEI();
				Intent addressintent = new Intent(ThirdLoginRegisterActivity.this, MainActivity.class);
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
				passwordEditText.setText("");
				Toast.makeText(ThirdLoginRegisterActivity.this, "设置失败!", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third_register);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("设置密码");
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		Intent intent=getIntent();
		inviteType=intent.getStringExtra("inviteType");
		loginType=intent.getIntExtra("loginType", 0);
		stringVerification = new YLStringVerification(this);
		passwordEditText = (EditText) findViewById(R.id.pwd_id);
		final ImageView chaHaoImg=(ImageView)findViewById(R.id.pwd_img);
		if(loginType==3){
			name = intent.getStringExtra("name");
			gender = intent.getStringExtra("gender");
			userIcon = intent.getStringExtra("userIcon");
			userId = intent.getStringExtra("userId");
			thirdType = intent.getStringExtra("third_type");
		}
		findViewById(R.id.register_bt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setOnclickFinish();
			}
		});
		passwordEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				chaHaoImg.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		chaHaoImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				passwordEditText.setText("");
				chaHaoImg.setVisibility(View.GONE);
			}
		});
	}
	private void getUserLoginID(String phone){
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(ThirdLoginRegisterActivity.this);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			//finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			return false;
		}
		return true;
	}
	private void setOnclickFinish() {
		String pwdStr=passwordEditText.getText().toString();
		if (!stringVerification.checkPwdFormat(pwdStr)) {
			Toast.makeText(ThirdLoginRegisterActivity.this, "输入密码格式不正确",Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		}else{
			runOnUiThread(new Runnable() {
				public void run() {
					pd = new ProgressDialog(ThirdLoginRegisterActivity.this);
					pd.setMessage("设置中...");
					pd.setCancelable(false);
					pd.show();
				}
			});
				regActionThread = new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						NeighborsHttpRequest neighborsHttpRequest = new NeighborsHttpRequest(ThirdLoginRegisterActivity.this);
						neighborsHttpRequest.setHttpUrl(IHttpRequestUtils.URL);
						Message msg = new Message();
						Account account = new Account(ThirdLoginRegisterActivity.this);
						String phone = loadPhonePrefrence();
						App.sUserPhone = phone.trim();
						String pwd = passwordEditText.getText().toString().trim();
						account.setUser_phone_number(phone);
						if(gender.equals("1")){
							account.setUser_gender(1); // 男
						}else if(gender.equals("2")){
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
						account.setUser_name(name);
						// 注册论坛账户成
						Loger.i("TEST", "22222222222222222--->" + inviteType);
						if (true == neighborsHttpRequest.regThirdAccount(account, encryptedPwd, inviteType, userId,
							userIcon, thirdType)) {
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
							AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(ThirdLoginRegisterActivity.this);
							Loger.i("TEST","account.user_id:" + account.getUser_id());
							accountDaoDBImpl.saveObject(account);
							accountDaoDBImpl.releaseDatabaseRes();
							// 登录环信账户
							try {
								easemobHandler = new EasemobHandler(ThirdLoginRegisterActivity.this);
								easemobHandler.userLogin(String.valueOf(App.sUserLoginId));
								DemoApplication.getInstance().setUserName(String.valueOf(App.sUserLoginId));
								new Thread(new Runnable() {
									public void run() {
										boolean myBool=EMChatManager.getInstance().updateCurrentUserNick(name);
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
		JPushInterface.onResume(ThirdLoginRegisterActivity.this);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(ThirdLoginRegisterActivity.this);
		MobclickAgent.onPause(this);
	}
}
