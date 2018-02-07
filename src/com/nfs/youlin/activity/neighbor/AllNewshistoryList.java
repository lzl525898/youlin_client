package com.nfs.youlin.activity.neighbor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.find.NewsOperation;
import com.nfs.youlin.activity.personal.MyPushActivity;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.adapter.NewsListAdapter;
import com.nfs.youlin.dao.NewsDaoDBImpl;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.entity.NewsBlock;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.jpush.android.api.JPushInterface;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class AllNewshistoryList extends FragmentActivity implements OnRefreshListener<ListView>{
	private ListView NewsCircleLv;
	private ActionBar actionBar;
	private TextView nonemsgtext;
	private ListView actualListView;
	private PullToRefreshListView mPullRefreshListView;
	private String Flag = "none";
	private String xinwengroupflag = "none";
	private String xinwengroup_url = "";
	private String xinwengroup_scribe = "";
	private boolean cRequesttype = false;
	private boolean bPullUpStatus = false;
	private NewsListAdapter maddapter;
	public static List<Map<String, Object>> NewsLists = new ArrayList<Map<String, Object>>();
	private RelativeLayout nonewsview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newslist);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("新闻");
		nonemsgtext =(TextView) findViewById(R.id.friend_circle_noinfo_tvnews);
		nonewsview = (RelativeLayout) findViewById(R.id.nonewsview);
		NewsLists.clear();
		if(NetworkService.networkBool){
			RequestParams reference = new RequestParams();
			reference.put("community_id", App.sFamilyCommunityId);	
			reference.put("tag", "getnews");
			reference.put("apitype", IHttpRequestUtils.APITYPE[1]);
			Loger.d("test5", "community_id="+App.sFamilyCommunityId);
			//reference.put("key_word",searchmessage);
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
					reference, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONArray response) {
					// TODO Auto-generated method stub 18945051215
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
							cRequesttype = true;
//							nonemsgtext.setVisibility(View.VISIBLE);
//								new Timer().schedule(new TimerTask() {
//									@Override
//									public void run() {
//										// TODO Auto-generated method stub
//										AllNewshistoryList.this.runOnUiThread(new Runnable() {
//											public void run() {
//												nonemsgtext.setVisibility(View.GONE);
//											}
//										});
//									}
//								}, 1000);
							Loger.i("TEST", "topic is empty yes");
						}else{
							Loger.i("TEST", "topic is empty no");
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
					new ErrorServer(AllNewshistoryList.this, responseString.toString());
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		}
		/*********************refresh*********************/
		FragmentManager manager  = this.getSupportFragmentManager();
		FragmentTransaction tx = manager.beginTransaction();
		tx.replace(R.id.tabcontentnews,new PullToRefreshListFragment(),"myfragment");
		tx.commit();
		
        // TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				try {
					Long currenttime = System.currentTimeMillis();
					while (AllNewshistoryList.this.getSupportFragmentManager().findFragmentByTag(
							"myfragment") == null) {
					}
					while(!cRequesttype ){
						if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
							cRequesttype = true;
						}
					}
					AllNewshistoryList.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Loger.d("test5", "cRequesttype="+cRequesttype+"Flag="+Flag);
							if(cRequesttype&&Flag!=null){
								cRequesttype = false;

								final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) AllNewshistoryList.this.getSupportFragmentManager()
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
								mPullRefreshListView.setMode(Mode.BOTH);
								mPullRefreshListView.setOnRefreshListener(AllNewshistoryList.this);
								// You can also just use
								// mPullRefreshListFragment.getListView()
								actualListView = mPullRefreshListView.getRefreshableView();
								// TODO Auto-generated method stub
								actualListView.setDividerHeight(0);
								actualListView.setAdapter(maddapter);
								try {
									actualListView.setSelection(maddapter.getCount());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
								finalfrag9.setListShown(true);
								
								if(Flag.equals("ok")){
									Flag="none";
									Loger.d("test5", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
								}else{
									Flag="none";
									nonemsgtext.setVisibility(View.VISIBLE);
									nonewsview.setVisibility(View.VISIBLE);
									new Timer().schedule(new TimerTask() {
										@Override
										public void run() {
											// TODO Auto-generated method stub
											AllNewshistoryList.this.runOnUiThread(new Runnable() {
												public void run() {
													nonemsgtext.setVisibility(View.GONE);
												}
											});
										}
									}, 1000);
								}
							}	
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
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(android.R.id.home == item.getItemId()){
			Loger.d("hyytest", "22222xxxxxxxx");
			finish();
			return true;
		}
//		else if(R.id.newsactionbar == item.getItemId()){
//			
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.newlistbar, menu);
		LinearLayout SearchGroup = (LinearLayout) menu.findItem(R.id.newsactionbar).getActionView();
		ImageView newset = (ImageView)SearchGroup.findViewById(R.id.newset);
		getHttpfornewsgroup(App.sFamilyCommunityId);
		newset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(xinwengroupflag.equals("no")){
					Toast.makeText(AllNewshistoryList.this, "当前小区还没有新闻广播台！", Toast.LENGTH_SHORT).show();
				}else if(xinwengroupflag.equals("ok")){
					Intent intent = new Intent(AllNewshistoryList.this,NewsOperation.class);
					intent.putExtra("url", xinwengroup_url);
					intent.putExtra("descripe", xinwengroup_scribe);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				}
				
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	private void getHttpfornewsgroup(long community_id){
		RequestParams reference = new RequestParams();
		reference.put("community_id", community_id);	
		reference.put("tag", "getsubscription");
		reference.put("apitype", "comm");
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
				reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub 1894505121
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				Message message = new Message();
				try {
					xinwengroupflag = response.getString("flag");
					Loger.d("test6", "xinwengroupflag=="+xinwengroupflag);
					if("no".equals(xinwengroupflag)){
						
					}else{
						xinwengroup_scribe = response.getString("scription");
						xinwengroup_url = response.getString("img_url");
						Loger.d("test6", "xinwengroup_scribe==>"+xinwengroup_scribe+"xinwengroup_url==>"+xinwengroup_url);
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
				new ErrorServer(AllNewshistoryList.this, responseString.toString());
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	private void getHttpfornews(long community_id,long push_time){
		RequestParams reference = new RequestParams();
		reference.put("community_id", community_id);	
		reference.put("push_time", push_time);
		reference.put("tag", "getnews");
		reference.put("apitype", IHttpRequestUtils.APITYPE[1]);
		Loger.d("test5", "community_id="+App.sFamilyCommunityId);
		//reference.put("key_word",searchmessage);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
				reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub 18945051215
				getNewsDetailsInfosforRef(response,"down");
				Flag = "ok";
				bPullUpStatus = true;
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
						bPullUpStatus = true;
						Loger.i("TEST", "topic is empty yes");
					}else{
						bPullUpStatus = true;
						Loger.i("TEST", "topic is empty no");
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
				new ErrorServer(AllNewshistoryList.this, responseString.toString());
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	private void getHttpfornewsup(long community_id,long push_time,String flag){
		RequestParams reference = new RequestParams();
		reference.put("community_id", community_id);	
		reference.put("push_time", push_time);
		reference.put("tag", "getnews");
		reference.put("apitype", IHttpRequestUtils.APITYPE[1]);
		if(flag != null)
			reference.put("flag", flag);
		Loger.d("test5", "community_id="+App.sFamilyCommunityId);
		//reference.put("key_word",searchmessage);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
				reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub 18945051215
				getNewsDetailsInfosforRef(response,"up");
				Flag = "ok";
				bPullUpStatus = true;
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				Message message = new Message();
				Loger.i("test4", "news response="+response);
				try {
					
					Flag = response.getString("flag");
					if("no".equals(Flag)){
						bPullUpStatus = true;
						Loger.i("TEST", "topic is empty yes");
					}else{
						bPullUpStatus = true;
						Loger.i("TEST", "topic is empty no");
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
				new ErrorServer(AllNewshistoryList.this, responseString.toString());
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	private void getTopicDetailsInfos(JSONArray response){
		int responseLen = response.length();
		Loger.i("TEST", "json obj length->"+responseLen);
		if(responseLen>0){
			NewsDaoDBImpl newsdb = new NewsDaoDBImpl(this);
		
		/**************************new add**************************/
		for (int i = 0; i < responseLen; i++) {
			try {
				JSONObject jsonObject = new JSONObject(response.getString(i));
				String news_title = jsonObject.getString("new_title");
				String news_pic = jsonObject.getString("new_small_pic");
				String news_url = jsonObject.getString("new_url");
//				long news_belongs = jsonObject.getLong("belong");
				int news_id = jsonObject.getInt("new_id");
//				long news_send_time = jsonObject.getLong("new_add_time");
//				long news_push_time = jsonObject.getLong("push_time");
				long news_send_time = Long.parseLong(jsonObject.getString("new_add_time"));
				long news_push_time = Long.parseLong(jsonObject.getString("push_time"));
				Loger.d("test4", "new_id="+news_id+"new_small_pic="+news_pic+"news_push_time="+news_push_time+"new_url="+news_url);
				String news_others = jsonObject.getString("otherNew");
				NewsBlock news = new NewsBlock(this);
				news.setfirstflag(1);
				news.setnewsbelong(App.sFamilyId);
				news.setnewsid(news_id);
				news.setnewspic(news_pic);
				news.setnewspushtime(news_push_time);
				news.setnewssendtime(news_send_time);
				news.setnewstitle(news_title);
				news.setnewsurl(news_url);
				news.setnewsothers(news_others);
				newsdb.saveObject(news);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		}
		getData(NewsLists, response,"nomal");
		maddapter = new NewsListAdapter(this,NewsLists,R.layout.news_list_item,
        		new String[] {"firsturl","firsttitle","firstlinkurl","othernews"},new int[]{R.id.newsone_img,R.id.newsone_title,R.id.newsforothers});
		
	}
	private void getNewsDetailsInfosforRef(JSONArray response,String flag){
		int responseLen = response.length();
		Loger.i("TEST", "json obj length->"+responseLen);
		if(responseLen>0){
			NewsDaoDBImpl newsdb = new NewsDaoDBImpl(this);
		
		/**************************new add**************************/
		for (int i = 0; i < responseLen; i++) {
			try {
				JSONObject jsonObject = new JSONObject(response.getString(i));
				String news_title = jsonObject.getString("new_title");
				String news_pic = jsonObject.getString("new_small_pic");
				String news_url = jsonObject.getString("new_url");
//				long news_belongs = jsonObject.getLong("belong");
				int news_id = jsonObject.getInt("new_id");
//				long news_send_time = jsonObject.getLong("new_add_time");
//				long news_push_time = jsonObject.getLong("push_time");
				long news_send_time = Long.parseLong(jsonObject.getString("new_add_time"));
				long news_push_time = Long.parseLong(jsonObject.getString("push_time"));
				Loger.d("test4", "new_id="+news_id+"new_small_pic="+news_pic+"news_push_time="+news_push_time+"new_url="+news_url);
				String news_others = jsonObject.getString("otherNew");
				NewsBlock news = new NewsBlock(this);
				news.setfirstflag(1);
				news.setnewsbelong(App.sFamilyId);
				news.setnewsid(news_id);
				news.setnewspic(news_pic);
				news.setnewspushtime(news_push_time);
				news.setnewssendtime(news_send_time);
				news.setnewstitle(news_title);
				news.setnewsurl(news_url);
				news.setnewsothers(news_others);
				newsdb.saveObject(news);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		}
		getData(NewsLists,response,flag);
//		maddapter = new NewsListAdapter(this,NewsLists,R.layout.news_list_item,
//        		new String[] {"firsturl","firsttitle","firstlinkurl","othernews"},new int[]{R.id.newsone_img,R.id.newsone_title,R.id.newsforothers});
		
	}

    private List<Map<String, Object>> getData(List<Map<String, Object>> newslist,JSONArray httpresponse,String flag) {  
	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
        if("up".equals(flag)){
	        for (int i = 0; i < httpresponse.length(); i++) {  
	        	Map<String, Object> map = new HashMap<String, Object>();
	            try {
	            	JSONObject jsonObject = new JSONObject(httpresponse.getString(httpresponse.length()-i-1));
	            	map.put("pushtime", jsonObject.getString("push_time"));
	            	map.put("firstnewsid", jsonObject.getString("new_id"));
					map.put("firsturl", jsonObject.getString("new_small_pic"));
					map.put("firsttitle",jsonObject.getString("new_title"));
					map.put("firstlinkurl",jsonObject.getString("new_url"));
					map.put("othernews", jsonObject.getJSONArray("otherNew").toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            	newslist.add(map);  
	            	       
	        }  
        }else{
        	for (int i = 0; i < httpresponse.length(); i++) {  
	        	Map<String, Object> map = new HashMap<String, Object>();
	            try {
	            	JSONObject jsonObject = new JSONObject(httpresponse.getString(i));
	            	map.put("pushtime", jsonObject.getString("push_time"));
	            	map.put("firstnewsid", jsonObject.getString("new_id"));
					map.put("firsturl", jsonObject.getString("new_small_pic"));
					map.put("firsttitle",jsonObject.getString("new_title"));
					map.put("firstlinkurl",jsonObject.getString("new_url"));
					map.put("othernews", jsonObject.getJSONArray("otherNew").toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            	newslist.add(0,map);  
	            	       
	        }  
        }
	        return newslist;  
	    }
    
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (refreshView.isShownHeader()) {
			// 判断尾布局是否可见，如果可见执行上拉加载更多
			// 设置尾布局样式文字
			Loger.d("hyytest", "???????????????????????");
			mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			mPullRefreshListView.getLoadingLayoutProxy().setPullLabel("加载更多历史");
			mPullRefreshListView.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			if (NetworkService.networkBool) {
				getHttpfornews(App.sFamilyCommunityId, Long.parseLong(NewsLists.get(0).get("pushtime").toString()));
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
//						bPullUpStatus = false;
						return null;
					}

					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						// 完成对下拉刷新ListView的更新操作
						// 将下拉视图收起
						if(bPullUpStatus){
							if("ok".equals(Flag)){
								Loger.i("test4", "pulldown refresh ok");
								//maddapter.notifyDataSetChanged();
								actualListView.setAdapter(maddapter);
								actualListView.setSelection(3);
		
							}else if("no1".equals(Flag)){
								nonemsgtext.setVisibility(View.VISIBLE);
								new Timer().schedule(new TimerTask() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										AllNewshistoryList.this.runOnUiThread(new Runnable() {
											public void run() {
												nonemsgtext.setVisibility(View.GONE);
											}
										});
									}
								}, 1000);
							}else{
								
							}
							Flag = "none";
							bPullUpStatus = false;
							mPullRefreshListView.onRefreshComplete();
						}
						
						
					};
				}.execute();
			}else{
				Toast.makeText(this, "当前网络异常！", Toast.LENGTH_SHORT).show();
			}
		}else if(refreshView.isShownFooter()){
			Loger.d("hyytest", "???????????????????????");
			mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
			mPullRefreshListView.getLoadingLayoutProxy().setPullLabel("查看最新内容");
			mPullRefreshListView.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
			NewsDaoDBImpl newsdb = new NewsDaoDBImpl(this);
			long maxpushtime = newsdb.findNewsPushtimeMax(App.sFamilyId, 1);
			Loger.d("test5","db max news id="+maxpushtime);
			if (NetworkService.networkBool) {
				getHttpfornewsup(App.sFamilyCommunityId,maxpushtime,"up");
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
//						bPullUpStatus = false;
						return null;
					}

					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						// 完成对下拉刷新ListView的更新操作
						// 将下拉视图收起
						if("ok".equals(Flag)){
							Loger.i("test4", "pullUp refresh ok");
							maddapter.notifyDataSetChanged();
//							actualListView.setAdapter(maddapter);
//							actualListView.setSelection(3);
	
						}else if("no1".equals(Flag)){
							Loger.i("test4", "pullUp refresh no1");
							nonemsgtext.setVisibility(View.VISIBLE);
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									AllNewshistoryList.this.runOnUiThread(new Runnable() {
										public void run() {
											nonemsgtext.setVisibility(View.GONE);
										}
									});
								}
							}, 1000);
						}else{
							Loger.i("test4", "pullUp refresh undefine flag="+Flag);
						}
						Flag = "none";
						bPullUpStatus = false;
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
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}
}
