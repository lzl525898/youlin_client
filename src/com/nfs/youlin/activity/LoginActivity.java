package com.nfs.youlin.activity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import u.aly.cl;

import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.thirdlogin.OnLoginListener;
import com.nfs.youlin.activity.thirdlogin.ThirdLoginBindActivity;
import com.nfs.youlin.activity.thirdlogin.UserInfo;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.entity.PushRecord;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.push.YLPushInitialization;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.service.NetworkMonitorService;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.AESandCBC;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.DataCleanManager;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.MD5Util;
import com.nfs.youlin.utils.NetworkJudge;
import com.nfs.youlin.utils.YLStringVerification;
import com.nfs.youlin.view.CustomToast;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class LoginActivity extends Activity implements OnClickListener, Callback, PlatformActionListener{
	public static LoginActivity sLoginActivity;
	private String[] userInfo = new String[2];
	private EditText username;
	private EditText password;
	private TextView textView;
	private Thread runQueryThread;
//	private ProgressDialog pd;
	private YLProgressDialog ylProgressDialog;
	private Bundle updateShareXmlBundle;
	private final int REG_SUCCESS      = 100;
	private final int REG_FAILED       = 101;
	private final int REG_NOTHING      = 102;
//	private final int SUCCESS_GET_INFO = 104;
	private final int FAILED_GET_INFO  = 105;
	private final int REFUSE_GET_INFO  = 106;
	private final int UPLOAD_INFO      = 107;
	private final int SUCCESS_LOGIN    = 111;
	private NeighborsHttpRequest httpRequest;
	private TextView forget_password; //忘记密码
	private boolean bReturnMianActiviy; //用来判断是否跳转init
	private String setPushFlag= "no";
	private String responsePushRecord;
	private String responseUserJson;
	private Thread loginInsertDataThread;
	private static int sInsertDataForLogin = 0;

	private static final int MSG_SMSSDK_CALLBACK = 1;
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR= 3;
	private static final int MSG_AUTH_COMPLETE = 4;
	
	private OnLoginListener signupListener;
	private Handler handler1;
	//短信验证的对话框
	private Dialog msgLoginDlg;
	private String picturePath;
	//private UserInfo userInfo = new UserInfo();
	private final String PICTURE_NAME = "userIcon.jpg";
	private Platform platform;
	private class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}
		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
			ds.setColor(Color.WHITE);
		}
	}
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
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
					handler.sendMessage(msg0);
					return;
				}
				break;
			case SUCCESS_LOGIN:
				Intent logintent = new Intent(LoginActivity.this,MainActivity.class);
				//logintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				logintent.putExtra("startmethod", "login");
				logintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(logintent);
				bReturnMianActiviy = true;
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
				Toast.makeText(LoginActivity.this, "帐户名或密码错误!", 0).show();
				break;
			case FAILED_GET_INFO:
				Toast.makeText(LoginActivity.this, "网络有问题", 0).show();
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
//				Loger.i("TEST","UPLOAD_INFO "+"App.sUserLoginId=>"+App.sUserLoginId);
//				Loger.i("TEST","UPLOAD_INFO "+"App.sUserPhone=>"+App.sUserPhone);
//				Loger.i("TEST","UPLOAD_INFO "+"imei=>"+imeiString);
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
									YLPushInitialization ylPushInitialization = new YLPushInitialization(LoginActivity.this);
									ylPushInitialization.setTags(LoginActivity.this, tagList);
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
						new ErrorServer(LoginActivity.this, responseString);						
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
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_login);
		bReturnMianActiviy = false;
		ylProgressDialog = YLProgressDialog.createDialogwithcircle(this,"加载中...",0);
		
		sLoginActivity = this;
		updateShareXmlBundle = new Bundle();
		App.sDeleteTagFinish = false;
		username = (EditText) findViewById(R.id.et_login_username);
		password = (EditText) findViewById(R.id.et_login_password);
		textView = (TextView) findViewById(R.id.tv_web_url);
		SpannableString text = new SpannableString(IHttpRequestUtils.WWW);
		text.setSpan(new URLSpanNoUnderline(IHttpRequestUtils.DOWN_URL),0,IHttpRequestUtils.WWW.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(text);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		final RelativeLayout userLayout=(RelativeLayout)findViewById(R.id.iv_username);
		final RelativeLayout pwdLayout=(RelativeLayout)findViewById(R.id.iv_password);
		final Button loginBtn=(Button)findViewById(R.id.login_btn);
		final Button registerBtn=(Button)findViewById(R.id.register_btn);
		getUserPhone();
		if(userInfo[0] != null){
			username.setText(userInfo[0]);
		}
		forget_password = (TextView) findViewById(R.id.tv_forget_password);
		forget_password.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		
		userLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				username.setText("");
			}
		});
		pwdLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				password.setText("");
			}
		});
		
		username.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				userLayout.setVisibility(View.VISIBLE);
				if(password.getText().toString().length()>0 && username.getText().toString().length()>0){
					loginBtn.setTextColor(LoginActivity.this.getResources().getColor(R.color.main));
					registerBtn.setTextColor(LoginActivity.this.getResources().getColor(R.color.main2));
				}else{
					loginBtn.setTextColor(LoginActivity.this.getResources().getColor(R.color.main2));
					registerBtn.setTextColor(LoginActivity.this.getResources().getColor(R.color.main));
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
		
		password.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				pwdLayout.setVisibility(View.VISIBLE);
				if(username.getText().toString().length()>0 && password.getText().toString().length()>0){
					loginBtn.setTextColor(LoginActivity.this.getResources().getColor(R.color.main));
					registerBtn.setTextColor(LoginActivity.this.getResources().getColor(R.color.main2));
				}else{
					loginBtn.setTextColor(LoginActivity.this.getResources().getColor(R.color.main2));
					registerBtn.setTextColor(LoginActivity.this.getResources().getColor(R.color.main));
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
		username.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					userLayout.setVisibility(View.VISIBLE);
				}else{
					//userLayout.setVisibility(View.GONE);
				}
			}
		});
		password.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					pwdLayout.setVisibility(View.VISIBLE);
				}else{
					//pwdLayout.setVisibility(View.GONE);
				}
			}
		});
		
		handler1 = new Handler(this);
		setOnLoginListener(new OnLoginListener() {
			public boolean onSignin(String platform, HashMap<String, Object> res) {
				// 在这个方法填写尝试的代码，返回true表示还不能登录，需要注册
				// 此处全部给回需要注册
				return true;
			}
			public boolean onSignUp(UserInfo info) { 
				// 填写处理注册信息的代码，返回true表示数据合法，注册页面可以关闭
				return true;
			}
		});
		initSDK(LoginActivity.this);
		findViewById(R.id.qq_bt).setOnClickListener(this);
		
	}
	
	
	public void login(View view) {
		Loger.i("TEST", "点击登录时，当前网络状态为==>"+NetworkMonitorService.bNetworkMonitor);
		if(!NetworkMonitorService.bNetworkMonitor){
			return;
		}
		final String userName = username.getText().toString().trim();
		if (userName == null || userName.isEmpty()) {
			Toast toast = Toast.makeText(getApplicationContext(), "账户不能为空",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		} else {
//			if (userInfo[0] == null || userInfo[0].isEmpty() || userInfo[1] == null || userInfo[1].isEmpty()) {
				Loger.i("TEST", "手机没有用户信息");
//			pd = new ProgressDialog(LoginActivity.this);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ylProgressDialog.show();
						}
					});
					Looper.loop();
				}
			}).start();
			runQueryThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					releaseData();
					final NeighborsHttpRequest httpRequest = new NeighborsHttpRequest(LoginActivity.this);
					httpRequest.setHttpUrl(IHttpRequestUtils.URL);
					String encryptedPwd = null;
					try {
						Loger.d("test4", "password.getText()=" + password.getText().toString().trim()
								+ "username.getText().toString()=" + username.getText().toString().trim());
						YLStringVerification verification = new YLStringVerification(LoginActivity.this);
						if (!verification.checkPhoneNumFormat(username.getText().toString())) {
							Toast.makeText(LoginActivity.this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
							return;
						}
//						encryptedPwd = AESandCBC.encrypt(password.getText().toString().trim(),
//								username.getText().toString().trim());
						encryptedPwd = MD5Util.getEncryptedPwd(password.getText().toString().trim(),username.getText().toString().trim());
						String MD5edPwd = MD5Util.getEncryptedPwd(password.getText().toString().trim(),username.getText().toString().trim());
//						Loger.i("LYM", "1111111--->"+MD5edPwd);
//						Loger.d("test4", "encryptedPwd=" + encryptedPwd+"MD5edPwd="+MD5edPwd+" ...phonenum="+username.getText().toString().trim());
					} catch (Exception e1) {
						encryptedPwd = "0";
						e1.printStackTrace();
					}
					httpRequest.getAccountInfo(userName, encryptedPwd.trim());
					Long currenttime = System.currentTimeMillis();
					while (!httpRequest.cRequestSet) {
						if(httpRequest.flag != null){
							if (httpRequest.flag.equals("no1")) {
								Loger.d("TEST", "httpRequest.flag.equals(no1)");
								httpRequest.flag = "none";
								Message msg = new Message();
								msg.what = REG_NOTHING;
								handler.sendMessage(msg);
								return;
							} else if (httpRequest.flag.equals("no")) {
								Loger.d("TEST", "httpRequest.flag.equals(no)");
								httpRequest.flag = "none";
								Message msg = new Message();
								msg.what = REG_FAILED;
								handler.sendMessage(msg);
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
							httpRequest.flag = "none";
							Message msg = new Message();
							msg.what = REG_NOTHING;
							handler.sendMessage(msg);
						} else if (httpRequest.flag.equals("no")) {
							httpRequest.flag = "none";
							Message msg = new Message();
							msg.what = REG_FAILED;
							handler.sendMessage(msg);
							return;
						} else if (httpRequest.flag.equals("ok")) {
							loginInsertDataThread = new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(LoginActivity.this);
									Bundle bundle = null;
									boolean bRet = false;
									int accountLen = httpRequest.acountBundleList.size();
									if (accountLen == 1) {
										bundle = httpRequest.acountBundleList.get(0);

									} else {
										AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(LoginActivity.this);
										AllFamily allFamily = null;
										Bundle familyBundle = null;
										bundle = httpRequest.acountBundleList.get(0);

										for (int i = 1; i < accountLen; i++) {
											allFamily = new AllFamily(LoginActivity.this);
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
										account = new Account(LoginActivity.this);
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
											App.setNewsRecviceStatus(LoginActivity.this, false);
										}else{
											App.setNewsRecviceStatus(LoginActivity.this, true);
										}
									}
									if (account != null) {
										Loger.i("TEST", "向数据库中插入新数据");
										saveSignature(bundle.getString("user_signature"));
										saveSharePrefrence(bundle.getString("user_phone_number"));
										accountDaoDBImpl.deleteAllObjects();
										accountDaoDBImpl.saveObject(account);
									}
									EasemobHandler easemobHandler = new EasemobHandler(LoginActivity.this);
									easemobHandler.userLogin(bundle.getString("user_id"));
									DemoApplication.getInstance().setUserName(bundle.getString("user_id"));
									uploadUserSharePrefrence(bundle);
									uploadSMSSharePrefrence(userName);
									getUserLoginID(userName);
									LoginActivity.sInsertDataForLogin = 1;
								}
							});
							loginInsertDataThread.start();
							/////////////////////////////////////////////////////////////////////////
							new AsyncTask<Void, Void, Void>(){
								@Override
								protected Void doInBackground(Void... params) {
									// TODO Auto-generated method stub
									while(true){
										if(LoginActivity.sInsertDataForLogin>0){
											LoginActivity.sInsertDataForLogin = 0;
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
									handler.sendMessage(msg);
								};
							}.execute();
						}
					} else {
						Message msg = new Message();
						msg.what = REG_NOTHING;
						Loger.d("TEST", "httpRequest.flag.equals(no1111111111)");
						handler.sendMessage(msg);
					}
				}
			});
			runQueryThread.start();
		}
	}
	
	public void register(View view){
		if(loadSharePrefrence()){
			startActivity(new Intent(LoginActivity.this, VerificationActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		}else{
			startActivity(new Intent(LoginActivity.this, VerificationActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		}
	
	}
		
	private void thirdLogin(final String phone,final String pwd){
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
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ylProgressDialog.show();
						}
					});
					Looper.loop();
				}
			}).start();
			runQueryThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					releaseData();
					final NeighborsHttpRequest httpRequest = new NeighborsHttpRequest(LoginActivity.this);
					httpRequest.setHttpUrl(IHttpRequestUtils.URL);
					try {
						YLStringVerification verification = new YLStringVerification(LoginActivity.this);
						if (!verification.checkPhoneNumFormat(phone)) {
							Toast.makeText(LoginActivity.this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
							return;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					httpRequest.getAccountInfo(phone, pwd);
					Long currenttime = System.currentTimeMillis();
					while (!httpRequest.cRequestSet) {
						if(httpRequest.flag != null){
							if (httpRequest.flag.equals("no1")) {
								Loger.d("TEST", "httpRequest.flag.equals(no1)");
								httpRequest.flag = "none";
								Message msg = new Message();
								msg.what = REG_NOTHING;
								handler.sendMessage(msg);
								return;
							} else if (httpRequest.flag.equals("no")) {
								Loger.d("TEST", "httpRequest.flag.equals(no)");
								httpRequest.flag = "none";
								Message msg = new Message();
								msg.what = REG_FAILED;
								handler.sendMessage(msg);
								return;
							}
						}
						if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
							httpRequest.cRequestSet = true;
						}
					}
					if (httpRequest.cRequestSet && httpRequest.flag != null) {// 正确获取数据
						Loger.i("TEST", "正确获取数据");
						if (httpRequest.flag.equals("no1")) {
							httpRequest.flag = "none";
							Message msg = new Message();
							msg.what = REG_NOTHING;
							handler.sendMessage(msg);
						} else if (httpRequest.flag.equals("no")) {
							httpRequest.flag = "none";
							Message msg = new Message();
							msg.what = REG_FAILED;
							handler.sendMessage(msg);
							return;
						} else if (httpRequest.flag.equals("ok")) {
							loginInsertDataThread = new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(LoginActivity.this);
									Bundle bundle = null;
									boolean bRet = false;
									int accountLen = httpRequest.acountBundleList.size();
									if (accountLen == 1) {
										bundle = httpRequest.acountBundleList.get(0);

									} else {
										AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(LoginActivity.this);
										AllFamily allFamily = null;
										Bundle familyBundle = null;
										bundle = httpRequest.acountBundleList.get(0);

										for (int i = 1; i < accountLen; i++) {
											allFamily = new AllFamily(LoginActivity.this);
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
										account = new Account(LoginActivity.this);
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
											App.setNewsRecviceStatus(LoginActivity.this, false);
										}else{
											App.setNewsRecviceStatus(LoginActivity.this, true);
										}
									}
									if (account != null) {
										Loger.i("TEST", "向数据库中插入新数据");
										saveSignature(bundle.getString("user_signature"));
										saveSharePrefrence(bundle.getString("user_phone_number"));
										accountDaoDBImpl.deleteAllObjects();
										accountDaoDBImpl.saveObject(account);
									}
									EasemobHandler easemobHandler = new EasemobHandler(LoginActivity.this);
									easemobHandler.userLogin(bundle.getString("user_id"));
									DemoApplication.getInstance().setUserName(bundle.getString("user_id"));
									uploadUserSharePrefrence(bundle);
									uploadSMSSharePrefrence(phone);
									getUserLoginID(phone);
									LoginActivity.sInsertDataForLogin = 1;
								}
							});
							loginInsertDataThread.start();
							/////////////////////////////////////////////////////////////////////////
							new AsyncTask<Void, Void, Void>(){
								@Override
								protected Void doInBackground(Void... params) {
									// TODO Auto-generated method stub
									while(true){
										if(LoginActivity.sInsertDataForLogin>0){
											LoginActivity.sInsertDataForLogin = 0;
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
									handler.sendMessage(msg);
								};
							}.execute();
						}
					} else {
						Message msg = new Message();
						msg.what = REG_NOTHING;
						handler.sendMessage(msg);
					}
				}
			});
			runQueryThread.start();
		}
	}
	
	private boolean loadSharePrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER, Context.MODE_PRIVATE);
		String phone = sharedata.getString("phone", null);
		if(phone != null){
			return true;
		}else{
			return false;
		}
	}
	
	private void setPushTag(String userJson){
		Message msg = new Message();
		msg.what = UPLOAD_INFO;
		msg.obj = userJson;
		handler.sendMessage(msg);
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
	
	private void uploadSMSSharePrefrence(String phone){
		Editor sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("phone", phone);
		sharedata.commit();
	}
	
	private void saveSharePrefrence(String phone){
		Editor sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("phone", phone);
		sharedata.commit();
	}
	
	private void getUserPhone() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String phone = sharedata.getString("phone", null);
		String encryption = sharedata.getString("encryption", null);
		userInfo[0] = phone;
		userInfo[1] = encryption;
	}
	
	private void getUserLoginID(String phone){
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(LoginActivity.this);
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
	
	private void setLogOffInfo(){
		//注销环信账户
		EMChatManager.getInstance().logout();

		MainActivity.currentcity="未设置";
		MainActivity.currentvillage = "未设置";
		App.sFamilyBlockId = 0L;
		App.sFamilyCommunityId = 0L;
		App.sFamilyId = 0L;
		App.sUserAppTime = 0L;
		App.sNewPushRecordCount = null;
		saveSharePrefrence(null,null,null,null,null,null,null);
		// TODO Auto-generated method stub
		AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(getApplicationContext());
		allFamilyDaoDBImpl.deleteAllObject();
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(getApplicationContext());
		accountDaoDBImpl.deleteAllObjects();
//		PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(getApplicationContext());
//		daoDBImpl.deleteAllObjects();
	}
	
	private void saveSharePrefrence(String city, String village,String detail,String familyid,
			String fimalycommunityid,String blockid,String username){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("city", city);
		sharedata.putString("village", village);
		sharedata.putString("detail", detail);
		sharedata.putString("familyid", familyid);
		sharedata.putString("familycommunityid", fimalycommunityid);
		sharedata.putString("blockid", blockid);
		sharedata.putString("username", username);
		sharedata.commit();
	}
	
	private void saveSignature(String strInfo){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("signature", strInfo);
		sharedata.commit();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(LoginActivity.this);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(LoginActivity.this);
		MobclickAgent.onPause(this);
	}
		
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(!bReturnMianActiviy){//判断是否跳转至mainactivity
			if(!VerificationActivity.sVerificationStatus){
				Loger.i("TEST", "LoginActivity onDestory");
				Intent intent = new Intent(LoginActivity.this,InitTransparentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
				intent.putExtra("exit", "exit");
				startActivity(intent);
				
			}
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
	
 
	/** 设置授权回调，用于判断是否进入注册 */
	public void setOnLoginListener(OnLoginListener l) {
		this.signupListener = l;
	}
	

	public void onClick(View v) {
		switch(v.getId()) {
//			case R.id.tvMsgRegister: {
//				//短信登录
//				//popupMsgLogin();
//				//Toast.makeText(this, "未完成短信登录", Toast.LENGTH_SHORT).show();
//			} break;
//			case R.id.tvWeixin: {
//				//微信登录
//				//测试时，需要打包签名；sample测试时，用项目里面的demokey.keystore
//				//打包签名apk,然后才能产生微信的登录
//				Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
//				authorize(wechat);
//			} break;
//			case R.id.tvWeibo: {
//				//新浪微博
//				Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
//				authorize(sina);
//			} break;
			case R.id.qq_bt: {
				//QQ空间
				Platform qq = ShareSDK.getPlatform(QQ.NAME);
				authorize(qq);
			} break;

		}
	}
	
	// 短信注册对话框
	/*
	private void popupMsgLogin() {
		msgLoginDlg = new Dialog(activity, R.style.WhiteDialog);
		View dlgView = View.inflate(activity, R.layout.tpl_msg_login_dialog, null);
		final EditText etPhone = (EditText) dlgView.findViewById(R.id.et_phone);
		final EditText etVerifyCode = (EditText) dlgView.findViewById(R.id.et_verify_code);
		Button btnGetVerifyCode = (Button) dlgView.findViewById(R.id.btn_get_verify_code);
		Button btnSendVerifyCode = (Button) dlgView.findViewById(R.id.btn_send_verify_code);
		btnGetVerifyCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = etPhone.getText().toString();
				if(TextUtils.isEmpty(phone)){
					Toast.makeText(activity, "请输入手机号码", Toast.LENGTH_SHORT).show();
				}else{
					SMSSDK.getVerificationCode("86", phone);
				}
			}
		});
		btnSendVerifyCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = etPhone.getText().toString();
				String verifyCode = etVerifyCode.getText().toString();
				if(TextUtils.isEmpty(verifyCode)){
					Toast.makeText(activity, "请输入验证码", Toast.LENGTH_SHORT).show();
				}else{
					SMSSDK.submitVerificationCode("86", phone, verifyCode);
				}
			}
		});
		msgLoginDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		msgLoginDlg.setContentView(dlgView);
		msgLoginDlg.show();
	}
	*/
	//执行授权,获取用户信息
	//文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
	private void authorize(Platform plat) {
		if (plat == null) {
			 //popupOthers();
			Toast.makeText(LoginActivity.this, "---null---", 0).show();
			return;
		}
		// 若本地没有授权过就请求用户数据
		plat.setPlatformActionListener(this);//
		plat.SSOSetting(false);// 此处设置为false，则在优先采用客户端授权的方法，设置true会采用网页方式
		plat.showUser(null);// 获得用户数据
	}
	
	//其他登录对话框
	/*
	private void popupOthers() {
		Dialog dlg = new Dialog(activity, R.style.WhiteDialog);
		View dlgView = View.inflate(activity, R.layout.tpl_other_plat_dialog, null);
		View tvFacebook = dlgView.findViewById(R.id.tvFacebook);
		tvFacebook.setTag(dlg);
		tvFacebook.setOnClickListener(this);
		View tvTwitter = dlgView.findViewById(R.id.tvTwitter);
		tvTwitter.setTag(dlg);
		tvTwitter.setOnClickListener(this);
		
		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlg.setContentView(dlgView);
		dlg.show();
	}
	*/
	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[] {platform.getName(), res};
			handler1.sendMessage(msg);
		}
	}
	
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler1.sendEmptyMessage(MSG_AUTH_ERROR);
		}
		t.printStackTrace();
	}
	
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler1.sendEmptyMessage(MSG_AUTH_CANCEL);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
			case MSG_AUTH_CANCEL: {
				//取消授权
				Toast.makeText(LoginActivity.this, "授权操作已取消", Toast.LENGTH_SHORT).show();
			} break;
			case MSG_AUTH_ERROR: {
				//授权失败
				//Toast.makeText(LoginActivity.this, "授权操作遇到错误，请阅读Logcat输出; \n 如微信登录，需要微信客户端", Toast.LENGTH_SHORT).show();
			} break;
			case MSG_AUTH_COMPLETE: {
				//授权成功
				Toast.makeText(LoginActivity.this, "授权成功，正在跳转登录操作…", Toast.LENGTH_SHORT).show();
				Object[] objs = (Object[]) msg.obj;
				String platform = (String) objs[0];
				HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
				if (signupListener != null && signupListener.onSignin(platform, res)) {
					initData(platform);
				}
			} break;
			case MSG_SMSSDK_CALLBACK: {
				if (msg.arg2 == SMSSDK.RESULT_ERROR) {
					Toast.makeText(LoginActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
				} else {
					switch (msg.arg1) {
						case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE: {
							if(msgLoginDlg != null && msgLoginDlg.isShowing()){
								msgLoginDlg.dismiss();
							}
							Toast.makeText(LoginActivity.this, "提交验证码成功", Toast.LENGTH_SHORT).show();
							Message m = new Message();
							m.what = MSG_AUTH_COMPLETE;
							m.obj = new Object[] {"SMSSDK", (HashMap<String, Object>) msg.obj};
							handler1.sendMessage(m);
						} break;
						case SMSSDK.EVENT_GET_VERIFICATION_CODE:{
							Toast.makeText(LoginActivity.this, "验证码已经发送", Toast.LENGTH_SHORT).show();
						} break;
					}
				}
			} break;
		}
		return false;
	}
	

	private void initSDK(Context context) {
		//初始化sharesdk,具体集成步骤请看文档：
		//http://wiki.mob.com/Android_%E5%BF%AB%E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
		ShareSDK.initSDK(context);
	}

	/** 初始化数据 */
	private void initData(String platName) {
		platform = ShareSDK.getPlatform(platName);
		String name = "";
		String userId = "";
		String userIcon = "";
		String gender = "";
		String thirdType = "";
		if (platform != null) {
			name = platform.getDb().getUserName();
			userId = platform.getDb().getUserId();
			userIcon = platform.getDb().getUserIcon();
			gender = platform.getDb().getUserGender();
			if (gender.equals("m")) {
				gender = "1";
			} else {
				gender = "2";
			}
		}
		//Toast.makeText(LoginActivity.this, name + "\n" + userId + "\n" + userIcon + "\n" + gender, 1).show();
		Loger.i("NEW", name + "\n" + userId + "\n" + userIcon + "\n" + gender + "\n" + platName);
		if(platName.equals("QQ")){
			thirdType="qq";
			Loger.i("NEW", "thirdType->"+thirdType);
		}
		requestIsBind(userId,name,userIcon,gender,thirdType);
	}
	private void requestIsBind(final String userId,final String name,final String userIcon,final String gender,final String thirdType){
		RequestParams params=new RequestParams();
		params.put("third_account", userId);
		String imei = YLPushUtils.getImei(getApplicationContext(), App.sPhoneIMEI);
		params.put("phone_imei", imei);
		params.put("tag", "loginthird");
		params.put("apitype", "users");
		AsyncHttpClient client=new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				Loger.i("NEW", "requestIsBind->"+response.toString());
				try {
					String flag=response.getString("flag");
					if(flag.equals("exist")){
						startActivity(new Intent(LoginActivity.this,ThirdLoginBindActivity.class)
								.putExtra("userId", userId)
								.putExtra("name", name)
								.putExtra("userIcon", userIcon)
								.putExtra("gender", gender)
								.putExtra("third_type", thirdType));
								
					}else if(flag.equals("already")){
						String phone=response.getString("phone");
						String passwd=response.getString("passwd");
						thirdLogin(phone,passwd);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				new ErrorServer(LoginActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
}
