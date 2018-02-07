package com.nfs.youlin.activity.personal;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.neighbor.PropertyRepairAddActivity;
import com.nfs.youlin.activity.titlebar.startactivity.SendActivity;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.BackgroundAlpha;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.error_logtext;
import com.umeng.analytics.MobclickAgent;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

public class OpinionFeedBackActivity extends Activity implements OnClickListener{
	private ActionBar actionBar;
	EditText opinionEt;
	ImageView opinionClear;
	Button opinionBn;
	Long time=System.currentTimeMillis();
	private static ProgressDialog pd;
	private  RequestParams sRequestParams;
	private int category=1;
	private RelativeLayout category_lay;
	private TextView category_text;
	private ImageView category_empty;
	private ImageView content_empty;
	private PopupWindow popWindow;
	private View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_opinion_feed);
		actionBar=getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("意见反馈");
		category_lay = (RelativeLayout)findViewById(R.id.category_layout1);
		category_text = (TextView)findViewById(R.id.category_tv);
		opinionEt=(EditText) findViewById(R.id.opinion_et);
		opinionClear=(ImageView)findViewById(R.id.opinion_clear);
		opinionBn=(Button)findViewById(R.id.opinion_bt);
		category_empty = (ImageView)findViewById(R.id.category_tanhao_img);
		content_empty = (ImageView)findViewById(R.id.content_tanhao_img);
		opinionClear.setOnClickListener(this);
		opinionBn.setOnClickListener(this);
		category_lay.setOnClickListener(this);
		opinionEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				opinionClear.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(s.toString().length()>0){
					content_empty.setVisibility(View.GONE);
				}
			}
		});
		
		popWindow=new PopupWindow(this);
		view=getLayoutInflater().inflate(R.layout.activity_share_popwindow_repair, null);
		final ListView listView=(ListView)view.findViewById(R.id.share_pop_repair_lv);
		final String[] arr={"界面","功能","其他"};
		final int[] type={1,2,3};
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,arr);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				listView.setItemChecked(arg2, true);
				category_text.setText(arr[arg2]);
				category = type[arg2];
				category_empty.setVisibility(arg1.GONE);
				popWindow.dismiss();
			}
		});
		
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(getApplicationContext());
		MobclickAgent.onResume(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.opinion_clear:
			opinionEt.setText("");
			opinionClear.setVisibility(View.GONE);
			break;
		case R.id.opinion_bt:
			String str=opinionEt.getText().toString();
			if(!category_text.getText().equals("类别")){
				if(!str.isEmpty()){
					sendFinish(category, str);			
				}else{
					Toast.makeText(OpinionFeedBackActivity.this,"内容不能为空", Toast.LENGTH_SHORT).show();
					content_empty.setVisibility(View.VISIBLE);
				}
			}else{
				Toast.makeText(OpinionFeedBackActivity.this,"类别不能为空", Toast.LENGTH_SHORT).show();
				category_empty.setVisibility(v.VISIBLE);
			}
			
			break;
		case R.id.category_layout1:
			new BackgroundAlpha(0.5f, OpinionFeedBackActivity.this);
			popWindow.setWidth(LayoutParams.WRAP_CONTENT);
			popWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popWindow.setBackgroundDrawable(new BitmapDrawable());
			popWindow.setFocusable(true);
			popWindow.setOutsideTouchable(true);
			popWindow.setContentView(view);
			popWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
			popWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					new BackgroundAlpha(1f, OpinionFeedBackActivity.this);
				}
			});
			
		default:
			break;
		}
	}
	private void sendFinish(int category,String message){
		 pd = new ProgressDialog(this);
		 pd.setMessage("发布中...");
		pd.show();
		sRequestParams = new RequestParams();
		sRequestParams.put("userId", App.sUserLoginId); 
		sRequestParams.put("communityId", App.sFamilyCommunityId); 
		sRequestParams.put("opinionType", category); // 0表示本小区、1、周边、2.同城
		sRequestParams.put("opinionContent", message);
		sRequestParams.put("tag", "feedback");
		sRequestParams.put("apitype", IHttpRequestUtils.APITYPE[3]);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(IHttpRequestUtils.URL+IHttpRequestUtils.YOULIN,
				sRequestParams, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					String Flag = response.getString("flag");
					if("full".equals(Flag)){
						Toast.makeText(OpinionFeedBackActivity.this,response.getString("yl_msg"), Toast.LENGTH_SHORT).show();
						finish();
					}
					if("ok".equals(Flag)){
						Toast.makeText(OpinionFeedBackActivity.this,"发送成功", Toast.LENGTH_SHORT).show();
						finish();
					}else{
						Toast.makeText(OpinionFeedBackActivity.this,"建议发送失败", Toast.LENGTH_SHORT).show();
					}
					pd.dismiss();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				// TODO Auto-generated method stub
				pd.dismiss();
				new ErrorServer(OpinionFeedBackActivity.this, responseString);
				super.onFailure(statusCode, headers, responseString, throwable);
			}
		});
	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
	}
}
