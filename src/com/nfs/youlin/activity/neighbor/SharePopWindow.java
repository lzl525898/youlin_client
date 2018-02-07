package com.nfs.youlin.activity.neighbor;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import u.aly.bu;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.find.NewsShareCompileChange;
import com.nfs.youlin.activity.personal.CollectionActivity;
import com.nfs.youlin.activity.personal.MyPushActivity;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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

public class SharePopWindow {
	private Context context;
	private LinearLayout layout;
	private ListView sharePopLv;
	private PopupWindow popupShareWindow;
	private View popupShareView;
	private LinearLayout topLinearLayout;
	private RelativeLayout shareRrecordLayout;
	private ImageView contactsImageView;
	private ImageView tengxunImageView;
	private ImageView weixinImageView;
	private String shareInformation = "测试分享信息，测试分享信息，测试分享信息，测试分享信息！";
	private String[] arr;
	public static final int CHATTYPE_SINGLE = 1;
	private String flag = "none";
	private boolean cRequestSet = false;
	private int collectTag;
	private ForumTopic forumTopic;
	private int parentclass;
	private int listposition;
	public SharePopWindow(final Context context,final int parent,int myTopic,final long topic_id,final long community_id,final Long sender_id,
			final String display_name,final String sender_portrait,final int type,final ForumTopic forumTopic,final int listposition,final int parentclass){
		Loger.i("youlin","myTopic---->"+myTopic);
		this.context=context;
		this.collectTag=forumTopic.getSender_nc_role();
		this.forumTopic=forumTopic;
		this.parentclass=parentclass;
		this.listposition=listposition;
		List<String> list = new ArrayList<String>();
		list.add(0, "aaaaa");
		final PopupWindow popWindow=new PopupWindow(context);
		View view = ((Activity)context).getLayoutInflater().inflate(R.layout.activity_share_popwindow, null);
		popupShareView =((Activity)context).getLayoutInflater().inflate(R.layout.popup_personal_share, null);
		topLinearLayout = (LinearLayout) popupShareView.findViewById(R.id.ll_top_share);
		shareRrecordLayout = (RelativeLayout) popupShareView.findViewById(R.id.ll_popup_share_record);
		contactsImageView = (ImageView) popupShareView.findViewById(R.id.iv_popup_personal_share_contact);
		tengxunImageView = (ImageView) popupShareView.findViewById(R.id.iv_popup_personal_share_tengxun);
		weixinImageView = (ImageView) popupShareView.findViewById(R.id.iv_popup_personal_share_weixin);
		layout=(LinearLayout)view.findViewById(R.id.share_pop_layout);
		sharePopLv=(ListView)view.findViewById(R.id.share_pop_lv);
		if(parent == 3 || parent==4 || parent==5){
			if(myTopic==1){
				if(collectTag==3){
					arr=new String[]{"修改","取消收藏"};
				}else{
					arr=new String[]{"修改","收藏"};
				}
			}else{
				if(collectTag==3){
					arr=new String[]{"私信","取消收藏"};
				}else{
					arr=new String[]{"私信","收藏"};
				}
			}
		}else if(myTopic==1){
			if(collectTag==3){
				arr=new String[]{"修改","取消收藏"};
			}else{
				arr=new String[]{"修改","收藏"};
			}
		}else if(sender_id==1){
			if(collectTag==3){
				arr=new String[]{"取消收藏"};
			}else{
				arr=new String[]{"收藏"};
			}
		}else{
			if(collectTag==3){
				arr=new String[]{"私信","举报","取消收藏"};
			}else{
				arr=new String[]{"私信","举报","收藏"};
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
		popWindow.showAsDropDown(CircleDetailActivity.morelayout,0,(((Activity)context).getActionBar().getHeight()-CircleDetailActivity.morelayout.getHeight())/2+2);
		sharePopLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(NetworkService.networkBool){				
				if(arr[arg2].equals("修改")){
					AllFamilyDaoDBImpl curfamilyDaoDBImpl=new AllFamilyDaoDBImpl(context);
					if(curfamilyDaoDBImpl.getCurrentAddrDetail("132").getEntity_type()==1){
					if(type==0){
						if(parent==3){
							Intent intent=new Intent(context,PropertyGonggaoAddChange.class);
							Bundle bundle=new Bundle();
							bundle.putLong("topic_id",forumTopic.getTopic_id());
							bundle.putLong("forum_id", forumTopic.getForum_id());
							bundle.putString("forum_name", forumTopic.getForum_name());
							bundle.putLong("topic_category_type", forumTopic.getTopic_category_type());
							bundle.putLong("sender_id", forumTopic.getSender_id());
							bundle.putString("sender_name", forumTopic.getSender_name());
							bundle.putString("sender_lever", forumTopic.getSender_lever());
							bundle.putString("sender_portrait", forumTopic.getSender_portrait());
							bundle.putLong("sender_family_id", forumTopic.getSender_family_id());
							bundle.putString("sender_family_address", forumTopic.getSender_family_address());
							bundle.putLong("sender_nc_role", forumTopic.getSender_nc_role());
							bundle.putString("display_name", forumTopic.getDisplay_name());
							bundle.putLong("object_data_id", forumTopic.getObject_type());
							bundle.putLong("circle_type", forumTopic.getCircle_type());
							bundle.putString("topic_title", forumTopic.getTopic_title());
							bundle.putString("topic_content", forumTopic.getTopic_content());
							intent.putExtra("bundle", bundle);
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							context.startActivity(intent);
						}else if(parent==5){
							Intent intent=new Intent(context,PropertyAdviceAddChange.class);
							Bundle bundle=new Bundle();
							bundle.putLong("topic_id",forumTopic.getTopic_id());
							bundle.putLong("forum_id", forumTopic.getForum_id());
							bundle.putString("forum_name", forumTopic.getForum_name());
							bundle.putLong("topic_category_type", forumTopic.getTopic_category_type());
							bundle.putLong("sender_id", forumTopic.getSender_id());
							bundle.putString("sender_name", forumTopic.getSender_name());
							bundle.putString("sender_lever", forumTopic.getSender_lever());
							bundle.putString("sender_portrait", forumTopic.getSender_portrait());
							bundle.putLong("sender_family_id", forumTopic.getSender_family_id());
							bundle.putString("sender_family_address", forumTopic.getSender_family_address());
							bundle.putLong("sender_nc_role", forumTopic.getSender_nc_role());
							bundle.putString("display_name", forumTopic.getDisplay_name());
							bundle.putLong("object_data_id", forumTopic.getObject_type());
							bundle.putLong("circle_type", forumTopic.getCircle_type());
							bundle.putString("topic_title", forumTopic.getTopic_title());
							bundle.putString("topic_content", forumTopic.getTopic_content());
							bundle.putLong("topic_time", forumTopic.getTopic_time());
							intent.putExtra("bundle", bundle);
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							context.startActivity(intent);
						}else{
						Intent intent=new Intent(context,NewTopicChange.class);
						Bundle bundle=new Bundle();
						bundle.putLong("topic_id",forumTopic.getTopic_id());
						bundle.putLong("forum_id", forumTopic.getForum_id());
						bundle.putString("forum_name", forumTopic.getForum_name());
						bundle.putLong("topic_category_type", forumTopic.getTopic_category_type());
						bundle.putLong("sender_id", forumTopic.getSender_id());
						bundle.putString("sender_name", forumTopic.getSender_name());
						bundle.putString("sender_lever", forumTopic.getSender_lever());
						bundle.putString("sender_portrait", forumTopic.getSender_portrait());
						bundle.putLong("sender_family_id", forumTopic.getSender_family_id());
						bundle.putString("sender_family_address", forumTopic.getSender_family_address());
						bundle.putLong("sender_nc_role", forumTopic.getSender_nc_role());
						bundle.putString("display_name", forumTopic.getDisplay_name());
						bundle.putLong("object_data_id", forumTopic.getObject_type());
						bundle.putLong("circle_type", forumTopic.getCircle_type());
						bundle.putString("topic_title", forumTopic.getTopic_title());
						bundle.putString("topic_content", forumTopic.getTopic_content());
						bundle.putLong("sender_community_id", forumTopic.getSender_community_id());
						bundle.putLong("topic_time", forumTopic.getTopic_time());
						intent.putExtra("bundle", bundle);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						context.startActivity(intent);
						}
					}else if(type==1){
						Intent intent=new Intent(context,SendActivityChange.class);
						Bundle bundle=new Bundle();
						bundle.putLong("topic_id",forumTopic.getTopic_id());
						bundle.putLong("forum_id", forumTopic.getForum_id());
						bundle.putString("forum_name", forumTopic.getForum_name());
						bundle.putLong("topic_category_type", forumTopic.getTopic_category_type());
						bundle.putLong("sender_id", forumTopic.getSender_id());
						bundle.putString("sender_name", forumTopic.getSender_name());
						bundle.putString("sender_lever", forumTopic.getSender_lever());
						bundle.putString("sender_portrait", forumTopic.getSender_portrait());
						bundle.putLong("sender_family_id", forumTopic.getSender_family_id());
						bundle.putString("sender_family_address", forumTopic.getSender_family_address());
						bundle.putLong("sender_nc_role", forumTopic.getSender_nc_role());
						bundle.putString("display_name", forumTopic.getDisplay_name());
						bundle.putString("topic_title", forumTopic.getTopic_title());
						bundle.putString("topic_content", forumTopic.getTopic_content());
						bundle.putLong("topic_time", forumTopic.getTopic_time());
						bundle.putString("object_data", forumTopic.getObject_data());
						intent.putExtra("bundle", bundle);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						context.startActivity(intent);
					}else if(type==3){
						Intent intent=new Intent(context,NewsShareCompileChange.class);
						Bundle bundle=new Bundle();
						bundle.putLong("topic_id",forumTopic.getTopic_id());
						bundle.putLong("forum_id", forumTopic.getForum_id());
						bundle.putString("forum_name", forumTopic.getForum_name());
						bundle.putLong("topic_category_type", forumTopic.getTopic_category_type());
						bundle.putLong("sender_id", forumTopic.getSender_id());
						bundle.putString("sender_name", forumTopic.getSender_name());
						bundle.putString("sender_lever", forumTopic.getSender_lever());
						bundle.putString("sender_portrait", forumTopic.getSender_portrait());
						bundle.putLong("sender_family_id", forumTopic.getSender_family_id());
						bundle.putString("sender_family_address", forumTopic.getSender_family_address());
						bundle.putLong("sender_nc_role", forumTopic.getSender_nc_role());
						bundle.putString("display_name", forumTopic.getDisplay_name());
						bundle.putLong("object_data_id", forumTopic.getObject_type());
						bundle.putLong("circle_type", forumTopic.getCircle_type());
						bundle.putString("topic_title", forumTopic.getTopic_title());
						bundle.putString("topic_content", forumTopic.getTopic_content());
						String newsId="0";
						String newsPicUrl="null";
						String newsTitle="null";
						try {
							JSONArray jsonArray=new JSONArray(forumTopic.getObject_data());
							Loger.i("TEST", "3333333333333333333333333333---->"+jsonArray.toString());
							JSONObject jsonObject=new JSONObject(jsonArray.getString(0));
							newsId = jsonObject.getString("new_id");
							newsPicUrl = jsonObject.getString("new_small_pic");
							newsTitle = jsonObject.getString("new_title");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						bundle.putString("news_id", newsId);
						bundle.putString("news_picurl",newsPicUrl);
						bundle.putString("news_title",newsTitle);
						bundle.putLong("topic_time", forumTopic.getTopic_time());
						bundle.putInt("send_status", forumTopic.getSend_status());
						intent.putExtra("bundle", bundle);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						context.startActivity(intent);
					}
					}else{
						Toast.makeText(context, "您的地址还未经过验证", Toast.LENGTH_SHORT).show();
					}
				}
				if(arr[arg2].equals("私信")){
					AccountDaoDBImpl account = new AccountDaoDBImpl(context);
					Intent intent = new Intent(context, com.easemob.chatuidemo.activity.ChatActivity.class);
			        intent.putExtra("userId",String.valueOf(sender_id));
			        intent.putExtra("chatType", CHATTYPE_SINGLE);
			        intent.putExtra("usernick",display_name);
			        String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
			        intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
			        intent.putExtra("neighborurl", sender_portrait);
			        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			        context.startActivity(intent);
				}
				if(arr[arg2].equals("举报")){
					Bundle bundle=new Bundle();
					bundle.putLong("topic_id", topic_id);
					bundle.putLong("community_id", community_id);
					bundle.putLong("sender_id", sender_id);
					context.startActivity(new Intent(context,ReportActivity.class).putExtra("ID", bundle).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				}
				if(arr[arg2].equals("收藏")){
					   gethttpdata(App.sUserLoginId,community_id, topic_id,IHttpRequestUtils.YOULIN, 1,forumTopic.getSender_id());
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
					gethttpdata2(App.sUserLoginId,community_id, topic_id,IHttpRequestUtils.YOULIN, 1);
				}
				}else{
					Toast.makeText(context, "网络有问题", Toast.LENGTH_SHORT).show();
				}
				popWindow.dismiss();
			}	
		});
		contactsImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "分享中...",Toast.LENGTH_SHORT).show();
				Uri smsToUri = Uri.parse("smsto:");
				Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
				sendIntent.putExtra( "sms_body", shareInformation);  
			    sendIntent.setType( "vnd.android-dir/mms-sms" ); 
			    context.startActivity(sendIntent);
				popupShareWindow.dismiss();
			}
		});
		weixinImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isApkInstalled(context,"com.tencent.mm")) {
					Toast.makeText(context, "手机未发现有微信，请先安装!",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(context, "分享中...",
							Toast.LENGTH_SHORT).show();
					initShareIntent("com.tencent.mm");
				}
				popupShareWindow.dismiss();
			}
		});
		tengxunImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isApkInstalled(context, "com.tencent.mobileqq")) {
					Toast.makeText(context, "手机未发现有QQ，请先安装!",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(context, "分享中...",
							Toast.LENGTH_SHORT).show();
					initShareIntent("tencent.mobileqq");
				}
				popupShareWindow.dismiss();
			}
		});
		popupShareView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupShareWindow.dismiss();
			}
		});

		shareRrecordLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "查看分享", Toast.LENGTH_LONG)
						.show();
			}
		});
	}
	private void setPopupWindow() {
		popupShareWindow = new PopupWindow(popupShareView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ColorDrawable cd = new ColorDrawable(0x90000000);
		popupShareWindow.setBackgroundDrawable(cd);
		TranslateAnimation anim = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 1f,
				Animation.RELATIVE_TO_PARENT, 0f);
		popupShareWindow.setFocusable(true);
		anim.setDuration(500);
		topLinearLayout.setAnimation(anim);
		popupShareWindow.showAtLocation(((Activity)context).getWindow()
				.getDecorView(), Gravity.BOTTOM, 0, 0);
		popupShareWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
	}
	@SuppressLint("DefaultLocale")
	private void initShareIntent(String type) {
		boolean found = false;
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		// gets the list of intents that can be loaded.
		List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(
				share, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains(type)
						|| info.activityInfo.name.toLowerCase().contains(type)) {
					share.putExtra(Intent.EXTRA_TEXT, shareInformation);
					share.setPackage(info.activityInfo.packageName);
					found = true;
					break;
				}
			}
			if (!found)
				return;
			context.startActivity(share);
		}
	}

	private boolean isApkInstalled(Context context, String packageName) {
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
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
	private String getAddrDetail(){
		SharedPreferences sharedata = context.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String addrDetail = null;
		String city = sharedata.getString("city", null);
		String village = sharedata.getString("village", null);
		String detail = sharedata.getString("detail", null);
		if(city!=null && village!=null && detail!=null){
			addrDetail = city+village+detail;
		}
		return addrDetail;
	}
}
