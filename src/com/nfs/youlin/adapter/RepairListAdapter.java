package com.nfs.youlin.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.neighbor.CircleDetailGalleryPictureActivity;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.TimeToStr;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class RepairListAdapter extends BaseAdapter {
	public List<Object> data;
	private Context context;
	private int selectID;
	private int myPush;
	private int myTopic;
	private int isgonggao;
	private String responseString;
	private ForumTopic forumTopicObj;
	private ForumtopicDaoDBImpl forumtopicDaoDBImplObj;
	private List<String> commentNameList;
	private List<String> commentContentList;
	private boolean setaddrRequestSet = false;
	public static String flag = "none";
	private static String applyflag = "false";
	private int Ishost;
	private static TextView currentactionapplynum;
	private static TextView currentactionapplyname;
	private int REQUEST_APPLY_NUM = 200;
	public static boolean cRequestSet = false;
	private String applytotalcount;
	private String currenttotalcount;
	public static String dflag = "none";
	private String isDelString;
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;
	ImageLoader imageLoader;
	public RepairListAdapter(List<Object>list, Context context,int myPush) { //searchOrdisplay=0  from search
		imageLoader = ImageLoader.getInstance();
		this.data = list;
		Loger.i("youlin","list.size()---->"+data.size());
		this.context = context;
		this.myPush = myPush;
		
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
		final ViewHolder viewHolder;
		if (convertView == null) {
			Loger.d("test5", "RepairCircleAdapter getview position = "+position);
			convertView = LayoutInflater.from(context).inflate(R.layout.repairlistwithhead, null);
			viewHolder=new ViewHolder();
			viewHolder.repair_title = (TextView)convertView.findViewById(R.id.repair_title);
			viewHolder.repair_time = (TextView)convertView.findViewById(R.id.repair_time);
			viewHolder.repair_name = (TextView)convertView.findViewById(R.id.repair_name);
			viewHolder.repair_status = (TextView)convertView.findViewById(R.id.repair_status);
			viewHolder.repair_head = (ImageView)convertView.findViewById(R.id.repair_head);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		final String headUrl = String.valueOf(((ForumTopic)data.get(position)).getSender_portrait());
//		Picasso.with(context) 
//				.load(headUrl) 
//				.placeholder(R.drawable.default_normal_avatar) 
//				.error(R.drawable.default_normal_avatar)
//				.fit()
//				.tag(context)
//				.into(viewHolder.repair_head);
		imageLoader.displayImage(headUrl, viewHolder.repair_head, App.options_account);
		viewHolder.repair_head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,CircleDetailGalleryPictureActivity.class);
				intent.putExtra("type", 1);
				intent.putExtra("url",headUrl);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				context.startActivity(intent);
			}
		});
		String titleStr=((ForumTopic)data.get(position)).getTopic_title();
		viewHolder.repair_title.setText(titleStr);
		viewHolder.repair_name.setText(((ForumTopic)data.get(position)).getDisplay_name());
		String time=TimeToStr.getTimeElapse(((ForumTopic)data.get(position)).getTopic_time(),App.CurrentSysTime);
		viewHolder.repair_time.setText(time);
		int schedule = ((ForumTopic)data.get(position)).getLike_num(); // 进度
		Loger.d("test5", "schedule="+schedule);
		if(schedule == 1){
			viewHolder.repair_status.setText("等待审核");
			viewHolder.repair_status.setTextColor(0xffff7702);
		}else if(schedule == 2){
			viewHolder.repair_status.setText("审核通过，派人维修");
			viewHolder.repair_status.setTextColor(0xffffba02);
		}else if(schedule == 3){
			viewHolder.repair_status.setText("维修完成");
			viewHolder.repair_status.setTextColor(0xff777777);
		}
		return convertView;
	}
	private static class ViewHolder {
		public TextView repair_title;
		public TextView repair_time;
		public TextView repair_name;
		public TextView repair_status;
		public ImageView repair_head;
 	}
}
