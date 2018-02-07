package com.nfs.youlin.adapter;

import java.util.List;
import java.util.Map;

import com.nfs.youlin.utils.Loger;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
class ViewHolder {    

    public TextView item_Text;  
    public ImageView item_view;
}
public class Search_history_adapter extends BaseAdapter{
	private Context mContext;
	private LayoutInflater mInflater = null;
	private int res;
	List<Map<String, Object>> list;
	private String[] fromstring; 
	private int[] Toelement;
	private Handler whathand;
	private int SelectID;
    public Search_history_adapter(Context context, List<Map<String, Object>> data, 
    		int resource, String[] from, int[] to,Handler hander){  
        super();  
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        res = resource;
        list = data;
        fromstring =from;
        Toelement = to;
        whathand = hander;
    }  
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(res, null);
			holder.item_Text = (TextView) convertView.findViewById(Toelement[0]);
			holder.item_view = (ImageView) convertView.findViewById(Toelement[1]);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.item_Text.setText(list.get(position).get(fromstring[0]).toString());
		holder.item_view.setTag(position);
		holder.item_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Loger.d("test1", "holder.item_view.setOnClickListener"+arg0.getTag());
				SelectID = Integer.parseInt(arg0.getTag().toString());
//				Search_history_adapter.this.notifyDataSetChanged();
				Message msg = new Message();
				msg.what = 1;
				Bundle data = new Bundle();
				data.putInt("position", SelectID);
				msg.setData(data);
				whathand.sendMessage(msg);
			}
		});
		return convertView;
	}

}
