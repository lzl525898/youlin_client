package com.nfs.youlin.adapter;

import java.util.List;
import java.util.Map;

import com.nfs.youlin.R;
import com.nfs.youlin.adapter.AddressShowAdapter.ViewHolder;
import com.nfs.youlin.utils.AutoscrallTextview;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.TimeToStr;
import com.nfs.youlin.utils.gettime;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Invitehistoryadapter extends BaseAdapter{
	private List<Map<String, Object>> data;
	private Context context;
	private LayoutInflater mInflater = null;
	private int res;
	private String[] fromstring; 
	private int[] Toelement;
	
	
	public Invitehistoryadapter(Context context, List<Map<String, Object>> list, 
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
		ViewHolder holder = null;
		 if (convertView == null) {    
	            holder = new ViewHolder();    
	            convertView = mInflater.inflate(res, null); 
	           
	            holder.inv_time = (TextView) convertView.findViewById(Toelement[0]);
		        holder.inv_phone = (TextView)convertView.findViewById(Toelement[1]);
		        holder.inv_status = (TextView)convertView.findViewById(Toelement[2]);
	            convertView.setTag(holder);    
	    } else {    
	            holder = (ViewHolder) convertView.getTag();    
	    } 
//		 holder.inv_time.setText(gettime.getStrTime2(data.get(position).get(fromstring[0]).toString()));
		 holder.inv_time.setText(TimeToStr.getTimeToStrInvate(Long.parseLong(data.get(position).get(fromstring[0]).toString())));
		 holder.inv_phone.setText(data.get(position).get(fromstring[1]).toString());
		 if(data.get(position).get(fromstring[2]).toString().equals("1")){
			 holder.inv_status.setText("邀请中");
		 }else if(data.get(position).get(fromstring[2]).toString().equals("2")){
			 holder.inv_status.setText("邀请成功");
		 }else if(data.get(position).get(fromstring[2]).toString().equals("3")){
			 holder.inv_status.setText("邀请失败");
		 }
		 
		 
		return convertView;
	}
	class ViewHolder {    
		public TextView inv_time;
		public TextView inv_phone;
	    //public LinearLayout declare_type1;   
	    public TextView inv_status; 
	}
}
