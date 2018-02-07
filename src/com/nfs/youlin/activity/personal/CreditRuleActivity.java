package com.nfs.youlin.activity.personal;

import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.umeng.analytics.MobclickAgent;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class CreditRuleActivity extends Activity {
	private WebView webView;
	private TextView disconnect;
	private ProgressBar progressB;
	private boolean downloadurlflag = true;
	private String url = "https://123.57.9.62/yl/web/integralinfo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private_policy);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("积分规则");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		disconnect = (TextView) findViewById(R.id.webdisconnect);
		webView = (WebView) findViewById(R.id.webView);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setHorizontalScrollBarEnabled(false);// 水平不显示
		webView.setVerticalScrollBarEnabled(false); // 垂直不显示
		progressB = (ProgressBar)findViewById(R.id.web_pd);
		progressB.setMax(100);
		progressB.setIndeterminate(false);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setUseWideViewPort(true);
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
                    // 网页加载完成
					progressB.setVisibility(View.GONE);
                } else {
                    // 加载中
                	progressB.setProgress(newProgress);
                }
			}
		});
		webView.loadUrl(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.private_policy, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
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
