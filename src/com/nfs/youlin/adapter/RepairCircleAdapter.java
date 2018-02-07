package com.nfs.youlin.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailGalleryPictureActivity;
import com.nfs.youlin.activity.neighbor.GalleryPictureActivity;
import com.nfs.youlin.activity.neighbor.PropertyRepairActivity;
import com.nfs.youlin.activity.neighbor.PropertyRepairAddActivity;
import com.nfs.youlin.activity.neighbor.RepairScheduleActivity;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.ForumtopicDaoDBImpl;
import com.nfs.youlin.dao.NeighborChatDaoBImpl;
import com.nfs.youlin.dao.NeighborDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.BackgroundAlpha;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.SampleScrollListener;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.TimeToStr;
import com.nfs.youlin.utils.error_logtext;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

public class RepairCircleAdapter  extends BaseAdapter implements OnClickListener{
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
	PopupWindow popupWindow;
	private StatusChangeutils statusutils;
	String[] arr;
	private int menuSelectID;
	ImageLoader imageLoader;
	public RepairCircleAdapter(List<Object>list, Context context,int myPush) { //searchOrdisplay=0  from search
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
			convertView = LayoutInflater.from(context).inflate(R.layout.repair_circle_item, null);
			viewHolder=new ViewHolder();
			viewHolder.repair_circle_layout = (LinearLayout)convertView.findViewById(R.id.repair_item_lay);
			viewHolder.repair_user_msg = (RelativeLayout)convertView.findViewById(R.id.repairuser_msg);
			viewHolder.repair_username = (TextView)convertView.findViewById(R.id.topic_title);
			viewHolder.repair_title_msg = (RelativeLayout)convertView.findViewById(R.id.repairtitle_msg);
			viewHolder.repair_title = (TextView)convertView.findViewById(R.id.topic_name);
			viewHolder.repair_time = (TextView)convertView.findViewById(R.id.topic_time);
			viewHolder.repair_detail = (TextView)convertView.findViewById(R.id.content_repair);
			viewHolder.repair_picture = (GridView)convertView.findViewById(R.id.gridView);
			viewHolder.repair_letter = (LinearLayout)convertView.findViewById(R.id.letter_ll);
			viewHolder.repair_schedule = (LinearLayout)convertView.findViewById(R.id.jindu_ll);
			viewHolder.repair_head = (ImageView)convertView.findViewById(R.id.repair_head_img);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		viewHolder.repair_circle_layout.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				View contentView=((Activity) context).getLayoutInflater().inflate(R.layout.activity_share_popwindow_newpush, null);
				ListView listView=(ListView) contentView.findViewById(R.id.share_pop_repair_lv);
				arr=new String[]{"删除","删除全部"};
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(context, R.layout.activity_share_pop_textview,arr);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new listViewListen(position));
				popupWindow=new PopupWindow(context);
				popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
				popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.setFocusable(true);
				popupWindow.setOutsideTouchable(true);
				popupWindow.setContentView(contentView);
				popupWindow.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
				return false;
			}
		});
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
		viewHolder.repair_username.setText(((ForumTopic)data.get(position)).getDisplay_name());
		String time=TimeToStr.getTimeElapse(((ForumTopic)data.get(position)).getTopic_time(),App.CurrentSysTime);
		viewHolder.repair_time.setText(time);
		String contentStr=((ForumTopic)data.get(position)).getTopic_content();
		viewHolder.repair_detail.setText(contentStr);
		viewHolder.repair_picture.setSelector(new ColorDrawable(Color.TRANSPARENT));
		final List<String> pictureList = new ArrayList<String>();
		JSONArray imgJsonArray = null;
		try {
			if (((ForumTopic) data.get(position)).getMeadia_files_json() != null) {
				imgJsonArray = new JSONArray(((ForumTopic) data.get(position)).getMeadia_files_json());
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
	viewHolder.repair_picture.setAdapter(new FriendCircleImageAdapter(context,pictureList));
	viewHolder.repair_picture.setOnScrollListener(new SampleScrollListener(context));
	viewHolder.repair_picture.setOnItemClickListener(new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				final int arg2, long arg3) {
			// TODO Auto-generated method stub
			
			Intent intent=new Intent(context,GalleryPictureActivity.class);
			intent.putExtra("ID", arg2);
			intent.putExtra("url",((ForumTopic)data.get(position)).getMeadia_files_json());
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			context.startActivity(intent);
		}
	});
	viewHolder.repair_letter.setTag(position);
	viewHolder.repair_letter.setOnClickListener(this);
	viewHolder.repair_schedule.setTag(position);
	viewHolder.repair_schedule.setOnClickListener(this);
		return convertView;
	}
	private static class ViewHolder {
		public LinearLayout repair_circle_layout;
		public RelativeLayout repair_user_msg;
		public TextView repair_username;
		public RelativeLayout repair_title_msg;
		public TextView repair_title;
		public TextView repair_time;
		public TextView repair_detail;
		public GridView repair_picture;
		public LinearLayout repair_letter;
		public LinearLayout repair_schedule;
		public ImageView repair_head;
 	}
	private class listViewListen implements OnItemClickListener{
		listViewListen(int id){
			menuSelectID=id;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if(arr[position].equals("删除")){
				if(NetworkService.networkBool){
					RequestParams reference = new RequestParams();
					reference.put("community_id", App.sFamilyCommunityId);		
					reference.put("user_id", App.sUserLoginId);	
					reference.put("topic_type", 4);	
					reference.put("topic_id", ((ForumTopic)data.get(menuSelectID)).getTopic_id());	
					reference.put("tag", "deltopic");
					reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
					Loger.d("test5", "commonuser delete baoxiu"+"community_id="+App.sFamilyCommunityId+"user_id="+App.sUserLoginId+"topic_id"+((ForumTopic)data.get(menuSelectID)).getTopic_id());
					//reference.put("key_word",searchmessage);
					AsyncHttpClient client = new AsyncHttpClient();
					client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
							reference, new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONArray response) {
							// TODO Auto-generated method stub 18945051215
							super.onSuccess(statusCode, headers, response);
						}
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							// TODO Auto-generated method stub
							Message message = new Message();
							Loger.d("test4", "删除报修 flag="+response);
							try {
								String flag = response.getString("flag");
								if(flag.equals("ok")){
									statusutils = new StatusChangeutils();
									statusutils.setstatuschange("BAOXIU", 1);
									popupWindow.dismiss();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							super.onSuccess(statusCode, headers, response);
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							// TODO Auto-generated method stub
							new ErrorServer(context, responseString);
							super.onFailure(statusCode, headers, responseString, throwable);
						}
					});
				}
			}
			if(arr[position].equals("删除全部")){
				if(NetworkService.networkBool){
					RequestParams reference = new RequestParams();
					reference.put("community_id", App.sFamilyCommunityId);		
					reference.put("user_id", App.sUserLoginId);	
					reference.put("topic_type", 4);	
					reference.put("process_type", ((ForumTopic)data.get(menuSelectID)).getLike_num());	
					reference.put("tag", "deltopic");
					reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
					Loger.d("test5", "commonuser delete baoxiu"+"community_id="+App.sFamilyCommunityId+"user_id="+App.sUserLoginId+"baoxiu_status"+((ForumTopic)data.get(menuSelectID)).getLike_num());
					//reference.put("key_word",searchmessage);
					AsyncHttpClient client = new AsyncHttpClient();
					client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
							reference, new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONArray response) {
							// TODO Auto-generated method stub 18945051215
							super.onSuccess(statusCode, headers, response);
						}
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							// TODO Auto-generated method stub
							Message message = new Message();
							Loger.d("test4", "删除报修 flag="+response);
							try {
								String flag = response.getString("flag");
								if(flag.equals("ok")){
									statusutils = new StatusChangeutils();
									statusutils.setstatuschange("BAOXIU", 1);
									popupWindow.dismiss();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							super.onSuccess(statusCode, headers, response);
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							// TODO Auto-generated method stub
							new ErrorServer(context, responseString);
							super.onFailure(statusCode, headers, responseString, throwable);
						}
					});
				}
			}
		}
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.letter_ll:
			selectID = Integer.parseInt( arg0.getTag().toString());
			JSONArray obj;
			try {
				Loger.d("test5",((ForumTopic)data.get(selectID)).getForward_path());
				obj = new JSONArray(((ForumTopic)data.get(selectID)).getForward_path());
				if(obj.length()<1){ // 1 2 无用户
					Toast.makeText(context, "当前小区还没有物业账号", Toast.LENGTH_SHORT).show();
					break;
				}
				final PopupWindow popWindow=new PopupWindow(context);
				final View view=((Activity)context).getLayoutInflater().inflate(R.layout.activity_share_popwindow_repair, null);
				final ListView listView=(ListView)view.findViewById(R.id.share_pop_repair_lv);
				final List<String> arr = new ArrayList<String>();
				List<String> arrdisplay = new ArrayList<String>();
				for(int i = 0;i<obj.length();i++){
					arr.add(((JSONObject)obj.get(i)).getString("user_id"));
					arrdisplay.add("物业("+((JSONObject)obj.get(i)).getString("user_nick")+")");
				}
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice,arrdisplay);
				listView.setAdapter(adapter);
				listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method stub
						AccountDaoDBImpl account = new AccountDaoDBImpl(context);
						Intent intent = new Intent(context, com.easemob.chatuidemo.activity.ChatActivity.class);
				        intent.putExtra("userId",arr.get(position));
				        intent.putExtra("chatType", CHATTYPE_SINGLE);
				        String sub[] = ((ForumTopic)data.get(selectID)).getDisplay_name().split("@");
				        intent.putExtra("usernick","物业"+"@"+sub[1]);
				        String selfurl = ((Account) account.findAccountByLoginID(String.valueOf(App.sUserLoginId))).getUser_portrait();
			            intent.putExtra("selfurl", selfurl);  // hyy 有数据库后 从写
			            NeighborDaoDBImpl neighbordb = new NeighborDaoDBImpl(context);
			            List<Object> neighborlist= neighbordb.findPointFamilyandUserObject(Long.parseLong(arr.get(position)));
			            if(neighborlist.size()>0){
			            	intent.putExtra("neighborurl", ((Neighbor)neighborlist.get(0)).getUser_portrait());
			            }else{
			            	intent.putExtra("neighborurl", "");
//			            	intent.putExtra("neighborurl", ((ForumTopic)data.get(selectID)).getSender_portrait());
			            }
			            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			            context.startActivity(intent);
			            popWindow.dismiss();
					}
				});
				new BackgroundAlpha(0.5f, context);
				popWindow.setWidth(LayoutParams.WRAP_CONTENT);
				popWindow.setHeight(LayoutParams.WRAP_CONTENT);
				popWindow.setBackgroundDrawable(new BitmapDrawable());
				popWindow.setFocusable(true);
				popWindow.setOutsideTouchable(true);
				popWindow.setContentView(view);
				popWindow.showAtLocation(((Activity)context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
				popWindow.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						new BackgroundAlpha(1f,context );
					}
				});
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			break;
		case R.id.jindu_ll:
			selectID = Integer.parseInt( arg0.getTag().toString());
			Intent intent = new Intent(context, RepairScheduleActivity.class);
			intent.putExtra("schedule",((ForumTopic)data.get(selectID)).getLike_num());//进度
			intent.putExtra("scheduledata",((ForumTopic)data.get(selectID)).getObject_data());
			intent.putExtra("position", selectID);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			context.startActivity(intent);
			break;
		default:
			break;
		}
	}

}
