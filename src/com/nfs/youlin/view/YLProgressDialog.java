package com.nfs.youlin.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.nfs.youlin.R;

public class YLProgressDialog extends Dialog {
	private static Context context = null;
	private static YLProgressDialog youlinProgressDialog = null;
	public YLProgressDialog(Context context){
		super(context);
		this.context = context;
	}
	public YLProgressDialog(Context context, int theme) {
		
        super(context, theme);
    }
	
	public static YLProgressDialog createDialog(Context context){
		youlinProgressDialog = new YLProgressDialog(context,R.style.FullTransparentProgressdialog);
		youlinProgressDialog.setContentView(R.layout.inflaterprogress);
		youlinProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return youlinProgressDialog;
	}
	public static YLProgressDialog createDialogwithcircle(Context context,String text,int type){
		
		if(text!=null){
			if(1==type){
				youlinProgressDialog = new YLProgressDialog(context,R.style.FullTransparentProgressdialog);
			}else{
				youlinProgressDialog = new YLProgressDialog(context,R.style.FullTransparentProgressdialogpinyin);
			}
			View view=LayoutInflater.from(context).inflate(R.layout.youcircleprogress, null);
			TextView textView = (TextView) view.findViewById(R.id.tv_pd_wait_circle);
			textView.setText(text);
			textView.setVisibility(View.VISIBLE);
			youlinProgressDialog.setContentView(view);
		}else{
			if(1==type){
				youlinProgressDialog = new YLProgressDialog(context,R.style.FullTransparentProgressdialog);
			}else{
				youlinProgressDialog = new YLProgressDialog(context,R.style.FullTransparentProgressdialogpinyin);
			}
			youlinProgressDialog.setContentView(R.layout.youcircleprogress);
			youlinProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		}
		return youlinProgressDialog;
	}
	public static YLProgressDialog createDialogWithTopic(Context context){
		YLProgressDialog.context = context;
		youlinProgressDialog = new YLProgressDialog(context,R.style.FullTransparentProgressdialog);
		View view=LayoutInflater.from(context).inflate(R.layout.inflater_topic_pd, null);
		ImageView imageView=(ImageView)view.findViewById(R.id.pd_id);
		AnimationDrawable animationDrawable=(AnimationDrawable)imageView.getBackground();
		animationDrawable.start();
		youlinProgressDialog.setContentView(view);
		//youlinProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams params=youlinProgressDialog.getWindow().getAttributes();
		params.gravity=Gravity.CENTER;
		youlinProgressDialog.getWindow().setAttributes(params);
		return youlinProgressDialog;
	}
		
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (youlinProgressDialog == null){
    		return;
    	}
    	
//        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
//        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
//        animationDrawable.start();
    }
 
    /**
     * 
     * [Summary]
     *       setTitile 锟斤拷锟斤拷
     * @param strTitle
     * @return
     *
     */
    public YLProgressDialog setTitile(String strTitle){
    	return youlinProgressDialog;
    }
    
    /**
     * 
     * [Summary]
     *       setMessage 锟斤拷示锟斤拷锟斤拷
     * @param strMessage
     * @return
     *
     */
    public YLProgressDialog setMessage(String strMessage){
//    	TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
    	
//    	if (tvMsg != null){
//    		tvMsg.setText(strMessage);
//    	}
    	
    	return youlinProgressDialog;
    }
}
