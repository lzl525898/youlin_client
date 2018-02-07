package com.nfs.youlin.activity.personal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.find.FindFragment;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoActivity;
import com.nfs.youlin.adapter.ContentlistWithHead;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.adapter.PopupMenuAddAdapter;
import com.nfs.youlin.adapter.searchmodeadapter;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.PopupMenuAddListView;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;


public class MyPushActivity extends FragmentActivity implements OnRefreshListener<ListView>{
	private ActionBar actionBar;
	public  static List<Object> forumtopicLists = new ArrayList<Object>();
	private final  int REFUSE_CODE            = 2003;
	private final static int INIT_FAILED_CODE       = 2005;
	private final static int NOINFO_SUCCESS_CODE    = 2008;
	public static FriendCircleAdapter maddapter ;
	private ListView friendCircleLv;
	public  ListView actualListView;
	private PullToRefreshListView mPullRefreshListView;
	private boolean bPullUpStatus = false;
	private TextView nonemsgtext;
	private LinearLayout initHavenLayout;
	private String Flag = "none";
	private boolean cRequesttype = false;
	int updatePosition;
	int type;//0 我发的 1他发的
	long sendId;
	private BroadcastReceiver broadcastReceiver =new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("youlin.update.topic.action")){
				String topicId=intent.getStringExtra("topic_id");
				for (int i = 0; i < forumtopicLists.size(); i++) {
					if(String.valueOf(((ForumTopic)forumtopicLists.get(i)).getTopic_id()).equals(topicId)){
						updatePosition=i;
						gethttpdata(Long.parseLong(topicId),1);
					}
				}
			}
		}
	};
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter intentFilter=new IntentFilter("youlin.update.topic.action");
		registerReceiver(broadcastReceiver, intentFilter);
		JPushInterface.onResume(MyPushActivity.this);
		MobclickAgent.onResume(this);
		try {
//			maddapter.notifyDataSetChanged();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.friend_circle_fragment_mypush);
//		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_search_item);
		type=getIntent().getIntExtra("type", 0);
		sendId=getIntent().getLongExtra("sender_id", 0);
		if(type==0){
			setTitle("我发的");
		}else{
			setTitle("他发的");
		}
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		forumtopicLists.clear();
		nonemsgtext=(TextView)findViewById(R.id.friend_circle_noinfo_tvmypush);
		initHavenLayout=(LinearLayout)findViewById(R.id.my_push_layout); 
		maddapter = new FriendCircleAdapter(forumtopicLists,MyPushActivity.this,1,2);
	
//			pd1.show();
			forumtopicLists.clear();
			RequestParams reference = new RequestParams();
			if(type==0){
				reference.put("community_id", App.sFamilyCommunityId);
				reference.put("user_id", App.sUserLoginId);
			}else if(type==1){
				reference.put("community_id", App.sFamilyCommunityId);
				reference.put("user_id", sendId);
			}
			reference.put("count", 6);	
			reference.put("tag", "mytopic");
			reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
			Loger.d("test5", "community_id="+App.sFamilyCommunityId+"user_id="+sendId);
			//reference.put("key_word",searchmessage);
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
					reference, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONArray response) {
					// TODO Auto-generated method stub 18945051215
//					Loger.i(tag, msg);
					getTopicDetailsInfos(response);
					Flag = "ok";
					cRequesttype = true;
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
					Message message = new Message();
					try {
						
						Flag = response.getString("flag");
						if("no".equals(Flag)){
//							pd1.dismiss();
							cRequesttype = true;
							initHavenLayout.setVisibility(View.VISIBLE);
							Loger.i("TEST", "topic is empty yes");
						}else{
							Loger.i("TEST", "topic is empty no");
//							pd1.dismiss();
						}
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
					new ErrorServer(MyPushActivity.this, responseString.toString());
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		
		/*********************refresh*********************/
		FragmentManager manager  = this.getSupportFragmentManager();
		FragmentTransaction tx = manager.beginTransaction();
		tx.replace(R.id.tabcontent5,new PullToRefreshListFragment(),"myfragment");
		tx.commit();
		
        // TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				try {
					Long currenttime = System.currentTimeMillis();
					while (MyPushActivity.this.getSupportFragmentManager().findFragmentByTag(
							"myfragment") == null) {
					}
					while(!cRequesttype ){
						if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
							cRequesttype = true;
						}
					}
					MyPushActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Loger.d("test5", "cRequesttype="+cRequesttype+"Flag="+Flag);
							if(cRequesttype&&Flag!=null&& (Flag.equals("ok")||Flag.equals("no"))){
								cRequesttype = false;
								Flag="none";
								Loger.d("test5", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
								
								//Loger.i("youlin","11111111111111111->"+((ForumTopic)forumtopicLists.get(0)).getSender_id()+"  "+App.sUserLoginId);
							}
							final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) MyPushActivity.this.getSupportFragmentManager()
									.findFragmentByTag("myfragment");
							try {
								mPullRefreshListView = finalfrag9.getPullToRefreshListView();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								return;
							}
							// Set a listener to be invoked when the list should be
							// refreshed.
							mPullRefreshListView.setMode(Mode.PULL_FROM_END);
							mPullRefreshListView.setOnRefreshListener(MyPushActivity.this);
							// You can also just use
							// mPullRefreshListFragment.getListView()
							actualListView = mPullRefreshListView.getRefreshableView();
							// TODO Auto-generated method stub
							actualListView.setDividerHeight(100);
							actualListView.setDivider(MyPushActivity.this.getResources().getDrawable(R.drawable.fengexian));
							actualListView.setAdapter(maddapter);
							actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//							mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
//								@Override
//								public void onLastItemVisible() {
//									// TODO Auto-generated method stub
//									Toast.makeText(MyPushActivity.this, "1111111111111111", 0).show();
//								}
//							});
//							actualListView.setOnScrollListener(new OnScrollListener() {
//								@Override
//								public void onScrollStateChanged(AbsListView view, int scrollState) {
//									// TODO Auto-generated method stub
//									if(scrollState==OnScrollListener.SCROLL_STATE_IDLE){
//										if(view.getLastVisiblePosition()==view.getCount()-1){
//											Toast.makeText(MyPushActivity.this, "22222222222222", 0).show();
//										}
//									}
//								}
//
//								@Override
//								public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
//										int totalItemCount) {
//									// TODO Auto-generated method stub
//									
//								}
//							});
							finalfrag9.setListShown(true);
//							pd1.dismiss();	
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		}).start();
		
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		
//		return super.onCreateOptionsMenu(menu);
//	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(android.R.id.home == item.getItemId()){
			Loger.d("hyytest", "22222xxxxxxxx");
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void getTopicDetailsInfos(JSONArray response){
		int responseLen = response.length();
		Loger.i("TEST", "json obj length->"+responseLen);
		if(responseLen>0){
		}
		for (int i = 0; i < responseLen; i++) {
			try {
				Loger.i("TEST", "mediaJson->start");
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
				String praise_type = jsonObject.getString("praiseType");
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
				ForumTopic forumTopic = new ForumTopic(this);
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
				forumTopic.setSender_nc_role(Integer.parseInt(collectStatus));
				forumTopic.setSender_community_id(Long.parseLong(sender_community_id));
				if(comment_num==null || comment_num.isEmpty() || comment_num.length()<=0 || comment_num == "null"){
					comment_num = "0";
				}
				forumTopic.setComment_num(Integer.parseInt(comment_num));
				if(like_num==null || like_num.isEmpty() || like_num.length()<=0 || like_num == "null"){
					like_num = "0";
				}
				forumTopic.setLike_num(Integer.parseInt(like_num));
				if(praise_type==null || praise_type.isEmpty() || praise_type.length()<=0 || praise_type == "null"){
					praise_type = "0";
				}
				forumTopic.setLike_status(Integer.parseInt(praise_type));
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
				forumtopicLists.add(forumTopic);
				maddapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Loger.i("TEST", "ERROR->"+e.getMessage());
				e.printStackTrace();
			}
		}
		Loger.d("test5", "forumtopicLists size="+forumtopicLists.size()+""+((ForumTopic)forumtopicLists.get(0)).getTopic_title());
		
	}
	public void pullUpFromSrv(String search){
		RequestParams params = new RequestParams();
		if(forumtopicLists.size()>0){
			long currentTopicID = ((ForumTopic)(forumtopicLists.get(forumtopicLists.size()-1))).getTopic_id();
			Loger.i("TEST", "最后一个TopicId->"+currentTopicID);
			if(type==0){
				params.put("community_id", App.sFamilyCommunityId);
				params.put("user_id", App.sUserLoginId);
			}else if(type==1){
				params.put("community_id", App.sFamilyCommunityId);
				params.put("user_id", sendId);
			}
			params.put("count", 6);
			params.put("topic_id", currentTopicID);
			params.put("tag", "mytopic");
			params.put("apitype", IHttpRequestUtils.APITYPE[5]);
			AsyncHttpClient httpClient = new  AsyncHttpClient();
			httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
					new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONArray response) {
					// TODO Auto-generated method stub
					Loger.i("TEST",response.toString());
					getTopicDetailsInfos(response);
					bPullUpStatus = true;
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
					String flag = null;
					Message message = new Message();
					try {
						flag = response.getString("flag");
						if("no".equals(flag)){
							Loger.i("TEST", "topic is empty yes");
							message.what = NOINFO_SUCCESS_CODE;
						}else{
							Loger.i("TEST", "topic is empty no");
							message.what = INIT_FAILED_CODE;
						}
						getTopicHandler.sendMessage(message);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Loger.i("TEST", "pullUpFromSrv-ERROR->"+e.getMessage());
						message.what = INIT_FAILED_CODE;
						e.printStackTrace();
					}
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					// TODO Auto-generated method stub
					Message message = new Message();
					message.what = REFUSE_CODE;
					getTopicHandler.sendMessage(message);
					new ErrorServer(MyPushActivity.this, responseString);
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		}else{
			if(NetworkService.networkBool){
				forumtopicLists.clear();
				RequestParams reference = new RequestParams();
				if(type==0){
					reference.put("community_id", App.sFamilyCommunityId);
					reference.put("user_id", App.sUserLoginId);
				}else if(type==1){
					reference.put("community_id", App.sFamilyCommunityId);
					reference.put("user_id", sendId);
				}	
				reference.put("count", 6);	
				reference.put("tag", "mytopic");
				reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
				AsyncHttpClient client = new AsyncHttpClient();
				client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
						reference, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						// TODO Auto-generated method stub 18945051215
						getTopicDetailsInfos(response);
						bPullUpStatus = true;
//						pd1.dismiss();
						super.onSuccess(statusCode, headers, response);
					}
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						String flag = null;
						Message message = new Message();
						try {
							flag = response.getString("flag");
							if("no".equals(flag)){
								Loger.i("TEST", "topic is empty yes");
								message.what = NOINFO_SUCCESS_CODE;
							}else{
								Loger.i("TEST", "topic is empty no");
								message.what = INIT_FAILED_CODE;
							}
							getTopicHandler.sendMessage(message);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							message.what = INIT_FAILED_CODE;
							e.printStackTrace();
						}
						super.onSuccess(statusCode, headers, response);
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						Message message = new Message();
						message.what = REFUSE_CODE;
						getTopicHandler.sendMessage(message);
						new ErrorServer(MyPushActivity.this, responseString);
						super.onFailure(statusCode, headers, responseString, throwable);
					}
				});
			}
			
		}
		
	}
	private Handler getTopicHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFUSE_CODE:
				Loger.i("TEST", "服务器拒绝访问！！！！！！！！");
//				pd1.dismiss();
				break;
			case NOINFO_SUCCESS_CODE:
				bPullUpStatus = true;
				nonemsgtext.setVisibility(View.VISIBLE);
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							MyPushActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									nonemsgtext.setVisibility(View.GONE);
								}
							});
						}
					}, 1000);

				break;
			case INIT_FAILED_CODE:
				Loger.i("TEST", "第一次访问服务器失败！！！！！！！！");
//				pd1.dismiss();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (refreshView.isShownFooter()) {
			// 判断尾布局是否可见，如果可见执行上拉加载更多
			// 设置尾布局样式文字
			Loger.d("hyytest", "???????????????????????");
			mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			mPullRefreshListView.getLoadingLayoutProxy().setPullLabel("加载更多");
			mPullRefreshListView.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			if (NetworkService.networkBool) {
				pullUpFromSrv("aa");
				// 模拟加载数据线程休息3秒
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						Long currenttime = System.currentTimeMillis();
						while (!bPullUpStatus) {
							if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
								bPullUpStatus = true;
							}
						}
						Loger.i("TEST", "pullUp置位成功！！");
						bPullUpStatus = false;
						return null;
					}

					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						// 完成对下拉刷新ListView的更新操作
						maddapter.notifyDataSetChanged();
						// 将下拉视图收起
						mPullRefreshListView.onRefreshComplete();
						//actualListView.setSelection(7);
					};
				}.execute();
			}
		}
	}
	private void gethttpdata(long topicId,int request_index){
		RequestParams reference = new RequestParams();
		if(type==0){
			try {
				reference.put("community_id",App.sFamilyCommunityId);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				reference.put("community_id",0);
				e1.printStackTrace();
			}
			reference.put("user_id", App.sUserLoginId);
		}else if(type==1){
			try {
				reference.put("community_id",App.sFamilyCommunityId);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				reference.put("community_id",0);
				e1.printStackTrace();
			}
			reference.put("user_id", sendId);
		}
		reference.put("count", 1);
		reference.put("topic_id",topicId);
		reference.put("type", 2);
		reference.put("tag","gettopic");
		reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
		Loger.i("youlin", "33333333333333333--->"+App.sFamilyCommunityId+" "+App.sUserLoginId+" "+topicId);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+ IHttpRequestUtils.YOULIN,reference,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,Header[] headers,JSONArray response) {
								getTopicDetailsInfos2(response);
								super.onSuccess(statusCode,headers,response);
							}
							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
								// TODO Auto-generated method stub
							}
							@Override
							public void onFailure(int statusCode,Header[] headers,String responseString,Throwable throwable) {
								new ErrorServer(MyPushActivity.this, responseString);
								super.onFailure(statusCode,headers,responseString,throwable);
							}
						});
	}
	private void getTopicDetailsInfos2(JSONArray response){
		int responseLen = response.length();
		Loger.i("TEST", "json obj length->"+responseLen);
		if(responseLen>0){
		}
		for (int i = 0; i < responseLen; i++) {
			try {
//				Loger.i("TEST", "mediaJson->start");
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
				Loger.d("test5", "get sender_id="+sender_id);
				Loger.d("test5", "get topic_id="+topic_id);
				Loger.d("test5", "get disply_name="+disply_name);
				String praise_type = jsonObject.getString("praiseType");
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
				ForumTopic forumTopic = new ForumTopic(MyPushActivity.this);
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
				if(praise_type==null || praise_type.isEmpty() || praise_type.length()<=0 || praise_type == "null"){
					praise_type = "0";
				}
				forumTopic.setLike_status(Integer.parseInt(praise_type));
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
					e.printStackTrace();
				}
				forumTopic.setObject_data(object_json);
				forumtopicLists.set(updatePosition, forumTopic);
				maddapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Loger.i("TEST", "ERROR->"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(MyPushActivity.this);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}
}
