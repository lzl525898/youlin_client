package com.nfs.youlin.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.jivesoftware.smackx.packet.Time;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActionBarclicklisener;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.find.NewsDetail;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailGalleryPictureActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.GalleryPictureActivity;
import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoActivity;
import com.nfs.youlin.activity.neighbor.SharePopWindow;
import com.nfs.youlin.activity.neighbor.SignUpDetailActivity;
import com.nfs.youlin.activity.personal.CollectionActivity;
import com.nfs.youlin.activity.personal.Collection_delensure;
import com.nfs.youlin.activity.personal.FriendInformationActivity;
import com.nfs.youlin.activity.personal.MyPushActivity;
import com.nfs.youlin.activity.personal.PersonalInformationActivity;
import com.nfs.youlin.activity.personal.moreaddrdetail;
import com.nfs.youlin.activity.titlebar.barter.BarterDedailActivity;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ClearSelectImg;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.SampleScrollListener;
import com.nfs.youlin.utils.StatusChangeListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.TimeClick;
//import com.nfs.youlin.utils.SharePopWindow;
import com.nfs.youlin.utils.TimeToStr;
import com.nfs.youlin.utils.applycount;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.CustomDialog;
import com.nfs.youlin.view.PopupMenuAddListView;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class FriendCircleAdapter extends BaseAdapter  implements OnClickListener,OnActivityResultListener{
	public List<Object> data;
	private Context context;
	private int selectID;
	private int myPush;
	private int myTopic;
	private int isgonggao;
	private String responseString;
	private ForumTopic forumTopicObj;
	private ForumtopicDaoDBImpl forumtopicDaoDBImplObj;
	private List<String> commentNameList;
	private List<String> commentContentList;
	private boolean setaddrRequestSet = false;
	
	public static String flag = "none";
	private static String applyflag = "false";
	private int searchordisplay;
	private int Ishost;
	private static TextView currentactionapplynum;
	private static TextView currentactionapplyname;
	private int REQUEST_APPLY_NUM = 200;
	public static boolean cRequestSet = false;
	private String applytotalcount;
	private String currenttotalcount;
	public static String dflag = "none";
	private String isDelString;
	private Boolean signUpBool = false;
	ImageLoader imageLoader;
	private boolean deleteflag = false;
	private StatusChangeutils changeutils;
	private Animation animation;
	public static Map<Integer, Boolean> isFristMap;
	public FriendCircleAdapter(List<Object>list, Context context,int myPush,int searchOrdisplay) { //searchOrdisplay=0  from search
		imageLoader = ImageLoader.getInstance();
		this.data = list;
		Loger.i("youlin","list.size()---->"+data.size());
		this.context = context;
		this.myPush = myPush;
		searchordisplay = searchOrdisplay;
		animation=AnimationUtils.loadAnimation(context, R.anim.list_anim_layout);
		isFristMap = new HashMap<Integer, Boolean>();
		File cacheDir = new File(context.getCacheDir()+File.separator+"imageloader/Cache");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public static void setapplcount(String count,String name){
		final String num = count;
		final String title = name;
				// TODO Auto-generated method stub
				currentactionapplynum.setText(num);
				currentactionapplyname.setText(title);

		if(name.equals("取消报名")){
			currentactionapplyname.setTag(1);
		}else{
			currentactionapplyname.setTag(0);
		}
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.list_circle_item, null);
			viewHolder=new ViewHolder();
			Loger.i("youlin","position------------------------------------>"+position);
			viewHolder.delete = (TextView)convertView.findViewById(R.id.cirlce_delete);
			viewHolder.shuaxin = (ProgressBar)convertView.findViewById(R.id.gengxin);
			//viewHolder.hint1 = (ImageView)convertView.findViewById(R.id.friend_head_img);
			//viewHolder.hint2 = (View)convertView.findViewById(R.id.view1);
			viewHolder.hint3 = (LinearLayout)convertView.findViewById(R.id.circle_item_lay);  // all layout
			viewHolder.cover = (ImageView)convertView.findViewById(R.id.cover);
			viewHolder.morelayout = (RelativeLayout)convertView.findViewById(R.id.morelayout);
			viewHolder.title_msg = (RelativeLayout)convertView.findViewById(R.id.title_msg);
			viewHolder.friendCircleLayout=(RelativeLayout)convertView.findViewById(R.id.friend_circle_layout);
			viewHolder.friendHeadImg=(ImageView)convertView.findViewById(R.id.friend_head_img);
			viewHolder.titleTv=(TextView)convertView.findViewById(R.id.topic_title);
			viewHolder.nameTv=(TextView)convertView.findViewById(R.id.topic_name);
			viewHolder.timeTv=(TextView)convertView.findViewById(R.id.topic_time);
			viewHolder.callImg=(ImageView)convertView.findViewById(R.id.topic_call);
			viewHolder.contentTv = (TextView) convertView.findViewById(R.id.content_neighbor);
			viewHolder.lookAllTv = (TextView) convertView.findViewById(R.id.look_all_tv);
			viewHolder.gridView = (GridView) convertView.findViewById(R.id.gridView);
			viewHolder.signUpLayout=(LinearLayout)convertView.findViewById(R.id.enter_name_layout);
			viewHolder.barterLayout=(RelativeLayout)convertView.findViewById(R.id.barter_layout);
			viewHolder.priceTv=(TextView)convertView.findViewById(R.id.circle_price_tv);
			viewHolder.newOldTv=(TextView)convertView.findViewById(R.id.circle_newold_tv);
			viewHolder.commentCount=(TextView)convertView.findViewById(R.id.comment_count);
			viewHolder.praise=(TextView)convertView.findViewById(R.id.dianzan_count);
			viewHolder.browseLayout=(LinearLayout) convertView.findViewById(R.id.browse_ll);
			viewHolder.browse=(TextView)convertView.findViewById(R.id.browse_count);
//			viewHolder.moreImg=(ImageView)convertView.findViewById(R.id.more_img);		
//			viewHolder.commentLv = (ListView) convertView.findViewById(R.id.comment_lv);
//			viewHolder.lookCommentTv = (TextView) convertView.findViewById(R.id.friend_comment_tv);
//			viewHolder.divider = convertView.findViewById(R.id.friend_divider);
			viewHolder.commentLayout = (ImageButton) convertView.findViewById(R.id.comment_img_bt);
			viewHolder.commentImg = (LinearLayout)convertView.findViewById(R.id.comment_img);
			viewHolder.dianzanLayount = (LinearLayout)convertView.findViewById(R.id.dianzan_ll);
			viewHolder.dianzanImageView = (ImageView)convertView.findViewById(R.id.dianzan_img);
			viewHolder.noinfo = (TextView)convertView.findViewById(R.id.adapter_noinfo_tv);
			viewHolder.newslay = (LinearLayout)convertView.findViewById(R.id.enter_news_layout);
			viewHolder.newspic = (ImageView)convertView.findViewById(R.id.enter_news_pic);
			viewHolder.newstitle = (TextView)convertView.findViewById(R.id.enter_news_title);
			viewHolder.overline = (ImageView)convertView.findViewById(R.id.overline);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
			/********************hyy add message detail*******************/
			try {
				//Loger.i("youlin","((ForumTopic)data.get(position)).getFlag()---->"+((ForumTopic)data.get(position)).getFlag());
				boolean refreshflag=false;
				try {
					refreshflag = ((ForumTopic)data.get(position)).getFlag();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					refreshflag = false;
					e1.printStackTrace();
				}
			final int staticposition;
			if (refreshflag) {
				if (FriendCircleFragment.bNoNewInfoStatus) {
					viewHolder.noinfo.setVisibility(View.VISIBLE);
					viewHolder.hint3.setVisibility(View.GONE);
					viewHolder.shuaxin.setVisibility(View.GONE);
					viewHolder.morelayout.setVisibility(View.GONE);
					viewHolder.overline.setVisibility(View.GONE);
					// FriendCircleFragment.allList.remove(FriendCircleFragment.allList.size()
					// - 1);
				} else {
					viewHolder.shuaxin.setVisibility(View.VISIBLE);
					viewHolder.noinfo.setVisibility(View.GONE);
					// viewHolder.hint1.setVisibility(View.INVISIBLE);
					// viewHolder.hint2.setVisibility(View.GONE);
					viewHolder.hint3.setVisibility(View.GONE);
					viewHolder.morelayout.setVisibility(View.GONE);
					viewHolder.overline.setVisibility(View.GONE);
				}

			} else {
					viewHolder.shuaxin.setVisibility(View.GONE);
					viewHolder.noinfo.setVisibility(View.GONE);
					viewHolder.overline.setVisibility(View.GONE);
					if(searchordisplay ==1){
						if(position == 0){
							viewHolder.hint3.setVisibility(View.GONE);
							viewHolder.morelayout.setVisibility(View.VISIBLE);
//							viewHolder.categorylayout = ((Activity) context).getLayoutInflater().inflate(R.layout.categorylay, viewHolder.morelayout);
							viewHolder.categorylay1 = (TextView)convertView.findViewById(R.id.layout1);
							viewHolder.categorylay2 = (TextView)convertView.findViewById(R.id.layout2);
							viewHolder.categorylay3 = (TextView)convertView.findViewById(R.id.layout3);
							viewHolder.categorylay4 = (TextView)convertView.findViewById(R.id.layout4);
							viewHolder.categorylay5 = (TextView)convertView.findViewById(R.id.layout5);
							viewHolder.categorylay6 = (TextView)convertView.findViewById(R.id.layout6);
							viewHolder.categorylay1.setBackgroundColor(0x000000000);
							viewHolder.categorylay2.setBackgroundColor(0x000000000);
							viewHolder.categorylay3.setBackgroundColor(0x000000000);
							viewHolder.categorylay4.setBackgroundColor(0x000000000);
							viewHolder.categorylay5.setBackgroundColor(0x000000000);
							viewHolder.categorylay6.setBackgroundColor(0x000000000);
							
							if(FriendCircleFragment.categoryindex==0){
								Loger.d("test1", "FriendCircleFragment.categoryindex==0");
								viewHolder.categorylay1.setBackgroundResource(R.drawable.bg_barter_new_old);
							}else if(FriendCircleFragment.categoryindex==1){
								Loger.d("test1", "FriendCircleFragment.categoryindex==1");
								viewHolder.categorylay2.setBackgroundResource(R.drawable.bg_barter_new_old);
							}else if(FriendCircleFragment.categoryindex==2){
								Loger.d("test1", "FriendCircleFragment.categoryindex==2");
								viewHolder.categorylay3.setBackgroundResource(R.drawable.bg_barter_new_old);
							}else if(FriendCircleFragment.categoryindex==3){
								Loger.d("test1", "FriendCircleFragment.categoryindex==3");
								viewHolder.categorylay4.setBackgroundResource(R.drawable.bg_barter_new_old);
							}else if(FriendCircleFragment.categoryindex==4){
								viewHolder.categorylay5.setBackgroundResource(R.drawable.bg_barter_new_old);
							}else if(FriendCircleFragment.categoryindex==5){
								viewHolder.categorylay6.setBackgroundResource(R.drawable.bg_barter_new_old);
							}
							viewHolder.categorylay1.setOnClickListener(this);
							viewHolder.categorylay2.setOnClickListener(this);
							viewHolder.categorylay3.setOnClickListener(this);
							viewHolder.categorylay4.setOnClickListener(this);
							viewHolder.categorylay5.setOnClickListener(this);
							viewHolder.categorylay6.setOnClickListener(this);
					
							return convertView;
//							staticposition = position;
						}else{
							viewHolder.hint3.setVisibility(View.VISIBLE);
							viewHolder.morelayout.setVisibility(View.GONE);
							position = position-1;
						}
					}else{
						if(searchordisplay == 3){
							if(((ForumTopic)data.get(position)).getdeleteflag().equals("2")){
								deleteflag = true;
//								viewHolder.friendCircleLayout.setBackgroundColor(0xc0000000);
								viewHolder.friendCircleLayout.setAlpha((float) 0.7);
								viewHolder.cover.setVisibility(View.VISIBLE);
							}else{
								deleteflag = false;
								viewHolder.friendCircleLayout.setAlpha(1);
								viewHolder.cover.setVisibility(View.INVISIBLE);
							}
							
						}
						Loger.i("LYM", "9999999999999--->");
						viewHolder.hint3.setVisibility(View.VISIBLE);
						viewHolder.morelayout.setVisibility(View.GONE);
//						position = position+1;
					}
					
//					else{
						staticposition = position;
						
						viewHolder.friendCircleLayout.setOnClickListener(this);
						viewHolder.title_msg.setOnClickListener(this);
						viewHolder.lookAllTv.setOnClickListener(this);
						viewHolder.contentTv.setOnClickListener(this);
						//viewHolder.lookCommentTv.setOnClickListener(this);
						viewHolder.commentCount.setOnClickListener(this);
						viewHolder.commentImg.setOnClickListener(this);
						viewHolder.delete.setOnClickListener(this);
						viewHolder.dianzanLayount.setOnClickListener(this);
						viewHolder.browseLayout.setOnClickListener(this);
						viewHolder.commentLayout.setOnClickListener(this);
						
						viewHolder.friendCircleLayout.setTag(position);
						viewHolder.title_msg.setTag(position);
						viewHolder.lookAllTv.setTag(position);
						viewHolder.contentTv.setTag(position);
						//viewHolder.lookCommentTv.setTag(position);
						//viewHolder.moreImg.setTag(position);
						viewHolder.commentCount.setTag(position);
						viewHolder.commentImg.setTag(position);
						viewHolder.commentLayout.setTag(position);
						viewHolder.delete.setTag(position);
						viewHolder.hint3.setTag(position);
						viewHolder.dianzanLayount.setTag(position);
						viewHolder.browseLayout.setTag(position);
//					}
					

					String headUrl = String.valueOf(((ForumTopic)data.get(staticposition)).getSender_portrait());
					if(headUrl==null||headUrl.equals("")){
						headUrl = "null";
					}
					final String headUrl2=headUrl;
//					Picasso.with(context) 
//							.load(headUrl) 
//							.placeholder(R.drawable.account) 
//							.error(R.drawable.account)
//							.fit()
//							.tag(context)
//							.into(viewHolder.friendHeadImg);
					Loger.i("temp","headUrl-adapter->"+headUrl);
					imageLoader.displayImage(headUrl, viewHolder.friendHeadImg, App.options_account);
					viewHolder.friendHeadImg.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(((ForumTopic)data.get(staticposition)).getSender_id()==App.sUserLoginId){
								Intent intent = new Intent(context,PersonalInformationActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								intent.putExtra("type", 1);
								intent.putExtra("topic_id", ((ForumTopic)data.get(staticposition)).getTopic_id());
								intent.putExtra("position", staticposition);
								intent.putExtra("parent", searchordisplay);
								context.startActivity(intent);
							}else{
								if(((ForumTopic)data.get(staticposition)).getSender_id()==1){
									Intent intent = new Intent(context,CircleDetailGalleryPictureActivity.class);
									intent.putExtra("type", 1);
									intent.putExtra("url",((ForumTopic)data.get(staticposition)).getSender_portrait());
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									context.startActivity(intent);
								}else{
									Intent intent = new Intent(context,FriendInformationActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									intent.putExtra("sender_id",((ForumTopic)data.get(staticposition)).getSender_id());
									intent.putExtra("display_name",((ForumTopic)data.get(staticposition)).getDisplay_name());
									intent.putExtra("sender_portrait",((ForumTopic)data.get(staticposition)).getSender_portrait());
									context.startActivity(intent);
								}
							}
						}
					});
					TextView signUpTv=(TextView)convertView.findViewById(R.id.sign_up_tv);
					TextView applydetailTv=(TextView)convertView.findViewById(R.id.sign_up_num_tv);
					String titleStr=((ForumTopic)data.get(position)).getTopic_title();
					//Loger.i("youlin","titleStr---->"+titleStr+" "+((ForumTopic)data.get(position)).getTopic_id());
					//String[] topicType=titleStr.split("[#]");
					
					int topicType = ((ForumTopic)data.get(position)).getObject_type();
						if (topicType==1) {
							viewHolder.newslay.setVisibility(View.GONE);
							viewHolder.barterLayout.setVisibility(View.GONE);
							viewHolder.gridView.setVisibility(View.VISIBLE);
							viewHolder.signUpLayout.setVisibility(View.VISIBLE);
							viewHolder.signUpLayout.setTag(position);
							try {
								JSONArray Object_data = new JSONArray(((ForumTopic)data.get(position)).getObject_data());
								Loger.d("test4",Object_data.toString());
								applytotalcount =((JSONObject) Object_data.get(0)).get("enrollTotal").toString();
								applyflag = ((JSONObject) Object_data.get(0)).get("enrollFlag").toString();
							} catch (JSONException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
							Loger.d("test4", "applyflag="+applyflag);
							if(((ForumTopic)data.get(position)).getSender_id()==App.sUserLoginId){
								signUpTv.setText("报名详情");
								signUpTv.setTextColor(0xff008000);
								applydetailTv.setTextColor(0xff008000);
								applydetailTv.setText(applytotalcount);
								//判断是否过期
								JSONArray Object_data = new JSONArray(((ForumTopic)data.get(staticposition)).getObject_data());
								JSONObject object=new JSONObject(Object_data.getString(0));
								Long endTime=Long.parseLong(object.getString("endTime"));
								if(endTime<App.CurrentSysTime){
									signUpTv.setTextColor(0xffffffff);
									applydetailTv.setTextColor(0xffffffff);
									viewHolder.overline.setVisibility(View.VISIBLE);
								}else{
									signUpTv.setTextColor(0xff008000);
									applydetailTv.setTextColor(0xff008000);
									viewHolder.overline.setVisibility(View.GONE);
								}
								//--------------------------------------------------
								viewHolder.signUpLayout.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										if(!signUpBool){
											signUpBool=true;
											new Timer().schedule(new TimerTask() {
												@Override
												public void run() {
													// TODO Auto-generated method stub
													signUpBool=false;
												}
											}, 2000);
										selectID = Integer.parseInt(v.getTag().toString());
										forumTopicObj = ((ForumTopic)data.get(selectID));
										forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(context);
										Loger.i("youlin", "hyy add for neighbor circle message----->"+selectID);
										RequestParams delParams = new RequestParams();
										delParams.put("community_id", forumTopicObj.getSender_community_id());
										delParams.put("topic_id", forumTopicObj.getTopic_id());
										delParams.put("user_id", App.sUserLoginId);
										delParams.put("sender_id", ((ForumTopic)data.get(selectID)).getSender_id());
										delParams.put("tag", "delstatus");
										delParams.put("apitype", IHttpRequestUtils.APITYPE[5]);
										Loger.i("youlin", "topicId==>"+forumTopicObj.getTopic_id());
										Loger.i("youlin", "communityId==>"+forumTopicObj.getSender_community_id());
										AsyncHttpClient isDelRequest = new AsyncHttpClient();
										isDelRequest.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
												delParams, new JsonHttpResponseHandler(){
											public void onSuccess(int statusCode, Header[] headers,
													JSONObject response) {
												try {
													isDelString = response.getString("flag");
													if(isDelString.equals("ok")){
														Intent intent=new Intent(context,SignUpDetailActivity.class);
														intent.putExtra("position", selectID);
														intent.putExtra("parent", searchordisplay);
														intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
														context.startActivity(intent);
													}else{
														Toast toast=new Toast(context);
														toast.makeText(context, "此帖已不可见", Toast.LENGTH_SHORT).show();
														toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
														forumtopicDaoDBImplObj.deleteObject(forumTopicObj.getTopic_id());
												    	Loger.i("TEST", "删除帖子成功");
												    	Intent intent = new Intent();  
														intent.setAction("youlin.delete.topic.action");
														intent.putExtra("ID", selectID);
														intent.putExtra("type", searchordisplay);
														context.sendBroadcast(intent);  
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
												Loger.i("TEST", responseString);
												super.onFailure(statusCode, headers, responseString, throwable);
											}
										});	
										
										}
										
									}
								});
							}else{
								//请求服务器判断
								JSONArray Object_data = new JSONArray(((ForumTopic)data.get(staticposition)).getObject_data());
								JSONObject object=new JSONObject(Object_data.getString(0));
								Long endTime=Long.parseLong(object.getString("endTime"));
								if(endTime<App.CurrentSysTime){
									signUpTv.setTextColor(0xffffffff);
									applydetailTv.setTextColor(0xffffffff);
									viewHolder.overline.setVisibility(View.VISIBLE);
								}else{
									signUpTv.setTextColor(0xff008000);
									applydetailTv.setTextColor(0xff008000);
									viewHolder.overline.setVisibility(View.GONE);
								}
								if(applyflag.equals("true")){
									signUpTv.setText("取消报名");
									signUpTv.setTag(1);
									applydetailTv.setText(applytotalcount);
								}else if(applyflag.equals("false")){
									signUpTv.setText("我要报名");
									signUpTv.setTag(0);
									applydetailTv.setText(applytotalcount);
								}
								viewHolder.signUpLayout.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										AllFamilyDaoDBImpl curfamilyDaoDBImpl=new AllFamilyDaoDBImpl(context);
										if(curfamilyDaoDBImpl.getCurrentAddrDetail("132").getEntity_type()==1){
										if(!signUpBool){
											signUpBool=true;
											new Timer().schedule(new TimerTask() {
												@Override
												public void run() {
													// TODO Auto-generated method stub
													signUpBool=false;
												}
											}, 2000);
										selectID = Integer.parseInt(v.getTag().toString());
										forumTopicObj = ((ForumTopic)data.get(selectID));
										forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(context);
//									Loger.i("youlin", "hyy add for neighbor circle message----->"+selectID);
										RequestParams delParams = new RequestParams();
										delParams.put("community_id", forumTopicObj.getSender_community_id());
										delParams.put("topic_id", forumTopicObj.getTopic_id());
										delParams.put("user_id", App.sUserLoginId);
										delParams.put("sender_id", ((ForumTopic)data.get(selectID)).getSender_id());
										delParams.put("tag", "delstatus");
										delParams.put("apitype", IHttpRequestUtils.APITYPE[5]);
//									Loger.i("youlin", "topicId==>"+forumTopicObj.getTopic_id());
//									Loger.i("youlin", "communityId==>"+forumTopicObj.getSender_community_id());
										currentactionapplynum = (TextView)v.findViewById(R.id.sign_up_num_tv);
										currentactionapplyname = (TextView)v.findViewById(R.id.sign_up_tv);
										AsyncHttpClient isDelRequest = new AsyncHttpClient();
										isDelRequest.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
												delParams, new JsonHttpResponseHandler(){
											public void onSuccess(int statusCode, Header[] headers,
													JSONObject response) {
												try {
													isDelString = response.getString("flag");
													if(isDelString.equals("ok")){
														Loger.d("test4", "selectID="+selectID);
														JSONArray Object_data;
														JSONObject object;
														Object_data = new JSONArray(((ForumTopic)data.get(selectID)).getObject_data());
														object=new JSONObject(Object_data.getString(0));
														Long endTime=Long.parseLong(object.getString("endTime"));
														Loger.d("test5", "当前活动结束时间："+endTime);
														Loger.d("test5", "当前时间："+System.currentTimeMillis());
														if(endTime<System.currentTimeMillis()){
															cRequestSet = true;
															Toast.makeText(context, "此活动已过期", Toast.LENGTH_SHORT).show();
														}else{
														if(currentactionapplyname.getTag().toString().equals("0")){	
															
															Intent intent = new Intent(context,applycount.class);
															intent.putExtra("position", selectID);
															//intent.putExtra("totalcount", Integer.parseInt(currenttotalcount));
															intent.putExtra("parent", searchordisplay);
															intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
															((Activity) context).startActivity(intent);
																
																	//currenttotalcount = ((JSONObject) Object_data.get(0)).get("enrollTotal").toString();
																	
																
															new AsyncTask<Void, Void, Void>(){
																@Override
																protected Void doInBackground(Void... params) {
																	// TODO Auto-generated method stub
																	Long currenttime = System.currentTimeMillis();
																	while(!cRequestSet){
																		if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
																			cRequestSet = true;
																		}
																	}
																	return null;
																}
																protected void onPostExecute(Void result) {
																	if(cRequestSet == true && flag.equals("ok")){
																		flag = "none";
																		cRequestSet = false;
																		currentactionapplyname.setTag(1);
																		
																	}	
																	cRequestSet = false;
																}
															}.execute();
														}else{
															//AlertDialog.Builder builder=new AlertDialog.Builder(context);
															CustomDialog.Builder builder=new CustomDialog.Builder(context);
															builder.setTitle("请确认是否取消报名");
															//builder.setCancelable(false);
															builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	// TODO Auto-generated method stub
																	try {
																		JSONArray Object_data = new JSONArray(((ForumTopic)data.get(selectID)).getObject_data());
																		gethttpdata(Long.parseLong(((JSONObject) Object_data.get(0)).get("activityId").toString()),App.sUserLoginId,
																				IHttpRequestUtils.YOULIN, 1);
																	} catch (NumberFormatException e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	} catch (JSONException e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	}
																	new AsyncTask<Void, Void, Void>(){
																		@Override
																		protected Void doInBackground(Void... params) {
																			// TODO Auto-generated method stub
																			Long currenttime = System.currentTimeMillis();
																			while(!cRequestSet){
																				if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
																					cRequestSet = true;
																				}
																			}
																			return null;
																		}
																		protected void onPostExecute(Void result) {
																			if(cRequestSet == true && dflag.equals("ok")){
																				dflag = "none";
																				cRequestSet = false;
																				setapplcount(currenttotalcount,"我要报名");
																				Loger.d("test4","**applytotalcount="+currenttotalcount);
																				currentactionapplyname.setTag(0);
																				JSONArray Object_data;
																				try {
																					Object_data = new JSONArray(((ForumTopic)data.get(staticposition)).getObject_data());
																					((JSONObject) Object_data.get(0)).put("enrollTotal",String.valueOf(currenttotalcount));
																					((JSONObject) Object_data.get(0)).put("enrollFlag","false");
																					((ForumTopic)data.get(staticposition)).setObject_data(Object_data.toString());
																					Loger.d("test4","我要报名"+Object_data.toString());
																					
																				} catch (JSONException e) {
																					// TODO Auto-generated catch block
																					e.printStackTrace();
																				}
																			}
																		}
																	}.execute();
																	dialog.cancel();
																}
															});
															builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																	// TODO Auto-generated method stub
																	dialog.cancel();
																}
															});
															builder.create().show();
															
														}
														}
													}else{
														Toast toast=new Toast(context);
														toast.makeText(context, "此帖已不可见", Toast.LENGTH_SHORT).show();
														toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
														forumtopicDaoDBImplObj.deleteObject(forumTopicObj.getTopic_id());
												    	Loger.i("TEST", "删除帖子成功");
												    	Intent intent = new Intent();  
														intent.setAction("youlin.delete.topic.action");
														intent.putExtra("ID", selectID);
														intent.putExtra("type", searchordisplay);
														context.sendBroadcast(intent);  
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
												Loger.i("TEST", responseString);
												super.onFailure(statusCode, headers, responseString, throwable);
											}
										});	
										
										}
										
										}else{
											Toast.makeText(context, "您的地址还未经过验证", Toast.LENGTH_SHORT).show();
										}
										
									}
								});
								//currentactionapplynum = applydetailTv;
							}
						} else if(topicType==3){  //  新闻
							Loger.d("test5", "topicType==3");
							Loger.d("test5", "topicType==3"+((ForumTopic)data.get(position)).getObject_data().toString());
							final JSONArray newsjson = new JSONArray(((ForumTopic)data.get(position)).getObject_data().toString());
							
							viewHolder.newslay.setVisibility(View.VISIBLE);
//							Picasso.with(context) 
//							.load(((JSONObject)newsjson.get(0)).getString("new_small_pic")) 
//							.placeholder(R.drawable.default_normal_avatar) 
//							.error(R.drawable.default_normal_avatar)
//							.fit()
//							.tag(context)
//							.into(viewHolder.newspic);
							imageLoader.displayImage(((JSONObject)newsjson.get(0)).getString("new_small_pic"), viewHolder.newspic, App.options_account);
							viewHolder.newstitle.setText(((JSONObject)newsjson.get(0)).getString("new_title"));
							viewHolder.newslay.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									try {
										Intent newsdetailintent = new Intent(context,NewsDetail.class);
										newsdetailintent.putExtra("linkurl",((JSONObject)newsjson.get(0)).getString("new_url").toString());
										Loger.i("NEW", "Adapter_url->"+((JSONObject)newsjson.get(0)).getString("new_url").toString());
										newsdetailintent.putExtra("picurl", ((JSONObject)newsjson.get(0)).getString("new_small_pic").toString());
										newsdetailintent.putExtra("title",((JSONObject)newsjson.get(0)).getString("new_title").toString());
										newsdetailintent.putExtra("newsid",((JSONObject)newsjson.get(0)).getString("new_id").toString());
										newsdetailintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										context.startActivity(newsdetailintent);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
							viewHolder.delete.setVisibility(View.GONE);
							viewHolder.signUpLayout.setVisibility(View.GONE);
							viewHolder.gridView.setVisibility(View.GONE);
							viewHolder.lookAllTv.setVisibility(View.GONE);
							viewHolder.barterLayout.setVisibility(View.GONE);
						}else if(topicType==4){ //以物换物
							viewHolder.signUpLayout.setVisibility(View.GONE);
							viewHolder.newslay.setVisibility(View.GONE);
							viewHolder.gridView.setVisibility(View.VISIBLE);
							viewHolder.barterLayout.setVisibility(View.VISIBLE);
							Loger.d("youlin", "topicType==4---->"+((ForumTopic)data.get(position)).getObject_data().toString());
							JSONArray barterJSONArray = new JSONArray(((ForumTopic)data.get(position)).getObject_data().toString());
							viewHolder.priceTv.setText("￥"+((JSONObject)barterJSONArray.get(0)).getString("price"));
							String newOldStr=((JSONObject)barterJSONArray.get(0)).getString("oldornew");
							if(newOldStr.equals("0")){
								viewHolder.newOldTv.setText("全新");
							}else if(newOldStr.equals("1")){
								viewHolder.newOldTv.setText("九成新");
							}else if(newOldStr.equals("2")){
								viewHolder.newOldTv.setText("八成新");
							}else if(newOldStr.equals("3")){
								viewHolder.newOldTv.setText("七成新");
							}else if(newOldStr.equals("4")){
								viewHolder.newOldTv.setText("六成新");
							}else if(newOldStr.equals("5")){
								viewHolder.newOldTv.setText("五成新");
							}else if(newOldStr.equals("6")){
								viewHolder.newOldTv.setText("五成新以下");
							}
						}else {
							viewHolder.signUpLayout.setVisibility(View.GONE);
							viewHolder.newslay.setVisibility(View.GONE);
							viewHolder.barterLayout.setVisibility(View.GONE);
							viewHolder.gridView.setVisibility(View.VISIBLE);
						}
					
					viewHolder.titleTv.setText(titleStr);
					viewHolder.nameTv.setText(((ForumTopic)data.get(position)).getDisplay_name());
					//String time=TimeToStr.getTimeToStr(((ForumTopic)data.get(position)).getTopic_time());
					Loger.d("test6", "getTopic_time="+((ForumTopic)data.get(position)).getTopic_time()+"CurrentSysTime="+App.CurrentSysTime);
					viewHolder.timeTv.setText(TimeToStr.getTimeElapse(((ForumTopic)data.get(position)).getTopic_time(), App.CurrentSysTime));
					long callSenderId=((ForumTopic)data.get(position)).getSender_id();
					String sayHelloStatus="null";
					if(callSenderId==1){
						String callStr=((ForumTopic)data.get(position)).getObject_data();
						JSONArray jsonArray=new JSONArray(callStr);
						JSONObject jsonObject=(JSONObject)jsonArray.get(0);
						sayHelloStatus=jsonObject.getString("sayHelloStatus");
					}
					if(callSenderId == 1 && ((ForumTopic)data.get(position)).getCache_key() != App.sUserLoginId && sayHelloStatus.equals("0")){
						viewHolder.callImg.setVisibility(View.VISIBLE);
						viewHolder.callImg.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								AllFamilyDaoDBImpl curfamilyDaoDBImpl2=new AllFamilyDaoDBImpl(context);
								if(curfamilyDaoDBImpl2.getCurrentAddrDetail("132").getEntity_type()==1){
								CustomDialog.Builder builder=new CustomDialog.Builder(context);
								View view=LayoutInflater.from(context).inflate(R.layout.topic_call, null);
								final EditText editText=(EditText)view.findViewById(R.id.call_et);
								editText.setText("欢迎来到本小区");
								editText.setSelection(editText.getText().length());
								editText.setBackgroundColor(Color.WHITE);
								editText.setOnEditorActionListener(new OnEditorActionListener() {
									@Override
									public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
										// TODO Auto-generated method stub
										return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
									}
								});
								builder.setContentView(view);
								builder.setPositiveButton("发送", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										if(editText.getText().toString().trim().length()>0){
										RequestParams params = new RequestParams();
										params.put("tag", "sayhello");
										params.put("apitype",IHttpRequestUtils.APITYPE[5]);
										params.put("user_id", App.sUserLoginId);
										params.put("topic_id",((ForumTopic)data.get(staticposition)).getTopic_id());
										params.put("content", editText.getText().toString().trim());
										AsyncHttpClient client = new AsyncHttpClient();
										client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,params, new JsonHttpResponseHandler() {
											@Override
											public void onSuccess(int statusCode,org.apache.http.Header[] headers,
													org.json.JSONObject response) {
												// TODO Auto-generated method stub
												try {
													Loger.i("youlin", "555555555555--->"+response.toString());
													if(response.getString("flag").equals("ok")){
													String countStr=response.getString("commCount");
													viewHolder.commentCount.setText(countStr);
													if(searchordisplay==0){
														ForumTopic forumTopic=(ForumTopic)TitleBarSearchActivity.forumtopicLists.get(staticposition);
														forumTopic.setComment_num(Integer.parseInt(countStr));
														forumTopic.setObject_data("[{\"sayHelloStatus\":1}]");
														TitleBarSearchActivity.forumtopicLists.set(staticposition, forumTopic);
														Intent intent =new Intent("youlin.friend.action.sayhello");
														intent.putExtra("topic_id",((ForumTopic)data.get(staticposition)).getTopic_id());
														intent.putExtra("count", Integer.parseInt(countStr));
														context.sendBroadcast(intent);
														Loger.d("TEST", "56787u4398289424982");
													}else if(searchordisplay==1){
														ForumTopic forumTopic=(ForumTopic)FriendCircleFragment.allList.get(staticposition);
														forumTopic.setComment_num(Integer.parseInt(countStr));
														forumTopic.setObject_data("[{\"sayHelloStatus\":1}]");
														FriendCircleFragment.allList.set(staticposition, forumTopic);
													}else if(searchordisplay==3){
														ForumTopic forumTopic=(ForumTopic)CollectionActivity.forumtopicLists.get(staticposition);
														forumTopic.setComment_num(Integer.parseInt(countStr));
														forumTopic.setObject_data("[{\"sayHelloStatus\":1}]");
														CollectionActivity.forumtopicLists.set(staticposition, forumTopic);
														Intent intent =new Intent("youlin.friend.action.sayhello");
														intent.putExtra("topic_id",((ForumTopic)data.get(staticposition)).getTopic_id());
														intent.putExtra("count", Integer.parseInt(countStr));
														context.sendBroadcast(intent);
													}
													viewHolder.callImg.setVisibility(View.GONE);
													}else if(response.getString("flag").equals("black")){
														Toast toast=Toast.makeText(context, "对方不是你好友", 0);
														toast.setGravity(Gravity.CENTER,0,0);
														toast.show();
													}
												} catch (JSONException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												super.onSuccess(statusCode, headers,response);
											}
											@Override
											public void onSuccess(int statusCode,org.apache.http.Header[] headers,
													org.json.JSONArray response) {
												// TODO Auto-generated method stub
											}
											@Override
											public void onFailure(int statusCode,org.apache.http.Header[] headers,String responseString,
														Throwable throwable) {
													// TODO Auto-generated method stub
												new ErrorServer(context, responseString);
													super.onFailure(statusCode, headers,responseString, throwable);
												}
											});
										dialog.cancel();
									}else{
										Toast.makeText(context, "发送内容不能为空", 0).show();
									}
									}
								});
								builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}
								});
								builder.create().show();
								}else{
									Toast.makeText(context, "您的地址还未经过验证", Toast.LENGTH_SHORT).show();
								}
							}
						});
						
					}else{
						viewHolder.callImg.setVisibility(View.GONE);
					}
					String contentStr=((ForumTopic)data.get(position)).getTopic_content();
					if(contentStr.isEmpty()||contentStr==null){
						viewHolder.contentTv.setText("");
						viewHolder.lookAllTv.setVisibility(View.GONE);
					}else{
						viewHolder.contentTv.setText(Html.fromHtml(contentStr));
						if(viewHolder.contentTv.getLineCount()<4){
							
						}
						viewHolder.lookAllTv.getViewTreeObserver().addOnPreDrawListener(
							new OnPreDrawListener() {

								@Override
								public boolean onPreDraw() {
									// TODO Auto-generated method stub
									if (viewHolder.contentTv.getLineCount() >= 4) {
										viewHolder.lookAllTv.setVisibility(View.VISIBLE);
									}else{
										viewHolder.lookAllTv.setVisibility(View.GONE);
									}
									return true;
								}
							});
					}
					viewHolder.gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
					final List<String> pictureList = new ArrayList<String>();
					JSONArray imgJsonArray = null;
					try {
						if (((ForumTopic) data.get(position)).getMeadia_files_json() != null) {
							imgJsonArray = new JSONArray(((ForumTopic) data.get(position)).getMeadia_files_json());
							for (int i = 0; i < imgJsonArray.length(); i++) {
								JSONObject jsonObject = new JSONObject(imgJsonArray.getString(i));
								try {
									pictureList.add(jsonObject.getString("resPath"));
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
				viewHolder.gridView.setAdapter(new FriendCircleImageAdapter(context,pictureList));
				viewHolder.gridView.setOnScrollListener(new SampleScrollListener(context));
				viewHolder.gridView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							final int arg2, long arg3) {
						// TODO Auto-generated method stub
						//selectID = Integer.parseInt(((View)arg1.getParent()).getTag().toString());
						Intent intent=new Intent(context,GalleryPictureActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						intent.putExtra("ID", arg2);
						intent.putExtra("url",((ForumTopic)data.get(staticposition)).getMeadia_files_json());
						context.startActivity(intent);
					}
				});
				int type = App.sUserType;
//				Loger.i("youlin","2222222222222222---->"+((ForumTopic)data.get(position)).getSender_id()+" "+App.sUserLoginId);
				if(((ForumTopic)data.get(position)).getSender_id()==App.sUserLoginId||type==2||type==6){
					viewHolder.delete.setVisibility(View.VISIBLE);
				}else{
					viewHolder.delete.setVisibility(View.GONE);
				}
				final int commentCountInt=((ForumTopic)data.get(position)).getComment_num();
				if(commentCountInt==0){
					viewHolder.commentCount.setText("回复");
				}else{
					viewHolder.commentCount.setText(String.valueOf(commentCountInt));
				}
				int praiseNum=((ForumTopic)data.get(position)).getLike_num();
				if(praiseNum==0){
					viewHolder.praise.setText("赞");
				}else{
					viewHolder.praise.setText(String.valueOf(praiseNum));
				}
				int praiseStatus=((ForumTopic)data.get(position)).getLike_status();
				if(praiseStatus==1){//点赞了
					viewHolder.dianzanImageView.setImageResource(R.drawable.dianzan_2);
				}else{
					viewHolder.dianzanImageView.setImageResource(R.drawable.dianzan);
				}
				
				int browseNum=((ForumTopic)data.get(position)).getView_num();
				if(browseNum==0){
					viewHolder.browse.setText("0");
				}else{
					viewHolder.browse.setText(String.valueOf(browseNum));
				}
//			if(myPush==1){
//				viewHolder.moreImg.setVisibility(View.INVISIBLE);
//			}
				
//			viewHolder.moreImg.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(((ForumTopic)data.get(position)).getSender_id()==App.sUserLoginId){
//						myTopic=1;
//					}else{
//						myTopic=0;
//					}
//					if(((ForumTopic)data.get(position)).getTopic_category_type()==3){
//						isgonggao=3;
//					}else{
//						isgonggao=0;
//					}
//					new SharePopWindow(context,isgonggao,myTopic,Integer.parseInt(v.getTag().toString()),
//							((ForumTopic)data.get(position)).getTopic_id(),
//							((ForumTopic)data.get(position)).getSender_community_id(),
//							((ForumTopic)data.get(position)).getSender_id());
//				}
//			});
				
//			commentNameList = new ArrayList<String>();
//			commentContentList = new ArrayList<String>();
//			String insteadImg = null;
//			JSONArray commentJArray=null;
//			JSONObject commentObject=null;
//				try {
//
//					if (((ForumTopic) data.get(position)).getComments_summary() != null) {
//
//						commentJArray = new JSONArray(((ForumTopic) data.get(position)).getComments_summary());
//						for (int i = 0; i < 2; i++) {
//							try {
//								commentObject = new JSONObject(commentJArray.getString(i));
//								if (commentObject.getString("contentType").equals("0")==false) {
//									insteadImg = " [媒体]";
//									commentNameList.add(commentObject.getString("displayName"));
//									if(commentObject.getString("content").equals("null")){
//										commentContentList.add(insteadImg);
//									}else{
//										commentContentList.add(commentObject.getString("content") + insteadImg);
//									}
//								}else{
//									commentNameList.add(commentObject.getString("displayName"));
//									commentContentList.add(commentObject.getString("content"));
//								}
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//
//						}
//					}
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Loger.d("test3","commentCountInt size="+commentCountInt);
//				viewHolder.commentLv.setAdapter(new FriendCircleCommentAdapter(context, commentNameList, commentContentList,commentCountInt));
//				
//				if (commentCountInt == 0) {
//					viewHolder.lookCommentTv.setText("");
//					viewHolder.lookCommentTv.setVisibility(View.GONE);
//					viewHolder.divider.setVisibility(View.GONE);
//					viewHolder.commentLayout.setBackgroundColor(Color.TRANSPARENT);
//				}else{
//					viewHolder.commentLayout.setBackgroundResource(R.drawable.detail);
//				}
//				if (commentCountInt == 1) {
//					viewHolder.lookCommentTv.setText("");
//					viewHolder.lookCommentTv.setVisibility(View.GONE);
//				}
//				if (commentCountInt == 2) {
//					viewHolder.lookCommentTv.setText("");
//					viewHolder.lookCommentTv.setVisibility(View.GONE);
//				}
//				if (commentCountInt > 2) {
//					viewHolder.lookCommentTv.setText("查看全部"+ commentCountInt + "条回复");
//					viewHolder.lookCommentTv.setVisibility(View.VISIBLE);
//				}
//				
//				if (commentCountInt > 2) {
//					viewHolder.divider.setVisibility(View.VISIBLE);
//				}
					
//					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if(isFristMap.get(position)==null || isFristMap.get(position)){
			if(data.size()>1){
				convertView.startAnimation(animation);
				isFristMap.put(position, false);
			}
		}
		return convertView;
		
	}

	private static class ViewHolder {
		public LinearLayout baomingxiangqing;
		public ProgressBar shuaxin;
		//public ImageView hint1;
		//public View hint2;
		public LinearLayout hint3;
		public RelativeLayout morelayout;
		public RelativeLayout title_msg;
		public ImageView friendHeadImg;
		public TextView titleTv;
		public TextView nameTv;
		public TextView timeTv;
		public ImageView callImg;
//		public static TextView applydetailTv;
		public TextView contentTv;
		public TextView lookAllTv;
		public GridView gridView;
		public LinearLayout signUpLayout;
		public RelativeLayout barterLayout;
		public TextView priceTv;
		public TextView newOldTv;
		public TextView delete ;
		public TextView commentCount;
		public LinearLayout commentImg;
		public ImageButton commentLayout;
		public TextView praise;
		public LinearLayout browseLayout;
		public TextView browse;
		public TextView noinfo;
		public LinearLayout newslay;
		public ImageView newspic;
		public TextView newstitle;
		public ImageView cover;
		public ImageView overline;
		//public ImageView moreImg;
		//public ListView commentLv;
		//public TextView lookCommentTv;	
		//public View divider;
		//public LinearLayout commentLayout;   //hint2
		public RelativeLayout friendCircleLayout;
		public LinearLayout dianzanLayount;
		public ImageView dianzanImageView;
		public View categorylayout;
		public TextView categorylay1;
		public TextView categorylay2;
		public TextView categorylay3;
		public TextView categorylay4;
		public TextView categorylay5;
		public TextView categorylay6;
		
 	}
	private void gethttpdata(long activityId,long user_id ,String http_addr,int request_index){
		/***********************************************/
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		final int position = request_index;
			Loger.d("test3", "3333333333333333333333");
		
			if(request_index ==1){
					params.put("activityId", activityId);
					params.put("userId", user_id);
					params.put("tag", "delenroll");
					params.put("apitype", IHttpRequestUtils.APITYPE[5]);
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
					
					Loger.i("test4", "set applycount flag->" + jsonContext.getString("flag"));
					if(jsonContext.getString("flag").equals("ok")){
						currenttotalcount = jsonContext.getString("enrollTotal");
						Loger.i("test4", "set applycount enrollTotal->" + currenttotalcount);
						cRequestSet = true;
						dflag = "ok";
					}else{
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
//				jsonObjList.clear();
//				try {
//					org.json.JSONObject obj = null;
//					for (int i = 0; i < jsonContext.length(); i++) {
//						jsonObjList.add(jsonContext.getJSONObject(i));
//						obj = jsonContext.getJSONObject(i);
//						Loger.d("test3", ""+obj.getString("pk"));
//					}
//					cRequestSet = true;
//				} catch (org.json.JSONException e) {
//					// TODO Auto-generated catch block
//					Loger.i("TEST","OK(error)->" + e.getMessage());
//					cRequestSet = false;
//					e.printStackTrace();
//					}
					super.onSuccess(statusCode, headers, response);
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					new ErrorServer(context, responseString);
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	@Override
	public void onClick(final View view) {
		// TODO Auto-generated method stub
		if(!TimeClick.isFastClick1000()){
		switch (view.getId()) {
		case R.id.friend_circle_layout:
		case R.id.title_msg:
		case R.id.content_neighbor:
		case R.id.look_all_tv:
		//case R.id.friend_comment_tv:
		case R.id.comment_count:
		case R.id.comment_img:
		case R.id.comment_img_bt:
		case R.id.browse_ll:
			Loger.d("test4", "searchordisplay="+searchordisplay+"deleteflag="+deleteflag);
			selectID = Integer.parseInt(view.getTag().toString());
			if(searchordisplay == 3 && ((ForumTopic)data.get(selectID)).getdeleteflag().equals("2")){
				//Toast.makeText(context, "此贴已被删除，需要删除收藏么", Toast.LENGTH_SHORT).show();
				Intent collectintent = new Intent(context,Collection_delensure.class); 
				collectintent.putExtra("sendercommunityid", ((ForumTopic)data.get(selectID)).getSender_community_id());
				collectintent.putExtra("topicid", ((ForumTopic)data.get(selectID)).getTopic_id());
				collectintent.putExtra("position", selectID);
				collectintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				context.startActivity(collectintent);
				break;
			}

			try {
				forumTopicObj = ((ForumTopic)data.get(selectID));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(context);
			Loger.i("youlin", "hyy add for neighbor circle message----->"+selectID);
			RequestParams delParams = new RequestParams();
			delParams.put("community_id", forumTopicObj.getSender_community_id());
			delParams.put("topic_id", forumTopicObj.getTopic_id());
			delParams.put("user_id", App.sUserLoginId);
			delParams.put("sender_id", ((ForumTopic)data.get(selectID)).getSender_id()); //hyy
			delParams.put("tag", "delstatus");
			delParams.put("apitype", IHttpRequestUtils.APITYPE[5]);
			Loger.i("LYM", "topicId==>"+forumTopicObj.getTopic_id());
			Loger.i("LYM", "communityId==>"+forumTopicObj.getSender_community_id());
			AsyncHttpClient isDelRequest = new AsyncHttpClient();
			isDelRequest.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
					delParams, new JsonHttpResponseHandler(){
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					try {
						isDelString = response.getString("flag");
						boolean refreshflag = forumTopicObj.getFlag();
						//Loger.i("TEST", "5555555555555555555555555---->"+refreshflag);
						if(isDelString.equals("ok") && !refreshflag){
							int topicType = forumTopicObj.getObject_type();
							if(!App.Detailactivityisshown){
								App.Detailactivityisshown = true;
								if(topicType==4){
									Intent intent = new Intent(context,BarterDedailActivity.class);
									intent.putExtra("url",forumTopicObj.getMeadia_files_json());
									intent.putExtra("position", selectID);
									intent.putExtra("parent", searchordisplay);
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									context.startActivity(intent);
								}else{
									Intent intent = new Intent(context,CircleDetailActivity.class);
									intent.putExtra("position", selectID);
									intent.putExtra("parent", searchordisplay);
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									context.startActivity(intent);
								}
							}
							
						}else if(isDelString.equals("no") && !refreshflag){
							Toast toast=new Toast(context);
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
							toast.makeText(context, "此帖已不可见", Toast.LENGTH_SHORT).show();
							
							if(searchordisplay == 3 ){
								((ForumTopic) data.get(selectID)).setdeleteflag("2");
								Intent collectintent = new Intent(context,Collection_delensure.class); 
								collectintent.putExtra("sendercommunityid", ((ForumTopic)data.get(selectID)).getSender_community_id());
								collectintent.putExtra("topicid", ((ForumTopic)data.get(selectID)).getTopic_id());
								collectintent.putExtra("position", selectID);
								collectintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								context.startActivity(collectintent);
							}else{
								forumtopicDaoDBImplObj.deleteObject(forumTopicObj.getTopic_id());
						    	Loger.i("TEST", "删除帖子成功");
						    	Intent intent = new Intent();  
								intent.setAction("youlin.delete.topic.action");
								intent.putExtra("ID", selectID);
								intent.putExtra("type", searchordisplay);
								context.sendBroadcast(intent);
							}
							  
						}else if(isDelString.equals("black") && !refreshflag){
							Toast toast=new Toast(context);
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
							toast.makeText(context, "此帖已不可见", Toast.LENGTH_SHORT).show();
					    	Loger.i("TEST", "删除帖子成功");
							if(searchordisplay==3){
								CollectionActivity.forumtopicLists.remove(selectID);
								CollectionActivity.maddapter.notifyDataSetChanged();
							}else{
								forumtopicDaoDBImplObj.deleteObject(forumTopicObj.getTopic_id());
								Intent intent = new Intent();  
								intent.setAction("youlin.delete.topic.action");
								intent.putExtra("ID", selectID);
								intent.putExtra("type", searchordisplay);
								context.sendBroadcast(intent);
							}
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
					new ErrorServer(context, responseString);
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});	
			
			break;
		case R.id.cirlce_delete:
			AllFamilyDaoDBImpl curfamilyDaoDBImpl=new AllFamilyDaoDBImpl(context);
			if(curfamilyDaoDBImpl.getCurrentAddrDetail("132").getEntity_type()==1){
			selectID = Integer.parseInt(view.getTag().toString());	
			if(searchordisplay == 3 && ((ForumTopic)data.get(selectID)).getdeleteflag().equals("2")){
				Intent collectintent = new Intent(context,Collection_delensure.class); 
				collectintent.putExtra("sendercommunityid", ((ForumTopic)data.get(selectID)).getSender_community_id());
				collectintent.putExtra("topicid", ((ForumTopic)data.get(selectID)).getTopic_id());
				collectintent.putExtra("position", selectID);
				collectintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				context.startActivity(collectintent);
				break;
			}
			forumTopicObj = ((ForumTopic)data.get(selectID));
			CustomDialog.Builder builder = new CustomDialog.Builder(context);
//			if(forumTopicObj.getObject_type()==0){
//				builder.setTitle("删除话题");
//			}else if(forumTopicObj.getObject_type()==1){
//				builder.setTitle("删除活动");
//			}
			builder.setMessage("确定要删除该内容吗？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(context);
					RequestParams delParams = new RequestParams();
					delParams.put("user_id",App.sUserLoginId);
					delParams.put("community_id", forumTopicObj.getSender_community_id());
					delParams.put("topic_id", forumTopicObj.getTopic_id());
					delParams.put("tag", "deltopic");
					delParams.put("apitype", IHttpRequestUtils.APITYPE[5]);
					Loger.i("TEST", "topicId==>"+forumTopicObj.getTopic_id());
					Loger.i("TEST", "communityId==>"+forumTopicObj.getSender_community_id());
					Loger.i("TEST", "senderid==>"+forumTopicObj.getSender_id());
					AsyncHttpClient delRequest = new AsyncHttpClient();
					delRequest.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
							delParams, new JsonHttpResponseHandler(){
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							try {
							    String delFlag = response.getString("flag");
							    if("ok".equals(delFlag)){
							    	forumtopicDaoDBImplObj.deleteObject(forumTopicObj.getTopic_id());
							    	Loger.i("TEST", "删除帖子成功----->"+selectID);
							    	Intent intent = new Intent();  
									intent.setAction("youlin.delete.topic.action");
									intent.putExtra("ID", selectID);
									intent.putExtra("type", searchordisplay);
									context.sendBroadcast(intent);  
							    }else{
							    	Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
							    }
								dialog.dismiss();
							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
							Loger.d("test4", "删除成功");
							super.onSuccess(statusCode, headers, response);
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							new ErrorServer(context, responseString);
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
			}else{
				Toast.makeText(context, "您的地址还未经过验证", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.dianzan_ll:
			AllFamilyDaoDBImpl curfamilyDaoDBImpl2=new AllFamilyDaoDBImpl(context);
			if(curfamilyDaoDBImpl2.getCurrentAddrDetail("132").getEntity_type()==1){
			final LinearLayout dianzanLayout = (LinearLayout)view;
			final ImageView imageView = (ImageView) view.findViewById(R.id.dianzan_img);
			final TextView textView = (TextView) view.findViewById(R.id.dianzan_count);
			dianzanLayout.setEnabled(false);
			imageView.setEnabled(false);
			textView.setEnabled(false);
			final Timer adapterTimer=new Timer();
			adapterTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						dianzanLayout.setEnabled(true);
						imageView.setEnabled(true);
						textView.setEnabled(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, 10000);
			selectID = Integer.parseInt(view.getTag().toString());
			if(searchordisplay == 3 && ((ForumTopic)data.get(selectID)).getdeleteflag().equals("2")){
				Intent collectintent = new Intent(context,Collection_delensure.class); 
				collectintent.putExtra("sendercommunityid", ((ForumTopic)data.get(selectID)).getSender_community_id());
				collectintent.putExtra("topicid", ((ForumTopic)data.get(selectID)).getTopic_id());
				collectintent.putExtra("position", selectID);
				collectintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				context.startActivity(collectintent);
				dianzanLayout.setEnabled(true);
				imageView.setEnabled(true);
				textView.setEnabled(true);
				adapterTimer.cancel();
				break;
			}
			forumTopicObj = ((ForumTopic)data.get(selectID));
			forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(context);
			RequestParams params = new RequestParams();
			params.put("user_id", App.sUserLoginId);
			params.put("topic_id", forumTopicObj.getTopic_id());
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
						adapterTimer.cancel();
						int nLikeNum = 0;
						try {
							nLikeNum = Integer.parseInt(response.getString("likeNum"));
							if(nLikeNum==-1 || response.getString("flag").equals("black")){
								Toast toast=new Toast(context);
								toast.makeText(context, "此帖已不可见", Toast.LENGTH_SHORT).show();
								toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
								if(searchordisplay == 3){
									((ForumTopic) data.get(selectID)).setdeleteflag("2");
									Intent collectintent = new Intent(context,Collection_delensure.class); 
									collectintent.putExtra("sendercommunityid", ((ForumTopic)data.get(selectID)).getSender_community_id());
									collectintent.putExtra("topicid", ((ForumTopic)data.get(selectID)).getTopic_id());
									collectintent.putExtra("position", selectID);
									collectintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									context.startActivity(collectintent);
									dianzanLayout.setEnabled(true);
									imageView.setEnabled(true);
									textView.setEnabled(true);
								}else{
									forumtopicDaoDBImplObj.deleteObject(forumTopicObj.getTopic_id());
									Intent intent = new Intent();  
									intent.setAction("youlin.delete.topic.action");
									intent.putExtra("ID", selectID);
									intent.putExtra("type", searchordisplay);
									context.sendBroadcast(intent);
								}
							}else{
								if(response.getString("flag").equals("ok")){
								int nType = Integer.parseInt(response.getString("hitStatus"));
								if(nType == 1){
									imageView.setImageResource(R.drawable.dianzan_2);
									try {
										textView.setText(String.valueOf(nLikeNum));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else{
									imageView.setImageResource(R.drawable.dianzan);
									try {
										if(nLikeNum==0){
											textView.setText("赞");
										}else{
											textView.setText(String.valueOf(nLikeNum));
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								forumtopicDaoDBImplObj.modifyObject(forumTopicObj, 1);
								if(searchordisplay == 2||searchordisplay == 3){
									for(int i=0;i<FriendCircleFragment.maddapter.data.size();i++){
										if(((ForumTopic)FriendCircleFragment.maddapter.data.get(i)).getTopic_id()==forumTopicObj.getTopic_id()){
											((ForumTopic)FriendCircleFragment.maddapter.data.get(i)).setLike_status(nType);
											((ForumTopic)FriendCircleFragment.maddapter.data.get(i)).setLike_num(nLikeNum);
										}
									}
									
								}
								dianzanLayout.setEnabled(true);
								imageView.setEnabled(true);
								textView.setEnabled(true);
								}else if(response.getString("flag").equals("black")){
									Toast.makeText(context, "点赞失败", 0).show();
								}
							}
							
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
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
				Toast.makeText(context, "您的地址还未经过验证", Toast.LENGTH_SHORT).show();
			}
			
			break;
		case R.id.layout1:
			Bundle data =new Bundle();
			data.putString("category", ((TextView)view).getText().toString());
			data.putInt("categoryid", R.id.layout1);
			Loger.d("test1", data.getString("category"));
			changeutils = new StatusChangeutils();
			changeutils.setstatuschange("CATEGORY",1,data);
			
			break;
		case R.id.layout2:
			data =new Bundle();
			data.putString("category", ((TextView)view).getText().toString());
			data.putInt("categoryid", R.id.layout2);
			changeutils = new StatusChangeutils();
			changeutils.setstatuschange("CATEGORY",1,data);
			Loger.d("test1", data.getString("category"));
			break;
		case R.id.layout3:
			data =new Bundle();
			data.putString("category", ((TextView)view).getText().toString());
			data.putInt("categoryid", R.id.layout3);
			changeutils = new StatusChangeutils();
			changeutils.setstatuschange("CATEGORY",1,data);
			Loger.d("test1", data.getString("category"));
			break;
		case R.id.layout4:
			data =new Bundle();
			data.putString("category", ((TextView)view).getText().toString());
			data.putInt("categoryid", R.id.layout4);
			changeutils = new StatusChangeutils();
			changeutils.setstatuschange("CATEGORY",1,data);
			Loger.d("test1", data.getString("category"));
			break;
		case R.id.layout5:
			data =new Bundle();
			data.putString("category", ((TextView)view).getText().toString());
			data.putInt("categoryid", R.id.layout5);
			changeutils = new StatusChangeutils();
			changeutils.setstatuschange("CATEGORY",1,data);
			Loger.d("test1", data.getString("category"));
			break;
		case R.id.layout6:
			data =new Bundle();
			data.putString("category", ((TextView)view).getText().toString());
			data.putInt("categoryid", R.id.layout6);
			changeutils = new StatusChangeutils();
			changeutils.setstatuschange("CATEGORY",1,data);
			Loger.d("test1", data.getString("category"));
			break;
		default:
			break;
		}
		}
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
			if(requestCode == REQUEST_APPLY_NUM){
				Loger.d("test4", "REQUEST_APPLY_NUM result  1111111111111111");
			}
			return false;
	}
}
