package com.nfs.youlin.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.personal.PersonalInfoGuanyuActivity;
import com.nfs.youlin.activity.personal.SystemSetActivity;
import com.nfs.youlin.service.YLUpdateService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class Apk_update_detail extends Activity implements OnClickListener {
	private TextView repairedbug;
	private TextView newfunction;
	private TextView bugtitle;
	private TextView functitle;
	private TextView updateversion;
	private TextView updatesize;
	private Button ok;
	private Button cancel;
	private String bugdetailstring = "";
	private String bugsamplestring = "";
	private String bugdetailtitle = "<b>修复Bug</b>";

	private List<String> funcdetailall = new ArrayList<String>();
	private String funcsamplestring = "";
	private String funcdetailstring = "";
	private String funcdetailtitle = "<b>新添加功能</b>";
	private String apkversion;
	private String apksize;
	private String apkdetail;
	private String updateforce;
	private String type;
	private String appName="null";
	private String url="null";
	private String status="null";
	public  ProgressDialog updatapd1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update_detail_layout);
		this.setFinishOnTouchOutside(false);
		SystemSetActivity.isOpen = true;
		PersonalInfoGuanyuActivity.isOpen = true;
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		// p.height = (int) (d.getHeight() * 0.5);
		// p.height = LayoutParams.WRAP_CONTENT;
		// 高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.80); // 宽度设置为屏幕的0.8
		p.x = 0;
		p.y = 0;
		getWindow().setAttributes(p);
		updatapd1 = new ProgressDialog(this);
		updatapd1.setMessage("优邻正在更新...");
        updatapd1.setCancelable(false);
        updatapd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        updatapd1.setIndeterminate(false);
        DemoApplication myapp = (DemoApplication)getApplication();
        myapp.setupdateDialog(updatapd1);
		repairedbug = (TextView) findViewById(R.id.repairedbug);
		newfunction = (TextView) findViewById(R.id.newfunction);
		bugtitle = (TextView) findViewById(R.id.repairedbugtitle);
		functitle = (TextView) findViewById(R.id.newfunctiontitle);
		updateversion = (TextView) findViewById(R.id.updateversion);
		updatesize = (TextView) findViewById(R.id.updatesize);
		cancel = (Button) findViewById(R.id.updatecancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		ok = (Button) findViewById(R.id.updateok);
		ok.setOnClickListener(this);
		Intent intent = getIntent();
		apksize = intent.getStringExtra("apksize");
		apkversion = intent.getStringExtra("version");
		apkdetail = intent.getStringExtra("apkdetail");
		updateforce = intent.getStringExtra("apkforce");
		try {
			type = intent.getStringExtra("type");
			appName = intent.getStringExtra("appName");
			url = intent.getStringExtra("url");
			status = intent.getStringExtra("status");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		updateversion.setText("最新版本：" + apkversion);
		updatesize.setText("新版本大小：" + apksize);
		if (updateforce.equals("1")) {
			EMChatManager.getInstance().endCall();
			EMChatManager.getInstance().logout();
			cancel.setVisibility(View.GONE);
		}

		try {
			JSONObject json = new JSONObject(apkdetail);
			String funString = json.getString("fun");
			String bugString = json.getString("bug");

			String[] bugdetailsample = new String[4];
			String[] bugdetailall = bugString.split("\\|\\|\\|");
			String[] funcdetailsample = new String[4];
			String[] funcdetailall = funString.split("\\|\\|\\|");
			Loger.d("test4", "funString=" + funString);
			Loger.d("test4", "bugString=" + bugString);
			Loger.d("test4", "bugdetailall=" + bugdetailall.length);
			for (int i = 0; i < bugdetailall.length; i++) {
				if (i < 3) {
					bugdetailsample[i] = bugdetailall[i];
					bugsamplestring += bugdetailsample[i] + "<br>";
				} else if (i == 3) {
					bugdetailsample[i] = bugdetailall[i];
					bugsamplestring += bugdetailsample[i];
				}
				if (i < bugdetailall.length - 1) {
					bugdetailall[i] = bugdetailall[i];
					bugdetailstring += bugdetailall[i] + "<br>";
				} else if (i == bugdetailall.length - 1) {
					bugdetailall[i] = bugdetailall[i];
					bugdetailstring += bugdetailall[i];
				}
			}
			for (int i = 0; i < funcdetailall.length; i++) {
				if (i < 3) {
					funcdetailsample[i] = funcdetailall[i];
					funcsamplestring += funcdetailsample[i] + "<br>";
				} else if (i == 3) {
					funcdetailsample[i] = funcdetailall[i];
					funcsamplestring += funcdetailsample[i];
				}
				if (i < 6) {
					funcdetailall[i] = funcdetailall[i];
					funcdetailstring += funcdetailall[i] + "<br>";
				} else if (i == 6) {
					funcdetailall[i] = funcdetailall[i];
					funcdetailstring += funcdetailall[i];
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// repairedbug.setText(Html.fromHtml("<font
		// color='black'>"+bugdetail1+"..."+"</font>"+"<font color= '#ffba02'>
		// "+getClickableSpan("更多")+"</font>"));
		bugtitle.setText(Html.fromHtml("<font color='#323232' size =48px>" + bugdetailtitle + "</font>"));
		functitle.setText(Html.fromHtml("<font color='#323232' size =48px>" + funcdetailtitle + "</font>"));
		repairedbug.setText(Html.fromHtml("<font color='#646464'>" + bugsamplestring + "..." + "</font>"));
		newfunction.setText(Html.fromHtml("<font color='#646464'>" + funcsamplestring + "..." + "</font>"));
		repairedbug.setText(getClickableSpan(repairedbug.getText().toString() + " 更多", "bug"));
		newfunction.setText(getClickableSpan(newfunction.getText().toString() + " 更多", "func"));
		// 此行必须有
		repairedbug.setMovementMethod(LinkMovementMethod.getInstance());
		newfunction.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private SpannableString getClickableSpan(String Info, final String whickmore) {
		// 监听器
		SpannableString spanableInfo = new SpannableString(Info);

		int end = Info.length();
		Loger.d("test4", "SpannableString long=" + end);

		spanableInfo.setSpan(new ClickableSpan() {
			@Override
			public void updateDrawState(TextPaint ds) {
				ds.setUnderlineText(false);
			}

			@Override
			public void onClick(View widget) {
				// TODO Auto-generated method stub
				if (whickmore.equals("bug")) {
					repairedbug.setText(Html.fromHtml("<font color='#646464'>" + bugdetailstring + "</font>"));
				} else {
					newfunction.setText(Html.fromHtml("<font color='#646464'>" + funcdetailstring + "</font>"));
				}
				//Toast.makeText(Apk_update_detail.this, "Click Success", Toast.LENGTH_SHORT).show();
			}
		}, end - 2, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		spanableInfo.setSpan(new ForegroundColorSpan(0xFFffba02), end - 2, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spanableInfo;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.updateok) {
			if(type!=null && type.equals("push")){
				Intent updateinte = new Intent(this,YLUpdateService.class);  
				updateinte.putExtra("appName",appName);  
				updateinte.putExtra("url",url);  
				updateinte.putExtra("status",status);  
				startService(updateinte);
				if(status.equals("1")){
			        updatapd1.show();
				}
			}else{
				setResult(RESULT_OK);
				finish();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN && isOutOfBounds(this, event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	private boolean isOutOfBounds(Activity context, MotionEvent event) {
		final int x = (int) event.getX();
		final int y = (int) event.getY();
		final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
		final View decorView = context.getWindow().getDecorView();
		return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
				|| (y > (decorView.getHeight() + slop));
	}

	// /**
	// * 无下划线的Span
	// * Author: msdx (645079761@qq.com)
	// * Time: 14-9-4 上午10:43 */
	// public class NoUnderlineSpan extends ClickableSpan {
	//
	// @Override
	// public void updateDrawState(TextPaint ds) {
	// ds.setColor(ds.linkColor);
	// ds.setUnderlineText(false);
	// }
	//
	// @Override
	// public void onClick(View widget) {
	// // TODO Auto-generated method stub
	//
	// }
	// }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SystemSetActivity.isOpen=false;
		PersonalInfoGuanyuActivity.isOpen=false;
	}
}
