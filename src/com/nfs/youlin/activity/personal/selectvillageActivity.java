package com.nfs.youlin.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import cn.jpush.android.api.JPushInterface;

import com.google.zxing.common.detector.WhiteRectangleDetector;
import com.nfs.youlin.R;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.YLProgressDialog;
import com.nfs.youlin.view.number_fragment;
import com.nfs.youlin.view.rooft_fragment;
import com.umeng.analytics.MobclickAgent;
public class selectvillageActivity extends Activity{
	public String selectedcity;
	private String selectedcitycode;
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
	public static List<Map<String, Object>> buildnumlist = new ArrayList<Map<String,Object>>();
	private ListView villagelist;
	private Thread requestTHttphgread;
	public static List<JSONObject> jsonObjList;
	private boolean bRequestSet = false;
	private final int REQUEST_OK = 101;
	private final int REQUEST_NO = 102;
	private YLProgressDialog ylProgressDialog;
	private Intent resultintent;
	void buildprocessdialog(){
		ylProgressDialog = YLProgressDialog.createDialogwithcircle(this,"加载中...",0);
//		pd1 = new ProgressDialog(this);
//		pd1.setTitle("正在刷新地址列表");
//		pd1.setMessage("正在玩命为您加载，请稍侯...");
//		pd1.setCancelable(false);
//		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		pd1.setIndeterminate(false);
	} 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Loger.d("hyytest", "selectvillageActivity oncreate");
		this.setContentView(R.layout.selectvillage);
		Intent intent = getIntent();
		selectedcity = intent.getStringExtra("selectedcity");
		//selectedcitycode = intent.getStringExtra("selectedcityCode");
		Loger.d("test3", selectedcity);
		Button villageback = (Button)findViewById(R.id.villagebackbutton);
		Button cityselected = (Button)findViewById(R.id.cityposition);
		villagelist = (ListView)findViewById(R.id.choicevillage);
//        Drawable drawable1 = getResources().getDrawable(R.drawable.nav_fanhui_xin_tiao);
//        drawable1.setBounds(0, 1, 28, 46);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
//        villageback.setCompoundDrawables(drawable1, null, null, null);//只放左边
		cityselected.setText(selectedcity.toString());
		villageback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CommonTools.goBack();
			}
		});
		list.clear();
		buildnumlist.clear();
		buildprocessdialog();
		gethttpdata("city_name",selectedcity,IHttpRequestUtils.YOULIN);
		requestTHttphgread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ylProgressDialog.show();
					}
				});
				Long currenttime = System.currentTimeMillis();
				while (!bRequestSet) {
					if ((System.currentTimeMillis() - currenttime) > 4000) {
						bRequestSet = true;
					}
				}
				Message msg = new Message();
				msg.what = REQUEST_OK;
				handler.sendMessage(msg);
				return;
			}
		});
		requestTHttphgread.start();
		/************************************************/
//		final String villagename[] = new String[]{"xxxxxxx1小区","xxxxxxx2小区","xxxxxxx3小区","xxxxxxx4小区","xxxxxxx5小区"};
//		String streetname[] = new String[]{"xxxxx街道xxxx号","xxxxx街道xxxx号","xxxxx街道xxxx号","xxxxx街道xxxx号","xxxxx街道xxxx号"};
		
	}
//	  private List<Map<String, Object>> getData(String[] strs,String[] strs2) {  
//	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
//	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//	        for (int i = 0; i < strs.length; i++) {  
//	            Map<String, Object> map = new HashMap<String, Object>();  
//	            map.put("village", strs[i]); 
//	            map.put("street",strs2[i]);
//	            list.add(map);  
//	              
//	        }  
//	          
//	        return list;  
//	    } 
	private void gethttpdata(String request_index,String request_name,String http_addr){
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		params.put(request_index, request_name);
		params.put("tag", "addr");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				String flag = null;
				String community_id = null;
				String community_name = null;
				try {
					flag = jsonContext.getString("flag");
					Loger.i("TEST", "flag->" + flag);
					Loger.i("TEST", "community_name:"+community_name);
					bRequestSet = true;
				} catch (org.json.JSONException e) {
					e.printStackTrace();
					bRequestSet = true;
					Loger.i("TEST","JSONObject->"+ e.getMessage());
				}
				super.onSuccess(statusCode, headers,response);
			}
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONArray response) {
				// TODO Auto-generated method stub
				org.json.JSONArray jsonContext = response;
				Loger.d("TEST", jsonContext.toString());
				jsonObjList.clear();
				try {
//					String json = null;
//					String city_id = null;
//					String community_name = null;
//					String community_id = null;
					org.json.JSONObject obj = null;
					for (int i = 0; i < jsonContext.length(); i++) {
						jsonObjList.add(jsonContext.getJSONObject(i));
						obj = jsonContext.getJSONObject(i);
//						community_id = obj.getString("pk");
//						community_name = obj.getJSONObject("fields").getString("community_name");
//						city_id = obj.getJSONObject("fields").getString("city");
//						json = community_id + community_name + city_id + "\r\n";
						Loger.d("TEST", ""+obj.getString("pk"));
					}
					bRequestSet = true;
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
					Loger.i("TEST",
							responseString + "\r\n" + throwable.toString()
							+"\r\n-----\r\n" + statusCode);
					jsonObjList.clear();
					bRequestSet = false;
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	  private List<Map<String, Object>> AddData(List<Map<String, Object>> oldlist,String strname,String strid,String itemname,String itemid){
		  Map<String, Object> map = new HashMap<String, Object>();  
          map.put("name", strname); 
          map.put("_id", strid);
          map.put("block_name", itemname);
          map.put("block_id", itemid);
          map.put("street",strid);
          oldlist.add(map);
          return oldlist;
	  }
	  private List<Map<String, Object>> AddData(List<Map<String, Object>> oldlist,String strname,String strid){
		  Map<String, Object> map = new HashMap<String, Object>();  
          map.put("name", strname); 
          map.put("_id", strid);
          map.put("street",strid);
          oldlist.add(map);
          return oldlist;
	  }
	  private Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case REQUEST_OK:
					if(requestTHttphgread!=null){
						requestTHttphgread.interrupt();
						requestTHttphgread = null;
					}
					bRequestSet = false;
					ylProgressDialog.dismiss();
					/*	obj = jsonContext.getJSONObject(i);
					community_id = obj.getString("pk");
					community_name = obj.getJSONObject("fields").getString("community_name");
					city_id = obj.getJSONObject("fields").getString("city");
					json = community_id + community_name + city_id + "\r\n";*/
					Loger.i("test3","jsonObjList->"+jsonObjList.size());
					for(int i=0 ;i<jsonObjList.size();i++){
						if(i == 0){
							try {
								write_address.city_code = jsonObjList.get(i).getJSONObject("fields").getString("city_code");
								write_address.city_id = jsonObjList.get(i).getString("pk");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else{
							try {
								String villagename = jsonObjList.get(i).getString("community_name");
								String blockid = jsonObjList.get(i).getString("block_id");
								String communityid = jsonObjList.get(i).getString("pk");
								String blockname = "";
								if(!blockid.equals("0")){
									blockname = jsonObjList.get(i).getString("block_name");
								}
								Loger.d("test3", "villagename="+villagename+"communityid="+communityid+"city_id"+write_address.city_id);
								AddData(list, villagename,communityid,blockname,blockid);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
				
					final SimpleAdapter villageadapter = new SimpleAdapter(selectvillageActivity.this, list, R.layout.villagelist,
							new String[] { "name", "street" }, 
							new int[] { R.id.villagename,
							R.id.streetname });
					
					villagelist.setAdapter(villageadapter);
					villagelist.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {

							// TODO Auto-generated method stub
							Loger.d("test3",  ((Map)villageadapter.getItem(position)).get("name") +"被选中");
							//((Map)villageadapter.getItem(position)).get("_id").toString();
							resultintent = selectvillageActivity.this.getIntent();
							resultintent.putExtra("village", ((Map)villageadapter.getItem(position)).get("name").toString());
							resultintent.putExtra("village_id", ((Map)villageadapter.getItem(position)).get("_id").toString());
							resultintent.putExtra("block_id", ((Map)villageadapter.getItem(position)).get("block_id").toString());
							resultintent.putExtra("block_name", ((Map)villageadapter.getItem(position)).get("block_name").toString());
							if(((Map)villageadapter.getItem(position)).get("block_id").toString().equals("0"))
								gethttpdata("community_id",((Map)villageadapter.getItem(position)).get("_id").toString(),
									IHttpRequestUtils.YOULIN);
							else
								gethttpdata("block_id",((Map)villageadapter.getItem(position)).get("block_id").toString(),
										IHttpRequestUtils.YOULIN);
							requestTHttphgread = new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											ylProgressDialog.show();
										}
									});
									Long currenttime = System.currentTimeMillis();
									while (!bRequestSet) {
										if ((System.currentTimeMillis() - currenttime) > 4000) {
											bRequestSet = true;
										}
									}
									Message msg = new Message();
									msg.what = REQUEST_OK;
									buildhandler.sendMessage(msg);
									return;
								}
							});
							requestTHttphgread.start();

						}
					});
					
					break;
				case REQUEST_NO:
					if(requestTHttphgread!=null){
						requestTHttphgread.interrupt();
						requestTHttphgread = null;
					}
					bRequestSet = false;
					jsonObjList.clear();
					break;
				default:
					break;
				}
			}
		};
		 private Handler buildhandler = new Handler(){
				/* (non-Javadoc)
				 * @see android.os.Handler#handleMessage(android.os.Message)
				 */
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case REQUEST_OK:{
						Loger.i("test3","jsonObjList->"+jsonObjList.size());
						ylProgressDialog.dismiss();
						if(jsonObjList.size()<0){
							break;
						}
						buildnumlist.clear();
						for(int i=0 ;i<jsonObjList.size();i++){
							
							try {
								String buildingname = jsonObjList.get(i).getJSONObject("fields").getString("building_name");
								String buildingid = jsonObjList.get(i).getString("pk");
								Loger.d("test3", "buildingname="+buildingname+"buildingid="+buildingid);
								AddData(buildnumlist, buildingname, buildingid);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Loger.i("test3","555555555555555");
								e.printStackTrace();
							}
						}

						Loger.d("test3","hyyhyyhyytest3");
						selectvillageActivity.this.setResult(RESULT_OK, resultintent);
						finish();
					}
						break;
					default:
						break;
					}
				}
		 };
		 
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
