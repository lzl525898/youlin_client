package com.nfs.youlin.view;

import com.nfs.youlin.R;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VerificationToast extends Object {
	
	private LayoutInflater inflater;
	private View layout;
	private ImageView imageView;
	private TextView textView;
	private Toast toast;
	private Activity activity;
	
	public VerificationToast(Activity activity) {
		this.activity = activity;
		initObj();
	}
	
	private void initObj(){
		this.inflater = activity.getLayoutInflater();
		this.layout = this.inflater.inflate(R.layout.addr__toast,(ViewGroup) activity.findViewById(R.id.addr_custom_toast));
		this.imageView = (ImageView) this.layout.findViewById(R.id.iv_addr_toast);
		this.textView = (TextView) this.layout.findViewById(R.id.tv_addr_toast);
	}
	
	private void setImageViewBackground(int id){
		this.imageView.setImageResource(id);
	}
	
	private void setTextViewInfo(String content, String id){
		this.textView.setTextColor(Color.parseColor(id));
		this.textView.setText(content);
	}
	
	public void show(String content, String color, int id){
		setImageViewBackground(id);
		setTextViewInfo(content,color);
		toast = new Toast(this.activity.getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, -100);
		toast.setDuration(Toast.LENGTH_SHORT);
	    toast.setView(layout);
	    toast.show();
	}
}
