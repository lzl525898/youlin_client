package com.nfs.youlin.activity.neighbor;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.nfs.youlin.R;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.activity.TitleBarSearchActivity;
import com.nfs.youlin.activity.personal.CollectionActivity;
import com.nfs.youlin.activity.personal.MyPushActivity;
import com.nfs.youlin.adapter.FriendCircleAdapter;
import com.nfs.youlin.adapter.SignUpFamilyAdapter;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.applycount;
import com.nfs.youlin.utils.error_logtext;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpDetailActivity extends Activity {
	private AccountDaoDBImpl accountDaoDBImpl;
	private Account account;
	private ImageView qr_show;
	private Bitmap bitmap;
	private int weight;
	private List<String> portraitList=new ArrayList<String>();
	private List<String> nameList=new ArrayList<String>();
	private List<String> signUpNumList=new ArrayList<String>();
	private boolean cRequestSet = false;
	private String flag = "none";
	private Intent intent;
	public static List<JSONObject> jsonObjList;
	private String applytotalcount;
	private TextView totalcount;
	private List<Object> forumtopicLists ;
	private FriendCircleAdapter currentadapter;
	private int REQUEST_APPLY_DETAIL = 302;
	ImageLoader imageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_detail);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("报名详情");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		totalcount = (TextView)findViewById(R.id.tv_signup_num);
		accountDaoDBImpl=new AccountDaoDBImpl(this);
		account = accountDaoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
		accountDaoDBImpl.releaseDatabaseRes();
		ImageView portrait=(ImageView)findViewById(R.id.iv_signup_portrait);
		TextView nameTv=(TextView)findViewById(R.id.tv_signup_name);
		String headUrl = String.valueOf(account.getUser_portrait());
		imageLoader = ImageLoader.getInstance();
//		Picasso.with(this) 
//				.load(headUrl) 
//				.placeholder(R.drawable.account) 
//				.error(R.drawable.account) 
//				.fit() 
//				.into(portrait);
		imageLoader.displayImage(headUrl,portrait,App.options_account);
		nameTv.setText(account.getUser_name());
		intent = getIntent();
		final int position = intent.getIntExtra("position", 0);
		final int parentclass = intent.getIntExtra("parent", 0);
		Loger.d("test4", "circle detail parentclass=="+parentclass+"position="+position);
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
			currentadapter = new FriendCircleAdapter(forumtopicLists, this, 0, 4);
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
		}
		else if(parentclass==6){
			forumtopicLists = PropertyGonggaoActivity.forumtopicLists;
			currentadapter = PropertyGonggaoActivity.maddapter;
		}
		try {
			JSONArray Object_data = new JSONArray(((ForumTopic)forumtopicLists.get(position)).getObject_data());
			gethttpdata(Long.parseLong(((JSONObject) Object_data.get(0)).get("activityId").toString()),
					IHttpRequestUtils.YOULIN, 1);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					for(int i=0;i<jsonObjList.size();i++){
						try {
							totalcount.setText("报名人数:"+applytotalcount+"个");
							nameList.add(jsonObjList.get(i).getString("userNick"));
							portraitList.add(jsonObjList.get(i).getString("userPortrait"));
							signUpNumList.add("共"+jsonObjList.get(i).getString("total")+"人  (小孩："+jsonObjList.get(i).getString("enrollNeCount")+"人)");
							ListView listView=(ListView)findViewById(R.id.signup_family_lv);
							SignUpFamilyAdapter adapter=new SignUpFamilyAdapter(SignUpDetailActivity.this,getData());
							listView.setAdapter(adapter);
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					JSONArray Object_data;
					try {
						Object_data = new JSONArray(((ForumTopic)currentadapter.data.get(position)).getObject_data());
						((JSONObject) Object_data.get(0)).put("enrollTotal",applytotalcount);
						((ForumTopic)currentadapter.data.get(position)).setObject_data(Object_data.toString());
						Loger.d("test4",Object_data.toString());	
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(parentclass == 5){
						Intent data = new Intent();
						data.putExtra("totalcount", applytotalcount);
						setResult(RESULT_OK,data);
					}
					if(currentadapter!=null){
						currentadapter.notifyDataSetChanged();
					}
				}
			}
		}.execute();
	
//		qr_show = (ImageView) findViewById(R.id.iv_twoDimCode);
//		//生成二维码
//		CreateTwoDimCode twoDimCode = new CreateTwoDimCode(this);
//		twoDimCode.initViews(1, "http://baidu.com", qr_show);
//		
//		qr_show.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(SignUpDetailActivity.this, TwoDimenCodeActivity.class);
//				startActivity(intent);
//			}
//		});
		
		
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
	
	public List<Map<String, Object>> getData(){
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		for (int i = 0; i < nameList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("portrait", portraitList.get(i));
			map.put("name", nameList.get(i));
			map.put("sign_num", signUpNumList.get(i));
			list.add(map);
		}
		return list;
	}
	private void gethttpdata(long activityId,String http_addr,int request_index){
		/***********************************************/
		jsonObjList = new ArrayList<JSONObject>();
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		final int position = request_index;
			Loger.d("test3", "3333333333333333333333");
		
			if(request_index ==1){
					params.put("activityId", activityId);
					params.put("tag", "detenroll");
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
				Loger.i("test4","JSONObject->"+ jsonContext.toString());
				jsonObjList.clear();
				try {
					org.json.JSONArray jsonarray = jsonContext.getJSONArray("enrollData");
					applytotalcount = jsonContext.getString("enrollTotal");
					for (int i = 0; i < jsonarray.length(); i++) {
						jsonObjList.add(jsonarray.getJSONObject(i));

					}
					Loger.i("test4","JSONarray size->"+ jsonObjList.size());
					flag="ok";
					cRequestSet = true;
					
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
//				jsonObjList.clear();
//				try {
//					org.json.JSONObject obj = null;
//					for (int i = 0; i < jsonContext.length(); i++) {
//						jsonObjList.add(jsonContext.getJSONObject(i));
//						obj = jsonContext.getJSONObject(i);
//					}
//					flag="ok";
//					cRequestSet = true;
//				} catch (org.json.JSONException e) {
//					// TODO Auto-generated catch block
//					Loger.i("TEST","OK(error)->" + e.getMessage());
//					cRequestSet = false;
//					e.printStackTrace();
//					}
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
					new ErrorServer(SignUpDetailActivity.this, responseString.toString());
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
