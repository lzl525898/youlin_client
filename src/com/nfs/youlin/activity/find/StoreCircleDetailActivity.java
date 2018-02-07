package com.nfs.youlin.activity.find;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.NewPushRecordAbsActivity;
import com.nfs.youlin.activity.NewPushStoreCommentActivity;
import com.nfs.youlin.activity.neighbor.CircleDetailGalleryPictureActivity;
import com.nfs.youlin.activity.neighbor.GalleryPictureActivity;
import com.nfs.youlin.activity.personal.FriendInformationActivity;
import com.nfs.youlin.activity.personal.PersonalInformationActivity;
import com.nfs.youlin.adapter.FriendCircleImageAdapter;
import com.nfs.youlin.entity.ForumTopic;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.SampleScrollListener;
import com.nfs.youlin.utils.TimeToStr;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class StoreCircleDetailActivity extends Activity {
	ImageLoader imageLoader;
	TextView storeNameTv;
	ImageView topImg;
	TextView addressTv;
	TextView jobTimeTv;
	TextView phoneTv;
	ImageView headImg;
	TextView nameTv;
	TextView commentTimeTv;
	RatingBar star;
	TextView contentTv;
	GridView gridView;
	RelativeLayout storeAcountLayout;
	RelativeLayout layout1;
	List<String> pictureList = new ArrayList<String>();
	double lat=0,lon=0;
	String uid;
	View bottomView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_store_circle_detail);
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("内容详情");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		uid=getIntent().getStringExtra("uid");
		Loger.i("LYM","11111111111---->"+uid+" "+App.sUserLoginId);
		imageLoader = ImageLoader.getInstance();
		storeNameTv=(TextView)findViewById(R.id.store_name);
		topImg=(ImageView)findViewById(R.id.top_img);
		addressTv=(TextView)findViewById(R.id.adrress_detail);
		jobTimeTv=(TextView)findViewById(R.id.time_detail);
		phoneTv=(TextView)findViewById(R.id.phone_detatil);
		headImg=(ImageView)findViewById(R.id.head);
		nameTv=(TextView)findViewById(R.id.name);
		commentTimeTv=(TextView)findViewById(R.id.comment_time);
		star=(RatingBar)findViewById(R.id.star);
		contentTv=(TextView)findViewById(R.id.content);
		gridView=(GridView)findViewById(R.id.gridView);
		storeAcountLayout=(RelativeLayout)findViewById(R.id.acount_layout);
		layout1=(RelativeLayout)findViewById(R.id.layout1);
		bottomView=(View)findViewById(R.id.bottom_id);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		findViewById(R.id.layout1).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(StoreCircleDetailActivity.this,StoreCircleAllCommentActivity.class).putExtra("uid", uid).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
				//startActivity(new Intent(StoreCircleDetailActivity.this,NewPushStoreCommentActivity.class));
			}
		});
		findViewById(R.id.adrress_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder=new AlertDialog.Builder(StoreCircleDetailActivity.this);
				builder.setCancelable(true);
				builder.setMessage(addressTv.getText().toString());
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				if(!TextUtils.isEmpty(addressTv.getText())){
					builder.show();
				}
			}
		});
		findViewById(R.id.phone_layout).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!phoneTv.getText().toString().equals("未提供") && phoneTv.getText().toString()!=null && !phoneTv.getText().toString().equals("null")){
					commentRequst();
					startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneTv.getText().toString())).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}
			}
		});
		findViewById(R.id.location_icon).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(lat!=0 && lon!=0){
					commentRequst();
					new MapPathPlan(StoreCircleDetailActivity.this, lat, lon);
				}
			}
		});
		initRequst();
	}
	
	private void initRequst() {
		RequestParams params = new RequestParams();
		params.put("uid", uid);
		params.put("tag", "intobizcirdetail");
		params.put("apitype", "address");
		params.put("community_id", App.sFamilyCommunityId);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(IHttpRequestUtils.URL+ IHttpRequestUtils.YOULIN, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String flag=response.getString("flag");
					if(flag.equals("ok")){
						JSONObject jsonObject=(JSONObject)response.get("detail");
						App.CurrentSysTime=Long.parseLong(jsonObject.getString("systime"));
						if(jsonObject.getString("eva_count").equals("0")){
							storeAcountLayout.setVisibility(View.GONE);
							bottomView.setVisibility(View.GONE);
						}else{
							storeAcountLayout.setVisibility(View.VISIBLE);
							bottomView.setVisibility(View.VISIBLE);
						}
						if(jsonObject.getString("eva_count").equals("0") || jsonObject.getString("eva_count").equals("1")){
							layout1.setVisibility(View.GONE);
						}else{
							layout1.setVisibility(View.VISIBLE);
						}
						
						storeNameTv.setText(jsonObject.getString("name"));
						imageLoader.displayImage(jsonObject.getString("imgurl"), topImg, App.options_news_pic);
						addressTv.setText(jsonObject.getString("address"));
						if(jsonObject.getString("shophours").equals("null")){
							jobTimeTv.setText("未提供");
						}else{
							jobTimeTv.setText(jsonObject.getString("shophours"));
							
						}
						
						if(jsonObject.getString("telephone").equals("null")){
							phoneTv.setText("未提供");
						}else{
							phoneTv.setText(jsonObject.getString("telephone"));
						}
						
						JSONObject locaObject=new JSONObject(jsonObject.getString("location"));
						lat=locaObject.getDouble("lat");
						lon=locaObject.getDouble("lng");
						Loger.i("LYM", "location---->"+locaObject.getDouble("lat")+ " "+locaObject.getDouble("lng"));
						final JSONObject acountObject=new JSONObject(jsonObject.getString("eva_dict"));
						imageLoader.displayImage(acountObject.getString("avatar"), headImg, App.options_account);
						headImg.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								try {
									if(Long.parseLong(acountObject.getString("userid"))==App.sUserLoginId){
										Intent intent = new Intent(StoreCircleDetailActivity.this,PersonalInformationActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
										startActivity(intent);
									}else {
											if(acountObject.getString("status").equals("0")){
												Intent intent = new Intent(StoreCircleDetailActivity.this,FriendInformationActivity.class);
												intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												intent.putExtra("sender_id",Long.parseLong(acountObject.getString("userid")));
												intent.putExtra("display_name",acountObject.getString("nick"));
												intent.putExtra("sender_portrait",acountObject.getString("avatar"));
												startActivity(intent);
											}else{
												
											}
									}
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						if(acountObject.getString("status").equals("0")){
							nameTv.setText(acountObject.getString("nick"));
						}else{
							nameTv.setText("匿名");
						}
						commentTimeTv.setText(TimeToStr.getTimeElapse(Long.parseLong(acountObject.getString("time")), App.CurrentSysTime));
						star.setRating(Float.valueOf(acountObject.getString("facility")));
						contentTv.setText(acountObject.getString("content"));
						JSONArray imgJsonArray = null;
						try {
							if (acountObject.getString("media") != null) {
								imgJsonArray = new JSONArray(acountObject.getString("media"));
								for (int i = 0; i < imgJsonArray.length(); i++) {
									try {
										pictureList.add(imgJsonArray.getString(i));
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						gridView.setAdapter(new FriendCircleImageAdapter(StoreCircleDetailActivity.this, pictureList));
						gridView.setOnScrollListener(new SampleScrollListener(StoreCircleDetailActivity.this));
						gridView.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
								// TODO Auto-generated method stub
								// selectID =
								// Integer.parseInt(((View)arg1.getParent()).getTag().toString());
								Intent intent = new Intent(StoreCircleDetailActivity.this,GalleryPictureStoreActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
								intent.putExtra("ID", arg2);
								try {
									intent.putExtra("url",acountObject.getString("media"));
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								startActivity(intent);
							}
						});
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(StoreCircleDetailActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	private void commentRequst(){
		RequestParams params=new RequestParams();
		params.put("tag", "guorecord");
		params.put("apitype", "address");
		params.put("uid", uid);
		params.put("user", App.sUserLoginId);
		params.put("community", App.sFamilyCommunityId);
		AsyncHttpClient asyncHttpClient=new AsyncHttpClient();
		asyncHttpClient.post(IHttpRequestUtils.URL+ IHttpRequestUtils.YOULIN, params,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String flag=response.getString("flag");
					Loger.i("LYM", "comment ok!--->"+flag);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				new ErrorServer(StoreCircleDetailActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
