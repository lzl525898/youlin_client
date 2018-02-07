package com.nfs.youlin.activity.personal;

import cn.jpush.android.api.JPushInterface;

import com.easemob.chat.EMChatManager;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.RegisterActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.YLStringVerification;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class PersonalInfoNameActivity extends Activity {
	private NeighborsHttpRequest httpRequest;
	private AccountDaoDBImpl accountDaoDBImpl;
	private EditText changeNameEt;
	private String changeNameStr;
	private final int SUCCESS_CODE = 100;
	private final int FAILED_CODE = 101;
	private final int REFUSE_CODE = 103;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCESS_CODE:
				//MyInformationFragment.accountNameTv.setText(changeNameStr);
				PersonalInformationActivity.nameTv.setText(changeNameStr);
				Account account=accountDaoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
				account.setUser_name(changeNameStr);
				accountDaoDBImpl.modifyObject(account);
				accountDaoDBImpl.releaseDatabaseRes();
				saveSharePrefrence(changeNameStr);
				new Thread(new Runnable() {
					public void run() {
						boolean myBool=EMChatManager.getInstance().updateCurrentUserNick(changeNameStr);
						Loger.i("TEST", "updateCurrentUserNick-->"+myBool);
					}
				}).start();
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
		setContentView(R.layout.activity_personal_info_name);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("昵称");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		accountDaoDBImpl=new AccountDaoDBImpl(this);
		String defaultName=PersonalInformationActivity.nameTv.getText().toString();
		changeNameEt=(EditText)findViewById(R.id.changeName);
		changeNameEt.setText(defaultName);
	}

	private void saveSharePrefrence(String username){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("username", username);
		sharedata.commit();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_info_signature, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.finish:
			OnClickFinish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void OnClickFinish(){
		changeNameStr=changeNameEt.getText().toString().trim();
		if(!changeNameStr.isEmpty()){
			if(YLStringVerification.checkNickNameBiaoQing(changeNameStr) && !changeNameStr.equals("null")){
				if(NetworkService.networkBool){
					finish();
					httpRequest = new NeighborsHttpRequest(PersonalInfoNameActivity.this);
					RequestParams params = new RequestParams();
					params.put("user_id", App.sUserLoginId);
					params.put("user_phone_number", App.sUserPhone);
					params.put("user_nick", changeNameStr);
					params.put("tag", "upload");
					params.put("apitype", IHttpRequestUtils.APITYPE[0]);
					httpRequest.updateUserInfo(params,handler);
				}else{
					Toast.makeText(PersonalInfoNameActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(PersonalInfoNameActivity.this, "名字格式不正确",Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(PersonalInfoNameActivity.this, "名字不能为空", Toast.LENGTH_SHORT).show();
		}
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
