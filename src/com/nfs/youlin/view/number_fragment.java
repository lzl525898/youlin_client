package com.nfs.youlin.view;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;  
import java.util.HashMap;  

import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
  

import com.nfs.youlin.activity.personal.selectvillageActivity;
import com.nfs.youlin.activity.personal.write_address;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.KMP_search;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.StatusChangeutils;
import com.nfs.youlin.utils.TimeClick;

import android.app.ListActivity;  
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;  
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;  
import android.widget.SimpleAdapter;  
import android.widget.TextView;
import android.widget.Toast; 
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
public class number_fragment extends Fragment{
	public static AutoCompleteTextView numbermessage;
	public static ImageButton setimage1 ;
	private Thread requestTHttphgread;
	public static List<JSONObject> jsonObjList = new ArrayList<JSONObject>();
	private boolean bRequestSet = false;
	private final int REQUEST_OK = 101;
	private final int REQUEST_NO = 102;
	public static List<Map<String, Object>> aptlist = new ArrayList<Map<String,Object>>();
	private List<String> chooselist = new ArrayList<String>();
	public static String selectedbuild;
	public static String selectedbuild_id="0" ;
	private StatusChangeutils statusutils;
	private YLProgressDialog ylProgressDialog;
	void buildprocessdialog(){
		ylProgressDialog = YLProgressDialog.createDialogwithcircle(getActivity(),"加载中...",0);
	} 
	 @Override  
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
	            Bundle savedInstanceState)  
	    {  
	    	View view = inflater.inflate(R.layout.numberset, container, false);
	    	final TextView setnumber = (TextView)view.findViewById(R.id.numset);
	    	setnumber.setText("门牌号");
	    	numbermessage =(AutoCompleteTextView) view.findViewById(R.id.number);
	    	setimage1 = (ImageButton)view.findViewById(R.id.setimage1);
	    	numbermessage.setHint("如1802");
	    	buildprocessdialog();
	    	chooselist.clear();
	    	aptlist.clear();
	    	numbermessage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					if(numbermessage.getText().toString().length()>0 || TimeClick.isFastClick()){
//						return;
//					}
					chooselist.clear();
					write_address.searchresult.clear();
					if(aptlist.size()>0){
						for(int i = 0;i<number_fragment.aptlist.size();i++){
  	    		    		write_address.searchresult.add(number_fragment.aptlist.get(i));
  	    		    		chooselist.add(aptlist.get(i).get("name").toString());
  						}
  	        		    statusutils = new StatusChangeutils();
  	    				statusutils.setstatuschange("ADDRESSLIST",2);
  	    				return;
					}
					
					bRequestSet = false;
					selectedbuild = rooft_fragment.rooftmessage.getText()
							.toString();
					selectedbuild_id = "0";
					Loger.d("test3", "===="+selectedbuild);
					for (int i = 0; i < selectvillageActivity.buildnumlist
							.size(); i++) {
						Loger.d("test3", "+==="+selectvillageActivity.buildnumlist.get(i));
						Loger.d("test3", "number_fragment rooft circle"+i);
						if (selectvillageActivity.buildnumlist.get(i)
								.get("name").equals(selectedbuild)) {
							selectedbuild_id = selectvillageActivity.buildnumlist
									.get(i).get("_id").toString();
							gethttpdata("buildnum_id", selectedbuild_id,
									IHttpRequestUtils.YOULIN);
							requestTHttphgread = new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											ylProgressDialog.show();
										}
									});
									Long currenttime = System.currentTimeMillis();
									while (!bRequestSet) {
										if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME+5000) {
											bRequestSet = true;
										}
									}
									Message msg = new Message();
									msg.what = REQUEST_OK;
//									Bundle bundle = new Bundle();
//									bundle.putBoolean("flag", true);
//									msg.setData(bundle);
									buildhandler.sendMessage(msg);
								}
							});
							requestTHttphgread.start();
							break;
						}
					}
					Message msg = new Message();
					msg.what = REQUEST_OK;
					Bundle bundle = new Bundle();
					bundle.putBoolean("flag", false);
					msg.setData(bundle);
					buildhandler.sendMessage(msg);
				}
			});
	    	numbermessage.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg0.getId() == R.id.number && arg1 == true) {
					write_address.searchresult.clear();
					bRequestSet = false;
					selectedbuild = rooft_fragment.rooftmessage.getText()
							.toString();
					selectedbuild_id = "0";
					for (int i = 0; i < selectvillageActivity.buildnumlist
							.size(); i++) {
						Loger.d("test3", "number_fragment rooft circle"+i);
						if (selectvillageActivity.buildnumlist.get(i)
								.get("name").equals(selectedbuild)) {
							selectedbuild_id = selectvillageActivity.buildnumlist
									.get(i).get("_id").toString();
							gethttpdata("buildnum_id", selectedbuild_id,
									IHttpRequestUtils.YOULIN);
							requestTHttphgread = new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											ylProgressDialog.show();
										}
									});
									Long currenttime = System.currentTimeMillis();
									while (!bRequestSet) {
										if ((System.currentTimeMillis() - currenttime) > App.WAITFORHTTPTIME+5000) {
											bRequestSet = true;
										}
									}
									Message msg = new Message();
									msg.what = REQUEST_OK;
//									Bundle bundle = new Bundle();
//									bundle.putBoolean("flag", true);
//									msg.setData(bundle);
									buildhandler.sendMessage(msg);
								}
							});
							requestTHttphgread.start();
							break;
						}
					}
					Message msg = new Message();
					msg.what = REQUEST_OK;
					Bundle bundle = new Bundle();
					bundle.putBoolean("flag", false);
					msg.setData(bundle);
					buildhandler.sendMessage(msg);
				}

			}
		});
		numbermessage.addTextChangedListener(new TextWatcher() {
			int l=0;////////记录字符串被删除字符之前，字符串的长度
	  		   int location=0;//记录光标的位置
	 			@Override
	 			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	 				// TODO Auto-generated method stub
	 				
	 			}
	 			
	 			@Override
	 			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
	 					int arg3) {
	 				// TODO Auto-generated method stub
	     		    l=arg0.length();
	     		    location=numbermessage.getSelectionStart();
	     		   
	 			}
	 			
	 			@Override
	 			public void afterTextChanged(Editable arg0) {
	 				// TODO Auto-generated method stub
	     		    if (arg0.toString().length() > 0) {
	     		    	setimage1.setVisibility(View.VISIBLE);
	     		    	setimage1.setBackgroundResource(R.drawable.search_clear_pressed);
	     		    	if(arg0.toString().length() == 1){
	    			    	final String whattosearch = numbermessage.getText().toString();
	        		    	new Thread(new Runnable() {
	        					
	        					@Override
	        					public void run() {
	        						// TODO Auto-generated method stub
	        						write_address.searchresult.clear();
	        						for(int i = 0;i<chooselist.size();i++){
	        							int index = 10000;
	        							if(whattosearch.length()>chooselist.get(i).length()){
	        								
	        							}else{
	        								index = KMP_search.KMP_search(chooselist.get(i),chooselist.get(i).length(),whattosearch,whattosearch.length());
	        							}
	        							
	        							Loger.d("hyytest", "index="+index);
	        							if(index<chooselist.get(i).length())
	        								write_address.searchresult.add(number_fragment.aptlist.get(i));
	        						}
//	        						Intent intent = new Intent();
//	        						intent.setAction("com.nfs.youlin.view.number_fragment");
//	        						getActivity().sendBroadcast(intent);
	        						statusutils = new StatusChangeutils();
	        						statusutils.setstatuschange("ADDRESSLIST",2);
	        					}
	        				}).run();
	    		    	}
	     		    }
	     		    else if (arg0.toString().length() == 0) {
	     		    	setimage1.setImageBitmap(null);
    		    
	     		    }
	     		}
	 		});

	    	setimage1.setOnClickListener(new OnClickListener() {
	 			
	 			@Override
	 			public void onClick(View arg0) {
	 				// TODO Auto-generated method stub
	 				numbermessage.setText("");
	 				setimage1.setVisibility(View.GONE);
//	 				if(setimage1.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.search_clear_pressed).getConstantState()))
//	 				{numbermessage.setText("");
//	 				Loger.d("hyytest","clear rooftmessage");}
//	 				if(setimage1.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.appitem_del_btn_pressed).getConstantState()))
//	 				{Loger.d("hyytest","rooftmessage empty");}
	 			}
	 		});
	        return view;  
	    } 
	    public void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	     //   String[] str = new String[] {"buttonview","text"};

	    }
	    private List<Map<String, Object>> AddData(List<Map<String, Object>> oldlist,String strname,String strid){
			  Map<String, Object> map = new HashMap<String, Object>();  
	          map.put("name", strname); 
	          map.put("_id", strid);
	          map.put("street",strid);
	          oldlist.add(map);
	          return oldlist;
		  }
	    private void gethttpdata(String request_index,String request_name,String http_addr){
			/***********************************************/
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
					try {
						flag = jsonContext.getString("flag");
						Loger.i("test3", "flag->" + flag);
						bRequestSet = true;
					} catch (org.json.JSONException e) {
						e.printStackTrace();
						bRequestSet = true;
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
					try {
//						String json = null;
//						String city_id = null;
//						String community_name = null;
//						String community_id = null;
						org.json.JSONObject obj = null;
						for (int i = 0; i < jsonContext.length(); i++) {
							jsonObjList.add(jsonContext.getJSONObject(i));
							obj = jsonContext.getJSONObject(i);
//							community_id = obj.getString("pk");
//							community_name = obj.getJSONObject("fields").getString("community_name");
//							city_id = obj.getJSONObject("fields").getString("city");
//							json = community_id + community_name + city_id + "\r\n";
							Loger.d("test3", ""+obj.getString("pk"));
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
						bRequestSet = false;
						super.onFailure(statusCode, headers,
								responseString, throwable);
					}
				});
		}
		 private Handler buildhandler = new Handler(){
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case REQUEST_OK:{
//							Loger.i("test3","jsonObjList->"+jsonObjList.size());
							bRequestSet = false;
							ylProgressDialog.dismiss();
							chooselist.clear();
							aptlist.clear();
							for(int i=0 ;i<jsonObjList.size();i++){
								
								try {
									String aptname = jsonObjList.get(i).getJSONObject("fields").getString("apt_name");
									String aptid = jsonObjList.get(i).getString("pk");
									Loger.d("test3", "aptname="+aptname+"aptid="+aptid);
									AddData(aptlist, aptname, aptid);
									chooselist.add(aptname);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									Loger.i("test3","555555555555555");
									e.printStackTrace();
								}
							}
		    		    	 if(true){
		  	    		    	write_address.searchresult.clear();
		  	    		    	for(int i = 0;i<number_fragment.aptlist.size();i++){
		  	    		    		write_address.searchresult.add(number_fragment.aptlist.get(i));
		  						}
		  	        		    statusutils = new StatusChangeutils();
		  	    				statusutils.setstatuschange("ADDRESSLIST",2);
		  	    		    }
//							try {
//								ArrayAdapter<String> search_choose = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, chooselist.toArray(new String[chooselist.size()]));
//								number_fragment.numbermessage.setAdapter(search_choose);
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						
//						selectvillageActivity.this.setResult(RESULT_OK, resultintent);
//						finish();
					}
						break;
					default:
						break;
					}
				}
		 };
}
