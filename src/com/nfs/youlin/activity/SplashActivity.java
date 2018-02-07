package com.nfs.youlin.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import gov.nist.core.NameValue;

import com.easemob.applib.model.HXNotifier;
import com.easemob.chat.EMChat;
import com.easemob.chatuidemo.DemoApplication;
import com.nfs.youlin.R;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.HttpClientHelper;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SSLCustomSocketFactory;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.CustomToast;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 开屏页
 *
 */
@SuppressLint("HandlerLeak")
public class SplashActivity extends Activity {
//	private RelativeLayout rootLayout;
//	private TextView versionText;
	private String userPhone;	
	private final int NET_EXIT = 101;    //退出当前activity
	private final int NET_SUCCESS = 102; //有网络
	private final int NET_FAILED = 103;  //无网络
	private int NotificationStatus;
	private String Extras = null;
	private boolean bIsRunningNetMonitor = false;
	private boolean bNetWorkStop = false;
	private boolean bNetWorkIsConnect = false;
	private boolean bNetWorkMonitorStatus = false;
	private boolean bCheckToken = false;
	private boolean bRequestFlag = false;
	private String sFlag = "no"; //ok
	private CustomToast customToast;
	private Bundle userInfobundle;
	private boolean bIsRunningInitSplash = false;
	private int TIME = 8000;
	private Thread netWorkThread = new Thread(new Runnable() {
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
					if(!bIsRunningInitSplash){
						msg.what=NET_SUCCESS;
					    handler.sendMessage(msg);
					}
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
	Boolean initBool;
	
	@Override
	protected void onCreate(Bundle arg0) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_splash);
		super.onCreate(arg0);
		NotificationStatus = this.getIntent().getIntExtra("Notification", 0);
		try {
			Extras = this.getIntent().getStringExtra("Extras");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Extras = null;
			e.printStackTrace();
		}
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0525);
//		TokenService.sBooleanShowToastFromSrv = false;
		EMChat.getInstance().setAutoLogin(false);
		SharedPreferences initBoolXml=getSharedPreferences("initStatus",Context.MODE_PRIVATE);
		initBool=initBoolXml.getBoolean("init_status",false);
		customToast = new CustomToast(this);
		App.sNewPushRecordCount = getNewPushRecordCount();
		userInfobundle = new Bundle();
		Loger.i("TEST", "当前唯独推送信息数==>"+App.sNewPushRecordCount);
		handler.postDelayed(runnable, TIME);
		//requestHTTPSPage();
		//进入主页面
		if(!bIsRunningInitSplash){
			initSplash();
		}
		bIsRunningNetMonitor = true;
		netWorkThread.start();
		
	}
	
	private MyTrustManager xtm = new MyTrustManager();
	private MyHostnameVerifier hnv = new MyHostnameVerifier();

	private void initSplash3() {
		new Thread(new Runnable() {
			public void run() {
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("SSL");
			X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
			sslContext.init(null, xtmArray, new java.security.SecureRandom());
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(hnv);
		HttpsURLConnection urlCon = null;
		URL urlInstance = null;
		String firstCookie = "";
		String returnVal = "";
		StringBuffer sb = new StringBuffer();
		try {
			String params = "imei=" + App.sPhoneIMEI + "&tag=token" + "&apitype=users";
			urlInstance = new URL(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN);
			urlCon = (HttpsURLConnection) (urlInstance).openConnection();
			urlCon.setInstanceFollowRedirects(false);
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setRequestMethod("POST");
			// urlCon.setRequestProperty("Cookie", cookie);
			urlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			urlCon.setRequestProperty("Connection", "Keep-Alive");
			urlCon.connect();
			byte[] bpara = params.toString().getBytes();
			urlCon.getOutputStream().write(bpara, 0, bpara.length);
			urlCon.getOutputStream().flush();
			urlCon.getOutputStream().close();
			int responseCode = urlCon.getResponseCode();
			
			if (responseCode == 200) { // 请求成功
				BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			    // 读取结果，发送到主线程
			   
			    String read;
			    StringBuffer buffer=new StringBuffer();
			    while((read=bufferedReader.readLine())!=null){
			    	buffer.append(read);
			    }
			    Loger.i("LYM", "111111111111111--->"+buffer.toString());
			    bufferedReader.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlCon != null) {
				urlCon.disconnect();
			}
			if (urlInstance != null) {
				urlInstance = null;
			}
		}
			}
		}).start();
	}
	
	private void initSplash2(){
		new Thread(new Runnable() {
			public void run() {
				try {
					URL url = new URL(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN);
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("POST");
					 
					// 设置SSLSocketFoactory，这里有两种：1.需要安全证书 2.不需要安全证书；看官且往下看
					if (urlConnection instanceof HttpsURLConnection) { // 是Https请求
					    SSLContext sslContext = SplashActivity.getSLLContext();
					    if (sslContext != null) {
					        //SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
					        //((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslSocketFactory);
					    }
					}
					urlConnection.setDoOutput(true);
					PrintWriter pw = new PrintWriter(urlConnection.getOutputStream());
					String param="imei="+App.sPhoneIMEI+"&tag=token"+"&apitype=users";
					pw.print(param);
					pw.flush();
					pw.close();
					// 设置属性
					urlConnection.setConnectTimeout(8 * 1000);
					urlConnection.setReadTimeout(8 * 1000);
					 
					int responseCode = urlConnection.getResponseCode();
					if (responseCode == 200) { // 请求成功
						BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
					    // 读取结果，发送到主线程
					   
					    String read;
					    StringBuffer buffer=new StringBuffer();
					    while((read=bufferedReader.readLine())!=null){
					    	buffer.append(read);
					    }
					    Loger.i("LYM","11111111111111111111111111--->"+buffer.toString());
					    bufferedReader.close();
					}
					urlConnection.disconnect();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	public static SSLContext getSLLContext() {
	    SSLContext sslContext = null;
	    try {
	        sslContext = SSLContext.getInstance("TLS");
	        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
	            @Override
	            public void checkClientTrusted(X509Certificate[] chain, String authType)  {}
	 
	            @Override
	            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
	 
	            @Override
	            public X509Certificate[] getAcceptedIssuers() {
	                return new X509Certificate[0];
	            }
	        }}, new SecureRandom());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return sslContext;
	}
	private class MyHostnameVerifier implements HostnameVerifier{  
		  
        @Override  
        public boolean verify(String hostname, SSLSession session) {  
                // TODO Auto-generated method stub  
                return true;  
        }  
	}
	private class MyTrustManager implements X509TrustManager{  
		  
        @Override  
        public void checkClientTrusted(X509Certificate[] chain, String authType)  
                        throws CertificateException {  
                // TODO Auto-generated method stub  
                  
        }  

        @Override  
        public void checkServerTrusted(X509Certificate[] chain, String authType)  
                        throws CertificateException {  
                // TODO Auto-generated method stub  
                  
        }  

        @Override  
        public X509Certificate[] getAcceptedIssuers() {  
                // TODO Auto-generated method stub  
                return null;  
        }          
	}

	  
	private void  requestHTTPSPage() {
		new Thread(new Runnable() {
			public void run() {
				InputStream ins = null;
				String result = "";
				try {			
					
					ins = getAssets().open("youlin.cer"); // 下载的证书放到项目中的assets目录中
					CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
					Certificate cer = cerFactory.generateCertificate(ins);
					KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
					keyStore.load(null, null);
					keyStore.setCertificateEntry("trust", cer);
		                
					SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
					Scheme sch = new Scheme("https", socketFactory, 443);
					
					HttpParams params = new BasicHttpParams();
	                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	                HttpProtocolParams.setContentCharset(params,HTTP.DEFAULT_CONTENT_CHARSET);
	                HttpProtocolParams.setUseExpectContinue(params, true);
	                ConnManagerParams.setTimeout(params, 10000);
	                HttpConnectionParams.setConnectionTimeout(params, 10000);
	                HttpConnectionParams.setSoTimeout(params, 10000);
	                // 设置http https支持
	                SchemeRegistry schReg = new SchemeRegistry();
	                schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	                schReg.register(new Scheme("https", SSLCustomSocketFactory.getSocketFactory(SplashActivity.this), 443));
	                ClientConnectionManager conManager = new ThreadSafeClientConnManager(params, schReg);
	                
					//HttpClient mHttpClient = new DefaultHttpClient(conManager,params);
	                HttpClient mHttpClient = new DefaultHttpClient();
					mHttpClient.getConnectionManager().getSchemeRegistry().register(sch);
					//HttpClient mHttpClient=HttpClientHelper.getHttpClient();
					BufferedReader reader = null;
					
					try {
						HttpPost request = new HttpPost(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN);
						List<NameValuePair> param=new ArrayList<NameValuePair>();
						param.add(new BasicNameValuePair("imei", App.sPhoneIMEI));
						param.add(new BasicNameValuePair("tag", "token"));
						param.add(new BasicNameValuePair("apitype", "users"));
						request.setEntity(new UrlEncodedFormEntity(param,HTTP.UTF_8));
						HttpResponse response = mHttpClient.execute(request);
						Loger.i("LYM", "2222222222222222222---->"+response.getStatusLine().getStatusCode());
						if (response.getStatusLine().getStatusCode() != 200) {
							request.abort();
							return;
						}
						reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
						StringBuffer buffer = new StringBuffer();
						String line = null;
						while ((line = reader.readLine()) != null) {
							buffer.append(line);
						}
						result = buffer.toString();
						Loger.d("LYM", "result = " + result);
					} catch (Exception e) {
						e.printStackTrace();
						Loger.i("LYM", "6666666666666666666--->"+e.getMessage());
					} finally {
						if (reader != null) {
							reader.close();
						}
					}

				} catch (Exception e) {
					// TODO: handle exception
					Loger.i("LYM", "0000000--->"+e.getMessage());
				} finally {
					try {
						if (ins != null)
							ins.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();

	}
	
	private void initSplash(){
		SharedPreferences preferences=getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		final int addressTag=preferences.getInt("address_tag", 0);
		RequestParams params = new RequestParams();
		params.put("imei", App.sPhoneIMEI);
		params.put("tag", "token");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		if(!initBool){
			params.put("addr_cache", -1);
		}else{
			params.put("addr_cache", addressTag);
		}
		Loger.i("LYM", "splash-->"+addressTag+" "+App.sPhoneIMEI);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		bIsRunningInitSplash = true;
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params,
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				Loger.i("LYM", "flag==>"+response);
				bIsRunningInitSplash = false;
				String flag = null;
				String addressFlag = null;
				int addressFlagNum = 0;
				try {
					flag = response.getString("flag");
					App.shareStr=response.getString("share_info");
					if ("ok".equals(flag)) {// 可以直接跳转到main
						bCheckToken = true;
						bRequestFlag = true;
						sFlag = "ok";
						addressFlag = response.getString("addr_flag");
						if(addressFlag.equals("no")){
							addressFlagNum = response.getInt("addr_cache");
							String userNick = response.getString("user_nick");
							String userPortrait = response.getString("user_portrait");
							String userId = response.getString("user_id");
							Loger.i("LYM", "addressTag==>"+flag+" "+addressFlag);
							Editor editor=getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE).edit();
							editor.putInt("address_tag", addressFlagNum);
							editor.commit();
							AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(SplashActivity.this);
							try {
								allFamilyDaoDBImpl.deleteAllObject();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							List<Bundle> list=new ArrayList<Bundle>();
							JSONArray jsonArray=new JSONArray(response.getString("addr_info"));
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject=new JSONObject(jsonArray.getString(i));
								String family_name = jsonObject.getString("family_name");
								String user_avatar = jsonObject.getString("user_avatar");
								String family_address = jsonObject.getString("family_address");
								String family_id = jsonObject.getString("family_id");
								String family_member_count = jsonObject.getString("family_member_count");
								String primary_flag = jsonObject.getString("primary_flag");
								String is_family_member = jsonObject.getString("is_family_member");
								String family_building_num = jsonObject.getString("family_building_num");
								String family_apt_num = jsonObject.getString("family_apt_num");
								String city_name = jsonObject.getString("city_name");
								String city_id = jsonObject.getString("city_id");
								String block_name = jsonObject.getString("block_name");
								String block_id = jsonObject.getString("block_id");
								String community_name = jsonObject.getString("community_name");
								String community_id = jsonObject.getString("community_id");
								String entity_type = jsonObject.getString("entity_type");
								String ne_status = jsonObject.getString("ne_status");
								String fr_id = jsonObject.getString("fr_id");
								String city_code = jsonObject.getString("city_code");
								String apt_num_id = jsonObject.getString("apt_num_id");
								String building_num_id = jsonObject.getString("building_num_id");
								
								Bundle acountBundle = new Bundle();
								acountBundle.putString("fr_id", fr_id);
								acountBundle.putString("family_name", family_name);
								acountBundle.putString("user_avatar", user_avatar);
								acountBundle.putString("family_address", family_address);
								acountBundle.putString("family_id", family_id);
								acountBundle.putString("family_member_count", family_member_count);
								acountBundle.putString("primary_flag", primary_flag);
								acountBundle.putString("is_family_member", is_family_member);
								acountBundle.putString("family_building_num", family_building_num);
								acountBundle.putString("family_apt_num", family_apt_num);
								acountBundle.putString("city_name", city_name);
								acountBundle.putString("city_id", city_id);
								acountBundle.putString("block_name", block_name);
								acountBundle.putString("block_id", block_id);
								acountBundle.putString("community_name", community_name);
								acountBundle.putString("community_id", community_id);
								acountBundle.putString("entity_type", entity_type);
								acountBundle.putString("ne_status", ne_status);
								acountBundle.putString("city_code", city_code);
								acountBundle.putString("apt_num_id", apt_num_id);
								acountBundle.putString("building_num_id", building_num_id);
								list.add(acountBundle);
							}

							AllFamily allFamily = null;
							Bundle familyBundle = null;
							for (int i = 0; i < list.size(); i++) {
								allFamily = new AllFamily(SplashActivity.this);
								familyBundle = list.get(i);
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
								long cityId;
								try {
									cityId = Long.parseLong(familyBundle.getString("city_id"));
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									cityId = 0;
									e.printStackTrace();
								}
								allFamily.setFamily_city_id(cityId);
								allFamily.setFamily_block(familyBundle.getString("block_name"));
								String blockId = familyBundle.getString("block_id");
								if (blockId == null || blockId.isEmpty() || blockId.length() <= 0
										|| blockId.equals("null")) {
									blockId = "0";
								}
								allFamily.setFamily_block_id(Long.parseLong(blockId));
								String communityName = familyBundle.getString("community_name");
								allFamily.setFamily_community(communityName);
								long communityId = 0;
								try {
									communityId = Long.parseLong(familyBundle.getString("community_id"));
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								allFamily.setFamily_community_id(communityId);
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
								allFamily.setUser_alias(userNick);
								allFamily.setUser_avatar(userPortrait);
								allFamily.setLogin_account(Long.parseLong(userId));
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
								String cityCode = familyBundle.getString("city_code");
								Loger.d("test4", "login citycode="+cityCode);
								if (cityCode == null || cityCode.isEmpty() || cityCode.length() <= 0
										|| cityCode.equals("null")) {
									cityCode = "0";
								}
								allFamily.setfamily_city_code(cityCode);
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
								allFamily.setFamily_address_id(Long.parseLong(familyBundle.getString("fr_id")));
								allFamilyDaoDBImpl.saveObject(allFamily);
							}
						}
						
						
//						if(App.sUserLoginId>0){
//							EasemobHandler easemobHandler = new EasemobHandler(SplashActivity.this);
//							easemobHandler.userLogin(String.valueOf(App.sUserLoginId));
//							DemoApplication.getInstance().setUserName(String.valueOf(App.sUserLoginId));
//						}
//						Loger.i("TEST", "flag=====>"+flag);
					} else if ("no".equals(flag)) {// 需要重新登录
						bCheckToken = false;
						bRequestFlag = true;
						sFlag = "ok";
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
								Loger.i("TEST", "执行intoMainContent()");
								intoMainContent();
							}
							bRequestFlag = false;
							sFlag = "no";
						};
					}.execute();
				} catch (JSONException e) {
					e.printStackTrace();
					Loger.i("LYM", "error-------->"+e.getMessage());
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
					Throwable throwable) {
				Toast toast = Toast.makeText(getApplicationContext(),
					     "服务器异常...", Toast.LENGTH_SHORT);
			    toast.setGravity(Gravity.CENTER, 0, 0);
			    try {
					toast.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			    bIsRunningInitSplash = false;
			    Intent intent = new Intent(SplashActivity.this, InitTransparentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("exit", "exit");
				startActivity(intent);
			    finish();
			    new ErrorServer(SplashActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	
	private void intoMainContent(){
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(loadSharePrefrence() && checkUserLogon() && checkFileExist()){
					if(bCheckToken){//可以直接跳转到main
						getUserLoginID(userPhone);
						Loger.i("TEST","Splash登陆环信");
						EasemobHandler easemobHandler = new EasemobHandler(SplashActivity.this);
						easemobHandler.userLogin(String.valueOf(App.sUserLoginId));
						DemoApplication.getInstance().setUserName(String.valueOf(App.sUserLoginId));
						Intent intentMain = new Intent(SplashActivity.this, MainActivity.class);
						//intentMain.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						intentMain.putExtra("Notifications", NotificationStatus);
						intentMain.putExtra("NotificationString", Extras);
						startActivity(intentMain);
					}else{//需要重新登录
						Loger.i("TEST","需要重新登录Splash跳转到LoginActivity");
						startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					}
				}else{
					loadSharePrefrence();
					getUserLoginID(userPhone);
					if(!initBool){
						Loger.i("TEST","Splash跳转到InitActivity");
						Intent intent=new Intent(SplashActivity.this,InitActivity.class);
						startActivity(intent);
					}else{
						Loger.i("TEST","Splash跳转到LoginActivity");
						Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
						intent.putExtras(userInfobundle);
						startActivity(intent);
					}
				}
				Loger.i("TEST", "intoMainContent中执行finish");
				finish();
			}
		}, 500);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
//	private void uploadAllfamilySharePrefrence(Bundle bundle){
//		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
//		sharedata.putString("blockid", bundle.getString("blockid"));
//		sharedata.putString("detail", bundle.getString("detail"));
//		sharedata.putString("village", bundle.getString("village"));
//		sharedata.putString("familycommunityid", bundle.getString("familycommunityid"));
//		App.sFamilyCommunityId = Long.parseLong(bundle.getString("familycommunityid"));
//		App.sFamilyBlockId = Long.parseLong(bundle.getString("blockid"));
//		sharedata.putString("city", bundle.getString("city"));
//		sharedata.commit();
//	}
	/**
	 * 获取当前应用程序的版本号
	 */
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Loger.i("TEST", "执行onKeyDown");
			Intent intent = new Intent(SplashActivity.this, InitTransparentActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("exit", "exit");
			startActivity(intent);
			finish();
			//System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getUserPhone(){
		if(null==App.sUserPhone){
			loadSharePrefrence();
		}
		if(null==App.sUserPhone){
			SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
			String userPhone = sharedata.getString("phone", null);
			App.sUserPhone = userPhone;
		}
		Loger.i("TEST", "App.sUserPhone==>"+App.sUserPhone);
	}
	
	private void getUserLoginID(String phone){
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(SplashActivity.this);
		com.nfs.youlin.entity.Account account = accountDaoDBImpl.findAccountByPhone(phone);
		accountDaoDBImpl.releaseDatabaseRes();
		long userLoginId = account.getLogin_account();
		if(userLoginId>0){
			Loger.i("TEST", "Splash LoginId->"+ userLoginId);
			Loger.i("TEST", "Splash phone->"+ account.getUser_phone_number());
			App.sUserLoginId = userLoginId;
			App.sUserPhone   = account.getUser_phone_number();
		}
	}
	
	private void getUserID(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String userLongId = sharedata.getString("account", null);
		if(userLongId == null){
			userLongId = "-1";
		}
		App.sUserLoginId = Long.parseLong(userLongId);
	}
	
	private boolean checkFileExist(){
		String filePath = "/data/data/" + getPackageName().toString()+"/shared_prefs";
		String fileName = App.SMS_VERIFICATION_USER + ".xml";
		File file= new File(filePath, fileName);
		if(file.exists()){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean loadSharePrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER, Context.MODE_PRIVATE);
		userPhone = sharedata.getString("phone", null);
		if(userPhone != null){
			App.sUserPhone = userPhone;
			return true;
		}else{
			return false;
		}
	}
	
	private boolean checkUserLogon() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String phone = null;
		String login = null;
		String nick = null;
		String entype = null;
		
			phone = sharedata.getString("phone", null);
			login = sharedata.getString("account", "0");
			nick = sharedata.getString("username", null);
			entype = sharedata.getString("encryption", null);
		int reVal = Integer.parseInt(login);
		
		if(phone != null && reVal>0 && nick!=null && entype!=null){
			return true;
		}else{
			return false;
		}
	}
	
	public String getNewPushRecordCount(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String countString = sharedata.getString("recordCount", "0");
		if(countString==null | countString == "null" | countString.length()<=0 | countString.isEmpty()){
			countString = "0";
		}
		return countString;
	}
	
	public void saveNewPushRecordCount(String count){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		if(count==null){
			count = "0";
		}
		sharedata.putString("recordCount", count);
		sharedata.commit();
	}
	
	Handler handler = new Handler(){
		public void handleMessage(final Message msg) {
			switch(msg.what){
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
					Loger.i("TEST", "当前有网络"+"   bIsRunningInitSplash=>"+bIsRunningInitSplash);
					if(!bIsRunningInitSplash){
						initSplash();
					}
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
				
				bIsRunningInitSplash = false;
				//showToast(customToast);
				try {
					Toast toast=Toast.makeText(SplashActivity.this, "网络有问题", 0);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(SplashActivity.this, InitTransparentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("exit", "exit");
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(SplashActivity.this);
		MobclickAgent.onResume(this);
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
		super.onPause();
		JPushInterface.onPause(SplashActivity.this);
		MobclickAgent.onPause(this);
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
            connect.connect(new InetSocketAddress(host, port), 10*1000);  
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
	
	Runnable runnable = new Runnable() {  
        @Override  
        public void run() {  
            try {  
                handler.postDelayed(this, TIME);  
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
        		finish();
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    };
}
