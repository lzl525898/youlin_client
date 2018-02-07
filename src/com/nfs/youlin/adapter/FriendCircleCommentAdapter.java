package com.nfs.youlin.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FriendCircleCommentAdapter extends BaseAdapter{
	private Context context;
	private List<String> nameList;
	private List<String> contentList;
	private int ret = 0;
	private int commentNum;
	public FriendCircleCommentAdapter(Context context,List<String> nameList,List<String> contentList,int commentNum){
		this.context=context;
		this.nameList=nameList;
		this.contentList=contentList;
		this.commentNum=commentNum;
		Loger.i("youlin","nameList.size()-->"+nameList.size());
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(commentNum==0){
			ret=0;
		}
		if(commentNum==1){
			ret=1;
		}
		if(commentNum==2){
			ret=2;
		}
		if(commentNum>2){
			ret=2;
		}
		return ret;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_comment_item, null);
			holder.commentTv = (TextView) convertView.findViewById(R.id.observer);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
			holder.commentTv.setText(Html.fromHtml("<font color='#008000'>"+nameList.get(position)+"</font>:"+contentList.get(position)));

		return convertView;
	}

	private static class ViewHolder {
		private TextView commentTv;
	}
}
