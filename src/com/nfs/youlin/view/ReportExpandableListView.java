package com.nfs.youlin.view;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.nfs.youlin.R;
import com.nfs.youlin.R.color;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.activity.ReportDetailActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.titlebar.barter.BarterDedailActivity;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.TimeToStr;
import com.nfs.youlin.utils.error_logtext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class ReportExpandableListView extends ExpandableListView 
	implements OnChildClickListener,OnGroupClickListener{
	private final int SUCCESS = 100;
	private final int FAILED  = 101;
	private final int ERROR   = 102;
	private final int REQUEST = 103;
	private final int REQUEST_CODE_REPORT_DETAIL = 9527;
	private final String REPORT_CATEGORY_SENSITIVE = "敏感信息";
	private final String REPORT_CATEGORY_COPYRIGHT = "版权问题";
	private final String REPORT_CATEGORY_VIOLENCE  = "暴力色情";
	private final String REPORT_CATEGORY_DECEPTION = "诈骗和虚假信息";
	private final String REPORT_CATEGORY_HARASS    = "骚扰";
	private final String REPORT_CATEGORY_OTHERS    = "其他";
	
	private Map<String, List<String>> reportMap;
	private Map<String,String> myReportMap;
	private List<Map<String,String>> sensitiveMapList = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> copyrightMapList = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> violenceMapList = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> deceptionMapList = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> harassMapList = new ArrayList<Map<String,String>>();
	private List<Map<String,String>> othersMapList = new ArrayList<Map<String,String>>();
	private List<String> sensitiveList;
	private List<String> copyrightList;
	private List<String> violenceList;
	private List<String> deceptionList;
	private List<String> harassList;
	private List<String> othersList;
	
	public ExpandInfoAdapter expandInfoAdapter;
	private Context context;
	
	private LinearLayout detailLinearLayout;
	private String topic_type;
	private String media_files_json;
	private final String[] str_group_items_ = {
							REPORT_CATEGORY_SENSITIVE,
							REPORT_CATEGORY_COPYRIGHT,
							REPORT_CATEGORY_VIOLENCE,
							REPORT_CATEGORY_DECEPTION,
							REPORT_CATEGORY_HARASS,
							REPORT_CATEGORY_OTHERS
	};
	int[] reportNum;
//    private String[][] str_child_items_;
    private int length = 0;
    
    private boolean cRequesttype = false;
	private String cflag = "none";
	
    private Thread thread;
    YLProgressDialog ylProgressDialog;
    public ReportExpandableListView(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
    	this(context);  
	}
    
	public ReportExpandableListView(Context context) {
		super(context);
		this.context = context;
		initData();
		Loger.i("TEST", "reportTopicId==>"+ReportDetailActivity.reportTopicId);
		/* 隐藏默认箭头显示 */  
        this.setGroupIndicator(null);
        /* 隐藏垂直滚动条 */  
        this.setVerticalScrollBarEnabled(false);  
        /* 监听child，group点击事件 */  
        this.setOnChildClickListener(this);  
        this.setOnGroupClickListener(this);  
        
        setCacheColorHint(Color.TRANSPARENT);  
        setChildrenDrawnWithCacheEnabled(false);  
        setGroupIndicator(null); 
        setDivider(null);
//        setBackgroundColor(Color.parseColor("#ffffff"));
        /*隐藏选择的黄色高亮*/  
//        ColorDrawable drawable_tranparent_ = new ColorDrawable(Color.TRANSPARENT);  
//        setSelector(drawable_tranparent_); 
       
        /*设置adapter*/  
        expandInfoAdapter = new ExpandInfoAdapter();
        
        /*开始加载数据*/
        thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					if(ReportDetailActivity.reportTopicId>0){
						getResponseValue();
			        	break;
					}
		        }
			}
		});
        thread.start();
        ylProgressDialog=YLProgressDialog.createDialogwithcircle(context,"加载中...",0);
		ylProgressDialog.setCancelable(true);
	}
	
	
	
	private void getResponseValue(){
		Message message = new Message();
		message.what = REQUEST;
		handler.sendMessage(message);
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCESS:
				reportNum=new int[]{sensitiveList.size(),
									copyrightList.size(),
									violenceList.size(),
									deceptionList.size(),
									harassList.size(),
									othersList.size()};
				ReportDetailActivity.ylProgressDialog.dismiss();
				ReportDetailActivity.topLayout.setVisibility(View.VISIBLE);
				setAdapter(expandInfoAdapter);  
				//for (int i = 0; i < length; i++) {//默认展开
				//	expandGroup(i);
				//}
				//ReportDetailActivity.topLayout.setVisibility(View.VISIBLE);
				detailLinearLayout = (LinearLayout) ReportDetailActivity.sReportDetailActivity.findViewById(R.id.ll_into_detail_page);
				detailLinearLayout.setVisibility(View.VISIBLE);
				detailLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ylProgressDialog.show();
						intoDetailActivity(ReportDetailActivity.reportTopicId);
					}
				});
				break;
			case FAILED:
			case ERROR:
				ReportDetailActivity.ylProgressDialog.dismiss();
				Toast.makeText(ReportDetailActivity.sReportDetailActivity, "数据加载失败", Toast.LENGTH_SHORT).show();
				ReportDetailActivity.sReportDetailActivity.finish();
				break;
			case REQUEST:
				if(null!=thread){
					thread.interrupt();
					thread = null;
				}
				RequestParams params = new RequestParams();
				params.put("topic_id", ReportDetailActivity.reportTopicId);
				params.put("user_id", App.sUserLoginId);
				params.put("tag", "getreport");
				params.put("apitype", IHttpRequestUtils.APITYPE[5]);
				AsyncHttpClient client = new AsyncHttpClient();
//				Loger.i("TEST","topic_id==>"+ReportDetailActivity.reportTopicId);
//				Loger.i("TEST","user_id==>"+App.sUserLoginId);
				client.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, 
						new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
						// TODO Auto-generated method stub
						JSONObject reportJSON = null;
						length = response.length();
						for(int i=0;i<length;i++){
							try {
								reportJSON = response.getJSONObject(i);
								String getTitle = reportJSON.getString("title");
								if(REPORT_CATEGORY_SENSITIVE.equals(getTitle)){//敏感信息
									sensitiveList.add(reportJSON.toString());
								}else if(REPORT_CATEGORY_COPYRIGHT.equals(getTitle)){//版权问题
									copyrightList.add(reportJSON.toString());
								}else if(REPORT_CATEGORY_VIOLENCE.equals(getTitle)){//黄色暴力
									violenceList.add(reportJSON.toString());
								}else if(REPORT_CATEGORY_DECEPTION.equals(getTitle)){//欺骗和虚假信息
									deceptionList.add(reportJSON.toString());
								}else if(REPORT_CATEGORY_HARASS.equals(getTitle)){//骚扰
									harassList.add(reportJSON.toString());
								}else if(REPORT_CATEGORY_OTHERS.equals(getTitle)){//其他
									othersList.add(reportJSON.toString());
								}
//								String title     = reportJSON.getString("title");
//								String content   = reportJSON.getString("content");
//								String time      = reportJSON.getString("time");
//								long communityId = reportJSON.getLong("community_id");
//								long senderId    = reportJSON.getLong("sender_user_id");
//								long complainId  = reportJSON.getLong("complain_user_id");
//								long reportId    = reportJSON.getLong("report_id");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Loger.i("TEST", "SUCCESS==>"+e.getMessage());
							} 
						}
						for(int j=0;j<sensitiveList.size();j++){
							
							try {
								myReportMap=new HashMap<String,String>();
								myReportMap.put("name","举报者"+(j+1));
								JSONObject myReportJSON=new JSONObject(sensitiveList.get(j));
								myReportMap.put("time",myReportJSON.getString("time"));
								myReportMap.put("content",myReportJSON.getString("content"));
								sensitiveMapList.add(myReportMap);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						for(int k=0;k<copyrightList.size();k++){
							try {
								myReportMap=new HashMap<String,String>();
								myReportMap.put("name","举报者"+(k+1));
								JSONObject myReportJSON=new JSONObject(copyrightList.get(k));
								myReportMap.put("time",myReportJSON.getString("time"));
								myReportMap.put("content",myReportJSON.getString("content"));
								copyrightMapList.add(myReportMap);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						for(int l=0;l<violenceList.size();l++){
							try {
								myReportMap=new HashMap<String,String>();
								myReportMap.put("name","举报者"+(l+1));
								JSONObject myReportJSON=new JSONObject(violenceList.get(l));
								myReportMap.put("time",myReportJSON.getString("time"));
								myReportMap.put("content",myReportJSON.getString("content"));
								violenceMapList.add(myReportMap);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						for(int m=0;m<deceptionList.size();m++){
							try {
								myReportMap=new HashMap<String,String>();
								myReportMap.put("name","举报者"+(m+1));
								JSONObject myReportJSON=new JSONObject(deceptionList.get(m));
								myReportMap.put("time",myReportJSON.getString("time"));
								myReportMap.put("content",myReportJSON.getString("content"));
								deceptionMapList.add(myReportMap);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						for(int n=0;n<harassList.size();n++){
							try {
								myReportMap=new HashMap<String,String>();
								myReportMap.put("name","举报者"+(n+1));
								JSONObject myReportJSON=new JSONObject(harassList.get(n));
								myReportMap.put("time",myReportJSON.getString("time"));
								myReportMap.put("content",myReportJSON.getString("content"));
								harassMapList.add(myReportMap);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						for(int o=0;o<othersList.size();o++){
							try {
								myReportMap=new HashMap<String,String>();
								myReportMap.put("name","举报者"+(o+1));
								JSONObject myReportJSON=new JSONObject(othersList.get(o));
								myReportMap.put("time",myReportJSON.getString("time"));
								myReportMap.put("content",myReportJSON.getString("content"));
								othersMapList.add(myReportMap);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						Message msg = new Message();
						msg.what = SUCCESS;
						handler.sendMessage(msg);
						super.onSuccess(statusCode, headers, response);
					}
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						// TODO Auto-generated method stub
						try {
							String flag = response.getString("flag");
							Message msg = new Message();
							if("none".equals(flag)){
								msg.what = FAILED;
							}else{
								msg.what = ERROR;
							}
							handler.sendMessage(msg);
						} catch (JSONException e) {
							e.printStackTrace();
							Loger.i("TEST", "onSuccess==>"+e.getMessage());
						}
						super.onSuccess(statusCode, headers, response);
					}
					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString,
							Throwable throwable) {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.what = ERROR;
						handler.sendMessage(msg);
						super.onFailure(statusCode, headers, responseString, throwable);
					}
				});
			}
		};
	};
	
	private void intoDetailActivity(final long topicId){
		RequestParams reference = new RequestParams();
		reference.put("community_id",App.sFamilyCommunityId);
		reference.put("user_id",App.sUserLoginId);
		reference.put("count", 1);
		reference.put("topic_id",topicId);
		reference.put("type", 2);
		reference.put("tag","gettopic");
		reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+ IHttpRequestUtils.YOULIN,reference,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
						Loger.i("TEST", response.toString());
						getTopicDetailsInfos(response);
						cRequesttype = true;
						if (NewPushRecordAbsActivity.forumtopicLists.size() > 0) {
							cflag = "ok";
						}
						if(topic_type.equals("4")){
							Intent intent = new Intent(ReportDetailActivity.sReportDetailActivity,BarterDedailActivity.class);
							intent.putExtra("parent", 4);
							intent.putExtra("position", 0);
							intent.putExtra("report", REQUEST_CODE_REPORT_DETAIL);
							intent.putExtra("url", media_files_json);
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							ReportDetailActivity.sReportDetailActivity.startActivity(intent);
						}else{
							Intent intent = new Intent(ReportDetailActivity.sReportDetailActivity,CircleDetailActivity.class);
							intent.putExtra("parent", 4);
							intent.putExtra("position", 0);
							intent.putExtra("report", REQUEST_CODE_REPORT_DETAIL);
							intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							ReportDetailActivity.sReportDetailActivity.startActivity(intent);
						}
						ylProgressDialog.dismiss();
						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						// TODO Auto-generated method stub
						try {
							String flag = null;
							flag = response.getString("flag");
							if (flag.equals("no")) {
								cRequesttype = true;
								ylProgressDialog.dismiss();
								Loger.i("TEST", "topicId==>"+ReportDetailActivity.reportTopicId + "    flag==>"+flag);
								PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(
										ReportDetailActivity.sReportDetailActivity);
								daoDBImpl.deleteObject(topicId);
								Toast.makeText(ReportDetailActivity.sReportDetailActivity, "此贴已被删除", Toast.LENGTH_SHORT).show();
								ReportDetailActivity.sReportDetailActivity.finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString,
							Throwable throwable) {
						// TODO
						// Auto-generated
						// method
						// stub
						new ErrorServer(ReportDetailActivity.sReportDetailActivity, responseString);
						super.onFailure(statusCode, headers, responseString, throwable);
					}
				});
		/*
		new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				Long currenttime = System.currentTimeMillis();
				while(!cRequesttype){
					if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
						cRequesttype = true;
					}
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				if(cRequesttype == true && cflag.equals("ok")){
					if(topic_type.equals("4")){
						Intent intent = new Intent(ReportDetailActivity.sReportDetailActivity,BarterDedailActivity.class);
						intent.putExtra("parent", 4);
						intent.putExtra("position", 0);
						intent.putExtra("report", REQUEST_CODE_REPORT_DETAIL);
						intent.putExtra("url", media_files_json);
						ReportDetailActivity.sReportDetailActivity.startActivity(intent);
					}else{
						Intent intent = new Intent(ReportDetailActivity.sReportDetailActivity,CircleDetailActivity.class);
						intent.putExtra("parent", 4);
						intent.putExtra("position", 0);
						intent.putExtra("report", REQUEST_CODE_REPORT_DETAIL);
						ReportDetailActivity.sReportDetailActivity.startActivity(intent);
					}
				}
				cflag = "none";
				cRequesttype = false;
			};
		}.execute();
		*/
	}
	
	private void getTopicDetailsInfos(JSONArray response){
		int responseLen = response.length();
		if(responseLen>0){
		}
		NewPushRecordAbsActivity.forumtopicLists = new ArrayList<Object>();
		for (int i = 0; i < responseLen; i++) {
			try {
				JSONObject jsonObject = new JSONObject(response.getString(i));
				String cache_key = jsonObject.getString("cacheKey");
				String topic_id = jsonObject.getString("topicId");
				String forum_id = jsonObject.getString("forumId");
				String forum_name = jsonObject.getString("forumName");
				String circle_type = jsonObject.getString("circleType");
				String sender_id = jsonObject.getString("senderId");
				String sender_name = jsonObject.getString("senderName");
				String sender_lever = jsonObject.getString("senderLevel");
				String sender_portrait = jsonObject.getString("senderPortrait");
				String sender_family_id = jsonObject.getString("senderFamilyId");
				String sender_family_address = jsonObject.getString("senderFamilyAddr");
				String disply_name = jsonObject.getString("displayName");
				String topic_time = jsonObject.getString("topicTime");
				String topic_title = jsonObject.getString("topicTitle");
				String topic_content = jsonObject.getString("topicContent");
				String topic_category_type = jsonObject.getString("topicCategoryType");
				String comment_num = jsonObject.getString("commentNum");
				String like_num = jsonObject.getString("likeNum");
				String send_status = jsonObject.getString("sendStatus");
				String visiable_type = jsonObject.getString("visiableType");
				String hot_flag = jsonObject.getString("hotFlag");
				String view_num = jsonObject.getString("viewNum");
				String sender_community_id = jsonObject.getString("communityId");
				String collectStatus = jsonObject.getString("collectStatus");
				App.CurrentSysTime = Long.parseLong(jsonObject.getString("systemTime"));
				media_files_json = null;
				try {
					media_files_json = jsonObject.getJSONArray("mediaFile").toString();
				} catch (Exception e) {
					Loger.i("TEST", "(E)getTopic->"+e.getMessage());
					e.printStackTrace();
				}
				String comment_json = null;
				try {
					comment_json = jsonObject.getJSONArray("comments").toString();
				} catch (Exception e) {
					Loger.i("TEST", "(E)getTopicComm->"+e.getMessage());
					e.printStackTrace();
				}
				topic_type = jsonObject.getString("objectType");  //0.表示一般话题、1.表示活动
				if(topic_type==null || topic_type.isEmpty() || topic_type.length()<=0 || topic_type == "null"){
					topic_type = "0";
				}
				ForumTopic forumTopic = new ForumTopic(ReportDetailActivity.sReportDetailActivity);
				forumTopic.setFlag(false);
				forumTopic.setCache_key(Integer.parseInt(cache_key));
				forumTopic.setTopic_id(Long.parseLong(topic_id));
				Loger.d("test5", "topic position="+i+"topicid="+topic_id);
				forumTopic.setForum_id(Long.parseLong(forum_id));
				forumTopic.setForum_name(forum_name);
				forumTopic.setCircle_type(Integer.parseInt(circle_type));
				forumTopic.setSender_id(Long.parseLong(sender_id));
				forumTopic.setSender_name(sender_name);
				forumTopic.setSender_lever(sender_lever);
				forumTopic.setSender_portrait(sender_portrait);
				forumTopic.setSender_family_id(Long.parseLong(sender_family_id));
				forumTopic.setSender_family_address(sender_family_address);
				forumTopic.setDisplay_name(disply_name);
				forumTopic.setTopic_time(Long.parseLong(topic_time));
				forumTopic.setTopic_title(topic_title);
				forumTopic.setTopic_content(topic_content);
				forumTopic.setTopic_category_type(Integer.parseInt(topic_category_type));
				forumTopic.setComments_summary(comment_json);
				forumTopic.setSender_community_id(Long.parseLong(sender_community_id));
				forumTopic.setSender_nc_role(Integer.parseInt(collectStatus));
				if(comment_num==null || comment_num.isEmpty() || comment_num.length()<=0 || comment_num == "null"){
					comment_num = "0";
				}
				forumTopic.setComment_num(Integer.parseInt(comment_num));
				if(like_num==null || like_num.isEmpty() || like_num.length()<=0 || like_num == "null"){
					like_num = "0";
				}
				forumTopic.setLike_num(Integer.parseInt(like_num));
				forumTopic.setSend_status(Integer.parseInt(send_status));
				if(visiable_type==null || visiable_type.isEmpty() || visiable_type.length()<=0 || visiable_type == "null"){
					visiable_type = "0";
				}
				forumTopic.setVisiable_type(Integer.parseInt(visiable_type));
				if(hot_flag==null || hot_flag.isEmpty() || hot_flag.length()<=0 || hot_flag == "null"){
					hot_flag = "0";
				}
				forumTopic.setHot_flag(Integer.parseInt(hot_flag));
				if(view_num==null || view_num.isEmpty() || view_num.length()<=0 || view_num == "null"){
					view_num = "0";
				}
				forumTopic.setView_num(Integer.parseInt(view_num));
				forumTopic.setMeadia_files_json(media_files_json);
				forumTopic.setObject_type(Integer.parseInt(topic_type));
				String object_json = null;
				try {
					object_json = jsonObject.getJSONArray("objectData").toString();
				} catch (Exception e) {
//					Loger.i("TEST", "(E)getTopicComm->"+e.getMessage());
					e.printStackTrace();
				}
				forumTopic.setObject_data(object_json);
				NewPushRecordAbsActivity.forumtopicLists.add(forumTopic);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Loger.i("TEST", "ERROR->"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		Loger.i("TEST", "点击了第" + (groupPosition + 1) + "组");
		return false;
	}
	
	public class ExpandInfoAdapter extends BaseExpandableListAdapter{		
		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return str_group_items_.length;  
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			int length = 0;
			switch(groupPosition){
			case 0:
				length = reportMap.get(REPORT_CATEGORY_SENSITIVE).size();
				break;
			case 1:
				length = reportMap.get(REPORT_CATEGORY_COPYRIGHT).size();
				break;
			case 2:
				length = reportMap.get(REPORT_CATEGORY_VIOLENCE).size();
				break;
			case 3:
				length = reportMap.get(REPORT_CATEGORY_DECEPTION).size();
				break;
			case 4:
				length = reportMap.get(REPORT_CATEGORY_HARASS).size();
				break;
			case 5:
				length = reportMap.get(REPORT_CATEGORY_OTHERS).size();
				break;
			}
//			Loger.i("TEST", "getChildrenCount==>"+length);
			return length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return str_group_items_[groupPosition]; 
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
//			return str_child_items_[groupPosition][childPosition]; 
			return reportMap.get(REPORT_CATEGORY_SENSITIVE).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
            if(null == convertView){  
                convertView = LayoutInflater.from(context).inflate(R.layout.report_child_item, null);    
            }  
            /*判断是否是最后一项，最后一项设计特殊的背景*/  
//            if(isLastChild){  
//                convertView.setBackgroundResource(R.drawable.abs__ab_share_pack_holo_dark);  
//            } else {  
//                convertView.setBackgroundResource(R.drawable.abs__tab_selected_pressed_holo);  
//            }  
              
			TextView reportNameTv = (TextView)convertView.findViewById(R.id.name_tv);
			TextView timeTv = (TextView)convertView.findViewById(R.id.report_child_time_tv);
			TextView contentTv = (TextView)convertView.findViewById(R.id.report_child_content_tv);
            switch(groupPosition){
            case 0:
            	reportNameTv.setText(sensitiveMapList.get(childPosition).get("name"));
            	timeTv.setText(TimeToStr.getDateToString(Long.parseLong(sensitiveMapList.get(childPosition).get("time"))));
            	if((sensitiveMapList.get(childPosition).get("content")).length()>0){
            		contentTv.setText(sensitiveMapList.get(childPosition).get("content"));
            	}else{
            		contentTv.setText("无评论");
            	}
				break;
			case 1:
				reportNameTv.setText(copyrightMapList.get(childPosition).get("name"));
				timeTv.setText(TimeToStr.getDateToString(Long.parseLong(copyrightMapList.get(childPosition).get("time"))));
            	if((copyrightMapList.get(childPosition).get("content")).length()>0){
            		contentTv.setText(copyrightMapList.get(childPosition).get("content"));
            	}else{
            		contentTv.setText("无评论");
            	}
				break;
			case 2:
				reportNameTv.setText(violenceMapList.get(childPosition).get("name"));
				timeTv.setText(TimeToStr.getDateToString(Long.parseLong(violenceMapList.get(childPosition).get("time"))));
            	if((violenceMapList.get(childPosition).get("content")).length()>0){
            		contentTv.setText(violenceMapList.get(childPosition).get("content"));
            	}else{
            		contentTv.setText("无评论");
            	}
				break;
			case 3:
				reportNameTv.setText(deceptionMapList.get(childPosition).get("name"));
				timeTv.setText(TimeToStr.getDateToString(Long.parseLong(deceptionMapList.get(childPosition).get("time"))));
            	if((deceptionMapList.get(childPosition).get("content")).length()>0){
            		contentTv.setText(deceptionMapList.get(childPosition).get("content"));
            	}else{
            		contentTv.setText("无评论");
            	}
				break;
			case 4:
				reportNameTv.setText(harassMapList.get(childPosition).get("name"));
				timeTv.setText(TimeToStr.getDateToString(Long.parseLong(harassMapList.get(childPosition).get("time"))));
            	if((harassMapList.get(childPosition).get("content")).length()>0){
            		contentTv.setText(harassMapList.get(childPosition).get("content"));
            	}else{
            		contentTv.setText("无评论");
            	}
				break;
			case 5:
				reportNameTv.setText(othersMapList.get(childPosition).get("name"));
				timeTv.setText(TimeToStr.getDateToString(Long.parseLong(othersMapList.get(childPosition).get("time"))));
            	if((othersMapList.get(childPosition).get("content")).length()>0){
            		contentTv.setText(othersMapList.get(childPosition).get("content"));
            	}else{
            		contentTv.setText("无评论");
            	}
				break;
            }
//            txt_child.setText(str_child_items_[groupPosition][childPosition]);  
  
            return convertView;  
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView txt_group;  		
			convertView = LayoutInflater.from(context).inflate(R.layout.report_group_item, null);
			txt_group = (TextView)convertView.findViewById(R.id.id_group_txt);
			ImageView jiantouImg=(ImageView) convertView.findViewById(R.id.report_group_img);
			TextView reportNumTv=(TextView) convertView.findViewById(R.id.report_group_tv);
            /*判断是否group张开，来分别设置背景图*/  
            if(isExpanded){
                //convertView.setBackgroundResource(R.drawable.abs__ab_share_pack_holo_dark); 
            	jiantouImg.setBackgroundResource(R.drawable.icon_jiantou_shang);
            }else{  
                //convertView.setBackgroundResource(R.drawable.abs__ab_solid_light_holo);
            	jiantouImg.setBackgroundResource(R.drawable.icon_jiantou_xia);
            } 
            
            txt_group.setText(str_group_items_[groupPosition]);
            reportNumTv.setText(reportNum[groupPosition]+"人举报");
            return convertView;  
		}
		
		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	private void initData(){
		reportMap = new HashMap<String,List<String>>();
		sensitiveList = new ArrayList<String>();
		copyrightList = new ArrayList<String>();
		violenceList  = new ArrayList<String>();
		deceptionList = new ArrayList<String>();
		harassList    = new ArrayList<String>();
		othersList    = new ArrayList<String>();
		reportMap.put(REPORT_CATEGORY_SENSITIVE, sensitiveList);
		reportMap.put(REPORT_CATEGORY_COPYRIGHT, copyrightList);
		reportMap.put(REPORT_CATEGORY_VIOLENCE, violenceList);
		reportMap.put(REPORT_CATEGORY_DECEPTION, deceptionList);
		reportMap.put(REPORT_CATEGORY_HARASS, harassList);
		reportMap.put(REPORT_CATEGORY_OTHERS, othersList);
	}
}
