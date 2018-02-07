package com.nfs.youlin.activity.personal;

import android.app.ProgressDialog;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.nfs.youlin.R;
import com.nfs.youlin.utils.CommonTools;
import com.nfs.youlin.utils.Loger;
import com.umeng.analytics.MobclickAgent;
import com.easemob.chatuidemo.activity.BaiduMapActivity;

public class SelectaddrActivity extends Activity {

	private final static String TAG = "map";
	//static MapView mMapView = null;
	FrameLayout mMapViewContainer = null;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	public NotifyLister mNotifyer = null;

	//TextView sendButton = null;

	EditText indexText = null;
	int index = 0;
	// LocationData locData = null;
	static BDLocation lastLocation = null;
	public static SelectaddrActivity instance = null;
	ProgressDialog progressDialog;
	Intent sintent;
	int lastviewid = -1;
	private String oldcity;
	//private BaiduMap mBaiduMap;
	
	private LocationMode mCurrentMode;
//	private int[] CityResurceId = {R.id.selectcity1,R.id.selectcity2,R.id.selectcity3,R.id.selectcity4,R.id.selectcity5,R.id.selectcity6,
//			R.id.selectcity7,R.id.selectcity8,R.id.selectcity9};
	private int[] CityResurceId = {R.id.selectcity8};
	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduSDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			String st1 = getResources().getString(R.string.Network_error);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				
				String st2 = getResources().getString(R.string.please_check);
				Loger.d("hyytest", st2);
				//Toast.makeText(instance, st2, Toast.LENGTH_SHORT).show();
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Loger.d("hyytest", st1);
				//Toast.makeText(instance, st1, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private BaiduSDKReceiver mBaiduReceiver;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        //SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.selectaddr);
		try {
			sintent = this.getIntent();
			oldcity = sintent.getStringExtra("oldcity");
			for(int i = 0;i<CityResurceId.length;i++){
				if(((TextView)findViewById(CityResurceId[i])).getText().equals(oldcity)){
					((TextView)findViewById(CityResurceId[i])).setBackgroundResource(R.drawable.btn_chegnshi_h);
					lastviewid =CityResurceId[i];
					break;
				}
			}
			sintent.putExtra("latitude", "1");
			sintent.putExtra("longitude", "2");
			sintent.putExtra("address", "3");
			sintent.putExtra("city", oldcity);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//sendButton = (TextView) findViewById(R.id.getcurrentcity);
		//sendButton.setText("未定城市");
		Button unselect = (Button)findViewById(R.id.backbutton2);
//        Drawable drawable1 = getResources().getDrawable(R.drawable.nav_fanhui_xin_tiao);
//        drawable1.setBounds(0, 1, 28, 46);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
//        unselect.setCompoundDrawables(drawable1, null, null, null);//只放左边
		unselect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CommonTools.goBack();
			}
		});

		Button nextb = (Button) findViewById(R.id.nextbutton);
		nextb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(lastviewid == -1){
					Toast.makeText(SelectaddrActivity.this, "请先选择城市", Toast.LENGTH_SHORT).show();
				}else{
					SelectaddrActivity.this.setResult(RESULT_OK, sintent);
					//Loger.d("hyytest", "sendcurrentLocation city= "+sendButton.getText().toString());
					finish();
					overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
				}
				
			}
		});
		//mCurrentMode = LocationMode.NORMAL;

	//	MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);

//		showMapWithLocationClient();

//		// 注册 SDK 广播监听者
//		IntentFilter iFilter = new IntentFilter();
//		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
//		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
//		mBaiduReceiver = new BaiduSDKReceiver();
//		registerReceiver(mBaiduReceiver, iFilter);
	}


	private void showMapWithLocationClient() {
		String str1 = getResources().getString(R.string.Making_sure_your_location);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(str1);

		progressDialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface arg0) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				Loger.d("map", "cancel retrieve location");
				finish();
			}
		});

		progressDialog.show();

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		// option.setCoorType("bd09ll"); //设置坐标类型
		// Johnson change to use gcj02 coordination. chinese national standard
		// so need to conver to bd09 everytime when draw on baidu map
		option.setCoorType("gcj02");
		option.setScanSpan(30000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
	}

	@Override
	protected void onPause() {
		JPushInterface.onPause(getApplicationContext());
		MobclickAgent.onPause(this);
		//mMapView.onPause();
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		JPushInterface.resumePush(getApplicationContext());
		MobclickAgent.onResume(this);
		//mMapView.onResume();
		if (mLocClient != null) {
			mLocClient.start();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null)
			mLocClient.stop();
		//mMapView.onDestroy();
//		unregisterReceiver(mBaiduReceiver);
		super.onDestroy();
	}
	private void initMapView() {
		//mMapView.setLongClickable(true);
	}

	/**
	 * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			location.getCity();
			Loger.d("map", "On location change received:" + location);
			Loger.d("map", "addr:" + location.getAddrStr());
			Loger.d("hyytest", "city:" + location.getCity());
			//sendButton.setEnabled(true);
			//sendButton.setText(location.getCity());
			if (progressDialog != null) {
				progressDialog.dismiss();
			}

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
					Loger.d("map", "same location, skip refresh");
					// mMapView.refresh(); //need this refresh?
					return;
				}
			}
			lastLocation = location;
			//mBaiduMap.clear();
			LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
			CoordinateConverter converter= new CoordinateConverter();
			converter.coord(llA);
			converter.from(CoordinateConverter.CoordType.COMMON);
			LatLng convertLatLng = converter.convert();
			OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
					.fromResource(R.drawable.icon_marka))
					.zIndex(4).draggable(true);
			//mBaiduMap.addOverlay(ooA);
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
			//mBaiduMap.animateMapStatus(u);
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	public class NotifyLister extends BDNotifyListener {
		public void onNotify(BDLocation mlocation, float distance) {
		}
	}

	public void back(View v) {
		finish();
	}

	public void sendcurrentLocation(View view) {
		sintent = this.getIntent();
//		intent.putExtra("latitude", lastLocation.getLatitude());
//		intent.putExtra("longitude", lastLocation.getLongitude());
//		intent.putExtra("address", lastLocation.getAddrStr());
		sintent.putExtra("latitude", "1");
		sintent.putExtra("longitude", "2");
		sintent.putExtra("address", "3");
		try {
			((TextView)findViewById(lastviewid)).setBackgroundResource(R.drawable.btn_chegnshi_n);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((TextView)findViewById(view.getId())).setBackgroundResource(R.drawable.btn_chegnshi_h);
		lastviewid =view.getId();
		sintent.putExtra("city", ((TextView)findViewById(view.getId())).getText().toString() );
	}
	
}
