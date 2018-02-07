package com.nfs.youlin.view;

import com.nfs.youlin.R;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

public class Getgiftsuccess extends Dialog{
	private static Context context = null;
	private static Getgiftsuccess GetgiftsuccessDialog = null;
	public Getgiftsuccess(Context context){
		super(context);
		this.context = context;
	}
	public Getgiftsuccess(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}
	public static Getgiftsuccess createDialog(Context context){
		GetgiftsuccessDialog = new Getgiftsuccess(context,R.style.FullTransparentProgressdialogpinyin);
		GetgiftsuccessDialog.setContentView(R.layout.getgiftsuccessview);
		GetgiftsuccessDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return GetgiftsuccessDialog;
	}
	
	public void pdShow() {
		// TODO Auto-generated method stub
		super.show();
		
	}
	
    public void onWindowFocusChanged(boolean hasFocus){
    	
    	if (GetgiftsuccessDialog == null){
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
    public Getgiftsuccess setTitile(String strTitle){
    	return GetgiftsuccessDialog;
    }
    
    /**
     * 
     * [Summary]
     *       setMessage 锟斤拷示锟斤拷锟斤拷
     * @param strMessage
     * @return
     *
     */
    public Getgiftsuccess setMessage(String strMessage){
    	TextView tvMsg = (TextView)GetgiftsuccessDialog.findViewById(R.id.getmessage);
    	
    	if (tvMsg != null){
    		tvMsg.setText(Html.fromHtml("<font color='#808080'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;亲爱的用户您点击领取的礼品我们已收到通知，我们会在</font>"+"<font color='#ffba02'>&nbsp;&nbsp;&nbsp;&nbsp;每周五定时为您配送至您的小区</font>"+
    	"<font color='#808080'>，请这段时间内保持电话畅通，再次谢谢您参与到优邻这个大家庭，祝您生活愉快！</font>"));
    	}
    	
    	return GetgiftsuccessDialog;
    }
}
