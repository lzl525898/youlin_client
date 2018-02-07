package com.nfs.youlin.activity.personal;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Handler;
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
public class PersonalInfoSignatureActivity extends Activity {
	private EditText changeSignEt;
	private String changeSignStr;
	private NeighborsHttpRequest httpRequest;
	private final int SUCCESS_CODE = 100;
	private final int FAILED_CODE = 101;
	private final int REFUSE_CODE = 103;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCESS_CODE:
				PersonalInformationActivity.SignTv.setText(changeSignStr);
				saveSharePrefrence(changeSignStr);
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
		setContentView(R.layout.activity_personal_info_signature);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("个性签名");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		String defaultSign=PersonalInformationActivity.SignTv.getText().toString();
		changeSignEt =(EditText)findViewById(R.id.sign_et);
		Loger.i("TEST", "default=->"+defaultSign);
		if(defaultSign.toString().equals("null")){
			changeSignEt.setText("");
		}else{
			changeSignEt.setText(defaultSign);
		}
		
		
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
		changeSignStr=changeSignEt.getText().toString().trim();
		if(!changeSignStr.isEmpty()){
			finish();
			if(NetworkService.networkBool){
				httpRequest = new NeighborsHttpRequest(PersonalInfoSignatureActivity.this);
				RequestParams params = new RequestParams();
				params.put("user_id", App.sUserLoginId);
				params.put("user_phone_number", App.sUserPhone);
				params.put("user_signature", changeSignStr);
				params.put("tag", "upload");
				params.put("apitype", IHttpRequestUtils.APITYPE[0]);
				httpRequest.updateUserInfo(params,handler);
			}
		}else{
			Toast.makeText(PersonalInfoSignatureActivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void saveSharePrefrence(String strInfo){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("signature", strInfo);
		sharedata.commit();
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
