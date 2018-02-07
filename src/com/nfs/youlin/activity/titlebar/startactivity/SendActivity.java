package com.nfs.youlin.activity.titlebar.startactivity;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.MainActivity;
import com.nfs.youlin.activity.personal.PersonalInfoPasswordActivity;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopic;
import com.nfs.youlin.activity.titlebar.newtopic.NewTopicChange;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.AddPicture;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.BackgroundAlpha;
import com.nfs.youlin.utils.Bimp;
import com.nfs.youlin.utils.ClearSelectImg;
import com.nfs.youlin.utils.FileUtils;
import com.nfs.youlin.utils.ImageItem;
import com.nfs.youlin.utils.ImageThumbnail;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.PublicWay;
import com.nfs.youlin.utils.Res;
import com.nfs.youlin.utils.SetTimeractivity;
import com.nfs.youlin.utils.StrToTime;
import com.nfs.youlin.utils.UploadPicture;
import com.nfs.youlin.utils.UploadPicture2;
import com.nfs.youlin.utils.XuanzhuanBitmap;
import com.nfs.youlin.utils.gettime;
import com.nfs.youlin.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemClickListener;

public class SendActivity extends Activity implements OnClickListener{
	private ActionBar actionBar;
	public static TextView sendToTv;
	//public static TextView sendLableTv;
	public static TextView sendAddressTv;
	private LinearLayout sendToLayout;
	private LinearLayout startTimeLayout;
	private LinearLayout endTimeLayout;
	//private LinearLayout sendLableLayout;
	private RelativeLayout sendAddressLayout;
	private TextView startTimeTv;
	private TextView endTimeTv;
	private EditText sendTitleEt;
	private EditText sendContentEt;
	private ImageView sendToImg;
	private ImageView sendTitleImg;
	private ImageView startTimeImg;
	private ImageView endTimeImg;
	private ImageView sendAddressImg;
	//private ImageView sendLableImg;
	private String startTimeStr;
	private String startTimeStr1;
	private String endTimeStr;
	private String endTimeStr1;
	public static ProgressDialog pd;
	private static final int REQUEST_CODE_MAP = 4;
	private static final int REQUESTSTARTTIME = 110;
	private static final int REQUESTENDTIME = 111;
	private final int SUCCESS_CODE     = 100;
	private final int FAILED_CODE      = 101;
	private final int REFUSE_CODE      = 103;
	private long start_time;
	private long finish_time;
	private String starttime;
	private String finishtime;
	public  RequestParams sRequestParams;
	public static int nactionId = 0;
	private Account account;
	private String communityDetail;
	private String addrDetails;
	private AllFamilyDaoDBImpl curfamilyDaoDBImpl;
	private AllFamily currentFamily;
	Bitmap bitmap;
	//Bitmap bitMap;
	Bitmap newbitmap;
	Bimp bimp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_send_activity);
		actionBar=getActionBar();
		actionBar.setTitle("活动");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		bimp=new Bimp();
		sendToTv=(TextView)findViewById(R.id.send_to_tv);
		sendToTv.setText("本小区");
		startTimeTv=(TextView)findViewById(R.id.start_time_tv);
		endTimeTv=(TextView)findViewById(R.id.end_time_tv);
		//sendLableTv=(TextView)findViewById(R.id.send_lable_tv);
		sendAddressTv=(TextView)findViewById(R.id.send_address_tv);
		sendTitleEt=(EditText)findViewById(R.id.send_title_et);
		sendContentEt=(EditText)findViewById(R.id.send_content_et);
		sendToImg=(ImageView)findViewById(R.id.send_to_img);
		sendTitleImg=(ImageView)findViewById(R.id.send_title_img);
		startTimeImg=(ImageView)findViewById(R.id.start_time_img);
		endTimeImg=(ImageView)findViewById(R.id.end_time_img);
		sendAddressImg=(ImageView)findViewById(R.id.send_address_img);
		//sendLableImg=(ImageView)findViewById(R.id.send_lable_img);
		
		sendToLayout=(LinearLayout)findViewById(R.id.send_to_layout);
		startTimeLayout=(LinearLayout)findViewById(R.id.start_time_layout);
		endTimeLayout=(LinearLayout)findViewById(R.id.end_time_layout);
		//sendLableLayout=(LinearLayout)findViewById(R.id.send_lable_layout);
		sendAddressLayout=(RelativeLayout)findViewById(R.id.send_address_layout);
		
		sendToLayout.setOnClickListener(this);
		startTimeLayout.setOnClickListener(this);
		endTimeLayout.setOnClickListener(this);
		//sendLableLayout.setOnClickListener(this);
		sendAddressLayout.setOnClickListener(this);
		sendTitleEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
		account = getSenderInfo();
	    if(account==null){
	    	Loger.i("TEST", "未找到loginId");
	    	return;
	    }
	    addrDetails = getAddrDetail();
	    if(addrDetails==null){
	    	Loger.i("TEST", "地址信息错误");
	    	return;
	    }
	    communityDetail = getCommunityDetail();
	    curfamilyDaoDBImpl = new AllFamilyDaoDBImpl(this);
	    currentFamily = curfamilyDaoDBImpl.getCurrentAddrDetail(addrDetails);
		sendTvListen();
		new AddPicture(this);
	}
	private Account getSenderInfo(){
		Account account = null;
		if(App.sUserLoginId > 0){
			AccountDaoDBImpl daoDBImpl = new AccountDaoDBImpl(SendActivity.this);
			account = daoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
		}
		return account;
	}
	private String getAddrDetail(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String addrDetail = null;
		String city = sharedata.getString("city", null);
		String village = sharedata.getString("village", null);
		String detail = sharedata.getString("detail", null);
		if(city!=null && village!=null && detail!=null){
			addrDetail = city+village+detail;
		}
		return addrDetail;
	}
	private String getCommunityDetail(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		String commString = sharedata.getString("village", null);
		return commString;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.resumePush(getApplicationContext());
		MobclickAgent.onResume(this);
		new BackgroundAlpha(1f, SendActivity.this);
		AddPicture.adapter.notifyDataSetChanged();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExitDialog();
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(requestCode == AddPicture.TAKE_PICTURE){
			if(resultCode == RESULT_OK){
			if (Bimp.tempSelectBitmap.size() < 9 && resultCode == Activity.RESULT_OK) {
				String str=Environment.getExternalStorageDirectory()+File.separator+"circleImg.jpg";	
				try {
					bitmap=bimp.revitionImageSize(str);
					//Bimp.bitmapList.add(bitmap);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				XuanzhuanBitmap xuanzhuanBitmap=new XuanzhuanBitmap();
                int degree=xuanzhuanBitmap.readPictureDegree(str);
                newbitmap = xuanzhuanBitmap.rotaingImageView(degree, bitmap);
               //Bimp.bitmapList.add(newbitmap);
				String fileName = String.valueOf(System.currentTimeMillis());
				//Bitmap bm = (Bitmap) data.getExtras().get("data");
				FileUtils.saveBitmap(newbitmap,fileName);
                
				ImageItem takePhoto = new ImageItem();
				takePhoto.setBitmap(newbitmap);
				takePhoto.setImagePath(Environment.getExternalStorageDirectory()+ "/Photo_LJ/"+fileName+".jpg");
				takePhoto.setThumbnailPath(Environment.getExternalStorageDirectory()+ "/Photo_LJ/"+fileName+".jpg");
				Bimp.tempSelectBitmap.add(takePhoto);
				AddPicture.adapter.notifyDataSetChanged();
				}
			}
		}
		if(requestCode == REQUESTSTARTTIME){
			if(resultCode == RESULT_OK){
				int year = data.getIntExtra("year", 0);
				int month = data.getIntExtra("month", 0);
				int day = data.getIntExtra("day", 0);
				int hour = data.getIntExtra("hour", 0);
				int minute = data.getIntExtra("minute", 0);
				String y ;
				String m ;
				String d ;
				String h ;
				String min ;
				if(year<1000) y = "0000";
				else y = String.valueOf(year);
				if(month<10) m = "0"+month;
				else  m = String.valueOf(month);
				if(day<10) d = "0"+day;
				else  d = String.valueOf(day);
				if(hour<10) h = "0"+hour;
				else  h = String.valueOf(hour);
				if(minute<10) min = "0"+minute;
				else  min = String.valueOf(minute);
				starttime = y+"-"+m+"-"+d+"  "+h+":"+min;
				startTimeTv.setText(y+"年"+m+"月"+d+"日"+h+"时"+min+"分");
				//start_time = Long.parseLong(gettime.getTime(starttime));
				start_time = StrToTime.getTimeActivity(y+"年"+m+"月"+d+"日"+h+"时"+min+"分");
				Loger.d("timexx", start_time+"   "+starttime);
			}
		}
		if(requestCode == REQUESTENDTIME){
			if(resultCode == RESULT_OK){
				int year = data.getIntExtra("year", 0);
				int month = data.getIntExtra("month", 0);
				int day = data.getIntExtra("day", 0);
				int hour = data.getIntExtra("hour", 0);
				int minute = data.getIntExtra("minute", 0);
				String y ;
				String m ;
				String d ;
				String h ;
				String min ;
				if(year<1000) y = "0000";
				else y = String.valueOf(year);
				if(month<10) m = "0"+month;
				else  m = String.valueOf(month);
				if(day<10) d = "0"+day;
				else  d = String.valueOf(day);
				if(hour<10) h = "0"+hour;
				else  h = String.valueOf(hour);
				if(minute<10) min = "0"+minute;
				else  min = String.valueOf(minute);
				finishtime = y+"-"+m+"-"+d+"  "+h+":"+min;
				endTimeTv.setText(y+"年"+m+"月"+d+"日"+h+"时"+min+"分");
				//finish_time = Long.parseLong(gettime.getTime(finishtime));
				finish_time = StrToTime.getTimeActivity(y+"年"+m+"月"+d+"日"+h+"时"+min+"分");
				Loger.d("timexx", finish_time+"   "+finishtime);
			}
		}
			
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.send_to_layout:
			startActivity(new Intent(this,SendActivityRange.class).putExtra("village", communityDetail).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.start_time_layout:
			startTime();
			break;
		case R.id.end_time_layout:
			endTime();
			break;
		case R.id.send_address_layout:
			startActivityForResult(new Intent(this,SendActivityAddress.class),REQUEST_CODE_MAP);
			break;
//		case R.id.send_lable_layout:
//			startActivity(new Intent(this,SendActivityLabel.class));
//			break;
		default:
			break;
		}
	}

	public void startTime(){
		Intent intent = new Intent(this,SetTimeractivity.class);
		intent.putExtra("parent", "start");
		intent.putExtra("time", startTimeTv.getText().toString());
		startActivityForResult(intent, REQUESTSTARTTIME);
	}
	
	public void endTime(){
		Intent intent = new Intent(this,SetTimeractivity.class);
		intent.putExtra("parent", "end");
		intent.putExtra("time", endTimeTv.getText().toString());
		startActivityForResult(intent, REQUESTENDTIME);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Loger.i("youlin","onRestart()-----------");
	}
	
	private void sendFinish(){
		String sendToStr=sendToTv.getText().toString().trim();
		String sendTitleStr=sendTitleEt.getText().toString().trim();
		String sendContentStr=sendContentEt.getText().toString().trim();
		String startTimeStr=startTimeTv.getText().toString().trim();
		String endTimeStr=endTimeTv.getText().toString().trim();
		String sendAddressStr=sendAddressTv.getText().toString().trim();
		//String sendLableStr=sendLableTv.getText().toString().trim();
		String sendLableStr="无";
		Loger.i("youlin","sendToStr--->"+sendToStr);
		 pd = new ProgressDialog(SendActivity.this);
		 pd.setMessage("发布中...");
		if(!sendToStr.isEmpty()){
			if(!sendTitleStr.isEmpty()){
				if(!startTimeStr.isEmpty()){
					if(!endTimeStr.isEmpty()){
						if(finish_time>start_time){
							if(!sendAddressStr.isEmpty()){
								if(!sendLableStr.isEmpty()){
									if(sendContentStr.length()<=1000){
									if(NetworkService.networkBool){
											for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
												File file = new File(Bimp.tempSelectBitmap.get(i).getImagePath());
												if (!file.exists()) {
													Toast.makeText(SendActivity.this, "上传失败", 0).show();
													return;
												}
											}
											pd.show();
											sRequestParams = new RequestParams();
											sRequestParams.put("forum_id", SendActivity.nactionId); // 0表示本小区、1、周边、2.同城
											sRequestParams.put("forum_name", sendToStr);
											sRequestParams.put("topic_category_type", 2);  //2 表示普通话题
											sRequestParams.put("sender_id", App.sUserLoginId);
											sRequestParams.put("sender_name", account.getUser_name());
											sRequestParams.put("sender_lever", account.getUser_level()); 
											sRequestParams.put("sender_portrait", account.getUser_portrait());
											sRequestParams.put("sender_family_id", account.getUser_family_id());
											sRequestParams.put("sender_family_address", account.getUser_family_address()); 
											sRequestParams.put("sender_nc_role", 0);
											sRequestParams.put("display_name", communityDetail!=null?account.getUser_name()+"@"+communityDetail:null);
											sRequestParams.put("object_data_id", 1);  //0.表示一般话题、1.表示活动
											sRequestParams.put("circle_type", 1);  //1.表示一般话题
											sRequestParams.put("topic_title", "#活动#"+sendTitleStr);
											//"<font color='white'>我的积分：</font>"+"<font color='white'><b><big>"+credit+"</big></b></font>"
											sRequestParams.put("topic_content", "<font color='#323232'>"+"开始时间："+"</font>"+
																"<font color='#808080'>"+starttime+"<br>"+"</font>"+
																"<font color='#323232'>"+"结束时间："+"</font>"+
																"<font color='#808080'>"+finishtime+"<br>"+"</font>"+
																"<font color='#323232'>"+"地址："+sendAddressStr+"<br>"+"内容："+sendContentEt.getText().toString()+"</font>");
											Loger.d("test4", sendContentEt.getText().toString());
											sRequestParams.put("sender_city_id",currentFamily.getFamily_city_id());
											sRequestParams.put("sender_community_id",currentFamily.getFamily_community_id());
											sRequestParams.put("startTime",start_time);
											sRequestParams.put("endTime",finish_time);
											sRequestParams.put("location",sendAddressStr);
											sRequestParams.put("content",sendContentEt.getText().toString());
											sRequestParams.put("tag",sendLableStr);
											Loger.i("youlin", "555555555555555555555---->"+start_time+" "+finish_time);
											new UploadPicture2(SendActivity.this,sRequestParams);
											new Timer().schedule(new TimerTask() {
												@Override
												public void run() {
													// TODO Auto-generated method stub
													try {
														new ClearSelectImg();
														finish();
													} catch (Exception e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												}
											}, 20000);
											MobclickAgent.onEvent(SendActivity.this,"addActivity");
									}else{
										Toast.makeText(SendActivity.this,"请先开启网络", Toast.LENGTH_SHORT).show();
									}
									}else{
										Toast.makeText(SendActivity.this,"发送内容太长", Toast.LENGTH_SHORT).show();
									}
								}else{
									//sendLableImg.setVisibility(View.VISIBLE);
								}
							}else{
								sendAddressImg.setVisibility(View.VISIBLE);
							}
						}else{
							Toast.makeText(SendActivity.this, "结束时间小于或等于开始时间", Toast.LENGTH_SHORT).show();
						}
						
					}else{
						endTimeImg.setVisibility(View.VISIBLE);
					}
				}else{
					startTimeImg.setVisibility(View.VISIBLE);
				}
			}else{
				sendTitleImg.setVisibility(View.VISIBLE);
			}
		}else{
			sendToImg.setVisibility(View.VISIBLE);
		}
	}
	
	private void sendTvListen(){
		sendToTv.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				sendToImg.setVisibility(View.GONE);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		sendTitleEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				sendTitleImg.setVisibility(View.GONE);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		startTimeTv.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				startTimeImg.setVisibility(View.GONE);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		endTimeTv.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				endTimeImg.setVisibility(View.GONE);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		sendAddressTv.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				sendAddressImg.setVisibility(View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
		});
		
//		sendLableTv.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				// TODO Auto-generated method stub
//				sendLableImg.setVisibility(View.GONE);
//			}
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//			}
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//			}
//		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.new_topic, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			ExitDialog();
			break;
		case R.id.finish:
			sendFinish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void ExitDialog() {
		CustomDialog.Builder builder=new CustomDialog.Builder(SendActivity.this);
		//builder.setTitle("提示");
		builder.setMessage("确定要放弃此次编辑吗？");
		//builder.setCancelable(false);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				new ClearSelectImg();
				finish();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		builder.create().show();
	}
}
