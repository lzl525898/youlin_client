package com.nfs.youlin.view;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.PxchangeDp;
import com.nfs.youlin.utils.StatusChangeListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.umeng.analytics.MobclickAgent;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.jpush.android.api.JPushInterface;

public class myprogressdialog extends Activity implements StatusChangeListener{
	private ImageView redoval ;
	private ImageView yellowoval ;
	private ImageView blueoval ;
	private StatusChangeutils statusutils;
	private float redstartx;
	private float yellowstartx;
	private float bluestartx;
	private ObjectAnimator redBouncer;
	private ObjectAnimator yellowBouncer; 
	private ObjectAnimator blueBouncer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.inflaterprogress);
		statusutils = new StatusChangeutils();
		statusutils.statuschangelistener("SHUAXIN",this);
	}

	@Override
	public void setstatuschanged(int status) {
		// TODO Auto-generated method stub
		if(status == 1){
			finish();
		}
	}

	@Override
	public void setstatuschanged(int status, Bundle data) {
		// TODO Auto-generated method stub
		
	}
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
}
