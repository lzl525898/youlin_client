package com.nfs.youlin.activity.personal;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.R.id;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailGalleryPictureActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.NeighborDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.RoundImageView;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.YLStringVerification;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.CustomDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import cn.jpush.android.api.JPushInterface;

public class FriendInformationActivity extends Activity {
	public static final int CHATTYPE_SINGLE = 1;
	ImageLoader imageLoader;
	String occupStr;
	String addressStr;
	int sexInt;
	TextView nickTv;
	TextView occupTv;
	ImageView sexImg;
	TextView addressTv;
	TextView cityTv;
	String displayName = null;
	TextView nameTv;
	RelativeLayout pushLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_friend_information);
		imageLoader = ImageLoader.getInstance();
		Intent intent=getIntent();
		final long senderID=intent.getLongExtra("sender_id", 0L);
		//final String name=intent.getStringExtra("name");
		String displayNameOld=intent.getStringExtra("display_name");
		try {
			displayName = displayNameOld.split("[@]")[0];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final String senderPortrait=intent.getStringExtra("sender_portrait");
	
		LinearLayout chatLayout=(LinearLayout) findViewById(R.id.chat_layout);
		LinearLayout remarkLayout=(LinearLayout) findViewById(R.id.remark_layout);
		nameTv=(TextView) findViewById(R.id.name_tv);
		RoundImageView headImg=(RoundImageView) findViewById(R.id.head_img);
		addressTv=(TextView) findViewById(R.id.address_tv);
		nickTv=(TextView) findViewById(R.id.nick_tv);
		occupTv=(TextView) findViewById(R.id.occup_tv);
		cityTv=(TextView) findViewById(R.id.city_tv);
		pushLayout=(RelativeLayout) findViewById(R.id.push_layout);
		sexImg=(ImageView) findViewById(R.id.sex_img);
	
		nameTv.setText(displayName);
		chatLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AccountDaoDBImpl account = new AccountDaoDBImpl(FriendInformationActivity.this);
				Intent intent = new Intent(FriendInformationActivity.this, com.easemob.chatuidemo.activity.ChatActivity.class);
		        intent.putExtra("userId",String.valueOf(senderID));
		        intent.putExtra("chatType", CHATTYPE_SINGLE);
		        intent.putExtra("usernick",displayName);
		        String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
		        intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
		        intent.putExtra("neighborurl", senderPortrait);
		        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		        startActivity(intent);
			}
		});
		remarkLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CustomDialog.Builder builder=new CustomDialog.Builder(FriendInformationActivity.this);
				View view=LayoutInflater.from(FriendInformationActivity.this).inflate(R.layout.neighbor_remarks, null);
				final EditText editText=(EditText)view.findViewById(R.id.remark_et);
				editText.setText(nameTv.getText().toString());
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
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String remarkStr=editText.getText().toString().trim();
						if(remarkStr.length()>0){
						if(YLStringVerification.checkNickNameBiaoQing(remarkStr) && !remarkStr.equals("null")){
						RequestParams params = new RequestParams();
						params.put("tag", "setuserremarks");
						params.put("apitype",IHttpRequestUtils.APITYPE[0]);
						params.put("user_id", App.sUserLoginId);
						params.put("remarks_id",senderID);
						params.put("remarks_name", remarkStr);
						AsyncHttpClient client = new AsyncHttpClient();
						client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,params, new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,org.apache.http.Header[] headers,
									org.json.JSONObject response) {
								// TODO Auto-generated method stub
								try {
									Loger.i("youlin", "66666666666--->"+response.toString());
									if(response.getString("flag").equals("ok")){
										nameTv.setText(response.getString("remarks_name"));
										NeighborDaoDBImpl neighborDaoDBImpl=new NeighborDaoDBImpl(FriendInformationActivity.this);
										neighborDaoDBImpl.updateNeighborOne(response.getString("remarks_name"), senderID);
										sendBroadcast(new Intent("com.nfs.youlin.find.update"));
//										StatusChangeutils changeutils;
//										changeutils = new StatusChangeutils();
//										changeutils.setstatuschange("SETNEIGHBOR",1);
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
								new ErrorServer(FriendInformationActivity.this, responseString.toString());
									super.onFailure(statusCode, headers,responseString, throwable);
								}
							});
						dialog.cancel();
					}else{
						Toast.makeText(FriendInformationActivity.this, "备注格式不正确", 0).show();	
					}
					}else{
						Toast.makeText(FriendInformationActivity.this, "备注不能为空", 0).show();
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
			}
		});
		imageLoader.displayImage(senderPortrait,headImg,App.options_account);
		//Picasso.with(FriendInformationActivity.this).load(senderPortrait).placeholder(R.drawable.account).error(R.drawable.account).into(headImg);
		headImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FriendInformationActivity.this,CircleDetailGalleryPictureActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra("type", 1);
				intent.putExtra("url",senderPortrait);
				startActivity(intent);
			}
		});
		gethttpdata(senderID);
		pushLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isBlack(senderID);
			}
		});
	}


	private void gethttpdata(long senderID){
//		if(NetworkService.networkBool){
		RequestParams reference = new RequestParams();
		reference.put("my_user_id",App.sUserLoginId);
		reference.put("user_id",senderID);
		reference.put("tag","userdetailinfo");
		reference.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+ IHttpRequestUtils.YOULIN,reference,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,Header[] headers,JSONArray response) {
								Loger.i("TEST",response.toString());
								super.onSuccess(statusCode,headers,response);
							}
							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
								// TODO Auto-generated method stub
								Loger.i("youlin", "23123213131---->"+response.toString());
									try {
										String flag = response.getString("flag");
										if(flag.equals("no")){
											Loger.i("youlin", "2222222222222222222--->PersonalInformation");
										}else{
											//Loger.i("youlin", "3333333333333333333--->"+response.getString("user_gender"));
											nameTv.setText(response.getString("user_nick"));
											addressTv.setText(response.getString("cur_community_name"));
											sexInt = Integer.parseInt(response.getString("user_gender"));
											if(sexInt==1){
												sexImg.setBackgroundResource(R.drawable.nan);
											}else if(sexInt==2){
												sexImg.setBackgroundResource(R.drawable.nvtubiao);
											}else{
												sexImg.setBackgroundResource(R.drawable.baomi);
											}
											nickTv.setText(response.getString("current_nick"));
											int status= Integer.parseInt(response.getString("user_public_status"));
											if(status==3 || status==4){
												occupStr=response.getString("user_profession");
												if(occupStr.length()<=0 || occupStr.equals("null") || occupStr==null){
													occupTv.setText("未设置");
												}else{
													occupTv.setText(occupStr);
												}
											}else{
												occupTv.setText("未公开");
											}
											cityTv.setText(response.getString("cur_city_name"));
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							}
							@Override
							public void onFailure(int statusCode,Header[] headers,String responseString,Throwable throwable) {
								new ErrorServer(FriendInformationActivity.this, responseString.toString());
								super.onFailure(statusCode,headers,responseString,throwable);
							}
						});
//		}else{
//			Toast.makeText(FriendInformationActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
//		}
	}
	private void isBlack(final Long senderId){
		RequestParams params = new RequestParams();
		params.put("user_id",App.sUserLoginId);
		params.put("sender_id",senderId);
		params.put("tag","verifyblack");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+ IHttpRequestUtils.YOULIN,params,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,Header[] headers,JSONArray response) {
								Loger.i("TEST",response.toString());
								super.onSuccess(statusCode,headers,response);
							}
							@Override
							public void onSuccess(int statusCode, Header[] headers,
									JSONObject response) {
								// TODO Auto-generated method stub
								try {
									if(response.getString("flag").equals("no")){
										startActivity(new Intent(FriendInformationActivity.this,MyPushActivity.class).putExtra("type", 1).putExtra("sender_id", senderId).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
									}else if(response.getString("flag").equals("ok")){
										Toast.makeText(FriendInformationActivity.this,"您不在对方的邻居列表中",Toast.LENGTH_SHORT).show();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							@Override
							public void onFailure(int statusCode,Header[] headers,String responseString,Throwable throwable) {
								new ErrorServer(FriendInformationActivity.this, responseString.toString());
								super.onFailure(statusCode,headers,responseString,throwable);
							}
						});
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
