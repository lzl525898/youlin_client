package com.nfs.youlin.adapter;

import java.util.List;

import com.nfs.youlin.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Coml_cate_adapter extends BaseAdapter{
	private List<String> data;
	private Context mcontext;
	private int selectedindex;
	public Coml_cate_adapter(Context context,List<String> list,int selected) {
		// TODO Auto-generated constructor stub
		this.data = list;
		this.mcontext = context;
		selectedindex = selected;
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
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView = LayoutInflater.from(mcontext).inflate(R.layout.com_cate_item, null);
			holder.categrayitem=(TextView)convertView.findViewById(R.id.categrayname);
			
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.categrayitem.setText(data.get(position));
		if(selectedindex == position){
			holder.categrayitem.setTextColor(0xffffba02);
		}else{
			holder.categrayitem.setTextColor(0xff323232);
		}
		return convertView;
	}
	public class ViewHolder{
		TextView categrayitem;
	}
}
