package com.nfs.youlin.view;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

@SuppressWarnings("deprecation")
@SuppressLint("HandlerLeak")
public class QCodeDlgActivity extends Activity {
	private final int CODE_OK = 10001;
	private final int CODE_AD = 10002;   //#验证码或二维码错误
	private final int CODE_FR = 10003;   //#地址错误错误
	private final int CODE_AU = 10004;   //#告知用户无法审核需要物业
	private final int CODE_NU = 10005;   //#没有查到指定用户的
	private final int CODE_FD = 10006;   //#证明用户手动填写地址，需要联系管理员审核
	private final int CODE_ER = 10007;   //addrDetailObj.getAddrDetailId()
	private final int CODE_FU = 10008;   //onfailure
	private final int CODE_ME = 10009;   //匹配服务端地址码失败
	private final int NET_EXIT = 101;    //退出当前activity
	private final int NET_SUCCESS = 102; //有网络
	private final int NET_FAILED = 103;  //无网络
	private final int RESULT_OK = 10000; //#审核成功
	private final int RESULT_NO = 4001; //#审核失败
	private final int RESULT_ER = 4002; //#格式错误
	private ProgressBar progressBar;
	private TextView textView;
	private String mask_code;
	private final String TAG = "TEST";
	private int errCode = -1;
	private final String SUCCESS_COLOR = "#ffba01";
	private final String FAILURE_COLOR = "#ff4500";
	private boolean bRequestFlag = false;
	private String sFlag = "no"; //ok
	private Thread netWorkThread = null;
	private boolean bIsRunningNetMonitor = false;
	private boolean bNetWorkStop = false;
	private boolean bNetWorkIsConnect = false;
	private boolean bNetWorkMonitorStatus = false;
	private CustomToast customToast;
	private Long familyRecordId;
	private Long familyId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.qcode_dlg_activity);
		customToast = new CustomToast(this);
		progressBar = (ProgressBar) findViewById(R.id.pb_q_code);
		textView = (TextView) findViewById(R.id.tv_pb_q_code);
		mask_code = getIntent().getExtras().getString("code");
		familyRecordId = getIntent().getExtras().getLong("familyRecordId");
		familyId = getIntent().getExtras().getLong("familyId");
		if(mask_code.length()==8){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressBar.setVisibility(View.VISIBLE);
					textView.setVisibility(View.VISIBLE);
				}
			});
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					SystemClock.sleep(1000);
					postVerifyInfo(mask_code);
				}
			}).start();
		}else{
			VerificationToast verificationToast = new VerificationToast(QCodeDlgActivity.this);
			verificationToast.show("错误格式",FAILURE_COLOR,R.drawable.icon_toast_x);
			setResult(RESULT_ER);
			finish();
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		bIsRunningNetMonitor = true;
		netWorkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					if(false==bIsRunningNetMonitor){
						final Message msg=new Message();
						msg.what=NET_EXIT;
					    handler.sendMessage(msg);
						break;
					}
					bNetWorkMonitorStatus = connect(null,0);
					if(true == bNetWorkMonitorStatus){
						//当前有网络连接
						if(true==bNetWorkIsConnect){
							continue;
						}
						Loger.i("TEST", "当前有网络连接!!!!!!!");
						bNetWorkIsConnect = true;
						bNetWorkStop = false;
						final Message msg=new Message();
						msg.what=NET_SUCCESS;
					    handler.sendMessage(msg);
					}else{
						//当前无网络连接
						if((false==bNetWorkIsConnect)&&(true==bNetWorkStop)){
							continue;
						}
						Loger.i("TEST", "当前无网络连接.........");
						bNetWorkIsConnect = false;
						bNetWorkStop = true;
						final Message msg=new Message();
						msg.what=NET_FAILED;
					    handler.sendMessage(msg);
					}
				}
			}
		});
		netWorkThread.start();
		JPushInterface.onResume(QCodeDlgActivity.this);
		MobclickAgent.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}
	
	Handler handler = new Handler(){
		public void handleMessage(final Message msg) {
			switch(msg.what){
			case CODE_OK:
				new AsyncTask<Void, Void, Void>(){
					@Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						Long currenttime = System.currentTimeMillis();
						while(!bRequestFlag){
							if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
								bRequestFlag = true;
							}
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						if(bRequestFlag == true && sFlag.equals("ok")){
							VerificationToast verificationToast = new VerificationToast(QCodeDlgActivity.this);
							verificationToast.show("验证成功",SUCCESS_COLOR,R.drawable.icon_toast_dui);
							setResult(RESULT_OK);
							finish();
						}else{
							VerificationToast verificationToast = new VerificationToast(QCodeDlgActivity.this);
							verificationToast.show("验证失败",FAILURE_COLOR,R.drawable.icon_toast_x);
							setResult(RESULT_NO);
							finish();
						}
						bRequestFlag = false;
						sFlag = "no";
					};
				}.execute();
				break;
			case CODE_FD://#证明用户手动填写地址，需要联系管理员审核
				bRequestFlag = false;
				sFlag = "no";
				startActivity(new Intent(QCodeDlgActivity.this, VerifyCodeDlgActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				break;
			case CODE_AU://#告知用户无法审核需要物业
				new AsyncTask<Void, Void, Void>(){
					@Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						Long currenttime = System.currentTimeMillis();
						while(!bRequestFlag){
							if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
								bRequestFlag = true;
							}
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						if(bRequestFlag == true && sFlag.equals("ok")){
							VerificationToast verificationToast = new VerificationToast(QCodeDlgActivity.this);
							verificationToast.show("重复审核",FAILURE_COLOR,R.drawable.icon_toast_x);
						}else{
							VerificationToast verificationToast = new VerificationToast(QCodeDlgActivity.this);
							verificationToast.show("网络异常",FAILURE_COLOR,R.drawable.icon_toast_x);
							setResult(RESULT_NO);
							finish();
						}
						bRequestFlag = false;
						sFlag = "no";
					};
				}.execute();
				break;
			case CODE_ME://#匹配服务端地址码失败
				Loger.i(TAG, "CODE_ME==>验证码或二维码错误");
			case CODE_AD://#验证码或二维码错误
				Loger.i(TAG, "CODE_FR==>验证码或二维码错误");
			case CODE_FR://#地址错误错误
				Loger.i(TAG, "CODE_FR==>地址错误");
			case CODE_NU://#没有查到指定用户的
				Loger.i(TAG, "CODE_NU==>没有查到指定用户的");
			case CODE_ER://addrDetailObj.getAddrDetailId()
				Loger.i(TAG, "CODE_ER==>getAddrDetailId()");
			case CODE_FU://onFailure
				try {
					errCode = (Integer) msg.obj;
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					errCode = -1;
					Loger.i(TAG,"Exception e2=>"+e2.getMessage());
				}
				new AsyncTask<Void, Void, Void>(){
					@Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						Long currenttime = System.currentTimeMillis();
						while(!bRequestFlag){
							if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
								bRequestFlag = true;
							}
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						if(bRequestFlag == true && sFlag.equals("ok")){
							VerificationToast verificationToast = new VerificationToast(QCodeDlgActivity.this);
							if(-1!=errCode){
								verificationToast.show("ERROR:"+String.valueOf(errCode),FAILURE_COLOR,R.drawable.icon_toast_x);
								setResult(RESULT_NO);
								finish();
							}else{
								verificationToast.show("审核失败",FAILURE_COLOR,R.drawable.icon_toast_x);	
								setResult(RESULT_NO);
								finish();
							}
						}else{
							VerificationToast verificationToast = new VerificationToast(QCodeDlgActivity.this);
							verificationToast.show("网络异常",FAILURE_COLOR,R.drawable.icon_toast_x);
							setResult(RESULT_NO);
							finish();
						}
						bRequestFlag = false;
						sFlag = "no";
					};
				}.execute();
				break;
			case NET_EXIT:
				bIsRunningNetMonitor = false;
				if(netWorkThread!=null){
					try {
						customToast.hide();
					} catch (Exception e) {
						e.printStackTrace();
					}
					customToast = null;
					netWorkThread.interrupt();
					netWorkThread = null;
				}
				break;
			case NET_SUCCESS:
				bNetWorkIsConnect = true;
				try {
					customToast.hide();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case NET_FAILED:
				bNetWorkIsConnect = false;
				bRequestFlag = false;
				sFlag = "no";
				Loger.i("TEST","NETWORK_FAILED");
				try {
					showToast(customToast);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			default:
				break;
			}
		};
	};
	
	private void postVerifyInfo(String code){
		RequestParams params = new RequestParams();
		params.put("user_id", App.sUserLoginId);
		params.put("record_id", familyRecordId);
		if(familyId!=0){
			params.put("family_id", familyId);
			Loger.d(TAG, "family_id==>"+familyId);
		}
		params.put("mask_code", code);
		params.put("tag", "verify");
		params.put("apitype", IHttpRequestUtils.APITYPE[2]);
		Loger.d(TAG, "user_id==>"+App.sUserLoginId);
		Loger.d(TAG, "mask_code==>"+code);
		Loger.d(TAG, "record_id==>"+familyRecordId);
		SharedPreferences preferences=getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		int addressTag=preferences.getInt("address_tag", 0);
		params.put("addr_cache",addressTag);
		SyncHttpClient httpClient = new SyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params,
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				String flag = null;
				try {
					Loger.i("LYM", "flag----->"+response);
					Message msg = new Message();
					flag = response.getString("flag");
					String addressTag = response.getString("addr_flag");
					bRequestFlag = true;
					sFlag = "ok";
					
					if("ok".equals(flag) && addressTag.equals("ok")){
						
						msg.obj = response.getString("context");
						if(!updateDatabase((String) msg.obj)){
							//修改数据库失败
							msg.what = CODE_ER;
						}else{
							msg.what = CODE_OK;
						}
						handler.sendMessage(msg);
						return;
					}else if("match_err".equals(flag)){
						msg.what = CODE_ME;
						handler.sendMessage(msg);
						return;
					}else if("no_ad".equals(flag)){//#验证码或二维码错误
						msg.what = CODE_AD;
						handler.sendMessage(msg);
						return;
					}else if("no_fr".equals(flag)){// #地址错误错误
						msg.what = CODE_FR;
						handler.sendMessage(msg);
						return;
					}else if("audited".equals(flag)){// #告知用户无法审核需要物业
						msg.what = CODE_AU;
						handler.sendMessage(msg);
						return;
					}else if("no_user".equals(flag)){//# 没有查到指定用户的
						msg.what = CODE_NU;
						handler.sendMessage(msg);
						return;
					}else if("error".equals(flag)){//addrDetailObj.getAddrDetailId()
						msg.what = CODE_ER;
						handler.sendMessage(msg);
						return;
					}else if("failed".equals(flag)){//#证明用户手动填写地址，需要联系管理员审核
						msg.what = CODE_FD;
						handler.sendMessage(msg);
						return;
					}else if(addressTag.equals("no")){
						Toast.makeText(QCodeDlgActivity.this, response.getString("yl_msg"), 0).show();
						return;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
					Throwable throwable) {
				// TODO Auto-generated method stub
				new ErrorServer(QCodeDlgActivity.this, responseString);
				bRequestFlag = true;
				sFlag = "ok";
				Message msg = new Message();
				msg.what = CODE_FU;
				msg.obj = statusCode;
				handler.sendMessage(msg);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	private boolean updateDatabase(String jsonString){
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(jsonString);
		} catch (JSONException e1) {
			jsonArray = null;
			Loger.d(TAG,"updateDatabase Err=>"+e1.getMessage());
		}
		if(null==jsonArray){
			return false;
		}
		@SuppressWarnings("unused")
		Long city_id,block_id,community_id,building_num_id,apt_num_id,ne_status,entity_type,family_id;
		try {
			city_id         = jsonArray.getJSONObject(0).getLong("city_id");
			block_id        = jsonArray.getJSONObject(0).getLong("block_id");
			community_id    = jsonArray.getJSONObject(0).getLong("community_id");
			building_num_id = jsonArray.getJSONObject(0).getLong("building_num_id");
			apt_num_id      = jsonArray.getJSONObject(0).getLong("apt_num_id");
			ne_status       = jsonArray.getJSONObject(0).getLong("ne_status");
			entity_type     = jsonArray.getJSONObject(0).getLong("entity_type");
			family_id       = jsonArray.getJSONObject(0).getLong("family_id");
			AllFamilyDaoDBImpl dbImpl = new AllFamilyDaoDBImpl(QCodeDlgActivity.this);
			AllFamily family = dbImpl.findVerifyObject(0, String.valueOf(family_id), 
					String.valueOf(App.sUserLoginId)); //通过familyId查找
			family.setFamily_id(family_id);
			family.setFamily_city_id(city_id);
			family.setFamily_community_id(community_id);
			family.setFamily_block_id(block_id);
			family.setEntity_type(1);
			family.setNe_status(0);
			dbImpl.modifyObject(family,family.getFamily_address());
			dbImpl.releaseDatabaseRes();
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Loger.i(TAG, "修改数据库失败==>"+e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	private void showToast(final CustomToast toast) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					toast.show(-1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Looper.loop();
			}
		}).start();
	}
	
	private boolean connect(String host, int port) {  
		if (host == null) host = IHttpRequestUtils.SRV_URL;
        if (port == 0) port = 80;  
        Socket connect = new Socket();
        try {  
            connect.connect(new InetSocketAddress(host, port), 5*1000);  
            return connect.isConnected();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally{  
            try {  
                connect.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return false;  
    } 
	
}
