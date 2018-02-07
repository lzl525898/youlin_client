package com.nfs.youlin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nfs.youlin.R;

public class searchmodeadapter extends BaseAdapter{
	private int[] picIdArrs = {
			R.drawable.quanbusou,R.drawable.huatisou,R.drawable.huodongsou,R.drawable.gonggaosou,R.drawable.jianyisou
	};
	
	private String[] addContexts = {
			"搜索全部","搜索话题","搜索活动","搜索公告","搜索建议"
	};
	
	private Context context; 
	
	public searchmodeadapter(Context context){
		this.context = context;
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return picIdArrs.length;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = LayoutInflater.from(this.context.getApplicationContext()).inflate(R.layout.popup_title_add_item, null);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_popup_title_item);
			imageView.setImageResource(picIdArrs[position]);
			TextView textView = (TextView) convertView.findViewById(R.id.tv_popup_title_item);
			textView.setText(addContexts[position]);
		}
		return convertView;
	}
}
