package com.nfs.youlin.activity.square;

import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.push.YLPushUtils;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SquareFragment extends Fragment {
	View view;
	WebView webView;
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (view == null) {
			view = inflater.inflate(R.layout.activity_custom_webview, container, false);
		}
		ViewGroup viewGroup = (ViewGroup) view.getParent();
		if (viewGroup != null) {
			viewGroup.removeView(view);
		}
		webView = (WebView)view.findViewById(R.id.id_webview);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setUseWideViewPort(true);
		//webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		MyWebViewClient myWebViewClient = new MyWebViewClient();
		webView.setWebViewClient(myWebViewClient);
		webView.addJavascriptInterface(new JavaScriptObject(getActivity()), "myObj");
		webView.loadUrl("https://www.youlinzj.cn/yl/web/mySquare?community_id="+App.sFamilyCommunityId);
		return view;
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
		public void mStartActivity(int type) { //type 0->培训  1->旧物置换  2->爆款
			switch (type) {
			case 0:
				startActivity(new Intent(getActivity(),TrainingWebview.class));
				break;
			case 1:
				startActivity(new Intent(getActivity(),CustomWebview.class));
				break;
			case 2:
				Toast.makeText(getActivity(), "还没写", 0).show();
				break;
			default:
				break;
			}
		}
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		webView.resumeTimers();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		webView.pauseTimers();
	}
}
