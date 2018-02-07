package com.nfs.youlin.activity.neighbor;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.activity.titlebar.startactivity.SendActivity;
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
import com.nfs.youlin.utils.FileUtils;
import com.nfs.youlin.utils.ImageItem;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.PublicWay;
import com.nfs.youlin.utils.UploadPictureforbaoxiu;
import com.nfs.youlin.utils.XuanzhuanBitmap;
import com.nfs.youlin.utils.uploadpictureforgonggao;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

public class PropertyRepairAddActivity extends Activity {
	private ActionBar actionBar;
	private EditText addressEt;
	private EditText contentEt;
	private TextView categoryTv;
	private EditText categorydescirbe;
	private ImageView titleImg;
	private ImageView categoryImg;
	private ImageView contentImg;
	private RelativeLayout categorylayout;
	private String repaircategory= new String();
	private String repaircategorydescirbe = new String();
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
		setContentView(R.layout.activity_property_repair_add_old);
		actionBar=getActionBar();
		actionBar.setTitle("报修");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		final PopupWindow popWindow=new PopupWindow(this);
		final View view=getLayoutInflater().inflate(R.layout.activity_share_popwindow_repair, null);
		final ListView listView=(ListView)view.findViewById(R.id.share_pop_repair_lv);
		final String[] arr={"门窗","水电","墙体","垃圾","其他"};
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,arr);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				listView.setItemChecked(arg2, true);
				categoryTv.setText(arr[arg2]);
				repaircategory = arr[arg2];
				categorydescirbe.setVisibility(View.VISIBLE);
				popWindow.dismiss();
			}
		});
		addressEt=(EditText)findViewById(R.id.address_et);
		contentEt=(EditText)findViewById(R.id.content_et);
		categoryTv=(TextView)findViewById(R.id.category_tv);
		categorydescirbe = (EditText)findViewById(R.id.category_detailedit);
		titleImg=(ImageView)findViewById(R.id.address_img);
		categoryImg=(ImageView)findViewById(R.id.category_tanhao_img);
		contentImg=(ImageView)findViewById(R.id.content_img);
		categorylayout=(RelativeLayout)findViewById(R.id.category_layout);
		categorylayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new BackgroundAlpha(0.5f, PropertyRepairAddActivity.this);
				popWindow.setWidth(LayoutParams.WRAP_CONTENT);
				popWindow.setHeight(LayoutParams.WRAP_CONTENT);
				popWindow.setBackgroundDrawable(new BitmapDrawable());
				popWindow.setFocusable(true);
				popWindow.setOutsideTouchable(true);
				popWindow.setContentView(view);
				popWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
				popWindow.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						new BackgroundAlpha(1f, PropertyRepairAddActivity.this);
					}
				});
			}
		});
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
	    addressEt.setText(addrDetails);
		sendTvListen();
		addressEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
		categorydescirbe.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
		new AddPicture(this);
	}
	private Account getSenderInfo(){
		Account account = null;
		if(App.sUserLoginId > 0){
			AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(PropertyRepairAddActivity.this);
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_topic, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			new ClearSelectImg();
			finish();
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
		String sendTitleStr = addressEt.getText().toString().trim();
//		String categoryStr = categoryTv.getText().toString().trim();
		String contentStr = contentEt.getText().toString().trim();
			if (!sendTitleStr.isEmpty()) {
				if(repaircategory.length()>0){
					if(repaircategorydescirbe.length()>0){
						if(!contentStr.isEmpty()){
							if(contentStr.length()<=1000){
							if(NetworkService.networkBool){
								for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
									File file=new File(Bimp.tempSelectBitmap.get(i).getImagePath());
									if(!file.exists()){
										Toast.makeText(PropertyRepairAddActivity.this, "上传失败", 0).show();
										return;
									}
								}
								pd.show();
								sRequestParams = new RequestParams();
								sRequestParams.put("forum_id", 0); // 0表示本小区、1、周边、2.同城
								sRequestParams.put("forum_name", "");
								sRequestParams.put("topic_category_type", App.BAOXIU_TYPE);  //2 表示普通话题
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
								sRequestParams.put("topic_title", "#物业报修#"+repaircategory+"（"+repaircategorydescirbe+"）");
								sRequestParams.put("topic_content", contentStr);
//								Loger.i("TEST", "发帖当前内容->"+newTopicContentEt.getText().toString());
//								Loger.i("TEST", "发帖当前cityID->"+currentFamily.getFamily_city_id());
//								Loger.i("TEST", "发帖当前commID->"+currentFamily.getFamily_community_id());
								sRequestParams.put("sender_city_id",currentFamily.getFamily_city_id());
								sRequestParams.put("sender_community_id",currentFamily.getFamily_community_id());
								new UploadPictureforbaoxiu(PropertyRepairAddActivity.this,sRequestParams);
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
							}else{
								Toast.makeText(PropertyRepairAddActivity.this,"请先开启网络",Toast.LENGTH_SHORT).show();
							}
							}else{
								Toast.makeText(PropertyRepairAddActivity.this,"发送内容过长",Toast.LENGTH_SHORT).show();
							}
						}else{
							contentImg.setVisibility(View.VISIBLE);
						}
					}else{
						categoryImg.setVisibility(View.VISIBLE);
					}
				}else{
					Toast.makeText(PropertyRepairAddActivity.this,"请选择报修类别",Toast.LENGTH_SHORT).show();
				}	
					
			} else {
				titleImg.setVisibility(View.VISIBLE);
			}
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
	private void sendTvListen() {
		addressEt.addTextChangedListener(new TextWatcher() {
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
				addrDetails = s.toString();
			}
		});
		categorydescirbe.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				categoryImg.setVisibility(View.GONE);
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if(arg0.length()>0){
					repaircategorydescirbe = arg0.toString();
				}
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
		new BackgroundAlpha(1f, PropertyRepairAddActivity.this);
		AddPicture.adapter.notifyDataSetChanged();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new ClearSelectImg();
			finish();
		}
		return true;
	}

//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch (requestCode) {
//		case AddPicture.TAKE_PICTURE:
//			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == Activity.RESULT_OK) {
//				String fileName = String.valueOf(System.currentTimeMillis());
//				Bitmap bm = (Bitmap) data.getExtras().get("data");
//				FileUtils.saveBitmap(bm, fileName);
//				ImageItem takePhoto = new ImageItem();
//				takePhoto.setBitmap(bm);
//				Bimp.tempSelectBitmap.add(takePhoto);
//				AddPicture.adapter.update();
//			}
//			break;
//		}
//	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
	}
}
