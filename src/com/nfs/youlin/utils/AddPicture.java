package com.nfs.youlin.utils;

import java.io.File;
import java.io.InputStream;

import com.easemob.chatuidemo.adapter.MessageAdapter.ViewHolder;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.activity.titlebar.startactivity.AlbumActivity;
import com.nfs.youlin.activity.titlebar.startactivity.GalleryActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

public class AddPicture {
	public static GridAdapter adapter;
	private View parentView;
	private GridView noScrollgridview;
	private LinearLayout ll_popup;
	private PopupWindow pop = null;
	private Context mContext;
	public AddPicture(Context context){
		mContext=context;
		parentView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.activity_new_topic,null);
		Res.init(context);
//		PublicWay.activityList.add((Activity) mContext);
		Init();
	}
	public void Init() {
		pop = new PopupWindow(mContext);
		View view = ((Activity) mContext).getLayoutInflater().inflate(
				R.layout.activity_send_activity_picture, null);
		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		pop.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				new BackgroundAlpha(1f, mContext);
			}
		});
		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		Button bt1 = (Button) view.findViewById(R.id.send_btn_take_photo);
		Button bt2 = (Button) view.findViewById(R.id.send_btn_pick_photo);
		Button bt3 = (Button) view.findViewById(R.id.send_cancel);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				photo();
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(mContext,AlbumActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				mContext.startActivity(intent);
				((Activity) mContext).overridePendingTransition(R.anim.activity_translate_in,
						R.anim.activity_translate_out);
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				pop.dismiss();
				ll_popup.clearAnimation();
			}
		});

		noScrollgridview = (GridView) ((Activity) mContext).findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(mContext);
		noScrollgridview.setAdapter(adapter);
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				new BackgroundAlpha(0.5f, mContext);
				InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				Loger.i("youlin", "size+arg2-->" + Bimp.tempSelectBitmap.size()
						+ "+" + arg2);
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					Loger.i("youlin", "---onItemClick------");
					ll_popup.startAnimation(AnimationUtils.loadAnimation(
							mContext, R.anim.activity_translate_in));
					pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
				} else {
					Intent intent = new Intent(mContext,GalleryActivity.class);
					intent.putExtra("position", "1");
					intent.putExtra("ID", arg2);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					mContext.startActivity(intent);
				}
			}
		});

	}
	
	public static final int TAKE_PICTURE = 0x000001;

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "circleImg.jpg")));
		((Activity) mContext).startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
	
	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			if (Bimp.tempSelectBitmap.size() == 9) {
				return 9;
			}
			return (Bimp.tempSelectBitmap.size() + 1);
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.item_published_grida,parent, false);
			ImageView image = (ImageView) convertView.findViewById(R.id.item_grida_image);
			if (position == Bimp.tempSelectBitmap.size()) {
				InputStream in=mContext.getResources().openRawResource(R.drawable.icon_addpic_unfocused);
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inPreferredConfig=Config.RGB_565;
				options.inJustDecodeBounds=false;
				options.inPurgeable=true;
				options.inInputShareable=true;
				image.setImageBitmap(BitmapFactory.decodeStream(in,null,options));
				//image.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					image.setVisibility(View.GONE);
				}
			} else {
				Bitmap bitmap=Bimp.tempSelectBitmap.get(position).getBitmap();
				File file=new File(Bimp.tempSelectBitmap.get(position).getImagePath());
				if(bitmap==null || !file.exists()){
					image.setImageResource(R.drawable.plugin_camera_no_pictures);
				}else{
					image.setImageBitmap(bitmap);
				}
			}
			return convertView;
		}
		
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.tempSelectBitmap.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							Bimp.max += 1;
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					}
				}
			}).start();
		}
	}
	

}
