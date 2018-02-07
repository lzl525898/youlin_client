package com.nfs.youlin.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.find.NewsDetail;
import com.nfs.youlin.activity.find.StoreCircleDetailActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailActivity;
import com.nfs.youlin.activity.neighbor.PropertyRepairActivity;
import com.nfs.youlin.activity.neighbor.PropertyRepairList;
import com.nfs.youlin.activity.titlebar.barter.BarterDedailCommentActivity;
import com.nfs.youlin.adapter.PushMessgeAdapter;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.PushRecordDaoDBImpl;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.entity.PushRecord;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.push.PushRecordDetailActivity;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.TimeClick;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.YLProgressDialog;
import com.nfs.youlin.view.YLSwipeDismissListViewTouchListener;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class NewPushRecordAbsActivity extends SwipeBackActivity {
	private RelativeLayout rlshowInfo;
	private ListView pushListView;
	private PushMessgeAdapter pushMsgAdapter;
	private List<String> pushListmsg;
	public static List<Object> sPushInfoObjList; 
	public static int sPushRecordObjSize = 0;
	public static NewPushRecordAbsActivity sNewPushRecordAbsContext;
//	private boolean setPushRequestSet = false;
//	private String setPushFlag= "no";
	private int menuSelectID;
	private View curSelectListViewItem;
	private ProgressBar progressBar;
	private Thread thread;
	public static List<Object> forumtopicLists;
	private boolean cRequesttype = false;
	private String flag = "none";
	private String communityName;
	PopupWindow popupWindow;
	String[] arr;
	YLProgressDialog ylProgressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pushinfo);
		ylProgressDialog=YLProgressDialog.createDialogwithcircle(this,"加载中...",0);
		ylProgressDialog.setCancelable(true);
		sNewPushRecordAbsContext = this;
		clearReportRes();//释放ReportDetail资源
		
		this.progressBar = (ProgressBar) findViewById(R.id.pb_push_circle);
		this.rlshowInfo = (RelativeLayout) findViewById(R.id.rl_show_info_push_circle);
		
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		
		WindowManager m = getWindowManager();    
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高    
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值    
        p.height = (int) (d.getHeight());   //高度设置为屏幕的1.0   
        p.width = (int) (d.getWidth() * 0.8);    //宽度设置为屏幕的0.8   
        p.x = width/5;
        p.y = 0;
        getWindow().setAttributes(p);  
        this.progressBar.setVisibility(View.VISIBLE);
        thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				initPushMessageInfo();
				handler.sendEmptyMessage(1);
			}
		});
        thread.start();
		
	}
	
	private void initPushMessageInfo() {  
		int userType = 0;
		if(App.sUserType<=0){
			AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
			userType = dbImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId)).getUser_type();
		}else{
			userType = App.sUserType;
		}
		PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(this);
		NewPushRecordAbsActivity.sPushInfoObjList = daoDBImpl.findAllObject(String.valueOf(App.sUserLoginId));
		Loger.i("TEST", "App.UserId==>"+App.sUserLoginId);
		Loger.i("TEST", "App.UserType==>"+userType);
		NewPushRecordAbsActivity.sPushRecordObjSize = NewPushRecordAbsActivity.sPushInfoObjList.size();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(NewPushRecordAbsActivity.sPushRecordObjSize<=0){
					rlshowInfo.setVisibility(View.VISIBLE);
				}else{
					rlshowInfo.setVisibility(View.GONE);
				}
			}
		});
		this.pushListmsg = new ArrayList<String>();
		this.pushListView = (ListView) findViewById(R.id.lv_push_new_msg);
		this.pushMsgAdapter = new PushMessgeAdapter(this, 
													R.layout.listview_push_msg_item, 
													getListData(NewPushRecordAbsActivity.sPushRecordObjSize), 
													NewPushRecordAbsActivity.sPushInfoObjList);
    } 
	
	private List<String> getListData(int size){
		for(int i=0;i<size;i++){
			this.pushListmsg.add("push");
		}
		return this.pushListmsg;
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			progressBar.setVisibility(View.GONE);
			if (pushListmsg.size() > 0) {
				pushListView.setVisibility(View.VISIBLE);
			} else {
				pushListView.setVisibility(View.GONE);
			}
			pushListView.setAdapter(pushMsgAdapter);
			YLSwipeDismissListViewTouchListener touchListener = new YLSwipeDismissListViewTouchListener(
					pushListView,
					new YLSwipeDismissListViewTouchListener.DismissCallbacks() {
						public boolean canDismiss(int position) {
							return true;
						}

						public void onDismiss(ListView listView,
								int[] reverseSortedPositions) {
							for (int position : reverseSortedPositions) {
								pushMsgAdapter.remove(pushMsgAdapter
										.getItem(position));
								if (NewPushRecordAbsActivity.sPushRecordObjSize > 0) {
									PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(
											NewPushRecordAbsActivity.this);
									PushRecord pushRecord = (PushRecord) NewPushRecordAbsActivity.sPushInfoObjList
											.get(position);
									if (pushRecord.getType() == 1) {
										int newRecordCount = Integer
												.parseInt(App.sNewPushRecordCount);
										if (newRecordCount > 0) {
											newRecordCount--;
										}
										App.sNewPushRecordCount = String
												.valueOf(newRecordCount);
										saveNewPushRecordCount(App.sNewPushRecordCount);
										MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
									}
									if(pushRecord.getLogin_account()==2001){//删除的是新闻
										Loger.i("TEST","删除新闻Item成功！");
										if(App.sUserLoginId<=0){
											AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
											App.sUserLoginId = dbImpl.getUserId();
										}
										daoDBImpl.deleteNewsObj("2001", String.valueOf(App.sUserLoginId));
									}else if(pushRecord.getLogin_account()==1001){//删除的是举报
										long topicId;
										try {
											JSONObject jsonObj = new JSONObject(pushRecord.getContent());
											topicId = jsonObj.getLong("topicId");
											if(App.sUserLoginId<=0){
												AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
												App.sUserLoginId = dbImpl.getUserId();
											}
											daoDBImpl.deleteReportObjs(topicId,String.valueOf(App.sUserLoginId));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											Loger.i("TEST", "划动删除失败=>"+e.getMessage());
										}
									}else if(pushRecord.getLogin_account()==1010){//删除的是回复
										long topicId;
										try {
											JSONObject jsonObj = new JSONObject(pushRecord.getContent());
											topicId = jsonObj.getLong("topicId");
											if(App.sUserLoginId<=0){
												AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
												App.sUserLoginId = dbImpl.getUserId();
											}
											daoDBImpl.deleteCommentObjs(topicId,String.valueOf(App.sUserLoginId));
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											Loger.i("TEST", "划动删除失败=>"+e.getMessage());
										}
									}else if(pushRecord.getLogin_account()==1011){//打招呼
										long topicId;
										try {
											JSONObject jsonObj = new JSONObject(pushRecord.getContent());
											topicId = jsonObj.getLong("topicId");
											String sayHelloUserId = jsonObj.getString("commentType");
											Loger.i("TEST", "打招呼人的UserId=>"+sayHelloUserId);
											if(App.sUserLoginId<=0){
												AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
												App.sUserLoginId = dbImpl.getUserId();
											}
											daoDBImpl.deleteSayHelloObjs(topicId,String.valueOf(App.sUserLoginId),sayHelloUserId);
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											Loger.i("TEST", "划动删除失败=>"+e.getMessage());
										}
									}else{
										daoDBImpl.deleteObject(pushRecord.getRecord_id());
									}
									NewPushRecordAbsActivity.sPushInfoObjList
											.remove(position);
									NewPushRecordAbsActivity.sPushRecordObjSize--;
									if (pushListmsg.size() > 0) {
										pushListView.setVisibility(View.VISIBLE);
									} else {
										pushListView.setVisibility(View.GONE);
										NewPushRecordAbsActivity.this.finish();
										NewPushRecordAbsActivity.sPushInfoObjList = null;
										NewPushRecordAbsActivity.sPushRecordObjSize = 0;
									}
								}
							}
							pushMsgAdapter.notifyDataSetChanged();
						}
					});
			pushListView.setOnTouchListener(touchListener);
			pushListView.setOnScrollListener(touchListener.makeScrollListener());
			pushListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, final View view,
						final int position, long id) {
					if(!TimeClick.isFastClick()){
					final PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(NewPushRecordAbsActivity.this);
					final PushRecord pushRecord = (PushRecord) NewPushRecordAbsActivity.sPushInfoObjList.get(position);
					final TextView pushContent = (TextView) view.findViewById(R.id.tv_newmessage_content);
					Loger.i("TEST", "当前阅读状态=》"+pushRecord.getType());
					JSONObject pushjsonObj = null;
					try {
						pushjsonObj = new JSONObject(pushRecord.getContent());
						long currentCommunityId = 0;
						try {
							currentCommunityId = pushjsonObj.getLong("communityId");
							if(currentCommunityId!=App.sFamilyCommunityId){
								AllFamilyDaoDBImpl dao = new AllFamilyDaoDBImpl(NewPushRecordAbsActivity.this);
						        communityName = dao.getCommunityNameById(String.valueOf(currentCommunityId));
						        String errorInfo = null;
						        if(communityName==null){//表明没有该小区
						        	errorInfo = "您尚未加入该小区";
						        }else{
						        	errorInfo = "请切换到["+communityName+"]进行查看";
						        }
								Toast.makeText(NewPushRecordAbsActivity.this, errorInfo, Toast.LENGTH_LONG).show();
								return;
							}
						} catch (Exception e3) {
							currentCommunityId = 0;
						}
					} catch (JSONException e4) {
						e4.printStackTrace();
					}
					ylProgressDialog.show();
					if(pushRecord.getType()==2){//1=>未阅读  2=>已阅读
						try {
							int pushType = pushjsonObj.getInt("pushType");
							int contType = 0;
							try {
								contType = pushjsonObj.getInt("contentType");
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							int topicType = 0;
							try {
								Loger.i("TEST", "NewPushRecordAbsActivity ---> pushjsonObj ====>"+pushjsonObj.toString());
								topicType = pushjsonObj.getInt("topicType");
							} catch (Exception e) {
								Loger.i("TEST", "Error"+e.getMessage());
							}
							Loger.i("TEST", "当前pushType=》"+pushType);
							if(2==pushType){
								Intent intent = new Intent(NewPushRecordAbsActivity.this,PushRecordDetailActivity.class);
								intent.putExtra("pushInfo", pushContent.getTag().toString());
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								startActivity(intent);
								finish();
								ylProgressDialog.dismiss();
							}else if(1==pushType){
								if(5==contType){
									Intent intent = new Intent(NewPushRecordAbsActivity.this,NewsDetail.class);
									JSONObject newspush = new JSONObject(pushContent.getTag().toString());
									intent.putExtra("linkurl", newspush.getString("new_url"));
									intent.putExtra("title", newspush.getString("new_title"));
									intent.putExtra("newsid", newspush.getString("new_id"));
									intent.putExtra("picurl",newspush.getString("new_small_pic"));
									//intent.putExtra("pushInfo", pushContent.getTag().toString());
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
									ylProgressDialog.dismiss();
								}else{
									Intent intent = new Intent(NewPushRecordAbsActivity.this,PushRecordDetailActivity.class);
									intent.putExtra("pushInfo", pushContent.getTag().toString());
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
									ylProgressDialog.dismiss();
								}
								finish();
							}else if(3==pushType){
								JSONObject json = new JSONObject(pushContent.getTag().toString());
								String pushTitle = json.getString("title");
								Loger.i("TEST","pushTitle===>"+pushTitle);
								if (pushTitle.equals(App.PUSH_PROPERTY_NEW_NOTICE)) {// 有公告
									RequestParams reference = new RequestParams();
									reference.put("community_id", App.sFamilyCommunityId);
									reference.put("user_id", App.sUserLoginId);
									reference.put("count", 1);
									reference.put("topic_id", Integer.parseInt(json.getString("topicId")));
									reference.put("category_type", App.GONGGAO_TYPE);
									reference.put("tag", "getnotice");
									reference.put("apitype", IHttpRequestUtils.APITYPE[4]);
									AsyncHttpClient httpClient = new AsyncHttpClient();
									httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
											new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
											Loger.i("TEST", response.toString());
											getTopicDetailsInfos(response);
											cRequesttype = true;
											if (forumtopicLists.size() > 0) {
												flag = "ok";
											}
											super.onSuccess(statusCode, headers, response);
										}
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
											try {
												flag = response.getString("flag");
												if (flag.equals("no")) {
													cRequesttype = true;
													daoDBImpl.deleteObject(pushRecord.getRecord_id());
													Toast.makeText(NewPushRecordAbsActivity.this, "此公告已删除",
															Toast.LENGTH_SHORT).show();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
										@Override
										public void onFailure(int statusCode, Header[] headers, String responseString,
												Throwable throwable) {
											new ErrorServer(NewPushRecordAbsActivity.this, responseString);
											super.onFailure(statusCode, headers, responseString, throwable);
										}
									});
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											// TODO Auto-generated method stub
											Long currenttime = System.currentTimeMillis();
											while (!cRequesttype) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													cRequesttype = true;
												}
											}
											return null;
										}
										protected void onPostExecute(Void result) {
											if (cRequesttype == true && flag.equals("ok")) {
												flag = "none";
												ylProgressDialog.dismiss();
												Intent intent = new Intent(NewPushRecordAbsActivity.this,CircleDetailActivity.class);
												intent.putExtra("parent", 4);
												intent.putExtra("position", 0);
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											}
											NewPushRecordAbsActivity.this.finish();
										};
									}.execute();
									return;
								}
								if(pushTitle.equals(App.PUSH_PROPERTY_NEW_REPAIR)){//物业接收到报修信息
									Loger.i("TEST", "//物业接收到报修信息");
									ylProgressDialog.dismiss();
									startActivity(new Intent(NewPushRecordAbsActivity.this,PropertyRepairList.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
									return;
								}
								if(pushTitle.equals(App.PUSH_PROPERTY_RUN_REPAIR)){//物业推送给个人修改状态
									Loger.i("TEST", "//物业推送给个人修改状态");
									ylProgressDialog.dismiss();
									Intent intent = new Intent(NewPushRecordAbsActivity.this,PropertyRepairActivity.class);
									JSONObject repairshadulepush = new JSONObject(pushContent.getTag().toString());
									Loger.i("test3","物业推送给个人修改状态"+repairshadulepush.toString());
									intent.putExtra("repairstatus", repairshadulepush.getString("repairStatus"));
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
									// hyy
									return;
								}
								return;
							}else if(4==pushType){//举报
								final JSONObject json = new JSONObject(pushContent.getTag().toString());
								RequestParams reference = new RequestParams();
								reference.put("community_id", App.sFamilyCommunityId);
								reference.put("user_id", App.sUserLoginId);
								reference.put("count", 1);
								reference.put("topic_id", json.getString("topicId"));
								reference.put("type", App.sUserType);
								reference.put("tag", "gettopic");
								reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
								AsyncHttpClient httpClient = new AsyncHttpClient();
								httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
										new JsonHttpResponseHandler() {
									@Override
									public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
										Loger.i("TEST", response.toString());
										getTopicDetailsInfos(response);
										cRequesttype = true;
										if (forumtopicLists.size() > 0) {
											flag = "ok";
										}
										super.onSuccess(statusCode, headers, response);
									}
									@Override
									public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
										try {
											flag = response.getString("flag");
											if (flag.equals("no")) {
												cRequesttype = true;
												// daoDBImpl.deleteObject(pushRecord.getRecord_id());
												Toast.makeText(NewPushRecordAbsActivity.this, "此内容已被删除",
														Toast.LENGTH_SHORT).show();
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
									@Override
									public void onFailure(int statusCode, Header[] headers, String responseString,
											Throwable throwable) {
										new ErrorServer(NewPushRecordAbsActivity.this, responseString);
										super.onFailure(statusCode, headers, responseString, throwable);
									}
								});
								new AsyncTask<Void, Void, Void>() {
									@Override
									protected Void doInBackground(Void... params) {
										Long currenttime = System.currentTimeMillis();
										while (!cRequesttype) {
											if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
												cRequesttype = true;
											}
										}
										return null;
									}
									protected void onPostExecute(Void result) {
										if (cRequesttype == true && flag.equals("ok")) {
											flag = "none";
											ylProgressDialog.dismiss();
											Intent intent = new Intent(NewPushRecordAbsActivity.this, ReportDetailActivity.class);
											try {
												intent.putExtra("report_tId", Long.parseLong(json.getString("topicId")));
												intent.putExtra("report_detail",String.valueOf(json.getString("topicDetail")));
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											} catch (NumberFormatException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (JSONException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
										NewPushRecordAbsActivity.this.finish();
									};
								}.execute();
								
								return;
							}else if(5==pushType){//新闻
								ylProgressDialog.dismiss();
								Intent intent = new Intent(NewPushRecordAbsActivity.this,PushNewsDetailActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								startActivity(intent);
								finish();
								return;
							}else if(8==pushType){//天气
								final JSONObject json = new JSONObject(pushContent.getTag().toString());
								Intent intent = new Intent(NewPushRecordAbsActivity.this,WeatherListActivity.class);
								intent.putExtra("weaorzoc_id", Long.parseLong(json.getString("weaId")));
								intent.putExtra("community_id",Long.parseLong(json.getString("communityId")));
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								startActivity(intent);
								finish();
								return;
							}else if(7==pushType){//评论
								final JSONObject json = new JSONObject(pushContent.getTag().toString());
								RequestParams params=new RequestParams();
								params.put("tag", "getorcheckrecord");
								params.put("apitype", "address");
								params.put("uer_id", json.getString("uerId"));
								AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
								asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler(){
									public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
										ylProgressDialog.dismiss();
										try {
											String flag=response.getString("flag");
											Loger.i("LYM", "7==pushType--->"+flag);
											if(flag.equals("no")){
												Intent intent = new Intent(NewPushRecordAbsActivity.this,NewPushStoreCommentActivity.class);
												intent.putExtra("tag", json.getString("tag"));
												intent.putExtra("uid", json.getString("uid"));
												intent.putExtra("shop_name", json.getString("shopName"));
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
												finish();
											}else if(flag.equals("ok")){
												Intent intent = new Intent(NewPushRecordAbsActivity.this,StoreCircleDetailActivity.class);
												intent.putExtra("uid", json.getString("uid"));
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
												finish();
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										super.onSuccess(statusCode, headers, response);
									};
									public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
										new ErrorServer(NewPushRecordAbsActivity.this, responseString);
										super.onFailure(statusCode, headers, responseString, throwable);
									};
								});
								return;
							}else if(6==pushType){//回复
								Loger.i("TEST", "NewPushRecordAbsActivity ---> topicType ====>"+topicType);
								if(4==topicType){//以物易物
									JSONObject json = null;
									try {
										json = new JSONObject(pushContent.getTag().toString());
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									String topicId = null;
									try {
										topicId = json.getString("topicId");
									} catch (NumberFormatException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
									RequestParams reference = new RequestParams();
									reference.put("community_id", App.sFamilyCommunityId);
									reference.put("user_id", App.sUserLoginId);
									reference.put("count", 1);
									reference.put("topic_id", topicId);
									reference.put("type", App.sUserType);
									reference.put("tag", "gettopic");
									reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
									AsyncHttpClient httpClient = new AsyncHttpClient();
									httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
											new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
											Loger.i("TEST", response.toString());
											getTopicDetailsInfos(response);
											cRequesttype = true;
											if (forumtopicLists.size() > 0) {
												flag = "ok";
											}
											super.onSuccess(statusCode, headers, response);
										}
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
											try {
												flag = response.getString("flag");
												if (flag.equals("no")) {
													cRequesttype = true;
													// daoDBImpl.deleteObject(pushRecord.getRecord_id());
													Toast.makeText(NewPushRecordAbsActivity.this, "此内容已被删除",
															Toast.LENGTH_SHORT).show();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
										@Override
										public void onFailure(int statusCode, Header[] headers, String responseString,
												Throwable throwable) {
											new ErrorServer(NewPushRecordAbsActivity.this, responseString);
											super.onFailure(statusCode, headers, responseString, throwable);
										}
									});
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											Long currenttime = System.currentTimeMillis();
											while (!cRequesttype) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													cRequesttype = true;
												}
											}
											return null;
										}
										protected void onPostExecute(Void result) {
											if (cRequesttype == true && flag.equals("ok")) {
												flag = "none";
												ylProgressDialog.dismiss();
												Intent intent = new Intent(NewPushRecordAbsActivity.this,BarterDedailCommentActivity.class);
												try {
													JSONObject object = new JSONObject(pushContent.getTag().toString());
													intent.putExtra("topic_id", Long.parseLong(object.getString("topicId")));
												} catch (NumberFormatException e) {
													e.printStackTrace();
												} catch (JSONException e) {
													e.printStackTrace();
												}
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											}
											NewPushRecordAbsActivity.this.finish();
										};
									}.execute();
								}else if(11==topicType){//打招呼
									JSONObject json = null;
									try {
										json = new JSONObject(pushContent.getTag().toString());
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									String topicId = null;
									try {
										topicId = json.getString("topicId");
									} catch (NumberFormatException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
									RequestParams reference = new RequestParams();
									reference.put("community_id", App.sFamilyCommunityId);
									reference.put("user_id", App.sUserLoginId);
									reference.put("count", 1);
									reference.put("topic_id", topicId);
									reference.put("type", App.sUserType);
									reference.put("tag", "gettopic");
									reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
									AsyncHttpClient httpClient = new AsyncHttpClient();
									httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
											new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
											Loger.i("TEST", response.toString());
											getTopicDetailsInfos(response);
											cRequesttype = true;
											if (forumtopicLists.size() > 0) {
												flag = "ok";
											}
											super.onSuccess(statusCode, headers, response);
										}
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
											try {
												flag = response.getString("flag");
												if (flag.equals("no")) {
													cRequesttype = true;
													// daoDBImpl.deleteObject(pushRecord.getRecord_id());
													Toast.makeText(NewPushRecordAbsActivity.this, "此内容已被删除",
															Toast.LENGTH_SHORT).show();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
										@Override
										public void onFailure(int statusCode, Header[] headers, String responseString,
												Throwable throwable) {
											new ErrorServer(NewPushRecordAbsActivity.this, responseString);
											super.onFailure(statusCode, headers, responseString, throwable);
										}
									});
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											Long currenttime = System.currentTimeMillis();
											while (!cRequesttype) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													cRequesttype = true;
												}
											}
											return null;
										}
										protected void onPostExecute(Void result) {
											if (cRequesttype == true && flag.equals("ok")) {
												flag = "none";
												ylProgressDialog.dismiss();
												Intent intent = new Intent(NewPushRecordAbsActivity.this,
														CircleDetailActivity.class);
												intent.putExtra("parent", 4);
												intent.putExtra("position", 0);
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											}
											NewPushRecordAbsActivity.this.finish();
										};
									}.execute();
								}else{
									JSONObject json = null;
									try {
										json = new JSONObject(pushContent.getTag().toString());
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									String topicId = null;
									try {
										topicId = json.getString("topicId");
									} catch (NumberFormatException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
									RequestParams reference = new RequestParams();
									reference.put("community_id", App.sFamilyCommunityId);
									reference.put("user_id", App.sUserLoginId);
									reference.put("count", 1);
									reference.put("topic_id", topicId);
									reference.put("type", App.sUserType);
									reference.put("tag", "gettopic");
									reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
									AsyncHttpClient httpClient = new AsyncHttpClient();
									httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
											new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
											Loger.i("TEST", response.toString());
											getTopicDetailsInfos(response);
											cRequesttype = true;
											if (forumtopicLists.size() > 0) {
												flag = "ok";
											}
											super.onSuccess(statusCode, headers, response);
										}
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
											try {
												flag = response.getString("flag");
												if (flag.equals("no")) {
													cRequesttype = true;
													// daoDBImpl.deleteObject(pushRecord.getRecord_id());
													Toast.makeText(NewPushRecordAbsActivity.this, "此内容已被删除",
															Toast.LENGTH_SHORT).show();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
										@Override
										public void onFailure(int statusCode, Header[] headers, String responseString,
												Throwable throwable) {
											new ErrorServer(NewPushRecordAbsActivity.this, responseString);
											super.onFailure(statusCode, headers, responseString, throwable);
										}
									});
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											Long currenttime = System.currentTimeMillis();
											while (!cRequesttype) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													cRequesttype = true;
												}
											}
											return null;
										}
										protected void onPostExecute(Void result) {
											if (cRequesttype == true && flag.equals("ok")) {
												flag = "none";
												ylProgressDialog.dismiss();
												Intent intent = new Intent(NewPushRecordAbsActivity.this,CircleDetailActivity.class);
												intent.putExtra("parent", 4);
												intent.putExtra("position", 0);
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											}
											NewPushRecordAbsActivity.this.finish();
										};
									}.execute();
								}
								return;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
					if (pushRecord.getType() == 1) {//1=>未阅读  2=>已阅读
						try {
//							JSONObject pushjsonObj = new JSONObject(pushRecord.getContent());
							int pushType = pushjsonObj.getInt("pushType");
							int contType = 0;
							try {
								contType = pushjsonObj.getInt("contentType");
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							int topicType = 0;
							try {
								Loger.i("TEST", "NewPushRecordAbsActivity ---> pushjsonObj ====>"+pushjsonObj.toString());
								topicType = pushjsonObj.getInt("topicType");
							} catch (Exception e) {
								Loger.i("TEST", "Error"+e.getMessage());
							}
							if(2==pushType){
								getAboutRecordArray();
								pushRecord.setType(2);
								daoDBImpl.modifyObject(pushRecord);
								int newRecordCount = Integer
										.parseInt(App.sNewPushRecordCount);
								if (newRecordCount > 0) {
									newRecordCount--;
								}
								App.sNewPushRecordCount = String
										.valueOf(newRecordCount);
								saveNewPushRecordCount(App.sNewPushRecordCount);
								MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
								ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
								pushImageView.setVisibility(View.GONE);
								Intent intent = new Intent(NewPushRecordAbsActivity.this,PushRecordDetailActivity.class);
								intent.putExtra("pushInfo", pushContent.getTag().toString());
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								startActivity(intent);
								finish();
								ylProgressDialog.dismiss();
							}else if(1==pushType){
								getAboutRecordArray();
								pushRecord.setType(2);
								daoDBImpl.modifyObject(pushRecord);
								int newRecordCount = Integer
										.parseInt(App.sNewPushRecordCount);
								if (newRecordCount > 0) {
									newRecordCount--;
								}
								App.sNewPushRecordCount = String
										.valueOf(newRecordCount);
								saveNewPushRecordCount(App.sNewPushRecordCount);
								MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
								ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
								pushImageView.setVisibility(View.GONE);
								if(5==contType){
									Intent intent = new Intent(NewPushRecordAbsActivity.this,NewsDetail.class);
									Loger.i("test5", pushContent.getTag().toString() );
									JSONObject newspush = new JSONObject(pushContent.getTag().toString());
									intent.putExtra("linkurl", newspush.getString("new_url"));
									intent.putExtra("title", newspush.getString("new_title"));
									intent.putExtra("newsid", newspush.getString("new_id"));
									intent.putExtra("picurl",newspush.getString("new_small_pic"));
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
								}else{
									Intent intent = new Intent(NewPushRecordAbsActivity.this,
											PushRecordDetailActivity.class);
									intent.putExtra("pushInfo", pushContent.getTag().toString());
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
								}
								ylProgressDialog.dismiss();
								finish();
							}else if(3==pushType){
								JSONObject jsonTitle = new JSONObject(pushContent.getTag().toString());
								String pushTitle = jsonTitle.getString("title");
								Loger.i("TEST","pushTitle===>"+pushTitle);
								if(pushTitle.equals(App.PUSH_PROPERTY_NEW_NOTICE)){//有公告
									getAboutRecordArray();
									pushRecord.setType(2);
									daoDBImpl.modifyObject(pushRecord);
									int newRecordCount = Integer
											.parseInt(App.sNewPushRecordCount);
									if (newRecordCount > 0) {
										newRecordCount--;
									}
									App.sNewPushRecordCount = String
											.valueOf(newRecordCount);
									saveNewPushRecordCount(App.sNewPushRecordCount);
									MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
									ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
									pushImageView.setVisibility(View.GONE);
									// 跳转至详情
									JSONObject json = null;
									try {
										json = new JSONObject(pushContent
												.getTag().toString());
									} catch (JSONException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									RequestParams reference = new RequestParams();
									reference.put("tag","getnotice");
									reference.put("apitype",IHttpRequestUtils.APITYPE[4]);
									reference.put("community_id",
											App.sFamilyCommunityId);
									reference.put("user_id", App.sUserLoginId);
									reference.put("count", 1);
									reference.put("category_type",
											App.GONGGAO_TYPE);
									try {
										reference.put("topic_id", Integer
												.parseInt(json
														.getString("topicId")));
									} catch (NumberFormatException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} catch (JSONException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									reference.put("type", 2);
									AsyncHttpClient httpClient = new AsyncHttpClient();
									httpClient
											.post(IHttpRequestUtils.URL
													+ IHttpRequestUtils.YOULIN,
													reference,
													new JsonHttpResponseHandler() {
														@Override
														public void onSuccess(
																int statusCode,
																Header[] headers,
																JSONArray response) {
															Loger.i("TEST",response.toString());
															getTopicDetailsInfos(response);
															cRequesttype = true;
															if (forumtopicLists.size() > 0) {
																flag = "ok";
															}
															super.onSuccess(statusCode,headers,response);
														}
														@Override
														public void onSuccess(int statusCode,Header[] headers,JSONObject response) {
															try {
																flag = response.getString("flag");
																if (flag.equals("no")) {
																	cRequesttype = true;
																	daoDBImpl.deleteObject(pushRecord.getRecord_id());
																	Toast.makeText(NewPushRecordAbsActivity.this,"此公告已删除",Toast.LENGTH_SHORT).show();
																}
															} catch (JSONException e) {
																e.printStackTrace();
															}
														}
														@Override
														public void onFailure(int statusCode,Header[] headers,String responseString,Throwable throwable) {
															new ErrorServer(NewPushRecordAbsActivity.this, responseString);
															super.onFailure(statusCode,headers,responseString,throwable);
														}
													});
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(
												Void... params) {
											// TODO Auto-generated method stub
											Long currenttime = System
													.currentTimeMillis();
											while (!cRequesttype) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													cRequesttype = true;
												}
											}
											return null;
										}
										protected void onPostExecute(Void result) {
											if (cRequesttype == true && flag.equals("ok")) {
												flag = "none";
												ylProgressDialog.dismiss();
												Intent intent = new Intent(NewPushRecordAbsActivity.this,CircleDetailActivity.class);
												intent.putExtra("parent", 4);
												intent.putExtra("position", 0);
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											}
											NewPushRecordAbsActivity.this.finish();
										};
									}.execute();
									return;
								}
								if(pushTitle.equals(App.PUSH_PROPERTY_NEW_REPAIR)){//物业接收到报修信息
									getAboutRecordArray();
									pushRecord.setType(2);
									daoDBImpl.modifyObject(pushRecord);
									int newRecordCount = Integer
											.parseInt(App.sNewPushRecordCount);
									if (newRecordCount > 0) {
										newRecordCount--;
									}
									App.sNewPushRecordCount = String
											.valueOf(newRecordCount);
									saveNewPushRecordCount(App.sNewPushRecordCount);
									MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
									ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
									pushImageView.setVisibility(View.GONE);
									startActivity(new Intent(NewPushRecordAbsActivity.this,PropertyRepairList.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
									ylProgressDialog.dismiss();
									return;
								}
								if(pushTitle.equals(App.PUSH_PROPERTY_RUN_REPAIR)){//物业推送给个人修改状态
									Loger.i("TEST", "//物业推送给个人修改状态");
									getAboutRecordArray();
									pushRecord.setType(2);
									daoDBImpl.modifyObject(pushRecord);
									int newRecordCount = Integer
											.parseInt(App.sNewPushRecordCount);
									if (newRecordCount > 0) {
										newRecordCount--;
									}
									App.sNewPushRecordCount = String.valueOf(newRecordCount);
									saveNewPushRecordCount(App.sNewPushRecordCount);
									MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
									ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
									pushImageView.setVisibility(View.GONE);
									Intent intent = new Intent(NewPushRecordAbsActivity.this,PropertyRepairActivity.class);
									JSONObject repairshadulepush = new JSONObject(pushContent.getTag().toString());
									Loger.d("test3","物业推送给个人修改状态:"+repairshadulepush.toString());
									intent.putExtra("repairstatus", repairshadulepush.getString("repairStatus"));
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
									ylProgressDialog.dismiss();
									return;
								}
								return;
							}else if(4==pushType){
								getAboutRecordArray();
								if(App.sUserLoginId<=0){
									AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
									App.sUserLoginId = dbImpl.getUserId();
								}
								JSONObject topicObj = new JSONObject(pushRecord.getContent());		
								Loger.i("TEST", "当前修改的topic==>"+topicObj.getLong("topicId"));
								daoDBImpl.modifyReportObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
								int newRecordCount = Integer
										.parseInt(App.sNewPushRecordCount);
								if (newRecordCount > 0) {
									newRecordCount--;
								}
								App.sNewPushRecordCount = String.valueOf(newRecordCount);
								saveNewPushRecordCount(App.sNewPushRecordCount);
								MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
								ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
								pushImageView.setVisibility(View.GONE);

								JSONObject json = new JSONObject(pushContent.getTag().toString());
								Intent intent = new Intent(NewPushRecordAbsActivity.this, ReportDetailActivity.class);
								intent.putExtra("report_tId", Long.parseLong(json.getString("topicId")));
								intent.putExtra("report_detail",String.valueOf(json.getString("topicDetail")));
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								startActivity(intent);
								ylProgressDialog.dismiss();
								NewPushRecordAbsActivity.this.finish();
							}else if(5==pushType){//新闻
								getAboutRecordArray();
								if(App.sUserLoginId<=0){
									AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
									App.sUserLoginId = dbImpl.getUserId();
								}
								daoDBImpl.modifyNewsObjs(2,App.sUserLoginId);
								int newRecordCount = Integer.parseInt(App.sNewPushRecordCount);
								if (newRecordCount > 0) {
									newRecordCount--;
								}
								App.sNewPushRecordCount = String.valueOf(newRecordCount);
								saveNewPushRecordCount(App.sNewPushRecordCount);
								MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
								ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
								pushImageView.setVisibility(View.GONE);
								Intent intent = new Intent(NewPushRecordAbsActivity.this,PushNewsDetailActivity.class);
//								intent.putExtra("pushInfo", pushContent.getTag().toString());
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								startActivity(intent);
								ylProgressDialog.dismiss();
								finish();
								return;
							}else if(8==pushType){//天气
								getAboutRecordArray();
								pushRecord.setType(2);
								daoDBImpl.modifyObject(pushRecord);
								int newRecordCount = Integer
										.parseInt(App.sNewPushRecordCount);
								if (newRecordCount > 0) {
									newRecordCount--;
								}
								App.sNewPushRecordCount = String
										.valueOf(newRecordCount);
								saveNewPushRecordCount(App.sNewPushRecordCount);
								MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
								ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
								pushImageView.setVisibility(View.GONE);
								final JSONObject json = new JSONObject(pushContent.getTag().toString());
								Intent intent = new Intent(NewPushRecordAbsActivity.this,WeatherListActivity.class);
								intent.putExtra("weaorzoc_id", Long.parseLong(json.getString("weaId")));
								intent.putExtra("community_id",String.valueOf(json.getString("communityId")));
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								startActivity(intent);
								finish();
								return;
							}else if(7==pushType){//评论
								getAboutRecordArray();
								pushRecord.setType(2);
								daoDBImpl.modifyObject(pushRecord);
								int newRecordCount = Integer
										.parseInt(App.sNewPushRecordCount);
								if (newRecordCount > 0) {
									newRecordCount--;
								}
								App.sNewPushRecordCount = String.valueOf(newRecordCount);
								saveNewPushRecordCount(App.sNewPushRecordCount);
								MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
								ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
								pushImageView.setVisibility(View.GONE);
								final JSONObject json = new JSONObject(pushContent.getTag().toString());
								RequestParams params=new RequestParams();
								params.put("tag", "getorcheckrecord");
								params.put("apitype", "address");
								params.put("uer_id", json.getString("uerId"));
								AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
								asyncHttpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler(){
									public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
										ylProgressDialog.dismiss();
										try {
											String flag=response.getString("flag");
											Loger.i("LYM", "7==2pushType--->"+flag);
											if(flag.equals("no")){
												Intent intent = new Intent(NewPushRecordAbsActivity.this,NewPushStoreCommentActivity.class);
												intent.putExtra("tag", json.getString("tag"));
												intent.putExtra("uid", json.getString("uid"));
												intent.putExtra("shop_name", json.getString("shopName"));
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
												finish();
											}else if(flag.equals("ok")){
												Intent intent = new Intent(NewPushRecordAbsActivity.this,StoreCircleDetailActivity.class);
												intent.putExtra("uid", json.getString("uid"));
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
												finish();
											}
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										super.onSuccess(statusCode, headers, response);
									};
									public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
										new ErrorServer(NewPushRecordAbsActivity.this, responseString);
										super.onFailure(statusCode, headers, responseString, throwable);
									};
								});
								return;
							}else if(6==pushType){//回复
								Loger.i("TEST", "NewPushRecordAbsActivity ---> topicType ====>"+topicType);
								if(4==topicType){//以物易物
									getAboutRecordArray();
									if(App.sUserLoginId<=0){
										AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
										App.sUserLoginId = dbImpl.getUserId();
									}
									JSONObject topicObj = new JSONObject(pushRecord.getContent());		
									Loger.i("TEST", "当前修改的topic==>"+topicObj.getLong("topicId"));
									daoDBImpl.modifyCommentObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
									int newCommentCount = Integer.parseInt(App.sNewPushRecordCount);
									if (newCommentCount > 0) {
										newCommentCount--;
									}
									App.sNewPushRecordCount = String.valueOf(newCommentCount);
									saveNewPushRecordCount(App.sNewPushRecordCount);
									MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
									ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
									pushImageView.setVisibility(View.GONE);

									// 跳转至详情
									JSONObject json = null;
									try {
										json = new JSONObject(pushContent.getTag().toString());
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									String topicId = null;
									try {
										topicId = json.getString("topicId");
									} catch (NumberFormatException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
									RequestParams reference = new RequestParams();
									reference.put("community_id",App.sFamilyCommunityId);
									reference.put("user_id",App.sUserLoginId);
									reference.put("count", 1);
									reference.put("topic_id",topicId);
									reference.put("type", App.sUserType);
									reference.put("tag","gettopic");
									reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
									AsyncHttpClient httpClient = new AsyncHttpClient();
									httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
											new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
											Loger.i("TEST", response.toString());
											getTopicDetailsInfos(response);
											cRequesttype = true;
											if (forumtopicLists.size() > 0) {
												flag = "ok";
											}
											super.onSuccess(statusCode, headers, response);
										}

										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
											try {
												flag = response.getString("flag");
												if (flag.equals("no")) {
													cRequesttype = true;
													// daoDBImpl.deleteObject(pushRecord.getRecord_id());
													Toast.makeText(NewPushRecordAbsActivity.this, "此内容已被删除",
															Toast.LENGTH_SHORT).show();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}

										@Override
										public void onFailure(int statusCode, Header[] headers, String responseString,
												Throwable throwable) {
											new ErrorServer(NewPushRecordAbsActivity.this, responseString);
											super.onFailure(statusCode, headers, responseString, throwable);
										}
									});
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											// TODO Auto-generated method stub
											Long currenttime = System.currentTimeMillis();
											while (!cRequesttype) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													cRequesttype = true;
												}
											}
											return null;
										}

										protected void onPostExecute(Void result) {
											if (cRequesttype == true && flag.equals("ok")) {
												flag = "none";
												ylProgressDialog.dismiss();
												Intent intent = new Intent(NewPushRecordAbsActivity.this,
														BarterDedailCommentActivity.class);
												try {
													JSONObject object = new JSONObject(pushContent.getTag().toString());
													intent.putExtra("topic_id", Long.parseLong(object.getString("topicId")));
												} catch (NumberFormatException e) {
													e.printStackTrace();
												} catch (JSONException e) {
													e.printStackTrace();
												}
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											}
											NewPushRecordAbsActivity.this.finish();
										};
									}.execute();
								}else if(11==topicType){//打招呼
									getAboutRecordArray();
									if(App.sUserLoginId<=0){
										AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
										App.sUserLoginId = dbImpl.getUserId();
									}
									JSONObject topicObj = new JSONObject(pushRecord.getContent());		
									Loger.i("TEST", "当前修改的topic==>"+topicObj.getLong("topicId"));
									String sayHelloUserId = topicObj.getString("commentType");
									Loger.i("TEST", "当前打招呼UserId==>"+sayHelloUserId);
									daoDBImpl.modifySayHelloObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId),sayHelloUserId);
									int newCommentCount = Integer.parseInt(App.sNewPushRecordCount);
									if (newCommentCount > 0) {
										newCommentCount--;
									}
									App.sNewPushRecordCount = String.valueOf(newCommentCount);
									saveNewPushRecordCount(App.sNewPushRecordCount);
									MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
									ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
									pushImageView.setVisibility(View.GONE);
									
									// 跳转至详情
									JSONObject json = null;
									try {
										json = new JSONObject(pushContent.getTag().toString());
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									String topicId = null;
									try {
										topicId = json.getString("topicId");
									} catch (NumberFormatException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
									RequestParams reference = new RequestParams();
									reference.put("community_id",App.sFamilyCommunityId);
									reference.put("user_id",App.sUserLoginId);
									reference.put("count", 1);
									reference.put("topic_id",topicId);
									reference.put("type", App.sUserType);
									reference.put("tag","gettopic");
									reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
									AsyncHttpClient httpClient = new AsyncHttpClient();
									httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
											new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
											Loger.i("TEST", response.toString());
											getTopicDetailsInfos(response);
											cRequesttype = true;
											if (forumtopicLists.size() > 0) {
												flag = "ok";
											}
											super.onSuccess(statusCode, headers, response);
										}

										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
											try {
												flag = response.getString("flag");
												if (flag.equals("no")) {
													cRequesttype = true;
													// daoDBImpl.deleteObject(pushRecord.getRecord_id());
													Toast.makeText(NewPushRecordAbsActivity.this, "此内容已被删除",
															Toast.LENGTH_SHORT).show();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}

										@Override
										public void onFailure(int statusCode, Header[] headers, String responseString,
												Throwable throwable) {
											new ErrorServer(NewPushRecordAbsActivity.this, responseString);
											super.onFailure(statusCode, headers, responseString, throwable);
										}
									});
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											// TODO Auto-generated method stub
											Long currenttime = System.currentTimeMillis();
											while (!cRequesttype) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													cRequesttype = true;
												}
											}
											return null;
										}

										protected void onPostExecute(Void result) {
											if (cRequesttype == true && flag.equals("ok")) {
												flag = "none";
												ylProgressDialog.dismiss();
												Intent intent = new Intent(NewPushRecordAbsActivity.this,CircleDetailActivity.class);
												intent.putExtra("parent", 4);
												intent.putExtra("position", 0);
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											}
											NewPushRecordAbsActivity.this.finish();
										};
									}.execute();
								}else{
									getAboutRecordArray();
									if(App.sUserLoginId<=0){
										AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
										App.sUserLoginId = dbImpl.getUserId();
									}
									JSONObject topicObj = new JSONObject(pushRecord.getContent());		
									Loger.i("TEST", "当前修改的topic==>"+topicObj.getLong("topicId"));
									daoDBImpl.modifyCommentObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
									int newCommentCount = Integer.parseInt(App.sNewPushRecordCount);
									if (newCommentCount > 0) {
										newCommentCount--;
									}
									App.sNewPushRecordCount = String.valueOf(newCommentCount);
									saveNewPushRecordCount(App.sNewPushRecordCount);
									MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
									ImageView pushImageView = (ImageView) view.findViewById(R.id.iv_push_info_read_status);
									pushImageView.setVisibility(View.GONE);

									// 跳转至详情
									JSONObject json = null;
									try {
										json = new JSONObject(pushContent.getTag().toString());
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									String topicId = null;
									try {
										topicId = json.getString("topicId");
									} catch (NumberFormatException e2) {
										// TODO Auto-generated catch block
										e2.printStackTrace();
									}
									RequestParams reference = new RequestParams();
									reference.put("community_id",App.sFamilyCommunityId);
									reference.put("user_id",App.sUserLoginId);
									reference.put("count", 1);
									reference.put("topic_id",topicId);
									reference.put("type", App.sUserType);
									reference.put("tag","gettopic");
									reference.put("apitype", IHttpRequestUtils.APITYPE[5]);
									AsyncHttpClient httpClient = new AsyncHttpClient();
									httpClient.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN, reference,
											new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
											Loger.i("TEST", response.toString());
											getTopicDetailsInfos(response);
											cRequesttype = true;
											if (forumtopicLists.size() > 0) {
												flag = "ok";
											}
											super.onSuccess(statusCode, headers, response);
										}

										@Override
										public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
											try {
												flag = response.getString("flag");
												if (flag.equals("no")) {
													cRequesttype = true;
													// daoDBImpl.deleteObject(pushRecord.getRecord_id());
													Toast.makeText(NewPushRecordAbsActivity.this, "此内容已被删除",
															Toast.LENGTH_SHORT).show();
												}
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}

										@Override
										public void onFailure(int statusCode, Header[] headers, String responseString,
												Throwable throwable) {
											new ErrorServer(NewPushRecordAbsActivity.this, responseString);
											super.onFailure(statusCode, headers, responseString, throwable);
										}
									});
									new AsyncTask<Void, Void, Void>() {
										@Override
										protected Void doInBackground(Void... params) {
											// TODO Auto-generated method stub
											Long currenttime = System.currentTimeMillis();
											while (!cRequesttype) {
												if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
													cRequesttype = true;
												}
											}
											return null;
										}

										protected void onPostExecute(Void result) {
											if (cRequesttype == true && flag.equals("ok")) {
												flag = "none";
												ylProgressDialog.dismiss();
												Intent intent = new Intent(NewPushRecordAbsActivity.this,CircleDetailActivity.class);
												intent.putExtra("parent", 4);
												intent.putExtra("position", 0);
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												startActivity(intent);
											}
											NewPushRecordAbsActivity.this.finish();
										};
									}.execute();
								}
								return;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					
					}
				}
			});
			pushListView.setOnItemLongClickListener(new OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							PushRecord pushRecord = (PushRecord) NewPushRecordAbsActivity.sPushInfoObjList.get(position);
							int seletStatus = pushRecord.getType();
							curSelectListViewItem = view;
//							pushListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
//										@Override
//										public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
//											// TODO Auto-generated method stub
//											MenuInflater inflater = getMenuInflater();
//											inflater.inflate(R.menu.push_record_context_menu,menu);
//											// menu.add(0,MENU_READ,Menu.NONE,"string");
//										}
//									});
							View contentView=getLayoutInflater().inflate(R.layout.activity_share_popwindow_newpush, null);
							ListView listView=(ListView) contentView.findViewById(R.id.share_pop_repair_lv);
							if(1==seletStatus){//未读
								//arr=new String[]{"标记已读","全部标记已读","删除","删除全部"};
								arr=new String[]{"删除","删除全部"};
							}else{//已读
								//arr=new String[]{"标记未读","全部标记已读","删除","删除全部"};
								arr=new String[]{"删除","删除全部"};
							}
							ArrayAdapter<String> adapter=new ArrayAdapter<String>(NewPushRecordAbsActivity.this, R.layout.activity_share_pop_textview,arr);
							listView.setAdapter(adapter);
							listView.setOnItemClickListener(new listViewListen(position));
							popupWindow=new PopupWindow(NewPushRecordAbsActivity.this);
							popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
							popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
							popupWindow.setBackgroundDrawable(new BitmapDrawable());
							popupWindow.setFocusable(true);
							popupWindow.setOutsideTouchable(true);
							popupWindow.setContentView(contentView);
							popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
							return true;
						}
					});
			if(thread != null){
				thread.interrupt();
				thread = null;
			}
		};
	};

	private void getTopicDetailsInfos(JSONArray response){
		int responseLen = response.length();
		Loger.i("TEST", "json obj length->"+responseLen);
		if(responseLen>0){
		}
		
		
		forumtopicLists = new ArrayList<Object>();
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
				String media_files_json = null;
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
				String topic_type = jsonObject.getString("objectType");  //0.表示一般话题、1.表示活动
				if(topic_type==null || topic_type.isEmpty() || topic_type.length()<=0 || topic_type == "null"){
					topic_type = "0";
				}
				ForumTopic forumTopic = new ForumTopic(this);
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
				forumtopicLists.add(forumTopic);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Loger.i("TEST", "ERROR->"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private String[] getAboutRecordArray(){
		String[] strInfo = new String[2];
		int userType = 0;
		if(App.sUserType<=0){
			AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
			userType = dbImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId)).getUser_type();
			App.sUserType = userType;
		}
		strInfo[0] = String.valueOf(userType);
		long communityId = 0;
		if(App.sFamilyCommunityId<=0){
			AllFamilyDaoDBImpl allFamilyDaoDBImpl = new AllFamilyDaoDBImpl(NewPushRecordAbsActivity.this);
			communityId = allFamilyDaoDBImpl.getCurrentAddrDetail(MainActivity.currentcity+MainActivity.currentvillage+loadAddrdetailPrefrence()).getFamily_community_id();
			App.sFamilyCommunityId = communityId;
		}
		strInfo[1] = String.valueOf(communityId);
		return strInfo;
	}
	
	private String loadAddrdetailPrefrence(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("detail", "未设置");
	}
	
	private void saveNewPushRecordCount(String count){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		if(count==null){
			count = "0";
		}
		sharedata.putString("recordCount", count);
		sharedata.commit();
	}
	
	public class listViewListen implements OnItemClickListener{
		listViewListen(int id){
			menuSelectID=id;
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(arr[arg2].equals("标记已读")){
				popupWindow.dismiss();
				final PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(NewPushRecordAbsActivity.this);
				final PushRecord pushRecord = (PushRecord) NewPushRecordAbsActivity.sPushInfoObjList.get(menuSelectID);
				Loger.i("TEST","当前menuSelectID==》"+menuSelectID);
				if(pushRecord.getType()==1){
					getAboutRecordArray();
					if(pushRecord.getLogin_account()==2001){//标记已读为新闻
						if(App.sUserLoginId<=0){
							AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
							App.sUserLoginId = dbImpl.getUserId();
						}
						daoDBImpl.modifyNewsObjs(2,App.sUserLoginId);
						int newRecordCount = Integer
								.parseInt(App.sNewPushRecordCount);
						if (newRecordCount > 0) {
							newRecordCount--;
						}
						App.sNewPushRecordCount = String.valueOf(newRecordCount);
						saveNewPushRecordCount(App.sNewPushRecordCount);
						MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
						((ImageView) curSelectListViewItem
								.findViewById(R.id.iv_push_info_read_status))
								.setVisibility(View.GONE);
						NewPushRecordAbsActivity.this.finish();
						pushMsgAdapter.notifyDataSetChanged();
						return;
					}else if(pushRecord.getLogin_account()==1001){//标记已读为举报
						try {
							if(App.sUserLoginId<=0){
								AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
								App.sUserLoginId = dbImpl.getUserId();
							}
							JSONObject topicObj = new JSONObject(pushRecord.getContent());		
							Loger.i("TEST", "当前修改的topic==>"+topicObj.getLong("topicId"));
							daoDBImpl.modifyReportObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
						} catch (JSONException e) {
							Loger.i("TEST", "当前修改的举报失败==>"+e.getMessage());
						}
						int newRecordCount = Integer
								.parseInt(App.sNewPushRecordCount);
						if (newRecordCount > 0) {
							newRecordCount--;
						}
						App.sNewPushRecordCount = String.valueOf(newRecordCount);
						saveNewPushRecordCount(App.sNewPushRecordCount);
						MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
						((ImageView) curSelectListViewItem
								.findViewById(R.id.iv_push_info_read_status))
								.setVisibility(View.GONE);
						NewPushRecordAbsActivity.this.finish();
						pushMsgAdapter.notifyDataSetChanged();
						return;
					}else if(pushRecord.getLogin_account()==1010){//标记已读为回复
						try {
							if(App.sUserLoginId<=0){
								AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
								App.sUserLoginId = dbImpl.getUserId();
							}
							JSONObject topicObj = new JSONObject(pushRecord.getContent());		
							Loger.i("TEST", "当前修改的topic==>"+topicObj.getLong("topicId"));
							daoDBImpl.modifyCommentObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
						} catch (JSONException e) {
							Loger.i("TEST", "当前修改的回复失败==>"+e.getMessage());
						}
						int newRecordCount = Integer
								.parseInt(App.sNewPushRecordCount);
						if (newRecordCount > 0) {
							newRecordCount--;
						}
						App.sNewPushRecordCount = String.valueOf(newRecordCount);
						saveNewPushRecordCount(App.sNewPushRecordCount);
						MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
						((ImageView) curSelectListViewItem
								.findViewById(R.id.iv_push_info_read_status))
								.setVisibility(View.GONE);
						NewPushRecordAbsActivity.this.finish();
						pushMsgAdapter.notifyDataSetChanged();
						return;
					}else if(pushRecord.getLogin_account()==1011){//标记已读为打招呼
						try {
							if(App.sUserLoginId<=0){
								AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
								App.sUserLoginId = dbImpl.getUserId();
							}
							JSONObject topicObj = new JSONObject(pushRecord.getContent());		
							Loger.i("TEST", "当前修改的topic==>"+topicObj.getLong("topicId"));
							String sayHelloUserId = topicObj.getString("commentType");
							Loger.i("TEST", "当前打招呼UserId==>"+sayHelloUserId);
							daoDBImpl.modifySayHelloObjs(2, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId),sayHelloUserId);
						} catch (JSONException e) {
							Loger.i("TEST", "当前修改的回复失败==>"+e.getMessage());
						}
						int newRecordCount = Integer
								.parseInt(App.sNewPushRecordCount);
						if (newRecordCount > 0) {
							newRecordCount--;
						}
						App.sNewPushRecordCount = String.valueOf(newRecordCount);
						saveNewPushRecordCount(App.sNewPushRecordCount);
						MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
						((ImageView) curSelectListViewItem
								.findViewById(R.id.iv_push_info_read_status))
								.setVisibility(View.GONE);
						NewPushRecordAbsActivity.this.finish();
						pushMsgAdapter.notifyDataSetChanged();
						return;
					}else{
						pushRecord.setType(2);
						daoDBImpl.modifyObject(pushRecord);
					}
					Loger.i("TEST","当前menuSelectID==》"+menuSelectID);
					Loger.i("TEST","当前pushRecord.get_id()==》"+pushRecord.get_id());
					int newRecordCount = Integer
							.parseInt(App.sNewPushRecordCount);
					if (newRecordCount > 0) {
						newRecordCount--;
					}
					App.sNewPushRecordCount = String.valueOf(newRecordCount);
					saveNewPushRecordCount(App.sNewPushRecordCount);
					MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
					((ImageView) curSelectListViewItem
							.findViewById(R.id.iv_push_info_read_status))
							.setVisibility(View.GONE);
					// NewPushRecordAbsActivity.this.finish();
					pushMsgAdapter.notifyDataSetChanged();
				}
			}
			if(arr[arg2].equals("标记未读")){//标记未读
				popupWindow.dismiss();
				final PushRecordDaoDBImpl daoDBImpl = new PushRecordDaoDBImpl(NewPushRecordAbsActivity.this);
				final PushRecord pushRecord = (PushRecord) NewPushRecordAbsActivity.sPushInfoObjList.get(menuSelectID);
				Loger.i("TEST","当前menuSelectID==》"+menuSelectID);
				if(pushRecord.getType()==2){
					getAboutRecordArray();
					if(pushRecord.getLogin_account()==2001){//标记未读为新闻
						if(App.sUserLoginId<=0){
							AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
							App.sUserLoginId = dbImpl.getUserId();
						}
						daoDBImpl.modifyNewsObjs(1,App.sUserLoginId);
						int newRecordCount = Integer
								.parseInt(App.sNewPushRecordCount);
						newRecordCount++;
						App.sNewPushRecordCount = String.valueOf(newRecordCount);
						saveNewPushRecordCount(App.sNewPushRecordCount);
						MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
						((ImageView) curSelectListViewItem
								.findViewById(R.id.iv_push_info_read_status))
								.setVisibility(View.VISIBLE);
						NewPushRecordAbsActivity.this.finish();
						pushMsgAdapter.notifyDataSetChanged();
						return;
					}else if(pushRecord.getLogin_account()==1001){//标记未读为举报
						try {
							if(App.sUserLoginId<=0){
								AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
								App.sUserLoginId = dbImpl.getUserId();
							}
							JSONObject topicObj = new JSONObject(pushRecord.getContent());		
							Loger.i("TEST", "当前修改的topic==>"+topicObj.getLong("topicId"));
							daoDBImpl.modifyReportObjs(1, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
						} catch (JSONException e) {
							Loger.i("TEST", "当前修改的举报失败==>"+e.getMessage());
						}
						int newRecordCount = Integer
								.parseInt(App.sNewPushRecordCount);
						newRecordCount++;
						App.sNewPushRecordCount = String.valueOf(newRecordCount);
						saveNewPushRecordCount(App.sNewPushRecordCount);
						MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
						((ImageView) curSelectListViewItem
								.findViewById(R.id.iv_push_info_read_status))
								.setVisibility(View.VISIBLE);
						NewPushRecordAbsActivity.this.finish();
						pushMsgAdapter.notifyDataSetChanged();
						return;
					}else if(pushRecord.getLogin_account()==1010){//标记未读为回复
						try {
							if(App.sUserLoginId<=0){
								AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
								App.sUserLoginId = dbImpl.getUserId();
							}
							JSONObject topicObj = new JSONObject(pushRecord.getContent());		
							Loger.i("TEST", "当前修改的topic==>"+topicObj.getLong("topicId"));
							daoDBImpl.modifyCommentObjs(1, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId));
						} catch (JSONException e) {
							Loger.i("TEST", "当前修改的举报失败==>"+e.getMessage());
						}
						int newRecordCount = Integer
								.parseInt(App.sNewPushRecordCount);
						newRecordCount++;
						App.sNewPushRecordCount = String.valueOf(newRecordCount);
						saveNewPushRecordCount(App.sNewPushRecordCount);
						MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
						((ImageView) curSelectListViewItem
								.findViewById(R.id.iv_push_info_read_status))
								.setVisibility(View.VISIBLE);
						NewPushRecordAbsActivity.this.finish();
						pushMsgAdapter.notifyDataSetChanged();
						return;
					}else if(pushRecord.getLogin_account()==1011){//标记未读为打招呼
						try {
							if(App.sUserLoginId<=0){
								AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
								App.sUserLoginId = dbImpl.getUserId();
							}
							JSONObject topicObj = new JSONObject(pushRecord.getContent());		
							Loger.i("TEST", "当前修改的topic==>"+topicObj.getLong("topicId"));
							String sayHelloUserId = topicObj.getString("commentType");
							Loger.i("TEST", "当前打招呼UserId==>"+sayHelloUserId);
							daoDBImpl.modifySayHelloObjs(1, topicObj.getLong("topicId"), String.valueOf(App.sUserLoginId),sayHelloUserId);
						} catch (JSONException e) {
							Loger.i("TEST", "当前修改的举报失败==>"+e.getMessage());
						}
						int newRecordCount = Integer
								.parseInt(App.sNewPushRecordCount);
						newRecordCount++;
						App.sNewPushRecordCount = String.valueOf(newRecordCount);
						saveNewPushRecordCount(App.sNewPushRecordCount);
						MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
						((ImageView) curSelectListViewItem
								.findViewById(R.id.iv_push_info_read_status))
								.setVisibility(View.VISIBLE);
						NewPushRecordAbsActivity.this.finish();
						pushMsgAdapter.notifyDataSetChanged();
					}else{
						pushRecord.setType(1);
						daoDBImpl.modifyObject(pushRecord);
					}
					Loger.i("TEST","当前menuSelectID==》"+menuSelectID);
					Loger.i("TEST","当前pushRecord.get_id()==》"+pushRecord.get_id());
					int newRecordCount = Integer
							.parseInt(App.sNewPushRecordCount);
					newRecordCount++;
					App.sNewPushRecordCount = String.valueOf(newRecordCount);
					saveNewPushRecordCount(App.sNewPushRecordCount);
					MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
					((ImageView) curSelectListViewItem
							.findViewById(R.id.iv_push_info_read_status))
							.setVisibility(View.VISIBLE);
					// NewPushRecordAbsActivity.this.finish();
					pushMsgAdapter.notifyDataSetChanged();
				}
			}
			if(arr[arg2].equals("全部标记已读")){
				popupWindow.dismiss();
				getAboutRecordArray();
				PushRecordDaoDBImpl daoImplAll = new PushRecordDaoDBImpl(NewPushRecordAbsActivity.this);
				for(int i=0;i<NewPushRecordAbsActivity.sPushInfoObjList.size();i++){
					PushRecord recordAll = (PushRecord) NewPushRecordAbsActivity.sPushInfoObjList.get(i);
					if(recordAll.getType()==1){
						recordAll.setType(2);
						daoImplAll.modifyObject(recordAll);
						curSelectListViewItem = pushListView.getAdapter().getView(i, null, pushListView);
						((ImageView) curSelectListViewItem.findViewById(R.id.iv_push_info_read_status)).setVisibility(View.GONE);
					}
				}
				App.sNewPushRecordCount = "0";
				saveNewPushRecordCount(App.sNewPushRecordCount);
				MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
				pushMsgAdapter.notifyDataSetChanged();
//				NewPushRecordAbsActivity.this.finish();
			}
			if(arr[arg2].equals("删除")){
				popupWindow.dismiss();
				getAboutRecordArray();
				final PushRecordDaoDBImpl daoImplDel = new PushRecordDaoDBImpl(NewPushRecordAbsActivity.this);
				final PushRecord pushRecordDel = (PushRecord) NewPushRecordAbsActivity.sPushInfoObjList.get(menuSelectID);
				if (pushRecordDel.getType() == 1) {
					int countDel = Integer.parseInt(App.sNewPushRecordCount);
					if (countDel > 0) {
						countDel--;
					}
					App.sNewPushRecordCount = String.valueOf(countDel);
					saveNewPushRecordCount(App.sNewPushRecordCount);
					MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
				}
				if(pushRecordDel.getLogin_account()==2001){//删除的是新闻
					Loger.i("TEST","删除新闻Item成功！");
					if(App.sUserLoginId<=0){
						AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
						App.sUserLoginId = dbImpl.getUserId();
					}
					daoImplDel.deleteNewsObj("2001", String.valueOf(App.sUserLoginId));
				}else if(pushRecordDel.getLogin_account()==1001){//删除的是举报
					long topicId;
					try {
						if(App.sUserLoginId<=0){
							AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
							App.sUserLoginId = dbImpl.getUserId();
						}
						JSONObject jsonObj = new JSONObject(pushRecordDel.getContent());
						topicId = jsonObj.getLong("topicId");
						daoImplDel.deleteReportObjs(topicId,String.valueOf(App.sUserLoginId));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Loger.i("TEST", "删除失败=>"+e.getMessage());
					}
				}else if(pushRecordDel.getLogin_account()==1010){//删除的是回复
					long topicId;
					try {
						if(App.sUserLoginId<=0){
							AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
							App.sUserLoginId = dbImpl.getUserId();
						}
						JSONObject jsonObj = new JSONObject(pushRecordDel.getContent());
						topicId = jsonObj.getLong("topicId");
						daoImplDel.deleteCommentObjs(topicId,String.valueOf(App.sUserLoginId));
					} catch (JSONException e) {
						Loger.i("TEST", "删除失败=>"+e.getMessage());
					}
				}else if(pushRecordDel.getLogin_account()==1011){//删除的是打招呼
					long topicId;
					try {
						if(App.sUserLoginId<=0){
							AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
							App.sUserLoginId = dbImpl.getUserId();
						}
						JSONObject jsonObj = new JSONObject(pushRecordDel.getContent());
						topicId = jsonObj.getLong("topicId");
						String sayHelloUserId = jsonObj.getString("commentType");
						Loger.i("TEST", "打招呼人的UserId=>"+sayHelloUserId);
						daoImplDel.deleteSayHelloObjs(topicId,String.valueOf(App.sUserLoginId),sayHelloUserId);
					} catch (JSONException e) {
						Loger.i("TEST", "删除失败=>"+e.getMessage());
					}
				}else{
					daoImplDel.deleteObject(pushRecordDel.getRecord_id());
				}
				NewPushRecordAbsActivity.sPushInfoObjList.remove(menuSelectID);
				NewPushRecordAbsActivity.sPushRecordObjSize--;

				pushMsgAdapter.remove(pushMsgAdapter.getItem(menuSelectID));
				pushMsgAdapter.notifyDataSetChanged();
			}
			if(arr[arg2].equals("删除全部")){
				popupWindow.dismiss();
				getAboutRecordArray();
				if(App.sUserLoginId<=0){
					AccountDaoDBImpl dbImpl = new AccountDaoDBImpl(NewPushRecordAbsActivity.this);
					App.sUserLoginId = dbImpl.getUserId();
				}
				PushRecordDaoDBImpl daoImplDelAll = new PushRecordDaoDBImpl(
						NewPushRecordAbsActivity.this);
				daoImplDelAll.deleteAllObjects(String.valueOf(App.sUserLoginId));
				App.sNewPushRecordCount = "0";
				saveNewPushRecordCount(App.sNewPushRecordCount);
				MainActivity.setnewMessageTextView(App.sNewPushRecordCount);
				NewPushRecordAbsActivity.sPushInfoObjList.clear();
				NewPushRecordAbsActivity.sPushRecordObjSize = 0;
				pushMsgAdapter.clear();
				pushMsgAdapter.notifyDataSetChanged();
				NewPushRecordAbsActivity.this.finish();
			}
		}
	}
	
	private void clearReportRes(){
		ReportDetailActivity.sReportDetailActivity = null;
		ReportDetailActivity.reportTopicId = -1;
		try {
			ReportDetailActivity.sReportDetailActivity.findViewById(R.id.ll_into_detail_page).setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		NewPushRecordAbsActivity.sNewPushRecordAbsContext = null;
		super.onDestroy();
	}
}
