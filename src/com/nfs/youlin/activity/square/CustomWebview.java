package com.nfs.youlin.activity.square;

import java.lang.reflect.Array;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.titlebar.barter.BarterActivity;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
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
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class CustomWebview extends Activity {
	WebView webView;
	Long mtopicId = 0L;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("youlin.square.send.action")) {
				webView.loadUrl("javascript:sendSuccess()");
			}
		}
	};

	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_webview);
		webView = (WebView) findViewById(R.id.id_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBlockNetworkImage(false);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				webView.loadUrl(url);
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
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				// super.onReceivedSslError(view, handler, error);
				handler.proceed();
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				// TODO Auto-generated method stub
				CustomDialog.Builder builder=new CustomDialog.Builder(CustomWebview.this);
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
		webView.addJavascriptInterface(new JavaScriptObject(CustomWebview.this), "myObj");
		webView.loadUrl("https://www.youlinzj.cn/yl/web/replacelist?community_id=" + App.sFamilyCommunityId
				+ "&topic_id=0" + "&user_id=" + App.sUserLoginId + "&user_type=" + App.sUserType);
		// "http://172.16.50.179/youlin/myRepIndex.html?userId="+16
		Loger.i("NEW", "loadUrl->" + "https://www.youlinzj.cn/yl/web/replacelist?community_id=" + App.sFamilyCommunityId
				+ "&topic_id=0" + "&user_id=" + App.sUserLoginId + "&user_type=" + App.sUserType);
		registerReceiver(receiver, new IntentFilter("youlin.square.send.action"));
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
		public void send() {
			Intent intent = new Intent(CustomWebview.this, BarterActivity.class);
			startActivity(intent);
		}
		@JavascriptInterface
		public void startDetailWebview(long topicid,long senderId){
			//Toast.makeText(CustomWebview.this, topicid+"", 0).show();
			Intent intent = new Intent(CustomWebview.this, BarterDetailWebview.class).putExtra("topic_id", topicid).putExtra("sender_id", senderId);
			startActivityForResult(intent, 1003);
		}
		@JavascriptInterface
		public void mBack() {
			webView.goBack();
		}
		// public String getAndroid(String jsonString) {
		// // webView.loadUrl("javascript:funFromjs('hahahaha')");
		// JSONObject jsonObj = null;
		// try {
		// jsonObj = new JSONObject(jsonString);
		// jsonObj.put("imei", YLPushUtils.getImei(getApplicationContext(), new
		// String()));
		// jsonObj.put("phone", App.sUserPhone == null ? "none" :
		// App.sUserPhone);
		// jsonObj.put("userid", App.sUserLoginId == 0L ? "none" :
		// String.valueOf(App.sUserLoginId));
		// } catch (JSONException e) {
		// jsonObj = null;
		// e.printStackTrace();
		// }
		// Loger.i("LYM", jsonObj.toString());
		// return jsonObj.toString();
		// }

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode==1003){
			if(resultCode==10086){
				long topicId=data.getLongExtra("topicId", 0);
				//Toast.makeText(CustomWebview.this, "1111111111", 0).show();
				webView.loadUrl("javascript:myRemove("+topicId+")");
			}
			if(resultCode==10087){
				long topicId=data.getLongExtra("topicId", 0);
				gethttpdata(topicId);
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
//		if (isFinishing()) {
//			webView.loadUrl("about:blank");
//			setContentView(new FrameLayout(this));
//		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	private void gethttpdata(final long topicId) {
		RequestParams reference = new RequestParams();
		reference.put("user_id", App.sUserLoginId);
		reference.put("topic_id", topicId);
		reference.put("tag", "checktopic");
		reference.put("apitype", "h5");
		reference.put("access", 9527);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String flag = response.getString("flag");
					if (flag.equals("no")) {
						webView.loadUrl("javascript:myRemove("+topicId+")");
						Toast toast = Toast.makeText(CustomWebview.this, "此贴已经不可见", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}else if(flag.equals("ok")){
						JSONObject jsonObject=new JSONObject(response.getString("info"));
						String browserNum=jsonObject.getString("viewNum");
						String likeType=jsonObject.getString("likeType");
						String likeNum=jsonObject.getString("favouriteNum");
						//Toast.makeText(CustomWebview.this,likeNum, 0).show();
						String commentNum=jsonObject.getString("commentNum");
						webView.loadUrl("javascript:updateTopic("+topicId+","+browserNum+","+likeType+","+likeNum+","+commentNum+")");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(CustomWebview.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
}
