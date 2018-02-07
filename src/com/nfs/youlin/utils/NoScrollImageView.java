package com.nfs.youlin.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class NoScrollImageView extends ImageView{
	public NoScrollImageView(Context context){
		super(context);
	}
	public NoScrollImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}
}
