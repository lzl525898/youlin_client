package com.nfs.youlin.adapter;

import java.util.List;
import java.util.Map;

import com.nfs.youlin.R;
import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Baraddresslistadapter extends BaseAdapter{
	private List<Map<String, Object>> data;
	private Context context;
	private LayoutInflater mInflater = null;
	private int res;
	private String[] fromstring; 
	private int[] Toelement;
	private int selectID;
	public Baraddresslistadapter(Context context, List<Map<String, Object>> list, 
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
	            holder.check_view = (ImageView) convertView.findViewById(R.id.address_check);
		        holder.declare_type1 = (LinearLayout)convertView.findViewById(R.id.address_status_success_layout);
		        holder.declare_type2 = (LinearLayout)convertView.findViewById(R.id.address_status_failed_layout);
		        holder.declare_type3 = (LinearLayout)convertView.findViewById(R.id.address_status_waiting_layout);
	           // holder.check_view = (ImageView) convertView.findViewById(Toelement[2]);
	           // holder.declare_type = (LinearLayout)convertView.findViewById(Toelement[3]);
	            convertView.setTag(holder);    
	    } else {    
	            holder = (ViewHolder) convertView.getTag();    
	    } 
		 holder.check_view.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_weixuanzhong_b));
		 holder.name_Text.setText(data.get(position).get(fromstring[0]).toString());
		 if(data.get(position).get(fromstring[1]).equals("1")){
			 holder.check_view.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_xuanzhong_a));
			 holder.name_Text.setTextColor(0xff323232);
		 }
		 if(data.get(position).get(fromstring[2]).toString().equals("1")){//审核通过
			 holder.declare_type1.setVisibility(convertView.VISIBLE);
			 holder.declare_type2.setVisibility(convertView.GONE);
			 holder.declare_type3.setVisibility(convertView.GONE);
		 }else if(data.get(position).get(fromstring[2]).toString().equals("0")){//等待审核
			 holder.declare_type1.setVisibility(convertView.GONE);
			 holder.declare_type3.setVisibility(convertView.VISIBLE);
			 holder.declare_type2.setVisibility(convertView.GONE);
		 }else{//审核失败  2表示审核失败
			 holder.declare_type1.setVisibility(convertView.GONE);
			 holder.declare_type3.setVisibility(convertView.GONE);
			 holder.declare_type2.setVisibility(convertView.VISIBLE);
		 }
		// TODO Auto-generated method stub
		return convertView;
	}
	class ViewHolder {    
		public ImageView check_view;
		public TextView name_Text;
	    public LinearLayout declare_type1;   //审核通过 1
	    public LinearLayout declare_type2;   //等待审核 0
	    public LinearLayout declare_type3;   //审核失败 2
	}
}
