package com.nfs.youlin.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.PushRecord;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
//import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class YLPushBroadcastReceiver extends BroadcastReceiver {

	private String sSetPushTagflag;
	private Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Bundle bundle = intent.getBundleExtra("push_info");
		JSONObject jsonObject = null;
		String customContentString = bundle.getString("jsonObject");
		JSONObject objectContent = null;
		App.sUserLoginId = getUserLoginId();
		try {
			if(customContentString!=null){
				objectContent = new JSONObject(customContentString);
			}else{
				return;
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
		String pushTitle = null;
		try {
			pushTitle = objectContent.getString("title");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(App.PUSH_COMM_REPORT_ADMIN.equals(pushTitle)){
			String msgContent = bundle.getString("jsonObject");
			try {
				Loger.i("TEST","有管理员相关推送");
				jsonObject = new JSONObject(msgContent);
				String contentType = jsonObject.getString("contentType");
				String pushTime = jsonObject.getString("pushTime");
				String recordId = jsonObject.getString("recordId");
				Loger.i("TEST", "有举报==>"+recordId);
				PushRecord pushRecord = new PushRecord(context);
				pushRecord.setUser_id(App.sUserLoginId);
				pushRecord.setRecord_id(Long.parseLong(recordId));
				pushRecord.setContent(msgContent);
				pushRecord.setContent_type(Integer.parseInt(contentType));
				pushRecord.setLogin_account(1001);//表示admin
				pushRecord.setPush_time(Long.parseLong(pushTime));
				pushRecord.setCommunity_id(App.sFamilyCommunityId);
				pushRecord.setType(1);//是否已经阅读的状态   1=>未阅读  2=>已阅读
				pushRecord.setCommunity_id(App.sFamilyCommunityId);
	   			PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
				daoDBImpl.saveObject(pushRecord);
				//设置通知相关信息
				setNoticeStatusWithReport();
			} catch (JSONException e) {
				Loger.i("TEST", "YLPushBroadcastReceiver-ERROR:"+e.getMessage());
				e.printStackTrace();
			}
			return;
		}else if(App.PUSH_PROPERTY_NEW_NOTICE.equals(pushTitle)){
			String msgContent = bundle.getString("jsonObject");
			try {
				Loger.i("TEST","有物业相关推送");
				jsonObject = new JSONObject(msgContent);
//				String userId = jsonObject.getString("userId");
				String contentType = jsonObject.getString("contentType");
				String pushTime = jsonObject.getString("pushTime");
				String recordId = jsonObject.getString("recordId");
				PushRecord pushRecord = new PushRecord(context);
				pushRecord.setUser_id(App.sUserLoginId);
				pushRecord.setRecord_id(Long.parseLong(recordId));
				pushRecord.setContent(msgContent);
				pushRecord.setContent_type(Integer.parseInt(contentType));
				pushRecord.setLogin_account(1002);//表示物业 1002
				pushRecord.setPush_time(Long.parseLong(pushTime));
				pushRecord.setType(1);//是否已经阅读的状态   1=>未阅读  2=>已阅读
				pushRecord.setCommunity_id(App.sFamilyCommunityId);
	   			PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
				daoDBImpl.saveObject(pushRecord);
				//设置通知相关信息
				setNoticeStatus();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}else if(App.PUSH_PROPERTY_NEW_REPAIR.equals(pushTitle)){
			String msgContent = bundle.getString("jsonObject");
			try {
				Loger.i("TEST","有物业报修推送");
				jsonObject = new JSONObject(msgContent);
				String contentType = jsonObject.getString("contentType");
				String pushTime = jsonObject.getString("pushTime");
				String recordId = jsonObject.getString("recordId");
				PushRecord pushRecord = new PushRecord(context);
				pushRecord.setUser_id(App.sUserLoginId);
				pushRecord.setRecord_id(Long.parseLong(recordId));
				pushRecord.setContent(msgContent);
				pushRecord.setContent_type(Integer.parseInt(contentType));
				pushRecord.setLogin_account(1003);//表示物业包修1003
				pushRecord.setPush_time(Long.parseLong(pushTime));
				pushRecord.setType(1);//是否已经阅读的状态   1=>未阅读  2=>已阅读
				pushRecord.setCommunity_id(App.sFamilyCommunityId);
	   			PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
				daoDBImpl.saveObject(pushRecord);
				//设置通知相关信息
				setNoticeStatus();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}else if(App.PUSH_PROPERTY_RUN_REPAIR.equals(pushTitle)){
			String msgContent = bundle.getString("jsonObject");
			try {
				Loger.i("TEST","有物业报修更新推送");
				jsonObject = new JSONObject(msgContent);
//				String userId = jsonObject.getString("userId");
				String contentType = jsonObject.getString("contentType");
				String pushTime = jsonObject.getString("pushTime");
				String recordId = jsonObject.getString("recordId");
				PushRecord pushRecord = new PushRecord(context);
				pushRecord.setUser_id(App.sUserLoginId);
				pushRecord.setRecord_id(Long.parseLong(recordId));
				pushRecord.setContent(msgContent);
				pushRecord.setContent_type(Integer.parseInt(contentType));
				pushRecord.setLogin_account(1004);//表示物业包修1004
				pushRecord.setPush_time(Long.parseLong(pushTime));
				pushRecord.setType(1);//是否已经阅读的状态   1=>未阅读  2=>已阅读
				pushRecord.setCommunity_id(App.sFamilyCommunityId);
	   			PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
				daoDBImpl.saveObject(pushRecord);
				//设置通知相关信息
				setNoticeStatus();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return;
		}else{
			try {
				String msgContent = bundle.getString("jsonObject");
				Loger.i("TEST", "ELSE-》msgContent==>"+msgContent);
				jsonObject = new JSONObject(msgContent);
				String pushType = null;
				String contType = null;
				int topicType = 0;
				try {
					pushType = jsonObject.getString("pushType");
					contType = jsonObject.getString("contentType");
					Loger.i("TEST", "pushType=>"+pushType+"  contType=>"+contType);
				} catch (Exception e) {
					Loger.i("TEST", "崩溃原因==>"+e.getMessage());
					pushType = null;
				}
				try {
					topicType = jsonObject.getInt("topicType");
				} catch (Exception e) {
					Loger.i("TEST", "updateContent-topicType=>"+e.getMessage());
				}
				if(1==Integer.parseInt(pushType) && 5==Integer.parseInt(contType)){//有新闻分享
					Loger.i("TEST", "新闻分享=>"+msgContent);
					String userId = jsonObject.getString("new_sender");
					String pushTime = jsonObject.getString("pushTime");
					String recordId = jsonObject.getString("recordId");
					PushRecord pushRecord = new PushRecord(context);
					pushRecord.setUser_id(App.sUserLoginId);
					pushRecord.setRecord_id(Long.parseLong(recordId));
					pushRecord.setContent(msgContent);
					pushRecord.setContent_type(Integer.parseInt(contType));
					pushRecord.setLogin_account(Long.parseLong(userId));
					pushRecord.setPush_time(Long.parseLong(pushTime));
					pushRecord.setType(1);//是否已经阅读的状态   1=>未阅读  2=>已阅读
					pushRecord.setCommunity_id(App.sFamilyCommunityId);
		   			PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
					daoDBImpl.saveObject(pushRecord);
					//设置通知相关信息
					setNoticeStatus();
					return;
				}
				if(null==pushType || ((Integer.parseInt(pushType)<4))){//个人、地址
					String contentType = jsonObject.getString("contentType");
					String userId = jsonObject.getString("userId");
					String pushTime = jsonObject.getString("pushTime");
					String recordId = jsonObject.getString("recordId");
					PushRecord pushRecord = new PushRecord(context);
					pushRecord.setUser_id(App.sUserLoginId);
					pushRecord.setRecord_id(Long.parseLong(recordId));
					pushRecord.setContent(msgContent);
					pushRecord.setContent_type(Integer.parseInt(contentType));
					pushRecord.setLogin_account(Long.parseLong(userId));
					pushRecord.setPush_time(Long.parseLong(pushTime));
					pushRecord.setType(1);//是否已经阅读的状态   1=>未阅读  2=>已阅读
					pushRecord.setCommunity_id(App.sFamilyCommunityId);
		   			PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
					daoDBImpl.saveObject(pushRecord);
					//设置通知相关信息
					setNoticeStatus();
					//设置PushTag
					setPushTag();
					return;
				}
				if(5==Integer.parseInt(pushType)){//新闻
					String contentType = jsonObject.getString("contentType");
					String newsType = jsonObject.getString("newsType");
					String pushTime = jsonObject.getString("pushTime");
					String recordId = jsonObject.getString("recordId");
//					Loger.i("TEST", "contentType==>"+contentType);
//					Loger.i("TEST", "newsType==>"+newsType);
//					Loger.i("TEST", "pushTime==>"+pushTime);
//					Loger.i("TEST", "recordId==>"+recordId);
					PushRecord pushRecord = new PushRecord(context);
					pushRecord.setUser_id(App.sUserLoginId);
					pushRecord.setRecord_id(Long.parseLong(recordId));
					pushRecord.setContent(msgContent);
					pushRecord.setContent_type(Integer.parseInt(contentType));
					pushRecord.setLogin_account(Long.parseLong(newsType));
					pushRecord.setPush_time(Long.parseLong(pushTime));
					pushRecord.setType(1);//是否已经阅读的状态   1=>未阅读  2=>已阅读
					pushRecord.setCommunity_id(App.sFamilyCommunityId);
					PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
					daoDBImpl.saveObject(pushRecord);
					//设置通知相关信息
					setNoticeStatusWithNews();
					return;
				}
				if(6==Integer.parseInt(pushType)){//回复
					String contentType = jsonObject.getString("contentType");
					String commenyType = jsonObject.getString("commentType");
					String pushTime = jsonObject.getString("pushTime");
					String recordId = jsonObject.getString("recordId");
					PushRecord pushRecord = new PushRecord(context);
					pushRecord.setUser_id(App.sUserLoginId);
					pushRecord.setRecord_id(Long.parseLong(recordId));
					pushRecord.setContent(msgContent);
					pushRecord.setContent_type(Integer.parseInt(contentType));
					pushRecord.setLogin_account(Long.parseLong(commenyType));
					pushRecord.setPush_time(Long.parseLong(pushTime));
					pushRecord.setType(1);//是否已经阅读的状态   1=>未阅读  2=>已阅读
					pushRecord.setCommunity_id(App.sFamilyCommunityId);
					PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
					daoDBImpl.saveObject(pushRecord);
					//设置通知相关信息
					if(11==topicType){//打招呼
						Loger.i("DDDDDD", "打招呼详细信息==>"+msgContent);
						setNoticeStatus();
						return;
					}else{//回复
						Loger.i("DDDDDD", "回复详细信息==>"+msgContent);
						setNoticeStatusWithComment();
						return;
					}
				}
				if(7==Integer.parseInt(pushType)){//评价
					String contentType = jsonObject.getString("contentType");
					String pushTime = jsonObject.getString("pushTime");
					String recordId = jsonObject.getString("recordId");
					String uerId = jsonObject.getString("uerId");
					PushRecord pushRecord = new PushRecord(context);
					pushRecord.setUser_id(App.sUserLoginId);
					pushRecord.setRecord_id(Long.parseLong(recordId));
					pushRecord.setContent(msgContent);
					pushRecord.setContent_type(Integer.parseInt(contentType));
					pushRecord.setLogin_account(Long.parseLong(uerId));
					pushRecord.setPush_time(Long.parseLong(pushTime));
					pushRecord.setType(1);//是否已经阅读的状态   1=>未阅读  2=>已阅读
					pushRecord.setCommunity_id(App.sFamilyCommunityId);
					PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
					daoDBImpl.saveObject(pushRecord);
					//设置通知相关信息
					setNoticeStatus();
				}
				if(8==Integer.parseInt(pushType)){//天气预报
					String contentType = jsonObject.getString("contentType");
					String pushTime = jsonObject.getString("pushTime");
					String recordId = jsonObject.getString("recordId");
					String weaId = jsonObject.getString("weaId");
					PushRecord pushRecord = new PushRecord(context);
					pushRecord.setUser_id(App.sUserLoginId);
					pushRecord.setRecord_id(Long.parseLong(recordId));
					pushRecord.setContent(msgContent);
					pushRecord.setContent_type(Integer.parseInt(contentType));
					pushRecord.setLogin_account(Long.parseLong(weaId));
					pushRecord.setPush_time(Long.parseLong(pushTime));
					pushRecord.setType(1);//是否已经阅读的状态   1=>未阅读  2=>已阅读
					pushRecord.setCommunity_id(App.sFamilyCommunityId);
					PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
					daoDBImpl.saveObject(pushRecord);
					//设置通知相关信息
					setNoticeStatus();
				}
			} catch (JSONException e) {
				Loger.i("TEST", "YLPushBroadcastReceiver-ERROR:"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private void setPushTag(){
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
					}
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				String newTags = App.PUSH_NEWS_NOTICE + String.valueOf(App.sFamilyCommunityId);
				if(App.sDeleteTagFinish == true && "ok".equals(sSetPushTagflag)){
					Set<String> tagList = null;
					String adminTag = null;
					switch(App.sUserType){
					case 0:
					case 1:
						tagList = new HashSet<String>();
						adminTag = App.PUSH_TAG_COMMUNITY_TOPIC + String.valueOf(App.sFamilyCommunityId);
						tagList.add(adminTag);
						tagList.add(newTags);
						Loger.i("TEST","当前所在组==>"+adminTag);
						JPushInterface.setAliasAndTags(context.getApplicationContext(), null, tagList, mTagsCallback);
						tagList.clear();
						tagList = null;
						break;
					case 2:
					case 3:
						tagList = new HashSet<String>();
						adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + String.valueOf(App.sFamilyCommunityId);
						tagList.add(adminTag);
						tagList.add(newTags);
						Loger.i("TEST","当前所在组==>"+adminTag);
						JPushInterface.setAliasAndTags(context.getApplicationContext(), null, tagList, mTagsCallback);
						tagList.clear();
						break;
					case 4:
					case 5:
						tagList = new HashSet<String>();
						adminTag = App.PUSH_TAG_PROPERTY_ADMIN + String.valueOf(App.sFamilyCommunityId);
						tagList.add(adminTag);
						tagList.add(newTags);
						Loger.i("TEST","当前所在组==>"+adminTag);
						JPushInterface.setAliasAndTags(context.getApplicationContext(), null, tagList, mTagsCallback);
						tagList.clear();
						break;
					case 6:
						tagList = new HashSet<String>();
						adminTag = App.PUSH_TAG_COMM_REPORT_ADMIN + String.valueOf(App.sFamilyCommunityId);
						tagList.add(adminTag);
						tagList.add(newTags);
						Loger.i("TEST","当前所在组==>"+adminTag);
						adminTag = App.PUSH_TAG_PROPERTY_ADMIN + String.valueOf(App.sFamilyCommunityId);
						tagList.add(adminTag);
						Loger.i("TEST","当前所在组==>"+adminTag);
						JPushInterface.setAliasAndTags(context.getApplicationContext(), null, tagList, mTagsCallback);
						tagList.clear();
						break;
					default:
						break;
					}
					App.sDeleteTagFinish = false;
					sSetPushTagflag = "no";
				}
			};
		}.execute();
	}
	
	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {
		@Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
            case 0:
                logs = "Set tag and alias success";
                App.sDeleteTagFinish = true;
                Loger.i("TEST", logs);
                break;
            case 6002:
                logs = "TagAliasCallback==>Failed to set alias and tags due to timeout. Try again after 5s.";
                Loger.i("TEST", logs);
                if (YLPushUtils.isConnected(context.getApplicationContext())) {
                	mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 5);
                } else {
                	Loger.i("TEST", "No network");
                }
                break;
            default:
                logs = "Failed with errorCode = " + code;
            }
            Loger.i("TEST", "成功绑定==>"+logs);
//            YLPushUtils.showToast(logs, context.getApplicationContext());
        }
	};
	
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
		@Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
            case 0:
                logs = "Set tag and alias success";
                App.sDeleteTagFinish = true;
                Loger.i("TEST", logs);
                break;
            case 6002:
                logs = "TagAliasCallback===>Failed to set alias and tags due to timeout. Try again after 5s.";
                Loger.i("TEST", logs);
                if (YLPushUtils.isConnected(context.getApplicationContext())) {
                	mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 5);
                } else {
                	Loger.i("TEST", "No network");
                }
                break;
            default:
                logs = "Failed with errorCode = " + code;
            }
        }
	};
	
	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;
	private final Handler mHandler = new Handler() {
        @SuppressWarnings("unchecked")
		@Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case MSG_SET_ALIAS:
                Loger.d("TEST", "Set alias in handler.");
                JPushInterface.setAliasAndTags(context.getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                break;
            case MSG_SET_TAGS:
                Loger.d("TEST", "Set tags in handler.");
                JPushInterface.setAliasAndTags(context.getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
                break;
            default:
                Loger.i("TEST", "Unhandled msg - " + msg.what);
            }
        }
    };
	
	private void setNoticeStatus(){
		if(App.sNewPushRecordCount==null||App.sNewPushRecordCount.isEmpty()||App.sNewPushRecordCount=="null"||App.sNewPushRecordCount.length()<=0){
			App.sNewPushRecordCount = "0";
		}
		int newPushRecordCount = Integer.parseInt(App.sNewPushRecordCount);
		newPushRecordCount++;
		App.sNewPushRecordCount = String.valueOf(newPushRecordCount);
		saveNewPushRecordCount(App.sNewPushRecordCount);
		MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
	}
	
	@SuppressLint("UseSparseArrays")
	private void setNoticeStatusWithComment(){
		if(App.sUserLoginId<=0){
			AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
			App.sUserLoginId = dbImpl.getUserId();
		}
		Map<Long,Integer> map = new HashMap<Long,Integer>();
		JSONObject jsonObject = null;
		List<Long> reportTopicId = new ArrayList<Long>();
		PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
		List<Object> listObj = daoDBImpl.findCommentObjs("1010",String.valueOf(App.sUserLoginId));
		int size = listObj.size();
		for(int i=0;i<size;i++){
			String jsonContent = ((PushRecord)listObj.get(i)).getContent();
			try {
				jsonObject = new JSONObject(jsonContent);
				long topicId = jsonObject.getLong("topicId");
				if(-1 != reportTopicId.indexOf(topicId)){
					if(((PushRecord)listObj.get(i)).getType()==1){
						int index = map.get(topicId) + 1;
						map.put(topicId, index);//未阅读
					}
				}else{
					reportTopicId.add(topicId);
					if(((PushRecord)listObj.get(i)).getType()==1){
						map.put(topicId, 1);//未阅读
					}
				}
			} catch (JSONException e) {
				Loger.i("DDDDDD", "setNoticeStatusWithReport=Err"+e.getMessage());
			}
		}
		if(App.sNewPushRecordCount==null||App.sNewPushRecordCount.isEmpty()||App.sNewPushRecordCount=="null"||App.sNewPushRecordCount.length()<=0){
			App.sNewPushRecordCount = "0";
		}
		
		for(Integer value : map.values()){
			if(value<2){//全部举报已读时，增加未读取总数
				Loger.i("DDDDDD", "value===>"+value);
				int newPushRecordCount = Integer.parseInt(App.sNewPushRecordCount);
				newPushRecordCount++;
				App.sNewPushRecordCount = String.valueOf(newPushRecordCount);
				saveNewPushRecordCount(App.sNewPushRecordCount);
				MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
			}
		}
		listObj.clear();
		map.clear();
		Loger.i("DDDDDD", "当前未读取总数量为:"+App.sNewPushRecordCount);
	}
	
	private void setNoticeStatusWithNews(){
		if(App.sUserLoginId<=0){
			AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
			App.sUserLoginId = dbImpl.getUserId();
		}
		int readStatusIndex = 0;
		PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
		List<Object> listObj = daoDBImpl.findAppointObjs("2001",String.valueOf(App.sUserLoginId));
		int size = listObj.size();
		for(int i=0;i<size;i++){
			if(((PushRecord)listObj.get(i)).getType()==1){
				readStatusIndex++;//未阅读
			}
		}
		if(App.sNewPushRecordCount==null||App.sNewPushRecordCount.isEmpty()||App.sNewPushRecordCount=="null"||App.sNewPushRecordCount.length()<=0){
			App.sNewPushRecordCount = "0";
		}
		
		Loger.i("TEST", "readStatusIndex===>"+readStatusIndex);
		if(readStatusIndex<2){//全部新闻已读时，增加未读取总数
			int newPushRecordCount = Integer.parseInt(App.sNewPushRecordCount);
			newPushRecordCount++;
			App.sNewPushRecordCount = String.valueOf(newPushRecordCount);
			saveNewPushRecordCount(App.sNewPushRecordCount);
			MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
		}
	}
	
	@SuppressLint("UseSparseArrays")
	private void setNoticeStatusWithReport(){
		if(App.sUserLoginId<=0){
			AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
			App.sUserLoginId = dbImpl.getUserId();
		}
		Map<Long,Integer> map = new HashMap<Long,Integer>();
		JSONObject jsonObject = null;
		List<Long> reportTopicId = new ArrayList<Long>();
		PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
		List<Object> listObj = daoDBImpl.findAppointObjs("1001",String.valueOf(App.sUserLoginId));
		int size = listObj.size();
		for(int i=0;i<size;i++){
			String jsonContent = ((PushRecord)listObj.get(i)).getContent();
			try {
				jsonObject = new JSONObject(jsonContent);
				long topicId = jsonObject.getLong("topicId");
				if(-1 != reportTopicId.indexOf(topicId)){
					if(((PushRecord)listObj.get(i)).getType()==1){
						int index = map.get(topicId) + 1;
						map.put(topicId, index);//未阅读
					}
				}else{
					reportTopicId.add(topicId);
					if(((PushRecord)listObj.get(i)).getType()==1){
						map.put(topicId, 1);//未阅读
					}
				}
			} catch (JSONException e) {
				Loger.i("TEST", "setNoticeStatusWithReport=Err"+e.getMessage());
			}
		}
		if(App.sNewPushRecordCount==null||App.sNewPushRecordCount.isEmpty()||App.sNewPushRecordCount=="null"||App.sNewPushRecordCount.length()<=0){
			App.sNewPushRecordCount = "0";
		}
		
		for(Integer value : map.values()){
			if(value<2){//全部回复已读时，增加未读取总数
				Loger.i("TEST", "value===>"+value);
				int newPushRecordCount = Integer.parseInt(App.sNewPushRecordCount);
				newPushRecordCount++;
				App.sNewPushRecordCount = String.valueOf(newPushRecordCount);
				saveNewPushRecordCount(App.sNewPushRecordCount);
				MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
			}
		}
		listObj.clear();
		map.clear();
		Loger.i("TEST", "当前未读取总数量为:"+App.sNewPushRecordCount);
	}
	
	public String getNewPushRecordCount(){
		SharedPreferences sharedata = this.context.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String countString = sharedata.getString("recordCount", "0");
		if(countString==null | countString == "null" | countString.length()<=0 | countString.isEmpty()){
			countString = "0";
		}
		return countString;
	}
	
	public void saveNewPushRecordCount(String count){
		Editor sharedata = this.context.getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		if(count==null){
			count = "0";
		}
		sharedata.putString("recordCount", count);
		sharedata.commit();
	}
	
	private long getUserLoginId(){
		long userId = 0;
		if(App.sUserLoginId<=0){
			AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(context);
			userId = daoDBImpl.getUserId();
			return userId;
		}else{
			userId = App.sUserLoginId;
			return userId;
		}
	}
}
