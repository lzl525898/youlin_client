package com.nfs.youlin.activity.find;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.personal.Exchanged_giftactivity;
import com.nfs.youlin.adapter.Coml_cate_adapter;
import com.nfs.youlin.adapter.comml_list_adapter;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.YLProgressDialog;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class coml_everyitem_list extends Activity implements OnClickListener,OnRefreshListener<ListView>{
	private ActionBar actionBar;
	private ImageView commsearch;
	private EditText commedit;
	private RelativeLayout commbarlay;
	private TextView categrayname;
//	private TextView categraydistname;
	private TextView categraymethodname;
	private ImageView categrayjiantou1;
	private ImageView categrayjiantou2;
	private ImageView categrayjiantou3;
	private ListView categray_methodlistview1;
	private ListView categray_methodlistview2;
	private LinearLayout categray_methodlay;
	private LinearLayout methodlay;
	private LinearLayout namelay;
	private PullToRefreshListView comml_catelist;
	private YLProgressDialog dialog;
	private comml_list_adapter madapter;
	private int page = 0;
	private int searchplace = 0;
//	private String actiontag;
	private String searchitem = "";
//	private String[] actionbaritem = new String[]{"美食","娱乐","购物","健身","银行","其他"};
	private List<String> cateitem1list = new ArrayList<String>();
	private int cate1selected = 0;
	private List<String> cateitem2list = new ArrayList<String>();
	private int cate2selected = 0;
	private List<String> cateitem3list = new ArrayList<String>();
	private int cate3selected = 0;
	private Coml_cate_adapter categrayadapter;
	private List<Object> coml_list = new ArrayList<Object>();
	private String httpflag = "none";
	private boolean getcommsuccess = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coml_cate_detaillay);
//		Intent intent = getIntent();
//		actiontag = intent.getStringExtra("tag");
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("商圈");
		categrayname = (TextView)findViewById(R.id.categrayname);
//		categraydistname = (TextView)findViewById(R.id.categraydistname);
		categraymethodname = (TextView)findViewById(R.id.categraymethodname);
		categray_methodlistview1 = (ListView)findViewById(R.id.categray_methodlist1);
		categray_methodlistview2 = (ListView)findViewById(R.id.categray_methodlist2);
		categray_methodlay = (LinearLayout)findViewById(R.id.categray_methodlay);
		categrayjiantou1 = (ImageView)findViewById(R.id.categrayjiantou1);
		categrayjiantou3 = (ImageView)findViewById(R.id.categrayjiantou3);
		methodlay = (LinearLayout)findViewById(R.id.categraymethodlay);
		namelay = (LinearLayout)findViewById(R.id.categraynamelay);
		comml_catelist = (PullToRefreshListView)findViewById(R.id.comml_catelist);
		methodlay.setOnClickListener(this);
//		categraydistname.setOnClickListener(this);
		namelay.setOnClickListener(this);
		dialog = YLProgressDialog.createDialogwithcircle(this,"",1);
		dialog.show();
		categrapinput();
		Getcredithttprequest(cate1selected, cate3selected,0,null);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {

				Long currenttime = System.currentTimeMillis();
					while(!getcommsuccess){
						if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+5000)){
							getcommsuccess = true;
						}
					}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if(getcommsuccess&& httpflag.equals("ok")){
					Loger.d("test5", "getgiftsuccess&& httpflag.equals(ok)");
					madapter.notifyDataSetChanged();
				}
				dialog.dismiss();
				getcommsuccess = false;
				httpflag = "none";
			}
		}.execute();
		categray_methodlay.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				categray_methodlay.setVisibility(View.GONE);
				categrayjiantou3.setImageResource(R.drawable.sanjiao_2);
				categrayjiantou1.setImageResource(R.drawable.sanjiao_2);
				categrayname.setTextColor(0xff808080);
				categraymethodname.setTextColor(0xff808080);
				methodlay.setTag("0");
				namelay.setTag("0");
				return false;
			}
		});
	}
	private void categrapinput(){
		cateitem1list.add("全部");
		cateitem1list.add("宾馆");
		cateitem1list.add("餐饮");
		cateitem1list.add("生活");
		
		cateitem2list.add("附近");
		cateitem2list.add("1km");
		cateitem2list.add("2km");
		cateitem2list.add("5km");
		cateitem2list.add("10km");
		
		cateitem3list.add("智能排序");
		cateitem3list.add("离我最近");
		cateitem3list.add("好评优先");
		cateitem3list.add("人气最高");
		madapter = new comml_list_adapter(this,coml_list) ;
		comml_catelist.setAdapter(madapter);
		comml_catelist.setMode(Mode.PULL_FROM_END);
		comml_catelist.setOnRefreshListener(this);
	}
	private void Getcredithttprequest(final int categray_type,int order_type,final int intype,String searchitem){
		RequestParams params = new RequestParams();
		if(searchitem == null || searchitem.equals("") || searchitem.length()<1){
			params.put("bctag", categray_type);
			params.put("sort", order_type);
			params.put("community_id", App.sFamilyCommunityId);
			params.put("page", intype);
			params.put("tag", "bizcir");
			params.put("apitype", "address");
		}else{
			params.put("q", searchitem);
			params.put("page",intype+1);
			params.put("commid", App.sFamilyCommunityId);
			params.put("tag", "searchbizcir");
			params.put("apitype", "address");
		}
		
		AsyncHttpClient httpClient = new  AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN, params, 
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					Loger.d("test5", "1111111111111--->"+response.toString());
					String flag = response.getString("flag");
					
					if("none".equals(flag)){
						if(intype == 0){
							coml_list.clear();
							Toast.makeText(coml_everyitem_list.this, "没有搜到相关结果", 0).show();
						}else{
							Toast.makeText(coml_everyitem_list.this, "没有更多了", 0).show();
						}
						getcommsuccess = true;
						httpflag = "ok";
					}else if("ok".equals(flag)){
//						Loger.d("test5", response.get("data").toString());
						Loger.d("test5", "get searchplace="+response.get("page").toString());
						searchplace = Integer.parseInt(response.get("page").toString());
						JSONArray jsonlist = new JSONArray(response.get("data").toString());
						if(intype == 0){
							coml_list.clear();
						}
						for(int i=0;i<jsonlist.length();i++){
							try {
								coml_list.add(jsonlist.get(i).toString());
//								Loger.d("test5", ((JSONObject)jsonlist.get(i)).get("img_url").toString());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						httpflag = "ok";
						getcommsuccess = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				Loger.d("test5", "2222222222--->"+response.toString());
				if(intype == 0){
					coml_list.clear();
				}
				for(int i=0;i<response.length();i++){
					try {
						coml_list.add(response.get(i));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				httpflag = "ok";
				getcommsuccess = true;
				
				
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.newcommbar, menu);
		LinearLayout SearchGroup = (LinearLayout) menu.findItem(R.id.commactionbar).getActionView();
		commsearch = (ImageView)SearchGroup.findViewById(R.id.commsearch);
		commedit = (EditText)SearchGroup.findViewById(R.id.commedit);
		commbarlay = (RelativeLayout)SearchGroup.findViewById(R.id.commbarlay);
		
		commsearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				categray_methodlay.setVisibility(View.INVISIBLE);
				categrayname.setTextColor(0xff808080);
				categraymethodname.setTextColor(0xff808080);
				categrayjiantou1.setImageResource(R.drawable.sanjiao_2);
				categrayjiantou3.setImageResource(R.drawable.sanjiao_2);
				namelay.setTag("0");
				methodlay.setTag("0");
				if(commsearch.getTag().equals("0")){
					commedit.setVisibility(View.VISIBLE);
					commsearch.setTag("1");
					commbarlay.setBackgroundResource(R.drawable.comm_search_normal);
					commsearch.setImageResource(R.drawable.sousuohuang);
					actionBar.setTitle("搜索");
				}else{
//					commsearch.setTag("0");
//					commbarlay.setBackgroundColor(0xffffba02);
					if(TextUtils.isEmpty(commedit.getText())){
						searchitem="";
					}else{
						searchitem=commedit.getText().toString();
					}
					
					if(searchitem.length()<1){
						Toast.makeText(coml_everyitem_list.this, "请输入正确的搜索关键字", Toast.LENGTH_SHORT).show();;
					}else{
						dialog.show();
						searchplace=0;
						Getcredithttprequest(cate1selected, cate3selected,searchplace,searchitem);
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... params) {

								Long currenttime = System.currentTimeMillis();
									while(!getcommsuccess){
										if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+5000)){
											getcommsuccess = true;
										}
									}
								return null;
							}
							@Override
							protected void onPostExecute(Void result) {
								super.onPostExecute(result);
								if(getcommsuccess&& httpflag.equals("ok")){
									Loger.d("test5", "getgiftsuccess&& httpflag.equals(ok)");
									madapter.notifyDataSetChanged();
								}
								cate1selected = 0;
								cate2selected = 0;
								categrayname.setText(cateitem1list.get(cate1selected));
								categraymethodname.setText(cateitem3list.get(cate3selected));
								categray_methodlay.setVisibility(View.GONE);
								categrayjiantou3.setImageResource(R.drawable.sanjiao_2);
								categrayjiantou1.setImageResource(R.drawable.sanjiao_2);
								methodlay.setTag("0");
								namelay.setTag("0");
								
								dialog.dismiss();
								getcommsuccess = false;
								httpflag = "none";
							}
						}.execute();
					}
				}
			}
		});
		commedit.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus == true){
					categray_methodlistview1.setVisibility(View.INVISIBLE);
					categray_methodlistview2.setVisibility(View.INVISIBLE);
					categray_methodlay.setVisibility(View.INVISIBLE);
					categrayname.setTextColor(0xff808080);
					categraymethodname.setTextColor(0xff808080);
					categrayjiantou1.setImageResource(R.drawable.sanjiao_2);
					categrayjiantou3.setImageResource(R.drawable.sanjiao_2);
					namelay.setTag("0");
					methodlay.setTag("0");
				}
			}
		});
		commedit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				categray_methodlistview1.setVisibility(View.INVISIBLE);
				categray_methodlistview2.setVisibility(View.INVISIBLE);
				categray_methodlay.setVisibility(View.INVISIBLE);
				categrayname.setTextColor(0xff808080);
				categraymethodname.setTextColor(0xff808080);
				categrayjiantou1.setImageResource(R.drawable.sanjiao_2);
				categrayjiantou3.setImageResource(R.drawable.sanjiao_2);
				namelay.setTag("0");
				methodlay.setTag("0");
			}
		});
		commedit.addTextChangedListener(new TextWatcher() {
			int l=0;////////记录字符串被删除字符之前，字符串的长度
	  		int location=0;//记录光标的位置
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				 l=s.length();
	     		 location=commedit.getSelectionStart();
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				//searchitem = s.toString();
			}
		});
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			if(categray_methodlay.getVisibility() == View.VISIBLE){
				categray_methodlay.setVisibility(View.GONE);
				categrayjiantou3.setImageResource(R.drawable.sanjiao_2);
				categrayjiantou1.setImageResource(R.drawable.sanjiao_2);
				methodlay.setTag("0");
				namelay.setTag("0");
				return true;
			}else if(commsearch.getTag().equals("1")){
				commsearch.setTag("0");
				commbarlay.setBackgroundColor(0xffffba02);
				searchitem = "";
				commedit.setText("");
				commedit.setVisibility(View.INVISIBLE);
				commsearch.setImageResource(R.drawable.icon_sousuo);
				actionBar.setTitle("商圈");
			}else{
				finish();
			}
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if(categray_methodlay.getVisibility() == View.VISIBLE){
				categray_methodlay.setVisibility(View.GONE);
				categrayjiantou3.setImageResource(R.drawable.sanjiao_2);
				categrayjiantou1.setImageResource(R.drawable.sanjiao_2);
				methodlay.setTag("0");
				namelay.setTag("0");
				return true;
			}else if(commsearch.getTag().equals("1")){
				commsearch.setTag("0");
				commbarlay.setBackgroundColor(0xffffba02);
				searchitem = "";
				commedit.setText("");
				commedit.setVisibility(View.INVISIBLE);
				commsearch.setImageResource(R.drawable.icon_sousuo);
				actionBar.setTitle("商圈");
				return true;
			}else{
				return super.onKeyDown(keyCode, event);
			}
			
		}else{
			return super.onKeyDown(keyCode, event);
		}
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.categraynamelay){
			if(v.getTag().equals("0")){
				categrayadapter = new Coml_cate_adapter(this, cateitem1list,cate1selected);	
				categray_methodlistview1.setVisibility(View.VISIBLE);
				categray_methodlistview2.setVisibility(View.INVISIBLE);
				categray_methodlistview1.setAdapter(categrayadapter);
				categray_methodlay.setVisibility(View.VISIBLE);
				categrayname.setTextColor(0xffffba02);
//				categraydistname.setTextColor(0xff808080);
				categraymethodname.setTextColor(0xff808080);
				categrayjiantou1.setImageResource(R.drawable.sanjiao_1);
				categrayjiantou3.setImageResource(R.drawable.sanjiao_2);
				namelay.setTag("1");
				methodlay.setTag("0");
				categray_methodlistview1.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method stub
						cate1selected = position;
						categrayadapter.notifyDataSetChanged();
						categray_methodlay.setVisibility(View.GONE);
						categrayname.setText(cateitem1list.get(position));
						categrayjiantou1.setImageResource(R.drawable.sanjiao_2);
						categrayname.setTextColor(0xff808080);
						dialog.show();
						page = 0;
						searchplace=0;
						if(commsearch.getTag().equals("1")){
							commsearch.setTag("0");
							commbarlay.setBackgroundColor(0xffffba02);
							searchitem = "";
							commedit.setText("");
							commedit.setVisibility(View.INVISIBLE);
							commsearch.setImageResource(R.drawable.icon_sousuo);
						}
						Getcredithttprequest(cate1selected, cate3selected,0,null);
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... params) {

								Long currenttime = System.currentTimeMillis();
									while(!getcommsuccess){
										if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+5000)){
											getcommsuccess = true;
										}
									}
								return null;
							}
							@Override
							protected void onPostExecute(Void result) {
								super.onPostExecute(result);
								if(getcommsuccess&& httpflag.equals("ok")){
									Loger.d("test5", "getgiftsuccess&& httpflag.equals(ok)");
									madapter.notifyDataSetChanged();
								}
								dialog.dismiss();
								namelay.setTag("0");
								getcommsuccess = false;
								httpflag = "none";
							}
						}.execute();
					}
				});
			}else{
				namelay.setTag("0");
				categray_methodlay.setVisibility(View.GONE);
				categrayjiantou1.setImageResource(R.drawable.sanjiao_2);
				categrayname.setTextColor(0xff808080);
			}
			
		}
//		else if(v.getId() == R.id.categraydistname){
//			categrayadapter = new Coml_cate_adapter(this, cateitem2list,cate2selected);	
//			categray_methodlistview.setAdapter(categrayadapter);
//			categray_methodlay.setVisibility(View.VISIBLE);
//			categraydistname.setTextColor(0xffffba02);
//			categraymethodname.setTextColor(0xff808080);
//			categrayname.setTextColor(0xff808080);
//			categray_methodlistview.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//					// TODO Auto-generated method stub
//					cate2selected = position;
//					categrayadapter.notifyDataSetChanged();
//					categray_methodlay.setVisibility(View.GONE);
//				}
//			});
//		}
		else if(v.getId() == R.id.categraymethodlay){
			if(v.getTag().equals("0")){
				categrayadapter = new Coml_cate_adapter(this, cateitem3list,cate3selected);	
				categray_methodlistview2.setVisibility(View.VISIBLE);
				categray_methodlistview1.setVisibility(View.INVISIBLE);
				categray_methodlistview2.setAdapter(categrayadapter);
				categray_methodlay.setVisibility(View.VISIBLE);
				categraymethodname.setTextColor(0xffffba02);
//				categraydistname.setTextColor(0xff808080);
				categrayname.setTextColor(0xff808080);
				categrayjiantou3.setImageResource(R.drawable.sanjiao_1);
				categrayjiantou1.setImageResource(R.drawable.sanjiao_2);
				methodlay.setTag("1");
				namelay.setTag("0");
				categray_methodlistview2.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method stub
						cate3selected = position;
						categrayadapter.notifyDataSetChanged();
						categray_methodlay.setVisibility(View.GONE);
						categraymethodname.setText(cateitem3list.get(position));
						categrayjiantou3.setImageResource(R.drawable.sanjiao_2);
						categraymethodname.setTextColor(0xff808080);
						dialog.show();
						page = 0;
						searchplace=0;
						if(commsearch.getTag().equals("1")){
							commsearch.setTag("0");
							commbarlay.setBackgroundColor(0xffffba02);
							searchitem = "";
							commedit.setText("");
							commedit.setVisibility(View.INVISIBLE);
							commsearch.setImageResource(R.drawable.icon_sousuo);
						}
						Getcredithttprequest(cate1selected, cate3selected,0,null);
						new AsyncTask<Void, Void, Void>() {
							@Override
							protected Void doInBackground(Void... params) {

								Long currenttime = System.currentTimeMillis();
									while(!getcommsuccess){
										if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+5000)){
											getcommsuccess = true;
										}
									}
								return null;
							}
							@Override
							protected void onPostExecute(Void result) {
								super.onPostExecute(result);
								if(getcommsuccess&& httpflag.equals("ok")){
									Loger.d("test5", "getgiftsuccess&& httpflag.equals(ok)");
									madapter.notifyDataSetChanged();
								}
								methodlay.setTag("0");
								dialog.dismiss();
								getcommsuccess = false;
								httpflag = "none";
							}
						}.execute();
					}
				});
			}else{
				methodlay.setTag("0");
				categray_methodlay.setVisibility(View.GONE);
				categrayjiantou3.setImageResource(R.drawable.sanjiao_2);
				categraymethodname.setTextColor(0xff808080);
			}
			
		}
		
	}
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (refreshView.isShownFooter()) {
			Loger.d("test5", "searchitem="+searchitem+"searchplace="+searchplace);
			if(!searchitem.equals("")){
				Getcredithttprequest(cate1selected, cate3selected,searchplace,searchitem);
			}else{
				page++;
				Getcredithttprequest(cate1selected, cate3selected,page,searchitem);
			}
			
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {

					Long currenttime = System.currentTimeMillis();
						while(!getcommsuccess){
							if((System.currentTimeMillis()-currenttime)>(App.WAITFORHTTPTIME+5000)){
								getcommsuccess = true;
							}
						}
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					if(getcommsuccess&& httpflag.equals("ok")){
						Loger.d("test5", "getcommlsuccess&& httpflag.equals(ok)");
						madapter.notifyDataSetChanged();
						
					}else if(getcommsuccess && !httpflag.equals("ok")){
						page--;
					}
					comml_catelist.onRefreshComplete();
					getcommsuccess = false;
					httpflag = "none";
				}
			}.execute();
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
