package com.nfs.youlin.adapter;



import com.nfs.youlin.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OpinionPopupMenuAddAdapter extends BaseAdapter {
	private int[] picIdArrs = {
			R.drawable.title_icon_add,
			R.drawable.title_icon_add,
	};
	
	private String[] addContexts = {
			"新建话题",
			"发起活动",
	};
	
	private Context context; 
	
	public OpinionPopupMenuAddAdapter(Context context){
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
	
//	private class ViewHolder{
//		private TextView  textView;
//		private ImageView imageView;
//		protected TextView getTextView() {
//			return textView;
//		}
//		protected void setTextView(TextView textView) {
//			this.textView = textView;
//		}
//		protected ImageView getImageView() {
//			return imageView;
//		}
//		protected void setImageView(ImageView imageView) {
//			this.imageView = imageView;
//		}
//	}
}
