package com.nfs.youlin.activity.find;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.R.drawable;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
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
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.ClientCertRequest;
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

public class NewsDetail extends Activity {
	private WebView webView;
	private ActionBar actionbar;
	private ProgressBar progressB;
	private PopupWindow popupShareWindow;
	private View popupShareView;
	private LinearLayout topLinearLayout;
	private RelativeLayout newssharedeclare;
	private LinearLayout neighborshare;
	private LinearLayout youlinshare;
	private String currentitemtitle;
	private String currentitempic;
	private String currentitemid;
	private String currentitemlink;
	private TextView disconnect;
	private String url;
	private boolean downloadurlflag = true;
	private String actionBarName="";
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_policy);
		actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setTitle("");
		Intent intent = getIntent();
		url = intent.getStringExtra("linkurl");
		currentitempic = intent.getStringExtra("picurl");
		currentitemtitle = intent.getStringExtra("title");
		currentitemid = intent.getStringExtra("newsid");
		currentitemlink = intent.getStringExtra("linkurl");
		disconnect = (TextView) findViewById(R.id.webdisconnect);
		webView = (WebView) findViewById(R.id.webView);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		progressB = (ProgressBar) findViewById(R.id.web_pd);
		progressB.setMax(100);
		progressB.setIndeterminate(false);
		popupShareView = getLayoutInflater().inflate(R.layout.sharepage, null);
		topLinearLayout = (LinearLayout) popupShareView.findViewById(R.id.news_top_share);
		newssharedeclare = (RelativeLayout) popupShareView.findViewById(R.id.ll_news_share_declare);
		neighborshare = (LinearLayout) popupShareView.findViewById(R.id.iv_share_neighborcircle);
		youlinshare = (LinearLayout) popupShareView.findViewById(R.id.iv_share_youlin);
		newssharedeclare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupShareWindow.dismiss();
			}
		});
		youlinshare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NewsDetail.this, NewsSharerangeList.class);
				intent.putExtra("newstitle", currentitemtitle);
				intent.putExtra("newspic", currentitempic);
				intent.putExtra("newslink", currentitemlink);
				intent.putExtra("newsid", currentitemid);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				popupShareWindow.dismiss();
			}

		});
		neighborshare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NewsDetail.this, NewsShareCompile.class);
				intent.putExtra("picurl", currentitempic);
				intent.putExtra("title", currentitemtitle);
				intent.putExtra("newsid", currentitemid);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				popupShareWindow.dismiss();
			}

		});
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setUseWideViewPort(true);
		MyWebViewClient myWebViewClient = new MyWebViewClient();
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
		webView.loadUrl(url);
		requestActionName();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.newsdetailbar, menu);
		LinearLayout SearchGroup = (LinearLayout) menu.findItem(R.id.newsdetailbar).getActionView();
		ImageView newbarLayout = (ImageView) SearchGroup.findViewById(R.id.news_share);
		newbarLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setPopupWindow();
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if (item.getItemId() == R.id.newsdetailbar) {

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
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

	private void setPopupWindow() {
		popupShareWindow = new PopupWindow(popupShareView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		ColorDrawable cd = new ColorDrawable(0x90000000);
		popupShareWindow.setBackgroundDrawable(cd);
		TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0f, Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0f);
		popupShareWindow.setFocusable(true);
		anim.setDuration(500);
		topLinearLayout.setAnimation(anim);
		popupShareWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
		popupShareWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
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
	void requestActionName(){
		RequestParams params=new RequestParams();
		params.put("tag", "getsubscription");
		params.put("apitype", "comm");
		params.put("community_id", App.sFamilyCommunityId);
		AsyncHttpClient client=new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					actionBarName=response.getString("scription");
					actionbar.setTitle(actionBarName);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
