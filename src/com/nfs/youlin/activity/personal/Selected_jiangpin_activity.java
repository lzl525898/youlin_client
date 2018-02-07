package com.nfs.youlin.activity.personal;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class Selected_jiangpin_activity extends Activity implements OnClickListener{
	private Intent intent;
	private String giftname;
	private String giftid;
	private String giftcredit;
	private int currentcredit;
	private int sumcredit;
	private int giftnum=1;
	private Button ensuregetbutton;
	private TextView sumcredittext;
	private EditText giftcount;
	private TextView enabledexchangetext;
	private ImageView selectedgiftview;
	private TextView selectedgiftname;
	private ImageLoader imageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.selected_jiangpin_lay);
		
        imageLoader = ImageLoader.getInstance();
		intent = getIntent();
		giftname = intent.getStringExtra("giftname");
		giftid = intent.getStringExtra("giftid");
		giftcredit = intent.getStringExtra("giftcredit");
		currentcredit = intent.getIntExtra("currentcredit", -1);
		
		ensuregetbutton = (Button)findViewById(R.id.ensureget);
		sumcredittext= (TextView)findViewById(R.id.sumcredit);
		enabledexchangetext = (TextView)findViewById(R.id.enabledexchange);
		giftcount = (EditText)(findViewById(R.id.giftcount).findViewById(R.id.et01));
		selectedgiftview = (ImageView)findViewById(R.id.selectedgiftview);
		selectedgiftname = (TextView)findViewById(R.id.selectedgiftname);
		
		selectedgiftname.setText(giftname);
//		Picasso.with(this)
//		  .load(intent.getStringExtra("giftview"))
//		  .error(R.drawable.plugin_camera_no_pictures)
//		  .placeholder(R.drawable.plugin_camera_no_pictures)
//		  .into(selectedgiftview);
		imageLoader.displayImage(intent.getStringExtra("giftview"), selectedgiftview, App.options_plugin_camera_no_pictures);
		sumcredit = Integer.parseInt(giftcredit);
		sumcredittext.setText(String.valueOf(sumcredit));
		if(sumcredit>currentcredit){
			enabledexchangetext.setVisibility(View.VISIBLE);
			ensuregetbutton.setBackgroundColor(0xffc0c0c0);
			ensuregetbutton.setTextColor(0xffffffff);
			ensuregetbutton.setEnabled(false);
		}
		giftcount.addTextChangedListener(new TextWatcher() {
			int location=0;//记录光标的位置
			int l=0;
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				location=giftcount.getSelectionStart();
				l=s.length();
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(s.toString().length()==0 && l>0){
					giftcount.removeTextChangedListener(this);
					giftcount.setText("0");
					giftcount.setSelection(giftcount.getText().toString().length());
					giftcount.addTextChangedListener(this);
				}else if(Integer.parseInt(s.toString())>=60000){
					giftcount.removeTextChangedListener(this);
					giftcount.setText("60000");
					giftcount.setSelection(giftcount.getText().toString().length());
					Toast.makeText(Selected_jiangpin_activity.this, "兑换数已达上限", Toast.LENGTH_SHORT).show();
					giftcount.addTextChangedListener(this);
				}
				giftnum = Integer.parseInt(giftcount.getText().toString());
				sumcredit = Integer.parseInt(giftcredit)*giftnum;
				Loger.d("test5", "sumcredit="+sumcredit);
				sumcredittext.setText(String.valueOf(sumcredit));
				if(sumcredit>currentcredit){
					enabledexchangetext.setVisibility(View.VISIBLE);
					ensuregetbutton.setBackgroundColor(0xffc0c0c0);
					ensuregetbutton.setTextColor(0xffffffff);
					ensuregetbutton.setEnabled(false);
				}else if(sumcredit<=currentcredit){
					if(sumcredit == 0){
						enabledexchangetext.setVisibility(View.GONE);
						ensuregetbutton.setBackgroundColor(0xffc0c0c0);
						ensuregetbutton.setTextColor(0xffffffff);
						ensuregetbutton.setEnabled(false);
					}else{
						enabledexchangetext.setVisibility(View.GONE);
						ensuregetbutton.setBackgroundColor(0xffffba02);
						ensuregetbutton.setTextColor(0xff323232);
						ensuregetbutton.setEnabled(true);
					}	
				}
			}
		});
		ensuregetbutton.setOnClickListener(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Selected_jiangpin_activity.this.finish();
		return super.onTouchEvent(event);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.ensureget){
			Loger.d("test5","v.getId() == R.id.ensureget" );
			Getjiangpinlisthttprequest(App.sFamilyCommunityId, App.sUserLoginId, giftid, giftnum);
		}
	}
	private void Getjiangpinlisthttprequest(long community_id,long userid,String gift_id,int count){
		RequestParams params = new RequestParams();
		params.put("community_id", community_id);
		params.put("user_id", userid);
		params.put("gl_id", gift_id);
		params.put("count", count);
		params.put("tag", "exchangegifts");
		params.put("apitype", "exchange");
		Loger.d("test5","gl_id ="+gift_id+"count="+count );
		AsyncHttpClient httpClient = new  AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Loger.d("test5","getgift  response="+response );
				try {
					String giftflag = response.getString("flag");
					if(giftflag.equals("ok")){
//						Toast.makeText(Selected_jiangpin_activity.this, "兑换礼品成功", Toast.LENGTH_SHORT).show();
						setResult(RESULT_OK);
						finish();
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
						// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				new ErrorServer(Selected_jiangpin_activity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
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
