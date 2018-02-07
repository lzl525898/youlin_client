package com.nfs.youlin.activity.personal;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.R.drawable;
import com.nfs.youlin.R.id;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StrToTime;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class PersonalInfoSexActivity extends Activity {

	private RelativeLayout maleLayout;
	private RelativeLayout femalLayout;
	private RelativeLayout secretLayout;
	private ImageView maleImage;
	private ImageView femaleImage;
	private ImageView secretImage;
	private Account account; 
	private AccountDaoDBImpl accountDaoDBImpl;
	private NeighborsHttpRequest httpRequest;
	private final int SUCCESS_CODE = 100;
	private final int FAILED_CODE = 101;
	private final int REFUSE_CODE = 103;
	private int uSex = -1;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCESS_CODE:
				account.setUser_portrait(String.valueOf(msg.obj));
				if(1 == uSex){
					account.setUser_gender(1);
					accountDaoDBImpl.modifyObject(account);
					accountDaoDBImpl.releaseDatabaseRes();
					show(1);
				}else if(2 == uSex){
					account.setUser_gender(2);
					accountDaoDBImpl.modifyObject(account);
					accountDaoDBImpl.releaseDatabaseRes();
					show(2);
				}else if(3 == uSex){
					account.setUser_gender(3);
					accountDaoDBImpl.modifyObject(account);
					accountDaoDBImpl.releaseDatabaseRes();
					show(3);
				}else{
					break;
				}
				finish();
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info_sex);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("性别");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		
		maleLayout = (RelativeLayout) findViewById(R.id.re_male);
		femalLayout = (RelativeLayout) findViewById(R.id.re_female);
		secretLayout = (RelativeLayout) findViewById(R.id.re_secret);
		maleImage = (ImageView) findViewById(R.id.image_male);
		femaleImage = (ImageView) findViewById(R.id.image_female);
		secretImage = (ImageView) findViewById(R.id.image_secret);
		
		accountDaoDBImpl = new AccountDaoDBImpl(PersonalInfoSexActivity.this);
		account = accountDaoDBImpl.findAccountByPhone(App.sUserPhone);
		if(account.getUser_gender()==1){
			maleImage.setImageResource(R.drawable.btn_sex_duihao);
		}
		if(account.getUser_gender()==2){
			femaleImage.setImageResource(R.drawable.btn_sex_duihao);
		}
		if(account.getUser_gender()==3){
			secretImage.setImageResource(R.drawable.btn_sex_duihao);
		}
		maleLayout.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				maleImage.setImageResource(R.drawable.btn_sex_duihao);
				femaleImage.setVisibility(View.INVISIBLE);
				secretImage.setVisibility(View.INVISIBLE);
				uSex = 1;
				if(NetworkService.networkBool){
					httpRequest = new NeighborsHttpRequest(PersonalInfoSexActivity.this);
					RequestParams params = new RequestParams();
					params.put("user_id", App.sUserLoginId);
					params.put("user_phone_number", App.sUserPhone);
					params.put("user_gender", 1);
					params.put("tag", "upload");
					params.put("apitype", IHttpRequestUtils.APITYPE[0]);
					httpRequest.updateUserInfo(params,handler);
				}else{
					Toast.makeText(PersonalInfoSexActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				}
			}
		});
		femalLayout.setOnClickListener(new OnClickListener() 
		{
			
			public void onClick(View v) {
			    femaleImage.setImageResource(R.drawable.btn_sex_duihao);
				maleImage.setVisibility(View.INVISIBLE);
				secretImage.setVisibility(View.INVISIBLE);
				uSex = 2;
				if(NetworkService.networkBool){
					httpRequest = new NeighborsHttpRequest(PersonalInfoSexActivity.this);
					RequestParams params = new RequestParams();
					params.put("user_id", App.sUserLoginId);
					params.put("user_phone_number", App.sUserPhone);
					params.put("user_gender", 2);
					params.put("tag", "upload");
					params.put("apitype", IHttpRequestUtils.APITYPE[0]);
					httpRequest.updateUserInfo(params,handler);
				}else{
					Toast.makeText(PersonalInfoSexActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				}
			}
		});
		secretLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				secretImage.setImageResource(R.drawable.btn_sex_duihao);
				maleImage.setVisibility(View.INVISIBLE);
				femaleImage.setVisibility(View.INVISIBLE);
				uSex = 3;
				if(NetworkService.networkBool){
					httpRequest = new NeighborsHttpRequest(PersonalInfoSexActivity.this);
					RequestParams params = new RequestParams();
					params.put("user_id", App.sUserLoginId);
					params.put("user_phone_number", App.sUserPhone);
					params.put("user_gender", 3);
					params.put("tag", "upload");
					params.put("apitype", IHttpRequestUtils.APITYPE[0]);
					httpRequest.updateUserInfo(params,handler);
				}else{
					Toast.makeText(PersonalInfoSexActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void show(int sex)
	{
		if(sex == 1)
			PersonalInformationActivity.sexTv.setText("男");
		else if(sex == 2)
			PersonalInformationActivity.sexTv.setText("女");
		else if(sex == 3)
			PersonalInformationActivity.sexTv.setText("保密");
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
}
