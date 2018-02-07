package com.nfs.youlin.adapter;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.find.StoreCircleDetailActivity;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class comml_list_adapter extends BaseAdapter{
	private Context mcontext;
	private List<Object> data;
	ImageLoader imageLoader;
	public comml_list_adapter(Context context,List<Object> list) {
		// TODO Auto-generated constructor stub
		mcontext = context;
		data = list;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder ;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mcontext).inflate(
					R.layout.coml_shop_listitem, parent, false);
			viewHolder.shop_img = (ImageView) convertView.findViewById(R.id.shop_img);
			viewHolder.shop_name = (TextView)convertView.findViewById(R.id.shop_name);
			viewHolder.shopposition = (TextView)convertView.findViewById(R.id.shopposition);
			viewHolder.distance =  (TextView)convertView.findViewById(R.id.distance);
			viewHolder.room_ratingbar = (RatingBar)convertView.findViewById(R.id.room_ratingbar);
			viewHolder.shopitemlay = (RelativeLayout)convertView.findViewById(R.id.shopitemlay);
			viewHolder.rating_text = (TextView)convertView.findViewById(R.id.rating_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final JSONObject itemjson;
		try {
			 itemjson = new JSONObject(data.get(position).toString());
			String shopUrl = String.valueOf(itemjson.get("img_url"));
			//itemjson.get("img_url").toString()
			if(shopUrl==null||shopUrl.equals("")){
				shopUrl = "null";
			}
			imageLoader.displayImage(shopUrl,viewHolder.shop_img, App.options_error);
			viewHolder.shop_name.setText(itemjson.get("name").toString());
			viewHolder.shopposition.setText(itemjson.get("address").toString());
			int distance = Integer.parseInt(itemjson.get("distance").toString());
			String distoshow = "＜10km";
			if(distance<200){
				distoshow = "＜200m";
			}else if(distance<500){
				distoshow = "＜500m";
			}else if(distance<1000){
				distoshow = "＜1km";
			}else if(distance<2000){
				distoshow = "＜2km";
			}else if(distance<5000){
				distoshow = "＜5km";
			}else if(distance<10000){
				distoshow = "＜10km";
			}
			viewHolder.distance.setText(distoshow);
			Loger.d("test5", "facility="+(int)(Float.parseFloat(itemjson.get("facility").toString())*2));
//			float rate = (int)(Float.parseFloat(itemjson.get("facility").toString())*2);
			viewHolder.rating_text.setText(itemjson.get("facility").toString());
			viewHolder.room_ratingbar.setRating(((float)((int)(Float.parseFloat(itemjson.get("facility").toString())*2)))/2);
			viewHolder.shopitemlay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//chuanshuju
					if(NetworkService.networkBool){
					try {
						Loger.d("test5", itemjson.get("uid").toString());
						Loger.d("test5", itemjson.get("name").toString());
						Loger.d("test5", itemjson.get("img_url").toString());
						mcontext.startActivity(new Intent(mcontext,StoreCircleDetailActivity.class).putExtra("uid", itemjson.get("uid").toString()).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return convertView;
	}
	private class ViewHolder {
		public TextView shop_name;
		public ImageView shop_img;
		public TextView shopposition;
		public TextView distance;
		public RatingBar room_ratingbar;
		public RelativeLayout shopitemlay;
		public TextView rating_text;
	}
}
