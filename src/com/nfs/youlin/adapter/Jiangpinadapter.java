package com.nfs.youlin.adapter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.personal.Selected_jiangpin_activity;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ImageItem;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.Res;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Jiangpinadapter extends BaseAdapter
{	private Context mContext;
	private List<Map<String, Object>> datalist = new ArrayList<Map<String, Object>>();
	private int currentcredit;
	private final int REQUEST_GET_GIFT = 4222;
	private ImageLoader imageLoader;
	public Jiangpinadapter(Context context,List<Map<String, Object>> jianglist,int currentcredit) {
		// TODO Auto-generated constructor stub
		datalist = jianglist;
		Loger.d("test5", "jiangpin adapter datalist="+datalist);
		mContext = context;
		Res.init(mContext);
		this.currentcredit = currentcredit;
        imageLoader = ImageLoader.getInstance();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datalist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datalist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder ;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.jiangpin_item, parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.jiangpinimg);
			viewHolder.toggleButton = (ImageView) convertView
					.findViewById(R.id.lingqu);
			viewHolder.nametextView = (TextView) convertView
					.findViewById(R.id.jiangpinname);
			viewHolder.credittextView = (TextView) convertView
					.findViewById(Res.getWidgetID("needcreditdate"));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
//		if(position%2 == 0){
//			viewHolder.imageView.setImageResource(R.drawable.pic_ka);
//		}else{
//			viewHolder.imageView.setImageResource(R.drawable.pic_mianfen);
//		}
		
//		Picasso.with(mContext)
//		  .load(datalist.get(position).get("imgurl").toString())
//		  .error(R.drawable.plugin_camera_no_pictures)
//		  .placeholder(R.drawable.plugin_camera_no_pictures)
//		  .into(viewHolder.imageView);
		imageLoader.displayImage(datalist.get(position).get("imgurl").toString(), viewHolder.imageView, App.options_plugin_camera_no_pictures);
		viewHolder.nametextView.setText(datalist.get(position).get("name").toString());
		viewHolder.credittextView.setText(datalist.get(position).get("credit").toString());
		viewHolder.toggleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent((Activity)mContext,Selected_jiangpin_activity.class);
				intent.putExtra("giftname", datalist.get(position).get("name").toString());
				intent.putExtra("giftid", datalist.get(position).get("giftid").toString());
				intent.putExtra("giftcredit", datalist.get(position).get("credit").toString());
				intent.putExtra("giftview", datalist.get(position).get("imgurl").toString());
				intent.putExtra("currentcredit",currentcredit);
//				mContext.startActivity(intent);
				((Activity)mContext).startActivityForResult(intent, REQUEST_GET_GIFT);
			}
		});
		
		return convertView;
	}
	private class ViewHolder {
		public ImageView imageView;
		public ImageView toggleButton;
		public TextView nametextView;
		public TextView credittextView;
	}
}
