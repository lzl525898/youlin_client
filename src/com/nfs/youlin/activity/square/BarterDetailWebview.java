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

public class BarterDetailWebview extends Activity {
	WebView webView;
	Long mtopicId = 0L;
	long topicId=0,senderId=0;
	ProgressBar bar;
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_webview);
		bar=(ProgressBar)findViewById(R.id.id_progress_bar);
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
		topicId=getIntent().getLongExtra("topic_id", 0);
		senderId=getIntent().getLongExtra("sender_id", senderId);
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
				CustomDialog.Builder builder=new CustomDialog.Builder(BarterDetailWebview.this);
				builder.setMessage(message);
				//builder.setCancelable(false);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(message.equals("确定下架该商品吗？")){
							webView.loadUrl("javascript:xiajia()");
						}
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
		webView.addJavascriptInterface(new JavaScriptObject(BarterDetailWebview.this), "myObj");
		webView.loadUrl("https://www.youlinzj.cn/yl/web/replacelistDetail?topic_id="+topicId+"&community_id=" + App.sFamilyCommunityId
				 + "&user_id=" + App.sUserLoginId + "&user_type=" + App.sUserType+"&sender_id="+senderId);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				Intent intent=new Intent();
				intent.putExtra("topicId", topicId);
				setResult(10087,intent);
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
		public void showToast(String str){
			Toast.makeText(BarterDetailWebview.this, str, 0).show();
		}
		@JavascriptInterface
		public void baiduMap(String Lng, String Lag) {
			Intent intent = new Intent(BarterDetailWebview.this, BarterBaiduMapActivity.class);
			intent.putExtra("jingdu", Lng);
			intent.putExtra("weidu", Lag);
			startActivity(intent);
		}

		@JavascriptInterface
		public void chat(String userNick,String senderPortrait) {
			AccountDaoDBImpl account = new AccountDaoDBImpl(BarterDetailWebview.this);
			Intent intent = new Intent(BarterDetailWebview.this, ChatActivity.class);
			intent.putExtra("userId", senderId+"");
			intent.putExtra("chatType", 1);
			intent.putExtra("usernick", userNick);
			String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId)))
					.getUser_portrait();
			intent.putExtra("selfurl", selfurl); // hyy 有数据库后 从写
			intent.putExtra("neighborurl", senderPortrait);
			startActivityForResult(intent, 1001);
		}
		@JavascriptInterface
		public void chatBuyers(String userNick,String senderPortrait,long senderId) {
			AccountDaoDBImpl account = new AccountDaoDBImpl(BarterDetailWebview.this);
			Intent intent = new Intent(BarterDetailWebview.this, ChatActivity.class);
			intent.putExtra("userId", senderId+"");
			intent.putExtra("chatType", 1);
			intent.putExtra("usernick", userNick);
			String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId)))
					.getUser_portrait();
			intent.putExtra("selfurl", selfurl); // hyy 有数据库后 从写
			intent.putExtra("neighborurl", senderPortrait);
			startActivity(intent);
		}
		@JavascriptInterface
		public void browseImg(int a, String[] b) {
			Intent intent = new Intent(BarterDetailWebview.this, GalleryPictureActivity.class);
			intent.putExtra("ID", a);
			intent.putExtra("url", b);
			intent.putExtra("type", 5);
			startActivity(intent);
		}

		@JavascriptInterface
		public void comment(long topicId) {
			mtopicId = topicId;
			Intent intent = new Intent(BarterDetailWebview.this, BarterDedailCommentActivity.class);
			intent.putExtra("topic_id", topicId);
			startActivityForResult(intent, 1002);
		}
		@JavascriptInterface
		public void report(long topicId,long communityId,long senderId) {
			Bundle bundle=new Bundle();
			bundle.putLong("topic_id",topicId);
			bundle.putLong("community_id", communityId);
			bundle.putLong("sender_id", senderId);
			startActivity(new Intent(BarterDetailWebview.this,ReportActivity.class).putExtra("ID", bundle));
		}
		@JavascriptInterface
		public void mFinish(int type){
			if(type==1){
				Intent intent=new Intent();
				intent.putExtra("topicId", topicId);
				setResult(10086,intent);
			}else if(type==0){
				Intent intent=new Intent();
				intent.putExtra("topicId", topicId);
				setResult(10087,intent);
			}
			finish();
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1001) {
			webView.loadUrl("javascript:setName()");
		}
		if (requestCode == 1002) {
			gethttpdata(mtopicId);
		}
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
						Intent intent=new Intent();
						intent.putExtra("topicId", topicId);
						setResult(10086,intent);
						Toast toast = Toast.makeText(BarterDetailWebview.this, "此贴已经不可见", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						finish();
					}else if(flag.equals("ok")){
						JSONObject jsonObject=new JSONObject(response.getString("info"));
						String commentNum=jsonObject.getString("commentNum");
						webView.loadUrl("javascript:setCommentNum("+commentNum+")");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(BarterDetailWebview.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	
}
