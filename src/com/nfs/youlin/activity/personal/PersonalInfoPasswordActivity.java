package com.nfs.youlin.activity.personal;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import cn.jpush.android.api.JPushInterface;
import com.nfs.youlin.R;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.AESandCBC;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.MD5Util;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.sax.TextElementListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class PersonalInfoPasswordActivity extends Activity {
	private EditText pwdOldEt;
	private EditText pwdNewEt;
	private EditText pwdAgainEt;
	private ImageView pwdOldImg;
	private ImageView pwdNewImg;
	private ImageView pwdAgainImg;
	private TextView pwdOldRemindTv;
	private TextView pwdNewRemindTv;
	private TextView pwdAgainRemindTv;
	private String[] userInfo = new String[2];
	private String encryptedPwd;
	private NeighborsHttpRequest httpRequest;
	private final int SUCCESS_CODE = 100;
	private final int FAILED_CODE = 101;
	private final int REFUSE_CODE = 103;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCESS_CODE:
				saveSharePrefrence(encryptedPwd, App.sUserPhone);
				Toast.makeText(PersonalInfoPasswordActivity.this,
						"修改成功", 1).show();
				finish();
				Loger.i("TEST", "ok...................");
				break;
			case FAILED_CODE:
				Toast.makeText(PersonalInfoPasswordActivity.this,
						"原密码错误", 1).show();
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
		setContentView(R.layout.activity_personal_info_password);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("修改密码");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		
		pwdOldRemindTv=(TextView)findViewById(R.id.pwdOldRemind);
		pwdNewRemindTv=(TextView)findViewById(R.id.pwdNewRemind);
		pwdAgainRemindTv=(TextView)findViewById(R.id.pwdAgainRemind);
		pwdOldEt=(EditText)findViewById(R.id.pwdOldId);
		pwdNewEt=(EditText)findViewById(R.id.pwdNewId);
		pwdAgainEt=(EditText)findViewById(R.id.pwdAgainId);
		pwdOldImg=(ImageView)findViewById(R.id.pwdOldImg);
		pwdNewImg=(ImageView)findViewById(R.id.pwdNewImg);
		pwdAgainImg=(ImageView)findViewById(R.id.pwdAgainImg);
		pwdOldEt.addTextChangedListener(new TextWatcher() {
				
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				pwdOldImg.setVisibility(View.VISIBLE);
				pwdOldImg.setBackgroundResource(R.drawable.chahao);
				pwdOldRemindTv.setVisibility(View.GONE);
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
		
		pwdNewEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				pwdNewImg.setVisibility(View.VISIBLE);
				pwdNewImg.setBackgroundResource(R.drawable.chahao);
				pwdNewRemindTv.setVisibility(View.GONE);
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
		
		pwdAgainEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				pwdAgainImg.setVisibility(View.VISIBLE);
				pwdAgainImg.setBackgroundResource(R.drawable.chahao);
				pwdAgainRemindTv.setVisibility(View.GONE);
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
		
		pwdOldImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pwdOldEt.setText("");
				pwdOldImg.setVisibility(View.GONE);
			}
		});
		
		pwdNewImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pwdNewEt.setText("");
				pwdNewImg.setVisibility(View.GONE);
			}
		});
		
		pwdAgainImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pwdAgainEt.setText("");
				pwdAgainImg.setVisibility(View.GONE);
			}
		});
		
		
	}
	
	private void getUserPhone() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String phone = sharedata.getString("phone", null);
		String encryption = sharedata.getString("encryption", null);
		userInfo[0] = phone;
		userInfo[1] = encryption;
	}
	
	private void saveSharePrefrence(String pwd, String phone){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("encryption", pwd);
		sharedata.putString("phone", phone);
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
	
	public void OnClickFinish() {
		if (NetworkService.networkBool) {
			getUserPhone();
			if (!App.sUserPhone.equals(userInfo[0])) {
				return;
			}
			String pwdOldName = pwdOldEt.getText().toString();
			if (pwdOldName.isEmpty()) {
				pwdOldImg.setBackgroundResource(R.drawable.tanhao);
				pwdOldImg.setVisibility(View.VISIBLE);
				pwdOldRemindTv.setVisibility(View.VISIBLE);
			}
			String pwdNewName = pwdNewEt.getText().toString();
			if (pwdNewName.isEmpty()) {
				pwdNewImg.setBackgroundResource(R.drawable.tanhao);
				pwdNewImg.setVisibility(View.VISIBLE);
				pwdNewRemindTv.setVisibility(View.VISIBLE);
			}
			if (pwdNewName.length() < 6) {
				pwdNewImg.setBackgroundResource(R.drawable.tanhao);
				pwdNewImg.setVisibility(View.VISIBLE);
				pwdNewRemindTv.setVisibility(View.VISIBLE);
			}
			String pwdAgainName = pwdAgainEt.getText().toString();
			if (pwdAgainName.isEmpty()) {
				pwdAgainImg.setBackgroundResource(R.drawable.tanhao);
				pwdAgainImg.setVisibility(View.VISIBLE);
				pwdAgainRemindTv.setVisibility(View.VISIBLE);
			}
			if (pwdNewName.equals(pwdAgainName) == false) {
				pwdAgainImg.setBackgroundResource(R.drawable.tanhao);
				pwdAgainImg.setVisibility(View.VISIBLE);
				pwdAgainRemindTv.setVisibility(View.VISIBLE);
			}
//			try {
				if (!pwdOldName.isEmpty() && !pwdNewName.isEmpty()&&
						!pwdAgainName.isEmpty()&& pwdNewName.equals(pwdAgainName) && 
						pwdNewName.length() >=6 ) {
					// 插入数据库操作
//					if (MD5Util.validPassword(pwdOldName, userInfo[1])) {
//						try {
//							encryptedPwd = MD5Util.getEncryptedPwd(pwdNewName);
//						} catch (NoSuchAlgorithmException e) {
//							e.printStackTrace();
//						} catch (UnsupportedEncodingException e) {
//							e.printStackTrace();
//						}
//	
						String encryptedOldPwd = null;
						try {
							encryptedOldPwd = MD5Util.getEncryptedPwd(pwdOldName, App.sUserPhone);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							encryptedOldPwd = "0";
							e.printStackTrace();
						}
						String encryptedPwd = null;
						try {
							encryptedPwd = MD5Util.getEncryptedPwd(pwdNewName, App.sUserPhone);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							encryptedPwd = "0";
							e.printStackTrace();
						}
						Loger.d("test4", "encryptedPwd="+encryptedPwd+"pwd="+pwdNewName+"tel="+App.sUserPhone+"encryptedoldPwd="+encryptedOldPwd+"oldpwd="+pwdOldName);
						httpRequest = new NeighborsHttpRequest(PersonalInfoPasswordActivity.this);
						RequestParams params = new RequestParams();
						params.put("user_id", App.sUserLoginId);
						params.put("user_phone_number", App.sUserPhone);
						params.put("user_password", encryptedPwd);
						params.put("user_oldpassword", encryptedOldPwd);
						params.put("tag", "modifypwd");
						params.put("apitype", IHttpRequestUtils.APITYPE[0]);
						httpRequest.updatePassword(params, handler);
//					} else {
//						if (!MD5Util.validPassword(pwdOldName, userInfo[1])) {
//							Toast.makeText(PersonalInfoPasswordActivity.this,
//									"原密码不正确", 1).show();
//						}
//					}
					
				}
//			} catch (NoSuchAlgorithmException e) {
//				// TODO Auto-generated catch block
//
//				e.printStackTrace();
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(PersonalInfoPasswordActivity.this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}else{
			Toast.makeText(PersonalInfoPasswordActivity.this,"网络有问题", 1).show();
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
