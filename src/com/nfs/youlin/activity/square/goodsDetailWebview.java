package com.nfs.youlin.activity.square;

import java.lang.reflect.Array;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chatuidemo.activity.ChatActivity;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.GalleryPictureActivity;
import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoActivity;
import com.nfs.youlin.activity.neighbor.ReportActivity;
import com.nfs.youlin.activity.personal.CollectionActivity;
import com.nfs.youlin.activity.personal.MyPushActivity;
import com.nfs.youlin.activity.titlebar.barter.BarterActivity;
import com.nfs.youlin.activity.titlebar.barter.BarterBaiduMapActivity;
import com.nfs.youlin.activity.titlebar.barter.BarterDedailActivity;
import com.nfs.youlin.activity.titlebar.barter.BarterDedailCommentActivity;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ClearSelectImg;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ReceiverCallNotAllowedException;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class goodsDetailWebview extends Activity {
	WebView webView;
	Long mtopicId = 0L;
	long topicId=0,senderId=0;
	ProgressBar bar;
	int sgId;
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_webview);
		bar=(ProgressBar)findViewById(R.id.id_progress_bar);
		sgId=getIntent().getIntExtra("sg_id", 0);
		webView = (WebView) findViewById(R.id.id_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBlockNetworkImage(false);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		// 开启 DOM storage API 功能  
		webView.getSettings().setDomStorageEnabled(true);  
		//开启 database storage API 功能  
		webView.getSettings().setDatabaseEnabled(true);   
		String cacheDirPath = getFilesDir().getAbsolutePath()+"/webcache";  
       	Loger.i("LYM", "cacheDirPath="+cacheDirPath);  
       	//设置数据库缓存路径  
       	webView.getSettings().setDatabasePath(cacheDirPath);  
       	//设置  Application Caches 缓存目录  
       	webView.getSettings().setAppCachePath(cacheDirPath);  
       	//开启 Application Caches 功能  
       	webView.getSettings().setAppCacheEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				webView.loadUrl(url);
				return true;
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				bar.setVisibility(View.GONE);
			}
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				// super.onReceivedSslError(view, handler, error);
				handler.proceed();
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, final String message, JsResult result) {
				// TODO Auto-generated method stub
				CustomDialog.Builder builder=new CustomDialog.Builder(goodsDetailWebview.this);
				builder.setMessage(message);
				//builder.setCancelable(false);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
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
				result.confirm();
				return true;
			}
			
		});
		webView.addJavascriptInterface(new JavaScriptObject(goodsDetailWebview.this), "myObj");
		Toast.makeText(goodsDetailWebview.this, sgId+"", 0).show();
		webView.loadUrl("https://www.youlinzj.cn/yl/web/goodsDetail?community_id="+1+"&user_id="+App.sUserLoginId+"&sg_id="+sgId);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				finish();
			}
		}
		return true;
	}
	
	public class JavaScriptObject {
		Context mContxt;

		public JavaScriptObject(Context mContxt) {
			this.mContxt = mContxt;
		}
		@JavascriptInterface
		public void mFinish(){
			finish();
		}
		@JavascriptInterface
		public void mStartActivity(int addressId){
			startActivityForResult(new Intent(goodsDetailWebview.this,AddressWebview.class).putExtra("address_id", addressId),101);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==101){
			if(resultCode==1011){
				int id=data.getIntExtra("address_id", 0);
				webView.loadUrl("javascript:setAddress("+id+")");
			}
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
