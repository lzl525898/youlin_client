package com.nfs.youlin.activity.personal;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.map.Text;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.activity.Apk_update_detail;
import com.nfs.youlin.activity.DownLoadManager;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.UpdataInfo;
import com.nfs.youlin.activity.UpdataInfoParser;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.service.YLUpdateService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.DataCleanManager;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.TimeClick;
import com.umeng.analytics.MobclickAgent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalInfoGuanyuActivity extends Activity {
	private String localVersion;
//	private UpdataInfo info;
	private final int UPDATA_NONEED = 0;
	private final int UPDATA_CLIENT = 1;
	private final int GET_UNDATAINFO_ERROR = 2;
	private final int SDCARD_NOMOUNTED = 3;
	private final int DOWN_ERROR = 4;
	private final int UPDATA_FORCE = 5;
	private boolean bStatus = false;
	private TextView versiontext;
	private TextView privateTv;
	private final int UPDATE_DETAIL = 40001;
	private String down_apk_url = null;
	private String strForce = null;
	private Context mContext;
	public ProgressDialog updatapd1;
	private DownLoadManager downLoadManager;
	public static boolean isOpen=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info_guanyu);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("关于优邻");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		downLoadManager=new DownLoadManager();
		mContext = this;
		RelativeLayout aboutLayout=(RelativeLayout)findViewById(R.id.Relayout);
		versiontext = (TextView)findViewById(R.id.tv);
		localVersion = getVersion();
		versiontext.setText("优邻V"+localVersion);
		privateTv = (TextView)findViewById(R.id.private_policy_tv);
		privateTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(PersonalInfoGuanyuActivity.this,PrivatePolicyActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		aboutLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Loger.i("TEST", "Version->"+localVersion);
				if(!TimeClick.isFastClick1000()){
					CheckVersionTask cv = new CheckVersionTask();
					new Thread(cv).start();
				}
//				Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+telPhoneTv.getText().toString()));
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
			}
		});
		updatapd1 = new ProgressDialog(this);
		updatapd1.setMessage("优邻正在更新...");
        updatapd1.setCancelable(false);
        updatapd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        updatapd1.setIndeterminate(false);
        DemoApplication myapp = (DemoApplication)getApplication();
        myapp.setupdateDialog(updatapd1);
		App.UPDATE_APK_PORSITION = "com.nfs.youlin.activity.personal.PersonalInfoGuanyuActivity";
		if(App.UPDATE_APK_STATUS){
			if(App.UPDATE_APK_TYPE == 1){
//				ProgressDialog pd1 = new ProgressDialog(this);
//				pd1.setMessage("优邻正在更新...");
//				pd1.setCancelable(false);
//				pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//				pd1.setIndeterminate(false);
//				pd1.show();
			}
			
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==android.R.id.home){
			finish();
		}
		return super.onOptionsItemSelected(item);
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
								Message msg = new Message();
								msg.what = UPDATA_NONEED;
								updateHandler.sendMessage(msg);
							}else{
								try {
									strForce = response.getString("force");
									down_apk_url = response.getString("url");
									String detail = response.getJSONArray("detail").get(0).toString();
									String apksize = response.getString("size");
									Intent intent = new Intent(PersonalInfoGuanyuActivity.this,Apk_update_detail.class);
									intent.putExtra("apksize", apksize);
									intent.putExtra("version", strCode);
									intent.putExtra("apkdetail", detail);
									intent.putExtra("apkforce", strForce);
									if(!isOpen){
										intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										startActivityForResult(intent, UPDATE_DETAIL);
									}
//									info = new UpdataInfo();
//									info.setSize(apksize);
//									info.setForce(strForce);
//									info.setDescription(detail);
//									info.setUrl(url);
//									info.setVersion(strCode);
//									info.setUrl_server(srv_url);
								} catch (JSONException e) {
									strForce = null;
								}
								if (strForce==null){
									Message msg = new Message();
									msg.what = GET_UNDATAINFO_ERROR;
									updateHandler.sendMessage(msg);
								}else{
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
//						startActivity(new Intent(PersonalInfoGuanyuActivity.this,Apk_update_detail.class));
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
	Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_NONEED:
				Toast.makeText(getApplicationContext(), "已经是最新版本!",Toast.LENGTH_SHORT).show();
				Loger.i("TEST", "已经是最新版本，不需要更新");
				bStatus = true;
				break;
			case UPDATA_CLIENT:
				 //对话框通知用户升级程序   
//				showUpdataDialog();
				break;
			case UPDATA_FORCE:
//				showForceUpdataDialog();
				break;
			case GET_UNDATAINFO_ERROR:
				//服务器超时   
//	            Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", 1).show(); 
	            Loger.i("TEST", "获取服务器更新信息失败");
	            bStatus = true;
				break;
			case SDCARD_NOMOUNTED:
				bStatus = true;
				Toast.makeText(getApplicationContext(), "手机没有SD内置卡，无法下载", Toast.LENGTH_SHORT).show(); 
				break;
			case DOWN_ERROR:
				//下载apk失败  
				bStatus = true;
	            Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show(); 
				break;
			}
		}
	};
	/*
	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("版本升级");
		builer.setMessage(info.getDescription());
		 //当点确定按钮时从服务器上下载 新的apk 然后安装   װ
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				updateApp(PersonalInfoGuanyuActivity.this);
				downLoadApk();
				bStatus = true;
			}
		});
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//do sth
				bStatus = true;
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
				updateApp(PersonalInfoGuanyuActivity.this);
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
	}*/
	
	protected void downLoadApk(final String url) {
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
					File file = downLoadManager.getFileFromServer(url, pd);
					if (file==null){
						Message msg = new Message();
						msg.what = SDCARD_NOMOUNTED;
						updateHandler.sendMessage(msg);
						pd.dismiss(); 
						return;
					}
					clearYoulinInfo(mContext);
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
	private void updateApp(Context context){
		//注销环信账户
		EMChatManager.getInstance().logout();
		//推送功能注销
		JPushInterface.stopPush(context);
	}
	
	private void clearYoulinInfo(Context context){
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == UPDATE_DETAIL){
			if(resultCode == RESULT_OK){
				String appName = getApplication().getResources().getText(R.string.app_name).toString();  
				Intent updateinte = new Intent(this,YLUpdateService.class);  
				updateinte.putExtra("appName",appName);  
				updateinte.putExtra("url",down_apk_url);  
				updateinte.putExtra("status",strForce);  
				startService(updateinte);  
				if(strForce.equals("1")){
			        updatapd1.show();
				}
			}
		}
	}
}
