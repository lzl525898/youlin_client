package com.nfs.youlin.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nfs.youlin.adapter.Imagebuttonadapter.ViewHolder;
import com.nfs.youlin.utils.Loger;

public class Imagebuttonadapterwithnew extends BaseAdapter{
	private List<Map<String, Object>> data;
	private Context context;
	private LayoutInflater mInflater = null;
	private int res;
	private String[] fromstring; 
	private int[] Toelement;
	private int selectID;
	public Imagebuttonadapterwithnew(Context context, List<Map<String, Object>> list, 
    		int resource, String[] from, int[] to) {
		// TODO Auto-generated constructor stub
		data = list;
		mInflater = (LayoutInflater) context  
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		res = resource;
		Loger.i("youlin","list.size()---->"+data.size());
		this.context = context;
		fromstring =from;
        Toelement = to;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		 return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		 if (convertView == null) {    
	            holder = new ViewHolder();    
	            convertView = mInflater.inflate(res, null); 
	            holder.name_Text = (TextView) convertView.findViewById(Toelement[0]);
	            holder.newmsg_view = (ImageView) convertView.findViewById(Toelement[1]);
	            holder.click_view = (LinearLayout) convertView.findViewById(Toelement[2]); 
	            holder.normal_view = (LinearLayout) convertView.findViewById(Toelement[3]);
	            convertView.setTag(holder);    
	    } else {    
	            holder = (ViewHolder) convertView.getTag();    
	    } 

		 holder.name_Text.setText(data.get(position).get(fromstring[0]).toString());
		// holder.newmsg_view.setImageResource(Integer.parseInt(data.get(position).get(fromstring[1]).toString()));
		 if(data.get(position).get(fromstring[1]).equals("1")){
			 holder.newmsg_view.setVisibility(View.VISIBLE);
		 }else{
			 holder.newmsg_view.setVisibility(View.INVISIBLE);
		 }
		 if(data.get(position).get(fromstring[2]).equals("1")){
			 holder.click_view.setVisibility(View.VISIBLE);
			 holder.normal_view.setVisibility(View.GONE);
			 holder.name_Text.setTextColor(0xffffba02);
		 }else if(data.get(position).get(fromstring[2]).equals("0")){
			 holder.click_view.setVisibility(View.GONE);
			 holder.normal_view.setVisibility(View.VISIBLE);
			 holder.name_Text.setTextColor(0xff909090);
		 }else{
			 
		 }
		// TODO Auto-generated method stub
		return convertView;
	}
	class ViewHolder {    
		public LinearLayout click_view;
		public LinearLayout normal_view;
		public TextView name_Text;
		public ImageView newmsg_view;
	}
}
