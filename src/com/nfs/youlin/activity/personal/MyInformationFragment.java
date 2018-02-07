package com.nfs.youlin.activity.personal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nfs.youlin.R;
import com.nfs.youlin.activity.AddressShowForBar;
import com.nfs.youlin.activity.MainActionBarclicklisener;
import com.nfs.youlin.activity.neighbor.CircleDetailGalleryPictureActivity;
import com.nfs.youlin.activity.personal.SystemSetActivity;
import com.nfs.youlin.activity.personal.addressseting;
import com.nfs.youlin.dao.AccountDaoDBImpl;
import com.nfs.youlin.dao.AllFamilyDaoDBImpl;
import com.nfs.youlin.entity.Account;
import com.nfs.youlin.http.AsyncHttpClient;
import com.nfs.youlin.http.IHttpRequestUtils;
import com.nfs.youlin.http.JsonHttpResponseHandler;
import com.nfs.youlin.http.RequestParams;
import com.nfs.youlin.http.SyncHttpClient;
import com.nfs.youlin.signcalendar.Signmajoractivity;
import com.nfs.youlin.utils.App;
import com.nfs.youlin.utils.ErrorServer;
import com.nfs.youlin.utils.Loger;
import com.nfs.youlin.utils.RoundImageView;
import com.nfs.youlin.utils.error_logtext;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


public class MyInformationFragment extends Fragment implements OnClickListener{
	private Context mcon;
	private View view;//缓存页面

	public  ImageView accountImageView;
	public  static TextView accountNameTv;

	private RelativeLayout accountLayout;
	private LinearLayout mySendLayout;
	private LinearLayout jifenLayout;
	private LinearLayout collectLayout;

	private TextView accountPhoneTv;
	private TextView jifenTv;
	private TextView lijuanTv;
	public  TextView mySendTv;
	private TextView collectTv;
	private ImageButton qiandaostep;
	private AccountDaoDBImpl accountDaoDBImpl;
	private Account accountWithInfo =null;
	private String flag = "none";
	//private boolean cRequestSet = false;
//	private ImageView pengyouImageView;
	private static final int CODE_PERSON_RETURN = 101;	
	private String mypushcount;
	private String collectcount;
	private String creditcount;
	ImageLoader imageLoader;
	String topicPath=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		imageLoader = ImageLoader.getInstance();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Loger.i("slide","MyInformationFragment-onCreateView");
		mcon=getActivity();
		if(view==null){
			view=inflater.inflate(R.layout.my_information_fragment,container, false);
		}
		ViewGroup parent = (ViewGroup) view.getParent();
		if(parent!=null){
			parent.removeView(view);//先移除
		}
		accountLayout=(RelativeLayout)view.findViewById(R.id.accountLayout);
		mySendLayout=(LinearLayout)view.findViewById(R.id.mySend_layout);
		jifenLayout = (LinearLayout)view.findViewById(R.id.jifen_layout);
		collectLayout=(LinearLayout)view.findViewById(R.id.collect_layout);
		accountImageView=(ImageView)view.findViewById(R.id.accountHead);
		accountNameTv=(TextView)view.findViewById(R.id.accountName);
		accountPhoneTv=(TextView)view.findViewById(R.id.accountPhone);
		mySendTv = (TextView)view.findViewById(R.id.mySendId);
		collectTv = (TextView)view.findViewById(R.id.collectId);
		jifenTv = (TextView)view.findViewById(R.id.jifenId);
		qiandaostep = (ImageButton)view.findViewById(R.id.qiandaostep);
		accountDaoDBImpl = new AccountDaoDBImpl(mcon);
		mySendTv.setText("0");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//SystemClock.sleep(300);
				getMyBitmap();
			}
		}).start();
		accountImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),CircleDetailGalleryPictureActivity.class);
				intent.putExtra("type", 1);
				intent.putExtra("url",topicPath);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				setUserPhoneWithPhoto();
			}
		}).start();
		
	    List<String> namelist = new ArrayList<String>();
	    namelist.add("地址信息");
//	    namelist.add("邀请好友");
	    namelist.add("意见反馈");
	    namelist.add("设置");
	    namelist.add("关于");
	    List<Object> imgList=new ArrayList<Object>();
	    imgList.add(R.drawable.dizhixinxi);
//	    imgList.add(R.drawable.nav_yaoqinghaoyou);
	    imgList.add(R.drawable.yijianfankui);
	    imgList.add(R.drawable.shezhi);
	    imgList.add(R.drawable.guanyu);

//      pengyouImageView = (ImageView)popupShareView.findViewById(R.id.iv_popup_personal_share_pengyouquan);

		// pengyouImageView.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if(!isApkInstalled(Tab3Activity.this, "com.tencent.mm")){
		// Toast.makeText(Tab3Activity.this, "手机未发现有微信，请先安装!",
		// Toast.LENGTH_LONG).show();
		// }else{
		// Intent intent = new Intent();
		// ComponentName comp = new ComponentName("com.tencent.mm",
		// "com.tencent.mm.ui.tools.ShareImgUI");
		// intent.setComponent(comp)
		// intent.setAction("android.intent.action.SEND");
		// intent.setType("image/*");
		// intent.putExtra(Intent.EXTRA_TEXT,shareInformation);
		// startActivity(intent);
		// }
		// popupShareWindow.dismiss();
		// }
		// });
	    gethttpdata(App.sUserLoginId, App.sFamilyCommunityId, IHttpRequestUtils.YOULIN, 1);

		List<Map<String,Object>> list =new ArrayList<Map<String,Object>>();
		for(int i=0;i<namelist.size();i++){
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("firstImg",imgList.get(i));
			map.put("name", namelist.get(i));
			list.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(mcon, list,
				R.layout.list_message_item, new String[] { "firstImg", "name"},
				new int[] { R.id.first_img, R.id.name});

		ListView listView = (ListView)view.findViewById(R.id.myMessageList);
		listView.setAdapter(simpleAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//listView.setBackgroundColor(Color.GRAY);
				switch (position) {
				case 0:
					AllFamilyDaoDBImpl allFDaoDBImpl = new AllFamilyDaoDBImpl(mcon);
					List<Object> familyObjs = allFDaoDBImpl.findPointTypeObject(App.sUserLoginId);
					int length =familyObjs.size();
					Loger.d("test4", "App.sUserLoginId="+App.sUserLoginId);
//					int length = DemoApplication.getInstance().addresslist.size();
					Loger.d("hyytest","addresslist size ="+length);
					
					if(length==0 ){
					Intent intent = new Intent(mcon,addressseting.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
					}else if(length > 0)
					{Intent intent = new Intent(mcon,com.nfs.youlin.activity.personal.addressshow.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);}
					break;
				case 1:
					startActivity(new Intent(mcon,OpinionFeedBackActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
					break;
				case 2:
					startActivity(new Intent(mcon, SystemSetActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
					break;
				case 3:
					startActivity(new Intent(mcon, PersonalInfoGuanyuActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
					break;
				default:
					break;
				}

			}
		});
		accountLayout.setOnClickListener(this);
		jifenLayout.setOnClickListener(this);
//		lijuanLayout.setOnClickListener(this);
		mySendLayout.setOnClickListener(this);
		collectLayout.setOnClickListener(this);
		qiandaostep.setOnClickListener(this);
		return view;
	}
	@Override
    public void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        Loger.i("slide","MyInformationFragment--onPause");
    }
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Loger.i("slide","MyInformationFragment--onStop");
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.accountLayout:
			Intent intent=new Intent(getActivity(),PersonalInformationActivity.class);
			startActivityForResult(intent,CODE_PERSON_RETURN);
			break;
		case R.id.jifen_layout:
			startActivity(new Intent(getActivity(), IntegralActivity.class).putExtra("creditdata", creditcount).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
//		case R.id.lijuan_layout:
//			startActivity(new Intent(getActivity(), CouponActivity.class));
//			break;
		case R.id.mySend_layout:
			startActivity(new Intent(getActivity(), MyPushActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.collect_layout:
			startActivity(new Intent(getActivity(), CollectionActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.qiandaostep:
			startActivity(new Intent(getActivity(), Signmajoractivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		default:
			break;
		}
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		gethttpdata(App.sUserLoginId, App.sFamilyCommunityId, IHttpRequestUtils.YOULIN, 1);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CODE_PERSON_RETURN:
			Loger.i("youlin","CODE_PERSON_RETURN---------------");
			//finish();
			getMyBitmap();
			accountNameTv.setText(accountDaoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId)).getUser_name());
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private Account setDaoDBImpl(AccountDaoDBImpl accountDaoDBImpl,Account account){
		try {
			accountDaoDBImpl = new AccountDaoDBImpl(this.getActivity());
			accountDaoDBImpl.releaseDatabaseRes();
			account = accountDaoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
			return account;
		} catch (Exception e) {
			Loger.i("TEST", "getMyBitmap()==Err=>"+e.getMessage());
			SystemClock.sleep(300);
			setDaoDBImpl(accountDaoDBImpl,account);
		}
		return null;
	}
	
	public void getMyBitmap(){
		boolean bStatus = false;
		AccountDaoDBImpl accountDaoDBImpl = new AccountDaoDBImpl(this.getActivity());
		Account account = null;
		while(true){
			if(!bStatus){
				account = setDaoDBImpl(accountDaoDBImpl,account);
				bStatus = true;
			}else{
				break;
			}
		}
		if(account != null){
			topicPath = account.getUser_portrait();
		}
		try {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (topicPath!=null) {
//						Picasso.with(getActivity()) 
//								.load(topicPath) 
//								.placeholder(R.drawable.default_normal_avatar) 
//								.error(R.drawable.default_normal_avatar) 
//								.fit() 
//								.tag(getActivity()) 
//								.into(accountImageView);
						imageLoader.displayImage(topicPath, accountImageView, App.options_account);
						Loger.i("TEST", "F---头像加载成功！！！！");
					} else {
						accountImageView.setBackgroundResource(R.drawable.account);
					}
				}
			});
		} catch (Exception e) {
			Loger.i("TEST","getActivity()===Err==>"+e.getMessage());
		}
	}
	private void gethttpdata(long user_id,long community_id,String http_addr,int request_index){
		/***********************************************/
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		final int position = request_index;
		Loger.d("test3", "user_id="+user_id+"community_id="+community_id);
			if(request_index ==1){
					params.put("user_id", user_id);
					params.put("community_id", community_id);
					params.put("tag", "getcount");
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
				
				try {
					flag = jsonContext.getString("flag");
					Loger.i("test3", "get mypush flag->" + flag);
					if(flag.equals("ok")){
						mypushcount = jsonContext.getString("myPost");
						collectcount = jsonContext.getString("mycolNum");
						creditcount = jsonContext.getString("myCredit");
						Loger.i("test3", "get mypush count->" + mypushcount+"get mycollect count->"+collectcount);
						mySendTv.setText(mypushcount);
						collectTv.setText(collectcount);
						jifenTv.setText(creditcount);
					}else{
					}

				} catch (org.json.JSONException e) {
					e.printStackTrace();
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
			}
				@Override
				public void onFailure(int statusCode,
						org.apache.http.Header[] headers,
						String responseString,
						Throwable throwable) {
					// TODO Auto-generated method stub
					new ErrorServer(getActivity(), responseString.toString());
					super.onFailure(statusCode, headers,
							responseString, throwable);
				}
			});

	}
	
	private void setUserPhoneWithPhoto(){
		try {
			accountWithInfo = accountDaoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
		} catch (Exception e) {
			Loger.i("TEST", "accountNameTv=Error=>"+e.getMessage());
			SystemClock.sleep(300);
			accountDaoDBImpl = new AccountDaoDBImpl(mcon);
			accountDaoDBImpl.releaseDatabaseRes();
			accountWithInfo = accountDaoDBImpl.findAccountByLoginID(String.valueOf(App.sUserLoginId));
		}
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				accountNameTv.setText(accountWithInfo.getUser_name());
				accountPhoneTv.setText(accountWithInfo.getUser_phone_number());
			}
		});
	}
}
