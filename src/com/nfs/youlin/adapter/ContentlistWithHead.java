package com.nfs.youlin.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.find.FindFragment;
import com.nfs.youlin.activity.find.NeighborActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailGalleryPictureActivity;
import com.nfs.youlin.activity.personal.FriendInformationActivity;
import com.nfs.youlin.activity.personal.PersonalInformationActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContentlistWithHead extends BaseAdapter{
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
	public ContentlistWithHead(Context context, List<Map<String, Object>> list, 
    		int resource, String[] from, int[] to) {
		// TODO Auto-generated constructor stub
		imageLoader = ImageLoader.getInstance();
		try {
			data = list;
			data.size();
		} catch (Exception e) {
			data = new ArrayList<Map<String, Object>>();
			Loger.i("TEST","ContentlistWithHead-Error->"+e.getMessage());
		}
		mInflater = (LayoutInflater) context  
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		res = resource;
		Loger.i("youlin","list.size()---->"+data.size());
		this.context = context;
		fromstring =from;
		Loger.d("test3","data:  "+data.size()+":"+from[2]);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		try {
			String item = data.get(position).get(fromstring[1]).toString();
			ViewHolder holder = null;
			if (item.length() == 1) {
//			if (convertView == null) {
					holder = new ViewHolder();
					convertView = mInflater.inflate(R.layout.neighbor_list_index_item, null);
					holder.indexTv = (TextView) convertView.findViewById(R.id.indexTv);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
			} else {
//			if (convertView == null) {
					holder = new ViewHolder();
					convertView = mInflater.inflate(res, null);
					holder.item_view = (ImageView) convertView.findViewById(Toelement[0]);
					holder.name_Text = (TextView) convertView.findViewById(Toelement[1]);
					holder.profession_Text = (TextView) convertView.findViewById(Toelement[2]);
					holder.detail_Text = (TextView) convertView.findViewById(Toelement[3]);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
			}
			
			if (item.length() == 1) {
				holder.indexTv.setText(data.get(position).get(fromstring[1]).toString());
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(NeighborActivity.indexInitFlag){
							Intent intent = new Intent("com.nfs.youlin.activity.find.pinyinActivity");
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							context.startActivity(intent);
							//((Activity)context).overridePendingTransition(R.anim.popup_enter_activity,R.anim.popup_exit_activity);
						}else{
							Loger.i("TEST", "convertView==>"+data.get(position).get(fromstring[1]).toString());
						}
					}
				});
			}else{
				int status = Integer.parseInt(data.get(position).get("status").toString());
				final String headUrl = data.get(position).get(fromstring[0]).toString();
				if (data.get(position).get("blackflag").toString().equals("1")) {
					// Picasso.with(context) //
					// .load(headUrl+"o") //
					// .placeholder(R.drawable.blackava) //
					// .error(R.drawable.blackava) //
					// .fit() //
					// .tag(context) //
					// .into(holder.item_view);
					imageLoader.displayImage(headUrl + "o", holder.item_view, App.options_blackava);
				} else {
					// Picasso.with(context) //
					// .load(headUrl) //
					// .placeholder(R.drawable.default_normal_avatar) //
					// .error(R.drawable.default_normal_avatar) //
					// .fit() //
					// .tag(context) //
					// .into(holder.item_view);
					imageLoader.displayImage(headUrl, holder.item_view, App.options_account);
				}
				holder.item_view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
							Intent intent = new Intent(context,FriendInformationActivity.class);
							intent.putExtra("sender_id",Long.parseLong(data.get(position).get("userid").toString()));
							intent.putExtra("display_name",data.get(position).get(fromstring[1]).toString());
							intent.putExtra("sender_portrait",headUrl);
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							context.startActivity(intent);
					}
				});
				String ss = data.get(position).get("profession").toString();
				if (ss.equals("0") || ss == null || ss.length() < 1 || ss.equals("null")) {
					ss = "未设置";
				}
				holder.name_Text.setText(data.get(position).get(fromstring[1]).toString());
				holder.name_Text.setTag(position);
				if (status == 3 || status == 4) {
					holder.profession_Text.setText(ss);
				} else {
					holder.profession_Text.setText("未公开");
				}
				if (status == 2 || status == 4) {
					holder.detail_Text.setText(data.get(position).get(fromstring[3]).toString());
				} else {
					holder.detail_Text.setText("未公开");
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertView;
	}
	
	class ViewHolder {
		public ImageView item_view;
		public TextView name_Text;
		public TextView profession_Text;
		public TextView detail_Text;
		public TextView indexTv;  
	}
}
