package com.nfs.youlin.activity.neighbor;

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
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
import com.nfs.youlin.R.id;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.adapter.RepairCircleAdapter;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ClearSelectImg;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StatusChangeListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.CustomDialog;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;

public class PropertyRepairActivity extends FragmentActivity implements OnRefreshListener<ListView>,StatusChangeListener,OnClickListener {
		private ActionBar actionBar;
		public static List<Object> forumtopicLists = new ArrayList<Object>();
//		private final static int DOWN_SUCCESS_CODE      = 2000;
//		private final static int UP_SUCCESS_CODE        = 2007;
//		private final static int FAILED_CODE            = 2001;
		private final static int REFUSE_CODE            = 2003;
		private final static int INIT_FAILED_CODE       = 2005;
		private final static int NOINFO_SUCCESS_CODE    = 2008;
//		private final static int FIRST_NOINFO_SUCCESS_CODE = 2009;
		public static RepairCircleAdapter maddapter ;
		private ListView friendCircleLv;
		private ListView actualListView;
		private PullToRefreshListView mPullRefreshListView;
		private boolean bPullUpStatus = false;
		private TextView nonemsgtext;
		private String Flag = "none";
		private boolean cRequesttype = false;
		private StatusChangeutils statusutils;
		private LinearLayout uncompletedbaoxiu;
		private LinearLayout completedbaoxiu;
		private TextView text1;
		private TextView text2;
		private ImageView uncompletedview;
		private ImageView completedview;
		PopupWindow popupWindow;
		String[] arr;
		private int menuSelectID;
		private String chooseflag = "0";
		private LinearLayout repairInitLayout;
		private LinearLayout repairInitBelowLayout;
		private RelativeLayout repairInitBelow2Layout;
		private TextView repairinittext;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
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
//			this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
			super.onCreate(savedInstanceState);
			this.setContentView(R.layout.activity_property_repair);
			Intent intent = getIntent();
			String repairstatus = intent.getStringExtra("repairstatus");
			repairInitLayout=(LinearLayout)findViewById(R.id.repair_init);
			repairInitBelowLayout=(LinearLayout)findViewById(R.id.repair_init_below);
			repairInitBelow2Layout=(RelativeLayout)findViewById(R.id.repair_init_below2);
			repairinittext = (TextView)findViewById(R.id.repair_init_text);
			repairinittext.setText("您家里维护的棒棒的，没有报修！");
			uncompletedbaoxiu = (LinearLayout)findViewById(R.id.uncompletedbaoxiu);
			completedbaoxiu = (LinearLayout)findViewById(R.id.completedbaoxiu);
			
			text1 = (TextView) findViewById(R.id.bxtext1);
			text2 = (TextView) findViewById(R.id.bxtext2);
			completedview = (ImageView)findViewById(R.id.completedview);
			uncompletedview = (ImageView)findViewById(R.id.uncompletedview);
			uncompletedbaoxiu.setOnClickListener(this);
			completedbaoxiu.setOnClickListener(this);
			if(repairstatus != null &&repairstatus.equals("3")){
				uncompletedbaoxiu.setTag("0");
				completedbaoxiu.setTag("1");
				text2.setTextColor(0xffffba02);
				text1.setTextColor(0xff909090);
				completedview.setBackgroundColor(0xffffba02);
				uncompletedview.setBackgroundColor(0xffa0a0a0);
			}else if(repairstatus != null){
				uncompletedbaoxiu.setTag("1");
				completedbaoxiu.setTag("0");
				text1.setTextColor(0xffffba02);
				text2.setTextColor(0xff909090);
				uncompletedview.setBackgroundColor(0xffffba02);
				completedview.setBackgroundColor(0xffa0a0a0);
			}else{
				uncompletedbaoxiu.setTag("1");
				completedbaoxiu.setTag("0");
				text1.setTextColor(0xffffba02);
				text2.setTextColor(0xff909090);
				uncompletedview.setBackgroundColor(0xffffba02);
				completedview.setBackgroundColor(0xffa0a0a0);
			}
//			this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_search_item); 
			setTitle("报修");
			actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(false);
			statusutils = new StatusChangeutils();
			statusutils.statuschangelistener("BAOXIU",this);
			forumtopicLists.clear();
			nonemsgtext =(TextView) findViewById(R.id.repair_noinfo_tv1);
				forumtopicLists.clear();
				RequestParams reference = new RequestParams();
				reference.put("community_id", App.sFamilyCommunityId);		
				reference.put("user_id", App.sUserLoginId);	
				reference.put("count", 6);	
				reference.put("category_type",App.BAOXIU_TYPE);	
				if(uncompletedbaoxiu.getTag().toString().equals("1")){
					reference.put("process_type",1);
				}else{
					reference.put("process_type",3);
				}
				reference.put("tag","getrepair");
				reference.put("apitype",IHttpRequestUtils.APITYPE[4]);
				Loger.d("test5", "community_id="+App.sFamilyCommunityId+"user_id="+App.sUserLoginId);
				//reference.put("key_word",searchmessage);
				AsyncHttpClient client = new AsyncHttpClient();
				client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
						reference, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						// TODO Auto-generated method stub 18945051215
						repairInitBelowLayout.setVisibility(View.VISIBLE);
						repairInitBelow2Layout.setVisibility(View.VISIBLE);
						getTopicDetailsInfos(response,"init");
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
							String empty = response.getString("empty");
							Loger.i("test4", "444444444--->"+empty);
							if(empty.equals("no")){
								cRequesttype = true;
								repairInitLayout.setVisibility(View.VISIBLE);
								Loger.i("test5", "baoxiutopic is empty yes");//已完成  和未完成都为空
							}else if(empty.equals("ok")){
								repairInitLayout.setVisibility(View.GONE);
								repairInitBelowLayout.setVisibility(View.VISIBLE);
								repairInitBelow2Layout.setVisibility(View.VISIBLE);
								cRequesttype = true;
								Loger.i("test5", "baoxiutopic is empty no");
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
						new ErrorServer(PropertyRepairActivity.this, responseString.toString());
						super.onFailure(statusCode, headers, responseString, throwable);
					}
				});
			/*********************refresh*********************/
			FragmentManager manager  = this.getSupportFragmentManager();
			FragmentTransaction tx = manager.beginTransaction();
			tx.replace(R.id.tabcontentrepair,new PullToRefreshListFragment(),"myfragment");
			tx.commit();
			
	        // TODO Auto-generated method stub
			new Thread(new Runnable() {
				public void run() {
					try {
						Long currenttime = System.currentTimeMillis();
						while (PropertyRepairActivity.this.getSupportFragmentManager().findFragmentByTag(
								"myfragment") == null) {
						}
						while(!cRequesttype ){
							if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME+5000){
								cRequesttype = true;
							}
						}
						PropertyRepairActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Loger.d("test5", "cRequesttype="+cRequesttype+"Flag="+Flag);
								final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) PropertyRepairActivity.this.getSupportFragmentManager()
										.findFragmentByTag("myfragment");
								if(cRequesttype && (Flag.equals("ok")||Flag.equals("no"))){
									Loger.d("test5", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
									
									try {
										mPullRefreshListView = finalfrag9.getPullToRefreshListView();
									} catch (Exception e) {
										e.printStackTrace();
										return;
									}
									// Set a listener to be invoked when the list should be
									// refreshed.
									mPullRefreshListView.setMode(Mode.PULL_FROM_END);
									mPullRefreshListView.setOnRefreshListener(PropertyRepairActivity.this);
									// You can also just use
									// mPullRefreshListFragment.getListView()
									actualListView = mPullRefreshListView.getRefreshableView();
									// TODO Auto-generated method stub
									actualListView.setDividerHeight(0);
//									actualListView.setDivider(PropertyRepairActivity.this.getResources().getDrawable(R.drawable.fengexian));
									actualListView.setAdapter(maddapter);
									actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
									actualListView.setOnItemLongClickListener(new OnItemLongClickListener() {
										@Override
										public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
												long id) {
											// TODO Auto-generated method stub
											Loger.d("test4", "baoxiu longclick");
//										
											return false;
										}
									});
									//Loger.i("youlin","11111111111111111->"+((ForumTopic)forumtopicLists.get(0)).getSender_id()+"  "+App.sUserLoginId);
								}
								cRequesttype = false;
								Flag="none";
								finalfrag9.setListShown(true);
							}
						});
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}
			}).start();
			
		}
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			int type = App.sUserType;
//			if(4 == type||5 == type||6 == type){//表示当前用户为物业管理员 //表示当前用户为物业管理员+申请管理员//表示当前用户为物业管理员+管理员
				getMenuInflater().inflate(R.menu.property_repair, menu);
				menu.getItem(0).setActionView(R.layout.property_advice_plus_layout);
				LinearLayout repairAddLayout=(LinearLayout) menu.findItem(R.id.repair_add).getActionView();
				ImageView repairAddImg=(ImageView) repairAddLayout.findViewById(R.id.advice_add);
				repairAddImg.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String addrDetails=getAddrDetail();
						Loger.i("youlin","addrDetails-->"+addrDetails);
						if(addrDetails!=null){
							AllFamilyDaoDBImpl curfamilyDaoDBImpl = new AllFamilyDaoDBImpl(PropertyRepairActivity.this);
							AllFamily currentFamily = curfamilyDaoDBImpl.getCurrentAddrDetail(addrDetails);
							Loger.i("youlin","11111111111-->"+currentFamily.getEntity_type()+"   "+currentFamily.getPrimary_flag());
							if(currentFamily.getEntity_type()==1 && currentFamily.getPrimary_flag()==1){
								startActivity(new Intent(PropertyRepairActivity.this,PropertyRepairAddActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
							}else{
								showAddDialog();
							}
						}else{
							showAddDialog();
						}
					}
				});
//			}
			return true;
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			default:
				break;
			}
			return super.onOptionsItemSelected(item);
		}
		public void showAddDialog(){
			CustomDialog.Builder builder=new CustomDialog.Builder(this);
			//builder.setTitle("提示");
			builder.setMessage("您当前的地址信息不完整或正在审核中");
			builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
		private String getAddrDetail(){
			SharedPreferences sharedata = this.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
			String addrDetail = null;
			String city = sharedata.getString("city", null);
			String village = sharedata.getString("village", null);
			String detail = sharedata.getString("detail", null);
			if(city!=null && village!=null && detail!=null){
				addrDetail = city+village+detail;
			}
			return addrDetail;
		}
		private void getTopicDetailsInfos(JSONArray response,String flag){
			int responseLen = response.length();
			Loger.i("TEST", "json obj length->"+responseLen);
			if(responseLen>0){
				
			}
			if(flag.equals("add")){
				
			}else{
				forumtopicLists.clear();
			}
			
			for (int i = 0; i < responseLen; i++) {
				try {
					Loger.i("TEST", "mediaJson->start");
					JSONObject jsonObject = new JSONObject(response.getString(i));
					String cache_key = jsonObject.getString("cacheKey");
					String topic_id = jsonObject.getString("topicId");
					String forum_id = jsonObject.getString("forumId");
					String forum_name = jsonObject.getString("forumName");
					//String circle_type = jsonObject.getString("circleType");
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
					String send_status = jsonObject.getString("sendStatus");
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
					String property_userId = jsonObject.getString("property_userId"); //用来接收物业user id
					String process_status = jsonObject.getString("process_status"); 
					
					String process_data = null;
					
					try {
						process_data = jsonObject.getString("process_data");
						Loger.d("test4", "schedule data= "+process_data);
						process_data.replace("\'", "\"");
						Loger.d("test4", "schedule data= "+process_data);
						//process_data = jsonObject.getJSONObject("process_data").toString();
					} catch (Exception e) {
						Loger.i("TEST", "(E)getTopicComm->"+e.getMessage());
						e.printStackTrace();
					}
					ForumTopic forumTopic = new ForumTopic(this);
					//forumTopic.setFlag(false);
					forumTopic.setCache_key(Integer.parseInt(cache_key));
					forumTopic.setTopic_id(Long.parseLong(topic_id));
					Loger.d("test5", "topic position="+i+"topicid="+topic_id);
					forumTopic.setForum_id(Long.parseLong(forum_id));
					forumTopic.setForum_name(forum_name);
				//	forumTopic.setCircle_type(Integer.parseInt(circle_type));
					
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
					
					Loger.d("test5", "baoxiu Topic_content="+topic_content);
					Loger.d("test5", "baoxiu 物业user id="+property_userId);
					/**********物业报修***************/
					if(property_userId==null || property_userId.isEmpty() || property_userId.length()<=0 || property_userId == "null"){
//						property_userId = "{\"xx\":\"1\"}";
						Loger.d("test5", "baoxiu 物业user id="+property_userId);
					}
					//forumTopic.setView_num(Integer.parseInt(property_userId)); //物业user id
					forumTopic.setForward_path(property_userId);
					if(process_status==null || process_status.isEmpty() || process_status.length()<=0 || process_status == "null"){
						process_status = "1";
						Loger.d("test5", "baoxiu 当前进度="+process_status);
					}
					forumTopic.setLike_num(Integer.parseInt(process_status));//当前进度
					forumTopic.setObject_data(process_data);//进度时间
					forumTopic.setTopic_category_type(Integer.parseInt(topic_category_type));
					forumTopic.setSender_community_id(Long.parseLong(sender_community_id));
					forumTopic.setSend_status(Integer.parseInt(send_status));
					forumTopic.setMeadia_files_json(media_files_json);
					forumTopic.setSender_nc_role(Integer.parseInt(collectStatus));
					forumtopicLists.add(forumTopic);
					maddapter = new RepairCircleAdapter(forumtopicLists, PropertyRepairActivity.this,1);
					maddapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "ERROR->"+e.getMessage());
					e.printStackTrace();
				}
			}
		//	Loger.d("test5", "baoxiu forumtopicLists size="+forumtopicLists.size()+""+((ForumTopic)forumtopicLists.get(0)).getTopic_title());
			
		}
		public void pullUpFromSrv(String search){
			RequestParams params = new RequestParams();
			if(forumtopicLists.size()>0){
				long currentTopicID = ((ForumTopic)(forumtopicLists.get(forumtopicLists.size()-1))).getTopic_id();
				Loger.i("TEST", "最后一个TopicId->"+currentTopicID);
				params.put("community_id", App.sFamilyCommunityId);
				params.put("user_id", App.sUserLoginId);	
				params.put("count", 6);
				params.put("topic_id", currentTopicID);
				params.put("category_type",App.BAOXIU_TYPE);
				if(uncompletedbaoxiu.getTag().toString().equals("1")){
					params.put("process_type",1);
				}else{
					params.put("process_type",3);
				}
				params.put("tag","getrepair");
				params.put("apitype",IHttpRequestUtils.APITYPE[4]);
				AsyncHttpClient httpClient = new  AsyncHttpClient();
				httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
						new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						// TODO Auto-generated method stub
						Loger.i("TEST",response.toString());
						getTopicDetailsInfos(response,"add");
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
						Loger.i("TEST", "topic->statusCode->"+statusCode);
						Message message = new Message();
						message.what = REFUSE_CODE;
						getTopicHandler.sendMessage(message);
						new ErrorServer(PropertyRepairActivity.this, responseString.toString());
						super.onFailure(statusCode, headers, responseString, throwable);
					}
				});
			}else{
				if(NetworkService.networkBool){
					forumtopicLists.clear();
					RequestParams reference = new RequestParams();
					reference.put("community_id", App.sFamilyCommunityId);								
					reference.put("user_id", App.sUserLoginId);	
					reference.put("count", 6);	
					reference.put("category_type",App.BAOXIU_TYPE);
					if(uncompletedbaoxiu.getTag().toString().equals("1")){
						reference.put("process_type",1);
					}else{
						reference.put("process_type",3);
					}
					reference.put("tag","getrepair");
					reference.put("apitype",IHttpRequestUtils.APITYPE[4]);
					AsyncHttpClient client = new AsyncHttpClient();
					client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
							reference, new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONArray response) {
							// TODO Auto-generated method stub 18945051215
							getTopicDetailsInfos(response,"init");
							bPullUpStatus = true;
							maddapter = new RepairCircleAdapter(forumtopicLists, PropertyRepairActivity.this,1);
							actualListView.setAdapter(maddapter);
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
							Loger.i("TEST", "topic->statusCode->"+statusCode);
							Loger.i("TEST", "topic->error->"+responseString);
							Message message = new Message();
							message.what = REFUSE_CODE;
							getTopicHandler.sendMessage(message);
							new ErrorServer(PropertyRepairActivity.this, responseString.toString());
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
					break;
				case NOINFO_SUCCESS_CODE:
					bPullUpStatus = true;
					nonemsgtext.setVisibility(View.VISIBLE);
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								PropertyRepairActivity.this.runOnUiThread(new Runnable() {
									public void run() {
										nonemsgtext.setVisibility(View.GONE);
									}
								});
							}
						}, 1000);

					break;
				case INIT_FAILED_CODE:
					Loger.i("TEST", "第一次访问服务器失败！！！！！！！！");
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
								if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME+5000) {
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
							try {
								maddapter.notifyDataSetChanged();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								maddapter = new RepairCircleAdapter(forumtopicLists, PropertyRepairActivity.this,1);
								e.printStackTrace();
							}
							// 将下拉视图收起
							mPullRefreshListView.onRefreshComplete();
						};
					}.execute();
				}
			}
		}
		@Override
		public void setstatuschanged(int status) {
			// TODO Auto-generated method stub
			Loger.d("test5","gonggao add refresh");
			if(NetworkService.networkBool){
				forumtopicLists.clear();
				RequestParams reference = new RequestParams();
				reference.put("community_id", App.sFamilyCommunityId);		
				reference.put("user_id", App.sUserLoginId);	
				reference.put("count", 6);	
				reference.put("category_type",App.BAOXIU_TYPE);
				if(uncompletedbaoxiu.getTag().toString().equals("1")){
					reference.put("process_type",1);
				}else{
					reference.put("process_type",3);
				}
				reference.put("tag","getrepair");
				reference.put("apitype",IHttpRequestUtils.APITYPE[4]);
				Loger.i("test5", "community_id="+App.sFamilyCommunityId+"user_id="+App.sUserLoginId +App.BAOXIU_TYPE +uncompletedbaoxiu.getTag().toString().equals("1")+IHttpRequestUtils.APITYPE[4]);
				//reference.put("key_word",searchmessage);
				AsyncHttpClient client = new AsyncHttpClient();
				client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
						reference, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						// TODO Auto-generated method stub 18945051215
						repairInitLayout.setVisibility(View.GONE);
						repairInitBelowLayout.setVisibility(View.VISIBLE);
						repairInitBelow2Layout.setVisibility(View.VISIBLE);
						getTopicDetailsInfos(response,"init");
						Flag = "ok";
						cRequesttype = true;
//						maddapter.notifyDataSetChanged();
						super.onSuccess(statusCode, headers, response);
					}
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						Message message = new Message();
						try {
							Flag = response.getString("flag");
							String empty=response.getString("empty");
							Loger.i("test5", "5555555555--->"+response.toString());
							if("no".equals(empty)){
								cRequesttype = true;
								repairInitLayout.setVisibility(View.VISIBLE);
								repairInitBelowLayout.setVisibility(View.GONE);
								repairInitBelow2Layout.setVisibility(View.GONE);
								Loger.i("test5", "baoxiutopic is empty yes1111111111111");
							}else if("ok".equals(empty)){
								repairInitLayout.setVisibility(View.GONE);
								repairInitBelowLayout.setVisibility(View.VISIBLE);
								repairInitBelow2Layout.setVisibility(View.VISIBLE);
								Loger.i("test5", "baoxiutopic is empty no");
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
						new ErrorServer(PropertyRepairActivity.this, responseString.toString());
						super.onFailure(statusCode, headers, responseString, throwable);
					}
				});
				
				Loger.d("test5", "gongao cRequesttype=");
				/*********************refresh*********************/
				Loger.d("test5", "gongao cRequesttype="+cRequesttype+"Flag="+Flag);
		        // TODO Auto-generated method stub
				new Thread(new Runnable() {
					public void run() {
						try {
							Long currenttime = System.currentTimeMillis();
							while (PropertyRepairActivity.this.getSupportFragmentManager().findFragmentByTag(
									"myfragment") == null) {
							}
							while(!cRequesttype ){
								if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME+5000){
									cRequesttype = true;
								}
							}
							PropertyRepairActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									Loger.d("test5", "gongao cRequesttype="+cRequesttype+"Flag="+Flag);
									if(cRequesttype&& (Flag.equals("ok")||Flag.equals("no"))){
										
										Loger.d("test5", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
										final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) PropertyRepairActivity.this.getSupportFragmentManager()
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
										mPullRefreshListView.setOnRefreshListener(PropertyRepairActivity.this);
										// You can also just use
										// mPullRefreshListFragment.getListView()
										actualListView = mPullRefreshListView
												.getRefreshableView();
										// TODO Auto-generated method stub
										actualListView.setDividerHeight(0);
//										actualListView.setDivider(PropertyGonggaoActivity.this.getResources().getDrawable(R.drawable.fengexian));
										actualListView.setAdapter(maddapter);
										actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
										
										finalfrag9.setListShown(true);
										//Loger.i("youlin","11111111111111111->"+((ForumTopic)forumtopicLists.get(0)).getSender_id()+"  "+App.sUserLoginId);
									}
									cRequesttype = false;
									Flag="none";
								}
							});
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


					}
				}).start();
				
				
			}
		}
		@Override
		public void setstatuschanged(int status, Bundle data) {
			// TODO Auto-generated method stub
		}
		
		@Override
		protected void onPause() {
			// TODO Auto-generated method stub
			super.onPause();
			JPushInterface.onPause(getApplicationContext());
			MobclickAgent.onPause(this);
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(chooseflag.equals("1")){
				return;
			}
			chooseflag = "1";
			if(v.getId() == R.id.uncompletedbaoxiu){
				if(v.getTag().toString().equals("1")){
					chooseflag = "0";
					return;
				}
				uncompletedbaoxiu.setTag("1");
				completedbaoxiu.setTag("0");
				text1.setTextColor(0xffffba02);
				text2.setTextColor(0xff909090);
				uncompletedview.setBackgroundColor(0xffffba02);
				completedview.setBackgroundColor(0xffa0a0a0);
				Loger.d("test5","gonggao add refresh");
				if(NetworkService.networkBool){
					forumtopicLists.clear();
					RequestParams reference = new RequestParams();
					reference.put("community_id", App.sFamilyCommunityId);		
					reference.put("user_id", App.sUserLoginId);	
					reference.put("count", 6);	
					reference.put("category_type",App.BAOXIU_TYPE);
					reference.put("process_type",1);
					reference.put("tag","getrepair");
					reference.put("apitype",IHttpRequestUtils.APITYPE[4]);
					Loger.d("test5", "community_id="+App.sFamilyCommunityId+"user_id="+App.sUserLoginId);
					//reference.put("key_word",searchmessage);
					AsyncHttpClient client = new AsyncHttpClient();
					client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
							reference, new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONArray response) {
							// TODO Auto-generated method stub 18945051215
							getTopicDetailsInfos(response,"init");
							Flag = "ok";
							cRequesttype = true;
							maddapter.notifyDataSetChanged();
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
									cRequesttype = true;
									nonemsgtext.setVisibility(View.VISIBLE);
										new Timer().schedule(new TimerTask() {
											@Override
											public void run() {
												// TODO Auto-generated method stub
												PropertyRepairActivity.this.runOnUiThread(new Runnable() {
													public void run() {
														nonemsgtext.setVisibility(View.GONE);
													}
												});
											}
										}, 1000);
									Loger.i("test5", "baoxiutopic is empty yes");
								}else{
									Loger.i("test5", "baoxiutopic is empty no");
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
							new ErrorServer(PropertyRepairActivity.this, responseString.toString());
							super.onFailure(statusCode, headers, responseString, throwable);
						}
					});
					/*********************refresh*********************/
					FragmentManager manager  = this.getSupportFragmentManager();
					FragmentTransaction tx = manager.beginTransaction();
					tx.replace(R.id.tabcontentrepair,new PullToRefreshListFragment(),"myfragment");
					tx.commit();
					Loger.d("test5", "gongao cRequesttype=");
					/*********************refresh*********************/
					Loger.d("test5", "gongao cRequesttype="+cRequesttype+"Flag="+Flag);
			        // TODO Auto-generated method stub
					new Thread(new Runnable() {
						public void run() {
							try {
								Long currenttime = System.currentTimeMillis();
								while (PropertyRepairActivity.this.getSupportFragmentManager().findFragmentByTag(
										"myfragment") == null) {
								}
								while(!cRequesttype ){
									if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME+5000){
										cRequesttype = true;
									}
								}
								PropertyRepairActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										Loger.d("test5", "gongao cRequesttype="+cRequesttype+"Flag="+Flag);
										if(cRequesttype&&Flag!=null&& (Flag.equals("ok")||Flag.equals("no"))){
											
											Loger.d("test5", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
											final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) PropertyRepairActivity.this.getSupportFragmentManager()
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
											mPullRefreshListView.setOnRefreshListener(PropertyRepairActivity.this);
											// You can also just use
											// mPullRefreshListFragment.getListView()
											actualListView = mPullRefreshListView
													.getRefreshableView();
											// TODO Auto-generated method stub
											actualListView.setDividerHeight(0);
//											actualListView.setDivider(PropertyGonggaoActivity.this.getResources().getDrawable(R.drawable.fengexian));
											actualListView.setAdapter(maddapter);
											actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
											actualListView.setOnItemLongClickListener(new OnItemLongClickListener() {

												@Override
												public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
														long id) {
													// TODO Auto-generated method stub
//													View contentView=getLayoutInflater().inflate(R.layout.activity_share_popwindow_newpush, null);
//													ListView listView=(ListView) contentView.findViewById(R.id.share_pop_repair_lv);
//													arr=new String[]{"删除","删除全部"};
//													ArrayAdapter<String> adapter=new ArrayAdapter<String>(PropertyRepairActivity.this, R.layout.activity_share_pop_textview,arr);
//													listView.setAdapter(adapter);
//													listView.setOnItemClickListener(new listViewListen(position-1));
//													popupWindow=new PopupWindow(PropertyRepairActivity.this);
//													popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
//													popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
//													popupWindow.setBackgroundDrawable(new BitmapDrawable());
//													popupWindow.setFocusable(true);
//													popupWindow.setOutsideTouchable(true);
//													popupWindow.setContentView(contentView);
//													popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
													return false;
												}
											});
											finalfrag9.setListShown(true);
											//Loger.i("youlin","11111111111111111->"+((ForumTopic)forumtopicLists.get(0)).getSender_id()+"  "+App.sUserLoginId);
										}else if(cRequesttype && Flag.equals("none")){
											final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) PropertyRepairActivity.this.getSupportFragmentManager()
													.findFragmentByTag("myfragment");
											try {
												mPullRefreshListView = finalfrag9.getPullToRefreshListView();
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
												return;
											}
											finalfrag9.setListShown(true);
										}
										cRequesttype = false;
										Flag="none";
										chooseflag = "0";
									}
								});
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}


						}
					}).start();
					
					
				}
			}else if(v.getId() == R.id.completedbaoxiu){
				Loger.d("test5","baoxiu add refresh");
				if(v.getTag().toString().equals("1")){
					chooseflag = "0";
					return;
				}
				uncompletedbaoxiu.setTag("0"); //hyy
				completedbaoxiu.setTag("1");
				text2.setTextColor(0xffffba02);
				text1.setTextColor(0xff909090);
				completedview.setBackgroundColor(0xffffba02);
				uncompletedview.setBackgroundColor(0xffa0a0a0);
				if(NetworkService.networkBool){
					forumtopicLists.clear();
					RequestParams reference = new RequestParams();
					reference.put("community_id", App.sFamilyCommunityId);		
					reference.put("user_id", App.sUserLoginId);	
					reference.put("count", 6);	
					reference.put("category_type",App.BAOXIU_TYPE);
					reference.put("process_type",3);
					reference.put("tag","getrepair");
					reference.put("apitype",IHttpRequestUtils.APITYPE[4]);
					Loger.d("test5", "community_id="+App.sFamilyCommunityId+"user_id="+App.sUserLoginId);
					//reference.put("key_word",searchmessage);
					AsyncHttpClient client = new AsyncHttpClient();
					client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
							reference, new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONArray response) {
							// TODO Auto-generated method stub 18945051215
							
							getTopicDetailsInfos(response,"init");
							Flag = "ok";
							cRequesttype = true;
							maddapter.notifyDataSetChanged();
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
									cRequesttype = true;
									nonemsgtext.setVisibility(View.VISIBLE);
										new Timer().schedule(new TimerTask() {
											@Override
											public void run() {
												// TODO Auto-generated method stub
												PropertyRepairActivity.this.runOnUiThread(new Runnable() {
													public void run() {
														nonemsgtext.setVisibility(View.GONE);
													}
												});
											}
										}, 1000);
									Loger.i("test5", "baoxiutopic is empty yes");
								}else{
									Loger.i("test5", "baoxiutopic is empty no");
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
							new ErrorServer(PropertyRepairActivity.this, responseString.toString());
							super.onFailure(statusCode, headers, responseString, throwable);
						}
					});
					/*********************refresh*********************/
					FragmentManager manager  = this.getSupportFragmentManager();
					FragmentTransaction tx = manager.beginTransaction();
					tx.replace(R.id.tabcontentrepair,new PullToRefreshListFragment(),"myfragment");
					tx.commit();
					Loger.d("test5", "gongao cRequesttype=");
					/*********************refresh*********************/
					Loger.d("test5", "gongao cRequesttype="+cRequesttype+"Flag="+Flag);
			        // TODO Auto-generated method stub
					new Thread(new Runnable() {
						public void run() {
							try {
								Long currenttime = System.currentTimeMillis();
								while (PropertyRepairActivity.this.getSupportFragmentManager().findFragmentByTag(
										"myfragment") == null) {
								}
								while(!cRequesttype ){
									if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME+5000){
										cRequesttype = true;
									}
								}
								PropertyRepairActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										Loger.d("test5", "gongao cRequesttype="+cRequesttype+"Flag="+Flag);
										if(cRequesttype && (Flag.equals("ok")||Flag.equals("no"))){
											
											Loger.d("test5", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
											final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) PropertyRepairActivity.this.getSupportFragmentManager()
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
											mPullRefreshListView.setOnRefreshListener(PropertyRepairActivity.this);
											// You can also just use
											// mPullRefreshListFragment.getListView()
											actualListView = mPullRefreshListView
													.getRefreshableView();
											// TODO Auto-generated method stub
											actualListView.setDividerHeight(0);
//											actualListView.setDivider(PropertyGonggaoActivity.this.getResources().getDrawable(R.drawable.fengexian));
											actualListView.setAdapter(maddapter);
											actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
											actualListView.setOnItemLongClickListener(new OnItemLongClickListener() {

												@Override
												public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
														long id) {
													// TODO Auto-generated method stub
//													View contentView=getLayoutInflater().inflate(R.layout.activity_share_popwindow_newpush, null);
//													ListView listView=(ListView) contentView.findViewById(R.id.share_pop_repair_lv);
//													arr=new String[]{"删除","删除全部"};
//													ArrayAdapter<String> adapter=new ArrayAdapter<String>(PropertyRepairActivity.this, R.layout.activity_share_pop_textview,arr);
//													listView.setAdapter(adapter);
//													listView.setOnItemClickListener(new listViewListen(position-1));
//													popupWindow=new PopupWindow(PropertyRepairActivity.this);
//													popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
//													popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
//													popupWindow.setBackgroundDrawable(new BitmapDrawable());
//													popupWindow.setFocusable(true);
//													popupWindow.setOutsideTouchable(true);
//													popupWindow.setContentView(contentView);
//													popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
													return false;
												}
											});
											finalfrag9.setListShown(true);
											//Loger.i("youlin","11111111111111111->"+((ForumTopic)forumtopicLists.get(0)).getSender_id()+"  "+App.sUserLoginId);
										}else if(cRequesttype && Flag.equals("none")){
											final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) PropertyRepairActivity.this.getSupportFragmentManager()
													.findFragmentByTag("myfragment");
											try {
												mPullRefreshListView = finalfrag9.getPullToRefreshListView();
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
												return;
											}
											finalfrag9.setListShown(true);
										}
										cRequesttype = false;
										Flag="none";
										chooseflag = "0";
									}
								});
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}


						}
					}).start();
					
					
				}
			}
		}
//		private class listViewListen implements OnItemClickListener{
//			listViewListen(int id){
//				menuSelectID=id;
//			}
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				// TODO Auto-generated method stub
//				if(arr[position].equals("删除")){
//					if(NetworkService.networkBool){
//						RequestParams reference = new RequestParams();
//						reference.put("community_id", App.sFamilyCommunityId);		
//						reference.put("user_id", App.sUserLoginId);	
//						reference.put("topic_type", 4);	
//						reference.put("topic_id", ((ForumTopic)forumtopicLists.get(menuSelectID)).getTopic_id());	
//						Loger.d("test5", "community_id="+App.sFamilyCommunityId+"user_id="+App.sUserLoginId);
//						//reference.put("key_word",searchmessage);
//						AsyncHttpClient client = new AsyncHttpClient();
//						client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN_DEL_TOPIC,
//								reference, new JsonHttpResponseHandler() {
//							@Override
//							public void onSuccess(int statusCode, Header[] headers,
//									JSONArray response) {
//								// TODO Auto-generated method stub 18945051215
//								super.onSuccess(statusCode, headers, response);
//							}
//							@Override
//							public void onSuccess(int statusCode, Header[] headers,
//									JSONObject response) {
//								// TODO Auto-generated method stub
//								Message message = new Message();
//								Loger.d("test4", "删除报修 flag="+response);
//								try {
//									String flag = response.getString("flag");
//									if(flag.equals("ok")){
//										statusutils = new StatusChangeutils();
//										statusutils.setstatuschange("BAOXIULIST", 1);
//										popupWindow.dismiss();
//									}
//								} catch (JSONException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								super.onSuccess(statusCode, headers, response);
//							}
//							@Override
//							public void onFailure(int statusCode, Header[] headers,
//									String responseString, Throwable throwable) {
//								// TODO Auto-generated method stub
//								new ErrorServer(PropertyRepairActivity.this, responseString.toString());
//								super.onFailure(statusCode, headers, responseString, throwable);
//							}
//						});
//					}
//				}
//			}
//		}
}
