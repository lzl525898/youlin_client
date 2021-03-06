package com.nfs.youlin.activity.titlebar.newtopic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import u.aly.da;
import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
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
import com.nfs.youlin.utils.BitmapCache;
import com.nfs.youlin.utils.ClearSelectImg;
import com.nfs.youlin.utils.FileUtils;
import com.nfs.youlin.utils.ImageItem;
import com.nfs.youlin.utils.ImageThumbnail;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.UploadPicture;
import com.nfs.youlin.utils.XuanzhuanBitmap;
import com.nfs.youlin.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NewTopic extends Activity implements OnClickListener {
	public static TextView newTopicTv;
	private ActionBar actionBar;
	private LinearLayout newTopicLayout;
	private EditText newTopicTitleEt;
	private EditText newTopicContentEt;
	private ImageView newTopicSendToImg;
	private ImageView newTopicTitleImg;
	public Thread runQueryThread;
	private Account account;
	private AllFamilyDaoDBImpl curfamilyDaoDBImpl;
	private AllFamily currentFamily;
	private String communityDetail;
	private String addrDetails;
	public  RequestParams sRequestParams;
	Bitmap bitmap;
	//Bitmap bitMap;
	Bitmap newbitmap;
	Bimp bimp;
	public static int nforumId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_new_topic);
		actionBar = getActionBar();
		actionBar.setTitle("话题");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		bimp=new Bimp();
		newTopicLayout = (LinearLayout) findViewById(R.id.new_topic_layout);
		newTopicTv = (TextView) findViewById(R.id.new_topic_tv);
		newTopicTv.setText("本小区");
		newTopicTitleEt = (EditText) findViewById(R.id.new_topic_title_et);
		newTopicContentEt = (EditText) findViewById(R.id.new_topic_content_et);
		newTopicSendToImg = (ImageView) findViewById(R.id.new_topic_send_to_img);
		newTopicTitleImg = (ImageView) findViewById(R.id.new_topic_title_img);
		new AddPicture(this);
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
		newTopicLayout.setOnClickListener(this);
		newTopicTitleEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
		sendTvListen();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
		new BackgroundAlpha(1f, NewTopic.this);
		AddPicture.adapter.notifyDataSetChanged();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExitDialog();
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AddPicture.TAKE_PICTURE:
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == Activity.RESULT_OK){
//				File file=new File(Environment.getExternalStorageDirectory(), "circleImg.jpg");
				String str=Environment.getExternalStorageDirectory()+File.separator+"circleImg.jpg";	
				try {
					bitmap=bimp.revitionImageSize(str);
					//Bimp.bitmapList.add(bitmap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				XuanzhuanBitmap xuanzhuanBitmap=new XuanzhuanBitmap();
                int degree=xuanzhuanBitmap.readPictureDegree(str);
                newbitmap = xuanzhuanBitmap.rotaingImageView(degree, bitmap);
               // Bimp.bitmapList.add(newbitmap);
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
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.new_topic_layout:
			Intent intent = new Intent(this, NewTopicRange.class);
			intent.putExtra("village", communityDetail);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void sendFinish() {
		ProgressDialog pd=new ProgressDialog(this);
		pd.setMessage("发布中...");
			String sendToStr = newTopicTv.getText().toString().trim();
			String sendTitleStr = newTopicTitleEt.getText().toString().trim();
			String sendContentStr= newTopicContentEt.getText().toString().trim();
			Loger.i("youlin", "sendToStr--->" + sendToStr);
			if (!sendToStr.isEmpty()) {
				if (!sendTitleStr.isEmpty()) {
					if(sendContentStr.length()<=1000){
					if(NetworkService.networkBool){
						for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
							File file=new File(Bimp.tempSelectBitmap.get(i).getImagePath());
							if(!file.exists()){
								Toast.makeText(NewTopic.this, "上传失败", 0).show();
								return;
							}
						}
						pd.show();
						sRequestParams = new RequestParams();
						sRequestParams.put("forum_id", NewTopic.nforumId); //0表示本小区、1、周边、2.同城
						sRequestParams.put("forum_name", sendToStr);
						sRequestParams.put("topic_category_type", 2);  //2 表示普通话题 345 物业
						sRequestParams.put("sender_id", account.getLogin_account());
						sRequestParams.put("sender_name", account.getUser_name());
						sRequestParams.put("sender_lever", account.getUser_level()); 
						sRequestParams.put("sender_portrait", account.getUser_portrait());
						sRequestParams.put("sender_family_id", account.getUser_family_id());
						sRequestParams.put("sender_family_address", account.getUser_family_address()); 
						sRequestParams.put("sender_nc_role", 0);
						sRequestParams.put("display_name", communityDetail!=null?account.getUser_name()+"@"+communityDetail:null);
						sRequestParams.put("object_data_id", 0);  //0.表示一般话题、1.表示活动  3.news
						sRequestParams.put("circle_type", 1);  //1.表示一般话题
						sRequestParams.put("topic_title", sendTitleStr);
						sRequestParams.put("topic_content", newTopicContentEt.getText().toString());
						sRequestParams.put("sender_city_id",currentFamily.getFamily_city_id());
						sRequestParams.put("sender_community_id",currentFamily.getFamily_community_id());
						new UploadPicture(NewTopic.this,sRequestParams,1);
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
						MobclickAgent.onEvent(NewTopic.this,"addTopic");
					}else{
//						if(pd!=null){
//							pd.cancel();
//						}
						Toast.makeText(NewTopic.this,"请先开启网络", Toast.LENGTH_SHORT).show();
					}
					}else{
						Toast.makeText(NewTopic.this,"发送内容太长", Toast.LENGTH_SHORT).show();
					}
				} else {
					newTopicTitleImg.setVisibility(View.VISIBLE);
				}
			} else {
				newTopicSendToImg.setVisibility(View.VISIBLE);
			}
		
	}

	private void sendTvListen() {
		newTopicTv.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				newTopicSendToImg.setVisibility(View.GONE);
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			public void afterTextChanged(Editable s) {
			}
		});
		newTopicTitleEt.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				newTopicTitleImg.setVisibility(View.GONE);
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_topic, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
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
	
	private Account getSenderInfo(){
		Account account = null;
		if(App.sUserLoginId > 0){
			AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(NewTopic.this);
			account = daoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
		}
		return account;
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
	public void ExitDialog() {
		CustomDialog.Builder builder=new CustomDialog.Builder(NewTopic.this);
		//builder.setTitle("提示");
		builder.setMessage("确定要放弃此次编辑吗？");
		//builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
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
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
	}
}
