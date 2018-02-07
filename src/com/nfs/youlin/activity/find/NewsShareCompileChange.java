package com.nfs.youlin.activity.find;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoAdd;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoAddChange;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ClearSelectImg;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.CustomDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import cn.jpush.android.api.JPushInterface;

public class NewsShareCompileChange extends Activity{
	ActionBar actionbar;
	private String currentitemtitle;
	private String currentitempic;
	private String currentitemid;
	private ImageView newspicview; 
	private TextView newstitleview;
	private EditText newssharetitle;
	private EditText newssharecontent;
	private ImageView newssharetitleflag;
	private ImageView newssharecontentflag;
	public  RequestParams sRequestParams;
	private Account account;
	private String communityDetail;
	private AllFamily currentFamily;
	private AllFamilyDaoDBImpl curfamilyDaoDBImpl;
	private String addrDetails;
	Bundle intentBundle;
	ImageLoader imageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newssharecompilelay);
		imageLoader = ImageLoader.getInstance();
		actionbar = getActionBar();
		setTitle("返回");
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		Intent intent = getIntent();
		intentBundle=intent.getBundleExtra("bundle");
		
		currentitempic = intentBundle.getString("news_picurl");
		currentitemtitle = intentBundle.getString("news_title");
		currentitemid = intentBundle.getString("news_id");
		
		newspicview = (ImageView)findViewById(R.id.sharenewspic);
		newstitleview = (TextView)findViewById(R.id.sharenewscontent);
		newssharetitle = (EditText)findViewById(R.id.newssharetitle);
		newssharetitle.setText(intentBundle.getString("topic_title"));
		newssharecontent = (EditText)findViewById(R.id.newssharecontent);
		newssharecontent.setText(intentBundle.getString("topic_content"));
		newssharetitleflag = (ImageView)findViewById(R.id.titleempty);
		newssharecontentflag = (ImageView)findViewById(R.id.contentempty);
		initview();
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
	   
		newstitleview.setText(currentitemtitle);
		
				
//		Picasso.with(this) 
//		.load(currentitempic) 
//		.placeholder(R.drawable.account) 
//		.error(R.drawable.account)
//		.fit()
//		.tag(this)
//		.into(newspicview);
		imageLoader.displayImage(currentitempic,newspicview,App.options_account);
		newssharetitle.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.new_topic, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == android.R.id.home){
			ExitDialog();
		}else if(item.getItemId() == R.id.finish){
			sendfinish();
		}
		return super.onOptionsItemSelected(item);
	}
	private Account getSenderInfo(){
		Account account = null;
		if(App.sUserLoginId > 0){
			AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(NewsShareCompileChange.this);
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
	private void initview(){
		newssharetitle.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(s.toString().length() > 0){
					newssharetitleflag.setVisibility(View.INVISIBLE);
				}
				
			}
			
		});
		newssharecontent.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(s.toString().length() > 0){
					newssharecontentflag.setVisibility(View.INVISIBLE);
				}
			}
			
		});
	}
	private void sendfinish(){
		ProgressDialog pd=new ProgressDialog(this);
		pd.setMessage("发布中...");
		String sendTitleStr = newssharetitle.getText().toString().trim();
		String sendContentStr= newssharecontent.getText().toString().trim();
		if(!sendTitleStr.isEmpty()){
			if(!sendContentStr.isEmpty()){
				if(sendContentStr.length()<=1000){
				if(NetworkService.networkBool){
					pd.show();
					sRequestParams = new RequestParams();
					sRequestParams.put("topic_id", intentBundle.getLong("topic_id"));
					sRequestParams.put("forum_id", intentBundle.getLong("forum_id")); //0表示本小区、1、周边、2.同城
					sRequestParams.put("forum_name", "本小区");
					sRequestParams.put("topic_category_type", intentBundle.getLong("topic_category_type"));  //2 表示普通话题 345 物业
					sRequestParams.put("sender_id", intentBundle.getLong("sender_id"));
					sRequestParams.put("sender_name", intentBundle.getString("sender_name"));
					sRequestParams.put("sender_lever", intentBundle.getString("sender_lever")); 
					sRequestParams.put("sender_portrait", intentBundle.getString("sender_portrait"));
					sRequestParams.put("sender_family_id", intentBundle.getLong("sender_family_id"));
					sRequestParams.put("sender_family_address", intentBundle.getString("sender_family_address")); 
					sRequestParams.put("sender_nc_role", intentBundle.getLong("sender_nc_role"));
					sRequestParams.put("display_name", intentBundle.getString("display_name"));
					sRequestParams.put("object_data_id", intentBundle.getLong("object_data_id"));  //0.表示一般话题、1.表示活动  3.news
					sRequestParams.put("circle_type", intentBundle.getLong("circle_type"));  //1.表示一般话题
					sRequestParams.put("topic_title", sendTitleStr);
					sRequestParams.put("topic_content", sendContentStr);
//					Loger.i("TEST", "发帖当前内容->"+sendContentStr);
//					Loger.i("TEST", "发帖当前cityID->"+currentFamily.getFamily_city_id());
//					Loger.i("TEST", "发帖当前commID->"+currentFamily.getFamily_community_id());
					sRequestParams.put("sender_city_id",currentFamily.getFamily_city_id());
					sRequestParams.put("sender_community_id",currentFamily.getFamily_community_id());
					sRequestParams.put("new_id",currentitemid);
					sRequestParams.put("topic_time", intentBundle.getLong("topic_time"));
					sRequestParams.put("send_status", intentBundle.getInt("send_status"));
					sRequestParams.put("tag", "updatetopic");
					sRequestParams.put("apitype", IHttpRequestUtils.APITYPE[5]);
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								CircleDetailActivity.finishBool=true;
								finish();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}, 20000);
					AsyncHttpClient httpClient = new AsyncHttpClient();
					httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, sRequestParams,
							new JsonHttpResponseHandler() {
								@Override
								public void onSuccess(int statusCode, Header[] headers,
										JSONObject response) {
									// TODO Auto-generated method stub
									String flag = "no";
									try {
										flag = response.getString("flag");
										if ("ok".equals(flag)) {
											Loger.i("youlin","ok topic_id->"+ response.getString("topic_id"));
											CircleDetailActivity.finishBool=true;
											finish();
										} else {
											Loger.i("youlin","no topic_id->"+ response.getString("topic_id"));
											sRequestParams = null;
											NewTopic.nforumId = -1;
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										Loger.i("youlin", "error->" + e.getMessage());
										e.printStackTrace();
									}
									super.onSuccess(statusCode, headers, response);
								}

								@Override
								public void onFailure(int statusCode, Header[] headers,
										String responseString, Throwable throwable) {
									// TODO Auto-generated method stub
									new ErrorServer(NewsShareCompileChange.this, responseString.toString());
									super.onFailure(statusCode, headers, responseString,
											throwable);
								}
					});
				}else{
					Toast.makeText(NewsShareCompileChange.this,"请先开启网络", Toast.LENGTH_SHORT).show();
				}
				}else{
					Toast.makeText(NewsShareCompileChange.this,"发送内容过长", Toast.LENGTH_SHORT).show();
				}
			}else{
				newssharecontentflag.setVisibility(View.VISIBLE);
			}
		}else{
			newssharetitleflag.setVisibility(View.VISIBLE);
		}
	}
	public void ExitDialog() {
		CustomDialog.Builder builder=new CustomDialog.Builder(NewsShareCompileChange.this);
		//builder.setTitle("提示");
		builder.setMessage("确定要放弃此次编辑吗？");
		//builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				NewsShareCompileChange.this.finish();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
			ExitDialog();
		}
		return super.onKeyDown(keyCode, event);
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
