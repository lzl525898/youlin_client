package com.nfs.youlin.view;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.PxchangeDp;
import com.nfs.youlin.utils.StatusChangeListener;
import com.nfs.youlin.utils.StatusChangeutils;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ProgressLay  extends RelativeLayout implements StatusChangeListener{
	private ImageView redoval ;
	private ImageView yellowoval ;
	private ImageView greenoval ;
	private StatusChangeutils statusutils;
	private float redstartx;
	private float yellowstartx;
	private float greenstartx;
	private ObjectAnimator redBouncer;
	private ObjectAnimator yellowBouncer; 
	private ObjectAnimator greenBouncer;
	public ProgressLay(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	protected void onFinishInflate() {
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.myprogressdialog, this);
		init_widget();

	}
	public void init_widget() {
		
		redoval = (ImageView)findViewById(R.id.redoval);
		yellowoval = (ImageView)findViewById(R.id.yellowoval);
		greenoval = (ImageView)findViewById(R.id.greenoval);
		
//		statusutils = new StatusChangeutils();
//		statusutils.statuschangelistener("SHUAXIN",this);


		 Keyframe rk0 = Keyframe.ofFloat(0f, 1f);
		 Keyframe rk1 = Keyframe.ofFloat(0.25f,1f);
	     Keyframe rk2 = Keyframe.ofFloat(0.5f, 1f);
	     Keyframe rk2p5 = Keyframe.ofFloat(0.55f, 0.3f);
	     Keyframe rk3 = Keyframe.ofFloat(0.75f, 0f);
	     Keyframe rk3p5 = Keyframe.ofFloat(0.95f, 0.3f);
	     Keyframe rk4 = Keyframe.ofFloat(1f, 1f);
	     
	     Keyframe yk1 = Keyframe.ofFloat(0f,1f); 
	     Keyframe yk2 = Keyframe.ofFloat(0.25f, 1f);
	     	Keyframe yk2p5 = Keyframe.ofFloat(0.3f, 0.3f);
	     Keyframe yk3 = Keyframe.ofFloat(0.5f, 0f);
	     	Keyframe yk3p5 = Keyframe.ofFloat(0.7f, 0.3f);
	     Keyframe yk4 = Keyframe.ofFloat(0.75f, 1f);
	     Keyframe yk0 = Keyframe.ofFloat(1f, 1f);
		  
	     Keyframe gk2 = Keyframe.ofFloat(0f, 1f);
	     Keyframe gk2p5 = Keyframe.ofFloat(0.05f, 0.3f);
	     Keyframe gk3 = Keyframe.ofFloat(0.25f, 0.1f);
	     Keyframe gk3p5 = Keyframe.ofFloat(0.45f, 0.3f);
	     Keyframe gk4 = Keyframe.ofFloat(0.5f, 1f);
	     Keyframe gk0 = Keyframe.ofFloat(0.75f, 1f);
		 Keyframe gk1 = Keyframe.ofFloat(1f,1f);
		 
	     /****************************************************************************/
	     Keyframe rks0 = Keyframe.ofFloat(0f, 0.7f);
		 Keyframe rks1 = Keyframe.ofFloat(0.25f,1f);
	     Keyframe rks2 = Keyframe.ofFloat(0.5f, 0.7f);
	     Keyframe rks3 = Keyframe.ofFloat(0.75f, 0.7f);
	     Keyframe rks4 = Keyframe.ofFloat(1f, 0.7f);
	    
		 Keyframe yks1 = Keyframe.ofFloat(0f,1f);
	     Keyframe yks2 = Keyframe.ofFloat(0.25f, 0.7f);
	     Keyframe yks3 = Keyframe.ofFloat(0.5f, 0.7f);
	     Keyframe yks4 = Keyframe.ofFloat(0.75f, 0.7f);
	     Keyframe yks0 = Keyframe.ofFloat(1f, 1f);
	     
	     Keyframe gks2 = Keyframe.ofFloat(0f, 0.7f);
	     Keyframe gks3 = Keyframe.ofFloat(0.25f, 0.7f);
	     Keyframe gks4 = Keyframe.ofFloat(0.5f, 0.7f);
	     Keyframe gks0 = Keyframe.ofFloat(0.75f, 1f);
		 Keyframe gks1 = Keyframe.ofFloat(1f,0.7f);
	     /****************************************************************************/
	     Keyframe rkx0 = Keyframe.ofFloat(0f, 0);
	     Keyframe rkx1 = Keyframe.ofFloat(0.25f, PxchangeDp.dip2px(getContext(), 60));
	     Keyframe rkx2 = Keyframe.ofFloat(0.5f, PxchangeDp.dip2px(getContext(), 120));
	     Keyframe rkx3 = Keyframe.ofFloat(0.75f, PxchangeDp.dip2px(getContext(), 60));
	     Keyframe rkx4 = Keyframe.ofFloat(1f, PxchangeDp.dip2px(getContext(), 0));
	     
	     Keyframe ykx0 = Keyframe.ofFloat(1f, PxchangeDp.dip2px(getContext(), 60));
	     Keyframe ykx1 = Keyframe.ofFloat(0f, PxchangeDp.dip2px(getContext(), 60));
	     Keyframe ykx2 = Keyframe.ofFloat(0.25f, PxchangeDp.dip2px(getContext(),120));
	     Keyframe ykx3 = Keyframe.ofFloat(0.5f, PxchangeDp.dip2px(getContext(), 60));
	     Keyframe ykx4 = Keyframe.ofFloat(0.75f, 0);
	     
	     Keyframe gkx0 = Keyframe.ofFloat(0.75f, PxchangeDp.dip2px(getContext(), 60));
	     Keyframe gkx1 = Keyframe.ofFloat(1f, PxchangeDp.dip2px(getContext(), 120));
	     Keyframe gkx2 = Keyframe.ofFloat(0f, PxchangeDp.dip2px(getContext(), 120));
	     Keyframe gkx3 = Keyframe.ofFloat(0.25f, PxchangeDp.dip2px(getContext(), 60));
	     Keyframe gkx4 = Keyframe.ofFloat(0.5f,  0);
	     /************************************************************************/
	     PropertyValuesHolder redpvhX = PropertyValuesHolder.ofKeyframe("x",rkx0,rkx1,rkx2,rkx3,rkx4);
	     PropertyValuesHolder yellowpvhX = PropertyValuesHolder.ofKeyframe("x",ykx1,ykx2,ykx3,ykx4,ykx0);
	     PropertyValuesHolder greenpvhX = PropertyValuesHolder.ofKeyframe("x",gkx2,gkx3,gkx4,gkx0,gkx1);
	     
	     PropertyValuesHolder redalpha = PropertyValuesHolder.ofKeyframe("alpha",rk0,rk1,rk2,rk2p5,rk3,rk3p5,rk4);
	     PropertyValuesHolder yellowalpha = PropertyValuesHolder.ofKeyframe("alpha",yk1,yk2,yk2p5,yk3,yk3p5,yk4,yk0);
	     PropertyValuesHolder bluealpha = PropertyValuesHolder.ofKeyframe("alpha",gk2,gk2p5,gk3,gk3p5,gk4,gk0,gk1);
	     
	     PropertyValuesHolder rscaleX = PropertyValuesHolder.ofKeyframe("scaleX", rks0, rks1,rks2,rks3,rks4);
	     PropertyValuesHolder rscaleY = PropertyValuesHolder.ofKeyframe("scaleY", rks0, rks1,rks2,rks3,rks4); 
	     
	     PropertyValuesHolder yscaleX = PropertyValuesHolder.ofKeyframe("scaleX", yks1,yks2,yks3,yks4, yks0);
	     PropertyValuesHolder yscaleY = PropertyValuesHolder.ofKeyframe("scaleY", yks1,yks2,yks3,yks4, yks0);
	     
	     PropertyValuesHolder gscaleX = PropertyValuesHolder.ofKeyframe("scaleX", gks2,gks3,gks4,gks0, gks1);
	     PropertyValuesHolder gscaleY = PropertyValuesHolder.ofKeyframe("scaleY", gks2,gks3,gks4,gks0, gks1);
	    
	     redBouncer = ObjectAnimator.ofPropertyValuesHolder(redoval,redalpha,redpvhX,rscaleX,rscaleY).setDuration(2000);
	     yellowBouncer = ObjectAnimator.ofPropertyValuesHolder(yellowoval,yellowalpha,yellowpvhX,yscaleX,yscaleY).setDuration(2000);
	     greenBouncer = ObjectAnimator.ofPropertyValuesHolder(greenoval,bluealpha,greenpvhX,gscaleX,gscaleY).setDuration(2000);
	     redBouncer.setRepeatCount(ObjectAnimator.INFINITE);
	     yellowBouncer.setRepeatCount(ObjectAnimator.INFINITE);
	     greenBouncer.setRepeatCount(ObjectAnimator.INFINITE);
	     redBouncer.setRepeatMode(ObjectAnimator.RESTART);
	     yellowBouncer.setRepeatMode(ObjectAnimator.RESTART);
	     greenBouncer.setRepeatMode(ObjectAnimator.RESTART);
//	     yellowBouncer.setStartDelay(500);
//	     greenBouncer.setStartDelay(1000);
	     redBouncer.start();
	     yellowBouncer.start();
	     greenBouncer.start();
	}
	@Override
	public void setstatuschanged(int status) {
		// TODO Auto-generated method stub
		 
	     
	    	
	}
	@Override
	public void setstatuschanged(int status, Bundle data) {
		// TODO Auto-generated method stub

	}
	
}
