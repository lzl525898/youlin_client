package com.nfs.youlin.service;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chat.EMChatManager;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.personal.SystemSetActivity;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.Loger;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class TokenService extends Service {
	public static boolean sBooleanShowToastFromSrv = false;
	private String imeiString = null;
	private Handler handler;
	private Runnable runnable;
	private final int TIME = 8000;
	private final String TAG = "TEST";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		imeiString = YLPushUtils.getImei(this, imeiString);
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				try {
					checkToken();
					handler.postDelayed(this, TIME);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		handler.postDelayed(runnable, TIME);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try {
			handler.removeCallbacks(runnable);
		} catch (Exception e) {
			Loger.i(TAG, "SrvError=>"+e.getMessage());
			e.printStackTrace();
		}
		handler = null;
		runnable = null;
		MainActivity.sMainInitData = 0;
		super.onDestroy();
	}
	
	private void checkToken(){
		RequestParams params = new RequestParams();
		params.put("imei", imeiString);
		params.put("tag", "token");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params,
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				String flag = null;
				try {
					flag = response.getString("flag");
					if ("ok".equals(flag)) {
//						Loger.i(TAG, "当前处于正常登录状态");
						return;
					}else{
						Loger.i(TAG, "当前处于异常登录状态");
						if(!TokenService.sBooleanShowToastFromSrv){//防止注销时显示
							if(!isForeground(getApplication(),"com.nfs.youlin.activity.LoginActivity")){
								Toast.makeText(getApplicationContext(), "登录时出现异常，请重新登录！", Toast.LENGTH_LONG).show();
							}
							EMChatManager.getInstance().logout();
							try {
								handler.removeCallbacks(runnable);
							} catch (Exception e) {
								e.printStackTrace();
							}
							handler = null;
							runnable = null;
							MainActivity.sMainInitData = 0;
							
							try {
								MainActivity.sMainActivity.finish();
							} catch (Exception e) {
								e.printStackTrace();
							}		
						}else{
							try {
								handler.removeCallbacks(runnable);
							} catch (Exception e) {
								Loger.i(TAG, "SrvError=>"+e.getMessage());
								e.printStackTrace();
							}
							handler = null;
							runnable = null;
							MainActivity.sMainInitData = 0;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
		});
	}
	
	private boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className)) {
			return false;
		}
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			String tmpString = cpn.getClassName();
			Loger.i(TAG, "当前界面名称=>"+tmpString);
			if (className.equals(tmpString)) {
				return true;
			}
		}
		return false;
	}
}
