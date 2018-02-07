package com.nfs.youlin.activity;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
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
import com.nfs.youlin.utils.UploadPicture;
import com.nfs.youlin.utils.UploadPictureStore;
import com.nfs.youlin.utils.XuanzhuanBitmap;
import com.nfs.youlin.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import cn.jpush.android.api.JPushInterface;

public class NewPushStoreCommentActivity extends Activity{
	EditText evaluateEt;
	Bimp bimp;
	Bitmap bitmap;
	Bitmap newbitmap;
	ImageView tanhaoImg;
	CheckBox nameBox;
	String evatag;
	String uid;
	String shopName;
	LinearLayout foodStarLayout;
	LinearLayout attitudeStarLayout;
	LinearLayout envStarlayout;
	RatingBar foodStar;
	RatingBar attitudeStar;
	RatingBar envStar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_new_push_store_comment);
		foodStarLayout=(LinearLayout)findViewById(R.id.food_star_layout);
		attitudeStarLayout=(LinearLayout)findViewById(R.id.attitude_star_layout);
		envStarlayout=(LinearLayout)findViewById(R.id.env_star_layout);
		foodStar=(RatingBar)findViewById(R.id.food_star);
		attitudeStar=(RatingBar)findViewById(R.id.attitude_star);
		envStar=(RatingBar)findViewById(R.id.env_star);
		Intent intent=getIntent();
		evatag=intent.getStringExtra("tag");
		uid=intent.getStringExtra("uid");
		shopName=intent.getStringExtra("shop_name");
		Loger.i("LYM", "11111111111--->"+evatag+" "+uid);
		if(evatag.equals("0")){
			foodStarLayout.setVisibility(View.VISIBLE);
			attitudeStarLayout.setVisibility(View.VISIBLE);
			envStarlayout.setVisibility(View.VISIBLE);
		}else if(evatag.equals("1") || evatag.equals("2")){
			foodStarLayout.setVisibility(View.GONE);
			attitudeStarLayout.setVisibility(View.VISIBLE);
			envStarlayout.setVisibility(View.VISIBLE);
		}
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(shopName);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		bimp=new Bimp();
		evaluateEt=(EditText)findViewById(R.id.evaluate_et);
		evaluateEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
		nameBox=(CheckBox)findViewById(R.id.check);
		tanhaoImg=(ImageView)findViewById(R.id.store_tanhao_img);
		new AddPicture(this);
		sendTvListen();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
		new BackgroundAlpha(1f, this);
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
	private void sendTvListen() {
		evaluateEt.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				tanhaoImg.setVisibility(View.GONE);
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
	public void ExitDialog() {
		CustomDialog.Builder builder=new CustomDialog.Builder(NewPushStoreCommentActivity.this);
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
	private void sendFinish() {
		ProgressDialog pd=new ProgressDialog(this);
		pd.setMessage("发布中...");
			String sendTitleStr = evaluateEt.getText().toString().trim();
				if (!sendTitleStr.isEmpty()) {
					
					if(NetworkService.networkBool){
						for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
							File file=new File(Bimp.tempSelectBitmap.get(i).getImagePath());
							if(!file.exists()){
								Toast.makeText(NewPushStoreCommentActivity.this, "上传失败", 0).show();
								return;
							}
						}
						pd.show();
						RequestParams sRequestParams = new RequestParams();
						sRequestParams.put("sender_id", App.sUserLoginId);
						Loger.i("LYM", "newpush--->"+uid);
						sRequestParams.put("uid",uid);
						if(Bimp.tempSelectBitmap.size()==0){
							sRequestParams.put("type", 0); 
						}else{
							sRequestParams.put("type", 1); 
						}
						sRequestParams.put("content", evaluateEt.getText().toString());
						if(evatag.equals("0")){
							sRequestParams.put("facility",attitudeStar.getRating()+":"+foodStar.getRating()+":"+envStar.getRating());
						}else if(evatag.equals("1") || evatag.equals("2")){
							sRequestParams.put("facility",attitudeStar.getRating()+":0:"+envStar.getRating());
						}
						
						if(nameBox.isChecked()){
							sRequestParams.put("status",1);
						}else{
							sRequestParams.put("status",0);
						}
						sRequestParams.put("evatag",evatag);
						new UploadPictureStore(NewPushStoreCommentActivity.this,sRequestParams,1);
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
						Toast.makeText(NewPushStoreCommentActivity.this,"请先开启网络", Toast.LENGTH_SHORT).show();
					}
					
				} else {
					tanhaoImg.setVisibility(View.VISIBLE);
				}
			
		
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
	}
	
}
