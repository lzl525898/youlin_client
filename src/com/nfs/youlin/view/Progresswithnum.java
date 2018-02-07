package com.nfs.youlin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.graphics.Shader;
import android.view.View;

public class Progresswithnum extends View {
	 Paint paint,textpaint;    
	 RectF area;    
	 int value = 100;     
	 private LinearGradient shader;
	
	 public Progresswithnum(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
		// TODO Auto-generated constructor stub
	}
	public Progresswithnum(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
		// TODO Auto-generated constructor stub
	}
	 public Progresswithnum(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}

	 
	 public Progresswithnum(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
	 public void setProgress(int value){        
		 this.value = value;        
		 invalidate();    
	 }
	   public void init() {        
		   paint = new Paint();        
		   paint.setStrokeWidth(50f);        
		   paint.setColor(Color.WHITE);        
		   paint.setStyle(Style.STROKE);        
		   paint.setAntiAlias(true);        
		   textpaint = new Paint();        
		   textpaint.setTextSize(50f);        
		   textpaint.setColor(Color.WHITE);       
		   area = new RectF(100, 100, 500, 500);                
		   shader =new LinearGradient(0, 0, 400, 0, new int[] {                     
				   Color.BLUE, Color.WHITE}, null,                    
				   Shader.TileMode.CLAMP);         
		   paint.setShader(shader);    
	}
	   @Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);        
		canvas.drawArc(area, 120, 360*value/100 , false, paint);       
		canvas.drawText(value+"%", 270, 290, textpaint);
	}
}
