package com.nfs.youlin.activity.titlebar.barter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.GalleryPictureActivity;
import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoActivity;
import com.nfs.youlin.activity.neighbor.ReportActivity;
import com.nfs.youlin.activity.neighbor.SharePopWindow;
import com.nfs.youlin.activity.neighbor.SharePopWindowBarter;
import com.nfs.youlin.activity.personal.CollectionActivity;
import com.nfs.youlin.activity.personal.FriendInformationActivity;
import com.nfs.youlin.activity.personal.MyPushActivity;
import com.nfs.youlin.activity.personal.PersonalInformationActivity;
import com.nfs.youlin.activity.titlebar.startactivity.zoom.ViewPagerFixed;
import com.nfs.youlin.adapter.CommentDetailAdapter;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Bimp;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class BarterDedailActivity extends Activity implements OnClickListener{
	private final int REQUEST_CODE_REPORT_DETAIL = 9527;
	ImageLoader imageLoader;
	public ViewPagerFixed pager;
	private String url;
	public ArrayList<View> listViews = null;
	public MyPageAdapter adapter;
	RelativeLayout addressLayout;
	RelativeLayout reportLayout;
	TextView countTv;
	private int listposition;
	private int parentclass;
	Bimp bimp;
	private FriendCircleAdapter currentadapter;
	private List<Object> forumtopicLists ;
	public static TextView liuYanTv;
	long topicId;
	public static LinearLayout morelayout;
	String jingDuStr;
	String weiDuStr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_barter_detail);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("详情");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		bimp=new Bimp();
		addressLayout=(RelativeLayout)findViewById(R.id.address_layout);
		reportLayout=(RelativeLayout)findViewById(R.id.report_layout);
		countTv=(TextView)findViewById(R.id.barter_cout_tv);
		addressLayout.setOnClickListener(this);
		reportLayout.setOnClickListener(this);
		imageLoader = ImageLoader.getInstance();
		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		if (listViews == null){
			listViews = new ArrayList<View>();
		}else{
			listViews.clear();
		}
		Intent intent = getIntent();
		if(REQUEST_CODE_REPORT_DETAIL==intent.getIntExtra("report", 0)){
			setResult(REQUEST_CODE_REPORT_DETAIL);
		}
		url=intent.getStringExtra("url");
		JSONArray imgJsonArray;
		try {
			if (url != null) {
				imgJsonArray = new JSONArray(url);
				for (int i = 0; i < imgJsonArray.length(); i++) {
					JSONObject jsonObject= new JSONObject(imgJsonArray.getString(i));
					String smallUrl=jsonObject.getString("resPath");
					String bigUrl=smallUrl.substring(0,smallUrl.lastIndexOf("/"))+"/"+"0"+smallUrl.split("[/]")[9];
					//Loger.i("youlin", "url-->" +bigUrl);
					initListViews(bigUrl);
				}
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);	
		//pager.setPageMargin((int)getResources().getDimensionPixelOffset(R.dimen.ui_10_dip));
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				countTv.setText(arg0+1+"/"+listViews.size());
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		countTv.setText("1/"+listViews.size());
		listposition=intent.getIntExtra("position", 0);
		parentclass=intent.getIntExtra("parent", 0);
		if(parentclass==1){
			forumtopicLists = FriendCircleFragment.allList;
			currentadapter = FriendCircleFragment.maddapter;
		}else if(parentclass==0){
			forumtopicLists = TitleBarSearchActivity.forumtopicLists;
			currentadapter = TitleBarSearchActivity.maddapter;
		}else if(parentclass==2){
			forumtopicLists = MyPushActivity.forumtopicLists;
			currentadapter = MyPushActivity.maddapter;
		}else if(parentclass==3){
			forumtopicLists = CollectionActivity.forumtopicLists;
			currentadapter = CollectionActivity.maddapter;
		}else if(parentclass==4){
			//管理员推送
			forumtopicLists = NewPushRecordAbsActivity.forumtopicLists;
			currentadapter = new FriendCircleAdapter(forumtopicLists,this, 0, 4);
		}else if(parentclass==6){ //(parentclass==5/从细节进来的
			forumtopicLists = PropertyGonggaoActivity.forumtopicLists;
			currentadapter = PropertyGonggaoActivity.maddapter;
		}else if(parentclass==7){ //(parentclass==5/从细节进来的
			forumtopicLists = PropertyAdviceActivity.forumtopicLists;
			currentadapter = PropertyAdviceActivity.maddapter;
		}
		
		ImageView headImg=(ImageView)findViewById(R.id.barter_head_img);
		String headUrl = String.valueOf(((ForumTopic)forumtopicLists.get(listposition)).getSender_portrait());
		if(headUrl==null||headUrl.equals("")){
			headUrl = "null";
		}
		final String headUrl2=headUrl;
		imageLoader.displayImage(headUrl, headImg,App.options_account);
		headImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(((ForumTopic)forumtopicLists.get(listposition)).getSender_id()==App.sUserLoginId){
					Intent intent = new Intent(BarterDedailActivity.this,PersonalInformationActivity.class);
					intent.putExtra("type", 1);
					intent.putExtra("topic_id", ((ForumTopic)forumtopicLists.get(listposition)).getTopic_id());
					intent.putExtra("position", listposition);
					intent.putExtra("parent", parentclass);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				}else{
					Intent intent = new Intent(BarterDedailActivity.this,FriendInformationActivity.class);
					intent.putExtra("sender_id",((ForumTopic)forumtopicLists.get(listposition)).getSender_id());
					intent.putExtra("display_name",((ForumTopic)forumtopicLists.get(listposition)).getDisplay_name());
					intent.putExtra("sender_portrait",headUrl2);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				}
			}
		});
		TextView nameTv=(TextView)findViewById(R.id.barter_name_tv);
		nameTv.setText(((ForumTopic)forumtopicLists.get(listposition)).getSender_name());
		TextView priceTv=(TextView)findViewById(R.id.price_tv);
		TextView newOldTv=(TextView)findViewById(R.id.barter_new_old_tv);
		try {
			JSONArray barterJSONArray = new JSONArray(((ForumTopic)forumtopicLists.get(listposition)).getObject_data().toString());
			priceTv.setText(((JSONObject)barterJSONArray.get(0)).getString("price"));
			String newOldStr=((JSONObject)barterJSONArray.get(0)).getString("oldornew");
			jingDuStr=((JSONObject)barterJSONArray.get(0)).getString("communityLng");
			weiDuStr=((JSONObject)barterJSONArray.get(0)).getString("communityLag");
			Loger.i("TEST", "55555555555555555555555---->"+barterJSONArray.toString());
			if(newOldStr.equals("0")){
				newOldTv.setText("全新");
			}else if(newOldStr.equals("1")){
				newOldTv.setText("九成新");
			}else if(newOldStr.equals("2")){
				newOldTv.setText("八成新");
			}else if(newOldStr.equals("3")){
				newOldTv.setText("七成新");
			}else if(newOldStr.equals("4")){
				newOldTv.setText("六成新");
			}else if(newOldStr.equals("5")){
				newOldTv.setText("五成新");
			}else if(newOldStr.equals("6")){
				newOldTv.setText("五成新以下");
			}
			TextView browseTv=(TextView)findViewById(R.id.barter_browse_tv);
			int browseNum=((ForumTopic)forumtopicLists.get(listposition)).getView_num();
			if(browseNum==0){
				browseTv.setText("0人浏览");
			}else{
				browseTv.setText(String.valueOf(browseNum)+"人浏览");
			}
			
			TextView contentTv=(TextView)findViewById(R.id.content_tv);
			contentTv.setText(((ForumTopic)forumtopicLists.get(listposition)).getTopic_content());
			TextView addressTv=(TextView)findViewById(R.id.address_default_tv);
			String addressStr=((ForumTopic)forumtopicLists.get(listposition)).getDisplay_name().split("[@]")[1];
			addressTv.setText(addressStr);
			liuYanTv=(TextView)findViewById(R.id.comment_tv);
			int commentCount=((ForumTopic)forumtopicLists.get(listposition)).getComment_num();
			liuYanTv.setText("留言"+commentCount);
			liuYanTv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(BarterDedailActivity.this,BarterDedailCommentActivity.class);
					intent.putExtra("topic_id", ((ForumTopic)forumtopicLists.get(listposition)).getTopic_id());
					startActivityForResult(intent, 10086);
				}
			});
			final TextView dianZanTv=(TextView)findViewById(R.id.want_tv);
			dianZanTv.setText(((ForumTopic)forumtopicLists.get(listposition)).getLike_num()+"人点赞");
			dianZanTv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(((ForumTopic)forumtopicLists.get(listposition)).getSender_id()!=App.sUserLoginId){
						AllFamilyDaoDBImpl curfamilyDaoDBImpl=new AllFamilyDaoDBImpl(BarterDedailActivity.this);
						if(curfamilyDaoDBImpl.getCurrentAddrDetail("132").getEntity_type()==1){
						dianZanTv.setEnabled(false);
						final Timer barterTimer=new Timer();
						barterTimer.schedule(new TimerTask() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									dianZanTv.setEnabled(true);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}, 10000);
						final ForumTopic forumTopicObj = (ForumTopic) forumtopicLists.get(listposition);
						RequestParams params = new RequestParams();
						params.put("user_id", App.sUserLoginId);
						params.put("topic_id", topicId);
						
						if(forumTopicObj.getLike_status()==0){
							params.put("type", 1);
						}else{
							params.put("type", 0);
						}
						params.put("tag", "hitpraise");
						params.put("apitype", IHttpRequestUtils.APITYPE[5]);
						AsyncHttpClient request = new AsyncHttpClient();
						request.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, 
								params, new JsonHttpResponseHandler(){
							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
									try {
										barterTimer.cancel();
										ForumtopicDaoDBImpl forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(BarterDedailActivity.this);
										JSONObject jsonObject = new JSONObject(response.toString());
										int nLikeNum = 0;
										try {
											nLikeNum = Integer.parseInt(jsonObject.getString("likeNum"));
										} catch (NumberFormatException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										int nType = Integer.parseInt(jsonObject.getString("hitStatus"));
										if(nType == 1){
											//imageView.setImageResource(R.drawable.dianzan_2);
											try {
												dianZanTv.setText(String.valueOf(nLikeNum)+"人点赞");
											} catch (Exception e) {
												e.printStackTrace();
											}
										}else{
											//imageView.setImageResource(R.drawable.dianzan);
											try {
												if(nLikeNum==0){
													dianZanTv.setText("0人点赞");
												}else{
													dianZanTv.setText(String.valueOf(nLikeNum)+"人点赞");
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
										forumtopicDaoDBImplObj.modifyObject(forumTopicObj, 1);//like
										dianZanTv.setEnabled(true);
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
					}else{
						Toast.makeText(BarterDedailActivity.this, "您的地址还未经过验证", Toast.LENGTH_SHORT).show();
					}
					}
				}
			});
			TextView chatTv=(TextView)findViewById(R.id.barter_chat_tv);
			chatTv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AccountDaoDBImpl account = new AccountDaoDBImpl(BarterDedailActivity.this);
					Intent intent = new Intent(BarterDedailActivity.this, com.easemob.chatuidemo.activity.ChatActivity.class);
			        intent.putExtra("userId",String.valueOf(((ForumTopic)forumtopicLists.get(listposition)).getSender_id()));
			        intent.putExtra("chatType", 1);
			        intent.putExtra("usernick",((ForumTopic)forumtopicLists.get(listposition)).getDisplay_name());
			        String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
			        intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
			        Loger.i("NEW", "((ForumTopic)forumtopicLists.get(listposition)).getSender_portrait()->"+((ForumTopic)forumtopicLists.get(listposition)).getSender_portrait());
			        intent.putExtra("neighborurl", ((ForumTopic)forumtopicLists.get(listposition)).getSender_portrait());
			        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			        startActivity(intent);
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addViewNum();
		updateCommNum();
		topicId=((ForumTopic)forumtopicLists.get(listposition)).getTopic_id();
	}

	private void initListViews(String url) {
//		RelativeLayout relativeLayout=new RelativeLayout(BarterDedailActivity.this);
//		relativeLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ImageView img = new ImageView(BarterDedailActivity.this);
		img.setBackgroundColor(0xff000000);
		//img.setImageBitmap(bm);
		img.setScaleType(ScaleType.CENTER_CROP);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//		Picasso.with(mContext)
//		.load(url)
//		.tag(mContext)
//		.into(img);
		imageLoader.displayImage(url, img, App.options_error);
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(BarterDedailActivity.this,GalleryPictureActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("ID", pager.getCurrentItem());
				intent.putExtra("url",BarterDedailActivity.this.url);
				startActivity(intent);
			}
		});
		listViews.add(img);
	}
	
	public class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;
		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);
			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
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
	@Override
	public void onClick(View view) {
		int id = view.getId();
		if(id == R.id.address_layout){
			Intent intent=new Intent(BarterDedailActivity.this,BarterBaiduMapActivity.class);
			Loger.i("NEW", jingDuStr+" "+weiDuStr);
			intent.putExtra("jingdu", jingDuStr);
			intent.putExtra("weidu", weiDuStr);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
		}
		if(id == R.id.report_layout){
			Bundle bundle=new Bundle();
			bundle.putLong("topic_id", ((ForumTopic)forumtopicLists.get(listposition)).getTopic_id());
			bundle.putLong("community_id", ((ForumTopic)forumtopicLists.get(listposition)).getSender_community_id());
			bundle.putLong("sender_id", ((ForumTopic)forumtopicLists.get(listposition)).getSender_id());
			startActivity(new Intent(BarterDedailActivity.this,ReportActivity.class).putExtra("ID", bundle).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		}
	}

	
	private void addViewNum(){
		RequestParams params = new RequestParams();
		params.put("tag", "intotopic");
		params.put("apitype", IHttpRequestUtils.APITYPE[5]);
		params.put("user_id", App.sUserLoginId);
		params.put("topic_id", ((ForumTopic)forumtopicLists.get(listposition)).getTopic_id());
		params.put("community_id", ((ForumTopic)forumtopicLists.get(listposition)).getSender_community_id());
		AsyncHttpClient request = new AsyncHttpClient();
		request.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
				params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				try {
					JSONObject jsonObject = new JSONObject(response.toString());
					String httpFlag = jsonObject.getString("flag");
					if("ok".equals(httpFlag)){
						int httpViewNum = Integer.parseInt(jsonObject.getString("viewNum"));
						ForumtopicDaoDBImpl daoDBImpl = new ForumtopicDaoDBImpl(BarterDedailActivity.this);
						ForumTopic topic = (ForumTopic)forumtopicLists.get(listposition);
						daoDBImpl.modifyObject(topic, 2);
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
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		
	}
	private void updateCommNum(){
		RequestParams params = new RequestParams();
		params.put("tag", "getcommentcount");
		params.put("apitype", IHttpRequestUtils.APITYPE[5]);
		params.put("user_id", App.sUserLoginId);
		params.put("topic_id", ((ForumTopic)forumtopicLists.get(listposition)).getTopic_id());
		AsyncHttpClient request = new AsyncHttpClient();
		request.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
				params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub22
				try {
					String flag = response.getString("flag");
					String count = response.getString("count");
					if(flag.equals("ok")){
						liuYanTv.setText("留言"+count);
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
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.circle_detail, menu);
		morelayout=(LinearLayout) menu.findItem(R.id.circle_detail_menu).getActionView();
		morelayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ForumTopic forumTopic = (ForumTopic)forumtopicLists.get(listposition);
				new SharePopWindowBarter(BarterDedailActivity.this,forumTopic,parentclass,listposition);
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==10086){
			gethttpdata(((ForumTopic) forumtopicLists.get(listposition)).getTopic_id(),1);
		}
		
	}
	private void gethttpdata(long topicId,int request_index){
		if(NetworkService.networkBool){
		RequestParams reference = new RequestParams();
		try {
			reference.put("community_id",App.sFamilyCommunityId);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			reference.put("community_id",0);
			e1.printStackTrace();
		}
		reference.put("user_id",App.sUserLoginId);
		reference.put("count", 1);
		reference.put("topic_id",topicId);
		reference.put("type", 2);
		reference.put("tag","gettopic");
		reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
		//Loger.i("youlin", "3333333333333333333333--->"+App.sFamilyCommunityId+" "+App.sUserLoginId+" "+topicId);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+ IHttpRequestUtils.YOULIN,reference,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,Header[] headers,JSONArray response) {
								Loger.i("TEST",response.toString());
								getTopicDetailsInfos(response);
								if(currentadapter!=null){
									currentadapter.notifyDataSetChanged();
								}
								super.onSuccess(statusCode,headers,response);
							}
							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
								// TODO Auto-generated method stub
									try {
										String flag = response.getString("flag");
										if(flag.equals("no")){
											final PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(BarterDedailActivity.this);
											daoDBImpl.deleteObject(((ForumTopic)forumtopicLists.get(listposition)).getTopic_id());
											if(App.sFamilyCommunityId != 0 || App.sFamilyCommunityId != null){
												if(parentclass==1){
													FriendCircleFragment.allList.remove(listposition);
													FriendCircleFragment.maddapter.notifyDataSetChanged();
												}else if(parentclass==0){
													TitleBarSearchActivity.forumtopicLists.remove(listposition);
													TitleBarSearchActivity.maddapter.notifyDataSetChanged();
												}else if(parentclass==2){
													MyPushActivity.forumtopicLists.remove(listposition);
													MyPushActivity.maddapter.notifyDataSetChanged();
												}else if(parentclass==3){
													//CollectionActivity.forumtopicLists.remove(listposition);
													((ForumTopic)CollectionActivity.forumtopicLists.get(listposition)).setdeleteflag("2");
													((ForumTopic)currentadapter.data.get(listposition)).setdeleteflag("2");
													CollectionActivity.maddapter.notifyDataSetChanged();
												}else if(parentclass==4){
													//管理员推送
													//NewPushRecordAbsActivity.forumtopicLists.remove(listposition);
													//forumtopicLists = NewPushRecordAbsActivity.forumtopicLists;
													//currentadapter = new FriendCircleAdapter(forumtopicLists,BarterDedailActivity.this, 0, 4);
												}else if(parentclass==6){ //(parentclass==5/从细节进来的
													PropertyGonggaoActivity.forumtopicLists.remove(listposition);
													PropertyGonggaoActivity.maddapter.notifyDataSetChanged();
												}else if(parentclass==7){ //(parentclass==5/从细节进来的
													PropertyAdviceActivity.forumtopicLists.remove(listposition);
													PropertyAdviceActivity.maddapter.notifyDataSetChanged();
												}
												Toast toast=Toast.makeText(BarterDedailActivity.this, "此贴已经不可见", Toast.LENGTH_SHORT);
												toast.setGravity(Gravity.CENTER, 0, 0);
												toast.show();
											}
											App.Detailactivityisshown = false;
											finish();
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							}
							@Override
							public void onFailure(int statusCode,Header[] headers,String responseString,Throwable throwable) {
								new ErrorServer(BarterDedailActivity.this, responseString);
								super.onFailure(statusCode,headers,responseString,throwable);
							}
						});
		}else{
			//Toast.makeText(BarterDedailActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
		}
	}
	private void getTopicDetailsInfos(JSONArray response){
			int responseLen = response.length();
			Loger.i("TEST", "json obj length->"+responseLen);
		
			for (int i = 0; i < responseLen; i++) {
				try {
//					Loger.i("TEST", "mediaJson->start");
					JSONObject jsonObject = new JSONObject(response.getString(i));
					String comment_num = jsonObject.getString("commentNum");
					App.CurrentSysTime = Long.parseLong(jsonObject.getString("systemTime"));
					if(comment_num==null || comment_num.isEmpty() || comment_num.length()<=0 || comment_num == "null"){
						comment_num = "0";
					}
					liuYanTv.setText("留言"+comment_num);
					ForumTopic forumTopic=((ForumTopic) forumtopicLists.get(listposition));
					forumTopic.setComment_num(Integer.parseInt(comment_num));
					forumtopicLists.set(listposition, forumTopic);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "ERROR->"+e.getMessage());
					e.printStackTrace();
				}
			}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			gethttpdata(((ForumTopic)forumtopicLists.get(listposition)).getTopic_id(), 1);
			App.Detailactivityisshown = false;
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==android.R.id.home){
			gethttpdata(((ForumTopic)forumtopicLists.get(listposition)).getTopic_id(), 1);
			App.Detailactivityisshown = false;
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
