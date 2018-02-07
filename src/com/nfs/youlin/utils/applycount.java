package com.nfs.youlin.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoActivity;
import com.nfs.youlin.activity.personal.CollectionActivity;
import com.nfs.youlin.activity.personal.MyPushActivity;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.signcalendar.Signmajoractivity;
import com.umeng.analytics.MobclickAgent;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class applycount extends Activity{
	private Intent intent;
	private boolean cRequestSet = false;
	private String flag = "none";
	public static List<JSONObject> jsonObjList;
	private Button applyyes;
	private Button applycancel;
	private EditText hunmancount;
	private EditText childrencount;
	private List<Object> forumtopicLists ;
	private FriendCircleAdapter currentadapter;
	private int applycount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.myapply);
		applyyes = (Button)findViewById(R.id.applyok);
		applycancel = (Button)findViewById(R.id.applycancel);
		hunmancount = (EditText)(findViewById(R.id.humancount).findViewById(R.id.et01));
		childrencount = (EditText)(findViewById(R.id.childrencount).findViewById(R.id.et01));
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
//		p.height = (int) (d.getHeight() * 0.28);
		 //高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.84); //宽度设置为屏幕的0.8
		p.x = 0;
		p.y = 0;
		getWindow().setAttributes(p);
		intent = this.getIntent();
		final int position = intent.getIntExtra("position", 0);
		//final int totalcount = intent.getIntExtra("totalcount",0);
		final int parentclass = intent.getIntExtra("parent", 0);
		if(parentclass==1){
			forumtopicLists = FriendCircleFragment.allList;
			currentadapter = FriendCircleFragment.maddapter;
		}else if(parentclass==0){
			forumtopicLists = TitleBarSearchActivity.forumtopicLists;
			currentadapter = TitleBarSearchActivity.maddapter;
		}else if(parentclass==2){
			forumtopicLists = MyPushActivity.forumtopicLists;
			currentadapter = MyPushActivity.maddapter;
		}else if(parentclass==3){
			forumtopicLists = CollectionActivity.forumtopicLists;
			currentadapter = CollectionActivity.maddapter;
		}else if(parentclass==4){
			//管理员接受举报
			forumtopicLists = NewPushRecordAbsActivity.forumtopicLists;
			currentadapter = new FriendCircleAdapter(forumtopicLists,this, 0, 4);
		}else if(parentclass==5){
			//从细节进来的
			final int grandfatherclass = intent.getIntExtra("grandfather", 0);
			if(grandfatherclass==1){
				forumtopicLists = FriendCircleFragment.allList;
				currentadapter = FriendCircleFragment.maddapter;
			}else if(grandfatherclass==0){
				forumtopicLists = TitleBarSearchActivity.forumtopicLists;
				currentadapter = TitleBarSearchActivity.maddapter;
			}else if(grandfatherclass==2){
				forumtopicLists = MyPushActivity.forumtopicLists;
				currentadapter = MyPushActivity.maddapter;
			}else if(grandfatherclass==3){
				forumtopicLists = CollectionActivity.forumtopicLists;
				currentadapter = CollectionActivity.maddapter;
			}else if(grandfatherclass==4){
				forumtopicLists = NewPushRecordAbsActivity.forumtopicLists;
				currentadapter = new FriendCircleAdapter(forumtopicLists,this, 0, 4);
			}
		}else if(parentclass==6){
			forumtopicLists = PropertyGonggaoActivity.forumtopicLists;
			currentadapter = PropertyGonggaoActivity.maddapter;
		}
		applyyes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Loger.d("test4", hunmancount.getText().toString());
				Loger.d("test4", childrencount.getText().toString());
			//	final int applycount = Integer.parseInt(hunmancount.getText().toString())+Integer.parseInt(childrencount.getText().toString());
				if(Integer.parseInt(hunmancount.getText().toString())+Integer.parseInt(childrencount.getText().toString())>0){
					try {
						JSONArray Object_data = new JSONArray(((ForumTopic)forumtopicLists.get(position)).getObject_data());
						gethttpdata(Long.parseLong(((JSONObject) Object_data.get(0)).get("activityId").toString()),App.sUserLoginId,
								Integer.parseInt(hunmancount.getText().toString()), Integer.parseInt(childrencount.getText().toString()),
								IHttpRequestUtils.YOULIN, 1);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					Toast.makeText(applycount.this, "请填写报名人数", Toast.LENGTH_SHORT).show();cRequestSet = true;
				}
				new AsyncTask<Void, Void, Void>(){
					@Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						Long currenttime = System.currentTimeMillis();
						while(!cRequestSet){
							if((System.currentTimeMillis()-currenttime)>App.WAITFORHTTPTIME){
								cRequestSet = true;
							}
						}
						return null;
					}
					protected void onPostExecute(Void result) {
						if(cRequestSet == true && flag.equals("ok")){
							flag = "none";
							cRequestSet = false;
//							FriendCircleAdapter.flag = "ok";
//							FriendCircleAdapter.cRequestSet = true;
//							FriendCircleAdapter.setapplcount(String.valueOf(applycount),"取消报名");
							JSONArray Object_data;
							try {
								Object_data = new JSONArray(((ForumTopic)currentadapter.data.get(position)).getObject_data());
								((JSONObject) Object_data.get(0)).put("enrollTotal",String.valueOf(applycount));
								((JSONObject) Object_data.get(0)).put("enrollFlag","true");
								((ForumTopic)currentadapter.data.get(position)).setObject_data(Object_data.toString());
								Loger.d("test4",Object_data.toString());	
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(parentclass == 5){
								Intent data = new Intent();
								data.putExtra("totalcount", String.valueOf(applycount));
								setResult(RESULT_OK,data);
							}
							if(currentadapter!=null){
								currentadapter.notifyDataSetChanged();
							}
							applycount.this.finish();
						}
						cRequestSet = false;
					}
				}.execute();
			}
		});
	applycancel.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			applycount.this.finish();
		}
	});

		
	}
	private void gethttpdata(long activityId,long user_id ,int humanbeingnum,int childrennum,String http_addr,int request_index){
		/***********************************************/
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		final int position = request_index;
			Loger.d("test3", "3333333333333333333333");
		
			if(request_index ==1){
					params.put("activityId", activityId);
					params.put("userId", user_id);
					params.put("enrollUserCount", humanbeingnum);
					params.put("enrollNeCount", childrennum);
					params.put("tag", "enroll");
					params.put("apitype", IHttpRequestUtils.APITYPE[5]);
			}
		
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
					Loger.i("test4", "set applycount flag->" + flag);
					if(flag.equals("ok")){
						cRequestSet = true;
						applycount = Integer.parseInt(jsonContext.getString("count"));
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
				jsonObjList.clear();
				try {
					org.json.JSONObject obj = null;
					for (int i = 0; i < jsonContext.length(); i++) {
						jsonObjList.add(jsonContext.getJSONObject(i));
						obj = jsonContext.getJSONObject(i);
						Loger.d("test3", ""+obj.getString("pk"));
					}
					cRequestSet = true;
				} catch (org.json.JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST","OK(error)->" + e.getMessage());
					cRequestSet = false;
					e.printStackTrace();
					}
					super.onSuccess(statusCode, headers, response);
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					cRequestSet = false;
					jsonObjList.clear();
					new ErrorServer(applycount.this, responseString);
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});
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
}
