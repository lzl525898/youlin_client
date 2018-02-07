package com.nfs.youlin.activity.find;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.LoginActivity;
import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.CustomToast;
import com.nfs.youlin.view.QCodeDlgActivity;
import com.nfs.youlin.view.VerificationToast;
import com.nfs.youlin.view.VerifyCodeDlgActivity;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import u.aly.bu;

@SuppressLint("HandlerLeak")
@SuppressWarnings({ "unused", "deprecation" })
public class VerificationCodeActivity extends Activity implements OnClickListener{
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
	private final int RESULT_OK = 4000; //#审核成功
	private Thread netWorkThread = null;
	private int errCode = -1;
	private final String SUCCESS_COLOR = "#ffba01";
	private final String FAILURE_COLOR = "#ff4500";
	private final String TAG = "TEST";
	private YLProgressDialog ylProgressDialog;
	private EditText editText;
	private Button button;
	private Toast toast;
	private Long familyRecordId;
	private Long familyId;
	private boolean bRequestFlag = false;
	private String sFlag = "no"; //ok
	private boolean bIsRunningNetMonitor = false;
	private boolean bNetWorkStop = false;
	private boolean bNetWorkIsConnect = false;
	private boolean bNetWorkMonitorStatus = false;
	private CustomToast customToast;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification_code);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("验证码验证地址");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		customToast = new CustomToast(this);
		familyRecordId = this.getIntent().getLongExtra("familyRecordId", 0);
		familyId = this.getIntent().getLongExtra("familyId", 0);
		ylProgressDialog = YLProgressDialog.createDialogwithcircle(this, "加载中...", 0);
		button = (Button) findViewById(R.id.btn_verfiy_code);
		editText = (EditText) findViewById(R.id.et_verfty_code);
		button.setOnClickListener(this);
		setHintStyle(editText);
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
		JPushInterface.onResume(VerificationCodeActivity.this);
		MobclickAgent.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		bIsRunningNetMonitor = false;
		if(netWorkThread!=null){
			Loger.i("TEST", "login挂起监听网络线程。。。。");
			try {
				customToast.hide();
			} catch (Exception e) {
				e.printStackTrace();
			}
			customToast = null;
			netWorkThread.interrupt();
			netWorkThread = null;
		}
		JPushInterface.onPause(VerificationCodeActivity.this);
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_verfiy_code:
			if(bNetWorkIsConnect==false){
				return;
			}
			final String editString = editText.getText().toString().trim();
			if(editString.length()==8){
				HideSoftKeyBoard(v);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						editText.setEnabled(false);
						button.setText("验证中");
						button.setEnabled(false);
						ylProgressDialog.show();
					}
				});
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						SystemClock.sleep(1000);
						postVerifyInfo(editString);
					}
				}).start();
			}else{
				toast = new Toast(getApplicationContext());
				toast= Toast.makeText(getApplicationContext(),
					     "请输入正确的验证码", Toast.LENGTH_SHORT);
				   toast.setGravity(Gravity.CENTER, 0, 0);
				   toast.show();
			}
			break;
		default:
			break;
		}
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
						attributeReset();
						if(bRequestFlag == true && sFlag.equals("ok")){
							VerificationToast verificationToast = new VerificationToast(VerificationCodeActivity.this);
							verificationToast.show("验证成功",SUCCESS_COLOR,R.drawable.icon_toast_dui);
							setResult(RESULT_OK);
							StatusChangeutils changeutils;
							changeutils = new StatusChangeutils();
							changeutils.setstatuschange("ADDRVERIFY",1);
							finish();
						}else{
							VerificationToast verificationToast = new VerificationToast(VerificationCodeActivity.this);
							verificationToast.show("验证失败",FAILURE_COLOR,R.drawable.icon_toast_x);
						}
						bRequestFlag = false;
						sFlag = "no";
					};
				}.execute();
				break;
			case CODE_FD://#证明用户手动填写地址，需要联系管理员审核
				attributeReset();
				bRequestFlag = false;
				sFlag = "no";
				startActivity(new Intent(VerificationCodeActivity.this, VerifyCodeDlgActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				break;
			case CODE_AU://#告知用户无法重复审核
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
						attributeReset();
						editText.setText("");
						if(bRequestFlag == true && sFlag.equals("ok")){
							VerificationToast verificationToast = new VerificationToast(VerificationCodeActivity.this);
							verificationToast.show("重复审核",FAILURE_COLOR,R.drawable.icon_toast_x);
						}else{
							VerificationToast verificationToast = new VerificationToast(VerificationCodeActivity.this);
							verificationToast.show("网络异常",FAILURE_COLOR,R.drawable.icon_toast_x);
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
						attributeReset();
						editText.setText("");
						if(bRequestFlag == true && sFlag.equals("ok")){
							VerificationToast verificationToast = new VerificationToast(VerificationCodeActivity.this);
							if(-1!=errCode){
								verificationToast.show("ERROR:"+String.valueOf(errCode),FAILURE_COLOR,R.drawable.icon_toast_x);
							}else{
								verificationToast.show("审核失败",FAILURE_COLOR,R.drawable.icon_toast_x);	
							}
						}else{
							VerificationToast verificationToast = new VerificationToast(VerificationCodeActivity.this);
							verificationToast.show("网络异常",FAILURE_COLOR,R.drawable.icon_toast_x);
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
				attributeReset();
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
					Loger.i("LYM", "VerificationCodeActivity==>"+response);
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
					}else if("audited".equals(flag)){// #告知用户无法重复审核
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
						Toast.makeText(VerificationCodeActivity.this, response.getString("yl_msg"), 0).show();
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
				new ErrorServer(VerificationCodeActivity.this, responseString.toString());
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
			AllFamilyDaoDBImpl dbImpl = new AllFamilyDaoDBImpl(VerificationCodeActivity.this);
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
	
	private void attributeReset(){
		ylProgressDialog.dismiss();
		editText.setEnabled(true);
		button.setText("验证");
		button.setEnabled(true);
	}
	
	private void HideSoftKeyBoard(View v){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	private void setHintStyle(EditText editText){
		editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
//		CharSequence text = editText.getText();
//		if (text instanceof Spannable) {
//		    Spannable spanText = (Spannable)text;
//		    Selection.setSelection(spanText, text.length());
//		}
	}
	
}
