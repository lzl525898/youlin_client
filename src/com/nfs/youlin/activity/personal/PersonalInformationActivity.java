package com.nfs.youlin.activity.personal;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jpush.android.api.JPushInterface;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailGalleryPictureActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoActivity;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.RoundImageView;
import com.nfs.youlin.utils.StrToTime;
import com.nfs.youlin.utils.ToRoundBitmap;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.SwitchView;
import com.nfs.youlin.view.SwitchView.OnStateChangedListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.R.anim;
import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.GridLayout.Spec;

@SuppressLint("HandlerLeak")
public class PersonalInformationActivity extends Activity implements OnClickListener{
	private AccountDaoDBImpl accountDaoDBImpl;
	private Account account;
	private RelativeLayout headLayout;
	private RelativeLayout nameLayout;
	private RelativeLayout pwdLayout;
	private RelativeLayout birthdayLayout;
	private RelativeLayout occupationLayout;
	private RelativeLayout signUpLayout;
	private RelativeLayout sexLayout;
	private static final int CODE_SELECT_REQUEST = 100;
	public  ImageView headPortraitImg;
	public  static TextView nameTv;
	public  static TextView sexTv;
	public  TextView brithdayTv;
	public  static TextView occupationTv;
	public  static TextView SignTv;
	private List<String> list = new ArrayList<String>();     
    private String time = null;
    private NeighborsHttpRequest httpRequestOpen;
    private ActionBar actionBar;
    private final int SUCCESS_CODE = 100;
	private final int FAILED_CODE = 101;
	private final int REFUSE_CODE = 103;
	private Long stampTime;
	private SwitchView switchView;
	String topicPath = null;
	ImageLoader imageLoader;
	int type=0;
	int listposition;
	int parentclass;
	long topicId;
	List<Object> forumtopicLists ;
	FriendCircleAdapter currentadapter;
	ForumTopic forumTopic;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCESS_CODE:
				brithdayTv.setText(time);
				account.setUser_birthday(stampTime);
				accountDaoDBImpl.modifyObject(account);
				accountDaoDBImpl.releaseDatabaseRes();
				Loger.i("TEST", "ok...................");
				break;
			case FAILED_CODE:
				Loger.i("TEST", "no...................");
				break;
			case REFUSE_CODE:
				Loger.i("TEST", "error................");
				break;
			case 105:
				getMyBitmap();
				//headLayout.invalidate();
				break;
			default:
				break;
			}
		};
	};
	private Handler addressHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCESS_CODE:
				Loger.i("TEST", "ok...................");
				break;
			case FAILED_CODE:
				Loger.i("TEST", "no...................");
				break;
			case REFUSE_CODE:
				Loger.i("TEST", "error................");
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_information);
		Intent intent=getIntent();
		type=intent.getIntExtra("type", 0);
		listposition=intent.getIntExtra("position", 0);
		parentclass=intent.getIntExtra("parent", 0);
		topicId=getIntent().getLongExtra("topic_id", 0L);
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
		imageLoader = ImageLoader.getInstance();
		actionBar=getActionBar();
		actionBar.setTitle("个人信息");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		accountDaoDBImpl=new AccountDaoDBImpl(this);
		headLayout =(RelativeLayout)findViewById(R.id.head_portrait);
		nameLayout=(RelativeLayout)findViewById(R.id.name);
		pwdLayout=(RelativeLayout)findViewById(R.id.pwd);
		birthdayLayout=(RelativeLayout)findViewById(R.id.birthday);
		occupationLayout=(RelativeLayout)findViewById(R.id.occupation);
		//signUpLayout=(RelativeLayout)findViewById(R.id.sign_up_layout);
		nameTv=(TextView)findViewById(R.id.nameTv);
		brithdayTv=(TextView)findViewById(R.id.brithdayTv);
		occupationTv=(TextView)findViewById(R.id.occupationTv);
	    headPortraitImg=(ImageView)findViewById(R.id.head_portrait_img);
	    
	    //familyAddressSpinner=(Spinner)findViewById(R.id.spinner1);
	    sexLayout = (RelativeLayout) findViewById(R.id.sex);
	    sexTv = (TextView) findViewById(R.id.sexTv);
	    //SignTv = (TextView) findViewById(R.id.sign_tv);
	    switchView=(SwitchView) findViewById(R.id.address_switch);
	    account = accountDaoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
//	    accountDaoDBImpl.releaseDatabaseRes();
	    setSwitch();
	   
	    getMyBitmap();
	    headPortraitImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PersonalInformationActivity.this,CircleDetailGalleryPictureActivity.class);
				intent.putExtra("type", 1);
				intent.putExtra("url",topicPath);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});
	    nameTv.setText(account.getUser_name());
	    Loger.i("youlin","time--->"+account.getUser_birthday());
	    String time=CommonTools.getFormatDate(account.getUser_birthday());
	    brithdayTv.setText(time);
	    occupationTv.setText(("null".equals(account.getUser_vocation())?"":account.getUser_vocation()));
	    sexTv.setText(getSex(account.getUser_gender()));
//	    if(getSignature()!=null){
//	    	SignTv.setText("null".equals(getSignature().toString())?"":getSignature());
//	    }else{
//	    	SignTv.setText("");
//	    }
	    headLayout.setOnClickListener(this);
		nameLayout.setOnClickListener(this);
		pwdLayout.setOnClickListener(this);
		birthdayLayout.setOnClickListener(this);
		occupationLayout.setOnClickListener(this);
		sexLayout.setOnClickListener(this);
		//signUpLayout.setOnClickListener(this);
	    
	    //从数据库中取出公开标志
	    /*
	    familyAddressSpinner.setSelection(account.getUser_public_status());
	    familyAddressSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	    
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//Toast.makeText(PersonalInformationActivity.this,"213", 1).show();
				if(MainActivity.networkBool){
					if(arg2==0){
						//插入数据库
						Account account=accountDaoDBImpl.findAccountByPhone(App.sUserPhone);
						account.setUser_public_status(0);
						accountDaoDBImpl.modifyObject(account);
						accountDaoDBImpl.releaseDatabaseRes();
					}else{
						Account account=accountDaoDBImpl.findAccountByPhone(App.sUserPhone);
						account.setUser_public_status(1);
						accountDaoDBImpl.modifyObject(account);
						accountDaoDBImpl.releaseDatabaseRes();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		*/
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case CODE_SELECT_REQUEST:
			Loger.i("youlin","CODE_SELECT_REQUEST---------------");
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg=new Message();
					msg.what=105;
					handler.sendMessage(msg);
				}
			}).start();
		
			break;
			
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void getMyBitmap() {
			AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(PersonalInformationActivity.this);
			Account account = null;
			account = accountDaoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
			if(account != null){
				topicPath = account.getUser_portrait();
			}
			if (topicPath!=null) {
//				Picasso.with(PersonalInformationActivity.this) 
//						.load(topicPath) 
//						.placeholder(R.drawable.account) 
//						.error(R.drawable.account) 
//						.fit() 
//						.tag(PersonalInformationActivity.this) 
//						.into(headPortraitImg);
				Loger.i("TEST","1111111111111111111111111111111---->"+topicPath);
				imageLoader.displayImage(topicPath,headPortraitImg,App.options_account);
				Loger.i("TEST", "头像加载成功！！！！");
			} else {
				headPortraitImg.setBackgroundResource(R.drawable.account);
			}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			if(type==1){
				//gethttpdata(topicId,1);
			}
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.head_portrait:
			Intent intent=new Intent(PersonalInformationActivity.this,SelectPicPopupWindowActivity.class);
			startActivityForResult(intent,CODE_SELECT_REQUEST);
			break;
		case R.id.name:
			startActivity(new Intent(PersonalInformationActivity.this,PersonalInfoNameActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.pwd:
			startActivity(new Intent(PersonalInformationActivity.this,PersonalInfoPasswordActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.birthday:
			setBirthday();
			break;
		case R.id.sex:
			Intent sexIntent=new Intent(PersonalInformationActivity.this,PersonalInfoSexActivity.class);
			startActivityForResult(sexIntent,CODE_SELECT_REQUEST);
			break;
		case R.id.occupation:
			startActivity(new Intent(PersonalInformationActivity.this,PersonalInfoOccupActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
//		case R.id.sign_up_layout:
//			startActivity(new Intent(PersonalInformationActivity.this,PersonalInfoSignatureActivity.class));
//			break;
		default:
			break;
		}
	}
	
	private String getSex(int nSex){
		String sexString  = null;
		switch (nSex) {
		case 1:
			sexString = "男";
			break;
		case 2:
			sexString = "女";
			break;
		case 3:
			sexString = "保密";
			break;
		default:
			sexString = "未知";
			break;
		}
		return sexString;
	}
	public void setBirthday(){
		Calendar calendar=Calendar.getInstance();
		final DatePickerDialog datePickerDialog=new DatePickerDialog(PersonalInformationActivity.this, null, 
		calendar.get(Calendar.YEAR),
		calendar.get(Calendar.MONTH),
		calendar.get(Calendar.DAY_OF_MONTH));
		
		datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				DatePicker datePicker=datePickerDialog.getDatePicker();
				int year=datePicker.getYear();
				int month=datePicker.getMonth();
				int day=datePicker.getDayOfMonth();
				StrToTime strToTime=new StrToTime();
				time=year+"年"+(month+1)+"月"+day+"日";
				stampTime = Long.valueOf(strToTime.getTime(time));
				if(NetworkService.networkBool){
				if(stampTime>System.currentTimeMillis()){
					Toast.makeText(PersonalInformationActivity.this, "生日日期不合理", Toast.LENGTH_SHORT).show();
				}else{
					Loger.i("TEST","stampTime->"+stampTime);
					httpRequestOpen = new NeighborsHttpRequest(PersonalInformationActivity.this);
					RequestParams params = new RequestParams();
					params.put("user_id", App.sUserLoginId);
					params.put("user_phone_number", App.sUserPhone);
					params.put("user_birthday", stampTime);
					params.put("tag", "upload");
					params.put("apitype", IHttpRequestUtils.APITYPE[0]);
					httpRequestOpen.updateUserInfo(params,handler);
				}
				}else{
					Toast.makeText(PersonalInformationActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				}
			}
		});
		datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"取消",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				datePickerDialog.cancel();
			}
		});
		datePickerDialog.show();
	}
	
	private String getSignature(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String strtmp = sharedata.getString("signature", null);
		return strtmp;
	}
	
	public void setSwitch(){
		getOpenStatus("null");
		switchView.setOnStateChangedListener(new OnStateChangedListener() {
			@Override
			public void toggleToOn() {
				// TODO Auto-generated method stub
				if(NetworkService.networkBool){
					switchView.toggleSwitch(true);
					getOpenStatus("on");
				}else{
					switchView.toggleSwitch(false);
					Toast.makeText(PersonalInformationActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				}
				
//				switchView.postDelayed(new Runnable() {
//		            @Override 
//		            public void run() {
//		            	switchView.toggleSwitch(true); //以动画效果切换到打开的状态
//		        }},200);
			}
			@Override
			public void toggleToOff() {
				// TODO Auto-generated method stub
				if(NetworkService.networkBool){
					switchView.toggleSwitch(false);
					getOpenStatus("off");
				}else{
					switchView.toggleSwitch(true);
					Toast.makeText(PersonalInformationActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	public void getOpenStatus(final String kaiguan){
		RequestParams params = new RequestParams();
		params.put("user_id", App.sUserLoginId);
		params.put("user_phone", App.sUserPhone);
		params.put("tag", "getstatus");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient httpRequest = new AsyncHttpClient();
		httpRequest.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// TODO Auto-generated method stub
				try {
					int status= Integer.parseInt(response.getString("status"));
					String flag = response.getString("flag");
					Loger.i("TEST", "----------------------------->"+status+" "+flag+" "+App.sUserPhone+" "+App.sUserLoginId);
					if(kaiguan.equals("null")){
						if(status==2||status==4){
							switchView.setState(true);
						}else{
							switchView.setState(false);
						}					}
					if(kaiguan.equals("on")){
						if(status==1){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInformationActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 2);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,addressHandler);
						}
						if(status==2){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInformationActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 2);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,addressHandler);
						}
						if(status==3){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInformationActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 4);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,addressHandler);
						}
						if(status==4){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInformationActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 4);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,addressHandler);
						}
					}
					if(kaiguan.equals("off")){
						if(status==1){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInformationActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 1);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,addressHandler);
						}
						if(status==2){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInformationActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 1);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,addressHandler);
						}
						if(status==3){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInformationActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 3);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,addressHandler);
						}
						if(status==4){
							httpRequestOpen = new NeighborsHttpRequest(PersonalInformationActivity.this);
							RequestParams params = new RequestParams();
							params.put("user_id", App.sUserLoginId);
							params.put("user_phone_number", App.sUserPhone);
							params.put("user_public_status", 3);
							params.put("tag", "upload");
							params.put("apitype", IHttpRequestUtils.APITYPE[0]);
							httpRequestOpen.updateUserInfo(params,addressHandler);
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
				// TODO Auto-generated method stub
				new ErrorServer(PersonalInformationActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
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
								finish();
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
											Loger.i("youlin", "111111111111111111--->PersonalInformation");
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							}
							@Override
							public void onFailure(int statusCode,Header[] headers,String responseString,Throwable throwable) {
								new ErrorServer(PersonalInformationActivity.this, responseString);
								super.onFailure(statusCode,headers,responseString,throwable);
							}
						});
		}else{
			//Toast.makeText(CircleDetailActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
		}
	}
	private void getTopicDetailsInfos(JSONArray response){
		int responseLen = response.length();
		Loger.i("TEST", "json obj length->"+responseLen);
		if(responseLen>0){
		}
		for (int i = 0; i < responseLen; i++) {
			try {
//				Loger.i("TEST", "mediaJson->start");
				JSONObject jsonObject = new JSONObject(response.getString(i));
				String sender_portrait = jsonObject.getString("senderPortrait");
				String disply_name = jsonObject.getString("displayName");
				forumTopic=(ForumTopic) forumtopicLists.get(listposition);
				forumTopic.setSender_portrait(sender_portrait);
				forumTopic.setDisplay_name(disply_name);
				forumtopicLists.set(listposition, forumTopic);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Loger.i("TEST", "ERROR->"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
