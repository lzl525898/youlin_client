package com.nfs.youlin.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.entity.PushRecord;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.TimeToStr;
import com.nfs.youlin.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class PushMessgeAdapter extends ArrayAdapter<String>{

	private int resource;
	private Context context;
	List<Object> list=new ArrayList<Object>();
//	private List<Object> pushObjectList;
	ImageLoader imageLoader;
	String uId,contentType,pushTime,title,content,userAvatar;
	
	public PushMessgeAdapter(final Context context, int resource, List<String> objects, List<Object> objs) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		imageLoader = ImageLoader.getInstance();
		this.list=objs;
		this.resource = resource;
		this.context = context;
		
	}
	
	class ViewHolder{
		public CircleImageView circleImageView;
		public TextView 	   titleTextView;
		public TextView 	   timeTextView;
		public TextView 	   contentTextView;
		public ImageView       readStatusImageView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) { 
		ViewHolder holder = null;
		//PushRecord pushRecord = (PushRecord)NewPushRecordAbsActivity.sPushInfoObjList.get(position);
		PushRecord pushRecord = (PushRecord)list.get(position);
		int type = pushRecord.getType();
		String msgContent = pushRecord.getContent();
		holder = new ViewHolder();
		convertView = new LinearLayout(this.context);   
		LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
        inflater.inflate(this.resource, (LinearLayout)convertView, true);
        holder.circleImageView = (CircleImageView) convertView.findViewById(R.id.roundImageView);
        holder.titleTextView   = (TextView) convertView.findViewById(R.id.tv_newmessage_title);
        holder.timeTextView    = (TextView) convertView.findViewById(R.id.tv_newmessage_time);
        holder.contentTextView = (TextView) convertView.findViewById(R.id.tv_newmessage_content);
        holder.readStatusImageView = (ImageView) convertView.findViewById(R.id.iv_push_info_read_status);
        holder.contentTextView.setTag(msgContent);
        convertView.setTag(holder);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(msgContent);
			int pushType = jsonObject.getInt("pushType");
			if((1 == pushType) || (2 == pushType) || (7 == pushType) || (8 == pushType)){
				contentType = jsonObject.getString("contentType");
				pushTime    = jsonObject.getString("pushTime");
				title       = jsonObject.getString("title");
				content     = jsonObject.getString("content");
				userAvatar  = jsonObject.getString("userAvatar");
			}else if((3 == pushType) || (4 == pushType)){
				contentType = jsonObject.getString("contentType");
				pushTime    = jsonObject.getString("pushTime");
				title       = jsonObject.getString("pushTitle");
				content     = jsonObject.getString("pushContent");
				userAvatar  = jsonObject.getString("userAvatar");
			}else if(5==pushType){
				contentType = jsonObject.getString("contentType");
				pushTime    = jsonObject.getString("pushTime");
				title       = jsonObject.getString("title");
				content     = jsonObject.getString("message");
				userAvatar  = jsonObject.getString("userAvatar");
			}else if(6==pushType){
				contentType = jsonObject.getString("contentType");
				pushTime    = jsonObject.getString("pushTime");
				title       = jsonObject.getString("title");
				content     = jsonObject.getString("message");
				userAvatar  = jsonObject.getString("userAvatar");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(userAvatar!=null){
			uploadPic(userAvatar,holder.circleImageView);
		}else{
			Drawable drawable = this.context.getResources().getDrawable(R.drawable.account);
			holder.circleImageView.setImageDrawable(drawable);
		}
		if(1==type){//1 new 2 old
			holder.titleTextView.setText(title);
			holder.timeTextView.setText(TimeToStr.getDateToString(Long.parseLong(pushTime)));
			holder.contentTextView.setText(content);
			holder.readStatusImageView.setVisibility(View.VISIBLE);
		}else{
			holder.titleTextView.setText(title);
			holder.timeTextView.setText(TimeToStr.getDateToString(Long.parseLong(pushTime)));
			holder.contentTextView.setText(content);
			holder.readStatusImageView.setVisibility(View.GONE);
		}
		
		convertView.setTag(holder);
		return convertView;
	}
	
	private void uploadPic(String picPath, CircleImageView circleImageView){
		try {
//			Picasso.with(this.context) 
//			.load(picPath) 
//			.placeholder(R.drawable.account) 
//			.error(R.drawable.account) 
//			.fit() 
//			.tag(this.context) 
//			.into(circleImageView);
			imageLoader.displayImage(picPath, circleImageView, App.options_account);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
