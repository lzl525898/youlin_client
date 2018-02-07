package com.nfs.youlin.activity.square;

import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.BaoXiangWeb.JavaScriptObject;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

public class SquareWebview extends Activity{
	WebView webView;
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_webview);
		webView = (WebView) findViewById(R.id.id_webview);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setUseWideViewPort(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		MyWebViewClient myWebViewClient = new MyWebViewClient();
		webView.setWebViewClient(myWebViewClient);
		//webView.addJavascriptInterface(new JavaScriptObject(getApplicationContext()), "myObj");
		webView.loadUrl("https://www.youlinzj.cn/yl/web/mySquare");
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
		
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			// TODO Auto-generated method stub
			// super.onReceivedSslError(view, handler, error);
			handler.proceed();
		}
	}
	public class JavaScriptObject {
		Context mContxt;

		public JavaScriptObject(Context mContxt) {
			this.mContxt = mContxt;
		}

		@JavascriptInterface
		public String getAndroid() {
			// webView.loadUrl("javascript:funFromjs('hahahaha')");
			JSONObject jsonObj = null;
			try {
				jsonObj = new JSONObject();
				jsonObj.put("imei", YLPushUtils.getImei(getApplicationContext(), new String()));
				jsonObj.put("phone", App.sUserPhone == null ? "none" : App.sUserPhone);
				jsonObj.put("userid", App.sUserLoginId == 0L ? "none" : String.valueOf(App.sUserLoginId));
				jsonObj.put("communityid", App.sFamilyCommunityId == 0L ? "none" : String.valueOf(App.sFamilyCommunityId));
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
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		webView.pauseTimers();
	}
}
