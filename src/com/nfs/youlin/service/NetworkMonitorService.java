package com.nfs.youlin.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.R.color;
import com.nfs.youlin.activity.Apk_update_detail;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.personal.PersonalInfoGuanyuActivity;
import com.nfs.youlin.activity.personal.PersonalInfoGuanyuActivity.CheckVersionTask;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.TimeClick;
import com.nfs.youlin.view.CustomToast;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

@SuppressLint("HandlerLeak")
public class NetworkMonitorService extends Service {
	private Runnable runnable;
	private final int TIME = 1000;
	private final String TAG = "TEST";
	private final int NET_SUCCESS = 102; // 有网络
	private final int NET_FAILED = 103; // 无网络
	private CustomToast customToast;
	private boolean bIsMsgStatus = false;
	public static boolean bNetworkMonitor = true;
	private Handler handler;
	private Handler msgHandler;
	private boolean pushBool = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Loger.i(TAG, "开启网络监听服务");
		msgHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case NET_SUCCESS:
					try {
						customToast.hide();
						int type = App.getAddrStatus(getApplicationContext());
						if (type == 1) {
							MainActivity.networkLayout.setVisibility(View.GONE);
						} else {//等待审核  0// 审核失败 2
							MainActivity.networkLayout.setVisibility(View.VISIBLE);
							MainActivity.networkLayout.setBackgroundColor(0xfffa5b11);
							MainActivity.networkLayout.setAlpha(0.6f);
							if (App.getNoAddrStatus(getApplicationContext())) {
								MainActivity.networkTv.setText("您的地址还未经过验证,请及时验证");
							} else {
								MainActivity.networkTv.setText("您还没有设置地址");
							}
						}
						if (pushBool) {
							if (!isForeground(getApplicationContext(), "com.nfs.youlin.activity.Apk_update_detail")) {// 界面不在前台运行
								if (!TimeClick.isFastClick1000()) {
									CheckVersionTask cv = new CheckVersionTask();
									new Thread(cv).start();
								}
								pushBool = false;
							}
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						//Loger.i(TAG, "NetworkMonitorService-HandlMsg-Success-Err" + e1.getMessage());
					}
					break;
				case NET_FAILED:
					try {
						showToast(customToast);
						try {
							MainActivity.networkLayout.setVisibility(View.VISIBLE);
							MainActivity.networkLayout.setBackgroundColor(color.black);
							MainActivity.networkLayout.setAlpha(0.5f);
							MainActivity.networkTv.setText("网络任性,出现问题,请检查设置");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						pushBool = true;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						Loger.i(TAG, "NetworkMonitorService-HandlMsg-Failure-Err" + e1.getMessage());
					}
					break;
				}
			};
		};
		customToast = new CustomToast(getApplicationContext());
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				try {
					// Loger.i(TAG, "开始判断网络");
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								bNetworkMonitor = connect(IHttpRequestUtils.SRV_URL, 0);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}).start();
					try {
						NetworkService.networkBool = bNetworkMonitor;
						if (bNetworkMonitor) {// 有网络
							//if (!bIsMsgStatus) {
								Message msg = new Message();
								msg.what = NET_SUCCESS;
								msgHandler.sendMessage(msg);
								bIsMsgStatus = true;
							//}
						} else {// 无网络
							customToast.show(10);
							//if (bIsMsgStatus) {
								Message msg = new Message();
								msg.what = NET_FAILED;
								msgHandler.sendMessage(msg);
								bIsMsgStatus = false;
							//}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handler.postDelayed(runnable, TIME);
					// Loger.i(TAG, "当前bNetworkMonitor状态==>"+bNetworkMonitor);

				} catch (Exception e) {
					e.printStackTrace();
					Loger.i(TAG, "异常===>" + e.getMessage());
				}
			}
		};
		handler.postDelayed(runnable, TIME);
		super.onCreate();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try {
			handler.removeCallbacks(runnable);
		} catch (Exception e) {
			Loger.i(TAG, "NetworkMonitorService=>" + e.getMessage());
			e.printStackTrace();
		}
		try {
			customToast.hide();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Loger.i(TAG, "结束网络监听服务");
		handler = null;
		msgHandler = null;
		runnable = null;
		customToast = null;
		bIsMsgStatus = false;
		super.onDestroy();
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
		if (port == 0)
			port = 80;
		Socket connect = new Socket();
		try {
			connect.connect(new InetSocketAddress(host, port), 8 * 1000);
			return connect.isConnected();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				connect.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
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
				httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
								// TODO Auto-generated method stub
								String strCode = null;
								String strForce = null;
								try {
									strCode = response.getString("vcode");
									strCode =strCode.substring(1, strCode.length());
									strForce = response.getString("force");
								} catch (JSONException e) {
									strCode = null;
									e.printStackTrace();
								}
								
								if (!strCode.equals(getVersion()) && strForce.equals("1")) {
									try {
										String detail = response.getJSONArray("detail").get(0).toString();
										String apksize = response.getString("size");
										Intent intent = new Intent(getApplicationContext(), Apk_update_detail.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra("apksize", apksize);
										intent.putExtra("version", strCode);
										intent.putExtra("apkdetail", detail);
										intent.putExtra("apkforce", strForce);
										intent.putExtra("appName", getApplicationContext().getResources()
												.getText(R.string.app_name).toString());
										intent.putExtra("url", response.getString("url"));
										intent.putExtra("status", response.getString("force"));
										intent.putExtra("type", "push");
										intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										startActivity(intent);
									} catch (NotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
								super.onSuccess(statusCode, headers, response);
							}

							@Override
							public void onFailure(int statusCode, Header[] headers, String responseString,
									Throwable throwable) {
								super.onFailure(statusCode, headers, responseString, throwable);
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getVersion() {
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

	// 判断某个界面是否在前台
	private boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className)) {
			return false;
		}
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			String tmpString = cpn.getClassName();
			Loger.i(TAG, "当前界面名称=>" + tmpString);
			if (className.equals(tmpString)) {
				return true;
			}
		}
		return false;
	}
	/*
	 * private boolean connect(String host) { if (host == null) host =
	 * NetworkService.host; boolean success=false; Process p =null; try { p =
	 * Runtime.getRuntime().exec("ping -c 1 -i 0.2 -W 1 " + host); int status =
	 * p.waitFor(); if (status == 0) { success = true; } else { success = false;
	 * } } catch (IOException e) { Loger.i(TAG, "IOException=>"+e.getMessage());
	 * success = false; } catch (InterruptedException e) { Loger.i(TAG,
	 * "InterruptedException=>"+e.getMessage()); success = false; } finally {
	 * p.destroy(); } return success; }
	 */
}
