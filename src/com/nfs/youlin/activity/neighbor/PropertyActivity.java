package com.nfs.youlin.activity.neighbor;

import java.util.ArrayList;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.applycount;
import com.nfs.youlin.utils.error_logtext;
import com.umeng.analytics.MobclickAgent;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PropertyActivity extends Activity {
	private TextView address;
	private TextView workingtime;
	private TextView telephone;
	private String addresstext;
	private String timetext;
	private String telephonetext;
	private boolean cRequestSet = false;
	private String flag = "none";
	private LinearLayout wuyexinxiInit;
	private LinearLayout wuyexinxiLayout;
	private RelativeLayout wuyexinxiBelowLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("物业");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		wuyexinxiInit=(LinearLayout)findViewById(R.id.wuyexinxi_init);
		wuyexinxiLayout=(LinearLayout)findViewById(R.id.wuyexinxi);
		wuyexinxiBelowLayout=(RelativeLayout)findViewById(R.id.wuyexinxi_below);
		LinearLayout repairLayout=(LinearLayout)findViewById(R.id.repair_layout);
		LinearLayout adviceLayout=(LinearLayout)findViewById(R.id.advice_layout);
		address = (TextView)findViewById(R.id.wuyeaddress);
		workingtime = (TextView)findViewById(R.id.wuyeworkingtime);
		telephone = (TextView)findViewById(R.id.wuyetelphone);
//		RelativeLayout helpLayout=(RelativeLayout)findViewById(R.id.help_layout);
		repairLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int type = 0;
				AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(getApplicationContext());
				Account account;
				try {
					account = daoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
					type = account.getUser_type();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(4 == type||5 == type||6 == type){//表示当前
					startActivity(new Intent(PropertyActivity.this,PropertyRepairList.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				}else{
					startActivity(new Intent(PropertyActivity.this,PropertyRepairActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				}
				
			}
		});
		adviceLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(PropertyActivity.this,PropertyAdviceActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			}
		});
		telephone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+telephone.getText().toString())).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
		});
		gethttpdata(App.sFamilyCommunityId,IHttpRequestUtils.YOULIN, 1);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.property, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==android.R.id.home){
			finish();
		}
//		if(item.getItemId()==R.id.about){
//			startActivity(new Intent(PropertyActivity.this,PropertyAboutActivity.class));
//		}
		return super.onOptionsItemSelected(item);
	}
	private void gethttpdata(long communityId,String http_addr,int request_index){
		/***********************************************/
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		final int position = request_index;
		
			if(request_index ==1){
					params.put("community_id", communityId);
					params.put("tag","getproperty");
					params.put("apitype",IHttpRequestUtils.APITYPE[4]);
			}
		
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				Loger.i("LYM", "set applycount flag1111111111->" + response);
				try {
					flag = jsonContext.getString("flag");
					if(flag.equals("ok")){
						wuyexinxiLayout.setVisibility(View.VISIBLE);
						wuyexinxiBelowLayout.setVisibility(View.VISIBLE);
						cRequestSet = true;
						addresstext = jsonContext.getString("address");
						timetext = jsonContext.getString("office_hours");
						telephonetext = jsonContext.getString("phone");
						address.setText("地址："+addresstext);
						workingtime.setText("营业时间："+timetext);
						telephone.setText(telephonetext);
					}else if(flag.equals("none")){
						wuyexinxiInit.setVisibility(View.VISIBLE);
						address.setText("地址："+"物业没有发布相关信息");
						workingtime.setText("营业时间："+"物业没有发布相关信息");
						telephone.setText("物业没有发布相关信息");
						cRequestSet = false;
					}

				} catch (org.json.JSONException e) {
					e.printStackTrace();
					cRequestSet = false;
					Loger.i("test3","JSONObject->"+ e.getMessage());
				}
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
				org.json.JSONArray jsonContext = response;
					super.onSuccess(statusCode, headers, response);
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					cRequestSet = false;
					new ErrorServer(PropertyActivity.this, responseString.toString());
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});
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
