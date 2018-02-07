package com.nfs.youlin.activity.personal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.activity.AlertDialogforblack;
import com.easemob.chatuidemo.activity.BlacklistActivity;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.Apk_update_detail;
import com.nfs.youlin.activity.DownLoadManager;
import com.nfs.youlin.activity.LoginActivity;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.UpdataInfo;
import com.nfs.youlin.activity.UpdataInfoParser;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.push.YLPushInitialization;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.service.YLUpdateService;
//import com.nfs.youlin.service.TokenService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.DataCleanManager;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.RingStatus;
import com.nfs.youlin.utils.VibrationStatus;
import com.nfs.youlin.view.SwitchView;
import com.nfs.youlin.view.SwitchView.OnStateChangedListener;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog.Builder;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("HandlerLeak")
public class SystemSetActivity extends Activity {
	private String localVersion;
	private UpdataInfo info;
	private final int UPDATA_NONEED = 0;
	private final int UPDATA_CLIENT = 1;
	private final int GET_UNDATAINFO_ERROR = 2;
	private final int SDCARD_NOMOUNTED = 3;
	private final int DOWN_ERROR = 4;
	private final int UPDATA_FORCE = 5;
	private final String TAG = "TEST";
	private SwitchView noticeSwitchView;
	private SwitchView vibratSwitchView;
	
	private RelativeLayout logonLinearLayout;
	private RelativeLayout blackLayout;
	private RelativeLayout freeLinearLayout;
	private RelativeLayout checkVersionLinearLayout;
	
	private LinearLayout topLinearLayout;
	private RelativeLayout cancelRelativeLayout;
	private RelativeLayout logonRelativeLayout;
	
	private PopupWindow popupLogonWindow;
	private View popupLogonView;
	private Thread logonUserThread;
	private ProgressDialog pd;
	private EMChatOptions chatOptions;
	private TextView cacheTv;
	String sFlag = null;
	public static boolean sbooleanSystemToLogin;
	private final int UPDATE_DETAIL = 40001;
	public ProgressDialog updatapd1;
	private DownLoadManager downLoadManager;
	public static boolean isOpen=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_systemset);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("设置");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		downLoadManager=new DownLoadManager(); 
		updatapd1 = new ProgressDialog(this);
		updatapd1.setMessage("优邻正在更新...");
        updatapd1.setCancelable(false);
        updatapd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        updatapd1.setIndeterminate(false);
        DemoApplication myapp = (DemoApplication)getApplication();
        myapp.setupdateDialog(updatapd1);
		cacheTv=(TextView)findViewById(R.id.cache_num_tv);
		//File dirFile=new File(getCacheDir()+File.separator+"picasso-cache");
		SystemSetActivity.sbooleanSystemToLogin = false;
		File cacheDir = new File(getCacheDir()+File.separator+"imageloader/Cache");
		if(cacheDir.exists()){
			Long cacheSizeLong=getFileSize(cacheDir);
			String cacheSizeStr=FormetFileSize(cacheSizeLong);
			if(cacheSizeStr.equals(".00B")){
				cacheTv.setText("0B");
			}else{
				cacheTv.setText(cacheSizeStr);
			}
		}
		chatOptions = EMChatManager.getInstance().getChatOptions();
		popupLogonView = getLayoutInflater().inflate(R.layout.popup_location_logon, null);
		topLinearLayout = (LinearLayout) popupLogonView.findViewById(R.id.top_ll);
		Loger.i("TEST", "app_id->"+App.sUserLoginId);
		popupLogonView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupLogonWindow.dismiss();
			}
		});
		
		cancelRelativeLayout = (RelativeLayout) topLinearLayout.findViewById(R.id.ll_popup_cancel);
		cancelRelativeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupLogonWindow.dismiss();
			}
		});
		
		logonRelativeLayout = (RelativeLayout) topLinearLayout.findViewById(R.id.ll_popup_logon);
		logonRelativeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupLogonWindow.dismiss();
				if(NetworkService.networkBool){
//					TokenService.sBooleanShowToastFromSrv = true;
					pd = new ProgressDialog(SystemSetActivity.this);
					pd.setMessage("正在注销...");
					pd.setCancelable(false);
					final Thread thread=new Thread(new Runnable() {
						public void run() {
							//Looper.prepare();
							handler.sendEmptyMessage(101);
							//Looper.loop();
						}
					});
					thread.start();
					final Timer timer=new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (pd.isShowing()) {
//								TokenService.sBooleanShowToastFromSrv = false;
								popupLogonWindow.dismiss();
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(SystemSetActivity.this, "注销失败,请检查网络！", Toast.LENGTH_SHORT).show();
									}
								});
								if(thread!=null){
									thread.interrupt();
								}
								finish();
							}
						}
					}, 10000);
					RequestParams params2 = new RequestParams();
					params2.put("user_id", App.sUserLoginId);
					params2.put("user_phone", App.sUserPhone);
					params2.put("status", 1);// 删除推送channelId和userId
					params2.put("tag", "logoff");
					params2.put("apitype", IHttpRequestUtils.APITYPE[0]);
					AsyncHttpClient httpClient = new AsyncHttpClient();
					httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params2,
							new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
							// TODO Auto-generated method stub
							try {
								sFlag = response.getString("flag");
								Loger.i("TEST", "注销时，flag==>" + sFlag);
								if("ok".equals(sFlag)){
									Loger.i("TEST", "注销时，推送ChannelId删除成功");
									// 注销环信账户
									EMChatManager.getInstance().logout();
									// 注销推送功能
									YLPushInitialization ylPushInitialization = new YLPushInitialization(getApplicationContext());
									ylPushInitialization.setTags(getApplicationContext(), new HashSet<String>());
									JPushInterface.stopPush(getApplicationContext());
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
									saveSharePrefrence(null, null, null, null, null, null, null);
									// TODO Auto-generated method stub
									AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(getApplicationContext());
									try {
										allFamilyDaoDBImpl.deleteAllObject();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(getApplicationContext());
									accountDaoDBImpl.deleteAllObjects();
									//PushRecordDaoDBImpl daoDBImpl =new PushRecordDaoDBImpl(getApplicationContext());
									//daoDBImpl.deleteAllObjects();
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
									timer.cancel();
									thread.interrupt();
									startActivity(new Intent(SystemSetActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
									finish();
									SystemSetActivity.sbooleanSystemToLogin = true;//为true时 初始化特殊处理
									MainActivity.sMainActivity.finish();
									MobclickAgent.onProfileSignOff();
									Platform qq = null;
									try {
										qq = ShareSDK.getPlatform(SystemSetActivity.this, QQ.NAME);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										ShareSDK.initSDK(SystemSetActivity.this);
										qq = ShareSDK.getPlatform(SystemSetActivity.this, QQ.NAME);
										e.printStackTrace();
									}
									if(qq.isValid()){
										qq.removeAccount();
									}
								}else{
									Loger.i("TEST", "注销时，没有找到userPhone");
									timer.cancel();
									thread.interrupt();
//									TokenService.sBooleanShowToastFromSrv = false;
									popupLogonWindow.dismiss();
									Toast.makeText(SystemSetActivity.this, "注销失败,请检查网络！", Toast.LENGTH_SHORT).show();
									finish();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Loger.i("TEST", "注销时，崩溃");
								timer.cancel();
								thread.interrupt();
//								TokenService.sBooleanShowToastFromSrv = false;
								popupLogonWindow.dismiss();
								Toast.makeText(SystemSetActivity.this, "注销失败,请检查网络！", Toast.LENGTH_SHORT).show();
								finish();
								e.printStackTrace();
							}
							super.onSuccess(statusCode, headers, response);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, String responseString,
								Throwable throwable) {
							// TODO Auto-generated method stub
							Loger.i("TEST", "注销时，推送ChannelId删除失败");
							timer.cancel();
							thread.interrupt();
//							TokenService.sBooleanShowToastFromSrv = false;
							popupLogonWindow.dismiss();
							Toast.makeText(SystemSetActivity.this, "注销失败,请检查网络！", Toast.LENGTH_SHORT).show();
							finish();
							super.onFailure(statusCode, headers, responseString, throwable);
						}
					});
				} else {
//					TokenService.sBooleanShowToastFromSrv = false;
					Toast.makeText(SystemSetActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		noticeSwitchView = (SwitchView) findViewById(R.id.switch_notice_voice);
		File voiceFile=new File(this.getFilesDir().getParent()+File.separator+"shared_prefs/"+"voice_status.xml");
		if(!voiceFile.exists()){
			noticeSwitchView.setState(true);
		}else{
			SharedPreferences preferences=getSharedPreferences(App.VOICE,Context.MODE_PRIVATE);
			Boolean voiceBool=preferences.getBoolean("voiceBool", false);
			if(voiceBool){
				noticeSwitchView.setState(true);
			}else{
				noticeSwitchView.setState(false);
			}
		}
		
		noticeSwitchView.setOnStateChangedListener(new OnStateChangedListener() {
			@Override
			public void toggleToOn() {
				// TODO Auto-generated method stub
				Loger.i("TEST","Notice status->"+noticeSwitchView.getState());
				Editor editor=getSharedPreferences(App.VOICE, Context.MODE_PRIVATE).edit();
				editor.putBoolean("voiceBool",true);
				editor.commit();
				
				chatOptions.setNoticeBySound(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgSound(true);		
				
				initPushNotificationStyle();
				noticeSwitchView.postDelayed(new Runnable() {
		            @Override 
		            public void run() {
		            	noticeSwitchView.toggleSwitch(true); //以动画效果切换到打开的状态
		            }},200);
			}
			@Override
			public void toggleToOff() {
				// TODO Auto-generated method stub
				Loger.i("TEST","Notice status->"+noticeSwitchView.getState());
				noticeSwitchView.toggleSwitch(false);
				Editor editor=getSharedPreferences(App.VOICE, Context.MODE_PRIVATE).edit();
				editor.putBoolean("voiceBool",false);
				editor.commit();
				
				chatOptions.setNoticeBySound(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
				
				initPushNotificationStyle();
				
			}
		});
		
		vibratSwitchView = (SwitchView) findViewById(R.id.switch_notice_vibration);
		File vibrationFile=new File(this.getFilesDir().getParent()+File.separator+"shared_prefs/"+"vibration_status.xml");
		if(!vibrationFile.exists()){
			vibratSwitchView.setState(true);
		}else{
			SharedPreferences preferences=getSharedPreferences(App.VIBRATION,Context.MODE_PRIVATE);
			Boolean vibraBool=preferences.getBoolean("vibraBool", false);
			if(vibraBool){
				vibratSwitchView.setState(true);
			}else{
				vibratSwitchView.setState(false);
			}
		}
		vibratSwitchView.setOnStateChangedListener(new OnStateChangedListener() {
			@Override
			public void toggleToOn() {
				// TODO Auto-generated method stub
				Loger.i("TEST","Vibrat status->"+vibratSwitchView.getState());
				Editor editor=getSharedPreferences(App.VIBRATION, Context.MODE_PRIVATE).edit();
				editor.putBoolean("vibraBool",true);
				editor.commit();

				chatOptions.setNoticedByVibrate(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(true);
				
				initPushNotificationStyle();
				
				vibratSwitchView.postDelayed(new Runnable() {
		            @Override 
		            public void run() {
		            	vibratSwitchView.toggleSwitch(true); //以动画效果切换到打开的状态
		            }},200);
				
			}
			@Override
			public void toggleToOff() {
				Loger.i("TEST","Vibrat status->"+vibratSwitchView.getState());
				// TODO Auto-generated method stub
				vibratSwitchView.toggleSwitch(false);
				Editor editor=getSharedPreferences(App.VIBRATION, Context.MODE_PRIVATE).edit();
				editor.putBoolean("vibraBool",false);
				editor.commit();
				
				chatOptions.setNoticedByVibrate(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				HXSDKHelper.getInstance().getModel().setSettingMsgVibrate(false);
				
				initPushNotificationStyle();
				
			}
		});
		
		
		logonLinearLayout = (RelativeLayout) findViewById(R.id.ll_systemset_logon);
		logonLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupLogonWindow = new PopupWindow(popupLogonView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				ColorDrawable cd = new ColorDrawable(0x90000000);
				popupLogonWindow.setBackgroundDrawable(cd);
				TranslateAnimation anim = new TranslateAnimation(
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 0f,
						Animation.RELATIVE_TO_PARENT, 1f,
						Animation.RELATIVE_TO_PARENT, 0f);
				anim.setDuration(500);
				topLinearLayout.setAnimation(anim);
				popupLogonWindow.showAtLocation(SystemSetActivity.this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
				popupLogonWindow.setTouchInterceptor(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						//取消popupweindow
//						popupLogonWindow.dismiss();
						return false;
					}
				});
				popupLogonWindow.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		
		freeLinearLayout = (RelativeLayout) findViewById(R.id.ll_systemset_free);
		freeLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File dirFile=new File(getCacheDir()+File.separator+"picasso-cache");
				File dirVoiceFile=new File(getCacheDir()+File.separator+"voice");
				File cacheDir = new File(getCacheDir()+File.separator+"imageloader/Cache");
				DataCleanManager cleanManager=new DataCleanManager();
				if(dirFile.exists()){
					cleanManager.deleteFilesByDirectory(dirFile);
				}
				if(dirVoiceFile.exists()){
					cleanManager.deleteFilesByDirectory(dirVoiceFile);
				}
				if(cacheDir.exists()){
					cleanManager.deleteFilesByDirectory(cacheDir);
				}
				cacheTv.setText("0B");
			}
		});
		
		checkVersionLinearLayout = (RelativeLayout)findViewById(R.id.ll_systemset_check_version);
		checkVersionLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				localVersion = getVersion();
				Loger.i("TEST", "Version->"+localVersion);
				CheckVersionTask cv = new CheckVersionTask();
				new Thread(cv).start();
			}
		});
		blackLayout=(RelativeLayout)findViewById(R.id.ll_black);
		blackLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			     startActivity(new Intent(SystemSetActivity.this, BlacklistActivity.class).putExtra("currentuser",String.valueOf(App.sUserLoginId)).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
	}
	
	public class CheckVersionTask implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				RequestParams params = new RequestParams();
				params.put("tag", "version");
				params.put("apitype", IHttpRequestUtils.APITYPE[6]);
				SyncHttpClient httpClient = new SyncHttpClient();
				httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params,
						new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						// TODO Auto-generated method stub
						Loger.i("LYM","1111111111111111--->"+response);
						String strCode = null;
						try {
							strCode = response.getString("vcode");
							strCode =strCode.substring(1, strCode.length());
						} catch (JSONException e) {
							strCode = null;
							e.printStackTrace();
						}
						if (strCode==null){
							Message msg = new Message();
							msg.what = GET_UNDATAINFO_ERROR;
							updateHandler.sendMessage(msg);
						}else{
							if (strCode.equals(localVersion)){
								Loger.i(TAG, "版本号相同1111111111");
								Message msg = new Message();
								msg.what = UPDATA_NONEED;
								updateHandler.sendMessage(msg);
							}else{
								String strForce = null;
								try {
									strForce = response.getString("force");
									String url = response.getString("url");
									String srv_url = response.getString("srv_url");
									//String detail = response.getString("detail");
									String detail = response.getJSONArray("detail").get(0).toString();
									String size = response.getString("size");
									info = new UpdataInfo();
									info.setSize(size);
									info.setForce(strForce);
									info.setDescription(detail);
									info.setUrl(url);
									info.setVersion(strCode);
									info.setUrl_server(srv_url);
									info.setApk_detail(response.getJSONArray("detail").get(0).toString());
									Intent intent = new Intent(SystemSetActivity.this,Apk_update_detail.class);
									intent.putExtra("apksize", info.getSize());
									intent.putExtra("version", strCode);
									intent.putExtra("apkdetail", info.getApk_detail());
									intent.putExtra("apkforce", info.getForce());
									if(!isOpen){
										intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										startActivityForResult(intent, UPDATE_DETAIL);
									}
								} catch (JSONException e) {
									strForce = null;
								}
								if (strForce==null){
									Message msg = new Message();
									msg.what = GET_UNDATAINFO_ERROR;
									updateHandler.sendMessage(msg);
								}else{
									Loger.i(TAG, "版本号不相同 ");
									Message msg = new Message();
									if("ok".equals(strForce)){
										msg.what = UPDATA_FORCE;
									}else{
										msg.what = UPDATA_CLIENT;
									}
									updateHandler.sendMessage(msg);
								}
							}
						}
						super.onSuccess(statusCode, headers, response);
					}
					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString,
							Throwable throwable) {
						Message msg = new Message();
						msg.what = GET_UNDATAINFO_ERROR;
						updateHandler.sendMessage(msg);
						super.onFailure(statusCode, headers, responseString, throwable);
					}
				});
			} catch (Exception e) {
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				updateHandler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}
	
	private String getVersion() {
//		String st = getResources().getString(R.string.Version_number_is_wrong);
		String st = "1.0";
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}
	
	Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_NONEED:
				Loger.i("TEST", "已经是最新版本，不需要更新1111111111111");
				Toast.makeText(SystemSetActivity.this, "已经是最新版本!",Toast.LENGTH_SHORT).show();
				break;
			case UPDATA_CLIENT:
				 //对话框通知用户升级程序   
				//showUpdataDialog();
				break;
			case UPDATA_FORCE:
				//showForceUpdataDialog();
				break;
			case GET_UNDATAINFO_ERROR:
				//服务器超时   
//	            Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", 1).show(); 
	            Loger.i("TEST", "获取服务器更新信息失败");
	            break;
			case SDCARD_NOMOUNTED:
				Toast.makeText(getApplicationContext(), "手机没有SD内置卡，无法下载", Toast.LENGTH_SHORT).show(); 
				break;
			case DOWN_ERROR:
				Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show(); 
				break;
			}
		}
	};
	
	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("版本升级");
		builer.setMessage(info.getDescription());
		 //当点确定按钮时从服务器上下载 新的apk 然后安装   װ
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Loger.i(TAG, "下载apk,更新");
				updateApp(SystemSetActivity.this);
				downLoadApk();
			}
		});
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builer.create();
		dialog.setCancelable(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				 if (keyCode == KeyEvent.KEYCODE_SEARCH){
					 return true;
				 }else{
					 return false;
				}
			}
		});
		dialog.show();
	}
	
	protected void showForceUpdataDialog(){
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("版本升级");
		builer.setMessage(info.getDescription());
		//当点确定按钮时从服务器上下载 新的apk 然后安装   
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Loger.i(TAG, "下载apk,更新");
				updateApp(SystemSetActivity.this);
				downLoadApk();
			}
		});
		AlertDialog dialog = builer.create();
		dialog.setCancelable(false);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				 if (keyCode == KeyEvent.KEYCODE_SEARCH){
					 return true;
				 }else{
					 return false;
				}
			}
		});
		dialog.show();
	}
	
	protected void downLoadApk() {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.show();
		pd.setCancelable(false);
		new Thread() {
			@Override
			public void run() {
				try {
					File file = downLoadManager.getFileFromServer(info.getUrl(), pd);
					if (file==null){
						Message msg = new Message();
						msg.what = SDCARD_NOMOUNTED;
						updateHandler.sendMessage(msg);
						pd.dismiss(); 
						return;
					}
					sleep(3000);
					installApk(file);
					pd.dismiss(); // 结束掉进度条对话框
				} catch (Exception e) {
					Loger.i("TEST", "error->" + e.getMessage());
					Message msg = new Message();
					msg.what = DOWN_ERROR;
					updateHandler.sendMessage(msg);
					pd.dismiss();
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	protected void installApk(File file) {  
	    Intent intent = new Intent();  
	    //执行动作  
	    intent.setAction(Intent.ACTION_VIEW);  
	    //执行的数据类型  
	    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");  
	    this.finish();
	    startActivity(intent);  
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

	public void saveSharePrefrence(String city, String village,String detail,String familyid,
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

	public String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public long getFileSize(File f) {
		long size = 0;
		try {
			File[] flist = f.listFiles();
			for (int i = 0; i < flist.length; i++) {
				size = size + flist[i].length();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			size = 0;
			e.printStackTrace();
		}
		return size;
	}
	private void initPushNotificationStyle(){
//		YLPushNotificationBuilder builder=new YLPushNotificationBuilder(SystemSetActivity.this);
//		SharedPreferences preferences1=getSharedPreferences(App.VOICE,Context.MODE_PRIVATE);
//		Boolean voiceBool=preferences1.getBoolean("voiceBool", true);
//		SharedPreferences preferences2=getSharedPreferences(App.VIBRATION,Context.MODE_PRIVATE);
//		Boolean vibraBool=preferences2.getBoolean("vibraBool", true);
//		builder.setNotificationBuilderStyle(voiceBool, vibraBool);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(SystemSetActivity.this);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(SystemSetActivity.this);
		MobclickAgent.onPause(this);
	}
	private void updateApp(Context context){
		//注销环信账户
		EMChatManager.getInstance().logout();
		//推送功能注销
		JPushInterface.stopPush(context);
		String dataBasePath=context.getFilesDir().getParent()+File.separator+"databases";
		String sharedPath=context.getFilesDir().getParent()+File.separator+"shared_prefs";
		Loger.i("youlin",dataBasePath);
		File dataBaseFile=new File(dataBasePath);
		File sharedFile=new File(sharedPath);
		DataCleanManager.deleteFilesByDirectory(dataBaseFile);
		DataCleanManager.deleteFilesByDirectory(sharedFile);
		MainActivity.currentcity="未设置";
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
	}
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==101){
				pd.show();
			}
		};
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) { // 清空消息
			if(requestCode == UPDATE_DETAIL){
				String appName = getApplication().getResources().getText(R.string.app_name).toString();  
				Intent updateinte = new Intent(this,YLUpdateService.class);  
				updateinte.putExtra("appName",appName);  
				updateinte.putExtra("url",info.getUrl());  
				updateinte.putExtra("status",info.getForce());  
				startService(updateinte);
				if(info.getForce().equals("1")){
			        updatapd1.show();
				}
				
			}
		}	
	}
}
