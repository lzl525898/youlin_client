package com.nfs.youlin.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.R.drawable;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.Progresswithnum;
import com.umeng.analytics.MobclickAgent;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.ClientCertRequest;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class BaoXiangWeb extends Activity {
	private WebView webView;
	private TextView disconnect;
	private ProgressBar progressB;
	private boolean downloadurlflag = true;
	private String url = "https://123.57.9.62/yl/web/baoxiang";

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_policy);
		disconnect = (TextView) findViewById(R.id.webdisconnect);
		webView = (WebView) findViewById(R.id.webView);
		webView.setHorizontalScrollBarEnabled(false);// 水平不显示
		webView.setVerticalScrollBarEnabled(false); // 垂直不显示
		progressB = (ProgressBar) findViewById(R.id.web_pd);
		progressB.setMax(100);
		progressB.setIndeterminate(false);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setUseWideViewPort(true);
		MyWebViewClient myWebViewClient = new MyWebViewClient();
		webView.setBackgroundColor(Color.argb(0, 0, 0, 0)); // 透明
		webView.setWebViewClient(myWebViewClient);
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
		webView.addJavascriptInterface(new JavaScriptObject(getApplicationContext()), "myObj");
		webView.loadUrl(url);
	}

	private class MyWebViewClient extends WebViewClient {
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
			disconnect.setVisibility(View.VISIBLE);
			disconnect.setOnClickListener(new OnClickListener() {
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
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.popup_exit_activity);
		}
		return super.onKeyDown(keyCode, event);
	}

	public class JavaScriptObject {
		Context mContxt;

		public JavaScriptObject(Context mContxt) {
			this.mContxt = mContxt;
		}

		@JavascriptInterface
		public String getAndroid(String jsonString) {
			// webView.loadUrl("javascript:funFromjs('hahahaha')");
			JSONObject jsonObj = null;
			try {
				jsonObj = new JSONObject(jsonString);
				jsonObj.put("imei", YLPushUtils.getImei(getApplicationContext(), new String()));
				jsonObj.put("phone", App.sUserPhone == null ? "none" : App.sUserPhone);
				jsonObj.put("userid", App.sUserLoginId == 0L ? "none" : String.valueOf(App.sUserLoginId));
			} catch (JSONException e) {
				jsonObj = null;
				e.printStackTrace();
			}
			Loger.i("LYM", jsonObj.toString());
			return jsonObj.toString();
		}

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
