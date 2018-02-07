package com.nfs.youlin.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.adapter.NewsListAdapter.ViewHolder;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OtherNewsadapter extends BaseAdapter{
	private List<Map<String, Object>> data;
	private Context context;
	private LayoutInflater mInflater = null;
	private int res;
	private String[] fromstring; 
	private int[] Toelement;
	private int selectID;
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;
	ImageLoader imageLoader;
	public OtherNewsadapter(Context context, List<Map<String, Object>> list, 
    		int resource, String[] from, int[] to) {
		// TODO Auto-generated constructor stub
		imageLoader = ImageLoader.getInstance();
		data = list;
		mInflater = (LayoutInflater) context  
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		res = resource;
		Loger.i("youlin","other news list.size()---->"+data.size());
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
		return data.get(arg0);
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
	            holder.newsimg = (ImageView)convertView.findViewById(R.id.newsotherimg);
	            holder.newstitle = (TextView) convertView.findViewById(R.id.newsothertitle);
	            convertView.setTag(holder);    
	        } else {    
	            holder = (ViewHolder) convertView.getTag();    
	        } 
//		Picasso.with(context) 
//			.load(data.get(position).get("url").toString()) 
//			.placeholder(R.drawable.bg_error) 
//			.error(R.drawable.bg_error)
//			.fit()
//			.tag(context)
//			.into(holder.newsimg);
		imageLoader.displayImage(data.get(position).get("url").toString(), holder.newsimg, App.options_error);
		
		holder.newstitle.setText(data.get(position).get("title").toString());
		
		return convertView;
	}
  
	class ViewHolder {    
	    public ImageView newsimg;
	    public TextView newstitle;
	}
}
