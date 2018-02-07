package com.nfs.youlin.activity.find;

import java.util.HashMap;
import java.util.Map;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import cn.jpush.android.api.JPushInterface;

@SuppressLint("NewApi")
public class PinyinActivity extends Activity implements OnClickListener{
	public static int selectIndex = -1;
	private GridLayout pinyinLayout;
	private int[] btnIdArray = {R.id.btn_index_11,R.id.btn_index_A,R.id.btn_index_B,R.id.btn_index_C,
								R.id.btn_index_D,R.id.btn_index_E,R.id.btn_index_F,R.id.btn_index_G,
								R.id.btn_index_H,R.id.btn_index_I,R.id.btn_index_J,R.id.btn_index_K,
								R.id.btn_index_L,R.id.btn_index_M,R.id.btn_index_N,R.id.btn_index_O,
								R.id.btn_index_P,R.id.btn_index_Q,R.id.btn_index_R,R.id.btn_index_S,
								R.id.btn_index_T,R.id.btn_index_U,R.id.btn_index_V,R.id.btn_index_W,
								R.id.btn_index_X,R.id.btn_index_Y,R.id.btn_index_Z,R.id.btn_index_0};
	private Button[] pinyinBtn = new Button[28];
	public static boolean sPinyinStatusBoolean = false;
	private Map<String,Integer> btnDetailMap = new HashMap<String,Integer>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);
		//getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		View view=getLayoutInflater().inflate(R.layout.activity_pinyin_index, null);
		Animation animation=AnimationUtils.loadAnimation(PinyinActivity.this,R.anim.popup_enter_activity);
		view.startAnimation(animation);
		setContentView(view);
		pinyinLayout = (GridLayout) findViewById(R.id.gridlayout_index);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0,R.anim.popup_exit_activity);
			}
		});
		for(int i=0;i<28;i++){
			pinyinBtn[i] = (Button) findViewById(btnIdArray[i]);
			pinyinBtn[i].setEnabled(false);
			btnDetailMap.put(pinyinBtn[i].getText().toString(),i);
		}
		setBtnStyle();
		PinyinActivity.selectIndex = -1;
		
		//AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		//animation.setDuration(1000);
	    
	}
	
	
	
	private void setBtnStyle(){
		pinyinBtn[0].setEnabled(true);
		pinyinBtn[0].setOnClickListener(this);
		pinyinBtn[27].setEnabled(true);
		pinyinBtn[27].setOnClickListener(this);
		for (Map.Entry<String, Integer> entry : NeighborActivity.indexSelector.entrySet()) {  
			if(btnDetailMap.containsKey(entry.getKey())){
				pinyinBtn[btnDetailMap.get(entry.getKey())].setEnabled(true);
				pinyinBtn[btnDetailMap.get(entry.getKey())].setOnClickListener(this);
			}
		} 
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
		}
		return true;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_index_11:
			try {
				PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("#");
				Loger.i("TEST", "R.id.btn_index_11==>"+PinyinActivity.selectIndex);
			} catch (Exception e) {
				e.printStackTrace();
				PinyinActivity.selectIndex=0;
			}
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_A:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("A");
			Loger.i("TEST", "R.id.btn_index_A==>"+PinyinActivity.selectIndex);
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_B:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("B");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_C:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("C");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_D:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("D");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_E:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("E");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_F:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("F");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_G:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("G");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_H:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("H");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;	
		case R.id.btn_index_I:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("I");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_J:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("J");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_K:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("K");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_L:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("L");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_M:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("M");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_N:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("N");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_O:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("O");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_P:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("P");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_Q:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("Q");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_R:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("R");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_S:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("S");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_T:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("T");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_U:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("U");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_V:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("V");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_W:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("W");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_X:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("X");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_Y:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("Y");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_Z:
			PinyinActivity.selectIndex = NeighborActivity.indexSelector.get("Z");
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.btn_index_0:
			PinyinActivity.selectIndex = 100;
			PinyinActivity.sPinyinStatusBoolean = true;
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		case R.id.gridlayout_index:
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
			break;
		default:
			PinyinActivity.selectIndex = -1;
			break;	
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			finish();
			overridePendingTransition(0,R.anim.popup_exit_activity);
		}
		return true;
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
