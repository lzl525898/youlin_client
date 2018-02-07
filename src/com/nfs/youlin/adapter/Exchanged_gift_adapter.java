package com.nfs.youlin.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.Res;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Exchanged_gift_adapter extends BaseAdapter{
	private Context mcontext;
	private List<Map<String, Object>> data;
	private String[] currentday = new String[3];
	private ImageLoader imageLoader;
	public Exchanged_gift_adapter(Context context,List<Map<String, Object>> list){
		mcontext = context;
		data = list;
		Loger.d("test5", "------"+data.toString());
        imageLoader = ImageLoader.getInstance();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder ;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mcontext).inflate(
					R.layout.exchanged_gift_item, parent, false);
			viewHolder.giftview = (ImageView) convertView.findViewById(R.id.giftview1);
			viewHolder.arrivetime = (TextView) convertView
					.findViewById(R.id.arrivetime);
			viewHolder.nametextView = (TextView) convertView
					.findViewById(R.id.giftname1);
			viewHolder.credittextView = (TextView) convertView
					.findViewById(R.id.sumcredit);
			viewHolder.counttextView = (TextView) convertView
					.findViewById(R.id.giftcount);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
//		Picasso.with(mcontext)
//		  .load(data.get(position).get("giftimg").toString())
//		  .error(R.drawable.plugin_camera_no_pictures)
//		  .placeholder(R.drawable.plugin_camera_no_pictures)
//		  .into(viewHolder.giftview);
		imageLoader.displayImage(data.get(position).get("giftimg").toString(), viewHolder.giftview, App.options_plugin_camera_no_pictures);
		viewHolder.nametextView.setText(data.get(position).get("name").toString());
		viewHolder.credittextView.setText(data.get(position).get("credit").toString()+"积分");
		viewHolder.counttextView.setText("x "+data.get(position).get("giftnum").toString());
		
//		data.get(position).get("gifttime").toString()
		
		Date gifyDay = new Date(Long.valueOf(data.get(position).get("gifttime").toString()));
		Loger.d("test5",data.get(position).get("gifttime").toString());
        int weekday = gifyDay.getDay();  //星期几
        Loger.d("test5","weekday="+weekday+"day="+gifyDay.getDate()+"month="+gifyDay.getMonth()+"year="+gifyDay.getYear());
        
        
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");       
		String date = sDateFormat.format(new java.util.Date());
		currentday = date.split("-");
		Date today = new Date(Integer.parseInt(currentday[0])-1900, Integer.parseInt(currentday[1])-1, Integer.parseInt(currentday[2]));
		if(data.get(position).get("giftstatus").toString().equals("2")){
			viewHolder.arrivetime.setText(" 交换成功 ");
		}else{
			if(today.getDay()<6 && today.getDay()>0){
	        	viewHolder.arrivetime.setText(" 本周五送达 ");
	        }else{
	        	viewHolder.arrivetime.setText(" 下周五送达 ");
	        }
		}
        
        
		return convertView;
	}
	private class ViewHolder {
		public ImageView giftview;
		public TextView arrivetime;
		public TextView nametextView;
		public TextView credittextView;
		public TextView counttextView;
	}
}
