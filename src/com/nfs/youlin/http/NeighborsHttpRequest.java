package com.nfs.youlin.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.sasl.SASLMechanism.Success;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.platform.comapi.map.A;
import com.easemob.chatuidemo.utils.MD5Util;
import com.nfs.youlin.activity.AddressShowForBar;
import com.nfs.youlin.activity.LoginActivity;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.PushRecord;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class NeighborsHttpRequest implements IHttpRequestUtils{
	public static int forumUser_id = -1;
	public static boolean bRequestStatus = false;
	public List<Bundle> acountBundleList;
	private String requestUrl;
	private DefaultHttpClient mHttpClient;
	private Bundle acountBundle;
	private Context context;
	private boolean setPushRequestSet = false;
	private String setPushFlag= "no";
	private String responsePushRecord;
	private final int SUCCESS_CODE     = 100;
	private final int FAILED_CODE      = 101;
	private final int REFUSE_CODE      = 103;
	private final int SUCCESS_GET_INFO = 104;
	private final int FAILED_GET_INFO  = 105;
	private final int REFUSE_GET_INFO  = 106;
	public boolean cRequestSet = false;
	public String flag;
	private String friendId="0";
	public NeighborsHttpRequest(Context context){
		this.context = context;
		//mHttpClient = new DefaultHttpClient(setTimeOut(IHttpRequestUtils.TIME_OUT));
		mHttpClient = HttpClientHelper.getHttpClient();
	}
	
	private HttpParams setTimeOut(int timeout){
		HttpParams params = new BasicHttpParams(); 
		HttpConnectionParams.setConnectionTimeout(params, timeout); 
		HttpConnectionParams.setSoTimeout(params, timeout);
		return params;
	}
	
	@Override
	public void setHttpUrl(String url) {
		// TODO Auto-generated method stub
		requestUrl = url;
	}

	public boolean checkEasemobIsOnline(String phone){
		if (requestUrl == null) {
			return false;
		}
		return false;
	}
	
	public boolean regEasemobAccount(String LoginId) {
		if (requestUrl == null) {
			return false;
		}
		RequestParams params = new RequestParams();
		params.put("user", LoginId);
		params.put("tag", "regeasemob");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		SyncHttpClient httpRequest = new SyncHttpClient();
		httpRequest.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String userName = response.getString("username");
					String appKey = response.getString("appKey");
					Loger.i("TEST", "registerUser==>" + userName);
					Loger.i("TEST", "registerAppKey==>" + appKey);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				Loger.i("TEST", "registerEase Failed==>");
				Loger.i("TEST", responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		return true;
	}
	public String getFriendId(){
		return  friendId;
	}
	public boolean regForumAccount(Account account, String userPwd, String inviteType){
		if (requestUrl == null) {
			return false;
		}
		HttpPost mPost = new HttpPost(requestUrl+IHttpRequestUtils.YOULIN);
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		String strGender = String.valueOf(account.getUser_gender());
		String strNick = account.getUser_name();
		String strPhonenum = account.getUser_phone_number();
		String strRecommend = String.valueOf(inviteType);
		pairs.add(new BasicNameValuePair("gender", strGender));
		pairs.add(new BasicNameValuePair("nick", strNick));
		pairs.add(new BasicNameValuePair("phonenum", strPhonenum));
		pairs.add(new BasicNameValuePair("password", userPwd));
		pairs.add(new BasicNameValuePair("recommend",strRecommend));
		pairs.add(new BasicNameValuePair("tag", "regist"));
		pairs.add(new BasicNameValuePair("apitype", IHttpRequestUtils.APITYPE[0]));
		
		StringBuilder builderWithInfo = new StringBuilder();
        builderWithInfo.append("gender");
        builderWithInfo.append(strGender);
        builderWithInfo.append("nick");
        builderWithInfo.append(strNick);
        builderWithInfo.append("phonenum");
        builderWithInfo.append(strPhonenum);
        builderWithInfo.append("password");
        builderWithInfo.append(userPwd);
        builderWithInfo.append("recommend");
        builderWithInfo.append(strRecommend);
        builderWithInfo.append("tag");
        builderWithInfo.append("regist");
        builderWithInfo.append("apitype");
        builderWithInfo.append("users");
        String saltCode = String.valueOf(System.currentTimeMillis());
        String hashCode = "";
        try {
			hashCode = MD5Util.getEncryptedPwd(builderWithInfo.toString(),saltCode);
		} catch (NoSuchAlgorithmException e1) {
			hashCode = "9573";
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			hashCode = "9573";
			e1.printStackTrace();
		}
        pairs.add(new BasicNameValuePair("salt",saltCode));
        pairs.add(new BasicNameValuePair("hash",hashCode));
        pairs.add(new BasicNameValuePair("keyset","gender:nick:phonenum:password:recommend:tag:apitype:"));
        
        if(App.sPhoneIMEI==null){
    		try {
				App.sPhoneIMEI = YLPushUtils.getImei(MainActivity.sMainActivity, App.sPhoneIMEI);
				pairs.add(new BasicNameValuePair("tokenvalue",App.sPhoneIMEI));
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}else{
    		pairs.add(new BasicNameValuePair("tokenvalue",App.sPhoneIMEI));
    	}
		try {
			mPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "UnsupportedEncodingException="+e.getMessage());
			e.printStackTrace();
			return false;
		}
		HttpResponse response = null;
		try {
			response = mHttpClient.execute(mPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "ClientProtocolException="+e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Loger.i("TEST", "IOException="+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		int res = response.getStatusLine().getStatusCode();
		Loger.i("TEST", "res=>"+res);
		if (res == 200) {
			HttpEntity entity = response.getEntity();
			String info = null;
			if (entity != null) {
				try {
					info = EntityUtils.toString(entity);
					Loger.i("TEST", "11111111111--->"+info.toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "ParseException="+e.getMessage());
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "IOException="+e.getMessage());
					e.printStackTrace();
					return false;
				}
				JSONObject jsonObject=null;
				String flag = "";
				String user_id = "";
				String friend_id="";
				try {
					Loger.i("NEW", "999999999999999--->"+info.toString());
					jsonObject = new JSONObject(info.toString());
					flag = jsonObject.getString("flag");
					user_id = jsonObject.getString("user_id");
					friend_id = jsonObject.getString("fr_id");
					String cache = jsonObject.getString("addr_cache");
					Editor editor=context.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE).edit();
					editor.putInt("address_tag", Integer.parseInt(cache));
					editor.commit();
					account.setUser_portrait(jsonObject.getString("user_avatr"));
					if("true".equals(flag)){
						App.sUserLoginId = Long.parseLong(user_id);
						Loger.i("TEST","注册论坛账号USERID:"+App.sUserLoginId);
						NeighborsHttpRequest.this.friendId=friend_id;
						return true;
					}else{
						Loger.i("TEST", "error flag is "+flag);
						Loger.i("TEST", "error user_id is "+user_id);
						return false;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST","JSONException:" + e.getMessage());
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}
	public boolean regThirdAccount(Account account, String userPwd, String inviteType,String userId,String userIcon,String thirdType){
		if (requestUrl == null) {
			return false;
		}
		HttpPost mPost = new HttpPost(requestUrl+IHttpRequestUtils.YOULIN);
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		String strGender = String.valueOf(account.getUser_gender());
		String strNick = account.getUser_name();
		String strPhonenum = account.getUser_phone_number();
		pairs.add(new BasicNameValuePair("third_account", userId));
		pairs.add(new BasicNameValuePair("phone_number", strPhonenum));
		pairs.add(new BasicNameValuePair("user_passwd", userPwd));
		pairs.add(new BasicNameValuePair("third_nick", strNick));
		pairs.add(new BasicNameValuePair("third_icon", userIcon));
		pairs.add(new BasicNameValuePair("third_gender", strGender));
		pairs.add(new BasicNameValuePair("third_note","111"));
		pairs.add(new BasicNameValuePair("third_type", thirdType));
		pairs.add(new BasicNameValuePair("tag", "bindthird"));
		pairs.add(new BasicNameValuePair("apitype", IHttpRequestUtils.APITYPE[0]));
        if(App.sPhoneIMEI==null){
    		try {
				App.sPhoneIMEI = YLPushUtils.getImei(MainActivity.sMainActivity, App.sPhoneIMEI);
				pairs.add(new BasicNameValuePair("phone_imei",App.sPhoneIMEI));
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}else{
    		pairs.add(new BasicNameValuePair("phone_imei",App.sPhoneIMEI));
    	}
        pairs.add(new BasicNameValuePair("access","9527"));
        Loger.i("NEW", "regThirdAccount-post-->"+userId+" "+strPhonenum+" "+userPwd+" "+strNick+" "+userIcon+" "+strGender+" "+thirdType+" "+App.sPhoneIMEI);
		try {
			mPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			Loger.i("NEW", "UnsupportedEncodingException="+e.getMessage());
			e.printStackTrace();
			return false;
		}
		HttpResponse response = null;
		try {
			mHttpClient.getParams().setParameter("http.socket.timeout", new Integer(30000));
			response = mHttpClient.execute(mPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Loger.i("NEW", "ClientProtocolException="+e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Loger.i("NEW", "IOException="+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		int res = response.getStatusLine().getStatusCode();
		Loger.i("NEW", "res=>"+res);
		if (res == 200) {
			HttpEntity entity = response.getEntity();
			String info = null;
			if (entity != null) {
				try {
					info = EntityUtils.toString(entity);
					Loger.i("NEW", "11111111111--->"+info.toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					Loger.i("NEW", "ParseException="+e.getMessage());
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Loger.i("NEW", "IOException="+e.getMessage());
					e.printStackTrace();
					return false;
				}
				JSONObject jsonObject=null;
				String flag = "";
				String user_id = "";
				String friend_id="";
				try {
					Loger.i("NEW", "regThirdAccount--->"+info.toString());
					jsonObject = new JSONObject(info.toString());
					flag = jsonObject.getString("flag");
					user_id = jsonObject.getString("user_id");
					friend_id = jsonObject.getString("fr_id");
					String cache = jsonObject.getString("addr_cache");
					Editor editor=context.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE).edit();
					editor.putInt("address_tag", Integer.parseInt(cache));
					editor.commit();
					account.setUser_portrait(jsonObject.getString("user_avatr"));
					if("true".equals(flag)){
						App.sUserLoginId = Long.parseLong(user_id);
						Loger.i("TEST","注册论坛账号USERID:"+App.sUserLoginId);
						NeighborsHttpRequest.this.friendId=friend_id;
						return true;
					}else{
						Loger.i("TEST", "error flag is "+flag);
						Loger.i("TEST", "error user_id is "+user_id);
						return false;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST","JSONException:" + e.getMessage());
					e.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}
	
	public void getAccountInfo(String phone,String password){
		RequestParams params = new RequestParams();
		SyncHttpClient client = new SyncHttpClient();
		
		params.put("phonenum", phone);
		params.put("password", password);
		params.put("tag", "login");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				org.json.JSONObject jsonContext = response;
				Loger.i("test4", "set currentlog json=JSONObject");
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test4", "set currentlog flag->" + flag);
					if(flag.equals("ok")){
						cRequestSet = true;
					}else{
						cRequestSet = false;
					}

				} catch (org.json.JSONException e) {
					e.printStackTrace();
					cRequestSet = false;
					Loger.i("test4","JSONObject->"+ e.getMessage());
				}
			
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
				Loger.i("test4", "set currentlog json=JSONArray-->"+response);
				org.json.JSONArray jsonContext = response;
				acountBundleList = new ArrayList<Bundle>();
				try {
					for (int i = 0; i < jsonContext.length(); i++) {
						JSONObject jsonObject = new JSONObject(response.getString(i));
						if(0==i){
							String user_id = jsonObject.getString("pk");
							String user_portrait = jsonObject.getJSONObject("fields").getString("user_portrait");
							String user_phone_number = jsonObject.getJSONObject("fields").getString("user_phone_number");
							String user_password = jsonObject.getJSONObject("fields").getString("user_password");
							String user_family_id = jsonObject.getJSONObject("fields").getString("user_family_id");
							String user_community_id = jsonObject.getJSONObject("fields").getString("user_community_id");
							if(user_community_id==null || user_community_id.isEmpty() || user_community_id == "null"){
								user_community_id = "0";
							}
							App.sFamilyCommunityId = Long.parseLong(user_community_id);
							String user_time = jsonObject.getJSONObject("fields").getString("user_time");
							if(user_time==null || user_time.isEmpty() || user_time == "null"){
								user_time = "0";
							}
							App.sUserAppTime = Long.parseLong(user_time);
							String user_family_address = jsonObject.getJSONObject("fields").getString("user_family_address");
							String user_nick = jsonObject.getJSONObject("fields").getString("user_nick");
							String user_gender = jsonObject.getJSONObject("fields").getString("user_gender");
							String user_email = jsonObject.getJSONObject("fields").getString("user_email");
							String user_birthday = jsonObject.getJSONObject("fields").getString("user_birthday");
							String user_public_status = jsonObject.getJSONObject("fields").getString("user_public_status");
							String user_profession = jsonObject.getJSONObject("fields").getString("user_profession");
							String user_signature = jsonObject.getJSONObject("fields").getString("user_signature");
							String user_type = jsonObject.getJSONObject("fields").getString("user_type");
							String user_json = jsonObject.getJSONObject("fields").getString("user_json");
							String user_news_receive = jsonObject.getJSONObject("fields").getString("user_news_receive");
							acountBundle = new Bundle();
							acountBundle.putString("user_id", user_id);
							acountBundle.putString("user_time", user_time);
							acountBundle.putString("user_portrait", user_portrait);
							acountBundle.putString("user_phone_number", user_phone_number);
							acountBundle.putString("user_community_id", user_community_id);
							acountBundle.putString("user_password", user_password);
							acountBundle.putString("user_family_id", user_family_id);
							acountBundle.putString("user_family_address", user_family_address);
							acountBundle.putString("user_nick", user_nick);
							acountBundle.putString("user_gender", user_gender);
							acountBundle.putString("user_email", user_email);
							acountBundle.putString("user_birthday", user_birthday);
							acountBundle.putString("user_public_status", user_public_status);
							acountBundle.putString("user_profession", user_profession);
							acountBundle.putString("user_signature", user_signature);
							acountBundle.putString("user_type", user_type);
							acountBundle.putString("user_json", user_json);
							acountBundle.putString("user_news_receive", user_news_receive);
							String cache=jsonObject.getJSONObject("fields").getString("addr_handle_cache");
							Editor editor=context.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE).edit();
							editor.putInt("address_tag", Integer.parseInt(cache));
							editor.commit();
							Loger.i("LYM", "1777777777777777777-->"+cache);
						}else{
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
							acountBundle = new Bundle();
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
						}
						acountBundleList.add(acountBundle);
					}
					
					cRequestSet = true;
					flag = "ok";
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Loger.i("test4", "----------------------------------------1111->"+e.getMessage());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Loger.i("test4", "----------------------------------------2222->"+e.getMessage());
				}
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					cRequestSet = false;
					new ErrorServer(context, responseString);
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});
		
	}
	
	public void updateUserInfo(RequestParams params, final Handler handler){
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
				params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				try {
					String flag = response.getString("flag");
					Loger.i("TEST","update flag->"+flag);
					if("ok".equals(flag)){
						
						String headUrl="null";
						try {
							 headUrl=response.getString("head_url");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Loger.i("TEST","success->"+headUrl);
//						Bundle bundle=new Bundle();
//						bundle.putString("head_url", headUrl);
						msg.obj = headUrl;
						msg.what = SUCCESS_CODE;
						handler.sendMessage(msg);
					}else if("no".equals(flag)){
						msg.what = FAILED_CODE;
						handler.sendMessage(msg);
					}else{
						Loger.i("TEST","else->"+flag);
						msg.what = FAILED_CODE;
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST","update->"+e.getMessage());
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = REFUSE_CODE;
				handler.sendMessage(msg);
				new ErrorServer(context, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	public void updatePassword(RequestParams params, final Handler handler){
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
				params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				try {
					String flag = response.getString("flag");
					Loger.i("TEST","update flag->"+flag);
					if("ok".equals(flag)){
						Loger.i("TEST","success->"+flag);
						msg.what = SUCCESS_CODE;
						handler.sendMessage(msg);
					}else if("no".equals(flag)){
						msg.what = FAILED_CODE;
						handler.sendMessage(msg);
					}else{
						Loger.i("TEST","else->"+flag);
						msg.what = FAILED_CODE;
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST","update->"+e.getMessage());
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = REFUSE_CODE;
				handler.sendMessage(msg);
				new ErrorServer(context, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	public void getUserInfo(RequestParams params, final Handler handler){
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
				params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode,
					Header[] headers, JSONArray response) {
				// TODO Auto-generated method stub
				int responseLen = response.length();
				Loger.i("TEST", "json obj length->"+responseLen);
				try {
					acountBundleList = new ArrayList<Bundle>();
					for (int i = 0; i < responseLen; i++) {
						JSONObject jsonObject = new JSONObject(response.getString(i));
						if(0==i){
							String user_id = jsonObject.getString("pk");
							String user_portrait = jsonObject.getJSONObject("fields").getString("user_portrait");
							String user_phone_number = jsonObject.getJSONObject("fields").getString("user_phone_number");
							String user_password = jsonObject.getJSONObject("fields").getString("user_password");
							String user_community_id = jsonObject.getJSONObject("fields").getString("user_community_id");
							if(user_community_id==null || user_community_id.isEmpty() || user_community_id == "null"){
								user_community_id = "0";
							}
							App.sFamilyCommunityId = Long.parseLong(user_community_id);
							String user_time = jsonObject.getJSONObject("fields").getString("user_time");
							if(user_time==null || user_time.isEmpty() || user_time == "null"){
								user_time = "0";
							}
							App.sUserAppTime = Long.parseLong(user_time);
							String user_family_id = jsonObject.getJSONObject("fields").getString("user_family_id");
							String user_family_address = jsonObject.getJSONObject("fields").getString("user_family_address");
							String user_nick = jsonObject.getJSONObject("fields").getString("user_nick");
							String user_gender = jsonObject.getJSONObject("fields").getString("user_gender");
							String user_email = jsonObject.getJSONObject("fields").getString("user_email");
							String user_birthday = jsonObject.getJSONObject("fields").getString("user_birthday");
							String user_public_status = jsonObject.getJSONObject("fields").getString("user_public_status");
							String user_profession = jsonObject.getJSONObject("fields").getString("user_profession");
							String user_level = jsonObject.getJSONObject("fields").getString("user_level");
							String user_signature = jsonObject.getJSONObject("fields").getString("user_signature");
							String user_type = jsonObject.getJSONObject("fields").getString("user_type");
							String user_json = jsonObject.getJSONObject("fields").getString("user_json");
							acountBundle = new Bundle();
							acountBundle.putString("user_id", user_id);
							App.sUserLoginId = Long.parseLong(user_id);
							acountBundle.putString("user_portrait", user_portrait);
							acountBundle.putString("user_time", user_time);
							acountBundle.putString("user_phone_number", user_phone_number);
							acountBundle.putString("user_community_id", user_community_id);
							acountBundle.putString("user_password", user_password);
							acountBundle.putString("user_family_id", user_family_id);
							acountBundle.putString("user_family_address", user_family_address);
							acountBundle.putString("user_nick", user_nick);
							acountBundle.putString("user_gender", user_gender);
							acountBundle.putString("user_email", user_email);
							acountBundle.putString("user_birthday", user_birthday);
							acountBundle.putString("user_public_status", user_public_status);
							acountBundle.putString("user_profession", user_profession);
							acountBundle.putString("user_level", user_level);
							acountBundle.putString("user_signature", user_signature);
							acountBundle.putString("user_type", user_type);
							acountBundle.putString("user_json", user_json);
						}else{
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
							acountBundle = new Bundle();
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
						}
						acountBundleList.add(acountBundle);
					}
					//生成pushRecord
//					if(App.sUserLoginId>0){
//						getResponsePushRecord(App.sUserLoginId);
//					}
					Loger.i("TEST", "获取个人信息成功");
					Message msg = new Message();
					msg.what = SUCCESS_GET_INFO;
					handler.sendMessage(msg);
					return;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onSuccess(int statusCode,
					Header[] headers, org.json.JSONObject response) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				try {
					String flag = response.getString("flag");
					if("no".equals(flag)){
						Loger.i("TEST", "query http failed!!!");
						msg.what = FAILED_GET_INFO;
						handler.sendMessage(msg);
					}else if ("none_o".equals(flag)){
						Loger.i("TEST", "userinfo not exits!!!");
						msg.what = FAILED_GET_INFO;
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode,
					Header[] headers, String responseString,
					Throwable throwable) {
				// TODO Auto-generated method stu
				Message msg = new Message();
				Loger.i("TEST", "FAILED=>"+responseString);
				msg.what = REFUSE_GET_INFO;
				handler.sendMessage(msg);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
}
