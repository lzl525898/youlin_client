package com.nfs.youlin.adapter;

import java.util.List;
import java.util.Map;

import com.nfs.youlin.R;
import com.nfs.youlin.adapter.ContentlistWithHead.ViewHolder;
import com.nfs.youlin.utils.Loger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class communityserviceadapter extends BaseAdapter{
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
	private static Toast toast ;
	public communityserviceadapter(Context context, List<Map<String, Object>> list, 
    		int resource, String[] from, int[] to) {
		// TODO Auto-generated constructor stub
		data = list;
		mInflater = (LayoutInflater) context  
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		res = resource;
		Loger.i("youlin","list.size()---->"+data.size());
		this.context = context;
		toast = new Toast(context.getApplicationContext());
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		 if (convertView == null) {    
	            holder = new ViewHolder();    
	            convertView = mInflater.inflate(res, null); 
	            holder.item_view = (TextView)convertView.findViewById(Toelement[0]);
	            holder.phone_Text = (TextView) convertView.findViewById(Toelement[1]);
	            holder.address_Text = (TextView) convertView.findViewById(Toelement[2]);
	            holder.worktime_Text = (TextView) convertView.findViewById(Toelement[3]);
	            holder.address_view = (ImageView) convertView.findViewById(R.id.addressview);
	            holder.phone_view = (ImageView) convertView.findViewById(R.id.phoneview);
	            holder.worktime_view = (ImageView) convertView.findViewById(R.id.timeview);
	            holder.wuyexinxi_layout = (LinearLayout) convertView.findViewById(R.id.wuyexinxi);
	            convertView.setTag(holder);    
	        } else {    
	            holder = (ViewHolder) convertView.getTag();    
	        } 
		if(data.get(position).get(fromstring[0]).toString().equals("居委会")){
			holder.item_view.setBackground(context.getResources().getDrawable(R.drawable.pic_juweihui));
			holder.address_view.setBackground(context.getResources().getDrawable(R.drawable.pic_dizhi));
			holder.phone_view.setBackground(context.getResources().getDrawable(R.drawable.pic_dianhua));
			holder.worktime_view.setBackground(context.getResources().getDrawable(R.drawable.pic_shijian));
			holder.phone_Text.setTextColor(0xffec5b5b);
		}else if(data.get(position).get(fromstring[0]).toString().equals("卫生所")){
			holder.item_view.setBackground(context.getResources().getDrawable(R.drawable.pic_weishengsuo));
			holder.address_view.setBackground(context.getResources().getDrawable(R.drawable.pic_dizhi2));
			holder.phone_view.setBackground(context.getResources().getDrawable(R.drawable.pic_dianhua2));
			holder.worktime_view.setBackground(context.getResources().getDrawable(R.drawable.pic_shijian2));
			holder.phone_Text.setTextColor(0xff4f91f2);
		}else if(data.get(position).get(fromstring[0]).toString().equals("派出所")){
			holder.item_view.setBackground(context.getResources().getDrawable(R.drawable.pic_paichusuo));
			holder.address_view.setBackground(context.getResources().getDrawable(R.drawable.pic_dizhi3));
			holder.phone_view.setBackground(context.getResources().getDrawable(R.drawable.dianhuayellow));
			holder.worktime_view.setBackground(context.getResources().getDrawable(R.drawable.pic_shijian3));
			holder.phone_Text.setTextColor(0xffffba02);
		}else{
			holder.item_view.setBackground(context.getResources().getDrawable(R.drawable.youlin));
			holder.address_view.setBackground(context.getResources().getDrawable(R.drawable.icon_dizhi));
			holder.phone_view.setBackground(context.getResources().getDrawable(R.drawable.icon_dianhua));
			holder.worktime_view.setBackground(context.getResources().getDrawable(R.drawable.icon_shijian));
		}
		
		
		holder.phone_Text.setText(data.get(position).get(fromstring[1]).toString());
		holder.address_Text.setTag(position);
		holder.address_Text.setText(data.get(position).get(fromstring[2]).toString());
		holder.address_Text.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					selectID = Integer.parseInt(v.getTag().toString());
					Loger.d("test5","address_Text touch down position="+selectID );
					LayoutInflater inflater = LayoutInflater.from(context);
					View layout = inflater.inflate(R.layout.selfdesign_toast_lay, null);
					TextView toasttext = (TextView)layout.findViewById(R.id.toastword);
					toasttext.setText(data.get(selectID).get(fromstring[2]).toString());
				
					toast.setGravity(Gravity.TOP,0,(int)toasttext.getTextSize()*3);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(layout);
					toast.show();
					return true;
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					selectID = Integer.parseInt(v.getTag().toString());
					toast.cancel();
					Loger.d("test5","address_Text touch up position="+selectID );
					return true;
				}
				return false;
			}
		});
		holder.worktime_Text.setTag(position);
		holder.worktime_Text.setText(data.get(position).get(fromstring[3]).toString());
		holder.worktime_Text.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					selectID = Integer.parseInt(v.getTag().toString());
					Loger.d("test5","worktime_Text touch down position="+selectID );
					LayoutInflater inflater = ((Activity)context).getLayoutInflater();
					View layout = inflater.inflate(R.layout.selfdesign_toast_lay, null);
					TextView toasttext = (TextView)layout.findViewById(R.id.toastword);
					toasttext.setText(data.get(selectID).get(fromstring[3]).toString());
//					Toast toast = new Toast(context);					
//					toast.setGravity(Gravity.TOP | Gravity.LEFT,(int)(v.getX()+((View)v.getParent()).getX()),(int)toasttext.getTextSize());
					toast.setGravity(Gravity.TOP,0,(int)toasttext.getTextSize()*3);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(layout);
					toast.show();
					return true;
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					selectID = Integer.parseInt(v.getTag().toString());
					toast.cancel();
					Loger.d("test5","worktime_Text touch up position="+selectID );
					return true;
				}
				return false;
			}
		});
		holder.phone_Text.setTag(position);
		holder.phone_Text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectID = Integer.parseInt(arg0.getTag().toString());
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ data.get(selectID).get(fromstring[1]).toString()));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});
//		holder.wuyexinxi_layout.setTag(position);
//		holder.wuyexinxi_layout.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				selectID = Integer.parseInt(v.getTag().toString());
//				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ data.get(selectID).get(fromstring[1]).toString()));
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				context.startActivity(intent);
//			}
//		});
		return convertView;
	}
	class ViewHolder {    
		public TextView item_view;
		public TextView address_Text;
	    public TextView phone_Text;   
	    public TextView worktime_Text; 
	    public ImageView address_view;
	    public ImageView phone_view;
	    public ImageView worktime_view;
	    public LinearLayout wuyexinxi_layout;
	    public TextView textdetail;
	}
}
