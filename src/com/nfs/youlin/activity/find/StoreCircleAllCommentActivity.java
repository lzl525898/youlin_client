package com.nfs.youlin.activity.find;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

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

import com.baidu.mapapi.map.Text;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.InitTransparentActivity;
import com.nfs.youlin.activity.SplashActivity;
import com.nfs.youlin.activity.neighbor.GalleryPictureActivity;
import com.nfs.youlin.activity.personal.Exchanged_giftactivity;
import com.nfs.youlin.activity.personal.FriendInformationActivity;
import com.nfs.youlin.activity.personal.PersonalInformationActivity;
import com.nfs.youlin.adapter.FriendCircleImageAdapter;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.SampleScrollListener;
import com.nfs.youlin.utils.TimeToStr;
import com.nfs.youlin.view.YLProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StoreCircleAllCommentActivity extends Activity implements OnClickListener, OnRefreshListener<ListView> {
	PullToRefreshListView storeListView;
	List<Map<String, Object>> storeAllList=new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> storePicList=new ArrayList<Map<String, Object>>();
	ViewHolder viewHolder;
	TextView noMoreTv;
	StoreAdapter storeAdapter;
	TextView allTv;
	TextView allNumTv;
	View allView;
	TextView picTv;
	TextView picNumTv;
	View picView;
	int onclick=1; //1是全部	2是晒图
	YLProgressDialog yLProgressDialog;
	ImageLoader imageLoader;
	String uid;
	String endId="0";
	Timer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_circle_all_comment);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("用户评价");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		uid=getIntent().getStringExtra("uid");
		imageLoader = ImageLoader.getInstance();
		yLProgressDialog = YLProgressDialog.createDialogwithcircle(StoreCircleAllCommentActivity.this,"",1);
		storeListView = (PullToRefreshListView) findViewById(R.id.store_list);
		storeListView.setMode(Mode.PULL_FROM_END);
		storeListView.setOnRefreshListener(this);
		storeAdapter=new StoreAdapter(storeAllList);
		storeListView.setAdapter(storeAdapter);
		noMoreTv = (TextView) findViewById(R.id.no_more_store);
		allTv=(TextView)findViewById(R.id.all_tv);
		allNumTv=(TextView)findViewById(R.id.all_num);
		allView=(View)findViewById(R.id.all_view);
		picTv=(TextView)findViewById(R.id.pic_tv);
		picNumTv=(TextView)findViewById(R.id.pic_num);
		picView=(View)findViewById(R.id.pic_view);
		
		findViewById(R.id.all_layout).setOnClickListener(this);
		findViewById(R.id.pic_layout).setOnClickListener(this);
		initRequst(false,1);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (refreshView.isShownFooter()) {
			timer=new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					storeListView.onRefreshComplete();
				}
			}, 10000);
			initRequst(true,onclick);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.all_layout:
			onclick=1;
			allTv.setTextColor(Color.parseColor("#ffba02"));
			allNumTv.setTextColor(Color.parseColor("#ffba02"));
			allView.setBackgroundColor(Color.parseColor("#ffba02"));
			picTv.setTextColor(Color.parseColor("#808080"));
			picNumTv.setTextColor(Color.parseColor("#808080"));
			picView.setBackgroundColor(Color.parseColor("#ffffff"));
			if(storeAllList.size()==0){
				yLProgressDialog.show();
				initRequst(false,1);
			}else{
				yLProgressDialog.dismiss();
				storeAdapter=new StoreAdapter(storeAllList);
				storeListView.setAdapter(storeAdapter);
				storeAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.pic_layout:
			onclick=2;
			picTv.setTextColor(Color.parseColor("#ffba02"));
			picNumTv.setTextColor(Color.parseColor("#ffba02"));
			picView.setBackgroundColor(Color.parseColor("#ffba02"));
			allTv.setTextColor(Color.parseColor("#808080"));
			allNumTv.setTextColor(Color.parseColor("#808080"));
			allView.setBackgroundColor(Color.parseColor("#ffffff"));
			if(storePicList.size()==0){
				yLProgressDialog.show();
				initRequst(false,2);
			}else{
				yLProgressDialog.dismiss();
				storeAdapter=new StoreAdapter(storePicList);
				storeListView.setAdapter(storeAdapter);
				storeAdapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}

	private void initRequst(final boolean isRefresh,final int onclick) { //1是全部      2是晒图 
		RequestParams params = new RequestParams();
		if(!isRefresh){
			params.put("uid", uid);
			params.put("tag", "getbizcireva");
			params.put("apitype", "address");
			params.put("bceid", 0);
			params.put("action_id", 0);
			params.put("community_id", App.sFamilyCommunityId);
			if(onclick==1){
				params.put("target", 0);
			}else if(onclick==2){
				params.put("target", 1);
			}
		}else{
			params.put("uid", uid);
			params.put("tag", "getbizcireva");
			params.put("apitype", "address");
			params.put("bceid", endId);
			params.put("action_id", 1);
			params.put("community_id", App.sFamilyCommunityId);
			if(onclick==1){
				params.put("target", 0);
				Loger.i("LYM", "55555---->"+uid+" "+endId+" "+App.sFamilyCommunityId);
			}else if(onclick==2){
				params.put("target", 1);
			}
		}
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				try {
					if(timer!=null){
						timer.cancel();
					}
					String flag = response.getString("flag");
					Loger.i("LYM", "6666666666---->"+flag);
					if ("no".equals(flag)) {
						noMoreTv.setVisibility(View.VISIBLE);
						if(isRefresh){
							storeAdapter.notifyDataSetChanged();
							storeListView.onRefreshComplete();
						}else{
							yLProgressDialog.dismiss();
							if(onclick==1){
								storeAdapter=new StoreAdapter(storeAllList);
							}else if(onclick==2){
								storeAdapter=new StoreAdapter(storePicList);
							}
							storeListView.setAdapter(storeAdapter);
							storeAdapter.notifyDataSetChanged();
						}
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								runOnUiThread(new Runnable() {
									public void run() {
										noMoreTv.setVisibility(View.GONE);
									}
								});
							}
						}, 1000);
					}else if("ok".equals(flag)){
						App.CurrentSysTime=Long.parseLong(response.getString("systime"));
						allNumTv.setText("（"+response.getString("allcount")+"）");
						picNumTv.setText("（"+response.getString("imgcount")+"）");
						JSONArray jsonArray=new JSONArray(response.getString("querylist"));
						endId=response.getString("id");
						if(onclick==1){
							getAllData(isRefresh,jsonArray);
						}else if(onclick==2){
							getPicData(isRefresh, jsonArray);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(StoreCircleAllCommentActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	private void getAllData(final boolean isRefresh,JSONArray jsonArray ) {
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("status", ((JSONObject) jsonArray.get(i)).get("status"));
				map.put("userid", ((JSONObject) jsonArray.get(i)).get("userid"));
				map.put("head", ((JSONObject) jsonArray.get(i)).get("avatar"));
				map.put("name", ((JSONObject) jsonArray.get(i)).get("nick"));
				map.put("time", ((JSONObject) jsonArray.get(i)).get("time"));
				map.put("star", ((JSONObject) jsonArray.get(i)).get("facility"));
				map.put("content", ((JSONObject) jsonArray.get(i)).get("content"));
				map.put("picture", ((JSONObject) jsonArray.get(i)).get("media"));
				storeAllList.add(map);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		runOnUiThread(new Runnable() {
			public void run() {
				if(isRefresh){
					storeAdapter.notifyDataSetChanged();
					storeListView.onRefreshComplete();
				}else{
					yLProgressDialog.dismiss();
					storeAdapter=new StoreAdapter(storeAllList);
					storeListView.setAdapter(storeAdapter);
					storeAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	private void getPicData(final boolean isRefresh, JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("status", ((JSONObject) jsonArray.get(i)).get("status"));
				map.put("userid", ((JSONObject) jsonArray.get(i)).get("userid"));
				map.put("head", ((JSONObject) jsonArray.get(i)).get("avatar"));
				map.put("name", ((JSONObject) jsonArray.get(i)).get("nick"));
				map.put("time", ((JSONObject) jsonArray.get(i)).get("time"));
				map.put("star", ((JSONObject) jsonArray.get(i)).get("facility"));
				map.put("content", ((JSONObject) jsonArray.get(i)).get("content"));
				map.put("picture", ((JSONObject) jsonArray.get(i)).get("media"));
				storePicList.add(map);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		runOnUiThread(new Runnable() {
			public void run() {
				if(isRefresh){
					storeAdapter.notifyDataSetChanged();
					storeListView.onRefreshComplete();
				}else{
					yLProgressDialog.dismiss();
					storeAdapter=new StoreAdapter(storePicList);
					storeListView.setAdapter(storeAdapter);
					storeAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	private class StoreAdapter extends BaseAdapter {
		List<Map<String, Object>> list;
		public StoreAdapter(List<Map<String, Object>> list) {
			// TODO Auto-generated constructor stub
			this.list=list;
			
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.list_store_item, null);
				viewHolder.headImg = (ImageView) convertView.findViewById(R.id.head_img);
				viewHolder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
				viewHolder.timeTv = (TextView) convertView.findViewById(R.id.time_tv);
				viewHolder.star = (RatingBar) convertView.findViewById(R.id.star);
				viewHolder.contentTv = (TextView) convertView.findViewById(R.id.content);
				viewHolder.gridView = (GridView) convertView.findViewById(R.id.gridView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
				
					imageLoader.displayImage((String) list.get(position).get("head"), viewHolder.headImg, App.options_account);
					viewHolder.headImg.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							try {
								if(Long.parseLong(list.get(position).get("userid").toString())==App.sUserLoginId){
									Intent intent = new Intent(StoreCircleAllCommentActivity.this,PersonalInformationActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
								}else {
										if(list.get(position).get("status").toString().equals("0")){
											Intent intent = new Intent(StoreCircleAllCommentActivity.this,FriendInformationActivity.class);
											intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
											intent.putExtra("sender_id",Long.parseLong(list.get(position).get("userid").toString()));
											intent.putExtra("display_name",(String) list.get(position).get("name"));
											intent.putExtra("sender_portrait",(String) list.get(position).get("head"));
											startActivity(intent);
										}else{
											
										}
								}
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					if(list.get(position).get("status").toString().equals("0")){
						viewHolder.nameTv.setText((String) list.get(position).get("name"));
					}else{
						viewHolder.nameTv.setText("匿名");
					}
					viewHolder.timeTv.setText(TimeToStr.getTimeElapse(Long.parseLong(list.get(position).get("time").toString()), App.CurrentSysTime));
					viewHolder.star.setRating(Float.valueOf(list.get(position).get("star").toString()));
					viewHolder.contentTv.setText((String) list.get(position).get("content"));
					viewHolder.gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
					final List<String> pictureList = new ArrayList<String>();
					JSONArray imgJsonArray = null;
					try {
						if (!list.get(position).get("picture").toString().equals("none")) {
							imgJsonArray = new JSONArray(list.get(position).get("picture").toString());
							for (int i = 0; i < imgJsonArray.length(); i++) {
								try {
									pictureList.add(imgJsonArray.getString(i));
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					viewHolder.gridView.setAdapter(new FriendCircleImageAdapter(StoreCircleAllCommentActivity.this, pictureList));
					viewHolder.gridView.setOnScrollListener(new SampleScrollListener(StoreCircleAllCommentActivity.this));
					viewHolder.gridView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
							// TODO Auto-generated method stub
							// selectID =
							// Integer.parseInt(((View)arg1.getParent()).getTag().toString());
							Intent intent = new Intent(StoreCircleAllCommentActivity.this, GalleryPictureStoreActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							intent.putExtra("ID", arg2);
							intent.putExtra("url", list.get(position).get("picture").toString());
							startActivity(intent);
						}
					});
				
			

			return convertView;
		}

	}

	static class ViewHolder {
		ImageView headImg;
		TextView nameTv;
		TextView timeTv;
		RatingBar star;
		TextView contentTv;
		GridView gridView;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==android.R.id.home){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
