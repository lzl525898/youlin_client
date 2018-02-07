package com.nfs.youlin.activity.neighbor;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.easemob.chatuidemo.activity.AlertDialogforclear;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.personal.moreaddrdetail;
import com.nfs.youlin.adapter.FriendCircleImageAdapter;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.SampleScrollListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.TimeToStr;
import com.nfs.youlin.utils.error_logtext;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RepairDetailActivity extends Activity implements OnClickListener{
	private ActionBar actionBar;
	private ScrollView repair_detail_scroll;
	private LinearLayout repair_circle_layout;
	private RelativeLayout repair_user_msg;
	private TextView repair_username;
	private RelativeLayout repair_title_msg;
	private TextView repair_title;
	private TextView repair_time;
	private TextView repair_detail;
	private GridView repair_picture;
	private LinearLayout repair_letter;
	private LinearLayout repair_schedule;
	private ImageView repair_head;
	private LinearLayout repair_user_phone;
	private View scheduleview1;
	private View scheduleview2;
	private ImageButton goingbutton;
	private ImageButton endbutton;
	private LinearLayout schedulelayout;
	private TextView addresstext;
	private int position;
	private boolean cRequestSet;
	private String flag = "none";
	public static final int CHATTYPE_SINGLE = 1;
	public static final int CHATTYPE_GROUP = 2;
	public static final int CHATTYPE_CHATROOM = 3;
	private int schedule;
	private StatusChangeutils statusutils;
	private int REQUEST_CODE_REFRESH_SCHEDULE_1 = 501;
	private int REQUEST_CODE_REFRESH_SCHEDULE_2 = 502;
	ImageLoader imageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repair_detail_item);
		imageLoader = ImageLoader.getInstance();
		setTitle("详情");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		repair_detail_scroll = (ScrollView)findViewById(R.id.repair_detail_scall);
		repair_circle_layout = (LinearLayout)findViewById(R.id.repair_item_lay);
		repair_user_msg = (RelativeLayout)findViewById(R.id.repairuser_msg);
		repair_username = (TextView)findViewById(R.id.topic_title);
		repair_title_msg = (RelativeLayout)findViewById(R.id.repairtitle_msg);
		repair_title = (TextView)findViewById(R.id.topic_name);
		repair_time = (TextView)findViewById(R.id.topic_time);
		repair_detail = (TextView)findViewById(R.id.content_repair);
		repair_picture = (GridView)findViewById(R.id.gridView);
		repair_letter = (LinearLayout)findViewById(R.id.letter_ll);
		repair_schedule = (LinearLayout)findViewById(R.id.jindu_ll);
		repair_head = (ImageView)findViewById(R.id.repair_head_img);
		repair_user_phone = (LinearLayout)findViewById(R.id.telephone_ll);
		scheduleview1 = (View)findViewById(R.id.viewspace1);
		scheduleview2 = (View)findViewById(R.id.viewspace2);
		goingbutton = (ImageButton)findViewById(R.id.gonging_status);
		endbutton = (ImageButton)findViewById(R.id.end_status);
		schedulelayout = (LinearLayout)findViewById(R.id.schedulelayout);
		addresstext = (TextView)findViewById(R.id.address_text);
		Intent intent = getIntent();
		position = intent.getIntExtra("position", 0);
		
		addresstext.setText(((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getuser_address());
		
		String headUrl = String.valueOf(((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getSender_portrait());
//		Picasso.with(this) 
//				.load(headUrl) 
//				.placeholder(R.drawable.account) 
//				.error(R.drawable.account)
//				.fit()
//				.tag(this)
//				.into(repair_head);
		imageLoader.displayImage(headUrl, repair_head, App.options_account);
		String titleStr=((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getTopic_title();
		repair_title.setText(titleStr);
		repair_username.setText(((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getDisplay_name());
		String time=TimeToStr.getTimeToStr(((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getTopic_time());
		repair_time.setText(time);
		String contentStr=((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getTopic_content();
		repair_detail.setText(contentStr);
		repair_picture.setSelector(new ColorDrawable(Color.TRANSPARENT));
		final List<String> pictureList = new ArrayList<String>();
		JSONArray imgJsonArray = null;
		try {
			if (((ForumTopic) PropertyRepairList.forumtopicLists.get(position)).getMeadia_files_json() != null) {
				imgJsonArray = new JSONArray(((ForumTopic) PropertyRepairList.forumtopicLists.get(position)).getMeadia_files_json());
				for (int i = 0; i < imgJsonArray.length(); i++) {
					JSONObject jsonObject = new JSONObject(imgJsonArray.getString(i));
					try {
						pictureList.add(jsonObject.getString("resPath"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	repair_picture.setAdapter(new FriendCircleImageAdapter(RepairDetailActivity.this,pictureList));
	repair_picture.setOnScrollListener(new SampleScrollListener(RepairDetailActivity.this));
	repair_picture.setOnItemClickListener(new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				final int arg2, long arg3) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(RepairDetailActivity.this,GalleryPictureActivity.class);
			intent.putExtra("ID", arg2);
			intent.putExtra("url",((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getMeadia_files_json());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			RepairDetailActivity.this.startActivity(intent);
		}
	});
	schedule = ((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getLike_num(); // 进度
	Loger.d("test5", "schedule="+schedule);
	if(schedule == 1){
	}else if(schedule == 2){
		scheduleview1.setBackgroundColor(0xffff7702);
		goingbutton.setBackgroundResource(R.drawable.dengdaishenhe);
	}else if(schedule == 3){
		scheduleview1.setBackgroundColor(0xffff7702);
		goingbutton.setBackgroundResource(R.drawable.dengdaishenhe);
		scheduleview2.setBackgroundColor(0xffff7702);
		endbutton.setBackgroundResource(R.drawable.dengdaishenhe);
	}
	repair_letter.setOnClickListener(this);
	repair_schedule.setOnClickListener(this);
	repair_user_phone.setOnClickListener(this);
	goingbutton.setOnClickListener(this);
	endbutton.setOnClickListener(this);

	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.letter_ll:
			AccountDaoDBImpl account = new AccountDaoDBImpl(RepairDetailActivity.this);
			Intent intent = new Intent(RepairDetailActivity.this, com.easemob.chatuidemo.activity.ChatActivity.class);
	        intent.putExtra("userId",String.valueOf(((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getSender_id()));
	        intent.putExtra("chatType", CHATTYPE_SINGLE);
	        intent.putExtra("usernick",((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getDisplay_name());
	        String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
            intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
            intent.putExtra("neighborurl", ((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getSender_portrait());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            RepairDetailActivity.this.startActivity(intent);
			break;
		case R.id.jindu_ll:
			schedulelayout.setVisibility(View.VISIBLE);
			Handler mHandler = new Handler();  
			mHandler.post(new Runnable() {  
			    @Override  
			    public void run() {  
			    	repair_detail_scroll.fullScroll(ScrollView.FOCUS_DOWN);  
			    }  
			});
			break;
		case R.id.telephone_ll:
			startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getPhone_num())).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			break;
		case R.id.gonging_status:
			
			if(schedule == 1){
				String st5 = "是否更新当前进度";
				startActivityForResult(new Intent(this, AlertDialogforclear.class).putExtra("titleIsCancel", true).putExtra("msg", st5)
						.putExtra("cancel", true), REQUEST_CODE_REFRESH_SCHEDULE_1);

			}
			break;
		case R.id.end_status:
			
			if(schedule == 2){
				String st5 = "是否更新当前进度";
				startActivityForResult(new Intent(this, AlertDialogforclear.class).putExtra("titleIsCancel", true).putExtra("msg", st5)
						.putExtra("cancel", true), REQUEST_CODE_REFRESH_SCHEDULE_2);
				
			}
			break;
		default:
			break;
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			statusutils = new StatusChangeutils();
			statusutils.setstatuschange("BAOXIULIST", 1);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void gethttpdata(long topic_id ,String process_data,int process_status,String http_addr){
		/***********************************************/
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
			
			params.put("user_id", App.sUserLoginId);
			params.put("community_id", App.sFamilyCommunityId);
			params.put("topic_id", topic_id);
			params.put("process_data", process_data);
			params.put("process_status", process_status);
			params.put("tag","setstatus");
			params.put("apitype",IHttpRequestUtils.APITYPE[4]);
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test3", "set currentaddress flag->" + flag);
					if(flag.equals("ok")){
						cRequestSet = true;
					}else{
						cRequestSet = false;
					}

				} catch (org.json.JSONException e) {
					e.printStackTrace();
					cRequestSet = false;
					Loger.i("test3","JSONObject->"+ e.getMessage());
				}
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
				org.json.JSONArray jsonContext = response;
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					cRequestSet = false;
					new ErrorServer(RepairDetailActivity.this, responseString.toString());
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Loger.d("test5", "RepairDetailActivity ACTION_DOWN keyCode="+keyCode);
		if(keyCode ==KeyEvent.KEYCODE_BACK){
			Loger.d("test5", "RepairDetailActivity ACTION_DOWN1");
			statusutils = new StatusChangeutils();
			statusutils.setstatuschange("BAOXIULIST", 1);
			finish();
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_REFRESH_SCHEDULE_1){
			if(resultCode == RESULT_OK){
				JSONObject object;
				//Toast.makeText(RepairDetailActivity.this, "确定", Toast.LENGTH_SHORT).show();
				try {
					object = new JSONObject(((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getObject_data());
					object.put("2",System.currentTimeMillis() );
					((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).setObject_data(object.toString());
					Loger.d("test4", "schedule data"+object.toString());
					gethttpdata(((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getTopic_id(), object.toString(), 2, IHttpRequestUtils.YOULIN);
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							// 网络请求 注册地址 参数{city,village,rooft,number} login_account
							// while(cRequestSet){;}
							Long currenttime = System.currentTimeMillis();
							while (!cRequestSet) {
								if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
									cRequestSet = true;
								}
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							super.onPostExecute(result);
							if (cRequestSet && flag != null && flag.equals("ok")) {
								cRequestSet = false;
								flag = "";
								schedule = 2;
								endbutton.setClickable(true);
								scheduleview1.setBackgroundColor(0xffff7702);
								goingbutton.setBackgroundResource(R.drawable.dengdaishenhe);
								
							}
						}
					}.execute();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(requestCode == REQUEST_CODE_REFRESH_SCHEDULE_2){
			if(resultCode == RESULT_OK){
				JSONObject object;
				try {
					object = new JSONObject(((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getObject_data());
					object.put("3",System.currentTimeMillis() );
					gethttpdata(((ForumTopic)PropertyRepairList.forumtopicLists.get(position)).getTopic_id(), object.toString(), 3, IHttpRequestUtils.YOULIN);
					Loger.d("test4", "schedule data"+object.toString());
					new AsyncTask<Void, Void, Void>() {
						@Override
						protected Void doInBackground(Void... params) {
							// 网络请求 注册地址 参数{city,village,rooft,number} login_account
							// while(cRequestSet){;}
							Long currenttime = System.currentTimeMillis();
							while (!cRequestSet) {
								if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
									cRequestSet = true;
								}
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							super.onPostExecute(result);
							if (cRequestSet && flag != null && flag.equals("ok")) {
								cRequestSet = false;
								flag = "";
								schedule = 3;
								scheduleview2.setBackgroundColor(0xffff7702);
								endbutton.setBackgroundResource(R.drawable.dengdaishenhe);
								scheduleview1.setBackgroundColor(0xffff7702);
								goingbutton.setBackgroundResource(R.drawable.dengdaishenhe);
							}
						}
					}.execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
