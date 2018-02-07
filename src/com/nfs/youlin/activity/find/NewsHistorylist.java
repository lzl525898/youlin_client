package com.nfs.youlin.activity.find;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.neighbor.AllNewshistoryList;
import com.nfs.youlin.adapter.NewsListAdapter;
import com.nfs.youlin.dao.NewsDaoDBImpl;
import com.nfs.youlin.entity.NewsBlock;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

public class NewsHistorylist extends FragmentActivity implements OnRefreshListener<ListView>{
	private ListView NewsCircleLv;
	private ActionBar actionBar;
	private TextView nonemsgtext;
	private ListView actualListView;
	private PullToRefreshListView mPullRefreshListView;
	private String Flag = "none";
	private boolean cRequesttype = false;
	private boolean bPullUpStatus = false;
	private NewsListAdapter maddapter;
	public static List<Map<String, Object>> NewsLists = new ArrayList<Map<String, Object>>();
	List<Object> newsList = new ArrayList<Object>();
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.newslist);
		setTitle("消息");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		nonemsgtext =(TextView) findViewById(R.id.friend_circle_noinfo_tvnews);
		NewsLists.clear();
		FragmentManager manager  = this.getSupportFragmentManager();
		FragmentTransaction tx = manager.beginTransaction();
		tx.replace(R.id.tabcontentnews,new PullToRefreshListFragment(),"myfragment");
		tx.commit();
		getTopicDetailsInfos();
        // TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				try {
					Long currenttime = System.currentTimeMillis();
					while (NewsHistorylist.this.getSupportFragmentManager().findFragmentByTag(
							"myfragment") == null) {
					}
					while(!cRequesttype ){
						if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
							cRequesttype = true;
						}
					}
					NewsHistorylist.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Loger.d("test5", "cRequesttype="+cRequesttype+"Flag="+Flag);
							if(cRequesttype&&Flag!=null){
								cRequesttype = false;

								final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) NewsHistorylist.this.getSupportFragmentManager()
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
								mPullRefreshListView.setMode(Mode.MANUAL_REFRESH_ONLY);
								mPullRefreshListView.setOnRefreshListener(NewsHistorylist.this);
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
									new Timer().schedule(new TimerTask() {
										@Override
										public void run() {
											// TODO Auto-generated method stub
											NewsHistorylist.this.runOnUiThread(new Runnable() {
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
	private void getTopicDetailsInfos(){
		NewsDaoDBImpl newsdb = new NewsDaoDBImpl(this);
		newsList = newsdb.findNewsObjectbelongto(App.sFamilyId, 1);
		Loger.d("test5", "newsList size"+newsList.size());
		getData(NewsLists, newsList);
		maddapter = new NewsListAdapter(this,NewsLists,R.layout.news_list_item,
        		new String[] {"firsturl","firsttitle","firstlinkurl","othernews"},new int[]{R.id.newsone_img,R.id.newsone_title,R.id.newsforothers});
		Flag = "ok";
		cRequesttype = true;
	}
    private void getData(List<Map<String, Object>> newslist,List<Object> newsListfromdb) {  
	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
	        for (int i = 0; i < newsListfromdb.size(); i++) {  
	        	Map<String, Object> map = new HashMap<String, Object>();
	            	NewsBlock news = (NewsBlock)newsListfromdb.get(i);
	            	map.put("pushtime", news.getnewspushtime());
	            	map.put("firstnewsid",news.getnewsid());
					map.put("firsturl", news.getnewspic());
					map.put("firsttitle",news.getnewstitle());
					map.put("firstlinkurl",news.getnewsurl());
					map.put("othernews", news.getnewsothers());
					Loger.d("test5", ""+news.getnewspushtime());
	            	newslist.add(map);    	       
	        }  
    }
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
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
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
