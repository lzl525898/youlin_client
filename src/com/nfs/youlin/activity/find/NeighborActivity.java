package com.nfs.youlin.activity.find;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.activity.AlertDialogforblack;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.utils.MD5Util;
import com.easemob.exceptions.EaseMobException;
import com.handmark.pulltorefresh.extras.listfragment.PullToRefreshListFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.SplashActivity;
import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;
import com.nfs.youlin.adapter.ContentlistWithHead;
import com.nfs.youlin.adapter.Imagebuttonadapter;
import com.nfs.youlin.adapter.Imagebuttonadapterwithnew;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.NeighborDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.entity.Person;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.HttpClientHelper;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StatusChangeListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.StringToPinyinHelper;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.neighbor_chat_fragment;
import com.nfs.youlin.view.neighbor_list;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class NeighborActivity extends FragmentActivity implements OnRefreshListener<ListView>,StatusChangeListener,EMEventListener{
	public static int sFindFragment = 0;	//0表示未开始初始化    1表示完成初始化    2表示请求网络失败
	final private neighbor_list finalfrag3 =new neighbor_list();
	private neighbor_chat_fragment neighbor_chat = new neighbor_chat_fragment();
	private PullToRefreshListFragment finalfrag9 = new PullToRefreshListFragment();
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	private List<Map<String,Object>> neighborlist  = new ArrayList<Map<String,Object>>();
	private List<Object> neighborobjectlist;
	private ContentlistWithHead mAdapter;
	private Imagebuttonadapterwithnew chatbuttonadapter;
	private Imagebuttonadapter neighborsimpleadapter;
	private List<Map<String,Object>> list ;
	private List<Map<String,Object>> list1;
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;
	/***********下拉刷新 请求邻居*********************/
	private Neighbor neighbor;
	private NeighborDaoDBImpl aNeighborDaoDBImpl;
	private boolean bRequestSet = false;
	private int REQUEST_NEIGHBOR = 1;
	public static List<JSONObject> jsonObjList;
	private String flag = null;
	private List<String> removeblicklist = new ArrayList<String>();
	/*********************************************/
	private AllFamily familydetail;
	private AllFamilyDaoDBImpl familydb ;
	private PopupWindow popupShareWindow;
	private View popupShareView;
	private View listbuttonview;
	private LinearLayout topLinearLayout;
	private TextView bttext1;
	private TextView bttext2;
	private TextView bttext_num;
	private LinearLayout neighborcolorclick;
	private LinearLayout neighborcolornomal;
	private LinearLayout chatcolorclick;
	private LinearLayout chatcolornormal;
	private FragmentTransaction tx;
	private StatusChangeutils statusutils;
	private List<String> blacklist = new ArrayList<String>();
	private List<Person> persons = null;  
    private List<Person> newPersons = null; 
    private FrameLayout layout;
    public static HashMap<String, Integer> indexSelector;
    public static boolean indexInitFlag = false;  
    private Pattern p = Pattern.compile("[a-zA-Z]");
	private Matcher m = null;
	private ListView listbutton1;
	private AccountDaoDBImpl account;
    private String[] indexStr = { "#", "A", "B", "C", "D", "E", "F", "G", "H",  
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",  
            "V", "W", "X", "Y", "Z" }; 
    private PullToRefreshListFragment neighborListView;
    private Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		NeighborActivity.indexInitFlag = true;
    	};
    };
	private BroadcastReceiver sBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Loger.i("LYM", "33333333333333333333333333333333");
			if(arg1.getAction().equals("com.nfs.youlin.find.newmsg")){
				Loger.d("test3", "sBroadcastReceiver receive ");
				setnewMessageflag(getSysUnreadMsgCountTotal(),"2");
			}
			if(arg1.getAction().equals("com.nfs.youlin.find.update")){
				Loger.d("youlin", "sBroadcastReceiver receive 111111111111111111111111");
				neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyObject(App.sFamilyId,App.sUserLoginId);
				getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
				mAdapter.notifyDataSetChanged();
			}
			if(arg1.getAction().equals("com.nfs.youlin.find.get_neighbor")){
				neighborlist.clear();
				runOnUiThread(new Runnable() {
					public void run() {
						mAdapter.notifyDataSetChanged();
					}
				});
			
				String addUserId=arg1.getStringExtra("add_user_id");
				if(addUserId!=null){
					EMChatManager.getInstance().clearConversation(addUserId);
				}
				clickButtonNum();
				getNeighbor();
			}
		}
		
	};
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent arg1) {  //设置当前地址 及 mainactivity
			// TODO Auto-generated method stub
			if(arg1.getAction().equals("com.nfs.youlin.find.FindFragment")){
				Long currentfamilyid = arg1.getLongExtra("family_id", 240000000);
				Loger.d("TEST", "接收到intent的action是"+arg1.getAction()+"接收到的消息是"+ currentfamilyid);
				neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyObject(currentfamilyid,App.sUserLoginId);	
				final AccountDaoDBImpl account = new AccountDaoDBImpl(NeighborActivity.this);
				
				getData(list,"附近邻居",": "+neighborobjectlist.size(),"1");
				neighborsimpleadapter.notifyDataSetChanged();
				setnewMessageflag(getSysUnreadMsgCountTotal(),"0");
				
				FragmentManager manager  = getSupportFragmentManager();
				//finalfrag9 = (PullToRefreshListFragment)manager.findFragmentById(R.id.frag_ptr_list);
				try {
					tx = manager.beginTransaction();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				neighborListView = new PullToRefreshListFragment();
				tx.replace(R.id.tabcontent3,neighborListView,"myfragment");
				tx.commitAllowingStateLoss();
				new Thread(new Runnable() {
					public void run() {
						try {
							while (getSupportFragmentManager().findFragmentByTag(
									"myfragment") == null) {
							}
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									PullToRefreshListFragment finalfrag9 = null;
									try {
										finalfrag9 = (PullToRefreshListFragment) getSupportFragmentManager().findFragmentByTag("myfragment");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										return;
									}
									try {
										mPullRefreshListView = finalfrag9.getPullToRefreshListView();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										return;
									}
									mPullRefreshListView.setMode(Mode.PULL_FROM_START);
									mPullRefreshListView.setOnRefreshListener(NeighborActivity.this);
									actualListView = mPullRefreshListView.getRefreshableView();
									neighborlist.clear();
									getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
									mAdapter = new ContentlistWithHead(
											NeighborActivity.this,
											neighborlist,
											R.layout.neighbor_detail,
											new String[] { "buttonview", "name","profession", "detail" },
											new int[] { R.id.neighbor_head,
													R.id.nerghbor_name,
													R.id.nerghbor_profession,
													R.id.nerghbor_detail});
									actualListView.setAdapter(mAdapter);
									actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
									actualListView.setOnItemLongClickListener(new OnItemLongClickListener() {
										@Override
										public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
												long id) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(NeighborActivity.this, AlertDialogforblack.class);
											intent.putExtra("msg", "");
											intent.putExtra("cancel", true);
											intent.putExtra("position", position-1);
											intent.putExtra("currentuser",String.valueOf(App.sUserLoginId));
											intent.putExtra("removeid",neighborlist.get(position-1).get("userid").toString());
											intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
											startActivityForResult(intent, ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST);
											return true;
										}
									});
									actualListView.setOnItemClickListener(new OnItemClickListener() {
										@Override
										public void onItemClick(
												AdapterView<?> arg0, View arg1,
												int position, long id) {
											// TODO Auto-generated method stub
											try {
												Loger.d("test3","actualListView position"+position);
												Intent intent = new Intent(NeighborActivity.this, com.easemob.chatuidemo.activity.ChatActivity.class);
												intent.putExtra("userId",neighborlist.get(position-1).get("userid").toString());
												intent.putExtra("chatType", CHATTYPE_SINGLE);
												intent.putExtra("usernick", neighborlist.get(position-1).get("name").toString());
												String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
												intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
												intent.putExtra("neighborurl", neighborlist.get(position-1).get("buttonview").toString());
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
									        
										}
									});
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
		
		}
	};

	/**
	 * 获取未读个人消息数
	 * 
	 * @return
	 */
	public int getSysUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		int userUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for (EMConversation conversation : EMChatManager.getInstance()
				.getAllConversations().values()) {
			if (!conversation.getUserName().equals("xitongxinxi"))
				userUnreadMsgCount += conversation.getUnreadMsgCount();
		}
		
		//return unreadMsgCountTotal - chatroomUnreadMsgCount;
		return userUnreadMsgCount;
	}
	public void setnewMessageflag(int count,String flag) {
		Loger.d("test3", "message count=" + count);
		if(count>0){
			getData(list1,"聊天记录","1",flag);
			NeighborActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					chatbuttonadapter.notifyDataSetChanged();
				}
			});
		}else if(count==0){
			getData(list1,"聊天记录","0",flag);
			NeighborActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					chatbuttonadapter.notifyDataSetChanged();
				}
			});
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_neighbor);
		//FindFragment.ylProgressDialog.dismiss();
		ActionBar actionbar=getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setTitle("邻居");
		neighbor = new Neighbor(NeighborActivity.this);
		aNeighborDaoDBImpl = new NeighborDaoDBImpl(NeighborActivity.this);
		indexSelector = new HashMap<String, Integer>(); 
        familydb = new AllFamilyDaoDBImpl(NeighborActivity.this);
        familydetail = new AllFamily(NeighborActivity.this);
        statusutils = new StatusChangeutils();
		statusutils.statuschangelistener("SETNEIGHBOR",NeighborActivity.this);  //地址审核通过发送
		layout = ((FrameLayout)findViewById(R.id.tabcontent3));
		String familyId= loadUserfamilyidPrefrence();
	    if(familyId==null || familyId.isEmpty() || familyId.length()<=0 || familyId.equals("null")){
	    	familyId = "0";
		}
		neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyObject(Long.parseLong(familyId),App.sUserLoginId);
		for (int i = 0; i < neighborobjectlist.size(); i++) {
			Loger.i("LYM","000000000000000000--->"+((Neighbor)neighborobjectlist.get(i)).getUser_name());
		}
		account = new AccountDaoDBImpl(NeighborActivity.this);
		FragmentManager manager  = getSupportFragmentManager();
		//finalfrag9 = (PullToRefreshListFragment)manager.findFragmentById(R.id.frag_ptr_list);
		FragmentTransaction tx = manager.beginTransaction();
		neighborListView = new PullToRefreshListFragment();
		tx.replace(R.id.tabcontent3,neighborListView,"myfragment");
		tx.commitAllowingStateLoss();
        // TODO Auto-generated method stub
		
		new Thread(new Runnable() {
			public void run() {
				try {
					while (NeighborActivity.this.getSupportFragmentManager().findFragmentByTag(
							"myfragment") == null) {
					}
					NeighborActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) NeighborActivity.this.getSupportFragmentManager()
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
							mPullRefreshListView.setMode(Mode.PULL_FROM_START);
							mPullRefreshListView.setOnRefreshListener(NeighborActivity.this);
							// You can also just use
							// mPullRefreshListFragment.getListView()
							actualListView = mPullRefreshListView.getRefreshableView();
//							aNeighborDaoDBImpl.findPointTypeObject(App.);
							neighborlist.clear();
							getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
							//Loger.d("test3", neighborlist.get(0).get("profession").toString());
								mAdapter = new ContentlistWithHead(
										NeighborActivity.this,
										neighborlist,
										R.layout.neighbor_detail,
										new String[] { "buttonview", "name","profession", "detail" },
										new int[] { R.id.neighbor_head,
												R.id.nerghbor_name,
												R.id.nerghbor_profession,
												R.id.nerghbor_detail });

							// TODO Auto-generated method stub
							actualListView.setAdapter(mAdapter);
							actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
							actualListView.setOnItemLongClickListener(new OnItemLongClickListener() {
								@Override
								public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
										long id) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(NeighborActivity.this, AlertDialogforblack.class);
									intent.putExtra("msg", "");
									intent.putExtra("cancel", true);
									intent.putExtra("position", position-1);
									intent.putExtra("currentuser",String.valueOf(App.sUserLoginId));
									intent.putExtra("removeid",neighborlist.get(position-1).get("userid").toString());
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivityForResult(intent, ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST);
									return true;
								}
							});
							actualListView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(
										AdapterView<?> arg0, View arg1,
										int position, long id) {
									// TODO Auto-generated method stub
									try {
										Loger.d("test3","actualListView position"+position);
										Intent intent = new Intent(NeighborActivity.this, com.easemob.chatuidemo.activity.ChatActivity.class);
										intent.putExtra("userId",neighborlist.get(position-1).get("userid").toString());
										intent.putExtra("chatType", CHATTYPE_SINGLE);
										intent.putExtra("usernick", neighborlist.get(position-1).get("name").toString());
										String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
										intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
										intent.putExtra("neighborurl", neighborlist.get(position-1).get("buttonview").toString());
										intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										startActivity(intent);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							        
								}
							});
							finalfrag9.setListShown(true);
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		}).start();
		Loger.d("test3", "FindFragment neighborobjectlist size="+neighborobjectlist.size()+"sFamilyId="+App.sFamilyId);
	        final String strs = "附近邻居";
	        final String strs1 = "聊天记录";
	        list = new ArrayList<Map<String,Object>>();
	        getData(list,strs,": "+neighborobjectlist.size(),"1");
	        neighborsimpleadapter = new Imagebuttonadapter(NeighborActivity.this,list,
	        		R.layout.fragment_button_withnum,
	        		new String[] {"text","number","color"},new int[]{R.id.bttext1,R.id.bttext_num,R.id.fengecolorclick1,R.id.fengecolornormal1}); 
	        listbutton1 = (ListView)findViewById(R.id.listbutton1);

	        listbutton1.setAdapter(neighborsimpleadapter);
	        list1 = new ArrayList<Map<String,Object>>();
	        getData(list1,strs1,"0","0");
	        chatbuttonadapter = new Imagebuttonadapterwithnew(NeighborActivity.this,list1,
	        		R.layout.fragment_button_withnew,
	        		new String[] {"text","number","color"},new int[]{R.id.bttext2,R.id.bttext_flag,R.id.fengecolorclick,R.id.fengecolornormal});
	        ListView listbutton2 = (ListView)findViewById(R.id.listbutton2);
	        listbutton2.setAdapter(chatbuttonadapter);
	        popupShareView =NeighborActivity.this.getLayoutInflater().inflate(
					R.layout.neighbor_detail_letter, null);
	        LinearLayout popempty = (LinearLayout) popupShareView.findViewById(R.id.pop_empty);
	        popempty.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					try {
						popupShareWindow.dismiss();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}
			});
	        topLinearLayout = (LinearLayout) popupShareView
					.findViewById(R.id.for_letter_top);
//	        detailimage = (ImageView)popupShareView.findViewById(R.id.neighbor_detail_image);
//	        detail_name = (TextView)popupShareView.findViewById(R.id.neighbor_detail_name);
//	        detail_briefdesc = (TextView)popupShareView.findViewById(R.id.neighbor_detail_describe);
//	        detail_send = (Button)popupShareView.findViewById(R.id.neighbor_detail_letter);
	       
	        /***************popwindow endr****************************/
	        listbutton1.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            	bttext1 = (TextView)(view.findViewById(R.id.bttext1));
            	bttext_num = (TextView)(view.findViewById(R.id.bttext_num));
            	neighborcolorclick = (LinearLayout)(view.findViewById(R.id.fengecolorclick1));
            	neighborcolornomal = (LinearLayout)(view.findViewById(R.id.fengecolornormal1));
            	try {
					bttext1.setTextColor(0xffffba02);
					bttext_num.setTextColor(0xffffba02);
					neighborcolorclick.setVisibility(view.VISIBLE);
					neighborcolornomal.setVisibility(view.GONE);
					bttext2.setTextColor(0xff909090);					
					chatcolorclick.setVisibility(view.GONE);
					chatcolornormal.setVisibility(view.VISIBLE);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					setnewMessageflag(getSysUnreadMsgCountTotal(),"0");
				}
            	
        		FragmentManager manager  = getSupportFragmentManager();  
        		FragmentTransaction tx = manager.beginTransaction();
        		neighborListView = new PullToRefreshListFragment();
        		tx.replace(R.id.tabcontent3,neighborListView,"myfragment");
        		tx.commitAllowingStateLoss();
                // TODO Auto-generated method stub
        		new Thread(new Runnable() {
        			public void run() {
        					try {
								while (NeighborActivity.this.getSupportFragmentManager().findFragmentByTag(
										"myfragment") == null) {
								}
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
        					NeighborActivity.this.runOnUiThread(new Runnable() {
        						@Override
        						public void run() {
        							// TODO Auto-generated method stub
        							final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) NeighborActivity.this.getSupportFragmentManager()
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
        							mPullRefreshListView.setMode(Mode.PULL_FROM_START);
        							mPullRefreshListView.setOnRefreshListener(NeighborActivity.this);
        							// You can also just use
        							actualListView = mPullRefreshListView.getRefreshableView();
        							neighborlist.clear();
        							getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
        								mAdapter = new ContentlistWithHead(
        										NeighborActivity.this,
        										neighborlist,
        										R.layout.neighbor_detail,
        										new String[] { "buttonview", "name","profession", "detail" },
        										new int[] { R.id.neighbor_head,
        												R.id.nerghbor_name,
        												R.id.nerghbor_profession,
        												R.id.nerghbor_detail });

        							// TODO Auto-generated method stub
        							actualListView.setAdapter(mAdapter);
        							actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        							actualListView.setOnItemLongClickListener(new OnItemLongClickListener() {
        								@Override
        								public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
        										long id) {
        									// TODO Auto-generated method stub
        									Intent intent = new Intent(NeighborActivity.this, AlertDialogforblack.class);
        									intent.putExtra("msg", "");
        									intent.putExtra("cancel", true);
        									intent.putExtra("position", position-1);
        									intent.putExtra("currentuser",String.valueOf(App.sUserLoginId));
        									intent.putExtra("removeid",neighborlist.get(position-1).get("userid").toString());
        									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        									startActivityForResult(intent, ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST);
        									return true;
        								}
        							});
        							actualListView.setOnItemClickListener(new OnItemClickListener() {

        								@Override
        								public void onItemClick(
        										AdapterView<?> arg0, View arg1,
        										int position, long id) {
        									// TODO Auto-generated method stub
        									Loger.d("test3","actualListView position"+position);
        									try {
												Intent intent = new Intent(NeighborActivity.this, com.easemob.chatuidemo.activity.ChatActivity.class);
												intent.putExtra("userId",neighborlist.get(position-1).get("userid").toString());
												intent.putExtra("chatType", CHATTYPE_SINGLE);
												intent.putExtra("usernick", neighborlist.get(position-1).get("name").toString());
												String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
												intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
												intent.putExtra("neighborurl", neighborlist.get(position-1).get("buttonview").toString());
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
        								}
        							});
        							try {
										finalfrag9.setListShown(true);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
        						}
        					});


        			}
        		}).start();
            }
        });
	        
	       listbutton2.setOnItemClickListener(new OnItemClickListener(){
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
	            	bttext2 = ((TextView)view.findViewById(R.id.bttext2));
	            	chatcolorclick = (LinearLayout)(view.findViewById(R.id.fengecolorclick));
	            	chatcolornormal = (LinearLayout)(view.findViewById(R.id.fengecolornormal));
	            	
						FragmentManager manager  = getSupportFragmentManager();
						FragmentTransaction tx = manager.beginTransaction();
						if(neighbor_chat.isAdded()){
							tx.show(neighbor_chat);
						}else{
							tx.replace(R.id.tabcontent3,neighbor_chat);
						}
						
						tx.commitAllowingStateLoss();
						//setnewMessageflag(0,"1");
	            	try {
						bttext2.setTextColor(0xffffba02);
						chatcolorclick.setVisibility(view.VISIBLE);
						chatcolornormal.setVisibility(view.GONE);
						bttext1.setTextColor(0xff909090);
						bttext_num.setTextColor(0xff909090);
						neighborcolorclick.setVisibility(view.GONE);
						neighborcolornomal.setVisibility(view.VISIBLE);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						getData(list,strs,": "+neighborobjectlist.size(),"0");
						neighborsimpleadapter.notifyDataSetChanged();
					}
	                // TODO Auto-generated method stub
	            	//Loger.d("hyytest",strs1[position]+"被选中");

	            }
	        });	
	}
	
	private List<Map<String, Object>> getData(
			List<Map<String, Object>> oldlist, String strs, String num,
			String color) {
		oldlist.clear();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", strs);
		map.put("number", num);  ///  flag
		map.put("color", color);
		oldlist.add(map);
		return oldlist;
	}
	private List<Map<String, Object>> getDataupdateblack(List<Map<String, Object>> neighborlist) {  
		 for (int i = 0; i < neighborlist.size(); i++) {
			if (neighborlist.get(i).get("name").toString().length() > 1) {
				Map<String, Object> map = neighborlist.get(i);
				map.put("blackflag", 0);
				for (int j = 0; j < blacklist.size(); j++) {
					if (Long.parseLong(neighborlist.get(i).get("userid").toString()) == Long.parseLong(blacklist.get(j))) {
						map.put("blackflag", 1);
						neighborlist.set(i, map);
						break;
					}
				}
			}

		}   
	        return neighborlist; 
	}
    private List<Map<String, Object>> getDatawithrank(List<Map<String, Object>> neighborlist,List<Object> objectneighbors,int getcount) {  
    	
    	persons = new ArrayList<Person>();
        for (int i = 0; i < getcount; i++) {  
            String personName = ((Neighbor)(objectneighbors.get(i))).getUser_name();
            Person person = new Person(personName,i);
            persons.add(person);    
        }  
        sortList(sortIndex(persons), neighborlist, objectneighbors);
        handler.sendEmptyMessage(0);
        return neighborlist; 
    }
    
    private Map<String, Object> setNeighborsWithMap(Neighbor neighbor){
    	Map<String, Object> map = new HashMap<String, Object>();  
        map.put("name", neighbor.getUser_name());  
        map.put("detail",neighbor.getBuilding_num()+"-"+neighbor.getAptnum());
        map.put("userid",neighbor.getUser_id());
        map.put("buttonview", neighbor.getUser_portrait());
        map.put("describe", neighbor.getBriefdesc());
        map.put("status", neighbor.getAddrstatus());
        map.put("profession", neighbor.getProfession());
        map.put("blackflag", 0);
        for(int j=0;j<blacklist.size();j++){
        	if(neighbor.getUser_id() == Long.parseLong(blacklist.get(j))){
        		map.put("blackflag", 1);
        		break;
        	}
        }
        return map;
    }
    
    private Map<String, Object> setNeighborIndexWithMap(String indexString){
    	Map<String, Object> map = new HashMap<String, Object>();  
    	map.put("name", indexString);  
    	return map;
    }


	@Override
	public void onStop() {
			super.onStop();
					Loger.i("slide", "SquareFragment--onStop");
						}
	private String loadAddrdetailPrefrence(){
		SharedPreferences sharedata = NeighborActivity.this.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("detail", "未设置");
	}
    @Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if(refreshView.isShownHeader()){
			//判断头布局是否可见，如果可见执行下拉刷新
			//设置尾布局样式文字
			NeighborActivity.indexInitFlag = false;
			bRequestSet = false;
			mPullRefreshListView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
			mPullRefreshListView.getLoadingLayoutProxy().setPullLabel("下拉刷新数据");
			mPullRefreshListView.getLoadingLayoutProxy().setReleaseLabel("释放开始刷新");
			/****************** database ***********************/
			AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(NeighborActivity.this);
			final AllFamily familyObjs = allFDaoDBImpl.getCurrentAddrDetail(MainActivity.currentcity+MainActivity.currentvillage+loadAddrdetailPrefrence());

			//模拟加载数据线程休息3秒
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					// TODO Auto-generated method stub
					/****************** database ***********************/
					if (((AllFamily) familyObjs).getEntity_type() == 1) {
						neighbor = new Neighbor(NeighborActivity.this);
						aNeighborDaoDBImpl = new NeighborDaoDBImpl(NeighborActivity.this);
						Loger.d("test3",((AllFamily) familyObjs).getFamily_apt_num()+"!--"+((AllFamily) familyObjs).getFamily_block_id()+"!--"+((AllFamily) familyObjs).getFamily_community_id());
						gethttpdata(((AllFamily) familyObjs).getFamily_apt_num(),((AllFamily) familyObjs).getFamily_block_id(),
								((AllFamily) familyObjs).getFamily_community_id(),
									IHttpRequestUtils.YOULIN,
									REQUEST_NEIGHBOR);
		
					} else{
						bRequestSet = true;
						aNeighborDaoDBImpl.deleteObject(App.sFamilyId);
					}
					try {
						Long currenttime = System.currentTimeMillis();
						while (!bRequestSet) {
							if ((System.currentTimeMillis() - currenttime) > (App.WAITFORHTTPTIME+5000)) {
								bRequestSet = true;
							}
						}
						if (bRequestSet && flag != null && flag.equals("ok")){
							bRequestSet = false;
							flag = "";
							Loger.d("test3","Belong_family_id="+App.sFamilyId);
							Loger.i("test3", "set neighbor flag->" + flag);
							if (jsonObjList.size() > 0) {
								Loger.d("test3", "neighbor list**=" + jsonObjList);
								aNeighborDaoDBImpl.deleteObject(App.sFamilyId);

								for (int i = 0; i < jsonObjList.size(); i++) {

									// try {
									if (Long.parseLong(jsonObjList.get(i).getString("family_id")) > 0) {
										neighbor.setAptnum(jsonObjList.get(i).getString("aptnum"));
										neighbor.setUser_id(Long.parseLong(jsonObjList.get(i).getString("user_id")));
										neighbor.setLogin_account(App.sUserLoginId);
										neighbor.setUser_family_id(
												Long.parseLong(jsonObjList.get(i).getString("family_id")));
										neighbor.setBelong_family_id(App.sFamilyId);
										neighbor.setUser_name(jsonObjList.get(i).getString("user_nick"));
										neighbor.setUser_portrait(jsonObjList.get(i).getString("user_portrait"));
										neighbor.setBuilding_num(jsonObjList.get(i).getString("building_num"));
										neighbor.setProfession(jsonObjList.get(i).getString("user_profession"));
										neighbor.setBriefdesc(jsonObjList.get(i).getString("user_signature"));
										neighbor.setAddrstatus(jsonObjList.get(i).getString("user_public_status"));
										neighbor.setUser_type(Integer.parseInt(jsonObjList.get(i).getString("user_type")));
										Loger.i("test3", "jsonObjList.get(i).getString(user_nick)="
												+ jsonObjList.get(i).getString("user_nick"));
									} else {
										Loger.i("test3", "jsonObjList.get(i).getString(user_nick)="
												+ jsonObjList.get(i).getString("user_nick"));
										continue;
									}

									// } catch (JSONException e) {
									// // TODO Auto-generated catch block
									// e.printStackTrace();
									// }
									aNeighborDaoDBImpl.saveObject(neighbor);

								}
							}
							
						}
						
						neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyObject(App.sFamilyId,App.sUserLoginId);
						getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					//完成对下拉刷新ListView的更新操作
					mAdapter.notifyDataSetChanged();
					getData(list,"附近邻居",": "+neighborobjectlist.size(),"2");
					neighborsimpleadapter.notifyDataSetChanged();
					//将下拉视图收起
					mPullRefreshListView.onRefreshComplete();
				};
			}.execute();
		}
		if(refreshView.isShownFooter()){
			//判断尾布局是否可见，如果可见执行上拉加载更多
		}
	}
	public String loadUserfamilyidPrefrence(){
		SharedPreferences sharedata = NeighborActivity.this.getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("familyid", "000000");
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		NeighborActivity.this.unregisterReceiver(mBroadcastReceiver);
		NeighborActivity.this.unregisterReceiver(sBroadcastReceiver);
		super.onDestroy();
	}
	private void gethttpdata(String apt_num,Long param2,Long param3,String http_addr,int request_index){
		/***********************************************/
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		SyncHttpClient client = new SyncHttpClient();
		Loger.d("test3","apt_num="+apt_num+"block_id="+param2+"community_id="+param3 );
		if(request_index == REQUEST_NEIGHBOR){
			params.put("user_id",App.sUserLoginId);
			params.put("apt_num", apt_num);
			if(!param2.equals("0") && param2 != null)
				params.put("block_id", param2);
			params.put("community_id", param3);
		}
		params.put("tag", "neighbors");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		flag ="";
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				String flag = null;
				Loger.d("test3", "neighbor JSONObject");
//					String community_id = null;
//					String community_name = null;
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test3", "set neighbor flag->" + flag);
					
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
				Loger.d("test3", "neighbor JSONArray");
				try {
					org.json.JSONObject obj = null;
					
					if(jsonContext.length()>0){
						for (int i = 0; i < jsonContext.length(); i++) {
							jsonObjList.add(jsonContext.getJSONObject(i));
							obj = jsonContext.getJSONObject(i);
						}
						Loger.i("test3","jsonObjList->"+jsonObjList.size());
						Loger.i("test3", "set neighbor flag->" + flag);
						flag = "ok";
						bRequestSet = true;
						
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
					Loger.i("test3",
							responseString + "\r\n" + throwable.toString()
							+"\r\n-----\r\n" + statusCode);
					bRequestSet = false;
					new ErrorServer(NeighborActivity.this, responseString.toString());
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	private void gethttpdata1(String apt_num,Long param2,Long param3,String http_addr,int request_index){
		/***********************************************/
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		Loger.d("test3","apt_num="+apt_num+"block_id="+param2+"community_id="+param3 );
		if(request_index == REQUEST_NEIGHBOR){
			params.put("user_id", App.sUserLoginId);
			params.put("apt_num", apt_num);
			if(!param2.equals("0") && param2 != null)
				params.put("block_id", param2);
			params.put("community_id", param3);
		}
		params.put("tag", "neighbors");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		flag ="";
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				String flag = null;
				Loger.d("test3", "neighbor JSONObject");
//					String community_id = null;
//					String community_name = null;
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test3", "set neighbor flag->" + flag);
					
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
				Loger.d("test3", "neighbor JSONArray");
				try {
					org.json.JSONObject obj = null;
					
					if(jsonContext.length()>0){
						for (int i = 0; i < jsonContext.length(); i++) {
							jsonObjList.add(jsonContext.getJSONObject(i));
							obj = jsonContext.getJSONObject(i);
						}
						Loger.i("test3","jsonObjList->"+jsonObjList.size());
						Loger.i("test3", "set neighbor flag->" + flag);
						flag = "ok";
						bRequestSet = true;
						
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
					Loger.i("test3",
							responseString + "\r\n" + throwable.toString()
							+"\r\n-----\r\n" + statusCode);
					bRequestSet = false;
					new ErrorServer(NeighborActivity.this, responseString.toString());
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	@Override
	public void setstatuschanged(int status) {
		// TODO Auto-generated method stub
		Loger.d("test5","findfragment setstatuschanged");
		bRequestSet = false;
		if(status == 1){
			/****************** database ***********************/
			AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(NeighborActivity.this);
			final AllFamily familyObjs = allFDaoDBImpl.getCurrentAddrDetail(MainActivity.currentcity+MainActivity.currentvillage+loadAddrdetailPrefrence());
			/****************** database ***********************/
			if (((AllFamily) familyObjs).getEntity_type() == 1) {
				neighbor = new Neighbor(NeighborActivity.this);
				aNeighborDaoDBImpl = new NeighborDaoDBImpl(NeighborActivity.this);
				Loger.d("test3",((AllFamily) familyObjs).getFamily_apt_num()+"--"+((AllFamily) familyObjs).getFamily_block_id()+"--"+((AllFamily) familyObjs).getFamily_community_id());
				gethttpdata1(((AllFamily) familyObjs).getFamily_apt_num(),((AllFamily) familyObjs).getFamily_block_id(),
						((AllFamily) familyObjs).getFamily_community_id(),
							IHttpRequestUtils.YOULIN,
							REQUEST_NEIGHBOR);
			} else{
				bRequestSet = true;
				aNeighborDaoDBImpl.deleteObject(App.sFamilyId);
			}
			//模拟加载数据线程休息3秒
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					// TODO Auto-generated method stub
					try {
						Long currenttime = System.currentTimeMillis();
						while (!bRequestSet) {
							if ((System.currentTimeMillis() - currenttime) > (App.WAITFORHTTPTIME+5000)) {
								bRequestSet = true;
							}
						}
						Loger.d("test3","1111111112--"+flag);
						if (bRequestSet && flag != null && flag.equals("ok")){
							bRequestSet = false;
							flag = "";
							Loger.d("test3","Belong_family_id="+App.sFamilyId);
							Loger.i("test3","jsonObjList->"+jsonObjList.size());
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
										neighbor.setUser_portrait(jsonObjList.get(i).getString("user_portrait"));
										neighbor.setBuilding_num(jsonObjList.get(i).getString("building_num"));
										neighbor.setProfession(jsonObjList.get(i).getString("user_profession"));
										neighbor.setBriefdesc(jsonObjList.get(i).getString("user_signature"));
										neighbor.setAddrstatus(jsonObjList.get(i).getString("user_public_status"));
									}else{
										Loger.i("LYM", "neiborActivity->"+jsonObjList.get(i).getString("family_id"));
										continue;
									}
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Loger.i("LYM", "neiborActivity-try-catch>"+e.getMessage());
								}
								aNeighborDaoDBImpl.saveObject(neighbor);
								
							}
							
						}

						neighborlist.clear();
						neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyObject(App.sFamilyId,App.sUserLoginId);
						getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						
						e.printStackTrace();
					}
					return null;
				}
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					//完成对下拉刷新ListView的更新操作
					mAdapter.notifyDataSetChanged();
					getData(list,"附近邻居",": "+neighborobjectlist.size(),"1");
					neighborsimpleadapter.notifyDataSetChanged();
					//将下拉视图收起
					mPullRefreshListView.onRefreshComplete();
				};
			}.execute();
		}
	}
	@Override
	public void setstatuschanged(int status, Bundle data) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == NeighborActivity.this.RESULT_OK){
			if (requestCode == ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单
				gethttpdataForBlack(App.sUserLoginId,neighborlist.get(data.getIntExtra("position", -1)).get("userid").toString(), 1, IHttpRequestUtils.YOULIN,data.getIntExtra("position", -1));
			}
	            
		}
		else if(resultCode == AlertDialogforblack.REMOVE_BLACK_USER){
			if (requestCode == ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST) { // 移出黑名单
				neighborlist.clear();
				runOnUiThread(new Runnable() {
					public void run() {
						mAdapter.notifyDataSetChanged();
					}
				});
				getNeighbor();
			}
		}
	}
	private void clickButtonNum(){
//		View view=LayoutInflater.from(NeighborActivity.this).inflate(R.layout.fragment_button_withnum, null);
//		bttext1 = (TextView)(view.findViewById(R.id.bttext1));
//    	bttext_num = (TextView)(view.findViewById(R.id.bttext_num));
//    	neighborcolorclick = (LinearLayout)(view.findViewById(R.id.fengecolorclick1));
//    	neighborcolornomal = (LinearLayout)(view.findViewById(R.id.fengecolornormal1));
    	try {
			bttext1.setTextColor(0xffffba02);
			bttext_num.setTextColor(0xffffba02);
			neighborcolorclick.setVisibility(View.VISIBLE);
			neighborcolornomal.setVisibility(View.GONE);
			bttext2.setTextColor(0xff909090);					
			chatcolorclick.setVisibility(View.GONE);
			chatcolornormal.setVisibility(View.VISIBLE);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			setnewMessageflag(getSysUnreadMsgCountTotal(),"0");
		}
    	
		FragmentManager manager  = getSupportFragmentManager(); 
		FragmentTransaction tx = manager.beginTransaction();
		neighborListView = new PullToRefreshListFragment();
		tx.replace(R.id.tabcontent3,neighborListView,"myfragment");
		tx.commitAllowingStateLoss();
        // TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
					try {
						while (NeighborActivity.this.getSupportFragmentManager().findFragmentByTag(
								"myfragment") == null) {
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					NeighborActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							final PullToRefreshListFragment finalfrag9 = (PullToRefreshListFragment) NeighborActivity.this.getSupportFragmentManager()
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
							mPullRefreshListView.setMode(Mode.PULL_FROM_START);
							mPullRefreshListView.setOnRefreshListener(NeighborActivity.this);
							// You can also just use
							actualListView = mPullRefreshListView.getRefreshableView();
							neighborlist.clear();
							getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
								mAdapter = new ContentlistWithHead(
										NeighborActivity.this,
										neighborlist,
										R.layout.neighbor_detail,
										new String[] { "buttonview", "name","profession", "detail" },
										new int[] { R.id.neighbor_head,
												R.id.nerghbor_name,
												R.id.nerghbor_profession,
												R.id.nerghbor_detail });

							// TODO Auto-generated method stub
							actualListView.setAdapter(mAdapter);
							actualListView.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
							actualListView.setOnItemLongClickListener(new OnItemLongClickListener() {
								@Override
								public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
										long id) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(NeighborActivity.this, AlertDialogforblack.class);
									intent.putExtra("msg", "");
									intent.putExtra("cancel", true);
									intent.putExtra("position", position-1);
									intent.putExtra("currentuser",String.valueOf(App.sUserLoginId));
									intent.putExtra("removeid",neighborlist.get(position-1).get("userid").toString());
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivityForResult(intent, ChatActivity.REQUEST_CODE_ADD_TO_BLACKLIST);
									return true;
								}
							});
							actualListView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(
										AdapterView<?> arg0, View arg1,
										int position, long id) {
									// TODO Auto-generated method stub
									Loger.d("test3","actualListView position"+position);
									try {
										Intent intent = new Intent(NeighborActivity.this, com.easemob.chatuidemo.activity.ChatActivity.class);
										intent.putExtra("userId",neighborlist.get(position-1).get("userid").toString());
										intent.putExtra("chatType", CHATTYPE_SINGLE);
										intent.putExtra("usernick", neighborlist.get(position-1).get("name").toString());
										String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
										intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
										intent.putExtra("neighborurl", neighborlist.get(position-1).get("buttonview").toString());
										intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										startActivity(intent);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
							try {
								finalfrag9.setListShown(true);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});


			}
		}).start();
	}
	private void getNeighbor(){
		bRequestSet = false;
		/****************** database ***********************/
		AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(NeighborActivity.this);
		final AllFamily familyObjs = allFDaoDBImpl.getCurrentAddrDetail(MainActivity.currentcity+MainActivity.currentvillage+loadAddrdetailPrefrence());
		/****************** database ***********************/
		if (((AllFamily) familyObjs).getEntity_type() == 1) {
			neighbor = new Neighbor(NeighborActivity.this);
			aNeighborDaoDBImpl = new NeighborDaoDBImpl(NeighborActivity.this);
			Loger.d("test3",((AllFamily) familyObjs).getFamily_apt_num()+"--"+((AllFamily) familyObjs).getFamily_block_id()+"--"+((AllFamily) familyObjs).getFamily_community_id());
			gethttpdata1(((AllFamily) familyObjs).getFamily_apt_num(),((AllFamily) familyObjs).getFamily_block_id(),
					((AllFamily) familyObjs).getFamily_community_id(),
						IHttpRequestUtils.YOULIN,
						REQUEST_NEIGHBOR);
		} else{
			bRequestSet = true;
			aNeighborDaoDBImpl.deleteObject(App.sFamilyId);
		}
		//模拟加载数据线程休息3秒
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					Long currenttime = System.currentTimeMillis();
					while (!bRequestSet) {
						if ((System.currentTimeMillis() - currenttime) > (App.WAITFORHTTPTIME+5000)) {
							bRequestSet = true;
						}
					}
					Loger.d("test3","1111111112--"+flag);
					if (bRequestSet && flag != null && flag.equals("ok")){
						bRequestSet = false;
						flag = "";
						Loger.d("test3","Belong_family_id="+App.sFamilyId);
						Loger.i("test3","jsonObjList->"+jsonObjList.size());
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
							}
							aNeighborDaoDBImpl.saveObject(neighbor);
							
						}
						
					}
					neighborlist.clear();
					neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyObject(App.sFamilyId,App.sUserLoginId);
					getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				//完成对下拉刷新ListView的更新操作
				mAdapter.notifyDataSetChanged();
				getData(list,"附近邻居",": "+neighborobjectlist.size(),"1");
				neighborsimpleadapter.notifyDataSetChanged();
				//将下拉视图收起
				mPullRefreshListView.onRefreshComplete();
			};
		}.execute();
	}
	private void gethttpdataForBlack(final long user_id,final String black_uesr_id,int action_id,String http_addr,final int position){
		/***********************************************/
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
			params.put("user_id", user_id);
			params.put("black_user_id", black_uesr_id);
			params.put("action_id", action_id);
			params.put("tag", "blacklist");
			params.put("apitype", IHttpRequestUtils.APITYPE[0]);
			flag ="";
			Loger.i("LYM","111111111111111");
			client.post(IHttpRequestUtils.URL+http_addr,params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				Loger.i("LYM","111111111111111");
				org.json.JSONObject jsonContext = response;
				String flag = null;
				try {
					flag = jsonContext.getString("flag");
					Loger.i("LYM", "set black flag->" + flag);
					if(flag.equals("ok")){
						NeighborActivity.this.runOnUiThread(new Runnable() {
							public void run() {
								try {
									Intent blackIntent=new Intent("youlin.friend.action.black");
	                                blackIntent.putExtra("sender_id", Long.parseLong(black_uesr_id));
	                                NeighborActivity.this.sendBroadcast(blackIntent);
									EMContactManager.getInstance().addUserToBlackList(black_uesr_id, false);
									EMChatManager.getInstance().clearConversation(neighborlist.get(position).get("userid").toString());
									NeighborDaoDBImpl neighborDaoDBImpl=new NeighborDaoDBImpl(NeighborActivity.this);
									neighborDaoDBImpl.deleteObjectByuserId(Long.parseLong(neighborlist.get(position).get("userid").toString()));
									neighborobjectlist = aNeighborDaoDBImpl.findPointFamilyObject(App.sFamilyId,App.sUserLoginId);
									getDatawithrank(neighborlist,neighborobjectlist, neighborobjectlist.size());
									mAdapter.notifyDataSetChanged();
									getData(list,"附近邻居",": "+neighborobjectlist.size(),"2");
									neighborsimpleadapter.notifyDataSetChanged();
								} catch (EaseMobException e) {
									e.printStackTrace();
										try {
											HttpClient httpClient=HttpClientHelper.getHttpClient();
											HttpPost httpPost=new HttpPost(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN);
											List<NameValuePair> params=new ArrayList<NameValuePair>();
											params.add(new BasicNameValuePair("black_user_id", black_uesr_id));
											params.add(new BasicNameValuePair("user_id", String.valueOf(user_id)));
											params.add(new BasicNameValuePair("action_id", "2"));
											params.add(new BasicNameValuePair("tag", "blacklist"));
											params.add(new BasicNameValuePair("apitype", "users"));
											
											 StringBuilder builderWithInfo = new StringBuilder();
						                        builderWithInfo.append("black_user_id");
						                        builderWithInfo.append(black_uesr_id);
						                        builderWithInfo.append("user_id");
						                        builderWithInfo.append(String.valueOf(user_id));
						                        builderWithInfo.append("action_id");
						                        builderWithInfo.append("2");
						                        builderWithInfo.append("tag");
						                        builderWithInfo.append("blacklist");
						                        builderWithInfo.append("apitype");
						                        builderWithInfo.append("users");
						                        String saltCode = String.valueOf(System.currentTimeMillis());
						                        String hashCode = "";
						                        try {
						                            hashCode = MD5Util.getEncryptedPwd(builderWithInfo.toString(),saltCode);
						                        } catch (NoSuchAlgorithmException e1) {
						                            hashCode = "9573";
						                            e1.printStackTrace();
						                        }
						                        params.add(new BasicNameValuePair("salt",saltCode));
						                        params.add(new BasicNameValuePair("hash",hashCode));
						                        params.add(new BasicNameValuePair("keyset","black_user_id:user_id:action_id:tag:apitype:"));
						                        if(App.sPhoneIMEI==null){
						                    		try {
						                				App.sPhoneIMEI = YLPushUtils.getImei(MainActivity.sMainActivity, App.sPhoneIMEI);
						                				params.add(new BasicNameValuePair("tokenvalue", App.sPhoneIMEI));
						                			} catch (Exception e1) {
						                				e1.printStackTrace();
						                			}
						                    	}else{
						                    		params.add(new BasicNameValuePair("tokenvalue", App.sPhoneIMEI));
						                    	}
											httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
											HttpResponse httpResponse=httpClient.execute(httpPost);
											if(httpResponse.getStatusLine().getStatusCode()==200){
												String msg=EntityUtils.toString(httpResponse.getEntity());
												JSONObject jsonObject=new JSONObject(msg);
												if(jsonObject.getString("flag").equals("ok")){
													Loger.i("youlin", "移出成功");
												}else{
													Loger.i("youlin", "移出失败");
												}
											}
										} catch (UnsupportedEncodingException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (ClientProtocolException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (ParseException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} catch (JSONException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
									Toast.makeText(NeighborActivity.this, "加入黑名单失败", 0).show();
								}
							}
						});
						
					}else{
						Toast.makeText(NeighborActivity.this, "加入黑名单失败", 0).show();
					}
				} catch (org.json.JSONException e) {
					e.printStackTrace();
					Loger.i("LYM","JSONObject->"+ e.getMessage());
				}
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
					Loger.i("LYM","22222222222222222");
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					new ErrorServer(NeighborActivity.this, responseString.toString());
					super.onFailure(statusCode, headers,responseString, throwable);
				}
			});

	}
	
	public String[] sortIndex(List<Person> persons) {
		TreeSet<String> set = new TreeSet<String>();  
        for (Person person : persons) {  
        	String tmp = StringToPinyinHelper.getPinYinHeadChar(person.getName()).substring(0, 1);
        	m = p.matcher(tmp); 
        	if(m.matches()){
        		set.add(tmp);  
        	}else{
        		set.add("#");  
        	}
        }  
        String[] names = new String[persons.size() + set.size()];  
        int i = 0;  
        for (String string : set) {
            names[i] = string;  
            i++;  
        } 
		String[] pinYinNames = new String[persons.size()];
		for (int j = 0; j < persons.size(); j++) {
			persons.get(j).setPinYinName(StringToPinyinHelper.getPingYin(persons.get(j).getName().toString()));
			pinYinNames[j] = StringToPinyinHelper.getPingYin(persons.get(j).getName().toString());
		}
		System.arraycopy(pinYinNames, 0, names, set.size(), pinYinNames.length);  
		Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
		return names;
	}
	
	private List<Map<String, Object>> sortList(String[] allNames,List<Map<String, Object>> neighborlist,List<Object> objectneighbors) {
//		Loger.i("TEST", "allNames size==>"+allNames.length);
		newPersons = new ArrayList<Person>();
		List<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < allNames.length; i++) {
			if (allNames[i].length() != 1) {
				for (int j = 0; j < persons.size(); j++) {
					if (allNames[i].equals(persons.get(j).getPinYinName())) {
						if (!indexList.contains(persons.get(j).getPersonindex())) {
							indexList.add(persons.get(j).getPersonindex());
							Person p = new Person(persons.get(j).getName(), persons.get(j).getPinYinName(),
									persons.get(j).getPersonindex());
							newPersons.add(p);
						}
					}
				}
			}else{
				newPersons.add(new Person(allNames[i]));  
			}
		}
		NeighborActivity.indexSelector.clear();
        for (int j = 0; j < indexStr.length; j++) {
            for (int i = 0; i < newPersons.size(); i++) {  
                try {
					if (newPersons.get(i).getName().equals(indexStr[j])) {  
						NeighborActivity.indexSelector.put(indexStr[j], i);  
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }
        } 
        neighborlist.clear();
        for (int i = 0; i < newPersons.size(); i++) {
        	int index = newPersons.get(i).getPersonindex();
        	if(-1 == index){
        		neighborlist.add(setNeighborIndexWithMap(newPersons.get(i).getName()));
        	}else{
        		Neighbor neighbor = (Neighbor)(objectneighbors.get(index));
        		neighborlist.add(setNeighborsWithMap(neighbor));
        	}
		}
//        for(Person person:newPersons){
//        	int index = person.getPersonindex();
//        	if(-1 == index){
//        		neighborlist.add(setNeighborIndexWithMap(person.getName()));
//        	}else{
//        		Neighbor neighbor = (Neighbor)(objectneighbors.get(index));
//        		neighborlist.add(setNeighborsWithMap(neighbor));
//        	}
//        }
        return neighborlist;
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return false;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		EMChatManager.getInstance().registerEventListener(NeighborActivity.this,new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged });
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.nfs.youlin.find.FindFragment");
		filter.setPriority(999);
		registerReceiver(mBroadcastReceiver, filter);
		IntentFilter nfilter = new IntentFilter();
		nfilter.addAction("com.nfs.youlin.find.newmsg");
		nfilter.addAction("com.nfs.youlin.find.update");
		nfilter.addAction("com.nfs.youlin.find.get_neighbor");
		nfilter.setPriority(999);
		registerReceiver(sBroadcastReceiver, nfilter);
		if(PinyinActivity.sPinyinStatusBoolean == true){
			if(PinyinActivity.selectIndex==100){
				neighborListView.setSelection(neighborlist.size()-1);  
			}else{
				neighborListView.setSelection(PinyinActivity.selectIndex);
			}
			PinyinActivity.sPinyinStatusBoolean = false;
		}else{
			Loger.i("TEST", "没有刷新数ListView");
		}
		if(neighbor_chat.isVisible()){
			setnewMessageflag(getSysUnreadMsgCountTotal(),"1");
		}else {
			setnewMessageflag(getSysUnreadMsgCountTotal(),"0");
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		EMChatManager.getInstance().unregisterEventListener(this);
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
	
}
