package com.nfs.youlin.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.nfs.youlin.R;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.view.YLProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;
import com.xonami.javaBells.JingleSession;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

public class ReportDetailActivity extends Activity {
	public static ReportDetailActivity sReportDetailActivity;
	public static YLProgressDialog ylProgressDialog;
	public static RelativeLayout topLayout;
	public static long reportTopicId = -1;
	public String reportJson="null";
	public String reportHead;
	public String reportName;
	public String reportTitle;
	public String reportContent;
	public String imgCount;
	//static public String report_Type;
	private final int REQUEST_CODE_REPORT_DETAIL = 9527;
	ImageLoader imageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_report);
		ylProgressDialog=YLProgressDialog.createDialogWithTopic(ReportDetailActivity.this);
		ylProgressDialog.setCancelable(true);
		ylProgressDialog.show();
		imageLoader = ImageLoader.getInstance();
		ReportDetailActivity.sReportDetailActivity = this;
		ActionBar actionBar=getActionBar();
		actionBar.setTitle("举报详情"); 
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);	
		ReportDetailActivity.reportTopicId = getIntent().getLongExtra("report_tId", -1);
		reportJson = getIntent().getStringExtra("report_detail");
		try {
			JSONArray jsonArray=new JSONArray(reportJson);
			JSONObject jsonObject=new JSONObject(jsonArray.getString(0));
			reportHead=jsonObject.getString("avatar");
			reportName=jsonObject.getString("nick");
			reportTitle=jsonObject.getString("title");
			reportContent=jsonObject.getString("content");
			imgCount=jsonObject.getString("img_count");
			//report_Type=jsonObject.getString("topicType");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 ImageView headImg=(ImageView) ReportDetailActivity.sReportDetailActivity.findViewById(R.id.head_img);
		 
	     //Picasso.with(ReportDetailActivity.this).load(reportHead).placeholder(R.drawable.pic_report_head).error(R.drawable.pic_report_head).into(headImg);
	     imageLoader.displayImage(reportHead, headImg, App.options_account);
	     TextView nameTv=(TextView) ReportDetailActivity.sReportDetailActivity.findViewById(R.id.name_tv);
	     nameTv.setText(reportName);
	     TextView titleTv=(TextView) ReportDetailActivity.sReportDetailActivity.findViewById(R.id.title_tv);
	     titleTv.setText(reportTitle);
	     TextView contentTv=(TextView) ReportDetailActivity.sReportDetailActivity.findViewById(R.id.content_tv);
	     contentTv.setText(reportContent);
		 Loger.i("TEST", "reportTopicId==>"+reportTopicId);
		 topLayout = (RelativeLayout) findViewById(R.id.top_layout);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			//if(ll_push_progress_dlg.getVisibility()!=View.VISIBLE){
				finish();
			//}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		JPushInterface.onResume(ReportDetailActivity.this);
		MobclickAgent.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(REQUEST_CODE_REPORT_DETAIL==resultCode){
			//从详情页返回
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	protected void onPause() {
		JPushInterface.onPause(ReportDetailActivity.this);
		MobclickAgent.onPause(this);
		super.onPause();
	}
}
