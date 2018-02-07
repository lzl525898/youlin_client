package com.nfs.youlin.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class FriendCircleListView extends ListView{

	public FriendCircleListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public FriendCircleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//initHeadView(context);
	}
	public FriendCircleListView(Context context, ArrayList<String> dataList) {
		this(context);
		//this.data = dataList;
	}
}
