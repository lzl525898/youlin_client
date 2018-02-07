package com.nfs.youlin.push;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class YLPushTagManager {
	private static final String TAG = "TEST";
	private Context context;
	private String sSetPushTagflag = "no";
	private String sUserJson;
	
	public YLPushTagManager(Context context){
		this.context = context;
	}
	
	public void setNewsRecviceStatusWithService(final Context context){
		int nNewsReceiveStatus = 0;
		final boolean bNewReceiveStatus = App.getNewsRecviceStatus(context);
		if(bNewReceiveStatus){//当前为可接受
			nNewsReceiveStatus = 2; //设置为不可接受
		}else{				  //当前不可接受
			nNewsReceiveStatus = 1; //设置为可以接受
		}
		RequestParams params = new RequestParams();
		params.put("user_id", App.sUserLoginId);
		params.put("user_phone_number", App.sUserPhone);
		params.put("receive_status", nNewsReceiveStatus);
		params.put("tag", "upload");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				String flag = null;
				try {
					flag = response.getString("flag");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if("ok".equals(flag)){
					if(bNewReceiveStatus){
						setNewsReceiveStatus(context, false);
					}else{
						setNewsReceiveStatus(context, true);
					}
				}else{
					Toast.makeText(context, "设置失败！", Toast.LENGTH_LONG).show();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
					Throwable throwable) {	
				Toast.makeText(context, "设置失败", Toast.LENGTH_LONG).show();
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	private void setNewsReceiveStatus(Context context, boolean bStatus){
		JSONArray jsonArray = null;
		AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
		Account account = dbImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
		dbImpl.releaseDatabaseRes();
		String userJson = account.getUser_json();
		try {
			jsonArray = new JSONArray(userJson);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i(TAG, "setNewsReceiveStatus=>Err0=>"+e.getMessage());
		}
		int dbUserType = 0;
		String dbCommunityId = null;
		try {
			dbUserType = jsonArray.getJSONObject(0).getInt("userType");
			dbCommunityId = jsonArray.getJSONObject(0).getString("communityId");
			AccountDaoDBImpl accountImpl = new AccountDaoDBImpl(context);
			if(dbCommunityId.equals(String.valueOf(App.sFamilyCommunityId))){
				account.setUser_type(dbUserType);
				App.sUserType = dbUserType;
				accountImpl.modifyObject(account);
			}else{
				account.setUser_type(0);
				App.sUserType = 0;
				accountImpl.modifyObject(account);
			}
			accountImpl.releaseDatabaseRes();
		} catch (Exception e) {
			Loger.i(TAG, "setNewsReceiveStatus=>Err1=>"+e.getMessage());
		}
		Set<String> setTags = new HashSet<String>(); 
		String strAlias = App.PUSH_ALIAS_SUFFIX + String.valueOf(App.sUserLoginId);
		String commTag  = App.PUSH_ALIAS_SUFFIX + String.valueOf(App.sUserLoginId);
		if(bStatus){//接受新闻推送
			String newsTag = App.PUSH_NEWS_NOTICE + String.valueOf(App.sFamilyCommunityId);
			if(0==App.sUserType || 1==App.sUserType){
				setTags.add(commTag);
			}else if(2==App.sUserType || 3==App.sUserType){
				String adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + String.valueOf(App.sFamilyCommunityId);
				setTags.add(newsTag);
				setTags.add(commTag);
				setTags.add(adminTag);
			}else if(4==App.sUserType || 5==App.sUserType){
				String proptyTag = App.PUSH_TAG_PROPERTY_ADMIN + String.valueOf(App.sFamilyCommunityId);
				setTags.add(newsTag);
				setTags.add(commTag);
				setTags.add(proptyTag);
			}else{
				String adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + String.valueOf(App.sFamilyCommunityId);
				String proptyTag = App.PUSH_TAG_PROPERTY_ADMIN + String.valueOf(App.sFamilyCommunityId);
				setTags.add(newsTag);
				setTags.add(commTag);
				setTags.add(adminTag);
				setTags.add(proptyTag);
			}
		}else{//屏蔽新闻推送
			if(0==App.sUserType || 1==App.sUserType){
				setTags.add(commTag);
			}else if(2==App.sUserType || 3==App.sUserType){
				String adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + String.valueOf(App.sFamilyCommunityId);
				setTags.add(commTag);
				setTags.add(adminTag);
			}else if(4==App.sUserType || 5==App.sUserType){
				String proptyTag = App.PUSH_TAG_PROPERTY_ADMIN + String.valueOf(App.sFamilyCommunityId);
				setTags.add(commTag);
				setTags.add(proptyTag);
			}else{
				String adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + String.valueOf(App.sFamilyCommunityId);
				String proptyTag = App.PUSH_TAG_PROPERTY_ADMIN + String.valueOf(App.sFamilyCommunityId);
				setTags.add(commTag);
				setTags.add(adminTag);
				setTags.add(proptyTag);
			}
		}
		JPushInterface.setAliasAndTags(context, strAlias, setTags, mTagsCallback);
		setTags.clear();
		setTags = null;
		App.setNewsRecviceStatus(context, bStatus);
	}
	
	public void setPushTag(){
		sUserJson = getUserJson();
		if(sUserJson!=null){
			try {
				AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(this.context);
				JSONArray jsonArray = new JSONArray(sUserJson);
				String curCommunityId = jsonArray.getJSONObject(0).getString("communityId");
				int curUserType = jsonArray.getJSONObject(0).getInt("userType");
				Account account = daoDBImpl.findAccountByPhone(App.sUserPhone);
				if(curCommunityId.equals(String.valueOf(App.sFamilyCommunityId))){
					account.setUser_type(curUserType);
					App.sUserType = curUserType;
					daoDBImpl.modifyObject(account);
				}else{
					account.setUser_type(0);
					App.sUserType = 0;
					daoDBImpl.modifyObject(account);
				}
				jsonArray = null;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					// TODO Auto-generated method stub
					Long currenttime = System.currentTimeMillis();
					while(!App.sDeleteTagFinish){
						if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
							App.sDeleteTagFinish = true;
						}
						if(App.sDeleteTagFinish==true){
							sSetPushTagflag = "ok";
							break;
						}
					}
					if(App.sDeleteTagFinish==true){
						sSetPushTagflag = "ok";
					}
					return null;
				}
				protected void onPostExecute(Void result) {
					if(App.sDeleteTagFinish == true && sSetPushTagflag.equals("ok")){
						if(sUserJson==null){
							sUserJson = getUserJson();
						}
						String nUserCommunityId = null;
						JSONArray jsonArray = null;
						Set<String> setTags = null; 
						String adminTag = null;
						getUserType();
						if(sUserJson!=null){
							try {
								jsonArray = new JSONArray(sUserJson);
								nUserCommunityId = jsonArray.getJSONObject(0).getString("communityId");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}finally{
								jsonArray = null;
							}
						}
						String strAlias = App.PUSH_ALIAS_SUFFIX + String.valueOf(App.sUserLoginId);
						String newTags = App.PUSH_NEWS_NOTICE + String.valueOf(App.sFamilyCommunityId);
						switch(App.sUserType){
						case 0:
						case 1:
							setTags = new HashSet<String>();
							adminTag = App.PUSH_TAG_COMMUNITY_TOPIC + String.valueOf(String.valueOf(App.sFamilyCommunityId));
							setTags.add(adminTag);
							setTags.add(newTags);
							Loger.i("TEST","当前所在组==>"+adminTag);
							JPushInterface.setAliasAndTags(context, strAlias, setTags, mTagsCallback);
							setTags.clear();
							setTags = null;
							break;
						case 2:
						case 3:
							if(nUserCommunityId!=null && nUserCommunityId.equals(String.valueOf(App.sFamilyCommunityId))){
								setTags = new HashSet<String>();
								adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + String.valueOf(App.sFamilyCommunityId);
								setTags.add(adminTag);
								setTags.add(newTags);
								Loger.i("TEST","当前所在组==>"+adminTag);
								JPushInterface.setAliasAndTags(context, strAlias, setTags, mTagsCallback);
								setTags.clear();
								setTags = null;
							}
							break;
						case 4:
						case 5:
							if(nUserCommunityId!=null && nUserCommunityId.equals(String.valueOf(App.sFamilyCommunityId))){
								setTags = new HashSet<String>();
								adminTag = App.PUSH_TAG_PROPERTY_ADMIN + String.valueOf(App.sFamilyCommunityId);
								setTags.add(adminTag);
								setTags.add(newTags);
								Loger.i("TEST","当前所在组==>"+adminTag);
								JPushInterface.setAliasAndTags(context, strAlias, setTags, mTagsCallback);
								setTags.clear();
								setTags = null;
							}
							break;
						case 6:
							if(nUserCommunityId!=null && nUserCommunityId.equals(String.valueOf(App.sFamilyCommunityId))){
								setTags = new HashSet<String>();
								adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + String.valueOf(App.sFamilyCommunityId);
								setTags.add(adminTag);
								setTags.add(newTags);
								Loger.i("TEST","当前所在组==>"+adminTag);
								adminTag = App.PUSH_TAG_PROPERTY_ADMIN + String.valueOf(App.sFamilyCommunityId);
								setTags.add(adminTag);
								Loger.i("TEST","当前所在组==>"+adminTag);
								JPushInterface.setAliasAndTags(context, strAlias, setTags, mTagsCallback);
								setTags.clear();
								setTags = null;
							}
							break;
						default:
							break;
						}
						sSetPushTagflag = "no";
						App.sDeleteTagFinish = false;
					}
				}
			}.execute();
		}
	}

	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {
		@Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
            case 0:
                logs = "TagsCallback==》设置tag和alias成功";
                App.sDeleteTagFinish = true;
                Loger.i(TAG, logs);
                break;
            case 6002:
                logs = "TagsCallback==>Failed to set alias and tags due to timeout. Try again after 5s.";
                Loger.i(TAG, logs);
                if (YLPushUtils.isConnected(context)) {
                	mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 5);
                } else {
                	Loger.i(TAG, "No network");
                }
                break;
            default:
                logs = "Failed with errorCode = " + code;
                Loger.e(TAG, logs);
            }
        }
	};
	
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
            case 0:
                logs = "AliasCallback==》设置tag和alias成功";
                Loger.i(TAG, logs);
                break;
            case 6002:
                logs = "AliasCallback==>Failed to set alias and tags due to timeout. Try again after 5s.";
                Loger.i(TAG, logs);
                if (YLPushUtils.isConnected(context)) {
                	mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 5);
                } else {
                	Loger.i(TAG, "No network");
                }
                break;
            
            default:
                logs = "Failed with errorCode = " + code;
                Loger.e(TAG, logs);
            }
        }
	};
	
	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_SET_ALIAS:
                Loger.d(TAG, "Set alias in handler.");
                JPushInterface.setAliasAndTags(context, (String) msg.obj, null, mAliasCallback);
                break;
            case MSG_SET_TAGS:
                Loger.d(TAG, "Set tags in handler.");
                JPushInterface.setAliasAndTags(context, null, (Set<String>) msg.obj, mTagsCallback);
                break;
            default:
                Loger.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
	
	private void getUserType(){
		if(App.sUserType<0){
			AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(this.context);
			Account account = daoDBImpl.findAccountByPhone(App.sUserPhone);
			App.sUserType = account.getUser_type();
		}
	}
	
	private String getUserJson(){
		AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(this.context);
		Account account = daoDBImpl.findAccountByPhone(App.sUserPhone);
		return account.getUser_json();
	}
}
