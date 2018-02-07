package com.nfs.youlin.activity.neighbor;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import u.aly.bu;

import com.baidu.platform.comapi.map.B;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.find.NewsShareCompileChange;
import com.nfs.youlin.activity.personal.CollectionActivity;
import com.nfs.youlin.activity.personal.MyPushActivity;
import com.nfs.youlin.activity.titlebar.barter.BarterDedailActivity;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopicChange;
import com.nfs.youlin.activity.titlebar.startactivity.SendActivity;
import com.nfs.youlin.activity.titlebar.startactivity.SendActivityChange;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.BackgroundAlpha;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.CustomDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

public class SharePopWindowBarter {
	private Context context;
	private ListView sharePopLv;
	private String[] arr;
	public static final int CHATTYPE_SINGLE = 1;
	private String flag = "none";
	private boolean cRequestSet = false;
	private ForumTopic forumTopic;
	private int parentclass;
	private int listposition;
	private long sendId;
	private long topicId;
	private long communityId;
	private int collectTag;
	public SharePopWindowBarter(final Context context, ForumTopic forumTopic,final int parentclass,final int listposition){
		this.context=context;
		this.forumTopic=forumTopic;
		this.parentclass=parentclass;
		this.listposition=listposition;
		this.sendId=forumTopic.getSender_id();
		this.topicId=forumTopic.getTopic_id();
		this.communityId=forumTopic.getSender_community_id();
		this.collectTag=forumTopic.getSender_nc_role();
		final PopupWindow popWindow=new PopupWindow(context);
		View view = ((Activity)context).getLayoutInflater().inflate(R.layout.activity_share_popwindow, null);
		sharePopLv=(ListView)view.findViewById(R.id.share_pop_lv);
		int type = App.sUserType;
		if(sendId==App.sUserLoginId){
			if(collectTag==3){
				arr=new String[]{"取消收藏","删除"};
			}else{
				arr=new String[]{"收藏","删除"};
			}
		}else if(type==2||type==6){
			if(collectTag==3){
				arr=new String[]{"举报","取消收藏","删除"};
			}else{
				arr=new String[]{"举报","收藏","删除"};
			}
		}else{
			if(collectTag==3){
				arr=new String[]{"举报","取消收藏"};
			}else{
				arr=new String[]{"举报","收藏"};
			}
		}
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,R.layout.activity_share_pop_textview,arr);
		sharePopLv.setAdapter(adapter);
		popWindow.setWidth(LayoutParams.WRAP_CONTENT);
		popWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);
		popWindow.setContentView(view);
		//popWindow.showAtLocation(((Activity)context).getWindow().getDecorView(), Gravity.RIGHT|Gravity.TOP,20, 230);
		popWindow.showAsDropDown(BarterDedailActivity.morelayout,0,(((Activity)context).getActionBar().getHeight()-BarterDedailActivity.morelayout.getHeight())/2+2);
		sharePopLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(NetworkService.networkBool){				
				if(arr[arg2].equals("举报")){
					Bundle bundle=new Bundle();
					bundle.putLong("topic_id", topicId);
					bundle.putLong("community_id", communityId);
					bundle.putLong("sender_id", sendId);
					context.startActivity(new Intent(context,ReportActivity.class).putExtra("ID", bundle).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				}
				if(arr[arg2].equals("收藏")){
					   gethttpdata(App.sUserLoginId,communityId, topicId,IHttpRequestUtils.YOULIN, 1,sendId);
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... params) {
								// 网络请求 注册地址 参数{city,village,rooft,number} login_account
								// while(cRequestSet){;}
								Long currenttime = System.currentTimeMillis();
								while (!cRequestSet) {
									if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
										cRequestSet = true;
									}
								}
								return null;
							}

							@Override
							protected void onPostExecute(Void result) {
								super.onPostExecute(result);
								if (cRequestSet && flag != null && flag.equals("ok")) {
									cRequestSet = false;
									flag = "none";
									//Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
								}else{
									//Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT).show();
								}
							}
						}.execute();
				}
				if(arr[arg2].equals("取消收藏")){
					gethttpdata2(App.sUserLoginId,communityId, topicId,IHttpRequestUtils.YOULIN, 1);
				}
				if(arr[arg2].equals("删除")){
					CustomDialog.Builder builder = new CustomDialog.Builder(context);
//					if(forumTopic.getObject_type()==0){
//						builder.setTitle("删除话题");
//					}else if(forumTopic.getObject_type()==1){
//						builder.setTitle("删除活动");
//					}
					builder.setMessage("确定要删除该内容吗？");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							final ForumtopicDaoDBImpl forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(context);
							RequestParams delParams = new RequestParams();
							delParams.put("user_id",App.sUserLoginId);
							delParams.put("community_id", communityId);
							delParams.put("topic_id", topicId);
							delParams.put("tag", "deltopic");
							delParams.put("apitype", IHttpRequestUtils.APITYPE[5]);
							AsyncHttpClient delRequest = new AsyncHttpClient();
							delRequest.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
									delParams, new JsonHttpResponseHandler(){
								public void onSuccess(int statusCode, Header[] headers,
										JSONObject response) {
									try {
										JSONObject jsonObject = new JSONObject(response.toString());
									    String delFlag = jsonObject.getString("flag");
									    if("ok".equals(delFlag)){
									    	forumtopicDaoDBImplObj.deleteObject(topicId);
									    	Loger.i("TEST", "删除帖子成功");
									    	Intent intent = new Intent();  
											intent.setAction("youlin.delete.topic.action");
											intent.putExtra("ID", listposition);
											intent.putExtra("type", parentclass);
											context.sendBroadcast(intent);  
											((Activity)context).finish();
									    }else{
									    	Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
									    }
										dialog.dismiss();
									} catch (NumberFormatException e) {
										e.printStackTrace();
									} catch (JSONException e) {
										e.printStackTrace();
									}
									super.onSuccess(statusCode, headers, response);
								}
								@Override
								public void onFailure(int statusCode, Header[] headers,
										String responseString, Throwable throwable) {
									Loger.i("TEST", responseString);
									super.onFailure(statusCode, headers, responseString, throwable);
								}
							});	
						}
					});
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
				}
				}else{
					Toast.makeText(context, "网络有问题", Toast.LENGTH_SHORT).show();
				}
				popWindow.dismiss();
			}	
		});
		
	}
	private void gethttpdata(long user_id,long community_id,long topic_id ,String http_addr,int request_index,long sender_id){
		/***********************************************/
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		final int position = request_index;
		Loger.d("test3", "3333333333333333333333");
		Loger.d("test3", "user_id="+user_id+"community_id="+community_id+"topic_id="+topic_id);
			if(request_index ==1){
					params.put("user_id", user_id);
					params.put("community_id", community_id);
					params.put("topic_id", topic_id);
					params.put("tag", "addcol");
					params.put("apitype", IHttpRequestUtils.APITYPE[5]);
					params.put("sender_id", sender_id);
			}
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test3", "get mypush flag->" + flag);
					if(flag.equals("ok")){
						forumTopic.setSender_nc_role(3);
						if(parentclass==0){
							TitleBarSearchActivity.forumtopicLists.set(listposition, forumTopic);
						}else if(parentclass==1){
							FriendCircleFragment.allList.set(listposition, forumTopic);
						}else if(parentclass==2){
							MyPushActivity.forumtopicLists.set(listposition, forumTopic);
						}else if(parentclass==6){ //(parentclass==5/从细节进来的
							PropertyGonggaoActivity.forumtopicLists.set(listposition, forumTopic);
						}else if(parentclass==7){ //(parentclass==5/从细节进来的
							PropertyAdviceActivity.forumtopicLists.set(listposition, forumTopic);
						}
						Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
						cRequestSet = true;
					}else if(flag.equals("none")){
						Toast.makeText(context, "该帖子已不可见", 0).show();
						ForumtopicDaoDBImpl forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(context);
						forumtopicDaoDBImplObj.deleteObject(forumTopic.getTopic_id());
						Loger.i("TEST", "删除帖子成功");
						Intent intent = new Intent();  
						intent.setAction("youlin.delete.topic.action");
						intent.putExtra("ID", listposition);
						intent.putExtra("type", parentclass);
						context.sendBroadcast(intent); 
						((Activity)context).finish();
					}else{
						Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT).show();
						cRequestSet = false;
					}

				} catch (org.json.JSONException e) {
					e.printStackTrace();
					cRequestSet = false;
					Loger.i("test3","JSONObject->"+ e.getMessage());
				}
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
				org.json.JSONArray jsonContext = response;
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					cRequestSet = false;
					new ErrorServer(context, responseString.toString());
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});
	}
	private void gethttpdata2(long user_id,long community_id,long topic_id ,String http_addr,int request_index){
		/***********************************************/
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		final int position = request_index;
		Loger.d("test3", "user_id="+user_id+"community_id="+community_id+"topic_id="+topic_id);
			if(request_index ==1){
					params.put("user_id", user_id);
					params.put("community_id", community_id);
					params.put("topic_id", topic_id);
					params.put("tag", "delcol");
					params.put("apitype", IHttpRequestUtils.APITYPE[5]);
			}
		client.post(IHttpRequestUtils.URL+http_addr,params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test3", "get mypush flag->" + flag);
					if(flag.equals("ok")){
						forumTopic.setSender_nc_role(0);
						if(parentclass==0){
							TitleBarSearchActivity.forumtopicLists.set(listposition, forumTopic);
						}else if(parentclass==1){
							FriendCircleFragment.allList.set(listposition, forumTopic);
						}else if(parentclass==2){
							MyPushActivity.forumtopicLists.set(listposition, forumTopic);
						}else if(parentclass==3){
							CollectionActivity.forumtopicLists.set(listposition, forumTopic);
							((Activity)context).finish();
							CollectionActivity.forumtopicLists.remove(listposition);
							CollectionActivity.maddapter.notifyDataSetChanged();
						}else if(parentclass==6){ //(parentclass==5/从细节进来的
							PropertyGonggaoActivity.forumtopicLists.set(listposition, forumTopic);
						}else if(parentclass==7){ //(parentclass==5/从细节进来的
							PropertyAdviceActivity.forumtopicLists.set(listposition, forumTopic);
						}
						Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
					}else if(flag.equals("none")){
						Toast.makeText(context, "该帖子已不可见", 0).show();
						ForumtopicDaoDBImpl forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(context);
						forumtopicDaoDBImplObj.deleteObject(forumTopic.getTopic_id());
						Loger.i("TEST", "删除帖子成功");
						Intent intent = new Intent();  
						intent.setAction("youlin.delete.topic.action");
						intent.putExtra("ID", listposition);
						intent.putExtra("type", parentclass);
						context.sendBroadcast(intent); 
						((Activity)context).finish();
					}else{
						Toast.makeText(context, "取消收藏失败", Toast.LENGTH_SHORT).show();
					}

				} catch (org.json.JSONException e) {
					e.printStackTrace();
					Toast.makeText(context, "取消收藏失败", Toast.LENGTH_SHORT).show();
					Loger.i("test3","JSONObject->"+ e.getMessage());
				}
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
				org.json.JSONArray jsonContext = response;
			}
				@Override
				public void onFailure(int statusCode,org.apache.http.Header[] headers,String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					new ErrorServer(context, responseString.toString());
					super.onFailure(statusCode, headers,responseString, throwable);
				}
			});
	}

}
