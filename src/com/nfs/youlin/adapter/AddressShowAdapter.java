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

import com.nfs.youlin.R;
import com.nfs.youlin.adapter.Baraddresslistadapter.ViewHolder;
import com.nfs.youlin.utils.AutoscrallTextview;
import com.nfs.youlin.utils.Loger;

public class AddressShowAdapter extends BaseAdapter{
	private List<Map<String, Object>> data;
	private Context context;
	private int aaaa;
	private LayoutInflater mInflater = null;
	private int res;
	private String[] fromstring; 
	private int[] Toelement;
	private int selectID;
	
	public AddressShowAdapter(Context context, List<Map<String, Object>> list, 
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
	           
	            holder.name_Text = (AutoscrallTextview) convertView.findViewById(R.id.address_detail);
		        //holder.declare_type1 = (LinearLayout)convertView.findViewById(R.id.address_status_success_showlayout);
	            holder.declare_text = (TextView)convertView.findViewById(R.id.address_false_text);
		        holder.declare_type2 = (LinearLayout)convertView.findViewById(R.id.address_status_failed_showlayout);
		        holder.current_view = (ImageView)convertView.findViewById(R.id.currentview);
	            convertView.setTag(holder);    
	    } else {    
	            holder = (ViewHolder) convertView.getTag();    
	    } 
		 String address = data.get(position).get(fromstring[0]).toString();

		 holder.name_Text.setText(address);
		 holder.name_Text.setSpeed(AutoscrallTextview.SPEED_NORMAL);  
		 if( holder.name_Text.length()>20){
			 holder.name_Text.startScroll();
		 }
		 

		 if(data.get(position).get(fromstring[1]).toString().equals("1")){
			// holder.declare_type1.setVisibility(View.GONE);
			 holder.declare_type2.setVisibility(View.GONE);
		 }else{
			// holder.declare_type1.setVisibility(View.GONE);
			 holder.declare_type2.setVisibility(View.VISIBLE);
			 Loger.d("test5","***************"+data.get(position).get(fromstring[1]).toString() );
			 if(data.get(position).get(fromstring[1]).toString().equals("2")){
				 holder.declare_text.setText("【审核失败】");
			 }else if(data.get(position).get(fromstring[1]).toString().equals("0")){
				 holder.declare_text.setText("【等待审核】");
			 }
		 }
		 if(data.get(position).get("current").toString().equals("1")){
			 holder.current_view.setVisibility(View.VISIBLE);
		 }else{
			 holder.current_view.setVisibility(View.GONE);
		 }
		// TODO Auto-generated method stub
		 
		return convertView;
	}
	class ViewHolder {    
		public ImageView current_view;
		public AutoscrallTextview  name_Text;
		private TextView declare_text;
	    //public LinearLayout declare_type1;   
	    public LinearLayout declare_type2; 
	}
}
