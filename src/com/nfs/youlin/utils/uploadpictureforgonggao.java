package com.nfs.youlin.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.nfs.youlin.R;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;

public class uploadpictureforgonggao {
	private Context mContext;
	Bitmap myBitmap=null;
	Bitmap bitmap=null;
	RequestParams mRequestParams;
	private StatusChangeutils changeutils;
	Bimp bimp=new Bimp();
	private String[] imgArray = {"img_0", "img_1", "img_2",
			                     "img_3", "img_4", "img_5",
			                     "img_6", "img_7", "img_8"};
	private List<String> filePathList = new ArrayList<String>();

	public uploadpictureforgonggao(final Context context,RequestParams sRequestParams) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		mRequestParams = sRequestParams;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				int length = Bimp.tempSelectBitmap.size();
				if (0 == length) {
					mRequestParams.put("send_status", 0);
				} else {
					mRequestParams.put("send_status", 1);
				}
				for (int i = 0; i < length; i++) {
					String newPath = null;
					if (Bimp.tempSelectBitmap.get(i).getImagePath() == null) {
						newPath = Bimp.tempSelectBitmap.get(i)
								.getThumbnailPath();
					} else {
						newPath = Bimp.tempSelectBitmap.get(i).getImagePath();
					}
					XuanzhuanBitmap xuanzhuanBitmap = new XuanzhuanBitmap();
					int degree = xuanzhuanBitmap.readPictureDegree(newPath);
					try {
						BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(newPath)));
						//FileInputStream in = new FileInputStream(new File(path));
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inJustDecodeBounds = true;
						BitmapFactory.decodeStream(in, null, options);
						in.close();
						
						int scale = 1;
				        if (options.outHeight > 1000 || options.outWidth > 1000) {
				            scale = (int)Math.pow(2, (int) Math.round(Math.log(1000 / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
				        }
				        BitmapFactory.Options o2 = new BitmapFactory.Options();
				        o2.inSampleSize = scale;
				        o2.inJustDecodeBounds = false;
				        options.inPurgeable = true;
						options.inInputShareable = true;
				        in = new BufferedInputStream(new FileInputStream(new File(newPath)));
				        bitmap = BitmapFactory.decodeStream(in, null, o2);
				        in.close();
						myBitmap = xuanzhuanBitmap.rotaingImageView(degree,bitmap);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						myBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.bg_error);
						e1.printStackTrace();
					}
					String path=FileUtils.SDPATH+System.currentTimeMillis()+".jpg";
					File fileDir=new File(FileUtils.SDPATH+"");
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
						fileDir.mkdir();
					}
					File file = new File(path);
					try {
						FileOutputStream out = new FileOutputStream(file);
						myBitmap.compress(Bitmap.CompressFormat.JPEG, 50,out);
						out.flush();
						out.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						mRequestParams.put(imgArray[i], file);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					filePathList.add(path);
					//Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), myBitmap, null, null));
					
					myBitmap.recycle();
					myBitmap=null;
					bitmap.recycle();
					bitmap=null;
					System.gc();
					
//					ContentResolver cr = context.getContentResolver();
//					String[] pro = { MediaStore.Images.Media.DATA };
//					Cursor cursor = cr.query(uri, pro, null, null, null);
//					if (cursor.moveToFirst()) {
//						int column_index = cursor
//								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//						String path = cursor.getString(column_index);
//						// upload pic
//						//Loger.i("youlin", "file-name->" + imgArray[i]	+ "   path:" + cursor.getString(column_index));
//						File file = new File(path);
//						try {
//							mRequestParams.put(imgArray[i], file);
//							// Loger.i("youlin", "post file..."+file.getPath());
//						} catch (FileNotFoundException e) {
//							// TODO Auto-generated catch block
//							Loger.i("youlin",
//									"post file error..." + e.getMessage());
//							e.printStackTrace();
//						}
//						filePathList.add(path);
//
//					}
				}
				msg.what = 101;
				handler.sendMessage(msg);
			}
		}).start();
	}
		public void backFlag(){
		mRequestParams.put("topic_time", System.currentTimeMillis()); 
		mRequestParams.put("tag","setnotice");
		mRequestParams.put("apitype",IHttpRequestUtils.APITYPE[4]);
    	AsyncHttpClient httpClient = new AsyncHttpClient();
    	httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, mRequestParams,
    			new JsonHttpResponseHandler(){
    		@Override
    		public void onSuccess(int statusCode, Header[] headers,
    				JSONObject response) {
    			// TODO Auto-generated method stub
    			String flag = "no";
    			try {
					flag = response.getString("flag");
					if("full".equals(flag)){
						mRequestParams = null;
						Toast.makeText(mContext, response.getString("yl_msg"), Toast.LENGTH_SHORT).show();
					}
					if("ok".equals(flag)){
						Loger.i("youlin", "ok topic_id->"+response.getString("topic_id"));
						Intent intent=new Intent("youlin.friend.action");
						intent.putExtra("selfsendwhat", 3);
						mContext.sendBroadcast(intent);
						changeutils = new StatusChangeutils();
						changeutils.setstatuschange("GONGGAO",1);
					}else{
						Loger.i("youlin", "no topic_id->"+response.getString("topic_id"));
						mRequestParams = null;
					}
					deleteAllRes();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					deleteAllRes();
					Loger.i("youlin", "error->"+e.getMessage());
					e.printStackTrace();
				}
    			super.onSuccess(statusCode, headers, response);
    		}
    		@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
    			deleteAllRes();
    			new ErrorServer(mContext, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
    	});
	}
	
	private void deleteAllRes(){
		((Activity)mContext).finish();
		new ClearSelectImg();
		mRequestParams = null;
		for (int i = 0; i < filePathList.size(); i++) {
			//File file = new File(filePathList.get(i));
			try {
				File photoFile = new File(Environment.getExternalStorageDirectory()+"/Photo_LJ/");
				DataCleanManager.deleteFilesByDirectory(photoFile);
				//String[] path = { filePathList.get(i) };
				//mContext.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.DATA+ " LIKE ?", path);
				//file.delete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Loger.i("youlin","error->newtop->" + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	@SuppressLint("HandlerLeak")
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 101:
				backFlag();
				break;
			default:
				break;
			}

		};
	};
}
