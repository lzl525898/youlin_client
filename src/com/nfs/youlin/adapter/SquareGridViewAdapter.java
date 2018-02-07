package com.nfs.youlin.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.App;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class SquareGridViewAdapter extends BaseAdapter{
	Context context;
	ArrayList<Map<String,String>> list;
	ImageLoader imageLoader;
	public SquareGridViewAdapter(Context context,ArrayList<Map<String,String>> list) {
		// TODO Auto-generated constructor stub
		imageLoader = ImageLoader.getInstance();
		this.context=context;
		this.list=list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.list_square_gridview_item, parent, false);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		viewHolder.imageView=(ImageView)convertView.findViewById(R.id.imageview);
		viewHolder.textView=(TextView)convertView.findViewById(R.id.textview);
		viewHolder.imageView.setScaleType(ScaleType.CENTER_CROP);
		imageLoader.displayImage(list.get(position).get("img"), viewHolder.imageView,App.options_error);
		viewHolder.textView.setText(list.get(position).get("name"));
		return convertView;
	}
	
	public static class ViewHolder{
		public ImageView imageView;
		public TextView textView;
	}
}
