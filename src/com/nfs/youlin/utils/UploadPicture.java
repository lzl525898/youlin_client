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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class UploadPicture {
	private Context mContext;
	Bitmap myBitmap=null;
	Bitmap bitmap=null;
	RequestParams mRequestParams;
	ProgressDialog pd;
	Bimp bimp=new Bimp();
	int type;
	private String[] imgArray = {"img_0", "img_1", "img_2",
			                     "img_3", "img_4", "img_5",
			                     "img_6", "img_7", "img_8"};
	private List<String> filePathList = new ArrayList<String>();
	public UploadPicture(final Context context,RequestParams sRequestParams,int type) { // type=5  代表以物换物
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mRequestParams=sRequestParams;
		this.type=type;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg=new Message();
				int length = Bimp.tempSelectBitmap.size();
				if(0==length){
					mRequestParams.put("send_status", 0);
				}else{
					mRequestParams.put("send_status", 1);
				}
				for (int i = 0; i < length; i++) {
					// TODO Auto-generated method stub
					String newPath = null;
					if (Bimp.tempSelectBitmap.get(i).getImagePath() == null) {
						newPath = Bimp.tempSelectBitmap.get(i).getThumbnailPath();
					} else {
						newPath = Bimp.tempSelectBitmap.get(i).getImagePath();
					}
					XuanzhuanBitmap xuanzhuanBitmap = new XuanzhuanBitmap();
					int degree = xuanzhuanBitmap.readPictureDegree(newPath);
					try {
						BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(newPath)));
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
						//listBitmap.add(myBitmap);
						e1.printStackTrace();
					}
					Loger.i("youlin", "3333333333333333333333333--->");
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
					Loger.i("youlin", "4444444444444444444444444--->");
					myBitmap.recycle();
					myBitmap=null;
					bitmap.recycle();
					bitmap=null;
					System.gc();
					
//					ContentResolver cr = context.getContentResolver();
//					String[] pro = { MediaStore.Images.Media.DATA };
//					Cursor cursor = cr.query(uri, pro, null, null, null);
//					if (cursor.moveToFirst()) {
//						int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//						String path = cursor.getString(column_index);
//						File file = new File(path);
//						try {
//							mRequestParams.put(imgArray[i], file);
//							// Loger.i("youlin", "post file..."+file.getPath());
//						} catch (FileNotFoundException e) {
//							// TODO Auto-generated catch block
//							Loger.i("youlin","post file error..." + e.getMessage());
//							e.printStackTrace();
//						}
//						filePathList.add(path);
//
//					}
				}
				msg.what=101;
				handler.sendMessage(msg);
			}
		}).start();
		// pd.cancel();
	}
	
	private void deleteAllRes(){
		((Activity)mContext).finish();
		new ClearSelectImg();
		mRequestParams = null;
		NewTopic.nforumId = -1;
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
	public void backFlag(){
		mRequestParams.put("topic_time", System.currentTimeMillis());
		Loger.i("NEW", "System.currentTimeMillis()->"+System.currentTimeMillis());
		if(type==5){
			mRequestParams.put("tag", "addoldreplace");
		}else{
			mRequestParams.put("tag", "addtopic");
		}
		mRequestParams.put("apitype", IHttpRequestUtils.APITYPE[5]);
		
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, mRequestParams,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						Loger.i("LYM", "1111---->"+response.toString());
						String flag = "no";
						try {
							flag = response.getString("flag");
							if ("full".equals(flag)){
								mRequestParams = null;
								NewTopic.nforumId = -1;
								Toast.makeText(mContext, response.getString("yl_msg"), Toast.LENGTH_SHORT).show();
							}
							if ("ok".equals(flag)) {
								Loger.i("youlin","ok topic_id->"+ response.getString("topic_id"));
								Intent intent = new Intent("youlin.friend.action");
								if(type==5){
									intent.putExtra("selfsendwhat", 5);
									Intent jsIntent = new Intent("youlin.square.send.action");
									mContext.sendBroadcast(jsIntent);
								}else{
									intent.putExtra("selfsendwhat", 1);
								}
								mContext.sendBroadcast(intent);
							} else {
								Loger.i("youlin","no topic_id->"+ response.getString("topic_id"));
								mRequestParams = null;
								NewTopic.nforumId = -1;
							}
							deleteAllRes();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							deleteAllRes();
							Loger.i("youlin", "error->" + e.getMessage());
							e.printStackTrace();
						}
						super.onSuccess(statusCode, headers, response);
					}
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						Loger.i("youlin", "UploadPicture999999999999999999999999--->");
					}
					@Override
					public void onSuccess(int statusCode, Header[] headers, String responseString) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, responseString);
						Loger.i("youlin", "UploadPicture111111111111111111111111--->");
					}
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						deleteAllRes();
						new ErrorServer(mContext, responseString);
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable,
							JSONArray errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable, errorResponse);
						Loger.i("youlin", "UploadPicture2222222222222222222222--->");
					}
					@Override
					public void onFailure(int statusCode, Header[] headers, Throwable throwable,
							JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable, errorResponse);
						Loger.i("youlin", "UploadPicture33333333333333333333333--->");
					}
					
		});
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
