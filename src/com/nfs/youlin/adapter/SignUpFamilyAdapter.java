package com.nfs.youlin.adapter;

import java.util.List;
import java.util.Map;

import com.nfs.youlin.R;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.utils.App;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.provider.MediaStore.Images;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SignUpFamilyAdapter extends BaseAdapter{
	private Context context;
	private List<Map<String, Object>> list;
	ImageLoader imageLoader;
	public SignUpFamilyAdapter(Context context,List<Map<String, Object>> list) {
		// TODO Auto-generated constructor stub
		imageLoader = ImageLoader.getInstance();
		this.context=context;
		this.list=list;
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.list_signup_item, null);
			viewHolder.headIv=(ImageView)convertView.findViewById(R.id.iv_signup_portrait);
			viewHolder.nameTv=(TextView)convertView.findViewById(R.id.tv_signup_name);
			viewHolder.familyMembersNumTv=(TextView)convertView.findViewById(R.id.signup_family_members_num);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		String headUrl = String.valueOf(list.get(position).get("portrait"));
//		Picasso.with(context) 
//				.load(headUrl) 
//				.placeholder(R.drawable.default_normal_avatar) 
//				.error(R.drawable.default_normal_avatar) 
//				.fit() 
//				.tag(context) 
//				.into(viewHolder.headIv);
		imageLoader.displayImage(headUrl, viewHolder.headIv, App.options_account);
		viewHolder.nameTv.setText(String.valueOf(list.get(position).get("name")));
		viewHolder.familyMembersNumTv.setText(String.valueOf(list.get(position).get("sign_num")));
		return convertView;
	}
	public static class ViewHolder{
		ImageView headIv;
		TextView nameTv;
		TextView familyMembersNumTv;
	}
}
