package com.nfs.youlin.activity.personal;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.neighbor.FriendCircleFragment;
import com.nfs.youlin.activity.neighbor.PropertyAdviceActivity;
import com.nfs.youlin.activity.neighbor.PropertyGonggaoActivity;
import com.nfs.youlin.activity.neighbor.PropertyRepairList;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class Collection_delensure extends Activity{
	private Button ok;
	private Button cancel;
	private long topicid;
	private long communityid;
	private int listposition;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collection_deled);
		Intent intent = getIntent();
		communityid = intent.getLongExtra("sendercommunityid", -1);
		topicid = intent.getLongExtra("topicid", -1);
		listposition = intent.getIntExtra("position", -1);
	     WindowManager m = getWindowManager();
			Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
			android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
//			p.height = (int) (d.getHeight() * 0.5);
//			p.height = LayoutParams.WRAP_CONTENT;
			 //高度设置为屏幕的1.0
			p.width = (int) (d.getWidth() * 0.80); //宽度设置为屏幕的0.8
			p.x = 0;
			p.y = 0;
			getWindow().setAttributes(p);
			cancel = (Button)findViewById(R.id.canceldelcol);
			ok = (Button)findViewById(R.id.okdelcol);
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					gethttpdata2(App.sUserLoginId,communityid, topicid,IHttpRequestUtils.YOULIN, 1);
				}
			});
			
	}
	private void gethttpdata2(long user_id,long community_id,long topic_id ,String http_addr,int request_index){
		/***********************************************/
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		final int position = request_index;
		Loger.d("test3", "user_id="+user_id+"community_id="+community_id+"topic_id="+topic_id);
			if(request_index ==1){
					params.put("user_id", user_id);
					params.put("community_id", community_id);
					params.put("topic_id", topic_id);
					params.put("tag", "delmycol");
					params.put("apitype", IHttpRequestUtils.APITYPE[5]);
		}
		client.post(IHttpRequestUtils.URL+http_addr,params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				Loger.i("test4", "del collection response->" + response.toString());
				String flag;
				try {
					flag = jsonContext.getString("flag");
					if(flag.equals("ok")){
//						forumTopic.setSender_nc_role(0);
//						if(parentclass==3){
//							CollectionActivity.forumtopicLists.set(listposition, forumTopic);
						Collection_delensure.this.finish();
						CollectionActivity.forumtopicLists.remove(listposition);
						CollectionActivity.maddapter.notifyDataSetChanged();
//						}
						Toast.makeText(Collection_delensure.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(Collection_delensure.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
					}

				} catch (org.json.JSONException e) {
					e.printStackTrace();
					Toast.makeText(Collection_delensure.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
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
				public void onFailure(int statusCode,org.apache.http.Header[] headers,String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					new ErrorServer(Collection_delensure.this, responseString.toString());
					super.onFailure(statusCode, headers,responseString, throwable);
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
