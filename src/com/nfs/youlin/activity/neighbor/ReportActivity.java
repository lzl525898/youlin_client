package com.nfs.youlin.activity.neighbor;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.service.NetworkService;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.umeng.analytics.MobclickAgent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ReportActivity extends Activity {
	long topicId;
	long communityId;
	long senderId;
	String contentStr;
	String typeStr;
	String[] arr;
	Map<Integer,Boolean> reportMap=new HashMap<Integer,Boolean>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		ActionBar actionBar=getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("举报");
		Intent intent=getIntent();
		topicId=(Long) intent.getBundleExtra("ID").get("topic_id");
		communityId=(Long) intent.getBundleExtra("ID").get("community_id");
		senderId=(Long) intent.getBundleExtra("ID").get("sender_id");
		Loger.i("youlin","topic_id+community_id+sender_id-->"+topicId+"  "+communityId+"  "+senderId);
		arr=new String[]{"敏感信息","版权问题","暴力色情","诈骗和虚假信息","骚扰","其他"};
		ReportAdapter adapter=new ReportAdapter();
		final ListView listView=(ListView) findViewById(R.id.report_lv);
		listView.setAdapter(adapter);
		final EditText reportEt=(EditText)findViewById(R.id.report_et);
		reportEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
			}
		});
		Button reportBn=(Button)findViewById(R.id.report_bn);
		reportBn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				contentStr=reportEt.getText().toString();
				if(NetworkService.networkBool){
					if(typeStr!=null){
					RequestParams reportParams = new RequestParams();
					reportParams.put("topic_id", topicId);
					reportParams.put("community_id", communityId);
					reportParams.put("complain_id", App.sUserLoginId);
					reportParams.put("sender_id", senderId);
					reportParams.put("content", contentStr);
					reportParams.put("title", typeStr);
					reportParams.put("tag", "report");
					reportParams.put("apitype", IHttpRequestUtils.APITYPE[5]);
					AsyncHttpClient delRequest = new AsyncHttpClient();
					delRequest.post(IHttpRequestUtils.URL + IHttpRequestUtils.YOULIN,
							reportParams, new JsonHttpResponseHandler(){
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
						    String reportFlag = null;
							try {
								reportFlag = response.getString("flag");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						    if("ok".equals(reportFlag)){
						    	Toast.makeText(ReportActivity.this, "举报成功", Toast.LENGTH_SHORT).show();
						    }else if("none".equals(reportFlag)){
						    	Toast.makeText(ReportActivity.this, "此帖已不可见", Toast.LENGTH_SHORT).show();
						    }
							super.onSuccess(statusCode, headers, response);
						}
						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							new ErrorServer(ReportActivity.this, responseString.toString());
							super.onFailure(statusCode, headers, responseString, throwable);
						}
					});
					finish();
					}else{
						Toast.makeText(ReportActivity.this, "举报原因不能为空", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(ReportActivity.this, "请先开启网络", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
	}
	public class ReportAdapter extends BaseAdapter{
		public ReportAdapter() {
			// TODO Auto-generated constructor stub
			for (int i = 0; i < arr.length; i++) {
				reportMap.put(i, false);
			}
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arr.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView=LayoutInflater.from(ReportActivity.this).inflate(R.layout.list_report_item, null);
			RelativeLayout reportLayout=(RelativeLayout) convertView.findViewById(R.id.list_report_layout);
			TextView reportTv=(TextView) convertView.findViewById(R.id.list_report_tv);
			final ImageView reportImg=(ImageView)convertView.findViewById(R.id.list_report_img);
			reportTv.setText(arr[position]);
			reportLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					for(int key:reportMap.keySet()){
						reportMap.put(key, false);
					}
					typeStr=arr[position];
					reportImg.setBackgroundResource(R.drawable.btn_sex_duihao);
					reportMap.put(position, true);
					ReportAdapter.this.notifyDataSetChanged();
				}
			});
			if(reportMap.get(position)==null || reportMap.get(position)==false){
				reportImg.setBackgroundColor(Color.parseColor("#00000000"));
			}else{
				reportImg.setBackgroundResource(R.drawable.btn_sex_duihao);
			}
			return convertView;
		}
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
