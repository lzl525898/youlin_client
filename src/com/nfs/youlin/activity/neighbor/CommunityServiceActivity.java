package com.nfs.youlin.activity.neighbor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.R.layout;
import com.nfs.youlin.R.menu;
import com.nfs.youlin.activity.personal.write_address;
import com.nfs.youlin.adapter.communityserviceadapter;
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

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.R.anim;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CommunityServiceActivity extends Activity {
	private List<String> nameList=new ArrayList<String>();
	private List<String> phoneList=new ArrayList<String>();
	private List<String> addressList=new ArrayList<String>();
	private List<String> timeList=new ArrayList<String>();
	private List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
	private boolean bRequestSet = false;
	public static List<JSONObject> jsonObjList;
	private String flag = "";
	private communityserviceadapter adapter;
	private TextView notice_text;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_community_service);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("社区服务");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		notice_text = (TextView) findViewById(R.id.community_service_notice);
		adapter=new communityserviceadapter(this, getData(list), R.layout.list_committee_item1, new String[]{"name","phone","address","time"},
				new int[]{R.id.name,R.id.phone2,R.id.address2,R.id.time2});
		ListView listView=(ListView) findViewById(R.id.community_service_lv);
		listView.setAdapter(adapter);
//		listView.setOnItemClickListener(new ListOnclickListen());
		if (NetworkService.networkBool) {
			gethttpdata("community_id", String.valueOf(App.sFamilyCommunityId),
					IHttpRequestUtils.YOULIN);
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					// 网络请求 注册地址 参数{city,village,rooft,number} login_account
					Long currenttime = System.currentTimeMillis();
					while (!bRequestSet) {
						if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
							bRequestSet = true;
						}
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					Loger.d("test3", "onPostExecute111111111111111");
					if (bRequestSet == true && !flag.equals("")&& !(flag == null) && flag.equals("ok")) {
						notice_text.setVisibility(View.GONE);
						nameList.clear();
						phoneList.clear();
						addressList.clear();
						timeList.clear();
						if(jsonObjList.size()>0){
							try {
								for (int i = 0; i < jsonObjList.size(); i++) {
									nameList.add(jsonObjList.get(i).getString("service_department").toString());
									phoneList.add(jsonObjList.get(i).getString("service_phone").toString());
									addressList.add(jsonObjList.get(i).getString("service_address").toString());
									timeList.add(jsonObjList.get(i).getString("service_office_hours").toString());
									getData(list);
									adapter.notifyDataSetChanged();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							notice_text.setVisibility(View.VISIBLE);
							notice_text.setText("没有相应的服务数据！");
						}
					}
				}
			}.execute();
		} else {
		}
//		nameList.add("居委会");
//		nameList.add("派出所");
//		nameList.add("卫生室");
//		phoneList.add("1234567891");
//		phoneList.add("1234567892");
//		phoneList.add("1234567893");
//		addressList.add("居委会地址");
//		addressList.add("派出所地址");
//		addressList.add("卫生室地址");
//		timeList.add("周一至周五");
//		timeList.add("周一至周五");
//		timeList.add("周一至周五");

	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.community, menu);
//		return true;
//	}
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

	public class ListOnclickListen implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ phoneList.get(arg2)));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

	}
	
	List<Map<String,Object>> getData(List<Map<String, Object>> list){
		list.clear();
		for (int i = 0; i < nameList.size(); i++) {
			Map<String, Object> map =new HashMap<String, Object>();
			map.put("name",nameList.get(i));
			map.put("phone",phoneList.get(i));
			map.put("address", addressList.get(i));
			map.put("time", timeList.get(i));
			list.add(map);
		}
		return list;
	}
	private void gethttpdata(String request_index,String request_name,String http_addr){
		/***********************************************/
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		params.put(request_index, request_name);
		params.put("tag", "loadsrv");
		params.put("apitype", IHttpRequestUtils.APITYPE[1]);
		AsyncHttpClient client = new AsyncHttpClient();
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
					Loger.i("test3", "flag->" + flag);
					bRequestSet = true;
				} catch (org.json.JSONException e) {
					e.printStackTrace();
					bRequestSet = false;
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
				Loger.d("test3", jsonContext.toString());
				jsonObjList.clear();
				try {
					for (int i = 0; i < jsonContext.length(); i++) {
						jsonObjList.add(jsonContext.getJSONObject(i));
					}
					bRequestSet = true;
					flag = "ok";
				} catch (org.json.JSONException e) {
					// TODO Auto-generated catch block
					Loger.i("TEST","OK(error)->" + e.getMessage());
					bRequestSet = false;
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
					new ErrorServer(CommunityServiceActivity.this, responseString.toString());
					jsonObjList.clear();
					bRequestSet = false;
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

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
