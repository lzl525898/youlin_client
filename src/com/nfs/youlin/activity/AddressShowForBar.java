package com.nfs.youlin.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


import com.nfs.youlin.R;
import com.nfs.youlin.activity.personal.addressseting;
import com.nfs.youlin.activity.personal.addressshow;
import com.nfs.youlin.activity.titlebar.barter.BarterDedailCommentActivity;
import com.nfs.youlin.adapter.Baraddresslistadapter;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.dao.NeighborDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.AllFamily;
import com.nfs.youlin.entity.Neighbor;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.push.YLPushTagManager;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.nfs.youlin.view.YLProgressDialog;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.jpush.android.api.JPushInterface;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AddressShowForBar extends Activity{
	protected static final int REQUEST_OK = 100;
	private  int currentindex = 0;
	List<Map<String,Object>> address_show = new ArrayList<Map<String,Object>>();
	private TextView nameandaddress;
	private ListView addresslistview;
	private ListView buttonlistview;
	public static List<JSONObject> jsonObjList;
	private AllFamilyDaoDBImpl allFDaoDBImpl;
	private boolean bRequestSet = false;
	private boolean cRequestSet = false;
	private Thread requestTHttphgread;
//	private  ProgressDialog pd1;
	private YLProgressDialog ylProgressDialog;
	private Neighbor neighbor;
	private NeighborDaoDBImpl aNeighborDaoDBImpl;
	private List<Map<String,Object>> neighborlist  = new ArrayList<Map<String,Object>>();
	private int REQUEST_NEIGHBOR = 1;
	private int REQUEST_SETCURRENTADDR = 2;
	private String flag = null;
	void buildprocessdialog(){
		ylProgressDialog = YLProgressDialog.createDialogwithcircle(this,"加载中...",0);
//		pd1 = new ProgressDialog(this);
//		pd1.setTitle("正在刷新邻居列表");
//		pd1.setMessage("正在玩命为您加载，请稍侯...");
//		pd1.setCancelable(false);
//		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		pd1.setIndeterminate(false);
	} 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.addressshowforbar);

		nameandaddress = (TextView) findViewById(R.id.nameaddress);
		addresslistview = (ListView) findViewById(R.id.address_list_bar);
		buttonlistview = (ListView) findViewById(R.id.address_list_button);
		/****************** database ***********************/
		allFDaoDBImpl = new AllFamilyDaoDBImpl(this);
		final Account account = new Account(this);
		final List<Object> familyObjs = allFDaoDBImpl
				.findPointTypeObject(App.sUserLoginId);
		/****************** database ***********************/
		buildprocessdialog();
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		// p.height = (int) (d.getHeight() * 0.1 * (familyObjs.size()+2));
		// //高度设置为屏幕的1.0
		// p.width = (int) (d.getWidth() * 0.8); //宽度设置为屏幕的0.8
		p.x = 0;
		p.y = 0;
		getWindow().setAttributes(p);
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int height = wm.getDefaultDisplay().getHeight();

		final AccountDaoDBImpl accountdao = new AccountDaoDBImpl(this);

		if (MainActivity.currentvillage.equals("未设置"))
			nameandaddress.setText("您还没有设置地址");
		else{
			String subvillage;
			if(MainActivity.currentvillage.length()>8){
				subvillage = MainActivity.currentvillage.substring(0, 8)+"...";
			}else{
				subvillage =  MainActivity.currentvillage;
			}
			nameandaddress.setText(this.loadUsernamePrefrence() + "@"
					+ subvillage);
		}
			
		address_show = getData(familyObjs);
		Loger.d("test4", "set address size " + address_show.size());
		final Baraddresslistadapter adapter = new Baraddresslistadapter(this,
				address_show, R.layout.singlechooselist, new String[] {
						"address_text", "check_flag", "verify_flag" },
				new int[] { R.id.address_show });
		addresslistview.setAdapter(adapter);
		if ((height / 45) * (familyObjs.size() * 4 + 1) < height * 4 / 5)
			addresslistview.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, (height / 45)
							* (familyObjs.size() * 4 + 1)));
		else
			addresslistview.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, height * 4 / 5));
		SimpleAdapter adapter1 = new SimpleAdapter(this,
				getData(new String[] { "地址信息" }), R.layout.list_button,
				new String[] { "name" }, new int[] { R.id.address_set });
		buttonlistview.setAdapter(adapter1);
		buttonlistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(AddressShowForBar.this);
				List<Object> familyObjs = allFDaoDBImpl.findPointTypeObject(App.sUserLoginId);
				int length =familyObjs.size();
				if(length==0 ){
					Intent intent = new Intent(AddressShowForBar.this,addressseting.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
					finish();
				}else if(length > 0){
					Intent addressintent = new Intent(AddressShowForBar.this,addressshow.class);
					addressintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					AddressShowForBar.this.startActivity(addressintent);
					finish();
				}
			}
		});
		addresslistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Loger.d("test5", "set address position =" + position
						+ "currentindex=" + currentindex);
				if(((AllFamily) familyObjs.get(position)).getPrimary_flag() == 1){
					finish();
					return;
				}
				final long block_id = ((AllFamily) familyObjs.get(position))
						.getFamily_block_id();
				final long family_id = ((AllFamily) familyObjs.get(position))
						.getFamily_id();
				int old_ne_status = ((AllFamily) familyObjs.get(currentindex))
						.getNe_status();
				final String old_family_address = ((AllFamily) familyObjs.get(currentindex))
						.getFamily_address();
				final AllFamily old_family = (AllFamily) familyObjs.get(currentindex);
				int ne_status = ((AllFamily) familyObjs.get(position))
						.getNe_status();

				gethttpdata(block_id, family_id, ne_status, App.sFamilyBlockId,
						App.sFamilyId, old_ne_status,
						IHttpRequestUtils.YOULIN, position);

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
							Loger.d("test3", "set address currentindex="
									+ currentindex);
							cRequestSet = false;
							flag = "";
							MainActivity.currentcity = ((AllFamily) familyObjs
									.get(currentindex)).getFamily_city();
							MainActivity.currentvillage = ((AllFamily) familyObjs
									.get(currentindex)).getFamily_community();
							App.sFamilyId = ((AllFamily) familyObjs
									.get(currentindex)).getFamily_id();
							App.sFamilyCommunityId = ((AllFamily) familyObjs
									.get(currentindex))
									.getFamily_community_id();
							App.sFamilyBlockId = ((AllFamily) familyObjs
									.get(currentindex)).getFamily_block_id();
							MainActivity
									.setcurrentaddressTextView(MainActivity.currentvillage);
							saveSharePrefrence(
									MainActivity.currentcity,
									MainActivity.currentvillage,
									((AllFamily) familyObjs.get(currentindex))
											.getFamily_building_num()
											+ "-"
											+ ((AllFamily) familyObjs
													.get(currentindex))
													.getFamily_apt_num(),
									String.valueOf(App.sFamilyId), String
											.valueOf(App.sFamilyBlockId),
									String.valueOf(App.sFamilyCommunityId));
							old_family.setPrimary_flag(0);
							int entityType = ((AllFamily) familyObjs.get(currentindex)).getEntity_type();
							App.setAddrStatus(getApplicationContext(), entityType);
							//设置PushTag
							YLPushTagManager pushTagManager = new YLPushTagManager(getApplicationContext());
							pushTagManager.setPushTag();
							allFDaoDBImpl.modifyObject(old_family, old_family_address);
							Loger.d("test4", "old_family_address="+old_family_address);
							getData(address_show, familyObjs);
							Intent intent2 = new Intent("youlin.square.action");
							sendBroadcast(intent2);
						}
						
						
						adapter.notifyDataSetChanged();
						/**************** 写到本地数据库 table_users ********************/
						Account selfAccount = accountdao.findAccountByLoginID(String.valueOf(App.sUserLoginId));
						if(selfAccount==null){
							account.setUser_family_id(App.sFamilyId);
							account.setUser_family_address(MainActivity.currentcity + MainActivity.currentvillage
									+ ((AllFamily) familyObjs.get(currentindex)).getFamily_building_num() + "-"
									+ ((AllFamily) familyObjs.get(currentindex)).getFamily_apt_num());
							accountdao.saveObject(account);
						}else{
							selfAccount.setUser_family_id(App.sFamilyId);
							selfAccount.setUser_family_address(MainActivity.currentcity + MainActivity.currentvillage
									+ ((AllFamily) familyObjs.get(currentindex)).getFamily_building_num() + "-"
									+ ((AllFamily) familyObjs.get(currentindex)).getFamily_apt_num());
							accountdao.modifyObject(selfAccount);
						}
						accountdao.releaseDatabaseRes();
						/*************** 请求邻居 *********************/
						aNeighborDaoDBImpl = new NeighborDaoDBImpl(
								AddressShowForBar.this);
						if (((AllFamily) familyObjs.get(currentindex))
								.getEntity_type() == 1) {
							neighbor = new Neighbor(AddressShowForBar.this);
							if (!((AllFamily) familyObjs.get(currentindex)).getFamily_apt_num().equals("0"))
								gethttpdata(((AllFamily) familyObjs.get(currentindex))
										.getFamily_apt_num(),block_id,
										((AllFamily) familyObjs.get(currentindex))
										.getFamily_community_id(),
										IHttpRequestUtils.YOULIN,
										REQUEST_NEIGHBOR);
							else {
								bRequestSet = true;
								aNeighborDaoDBImpl.deleteObject(family_id);
								Toast.makeText(AddressShowForBar.this,
										"此地址未审核通过\n无法显示邻居信息", Toast.LENGTH_SHORT);
							}

							
						} else {
							aNeighborDaoDBImpl.deleteObject(family_id);
							Toast toast = Toast.makeText(getApplicationContext(),
									"此地址信息未审核通过！\n 不能为您显示邻居", Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							Intent intent = new Intent();
							intent.setAction("com.nfs.youlin.find.FindFragment");
							intent.putExtra("family_id",family_id );
							AddressShowForBar.this.sendOrderedBroadcast(intent, null);
							finish();
						}
					}
				}.execute();	
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
							if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME) {
								bRequestSet = true;
							}
						}
						finish();
						Message msg = new Message();
						msg.what = REQUEST_OK;
						Bundle data = new Bundle();
						data.putLong("family_id", family_id);
						msg.setData(data);
						handler.sendMessage(msg);
						return;
					}
				});
				requestTHttphgread.start();
			}
		});
	}
	  private List<Map<String, Object>> getData(String[] strs) {  
	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	        for (int i = 0; i < strs.length; i++) {  
	            Map<String, Object> map = new HashMap<String, Object>();  
	            map.put("name", strs[i]); 
	            list.add(map);  
	              
	        }  
	          
	        return list;  
	    } 
	private List<Map<String, Object>> getData(List<Map<String, Object>> list,List<Object> Objslist) {  
	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
		list.clear();
	        for (int i = 0; i < Objslist.size(); i++) {  
	            Map<String, Object> map = new HashMap<String, Object>();  
	            map.put("address_text", ((AllFamily) Objslist.get(i)).getFamily_address()); 
	            map.put("family_id",String.valueOf(((AllFamily) Objslist.get(i)).getFamily_id()));
	            map.put("verify_flag", ((AllFamily) Objslist.get(i)).getEntity_type());
	            map.put("check_flag", "0");
	            Loger.d("test4", "数据库地址-->"+((AllFamily) Objslist.get(i)).getFamily_address());
	            Loger.d("test4", "xml地址-->"+MainActivity.currentcity + MainActivity.currentvillage
						+ loadAddrdetailPrefrence());
	            if (((AllFamily) Objslist.get(i)).getFamily_address().equals(
	            		loadAddrdetailPrefrence().equals("null-null")?MainActivity.currentcity + MainActivity.currentvillage:MainActivity.currentcity + MainActivity.currentvillage
	    						+ loadAddrdetailPrefrence())) 
	            {
//	    			currentindex = i;
	    			map.put("check_flag", "1");
	    			AllFamily currentfamily = ((AllFamily) Objslist.get(i));
	    			Loger.d("test4", "current family--->"+((AllFamily)currentfamily).getFamily_community());
	    			currentfamily.setPrimary_flag(1);
	    			allFDaoDBImpl.modifyObject(currentfamily, ((AllFamily) Objslist.get(i)).getFamily_address());
	    			Loger.d("test4", "changed family--->"+((AllFamily)currentfamily).getFamily_community());
	    		}
	            list.add(map);      
	        }  
	        return list;  
	    } 
	private List<Map<String, Object>> getData(List<Object> Objslist) {  
	    List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
		list.clear();
	        for (int i = 0; i < Objslist.size(); i++) {  
	            Map<String, Object> map = new HashMap<String, Object>();  
	            String currentaddr = ((AllFamily) Objslist.get(i)).getFamily_address();
	            if(currentaddr.length()>20){
	            	currentaddr = currentaddr.substring(0, 21)+"...";
	            }
	            map.put("address_text",currentaddr ); 
	            map.put("family_id",String.valueOf(((AllFamily) Objslist.get(i)).getFamily_id()));
	            map.put("verify_flag", ((AllFamily) Objslist.get(i)).getEntity_type());
	            map.put("check_flag", "0");
	            //MainActivity.currentcity + MainActivity.currentvillage+ loadAddrdetailPrefrence()
	            if (((AllFamily) Objslist.get(i)).getPrimary_flag() == 1) {
	    			currentindex = i;
	    			map.put("check_flag", "1");
	    		}
	            list.add(map);      
	        }  
	          
	        return list;  
	    } 
	private void gethttpdata(String apt_num,Long param2,Long param3,String http_addr,int request_index){
		/***********************************************/
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		Loger.d("test3","apt_num="+apt_num+"block_id="+param2+"community_id="+param3 );
		if(request_index == REQUEST_NEIGHBOR){
			params.put("user_id", App.sUserLoginId);
			params.put("apt_num", apt_num);
			if(!param2.equals("0") && param2 != null)
				params.put("block_id", param2);
			params.put("community_id", param3);
		}
		params.put("tag", "neighbors");
		params.put("apitype", IHttpRequestUtils.APITYPE[0]);
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				String flag = null;
//				String community_id = null;
//				String community_name = null;
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test3", "set neighbor flag->" + flag);
					
					if(flag.equals("none_f_o1")){
						bRequestSet = true;
					}else{
						bRequestSet = false;
					}
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
				try {
					org.json.JSONObject obj = null;
					if(jsonContext.length()>0){
						for (int i = 0; i < jsonContext.length(); i++) {
							jsonObjList.add(jsonContext.getJSONObject(i));
							obj = jsonContext.getJSONObject(i);
						}
						bRequestSet = true;
						flag = "ok";
					}
					
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
					bRequestSet = false;
					ylProgressDialog.dismiss();
					new ErrorServer(AddressShowForBar.this, responseString);
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	private void gethttpdata(long block_id,long family_id ,int ne_status,long old_block_id,long old_family_id,int old_ne_status,String http_addr,int request_index){
		/***********************************************/
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		final int position = request_index;
			Loger.d("test5", "--"+block_id+"--"+"--"+family_id+"--"+"--"+ne_status+"--"+"--"+App.sUserLoginId+"--"+"--"+old_block_id+"--"+"--"+old_family_id+"--"+"--"+old_ne_status+"--");
			if(family_id >0 || ne_status >0){
				if(block_id >0)
					params.put("block_id", block_id);
				if(family_id > 0)
					params.put("family_id", family_id);
				else
					params.put("ne_status", ne_status);
			}
			params.put("user_id", App.sUserLoginId);
			
			if(old_family_id >0 || old_ne_status >0){
				if(old_block_id >0)
					params.put("old_block_id", old_block_id);
				if(old_ne_status >0)
					params.put("old_ne_status", old_ne_status);
				if(old_family_id >0)
					params.put("old_family_id", old_family_id);
			}	
			params.put("tag", "curaddr");
			params.put("apitype", IHttpRequestUtils.APITYPE[0]);
			SharedPreferences preferences=getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
			int addressTag=preferences.getInt("address_tag", 0);
			params.put("addr_cache",addressTag);
			Loger.i("LYM", "add---->"+addressTag);
		client.post(IHttpRequestUtils.URL+http_addr,
				params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode,
					org.apache.http.Header[] headers,
					org.json.JSONObject response) {
				// TODO Auto-generated method stub
				org.json.JSONObject jsonContext = response;
				Loger.i("LYM", "set currentaddress flag->" + response);
				try {
					flag = jsonContext.getString("flag");
					String addressTag=jsonContext.getString("addr_flag");
					
					if(flag.equals("ok") && addressTag.equals("ok")){
						cRequestSet = true;
						currentindex = position;
					}else{
						cRequestSet = false;
						Toast.makeText(AddressShowForBar.this, "设置失败", 0).show();
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
				Loger.i("LYM", "set currentaddress flag JSONArray->" + response);
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					cRequestSet = false;
					new ErrorServer(AddressShowForBar.this, responseString);
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	
	 private Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case REQUEST_OK:
					if(requestTHttphgread!=null){
						requestTHttphgread.interrupt();
						requestTHttphgread = null;
					}
					Long Belong_family_id = msg.getData().getLong("family_id");
					if (bRequestSet && flag != null && flag.equals("ok")){
						bRequestSet = false;
						flag = "";
						Loger.d("test3","Belong_family_id="+Belong_family_id);
						Loger.i("test3","jsonObjList->"+jsonObjList.size());
						if(jsonObjList.size()>0){
							aNeighborDaoDBImpl.deleteObject(Belong_family_id);
						}
						for(int i=0 ;i<jsonObjList.size();i++){
							
							try {
								if(Long.parseLong(jsonObjList.get(i).getString("family_id"))>0){
									neighbor.setAptnum(jsonObjList.get(i).getString("aptnum"));
									neighbor.setUser_id(Long.parseLong(jsonObjList.get(i).getString("user_id")));
									neighbor.setLogin_account(App.sUserLoginId);
									neighbor.setUser_family_id(Long.parseLong(jsonObjList.get(i).getString("family_id")));
									neighbor.setBelong_family_id(Belong_family_id);
									neighbor.setUser_name(jsonObjList.get(i).getString("user_nick"));
									neighbor.setUser_portrait(jsonObjList.get(i).getString("user_portrait"));
									neighbor.setBuilding_num(jsonObjList.get(i).getString("building_num"));
									neighbor.setProfession(jsonObjList.get(i).getString("user_profession"));
									neighbor.setBriefdesc(jsonObjList.get(i).getString("user_signature"));
									neighbor.setAddrstatus(jsonObjList.get(i).getString("user_public_status"));
									neighbor.setUser_type(Integer.parseInt(jsonObjList.get(i).getString("user_type")));
								}else{
									continue;
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							aNeighborDaoDBImpl.saveObject(neighbor);
							
						}
						ylProgressDialog.dismiss();
					}
					Intent intent = new Intent();
					intent.setAction("com.nfs.youlin.find.FindFragment");
					intent.putExtra("family_id",Belong_family_id );
					sendOrderedBroadcast(intent, null);
				}
			}
	 };
	public String loadAddrdetailPrefrence() {
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("detail", "未设置");
	}
	public String loadUsernamePrefrence(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("username", "未设置");
	}
	public String loadUserfamilyidPrefrence(){
		SharedPreferences sharedata = getSharedPreferences(App.REGISTERED_USER, Context.MODE_PRIVATE);
		return sharedata.getString("familyid", "000000");
	}
	public void saveSharePrefrence(String city, String village,String detail,String familyid,String blockid,String fimalycommunityid){
		Editor sharedata = getSharedPreferences(App.REGISTERED_USER,Context.MODE_PRIVATE).edit();
		sharedata.putString("city", city);
		sharedata.putString("village", village);
		sharedata.putString("detail", detail);
		sharedata.putString("familyid", familyid);
		sharedata.putString("blockid", blockid);
		sharedata.putString("familycommunityid", fimalycommunityid);
		sharedata.commit();
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
