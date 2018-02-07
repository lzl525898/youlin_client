package com.nfs.youlin.activity;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.InviteMessage;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.domain.InviteMessage.InviteMesageStatus;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.find.AddrVerifiyActivity;
import com.nfs.youlin.activity.find.FindFragment;
import com.nfs.youlin.activity.find.NewsDetail;
import com.nfs.youlin.activity.find.StoreCircleDetailActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.PropertyRepairActivity;
import com.nfs.youlin.activity.neighbor.PropertyRepairList;
import com.nfs.youlin.activity.personal.MyInformationFragment;
import com.nfs.youlin.activity.personal.OpinionFeedBackActivity;
import com.nfs.youlin.activity.personal.PersonalInfoGuanyuActivity;
import com.nfs.youlin.activity.personal.SelectaddrActivity;
import com.nfs.youlin.activity.personal.SystemSetActivity;
import com.nfs.youlin.activity.personal.addressseting;
import com.nfs.youlin.activity.personal.addressshow;
import com.nfs.youlin.activity.personal.selectvillageActivity;
import com.nfs.youlin.activity.personal.write_address;
import com.nfs.youlin.activity.square.SquareFragment;
import com.nfs.youlin.activity.titlebar.barter.BarterDedailCommentActivity;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.activity.titlebar.startactivity.SendActivity;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.adapter.FriendCircleImageAdapter;
import com.nfs.youlin.adapter.MyViewPagerAdapter;
import com.nfs.youlin.adapter.PopupMenuAddAdapter;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.dao.NeighborDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.entity.PushRecord;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.push.PushRecordDetailActivity;
import com.nfs.youlin.push.YLPushInitialization;
import com.nfs.youlin.push.YLPushNotificationBuilder;
import com.nfs.youlin.push.YLPushTagManager;
import com.nfs.youlin.service.NetworkService;
//import com.nfs.youlin.service.TokenService;
import com.nfs.youlin.service.YLUpdateService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends FragmentActivity implements
		OnPageChangeListener,EMEventListener{
	public static int sMainInitData = 0;
	public static MainActivity sMainActivity;
	private List<Bundle> listBundle;
	private List<Bundle> listBundle2;
	private Bundle bundle;
	private Thread initThread;
	private final String TAG = "TEST";
	public long mExitTime = 0;
	private ViewPager pager;
	private PagerAdapter mAdapter;
	//public static MainActivity sMainActivity;
	private ArrayList<Fragment> fragments;
	private ArrayList<RadioButton> title = new ArrayList<RadioButton>();// 三个标题
	private MyConnectionListener connectionListener = null;
	private int GET_CURRENT_CITY = 1;
	private int GET_CURRENT_VILLAGE = 2;
	private int GET_CURRENT_DETAIL = 3;
	public static String currentcity="未设置";
	public static String currentvillage= "未设置";
//	public static String currentaddrdetail = "未设置";
//	public static String familyId = "未设置";
//	public static String familyCommunityId = "未设置";
	private ActionBar actionBar;
	public static TextView userAddressTextView;
	public static ImageButton searchImageView;
	public static ImageButton addImageView;
	//public static ImageButton friendImageView;
	public static ImageView newMessageTextView;
	public static ImageButton newMessageLayout;
	public static RelativeLayout networkLayout;
	public static TextView networkTv;
	private MainActionBarclicklisener Titlebaritem;
//	private Intent networkIntent;
	private MenuInflater inflater;
	private RelativeLayout SearchGroup;
	public static String citycode;
	public static boolean bPushBindStatus = false;
	public static Bitmap bitmap ;
	/***********下拉刷新 请求邻居*********************/
	private Neighbor neighbor;
	private NeighborDaoDBImpl aNeighborDaoDBImpl;
	private boolean bRequestSet = false;
	private int REQUEST_NEIGHBOR = 1;
	public static List<JSONObject> jsonObjList;
	private String flag = null;
	/*********************************************/
	private String localVersion;
	private final int UPDATA_NONEED = 0;
	private final int UPDATA_FORCE = 5;
	private final int UPDATA_CLIENT = 1;
	private final int GET_UNDATAINFO_ERROR = 2;
	private final int SDCARD_NOMOUNTED = 3;
	private final int DOWN_ERROR = 4;
	private final int VERIFY_CODE = 9527;

	private int notificationStatusWithMain = 0;
	private int notificationStatusWithSplash = 0;
	private String notificationStringWithMain = null;
	private String notificationStringWithSplash = null;
	private String szFlag = "no";
	private boolean bRequestType = false;
	public static boolean sbooleanDisconnected = false; //防止重复释放
	public static boolean sbooleanUpdateJPush = false; //判断是否需要重新更新 
	public static YLProgressDialog ylProgressDialog;
	private UpdataInfo info;
	private final int UPDATE_DETAIL = 40001;
	private Timer init_Timer;
	public static ProgressDialog updatapd1;
	public static RadioButton neighborBt;
	public static RadioButton squareBt;
	public static RadioButton findBt;
	public static RadioButton myBt;
	private Drawable drawableNeighborNormal;
	private Drawable drawableFindNormal;
	private JSONObject evaJson;
	private final static int CWJ_HEAP_SIZE= 6*1024*1024;
	private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);// /slidingmenu里面重写了
		//int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);  
		//Log.i("LYM", "Max memory is " + maxMemory + "KB");
		//ActivityManager manager=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		//setMinHeapSize(200*1024*1024);
		//Loger.i("LYM", "1111111111111111111111111size-->"+manager.getMemoryClass());
		drawableNeighborNormal=getResources().getDrawable(R.drawable.tab_bg_selector);
		drawableFindNormal=getResources().getDrawable(R.drawable.tab_find_bg_selector);
		MainActivity.sbooleanDisconnected = false;
		ylProgressDialog=YLProgressDialog.createDialogWithTopic(MainActivity.this);
		ylProgressDialog.show();
		MainActivity.sMainInitData = 0;
		MainActivity.sMainActivity = this;
		bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused);
		networkLayout=(RelativeLayout)findViewById(R.id.network_layout);
		networkTv=(TextView)findViewById(R.id.network_tv);
		networkLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if("您的地址还未经过验证,请及时验证".equals(networkTv.getText().toString())){
					Intent intent=new Intent(MainActivity.this,AddrVerifiyActivity.class);
					AllFamilyDaoDBImpl daoDBImpl = new AllFamilyDaoDBImpl(getApplicationContext());
					AllFamily family = daoDBImpl.getCurrentAddrDetail("flag");
					intent.putExtra("familyRecordId", family.getFamily_address_id());
					intent.putExtra("familyId", family.getFamily_id());
					startActivityForResult(intent, VERIFY_CODE);
				}else if("您还没有设置地址".equals(networkTv.getText().toString())){
					AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(MainActivity.this);
					List<Object> familyObjs = allFDaoDBImpl.findPointTypeObject(App.sUserLoginId);
					int length =familyObjs.size();
					if(length==0 ){
						Intent intent = new Intent(MainActivity.this,addressseting.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						startActivity(intent);
					}
				}
			}
		});
		
		App.sDeleteTagFinish = false;
		App.sDeviceLoginStatus = false;
		actionBar=getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		localVersion = getVersion();
		updatapd1 = new ProgressDialog(this);
		updatapd1.setMessage("优邻正在更新...");
        updatapd1.setCancelable(false);
        updatapd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        updatapd1.setIndeterminate(false);
        DemoApplication myapp = (DemoApplication)getApplication();
        myapp.setupdateDialog(updatapd1);
		if(App.UPDATE_APK_STATUS){
			if(App.UPDATE_APK_TYPE == 1){  //强制
		        updatapd1.show();
			}
		}
		else{
			App.UPDATE_APK_PORSITION = "com.nfs.youlin.activity.MainActivity";
			CheckVersionTask cv = new CheckVersionTask();
			new Thread(cv).start();
		}
		setUserCredit();
		YLPushInitialization.InitPush(getApplicationContext());
		initPushNotificationStyle();
		saveNewPushRecordCount(App.sNewPushRecordCount);
		VerificationActivity.bEditPhoneStatus = false;
		Loger.i("NEW", "onCreate");
        /********************new user seting address*******************/
        try {
			Intent intent = getIntent();
			String startmethod = intent.getStringExtra("startmethod");
			String inviteType = intent.getStringExtra("invite_type");
			String friendId = intent.getStringExtra("friend_id");
			if(startmethod.equals("registersuccess")){
				Loger.i("TEST", "45678901111111---->"+inviteType+" "+friendId);
				if(inviteType.equals("0") || inviteType.equals("2")){
					Intent setintent = new Intent(MainActivity.this,SelectaddrActivity.class);
					startActivityForResult(setintent, GET_CURRENT_CITY);
				}else if(inviteType.equals("1")){ 
					getAdress(friendId);
				}
			}else if(startmethod.equals("login")){
				setcurrentaddressTextView(loadVillagePrefrence());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        /********************get current address***********************/
        MainActivity.currentcity=loadCityPrefrence();
        MainActivity.currentvillage = loadVillagePrefrence();
        String familyid = loadFamilyIdPrefrence();
        if(familyid==null || familyid.isEmpty() || familyid.length()<=0 || familyid.equals("null")||familyid.equals("0")){
        	familyid = "0";
		}else{
			App.sUserLoginId=Long.parseLong(loadUserIdPrefrence());
			App.sFamilyBlockId = Long.parseLong(loadFamilyBlockIdPrefrence());
	        App.sFamilyId = Long.parseLong(loadFamilyIdPrefrence());
			App.sFamilyCommunityId = Long.parseLong(loadFamilyCommunityIdPrefrence());
		}
        initView();// 初始化控件
        initTitle();
		initViewPager();
        InitTopicList();
        init_Timer=new Timer();
        init_Timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(FriendCircleFragment.allList.size()==0){
					InitTopicList_Timer();
				}else{
					init_Timer.cancel();
				}
			}
		}, 10000,10000);
 /*
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
//				while(true){
//					if(MainActivity.sMainInitData>0){
//						if(initThread!=null){
//							initThread.interrupt();
//							initThread = null;
//						}
//						break;
//					}
//				}
				return null;
			}
			protected void onPostExecute(Void result) {	
				initTitle();
				initViewPager();
				getneighborhttp();
		        initPB.setVisibility(View.GONE);
		        pager.setVisibility(View.VISIBLE);
		        notificationStatusWithSplash = getIntent().getIntExtra("Notifications", 0);
		        notificationStringWithSplash = getIntent().getStringExtra("NotificationString");
		        Loger.i("TEST", "notificationStatusWithSplash==>"+notificationStatusWithSplash);
		        Loger.i("TEST", "notificationStringWithSplash==>"+notificationStringWithSplash);
		        if (notificationStatusWithSplash!=0){
		        	intoNotificationDetail(notificationStatusWithSplash,notificationStringWithSplash);
		        }
		        notificationStatusWithMain = getIntent().getIntExtra("Notification", 0);
		        try {
					notificationStringWithMain = getIntent().getStringExtra("Extras");
				} catch (Exception e) {
					notificationStringWithMain = null;
					e.printStackTrace();
				}
		        Loger.i("TEST", "notificationStatusWithMain==>"+notificationStatusWithMain);
		        Loger.i("TEST", "notificationStringWithMain==>"+notificationStringWithMain);
		        if (notificationStatusWithMain!=0){
		        	intoNotificationDetail(notificationStatusWithMain,notificationStringWithMain);
		        }
			};
		}.execute();
*/
        
//        MainActivity.currentaddrdetail = loadAddrdetailPrefrence();
//        MainActivity.familyId =  loadFamilyIdPrefrence();
		/********************init Listener************************/
		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(new MyContactListener());
		// 注册一个监听连接状态的listener
		connectionListener = new MyConnectionListener();
		EMChatManager.getInstance().addConnectionListener(connectionListener);
		MobclickAgent.onProfileSignIn(String.valueOf(App.sUserLoginId));
		if(App.sFamilyCommunityId != 0){
			//getUserBaoxiang();
		}
	}
	/*****************************************/
	
	private void getneighborhttp(){
		AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(this);
		AllFamily familyObjs = allFDaoDBImpl.getCurrentAddrDetail(MainActivity.currentcity+MainActivity.currentvillage+loadAddrdetailPrefrence());
		/****************** database ***********************/
		Loger.d("test5","sFamilyId="+App.sFamilyId+"familyObjs apt="+((AllFamily) familyObjs).getFamily_apt_num());
		aNeighborDaoDBImpl = new NeighborDaoDBImpl(this);
		if (((AllFamily) familyObjs).getEntity_type()==1 && familyObjs != null) {
			neighbor = new Neighbor(this);
			Loger.d("test5",((AllFamily) familyObjs).getFamily_apt_num()+"--"+((AllFamily) familyObjs).getFamily_block_id()+"--"+((AllFamily) familyObjs).getFamily_community_id());
			gethttpdata(((AllFamily) familyObjs).getFamily_apt_num(),((AllFamily) familyObjs).getFamily_block_id(),
					((AllFamily) familyObjs).getFamily_community_id(),
						IHttpRequestUtils.YOULIN,
						REQUEST_NEIGHBOR);
		}else{
			//bRequestSet = true;
			aNeighborDaoDBImpl.deleteObject(App.sFamilyId);
		}
		
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
					Long currenttime = System.currentTimeMillis();
					while (!bRequestSet) {
						if ((System.currentTimeMillis() - currenttime) > (App.WAITFORHTTPTIME+5000)) {
							bRequestSet = true;
						}
					}
					Loger.d("test5","--"+flag);
					if (bRequestSet && flag != null && flag.equals("ok")){
						bRequestSet = false;
						flag = "";
						Loger.d("test5","Belong_family_id="+App.sFamilyId);
						Loger.i("test5","jsonObjList->"+jsonObjList.size());
						if(jsonObjList.size()>0){
							aNeighborDaoDBImpl.deleteObject(App.sFamilyId);
						}
						for(int i=0 ;i<jsonObjList.size();i++){
							
							try {
								if(Long.parseLong(jsonObjList.get(i).getString("family_id"))>0){
									neighbor.setAptnum(jsonObjList.get(i).getString("aptnum"));
									neighbor.setUser_id(Long.parseLong(jsonObjList.get(i).getString("user_id")));
									neighbor.setLogin_account(App.sUserLoginId);
									neighbor.setUser_family_id(Long.parseLong(jsonObjList.get(i).getString("family_id")));
									neighbor.setBelong_family_id(App.sFamilyId);
									neighbor.setUser_name(jsonObjList.get(i).getString("user_nick"));
									Loger.d("test4", "user_id="+Long.parseLong(jsonObjList.get(i).getString("user_id"))+"--"+jsonObjList.get(i).getString("user_nick")
											+"usertype="+jsonObjList.get(i).getString("user_type"));
									neighbor.setUser_portrait(jsonObjList.get(i).getString("user_portrait"));
									neighbor.setBuilding_num(jsonObjList.get(i).getString("building_num"));
									neighbor.setProfession(jsonObjList.get(i).getString("user_profession"));
									neighbor.setBriefdesc(jsonObjList.get(i).getString("user_signature"));
									neighbor.setAddrstatus(jsonObjList.get(i).getString("user_public_status"));
									neighbor.setUser_type(Integer.parseInt(jsonObjList.get(i).getString("user_type")));
								}else{
									
									continue;
									
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Loger.i("NEW", "6789->"+e.getMessage());
							}
							Loger.i("NEW", "122222222222222222222222->"+neighbor.getUser_name());
							aNeighborDaoDBImpl.saveObject(neighbor);
							
						}

						Intent intent = new Intent();
						intent.setAction("com.nfs.youlin.find.FindFragment");
						intent.putExtra("family_id",App.sFamilyId);
						intent.putExtra("init",10086);
						MainActivity.this.sendOrderedBroadcast(intent, null);
						
					}
					return null;
			};
		}.execute();
	}
			private void gethttpdata(String apt_num,Long param2,Long param3,String http_addr,int request_index){
				/***********************************************/
				jsonObjList = new ArrayList<JSONObject>();
				RequestParams params = new RequestParams();
				AsyncHttpClient client = new AsyncHttpClient();
				Loger.d("test5","apt_num="+apt_num+"block_id="+param2+"community_id="+param3 );
				if(request_index == REQUEST_NEIGHBOR){
					params.put("user_id", App.sUserLoginId);
					params.put("apt_num", apt_num);
					if(param2 != null){
						if(param2!=0){
							params.put("block_id", param2);
						}
					}
					params.put("community_id", param3);
				}
				params.put("tag", "neighbors");
				params.put("apitype", IHttpRequestUtils.APITYPE[0]);
				client.post(IHttpRequestUtils.URL+http_addr,
						params, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode,
							org.apache.http.Header[] headers,
							org.json.JSONObject response) {
						// TODO Auto-generated method stub
						org.json.JSONObject jsonContext = response;
						String flag = null;
//						String community_id = null;
//						String community_name = null;
						try {
							flag = jsonContext.getString("flag");
							Loger.i("test5", "set neighbor flag->" + flag);
							
							if(flag.equals("none_f_o1")){
								bRequestSet = true;
							}else{
								bRequestSet = false;
							}
						} catch (org.json.JSONException e) {
							e.printStackTrace();
							bRequestSet = false;
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
//						Loger.d("test3", jsonContext.toString());
						try {
							org.json.JSONObject obj = null;
							if(jsonContext.length()>0){
								for (int i = 0; i < jsonContext.length(); i++) {
									jsonObjList.add(jsonContext.getJSONObject(i));
									obj = jsonContext.getJSONObject(i);
								}
								
								bRequestSet = true;
								flag = "ok";
								Loger.i("test5", "set neighbor flag->" + flag);
							}
							
						} catch (org.json.JSONException e) {
							// TODO Auto-generated catch block
							Loger.i("TEST","OK(error)->" + e.getMessage());
							bRequestSet = false;
							e.printStackTrace();
							}
							super.onSuccess(statusCode, headers, response);
						}
						@Override
						public void onFailure(int statusCode,
								org.apache.http.Header[] headers,
								String responseString,
								Throwable throwable) {
							// TODO Auto-generated method stub
							bRequestSet = false;
							new ErrorServer(MainActivity.this, responseString);
							super.onFailure(statusCode, headers,
									responseString, throwable);
						}
					});

			}
	/******************************************/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int height = wm.getDefaultDisplay().getHeight();
		if (inflater==null){
			inflater = getMenuInflater();
			inflater.inflate(R.menu.main_action_bar, menu);
		}
		if (SearchGroup==null){
			SearchGroup = (RelativeLayout) menu.findItem(
	        		R.id.mainactionbar).getActionView();
			SearchGroup.setLayoutParams(new RelativeLayout.LayoutParams(width-1,height/20));
			userAddressTextView =  (TextView)SearchGroup.findViewById(R.id.main_bar_address);
	        addImageView = (ImageButton)SearchGroup.findViewById(R.id.main_bar_search);
	        searchImageView = (ImageButton) SearchGroup.findViewById(R.id.main_bar_add);
	       // friendImageView = (ImageButton) SearchGroup.findViewById(R.id.main_bar_friend);
	        newMessageTextView = (ImageView) SearchGroup.findViewById(R.id.main_bar_new);
	        newMessageLayout = (ImageButton) SearchGroup.findViewById(R.id.new_msg);
	        Titlebaritem = new MainActionBarclicklisener(this);
	        userAddressTextView.setOnClickListener(Titlebaritem);
	        addImageView.setOnClickListener(Titlebaritem);
	        searchImageView.setOnClickListener(Titlebaritem);
	        newMessageLayout.setOnClickListener(Titlebaritem);
	       // friendImageView.setOnClickListener(Titlebaritem);
		}
        setnewMessageTextView(App.sNewPushRecordCount);
		return super.onCreateOptionsMenu(menu);
	}
	public static void setnewMessageTextView(String str) {
		final String count = str;
		Loger.d("hyytest", "message count=" + count);
		if(MainActivity.sMainActivity!=null){
		sMainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if(count.equals("0")){
						newMessageTextView.setVisibility(View.GONE);
					}else{
						newMessageTextView.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		}
		
	}

	public static void setcurrentaddressTextView(String str) {
		final String currentaddress = str;
		Loger.d("hyytest", "message count=" + currentaddress);
//		MainActivity.sMainActivity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
				// TODO Auto-generated method stub
		String subvillage;
		if(currentaddress.length()>8){
			subvillage = currentaddress.substring(0, 8)+"...";
		}else{
			subvillage =  currentaddress;
		}
		userAddressTextView.setText(subvillage);
//			}
//		});
	}
	/**
	 * 初始化视图
	 */
	private void initView() {
		pager = (ViewPager) findViewById(R.id.pager);// 初始化控件
		fragments = new ArrayList<Fragment>();// 初始化数据

		fragments.add(new FriendCircleFragment());
		fragments.add(new SquareFragment());
		fragments.add(new FindFragment());
		fragments.add(new MyInformationFragment());
	}

	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		mAdapter = new MyViewPagerAdapter(getSupportFragmentManager(),fragments);
		pager.setAdapter(mAdapter);
		pager.setOnPageChangeListener(this);
		pager.setCurrentItem(0);// 设置成当前第一个
	}

	/**
	 * 初始化几个用来显示title的RadioButton
	 */
	private void initTitle() {
		neighborBt=(RadioButton) findViewById(R.id.title1);
		squareBt=(RadioButton) findViewById(R.id.title2);
		findBt=(RadioButton) findViewById(R.id.title3);
		myBt=(RadioButton) findViewById(R.id.title4);
		title.add(neighborBt);// 四个title标签
		title.add(squareBt);
		title.add(findBt);
		title.add(myBt);
		title.get(0).setOnClickListener(new MyOnClickListener(0));// 设置响应	
		title.get(1).setOnClickListener(new MyOnClickListener(1));
		title.get(2).setOnClickListener(new MyOnClickListener(2));
		title.get(3).setOnClickListener(new MyOnClickListener(3));
	}

	/**
	 * 重写OnClickListener的响应函数，主要目的就是实现点击title时，pager会跟着响应切换
	 * 
	 * @author llb
	 * 
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index;

		public MyOnClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			pager.setCurrentItem(index);// 把viewpager的视图切过去，实现捏造title跟pager的联动
			title.get(index).setChecked(true);// 设置被选中，否则布局里面的背景不会切换\
			switch (index) {
			case 0:
				neighborBt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableNeighborNormal, null, null);
				break;
			case 2:
				findBt.setCompoundDrawablesRelativeWithIntrinsicBounds(null, drawableFindNormal, null, null);
				break;

			default:
				break;
			}
			
		}
	}

	/**
	 * 下面三个是OnPageChangeListener的接口函数
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		Loger.i("slide", "onPageSelected+agr0=" + arg0);
		title.get(arg0).setChecked(true);// 保持页面跟按钮的联动
//		NetWorkMonitor.sNetWorkInfoStyle = arg0;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		App.UPDATE_APK_PORSITION = "com.nfs.youlin.activity.MainActivity";
		AllFamilyDaoDBImpl allFamilyDaoDBImpl=new AllFamilyDaoDBImpl(MainActivity.this);
		App.setNoAddrStatus(MainActivity.this, allFamilyDaoDBImpl.isCurrentAddrExist());
		InitAppData();
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
		sdkHelper.pushActivity(MainActivity.this);
		EMChatManager.getInstance().registerEventListener(
				MainActivity.this,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });
		JPushInterface.onResume(MainActivity.this);
		MobclickAgent.onResume(this);
		JPushInterface.setDebugMode(false);
		YLPushInitialization.InitPush(MainActivity.this);
		if(YLPushInitialization.isPushStop(MainActivity.this)){
			YLPushInitialization.InitPush(MainActivity.this);
			Loger.i("TEST", "重新初始化App的push");
		}
		if(YLPushInitialization.isPushStop(MainActivity.this)){
			Loger.i("TEST", "重启App的push");
			YLPushInitialization.resumePush(MainActivity.this);
		}
		YLPushInitialization pushInitialization = new YLPushInitialization(MainActivity.this);
		pushInitialization.InitAlias(String.valueOf(App.sUserLoginId));
//		final String count = Integer.toString(getSysUnreadMsgCountTotal());
		try {
			if(App.sNewPushRecordCount == null || App.sNewPushRecordCount.isEmpty() || App.sNewPushRecordCount == "null"){
				App.sNewPushRecordCount = "0";
			}
			setnewMessageTextView(App.sNewPushRecordCount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//NetWorkMonitor.sNetWorkReceive = false;
		JPushInterface.onPause(MainActivity.this);
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
		EMChatManager.getInstance().unregisterEventListener(this);
		sdkHelper.popActivity(this);
	}
	/**
	 * 获取未读系统消息数
	 * 
	 * @return
	 */

	static void asyncFetchBlackListFromServer() {
		HXSDKHelper.getInstance().asyncFetchBlackListFromServer(
				new EMValueCallBack<List<String>>() {

					@Override
					public void onSuccess(List<String> value) {
						EMContactManager.getInstance().saveBlackList(value);
						HXSDKHelper.getInstance().notifyBlackListSyncListener(
								true);
					}

					@Override
					public void onError(int error, String errorMsg) {
						HXSDKHelper.getInstance().notifyBlackListSyncListener(
								false);
					}

				});
	}

	static void asyncFetchContactsFromServer() {
		HXSDKHelper.getInstance().asyncFetchContactsFromServer(
				new EMValueCallBack<List<String>>() {

					@Override
					public void onSuccess(List<String> usernames) {
						Context context = HXSDKHelper.getInstance()
								.getAppContext();

						System.out.println("----------------"
								+ usernames.toString());
						EMLog.d("roster", "contacts size: " + usernames.size());
						Map<String, User> userlist = new HashMap<String, User>();
						for (String username : usernames) {
							User user = new User();
							user.setUsername(username);
							// setUserHearder(username, user);
							userlist.put(username, user);
						}
						// 添加user"申请与通知"
						User newFriends = new User();
						newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
						String strChat = context
								.getString(R.string.Application_and_notify);
						newFriends.setNick(strChat);

						userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
						// 添加"群聊"
						User groupUser = new User();
						String strGroup = context
								.getString(R.string.group_chat);
						groupUser.setUsername(Constant.GROUP_USERNAME);
						groupUser.setNick(strGroup);
						groupUser.setHeader("");
						userlist.put(Constant.GROUP_USERNAME, groupUser);

						// 添加"聊天室"
						User chatRoomItem = new User();
						String strChatRoom = context
								.getString(R.string.chat_room);
						chatRoomItem.setUsername(Constant.CHAT_ROOM);
						chatRoomItem.setNick(strChatRoom);
						chatRoomItem.setHeader("");
						userlist.put(Constant.CHAT_ROOM, chatRoomItem);

						// 添加"Robot"
						User robotUser = new User();
						String strRobot = context
								.getString(R.string.robot_chat);
						robotUser.setUsername(Constant.CHAT_ROBOT);
						robotUser.setNick(strRobot);
						robotUser.setHeader("");
						userlist.put(Constant.CHAT_ROBOT, robotUser);

						// 存入内存
						DemoApplication.getInstance().setContactList(userlist);
						// 存入db
						UserDao dao = new UserDao(context);
						List<User> users = new ArrayList<User>(userlist
								.values());
						dao.saveContactList(users);

						HXSDKHelper.getInstance().notifyContactsSyncListener(
								true);

						if (HXSDKHelper.getInstance()
								.isGroupsSyncedWithServer()) {
							HXSDKHelper.getInstance().notifyForRecevingEvents();
						}

					}

					@Override
					public void onError(int error, String errorMsg) {
						HXSDKHelper.getInstance().notifyContactsSyncListener(
								false);
					}

				});
	}
	/**
	 * 监听事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewMessage: // 普通消息
		{
			EMMessage message = (EMMessage) event.getData();
			// 提示新消息
			HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
			//final String count = Integer.toString(getSysUnreadMsgCountTotal());
			//Loger.d("hyytest", "message count=" + count);
			setnewMessageTextView(App.sNewPushRecordCount);
			Loger.d("hyytest", "mainactivity EventNewMessage");
			Intent intent = new Intent();
			intent.setAction("com.nfs.youlin.find.newmsg");
			sendBroadcast(intent);
			break;
		}

		case EventOfflineMessage: {
			Loger.d("hyytest", "mainactivity EventOfflineMessage");
			break;
		}

		case EventConversationListChanged: {
			Loger.d("hyytest", "mainactivity EventConversationListChanged");
			break;
		}

		default:
			break;
		}
	}

	/***
	 * 好友变化listener
	 * 
	 */
	public static class MyContactListener implements EMContactListener {
		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			Loger.d("hyytest", "保存添加的联系人");

		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除
			Loger.d("hyytest", "联系人删除");

		}

		@Override
		public void onContactInvited(String username, String reason) {

			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
			Loger.d("hyytest", username + "接到邀请的消息");

		}

		@Override
		public void onContactAgreed(String username) {

			Loger.d("hyytest", username + "同意了你的好友请求");
		}

		@Override
		public void onContactRefused(String username) {

			// 参考同意，被邀请实现此功能,demo未实现
			Loger.d("hyytest", username + "拒绝了你的好友请求");
		}

	}

	/**
	 * 连接监听listener
	 * 
	 */
	public static class MyConnectionListener implements EMConnectionListener {
		@Override
		public void onConnected() {
			boolean groupSynced = HXSDKHelper.getInstance()
					.isGroupsSyncedWithServer();
			boolean contactSynced = HXSDKHelper.getInstance()
					.isContactsSyncedWithServer();
			// in case group and contact were already synced, we supposed to
			// notify sdk we are ready to receive the events
			if (groupSynced && contactSynced) {
				new Thread() {
					@Override
					public void run() {
						HXSDKHelper.getInstance().notifyForRecevingEvents();
					}
				}.start();
			} else {
				// if(!groupSynced){
				// asyncFetchGroupsFromServer();
				// }

				if (!contactSynced) {
					asyncFetchContactsFromServer();
				}

				if (!HXSDKHelper.getInstance().isBlackListSyncedWithServer()) {
					asyncFetchBlackListFromServer();
				}
			}

		}
		
		@Override
		public void onDisconnected(final int error) {
//			final String st1 = getResources().getString(
//					R.string.can_not_connect_chat_server_connection);
//			final String st2 = getResources().getString(
//					R.string.the_current_network);	
			if(error == EMError.CONNECTION_CONFLICT){
				Loger.i("TEST", "在其他账户上登录了,释放资源");
				if(MainActivity.sbooleanDisconnected){
					Loger.i("TEST", "防止了重复释放");
					return;
				}
				MainActivity.sbooleanDisconnected = true;
				Intent intent = new Intent();
				intent.setAction("com.nfs.youlin.activity.TransparentActivity");
				MainActivity.sMainActivity.startActivity(intent);
//				if(!isForeground(getApplication(),"com.nfs.youlin.activity.LoginActivity")){
//					builder.create().show();
//				}
			}else{
				if (NetUtils.hasNetwork(MainActivity.sMainActivity)){
					Loger.i("TEST", "连接不到聊天服务器");
				}else{
					Loger.i("TEST", "当前网络不可用，请检查网络设置");
					NetworkService.networkBool = false;
				}
			}
		}
	}
	
	private boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className)) {
			return false;
		}
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			String tmpString = cpn.getClassName();
			Loger.i(TAG, "MainActivity当前界面名称=>"+tmpString);
			if (className.equals(tmpString)) {
				return true;
			}
		}
		return false;
	}
//	public void UserLogin() {
//		String userName = "hyy00";
//		String passWord = "hyy00";
//
//		EMChatManager.getInstance().login(userName, passWord, new EMCallBack() {
//			@Override
//			public void onSuccess() {
//				// TODO Auto-generated method stub
//				EMChatManager.getInstance().loadAllConversations();
//				Loger.d("hyytest", "UserLogin success");
//			}
//
//			@Override
//			public void onProgress(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				Loger.d("hyytest", "UserLogin onProgress");
//			}
//
//			@Override
//			public void onError(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				Loger.d("hyytest", "UserLogin onError");
//			}
//		});
//	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == GET_CURRENT_CITY) {
//				double latitude = data.getDoubleExtra("latitude", 0);
//				Loger.d("hyytest", "latitude = "+latitude);
//				double longitude = data.getDoubleExtra("longitude", 0);
//				Loger.d("hyytest", "longitude = "+longitude);
//				String locationAddress = data.getStringExtra("address");
//				Loger.d("hyytest", "locationAddress = "+locationAddress);
				String city = "";
				try {
					city = data.getStringExtra("city");
					Loger.d("hyytest", "city = "+city);
//					String citycode = data.getStringExtra("city_id");
//					Loger.d("test2", "city = "+citycode);
					MainActivity.currentcity = city;
//					MainActivity.citycode = citycode;
					MainActivity.setcurrentaddressTextView(city);
					saveSharePrefrence(MainActivity.currentcity, MainActivity.currentvillage, "","","");
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(MainActivity.this,
						selectvillageActivity.class);
				intent.putExtra("selectedcity",MainActivity.currentcity );
				startActivityForResult(intent, GET_CURRENT_VILLAGE);
			}
			if (requestCode == GET_CURRENT_VILLAGE) {
				String village = data.getStringExtra("village");
				String block = data.getStringExtra("block_name");
				String blockid = data.getStringExtra("block_id");
				String villageid = data.getStringExtra("village_id");
				write_address.village = village;
				write_address.village_id = villageid;
				write_address.block = block;
				write_address.block_id = blockid;
				MainActivity.currentvillage = village;
				MainActivity.setcurrentaddressTextView(village);
				saveSharePrefrence(this.currentcity, this.currentvillage, "","",villageid,blockid);
				Intent intent = new Intent(MainActivity.this,write_address.class);
				intent.putExtra("setaddrmethod", "write");
				startActivityForResult(intent, GET_CURRENT_DETAIL);
			}
			if(requestCode == GET_CURRENT_DETAIL){
				saveSharePrefrence(this.currentcity, this.currentvillage, 
						this.loadAddrdetailPrefrence(),this.loadFamilyIdPrefrence(),this.loadFamilyCommunityIdPrefrence());
			}
			if(requestCode == UPDATE_DETAIL){
				Loger.d("test3", "APP name ="+getApplicationInfo().name);
				String appName = getApplication().getResources().getText(R.string.app_name).toString();  
				Intent updateinte = new Intent(this,YLUpdateService.class);  
				updateinte.putExtra("appName",appName);  
				updateinte.putExtra("url",info.getUrl());  
				updateinte.putExtra("status",info.getForce());  
				startService(updateinte);
				if(info.getForce().equals("1")){
			        updatapd1.show();
				}
			}
		}	
	}
	public void saveSharePrefrence(String city, String village,String detail,String familyid,String fimalycommunityid){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("city", city);
		sharedata.putString("village", village);
		sharedata.putString("detail", detail);
		sharedata.putString("familyid", familyid);
		sharedata.putString("familycommunityid", fimalycommunityid);
		sharedata.commit();
	}
	public void saveSharePrefrence(String city, String village,String detail,String familyid,String fimalycommunityid,String block_id){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("city", city);
		sharedata.putString("village", village);
		sharedata.putString("detail", detail);
		sharedata.putString("familyid", familyid);
		sharedata.putString("familycommunityid", fimalycommunityid);
		sharedata.putString("blockid", block_id);
		sharedata.commit();
	}
	
	private void initPushNotificationStyle(){
		SharedPreferences preferences1=getSharedPreferences(App.VOICE,Context.MODE_PRIVATE);
		Boolean voiceBool=preferences1.getBoolean("voiceBool", true);
		SharedPreferences preferences2=getSharedPreferences(App.VIBRATION,Context.MODE_PRIVATE);
		Boolean vibraBool=preferences2.getBoolean("vibraBool", true);
		YLPushNotificationBuilder notificationBuilder = new YLPushNotificationBuilder(getApplicationContext());
		notificationBuilder.setNotificationBuilderStyle(voiceBool, vibraBool);
	}
	
	private String loadCityPrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("city", "未设置");
	}
	private String loadVillagePrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("village", "未设置");
	}
	public String loadAddrdetailPrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("detail", "未设置");
	}
	public String loadFamilyIdPrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("familyid", "0");
	}
	public String loadFamilyCommunityIdPrefrence(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("familycommunityid", "0");
	}
	public String loadFamilyBlockIdPrefrence(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("blockid", "0");
	}
	public String loadUsernamePrefrence(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("username", "未设置");
	}
	public String loadUserIdPrefrence(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("account", "未设置");
	}
	private void saveNewPushRecordCount(String count){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		if(count==null){
			count = "0";
		}
		sharedata.putString("recordCount", count);
		sharedata.commit();
	}
	
	private String getVersion() {
//		String st = getResources().getString(R.string.Version_number_is_wrong);
		String st = "1.0";
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}

	
	private void InitAppData(){
		AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(getApplicationContext());
		Account account;
		try {
			account = daoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
			App.sUserType = account.getUser_type();
			App.sUserPhone = account.getUser_phone_number();
			App.sFamilyId = Long.parseLong(loadFamilyIdPrefrence());
		} catch (Exception e) {
			e.printStackTrace();
		}
		AllFamilyDaoDBImpl dbImpl = new AllFamilyDaoDBImpl(getApplicationContext());
		App.setAddrStatus(getApplicationContext(), dbImpl.getCurrentEntyType());
		dbImpl.releaseDatabaseRes();
		daoDBImpl.releaseDatabaseRes();
		dbImpl = null;
		account = null;
		daoDBImpl = null;
	}
	
	private void InitTopicList(){
		FriendCircleFragment.allList=new ArrayList<Object>();
		FriendCircleFragment.maddapter = new FriendCircleAdapter(FriendCircleFragment.allList,MainActivity.this,0,1);
		listBundle=new ArrayList<Bundle>();
		listBundle2=new ArrayList<Bundle>();
		RequestParams params = new RequestParams();
		Loger.i("TEST", "POST当前小区ID------->"+App.sFamilyCommunityId+" "+App.sUserLoginId);	
		params.put("community_id", App.sFamilyCommunityId);
		params.put("user_id", App.sUserLoginId);
		if(FriendCircleFragment.categorytype[FriendCircleFragment.categoryindex] != 0){
			params.put("category_type",FriendCircleFragment.categorytype[FriendCircleFragment.categoryindex]);
		}
		params.put("tag",FriendCircleFragment.tagstr[FriendCircleFragment.categoryindex]);
		params.put("apitype",FriendCircleFragment.apistr[FriendCircleFragment.categoryindex]);
		AsyncHttpClient httpClient = new AsyncHttpClient(this);
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			public void onSuccess(int statusCode, Header[] headers,
					final JSONArray response) {
//				initThread = new Thread(new Runnable() {
//					@Override
//					public void run() {
						Loger.i("youlin", "88888888---->"+response.toString());
						init_Timer.cancel();
						FriendCircleFragment.allList.add(new ForumTopic(MainActivity.this));
						FriendCircleFragment.allList.clear();
						listBundle.clear();
						ForumtopicDaoDBImpl daoDBImpl = new ForumtopicDaoDBImpl(getApplicationContext());
						daoDBImpl.deleteAllObjects();
						Loger.i("TEST", "删除完成");
						getTopicDetailsInfos(response);
						addResponseObject(listBundle);
						List<Object> forumtopicLists = daoDBImpl.findAllObject(listBundle.size());
						for(Object obj : forumtopicLists){
							FriendCircleFragment.allList.add(obj);
						}
						FriendCircleFragment.allList.add(forumtopicLists.get(forumtopicLists.size()-1));  //hyy  空白站位  应为  adapter中position=position-1
						try {
							FriendCircleFragment.id=((ForumTopic)FriendCircleFragment.allList.get(0)).getTopic_id();
						} catch (Exception e) {
							e.printStackTrace();
						}
						daoDBImpl.releaseDatabaseRes();
						runOnUiThread( new Runnable() {
							public void run() {
								ylProgressDialog.dismiss();
								FriendCircleFragment.maddapter.notifyDataSetChanged();
								getneighborhttp();
						        notificationStatusWithSplash = getIntent().getIntExtra("Notifications", 0);
						        notificationStringWithSplash = getIntent().getStringExtra("NotificationString");
						        Loger.i("TEST", "notificationStatusWithSplash==>"+notificationStatusWithSplash);
						        Loger.i("TEST", "notificationStringWithSplash==>"+notificationStringWithSplash);
						        if (notificationStatusWithSplash!=0 && notificationStatusWithSplash!=9527){
						        	intoNotificationDetail(notificationStatusWithSplash,notificationStringWithSplash);
						        }else if (notificationStatusWithSplash==9527){
						        	Toast.makeText(MainActivity.this, notificationStringWithSplash, Toast.LENGTH_LONG).show();
						        }
						        notificationStatusWithMain = getIntent().getIntExtra("Notification", 0);
						        try {
									notificationStringWithMain = getIntent().getStringExtra("Extras");
								} catch (Exception e) {
									notificationStringWithMain = null;
									e.printStackTrace();
								}
						        Loger.i("TEST", "notificationStatusWithMain==>"+notificationStatusWithMain);
						        Loger.i("TEST", "notificationStringWithMain==>"+notificationStringWithMain);
						        if (notificationStatusWithMain!=0 && notificationStatusWithMain!=9527){
						        	intoNotificationDetail(notificationStatusWithMain,notificationStringWithMain);
						        }else if (notificationStatusWithMain==9527){
						        	Toast.makeText(MainActivity.this, notificationStringWithMain, Toast.LENGTH_LONG).show();
						        }
							}
						});
						MainActivity.sMainInitData = 1;
//					}
//				});
				//initThread.start();
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				String flag = null;
				try {
					FriendCircleFragment.allList.add(new ForumTopic(MainActivity.this));
					FriendCircleFragment.allList.clear();
					listBundle.clear();
					ForumtopicDaoDBImpl daoDBImpl = new ForumtopicDaoDBImpl(getApplicationContext());
					daoDBImpl.deleteAllObjects();
					flag = response.getString("flag");
					if("no".equals(flag)){
						Loger.i("TEST", "topic is empty yes");
						if(FriendCircleFragment.neighborInitLayout!=null){
							FriendCircleFragment.neighborInitLayout.setVisibility(View.VISIBLE);
						}
						ylProgressDialog.dismiss();
						init_Timer.cancel();
						FriendCircleFragment.maddapter.notifyDataSetChanged();
						MainActivity.sMainInitData = 1;
					}else{
						Loger.i("TEST", "topic is empty no");
						MainActivity.sMainInitData = 1;
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
				new ErrorServer(MainActivity.this, responseString);
				MainActivity.sMainInitData = 1;
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	private void InitTopicList_Timer(){
		RequestParams params = new RequestParams();
		Loger.i("TEST", "POST当前小区ID------->"+App.sFamilyCommunityId+" "+App.sUserLoginId);	
		params.put("community_id", App.sFamilyCommunityId);
		params.put("user_id", App.sUserLoginId);
		if(FriendCircleFragment.categorytype[FriendCircleFragment.categoryindex] != 0){
			params.put("category_type",FriendCircleFragment.categorytype[FriendCircleFragment.categoryindex]);
		}
		params.put("tag",FriendCircleFragment.tagstr[FriendCircleFragment.categoryindex]);
		params.put("apitype",FriendCircleFragment.apistr[FriendCircleFragment.categoryindex]);
		SyncHttpClient httpClient = new SyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			public void onSuccess(int statusCode, Header[] headers,
					final JSONArray response) {
//				initThread = new Thread(new Runnable() {
//					@Override
//					public void run() {
						FriendCircleFragment.allList.add(new ForumTopic(MainActivity.this));
						FriendCircleFragment.allList.clear();
						listBundle.clear();
						ForumtopicDaoDBImpl daoDBImpl = new ForumtopicDaoDBImpl(getApplicationContext());
						daoDBImpl.deleteAllObjects();
						Loger.i("TEST", "删除完成");
						getTopicDetailsInfos(response);
						addResponseObject(listBundle);
						List<Object> forumtopicLists = daoDBImpl.findAllObject(listBundle.size());
						for(Object obj : forumtopicLists){
							FriendCircleFragment.allList.add(obj);
						}
						FriendCircleFragment.allList.add(forumtopicLists.get(forumtopicLists.size()-1));  //hyy  空白站位  应为  adapter中position=position-1
						try {
							FriendCircleFragment.id=((ForumTopic)FriendCircleFragment.allList.get(0)).getTopic_id();
						} catch (Exception e) {
							e.printStackTrace();
						}
						daoDBImpl.releaseDatabaseRes();
						runOnUiThread( new Runnable() {
							public void run() {
								try {
									ylProgressDialog.dismiss();
									FriendCircleFragment.maddapter.notifyDataSetChanged();
									getneighborhttp();
									notificationStatusWithSplash = getIntent().getIntExtra("Notifications", 0);
									notificationStringWithSplash = getIntent().getStringExtra("NotificationString");
									Loger.i("TEST", "notificationStatusWithSplash==>"+notificationStatusWithSplash);
									Loger.i("TEST", "notificationStringWithSplash==>"+notificationStringWithSplash);
									if (notificationStatusWithSplash!=0 && notificationStatusWithSplash!=9527){
							        	intoNotificationDetail(notificationStatusWithSplash,notificationStringWithSplash);
							        }else if (notificationStatusWithSplash==9527){
							        	Toast.makeText(MainActivity.this, notificationStringWithSplash, Toast.LENGTH_LONG).show();
							        }
									notificationStatusWithMain = getIntent().getIntExtra("Notification", 0);
									try {
										notificationStringWithMain = getIntent().getStringExtra("Extras");
									} catch (Exception e) {
										notificationStringWithMain = null;
										e.printStackTrace();
									}
									Loger.i("TEST", "notificationStatusWithMain==>"+notificationStatusWithMain);
									Loger.i("TEST", "notificationStringWithMain==>"+notificationStringWithMain);
									if (notificationStatusWithMain!=0){
										intoNotificationDetail(notificationStatusWithMain,notificationStringWithMain);
									}
								} catch (Exception e) {
									Loger.i("TEST", "异常退出");
									return;
								}
							}
						});
						MainActivity.sMainInitData = 1;
//					}
//				});
				//initThread.start();
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				String flag = null;
				try {
					FriendCircleFragment.allList.add(new ForumTopic(MainActivity.this));
					FriendCircleFragment.allList.clear();
					listBundle.clear();
					ForumtopicDaoDBImpl daoDBImpl = new ForumtopicDaoDBImpl(getApplicationContext());
					daoDBImpl.deleteAllObjects();
					flag = response.getString("flag");
					if("no".equals(flag)){
						Loger.i("TEST", "topic is empty yes");
						if(FriendCircleFragment.neighborInitLayout!=null){
							FriendCircleFragment.neighborInitLayout.setVisibility(View.VISIBLE);
						}
						ylProgressDialog.dismiss();
						init_Timer.cancel();
						FriendCircleFragment.maddapter.notifyDataSetChanged();
						MainActivity.sMainInitData = 1;
					}else{
						Loger.i("TEST", "topic is empty no");
						MainActivity.sMainInitData = 1;
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
				new ErrorServer(MainActivity.this, responseString);
				MainActivity.sMainInitData = 1;
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	private void addResponseObject(List<Bundle> bundles){
		if(bundles==null){
			return;
		}
		ForumtopicDaoDBImpl daoDBImpl = new ForumtopicDaoDBImpl(getApplicationContext());
		for(int i=0;i<bundles.size();i++){
			ForumTopic forumTopic = new ForumTopic(getApplicationContext());
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
				//String price = jsonObject.getString("price");
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
				e.printStackTrace();
			}
		}
	}

	private void getTopicDetailsInfosWithNotify(JSONArray response){
		NewPushRecordAbsActivity.forumtopicLists = new ArrayList<Object>();
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
				//String price = jsonObject.getString("price");
				ForumTopic forumTopic = new ForumTopic(this);
				forumTopic.setFlag(false);
				forumTopic.setCache_key(Integer.parseInt(cache_key));
				forumTopic.setTopic_id(Long.parseLong(topic_id));
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
				try {
					object_json = jsonObject.getJSONArray("objectData").toString();
				} catch (Exception e) {
//					Loger.i("TEST", "(E)getTopicComm->"+e.getMessage());
					e.printStackTrace();
				}
				forumTopic.setObject_data(object_json);
				NewPushRecordAbsActivity.forumtopicLists.add(forumTopic);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void intoNotificationDetail(int status, String extras){
		if((null==extras) || (status<=0)){
			return;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(extras);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			Loger.i("TEST", "intoNotificationDetail-Err"+e2.getMessage());
			jsonObj = null;
			return;
		}
		long recordId = 0;
		try {
			recordId = jsonObj.getLong("recordId");
		} catch (Exception e1) {
			Loger.i(TAG, "intoNotificationDetail-recordId=>"+e1.getMessage());
			return;
		}
		getAboutRecordArray();
		Intent intent = new Intent();
		PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(this);
		PushRecord pushRecord = daoDBImpl.findObjByRecordId(String.valueOf(recordId));
		switch(status){
		case 11://管理员申请
		case 12://成为管理员
		case 14://物业管理员
			if(null==pushRecord){
				Loger.i(TAG, "没有找到管理员申请、成为管理员、物业管理员");
				return;
			}
			pushRecord.setType(2);
			daoDBImpl.modifyObject(pushRecord);
			setRecordStatus();
			intent.putExtra("pushInfo", extras);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(MainActivity.this, PushRecordDetailActivity.class);
			startActivity(intent);
			break;
		case 15://分享了新闻
			if(null==pushRecord){
				Loger.i(TAG, "没有找到分享了新闻");
				return;
			}
			pushRecord.setType(2);
			daoDBImpl.modifyObject(pushRecord);
			setRecordStatus();
			try {
				JSONObject newspush = new JSONObject(extras);
				intent.putExtra("linkurl", newspush.getString("new_url"));
				intent.putExtra("title", newspush.getString("new_title"));
				intent.putExtra("newsid", newspush.getString("new_id"));
				intent.putExtra("picurl",newspush.getString("new_small_pic"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(MainActivity.this, NewsDetail.class);
				startActivity(intent);
			} catch (JSONException e) {
				Loger.i(TAG, "intoNotificationDetail-news-Err==>"+e.getMessage());
			}
			break;
		case 21://地址审核中
		case 22://地址审核通过
		case 23://地址审核失败
			if(null==pushRecord){
				Loger.i(TAG, "没有找到地址审核通过、地址审核通过");
				return;
			}
			pushRecord.setType(2);
			daoDBImpl.modifyObject(pushRecord);
			setRecordStatus();
			intent.putExtra("pushInfo", extras);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(MainActivity.this, PushRecordDetailActivity.class);
			startActivity(intent);
			break;
		case 31://有公告
			if(null==pushRecord){
				Loger.i(TAG, "没有找到有公告");
				return;
			}
			pushRecord.setType(2);
			daoDBImpl.modifyObject(pushRecord);
			setRecordStatus();
			// 跳转至详情
			jumpToDetailPage(extras);
			break;
		case 32://物业报修
			if(null==pushRecord){
				Loger.i(TAG, "没有找到物业报修");
				return;
			}
			pushRecord.setType(2);
			daoDBImpl.modifyObject(pushRecord);
			setRecordStatus();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(MainActivity.this, PropertyRepairList.class);
			startActivity(intent);
			break;
		case 33://推送给个人的报修状态
			if(null==pushRecord){
				Loger.i(TAG, "没有找到推送给个人的报修状态");
				return;
			}
			JSONObject newspush;
			try {
				newspush = new JSONObject(extras);
				pushRecord.setType(2);
				daoDBImpl.modifyObject(pushRecord);
				setRecordStatus();
				intent.putExtra("repairstatus", newspush.getString("repairStatus"));
				Loger.i(TAG,"repairstatus====>"+newspush.getString("repairStatus"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(MainActivity.this, PropertyRepairActivity.class);
				startActivity(intent);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			break;
		case 41://举报信息
			try {
				if(App.sUserLoginId<=0){
					AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(this);
					App.sUserLoginId = dbImpl.getUserId();
				}
				if(null==pushRecord){
					Loger.i(TAG, "没有找到举报信息");
					return;
				}
				JSONObject topicObj = new JSONObject(pushRecord.getContent());		
				daoDBImpl.modifyReportObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
				setRecordStatus();
				JSONObject json = new JSONObject(extras);
				intent.putExtra("report_tId", Long.parseLong(json.getString("topicId")));
				intent.putExtra("report_detail",String.valueOf(json.getString("topicDetail")));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(MainActivity.this, ReportDetailActivity.class);
				startActivity(intent);
			} catch (JSONException e) {
				Loger.i(TAG, "intoNotificationDetail-report-Err==>"+e.getMessage());
			}
			break;
		case 51://新闻
			if(App.sUserLoginId<=0){
				AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(this);
				App.sUserLoginId = dbImpl.getUserId();
			}
			int newRecordCount = 0;
			try {
				newRecordCount = Integer.parseInt(App.sNewPushRecordCount);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				Loger.i(TAG, "error+"+e.getMessage());
				newRecordCount = 0;
			}
			if(newRecordCount<=0){
				Loger.i(TAG, "没有找到新闻");
				return;
			}
			daoDBImpl.modifyNewsObjs(2,App.sUserLoginId);
			setRecordStatus();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(MainActivity.this, PushNewsDetailActivity.class);
			startActivity(intent);
			break;
		case 81://天气预报
			if(App.sUserLoginId<=0){
				AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(this);
				App.sUserLoginId = dbImpl.getUserId();
			}
			int weaRecordCount = 0;
			try {
				weaRecordCount = Integer.parseInt(App.sNewPushRecordCount);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				Loger.i(TAG, "error+"+e.getMessage());
				weaRecordCount = 0;
			}
			if(weaRecordCount<=0){
				Loger.i(TAG, "没有找到天气");
				return;
			}
			daoDBImpl.modifyObject(pushRecord);
			setRecordStatus();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			JSONObject json;
			try {
				json = new JSONObject(extras);
				intent.putExtra("weaorzoc_id", Long.parseLong(json.getString("weaId")));
				intent.putExtra("community_id",String.valueOf(json.getString("communityId")));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			intent.setClass(MainActivity.this, WeatherListActivity.class);
			startActivity(intent);
			break;
		case 61:
			try {
				if(App.sUserLoginId<=0){
					AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(this);
					App.sUserLoginId = dbImpl.getUserId();
				}
				if(null==pushRecord){
					Loger.i(TAG, "没有找到回复信息");
					return;
				}
				JSONObject topicObj = new JSONObject(pushRecord.getContent());		
				daoDBImpl.modifyCommentObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
				setRecordStatus();
			} catch (JSONException e) {
				Loger.i(TAG, "intoNotificationDetail-Comment-Err==>"+e.getMessage());
			}
			// 跳转至详情
			jumpToDetailPageWithComment(extras);
			break;
		case 62://以物易物
			Loger.i("TEST","以物易物.....>");
			try {
				if(App.sUserLoginId<=0){
					AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(this);
					App.sUserLoginId = dbImpl.getUserId();
				}
				if(null==pushRecord){
					Loger.i(TAG, "没有找到回复信息");
					return;
				}
				JSONObject topicObj = new JSONObject(pushRecord.getContent());		
				daoDBImpl.modifyCommentObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
				setRecordStatus();
			} catch (JSONException e) {
				Loger.i(TAG, "intoNotificationDetail-Comment-Err==>"+e.getMessage());
			}
			// 跳转至详情
			jumpToDetailPageWithCommentBarter(extras);
			break;
		case 63://打招呼
			Loger.i("TEST","打招呼.....>");
			try {
				if(App.sUserLoginId<=0){
					AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(this);
					App.sUserLoginId = dbImpl.getUserId();
				}
				if(null==pushRecord){
					Loger.i(TAG, "没有找到回复信息");
					return;
				}
				JSONObject topicObj = new JSONObject(pushRecord.getContent());		
				String sayHelloUserId = topicObj.getString("commentType");
				Loger.i("TEST", "当前打招呼UserId==>"+sayHelloUserId);
				daoDBImpl.modifySayHelloObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId),sayHelloUserId);
				setRecordStatus();
			} catch (JSONException e) {
				Loger.i(TAG, "intoNotificationDetail-Comment-Err==>"+e.getMessage());
			}
			// 跳转至详情
			jumpToDetailPageWithComment(extras);
			break;
		case 71://发布评价
			if(null==pushRecord){
				Loger.i(TAG, "没有找到评价");
				return;
			}
			pushRecord.setType(2);
			daoDBImpl.modifyObject(pushRecord);
			setRecordStatus();
			try {
				evaJson = new JSONObject(extras);
				RequestParams params=new RequestParams();
				params.put("tag", "getorcheckrecord");
				params.put("apitype", "address");
				params.put("uer_id", evaJson.getString("uerId"));
				AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
				asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler(){
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							String flag=response.getString("flag");
							Loger.i("LYM", "main==pushType--->"+flag);
							if(flag.equals("no")){
								Intent intent = new Intent(MainActivity.this,NewPushStoreCommentActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.putExtra("tag", evaJson.getString("tag"));
								intent.putExtra("uid", evaJson.getString("uid"));
								intent.putExtra("shop_name", evaJson.getString("shopName"));
								startActivity(intent);
							}else if(flag.equals("ok")){
								Intent intent = new Intent(MainActivity.this,StoreCircleDetailActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.putExtra("uid", evaJson.getString("uid"));
								startActivity(intent);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						super.onSuccess(statusCode, headers, response);
					};
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						new ErrorServer(MainActivity.this, responseString);
						super.onFailure(statusCode, headers, responseString, throwable);
					};
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Loger.i("LYM","12323--->"+e.getMessage());
			}
			break;
		default:
			break;
		}
	}
	
	private void jumpToDetailPage(final String json){
		RequestParams reference = new RequestParams();
		reference.put("community_id",App.sFamilyCommunityId);
		reference.put("user_id", App.sUserLoginId);
		reference.put("count", 1);
		reference.put("category_type",App.GONGGAO_TYPE);
		reference.put("type", 2);
		reference.put("tag","getnotice");
		reference.put("apitype",IHttpRequestUtils.APITYPE[4]);
		try {
			JSONObject object = new JSONObject(json);
			reference.put("topic_id", Long.parseLong(object.getString("topicId")));
		} catch (NumberFormatException e) {
			Loger.i(TAG, "jumpToDetailPage1=>"+e.getMessage());
			return;
		} catch (JSONException e) {
			Loger.i(TAG, "jumpToDetailPage2=>"+e.getMessage());
			return;
		}
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,reference,
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				getTopicDetailsInfos(response);
				bRequestType = true;
				if(NewPushRecordAbsActivity.forumtopicLists.size()>0){
					szFlag = "ok";
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					szFlag = response.getString("flag");
					if(szFlag.equals("no")){
						bRequestType = true;
						PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(MainActivity.this);
						long recordId = 0;
						try {
							JSONObject object = new JSONObject(json);
							recordId = object.getLong("recordId");
						} catch (Exception e1) {
							Loger.i(TAG, "jumpToDetailPage-recordId=>"+e1.getMessage());
						}
						daoDBImpl.deleteObject(recordId);
						Toast.makeText(MainActivity.this, "此公告已删除", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
					Throwable throwable) {
				new ErrorServer(MainActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Long currenttime = System.currentTimeMillis();
				while (!bRequestType) {
					if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
						bRequestType = true;
					}
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				if (bRequestType == true && szFlag.equals("ok")) {
					szFlag = "no";
					Intent intent = new Intent();
					intent.putExtra("parent", 4);
					intent.putExtra("position", 0);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(MainActivity.this, CircleDetailActivity.class);
					startActivity(intent);
				}
				bRequestType = false;
			};
		}.execute();
	}
	
	private void jumpToDetailPageWithComment(final String json){
		RequestParams reference = new RequestParams();
		reference.put("community_id", App.sFamilyCommunityId);
		reference.put("user_id", App.sUserLoginId);
		reference.put("count", 1);
		reference.put("type", App.sUserType);
		reference.put("tag", "gettopic");
		reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
		try {
			JSONObject object = new JSONObject(json);
			reference.put("topic_id", Long.parseLong(object.getString("topicId")));
		} catch (NumberFormatException e) {
			Loger.i(TAG, "jumpToDetailPageWithComment0=>" + e.getMessage());
		} catch (JSONException e) {
			Loger.i(TAG, "jumpToDetailPageWithComment1=>" + e.getMessage());
		}
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Loger.i("TEST", response.toString());
				getTopicDetailsInfosWithNotify(response);
				bRequestType = true;
				if (NewPushRecordAbsActivity.forumtopicLists.size() > 0) {
					szFlag = "ok";
				}
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					szFlag = response.getString("flag");
					if (szFlag.equals("no")) {
						bRequestType = true;
						// daoDBImpl.deleteObject(pushRecord.getRecord_id());
						Toast.makeText(MainActivity.this, "此内容已被删除", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(MainActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				Long currenttime = System.currentTimeMillis();
				while (!bRequestType) {
					if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
						bRequestType = true;
					}
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				if (bRequestType == true && szFlag.equals("ok")) {
					szFlag = "none";
					Intent intent = new Intent(MainActivity.this, CircleDetailActivity.class);
					intent.putExtra("parent", 4);
					intent.putExtra("position", 0);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				}
			};
		}.execute();
	}
	
	private void jumpToDetailPageWithCommentBarter(final String json){
		RequestParams reference = new RequestParams();
		reference.put("community_id", App.sFamilyCommunityId);
		reference.put("user_id", App.sUserLoginId);
		reference.put("count", 1);
		reference.put("type", App.sUserType);
		reference.put("tag", "gettopic");
		reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
		try {
			JSONObject object = new JSONObject(json);
			reference.put("topic_id", Long.parseLong(object.getString("topicId")));
		} catch (NumberFormatException e) {
			Loger.i(TAG, "jumpToDetailPageWithComment0=>" + e.getMessage());
		} catch (JSONException e) {
			Loger.i(TAG, "jumpToDetailPageWithComment1=>" + e.getMessage());
		}
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Loger.i("TEST", response.toString());
				getTopicDetailsInfosWithNotify(response);
				bRequestType = true;
				if (NewPushRecordAbsActivity.forumtopicLists.size() > 0) {
					szFlag = "ok";
				}
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					szFlag = response.getString("flag");
					if (szFlag.equals("no")) {
						bRequestType = true;
						// daoDBImpl.deleteObject(pushRecord.getRecord_id());
						Toast.makeText(MainActivity.this, "此内容已被删除", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(MainActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				Long currenttime = System.currentTimeMillis();
				while (!bRequestType) {
					if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
						bRequestType = true;
					}
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				if (bRequestType == true && szFlag.equals("ok")) {
					szFlag = "none";
					Intent intent = new Intent(MainActivity.this, BarterDedailCommentActivity.class);
					try {
						JSONObject object = new JSONObject(json);
						intent.putExtra("topic_id", Long.parseLong(object.getString("topicId")));
					} catch (NumberFormatException e) {
						Loger.i(TAG, "NumberFormatException==>"+e.getMessage());
					} catch (JSONException e) {
						Loger.i(TAG, "JSONException==>"+e.getMessage());
					}
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(MainActivity.this, BarterDedailCommentActivity.class);
					startActivity(intent);
				}
			};
		}.execute();
	}
	
	private String[] getAboutRecordArray(){
		String[] strInfo = new String[2];
		int userType = 0;
		if(App.sUserType<=0){
			AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(this);
			userType = dbImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId)).getUser_type();
			App.sUserType = userType;
		}
		strInfo[0] = String.valueOf(userType);
		long communityId = 0;
		if(App.sFamilyCommunityId<=0){
			AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(this);
			communityId = allFamilyDaoDBImpl.getCurrentAddrDetail(MainActivity.currentcity+MainActivity.currentvillage+loadAddrdetailPrefrence()).getFamily_community_id();
			App.sFamilyCommunityId = communityId;
		}
		strInfo[1] = String.valueOf(communityId);
		return strInfo;
	}
	/********************************************版本更新********************************/
	Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_NONEED:
//				Toast.makeText(getApplicationContext(), "已经是最新版本!",Toast.LENGTH_SHORT).show();
				Loger.i("TEST", "已经是最新版本，不需要更新");
				break;
			case UPDATA_CLIENT:
				 //对话框通知用户升级程序   
				break;
			case UPDATA_FORCE:
				break;
			case GET_UNDATAINFO_ERROR:
				//服务器超时   
//	            Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", 1).show(); 
	            Loger.i("TEST", "获取服务器更新信息失败");
				break;
			case SDCARD_NOMOUNTED:
				Toast.makeText(getApplicationContext(), "手机没有SD内置卡，无法下载", Toast.LENGTH_SHORT).show(); 
				break;
			case DOWN_ERROR:
				//下载apk失败  
	            Toast.makeText(getApplicationContext(), "下载新版本失败", Toast.LENGTH_SHORT).show(); 
				break;
			}
		}
	};
	public class CheckVersionTask implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				RequestParams params = new RequestParams();
				params.put("tag", "version");
				params.put("apitype", IHttpRequestUtils.APITYPE[6]);
				SyncHttpClient httpClient = new SyncHttpClient();
				httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params,
						new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						// TODO Auto-generated method stub
						Loger.i("LYM","222222222222222222--->"+response);
						String strCode = null;
						String strForce = null;
						try {
							strCode = response.getString("vcode");
							strCode = strCode.substring(1, strCode.length());
						} catch (JSONException e) {
							strCode = null;
							e.printStackTrace();
						}
						if (strCode==null){
							Message msg = new Message();
							msg.what = GET_UNDATAINFO_ERROR;
							updateHandler.sendMessage(msg);
						}else{
							if (strCode.equals(localVersion)){
								Message msg = new Message();
								msg.what = UPDATA_NONEED;
								updateHandler.sendMessage(msg);
							}else{
								try {
									strForce = response.getString("force");
//									down_apk_url = response.getString("url");
//									String detail = response.getJSONArray("detail").get(0).toString();
//									String apksize = response.getString("size");
									
									info = new UpdataInfo();
									info.setSize(response.getString("size"));
									info.setForce(response.getString("force"));
									info.setDescription(response.getJSONArray("detail").get(0).toString());
									info.setUrl(response.getString("url"));
									info.setVersion(strCode);
									info.setApk_detail(response.getJSONArray("detail").get(0).toString());
//									info.setUrl_server(srv_url);
									Intent intent = new Intent(MainActivity.this,Apk_update_detail.class);
									intent.putExtra("apksize", info.getSize());
									intent.putExtra("version", strCode);
									intent.putExtra("apkdetail", info.getApk_detail());
									intent.putExtra("apkforce", info.getForce());
									startActivityForResult(intent, UPDATE_DETAIL);
								} catch (JSONException e) {
									strForce = null;
								}
								if (strForce==null){
									Message msg = new Message();
									msg.what = GET_UNDATAINFO_ERROR;
									updateHandler.sendMessage(msg);
								}else{
									Message msg = new Message();
									if("ok".equals(strForce)){
										msg.what = UPDATA_FORCE;
									}else{
										msg.what = UPDATA_CLIENT;
									}
									updateHandler.sendMessage(msg);
								}
							}
						}
//						startActivity(new Intent(PersonalInfoGuanyuActivity.this,Apk_update_detail.class));
						super.onSuccess(statusCode, headers, response);
					}
					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString,
							Throwable throwable) {
						Message msg = new Message();
						msg.what = GET_UNDATAINFO_ERROR;
						updateHandler.sendMessage(msg);
						super.onFailure(statusCode, headers, responseString, throwable);
					}
				});
			} catch (Exception e) {
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				updateHandler.sendMessage(msg);
				e.printStackTrace();
			}
		}
	}
	/***********************************************************************************/
	//设置当前未阅读的数量和显示状态
	private void setRecordStatus(){
		int newRecordCount = Integer
				.parseInt(App.sNewPushRecordCount);
		if (newRecordCount > 0) {
			newRecordCount--;
		}
		App.sNewPushRecordCount = String
				.valueOf(newRecordCount);
		saveNewPushRecordCount(App.sNewPushRecordCount);
		setnewMessageTextView(App.sNewPushRecordCount);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Loger.i("TEST", "结束MainActivity");
		if(SystemSetActivity.sbooleanSystemToLogin){
			MainActivity.sMainInitData = 0;
			MainActivity.sMainActivity = null;
			SystemSetActivity.sbooleanSystemToLogin = false;
		}else if(TransparentActivity.sbooleanExitWithPasswd){
			MainActivity.sMainInitData = 0;
			MainActivity.sMainActivity = null;
			TransparentActivity.sbooleanExitWithPasswd = false;
		}else{
			Intent intent = new Intent(MainActivity.this,InitTransparentActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
			intent.putExtra("exit", "exit");
			startActivity(intent);
			MainActivity.sMainInitData = 0;
			MainActivity.sMainActivity = null;
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(System.currentTimeMillis()-mExitTime<2000){
				init_Timer.cancel();
				if(FriendCircleAdapter.isFristMap!=null){
					FriendCircleAdapter.isFristMap.clear();
				}
				try {
					LoginActivity.sLoginActivity.finish();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MainActivity.sMainActivity.finish();
			}else{
				mExitTime=System.currentTimeMillis();
				Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			}
		}
		return true;
//		if(keyCode==KeyEvent.KEYCODE_BACK){
//			moveTaskToBack(false);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
	}
	Handler getAdressHandler=new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what==1005){
				Long Belong_family_id = msg.getData().getLong("family_id");
				for (int i = 0; i < jsonObjList.size(); i++) {
					try {
						neighbor.setAptnum(jsonObjList.get(i).getString("aptnum"));
						neighbor.setUser_id(Long.parseLong(jsonObjList.get(i).getString("user_id")));
						neighbor.setLogin_account(App.sUserLoginId);
						neighbor.setUser_family_id(Long.parseLong(jsonObjList.get(i).getString("family_id")));
						neighbor.setBelong_family_id(Belong_family_id);
						neighbor.setUser_name(jsonObjList.get(i).getString("user_nick"));
						neighbor.setUser_portrait(jsonObjList.get(i).getString("user_portrait"));
						neighbor.setBuilding_num(jsonObjList.get(i).getString("building_num"));
						neighbor.setProfession(jsonObjList.get(i).getString("user_profession"));
						neighbor.setBriefdesc(jsonObjList.get(i).getString("user_signature"));
						neighbor.setAddrstatus(jsonObjList.get(i).getString("user_public_status"));
						neighbor.setUser_type(Integer.parseInt(jsonObjList.get(i).getString("user_type")));
						aNeighborDaoDBImpl.saveObject(neighbor);
						Loger.i("test9", "1111111111--->"+jsonObjList.get(i).getString("user_nick"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Intent intent = new Intent();
				intent.setAction("com.nfs.youlin.find.FindFragment");
				intent.putExtra("family_id", Belong_family_id);
				intent.putExtra("init",10086);
				MainActivity.this.sendOrderedBroadcast(intent, null);
			}
		};
	};
	
	private void setUserCredit(){
		if(App.sUserLoginId<=0){
			Loger.i(TAG, "当前用户没有userId，无法获取签到状态");
			return;
		}
		RequestParams params = new RequestParams();
		params.put("user_id", App.sUserLoginId);
		params.put("tag", "checkusersign");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler() {
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				String flag = null;
				long userCredit = 0L;
				try {
					flag = response.getString("flag");
					if("ok".equals(flag)){
						userCredit = response.getLong("credit");
					}else if("no".equals(flag)){
						userCredit = response.getLong("credit");
					}
					editUserCredit(userCredit,flag);
					Loger.i(TAG, "获取用户签到状态成功");
				} catch (JSONException e) {
					flag = null;
					e.printStackTrace();
				}
			};
		});
	}
	
	private void editUserCredit(long userCredit,String status){
		Editor sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER,Context.MODE_PRIVATE).edit();
		sharedata.putLong("credit", userCredit);
		sharedata.putString("signstatus", status);
		sharedata.commit();
	}
	
	private void getAdress(String friendId) {
		RequestParams reference = new RequestParams();
		reference.put("user_id", App.sUserLoginId);
		reference.put("fr_id", friendId);
		reference.put("tag", "getinvfamilyinfo");
		reference.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Loger.i("TEST", response.toString());
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String flag = response.getString("flag");
					if(flag.equals("ok")){
						JSONObject jsonObject=new JSONObject(response.getString("fr_info"));
						final AllFamily aFamily = new AllFamily(MainActivity.this);
						aFamily.setUser_alias(getSharedPreferences(App.REGISTERED_USER,
								Context.MODE_PRIVATE).getString("username", "未设置"));
						final String familyid =jsonObject.getString("family_id");
						if(familyid.equals("0")){
							aFamily.setNe_status(Integer.parseInt(jsonObject.getString("ne_status")));
						}else{
							aFamily.setNe_status(0);
						}
						aFamily.setPrimary_flag(1);
						int entityType = Integer.parseInt(jsonObject.getString("entity_type"));
						App.setAddrStatus(MainActivity.this, entityType);
						aFamily.setEntity_type(entityType);
						aFamily.setFamily_city(jsonObject.getString("family_city"));
						aFamily.setFamily_community(jsonObject.getString("family_community"));
						aFamily.setFamily_building_num(jsonObject.getString("family_building_num"));
						aFamily.setFamily_apt_num(jsonObject.getString("family_apt_num"));
						aFamily.setFamily_city_id(Long.parseLong(jsonObject.getString("family_city_id")));
						aFamily.setFamily_block_id(Long.parseLong(jsonObject.getString("family_block_id")));
						aFamily.setFamily_id(Long.parseLong(jsonObject.getString("family_id")));
						aFamily.setFamily_community_id(Long.parseLong(jsonObject.getString("family_community_id")));
						aFamily.setLogin_account(App.sUserLoginId);
						try {
							aFamily.setFamily_address_id(Long.parseLong(jsonObject.getString("fr_id")));
						} catch (NumberFormatException e) {
							Toast.makeText(getApplicationContext(), "Number=>"+e.getMessage(), Toast.LENGTH_SHORT).show();
						}
						aFamily.saveFamilyInfos();
						AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(MainActivity.this);
						allFamilyDaoDBImpl.saveObject(aFamily);
						allFamilyDaoDBImpl.releaseDatabaseRes();
						MainActivity.currentcity = jsonObject.getString("family_city");
						MainActivity.currentvillage = jsonObject.getString("family_community");
						
						Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
						sharedata.putString("city", jsonObject.getString("family_city"));
						sharedata.putString("village", jsonObject.getString("family_community"));
						sharedata.putString("detail", jsonObject.getString("family_building_num")+"-"+jsonObject.getString("family_apt_num"));
						sharedata.putString("familyid", jsonObject.getString("family_id"));
						sharedata.putString("blockid", jsonObject.getString("family_block_id"));
						sharedata.putString("familycommunityid", jsonObject.getString("family_community_id"));
						sharedata.commit();
						
						Intent intent2=new Intent("youlin.initFriend.action");
						sendBroadcast(intent2);
						AccountDaoDBImpl accountdao = new AccountDaoDBImpl(MainActivity.this);
						Account account = accountdao.findAccountByLoginID(String.valueOf(App.sUserLoginId));
						account.setUser_family_id(Long.parseLong(familyid));
						account.setUser_family_address(jsonObject.getString("family_city")+jsonObject.getString("family_community")+jsonObject.getString("family_building_num")+"-"+jsonObject.getString("family_apt_num"));
						accountdao.modifyObject(account);
						accountdao.releaseDatabaseRes();
						App.sFamilyId = Long.parseLong(familyid);
						App.sFamilyCommunityId = Long.parseLong(jsonObject.getString("family_community_id"));
						App.sFamilyBlockId = Long.parseLong(jsonObject.getString("family_block_id"));
						MainActivity.setcurrentaddressTextView(jsonObject.getString("family_community"));
						//设置PushTag
						YLPushTagManager pushTagManager = new YLPushTagManager(getApplicationContext());
						pushTagManager.setPushTag();
						App.sDeleteTagFinish = true;
						if (Long.parseLong(familyid) > 0) {
							neighbor = new Neighbor(MainActivity.this);
							aNeighborDaoDBImpl = new NeighborDaoDBImpl(MainActivity.this);
							if (allFamilyDaoDBImpl.getCurrentAddrDetail("1").getEntity_type()==1){
								gethttpdata(jsonObject.getString("family_apt_num"),aFamily.getFamily_block_id(),aFamily.getFamily_community_id(),
										IHttpRequestUtils.YOULIN,
										REQUEST_NEIGHBOR);
								new Thread(new Runnable() {
									public void run() {
										Message message=new Message();
										message.what=1005;
										Bundle bundle=new Bundle();
										bundle.putLong("family_id", Long.parseLong(familyid));
										message.setData(bundle);
										getAdressHandler.sendMessage(message);
									}
								}).start();
								
							}else {
								aNeighborDaoDBImpl.deleteObject(Long.parseLong(familyid));
							}
					}
				}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(MainActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}

	public void setMinHeapSize(long size) {
		try {
			Class<?> cls = Class.forName("dalvik.system.VMRuntime");
			Method getRuntime = cls.getMethod("getRuntime");
			Object obj = getRuntime.invoke(null);// obj就是Runtime
			if (obj == null) {
				Loger.i("OOM","obj is null");
			} else {
				Loger.i("OOM",obj.getClass().getName());
				Class<?> runtimeClass = obj.getClass();
				Method setMinimumHeapSize = runtimeClass.getMethod("setMinimumHeapSize", long.class);

				setMinimumHeapSize.invoke(obj, size);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	private void getUserBaoxiang(){
			RequestParams params = new RequestParams();
			params.put("tag", "gettreasurebox");
			params.put("apitype", "users");
			params.put("community_id", App.sFamilyCommunityId);
			params.put("user_id", App.sUserLoginId);
			AsyncHttpClient httpClient = new AsyncHttpClient();
			httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params,
					new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					// TODO Auto-generated method stub
					Loger.i("LYM", "1234567890---->"+response.toString());
					try {
						String flag=response.getString("flag");
						if(flag.equals("ok")){
							startActivity(new Intent(MainActivity.this,BaoXiangWeb.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
						}else{
							Loger.i("LYM", "能跳转->"+flag);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString,
						Throwable throwable) {
					new ErrorServer(MainActivity.this, responseString);
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
	}
}
