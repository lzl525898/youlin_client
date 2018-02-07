package com.nfs.youlin.activity.personal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.titlebar.barter.BarterActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.KMP_search;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.YLStringVerification;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.YLProgressDialog;
import com.nfs.youlin.view.number_fragment;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMPlatformData;
import com.umeng.analytics.social.UMPlatformData.GENDER;
import com.umeng.analytics.social.UMPlatformData.UMedia;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ActionBar.LayoutParams;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class Invite_friend_dealactivity extends Activity implements OnClickListener{
	private ActionBar actionbar;
	private EditText invitedphoneedit;
	private ImageView searchbook;
	private ImageButton jiaren;
	private ImageButton pengyou;
	private Button inviteatonce;
	private Button invitehistory;
	private Button share;
	private YLProgressDialog youpd;
	
	private static final int GETCONTACT_FROMBOOK = 50001;
	private String contactorphone;
	private int inv_type = 0;
	private String user_nick;
	private String icon_path;
	private String imgPath;
	private final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	private final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
	private Intent sentIntent;
	private Intent deliverIntent;
	private PendingIntent sentPI;
	private PendingIntent deliverPI; 
	private boolean bSMSstatus = false;
	private final String down_url = "http://123.57.9.62/yl";  //"https://www.baidu.com/"
	private PopupWindow popupWindow;
	private ListView contactLv;
	private List<String> contactList=new ArrayList<String>();
	private RelativeLayout invateLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_service_lay);
		actionbar = getActionBar();
		actionbar.setTitle("邀请好友");
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayHomeAsUpEnabled(true);
		sentIntent = new Intent(SENT_SMS_ACTION);
		sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
		deliverIntent = new Intent(DELIVERED_SMS_ACTION);
		deliverPI = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);
		searchbook = (ImageView)findViewById(R.id.searchlocaltel);
		invitedphoneedit = (EditText)findViewById(R.id.phoneedit);
		jiaren = (ImageButton)findViewById(R.id.jiaren);
		pengyou= (ImageButton)findViewById(R.id.pengyou);
		inviteatonce = (Button)findViewById(R.id.inviteatonce);
		invitehistory = (Button)findViewById(R.id.invitehistory);
		invateLayout = (RelativeLayout)findViewById(R.id.invate_layout);
		share = (Button)findViewById(R.id.share);
		youpd = YLProgressDialog.createDialogwithcircle(this,"加载中...",0);
		youpd.setCancelable(false);
		user_nick = new AccountDaoDBImpl(this).findAccountByLoginID(String.valueOf(App.sUserLoginId)).getUser_name();
		
		findViewById(R.id.xin_tv).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//startActivity(new Intent(Invite_friend_dealactivity.this,Invite_friend_integral.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		searchbook.setOnClickListener(this);
		jiaren.setOnClickListener(this);
		pengyou.setOnClickListener(this);
		inviteatonce.setOnClickListener(this);
		invitehistory.setOnClickListener(this);
		share.setOnClickListener(this);
		invitedphoneedit.addTextChangedListener(new TextWatcher() {
			int l=0;////////记录字符串被删除字符之前，字符串的长度
	  		   int location=0;//记录光标的位置
	 			@Override
	 			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	 				// TODO Auto-generated method stub
	 				
	 			}
	 			
	 			@Override
	 			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
	 					int arg3) {
	 				// TODO Auto-generated method stub
	     		    l=arg0.length();
	     		    location=invitedphoneedit.getSelectionStart();
	     		   
	 			}
	 			
	 			@Override
	 			public void afterTextChanged(Editable arg0) {
	 				// TODO Auto-generated method stub
	 				contactorphone = arg0.toString();
	     		}
	 		});
		imgPath=getFilesDir().getParent()+File.separator+"icon_youlin.png";
		saveBitmap();
		View contentView=getLayoutInflater().inflate(R.layout.activity_share_popwindow_invate, null);
		contactLv=(ListView) contentView.findViewById(R.id.share_pop_repair_lv);
		popupWindow=new PopupWindow(Invite_friend_dealactivity.this);
		popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setContentView(contentView);
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
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
		switch(v.getId()){
		case R.id.searchlocaltel:
			contactList.clear();
			startActivityForResult(new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI),GETCONTACT_FROMBOOK);  
			break;
		case R.id.jiaren:
			((ImageButton)v).setImageResource(R.drawable.pic_jiaren_b);
			pengyou.setImageResource(R.drawable.pic_pengyou_a);
			inv_type = 1;
			break;
		case R.id.pengyou:
			((ImageButton)v).setImageResource(R.drawable.pic_pengyou_b);
			jiaren.setImageResource(R.drawable.pic_jiaren_a);
			inv_type = 2;
			break;
		case R.id.inviteatonce:
			if(contactorphone == null || !new YLStringVerification(this).checkPhoneNumFormat86(contactorphone)){
				Toast.makeText(this, "请填写正确手机号！", Toast.LENGTH_SHORT).show();
			}else{
				if(inv_type == 0){
					Toast.makeText(this, "请选择邀请类别！", Toast.LENGTH_SHORT).show();
				}else{
					requestInviteCode(contactorphone, inv_type, App.sFamilyId);
				}
			}
			break;
		case R.id.invitehistory:
			startActivity(new Intent(Invite_friend_dealactivity.this,Invite_friend_history.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.share:
			showShare();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			if(requestCode == GETCONTACT_FROMBOOK){
				ContentResolver reContentResolverol = getContentResolver();  
				Uri contactData = data.getData();
				Cursor cursor = managedQuery(contactData, null, null, null, null);  
				cursor.moveToFirst(); 
				
//				String contactorname = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//				Cursor cursor=reContentResolverol.query(contactData, null, null, null, null);
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
	                     null,   
	                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,   
	                     null,   
	                     null);
				Loger.i("TEST","00000---->"+phone.getCount());
				if(phone.getCount()==0){
					invitedphoneedit.setText("");
					Toast.makeText(this, "联系人信息异常", Toast.LENGTH_LONG).show();
					phone.close();
					return;
				}
				while(phone.moveToNext()){
					try {
						contactorphone = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));  
						contactorphone = contactorphone.replace(" ","");  
						contactList.add(contactorphone);
					} catch (Exception e) {
						Loger.i("TEST", e.getMessage());
						invitedphoneedit.setText("");
						Toast.makeText(this, "联系人信息异常", Toast.LENGTH_LONG).show();
					}  
	            }
				phone.close();
//				for (int i = 0; i < contactList.size(); i++) {
//					arr[i]=contactList.get(i);
//				}
				if(contactList.size()>1){
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(Invite_friend_dealactivity.this, R.layout.activity_share_pop_textview_invate,contactList);
				contactLv.setAdapter(adapter);
				contactLv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method stub
						if(!new YLStringVerification(Invite_friend_dealactivity.this).checkPhoneNumFormat86(contactList.get(position)) && 
								!new YLStringVerification(Invite_friend_dealactivity.this).checkPhoneNumFormat(contactList.get(position))){
							Loger.i("TEST", "11111---->"+contactList.get(position));
							invitedphoneedit.setText("");
							Toast.makeText(Invite_friend_dealactivity.this, "联系人信息异常", Toast.LENGTH_LONG).show();
						}else{
							Loger.i("TEST", "22222---->"+contactorphone);
							invitedphoneedit.setText(contactList.get(position));
						}
						popupWindow.dismiss();
					}
				});
				//popupWindow.showAtLocation(getWindow().getDecorView());
				popupWindow.showAsDropDown(invateLayout);
				}else{
					if(!new YLStringVerification(Invite_friend_dealactivity.this).checkPhoneNumFormat86(contactList.get(0)) && 
							!new YLStringVerification(Invite_friend_dealactivity.this).checkPhoneNumFormat(contactList.get(0))){
						Loger.i("TEST", "11111---->"+contactList.get(0));
						invitedphoneedit.setText("");
						Toast.makeText(Invite_friend_dealactivity.this, "联系人信息异常", Toast.LENGTH_LONG).show();
					}else{
						Loger.i("TEST", "22222---->"+contactorphone);
						invitedphoneedit.setText(contactList.get(0));
					}
				}
			}
			
		}
	}
	
	private void requestInviteCode(String inv_phone,final int type,long family_id){
		Loger.i("TEST", "开始调用==》requestInviteCode");
		RequestParams params = new RequestParams();
		params.put("user_id", App.sUserLoginId);
		params.put("inv_phone", inv_phone);
		params.put("inv_status", 0);//常量值0
		params.put("inv_type", inv_type);
		params.put("family_id", family_id);
		params.put("tag", "getinvcode");
		params.put("apitype", "users");
		AsyncHttpClient httpClient = new  AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					String flag = response.getString("flag");
					Loger.i("TEST", "response=>"+response.toString());
//					String yl_msg = response.getString("yl_msg");
//					Toast.makeText(Invite_friend_dealactivity.this, yl_msg, Toast.LENGTH_SHORT).show();
					if(flag.equals("ok")){
						//调用手机短信
						final String invCode = response.getString("inv_code");
						Loger.i("TEST", "返回的邀请码==>"+invCode);
						bSMSstatus = false;
						String contentWithSMS = response.getString("yl_msg");
						Loger.i("LYM", "111111--->"+contentWithSMS);
//						if(type==1){
//							contentWithSMS = contentWithSMS+down_url;
//						}else if(type==2){
//							contentWithSMS = contentWithSMS+down_url;
//						}
						sendSMS(contactorphone,contentWithSMS);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								youpd.show();
							}
						});
						registerReceiver(new BroadcastReceiver(){
							@Override
							public void onReceive(Context context, Intent intent) {
								Loger.i("TEST", "接受者已经接收成功");
							}
						}, new IntentFilter(DELIVERED_SMS_ACTION));
						registerReceiver(new BroadcastReceiver() {
							@Override
							public void onReceive(Context context, Intent intent) {
								// TODO Auto-generated method stub
								switch (getResultCode()) {
								case Activity.RESULT_OK:
									Loger.i("TEST", "短信发送成功");
									bSMSstatus = true;
									break;
								case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
									Loger.i("TEST", "RESULT_ERROR_GENERIC_FAILURE");
									Toast.makeText(Invite_friend_dealactivity.this, "请开启发送短信权限", Toast.LENGTH_LONG).show();
									break;
								case SmsManager.RESULT_ERROR_RADIO_OFF:
									Loger.i("TEST", "RESULT_ERROR_RADIO_OFF");
									break;
								case SmsManager.RESULT_ERROR_NULL_PDU:
									Loger.i("TEST", "RESULT_ERROR_NULL_PDU");
									break;
								default:
									break;
								}
							}
						}, new IntentFilter(SENT_SMS_ACTION));
						new AsyncTask<Void, Void, Void>(){
							@Override
							protected Void doInBackground(Void... params) {
								Long currenttime = System.currentTimeMillis();
								while(true){
									if(((System.currentTimeMillis()-currenttime)>8000) || bSMSstatus){
										Loger.i("TEST", "bSMSstatus==>"+bSMSstatus);
										break;
									}
								}
								return null;
							}
							protected void onPostExecute(Void result) {
								if(!bSMSstatus){
									Loger.i("TEST", "手机没有赋予发送短信权限");
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											// TODO Auto-generated method stub
											youpd.dismiss();
										}
									});
									Toast.makeText(Invite_friend_dealactivity.this, "请开启发送短信权限", Toast.LENGTH_LONG).show();
									return;
								}else{
									Loger.i("TEST", "手机给app赋予发送短信权限");
									PostInvitehttprequest(contactorphone,invCode, App.sFamilyId);
								}
							};
						}.execute();
					}else if(flag.equals("overflow")){
						String msgInfo = response.getString("yl_msg");
						Toast.makeText(Invite_friend_dealactivity.this, msgInfo, Toast.LENGTH_LONG).show();
						youpd.cancel();
					}else if(flag.equals("exist")){//
						String msgInfo = response.getString("yl_msg");
						Toast.makeText(Invite_friend_dealactivity.this, msgInfo, Toast.LENGTH_LONG).show();
						youpd.cancel();
					}else{
						youpd.cancel();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	/*user_id==>发起邀请者用户ID
	inv_phone==>被邀请用户手机
	inv_type==>邀请类型 1表示家人 2表示朋友
	family_id==>发起邀请者familyId
	tag==>invitenewusers
	apitype==>users*/
	private void PostInvitehttprequest(String inv_phone,String inv_code,long family_id){
		RequestParams params = new RequestParams();
		params.put("user_id", App.sUserLoginId);
		params.put("inv_phone", inv_phone);
		params.put("inv_type", inv_type);
		params.put("family_id", family_id);
		params.put("inv_code", inv_code);
		params.put("tag", "invitenewusers");
		params.put("apitype", "users");
		AsyncHttpClient httpClient = new  AsyncHttpClient();
		Loger.i("TEST", "开始POST==>PostInvitehttprequest");
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Loger.i("TEST", "开始执行PostInvitehttprequest=>"+response.toString());
				try {
					String flag = response.getString("flag");
					String yl_msg = response.getString("yl_msg");
					Toast.makeText(Invite_friend_dealactivity.this, yl_msg, Toast.LENGTH_SHORT).show();
					if(flag.equals("ok")){
						invitedphoneedit.setText("");
						inv_type = 0;
						pengyou.setImageResource(R.drawable.pic_pengyou_a);
						jiaren.setImageResource(R.drawable.pic_jiaren_a);
						youpd.cancel();
					}else{
						youpd.cancel();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString,
					Throwable throwable) {
				new ErrorServer(Invite_friend_dealactivity.this, responseString.toString());
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	private void showShare() {
		Loger.d("test4", this.getFilesDir().getParent().toString());
//		BitmapDrawable draw = (BitmapDrawable)mMainActivity.getResources().getDrawable(R.drawable.icon_youlin);
		
    	 
		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//		 oks.setNotification(R.drawable.ic_launcher, mMainActivity.getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle(user_nick+" 邀请您立刻加入");
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl(down_url);
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText(App.shareStr+"\n"+down_url);
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl(down_url);
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
		 oks.setComment("好友邀请");
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite("优邻");
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl(down_url);
		 oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
//		 oks.setCallback(new PlatformActionListener() {
//			
//			@Override
//			public void onError(Platform arg0, int arg1, Throwable arg2) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//				// TODO Auto-generated method stub
//				Loger.d("test5", ""+arg2.size());
//				for(int i =0;i<arg2.size();i++){
//					Loger.d("test5", arg2.toString());
//				}
//			}
//			
//			@Override
//			public void onCancel(Platform arg0, int arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		// 启动分享GUI
		 oks.show(this);
		 }
	/**
	 * 快捷分享项目现在添加为不同的平台添加不同分享内容的方法。
	 *本类用于演示如何区别Twitter的分享内容和其他平台分享内容。
	 */

	public class ShareContentCustomizeDemo implements ShareContentCustomizeCallback {
		//oks.setText("我正在使用优邻APP，帮您快速方便地找到邻居，分享家庭闲置工具，物业通知、保修跟踪。。。\n http://123.57.9.62/adminpush/site/index");

			@Override
			public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
				// TODO Auto-generated method stub
                if (WechatMoments.NAME.equals(platform.getName())) {
           		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                	//paramsToShare.setImageUrl(IHttpRequestUtils.URL+"media/youlin/res/default/share/icon_youlin.png");
                	paramsToShare.setImagePath(icon_path);
                    paramsToShare.setText(App.shareStr+"\n"+down_url);
                    UMPlatformData platform4 = new UMPlatformData(UMedia.WEIXIN_CIRCLE, "user_id");  
                	platform4.setName(String.valueOf(App.sUserLoginId));   
                	MobclickAgent.onSocialEvent(Invite_friend_dealactivity.this, platform4);
                }else if(QQ.NAME.equals(platform.getName())){
                	paramsToShare.setImageUrl(IHttpRequestUtils.HTTP_URL+"media/youlin/res/default/share/icon_youlin.png");
                	paramsToShare.setText(App.shareStr);
                	paramsToShare.setTitleUrl(down_url);
                	UMPlatformData platform3 = new UMPlatformData(UMedia.TENCENT_QQ, "user_id");  
                	platform3.setName(String.valueOf(App.sUserLoginId));   
                	MobclickAgent.onSocialEvent(Invite_friend_dealactivity.this, platform3);
                }else if(QZone.NAME.equals(platform.getName())){
                	paramsToShare.setImageUrl(IHttpRequestUtils.HTTP_URL+"media/youlin/res/default/share/icon_youlin.png");
                	paramsToShare.setText(App.shareStr);
                	paramsToShare.setTitle("优邻");
                	paramsToShare.setTitleUrl("123.57.9.62/yl");
                	paramsToShare.setSite("优邻");
                	paramsToShare.setSiteUrl("优邻");
                	UMPlatformData platform2 = new UMPlatformData(UMedia.TENCENT_QZONE, "user_id");  
                	platform2.setName(String.valueOf(App.sUserLoginId));   
                	MobclickAgent.onSocialEvent(Invite_friend_dealactivity.this, platform2);
                }else if(Wechat.NAME.equals(platform.getName())){
                	paramsToShare.setText(App.shareStr+"\n"+"123.57.9.62/yl");
                	UMPlatformData platform1 = new UMPlatformData(UMedia.WEIXIN_FRIENDS, "user_id");  
                	platform1.setName(String.valueOf(App.sUserLoginId));   
                	MobclickAgent.onSocialEvent(Invite_friend_dealactivity.this, platform1);
                }
                
			}

	}
	/** 保存方法 */ 
	public void saveBitmap() {
		icon_path = this.getFilesDir().getParent()+File.separator+"icon_youlin.png";
		Loger.i("LYM", "111111111111-->"+icon_path);
		File f = new File(icon_path);
		if (!f.exists()) {
			Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_youlin);
			try {
				FileOutputStream out = new FileOutputStream(f);
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			return;
		}
	}
	
	private void sendSMS(String phoneNumber, String message){
		android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
		List<String> divideContents = smsManager.divideMessage(message);
		for(String text : divideContents){
			smsManager.sendTextMessage(phoneNumber, null, text, sentPI, deliverPI);
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
}
