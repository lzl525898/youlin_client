package com.nfs.youlin.view;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;  
import java.util.HashMap;  

import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.utils.KMP_search;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.R;
  


import com.nfs.youlin.activity.AddressShowForBar;
import com.nfs.youlin.activity.personal.selectvillageActivity;
import com.nfs.youlin.activity.personal.write_address;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.KMP_search;
import com.nfs.youlin.utils.StatusChangeutils;

import android.app.ListActivity;  
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;  
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;  
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;  
import android.widget.SimpleAdapter;  
import android.widget.TextView;
import android.widget.Toast; 
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class rooft_fragment  extends Fragment{
	public String rooft = "未设定";
	public static AutoCompleteTextView rooftmessage;
	public static ImageButton setimage;
	private List<String> chooselist = new ArrayList<String>();

	public static List<JSONObject> jsonObjList;
	private boolean bRequestSet = false;
	private Thread requestTHttphgread;
	private YLProgressDialog ylProgressDialog;
	private final int REQUEST_OK = 101;
	private final int REQUEST_NO = 102;
	private StatusChangeutils statusutils;
	void buildprocessdialog(){
		ylProgressDialog = YLProgressDialog.createDialogwithcircle(getActivity(),"加载中...",0);
//		pd1 = new ProgressDialog(getActivity());
//		pd1.setTitle("正在刷新地址列表");
//		pd1.setMessage("正在玩命为您加载，请稍侯...");
//		pd1.setCancelable(false);
//		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		pd1.setIndeterminate(false);
	} 
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Loger.d("test4", "rooft_fragment onResume");
		chooselist.clear();
		if(selectvillageActivity.buildnumlist.size() == 0 && App.sFamilyId>0 ){
			if(!write_address.village_id.equals("0")||!write_address.block_id.equals("0")){
				selectvillageActivity.buildnumlist.clear();
				Loger.d("test4", "selectvillageActivity.buildnumlist.size() == 0 && App.sFamilyId="+App.sFamilyId +"&&App.sFamilyCommunityId="+App.sFamilyCommunityId+"&&App.sFamilyBlockId="+App.sFamilyBlockId );
				//gethttpdata("community_id",String.valueOf(App.sFamilyCommunityId),IHttpRequestUtils.YOULIN_ADDR_DATE);
				if(write_address.block_id.equals("0"))
					gethttpdata("community_id",write_address.block_id,IHttpRequestUtils.YOULIN);
				else
					gethttpdata("block_id",write_address.village_id,IHttpRequestUtils.YOULIN);
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
						buildhandler.sendMessage(msg);
						return;
					}
				});
				requestTHttphgread.start();
			}
			
		}else{
			Loger.d("test4", "selectvillageActivity.buildnumlist.size() != 0 && App.sFamilyId="+App.sFamilyId);
			for(int i =0;i<selectvillageActivity.buildnumlist.size();i++){
	    		chooselist.add((String)selectvillageActivity.buildnumlist.get(i).get("name"));
	    	}
			if(selectvillageActivity.buildnumlist.size()<0){
				Toast toast = Toast.makeText(getActivity(), "请求楼栋失败，请点击小区选项以便我们可以为您重新加载楼栋！", Toast.LENGTH_SHORT);
				 toast.setGravity(Gravity.CENTER, 0, 0);
				 toast.show();
			}
			
//			ArrayAdapter<String> search_choose = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, chooselist.toArray(new String[chooselist.size()]));
//	    	rooftmessage.setAdapter(search_choose);
		}

    //	Loger.d("test3", "chooselist rooft ->"+chooselist.size());
    	
	}
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
    	View view = inflater.inflate(R.layout.rooftset, container, false);
    	final TextView setrooft = (TextView)view.findViewById(R.id.rooftset);
    	setrooft.setText("楼栋号");
    	rooftmessage =(AutoCompleteTextView) view.findViewById(R.id.rooft);
    	buildprocessdialog();
    	rooftmessage.setHint("(1-A表示1栋A单元\n3335-43表示3335弄43号)");
    	setimage = (ImageButton)view.findViewById(R.id.setimage);
    	rooftmessage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				if(rooftmessage.getText().toString().length()>0){
//					Loger.d("test4","rooftmessage.getText().toString().length()>0" );
//					return;
//				}
				chooselist.clear();
				if(selectvillageActivity.buildnumlist.size() == 0 && App.sFamilyId>0 ){
					if(!write_address.village_id.equals("0")||!write_address.block_id.equals("0")){
						selectvillageActivity.buildnumlist.clear();
						Loger.d("test4", "selectvillageActivity.buildnumlist.size() == 0 && App.sFamilyId="+App.sFamilyId +"&&App.sFamilyCommunityId="+App.sFamilyCommunityId+"&&App.sFamilyBlockId="+App.sFamilyBlockId );
						//gethttpdata("community_id",String.valueOf(App.sFamilyCommunityId),IHttpRequestUtils.YOULIN_ADDR_DATE);
						if(write_address.block_id.equals("0"))
							gethttpdata("community_id",write_address.village_id,IHttpRequestUtils.YOULIN);
						else
							gethttpdata("block_id",write_address.block_id,IHttpRequestUtils.YOULIN);
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
								buildhandler.sendMessage(msg);
								return;
							}
						});
						requestTHttphgread.start();
					}
					
				}else{
					Loger.d("test4", "selectvillageActivity.buildnumlist.size() != 0 && App.sFamilyId="+App.sFamilyId);
					write_address.searchresult.clear();
					for(int i =0;i<selectvillageActivity.buildnumlist.size();i++){
			    		chooselist.add((String)selectvillageActivity.buildnumlist.get(i).get("name"));
	    		    	write_address.searchresult.add(selectvillageActivity.buildnumlist.get(i));
			    	}
					statusutils = new StatusChangeutils();
	    			statusutils.setstatuschange("ADDRESSLIST",1);
					if(selectvillageActivity.buildnumlist.size()<0){
						Toast toast = Toast.makeText(getActivity(), "请求楼栋失败，请点击小区选项以便我们可以为您重新加载楼栋！", Toast.LENGTH_SHORT);
						 toast.setGravity(Gravity.CENTER, 0, 0);
						 toast.show();
					}
					
//					ArrayAdapter<String> search_choose = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, chooselist.toArray(new String[chooselist.size()]));
//			    	rooftmessage.setAdapter(search_choose);
				}
			}
		});
    	rooftmessage.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (arg0.getId() == R.id.rooft && hasFocus == true){
					chooselist.clear();
					if(selectvillageActivity.buildnumlist.size() == 0 && App.sFamilyId>0 ){
						if(!write_address.village_id.equals("0")||!write_address.block_id.equals("0")){
							selectvillageActivity.buildnumlist.clear();
							Loger.d("test4", "selectvillageActivity.buildnumlist.size() == 0 && App.sFamilyId="+App.sFamilyId +"&&App.sFamilyCommunityId="+App.sFamilyCommunityId+"&&App.sFamilyBlockId="+App.sFamilyBlockId );
							//gethttpdata("community_id",String.valueOf(App.sFamilyCommunityId),IHttpRequestUtils.YOULIN_ADDR_DATE);
							if(write_address.block_id.equals("0"))
								gethttpdata("community_id",write_address.village_id,IHttpRequestUtils.YOULIN);
							else
								gethttpdata("block_id",write_address.block_id,IHttpRequestUtils.YOULIN);
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
									buildhandler.sendMessage(msg);
									return;
								}
							});
							requestTHttphgread.start();
						}
						
					}else{
						Loger.d("test4", "selectvillageActivity.buildnumlist.size() != 0 && App.sFamilyId="+App.sFamilyId);
						write_address.searchresult.clear();
						for(int i =0;i<selectvillageActivity.buildnumlist.size();i++){
				    		chooselist.add((String)selectvillageActivity.buildnumlist.get(i).get("name"));
		    		    	write_address.searchresult.add(selectvillageActivity.buildnumlist.get(i));
				    	}
						statusutils = new StatusChangeutils();
		    			statusutils.setstatuschange("ADDRESSLIST",1);
						if(selectvillageActivity.buildnumlist.size()<0){
							Toast toast = Toast.makeText(getActivity(), "请求楼栋失败，请点击小区选项以便我们可以为您重新加载楼栋！", Toast.LENGTH_SHORT);
							 toast.setGravity(Gravity.CENTER, 0, 0);
							 toast.show();
						}
						
//						ArrayAdapter<String> search_choose = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, chooselist.toArray(new String[chooselist.size()]));
//				    	rooftmessage.setAdapter(search_choose);
					}
				}
			}
    		
    	});
    	rooftmessage.addTextChangedListener(new TextWatcher() {
 		   int l=0;////////记录字符串被删除字符之前，字符串的长度
 		   int location=0;//记录光标的位置
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				number_fragment.numbermessage.setText("");
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
    		    l=arg0.length();
    		    location=rooftmessage.getSelectionStart();

			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
//    		    if(arg0.toString().length() == 0 && l == 0){
//    		    	write_address.searchresult.clear();
//        		    write_address.searchresult = selectvillageActivity.buildnumlist;
//        		    statusutils = new StatusChangeutils();
//    				statusutils.setstatuschange("ADDRESSLIST",1);
//    		    }
    		    if (arg0.toString().length() > 0) {
    		    	setimage.setBackgroundResource(R.drawable.search_clear_pressed);
    		    	setimage.setVisibility(View.VISIBLE);
    		    	if(arg0.toString().length() == 1 && l == 0){
    			    	final String whattosearch = rooftmessage.getText().toString();
        		    	new Thread(new Runnable() {
        					
        					@Override
        					public void run() {
        						// TODO Auto-generated method stub
        						write_address.searchresult.clear();
        						//Loger.d("hyytest", "chooselist.size()="+chooselist.size()+"chooselist 0="+chooselist.get(0));
        						for(int i = 0;i<chooselist.size();i++){
        							int index = 10000;
        							Loger.d("hyytest", "index0="+index);
        							if(whattosearch.length()>chooselist.get(i).length()){
        								
        							}else{
        								Loger.d("hyytest", "index1="+index);
        								index = KMP_search.KMP_search(chooselist.get(i),chooselist.get(i).length(),whattosearch,whattosearch.length());
        							}
        							
        							Loger.d("hyytest", "index="+index);
        							if(index<chooselist.get(i).length())
        								write_address.searchresult.add(selectvillageActivity.buildnumlist.get(i));
        						}
//        						Intent intent = new Intent();
//        						intent.setAction("com.nfs.youlin.view.rooft_fragment");
//        						getActivity().sendBroadcast(intent);
        						statusutils = new StatusChangeutils();
        						statusutils.setstatuschange("ADDRESSLIST",1);
        					}
        				}).run();
    		    	}
    		    }
    		    else if (arg0.toString().length() == 0) {
    		    	setimage.setImageBitmap(null);
    		    	 if(l == 0|| l>5){
    	    		    	write_address.searchresult.clear();
    	    		    	for(int i = 0;i<selectvillageActivity.buildnumlist.size();i++){
    	    		    		write_address.searchresult.add(selectvillageActivity.buildnumlist.get(i));
    						}
    	        		    statusutils = new StatusChangeutils();
    	    				statusutils.setstatuschange("ADDRESSLIST",1);
    	    		    }
    		    }
    		}
		});
    	
    	setimage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Loger.d("hyytest","rooftmessage click");
				rooftmessage.setText("");
				setimage.setVisibility(View.GONE);
				number_fragment.aptlist.clear();
//				if(setimage.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.search_clear_pressed).getConstantState()))
//				{rooftmessage.setText("");
//				Loger.d("hyytest","clear rooftmessage");}
//				if(setimage.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.appitem_del_btn_pressed).getConstantState()))
//				{Loger.d("hyytest","rooftmessage empty");}
			}
		});
        return view;  
    } 
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
     //   String[] str = new String[] {"buttonview","text"};

    }
	  private List<Map<String, Object>> getData(String[] strs) {  
	      //  List<Map<String ,Object>> list = new ArrayList<Map<String,Object>>();  
	        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	        for (int i = 0; i < strs.length; i++) {  
	            Map<String, Object> map = new HashMap<String, Object>();  
	            map.put("text", strs[i]);    
	            map.put("buttonview", R.drawable.contacts);
	            list.add(map);  
	              
	        }  
	          
	        return list;  
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
						bRequestSet = true;
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
					bRequestSet = false;
					ylProgressDialog.dismiss();
					Loger.i("test3","jsonObjList->"+jsonObjList.size());
					if(jsonObjList.size()<0){
						Toast toast = Toast.makeText(getActivity(), "请求楼栋失败，请点击小区选项以便我们可以为您重新加载楼栋！", Toast.LENGTH_SHORT);
						 toast.setGravity(Gravity.CENTER, 0, 0);
						 toast.show();
					}
					selectvillageActivity.buildnumlist.clear();
					for(int i=0 ;i<jsonObjList.size();i++){
						
						try {
							String buildingname = jsonObjList.get(i).getJSONObject("fields").getString("building_name");
							String buildingid = jsonObjList.get(i).getString("pk");
							Loger.d("test3", "rooft_fragment buildingname="+buildingname+"buildingid="+buildingid);
							AddData(selectvillageActivity.buildnumlist, buildingname, buildingid);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					for(int i =0;i<selectvillageActivity.buildnumlist.size();i++){
			    		chooselist.add((String)selectvillageActivity.buildnumlist.get(i).get("name"));
			    	}
//					ArrayAdapter<String> search_choose = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, chooselist.toArray(new String[chooselist.size()]));
//			    	rooftmessage.setAdapter(search_choose);
				}
					break;
				default:
					break;
				}
			}
	 };
	  private List<Map<String, Object>> AddData(List<Map<String, Object>> oldlist,String strname,String strid){
		  Map<String, Object> map = new HashMap<String, Object>();  
          map.put("name", strname); 
          map.put("_id", strid);
          map.put("street",strid);
          oldlist.add(map);
          return oldlist;
	  }
}
