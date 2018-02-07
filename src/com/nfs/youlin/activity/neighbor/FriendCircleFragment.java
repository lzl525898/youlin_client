package com.nfs.youlin.activity.neighbor;

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.Header;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.R.string;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.personal.CollectionActivity;
import com.nfs.youlin.activity.personal.MyPushActivity;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.push.YLPushInitialization;
import com.nfs.youlin.service.NetworkService;
//import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StatusChangeListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.YLProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class FriendCircleFragment extends Fragment implements OnRefreshListener,StatusChangeListener
,OnClickListener{
	private View view;// 缓存页面
	private ListView friendCircleLv;
	private ForumtopicDaoDBImpl daoDBImpl;
	private List<Object> forumtopicLists;
	public static List<Object> allList;
	public static List<Object> allListreplace = new ArrayList<Object>();
	/*kjflkajf;lkjsad8*/
	private final int PAGE_SIZE = 6;
	private final static int UP_SUCCESS_CODE        = 2007;
	private final static int FAILED_CODE            = 2001;
	private final static int REFUSE_CODE            = 2003;
	private final static int FIRST_SUCCESS_CODE     = 2004;
	private final static int NEW_FIRST_SUCCESS_CODE = 2009;
	private final static int INIT_FAILED_CODE       = 2005;
	private final static int INIT_REFUSE_CODE       = 2006;
	private final static int NOINFO_SUCCESS_CODE    = 2008;
	
	private boolean bPullDownStatus = false;
	private boolean bPullUpStatus = false;
	public static boolean bNoNewInfoStatus = false;
	private Bundle bundle;
	private List<Bundle> listBundle=new ArrayList<Bundle>();
	private List<Bundle> listBundle2=new ArrayList<Bundle>();
	private String currentCommunityId;
	private TextView noNewInfoTextView;
	/************hyy add ************************/
	private PullToRefreshLayout mPullToRefreshLayout;
	public  static FriendCircleAdapter maddapter;
	private boolean refreshflag=false;
	private LinearLayout newMoreTopicLayout;
	private TextView newTopTextView;
//	private LinearLayout waitLoadInfoLayout;
	private boolean responseStatus = false;
	private String responseResult = null;
	private int REQUEST_APPLY_NUM = 200;
//	ProgressBar initPd;
	public static Long id=0L;
	int updatePosition;
	int callPosition;
	ViewGroup viewGroup;
	private ViewGroup circleviewGroup;
	private View categorylayout;

	private int finalY = 0 ;
	private StatusChangeutils statusutils;
	public static String[] str= {"全部","话题","活动","公告","建议","闲品会"};
//	public static int[] ImgRint = {R.id.categorychoose1,R.id.categorychoose2,R.id.categorychoose3,R.id.categorychoose4,R.id.categorychoose5,R.id.categorychoose6};
	public static String[] tagstr= {"gettopic","singletopic","singleactivity","getnotice","getsuggest","singlebarter"};
	public static int[] categorytype = {0,0,0,3,5,0};
	public static String[] apistr= {IHttpRequestUtils.APITYPE[5],"comm","comm",IHttpRequestUtils.APITYPE[4],IHttpRequestUtils.APITYPE[4],"comm"};
	public static int categoryindex=0;
//	private ProgressDialog pd1;
	private YLProgressDialog ylProgressDialog;
	private boolean getcirclerequest = false;
	private String httpflag = "none";
	public static LinearLayout neighborInitLayout;
	private Drawable drawableNeighborNormal;
	private Drawable drawableNeighborNormalUnread;
	void buildprocessdialog(){
		ylProgressDialog = YLProgressDialog.createDialogWithTopic(getActivity());
		ylProgressDialog.setCancelable(false);
//		pd1 = new ProgressDialog(getActivity());
//		pd1.setMessage("正在提交搜索请求，请稍侯...");
//		pd1.setCancelable(false);
//		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		pd1.setIndeterminate(false);
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				if(arg1.getAction().equals("com.nfs.youlin.find.FindFragment")){
					if(arg1.getIntExtra("init", 0)!=10086){
						Loger.d("TEST", "FriendCircleFragment mBroadcastReceiver====1");
						InitPullDown(categoryindex);
					}
				}
			}
	 };
	 private BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				if(arg1.getAction().equals("youlin.friend.action")){
					Loger.d("TEST", "FriendCircleFragment mBroadcastReceiver2");
					if(FriendCircleFragment.neighborInitLayout!=null){
						FriendCircleFragment.neighborInitLayout.setVisibility(View.GONE);
					}
					int what = arg1.getIntExtra("selfsendwhat", 0);
					if(categoryindex == 0 || categoryindex == what){
						newTopicPullDown();
					}
					
				}
				if(arg1.getAction().equals("youlin.friend.action.sayhello")){
					long topicId=arg1.getLongExtra("topic_id",0L);
					int count=arg1.getIntExtra("count", 0);
					for (int i = 0; i < allList.size(); i++) {
						if(((ForumTopic)allList.get(i)).getTopic_id()==topicId){
							ForumTopic forumTopic=(ForumTopic)allList.get(i);
							forumTopic.setComment_num(count);
							forumTopic.setObject_data("[{\"sayHelloStatus\":1}]");
							allList.set(i, forumTopic);
							Loger.d("TEST", "FriendCircleFragment mBroadcastReceiver2_2222222222222222222--->"+topicId+" "+count);
							break;
						}
					}
					maddapter.notifyDataSetChanged();
				}
				if(arg1.getAction().equals("youlin.friend.action.black")){
					long senderId=arg1.getLongExtra("sender_id",0L);
					Iterator<Object> iterator=allList.iterator();
					while(iterator.hasNext()){
						ForumTopic forumTopic=(ForumTopic)iterator.next();
						if(forumTopic.getSender_id()==senderId){
							ForumtopicDaoDBImpl forumtopicDaoDBImplObj = new ForumtopicDaoDBImpl(getActivity());
							forumtopicDaoDBImplObj.deleteObject(forumTopic.getTopic_id());
							iterator.remove();
							Loger.i("TEST", "FriendCircleFragment mBroadcastReceiver2_3333333333333--->"+forumTopic.getSender_id()+" "+senderId);
						}
					}
					maddapter.notifyDataSetChanged();
				}
			}
	 };
	 private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver(){
		 @Override
		 public void onReceive(Context arg0, Intent arg1) {
			 // TODO Auto-generated method stub
			 if(arg1.getAction().equals("youlin.delete.topic.action")){
				 Loger.d("TEST", "FriendCircleFragment mBroadcastReceiver3");
//				 Loger.i("youlin","2222222222222222222---->"+arg1.getIntExtra("ID", 0)+" "+arg1.getIntExtra("type", 0));
				 if(arg1.getIntExtra("type", 0)==0){
					 TitleBarSearchActivity.forumtopicLists.remove(arg1.getIntExtra("ID", 0));
					 TitleBarSearchActivity.maddapter.notifyDataSetChanged();
				 }else if(arg1.getIntExtra("type", 0)==1){
					 FriendCircleFragment.allList.remove(arg1.getIntExtra("ID", 0));
					 FriendCircleFragment.maddapter.notifyDataSetChanged();
				 }else if(arg1.getIntExtra("type", 0)==2){
					 MyPushActivity.forumtopicLists.remove(arg1.getIntExtra("ID", 0));
					 MyPushActivity.maddapter.notifyDataSetChanged();
				 }else if(arg1.getIntExtra("type", 0)==3){ // collection delete change
//					 CollectionActivity.forumtopicLists.remove(arg1.getIntExtra("ID", 0));
					 ((ForumTopic)CollectionActivity.forumtopicLists.get(arg1.getIntExtra("ID", 0))).setdeleteflag("2");
					 ((ForumTopic)CollectionActivity.maddapter.data.get(arg1.getIntExtra("ID", 0))).setdeleteflag("2");
					 CollectionActivity.maddapter.notifyDataSetChanged();
				 }else if(arg1.getIntExtra("type", 0)==6){
					 PropertyGonggaoActivity.forumtopicLists.remove(arg1.getIntExtra("ID", 0));
					 PropertyGonggaoActivity.maddapter.notifyDataSetChanged();
				 }else if(arg1.getIntExtra("type", 0)==7){
					 PropertyAdviceActivity.forumtopicLists.remove(arg1.getIntExtra("ID", 0));
					 PropertyAdviceActivity.maddapter.notifyDataSetChanged();
				 }
//				 Toast toast = Toast.makeText(getActivity(),"删除成功！", Toast.LENGTH_SHORT);
//				 toast.setGravity(Gravity.CENTER, 0, 0);
//				 toast.show();
			 }
		 }
	 };
	 private BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try {
				if(intent.getAction().equals("youlin.new.topic.action")){
					Loger.i("TEST", "FriendCircleFragment mBroadcastReceiver111111111111111114");
					if(App.sLoadNewTopicStatus == true){
						return;
					}
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							MainActivity.neighborBt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableNeighborNormalUnread, null, null);
						}
					});
					App.sLoadNewTopicStatus = true;
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
								newMoreTopicLayout.setVisibility(View.VISIBLE);
								newTopTextView.setVisibility(View.VISIBLE);
						}
					});
					newMoreTopicLayout.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							// TODO Auto-generated method stub
							newMoreTopicLayout.setVisibility(View.GONE);
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									MainActivity.neighborBt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableNeighborNormal, null, null);
								}
							});
							newTopTextView.setVisibility(View.GONE);
							ylProgressDialog.show();
							allListreplace.clear();
							for(int i =0;i<allList.size();i++){
								allListreplace.add(allList.get(i));
							}
							
							allList.clear();
							allList.add(new ForumTopic(getActivity()));
							maddapter.notifyDataSetChanged();
//							waitLoadInfoLayout.setVisibility(View.VISIBLE);
							RequestParams params = new RequestParams();
							if(allList.size()==0){
								params.put("topic_id",-1);
							}else{
								params.put("topic_id",id);
							}
							//params.put("count", PAGE_SIZE);
							currentCommunityId = getCurrentCommunityId();
							Loger.i("TEST", "POST当前小区ID->"+currentCommunityId);
							params.put("community_id", currentCommunityId);
							params.put("user_id", App.sUserLoginId);
							if(categorytype[categoryindex] != 0){
								params.put("category_type",categorytype[categoryindex]);
							}
							params.put("tag",tagstr[categoryindex]);  //tagstr[categoryindex],apistr[categoryindex]
							params.put("apitype",apistr[categoryindex]);
							AsyncHttpClient httpClient = new AsyncHttpClient();
							httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
									new JsonHttpResponseHandler(){
								@Override
								public void onSuccess(int statusCode, Header[] headers,
										JSONArray response) {
									// TODO Auto-generated method stub 18945051215
									Loger.i("TEST", response.toString());
									if(FriendCircleFragment.neighborInitLayout!=null){
										FriendCircleFragment.neighborInitLayout.setVisibility(View.GONE);
									}
									listBundle.clear();
									getTopicDetailsInfos(response);
									addResponseObject(listBundle);
									responseStatus = true;
									responseResult = "ok";
									super.onSuccess(statusCode, headers, response);
								}
								public void onSuccess(int statusCode, Header[] headers,
										JSONObject response) {
									// TODO Auto-generated method stub 18945051215
									try {
										if(response.getString("flag").equals("no")){
											responseStatus = true;
											responseResult = "ok";
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
									responseStatus = false;
									responseResult = "no";
									super.onFailure(statusCode, headers, responseString, throwable);
								}
							});
							
							new AsyncTask<Void, Void, Void>(){
								@Override
								protected Void doInBackground(Void... params) {
									// TODO Auto-generated method stub
									Long currenttime = System.currentTimeMillis();
									while(!responseStatus){
										if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME+6000){
											responseStatus = true;
											responseResult = "no";
										}
									}
									return null;
								}
								protected void onPostExecute(Void result) {
									if(responseStatus == true && "ok".equals(responseResult)){
										newTopTextView.setVisibility(View.GONE);
										newMoreTopicLayout.setVisibility(View.GONE);
//										waitLoadInfoLayout.setVisibility(View.GONE);
										ylProgressDialog.dismiss();
										allList.clear();
										for(int i =0;i<allListreplace.size();i++){
											allList.add(allListreplace.get(i));
										}
										newPullDown();
									}else if(responseStatus == true && !"ok".equals(responseResult)){
										ylProgressDialog.dismiss();
										allList.clear();
										for(int i =0;i<allListreplace.size();i++){
											allList.add(allListreplace.get(i));
										}
										maddapter.notifyDataSetChanged();
									}
									
									App.sLoadNewTopicStatus = false;
									responseStatus = false;
									responseResult = null;
								};
							}.execute();
						}
					});
				 }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	 };
	 private BroadcastReceiver mBroadcastReceiver5 = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				if(arg1.getAction().equals("youlin.initFriend.action")){
					Loger.i("TEST", "youlin.initFriend.action---->0000000000000000000000");
					InitPullDown(categoryindex);
					if(YLPushInitialization.isPushStop(getActivity())){
						YLPushInitialization.resumePush(getActivity());
					}
				}
				if(arg1.getAction().equals("youlin.update.topic.action")){
					String topicId=arg1.getStringExtra("topic_id");
					for (int i = 0; i < allList.size(); i++) {
						if(String.valueOf(((ForumTopic)allList.get(i)).getTopic_id()).equals(topicId)){
							updatePosition=i;
							gethttpdata(Long.parseLong(topicId),1);
						}
					}
				}
			}
	 };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//allList=new ArrayList<Object>();
		//allList.clear();
		drawableNeighborNormal=getActivity().getResources().getDrawable(R.drawable.tab_bg_selector);
		drawableNeighborNormalUnread=getActivity().getResources().getDrawable(R.drawable.tab_bg_selector_unread);
		daoDBImpl = new ForumtopicDaoDBImpl(this.getActivity());
		buildprocessdialog();
//		allListreplace.add(new ForumTopic(getActivity()));
		//InitPullDown();
		//maddapter = new FriendCircleAdapter(allList,getActivity(),0,1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loger.i("slide", "FriendCircleFragment-onCreateView");
		if (view == null) {
			view = inflater.inflate(R.layout.friend_circle_fragment, container, false);
		}
		statusutils = new StatusChangeutils();
		statusutils.statuschangelistener("CATEGORY",this);
		
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);// 先移除
		}
		neighborInitLayout = (LinearLayout)view.findViewById(R.id.neighbor_init);
		newTopTextView = (TextView)view.findViewById(R.id.tv_new_topic_notice);
		newMoreTopicLayout = (LinearLayout)view.findViewById(R.id.ll_new_topic_notice);
		friendCircleLv=(ListView)view.findViewById(R.id.friend_circle_lv);
		//ImageLoader imageLoader = ImageLoader.getInstance();
		//friendCircleLv.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true));
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				friendCircleLv.setAdapter(maddapter);
			}
		});
		viewGroup = (ViewGroup)view.findViewById(R.id.list_layout);
//		circleviewGroup = (ViewGroup)view.findViewById(R.id.circlefragment);
		mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
		currentCommunityId = getCurrentCommunityId();
		
		ActionBarPullToRefresh.from(getActivity())
        .insertLayoutInto(viewGroup)
        .theseChildrenArePullable(R.id.friend_circle_lv)
        .listener(this)
        .setup(mPullToRefreshLayout);
        Loger.i("TEST","开始加载最新数据");
    	IntentFilter filter = new IntentFilter();
		filter.addAction("com.nfs.youlin.find.FindFragment");
		filter.setPriority(998);
		getActivity().registerReceiver(mBroadcastReceiver, filter);

    	IntentFilter filter2 = new IntentFilter();
		filter2.addAction("youlin.friend.action");
		filter2.addAction("youlin.friend.action.sayhello");
		filter2.addAction("youlin.friend.action.black");
		filter2.setPriority(999);
		getActivity().registerReceiver(mBroadcastReceiver2, filter2);
		
		IntentFilter filter3 = new IntentFilter();
		filter3.addAction("youlin.delete.topic.action");
		filter3.setPriority(997);
		getActivity().registerReceiver(mBroadcastReceiver3, filter3);
		
		IntentFilter filter4 = new IntentFilter();
		filter4.addAction("youlin.new.topic.action");
		filter4.setPriority(994);
		getActivity().registerReceiver(mBroadcastReceiver4, filter4);
		
		IntentFilter filter5 = new IntentFilter();
		filter5.addAction("youlin.initFriend.action");
		filter5.addAction("youlin.update.topic.action");
		getActivity().registerReceiver(mBroadcastReceiver5, filter5);
		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Loger.i("slide", "FriendCircleFragment--onPause");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		

		Loger.i("slide", "FriendCircleFragment--onStop");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			getActivity().unregisterReceiver(mBroadcastReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getActivity().unregisterReceiver(mBroadcastReceiver2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getActivity().unregisterReceiver(mBroadcastReceiver3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			getActivity().unregisterReceiver(mBroadcastReceiver4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			getActivity().unregisterReceiver(mBroadcastReceiver5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Loger.i("slide", "FriendCircleFragment--onDestroy");
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			//maddapter.notifyDataSetChanged();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "onResumeError==>"+e.getMessage());
			e.printStackTrace();
		}
		//maddapter = new FriendCircleAdapter(allList, getActivity());
		//friendCircleLv.setAdapter(maddapter);
	}
	
	private void addResponseObject(List<Bundle> bundles){
		if(bundles==null){
			return;
		}
		ForumtopicDaoDBImpl daoDBImpl = new ForumtopicDaoDBImpl(this.getActivity());
		for(int i=0;i<bundles.size();i++){
			ForumTopic forumTopic = new ForumTopic(this.getActivity());
			forumTopic.setCache_key(Integer.parseInt(bundles.get(i).getString("cache_key")));
			forumTopic.setTopic_id(Long.parseLong(bundles.get(i).getString("topic_id")));
			forumTopic.setForum_id(Long.parseLong(bundles.get(i).getString("forum_id")));
			forumTopic.setForum_name(bundles.get(i).getString("forum_name"));
			forumTopic.setCircle_type(Integer.parseInt(bundles.get(i).getString("circle_type")));
			forumTopic.setSender_id(Long.parseLong(bundles.get(i).getString("sender_id")));
			forumTopic.setSender_name(bundles.get(i).getString("sender_name"));
			forumTopic.setSender_lever(bundles.get(i).getString("sender_lever"));
			forumTopic.setSender_portrait(bundles.get(i).getString("sender_portrait"));
			try {
				forumTopic.setSender_family_id(Long.parseLong(bundles.get(i).getString("sender_family_id")));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				forumTopic.setSender_family_id(0);
				e.printStackTrace();
			}
			forumTopic.setSender_family_address(bundles.get(i).getString("sender_family_address"));
			forumTopic.setDisplay_name(bundles.get(i).getString("disply_name"));
			forumTopic.setTopic_time(Long.parseLong(bundles.get(i).getString("topic_time")));
			forumTopic.setTopic_title(bundles.get(i).getString("topic_title"));
			forumTopic.setTopic_content(bundles.get(i).getString("topic_content"));
			forumTopic.setTopic_category_type(Integer.parseInt(bundles.get(i).getString("topic_category_type")));
			forumTopic.setSender_community_id(Long.parseLong(bundles.get(i).getString("sender_community_id")));
			String commentNum = bundles.get(i).getString("comment_num");
			if(commentNum==null || commentNum.isEmpty() || commentNum.length()<=0 || commentNum == "null"){
				commentNum = "0";
			}
			forumTopic.setComment_num(Integer.parseInt(commentNum));
			String likeNum = bundles.get(i).getString("like_num");
			if(likeNum==null || likeNum.isEmpty() || likeNum.length()<=0 || likeNum == "null"){
				likeNum = "0";
			}
			forumTopic.setLike_num(Integer.parseInt(likeNum));
			forumTopic.setSend_status(Integer.parseInt(bundles.get(i).getString("send_status")));
			String visiableType = bundles.get(i).getString("visiable_type");
			if(visiableType==null || visiableType.isEmpty() || visiableType.length()<=0 || visiableType == "null"){
				visiableType = "0";
			}
			forumTopic.setVisiable_type(Integer.parseInt(visiableType));
			String hotFlag = bundles.get(i).getString("hot_flag");
			if(hotFlag==null || hotFlag.isEmpty() || hotFlag.length()<=0 || hotFlag == "null"){
				hotFlag = "0";
			}
			forumTopic.setHot_flag(Integer.parseInt(hotFlag));
			String viewNum = bundles.get(i).getString("view_num");
			if(viewNum==null || viewNum.isEmpty() || viewNum.length()<=0 || viewNum == "null"){
				viewNum = "0";
			}
			forumTopic.setView_num(Integer.parseInt(viewNum));
			String likeStatus = bundles.get(i).getString("praise_type");
			if(likeStatus==null || likeStatus.isEmpty() || likeStatus.length()<=0 || likeStatus == "null"){
				likeStatus = "0";
			}
			forumTopic.setLike_status(Integer.parseInt(likeStatus));
			String objectType = bundles.get(i).getString("topic_type");
			if(objectType==null || objectType.isEmpty() || objectType.length()<=0 || objectType == "null"){
				objectType = "0";
			}
			forumTopic.setObject_type(Integer.parseInt(objectType));
			forumTopic.setMeadia_files_json(bundles.get(i).getString("media_files_json"));
			forumTopic.setComments_summary(bundles.get(i).getString("comment_json"));
			forumTopic.setObject_data(bundles.get(i).getString("object_json"));
			forumTopic.setSender_nc_role(Integer.parseInt(bundles.get(i).getString("collectStatus")));
			daoDBImpl.saveObject(forumTopic);
		}
		daoDBImpl.releaseDatabaseRes();
	}
	
	private void getTopicDetailsInfos(JSONArray response){
		int responseLen = response.length();
		for (int i = 0; i < responseLen; i++) {
			try {
//				Loger.i("TEST", "mediaJson->start"+"response="+response.toString());
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
				String topic_type = jsonObject.getString("objectType");  //0.表示一般话题、1.表示活动
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
//					Loger.i("TEST", "(E)getTopic->"+e.getMessage());
					e.printStackTrace();
				}
				String comment_json = null;
				try {
					comment_json = jsonObject.getJSONArray("comments").toString();
				} catch (Exception e) {
//					Loger.i("TEST", "(E)getTopicComm->"+e.getMessage());
					e.printStackTrace();
				}
				String object_json = null;
				try {
					object_json = jsonObject.getJSONArray("objectData").toString();
				} catch (Exception e) {
//					Loger.i("TEST", "(E)getTopicComm->"+e.getMessage());
					e.printStackTrace();
				}
				App.CurrentSysTime = Long.parseLong(jsonObject.getString("systemTime"));
				bundle = new Bundle();
				bundle.putString("cache_key", cache_key);
				bundle.putString("topic_id", topic_id);
				bundle.putString("forum_id", forum_id);
				bundle.putString("forum_name", forum_name);
				bundle.putString("circle_type", circle_type);
				bundle.putString("sender_id", sender_id);
				bundle.putString("sender_name", sender_name);
				bundle.putString("sender_lever", sender_lever);
				bundle.putString("sender_portrait", sender_portrait);
				bundle.putString("sender_family_id", sender_family_id);
				bundle.putString("sender_family_address", sender_family_address);
				bundle.putString("disply_name", disply_name);
				bundle.putString("topic_time", topic_time);
				bundle.putString("topic_title", topic_title);
				bundle.putString("topic_content", topic_content);
				bundle.putString("topic_category_type", topic_category_type);
				bundle.putString("topic_type",topic_type);
				bundle.putString("comment_num", comment_num);
				bundle.putString("like_num", like_num);
				bundle.putString("praise_type", praise_type);
				bundle.putString("send_status", send_status);
				bundle.putString("visiable_type", visiable_type);
				bundle.putString("hot_flag", hot_flag);
				bundle.putString("view_num", view_num);
				bundle.putString("media_files_json", media_files_json);
				bundle.putString("comment_json", comment_json);
				bundle.putString("sender_community_id", sender_community_id);
				bundle.putString("object_json", object_json);
				bundle.putString("collectStatus", collectStatus);
				if(!listBundle2.contains(bundle)){
					listBundle2.add(bundle);
					listBundle.add(bundle);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
//				Loger.i("TEST", "ERROR->"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	private void InitTopicList(){
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				MainActivity.neighborBt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableNeighborNormal, null, null);
			}
		});
		RequestParams params = new RequestParams();
			if(allList.size()==0){
				params.put("topic_id",-1);
			}else{
				params.put("topic_id",id);
			}
		//params.put("count", PAGE_SIZE);
		currentCommunityId = getCurrentCommunityId();
		Loger.i("TEST", "POST当前小区ID->"+currentCommunityId);
		params.put("community_id", currentCommunityId);
		params.put("user_id", App.sUserLoginId);
		if(categorytype[categoryindex] != 0){
			params.put("category_type",categorytype[categoryindex]);
		}
		params.put("tag",tagstr[categoryindex]);//tagstr[categoryindex],apistr[categoryindex]
		params.put("apitype",apistr[categoryindex]);
		Message message = new Message();
		message.what = 10086;
		getTopicHandler.sendMessage(message);
		SyncHttpClient httpClient = new SyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub
				newTopTextView.setVisibility(View.GONE);
				newMoreTopicLayout.setVisibility(View.GONE);
				if(FriendCircleFragment.neighborInitLayout!=null){
					FriendCircleFragment.neighborInitLayout.setVisibility(View.GONE);
				}
				listBundle.clear();
				getTopicDetailsInfos(response);
				addResponseObject(listBundle);
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
//						allList.remove(0);
					RequestParams params = new RequestParams();
					params.put("tag","systemtime");
					params.put("apitype",IHttpRequestUtils.APITYPE[0]);
					SyncHttpClient httpClient = new SyncHttpClient();
					httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
							new JsonHttpResponseHandler(){
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							// TODO Auto-generated method stub
							try {
								App.CurrentSysTime=response.getLong("system_time");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							super.onSuccess(statusCode, headers, response);
						}
						
					});
						message.what = FIRST_SUCCESS_CODE;
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
				new ErrorServer(getActivity(), responseString.toString());
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});

	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == REQUEST_APPLY_NUM) {
				Loger.d("test4", "REQUEST_APPLY_NUM result  1111111111111111");
			}
	}
	private Handler getTopicHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 10086:
				ylProgressDialog.cancel();
//				waitLoadInfoLayout.setVisibility(View.GONE);
				newTopTextView.setVisibility(View.GONE);
				newMoreTopicLayout.setVisibility(View.GONE);
				App.sLoadNewTopicStatus=false;
				break;
			case UP_SUCCESS_CODE:
				Loger.i("TEST", "UP访问服务器成功！！！！！！！！");
				pullUp();
				bPullUpStatus = true;
				break;
			case FAILED_CODE:
				Loger.i("TEST", "访问服务器失败！！！！！！！！");
				break;
			case REFUSE_CODE:
				Loger.i("TEST", "服务器拒绝访问！！！！！！！！");
				break;
			case FIRST_SUCCESS_CODE:
				Loger.i("TEST", "访问成功！没有数据返回！");
				bPullUpStatus = true;
				bPullDownStatus = true;
				newPullDown();
				maddapter.notifyDataSetChanged();
				break;
			case NOINFO_SUCCESS_CODE:
				
				bNoNewInfoStatus = true;
				bPullUpStatus = true;
				bPullDownStatus = true;
				break;
			case INIT_FAILED_CODE:
				
				Loger.i("TEST", "第一次访问服务器失败！！！！！！！！");
				break;
			case INIT_REFUSE_CODE:
				Loger.i("TEST", "第一次服务器拒绝访问！！！！！！！！");
				break;
			case NEW_FIRST_SUCCESS_CODE:
				try {
					ylProgressDialog.dismiss();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				maddapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	
	private void InitPullDown(final int index){
//		tagstr[categoryindex],apistr[categoryindex],categorytype[categoryindex]
		RequestParams params = new RequestParams();
			//params.put("topic_id",((ForumTopic)allList.get(0)).getTopic_id());
			currentCommunityId = getCurrentCommunityId();
			params.put("community_id", currentCommunityId);
			params.put("user_id", App.sUserLoginId);
			if(categorytype[index] != 0){
				params.put("category_type",categorytype[index]);
				Loger.i("TEST", "POST当前小区000------->");
			}
			params.put("tag",tagstr[index]);
			params.put("apitype", apistr[index]);
			Loger.i("TEST", "POST当前小区------->"+currentCommunityId+" "+App.sUserLoginId+" "+categorytype[index]+" "+tagstr[index]+" "+apistr[index]);	
			AsyncHttpClient httpClient = new AsyncHttpClient();
			httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
					new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						final JSONArray response) {
					// TODO Auto-generated method stub 18945051215
//					new Thread(new Runnable() {
//						@Override
//						public void run() {
							// TODO Auto-generated method stub
						Loger.i("TEST", "friendfragment22222222222222222222222222222222------>"+response);
							if(MainActivity.ylProgressDialog!=null && MainActivity.ylProgressDialog.isShowing()){
								MainActivity.ylProgressDialog.dismiss();
							}
							if(FriendCircleFragment.neighborInitLayout!=null){
								FriendCircleFragment.neighborInitLayout.setVisibility(View.GONE);
							}
							allList.clear();
							allListreplace.clear();
							listBundle.clear();
							daoDBImpl.deleteAllObjects();
							Loger.i("TEST", "删除完成");
							getTopicDetailsInfos(response);
							addResponseObject(listBundle);
							categoryindex = index;
							forumtopicLists = daoDBImpl.findAllObject(listBundle.size());
							
							for(Object obj : forumtopicLists){
								allList.add(obj);
							}
							//((ForumTopic)forumtopicLists.get(forumtopicLists.size()-1)).setfirsttopictext(index);
							if(forumtopicLists.size()!=0){
								allList.add(forumtopicLists.get(forumtopicLists.size()-1));  //hyy   空白站位  应为  adapter中position=position-1
							}
							
							try {
								id=((ForumTopic)allList.get(0)).getTopic_id();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							daoDBImpl.releaseDatabaseRes();
							MainActivity.sMainInitData = 1;
							Message message = new Message();
							message.what = NEW_FIRST_SUCCESS_CODE;
							httpflag = "ok";
							getcirclerequest = true;
							getTopicHandler.sendMessage(message);
//						}
//					}).start();
					
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
					String flag = null;
					Message message = new Message();
					try {
//						allList.clear();
						listBundle.clear();
						daoDBImpl.deleteAllObjects();
						flag = response.getString("flag");
						if("no".equals(flag)){
							Loger.i("TEST", "topic is empty yes");
							if(MainActivity.ylProgressDialog!=null && MainActivity.ylProgressDialog.isShowing()){
								MainActivity.ylProgressDialog.dismiss();
							}
							message.what = INIT_FAILED_CODE;
//							ForumTopic topic = new ForumTopic(getActivity());
//									topic.setfirsttopictext(index);
//									allList.add(topic );
//							categoryindex = index;
							allList.clear();
							allList.add(new ForumTopic(getActivity()));  //hyy   空白站位  应为  adapter中position=position-1
							maddapter.notifyDataSetChanged();
							Toast toast = Toast.makeText(getActivity() ,str[index]+"中还没有内容呢！", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							getcirclerequest = true;
							Loger.i("TEST", "topic is empty yes"+categoryindex);
							MainActivity.sMainInitData = 1;
						}else{
							Loger.i("TEST", "topic is empty no");
							message.what = INIT_FAILED_CODE;
							MainActivity.sMainInitData = 1;
						}
						getTopicHandler.sendMessage(message);
						ylProgressDialog.dismiss();
//						httpflag = "ok";
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						message.what = INIT_FAILED_CODE;
						MainActivity.sMainInitData = 1;
						e.printStackTrace();
					}
//					initPd.setVisibility(View.GONE);
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					// TODO Auto-generated method stub
					httpflag = "ok";
					Message message = new Message();
					message.what = REFUSE_CODE;
					getTopicHandler.sendMessage(message);
					new ErrorServer(getActivity(), responseString.toString());
//					initPd.setVisibility(View.GONE);
					MainActivity.sMainInitData = 1;
					super.onFailure(statusCode, headers, responseString, throwable);
				}
				
			});
			//friendCircleLv.setSelection(0);
	}
	private void newPullDown(){
		//friendCircleLv.setSelection(0);
		//friendCircleLv.setSelectionAfterHeaderView();
		//friendCircleLv.smoothScrollToPosition(0);
		Loger.i("LYM", "000000000000000000--------------------------"+allList.size());
		if(allList.size()==0){
			forumtopicLists = daoDBImpl.findNewObject(-1);
			Loger.i("LYM", "11111111111111111111--------------------------");
		}else{
			forumtopicLists = daoDBImpl.findNewObject(((ForumTopic)allList.get(0)).getTopic_id());
			Loger.i("LYM", "2222222222222222222--------------------------");
//			Loger.d("test", "id="+((ForumTopic)allList.get(0)).getTopic_id());
		}
	
		if(allList.size()==0){
			if(forumtopicLists.size()!=0){
				allList.add(0,forumtopicLists.get(0));//呵呵呵呵呵呵呵呵和
				Loger.i("LYM", "33333333333333--------------------------");
			}else{
				allList.add(0,new ForumTopic(getActivity()));//呵呵呵呵呵呵呵呵和2
				Loger.i("LYM", "44444444444444444--------------------------");
			}
		}
		Loger.i("LYM", "555555555555555555555--------------------------" + forumtopicLists.size());
		for (Object obj : forumtopicLists) {
			allList.add(0, obj);
			Loger.i("LYM", "6666666666666666666--------------------------");
		}
		Loger.i("LYM", "7777777777777777777777--------------------------"+allList.size());
		friendCircleLv.setAdapter(maddapter);
		maddapter.notifyDataSetChanged();
	}
	
	public void newTopicPullDown(){ 
		RequestParams params = new RequestParams();
		if(allList.size()==0){
			params.put("topic_id",-1);
		}else{
			params.put("topic_id",id);
		}
		//params.put("count", PAGE_SIZE);
		currentCommunityId = getCurrentCommunityId();
		Loger.i("TEST", "POST当前小区ID->"+currentCommunityId);
		params.put("community_id", currentCommunityId);
		params.put("user_id", App.sUserLoginId);
		if(categorytype[categoryindex] != 0){
			params.put("category_type",categorytype[categoryindex]);
		}
		params.put("tag",tagstr[categoryindex]);//tagstr[categoryindex],apistr[categoryindex]
		params.put("apitype", apistr[categoryindex]);
		if(App.sLoadNewTopicStatus){//有推送消息
//			if(waitLoadInfoLayout==null){
//				waitLoadInfoLayout = (LinearLayout) getActivity().findViewById(R.id.ll_wait_new_topic_info);
//			}
			if(newTopTextView==null){
				newTopTextView = (TextView)getActivity().findViewById(R.id.tv_new_topic_notice);
			}
			if(newMoreTopicLayout == null){
				newMoreTopicLayout = (LinearLayout) getActivity().findViewById(R.id.ll_new_topic_notice);
			}
			try {
//				waitLoadInfoLayout.setVisibility(View.GONE);
				ylProgressDialog.cancel();
				newTopTextView.setVisibility(View.GONE);
				newMoreTopicLayout.setVisibility(View.GONE);
				App.sLoadNewTopicStatus = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Loger.i("TEST","InitTopicUI.error=>"+e.getMessage());
				e.printStackTrace();
			}
		}
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub 18945051215
				Loger.i("TEST","7777777777777777777777777777777--->"+response.toString());
				if(FriendCircleFragment.neighborInitLayout!=null){
					FriendCircleFragment.neighborInitLayout.setVisibility(View.GONE);
				}
				listBundle.clear();
				getTopicDetailsInfos(response);
				addResponseObject(listBundle);
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
						message.what = FIRST_SUCCESS_CODE;
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
				
				new ErrorServer(getActivity(), responseString.toString());
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		//friendCircleLv.setSelection(0);
	}
	
	public void pullUpFromSrv(){
		RequestParams params = new RequestParams();
		currentCommunityId = getCurrentCommunityId();
		long currentTopicID = ((ForumTopic)(allList.get(allList.size()-1))).getTopic_id();
		Loger.i("TEST", "最后一个TopicId->"+currentTopicID);
		params.put("count", PAGE_SIZE);
		params.put("community_id", currentCommunityId);
		if(categorytype[categoryindex] != 0){
			params.put("category_type",categorytype[categoryindex]);
		}
		params.put("topic_id", currentTopicID);
		params.put("user_id", App.sUserLoginId);
		params.put("tag",tagstr[categoryindex]);//tagstr[categoryindex],apistr[categoryindex]
		params.put("apitype",apistr[categoryindex]);
		SyncHttpClient httpClient = new  SyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub
				Loger.i("lym", "1111--->"+response.toString());
				getTopicDetailsInfos(response);
				addResponseObject(listBundle);
				allList.remove(allList.size() - 1);   //hyy
				Message message = new Message();
				message.what = UP_SUCCESS_CODE;
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
				new ErrorServer(getActivity(), responseString.toString());
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	public void pullUp(){
		//coding..................................
		long curTopicId=0;
		try {
			curTopicId = ((ForumTopic)(allList.get(allList.size()-1))).getTopic_id();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		forumtopicLists = daoDBImpl.findAllObject(PAGE_SIZE, curTopicId);
		Loger.i("TEST", "下拉的当前TopicId->"+curTopicId);	
		Loger.i("TEST", "从本地数据库查询的下拉数据个数->"+forumtopicLists.size());
		long lastForumtopicListsObj = 0;
		try {
			lastForumtopicListsObj = ((ForumTopic)(forumtopicLists.get(forumtopicLists.size()-1))).getTopic_id();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(lastForumtopicListsObj==curTopicId){
			Loger.i("TEST", "已经是最新的数据，不需要进行更新！");
			return;
		}
		for (Object obj : forumtopicLists) {
			allList.add(obj);
		}
		allList.add(forumtopicLists.get(forumtopicLists.size()-1));  //hyy
	}
	
	public void setFlag(){
		ForumTopic forumTopic=new ForumTopic(getActivity());
		forumTopic.setFlag(true);
		allList.add(forumTopic);
	}
	
	@Override
	public void onRefreshStarted(View view) {
		// TODO Auto-generated method stub
		// Hide the list

		final ListView absListView = (ListView) view;
		if (mPullToRefreshLayout.mPullToRefreshAttacher.getmode() == 1) {
			Loger.d("hyytest",
					"mPullToRefreshLayout.mPullToRefreshAttacher.getmode()==1");
			
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						Thread.sleep(500);
						//allList.clear();
						InitTopicList();
						Long currenttime = System.currentTimeMillis();
						while(!bPullDownStatus){
							if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
								bPullDownStatus = true;
							}
						}
						Loger.i("TEST", "pulldown置位成功！！");
						bPullDownStatus = false;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// Notify PullToRefreshLayout that the refresh has finished
					mPullToRefreshLayout.setRefreshComplete();
					if (getView() != null) {
						// Show the list again
						// setListShown(true);
					}
				}
			}.execute();
		} else {
			// upprogress.setVisibility(View.VISIBLE);
			bNoNewInfoStatus = false;
			if (refreshflag == true) {
				mPullToRefreshLayout.setRefreshComplete();
			} else {
				refreshflag = true;
				setFlag();
				Loger.d("youlin", "LIST.size = " + allList.size());
				maddapter.notifyDataSetChanged();
				Loger.d("test1",
						"" + absListView.getLastVisiblePosition() + "++++"
								+ absListView.getFirstVisiblePosition() + "+++"
								+ absListView.getCount() + "++++"
								+ absListView.getChildCount());

//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						while (absListView.getChildCount() != absListView
//								.getCount()
//								- absListView.getFirstVisiblePosition()) {
//							;
//						}
//
//						getActivity().runOnUiThread(new Runnable() {
//
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								// absListView.getChildAt(absListView.getCount()-
//								// absListView.getFirstVisiblePosition()- 1)
//								// .findViewById(R.id.gengxin)
//								// .setVisibility(View.VISIBLE);
//							}
//						});
//					}
//				}).start();
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
					try {
							//刷新数据库
							Thread.sleep(500);
							allList.remove(allList.size() - 1);
							pullUpFromSrv();
							Long currenttime = System.currentTimeMillis();
							while(!bPullUpStatus){
								if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
									bPullUpStatus = true;
								}
							}
							Loger.i("TEST", "pullUp置位成功！！");
							
					} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);

						// Notify PullToRefreshLayout that the refresh has
						// finished
						// upprogress.setVisibility(View.INVISIBLE);
						Loger.d("test5", "bNoNewInfoStatus = "+bNoNewInfoStatus);
						if(bNoNewInfoStatus){
							setFlag();
							maddapter.notifyDataSetChanged();
							// absListView.getChildAt(absListView.getChildCount()-1).findViewById(R.id.gengxin).setVisibility(View.INVISIBLE);
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									allList.remove(allList.size() - 1);
									getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											maddapter.notifyDataSetChanged();
											mPullToRefreshLayout.setRefreshComplete();
										}
									});	
								}
							},300);
						}else{
							maddapter.notifyDataSetChanged();
							mPullToRefreshLayout.setRefreshComplete();
						}
						bPullUpStatus = false;
						if (getView() != null) {
							// Show the list again
							// setListShown(true);
							refreshflag = false;
						}
					}
				}.execute();
			}
		}

	}
	
	private String getCurrentCommunityId(){
		SharedPreferences sharedata = this.getActivity().getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String communityId = sharedata.getString("familycommunityid", "0");
		//String communityId = String.valueOf(App.sFamilyCommunityId);
		if(communityId=="" || communityId==null || communityId=="null"){
			communityId = "0";
		}
		return communityId;
	}
	
	private void gethttpdata(long topicId,int request_index){
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
		if(categorytype[categoryindex] == 0){
			reference.put("type", 2);
		}
		reference.put("tag",tagstr[categoryindex]);//tagstr[categoryindex],apistr[categoryindex]
		reference.put("apitype", apistr[categoryindex]);
		Loger.i("youlin", "3333333333333333333333--->"+App.sFamilyCommunityId+" "+App.sUserLoginId+" "+topicId);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+ IHttpRequestUtils.YOULIN,reference,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,Header[] headers,JSONArray response) {
								Loger.i("TEST",response.toString());
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
								new ErrorServer(getActivity(), responseString.toString());
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
//					Loger.i("TEST", "mediaJson->start");
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
					App.CurrentSysTime = Long.parseLong(jsonObject.getString("systemTime"));
					ForumTopic forumTopic = new ForumTopic(getActivity());
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
					allList.set(updatePosition, forumTopic);
					maddapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "ERROR->"+e.getMessage());
					e.printStackTrace();
				}
			}
		}

	@Override
	public void setstatuschanged(int status) {
		// TODO Auto-generated method stub
//		InitPullDown();


	}

	@Override
	public void setstatuschanged(int status, Bundle data) {
		// TODO Auto-generated method stub
		Loger.d("test5", data.getString("category"));
		
		if(status == 1){
			if(!NetworkService.networkBool){
				Toast.makeText(getActivity(), "请先开启网络", Toast.LENGTH_SHORT).show();
				return;
			}
			if(FriendCircleAdapter.isFristMap!=null){
				FriendCircleAdapter.isFristMap.clear();
			}
			ylProgressDialog.show();
			allListreplace.clear();
			for(int i =0;i<allList.size();i++){
				allListreplace.add(allList.get(i));
			}
			
			allList.clear();
			allList.add(new ForumTopic(getActivity()));
			maddapter.notifyDataSetChanged();
			
			if(data.getInt("categoryid") == R.id.layout1){
//				((TextView)friendCircleLv.getChildAt(0).findViewById(R.id.morebutton)).setText("全部");
				InitPullDown(0);

				friendCircleLv.setSelection(1);
			}else if(data.getInt("categoryid") == R.id.layout2){
//				((TextView)friendCircleLv.getChildAt(0).findViewById(R.id.morebutton)).setText("话题");
				InitPullDown(1);
				friendCircleLv.setSelection(1);

			}else if(data.getInt("categoryid") == R.id.layout3){
//				((TextView)friendCircleLv.getChildAt(0).findViewById(R.id.morebutton)).setText("活动");
				InitPullDown(2);
				friendCircleLv.setSelection(1);

			}else if(data.getInt("categoryid") == R.id.layout4){
//				((TextView)friendCircleLv.getChildAt(0).findViewById(R.id.morebutton)).setText("活动");
				InitPullDown(3);
				friendCircleLv.setSelection(1);

			}else if(data.getInt("categoryid") == R.id.layout5){
//				((TextView)friendCircleLv.getChildAt(0).findViewById(R.id.morebutton)).setText("活动");
				InitPullDown(4);
				friendCircleLv.setSelection(1);

			}else if(data.getInt("categoryid") == R.id.layout6){
				InitPullDown(5);
				friendCircleLv.setSelection(1);

			}
			
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
						//网络请求 注册地址 参数{city,village,rooft,number} login_account
					Long currenttime = System.currentTimeMillis();
						while(!getcirclerequest){
							if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+6000)){
								getcirclerequest = true;
								break;
							}
						}
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					if(getcirclerequest == true && !httpflag.equals("ok")){
						ylProgressDialog.dismiss();
						allList.clear();
						for(int i =0;i<allListreplace.size();i++){
							allList.add(allListreplace.get(i));
						}
						maddapter.notifyDataSetChanged();
//						Toast.makeText(getActivity(), "请求服务器失败！", Toast.LENGTH_SHORT).show();
					}
					httpflag="none";
					getcirclerequest = false;
					super.onPostExecute(result);
				}
			}.execute();
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		if(!NetworkService.networkBool){
//			Toast.makeText(getActivity(), "请先开启网络", Toast.LENGTH_SHORT).show();
//			return;
//		}
//		if(FriendCircleAdapter.isFristMap!=null){
//			FriendCircleAdapter.isFristMap.clear();
//		}
//		ylProgressDialog.show();
//		allListreplace.clear();
//		for(int i =0;i<allList.size();i++){
//			allListreplace.add(allList.get(i));
//		}
//		
//		allList.clear();
//		allList.add(new ForumTopic(getActivity()));
//		maddapter.notifyDataSetChanged();
//		
//		if(v.getId() == R.id.layout1){
////			((TextView)friendCircleLv.getChildAt(0).findViewById(R.id.morebutton)).setText("全部");
//			InitPullDown(0);
//			categoryviewlay.setVisibility(View.GONE);
//			friendCircleLv.setSelection(1);
//		}else if(v.getId() == R.id.layout2){
////			((TextView)friendCircleLv.getChildAt(0).findViewById(R.id.morebutton)).setText("话题");
//			InitPullDown(1);
//			friendCircleLv.setSelection(1);
//			categoryviewlay.setVisibility(View.GONE);
//		}else if(v.getId() == R.id.layout3){
////			((TextView)friendCircleLv.getChildAt(0).findViewById(R.id.morebutton)).setText("活动");
//			InitPullDown(2);
//			friendCircleLv.setSelection(1);
//			categoryviewlay.setVisibility(View.GONE);
//		}else if(v.getId() == R.id.layout4){
////			((TextView)friendCircleLv.getChildAt(0).findViewById(R.id.morebutton)).setText("活动");
//			InitPullDown(3);
//			friendCircleLv.setSelection(1);
//			categoryviewlay.setVisibility(View.GONE);
//		}else if(v.getId() == R.id.layout5){
////			((TextView)friendCircleLv.getChildAt(0).findViewById(R.id.morebutton)).setText("活动");
//			InitPullDown(4);
//			friendCircleLv.setSelection(1);
//			categoryviewlay.setVisibility(View.GONE);
//		}else if(v.getId() == R.id.layout6){
//			InitPullDown(5);
//			friendCircleLv.setSelection(1);
//			categoryviewlay.setVisibility(View.GONE);
//		}
//		
//		((ImageView)friendCircleLv.getChildAt(0).findViewById(R.id.moreview)).setImageResource(R.drawable.jiantou);
//		new AsyncTask<Void, Void, Void>() {
//			@Override
//			protected Void doInBackground(Void... params) {
//					//网络请求 注册地址 参数{city,village,rooft,number} login_account
//				Long currenttime = System.currentTimeMillis();
//					while(!getcirclerequest){
//						if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+6000)){
//							getcirclerequest = true;
//							break;
//						}
//					}
//				return null;
//			}
//			@Override
//			protected void onPostExecute(Void result) {
//				if(getcirclerequest == true && !httpflag.equals("ok")){
//					ylProgressDialog.dismiss();
//					allList.clear();
//					for(int i =0;i<allListreplace.size();i++){
//						allList.add(allListreplace.get(i));
//					}
//					maddapter.notifyDataSetChanged();
////					Toast.makeText(getActivity(), "请求服务器失败！", Toast.LENGTH_SHORT).show();
//				}
//				httpflag="none";
//				getcirclerequest = false;
//				super.onPostExecute(result);
//			}
//		}.execute();
	}
}