package com.nfs.youlin.activity.find;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.view.QCodeDlgActivity;
import com.nfs.youlin.view.VerificationToast;
import com.umeng.analytics.MobclickAgent;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import cn.jpush.android.api.JPushInterface;

public class AddrVerifiyActivity extends Activity {
	private final int RESULT_OK_MY = 4000; //#审核成功
	private final int RESULT_NO = 4001; //#审核失败
	private final int RESULT_ER = 4002; //#格式错误
	private final int VCODE = 10001;
	private final int QCODE = 10002;
	private final int ADDRS = 10003;
	
	private VerificationToast verificationToast = null;
	private Long familyRecordId;
	private Long familyId;
	private int fromAddr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addr_verifity);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("地址验证");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		familyRecordId = this.getIntent().getLongExtra("familyRecordId", 0);
		familyId       = this.getIntent().getLongExtra("familyId", 0);
		try {
			fromAddr = this.getIntent().getIntExtra("fromAddr", 0);
			Loger.i("TEST", "fromAddr=>"+fromAddr);
		} catch (Exception e) {
			e.printStackTrace();
			fromAddr = 0;
		}
		ImageView topImg=(ImageView)findViewById(R.id.iv_addr_veritity);
		WindowManager manager=(WindowManager)getSystemService(Context.WINDOW_SERVICE);
		int hei=manager.getDefaultDisplay().getHeight();
		LayoutParams params=topImg.getLayoutParams();
		params.height= (int) (hei*(0.27));
		topImg.setLayoutParams(params);
	}
	
	@Override
	protected void onResume() {
		if(verificationToast==null){
			verificationToast = new VerificationToast(AddrVerifiyActivity.this);
		}
		JPushInterface.onResume(AddrVerifiyActivity.this);
		MobclickAgent.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		JPushInterface.onPause(AddrVerifiyActivity.this);
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
	public void clickQCode(View view){
		Intent intent = new Intent(AddrVerifiyActivity.this, CaptureActivity.class);
		intent.putExtra("familyRecordId", familyRecordId);
		intent.putExtra("familyId", familyId);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intent, QCODE);
	}
	
	public void clickVerifyCode(View view){
		Intent intent = new Intent(AddrVerifiyActivity.this, VerificationCodeActivity.class);
		intent.putExtra("familyRecordId", familyRecordId);
		intent.putExtra("familyId", familyId);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intent, VCODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(RESULT_OK == resultCode){
			Bundle bundle = data.getExtras();
	        String scanResult = bundle.getString("result");
	        Intent resultIntent = new Intent(AddrVerifiyActivity.this,QCodeDlgActivity.class);
			Bundle bundle1 = new Bundle();
			bundle1.putString("code", scanResult);
			bundle1.putLong("familyRecordId", familyRecordId);
			bundle1.putLong("familyId", familyId);
			// bundle.putParcelable("bitmap", barcode);
			resultIntent.putExtras(bundle1);
			resultIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivityForResult(resultIntent, 9527);
		}
		if(resultCode==RESULT_ER){
			//finish();
		}else if(resultCode==RESULT_OK_MY){
			setResult(RESULT_OK_MY);
			StatusChangeutils changeutils;
			changeutils = new StatusChangeutils();
			changeutils.setstatuschange("ADDRVERIFY",1);
			finish();
		}else if(resultCode==RESULT_NO){
			//finish();
		}
//		Loger.d("TEST", "AddrVerifiyActivity requestCode:"+requestCode);
//		Loger.d("TEST", "AddrVerifiyActivity resultCode:"+resultCode);
		super.onActivityResult(requestCode, resultCode, data);
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

}
