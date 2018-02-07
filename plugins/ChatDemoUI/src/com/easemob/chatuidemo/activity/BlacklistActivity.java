package com.easemob.chatuidemo.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.db.NeighborDaoDBSearch;
import com.easemob.chatuidemo.utils.App;
import com.easemob.chatuidemo.utils.HttpClientHelper;
import com.easemob.chatuidemo.utils.Loger;
import com.easemob.chatuidemo.utils.MD5Util;
import com.easemob.exceptions.EaseMobException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

/**
 * 黑名单列表页面
 * 
 */
public class BlacklistActivity extends Activity {
	private ListView listView;
	private BlacklistAdapater adapter;
	private List<Map<String, String>> blackList=new ArrayList<Map<String, String>>();
	private ProgressDialog pd1;
	private int position;
	private TelephonyManager telephonyManager;
	private String imeiCode;
	private ImageLoader imageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_list);
        imageLoader = ImageLoader.getInstance();
		pd1 = new ProgressDialog(BlacklistActivity.this);
		final PopupWindow popupWindow=new PopupWindow(BlacklistActivity.this);
		popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        View view=LayoutInflater.from(BlacklistActivity.this).inflate(R.layout.take_out_black, null);
        final LinearLayout layout=(LinearLayout) view.findViewById(R.id.take_out_black_layout);
        popupWindow.setContentView(view);
		listView = (ListView) findViewById(R.id.list);
        adapter = new BlacklistAdapater(BlacklistActivity.this, blackList);
        listView.setAdapter(adapter);
		// 从本地获取黑名单
		//List<String> blacklist = EMContactManager.getInstance().getBlackListUsernames();
        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imeiCode = telephonyManager.getDeviceId();
		gethttpdata();
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // TODO Auto-generated method stub
                BlacklistActivity.this.position=position;
                startActivityForResult(new Intent(BlacklistActivity.this,AlertDialogforBlackCancel.class), 10001);
                layout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                     
                    }
                });
                return true;
            }
        });
		// 显示黑名单列表
//		if (blacklist != null) {
//			Collections.sort(blacklist);
//			adapter = new BlacklistAdapater(this, 1, blacklist);
//			listView.setAdapter(adapter);
//		}

		// 注册上下文菜单
		//registerForContextMenu(listView);
		
	}

	

	/**
	 * 移出黑民单
	 * 
	 * @param tobeRemoveUser
	 */
	void removeOutBlacklist(final String tobeRemoveUser) {
	    

	}

	/**
	 * adapter
	 * 
	 */
	private class BlacklistAdapater extends BaseAdapter {
	    Context context;
		public BlacklistAdapater(Context context, List<Map<String, String>> blackList) {
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.list_black_item, null);
			}
			TextView name = (TextView) convertView.findViewById(R.id.name);
			ImageView avatar = (ImageView) convertView.findViewById(R.id.black_avatar);
			TextView userAddr= (TextView) convertView.findViewById(R.id.detail);
			TextView userPro= (TextView) convertView.findViewById(R.id.profession);
			name.setText(blackList.get(position).get("userName"));
//			 Picasso.with(context) //
//             .load(blackList.get(position).get("userAvatar")) //
//             .placeholder(R.drawable.account) //
//             .error(R.drawable.account) //
//             .fit() //
//             .tag(this) //
//             .into(avatar);
			imageLoader.displayImage(blackList.get(position).get("userAvatar"), avatar, App.options_account);
			userAddr.setText(blackList.get(position).get("userAddr"));
			userPro.setText(blackList.get(position).get("userPro"));
			return convertView;
		}

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return blackList.size();
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

	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}
	private void gethttpdata(){
	    pd1.setMessage("正在加载中...");
        pd1.setCancelable(true);
        pd1.show();
	    new Thread(){
            @Override
            public void run() {
                try {
                    final HttpClient httpClient = HttpClientHelper.getHttpClient(BlacklistActivity.this);
                    HttpPost post = new HttpPost(App.URL);
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    String currentUserId = BlacklistActivity.this.getIntent().getStringExtra("currentuser");
                    params.add(new BasicNameValuePair("user_id",currentUserId));
                    params.add(new BasicNameValuePair("tag","getblacklist"));
                    params.add(new BasicNameValuePair("apitype","users"));
                    
                    StringBuilder builderWithInfo = new StringBuilder();
                    builderWithInfo.append("user_id");
                    builderWithInfo.append(currentUserId);
                    builderWithInfo.append("tag");
                    builderWithInfo.append("getblacklist");
                    builderWithInfo.append("apitype");
                    builderWithInfo.append("users");
                    String saltCode = String.valueOf(System.currentTimeMillis());
                    String hashCode = "";
                    try {
                        hashCode = MD5Util.getEncryptedPwd(builderWithInfo.toString(),saltCode);
                    } catch (NoSuchAlgorithmException e1) {
                        hashCode = "9573";
                        e1.printStackTrace();
                    }
                    params.add(new BasicNameValuePair("salt",saltCode));
                    params.add(new BasicNameValuePair("hash",hashCode));
                    params.add(new BasicNameValuePair("keyset","user_id:tag:apitype:"));
                    params.add(new BasicNameValuePair("tokenvalue",imeiCode));
                    
                    post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
                    HttpResponse httprequest = httpClient.execute(post);
                    if(httprequest.getStatusLine().getStatusCode() == 200){
                        String msg = EntityUtils.toString(httprequest.getEntity());
                        JSONObject obj;
                        JSONObject blackObj;
                        try {
                            obj = new JSONObject(msg);
                            if("ok".equals(obj.get("flag"))){
                                JSONArray array=new JSONArray(obj.getString("black_users_id"));
                                for(int i=0;i<array.length();i++){
                                    blackObj=new JSONObject(array.getString(i));
                                    Map<String, String> map=new HashMap<String, String>();
                                    map.put("userName", blackObj.getString("userName"));
                                    map.put("userAvatar", blackObj.getString("userAvatar"));
                                    map.put("userId", blackObj.getString("userId"));
                                    map.put("userAddr", blackObj.getString("userAddr"));
                                    map.put("userPro", blackObj.getString("userPro"));
                                    blackList.add(map);
                                }
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pd1.dismiss();
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }else if("none".equals(obj.get("flag"))){
                                handler.sendEmptyMessage(1001);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }else{
                        handler.sendEmptyMessage(1001);
                        Loger.i("youlin", "error");
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    Loger.d("test5", "remove return msg="+"catch 1");
                    handler.sendEmptyMessage(1001);
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    Loger.d("test5", "remove return msg="+"catch 2");
                    handler.sendEmptyMessage(1001);
                    e.printStackTrace();
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    Loger.d("test5", "remove return msg="+"catch 3");
                    handler.sendEmptyMessage(1001);
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Loger.d("test5", "remove return msg="+"catch 4");
                    handler.sendEmptyMessage(1001);
                    e.printStackTrace();
                }
            };
        }.start();
    }
	   private Handler handler=new Handler(){
	        public void handleMessage(android.os.Message msg) {
	            if(msg.what==1001){
	                pd1.dismiss();
	                Toast.makeText(BlacklistActivity.this, "没有黑名单成员", 0).show();
	            }
	        };
	    };
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(resultCode==10002){
	     // 把目标user移出黑名单
            final HttpClient httpClient = HttpClientHelper.getHttpClient(BlacklistActivity.this);
            final ProgressDialog pd = new ProgressDialog(BlacklistActivity.this);
            pd.setMessage(getString(R.string.be_removing));
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            new Thread(){
                @Override
                public void run() {
                    try {
                        HttpPost post = new HttpPost(App.URL);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        String currentUserId = BlacklistActivity.this.getIntent().getStringExtra("currentuser");
                        String blackUserId = blackList.get(position).get("userId");
                        Loger.i("LYM", "777777777777777--->"+currentUserId+" "+blackUserId);
                        params.add(new BasicNameValuePair("black_user_id", blackUserId));
                        params.add(new BasicNameValuePair("user_id",currentUserId));
                        params.add(new BasicNameValuePair("action_id","2"));
                        params.add(new BasicNameValuePair("tag","blacklist"));
                        params.add(new BasicNameValuePair("apitype","users"));
                        
                        StringBuilder builderWithInfo = new StringBuilder();
                        builderWithInfo.append("black_user_id");
                        builderWithInfo.append(blackUserId);
                        builderWithInfo.append("user_id");
                        builderWithInfo.append(currentUserId);
                        builderWithInfo.append("action_id");
                        builderWithInfo.append("2");
                        builderWithInfo.append("tag");
                        builderWithInfo.append("blacklist");
                        builderWithInfo.append("apitype");
                        builderWithInfo.append("users");
                        String saltCode = String.valueOf(System.currentTimeMillis());
                        String hashCode = "";
                        try {
                            hashCode = MD5Util.getEncryptedPwd(builderWithInfo.toString(),saltCode);
                        } catch (NoSuchAlgorithmException e1) {
                            hashCode = "9573";
                            e1.printStackTrace();
                        }
                        params.add(new BasicNameValuePair("salt",saltCode));
                        params.add(new BasicNameValuePair("hash",hashCode));
                        params.add(new BasicNameValuePair("keyset","black_user_id:user_id:action_id:tag:apitype:"));
                        params.add(new BasicNameValuePair("tokenvalue",imeiCode));
                        
                        post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
                        HttpResponse httprequest = httpClient.execute(post);
                        if(httprequest.getStatusLine().getStatusCode() == 200){
                            String msg = EntityUtils.toString(httprequest.getEntity());
                            JSONObject obj;
                            try {
                                obj = new JSONObject(msg);
                                Loger.i("youlin", "3333333333--->"+obj.toString());
                                if("ok".equals(obj.get("flag"))){
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            try {
                                                // 移出黑民单
                                                EMContactManager.getInstance().deleteUserFromBlackList(blackList.get(position).get("userId"));
                                                String blackId=blackList.get(position).get("userId");
                                                pd.dismiss();
                                                blackList.remove(position);
                                                adapter.notifyDataSetChanged();
                                                Intent intent = new Intent();
                                                intent.putExtra("userid", blackId);
                                                setResult(RESULT_OK,intent);
                                                sendBroadcast(new Intent("com.nfs.youlin.find.get_neighbor"));
                                            } catch (EaseMobException e) {
                                                e.printStackTrace();
                                                try {
                                                    HttpClient httpClient=HttpClientHelper.getHttpClient(BlacklistActivity.this);
                                                    HttpPost httpPost = new HttpPost(App.URL);
                                                    List<NameValuePair> params=new ArrayList<NameValuePair>();
                                                    String currentUserId = BlacklistActivity.this.getIntent().getStringExtra("currentuser");
                                                    String blackUserId = blackList.get(position).get("userId");
                                                    params.add(new BasicNameValuePair("black_user_id", blackUserId));
                                                    params.add(new BasicNameValuePair("user_id", currentUserId));
                                                    params.add(new BasicNameValuePair("action_id", "1"));
                                                    params.add(new BasicNameValuePair("tag", "blacklist"));
                                                    params.add(new BasicNameValuePair("apitype", "users"));
                                                    
                                                    StringBuilder builderWithInfo = new StringBuilder();
                                                    builderWithInfo.append("black_user_id");
                                                    builderWithInfo.append(blackUserId);
                                                    builderWithInfo.append("user_id");
                                                    builderWithInfo.append(currentUserId);
                                                    builderWithInfo.append("action_id");
                                                    builderWithInfo.append("1");
                                                    builderWithInfo.append("tag");
                                                    builderWithInfo.append("blacklist");
                                                    builderWithInfo.append("apitype");
                                                    builderWithInfo.append("users");
                                                    String saltCode = String.valueOf(System.currentTimeMillis());
                                                    String hashCode = "";
                                                    try {
                                                        hashCode = MD5Util.getEncryptedPwd(builderWithInfo.toString(),saltCode);
                                                    } catch (NoSuchAlgorithmException e1) {
                                                        hashCode = "9573";
                                                        e1.printStackTrace();
                                                    }
                                                    params.add(new BasicNameValuePair("salt",saltCode));
                                                    params.add(new BasicNameValuePair("hash",hashCode));
                                                    params.add(new BasicNameValuePair("keyset","black_user_id:user_id:action_id:tag:apitype:"));
                                                    params.add(new BasicNameValuePair("tokenvalue",imeiCode));
                                                    
                                                    httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
                                                    HttpResponse httpResponse=httpClient.execute(httpPost);
                                                    if(httpResponse.getStatusLine().getStatusCode()==200){
                                                        String msg=EntityUtils.toString(httpResponse.getEntity());
                                                        JSONObject jsonObject=new JSONObject(msg);
                                                        if(jsonObject.getString("flag").equals("ok")){
                                                            Loger.i("youlin", "加入成功");
                                                        }else{
                                                            Loger.i("youlin", "加入失败");
                                                        }
                                                    }
                                                } catch (UnsupportedEncodingException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                } catch (ClientProtocolException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                } catch (ParseException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                } catch (IOException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                } catch (JSONException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                }
                                                Loger.i("LYM", "22222222");
                                                Toast.makeText(BlacklistActivity.this,"移出失败", 0).show();
                                            }
                                        }
                                    });
                                    
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pd.dismiss();
                                            Toast.makeText(BlacklistActivity.this,"移出失败", 0).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 1");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),"移出失败", 0).show();
                            }
                        });
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 2");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "移出失败", 0).show();
                            }
                        });
                        e.printStackTrace();
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 3");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "移出失败", 0).show();
                            }
                        });
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 4");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "移出失败", 0).show();
                            }
                        });
                        e.printStackTrace();
                    }
                };
            }.start();
	    }
	    if(resultCode==10003){
	     // 把所有目标user移出黑名单
            final HttpClient httpClient = HttpClientHelper.getHttpClient(BlacklistActivity.this);
            final ProgressDialog pd = new ProgressDialog(BlacklistActivity.this);
            pd.setMessage(getString(R.string.be_removing));
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            new Thread(){
                @Override
                public void run() {
                    try {
                        HttpPost post = new HttpPost(App.URL);
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        String currentUserId = BlacklistActivity.this.getIntent().getStringExtra("currentuser");
                        params.add(new BasicNameValuePair("user_id",currentUserId));
                        params.add(new BasicNameValuePair("tag","clearblacklist"));
                        params.add(new BasicNameValuePair("apitype","users"));
                        
                        StringBuilder builderWithInfo = new StringBuilder();
                        builderWithInfo.append("user_id");
                        builderWithInfo.append(currentUserId);
                        builderWithInfo.append("tag");
                        builderWithInfo.append("clearblacklist");
                        builderWithInfo.append("apitype");
                        builderWithInfo.append("users");
                        String saltCode = String.valueOf(System.currentTimeMillis());
                        String hashCode = "";
                        try {
                            hashCode = MD5Util.getEncryptedPwd(builderWithInfo.toString(),saltCode);
                        } catch (NoSuchAlgorithmException e1) {
                            hashCode = "9573";
                            e1.printStackTrace();
                        }
                        params.add(new BasicNameValuePair("salt",saltCode));
                        params.add(new BasicNameValuePair("hash",hashCode));
                        params.add(new BasicNameValuePair("keyset","user_id:tag:apitype:"));
                        params.add(new BasicNameValuePair("tokenvalue",imeiCode));
                        
                        post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
                        HttpResponse httprequest = httpClient.execute(post);
                        if(httprequest.getStatusLine().getStatusCode() == 200){
                            String msg = EntityUtils.toString(httprequest.getEntity());
                            JSONObject obj;
                            try {
                                obj = new JSONObject(msg);
                                if("ok".equals(obj.get("flag"))){
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            try {
                                                // 移出黑民单
                                                for(int i=0;i<blackList.size();i++){
                                                    EMContactManager.getInstance().deleteUserFromBlackList(blackList.get(i).get("userId"));
                                                }
                                                //String blackId=blackList.get(position).get("userId");
                                                pd.dismiss();
                                                blackList.clear();
                                                adapter.notifyDataSetChanged();
                                                Intent intent = new Intent();
                                                intent.putExtra("userid", -1);
                                                setResult(RESULT_OK,intent);
                                                sendBroadcast(new Intent("com.nfs.youlin.find.get_neighbor"));
                                            } catch (EaseMobException e) {
                                                e.printStackTrace();
                                                try {
                                                    HttpClient httpClient=HttpClientHelper.getHttpClient(BlacklistActivity.this);
                                                    HttpPost httpPost = new HttpPost(App.URL);
                                                    List<NameValuePair> params=new ArrayList<NameValuePair>();
                                                    String blackUserId = blackList.get(position).get("userId");
                                                    String currentUserId = BlacklistActivity.this.getIntent().getStringExtra("currentuser");
                                                    params.add(new BasicNameValuePair("black_user_id", blackUserId));
                                                    params.add(new BasicNameValuePair("user_id", currentUserId));
                                                    StringBuilder stringBuilder=new StringBuilder();
                                                    for(int i=0;i<blackList.size();i++){
                                                        stringBuilder.append(blackList.get(i).get("userId")+":");
                                                    }
                                                    String blackStr=stringBuilder.toString();
                                                    params.add(new BasicNameValuePair("black_list", blackStr));
                                                    params.add(new BasicNameValuePair("tag", "undoblacklist"));
                                                    params.add(new BasicNameValuePair("apitype", "users"));
                                                    
                                                    StringBuilder builderWithInfo = new StringBuilder();
                                                    builderWithInfo.append("black_user_id");
                                                    builderWithInfo.append(blackUserId);
                                                    builderWithInfo.append("user_id");
                                                    builderWithInfo.append(currentUserId);
                                                    builderWithInfo.append("black_list");
                                                    builderWithInfo.append(blackStr);
                                                    builderWithInfo.append("tag");
                                                    builderWithInfo.append("undoblacklist");
                                                    builderWithInfo.append("apitype");
                                                    builderWithInfo.append("users");
                                                    String saltCode = String.valueOf(System.currentTimeMillis());
                                                    String hashCode = "";
                                                    try {
                                                        hashCode = MD5Util.getEncryptedPwd(builderWithInfo.toString(),saltCode);
                                                    } catch (NoSuchAlgorithmException e1) {
                                                        hashCode = "9573";
                                                        e1.printStackTrace();
                                                    }
                                                    params.add(new BasicNameValuePair("salt",saltCode));
                                                    params.add(new BasicNameValuePair("hash",hashCode));
                                                    params.add(new BasicNameValuePair("keyset","black_user_id:user_id:black_list:tag:apitype:"));
                                                    params.add(new BasicNameValuePair("tokenvalue",imeiCode));
                                                    
                                                    httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
                                                    HttpResponse httpResponse=httpClient.execute(httpPost);
                                                    if(httpResponse.getStatusLine().getStatusCode()==200){
                                                        String msg=EntityUtils.toString(httpResponse.getEntity());
                                                        JSONObject jsonObject=new JSONObject(msg);
                                                        if(jsonObject.getString("flag").equals("ok")){
                                                            Loger.i("youlin", "加入成功");
                                                        }else{
                                                            Loger.i("youlin", "加入失败");
                                                        }
                                                    }
                                                } catch (UnsupportedEncodingException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                } catch (ClientProtocolException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                } catch (ParseException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                } catch (IOException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                } catch (JSONException e1) {
                                                    // TODO Auto-generated catch block
                                                    e1.printStackTrace();
                                                }
                                                pd.dismiss();
                                                Toast.makeText(BlacklistActivity.this,"移出失败", 0).show();
                                            }
                                        }
                                    });
                                    
                                }else{
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            pd.dismiss();
                                            Toast.makeText(BlacklistActivity.this,"移出失败", 0).show();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 1");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),"移出失败", 0).show();
                            }
                        });
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 2");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "移出失败", 0).show();
                            }
                        });
                        e.printStackTrace();
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 3");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "移出失败", 0).show();
                            }
                        });
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Loger.d("test5", "remove return msg="+"catch 4");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "移出失败", 0).show();
                            }
                        });
                        e.printStackTrace();
                    }
                };
            }.start();
	    }
	};
	
	@Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
