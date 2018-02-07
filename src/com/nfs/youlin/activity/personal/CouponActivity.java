package com.nfs.youlin.activity.personal;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class CouponActivity extends Activity {
	private String webUrl = "http://v2.everhomes.com/mobile/html/gift_certificate/gift_certificate.html?" +
			"token=9m%2FGVm7nnSPj8R0vDCdF6zZZWpi3Ig9%2BB61PceAM23iRW38PYZUVF8tTylb00Sbm&" +
			"appKey=19268cec-a293-4360-a64e-9769d1cd4fa4";
	private ActionBar actionBar;
	private WebView   webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("我的礼券");
		setContentView(R.layout.activity_coupon);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		webView = (WebView) findViewById(R.id.wv_activity_coupon);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(webUrl);
		webView.setWebViewClient(new MyWebViewClient());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.coupon_refresh:
			webView.reload();
			return true;
		case R.id.coupon_copy:
			ClipboardManager cmb = (ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);  
			cmb.setText(webUrl); 
			Toast toast = Toast.makeText(getApplicationContext(),
				     "已复制到粘贴板", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return true;
		case R.id.coupon_open:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(webUrl));
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.coupon_info, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	class MyWebViewClient extends WebViewClient{
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		@Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        // TODO Auto-generated method stub
        	//super.onReceivedSslError(view, handler, error);
        	handler.proceed();
        }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
