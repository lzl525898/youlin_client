package com.nfs.youlin.activity.personal;

import com.nfs.youlin.R;
import com.nfs.youlin.R.id;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cn.jpush.android.api.JPushInterface;

public class Invite_friend_integral extends Activity {
	private WebView webView;
	private ProgressBar progressB;
	boolean downloadurlflag = true;
	LinearLayout integralNetLayout;
	String url;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_invite_friend_integral);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("积分");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		Intent intent = getIntent();
		//url = intent.getStringExtra("linkurl");
		url="http://www.baidu.com";
		webView = (WebView) findViewById(R.id.webView);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		progressB = (ProgressBar) findViewById(R.id.web_pd);
		progressB.setMax(100);
		progressB.setIndeterminate(false);
		integralNetLayout=(LinearLayout)findViewById(R.id.integral_net_layout);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setUseWideViewPort(true);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				if (newProgress == 100) {
					// 网页加载完成
					if (downloadurlflag == true) {
						webView.setVisibility(View.VISIBLE);
						progressB.setVisibility(View.GONE);
					}
				} else {
					// 加载中
					progressB.setProgress(newProgress);
				}

			}
		});
		webView.setWebViewClient(new WebViewClient() {
			// 重写父类方法，让新打开的网页在当前的WebView中显示
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				progressB.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				downloadurlflag = false;
				webView.setVisibility(View.GONE);
				integralNetLayout.setVisibility(View.VISIBLE);
				integralNetLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// webView.setVisibility(View.VISIBLE);
						downloadurlflag = true;
						webView.loadUrl(url);
					}
				});
			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				// super.onReceivedSslError(view, handler, error);
				handler.proceed();
			}
		});
		webView.loadUrl(url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==android.R.id.home){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		webView.resumeTimers();
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		webView.pauseTimers();
		if (isFinishing()) {
			webView.loadUrl("about:blank");
			setContentView(new FrameLayout(this));
		}
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}
}
