package com.nfs.youlin.activity;

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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
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
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class TitleBarSearchActivity extends FragmentActivity implements OnRefreshListener<ListView>{
	private ActionBar actionBar;
	private static Button select_search;
	private static TextView search_message;
	private ImageView search_return;
	private Button search_button;
	public static List<Object> forumtopicLists = new ArrayList<Object>();
	private final static int DOWN_SUCCESS_CODE      = 2000;
	private final static int UP_SUCCESS_CODE        = 2007;
	private final static int FAILED_CODE            = 2001;
	private final static int REFUSE_CODE            = 2003;
	private final static int FIRST_SUCCESS_CODE     = 2004;
	private final static int INIT_FAILED_CODE       = 2005;
	private final static int INIT_REFUSE_CODE       = 2006;
	private final static int NOINFO_SUCCESS_CODE    = 2008;
	private final static int FIRST_NOINFO_SUCCESS_CODE = 2009;
	public  static FriendCircleAdapter maddapter ;
	private ListView friendCircleLv;
	private ListView actualListView;
	private PullToRefreshListView mPullRefreshListView;
	private boolean bPullUpStatus = false;
	private TextView nonemsgtext;
	private ProgressDialog pd1;
	private static int searchtype = 0;
	void buildprocessdialog(){
		pd1 = new ProgressDialog(this);
		pd1.setMessage("正在提交搜索请求，请稍侯...");
		pd1.setCancelable(false);
		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd1.setIndeterminate(false);
	} 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_search);
		buildprocessdialog();
//		this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_search_item); 
		actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
//		actionBar.setDisplayHomeAsUpEnabled(true);
		forumtopicLists.clear();
		nonemsgtext =(TextView) findViewById(R.id.friend_circle_noinfo_tv1);
		maddapter = new FriendCircleAdapter(forumtopicLists,this,0,0);
		/*********************refresh*********************/
		FragmentManager manager  = this.getSupportFragmentManager();
		FragmentTransaction tx = manager.beginTransaction();
		tx.replace(R.id.tabcontent4,new PullToRefreshListFragment(),"myfragment");
		tx.commit();
		
        // TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				try {
					while (TitleBarSearchActivity.this.getSupportFragmentManager().findFragmentByTag(
							"myfragment") == null) {
					}
					TitleBarSearchActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) TitleBarSearchActivity.this.getSupportFragmentManager()
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
							mPullRefreshListView.setOnRefreshListener(TitleBarSearchActivity.this);
							// You can also just use
							// mPullRefreshListFragment.getListView()
							actualListView = mPullRefreshListView
									.getRefreshableView();
							// TODO Auto-generated method stub
							actualListView.setDividerHeight(0);
							actualListView.setAdapter(maddapter);
							actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
							
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
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int height = wm.getDefaultDisplay().getHeight();
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_title_search, menu);
        RelativeLayout SearchGroup = (RelativeLayout) menu.findItem(
        		R.id.search_function).getActionView();
        search_return = (ImageView)SearchGroup.findViewById(R.id.main_search_return);
        search_return.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        search_button = (Button)SearchGroup.findViewById(R.id.main_search);
        search_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Loger.d("hyytest", "22222xxxxxxxx");
				if(NetworkService.networkBool){
					forumtopicLists.clear();
					String searchmessage = search_message.getText().toString();
					if(searchmessage.length()<=0){
						Toast.makeText(TitleBarSearchActivity.this,"请输入搜索关键字", Toast.LENGTH_SHORT).show();
						return;
					}else{
						InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						try {
							inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						pd1.show();
						RequestParams reference = new RequestParams();
						reference.put("community_id", App.sFamilyCommunityId);	
						reference.put("user_id", App.sUserLoginId);
						reference.put("key_word",searchmessage);
						reference.put("category_type",searchtype);
						reference.put("tag","findtopic");
						reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
						AsyncHttpClient client = new AsyncHttpClient();
						client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
								reference, new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONArray response) {
								// TODO Auto-generated method stub 18945051215
								getTopicDetailsInfos(response);
								Message message = new Message();
								message.what = FIRST_SUCCESS_CODE;
								getTopicHandler.sendMessage(message);
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
										message.what = FIRST_NOINFO_SUCCESS_CODE;
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
								new ErrorServer(TitleBarSearchActivity.this, responseString);
								super.onFailure(statusCode, headers, responseString, throwable);
							}
						});
					}
					
				}
			}
		});
        select_search = (Button)SearchGroup.findViewById(R.id.main_search_button);

        search_message = (TextView)SearchGroup.findViewById(R.id.main_search_etail);
        search_message.addTextChangedListener(new TextWatcher(){
        	int l=0;////////记录字符串被删除字符之前，字符串的长度
  		   int location=0;//记录光标的位置
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				l=arg0.length();
    		    location=search_message.getSelectionStart();
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				if (arg0.toString().length()==0){
					forumtopicLists.clear();
				}
			}
        	
        });
        float dipflag = select_search.getTextSize();
        //SearchGroup.setLayoutParams(new LinearLayout.LayoutParams(width-(int)dipflag*5,height/20));
       // 
       
        select_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				// TODO Auto-generated method stub
				View addListViewButtom = TitleBarSearchActivity.this.getLayoutInflater().inflate(R.layout.popsearch_select, null);
				PopupMenuAddListView select = (PopupMenuAddListView)addListViewButtom.findViewById(R.id.popusearch_select);
				searchmodeadapter seardhmode = new searchmodeadapter(TitleBarSearchActivity.this);
				//ArrayAdapter<String> popupMenuAddAdapter = new ArrayAdapter<String>(TitleBarSearchActivity.this, android.R.layout.simple_dropdown_item_1line,onechoose );
				select.setAdapter(seardhmode);
				select.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						switch(position){
						case 0: //搜话题
							Loger.d("hyytest", "1111xxxxxxxx");
							((Button)arg0).setText("搜全部");
							searchtype = 0;
							CommonTools.goBack();
							break;
						case 1: //搜商家
							((Button)arg0).setText("搜话题");
							searchtype = 1;
							CommonTools.goBack();
							break;
						case 2: //搜商家
							((Button)arg0).setText("搜活动");
							searchtype = 2;
							CommonTools.goBack();
							break;
						case 3: //搜商家
							((Button)arg0).setText("搜公告");
							searchtype = 3;
							CommonTools.goBack();
							break;
						case 4: //搜商家
							((Button)arg0).setText("搜建议");
							searchtype = 5;
							CommonTools.goBack();
							break;
							
						default:
							break;
						}
						//Toast.makeText(MainTitleBar.sMainActivity, "position->"+position, Toast.LENGTH_LONG).show();
					}
				});
				PopupWindow popupWindow = new PopupWindow(TitleBarSearchActivity.this.getApplicationContext());
				popupWindow.setContentView(addListViewButtom);
				popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
				popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
				popupWindow.setOutsideTouchable(true);
				popupWindow.setFocusable(true);
				popupWindow.showAsDropDown(select_search, -1, 15);
				popupWindow.setBackgroundDrawable(new ColorDrawable(0x00ffffff));
				
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(android.R.id.home == item.getItemId()){
			finish();
		}
		if(R.id.main_search == item.getItemId()){
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
				try {
					forumTopic.setSender_family_id(Long.parseLong(sender_family_id));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					forumTopic.setSender_family_id(0);
				}
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
				forumtopicLists.add(forumTopic);
				maddapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Loger.i("TEST", "ERROR->"+e.getMessage());
				e.printStackTrace();
			}
		}
		Loger.d("test5", "forumtopicLists size="+forumtopicLists.size()+""+((ForumTopic)forumtopicLists.get(0)).getFlag());
		
	}
	public void pullUpFromSrv(String search){
		RequestParams params = new RequestParams();
		if(forumtopicLists.size()>0){
			long currentTopicID = ((ForumTopic)(forumtopicLists.get(forumtopicLists.size()-1))).getTopic_id();
			Loger.i("LYM", "最后一个TopicId->"+currentTopicID);
			params.put("community_id", App.sFamilyCommunityId);
			params.put("topic_id", currentTopicID);
			params.put("key_word", search);
			params.put("user_id", App.sUserLoginId);
			params.put("count", 6);
			params.put("category_type",searchtype);
			params.put("tag","findtopic");
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
					new ErrorServer(TitleBarSearchActivity.this, responseString);
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		}else{
			if(NetworkService.networkBool){
				forumtopicLists.clear();
				String searchmessage = search_message.getText().toString();
				RequestParams reference = new RequestParams();
				reference.put("community_id", App.sFamilyCommunityId);								
				reference.put("key_word",searchmessage);
				reference.put("user_id", App.sUserLoginId);
				reference.put("category_type",searchtype);
				reference.put("tag","findtopic");
				reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
				AsyncHttpClient client = new AsyncHttpClient();
				client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
						reference, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						// TODO Auto-generated method stub 18945051215
						getTopicDetailsInfos(response);
						Message message = new Message();
						message.what = FIRST_SUCCESS_CODE;
						getTopicHandler.sendMessage(message);
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
						new ErrorServer(TitleBarSearchActivity.this, responseString);
						super.onFailure(statusCode, headers, responseString, throwable);
					}
				});
			}
			
		}
		
	}
	private Handler getTopicHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UP_SUCCESS_CODE:
				Loger.i("TEST", "UP访问服务器成功！！！！！！！！");
				break;
			case FAILED_CODE:
				Loger.i("TEST", "访问服务器失败！！！！！！！！");
				break;
			case REFUSE_CODE:
				Loger.i("TEST", "服务器拒绝访问！！！！！！！！");
				pd1.dismiss();
				break;
			case FIRST_SUCCESS_CODE:
				Loger.i("TEST", "访问成功！没有数据返回！");
				bPullUpStatus = true;
				pd1.dismiss();
				maddapter = new FriendCircleAdapter(forumtopicLists,TitleBarSearchActivity.this,0,0);
				actualListView.setAdapter(maddapter);
				break;
			case NOINFO_SUCCESS_CODE:
				bPullUpStatus = true;
				nonemsgtext.setVisibility(View.VISIBLE);
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							TitleBarSearchActivity.this.runOnUiThread(new Runnable() {
								public void run() {
									nonemsgtext.setVisibility(View.GONE);
								}
							});
						}
					}, 1000);

				break;
			case FIRST_NOINFO_SUCCESS_CODE:
				bPullUpStatus = true;
				pd1.dismiss();
//				nonemsgtext.setVisibility(View.VISIBLE);
//					new Timer().schedule(new TimerTask() {
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							TitleBarSearchActivity.this.runOnUiThread(new Runnable() {
//								public void run() {
//									nonemsgtext.setVisibility(View.GONE);
//								}
//							});
//						}
//					}, 1000);
				Toast.makeText(TitleBarSearchActivity.this, "没有搜到", 0).show();
				maddapter.notifyDataSetChanged();	
			case INIT_FAILED_CODE:
				Loger.i("TEST", "第一次访问服务器失败！！！！！！！！");
				pd1.dismiss();
				break;
			case INIT_REFUSE_CODE:
				Loger.i("TEST", "第一次服务器拒绝访问！！！！！！！！");
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
			final String searchmessage = search_message.getText().toString();
			if (NetworkService.networkBool) {
				if(searchmessage.length()<=0||searchmessage.substring(0, 1).equals(" ")||searchmessage == null){
					bPullUpStatus = true;
					Loger.i("TEST","???????????????????????");
				}else{
					Loger.i("TEST","1111111111111???????????????????????");
					pullUpFromSrv(searchmessage);
				}
				
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
					};
				}.execute();
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(TitleBarSearchActivity.this);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(TitleBarSearchActivity.this);
		MobclickAgent.onPause(this);
	}
}
