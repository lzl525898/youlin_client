package com.nfs.youlin.activity.personal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.NeighborsHttpRequest;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.DataCleanManager;
import com.nfs.youlin.utils.FileUtils;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.MobclickAgent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
@SuppressLint("HandlerLeak")
public class SelectPicPopupWindowActivity extends Activity implements OnClickListener{
	private Button btn_take_photo, btn_pick_photo, btn_cancel;
    private LinearLayout layout;
    private Uri uri;
    //private String[] path=null; 
    private Bitmap photo;
    /* 头像文件 */
	private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
	/* 请求识别码 */
	private static final int CODE_GALLERY_REQUEST = 0xa0;
	private static final int CODE_CAMERA_REQUEST = 0xa1;
	private static final int CODE_RESULT_REQUEST = 0xa2;
	
	// 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
	private static int output_X = 480;
	private static int output_Y = 480;

	private Drawable drawable;
	private NeighborsHttpRequest httpRequest;
	private final int SUCCESS_CODE     = 100;
	private final int FAILED_CODE      = 101;
	private final int REFUSE_CODE      = 103;
	private final int SUCCESS_GET_INFO = 104;
	private final int FAILED_GET_INFO  = 105;
	private final int REFUSE_GET_INFO  = 106;
	File photoFile = new File(Environment.getExternalStorageDirectory()+"/Photo_LJ/");
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCESS_CODE:
				RequestParams params = new RequestParams();
				params.put("user_phone_number", App.sUserPhone);
				params.put("user_id", App.sUserLoginId);
				params.put("tag", "getinfo");
				params.put("apitype", IHttpRequestUtils.APITYPE[0]);
				httpRequest.getUserInfo(params, handler);		
				Loger.i("TEST", "ok...................");
				DataCleanManager.deleteFilesByDirectory(photoFile);
				//getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + " LIKE ?", path);
				break;
			case FAILED_CODE:
				Loger.i("TEST", "no...................");
				Toast.makeText(SelectPicPopupWindowActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				DataCleanManager.deleteFilesByDirectory(photoFile);
				//getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + " LIKE ?", path);
				break;
			case REFUSE_CODE:
				Loger.i("TEST", "error................");
				Toast.makeText(SelectPicPopupWindowActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				DataCleanManager.deleteFilesByDirectory(photoFile);
				//getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + " LIKE ?", path);
				break;
			case SUCCESS_GET_INFO:
				String topicPath = httpRequest.acountBundleList.get(0).getString("user_portrait");
				AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(SelectPicPopupWindowActivity.this);
				Account account = null;
				account = daoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
				if(account!=null){
					account.setUser_portrait(topicPath);
				}
				daoDBImpl.modifyObject(account);
				Loger.i("TEST", "topic_path->"+topicPath);
				Loger.i("TEST", "get ok...................");
				DataCleanManager.deleteFilesByDirectory(photoFile);
				//getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + " LIKE ?", path);
				finish();
				break;
			case FAILED_GET_INFO:
				Loger.i("TEST", "get no...................");
				Toast.makeText(SelectPicPopupWindowActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				DataCleanManager.deleteFilesByDirectory(photoFile);
				//getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + " LIKE ?", path);
				break;
			case REFUSE_GET_INFO:
				Loger.i("TEST", "get error................");
				Toast.makeText(SelectPicPopupWindowActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
				DataCleanManager.deleteFilesByDirectory(photoFile);
				//getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + " LIKE ?", path);
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_pic_popup_window);
		 	btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);  
	        btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);  
	        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);  
	        layout=(LinearLayout)findViewById(R.id.pop_layout);  
	        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity  
	        layout.setOnClickListener(new OnClickListener() {  
	            public void onClick(View v) {  
	                // TODO Auto-generated method stub    
	            }  
	        });  
	        //添加按钮监听  
	        btn_cancel.setOnClickListener(this);  
	        btn_pick_photo.setOnClickListener(this);  
	        btn_take_photo.setOnClickListener(this); 
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		return super.onTouchEvent(event);
	}
	@Override
	public void onClick(View v) {  
        switch (v.getId()) {  
        case R.id.btn_take_photo:
        	Loger.i("youlin","R.id.btn_take_photo:");
        	choseHeadImageFromCameraCapture();
            break;  
        case R.id.btn_pick_photo:
        	choseHeadImageFromGallery();
            break;  
        case R.id.btn_cancel:
        	finish();
            break;  
        default:  
            break;  
        }  
    } 

	// 启动手机相机拍摄照片作为头像
	private void choseHeadImageFromCameraCapture() {
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
		    startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
		    
	}
	// 从本地相册选取图片作为头像
	private void choseHeadImageFromGallery() {
		Intent intentFromGallery;
		if(Build.VERSION.SDK_INT<19){
			intentFromGallery=new Intent(Intent.ACTION_GET_CONTENT);
			intentFromGallery.setType("image/*");
		}else{
			intentFromGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		// 设置文件类型
		//intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
		startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		// 用户没有进行有效的设置操作，返回
//		if (resultCode == RESULT_CANCELED) {
//			Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
//			return;
//		}
		if(resultCode==RESULT_OK){
		switch (requestCode) {
		case CODE_GALLERY_REQUEST:
			if(intent!=null){
				uri=intent.getData();
				cropRawPhoto(uri);
			}
			break;

		case CODE_CAMERA_REQUEST:
			if(hasSdcard()){
				File tempFile = new File(Environment.getExternalStorageDirectory(),IMAGE_FILE_NAME);
				uri=Uri.fromFile(tempFile);
				cropRawPhoto(uri);
			} else {
				Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG)
						.show();
			}

			break;

		case CODE_RESULT_REQUEST:
			Loger.i("youlin","CODE_RESULT_REQUEST--->"+intent);
			if (intent != null) {
			setImageToHeadView(intent);
			}
			break;
			}
		}
		//super.onActivityResult(requestCode, resultCode, intent);
		
	}
	
	/**
	 * 裁剪原始的图片
	 */
	public void cropRawPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");

		// 设置裁剪
		intent.putExtra("crop", true);

		// aspectX , aspectY :宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		
		// outputX , outputY : 裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("scale",true);
		intent.putExtra("scaleUpIfNeeded",true);
//		intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
		
//		intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CODE_RESULT_REQUEST);
	}
	/**
	 * 提取保存裁剪之后的图片数据，并设置头像部分的View
	 */
	private void setImageToHeadView(Intent data) {
		
		Bundle extras = data.getExtras();
		if (extras != null) {
			if(photo!=null){
				photo.recycle();
			}
			photo = extras.getParcelable("data");
			//drawable=new BitmapDrawable(photo);
			//Bitmap bitmap=toRoundBitmap(photo);
			if(NetworkService.networkBool){
				saveMyBitmap(photo,"account");
			}else{
				finish();
				Toast.makeText(SelectPicPopupWindowActivity.this, "网络有问题", Toast.LENGTH_SHORT).show();
			}
			Loger.i("youlin","HeadView---------");
		}
		
		
	}
	  public Bitmap toRoundBitmap(Bitmap bitmap) {  
	        int width = bitmap.getWidth();  
	        int height = bitmap.getHeight();  
	        float roundPx;  
	        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;  
	        if (width <= height) {  
	            roundPx = width / 2;  
	            left = 0;  
	            top = 0;  
	            right = width;  
	            bottom = width;  
	            height = width;  
	            dst_left = 0;  
	            dst_top = 0;  
	            dst_right = width;  
	            dst_bottom = width;  
	        } else {  
	            roundPx = height / 2;  
	            float clip = (width - height) / 2;  
	            left = clip;  
	            right = width - clip;  
	            top = 0;  
	            bottom = height;  
	            width = height;  
	            dst_left = 0;  
	            dst_top = 0;  
	            dst_right = height;  
	            dst_bottom = height;  
	        }  
	  
	        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_4444);  
	        Canvas canvas = new Canvas(output);  
	  
	        final int color = 0xff424242;  
	        final Paint paint = new Paint();  
	        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);  
	        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);  
	        final RectF rectF = new RectF(dst);  
	  
	        paint.setAntiAlias(true);// 设置画笔无锯齿  
	  
	        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas  
	        paint.setColor(color);  
	  
	        // 以下有两种方法画圆,drawRounRect和drawCircle  
	        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。  
	        canvas.drawCircle(roundPx, roundPx, roundPx, paint);  
	  
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452  
	        canvas.drawBitmap(bitmap, src, dst, paint); //以Mode.SRC_IN模式合并bitmap和已经draw了的Circle  
	          
	        return output;  
	    }  
//	private void setImageToHeadView() {
//		if (uri != null) {
//			Loger.i("youlin","CODE_RESULT_REQUEST---------");
//			try {
//				Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//				Drawable drawable=new BitmapDrawable(bitmap);
//				MyInformationFragment.accountImageView.setBackgroundDrawable(drawable);
//				PersonalInformationActivity.headPortraitImg.setBackgroundDrawable(drawable);
//				saveMyBitmap(bitmap,"account");
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
//		finish();
//	}
	/**
	 * 检查设备是否存在SDCard的工具方法
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}

	}
	
	public void saveMyBitmap(Bitmap bitmap,String bitName){
		//coding
		//String newUri=null;
		String path=FileUtils.SDPATH+System.currentTimeMillis()+".jpg";
		File fileDir=new File(FileUtils.SDPATH+"");
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			fileDir.mkdir();
		}
		File file=new File(path);
		try {
			FileOutputStream outputStream=new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		RequestParams params = new RequestParams();
		if(file.exists()){
			Loger.i("TEST", "cunzai");
			try {
				params.put("user_phone_number", App.sUserPhone);
				params.put("user_id", App.sUserLoginId);
				params.put("user_portrait", file);
				params.put("tag", "upload");
				params.put("apitype", IHttpRequestUtils.APITYPE[0]);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpRequest = new NeighborsHttpRequest(SelectPicPopupWindowActivity.this);
			httpRequest.updateUserInfo(params,handler);
		}else{
			Loger.i("TEST", "image not exist!");
		}
		
//		Uri uri= Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, null, null));
//		ContentResolver cr = this.getContentResolver();
//       	String []pro={MediaStore.Images.Media.DATA};
//    	Cursor cursor = cr.query(uri,pro,null,null,null);
//    	if(cursor.moveToFirst()){
//    		int column_index =cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//    		path = new String[]{cursor.getString(column_index)};
//    		newUri=cursor.getString(column_index);
//    		Loger.i("youlin","newPath--->"+cursor.getString(column_index));
//
//    		//上传服务器
//    		RequestParams params = new RequestParams();
//    		File imageFile = new File(newUri);
//    		if(imageFile.exists()){
//    			Loger.i("TEST", "cunzai");
//    			try {
//    				params.put("user_phone_number", App.sUserPhone);
//    				params.put("user_id", App.sUserLoginId);
//					params.put("user_portrait", imageFile);
//					params.put("tag", "upload");
//					params.put("apitype", IHttpRequestUtils.APITYPE[0]);
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//    			httpRequest = new NeighborsHttpRequest(SelectPicPopupWindowActivity.this);
//				httpRequest.updateUserInfo(params,handler);
//    		}else{
//    			Loger.i("TEST", "image not exist!");
//    		}
//    	}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
	}
	
}
