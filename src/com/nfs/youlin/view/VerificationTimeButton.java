package com.nfs.youlin.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.ForgetPasswordActivity;
import com.nfs.youlin.activity.VerificationActivity;
import com.nfs.youlin.service.NetworkMonitorService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.NetworkJudge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class VerificationTimeButton extends Button implements OnClickListener {
	private long lenght = 60 * 1000;// 倒计时长度,默认60秒
	private String textafter = "秒后重新获取";
	private String textbefore = "获取验证码";
	private final String TIME = "time";
	private final String CTIME = "ctime";
	private OnClickListener mOnclickListener;
	private Timer t;
	private TimerTask tt;
	private long time;
	private Context context;
	private static boolean bClearInfo;
	Map<String, Long> map = new HashMap<String, Long>();

	public VerificationTimeButton(Context context) {
		super(context);
		this.context = context;
		setOnClickListener(this);
	}

	public VerificationTimeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setOnClickListener(this);
	}

	public void sendCloseMsg(){
		Message message =  new Message();
		message.what = 100;
		han.sendMessage(message);
	}
	
	@SuppressLint("HandlerLeak")
	Handler han = new Handler() {
		public void handleMessage(android.os.Message msg) {
			VerificationTimeButton.this.setText(time / 1000 + textafter);
			time -= 1000;
			if (time >= 0){
				VerificationTimeButton.this.setBackgroundResource(R.drawable.bg_forget_pwd);
			}
			
			if (time < 0) {
				VerificationTimeButton.this.setEnabled(true);
				VerificationTimeButton.this.setBackgroundResource(R.drawable.bg_get_verification_code);
				VerificationTimeButton.this.setText(textbefore);
				bClearInfo = true;
				VerificationActivity.sClearStatus = true;
				clearTimer();
			}
			if(msg.what == 100){
				VerificationTimeButton.this.setEnabled(true);
				VerificationTimeButton.this.setBackgroundResource(R.drawable.bg_get_verification_code);
				VerificationTimeButton.this.setText(textbefore);
				bClearInfo = true;
				clearTimer();
			}
		};
	};

	public boolean getClearInfoStatus(){
		return bClearInfo;
	}
	
	public void initTimer() {
		time = lenght;
//		VerificationTimeButton.this.setText(time / 1000 + textafter);
//		time -= 1000;
		t = new Timer();
		tt = new TimerTask() {
			@Override
			public void run() {
				han.sendEmptyMessage(0x01);
			}
		};
	}

	public void clearTimer() {
		if (tt != null) {
			tt.cancel();
			tt = null;
		}
		if (t != null)
			t.cancel();
		t = null;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {

		if (l instanceof VerificationTimeButton) {
			super.setOnClickListener(l);
		} else
			this.mOnclickListener = l;
	}

	@Override
	public void onClick(View v) {
		if(!NetworkMonitorService.bNetworkMonitor){
			Toast.makeText(context, "网络有问题", Toast.LENGTH_SHORT).show();
			return;
		}else if(!VerificationActivity.bEditPhoneStatus){
			Toast.makeText(context,"请填写正确的手机号", Toast.LENGTH_SHORT).show();
			return;
		}

		if (mOnclickListener != null)
			mOnclickListener.onClick(v);
		initTimer();
		this.setText(time / 1000 + textafter);
		this.setEnabled(false);
		bClearInfo = false;
		t.schedule(tt, 0, 1000);
//		this.setBackgroundResource(R.drawable.bg_get_verification_code);
//		 t.scheduleAtFixedRate(task, delay, period);
	}

	/**
	 * 和activity的onDestroy()方法同步
	 */
	public void onDestroy() {
		if (App.map == null)
			App.map = new HashMap<String, Long>();
		App.map.put(TIME, time);
		App.map.put(CTIME, System.currentTimeMillis());
		clearTimer();
		Loger.i("TEST", "onDestroy");
	}

	/**
	 * 和activity的onCreate()方法同步
	 */
	public void onCreate(Bundle bundle) {
		if (App.map == null)
			return;
		if (App.map.size() <= 0)// 这里表示没有上次未完成的计时
			return;
		long time = System.currentTimeMillis() - App.map.get(CTIME)
				- App.map.get(TIME);
		App.map.clear();
		if (time > 0)
			return;
		else {
			initTimer();
			this.time = Math.abs(time);
			t.schedule(tt, 0, 1000);
			this.setText(time + textafter);
			this.setEnabled(false);
		}
	}

	/** * 设置计时时候显示的文本 */
	public VerificationTimeButton setTextAfter(String text1) {
		this.textafter = text1;
		return this;
	}

	/** * 设置点击之前的文本 */
	public VerificationTimeButton setTextBefore(String text0) {
		this.textbefore = text0;
		this.setText(textbefore);
		return this;
	}

	/**
	 * 设置到计时长度
	 * 
	 * @param lenght
	 *            时间 默认毫秒
	 */
	public VerificationTimeButton setLenght(long lenght) {
		this.lenght = lenght;
		return this;
	}
}