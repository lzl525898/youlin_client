package com.nfs.youlin.adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.personal.SystemSetActivity;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Bimp;
import com.nfs.youlin.utils.ImageItem;
import com.nfs.youlin.utils.dp2px;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;

public class FriendCircleImageAdapter extends BaseAdapter {
	private Context context;
	private List<String> urls = new ArrayList<String>();
	ImageLoader imageLoader;
	public FriendCircleImageAdapter(Context context,List<String> url) {
		imageLoader = ImageLoader.getInstance();
		this.context = context;
		this.urls=url;
//		Collections.addAll(urls, ImgUrlData.URLS);
//		Collections.shuffle(urls);
//		circleImgUrl.url.clear();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return urls.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return urls.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.friend_circle_gridview_imageview, parent, false);
			holder.imageView = (ImageView) convertView.findViewById(R.id.friend_gridview_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.imageView.setScaleType(ScaleType.CENTER_CROP);
		String url = null;
		if (parent.getChildCount() == position) {
			url = (String) getItem(position);
//			Picasso.with(context) //
//					.load(url) //
//					.error(R.drawable.bg_error)
//					.placeholder(R.drawable.bg_error)
//					.tag(context) //
//					.into(holder.imageView);
			
			imageLoader.displayImage(url, holder.imageView, App.options_error);
		}
		return convertView;
	}

	private class ViewHolder {
		public ImageView imageView;
	}
}
