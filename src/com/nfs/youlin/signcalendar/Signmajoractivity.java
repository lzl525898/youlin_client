package com.nfs.youlin.signcalendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class Signmajoractivity extends Activity implements OnClickListener {
	private ActionBar actionbar;
	private WebView webView;
	private String url = "https://123.57.9.62/yl/web/integralinfo";
	private boolean downloadurlflag = true;
	private ImageButton signdown;
	private TextView currentjifen;
	private TextView[] sign7map = new TextView[7];
	private ImageView[] sign7imgmap = new ImageView[7];
	private final int HANDLER_SIGN_OK = 10001;
	private final int HANDLER_SIGN_FALSE = 10000;
	private final int HANDLER_GETCALENDAR_OK = 10011;
	private final int HANDLER_GETCALENDAR_FALSE = 10010;
	private final int HANDLER_GETSCALENDAR_OK = 20011;
	private final int HANDLER_GETSCALENDAR_FALSE = 20010;
	private String[] currentday = new String[3];
	private boolean getsignrequest = false;
	private String httpflag = "none";
	private YLProgressDialog ylProgressDialog;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int nowyear = Integer.parseInt(currentday[0]);
			int nowmonth = Integer.parseInt(currentday[1]);
			int nowday = Integer.parseInt(currentday[2]);
			Date currentDay = new Date(nowyear - 1900, nowmonth - 1, nowday);
			int currentweeknum = currentDay.getDay();
			switch (msg.what) {
			case HANDLER_SIGN_OK:
				Loger.d("test4", "签到成功");
				Toast.makeText(Signmajoractivity.this, "签到成功", Toast.LENGTH_SHORT).show();
				signdown.setImageResource(R.drawable.btn_qiandao_c);
				signdown.setTag("signed");
				sign7imgmap[currentweeknum].setVisibility(View.VISIBLE);
				Getcredithttprequest(App.sUserLoginId);
				break;
			case HANDLER_SIGN_FALSE:
				// signdown.setImageResource(R.drawable.btn_qiandao_c);
				// signdown.setTag("signed");
				Toast.makeText(Signmajoractivity.this, "签到失败", Toast.LENGTH_SHORT).show();
				Loger.d("test4", "签到失败");
				break;
			case HANDLER_GETCALENDAR_OK:
				Intent intent = new Intent(Signmajoractivity.this, Youlin_Sign_Calendar.class);
				intent.putExtra("signjsondata", msg.obj.toString());
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				ylProgressDialog.dismiss();
				break;
			case HANDLER_GETCALENDAR_FALSE:
				Intent intent1 = new Intent(Signmajoractivity.this, Youlin_Sign_Calendar.class);
				intent1.putExtra("signjsondata", "none");
				intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent1);
				ylProgressDialog.dismiss();
				break;
			case HANDLER_GETSCALENDAR_OK:
				try {
					JSONArray signdatajson = new JSONArray(msg.obj.toString());
					// int year =
					// Integer.parseInt(((JSONObject)signdatajson.get(signdatajson.length()-1)).getString("year"));
					// int month =
					// Integer.parseInt(((JSONObject)signdatajson.get(signdatajson.length()-1)).getString("month"));
					// int day =
					// Integer.parseInt(((JSONObject)signdatajson.get(signdatajson.length()-1)).getString("day"));
					// Date lastsignDay = new Date(year- 1900, month-1, day);
					// int nowyear = Integer.parseInt(currentday[0]);
					// int nowmonth = Integer.parseInt(currentday[1]);
					// int nowday = Integer.parseInt(currentday[2]);
					//
					// Date currentDay = new Date(nowyear - 1900, nowmonth-1,
					// nowday);
					// int currentweeknum = currentDay.getDay();
					Loger.d("test5",
							"now day" + nowyear + "-" + nowmonth + "-" + nowday + "currentweeknum=" + currentweeknum);
					ylProgressDialog.dismiss();
					int earlymonthDays = DateUtil.getDateNum(nowmonth == 1 ? nowyear - 1 : nowyear,
							nowmonth == 1 ? 11 : nowmonth - 2);
					int jsonlong = signdatajson.length();
					for (int i = 0; i <= currentweeknum && i < signdatajson.length(); i++) {
						int syear = Integer.parseInt(((JSONObject) signdatajson.get(i)).getString("year"));
						int smonth = Integer.parseInt(((JSONObject) signdatajson.get(i)).getString("month"));
						int sday = Integer.parseInt(((JSONObject) signdatajson.get(i)).getString("day"));
						Loger.d("test5", "signed day" + syear + "-" + smonth + "-" + sday);
						Date signedDay = new Date(syear - 1900, smonth - 1, sday);
						for (int j = 0; j <= currentweeknum; j++) {
							Date showDay = new Date(
									((nowday - j) <= 0 && nowmonth - 2 < 0) ? nowyear - 1 - 1900 : nowyear - 1900,
									(nowday - j) > 0 ? nowmonth - 1 : (nowmonth - 2) >= 0 ? nowmonth - 2 : 11,
									(nowday - j) > 0 ? (nowday - j) : earlymonthDays + nowday - j);
							if (i == 0 && j == 0 && DateUtil.compareDateDay(showDay, signedDay) != 0) {
								signdown.setImageResource(R.drawable.signbutton);
								signdown.setTag("unsign");
							}
							if (DateUtil.compareDateDay(showDay, signedDay) == 0) {
								sign7imgmap[currentweeknum - j].setVisibility(View.VISIBLE);
								Loger.d("test5", "signed j=" + j);
								break;
							} else if (DateUtil.compareDateDay(showDay, signedDay) == -1) {
								break;
							}
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case HANDLER_GETSCALENDAR_FALSE:
				ylProgressDialog.dismiss();
				initonweeksignimg();
				break;
			default:
				break;
			}

		};
	};

	private void SetUserCredit() {
		SharedPreferences sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER, Context.MODE_PRIVATE);
		final Long userCredit = sharedata.getLong("credit", 0L);
		String status = sharedata.getString("signstatus", null);
		if (status != null) {
			if ("ok".equals(status)) {// 已经签到
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						signdown.setImageResource(R.drawable.btn_qiandao_c);
						signdown.setTag("signed");
						currentjifen.setText(Html.fromHtml("<font color='white'>我的积分：</font>"
								+ "<font color='white'><b><big>" + userCredit + "</big></b></font>"));
					}
				});
			} else if ("no".equals(status)) {// 尚未签到
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						signdown.setImageResource(R.drawable.signbutton);
						signdown.setTag("unsign");
						currentjifen.setText(Html.fromHtml("<font color='white'>我的积分：</font>"
								+ "<font color='white'><b><big>" + userCredit + "</big></b></font>"));
					}
				});
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signmajorlayout);
		actionbar = getActionBar();
		actionbar.setTitle("积分签到");
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayHomeAsUpEnabled(true);
		ylProgressDialog = YLProgressDialog.createDialogwithcircle(Signmajoractivity.this, "加载中...", 0);
		ylProgressDialog.setCancelable(true);
		signdown = (ImageButton) findViewById(R.id.signbutton);
		currentjifen = (TextView) findViewById(R.id.currentjifen);
		sign7map[0] = (TextView) findViewById(R.id.signday1);
		sign7map[1] = (TextView) findViewById(R.id.signday2);
		sign7map[2] = (TextView) findViewById(R.id.signday3);
		sign7map[3] = (TextView) findViewById(R.id.signday4);
		sign7map[4] = (TextView) findViewById(R.id.signday5);
		sign7map[5] = (TextView) findViewById(R.id.signday6);
		sign7map[6] = (TextView) findViewById(R.id.signday7);
		sign7imgmap[0] = (ImageView) findViewById(R.id.signedimg1);
		sign7imgmap[1] = (ImageView) findViewById(R.id.signedimg2);
		sign7imgmap[2] = (ImageView) findViewById(R.id.signedimg3);
		sign7imgmap[3] = (ImageView) findViewById(R.id.signedimg4);
		sign7imgmap[4] = (ImageView) findViewById(R.id.signedimg5);
		sign7imgmap[5] = (ImageView) findViewById(R.id.signedimg6);
		sign7imgmap[6] = (ImageView) findViewById(R.id.signedimgright);

		/****************************************************************/
		SetUserCredit();
		initonweeksign();
		Getcredithttprequest(App.sUserLoginId);
		initonweeksignimg();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// 网络请求 注册地址 参数{city,village,rooft,number} login_account
				Long currenttime = System.currentTimeMillis();
				while (!getsignrequest) {
					if ((System.currentTimeMillis() - currenttime) > (App.WAITFORHTTPTIME + 5000)) {
						getsignrequest = true;
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (getsignrequest == true && !httpflag.equals("ok")) {
					// Toast.makeText(Signmajoractivity.this,
					// "首次请求数据失败！\n请耐心等待我们再次为您加载...",
					// Toast.LENGTH_SHORT).show();
					Message message = new Message();
					message.what = HANDLER_GETSCALENDAR_FALSE;
					handler.sendMessage(message);
				}
				getsignrequest = false;
				httpflag = "none";
				super.onPostExecute(result);
			}
		}.execute();
		signdown.setOnClickListener(this);
		webView = (WebView) findViewById(R.id.webView);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setHorizontalScrollBarEnabled(false);// 水平不显示
		webView.setVerticalScrollBarEnabled(false); // 垂直不显示
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBlockNetworkImage(false);
		webSettings.setUseWideViewPort(true);
		webView.loadUrl(url);
	}

	private void initonweeksignimg() {
		RequestParams params = new RequestParams();
		params.put("user_id", App.sUserLoginId);
		params.put("tag", "getsigndate");
		params.put("apitype", "users");
		ylProgressDialog.show();
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				String flag = null;
				// Message message = new Message();
				try {
					flag = response.getString("flag");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (flag.equals("none")) {
					Toast.makeText(Signmajoractivity.this, "还没有签到记录!", Toast.LENGTH_SHORT).show();
					ylProgressDialog.dismiss();
					signdown.setImageResource(R.drawable.signbutton);
					signdown.setTag("unsign");
					httpflag = "ok";
				}
				// handler.sendMessage(message);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Loger.i("test4", "5555555--->" + response.toString());
				Message message = new Message();
				message.what = HANDLER_GETSCALENDAR_OK;
				message.obj = response;
				handler.sendMessage(message);
				httpflag = "ok";
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				// TODO
				new ErrorServer(Signmajoractivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});

	}

	private void initonweeksign() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = sDateFormat.format(new java.util.Date());
		currentday = date.split("-");
		int nowyear = Integer.parseInt(currentday[0]);
		int nowmonth = Integer.parseInt(currentday[1]);
		int nowday = Integer.parseInt(currentday[2]);
		Date currentDay = new Date(nowyear - 1900, nowmonth - 1, nowday);
		int currentweeknum = currentDay.getDay();
		int earlymonthDays = DateUtil.getDateNum(nowyear, nowmonth - 2);
		Loger.d("test4", "earlymonthDays=" + earlymonthDays);
		if (nowday >= 7) {
			sign7map[0].setText(nowmonth + "." + (nowday - currentweeknum));
			sign7map[1].setText(nowmonth + "." + (nowday - currentweeknum + 1));
			sign7map[2].setText(nowmonth + "." + (nowday - currentweeknum + 2));
			sign7map[3].setText(nowmonth + "." + (nowday - currentweeknum + 3));
			sign7map[4].setText(nowmonth + "." + (nowday - currentweeknum + 4));
			sign7map[5].setText(nowmonth + "." + (nowday - currentweeknum + 5));
			sign7map[6].setText(nowmonth + "." + (nowday - currentweeknum + 6));
		} else if (nowday < 7) {
			sign7map[0].setText(
					((nowday - currentweeknum) > 0 ? nowmonth : nowmonth - 1) + "." + ((nowday - currentweeknum) > 0
							? nowday - currentweeknum : earlymonthDays + nowday - currentweeknum));
			sign7map[1].setText(((nowday - currentweeknum + 1) > 0 ? nowmonth : (nowmonth - 1) > 0 ? nowmonth - 1 : 12)
					+ "." + ((nowday - currentweeknum + 1) > 0 ? (nowday - currentweeknum + 1)
							: earlymonthDays + nowday - currentweeknum + 1));
			sign7map[2].setText(((nowday - currentweeknum + 2) > 0 ? nowmonth : (nowmonth - 1) > 0 ? nowmonth - 1 : 12)
					+ "." + ((nowday - currentweeknum + 2) > 0 ? (nowday - currentweeknum + 2)
							: earlymonthDays + nowday - currentweeknum + 2));
			sign7map[3].setText(((nowday - currentweeknum + 3) > 0 ? nowmonth : (nowmonth - 1) > 0 ? nowmonth - 1 : 12)
					+ "." + ((nowday - currentweeknum + 3) > 0 ? (nowday - currentweeknum + 3)
							: earlymonthDays + nowday - currentweeknum + 3));
			sign7map[4].setText(((nowday - currentweeknum + 4) > 0 ? nowmonth : (nowmonth - 1) > 0 ? nowmonth - 1 : 12)
					+ "." + ((nowday - currentweeknum + 4) > 0 ? (nowday - currentweeknum + 4)
							: earlymonthDays + nowday - currentweeknum + 4));
			sign7map[5].setText(((nowday - currentweeknum + 5) > 0 ? nowmonth : (nowmonth - 1) > 0 ? nowmonth - 1 : 12)
					+ "." + ((nowday - currentweeknum + 5) > 0 ? (nowday - currentweeknum + 5)
							: earlymonthDays + nowday - currentweeknum + 5));
			sign7map[6].setText(((nowday - currentweeknum + 6) > 0 ? nowmonth : (nowmonth - 1) > 0 ? nowmonth - 1 : 12)
					+ "." + ((nowday - currentweeknum + 6) > 0 ? (nowday - currentweeknum + 6)
							: earlymonthDays + nowday - currentweeknum + 6));
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			if(webView.canGoBack()){
				webView.goBack();
			}
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(webView.canGoBack()){
				webView.goBack();
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.signbutton) {
			if (v.getTag().equals("unsign")) {
				Puthttprequest(App.sUserLoginId);
			} else {
				Gethttprequest(App.sUserLoginId);

				Loger.d("test4", "ssssssssssssssss");
			}

		}
	}

	private void Getcredithttprequest(long user_id) {
		RequestParams params = new RequestParams();
		params.put("user_id", user_id);
		params.put("tag", "usercredit");
		params.put("apitype", "users");

		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					String credit = response.getString("credit");
					currentjifen.setText(Html.fromHtml("<font color='white'>我的积分：</font>"
							+ "<font color='white'><b><big>" + credit + "</big></b></font>"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void Gethttprequest(long user_id) {
		RequestParams params = new RequestParams();
		params.put("user_id", user_id);
		params.put("tag", "getsigndate");
		params.put("apitype", "users");
		ylProgressDialog.show();
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				String flag = null;
				Message message = new Message();
				Loger.d("test4", "2222222222222222");
				try {
					flag = response.getString("flag");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (flag.equals("none")) {
					message.what = HANDLER_GETCALENDAR_FALSE;
				}
				handler.sendMessage(message);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Loger.i("test4", "5555555--->" + response.toString());
				Message message = new Message();
				message.what = HANDLER_GETCALENDAR_OK;
				message.obj = response;
				handler.sendMessage(message);
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				// TODO
				new ErrorServer(Signmajoractivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}

	private void Puthttprequest(long user_id) {
		ylProgressDialog.show();
		RequestParams params = new RequestParams();
		params.put("user_id", user_id);
		params.put("tag", "usersign");
		params.put("apitype", "users");
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				String flag = null;
				Message message = new Message();
				try {
					flag = response.getString("flag");
					Loger.d("test4", "签到：" + response.toString());
					ylProgressDialog.dismiss();
					if (flag.equals("ok")) {
						Long userCredit = response.getLong("credit");
						Editor sharedata = getSharedPreferences(App.SMS_VERIFICATION_USER, Context.MODE_PRIVATE).edit();
						sharedata.putLong("credit", userCredit);
						sharedata.putString("signstatus", "ok");
						sharedata.commit();
						message.what = HANDLER_SIGN_OK;
					} else {
						message.what = HANDLER_SIGN_FALSE;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(message);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				// TODO
				ylProgressDialog.dismiss();
				new ErrorServer(Signmajoractivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
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
}
