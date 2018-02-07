package com.nfs.youlin.push;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chat.EMChatManager;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.Apk_update_detail;
import com.nfs.youlin.activity.InitTransparentActivity;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.activity.NewPushStoreCommentActivity;
import com.nfs.youlin.activity.PushNewsDetailActivity;
import com.nfs.youlin.activity.ReportDetailActivity;
import com.nfs.youlin.activity.TransparentActivity;
import com.nfs.youlin.activity.WeatherListActivity;
import com.nfs.youlin.activity.find.NewsDetail;
import com.nfs.youlin.activity.find.StoreCircleDetailActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.PropertyRepairActivity;
import com.nfs.youlin.activity.neighbor.PropertyRepairList;
import com.nfs.youlin.activity.titlebar.barter.BarterDedailCommentActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.entity.PushRecord;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.EasemobHandler;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.error_logtext;

import cn.jpush.android.api.JPushInterface;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

@SuppressLint("SdCardPath")
@SuppressWarnings("deprecation")
public class YLBroadReceiver extends BroadcastReceiver {

	private static final String TAG = "TEST";
	private Context context;
	private boolean bRequestType = false;
	private String szFlag = "no";
	private String communityName;
	private String errorInfo;

	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context = context;
		Bundle bundle = intent.getExtras();
//		Loger.d(TAG, "[YLBroadReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {//透传
        	String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        	String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			Loger.d(TAG, "[YLBroadReceiver] JPushInterface.EXTRA_TITLE: " + title);
//			Loger.d(TAG, "[YLBroadReceiver] JPushInterface.EXTRA_MESSAGE: " + message);
//			Loger.d(TAG, "[YLBroadReceiver] JPushInterface.EXTRA_EXTRA: " + extras);
			if(title.equals(App.PUSH_TITLE_NEW_TOPIC)){//有新话题
				String userId = message.split("[:]")[1];
				String communiyId = message.split("[:]")[2];
				String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
				Loger.i(TAG,App.PUSH_TITLE_NEW_TOPIC+"  "+App.sFamilyCommunityId+" "+communiyId);
				List<String> blackList=new ArrayList<String>();
				if(String.valueOf(App.sFamilyCommunityId).equals(communiyId)){
					String[] blackListId = extras.split("-");
					int Len = blackListId.length;
					for(int i=0;i<Len;i++){
						Loger.i(TAG, "BlackUserId==>"+blackListId[i]);
						blackList.add(blackListId[i]);
					}
					if(!blackList.contains(String.valueOf(App.sUserLoginId)) && (!String.valueOf(App.sUserLoginId).equals(userId))){
						Intent newTopicIntent = new Intent();  
						newTopicIntent.setAction("youlin.new.topic.action");  
						context.sendBroadcast(newTopicIntent);
						Loger.i("NEW", "有新话题->"+App.sUserLoginId+" "+userId);
						return;
					}
				}
				return;
			}else if(title.equals(App.PUSH_TITLE_DEL_TOPIC)){//删除帖子推送
				long topicId = Long.parseLong(message);
				ForumtopicDaoDBImpl forumtopicDaoDBImplObj=new ForumtopicDaoDBImpl(context);
				//coding...
				if(FriendCircleFragment.allList !=null && FriendCircleFragment.maddapter!=null){
					Iterator<Object> iterator=FriendCircleFragment.allList.iterator();
					while(iterator.hasNext()){
						ForumTopic forumTopic=(ForumTopic)iterator.next();
						if(forumTopic.getSender_id()!=App.sUserLoginId && FriendCircleFragment.allList.size()>1){
							if(forumTopic.getTopic_id()==topicId){
								forumtopicDaoDBImplObj.deleteObject(topicId);
								iterator.remove();
								FriendCircleFragment.maddapter.notifyDataSetChanged();
							}
							break;
						}
					}
				}
			}else if(title.equals(App.PUSH_FORCE_RETURN)){//强制更新，弹出dlg
				//message为json,提供了所需的所有数据
				Loger.i("TEST", "强制更新--->"+message);
				if(!isForeground(context, "com.nfs.youlin.activity.Apk_update_detail")){//界面不在前台运行
					Loger.i("TEST", "强制更新------------------->");
					try {
						JSONObject response=new JSONObject(message);
						Intent intent2 = new Intent(context,Apk_update_detail.class);
						intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						String strCode = response.getString("vcode");
						strCode =strCode.substring(1, strCode.length());
						if (!strCode.equals(getVersion())){
							intent2.putExtra("apksize", response.getString("size"));
							intent2.putExtra("version", strCode);
							intent2.putExtra("apkdetail", response.getJSONArray("detail").get(0).toString());
							intent2.putExtra("apkforce", response.getString("force"));
							intent2.putExtra("appName",context.getApplicationContext().getResources().getText(R.string.app_name).toString());  
							intent2.putExtra("url",response.getString("url"));  
							intent2.putExtra("status",response.getString("force"));
							intent2.putExtra("type", "push");
							context.startActivity(intent2);
						}
					} catch (NotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return;
			}else if(title.equals(App.PUSH_TITLE_DEL_COMMENT)){//有删除的回复
				Loger.i(TAG, "删除COMM==>"+message);
				String topicId = message.split("[:]")[0];
				String userId = message.split("[:]")[1];
				String communiyId = message.split("[:]")[2];
				String commentId = message.split("[:]")[3];
				String count = message.split("[:]")[4];
				if(String.valueOf(App.sFamilyCommunityId).equals(communiyId)){
//						if(false == App.sLoadNewTopicStatus){
							Intent delCommIntent = new Intent();  
							Bundle delCommBundle = new Bundle();
							delCommBundle.putString("communiyId", communiyId);//删除回复的小区Id
							delCommBundle.putString("topicId", topicId);//删除回复的topicId
							delCommBundle.putString("userId", userId);//执行删除操作userId
							delCommBundle.putString("commentId", commentId);//被删除的回复Id
							delCommBundle.putString("count", count);//count
							delCommIntent.putExtra("delCommBundle",delCommBundle);
							delCommIntent.setAction("youlin.del.comm.action");  
							context.sendBroadcast(delCommIntent);
//						}
				}
				return;
			}else if(title.equals(App.PUSH_ADD_BLACK)){//被某人添加黑名单
				Loger.i(TAG, "被某人添加黑名单==>"+message);
				String addUserId = message.split("[:]")[0]; //执行黑名单操作的用户id
				String myUserId = message.split("[:]")[1];  //当前登录用户id
				if((String.valueOf(App.sUserLoginId).equals(myUserId))){
					Loger.i(TAG, "userId比对成功,进行下一步操作");
					Intent blackIntent=new Intent("com.nfs.youlin.find.get_neighbor");
					blackIntent.putExtra("add_user_id", addUserId);
					context.sendBroadcast(blackIntent);
					Intent blackTopicIntent=new Intent("youlin.friend.action.black");
					blackTopicIntent.putExtra("sender_id", Long.parseLong(addUserId));
					context.sendBroadcast(blackTopicIntent);
				}else{
					Loger.i(TAG, "userId没有比对成功");
				}
			}else if(title.equals(App.PUSH_DEL_BLACK)){//被某人解除黑名单
				Loger.i(TAG, "被某人解除黑名单==>"+message);//message中返回需要你添加的邻居信息(信息内容同邻居列表项)
				context.sendBroadcast(new Intent("com.nfs.youlin.find.get_neighbor"));
			}else if(title.equals(App.PUSH_UPDATE_PASSWD)){//修改密码
				Loger.i(TAG, "更新passwd==>"+message);
				String localImei = null;
				localImei = YLPushUtils.getImei(context, localImei);
				Loger.i(TAG, "本机imei==>"+localImei);
				if(!message.equals(localImei)){//证明是本机操作屏蔽
					if (MainActivity.sMainActivity!=null){
		        		Loger.i(TAG, "从MainActivity跳转");
		        		Intent tranIntent = new Intent();
		        		tranIntent.setClass(context.getApplicationContext(),TransparentActivity.class);
		        		tranIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        		tranIntent.putExtra("passwd", "passwd");
		        		context.startActivity(tranIntent);
		        	}else{
		        		Intent tranIntent = new Intent();
		        		Loger.i(TAG, "从SplashActivity跳转");
		        		tranIntent.setClass(context.getApplicationContext(),TransparentActivity.class);
		        		tranIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        		tranIntent.putExtra("passwd", "passwd");
		        		context.startActivity(tranIntent);
		        	}
				}
				
				return;
			}else if(title.equals(App.PUSH_UPDATE_TOPIC)){
				Loger.i(TAG, "更新topic==>"+message);
				String topicId=message;
				Intent updateIntent=new Intent("youlin.update.topic.action");
				updateIntent.putExtra("topic_id", topicId);
				context.sendBroadcast(updateIntent);
				return;
			}else if(title.equals(App.PUSH_TITLE_NEW_COMMENT)){//有新回复消息
				String topicId = message.split("[:]")[0];
				String senderId = message.split("[:]")[1];
				String count = message.split("[:]")[3];
				String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
				String[] blackListId = extras.split("-");
				int Len = blackListId.length;
				List<String> blackList=new ArrayList<String>();
				for(int i=0;i<Len;i++){
					Loger.i(TAG, "BlackUserId==>"+blackListId[i]);
					blackList.add(blackListId[i]);
				}
				if(!blackList.contains(String.valueOf(App.sUserLoginId))){
					Bundle topicBundle = new Bundle();
					topicBundle.putString("topicId", topicId);
					topicBundle.putString("senderId", senderId);
					topicBundle.putString("count", count);
					StatusChangeutils changeutils;
					changeutils = new StatusChangeutils();
					changeutils.setstatuschange("HUIFU",1,topicBundle);
					return;
				}
				return;
			}
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {//明传
//            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//            Loger.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);
        	AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(context);
			Account account = daoDBImpl.findAccountByPhone(App.sUserPhone);
			
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        	String message = bundle.getString(JPushInterface.EXTRA_ALERT);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Loger.d(TAG, "JPushInterface.EXTRA_NOTIFICATION_TITLE: " + title);
//			Loger.d(TAG, "JPushInterface.EXTRA_ALERT: " + message);
//			Loger.d(TAG, "JPushInterface.EXTRA_EXTRA: " + extras);
			try {
				JSONObject jsonObject = new JSONObject(extras);
				String pushType = jsonObject.getString("pushType");
				int contentType = jsonObject.getInt("contentType");
				String titleType = jsonObject.getString("title");
				if(Integer.parseInt(pushType) == 1){//用户
					String adminJson;
					try {
						adminJson = jsonObject.getJSONArray("admin").toString();
					} catch (Exception e1) {
						adminJson = null;
						e1.printStackTrace();
					}
					if(1 == contentType){//管理员申请
						account.setUser_type(1);//申请管理员状态
					}else if(2 == contentType){//成为管理员
						account.setUser_type(2);//申请管理员成功状态
//						PushManager.listTags(context);
					}else if(4 == contentType){//成为物业管理员
						account.setUser_type(4);//物业管理员状态
//						PushManager.listTags(context);
					}else if(5 == contentType){//分享了新闻
						Loger.i("TEST", "接收到了来自["+title+"]的新闻推送");
					}
					App.sUserType = contentType;
					account.setUser_json(adminJson);
					daoDBImpl.modifyObject(account);
					daoDBImpl.releaseDatabaseRes();
					Loger.i("TEST","我的用户类型==>"+contentType);
					Intent broadIntent = new Intent("com.nfs.youlin.push.info");
			    	Bundle broadBundle = new Bundle();
			    	broadBundle.putString("title", title);
			    	broadBundle.putString("content", message);
			    	broadBundle.putString("jsonObject", extras);
			    	broadIntent.putExtra("push_info", broadBundle);
			    	context.sendBroadcast(broadIntent);
			    	return;
				}else if(Integer.parseInt(pushType) == 2){//地址
					Loger.i("LYM","extras===>"+extras);
					if(1 == contentType){//地址审核
						//地址审核通过状态
//						Loger.i("TEST","地址审核已经审核通过。。。。。。。。。。。。。。。。。。。");
						try {
							MainActivity.networkLayout.setVisibility(View.GONE);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							JSONObject object = new JSONObject(extras);
							JSONArray familyDict = object.getJSONArray("familyDict");
							JSONObject dictObj = familyDict.getJSONObject(0);
							String familyId = dictObj.getString("familyId");
							String cityId = dictObj.getString("cityId");
							String blockId = dictObj.getString("blockId");
//							String entityType = dictObj.getString("entityType");
							String neStatus = dictObj.getString("neStatus");
							AllFamilyDaoDBImpl dbImpl = new AllFamilyDaoDBImpl(context);
							AllFamily family = null;
//							Loger.i(TAG, "neStatus==>"+neStatus);
//							Loger.i(TAG, "UserId==>"+App.sUserLoginId);
							if(neStatus==null || "null".equals(neStatus) || "0".equals(neStatus)){
//								Loger.i(TAG, "IF 当前neStatus==>"+neStatus);
								family = dbImpl.findVerifyObject(0, familyId, String.valueOf(App.sUserLoginId)); //通过familyId查找
							}else{
//								Loger.i(TAG, "ELSE 当前neStatus==>"+neStatus);
								family = dbImpl.findVerifyObject(1, neStatus, String.valueOf(App.sUserLoginId)); //通过neStatus查找
							}
//							Loger.i(TAG, "Family_id()==>"+familyId);
//							Loger.i(TAG, "old family.getEntity_type()==>"+family.getEntity_type());
//							Loger.i(TAG, "old family.getFamily_id()==>"+family.getFamily_id());
//							Loger.i(TAG, "old family.getFamily_address()==>"+family.getFamily_address());
//							Loger.i(TAG, "old family.getFamily_community_id()==>"+family.getFamily_community_id());
							family.setFamily_id(Long.parseLong(familyId));
							family.setFamily_city_id(Long.parseLong(cityId));
							family.setFamily_block_id(Long.parseLong(blockId));
							family.setEntity_type(1);
							family.setNe_status(0);
							Loger.d("test5","address="+family.getFamily_address() );
							dbImpl.modifyObject(family,family.getFamily_address());
//							Loger.i(TAG, "new family.getEntity_type()==>"+family.getEntity_type());
//							Loger.i(TAG, "new family.getFamily_id()==>"+family.getFamily_id());
//							Loger.i(TAG, "new family.getFamily_address()==>"+family.getFamily_address());
//							Loger.i(TAG, "new family.getFamily_community_id()==>"+family.getFamily_community_id());
//							Loger.i(TAG, "familyDict==>"+dictObj.toString());
							App.setAddrStatus(context, 1);
							StatusChangeutils changeutils;
							changeutils = new StatusChangeutils();
							changeutils.setstatuschange("ADDRVERIFY",1);
							changeutils.setstatuschange("SETNEIGHBOR",1);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Loger.i(TAG, "Exception==>"+e.getMessage());
							e.printStackTrace();
						}
					}else if(2 == contentType){//地址审核中
						//地址审核中状态
					}else if(3 == contentType){//地址审核失败
						Loger.d("test5", "地址审核失败");
						JSONObject object = new JSONObject(extras);
						Loger.d("test5", extras);
						JSONArray familyDict = object.getJSONArray("familyDict");
						JSONObject dictObj = familyDict.getJSONObject(0);
						String familyId = dictObj.getString("familyId");
						Loger.d("test5", "familyId="+familyId);
						String cityId = dictObj.getString("cityId");
						String blockId = dictObj.getString("blockId");
						String neStatus = dictObj.getString("neStatus");
						AllFamilyDaoDBImpl dbImpl = new AllFamilyDaoDBImpl(context);
						AllFamily family = null;
						if(neStatus==null || "null".equals(neStatus) || "0".equals(neStatus)){
//							Loger.i(TAG, "IF 当前neStatus==>"+neStatus);
							family = dbImpl.findVerifyObject(0, familyId, String.valueOf(App.sUserLoginId)); //通过familyId查找
						}else{
//							Loger.i(TAG, "ELSE 当前neStatus==>"+neStatus);
							family = dbImpl.findVerifyObject(1, neStatus, String.valueOf(App.sUserLoginId)); //通过neStatus查找
						}
						family.setFamily_id(Long.parseLong(familyId));
						family.setFamily_city_id(Long.parseLong(cityId));
						family.setFamily_block_id(Long.parseLong(blockId));
						family.setEntity_type(2);
						family.setNe_status(Integer.parseInt(neStatus));
						Loger.d("test5","address="+family.getFamily_address() );
						dbImpl.modifyObject(family,family.getFamily_address());
						
						StatusChangeutils changeutils;
						changeutils = new StatusChangeutils();
						changeutils.setstatuschange("ADDRVERIFY",1);
					}
					savePushSuccess(getPushSuccessTag()+1);
					Intent addrIntent = new Intent("com.nfs.youlin.push.info");
			    	Bundle addrBundle = new Bundle();
			    	addrBundle.putString("title", title);
			    	addrBundle.putString("content", message);
			    	addrBundle.putString("jsonObject", extras);
			    	addrIntent.putExtra("push_info", addrBundle);
			    	context.sendBroadcast(addrIntent);
			    	return;
				}else if(Integer.parseInt(pushType) == 3){
					if(titleType.equals(App.PUSH_PROPERTY_NEW_NOTICE)){//有公告
						JSONObject customObject = new JSONObject(extras);
						String userId = customObject.getString("message").split("[:]")[1];
						Intent noticeIntent = new Intent("com.nfs.youlin.push.info");  
						Bundle noticeBundle = new Bundle();
						noticeBundle.putString("title", title);
						noticeBundle.putString("content", userId);
						noticeBundle.putString("jsonObject", extras);
				    	noticeIntent.putExtra("push_info", noticeBundle);
				    	context.sendBroadcast(noticeIntent);
				    	return;
					}else if(titleType.equals(App.PUSH_PROPERTY_NEW_REPAIR)){//物业报修
						JSONObject customObject = new JSONObject(extras);
						String userId = customObject.getString("message").split("[:]")[1];
						Intent repairIntent = new Intent("com.nfs.youlin.push.info");  
						Bundle repairBundle = new Bundle();
						repairBundle.putString("title", title);
						repairBundle.putString("content", userId);
						repairBundle.putString("jsonObject", extras);
				    	repairIntent.putExtra("push_info", repairBundle);
				    	context.sendBroadcast(repairIntent);
				    	return;
					}else if(titleType.equals(App.PUSH_PROPERTY_RUN_REPAIR)){//推送给个人的报修状态
						Intent repairStatusIntent = new Intent("com.nfs.youlin.push.info");  
						Bundle repairStatusBundle = new Bundle();
						repairStatusBundle.putString("title", title);
						repairStatusBundle.putString("content", message);
						repairStatusBundle.putString("jsonObject", extras);
				    	repairStatusIntent.putExtra("push_info", repairStatusBundle);
				    	context.sendBroadcast(repairStatusIntent);
				    	return;
					}
				}else if(Integer.parseInt(pushType) == 4){//有举报
					JSONObject customObject = new JSONObject(extras);
					String userId = customObject.getString("message").split("[:]")[1];
					Intent reportIntent = new Intent("com.nfs.youlin.push.info");  
					Bundle reportBundle = new Bundle();
					reportBundle.putString("title", title);
					reportBundle.putString("content", userId);
					reportBundle.putString("jsonObject", extras);
			    	reportIntent.putExtra("push_info", reportBundle);
			    	context.sendBroadcast(reportIntent);
					return;
				}else if(Integer.parseInt(pushType) == 5){//有新闻
					Loger.i("TEST","extras==>"+extras);
					Intent newsIntent = new Intent("com.nfs.youlin.push.info");  
					Bundle newsBundle = new Bundle();
					newsBundle.putString("title", title);
					newsBundle.putString("content", message);
					newsBundle.putString("jsonObject", extras);
					newsIntent.putExtra("push_info", newsBundle);
					context.sendBroadcast(newsIntent);
					return;
				}else if(Integer.parseInt(pushType) == 6){//有回复相关
					Intent repCommIntent = new Intent("com.nfs.youlin.push.info");  
					Bundle repCommBundle = new Bundle();
					repCommBundle.putString("title", title);
					repCommBundle.putString("content", message);
					repCommBundle.putString("jsonObject", extras);
					repCommIntent.putExtra("push_info", repCommBundle);
					context.sendBroadcast(repCommIntent);
					return;
				}else if(Integer.parseInt(pushType) == 7){//有评价
					Intent evaCommIntent = new Intent("com.nfs.youlin.push.info");  
					Bundle evaCommBundle = new Bundle();
					evaCommBundle.putString("title", title);
					evaCommBundle.putString("content", message);
					evaCommBundle.putString("jsonObject", extras);
					evaCommIntent.putExtra("push_info", evaCommBundle);
					context.sendBroadcast(evaCommIntent);
					return;
				}else if(Integer.parseInt(pushType) == 8){//天气预报
					Intent weaIntent = new Intent("com.nfs.youlin.push.info");  
					Bundle weaBundle = new Bundle();
					weaBundle.putString("title", title);
					weaBundle.putString("content", message);
					weaBundle.putString("jsonObject", extras);
					weaIntent.putExtra("push_info", weaBundle);
					context.sendBroadcast(weaIntent);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Loger.d(TAG, "用户点击打开了通知");
//    		BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
//    		builder.statusBarDrawable = R.drawable.youlin;
//    		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
//    		builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
//    		JPushInterface.setPushNotificationBuilder(1, builder);
        	//打开自定义的Activity
            String contentStatus = null;
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Loger.d(TAG, "JPushInterface.EXTRA_EXTRA: " + extras);
    		try {
    			JSONObject jsonObject = new JSONObject(extras);
    			int pushType = jsonObject.getInt("pushType");
    			int contentType = jsonObject.getInt("contentType");
    			if(pushType==1 && contentType==3){
    				contentStatus = "login";
    			}
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		updateContent(context,contentStatus,extras);
    		
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Loger.d(TAG, "用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Loger.w(TAG, "[YLBroadReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Loger.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}
	
	
	@SuppressWarnings("unused")
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
					Loger.i(TAG, "This message has no Extra data");
					continue;
				}
				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();
					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Loger.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	private boolean isRunningForeground (Context context)  //android.permission.GET_TASKS
	{  
	    ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
	    ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
	    String currentPackageName = cn.getPackageName();  
	    if(!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName()))  
	    {  
	    	Loger.i(TAG, "已经在前台运行");
	        return true ;  
	    }  
	    Loger.i(TAG, "尚未在前台运行");
	    return false ;  
	}
	
	private void updateContent(final Context context, String contentStatus, String extras) {
		if(App.sUserLoginId<=0){
    		Loger.i(TAG, "优邻还没有登陆过");
    		Intent intent = new Intent();
    		intent.setClass(context.getApplicationContext(),InitTransparentActivity.class);
    		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		context.getApplicationContext().startActivity(intent);
    		return;
    	}
		if("login".equals(contentStatus)){
			if(isRunningForeground(context)){//已经在前台运行
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						userLogOff(context);
						if(MainActivity.sMainActivity!=null){
							MainActivity.sMainActivity.finish();
						}
//						Intent intent = new Intent();
//			            intent.setClass(context.getApplicationContext(),SplashActivity.class);
//			            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			            context.getApplicationContext().startActivity(intent);
						Loger.i(TAG, "已经在前台运行");
					}
				}).start();
			}else{
//				Intent intent = new Intent();
//	            intent.setClass(context.getApplicationContext(),SplashActivity.class);
//	            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	            context.getApplicationContext().startActivity(intent);
				Loger.i(TAG, "不在前台运行");
			}
			return;
		}
		long currentCommunityId = 0;
		JSONObject jsonObj = null;
		AllFamilyDaoDBImpl dao = new AllFamilyDaoDBImpl(context);
		try {
			jsonObj = new JSONObject(extras);
			currentCommunityId = jsonObj.getLong("communityId");
			try {
				App.sFamilyCommunityId = dao.getUserCurrentAddrDetail().getFamily_community_id();
			} catch (Exception e1) {
				App.sFamilyCommunityId = 0L;
				e1.printStackTrace();
			}
		} catch (JSONException e2) {
			Loger.i(TAG, "updateContent-NULL=>"+e2.getMessage());
		}
        if(!isRunningForeground(context)){
        	Loger.i(TAG, "优邻不在前台运行");
        	Intent intent = new Intent();
        	if (MainActivity.sMainActivity!=null){//home
        		Loger.i(TAG, "从MainActivity跳转");
        		intent.setClass(context.getApplicationContext(),MainActivity.class);
        		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        		Intent intentPB = new Intent();  
//        		intentPB.setAction("youlin.initpb.action");  
//				context.sendBroadcast(intentPB);
        		if(currentCommunityId!=App.sFamilyCommunityId){
        			communityName = dao.getCommunityNameById(String.valueOf(currentCommunityId));
        			if(communityName==null){//表明没有该小区
			        	errorInfo = "您尚未加入该小区";
			        }else{
			        	errorInfo = "请切换到["+communityName+"]进行查看";
			        }
        			Intent intentWihtToast = new Intent();
        			intent.putExtra("Extras", errorInfo);
        			intent.putExtra("Notification", 9527);
			        intentWihtToast.setClass(context.getApplicationContext(),MainActivity.class);
			        intentWihtToast.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        context.getApplicationContext().startActivity(intentWihtToast);
			        return;
        		}
        	}else{//finish
        		Loger.i(TAG, "从Splash跳转");
        		intent.setClass(context.getApplicationContext(),InitTransparentActivity.class);
        		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				if(currentCommunityId!=App.sFamilyCommunityId){
			        communityName = dao.getCommunityNameById(String.valueOf(currentCommunityId));
			        if(communityName==null){//表明没有该小区
			        	errorInfo = "您尚未加入该小区";
			        }else{
			        	errorInfo = "请切换到["+communityName+"]进行查看";
			        }
			        Intent intentWihtToast = new Intent();
			        intentWihtToast.putExtra("exit", "toast");
			        intentWihtToast.putExtra("message", errorInfo);
			        intentWihtToast.setClass(context.getApplicationContext(),InitTransparentActivity.class);
			        intentWihtToast.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        context.getApplicationContext().startActivity(intentWihtToast);
			        return;
				}
        	}
        	try {
//				JSONObject jsonObj = new JSONObject(extras);
				int pushType = 0;
				try {
					pushType = jsonObj.getInt("pushType");
				} catch (Exception e) {
					Loger.i(TAG, "updateContent-pushType=>"+e.getMessage());
				}
				int contentType = 0;
				try {
					contentType = jsonObj.getInt("contentType");
				} catch (Exception e) {
					Loger.i(TAG, "updateContent-contentType=>"+e.getMessage());
				}
				int topicType = 0;
				try {
					topicType = jsonObj.getInt("topicType");
				} catch (Exception e) {
					Loger.i(TAG, "updateContent-topicType=>"+e.getMessage());
				}
				if(1==pushType){
					if(1 == contentType){//管理员申请
						intent.putExtra("Notification", 11);
					}else if (2 == contentType){//成为管理员
						intent.putExtra("Notification", 12);
					}else if (4 == contentType){//物业管理员
						intent.putExtra("Notification", 14);
					}else if (5 == contentType){//分享了新闻
						intent.putExtra("Notification", 15);
					}
					intent.putExtra("Extras",extras);
					context.getApplicationContext().startActivity(intent); 
					return;
				}else if(2==pushType){
					if(1==contentType){//地址审核通过
						intent.putExtra("Notification", 21);
					}else if(2==contentType){//申请地址
						intent.putExtra("Notification", 22);
					}else if(3==contentType){//审核失败
						intent.putExtra("Notification", 23);
					}
					intent.putExtra("Extras",extras);
					context.getApplicationContext().startActivity(intent); 
					return;
				}else if(3==pushType){
					String titleType = jsonObj.getString("title");
					if (titleType.equals(App.PUSH_PROPERTY_NEW_NOTICE)){//有公告
						intent.putExtra("Notification", 31);
					}else if(titleType.equals(App.PUSH_PROPERTY_NEW_REPAIR)){//物业报修
						intent.putExtra("Notification", 32);
					}else if(titleType.equals(App.PUSH_PROPERTY_RUN_REPAIR)){//推送给个人的报修状态
						intent.putExtra("Notification", 33);
					}
					intent.putExtra("Extras",extras);
					context.getApplicationContext().startActivity(intent); 
				}else if(4 == pushType){//管理员相关 举报
					intent.putExtra("Notification", 41);
					intent.putExtra("Extras",extras);
					context.getApplicationContext().startActivity(intent); 
				}else if(5 == pushType){//新闻
					intent.putExtra("Notification", 51);
					intent.putExtra("Extras",extras);
					context.getApplicationContext().startActivity(intent); 
				}else if(6 == pushType){//回复
					if (4 == topicType){//以物易物
						intent.putExtra("Notification", 62);
						intent.putExtra("Extras",extras);
						context.getApplicationContext().startActivity(intent);
					}else if(11 == pushType){//打招呼
						intent.putExtra("Notification", 63);
						intent.putExtra("Extras",extras);
						context.getApplicationContext().startActivity(intent);
					}else{
						intent.putExtra("Notification", 61);
						intent.putExtra("Extras",extras);
						context.getApplicationContext().startActivity(intent);
					}
				}else if(7 == pushType){//评价
					intent.putExtra("Notification", 71);
					intent.putExtra("Extras",extras);
					context.getApplicationContext().startActivity(intent);
				}else if(8 == pushType){//天气预报
					intent.putExtra("Notification", 81);
					intent.putExtra("Extras",extras);
					context.getApplicationContext().startActivity(intent);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Loger.i(TAG, "updateContent-Err=>"+e.getMessage());
			}    	
        }else{
        	Loger.i(TAG, "优邻正在前台运行");
        	Intent intent = new Intent();
        	try {
//				JSONObject jsonObj = new JSONObject(extras);
				long currCommunityId = 0;
				currCommunityId = jsonObj.getLong("communityId");
				if(currCommunityId!=App.sFamilyCommunityId){
					AllFamilyDaoDBImpl daoWithRunning = new AllFamilyDaoDBImpl(context);
			        communityName = daoWithRunning.getCommunityNameById(String.valueOf(currCommunityId));
			        String errorInfo = null;
			        if(communityName==null){//表明没有该小区
			        	errorInfo = "您尚未加入该小区";
			        }else{
			        	errorInfo = "请切换到["+communityName+"]进行查看";
			        }
					try {
						Toast.makeText(context, errorInfo, Toast.LENGTH_LONG).show();
						return;
					} catch (Exception e) {
						Loger.i(TAG, "推送toast Err=>"+e.getMessage());
						return;
					}
				}
				long recordId = 0;
				try {
					recordId = jsonObj.getLong("recordId");
				} catch (Exception e1) {
					Loger.i(TAG, "updateContent-recordId=>"+e1.getMessage());
				}
				int pushType = 0;
				try {
					pushType = jsonObj.getInt("pushType");
				} catch (Exception e) {
					Loger.i(TAG, "updateContent-pushType=>"+e.getMessage());
				}
				int contentType = 0;
				try {
					contentType = jsonObj.getInt("contentType");
				} catch (Exception e) {
					Loger.i(TAG, "updateContent-contentType=>"+e.getMessage());
				}
				int topicType = 0;
				try {
					topicType = jsonObj.getInt("topicType");
				} catch (Exception e) {
					Loger.i(TAG, "updateContent-topicType=>"+e.getMessage());
				}
				getAboutRecordArray();
				PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
				if (1 == pushType) {
					if ((1 == contentType) || (2 == contentType) || (4 == contentType)){//管理员申请、成为管理员、物业管理员
						if(!isForeground(context, "com.nfs.youlin.push.PushRecordDetailActivity")){//界面不在前台运行
							if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
								NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
							}
							PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
							if(null==pushRecord){
								Loger.i(TAG, "没有找到管理员申请、成为管理员、物业管理员");
								return;
							}
							pushRecord.setType(2);
							daoDBImpl.modifyObject(pushRecord);
							setRecordStatus();
							Loger.i(TAG, "com.nfs.youlin.push.PushRecordDetailActivity 不在前台运行");
							intent.putExtra("pushInfo", extras);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setClass(context.getApplicationContext(), PushRecordDetailActivity.class);
							context.getApplicationContext().startActivity(intent);
						}else{
							Loger.i(TAG, "com.nfs.youlin.push.PushRecordDetailActivity 正在前台运行");
						}
						return;
					}else if (5 == contentType){//分享了新闻
						if(!isForeground(context, "com.nfs.youlin.activity.find.NewsDetail")){//界面不在前台运行
							if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
								NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
							}
							PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
							if(null==pushRecord){
								Loger.i(TAG, "没有找到新闻");
								return;
							}
							pushRecord.setType(2);
							daoDBImpl.modifyObject(pushRecord);
							setRecordStatus();
							Loger.i(TAG, "com.nfs.youlin.activity.find.NewsDetail 不在前台运行");
							JSONObject newspush = new JSONObject(extras);
							intent.putExtra("linkurl", newspush.getString("new_url"));
							intent.putExtra("title", newspush.getString("new_title"));
							intent.putExtra("newsid", newspush.getString("new_id"));
							intent.putExtra("picurl",newspush.getString("new_small_pic"));
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setClass(context.getApplicationContext(), NewsDetail.class);
							context.getApplicationContext().startActivity(intent);
						}else{
							Loger.i(TAG, "com.nfs.youlin.activity.find.NewsDetail 正在前台运行");
						}
					}
					return;
				} else if (2 == pushType) {
					if ((1 == contentType) || (2 == contentType) || (3 == contentType)) {// 地址审核通过、申请地址
						if(!isForeground(context, "com.nfs.youlin.push.PushRecordDetailActivity")){//界面不在前台运行
							if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
								NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
							}
							PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
							if(null==pushRecord){
								Loger.i(TAG, "没有找到地址审核通过、申请地址");
								return;
							}
							pushRecord.setType(2);
							daoDBImpl.modifyObject(pushRecord);
							setRecordStatus();
							Loger.i(TAG, "com.nfs.youlin.push.PushRecordDetailActivity 不在前台运行");
							intent.putExtra("pushInfo", extras);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setClass(context.getApplicationContext(), PushRecordDetailActivity.class);
							context.getApplicationContext().startActivity(intent);
						}else{
							Loger.i(TAG, "com.nfs.youlin.push.PushRecordDetailActivity 正在前台运行");
						}
					} 
					return;
				} else if (3 == pushType){//物业相关
					String titleType = jsonObj.getString("title");
					if (titleType.equals(App.PUSH_PROPERTY_NEW_NOTICE)){//有公告
						if(!isForeground(context, "com.nfs.youlin.activity.neighbor.CircleDetailActivity")){//界面不在前台运行
							if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
								NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
							}
							PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
							if(null==pushRecord){
								Loger.i(TAG, "没有找到物业相关");
								return;
							}
							pushRecord.setType(2);
							daoDBImpl.modifyObject(pushRecord);
							setRecordStatus();
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.CircleDetailActivity 不在前台运行");
							// 跳转至详情
							jumpToDetailPage(extras);
						}else{
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.CircleDetailActivity 不在前台运行");
						}
						return;
					}else if(titleType.equals(App.PUSH_PROPERTY_NEW_REPAIR)){//物业报修
						if(!isForeground(context, "com.nfs.youlin.activity.neighbor.PropertyRepairList")){//界面不在前台运行
							if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
								NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
							}
							PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
							if(null==pushRecord){
								Loger.i(TAG, "没有找到物业报修");
								return;
							}
							pushRecord.setType(2);
							daoDBImpl.modifyObject(pushRecord);
							setRecordStatus();
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.PropertyRepairList 不在前台运行");
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setClass(context.getApplicationContext(), PropertyRepairList.class);
							context.startActivity(intent);
						}else{
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.PropertyRepairList 不在前台运行");
						}
						return;
					}else if(titleType.equals(App.PUSH_PROPERTY_RUN_REPAIR)){//推送给个人的报修状态
						if(!isForeground(context, "com.nfs.youlin.activity.neighbor.PropertyRepairActivity")){//界面不在前台运行
							if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
								NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
							}
							PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
							if(null==pushRecord){
								Loger.i(TAG, "没有找到推送给个人的报修状态");
								return;
							}
							pushRecord.setType(2);
							daoDBImpl.modifyObject(pushRecord);
							setRecordStatus();
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.PropertyRepairActivity 不在前台运行");
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setClass(context.getApplicationContext(), PropertyRepairActivity.class);
							context.startActivity(intent);
						}else{
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.PropertyRepairActivity 不在前台运行");
						}
						return;
					}
				} else if (4 == pushType){//管理员相关
					if(!isForeground(context, "com.nfs.youlin.push.ReportDetailActivity")){//界面不在前台运行
						if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
							NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
						}
						if(App.sUserLoginId<=0){
							AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
							App.sUserLoginId = dbImpl.getUserId();
						}
						PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
						if(null==pushRecord){
							Loger.i(TAG, "没有找到管理员相关");
							return;
						}
						JSONObject topicObj = new JSONObject(pushRecord.getContent());		
						daoDBImpl.modifyReportObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
						setRecordStatus();
						Loger.i(TAG, "com.nfs.youlin.activity.ReportDetailActivity 不在前台运行");
						JSONObject json = new JSONObject(extras);
						intent.putExtra("report_tId", Long.parseLong(json.getString("topicId")));
						intent.putExtra("report_detail",String.valueOf(json.getString("topicDetail")));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setClass(context.getApplicationContext(), ReportDetailActivity.class);
						context.startActivity(intent);
					}else{
						Loger.i(TAG, "com.nfs.youlin.activity.ReportDetailActivity 不在前台运行");
					}
					return;
				} else if(7 == pushType){//评价
					if(!isForeground(context, "com.nfs.youlin.activity.NewPushStoreCommentActivity")){//界面不在前台运行
						if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
							NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
						}
						if(App.sUserLoginId<=0){
							AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
							App.sUserLoginId = dbImpl.getUserId();
						}
						PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
						if(null==pushRecord){
							Loger.i(TAG, "没有找到评论相关");
							return;
						}
						pushRecord.setType(2);
						daoDBImpl.modifyObject(pushRecord);
						setRecordStatus();
						Loger.i(TAG, "com.nfs.youlin.activity.NewPushStoreCommentActivity不在前台运行");
						final JSONObject json = new JSONObject(extras);
						RequestParams params=new RequestParams();
						params.put("tag", "getorcheckrecord");
						params.put("apitype", "address");
						params.put("uer_id", json.getString("uerId"));
						AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
						asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler(){
							public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
								try {
									String flag=response.getString("flag");
									Loger.i("LYM", "notify==pushType--->"+flag);
									if(flag.equals("no")){
										Intent intent = new Intent(context,NewPushStoreCommentActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra("tag", json.getString("tag"));
										intent.putExtra("uid", json.getString("uid"));
										intent.putExtra("shop_name", json.getString("shopName"));
										context.startActivity(intent);
									}else if(flag.equals("ok")){
										Intent intent = new Intent(context,StoreCircleDetailActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra("uid", json.getString("uid"));
										context.startActivity(intent);
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								super.onSuccess(statusCode, headers, response);
							};
							public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
								new ErrorServer(context, responseString);
								super.onFailure(statusCode, headers, responseString, throwable);
							};
						});
					}
				} else if(8 == pushType){//天气预报
					if(!isForeground(context, "com.nfs.youlin.activity.WeatherListActivity")){//界面不在前台运行
						if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
							NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
						}
						PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
						if(null==pushRecord){
							Loger.i(TAG, "没有找到天气预报记录");
							return;
						}
						pushRecord.setType(2);
						daoDBImpl.modifyObject(pushRecord);
						setRecordStatus();
						JSONObject json = new JSONObject(extras);
						intent.putExtra("weaorzoc_id", Long.parseLong(json.getString("weaId")));
						intent.putExtra("community_id",String.valueOf(json.getString("communityId")));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setClass(context.getApplicationContext(), WeatherListActivity.class);
						context.startActivity(intent);
						Loger.i(TAG, "com.nfs.youlin.push.PushRecordDetailActivity 不在前台运行");
					} else{
						Loger.i(TAG, "com.nfs.youlin.activity.WeatherListActivity 不在前台运行");
					}
				} else if(5 == pushType){//新闻
					if(!isForeground(context, "com.nfs.youlin.activity.PushNewsDetailActivity")){//界面不在前台运行
						if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
							NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
						}
						if(App.sUserLoginId<=0){
							AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
							App.sUserLoginId = dbImpl.getUserId();
						}
						int newRecordCount = 0;
						try {
							newRecordCount = Integer.parseInt(App.sNewPushRecordCount);
						} catch (NumberFormatException e) {
							e.printStackTrace();
							Loger.i(TAG, "error+"+e.getMessage());
							newRecordCount = 0;
						}
						if(newRecordCount<=0){
							Loger.i(TAG, "没有找到新闻");
							return;
						}
						daoDBImpl.modifyNewsObjs(2,App.sUserLoginId);
						setRecordStatus();
						Loger.i(TAG, "com.nfs.youlin.activity.PushNewsDetailActivity 不在前台运行");
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setClass(context.getApplicationContext(), PushNewsDetailActivity.class);
						context.startActivity(intent);
					} else{
						Loger.i(TAG, "com.nfs.youlin.activity.PushNewsDetailActivity 不在前台运行");
					}
				} else if(6 == pushType){//回复
					if(11 == topicType){//打招呼
						if(!isForeground(context, "com.nfs.youlin.activity.neighbor.CircleDetailActivity")){//界面不在前台运行
							if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
								NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
							}
							if(App.sUserLoginId<=0){
								AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
								App.sUserLoginId = dbImpl.getUserId();
							}
							PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
							if(null==pushRecord){
								Loger.i(TAG, "没有找到详情相关");
								return;
							}
							JSONObject topicObj = new JSONObject(pushRecord.getContent());	
							String sayHelloUserId = topicObj.getString("commentType");
							daoDBImpl.modifySayHelloObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId),sayHelloUserId);
							setRecordStatus();
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.CircleDetailActivity 不在前台运行");
							// 跳转至详情
							jumpToDetailPageWithComment(extras);
						}else{
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.CircleDetailActivity 不在前台运行");
						}
					}
					if (4 == topicType){//以物易物
						if((!isForeground(context, "com.nfs.youlin.activity.BarterDedailActivity"))&&
						   (!isForeground(context, "com.nfs.youlin.activity.titlebar.barter.BarterDedailCommentActivity"))){//界面不在前台运行
							if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
								NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
							}
							if(App.sUserLoginId<=0){
								AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
								App.sUserLoginId = dbImpl.getUserId();
							}
							PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
							if(null==pushRecord){
								Loger.i(TAG, "没有找到详情相关");
								return;
							}
							JSONObject topicObj = new JSONObject(pushRecord.getContent());		
							daoDBImpl.modifyCommentObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
							setRecordStatus();
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.CircleDetailActivity 不在前台运行");
							// 跳转至详情
							jumpToDetailPageWithCommentBarter(extras);
						}else{
							Loger.i(TAG, "com.nfs.youlin.activity.BarterDedailActivity 不在前台运行");
						}
					}else{
						if(!isForeground(context, "com.nfs.youlin.activity.neighbor.CircleDetailActivity")){//界面不在前台运行
							if(null!=NewPushRecordAbsActivity.sNewPushRecordAbsContext){
								NewPushRecordAbsActivity.sNewPushRecordAbsContext.finish();
							}
							if(App.sUserLoginId<=0){
								AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
								App.sUserLoginId = dbImpl.getUserId();
							}
							PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
							if(null==pushRecord){
								Loger.i(TAG, "没有找到详情相关");
								return;
							}
							JSONObject topicObj = new JSONObject(pushRecord.getContent());		
							daoDBImpl.modifyCommentObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
							setRecordStatus();
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.CircleDetailActivity 不在前台运行");
							// 跳转至详情
							jumpToDetailPageWithComment(extras);
						}else{
							Loger.i(TAG, "com.nfs.youlin.activity.neighbor.CircleDetailActivity 不在前台运行");
						}
					}
        		}
			} catch (JSONException e) {
				Loger.i(TAG, "updateContent-Err=>"+e.getMessage());
			}
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
			Loger.i(TAG, "当前界面名称=>"+tmpString);
			if (className.equals(tmpString)) {
				return true;
			}
		}
		return false;
	}
	
	//设置当前未阅读的数量和显示状态
	private void setRecordStatus(){
		int newRecordCount = Integer
				.parseInt(App.sNewPushRecordCount);
		if (newRecordCount > 0) {
			newRecordCount--;
		}
		App.sNewPushRecordCount = String
				.valueOf(newRecordCount);
		saveNewPushRecordCount(App.sNewPushRecordCount);
		MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
	}
	
	private void saveNewPushRecordCount(String count){
		Editor sharedata = context.getApplicationContext().getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		if(count==null){
			count = "0";
		}
		sharedata.putString("recordCount", count);
		sharedata.commit();
	}
	
	private String[] getAboutRecordArray(){
		String[] strInfo = new String[2];
		int userType = 0;
		if(App.sUserType<=0){
			AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(context);
			userType = dbImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId)).getUser_type();
			App.sUserType = userType;
		}
		strInfo[0] = String.valueOf(userType);
		long communityId = 0;
		if(App.sFamilyCommunityId<=0){
			AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(context);
			communityId = allFamilyDaoDBImpl.getCurrentAddrDetail(MainActivity.currentcity+MainActivity.currentvillage+loadAddrdetailPrefrence()).getFamily_community_id();
			App.sFamilyCommunityId = communityId;
		}
		strInfo[1] = String.valueOf(communityId);
		return strInfo;
	}
	
	private String loadAddrdetailPrefrence(){
		SharedPreferences sharedata = context.getApplicationContext().getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("detail", "未设置");
	}
	
	private void jumpToDetailPageWithCommentBarter(final String json){
		RequestParams reference = new RequestParams();
		reference.put("community_id",App.sFamilyCommunityId);
		reference.put("user_id",App.sUserLoginId);
		reference.put("count", 1);
		reference.put("type", App.sUserType);
		reference.put("tag","gettopic");
		reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
		try {
			JSONObject object = new JSONObject(json);
			reference.put("topic_id", Long.parseLong(object.getString("topicId")));
		} catch (NumberFormatException e) {
			Loger.i(TAG, "jumpToDetailPageWithComment0=>"+e.getMessage());
			return;
		} catch (JSONException e) {
			Loger.i(TAG, "jumpToDetailPageWithComment1=>"+e.getMessage());
			return;
		}
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Loger.i("TEST", response.toString());
				getTopicDetailsInfos(response);
				bRequestType = true;
				if (NewPushRecordAbsActivity.forumtopicLists.size() > 0) {
					szFlag = "ok";
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					szFlag = response.getString("flag");
					if (szFlag.equals("no")) {
						bRequestType = true;
						// daoDBImpl.deleteObject(pushRecord.getRecord_id());
						Toast.makeText(context.getApplicationContext(), "此内容已被删除", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(context, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				Long currenttime = System.currentTimeMillis();
				while (!bRequestType) {
					if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
						bRequestType = true;
					}
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				if (bRequestType == true && szFlag.equals("ok")) {
					szFlag = "none";
					Intent intent = new Intent(context.getApplicationContext(), BarterDedailCommentActivity.class);
					try {
						JSONObject object = new JSONObject(json);
						intent.putExtra("topic_id", Long.parseLong(object.getString("topicId")));
					} catch (NumberFormatException e) {
						Loger.i(TAG, "NumberFormatException==>"+e.getMessage());
					} catch (JSONException e) {
						Loger.i(TAG, "JSONException==>"+e.getMessage());
					}
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(context.getApplicationContext(), BarterDedailCommentActivity.class);
					context.startActivity(intent);
				}
				bRequestType = false;
			};
		}.execute();
	}
	
	private void jumpToDetailPageWithComment(final String json){
		RequestParams reference = new RequestParams();
		reference.put("community_id",App.sFamilyCommunityId);
		reference.put("user_id",App.sUserLoginId);
		reference.put("count", 1);
		reference.put("type", App.sUserType);
		reference.put("tag","gettopic");
		reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
		try {
			JSONObject object = new JSONObject(json);
			reference.put("topic_id", Long.parseLong(object.getString("topicId")));
		} catch (NumberFormatException e) {
			Loger.i(TAG, "jumpToDetailPageWithComment0=>"+e.getMessage());
			return;
		} catch (JSONException e) {
			Loger.i(TAG, "jumpToDetailPageWithComment1=>"+e.getMessage());
			return;
		}
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Loger.i("TEST", response.toString());
				getTopicDetailsInfos(response);
				bRequestType = true;
				if (NewPushRecordAbsActivity.forumtopicLists.size() > 0) {
					szFlag = "ok";
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					szFlag = response.getString("flag");
					if (szFlag.equals("no")) {
						bRequestType = true;
						// daoDBImpl.deleteObject(pushRecord.getRecord_id());
						Toast.makeText(context.getApplicationContext(), "此内容已被删除", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(context, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				Long currenttime = System.currentTimeMillis();
				while (!bRequestType) {
					if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
						bRequestType = true;
					}
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				if (bRequestType == true && szFlag.equals("ok")) {
					szFlag = "none";
					Intent intent = new Intent(context.getApplicationContext(), CircleDetailActivity.class);
					intent.putExtra("parent", 4);
					intent.putExtra("position", 0);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(context.getApplicationContext(), CircleDetailActivity.class);
					context.startActivity(intent);
				}
				bRequestType = false;
			};
		}.execute();
	}
	
	private void jumpToDetailPage(final String json){
		RequestParams reference = new RequestParams();
		reference.put("community_id",App.sFamilyCommunityId);
		reference.put("user_id", App.sUserLoginId);
		reference.put("count", 1);
		reference.put("category_type",App.GONGGAO_TYPE);
		reference.put("type", 2);
		reference.put("tag","getnotice");
		reference.put("apitype",IHttpRequestUtils.APITYPE[4]);
		try {
			JSONObject object = new JSONObject(json);
			reference.put("topic_id", Long.parseLong(object.getString("topicId")));
		} catch (NumberFormatException e) {
			Loger.i(TAG, "jumpToDetailPage1=>"+e.getMessage());
			return;
		} catch (JSONException e) {
			Loger.i(TAG, "jumpToDetailPage2=>"+e.getMessage());
			return;
		}
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,reference,
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				getTopicDetailsInfos(response);
				bRequestType = true;
				if(NewPushRecordAbsActivity.forumtopicLists.size()>0){
					szFlag = "ok";
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					szFlag = response.getString("flag");
					if(szFlag.equals("no")){
						bRequestType = true;
						PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
						long recordId = 0;
						try {
							JSONObject object = new JSONObject(json);
							recordId = object.getLong("recordId");
						} catch (Exception e1) {
							Loger.i(TAG, "jumpToDetailPage-recordId=>"+e1.getMessage());
						}
						daoDBImpl.deleteObject(recordId);
						Toast.makeText(context.getApplicationContext(), "此公告已删除", Toast.LENGTH_SHORT).show();
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
				new ErrorServer(context, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Long currenttime = System.currentTimeMillis();
				while (!bRequestType) {
					if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
						bRequestType = true;
					}
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				if (bRequestType == true && szFlag.equals("ok")) {
					szFlag = "no";
					Intent intent = new Intent();
					intent.putExtra("parent", 4);
					intent.putExtra("position", 0);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(context.getApplicationContext(), CircleDetailActivity.class);
					context.startActivity(intent);
				}
				bRequestType = false;
			};
		}.execute();
	}
	
	private void getTopicDetailsInfos(JSONArray response){
		int responseLen = response.length();
		Loger.i("TEST", "json obj length->"+responseLen);
		if(responseLen>0){
		}
		NewPushRecordAbsActivity.forumtopicLists = new ArrayList<Object>();
		for (int i = 0; i < responseLen; i++) {
			try {
				JSONObject jsonObject = new JSONObject(response.getString(i));
				String cache_key = jsonObject.getString("cacheKey");
				String topic_id = jsonObject.getString("topicId");
				String forum_id = jsonObject.getString("forumId");
				String forum_name = jsonObject.getString("forumName");
				String circle_type = jsonObject.getString("circleType");
				String sender_id = jsonObject.getString("senderId");
				String sender_name = jsonObject.getString("senderName");
				String sender_lever = jsonObject.getString("senderLevel");
				String sender_portrait = jsonObject.getString("senderPortrait");
				String sender_family_id = jsonObject.getString("senderFamilyId");
				String sender_family_address = jsonObject.getString("senderFamilyAddr");
				String disply_name = jsonObject.getString("displayName");
				String topic_time = jsonObject.getString("topicTime");
				String topic_title = jsonObject.getString("topicTitle");
				String topic_content = jsonObject.getString("topicContent");
				String topic_category_type = jsonObject.getString("topicCategoryType");
				String comment_num = jsonObject.getString("commentNum");
				String like_num = jsonObject.getString("likeNum");
				String send_status = jsonObject.getString("sendStatus");
				String visiable_type = jsonObject.getString("visiableType");
				String hot_flag = jsonObject.getString("hotFlag");
				String view_num = jsonObject.getString("viewNum");
				String sender_community_id = jsonObject.getString("communityId");
				String collectStatus = jsonObject.getString("collectStatus");
				App.CurrentSysTime = Long.parseLong(jsonObject.getString("systemTime"));
				String media_files_json = null;
				try {
					media_files_json = jsonObject.getJSONArray("mediaFile").toString();
				} catch (Exception e) {
					Loger.i("TEST", "(E)getTopic->"+e.getMessage());
					e.printStackTrace();
				}
				String comment_json = null;
				try {
					comment_json = jsonObject.getJSONArray("comments").toString();
				} catch (Exception e) {
					Loger.i("TEST", "(E)getTopicComm->"+e.getMessage());
					e.printStackTrace();
				}
				String topic_type = jsonObject.getString("objectType");  //0.表示一般话题、1.表示活动
				if(topic_type==null || topic_type.isEmpty() || topic_type.length()<=0 || topic_type == "null"){
					topic_type = "0";
				}
				ForumTopic forumTopic = new ForumTopic(context);
				forumTopic.setFlag(false);
				forumTopic.setCache_key(Integer.parseInt(cache_key));
				forumTopic.setTopic_id(Long.parseLong(topic_id));
				Loger.d("test5", "topic position="+i+"topicid="+topic_id);
				forumTopic.setForum_id(Long.parseLong(forum_id));
				forumTopic.setForum_name(forum_name);
				forumTopic.setCircle_type(Integer.parseInt(circle_type));
				forumTopic.setSender_id(Long.parseLong(sender_id));
				forumTopic.setSender_name(sender_name);
				forumTopic.setSender_lever(sender_lever);
				forumTopic.setSender_portrait(sender_portrait);
				forumTopic.setSender_family_id(Long.parseLong(sender_family_id));
				forumTopic.setSender_family_address(sender_family_address);
				forumTopic.setDisplay_name(disply_name);
				forumTopic.setTopic_time(Long.parseLong(topic_time));
				forumTopic.setTopic_title(topic_title);
				forumTopic.setTopic_content(topic_content);
				forumTopic.setTopic_category_type(Integer.parseInt(topic_category_type));
				forumTopic.setComments_summary(comment_json);
				forumTopic.setSender_community_id(Long.parseLong(sender_community_id));
				forumTopic.setSender_nc_role(Integer.parseInt(collectStatus));
				if(comment_num==null || comment_num.isEmpty() || comment_num.length()<=0 || comment_num == "null"){
					comment_num = "0";
				}
				forumTopic.setComment_num(Integer.parseInt(comment_num));
				if(like_num==null || like_num.isEmpty() || like_num.length()<=0 || like_num == "null"){
					like_num = "0";
				}
				forumTopic.setLike_num(Integer.parseInt(like_num));
				forumTopic.setSend_status(Integer.parseInt(send_status));
				if(visiable_type==null || visiable_type.isEmpty() || visiable_type.length()<=0 || visiable_type == "null"){
					visiable_type = "0";
				}
				forumTopic.setVisiable_type(Integer.parseInt(visiable_type));
				if(hot_flag==null || hot_flag.isEmpty() || hot_flag.length()<=0 || hot_flag == "null"){
					hot_flag = "0";
				}
				forumTopic.setHot_flag(Integer.parseInt(hot_flag));
				if(view_num==null || view_num.isEmpty() || view_num.length()<=0 || view_num == "null"){
					view_num = "0";
				}
				forumTopic.setView_num(Integer.parseInt(view_num));
				forumTopic.setMeadia_files_json(media_files_json);
				forumTopic.setObject_type(Integer.parseInt(topic_type));
				String object_json = null;
				try {
					object_json = jsonObject.getJSONArray("objectData").toString();
				} catch (Exception e) {
//					Loger.i("TEST", "(E)getTopicComm->"+e.getMessage());
					e.printStackTrace();
				}
				forumTopic.setObject_data(object_json);
				NewPushRecordAbsActivity.forumtopicLists.add(forumTopic);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Loger.i("TEST", "ERROR->"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private void userLogOff(Context context){
		//注销环信账户
		EMChatManager.getInstance().logout();
		//推送功能注销
		JPushInterface.stopPush(context);
		MainActivity.currentcity="未设置";
		MainActivity.currentvillage = "未设置";
		MainActivity.sMainInitData = 0;
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
		saveSharePrefrence(null,null,null,null,null,null,null);
		// TODO Auto-generated method stub
		AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(context);
		allFamilyDaoDBImpl.deleteAllObject();
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(context);
		accountDaoDBImpl.deleteAllObjects();
//		PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(context);
//		daoDBImpl.deleteAllObjects();
		String filePath = "/data/data/" + context.getPackageName().toString()
				+ "/shared_prefs";
		String fileName1 = App.SMS_VERIFICATION_USER + ".xml";
		String fileName2 = App.VIBRATION + ".xml";
		String fileName3 = App.VOICE + ".xml";
		String fileName4 = App.ADDRESSOCCUP + ".xml";
		CommonTools commonTools = new CommonTools(context);
		commonTools.delTargetFile(filePath, fileName1);
		commonTools.delTargetFile(filePath, fileName2);
		commonTools.delTargetFile(filePath, fileName3);
		commonTools.delTargetFile(filePath, fileName4);
	}
	
	private void saveSharePrefrence(String city, String village,String detail,String familyid,
			String fimalycommunityid,String blockid,String username){
		try {
			Editor sharedata = context.getApplicationContext().getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
			sharedata.putString("city", city);
			sharedata.putString("village", village);
			sharedata.putString("detail", detail);
			sharedata.putString("familyid", familyid);
			sharedata.putString("familycommunityid", fimalycommunityid);
			sharedata.putString("blockid", blockid);
			sharedata.putString("username", username);
			sharedata.commit();
		} catch (Exception e) {
			Loger.i(TAG, "Exception-ERROR=>"+e.getMessage());
			e.printStackTrace();
		}
	}
	public int getPushSuccessTag(){
		SharedPreferences preferences=context.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		int res=preferences.getInt("address_tag", 0);
		return res;
	}
	public void savePushSuccess(int tag){
		Editor editor=context.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE).edit();
		editor.putInt("address_tag", tag);
		editor.commit();
	}
	private String getVersion() {
		String st = "1.0";
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}
}

//new Thread(new Runnable() {
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		userLogOff(context);
//		try {
//			int currentVersion = android.os.Build.VERSION.SDK_INT; 
//            if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) { 
//                Intent startMain = new Intent(Intent.ACTION_MAIN); 
//                startMain.addCategory(Intent.CATEGORY_HOME); 
//                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//                context.startActivity(startMain);
//                try {
//                	EMChatManager.getInstance().logout();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//                MainActivity.sMainInitData = 0;
//                MainActivity.sMainActivity.finish();
//                System.exit(0); 
//            } else {// android2.1 
//                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 
//                am.restartPackage(context.getPackageName()); 
//            } 
//		} catch (Exception e) {
//			Loger.i(TAG, "3 == contentTypeERROR==>"+e.getMessage());
//			e.printStackTrace();
//		}
//	}
//}).start();

