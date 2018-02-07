package com.nfs.youlin.activity.titlebar.barter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
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

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.BaiduMapActivity;
import com.easemob.chatuidemo.adapter.ExpressionAdapter;
import com.easemob.chatuidemo.adapter.ExpressionPagerAdapter;
import com.easemob.chatuidemo.adapter.VoicePlayClickListener;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.chatuidemo.utils.SmileUtils;
import com.easemob.chatuidemo.widget.ExpandGridView;
import com.easemob.chatuidemo.widget.PasteEditText;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoActivity;
import com.nfs.youlin.activity.neighbor.SharePopWindow;
import com.nfs.youlin.activity.personal.CollectionActivity;
import com.nfs.youlin.activity.personal.MyPushActivity;
import com.nfs.youlin.adapter.CommentDetailAdapter;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Bimp;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.NoScrollGridView;
import com.nfs.youlin.utils.StatusChangeListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.TimeToStr;
import com.nfs.youlin.utils.XuanzhuanBitmap;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.CustomDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import cn.jpush.android.api.JPushInterface;

public class BarterDedailCommentActivity extends Activity implements OnClickListener,StatusChangeListener,OnGestureListener{
	private static final String TAG = "ChatActivity";
	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_FILE = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int REQUEST_CODE_SELECT_VIDEO = 23;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;
	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	public static final int RESULT_CODE_TO_CLOUD = 6;
	public static final int RESULT_CODE_EXIT_GROUP = 7;
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;
	private final int REQUEST_CODE_REPORT_DETAIL = 9527;
	public static final String COPY_IMAGE = "EASEMOBIMG";
	private final int SUCCESS_RESPONSE = 2001;
	private final int FAILED_RESPONSE  = 2002;
	private final int REFUSE_RESPONSE  = 2003;
	private final int SUCCESS_INIT_RESPONSE = 2004;
	private final int FAILED_INIT_RESPONSE = 2005;
	private final int REFUSE_INIT_RESPONSE = 2006;
	private final int SUCCESS_UP_RESPONSE = 2007;
	private final int FAILED_UP_RESPONSE = 2008;
	private final int REFUSE_UP_RESPONSE = 2009;
	private final int CONTENTED_RESPONSE = 2018;
	private final int SUCCESS_DOWN_RESPONSE = 2010;
	private final int FAILED_DOWN_RESPONSE = 2011;
	private final int REFUSE_DOWN_RESPONSE = 2012;
	private final int BLACK_RESPONSE = 2016;
	private final int NOTOPIC_RESPONSE = 2017;
	private final int PAGE_COUNT = 6;
	private final long relpayId = 0;
	private ProgressDialog pd;
	private Uri selectedImage;
	private boolean sendCommentStatus = false;
	private JSONArray responseComment;
	//private List<Bundle> listCommBundle;
	//private Bundle commBundle;
	private String sendContents;
	private String currCommentId;
	private long topCommentId=0;
	private long bottomCommentId=0;
	private LinearLayout rl_bottom;
	private PasteEditText mEditTextContent;
	private View buttonSetModeKeyboard;
	private View buttonSetModeVoice;
	private View buttonSend;
	private View buttonPressToSpeak;
	private ViewPager expressionViewpager;
	private LinearLayout emojiIconContainer;
	private LinearLayout btnContainer;
	private ImageView locationImgview;
	private View more;
	private int listposition;
	private int parentclass;  // 0 search  1 circlefragment
	private ClipboardManager clipboard;
	private InputMethodManager manager;
	private List<String> reslist;
	private Drawable[] micImages;
	private int chatType;
	private EMConversation conversation;
	// 给谁发送消息
	private String toChatUsername;
	private String toChatUsernick;
	private VoiceRecorder voiceRecorder;
	private File cameraFile;
	static int resendPos;
	private ImageView micImage;
	private View recordingContainer;
//	private ImageView iv_emoticons_normal;
//	private ImageView iv_emoticons_checked;
	private RelativeLayout edittext_layout;
	private boolean isloading;
	private final int pagesize = 20;
	private boolean haveMoreData = true;
	private Button btnMore;
	public String playMsgId;
	private SwipeRefreshLayout swipeRefreshLayout;
	private static TextView applydetailTv;
	private String flag = "none";
	private String responseString = null;
	private TextView signUpTv;
    private CommentDetailAdapter commentDetailAdapter;
    Uri uri=null;
    String picturePath=null;
    String path[];
    Bitmap resizeBitmap;
    Bitmap xuanzhuanBitmap;
    Bitmap resizeBitmapCamera;
    Bitmap xuanzhuanBitmapCamera;
    List<Map<String, Object>> commentList = new ArrayList<Map<String,Object>>();
    List<Map<String, Object>> commentList2 = new ArrayList<Map<String,Object>>();
	private ForumTopic forumTopicObj;
	private ForumtopicDaoDBImpl forumtopicDaoDBImplObj;
	private String isDelString;
	private String currenttotalcount;
	private String dflag = "none";
	private boolean cRequestSet = false;
	private String applyflag;
	private int REQUEST_FOR_APPLY = 301;
	private int REQUEST_APPLY_DETAIL = 302;
	private boolean cRequesttype = false;
	private String relpayUserIdFromComment = "0";
	Long id=-1L;
	int myposition=0;
	public static Boolean finishBool=false;
	ImageLoader imageLoader;
	int commentCountInt;
	LinearLayout barBottomCircleLayout;
	TextView haveNoMoreTv;
	Timer timer, timer2;
	int voicei = 10;
	Boolean voiceBool = true;
	private PowerManager.WakeLock wakeLock;
//  private ImageView voiceCallBtn;
//  private ImageView videoCallBtn;
	private Toast mToast = null; 
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			try {
				micImage.setImageDrawable(micImages[msg.what]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	private BroadcastReceiver sendMsgBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context content, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("com.nfs.youlin.replay")){
				String relpayStr = intent.getStringExtra("relayInfo");
				relpayUserIdFromComment = intent.getStringExtra("relayUserId");
				mEditTextContent.setFocusable(true);
				mEditTextContent.setFocusableInTouchMode(true);
				mEditTextContent.requestFocus();
				mEditTextContent.setText(relpayStr);
				mEditTextContent.setSelection(relpayStr.length());
				InputMethodManager inputManager = (InputMethodManager)mEditTextContent.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
		        inputManager.showSoftInput(mEditTextContent, 0);
			}
		}
	};
	private BroadcastReceiver delCommBroadcastReceiver =new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals("youlin.del.comm.action")){
				int type=intent.getIntExtra("type", 0);
				if(type==1){
					int position=intent.getIntExtra("position", 0);
					if(commentList.size()!=0){
						commentList.remove(position);
					}
				}else{
					Bundle commBundle=intent.getBundleExtra("delCommBundle");
					String newTopicId= commBundle.getString("topicId");
					String newCommentId=commBundle.getString("commentId");
					for (int i = 0; i < commentList.size(); i++) {
						//Loger.i("TEST","33333333333--->"+commentList.size()+" "+newTopicId+" "+topicId+" "+newCommentId+" "+(commentList.get(i).get("commId")));
						if(newTopicId.equals(String.valueOf(topicId)) && newCommentId.equals((commentList.get(i).get("commId"))) && Long.parseLong(commentList.get(i).get("senderId").toString())!=App.sUserLoginId){
							if(commentList.size()!=0){
								commentList.remove(i);
							}
						}
					}
				}
				
				commentDetailAdapter.notifyDataSetChanged();
				//Utility.setListViewHeightBasedOnChildren(reply_list);
				
			}
			
			
		
		}
	};

	Handler replyHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1001:
				barterProgress.setVisibility(View.GONE);
				myReply();
				break;
			case 1002:
				break;
			case 1003:
				mPullToRefreshListView.setAdapter(commentDetailAdapter);
				//Utility.setListViewHeightBasedOnChildren(reply_list);
				break;
			case 1004:
//				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				try {
//					inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				commentDetailAdapter=new CommentDetailAdapter(CircleDetailActivity.this, commentList,topicId);
//				reply_list.setAdapter(commentDetailAdapter);
				commentDetailAdapter.notifyDataSetChanged();
				//Utility.setListViewHeightBasedOnChildren(reply_list);
				break;
			default:
				break;
			}
			
		};
	};
	private Handler sendCommHandle = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case SUCCESS_RESPONSE:
				if(resizeBitmap!=null){
					if(!resizeBitmap.isRecycled()){
						resizeBitmap.recycle();
						//System.gc();
					}
				}
				if(xuanzhuanBitmap!=null){
					if(!xuanzhuanBitmap.isRecycled()){
						xuanzhuanBitmap.recycle();
						//System.gc();
					}
				}
				if(resizeBitmapCamera!=null){
					if(resizeBitmapCamera.isRecycled()){
						resizeBitmapCamera.recycle();
					}
				}
				if(xuanzhuanBitmapCamera!=null){
					if(xuanzhuanBitmapCamera.isRecycled()){
						xuanzhuanBitmapCamera.recycle();
					}
				}
				if(picturePath!=null){
					path=new String[]{picturePath};
					BarterDedailCommentActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA+" LIKE ?",path);
					picturePath=null;
				}
				responseComment = null;
				mEditTextContent.setText("");
				Loger.i("TEST", "pullDownShowComments()------------------->");
				pullDownShowComments();
//				RequestParams params = new RequestParams();
//				long topicId = ((ForumTopic)forumtopicLists.get(listposition)).getTopic_id();
//				params.put("topic_id", topicId);
//				params.put("comment_id", currCommentId);
//				params.put("count", PAGE_COUNT);
//				params.put("type", 1);
//				AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//				asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN_GET_COMMENT, params, 
//						new JsonHttpResponseHandler(){
//					public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//						int responseLen = response.length();
//						listCommBundle = null;
//						if(responseLen>0){
//							responseComment = response;
//							listCommBundle = new ArrayList<Bundle>();
//						}else{
//							responseComment = null;
//						}
//						Loger.i("TEST", "comment json obj length->"+responseLen);
//						try {
//							for (int i = 0; i < responseLen; i++) {
//								JSONObject jsonObject = new JSONObject(response.getString(i));
//								String contentType = jsonObject.getString("contentType");
//								String topicId = jsonObject.getString("topicId");
//								String commId = jsonObject.getString("commId");
//								String senderNcRoleId = jsonObject.getString("senderNcRoleId");
//								String senderId = jsonObject.getString("senderId");
//								commBundle = new Bundle();
//								commBundle.putString("contentType", contentType);
//								commBundle.putString("topicId", topicId);
//								commBundle.putString("commId", commId);
//								commBundle.putString("senderNcRoleId", senderNcRoleId);
//								commBundle.putString("senderId", senderId);
//								listCommBundle.add(commBundle);
//							}
//							topCommentId = Long.parseLong(listCommBundle.get(0)
//									.getString("commId"));
//							Loger.i("TEST", "发送的最上一个ID:"+topCommentId);
//							myReplyDown();
//							//liangzelei发布评论\
//							
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					};
//					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//						String flag = null;
//						try {
//							flag = response.getString("flag");
//							if("no".equals(flag)){
//								Loger.i("TEST", "回复没有没有最新评论");
//							}
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					};
//					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//						Loger.i("TEST", "GetNewCommErr->"+throwable.toString());
//					};
//				});
				Loger.i("TEST","发送成功");
				break;
			case FAILED_RESPONSE:
				if(resizeBitmap!=null){
					if(!resizeBitmap.isRecycled()){
						resizeBitmap.recycle();
						//System.gc();
					}
				}
				if(xuanzhuanBitmap!=null){
					if(!xuanzhuanBitmap.isRecycled()){
						xuanzhuanBitmap.recycle();
						//System.gc();
					}
				}
				if(resizeBitmapCamera!=null){
					if(resizeBitmapCamera.isRecycled()){
						resizeBitmapCamera.recycle();
					}
				}
				if(xuanzhuanBitmapCamera!=null){
					if(xuanzhuanBitmapCamera.isRecycled()){
						xuanzhuanBitmapCamera.recycle();
					}
				}
				if(picturePath!=null){
					path=new String[]{picturePath};
					BarterDedailCommentActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA+" LIKE ?",path);
					picturePath=null;
				}
				Toast.makeText(BarterDedailCommentActivity.this, "地址未审核通过不能回复", 0).show();
				break;
			case BLACK_RESPONSE:
				if(resizeBitmap!=null){
					if(!resizeBitmap.isRecycled()){
						resizeBitmap.recycle();
						//System.gc();
					}
				}
				if(xuanzhuanBitmap!=null){
					if(!xuanzhuanBitmap.isRecycled()){
						xuanzhuanBitmap.recycle();
						//System.gc();
					}
				}
				if(resizeBitmapCamera!=null){
					if(resizeBitmapCamera.isRecycled()){
						resizeBitmapCamera.recycle();
					}
				}
				if(xuanzhuanBitmapCamera!=null){
					if(xuanzhuanBitmapCamera.isRecycled()){
						xuanzhuanBitmapCamera.recycle();
					}
				}
				if(picturePath!=null){
					path=new String[]{picturePath};
					BarterDedailCommentActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA+" LIKE ?",path);
					picturePath=null;
				}
				Toast.makeText(BarterDedailCommentActivity.this, "你不在对方邻居列表中", 0).show();
				break;
			case CONTENTED_RESPONSE:
				if(resizeBitmap!=null){
					if(!resizeBitmap.isRecycled()){
						resizeBitmap.recycle();
						//System.gc();
					}
				}
				if(xuanzhuanBitmap!=null){
					if(!xuanzhuanBitmap.isRecycled()){
						xuanzhuanBitmap.recycle();
						//System.gc();
					}
				}
				if(resizeBitmapCamera!=null){
					if(resizeBitmapCamera.isRecycled()){
						resizeBitmapCamera.recycle();
					}
				}
				if(xuanzhuanBitmapCamera!=null){
					if(xuanzhuanBitmapCamera.isRecycled()){
						xuanzhuanBitmapCamera.recycle();
					}
				}
				if(picturePath!=null){
					path=new String[]{picturePath};
					BarterDedailCommentActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA+" LIKE ?",path);
					picturePath=null;
				}
				Toast.makeText(BarterDedailCommentActivity.this, "休息下手指，一会儿再发！", 0).show();
			case NOTOPIC_RESPONSE:
				if(resizeBitmap!=null){
					if(!resizeBitmap.isRecycled()){
						resizeBitmap.recycle();
						//System.gc();
					}
				}
				if(xuanzhuanBitmap!=null){
					if(!xuanzhuanBitmap.isRecycled()){
						xuanzhuanBitmap.recycle();
						//System.gc();
					}
				}
				if(resizeBitmapCamera!=null){
					if(resizeBitmapCamera.isRecycled()){
						resizeBitmapCamera.recycle();
					}
				}
				if(xuanzhuanBitmapCamera!=null){
					if(xuanzhuanBitmapCamera.isRecycled()){
						xuanzhuanBitmapCamera.recycle();
					}
				}
				if(picturePath!=null){
					path=new String[]{picturePath};
					BarterDedailCommentActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA+" LIKE ?",path);
					picturePath=null;
				}
				Toast.makeText(BarterDedailCommentActivity.this, "该帖子已被删除", 0).show();
				break;
			case REFUSE_RESPONSE:
				if(resizeBitmap!=null){
					if(!resizeBitmap.isRecycled()){
						resizeBitmap.recycle();
						//System.gc();
					}
				}
				if(xuanzhuanBitmap!=null){
					if(!xuanzhuanBitmap.isRecycled()){
						xuanzhuanBitmap.recycle();
						//System.gc();
					}
				}
				if(resizeBitmapCamera!=null){
					if(resizeBitmapCamera.isRecycled()){
						resizeBitmapCamera.recycle();
					}
				}
				if(xuanzhuanBitmapCamera!=null){
					if(xuanzhuanBitmapCamera.isRecycled()){
						xuanzhuanBitmapCamera.recycle();
					}
				}
				if(picturePath!=null){
					path=new String[]{picturePath};
					BarterDedailCommentActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA+" LIKE ?",path);
					picturePath=null;
				}
				Toast.makeText(BarterDedailCommentActivity.this, "网络有问题", 0).show();
				break;
			case SUCCESS_INIT_RESPONSE:
				new Thread(new Runnable() {
				@Override
					public void run() {
						// TODO Auto-generated method stub
						Message msg=new Message();
						msg.what=1001;
						replyHandler.sendMessage(msg);
					}
				}).start();
				break;
			case SUCCESS_UP_RESPONSE:
				myReplyUp();
				break;
			case SUCCESS_DOWN_RESPONSE:
				myReplyDown();
				
				break;
			case FAILED_INIT_RESPONSE:
			case FAILED_UP_RESPONSE:
			case FAILED_DOWN_RESPONSE:
				break;
			case REFUSE_INIT_RESPONSE:
			case REFUSE_UP_RESPONSE:
			case REFUSE_DOWN_RESPONSE:
				break; 
			default:
				break;
			}
		};
	};
	public EMGroup group;
	public EMChatRoom room;
	public boolean isRobot;
	Bimp bimp;
	private StatusChangeutils statusutils;
	Boolean firstBool;
	long topicId;
	PullToRefreshListView mPullToRefreshListView;
	ProgressBar barterProgress;
	GestureDetector detector;
	Timer init_Timer;
	TextView recordingHint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		detector=new GestureDetector(BarterDedailCommentActivity.this, this);
		setContentView(R.layout.activity_barter_detail_comment);
		imageLoader = ImageLoader.getInstance();
		Intent intent=getIntent();
		topicId=intent.getLongExtra("topic_id", 0L);
		statusutils = new StatusChangeutils();
		statusutils.statuschangelistener("HUIFU", this);
		bimp = new Bimp();
		LinearLayout rl_bottom = (LinearLayout) findViewById(R.id.rl_bottom_circle);  // hyy
		rl_bottom.setVisibility(View.VISIBLE);
		barterProgress=(ProgressBar)findViewById(R.id.barter_progress);
		haveNoMoreTv=(TextView)findViewById(R.id.have_no_more_tv);
		barBottomCircleLayout = (LinearLayout)findViewById(R.id.bar_bottom_circle);
		recordingContainer = findViewById(R.id.recording_container_circle);
		recordingHint = (TextView) findViewById(R.id.recording_hint_circle);
		mEditTextContent = (PasteEditText) findViewById(R.id.et_sendmessage_circle);
		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard_circle);
		micImage = (ImageView) findViewById(R.id.mic_image_circle);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout_circle);
		buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice_circle);
		buttonSend = findViewById(R.id.btn_send_circle);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak_circle);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager_circle);
		emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container_circle);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container_circle);
		locationImgview = (ImageView) findViewById(R.id.btn_location_circle);
		btnMore = (Button) findViewById(R.id.btn_more_circle);
		more = findViewById(R.id.more_circle);
		edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
		mEditTextContent.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
					String content = mEditTextContent.getText().toString();
					int nCount = content.indexOf(":");
					int nStringLen = content.length()-1;
					try {
						if ("回复".equals(content.substring(0,2))
								&& (nCount == nStringLen)&&content.substring(nStringLen, nStringLen+1).equals(":")) {
							mEditTextContent.setText("");
							relpayUserIdFromComment="0";
						}
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
				return false;
			}

		});
		mEditTextContent.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});	
		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };

		// 表情list
		reslist = getExpressionRes(35);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		views.add(gv1);
		views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		edittext_layout.requestFocus();
		voiceRecorder = new VoiceRecorder(micImageHandler);
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}

			}
		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				more.setVisibility(View.GONE);
//				iv_emoticons_normal.setVisibility(View.VISIBLE);
//				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE);
			}
		});
		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		mPullToRefreshListView=(PullToRefreshListView)findViewById(R.id.barter_reply_list);
		mPullToRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				if(refreshView.isShownFooter()){
					mPullToRefreshListView.setEnabled(false);
					new GetDataTask1().execute();
				}
			}
		});
		InitShowComments();
		init_Timer=new Timer();
        init_Timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(commentList.size()==0 && !BarterDedailActivity.liuYanTv.getText().toString().equals("0")){
					Loger.i("youlin", "liuyahbjidfsghnming");
					InitShowCommentsTimer();
				}else{
					init_Timer.cancel();
				}
			}
		}, 10000,10000);
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return super.dispatchKeyEvent(event);
	}
	public void myReply(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
					commentDetailAdapter=new CommentDetailAdapter(BarterDedailCommentActivity.this, commentList,topicId,listposition,parentclass);
					Message msg=new Message();
					msg.what=1003;
					replyHandler.sendMessage(msg);
			}
		}).start();
		
	}
	
	public void myReplyDown(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg=new Message();
				msg.what=1004;
				replyHandler.sendMessage(msg);
			}
		}).start();
		
	}

	public void myReplyUp(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
					Message msg=new Message();
					msg.what=1004;
					replyHandler.sendMessage(msg);
			}
		}).start();
		
	}
	

	/** 
	02.     * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如: 
	03.     *  
	04.     * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ; 
	05.     *  
	06.     * B.本地路径:url="file://mnt/sdcard/photo/image.png"; 
	07.     *  
	08.     * C.支持的图片格式 ,png, jpg,bmp,gif等等 
	09.     *  
	10.     * @param url 
	11.     * @return 
	12.     */  
	    public static Bitmap GetLocalOrNetBitmap(String url)  
	    {  
	        Bitmap bitmap = null;  
	        InputStream in = null;  
	        BufferedOutputStream out = null;  
	        try  
	        {  
	            in = new BufferedInputStream(new URL(url).openStream(), 2*1024);  
	            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();  
	            out = new BufferedOutputStream(dataStream, 2*1024);  
	            copy(in, out);  
	            out.flush();  
	            byte[] data = dataStream.toByteArray();  
	            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  
	            data = null;  
	            return bitmap;  
	        }  
	        catch (IOException e)  
	        {  
	            e.printStackTrace();  
	            return null;  
	        }  
	    }
	private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[2*1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CODE_EXIT_GROUP) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
			switch (resultCode) {
			case RESULT_CODE_COPY: // 复制消息

				break;
			case RESULT_CODE_DELETE: // 删除消息

				break;

			case RESULT_CODE_FORWARD: // 转发消息
				
				break;

			default:
				break;
			}
		}
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == REQUEST_CODE_EMPTY_HISTORY) {
				// 清空会话
				EMChatManager.getInstance().clearConversation(toChatUsername);
			} else if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists()){
					sendPicByUriCamera(cameraFile);
					//sendPicture(cameraFile.getAbsolutePath());
				}
					
			} else if (requestCode == REQUEST_CODE_SELECT_VIDEO) { // 发送本地选择的视频

				int duration = data.getIntExtra("dur", 0);
				String videoPath = data.getStringExtra("path");
				File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
				Bitmap bitmap = null;
				FileOutputStream fos = null;
				try {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
					if (bitmap == null) {
						EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
						bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
					}
					fos = new FileOutputStream(file);

					bitmap.compress(CompressFormat.JPEG, 100, fos);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fos = null;
					}
					if (bitmap != null) {
						bitmap.recycle();
						bitmap = null;
					}

				}
				sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);
			} else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
				if (data != null) {
					selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
					
				}
			} else if (requestCode == REQUEST_CODE_SELECT_FILE) { // 发送选择的文件
				if (data != null) {
					Uri uri = data.getData();
					if (uri != null) {
						sendFile(uri);
					}
				}

			} else if (requestCode == REQUEST_CODE_MAP) { // 地图
			    Loger.d("hyytest","activityresult->REQUEST_CODE_MAP");
				double latitude = data.getDoubleExtra("latitude", 0);
				double longitude = data.getDoubleExtra("longitude", 0);
				String locationAddress = data.getStringExtra("address");
				Loger.d("hyytest","latitude="+latitude);
				Loger.d("hyytest","longitude="+longitude);
				Loger.d("hyytest","locationAddresss="+locationAddress);
				if (locationAddress != null && !locationAddress.equals("")) {
				    toggleMore(more);
//					sendLocationMsg(latitude, longitude, "", locationAddress);asdsad
				    sendText(locationAddress);
				} else {
					String st = getResources().getString(R.string.unable_to_get_loaction);
					Toast.makeText(this, st, 0).show();
				}
				// 重发消息
			} else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
					|| requestCode == REQUEST_CODE_PICTURE || requestCode == REQUEST_CODE_LOCATION
					|| requestCode == REQUEST_CODE_VIDEO || requestCode == REQUEST_CODE_FILE) {
				resendMessage();
			} else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
				// 粘贴
				if (!TextUtils.isEmpty(clipboard.getText())) {
					String pasteText = clipboard.getText().toString();
					if (pasteText.startsWith(COPY_IMAGE)) {
						// 把图片前缀去掉，还原成正常的path
						sendPicture(pasteText.replace(COPY_IMAGE, ""));
					}

				}
			} else if (requestCode == REQUEST_CODE_ADD_TO_BLACKLIST) { // 移入黑名单

			} else if(requestCode == REQUEST_FOR_APPLY){
				currenttotalcount = data.getStringExtra("totalcount");
				signUpTv.setText("取消报名");
				applydetailTv.setText(currenttotalcount);
				applyflag = "true";
			}else if(requestCode ==REQUEST_APPLY_DETAIL){
				currenttotalcount = data.getStringExtra("totalcount");
				signUpTv.setText("报名详情");
				applydetailTv.setText(currenttotalcount);	
			}
//			else if (conversation.getMsgCount() > 0) {
//				setResult(RESULT_OK);
//			} else if (requestCode == REQUEST_CODE_GROUP_DETAIL) {
//			}
			
		}
	}
	/**
	 * 消息图标点击事件
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		AllFamilyDaoDBImpl curfamilyDaoDBImpl = new AllFamilyDaoDBImpl(BarterDedailCommentActivity.this);
		if (curfamilyDaoDBImpl.getCurrentAddrDetail("132").getEntity_type() == 1) {
			String st1 = getResources().getString(R.string.not_connect_to_server);
			int id = view.getId();
			if (id == R.id.btn_send_circle) {// 点击发送按钮(发文字和表情)
				String s = mEditTextContent.getText().toString();
				Loger.i("TEST", "开始想服务器发送回复");
				sendText(s);
			} else if (id == R.id.btn_take_picture_circle) {
				selectPicFromCamera();// 点击照相图标
			} else if (id == R.id.btn_picture_circle) {
				selectPicFromLocal(); // 点击图片图标
			} else if (id == R.id.btn_location_circle) { // 位置
				Loger.d("hyytest", "start baidumap");
				startActivityForResult(new Intent(this, BaiduMapActivity.class), REQUEST_CODE_MAP);
			}
		} else {
			Toast.makeText(BarterDedailCommentActivity.this, "您的地址还未经过验证", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			String st = getResources().getString(R.string.sd_card_does_not_exist);
			Toast.makeText(getApplicationContext(), st, 0).show();
			return;
		}

		cameraFile = new File(PathUtil.getInstance().getImagePath(), DemoApplication.getInstance().getUserName()
				+ System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		//Loger.i("TEST","111111111111111111111111111111------->"+PathUtil.getInstance().getImagePath()+" "+DemoApplication.getInstance().getUserName());
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				REQUEST_CODE_CAMERA);
	}

	/**
	 * 选择文件
	 */
	private void selectFileFromLocal() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}

	/**
	 * 发送文本消息
	 * 
	 * @param content
	 *            message content
	 * @param isResend
	 *            boolean resend
	 */
	public void sendText(String content) {
		if(!NetworkService.networkBool){
			return;
		}
		pd = new ProgressDialog(BarterDedailCommentActivity.this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pd.setMessage("发送中...");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.show();
					}
				});
			}
		}).start();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					pd.dismiss();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 10000);
		RequestParams params = new RequestParams();
	
		long senderId = App.sUserLoginId;
		params.put("topic_id", topicId);
		params.put("sender_id", senderId);
		try {
			params.put("community_id",App.sFamilyCommunityId);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			params.put("community_id",0);
			e1.printStackTrace();
		}
		params.put("sendTime", System.currentTimeMillis());
		params.put("content", content);
		params.put("contentType", 0);
		params.put("tag", "addcomm");
		params.put("apitype", IHttpRequestUtils.APITYPE[5]);
		if(!"0".equals(relpayUserIdFromComment)){
			params.put("replay_user_id", relpayUserIdFromComment);
			relpayUserIdFromComment = "0";
		}
		Loger.i("TEST", ".............................");
		Loger.i("TEST", "topic_id->"+topicId);
		Loger.i("TEST", "sender_id->"+senderId);
		Loger.i("TEST", "sendTime->"+1010110011L);
		Loger.i("TEST", "content->"+content);
		Loger.i("TEST", "contentType->"+0);
		Loger.i("TEST", ".............................");
//		if(selectedImage!=null || cameraFile != null){
//			File imageFile = null;
//			if(selectedImage!=null){
//				Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
//				String st8 = getResources().getString(R.string.cant_find_pictures);
//				if (cursor != null) {
//					cursor.moveToFirst();
//					int columnIndex = cursor.getColumnIndex("_data");
//					String picturePath = cursor.getString(columnIndex);
//					cursor.close();
//					cursor = null;
//
//					if (picturePath == null || picturePath.equals("null")) {
//						Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
//						toast.setGravity(Gravity.CENTER, 0, 0);
//						toast.show();
//						return;
//					}
//					imageFile = new File(picturePath);
//					Loger.i("TEST", "当前上传的本地图片地址:"+picturePath);
//				} else {
//					File file = new File(selectedImage.getPath());
//					if (!file.exists()) {
//						Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
//						toast.setGravity(Gravity.CENTER, 0, 0);
//						toast.show();
//						return;
//
//					}
//					imageFile = file;
////					sendPicture(file.getAbsolutePath());
//					Loger.i("TEST", "当前上传的本地图片地址:"+file.getAbsolutePath());
//				}
//			}else{
//				imageFile = new File(cameraFile.getAbsolutePath());	
//			}
//			params.put("contentType", 1); //发送照片
//			try {
//				params.put("image", imageFile); //发送照片
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}else{
//			 //发送照片
//		}

		sendContents = content;
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				String flag = null;
				Message msg = new Message();
				try {
					flag = response.getString("flag");
					Loger.i("youlin", "返回当前评论ID2222222222222222222222->"+response.toString());
					if("ok".equals(flag)){
						msg.what = SUCCESS_RESPONSE;
						currCommentId = response.getString("commentId");
						selectedImage = null;
						cameraFile = null;
						
					}else if("full".equals(flag)){
						msg.what = CONTENTED_RESPONSE;
					}else if("black".equals(flag)){
						msg.what = BLACK_RESPONSE;
					}else if("none".equals(flag)){
						msg.what = NOTOPIC_RESPONSE;
					}else{
						msg.what = FAILED_RESPONSE;
					}
					sendCommHandle.sendMessage(msg);
					sendCommentStatus = true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "SendCommErr:"+e.getMessage());
					Loger.i("youlin","123");
					msg.what = REFUSE_RESPONSE;
					sendCommHandle.sendMessage(msg);
					sendCommentStatus = true;
					e.printStackTrace();
				}
				pd.dismiss();
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = REFUSE_RESPONSE;
				new ErrorServer(BarterDedailCommentActivity.this, responseString);
				pd.dismiss();
				sendCommHandle.sendMessage(msg);
				sendCommentStatus = true;
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}

	/**
	 * 发送语音
	 * 
	 * @param filePath
	 * @param fileName
	 * @param length
	 * @param isResend
	 */
	private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
		if(!NetworkService.networkBool){
			return;
		}
		if (!(new File(filePath).exists())) {
			return;
		}
		Loger.i("TEST","filePath:"+filePath+"\r\n"+"fileName:"+fileName+"\r\n"+"length:"+length);
		sendContents = null;
		pd = new ProgressDialog(BarterDedailCommentActivity.this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pd.setMessage("发送中...");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.show();
					}
				});
			}
		}).start();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					pd.dismiss();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 10000);
		RequestParams params = new RequestParams();
		long senderId = App.sUserLoginId;
		params.put("topic_id", topicId);
		params.put("sender_id", senderId);
		try {
			params.put("community_id",App.sFamilyCommunityId);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			params.put("community_id",0);
			e1.printStackTrace();
		}
		params.put("sendTime", System.currentTimeMillis());
		File vodicFile = null;
		vodicFile = new File(filePath);	
		params.put("contentType", 2); //发送声音
		try {
			params.put("video", vodicFile); // 发送声音
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Loger.i("TEST", "当前TopicId->"+topicId);
		params.put("video_length", length);
		params.put("tag", "addcomm");
		params.put("apitype", IHttpRequestUtils.APITYPE[5]);
		if(!"0".equals(relpayUserIdFromComment)){
			params.put("replay_user_id", relpayUserIdFromComment);
			relpayUserIdFromComment = "0";
		}
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				String flag = null;
				Message msg = new Message();
				try {
					flag = response.getString("flag");
					if("ok".equals(flag)){
						msg.what = SUCCESS_RESPONSE;
						currCommentId = response.getString("commentId");
						selectedImage = null;
						cameraFile = null;
						Loger.i("TEST", "返回当前评论ID->"+currCommentId);
					}else if("full".equals(flag)){
						msg.what = CONTENTED_RESPONSE;
					}else if("black".equals(flag)){
						msg.what = BLACK_RESPONSE;
					}else if("none".equals(flag)){
						msg.what = NOTOPIC_RESPONSE;
					}else{
						msg.what = FAILED_RESPONSE;
					}
					sendCommHandle.sendMessage(msg);
					sendCommentStatus = true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "SendCommErr:"+e.getMessage());
					msg.what = REFUSE_RESPONSE;
					Loger.i("youlin","789");
					sendCommHandle.sendMessage(msg);
					sendCommentStatus = true;
					e.printStackTrace();
				}
				pd.dismiss();
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = REFUSE_RESPONSE;
				pd.dismiss();
				sendCommHandle.sendMessage(msg);
				sendCommentStatus = true;
				new ErrorServer(BarterDedailCommentActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		
//		try {
//			final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
//			// 如果是群聊，设置chattype,默认是单聊
//			if (chatType == CHATTYPE_GROUP){
//				message.setChatType(ChatType.GroupChat);
//				}else if(chatType == CHATTYPE_CHATROOM){
//				    message.setChatType(ChatType.ChatRoom);
//				}
//			message.setReceipt(toChatUsername);
//			int len = Integer.parseInt(length);
//			VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
//			message.addBody(body);
//			if(isRobot){
//				message.setAttribute("em_robot_message", true);
//			}
//			conversation.addMessage(message);
//
//			setResult(RESULT_OK);
//			// send file
//			// sendVoiceSub(filePath, fileName, message);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 发送图片
	 * 
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		if(!NetworkService.networkBool){
			return;
		}
		String to = toChatUsername;
		// create and add image message in view
		final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP){
			message.setChatType(ChatType.GroupChat);
		}else if(chatType == CHATTYPE_CHATROOM){
		    message.setChatType(ChatType.ChatRoom);
		}

		message.setReceipt(to);
		ImageMessageBody body = new ImageMessageBody(new File(filePath));
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);
		message.addBody(body);
		if(isRobot){
			message.setAttribute("em_robot_message", true);
		}
		conversation.addMessage(message);


		setResult(RESULT_OK);
		// more(more);
	}

	/**
	 * 发送视频消息
	 */
	private void sendVideo(final String filePath, final String thumbPath, final int length) {
		if(!NetworkService.networkBool){
			return;
		}
		final File videoFile = new File(filePath);
		if (!videoFile.exists()) {
			return;
		}
		try {
			EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
			// 如果是群聊，设置chattype,默认是单聊
			if (chatType == CHATTYPE_GROUP){
				message.setChatType(ChatType.GroupChat);
			}else if(chatType == CHATTYPE_CHATROOM){
			    message.setChatType(ChatType.ChatRoom);
			}
			String to = toChatUsername;
			message.setReceipt(to);
			VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
			message.addBody(body);
			if(isRobot){
				message.setAttribute("em_robot_message", true);
			}
			conversation.addMessage(message);

			setResult(RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		if(!NetworkService.networkBool){
			return;
		}
		if(NetworkService.networkBool){
		 //String[] filePathColumn = { MediaStore.Images.Media.DATA };
		File imageFile = null;
		pd = new ProgressDialog(BarterDedailCommentActivity.this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pd.setMessage("发送中...");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.show();
					}
				});
			}
		}).start();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					pd.dismiss();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 10000);
		RequestParams params = new RequestParams();
		long senderId = App.sUserLoginId;
		params.put("topic_id", topicId);
		params.put("sender_id", senderId);
		try {
			params.put("community_id",App.sFamilyCommunityId);
		} catch (Exception e3) {
			// TODO Auto-generated catch block
			params.put("community_id",0);
			e3.printStackTrace();
		}
		params.put("sendTime", System.currentTimeMillis());
		String []pro={MediaStore.Images.Media.DATA}; 
			try {
				Cursor cursor1 = getContentResolver().query(selectedImage, pro,null, null, null);
				if (cursor1 != null) {
					cursor1.moveToFirst();
					int columnIndex = cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					String path = cursor1.getString(columnIndex);
					resizeBitmap = bimp.revitionImageSize(path);
					XuanzhuanBitmap myBitmap=new XuanzhuanBitmap();
					int degree = myBitmap.readPictureDegree(path);
					xuanzhuanBitmap = myBitmap.rotaingImageView(degree,resizeBitmap);
					uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), xuanzhuanBitmap, null, null));
				}else{
					Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
					uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
				}
			} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		Cursor cursor = getContentResolver().query(uri, pro, null, null, null);
		String st8 = getResources().getString(R.string.cant_find_pictures);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			Loger.i("TEST", "内容提供者:"+picturePath);
			imageFile = new File(picturePath);
			
			//sendPicture(picturePath);
			
		} else {
			
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			imageFile = file;
			//sendPicture(file.getAbsolutePath());
		}
		try {
			Loger.i("TEST","直接获取文件绝对路径->"+imageFile.getAbsolutePath()+"\r\n"+"从内容提供者获取路径->"+picturePath);
			params.put("image", imageFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			new ErrorServer(BarterDedailCommentActivity.this, responseString);
			e1.printStackTrace();
		}
		params.put("contentType", 1);
//		Loger.i("TEST", "当前TopicId->"+topicId);
//		Loger.i("TEST", "当前内容->"+content);
		String content = mEditTextContent.getText().toString();
//		params.put("content", content);
		params.put("tag", "addcomm");
		params.put("apitype", IHttpRequestUtils.APITYPE[5]);
		if(!"0".equals(relpayUserIdFromComment)){
			params.put("replay_user_id", relpayUserIdFromComment);
			relpayUserIdFromComment = "0";
		}
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				String flag = null;
				Message msg = new Message();
				try {
					flag = response.getString("flag");
					if("ok".equals(flag)){
						msg.what = SUCCESS_RESPONSE;
						currCommentId = response.getString("commentId");
//						selectedImage = null;
//						cameraFile = null;
						Loger.i("TEST", "返回当前评论ID->"+currCommentId);
					}else if("full".equals(flag)){
						msg.what = CONTENTED_RESPONSE;
					}else if("black".equals(flag)){
						msg.what = BLACK_RESPONSE;
					}else if("none".equals(flag)){
						msg.what = NOTOPIC_RESPONSE;
					}else{
						msg.what = FAILED_RESPONSE;
					}
					sendCommHandle.sendMessage(msg);
					sendCommentStatus = true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "SendCommErr:"+e.getMessage());
					if(picturePath!=null){
						path=new String[]{picturePath};
						BarterDedailCommentActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA+" LIKE ?",path);
						picturePath=null;
					}
					msg.what = REFUSE_RESPONSE;
					new ErrorServer(BarterDedailCommentActivity.this, responseString);
					
					sendCommHandle.sendMessage(msg);
					sendCommentStatus = true;
					e.printStackTrace();
				}
				pd.dismiss();
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				Loger.i("TEST", "SendCommErr:" + responseString);
				if(picturePath!=null){
					path=new String[]{picturePath};
					BarterDedailCommentActivity.this.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA+" LIKE ?",path);
					picturePath=null;
				}
				new ErrorServer(BarterDedailCommentActivity.this, responseString);
				Message msg = new Message();
				msg.what = REFUSE_RESPONSE;
				Loger.i("youlin","234");
				pd.dismiss();
				sendCommHandle.sendMessage(msg);
				sendCommentStatus = true;
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
		}else{
			Toast toast = Toast.makeText(this, "请先开启网络", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		
	}
	
	private void sendPicByUriCamera(File cameraFile) {
		 //String[] filePathColumn = { MediaStore.Images.Media.DATA };
		pd = new ProgressDialog(BarterDedailCommentActivity.this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pd.setMessage("发送中...");
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.show();
					}
				});
			}
		}).start();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					pd.dismiss();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 10000);
		RequestParams params = new RequestParams();
		long senderId = App.sUserLoginId;
		params.put("topic_id", topicId);
		params.put("sender_id", senderId);
		try {
			params.put("community_id",App.sFamilyCommunityId);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			params.put("community_id",0);
			e2.printStackTrace();
		}
		params.put("sendTime", System.currentTimeMillis());
		try {
			try {
				resizeBitmapCamera = bimp.revitionImageSize(cameraFile.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			XuanzhuanBitmap xuanzhuanBitmap=new XuanzhuanBitmap();
			int degree = xuanzhuanBitmap.readPictureDegree(cameraFile.getAbsolutePath());
			xuanzhuanBitmapCamera = xuanzhuanBitmap.rotaingImageView(degree,resizeBitmapCamera);
			Uri uri=Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), xuanzhuanBitmapCamera, null, null));
			String [] pro={MediaStore.Images.Media.DATA}; 
			Cursor cursor=getContentResolver().query(uri, pro, null, null, null);
			if(cursor!=null){
				cursor.moveToFirst();
				int columnIndex=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				String filePath=cursor.getString(columnIndex);
				File file=new File(filePath);
				params.put("image", file);
			}else{
				params.put("image", cameraFile);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		params.put("contentType", 1);
//		Loger.i("TEST", "当前TopicId->"+topicId);
//		Loger.i("TEST", "当前内容->"+content);
		String content = mEditTextContent.getText().toString();
//		params.put("content", content);
		params.put("tag", "addcomm");
		params.put("apitype", IHttpRequestUtils.APITYPE[5]);
		if(!"0".equals(relpayUserIdFromComment)){
			params.put("replay_user_id", relpayUserIdFromComment);
			relpayUserIdFromComment = "0";
		}
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				String flag = null;
				Message msg = new Message();
				try {
					flag = response.getString("flag");
					if("ok".equals(flag)){
						msg.what = SUCCESS_RESPONSE;
						currCommentId = response.getString("commentId");
//						selectedImage = null;
//						cameraFile = null;
						Loger.i("TEST", "返回当前评论ID->"+currCommentId);
					}else if("full".equals(flag)){
						msg.what = CONTENTED_RESPONSE;
					}else if("black".equals(flag)){
						msg.what = BLACK_RESPONSE;
					}else if("none".equals(flag)){
						msg.what = NOTOPIC_RESPONSE;
					}else{
						msg.what = FAILED_RESPONSE;
					}
					sendCommHandle.sendMessage(msg);
					sendCommentStatus = true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST", "SendCommErr:"+e.getMessage());
					msg.what = REFUSE_RESPONSE;
					Loger.i("youlin","567");
					sendCommHandle.sendMessage(msg);
					sendCommentStatus = true;
					e.printStackTrace();
				}
				pd.dismiss();
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = REFUSE_RESPONSE;
				new ErrorServer(BarterDedailCommentActivity.this, responseString);
				pd.dismiss();
				sendCommHandle.sendMessage(msg);
				sendCommentStatus = true;
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	/**
	 * 发送位置信息
	 * 
	 * @param latitude
	 * @param longitude
	 * @param imagePath
	 * @param locationAddress
	 */
	private void sendLocationMsg(double latitude, double longitude, String imagePath, String locationAddress) {
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.LOCATION);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP){
			message.setChatType(ChatType.GroupChat);
		}else if(chatType == CHATTYPE_CHATROOM){
		    message.setChatType(ChatType.ChatRoom);
		}
		LocationMessageBody locBody = new LocationMessageBody(locationAddress, latitude, longitude);
		message.addBody(locBody);
		message.setReceipt(toChatUsername);
		if(isRobot){
			message.setAttribute("em_robot_message", true);
		}
		conversation.addMessage(message);

		setResult(RESULT_OK);

	}

	/**
	 * 发送文件
	 * 
	 * @param uri
	 */
	private void sendFile(Uri uri) {
		String filePath = null;
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					filePath = cursor.getString(column_index);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			filePath = uri.getPath();
		}
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			String st7 = getResources().getString(R.string.File_does_not_exist);
			Toast.makeText(getApplicationContext(), st7, 0).show();
			return;
		}
		if (file.length() > 10 * 1024 * 1024) {
			String st6 = getResources().getString(R.string.The_file_is_not_greater_than_10_m);
			Toast.makeText(getApplicationContext(), st6, 0).show();
			return;
		}

		// 创建一个文件消息
		EMMessage message = EMMessage.createSendMessage(EMMessage.Type.FILE);
		// 如果是群聊，设置chattype,默认是单聊
		if (chatType == CHATTYPE_GROUP){
			message.setChatType(ChatType.GroupChat);
		}else if(chatType == CHATTYPE_CHATROOM){
		    message.setChatType(ChatType.ChatRoom);
		}

		message.setReceipt(toChatUsername);
		// add message body
		NormalFileMessageBody body = new NormalFileMessageBody(new File(filePath));
		message.addBody(body);
		if(isRobot){
			message.setAttribute("em_robot_message", true);
		}
		conversation.addMessage(message);

		setResult(RESULT_OK);
	}

	/**
	 * 重发消息
	 */
	private void resendMessage() {
		EMMessage msg = null;
		msg = conversation.getMessage(resendPos);
		// msg.setBackSend(true);
		msg.status = EMMessage.Status.CREATE;

	}

	/**
	 * 显示语音图标按钮
	 * 
	 * @param view
	 */
	public void setModeVoice(View view) {
		hideKeyboard();
		edittext_layout.setVisibility(View.GONE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeKeyboard.setVisibility(View.VISIBLE);
		buttonSend.setVisibility(View.GONE);
		btnMore.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.VISIBLE);
//		iv_emoticons_normal.setVisibility(View.VISIBLE);
//		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		btnContainer.setVisibility(View.VISIBLE);
		emojiIconContainer.setVisibility(View.GONE);

	}

	/**
	 * 显示键盘图标
	 * 
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		// mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener()
		// {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if(hasFocus){
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		// }
		// }
		// });
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		buttonSetModeVoice.setVisibility(View.VISIBLE);
		// mEditTextContent.setVisibility(View.VISIBLE);
		mEditTextContent.requestFocus();
		// buttonSend.setVisibility(View.VISIBLE);
		buttonPressToSpeak.setVisibility(View.GONE);
		if (TextUtils.isEmpty(mEditTextContent.getText())) {
			btnMore.setVisibility(View.VISIBLE);
			buttonSend.setVisibility(View.GONE);
		} else {
			btnMore.setVisibility(View.GONE);
			buttonSend.setVisibility(View.VISIBLE);
		}

	}

	
	/**
	 * 显示或隐藏图标按钮页
	 * 
	 * @param view
	 */
	public void toggleMore(View view) {
		if (more.getVisibility() == View.GONE) {
			EMLog.d(TAG, "more gone");
			hideKeyboard();
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
		} else {
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
//				iv_emoticons_normal.setVisibility(View.VISIBLE);
//				iv_emoticons_checked.setVisibility(View.INVISIBLE);
			} else {
				more.setVisibility(View.GONE);
			}

		}

	}
	/**
	 * 点击文字输入框
	 * 
	 * @param v
	 */
	public void editClick(View v) {
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
//			iv_emoticons_normal.setVisibility(View.VISIBLE);
//			iv_emoticons_checked.setVisibility(View.INVISIBLE);
		}

	}

 
    public void showToast(Context context, String text, int duration) {  
        if (mToast == null) {  
            mToast = Toast.makeText(context, text, duration);
            mToast.setGravity(Gravity.TOP, 0, 150);
        } else {  
            mToast.setText(text);  
            mToast.setDuration(duration);
            mToast.setGravity(Gravity.TOP, 0, 150);
        }  
        mToast.show();
    }  
	
	 Handler voiceHandler=new Handler(){
		 public void handleMessage(Message msg) {
			 if(msg.what==10001){
                 timer2=new Timer();
                 timer2.schedule(new TimerTask() {
                     @Override
                     public void run() {
                         // TODO Auto-generated method stub
                    	 runOnUiThread(new Runnable() {
							public void run() {
								 showToast(BarterDedailCommentActivity.this,"还可以说"+(voicei--)+"秒",1);
								 if(voicei==(-1)){
									voiceBool = false;
									if (mToast != null) {
										mToast.cancel();
									}

									if (timer2 != null) {
										timer2.cancel();
									}

									if (timer != null) {
										timer.cancel();
									}
									voicei = 10;
									buttonPressToSpeak.setPressed(false);
		             				 recordingContainer.setVisibility(View.INVISIBLE);
		             				 if (wakeLock.isHeld())
		             					 wakeLock.release();
		             					// stop recording and send voice file
		             					String st1 = getResources().getString(R.string.Recording_without_permission);
		             					String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
		             					String st3 = getResources().getString(R.string.send_failure_please);
		             					try {
		             						int length = voiceRecorder.stopRecoding();
		             						Loger.i("youlin", "22222222222222222222222222222----->"+length);
		             						if (length > 0) {
		             							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
		             									Integer.toString(length), false);
		             						} else if (length == EMError.INVALID_FILE) {
		             							Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
		             						} else {
		             							Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
		             						}
		             					} catch (Exception e) {
		             						e.printStackTrace();
		             						Toast.makeText(BarterDedailCommentActivity.this, st3, Toast.LENGTH_SHORT).show();
		             					}
		             				 return ;
		                         }
							}
						 });
                     }
                 }, 0,1000); 
			 }
		 };
	 };
	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				AllFamilyDaoDBImpl curfamilyDaoDBImpl=new AllFamilyDaoDBImpl(BarterDedailCommentActivity.this);
				if(curfamilyDaoDBImpl.getCurrentAddrDetail("132").getEntity_type()==1){
				voiceBool=true;
				voicei=10;
				 timer=new Timer();
				    timer.schedule(new TimerTask() {
	                    @Override
	                    public void run() {
	                        // TODO Auto-generated method stub
	                    	Thread thread=new Thread(new Runnable() {
								public void run() {
									Message message=new Message();
									message.what=10001;
									voiceHandler.sendMessage(message);
								}
							});
	                    	thread.start();
	                    }
	                }, 50000);
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
					Toast.makeText(BarterDedailCommentActivity.this, st4, Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					Toast.makeText(BarterDedailCommentActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}
				}else{
					Toast.makeText(BarterDedailCommentActivity.this, "您的地址未经过验证", Toast.LENGTH_SHORT).show();
					return false;
				}
				return true;
			case MotionEvent.ACTION_MOVE: { 
				if (event.getY() < 0) {
					recordingHint.setText(getString(R.string.release_to_cancel));
					recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				if(mToast!=null){
					mToast.cancel();
				}
			
				if(timer2!=null){
					timer2.cancel();
				}
				
				if(timer!=null){
					timer.cancel();
				}
				voicei=0;
				if(voiceBool){
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();
				} else {
					// stop recording and send voice file
					String st1 = getResources().getString(R.string.Recording_without_permission);
					String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						Loger.i("youlin", "11111111111111111111111111111111----->"+length);
						if (length > 0) {
							Loger.d("test5","voicepath="+voiceRecorder.getVoiceFilePath()+"voicename="+voiceRecorder.getVoiceFileName(toChatUsername));
							sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
									Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(BarterDedailCommentActivity.this, st3, Toast.LENGTH_SHORT).show();
					}

				}
				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(20, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName("com.easemob.chatuidemo.utils.SmileUtils");
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(BarterDedailCommentActivity.this,
									(String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {

								int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (SmileUtils.containsKey(cs.toString()))
											mEditTextContent.getEditableText().delete(i, selectionStart);
										else
											mEditTextContent.getEditableText().delete(selectionStart - 1,
													selectionStart);
									} else {
										mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}

	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
		if (group != null)
			((TextView) findViewById(R.id.name)).setText(group.getGroupName());

		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
		sdkHelper.pushActivity(this);
		IntentFilter msgfilter = new IntentFilter();
		msgfilter.addAction("com.nfs.youlin.replay");
		msgfilter.setPriority(995);
		registerReceiver(sendMsgBroadcastReceiver, msgfilter);
		
		IntentFilter commFilter = new IntentFilter();
		commFilter.addAction("youlin.del.comm.action");
		registerReceiver(delCommBroadcastReceiver, commFilter);
		if(CircleDetailActivity.finishBool){
			finish();
			CircleDetailActivity.finishBool=false;
		}
	}

	@Override
	protected void onStop() {

		DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
		// 把此activity 从foreground activity 列表里移除
		sdkHelper.popActivity(this);
		
		try {
			unregisterReceiver(sendMsgBroadcastReceiver);
		} catch (Exception e) {
			Loger.i("TEST", "sendMsgBroadcastReceiverErr==>"+e.getMessage());
			e.printStackTrace();
		}
		
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
		if (wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
			}
		} catch (Exception e) {
		}
		unregisterReceiver(delCommBroadcastReceiver);
	}
	
	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {

		if(chatType == CHATTYPE_CHATROOM){
			EMChatManager.getInstance().leaveChatRoom(toChatUsername);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if (CommentDetailAdapter.mediaPlayer != null) {
				if(CommentDetailAdapter.isPlaying){
					CommentDetailAdapter.mediaPlayer.pause();
					CommentDetailAdapter.mediaPlayer.seekTo(0);
					CommentDetailAdapter.mediaPlayer.release();
				}
			}
			CommentDetailAdapter.isPlaying = false;
			if(mToast!=null){
				mToast.cancel();
			}
		
			if(timer2!=null){
				timer2.cancel();
			}
			
			if(timer!=null){
				timer.cancel();
			}
			if (more.getVisibility() == View.VISIBLE) {
				more.setVisibility(View.GONE);
//				iv_emoticons_normal.setVisibility(View.VISIBLE);
//				iv_emoticons_checked.setVisibility(View.INVISIBLE);
			}
			finish();
			Loger.i("TEST", "213131313123----><返回");
		}
		return super.onKeyDown(keyCode, event);
	}
	public String getToChatUsername() {
		return toChatUsername;
	}

	private class GetDataTask1 extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
		//上拉
			pullUpShowComments();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here
			// Call onRefreshComplete when the list has been refreshed.
			mPullToRefreshListView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	private void InitShowComments(){
		try {
			RequestParams params = new RequestParams();
			params.put("topic_id", topicId);
			params.put("count", PAGE_COUNT);
			params.put("type", 0);
			params.put("tag", "getcomm");
			params.put("apitype", IHttpRequestUtils.APITYPE[5]);
			params.put("user_id", App.sUserLoginId);
			AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
			asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
					new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONArray response) {
					// TODO Auto-generated method stub
					Loger.i("youlin", "222222222222222222222222222222------->");
					init_Timer.cancel();
					int responseLen = response.length();
//					listCommBundle = null;
					if(responseLen>0){
						responseComment = response;
//						listCommBundle = new ArrayList<Bundle>();
					}else{
						responseComment = null;
					}
					String replyImg = null;
					String voice = null;
					String voiceTime = null;
					JSONArray commentJArray=null;
					JSONObject commentObject=null;
						try {
							commentJArray = responseComment;
							if(commentJArray!=null){
							for (int i = 0; i < commentJArray.length(); i++) {
								//commentList.add(commentJArray.getString(i));
								Map<String, Object> map=new HashMap<String, Object>();
								try {
										commentObject = new JSONObject(commentJArray.getString(i));
										if (!commentObject.getString("mediaFiles").isEmpty()) {
											JSONArray mediaArray=new JSONArray(commentObject.getString("mediaFiles"));
											for (int j = 0; j < mediaArray.length(); j++) {
												JSONObject mediaObject=new JSONObject(mediaArray.getString(j));
												try {
													replyImg = mediaObject.getString("resPath");
												} catch (Exception e) {
													// TODO Auto-generated catch block
													replyImg = "null";
													e.printStackTrace();
												}
												try {
													voice = mediaObject.getString("voicePath");
												} catch (Exception e) {
													// TODO Auto-generated catch block
													voice = "null";
													e.printStackTrace();
												}
												try {
													voiceTime = mediaObject.getString("videoLength");
												} catch (Exception e) {
													// TODO Auto-generated catch block
													voiceTime = "null";
													e.printStackTrace();
												}
											}
											App.CurrentSysTime=commentObject.getLong("systemTime");
											map.put("headUrl", commentObject.getString("senderAvatar"));
											map.put("name",commentObject.getString("displayName"));
											map.put("type", commentObject.getString("commentType"));
											map.put("remarkName", commentObject.getString("remarkName"));
											map.put("content", commentObject.getString("content"));
											map.put("senderId", commentObject.getString("senderId"));
											map.put("commId",commentObject.getString("commId"));
											map.put("replyImg",replyImg);
											map.put("voice",voice);
											map.put("voiceTime",voiceTime);
											map.put("time", Long.parseLong(commentObject.getString("sendTime")));
											commentList.add(map);
										}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									App.CurrentSysTime=commentObject.getLong("systemTime");
									map.put("headUrl", commentObject.getString("senderAvatar"));
									map.put("name",commentObject.getString("displayName"));
									map.put("type", commentObject.getString("commentType"));
									map.put("remarkName", commentObject.getString("remarkName"));
									map.put("content", commentObject.getString("content"));
									map.put("senderId", commentObject.getString("senderId"));
									map.put("commId",commentObject.getString("commId"));
									map.put("replyImg","null");
									map.put("voice","null");
									map.put("voiceTime","null");
									map.put("time", Long.parseLong(commentObject.getString("sendTime")));
									commentList.add(map);
									e.printStackTrace();
								}
								
							}
							try {
								id = Long.parseLong((String) commentList.get(0).get("commId"));
								Loger.i("youlin","idididiidid----->"+id);
								topCommentId = Long.parseLong((String) commentList.get(0).get("commId"));
								bottomCommentId = Long.parseLong((String) commentList.get(commentList.size()-1).get("commId"));
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						  }
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					Message msg = new Message();
					msg.what = SUCCESS_INIT_RESPONSE;
					sendCommHandle.sendMessage(msg);

					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					String flag = null;
					try {
						flag = response.getString("flag");
						if("no".equals(flag)){
							Loger.i("TEST", "没有最新评论");
							init_Timer.cancel();
							Message msg = new Message();
							msg.what = SUCCESS_INIT_RESPONSE;
							sendCommHandle.sendMessage(msg);
						}
					} catch (JSONException e) {
						Message msg = new Message();
						msg.what = FAILED_INIT_RESPONSE;
						sendCommHandle.sendMessage(msg);
						e.printStackTrace();
					}
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					// TODO Auto-generated method stub
					new ErrorServer(BarterDedailCommentActivity.this, responseString);
					Message msg = new Message();
					msg.what = REFUSE_INIT_RESPONSE;
					sendCommHandle.sendMessage(msg);
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void InitShowCommentsTimer(){
		try {
			RequestParams params = new RequestParams();
			params.put("topic_id", topicId);
			params.put("count", PAGE_COUNT);
			params.put("type", 0);
			params.put("tag", "getcomm");
			params.put("apitype", IHttpRequestUtils.APITYPE[5]);
			params.put("user_id", App.sUserLoginId);
			SyncHttpClient asyncHttpClient = new SyncHttpClient();
			asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
					new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONArray response) {
					// TODO Auto-generated method stub
					int responseLen = response.length();

					if(responseLen>0){
						responseComment = response;
//						listCommBundle = new ArrayList<Bundle>();
					}else{
						responseComment = null;
					}
					String replyImg = null;
					String voice = null;
					String voiceTime = null;
					JSONArray commentJArray=null;
					JSONObject commentObject=null;
						try {
							commentJArray = responseComment;
							if(commentJArray!=null){
							for (int i = 0; i < commentJArray.length(); i++) {
								//commentList.add(commentJArray.getString(i));
								Map<String, Object> map=new HashMap<String, Object>();
								try {
										commentObject = new JSONObject(commentJArray.getString(i));
										if (!commentObject.getString("mediaFiles").isEmpty()) {
											JSONArray mediaArray=new JSONArray(commentObject.getString("mediaFiles"));
											for (int j = 0; j < mediaArray.length(); j++) {
												JSONObject mediaObject=new JSONObject(mediaArray.getString(j));
												try {
													replyImg = mediaObject.getString("resPath");
												} catch (Exception e) {
													// TODO Auto-generated catch block
													replyImg = "null";
													e.printStackTrace();
												}
												try {
													voice = mediaObject.getString("voicePath");
												} catch (Exception e) {
													// TODO Auto-generated catch block
													voice = "null";
													e.printStackTrace();
												}
												try {
													voiceTime = mediaObject.getString("videoLength");
												} catch (Exception e) {
													// TODO Auto-generated catch block
													voiceTime = "null";
													e.printStackTrace();
												}
											}
											App.CurrentSysTime=commentObject.getLong("systemTime");
											map.put("headUrl", commentObject.getString("senderAvatar"));
											map.put("name",commentObject.getString("displayName"));
											map.put("type", commentObject.getString("commentType"));
											map.put("remarkName", commentObject.getString("remarkName"));
											map.put("content", commentObject.getString("content"));
											map.put("senderId", commentObject.getString("senderId"));
											map.put("commId",commentObject.getString("commId"));
											map.put("replyImg",replyImg);
											map.put("voice",voice);
											map.put("voiceTime",voiceTime);
											map.put("time", Long.parseLong(commentObject.getString("sendTime")));
											commentList.add(map);
										}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									App.CurrentSysTime=commentObject.getLong("systemTime");
									map.put("headUrl", commentObject.getString("senderAvatar"));
									map.put("name",commentObject.getString("displayName"));
									map.put("type", commentObject.getString("commentType"));
									map.put("remarkName", commentObject.getString("remarkName"));
									map.put("content", commentObject.getString("content"));
									map.put("senderId", commentObject.getString("senderId"));
									map.put("commId",commentObject.getString("commId"));
									map.put("replyImg","null");
									map.put("voice","null");
									map.put("voiceTime","null");
									map.put("time", Long.parseLong(commentObject.getString("sendTime")));
									commentList.add(map);
									e.printStackTrace();
								}
								
							}
							try {
								id = Long.parseLong((String) commentList.get(0).get("commId"));
								Loger.i("youlin","idididiidid----->"+id);
								topCommentId = Long.parseLong((String) commentList.get(0).get("commId"));
								bottomCommentId = Long.parseLong((String) commentList.get(commentList.size()-1).get("commId"));
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						  }
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					Message msg = new Message();
					msg.what = SUCCESS_INIT_RESPONSE;
					sendCommHandle.sendMessage(msg);

					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					String flag = null;
					try {
						flag = response.getString("flag");
						if("no".equals(flag)){
							Loger.i("TEST", "没有最新评论");
							Message msg = new Message();
							msg.what = SUCCESS_INIT_RESPONSE;
							sendCommHandle.sendMessage(msg);
						}
					} catch (JSONException e) {
						Message msg = new Message();
						msg.what = FAILED_INIT_RESPONSE;
						sendCommHandle.sendMessage(msg);
						e.printStackTrace();
					}
					super.onSuccess(statusCode, headers, response);
				}
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
					// TODO Auto-generated method stub
					new ErrorServer(BarterDedailCommentActivity.this, responseString);
					Message msg = new Message();
					msg.what = REFUSE_INIT_RESPONSE;
					sendCommHandle.sendMessage(msg);
					super.onFailure(statusCode, headers, responseString, throwable);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void pullUpShowComments(){
		RequestParams params = new RequestParams();
		params.put("topic_id", topicId);
		params.put("comment_id", bottomCommentId);
		params.put("count", PAGE_COUNT);
		params.put("type", 3);
		params.put("tag", "getcomm");
		params.put("apitype", IHttpRequestUtils.APITYPE[5]);
		params.put("user_id", App.sUserLoginId);
		SyncHttpClient asyncHttpClient = new SyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub
				int responseLen = response.length();
				Loger.i("youlin", "111111111111111111111----->"+responseLen);
//				listCommBundle = null;
				if(responseLen>0){
					responseComment = response;
//					listCommBundle = new ArrayList<Bundle>();
				}else{
					responseComment = null;
				}
			
				String replyImg = null;
				String voice = null;
				String voiceTime = null;
				JSONArray commentJArray=null;
				JSONObject commentObject=null;
					try {
						commentJArray = responseComment;
						for (int i = 0; i < commentJArray.length(); i++) {
							//commentList.add(commentJArray.getString(i));
							
							Map<String, Object> map=new HashMap<String, Object>();
							try {
									commentObject = new JSONObject(commentJArray.getString(i));
									if (!commentObject.getString("mediaFiles").isEmpty()) {
										JSONArray mediaArray=new JSONArray(commentObject.getString("mediaFiles"));
										for (int j = 0; j < mediaArray.length(); j++) {
											JSONObject mediaObject=new JSONObject(mediaArray.getString(j));
											try {
												replyImg = mediaObject.getString("resPath");
											} catch (Exception e) {
												// TODO Auto-generated catch block
												replyImg = "null";
												e.printStackTrace();
											}
											try {
												voice = mediaObject.getString("voicePath");
											} catch (Exception e) {
												// TODO Auto-generated catch block
												voice = "null";
												e.printStackTrace();
											}
											try {
												voiceTime = mediaObject.getString("videoLength");
											} catch (Exception e) {
												// TODO Auto-generated catch block
												voiceTime = "null";
												e.printStackTrace();
											}
										}
										App.CurrentSysTime=commentObject.getLong("systemTime");
										map.put("headUrl", commentObject.getString("senderAvatar"));
										map.put("name",commentObject.getString("displayName"));
										map.put("type", commentObject.getString("commentType"));
										map.put("remarkName", commentObject.getString("remarkName"));
										map.put("content", commentObject.getString("content"));
										map.put("senderId", commentObject.getString("senderId"));
										map.put("commId",commentObject.getString("commId"));
										map.put("replyImg",replyImg);
										map.put("voice",voice);
										map.put("voiceTime",voiceTime);
										map.put("time", Long.parseLong(commentObject.getString("sendTime")));
										commentList.add(map);
									}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								App.CurrentSysTime=commentObject.getLong("systemTime");
								map.put("headUrl", commentObject.getString("senderAvatar"));
								map.put("name",commentObject.getString("displayName"));
								map.put("type", commentObject.getString("commentType"));
								map.put("remarkName", commentObject.getString("remarkName"));
								map.put("content", commentObject.getString("content"));
								map.put("senderId", commentObject.getString("senderId"));
								map.put("commId",commentObject.getString("commId"));
								map.put("replyImg","null");
								map.put("voice","null");
								map.put("voiceTime","null");
								map.put("time", Long.parseLong(commentObject.getString("sendTime")));
								commentList.add(map);
								e.printStackTrace();
							}
							
						}
						try {
							bottomCommentId = Long.parseLong((String) commentList.get(commentList.size()-1).get("commId"));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				Message msg = new Message();
				msg.what = SUCCESS_UP_RESPONSE;
				sendCommHandle.sendMessage(msg);
			}
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				String flag = null;
				try {
					flag = response.getString("flag");
					if("no".equals(flag)){
						Loger.i("TEST", "没有最新评论");
						runOnUiThread(new Runnable() {
							public void run() {
								haveNoMoreTv.setVisibility(View.VISIBLE);
							}
						});
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								runOnUiThread(new Runnable() {
									public void run() {
										haveNoMoreTv.setVisibility(View.GONE);
									}
								});
							}
						}, 1000);
						responseComment = null;
					}
				} catch (JSONException e) {
					Message msg = new Message();
					msg.what = FAILED_UP_RESPONSE;
					sendCommHandle.sendMessage(msg);
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				Message msg = new Message();
				msg.what = REFUSE_UP_RESPONSE;
				sendCommHandle.sendMessage(msg);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	private void pullDownShowComments(){
		RequestParams params = new RequestParams();
		params.put("topic_id", topicId);
//		params.put("comment_id", topCommentId);
		Loger.i("TEST","下拉刷新的CommentID-》"+topCommentId);
		params.put("comment_id", id);
		params.put("count", PAGE_COUNT);
		params.put("type", 2);
		params.put("tag", "getcomm");
		params.put("apitype", IHttpRequestUtils.APITYPE[5]);
		params.put("user_id", App.sUserLoginId);
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				// TODO Auto-generated method stub
				int responseLen = response.length();
				//listCommBundle = null;
				if(responseLen>0){
					responseComment = response;
				//	listCommBundle = new ArrayList<Bundle>();
				}else{
					responseComment = null;
				}
					
				String replyImg = null;
				String voice = null;
				String voiceTime = null;
				JSONArray commentJArray=null;
				JSONObject commentObject=null;
				try {
					commentJArray = responseComment;
					for (int i = 0; i < commentJArray.length(); i++) {
						//commentList.add(commentJArray.getString(i));
						
						Map<String, Object> map=new HashMap<String, Object>();
						try {
								commentObject = new JSONObject(commentJArray.getString(i));
								if (!commentObject.getString("mediaFiles").isEmpty()) {
									JSONArray mediaArray=new JSONArray(commentObject.getString("mediaFiles"));
									for (int j = 0; j < mediaArray.length(); j++) {
										JSONObject mediaObject=new JSONObject(mediaArray.getString(j));
										try {
											replyImg = mediaObject.getString("resPath");
										} catch (Exception e) {
											// TODO Auto-generated catch block
											replyImg = "null";
											e.printStackTrace();
										}
										try {
											voice = mediaObject.getString("voicePath");
										} catch (Exception e) {
											// TODO Auto-generated catch block
											voice = "null";
											e.printStackTrace();
										}
										try {
											voiceTime = mediaObject.getString("videoLength");
										} catch (Exception e) {
											// TODO Auto-generated catch block
											voiceTime = "null";
											e.printStackTrace();
										}
									}
									App.CurrentSysTime=commentObject.getLong("systemTime");
									String commId=commentObject.getString("commId");
									map.put("headUrl", commentObject.getString("senderAvatar"));
									map.put("name",commentObject.getString("displayName"));
									map.put("type", commentObject.getString("commentType"));
									map.put("remarkName", commentObject.getString("remarkName"));
									map.put("content", commentObject.getString("content"));
									map.put("senderId", commentObject.getString("senderId"));
									map.put("commId",commId);
									map.put("replyImg",replyImg);
									map.put("voice",voice);
									map.put("voiceTime",voiceTime);
									map.put("time", Long.parseLong(commentObject.getString("sendTime")));
//									Loger.i("youlin","4444444444444444444444--->"+commentList2.contains(map));
									if(!commentList2.contains(map)){
										commentList2.add(0,map);
										commentList.add(0,map);
									}
								}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							App.CurrentSysTime=commentObject.getLong("systemTime");
							String commId=commentObject.getString("commId");
							map.put("headUrl", commentObject.getString("senderAvatar"));
							map.put("name",commentObject.getString("displayName"));
							map.put("type", commentObject.getString("commentType"));
							map.put("remarkName", commentObject.getString("remarkName"));
							map.put("content", commentObject.getString("content"));
							map.put("senderId", commentObject.getString("senderId"));
							map.put("commId",commId);
							map.put("replyImg","null");
							map.put("voice","null");
							map.put("voiceTime","null");
							map.put("time", Long.parseLong(commentObject.getString("sendTime")));
//							Loger.i("youlin","4444444444444444444444--->"+commentList2.contains(map));
							if(!commentList2.contains(map)){
								commentList2.add(0,map);
								commentList.add(0,map);
							}
								
//							for (int j = 0; j < commentList2.size(); j++) {
//								String comm=(String) commentList2.get(j).get("commId");
//								if(!commentList.contains(comm)){     
//							           //添加数据  
//									   commentList.add(0,commentList2.get(j));	 
//							    }
//							}
							
							e.printStackTrace();
						}
						
					}
					
					try {
						topCommentId=Long.parseLong((String) commentList.get(0).get("commId"));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					App.sCommStatus = false;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Message msg = new Message();
				msg.what = SUCCESS_DOWN_RESPONSE;
				sendCommHandle.sendMessage(msg);
			}
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				String flag = null;
				Loger.d("TEST", "success="+responseString);
				try {
					flag = response.getString("flag");
					if("no".equals(flag)){
						Loger.i("TEST", "没有最新评论");
//						App.sCommStatus = false;
						responseComment = null;
//						Message msg = new Message();
//						msg.what = SUCCESS_DOWN_RESPONSE;
//						sendCommHandle.sendMessage(msg);
					}
				} catch (JSONException e) {
					Message msg = new Message();
					msg.what = FAILED_DOWN_RESPONSE;
					sendCommHandle.sendMessage(msg);
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
//				App.sCommStatus = false;
				Message msg = new Message();
				Loger.d("TEST", "fail="+responseString);
				msg.what = REFUSE_DOWN_RESPONSE;
				sendCommHandle.sendMessage(msg);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==android.R.id.home){
			if (CommentDetailAdapter.mediaPlayer != null) {
				if(CommentDetailAdapter.isPlaying){
					CommentDetailAdapter.mediaPlayer.pause();
					CommentDetailAdapter.mediaPlayer.seekTo(0);
					CommentDetailAdapter.mediaPlayer.release();
				}
			}
			CommentDetailAdapter.isPlaying = false;
			if(mToast!=null){
				mToast.cancel();
			}
		
			if(timer2!=null){
				timer2.cancel();
			}
			
			if(timer!=null){
				timer.cancel();
			}
			finish();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	

	@Override
	public void setstatuschanged(int status) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void setstatuschanged(int status, Bundle data) {
		// TODO Auto-generated method stub
		long newTopicId = Long.parseLong(data.getString("topicId"));
		long senderId = Long.parseLong(data.getString("senderId"));
		commentCountInt = Integer.parseInt(data.getString("count"));
		Loger.d("test5", "topicId=" + topicId + "senderId=" + senderId + "user=" + App.sUserLoginId);
		if (newTopicId == topicId && senderId != App.sUserLoginId) {
			pullDownShowComments();
		}
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		if ((e2.getX() - e1.getX() > 50)&&(e2.getY()-e1.getY()<100&&e2.getY()-e1.getY()>-100)) {
			Loger.i("youlin", "56565656778789");
			finish();
			return true;
		}
		return true;
	}
	@Override  
	  public boolean dispatchTouchEvent(MotionEvent ev) {  
	      //TouchEvent dispatcher.  
	      if (detector != null) {  
	          detector.onTouchEvent(ev);
	              //If the gestureDetector handles the event, a swipe has been executed and no more needs to be done.  
	      }  
	      super.dispatchTouchEvent(ev);
	      return true;
	  }  
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouchEvent(event);
		return detector.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
