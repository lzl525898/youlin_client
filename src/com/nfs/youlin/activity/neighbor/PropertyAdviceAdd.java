package com.nfs.youlin.activity.neighbor;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.AddPicture;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.BackgroundAlpha;
import com.nfs.youlin.utils.Bimp;
import com.nfs.youlin.utils.ClearSelectImg;
import com.nfs.youlin.utils.CurrentTime;
import com.nfs.youlin.utils.FileUtils;
import com.nfs.youlin.utils.ImageItem;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.XuanzhuanBitmap;
import com.nfs.youlin.utils.uploadpictureforadvice;
import com.nfs.youlin.utils.uploadpictureforgonggao;
import com.nfs.youlin.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

public class PropertyAdviceAdd extends Activity{
	private ActionBar actionBar;
	private EditText titleEt;
	private ImageView titleImg;
	private EditText contentEt;
	private ImageView contentImg;
	private int CHOOSE_GONGGAO_TYPE = 1;
	public  RequestParams sRequestParams;
	private AllFamilyDaoDBImpl curfamilyDaoDBImpl;
	private AllFamily currentFamily;
	private Account account;
	private String communityDetail;
	private String addrDetails;
	Bitmap bitmap;
	//Bitmap bitMap;
	Bitmap newbitmap;
	Bimp bimp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property_advice_add);
		actionBar=getActionBar();
		actionBar.setTitle("物业建议");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		titleEt=(EditText)findViewById(R.id.property_advice_add_title_et);
		titleImg=(ImageView)findViewById(R.id.property_advice_add_title_img);
		contentEt=(EditText)findViewById(R.id.property_advice_add_content_et);
		contentImg=(ImageView)findViewById(R.id.property_advice_add_content_img);
		bimp=new Bimp();
		account = getSenderInfo();
	    if(account==null){
	    	Loger.i("TEST", "未找到loginId");
	    	return;
	    }
	    addrDetails = getAddrDetail();
	    if(addrDetails==null){
	    	Loger.i("TEST", "地址信息错误");
	    	return;
	    }
	    communityDetail = getCommunityDetail();
	    curfamilyDaoDBImpl = new AllFamilyDaoDBImpl(this);
	    currentFamily = curfamilyDaoDBImpl.getCurrentAddrDetail(addrDetails);
		new AddPicture(this);
		titleEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
		sendTvListen();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_topic, menu);
		return true;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
		new BackgroundAlpha(1f, PropertyAdviceAdd.this);
		AddPicture.adapter.notifyDataSetChanged();
	}
	private Account getSenderInfo(){
		Account account = null;
		if(App.sUserLoginId > 0){
			AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(PropertyAdviceAdd.this);
			account = daoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
		}
		return account;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			ExitDialog();
			break;
		case R.id.finish:
			sendFinish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void sendFinish() {
		ProgressDialog pd=new ProgressDialog(this);
		pd.setMessage("发布中...");
		String sendTitleStr = titleEt.getText().toString().trim();
		String contentStr = contentEt.getText().toString().trim();
			if (!sendTitleStr.isEmpty()) {
				if(!contentStr.isEmpty()){
					if(contentStr.length()<=1000){
					if(NetworkService.networkBool){
						//上传服务器
						for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
							File file=new File(Bimp.tempSelectBitmap.get(i).getImagePath());
							if(!file.exists()){
								Toast.makeText(PropertyAdviceAdd.this, "上传失败", 0).show();
								return;
							}
						}
						pd.show();
						CurrentTime currentTime=new CurrentTime();
						String time=currentTime.getCurTime();
						sRequestParams = new RequestParams();
						sRequestParams.put("forum_id", 0); // 0表示本小区、1、周边、2.同城
						sRequestParams.put("forum_name", "");
						sRequestParams.put("topic_category_type", 5);  //2 表示普通话题    5表示物业建议
						sRequestParams.put("sender_id", account.getLogin_account());
						sRequestParams.put("sender_name", account.getUser_name());
						sRequestParams.put("sender_lever", account.getUser_level()); 
						sRequestParams.put("sender_portrait", account.getUser_portrait());
						sRequestParams.put("sender_family_id", account.getUser_family_id());
						sRequestParams.put("sender_family_address", account.getUser_family_address()); 
						sRequestParams.put("sender_nc_role", 0);
						sRequestParams.put("display_name", communityDetail!=null?account.getUser_name()+"@"+communityDetail:null);
						sRequestParams.put("object_data_id", 0);  //0.表示一般话题、1.表示活动
						sRequestParams.put("circle_type", 1);  //1.表示一般话题
						sRequestParams.put("topic_title", "#建议#"+sendTitleStr);
						sRequestParams.put("topic_content", contentStr);
//						Loger.i("TEST", "发帖当前内容->"+newTopicContentEt.getText().toString());
//						Loger.i("TEST", "发帖当前cityID->"+currentFamily.getFamily_city_id());
//						Loger.i("TEST", "发帖当前commID->"+currentFamily.getFamily_community_id());
						sRequestParams.put("sender_city_id",currentFamily.getFamily_city_id());
						sRequestParams.put("sender_community_id",currentFamily.getFamily_community_id());
						new uploadpictureforadvice(PropertyAdviceAdd.this,sRequestParams);
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									new ClearSelectImg();
									finish();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}, 20000);
						if(bitmap!=null){
							if(!bitmap.isRecycled()){
								bitmap.recycle();
							}
						}
						if(newbitmap!=null){
							if(!newbitmap.isRecycled()){
								newbitmap.recycle();
							}
						}
						
					}
					else{
						Toast.makeText(PropertyAdviceAdd.this,"请先开启网络", Toast.LENGTH_SHORT).show();
					}
					}else{
						Toast.makeText(PropertyAdviceAdd.this,"发送内容过长", Toast.LENGTH_SHORT).show();
					}
				}else{
					contentImg.setVisibility(View.VISIBLE);
				}
			} else {
				titleImg.setVisibility(View.VISIBLE);
			}
	} 
	
	private void sendTvListen() {
		titleEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				titleImg.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
//				if(s.toString().length()==0){
//					titleEt.setVisibility(View.GONE);
//				}
			}
		});
		contentEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				contentImg.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) { // 清空消息
			if(requestCode== AddPicture.TAKE_PICTURE){
				if (Bimp.tempSelectBitmap.size() < 9){
//					File file=new File(Environment.getExternalStorageDirectory(), "circleImg.jpg");
					String str=Environment.getExternalStorageDirectory()+File.separator+"circleImg.jpg";	
					try {
						bitmap=bimp.revitionImageSize(str);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					XuanzhuanBitmap xuanzhuanBitmap=new XuanzhuanBitmap();
	                int degree=xuanzhuanBitmap.readPictureDegree(str);
	                newbitmap = xuanzhuanBitmap.rotaingImageView(degree, bitmap);
	                
					String fileName = String.valueOf(System.currentTimeMillis());
					//Bitmap bm = (Bitmap) data.getExtras().get("data");
					FileUtils.saveBitmap(newbitmap,fileName);
	                
					ImageItem takePhoto = new ImageItem();
					takePhoto.setBitmap(newbitmap);
					takePhoto.setImagePath(Environment.getExternalStorageDirectory()+ "/Photo_LJ/"+fileName+".jpg");
					takePhoto.setThumbnailPath(Environment.getExternalStorageDirectory()+ "/Photo_LJ/"+fileName+".jpg");
					Bimp.tempSelectBitmap.add(takePhoto);
					AddPicture.adapter.notifyDataSetChanged();
					
				}
			}
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExitDialog();
		}
		return true;
	}
	public void ExitDialog() {
		CustomDialog.Builder builder=new CustomDialog.Builder(PropertyAdviceAdd.this);
		//builder.setTitle("提示");
		builder.setMessage("确定要放弃此次编辑吗？");
		//builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(bitmap!=null){
					if(!bitmap.isRecycled()){
						bitmap.recycle();
					}
				}
				if(newbitmap!=null){
					if(!newbitmap.isRecycled()){
						newbitmap.recycle();
					}
				}
				new ClearSelectImg();
				finish();
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
	private String getCommunityDetail(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String commString = sharedata.getString("village", null);
		return commString;
	}
	private String getAddrDetail(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String addrDetail = null;
		String city = sharedata.getString("city", null);
		String village = sharedata.getString("village", null);
		String detail = sharedata.getString("detail", null);
		if(city!=null && village!=null && detail!=null){
			addrDetail = city+village+detail;
		}
		return addrDetail;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
	}
}
