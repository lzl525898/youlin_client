package com.nfs.youlin.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nfs.youlin.R;
import com.nfs.youlin.R.id;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.activity.find.StoreCircleAllCommentActivity;
import com.nfs.youlin.activity.neighbor.AllNewshistoryList;
import com.nfs.youlin.adapter.CardsAdapter;
import com.nfs.youlin.dao.NewsDaoDBImpl;
import com.nfs.youlin.entity.CardsBean;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.YLProgressDialog;

import android.R.integer;
import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebStorage;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherListActivity extends FragmentActivity implements OnRefreshListener<ListView>{
	private ActionBar actionBar;
	private PullToRefreshListView cardsList;
	private CardsAdapter myadapter;
	private String Flag = "none";
	private TextView nonemsgtext;
	private boolean flag=false;
	private boolean bPullUpStatus = false;
	private RelativeLayout nonewsview;
	YLProgressDialog yLProgressDialog;
	private List<CardsBean> mylist = new ArrayList<CardsBean>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_list);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("天气");
		Init();
		InterContext();
		cardsList.setOnItemClickListener(new MyOnClick());
		
	}
	//初始化
	public void Init(){
		nonemsgtext =(TextView) findViewById(R.id.friend_circle_noinfo_tvnews);
		nonewsview = (RelativeLayout) findViewById(R.id.nonewsview);
		yLProgressDialog = YLProgressDialog.createDialogwithcircle(WeatherListActivity.this,"",1);
		cardsList = (PullToRefreshListView) findViewById(R.id.weather_list);
		cardsList.setMode(Mode.BOTH);
		cardsList.setOnRefreshListener(this);
		myadapter=new CardsAdapter(this,mylist);
		cardsList.setAdapter(myadapter);
		yLProgressDialog.show();
	}
	class MyOnClick implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent=new Intent(WeatherListActivity.this,WeatherInforActivity.class);
			intent.putExtra("id", mylist.get(arg2-1).getId());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		}
	}
	//初始化数据连接
	public void InterContext(){
		if(NetworkService.networkBool){
			RequestParams reference = new RequestParams();
			reference.put("community_id", App.sFamilyCommunityId);	
			reference.put("tag", "getweaorzod");
			reference.put("action_id", "0");
			reference.put("weaorzod_id", "0");
			reference.put("apitype", IHttpRequestUtils.APITYPE[1]);
			Loger.d("test5", "community_id="+App.sFamilyCommunityId);
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
					reference, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONArray response) {
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
					try {
						
						Flag = response.getString("flag");
						if("ok".equals(Flag)){
							nonewsview.setVisibility(View.GONE);
							JSONArray weaorzod_detail=response.getJSONArray("weaorzod_detail");
							getNewsInfos(weaorzod_detail);
							Loger.i("TEST", "topic is empty yes");
							yLProgressDialog.dismiss();
						}else{
							if("empty".equals(Flag)){
								cardsList.setVisibility(View.GONE);
								nonewsview.setVisibility(View.VISIBLE);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					// TODO Auto-generated method stub
					new ErrorServer(WeatherListActivity.this, responseString.toString());
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		}
	}
	
	private void getNewsInfos(JSONArray response){
		int responseLen = response.length();
		Loger.e("testaaaa",""+ response);
		if(responseLen>0){
		/**************************new add**************************/
			for (int i = responseLen-1; i >-2 ; i--) {
			try {
				
				
				JSONObject weaorzod = response.getJSONObject(i);
				int id = weaorzod.getInt("weaorzod_id");
				String time = weaorzod.getString("wpr_time");
				JSONObject wpr_detail=weaorzod.getJSONObject("wpr_detail");
				JSONObject zod_info=weaorzod.getJSONObject("zod_info");
				String info=zod_info.getString("zodiac_summary");
				String img=zod_info.getString("zodiac_name");
				String imgUrl=zod_info.getString("zodiac_imgurl");
				JSONObject realtime=wpr_detail.getJSONObject("realtime");
				JSONObject weather=realtime.getJSONObject("weather");
				int nowwendu = Integer.parseInt(weather.getString("temperature"));
				String nowweather = weather.getString("info");
				JSONArray weather2=wpr_detail.getJSONArray("weather");
				JSONArray day = weather2.getJSONObject(0).getJSONObject("info").getJSONArray("day");
				JSONArray night = weather2.getJSONObject(0).getJSONObject("info").getJSONArray("night");
				int low=Integer.parseInt(night.getString(2));
				int high=Integer.parseInt(day.getString(2));
				Loger.e("test","111111111111----"+ low);
				String time1="明天";
				String time2="后天";
//				int time1_wendu =  Integer.parseInt(weather2.getJSONObject(1).getJSONObject("info").getJSONArray("dawn").getString(2));
//				int time2_wendu = Integer.parseInt(weather2.getJSONObject(2).getJSONObject("info").getJSONArray("dawn").getString(2));
				String time1_wendu = weather2.getJSONObject(1).getJSONObject("info").getJSONArray("night").getString(2)+ "°/"+weather2.getJSONObject(1).getJSONObject("info").getJSONArray("day").getString(2)+ "°";
				String time2_wendu = weather2.getJSONObject(2).getJSONObject("info").getJSONArray("night").getString(2)+ "°/"+weather2.getJSONObject(2).getJSONObject("info").getJSONArray("day").getString(2)+ "°";
				String position = wpr_detail.getJSONObject("pm25").getString("cityName");
//				Loger.d("test4", "new_id="+id+"position="+position+"time="+time+"nowwendu="+nowwendu);
				CardsBean news=new CardsBean();
				news.setTime(time);
				news.setId(id);
//				news.setInfo_id(info_id);
				news.setPosition(position);
				news.setNowwendu(nowwendu);
				news.setNowweather(nowweather);
				news.setTime1(time1);
				news.setTime2(time2);
				news.setLow(low);
				news.setHigh(high);
				news.setTime1_wendu(time1_wendu);
				news.setTime2_wendu(time2_wendu);
				news.setInfo(info);
				news.setImag(img);
				news.setImagUrl(imgUrl);
				mylist.add(news);
			
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			}
		myadapter.notifyDataSetChanged();
		cardsList.getRefreshableView().setSelection(mylist.size());
		}
		
		
	}
	
	private void getHttpfornews(final boolean flag,int id){
		RequestParams reference = new RequestParams();
		reference.put("community_id", App.sFamilyCommunityId);	
		reference.put("tag", "getweaorzod");
		if(flag){
		reference.put("action_id", "1");
		reference.put("weaorzod_id", id);
		}
		
		else {
			reference.put("action_id", "2");
			reference.put("weaorzod_id", id);
		}
		reference.put("apitype", IHttpRequestUtils.APITYPE[1]);
		//reference.put("key_word",searchmessage);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
				reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub 18945051215
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				try {
					JSONArray array = null;
					Flag = response.getString("flag");
					if("ok".equals(Flag)){
						array=response.getJSONArray("weaorzod_detail");
					}
					if("ok".equals(Flag)){
						bPullUpStatus=true;
						getNewsDetailsInfosforRef(array,flag);
					}else if("empty".equals(Flag)){
						runOnUiThread(new Runnable() {
							public void run() {
								cardsList.onRefreshComplete();
							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				new ErrorServer(WeatherListActivity.this, responseString.toString());
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	private void getNewsDetailsInfosforRef(JSONArray response,Boolean flag){
		int responseLen = response.length();
		if(responseLen>0){
		
		/**************************new add**************************/
		for (int i =0 ; i <responseLen ; i++) {			
			try {
				JSONObject weaorzod = new JSONObject(response.getString(i));
				int id = weaorzod.getInt("weaorzod_id");
				String time = weaorzod.getString("wpr_time");
				JSONObject wpr_detail=weaorzod.getJSONObject("wpr_detail");
				JSONObject zod_info=weaorzod.getJSONObject("zod_info");
				String info=zod_info.getString("zodiac_summary");
				String img=zod_info.getString("zodiac_name");
				String imgUrl=zod_info.getString("zodiac_imgurl");
				JSONObject realtime=wpr_detail.getJSONObject("realtime");
				JSONObject weather=realtime.getJSONObject("weather");
				int nowwendu = Integer.parseInt(weather.getString("temperature"));
				String nowweather = weather.getString("info");
				JSONArray weather2=wpr_detail.getJSONArray("weather");
				JSONArray day = weather2.getJSONObject(0).getJSONObject("info").getJSONArray("day");
				JSONArray night = weather2.getJSONObject(0).getJSONObject("info").getJSONArray("night");
				int low=Integer.parseInt(night.getString(2));
				int high=Integer.parseInt(day.getString(2));
				String time1="明天";
				String time2="后天";
				String time1_wendu = weather2.getJSONObject(1).getJSONObject("info").getJSONArray("night").getString(2)+ "°/"+weather2.getJSONObject(1).getJSONObject("info").getJSONArray("day").getString(2)+ "°";
				String time2_wendu = weather2.getJSONObject(2).getJSONObject("info").getJSONArray("night").getString(2)+ "°/"+weather2.getJSONObject(2).getJSONObject("info").getJSONArray("day").getString(2)+ "°";
				String position = wpr_detail.getJSONObject("pm25").getString("cityName");
				CardsBean news=new CardsBean();
				news.setTime(time);
				news.setId(id);
				news.setPosition(position);
				news.setNowwendu(nowwendu);
				news.setNowweather(nowweather);
				news.setTime1(time1);
				news.setTime2(time2);
				news.setLow(low);
				news.setHigh(high);
				news.setTime1_wendu(time1_wendu);
				news.setTime2_wendu(time2_wendu);
				news.setInfo(info);
				news.setImag(img);
				news.setImagUrl(imgUrl);
				if(flag){
					mylist.add(0,news);
				}else{
					mylist.add(news);
				}
//				newsdb.saveObject(news);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		}
//		if(flag){
//			myadapter.replaceList(mylist);
//		}else {
//			myadapter.addList(mylist);
//		}
		runOnUiThread(new Runnable() {
			public void run() {
				myadapter.notifyDataSetChanged();
				if(!WeatherListActivity.this.flag){
					cardsList.getRefreshableView().setSelection(mylist.size());	
				}
				
				
				cardsList.onRefreshComplete();
			}
		});
		
//		getData(NewsLists,response,flag);
//		maddapter = new NewsListAdapter(this,NewsLists,R.layout.news_list_item,
//        		new String[] {"firsturl","firsttitle","firstlinkurl","othernews"},new int[]{R.id.newsone_img,R.id.newsone_title,R.id.newsforothers});
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if(android.R.id.home == item.getItemId()){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (refreshView.isShownHeader()) {
			// 判断尾布局是否可见，如果可见执行上拉加载更多
			// 设置尾布局样式文字
			cardsList.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
			cardsList.getLoadingLayoutProxy().setPullLabel("加载更多历史");
			cardsList.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
			flag = true;
			if(mylist.size()>0){
				getHttpfornews(flag, mylist.get(0).getId());
			}
			

		} else if (refreshView.isShownFooter()) {
			cardsList.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
			cardsList.getLoadingLayoutProxy().setPullLabel("查看最新内容");
			cardsList.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
			if(mylist.size()>0){
				getHttpfornews(false, mylist.get(mylist.size()-1).getId());
			}
			
		} // TODO Auto-generated method stub
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						cardsList.onRefreshComplete();
					}
				});
			}
		}, 10000);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
